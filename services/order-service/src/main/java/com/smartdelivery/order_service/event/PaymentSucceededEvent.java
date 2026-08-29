package com.smartdelivery.order_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Publié par payment-service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSucceededEvent {
    private UUID paymentId;
    private UUID orderId;
    private UUID userId;
    private String paymentIntentId;
    private BigDecimal amount;
}
