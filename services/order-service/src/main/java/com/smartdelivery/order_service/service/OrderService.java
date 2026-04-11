package com.smartdelivery.order_service.service;

import com.smartdelivery.order_service.client.ProductResponse;
import com.smartdelivery.order_service.client.ProductServiceClient;
import com.smartdelivery.order_service.dto.*;
import com.smartdelivery.order_service.entity.Order;
import com.smartdelivery.order_service.entity.OrderItem;
import com.smartdelivery.order_service.entity.OrderStatus;
import com.smartdelivery.order_service.event.OrderCreatedEvent;
import com.smartdelivery.order_service.event.OrderItemEvent;
import com.smartdelivery.order_service.event.OrderStatusChangedEvent;
import com.smartdelivery.order_service.exception.OrderNotFoundException;
import com.smartdelivery.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProductServiceClient productServiceClient;

    private static final String TOPIC_ORDER_CREATED = "order.created";
    private static final String TOPIC_ORDER_STATUS_CHANGED = "order.status-changed";

    // Queries - read
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toOrderResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID id, Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (!order.getUserId().equals(userId)) {
            throw new OrderNotFoundException(id);
        }

        return toOrderResponse(order);
    }

    //Commands - write
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, Jwt jwt, String bearerToken) {
        UUID userId = UUID.fromString(jwt.getSubject());
        String userEmail = jwt.getClaimAsString("email");

        // Récupérer les infos produits depuis product-service et décrémenter le stock
        List<OrderItem> items = request.items().stream()
                .map(itemRequest -> {
                    ProductResponse product = productServiceClient
                            .getProduct(itemRequest.productId(), bearerToken);

                    productServiceClient.decrementStock(
                            itemRequest.productId(),
                            itemRequest.quantity(),
                            bearerToken
                    );

                    BigDecimal subtotal = product.price()
                            .multiply(BigDecimal.valueOf(itemRequest.quantity()));

                    return OrderItem.builder()
                            .productId(itemRequest.productId())
                            .productName(product.name())
                            .unitPrice(product.price())
                            .quantity(itemRequest.quantity())
                            .subtotal(subtotal)
                            .build();
                })
                .toList();

        BigDecimal totalAmount = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        DeliveryAddressRequest address = request.deliveryAddress();
        String country = address.country() != null ? address.country() : "FR";

        Order order = Order.builder()
                .userId(userId)
                .userEmail(userEmail)
                .totalAmount(totalAmount)
                .deliveryStreet(address.street())
                .deliveryCity(address.city())
                .deliveryPostalCode(address.postalCode())
                .deliveryCountry(country)
                .build();

        order.getItems().addAll(items);
        items.forEach(item -> item.setOrder(order));

        Order saved = orderRepository.save(order);

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(saved.getId())
                .userId(userId)
                .userEmail(userEmail)
                .totalAmount(totalAmount)
                .deliveryStreet(address.street())
                .deliveryCity(address.city())
                .deliveryPostalCode(address.postalCode())
                .deliveryCountry(country)
                .items(items.stream()
                        .map(item -> OrderItemEvent.builder()
                                .productId(item.getProductId())
                                .productName(item.getProductName())
                                .quantity(item.getQuantity())
                                .unitPrice(item.getUnitPrice())
                                .subtotal(item.getSubtotal())
                                .build())
                        .toList())
                .build();

        kafkaTemplate.send(TOPIC_ORDER_CREATED, saved.getId().toString(), event);
        log.info("Published order.created event for order {}", saved.getId());

        return toOrderResponse(saved);
    }

    @Transactional
    public OrderResponse updateOrderStatus(UUID id, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(request.status());
        Order saved = orderRepository.save(order);

        OrderStatusChangedEvent event = OrderStatusChangedEvent.builder()
                .orderId(saved.getId())
                .userId(saved.getUserId())
                .oldStatus(oldStatus)
                .newStatus(request.status())
                .build();

        kafkaTemplate.send(TOPIC_ORDER_STATUS_CHANGED, saved.getId().toString(), event);
        log.info("Published order.status-changed event for order {} : {} → {}",
                saved.getId(), oldStatus, request.status());

        return toOrderResponse(saved);
    }

    private OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getSubtotal()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getUserEmail(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getDeliveryStreet(),
                order.getDeliveryCity(),
                order.getDeliveryPostalCode(),
                order.getDeliveryCountry(),
                order.getCreatedAt(),
                items
        );
    }
}