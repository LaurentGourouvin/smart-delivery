package com.smartdelivery.order_service.service;

import com.smartdelivery.order_service.client.ProductResponse;
import com.smartdelivery.order_service.client.ProductServiceClient;
import com.smartdelivery.order_service.dto.CreateOrderRequest;
import com.smartdelivery.order_service.dto.DeliveryAddressRequest;
import com.smartdelivery.order_service.dto.OrderItemRequest;
import com.smartdelivery.order_service.dto.OrderResponse;
import com.smartdelivery.order_service.dto.UpdateOrderStatusRequest;
import com.smartdelivery.order_service.entity.Order;
import com.smartdelivery.order_service.entity.OrderStatus;
import com.smartdelivery.order_service.exception.OrderNotFoundException;
import com.smartdelivery.order_service.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductServiceClient productServiceClient;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private OrderService orderService;

    private UUID userId;
    private UUID productId;
    private UUID orderId;
    private Jwt jwt;
    private Order existingOrder;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        productId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(userId.toString());
        when(jwt.getClaimAsString("email")).thenReturn("laurent@smartdelivery.com");

        existingOrder = Order.builder()
                .id(orderId)
                .userId(userId)
                .userEmail("laurent@smartdelivery.com")
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(49.98))
                .deliveryStreet("1 rue de la Paix")
                .deliveryCity("Bordeaux")
                .deliveryPostalCode("33000")
                .deliveryCountry("FR")
                .items(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ─────────────────────────────────────────
    // getMyOrders
    // ─────────────────────────────────────────

    @Test
    @DisplayName("getMyOrders — retourne les commandes de l'utilisateur")
    void getMyOrders_returnsUserOrders() {
        when(orderRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(List.of(existingOrder));

        List<OrderResponse> result = orderService.getMyOrders(jwt);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).userEmail()).isEqualTo("laurent@smartdelivery.com");
    }

    // ─────────────────────────────────────────
    // getOrderById
    // ─────────────────────────────────────────

    @Test
    @DisplayName("getOrderById — retourne la commande si elle appartient à l'utilisateur")
    void getOrderById_ownerAccess_returnsOrder() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        OrderResponse result = orderService.getOrderById(orderId, jwt);

        assertThat(result.id()).isEqualTo(orderId);
    }

    @Test
    @DisplayName("getOrderById — lève OrderNotFoundException si la commande appartient à un autre")
    void getOrderById_wrongOwner_throwsException() {
        Order otherOrder = Order.builder()
                .id(orderId)
                .userId(UUID.randomUUID())
                .userEmail("other@test.com")
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(10.00))
                .deliveryStreet("rue")
                .deliveryCity("Paris")
                .deliveryPostalCode("75000")
                .deliveryCountry("FR")
                .items(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(otherOrder));

        assertThatThrownBy(() -> orderService.getOrderById(orderId, jwt))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("getOrderById — lève OrderNotFoundException si commande absente")
    void getOrderById_notFound_throwsException() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(orderId, jwt))
                .isInstanceOf(OrderNotFoundException.class);
    }

    // ─────────────────────────────────────────
    // createOrder
    // ─────────────────────────────────────────

    @Test
    @DisplayName("createOrder — crée une commande et publie sur Kafka")
    void createOrder_validRequest_createsAndPublishes() {
        ProductResponse product = new ProductResponse(
                productId, UUID.randomUUID(), "Sérum",
                "COSRX Snail Essence", null, "COSRX",
                BigDecimal.valueOf(24.99), 50, null, "ALL", 100, true
        );

        CreateOrderRequest request = new CreateOrderRequest(
                new DeliveryAddressRequest("1 rue de la Paix", "Bordeaux", "33000", "FR"),
                List.of(new OrderItemRequest(productId, 2))
        );

        when(productServiceClient.getProduct(eq(productId), anyString()))
                .thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);

        OrderResponse result = orderService.createOrder(request, jwt, "Bearer token");

        assertThat(result).isNotNull();
        verify(productServiceClient, times(1)).decrementStock(eq(productId), eq(2), anyString());
        verify(kafkaTemplate, times(1)).send(eq("order.created"), anyString(), any());
    }

    // ─────────────────────────────────────────
    // updateOrderStatus
    // ─────────────────────────────────────────

    @Test
    @DisplayName("updateOrderStatus — met à jour le statut et publie sur Kafka")
    void updateOrderStatus_validRequest_updatesAndPublishes() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(OrderStatus.CONFIRMED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);

        orderService.updateOrderStatus(orderId, request);

        verify(orderRepository, times(1)).save(existingOrder);
        verify(kafkaTemplate, times(1)).send(eq("order.status-changed"), anyString(), any());
    }

    @Test
    @DisplayName("updateOrderStatus — lève OrderNotFoundException si commande absente")
    void updateOrderStatus_notFound_throwsException() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(OrderStatus.CONFIRMED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, request))
                .isInstanceOf(OrderNotFoundException.class);
    }
}