package com.smartdelivery.delivery_service.event;

import com.smartdelivery.delivery_service.entity.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

// Consommé par notification-service
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryUpdatedEvent {
    private UUID           deliveryId;
    private UUID           orderId;
    private UUID           userId;
    private DeliveryStatus status;
    private String         trackingNumber;
    private LocalDateTime  estimatedDelivery;
    private LocalDateTime  updatedAt;
}