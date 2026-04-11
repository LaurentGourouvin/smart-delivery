package com.smartdelivery.payment_service.service;

import com.smartdelivery.payment_service.entity.Payment;
import com.smartdelivery.payment_service.entity.PaymentStatus;
import com.smartdelivery.payment_service.event.OrderCreatedEvent;
import com.smartdelivery.payment_service.event.PaymentFailedEvent;
import com.smartdelivery.payment_service.event.PaymentSucceededEvent;
import com.smartdelivery.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${payment.simulation.success-rate}")
    private double successRate;

    @Value("${payment.simulation.processing-delay-ms}")
    private long processingDelayMs;

    private static final String TOPIC_PAYMENT_SUCCEEDED = "payment.succeeded";
    private static final String TOPIC_PAYMENT_FAILED = "payment.failed";

    // Consummer
    @KafkaListener(topics = "order.created", groupId = "payment-service")
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received order.created event for order {}", event.getOrderId());

        // Idempotence | on ne traite pas deux fois la même commande
        if (paymentRepository.existsByOrderId(event.getOrderId())) {
            log.warn("Payment already exists for order {} — skipping", event.getOrderId());
            return;
        }

        // Créer le paiement en statut PENDING
        Payment payment = Payment.builder()
                .orderId(event.getOrderId())
                .userId(event.getUserId())
                .paymentIntentId(UUID.randomUUID().toString())
                .amount(event.getTotalAmount())
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);
        log.info("Payment {} created in PENDING status", payment.getId());

        // Simuler le délai de traitement bancaire
        try {
            Thread.sleep(processingDelayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simuler succès ou échec
        boolean success = Math.random() < successRate;

        if (success) {
            processSuccess(payment);
        } else {
            processFailure(payment, event);
        }
    }

    private void processSuccess(Payment payment) {
        payment.setStatus(PaymentStatus.SUCCEEDED);
        paymentRepository.save(payment);

        PaymentSucceededEvent event = PaymentSucceededEvent.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .paymentIntentId(payment.getPaymentIntentId())
                .amount(payment.getAmount())
                .build();

        kafkaTemplate.send(TOPIC_PAYMENT_SUCCEEDED,
                payment.getOrderId().toString(), event);

        log.info("Payment {} SUCCEEDED for order {}",
                payment.getId(), payment.getOrderId());
    }

    private void processFailure(Payment payment, OrderCreatedEvent orderEvent) {
        String reason = "Payment declined by issuing bank (simulation)";

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason);
        paymentRepository.save(payment);

        PaymentFailedEvent event = PaymentFailedEvent.builder()
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .failureReason(reason)
                .items(orderEvent.getItems())
                .build();

        kafkaTemplate.send(TOPIC_PAYMENT_FAILED,
                payment.getOrderId().toString(), event);

        log.info("Payment {} FAILED for order {} — Saga compensation triggered",
                payment.getId(), payment.getOrderId());
    }
}