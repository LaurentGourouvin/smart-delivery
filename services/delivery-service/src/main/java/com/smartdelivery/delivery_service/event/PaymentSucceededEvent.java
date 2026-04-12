package com.smartdelivery.delivery_service.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

// Publié par payment-service quand un paiement est validé
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSucceededEvent {
    private UUID   orderId;
    private UUID   userId;
    private BigDecimal amount;
}