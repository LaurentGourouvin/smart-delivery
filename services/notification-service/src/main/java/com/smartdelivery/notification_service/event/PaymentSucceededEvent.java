package com.smartdelivery.notification_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSucceededEvent {
    private UUID paymentId;
    private UUID orderId;
    private UUID userId;
    private String paymentIntentId;
    private BigDecimal amount;
}