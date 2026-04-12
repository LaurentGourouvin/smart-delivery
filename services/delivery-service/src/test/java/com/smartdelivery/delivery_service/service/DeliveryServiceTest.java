package com.smartdelivery.delivery_service.service;

import com.smartdelivery.delivery_service.dto.DeliveryResponse;
import com.smartdelivery.delivery_service.entity.Delivery;
import com.smartdelivery.delivery_service.entity.DeliveryStatus;
import com.smartdelivery.delivery_service.event.PaymentSucceededEvent;
import com.smartdelivery.delivery_service.exception.DeliveryNotFoundException;
import com.smartdelivery.delivery_service.repository.DeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
class DeliveryServiceTest {

    @Mock private DeliveryRepository            deliveryRepository;
    @Mock private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock private SimpMessagingTemplate         messagingTemplate;

    @InjectMocks
    private DeliveryService deliveryService;

    private UUID orderId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        userId  = UUID.randomUUID();
    }

    // ── onPaymentSucceeded ─────────────────────────────────────

    @Test
    @DisplayName("onPaymentSucceeded — crée une livraison en ASSIGNED")
    void onPaymentSucceeded_shouldCreateDelivery() {
        PaymentSucceededEvent event = new PaymentSucceededEvent(orderId, userId, BigDecimal.valueOf(49.99));

        when(deliveryRepository.existsByOrderId(orderId)).thenReturn(false);
        when(deliveryRepository.save(any())).thenAnswer(inv -> {
            Delivery d = inv.getArgument(0);
            d.setUpdatedAt(LocalDateTime.now());
            return d;
        });

        deliveryService.onPaymentSucceeded(event);

        ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
        verify(deliveryRepository).save(captor.capture());

        Delivery saved = captor.getValue();
        assertThat(saved.getOrderId()).isEqualTo(orderId);
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getStatus()).isEqualTo(DeliveryStatus.ASSIGNED);
        assertThat(saved.getCarrier()).isEqualTo("SmartExpress");
        assertThat(saved.getTrackingNumber()).startsWith("SD-");
    }

    @Test
    @DisplayName("onPaymentSucceeded — idempotence si la livraison existe déjà")
    void onPaymentSucceeded_shouldBeIdempotent_whenDeliveryAlreadyExists() {
        PaymentSucceededEvent event = new PaymentSucceededEvent(orderId, userId, BigDecimal.TEN);

        when(deliveryRepository.existsByOrderId(orderId)).thenReturn(true);

        deliveryService.onPaymentSucceeded(event);

        verify(deliveryRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
    }

    // ── advanceDeliveries ──────────────────────────────────────

    @Test
    @DisplayName("advanceDeliveries — avance ASSIGNED vers PICKED_UP")
    void advanceDeliveries_shouldAdvanceAssignedToPickedUp() {
        Delivery delivery = buildDelivery(DeliveryStatus.ASSIGNED);
        when(deliveryRepository.findAllByStatusNot(DeliveryStatus.DELIVERED))
                .thenReturn(List.of(delivery));
        when(deliveryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        deliveryService.advanceDeliveries();

        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.PICKED_UP);
        verify(kafkaTemplate).send(eq("delivery.updated"), anyString(), any());
    }

    @Test
    @DisplayName("advanceDeliveries — positionne deliveredAt quand statut devient DELIVERED")
    void advanceDeliveries_shouldSetDeliveredAt_whenStatusBecomesDelivered() {
        Delivery delivery = buildDelivery(DeliveryStatus.IN_TRANSIT);
        when(deliveryRepository.findAllByStatusNot(DeliveryStatus.DELIVERED))
                .thenReturn(List.of(delivery));
        when(deliveryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        deliveryService.advanceDeliveries();

        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.DELIVERED);
        assertThat(delivery.getDeliveredAt()).isNotNull();
    }

    @Test
    @DisplayName("advanceDeliveries — ne fait rien si aucune livraison active")
    void advanceDeliveries_shouldDoNothing_whenNoActiveDeliveries() {
        when(deliveryRepository.findAllByStatusNot(DeliveryStatus.DELIVERED))
                .thenReturn(List.of());

        deliveryService.advanceDeliveries();

        verify(deliveryRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
    }

    // ── getByOrderId ───────────────────────────────────────────

    @Test
    @DisplayName("getByOrderId — retourne la livraison correspondante")
    void getByOrderId_shouldReturnDeliveryResponse() {
        Delivery delivery = buildDelivery(DeliveryStatus.IN_TRANSIT);
        delivery.setOrderId(orderId);

        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));

        DeliveryResponse response = deliveryService.getByOrderId(orderId);

        assertThat(response.orderId()).isEqualTo(orderId);
        assertThat(response.status()).isEqualTo(DeliveryStatus.IN_TRANSIT);
    }

    @Test
    @DisplayName("getByOrderId — lève DeliveryNotFoundException si introuvable")
    void getByOrderId_shouldThrow_whenNotFound() {
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deliveryService.getByOrderId(orderId))
                .isInstanceOf(DeliveryNotFoundException.class);
    }

    // ── Helper ─────────────────────────────────────────────────

    private Delivery buildDelivery(DeliveryStatus status) {
        return Delivery.builder()
                .id(UUID.randomUUID())
                .orderId(orderId)
                .userId(userId)
                .status(status)
                .carrier("SmartExpress")
                .trackingNumber("SD-ABCD1234")
                .estimatedDelivery(LocalDateTime.now().plusDays(3))
                .assignedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .version(0L)
                .build();
    }
}
