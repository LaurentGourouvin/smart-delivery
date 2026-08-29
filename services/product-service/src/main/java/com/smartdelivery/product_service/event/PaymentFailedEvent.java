package com.smartdelivery.product_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Publié par payment-service. Redéfini ici plutôt que partagé : chaque service
 * garde sa propre vue du contrat, ce qui évite de recoupler les services au
 * moment du build.
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
