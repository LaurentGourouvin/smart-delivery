package com.smartdelivery.notification_service.service;

import com.smartdelivery.notification_service.event.OrderCreatedEvent;
import com.smartdelivery.notification_service.event.PaymentFailedEvent;
import com.smartdelivery.notification_service.event.PaymentSucceededEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    private UUID orderId;
    private UUID userId;
    private UUID paymentId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        userId = UUID.randomUUID();
        paymentId = UUID.randomUUID();
    }

    @Test
    @DisplayName("handleOrderCreated — traite l'événement sans exception")
    void handleOrderCreated_doesNotThrow() {
        OrderCreatedEvent event = new OrderCreatedEvent(
                orderId, userId, "laurent@smartdelivery.com",
                BigDecimal.valueOf(49.98)
        );

        assertThatNoException().isThrownBy(() ->
                notificationService.handleOrderCreated(event)
        );
    }

    @Test
    @DisplayName("handlePaymentSucceeded — traite l'événement sans exception")
    void handlePaymentSucceeded_doesNotThrow() {
        PaymentSucceededEvent event = new PaymentSucceededEvent(
                paymentId, orderId, userId,
                UUID.randomUUID().toString(),
                BigDecimal.valueOf(49.98)
        );

        assertThatNoException().isThrownBy(() ->
                notificationService.handlePaymentSucceeded(event)
        );
    }

    @Test
    @DisplayName("handlePaymentFailed — traite l'événement sans exception")
    void handlePaymentFailed_doesNotThrow() {
        PaymentFailedEvent event = new PaymentFailedEvent(
                orderId, userId,
                "Payment declined by issuing bank (simulation)"
        );

        assertThatNoException().isThrownBy(() ->
                notificationService.handlePaymentFailed(event)
        );
    }
}