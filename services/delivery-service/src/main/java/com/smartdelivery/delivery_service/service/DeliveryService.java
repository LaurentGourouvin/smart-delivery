package com.smartdelivery.delivery_service.service;

import com.smartdelivery.delivery_service.dto.DeliveryResponse;
import com.smartdelivery.delivery_service.entity.Delivery;
import com.smartdelivery.delivery_service.entity.DeliveryStatus;
import com.smartdelivery.delivery_service.event.DeliveryUpdatedEvent;
import com.smartdelivery.delivery_service.event.PaymentSucceededEvent;
import com.smartdelivery.delivery_service.exception.DeliveryNotFoundException;
import com.smartdelivery.delivery_service.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryRepository        deliveryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SimpMessagingTemplate     messagingTemplate;

    @Value("${delivery.simulation.delay-seconds}")
    private int delaySeconds;

    // Consomme payment.succeeded → crée la livraison en ASSIGNED
    @KafkaListener(topics = "payment.succeeded", groupId = "delivery-service")
    @Transactional
    public void onPaymentSucceeded(PaymentSucceededEvent event) {
        log.info("Received payment.succeeded for orderId={}", event.getOrderId());

        if (deliveryRepository.existsByOrderId(event.getOrderId())) {
            log.warn("Delivery already exists for orderId={} — skipping", event.getOrderId());
            return;
        }

        Delivery delivery = Delivery.builder()
                .orderId(event.getOrderId())
                .userId(event.getUserId())
                .status(DeliveryStatus.ASSIGNED)
                .carrier("SmartExpress")
                .trackingNumber(generateTrackingNumber())
                .estimatedDelivery(LocalDateTime.now().plusDays(5))
                .build();

        deliveryRepository.save(delivery);
        log.info("Delivery created id={} orderId={}", delivery.getId(), delivery.getOrderId());

        publishAndPush(delivery);
    }

    // Avance le statut de toutes les livraisons non terminées
    // fixedDelayString lit delivery.simulation.delay-seconds en ms
    @Scheduled(fixedDelayString = "#{${delivery.simulation.delay-seconds} * 1000}")
    @Transactional
    public void advanceDeliveries() {
        List<Delivery> active = deliveryRepository.findAllByStatusNot(DeliveryStatus.DELIVERED);

        if (active.isEmpty()) return;

        log.info("Advancing {} active deliveries", active.size());

        for (Delivery delivery : active) {
            try {
                DeliveryStatus next = nextStatus(delivery.getStatus());
                delivery.setStatus(next);

                if (next == DeliveryStatus.DELIVERED) {
                    delivery.setDeliveredAt(LocalDateTime.now());
                }

                deliveryRepository.save(delivery);
                publishAndPush(delivery);

                log.info("Delivery id={} advanced to {}", delivery.getId(), next);

            } catch (ObjectOptimisticLockingFailureException e) {
                log.warn("Optimistic lock conflict for deliveryId={} — skipping", delivery.getId());
            }
        }
    }

    // ── Query ──────────────────────────────────────────────────
    public DeliveryResponse getByOrderId(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .map(this::toResponse)
                .orElseThrow(() -> new DeliveryNotFoundException(orderId));
    }

    // ── Helpers ────────────────────────────────────────────────

    // Publie sur Kafka + push WebSocket en une seule opération
    private void publishAndPush(Delivery delivery) {
        DeliveryUpdatedEvent event = new DeliveryUpdatedEvent(
                delivery.getId(),
                delivery.getOrderId(),
                delivery.getUserId(),
                delivery.getStatus(),
                delivery.getTrackingNumber(),
                delivery.getEstimatedDelivery(),
                delivery.getUpdatedAt()
        );

        // Kafka — consommé par notification-service
        kafkaTemplate.send("delivery.updated", delivery.getOrderId().toString(), event);

        // WebSocket — push au client abonné à /topic/delivery/{orderId}
        messagingTemplate.convertAndSend(
                "/topic/delivery/" + delivery.getOrderId(),
                toResponse(delivery)
        );

        log.info("Published delivery.updated + WebSocket push for orderId={} status={}",
                delivery.getOrderId(), delivery.getStatus());
    }

    private DeliveryStatus nextStatus(DeliveryStatus current) {
        return switch (current) {
            case ASSIGNED   -> DeliveryStatus.PICKED_UP;
            case PICKED_UP  -> DeliveryStatus.IN_TRANSIT;
            case IN_TRANSIT -> DeliveryStatus.DELIVERED;
            case DELIVERED  -> DeliveryStatus.DELIVERED; // état terminal — ne devrait pas arriver
        };
    }

    // Génère un numéro de suivi simulé au format SD-XXXXXXXX
    private DeliveryResponse toResponse(Delivery d) {
        return new DeliveryResponse(
                d.getId(),
                d.getOrderId(),
                d.getStatus(),
                d.getCarrier(),
                d.getTrackingNumber(),
                d.getEstimatedDelivery(),
                d.getAssignedAt(),
                d.getDeliveredAt(),
                d.getUpdatedAt()
        );
    }

    private String generateTrackingNumber() {
        return "SD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}