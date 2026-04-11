package com.smartdelivery.payment_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

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