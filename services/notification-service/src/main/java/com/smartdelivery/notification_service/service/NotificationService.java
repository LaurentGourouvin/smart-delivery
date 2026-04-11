package com.smartdelivery.notification_service.service;

import com.smartdelivery.notification_service.event.OrderCreatedEvent;
import com.smartdelivery.notification_service.event.PaymentFailedEvent;
import com.smartdelivery.notification_service.event.PaymentSucceededEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    @KafkaListener(
            topics = "order.created",
            groupId = "notification-service",
            containerFactory = "orderCreatedListenerFactory"
    )
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("[EMAIL] → {} | Commande {} en cours de traitement | Montant : {}€",
                event.getUserEmail(),
                event.getOrderId(),
                event.getTotalAmount());

        sendEmail(
                event.getUserEmail(),
                "Votre commande est en cours de traitement",
                String.format("""
                    Bonjour,
                    
                    Votre commande %s a bien été reçue.
                    Montant total : %.2f€
                    
                    Nous traitons votre paiement et vous tiendrons informé.
                    
                    L'équipe SmartDelivery
                    """, event.getOrderId(), event.getTotalAmount())
        );
    }

    @KafkaListener(
            topics = "payment.succeeded",
            groupId = "notification-service",
            containerFactory = "paymentSucceededListenerFactory"
    )
    public void handlePaymentSucceeded(PaymentSucceededEvent event) {
        log.info("[EMAIL] → userId:{} | Paiement {} accepté | Montant : {}€",
                event.getUserId(),
                event.getPaymentIntentId(),
                event.getAmount());

        sendEmail(
                "user-" + event.getUserId() + "@smartdelivery.com",
                "Votre paiement a été accepté",
                String.format("""
                    Bonjour,
                    
                    Votre paiement de %.2f€ a été accepté.
                    Référence : %s
                    
                    Votre commande est maintenant confirmée et en cours de préparation.
                    
                    L'équipe SmartDelivery
                    """, event.getAmount(), event.getPaymentIntentId())
        );
    }


    @KafkaListener(
            topics = "payment.failed",
            groupId = "notification-service",
            containerFactory = "paymentFailedListenerFactory"
    )
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("[EMAIL] → userId:{} | Paiement refusé pour commande {} | Raison : {}",
                event.getUserId(),
                event.getOrderId(),
                event.getFailureReason());

        sendEmail(
                "user-" + event.getUserId() + "@smartdelivery.com",
                "Votre paiement a été refusé",
                String.format("""
                    Bonjour,
                    
                    Malheureusement votre paiement pour la commande %s a été refusé.
                    Raison : %s
                    
                    Votre commande a été annulée et votre stock réservé libéré.
                    Veuillez réessayer avec un autre moyen de paiement.
                    
                    L'équipe SmartDelivery
                    """, event.getOrderId(), event.getFailureReason())
        );
    }

    // Send fake Email
    private void sendEmail(String to, String subject, String body) {
        log.info("""
                ╔══════════════════════════════════════
                ║ [SIMULATED EMAIL]
                ║ To      : {}
                ║ Subject : {}
                ║ Body    : {}
                ╚══════════════════════════════════════
                """, to, subject, body.trim());
    }
}