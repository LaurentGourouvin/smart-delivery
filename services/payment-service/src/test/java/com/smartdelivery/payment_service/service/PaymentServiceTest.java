package com.smartdelivery.payment_service.service;

import com.smartdelivery.payment_service.entity.Payment;
import com.smartdelivery.payment_service.entity.PaymentStatus;
import com.smartdelivery.payment_service.event.OrderCreatedEvent;
import com.smartdelivery.payment_service.repository.PaymentRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private PaymentService paymentService;

    private UUID orderId;
    private UUID userId;
    private OrderCreatedEvent orderEvent;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        userId = UUID.randomUUID();

        ReflectionTestUtils.setField(paymentService, "successRate", 0.9);
        ReflectionTestUtils.setField(paymentService, "processingDelayMs", 0L);

        orderEvent = new OrderCreatedEvent(
                orderId, userId, "laurent@smartdelivery.com",
                BigDecimal.valueOf(49.98), List.of(),
                "1 rue de la Paix", "Bordeaux", "33000", "FR"
        );
    }

    @Test
    @DisplayName("handleOrderCreated — ignore si paiement déjà existant")
    void handleOrderCreated_alreadyExists_skips() {
        when(paymentRepository.existsByOrderId(orderId)).thenReturn(true);

        paymentService.handleOrderCreated(orderEvent);

        verify(paymentRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("handleOrderCreated — crée le paiement et publie payment.succeeded si succès")
    void handleOrderCreated_success_publishesSucceeded() {
        ReflectionTestUtils.setField(paymentService, "successRate", 1.0);

        Payment savedPayment = Payment.builder()
                .id(UUID.randomUUID())
                .orderId(orderId)
                .userId(userId)
                .paymentIntentId(UUID.randomUUID().toString())
                .amount(BigDecimal.valueOf(49.98))
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(paymentRepository.existsByOrderId(orderId)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        paymentService.handleOrderCreated(orderEvent);

        verify(kafkaTemplate, atLeastOnce()).send(
                eq("payment.succeeded"), anyString(), any()
        );
    }

    @Test
    @DisplayName("handleOrderCreated — publie payment.failed si échec")
    void handleOrderCreated_failure_publishesFailed() {
        ReflectionTestUtils.setField(paymentService, "successRate", 0.0);

        Payment savedPayment = Payment.builder()
                .id(UUID.randomUUID())
                .orderId(orderId)
                .userId(userId)
                .paymentIntentId(UUID.randomUUID().toString())
                .amount(BigDecimal.valueOf(49.98))
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(paymentRepository.existsByOrderId(orderId)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        paymentService.handleOrderCreated(orderEvent);

        verify(kafkaTemplate, atLeastOnce()).send(
                eq("payment.failed"), anyString(), any()
        );
    }

    @Test
    @DisplayName("handleOrderCreated — statut FAILED enregistré en base si échec")
    void handleOrderCreated_failure_savesFailedStatus() {
        ReflectionTestUtils.setField(paymentService, "successRate", 0.0);

        Payment savedPayment = Payment.builder()
                .id(UUID.randomUUID())
                .orderId(orderId)
                .userId(userId)
                .paymentIntentId(UUID.randomUUID().toString())
                .amount(BigDecimal.valueOf(49.98))
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(paymentRepository.existsByOrderId(orderId)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        paymentService.handleOrderCreated(orderEvent);

        verify(paymentRepository, atLeast(2)).save(any(Payment.class));
    }
}