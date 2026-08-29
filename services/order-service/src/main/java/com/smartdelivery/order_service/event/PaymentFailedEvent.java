package com.smartdelivery.order_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Publié par payment-service quand le paiement échoue.
 *
 * Porte les items de la commande (fat event) : order-service dispose ainsi de
 * tout ce qu'il faut pour déclencher la compensation sans rappeler personne.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFailedEvent {
    private UUID orderId;
    private UUID userId;
    private String failureReason;
    private List<OrderItemEvent> items;
}
