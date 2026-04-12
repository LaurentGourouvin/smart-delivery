package com.smartdelivery.delivery_service.dto;

import com.smartdelivery.delivery_service.entity.DeliveryStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryResponse(
        UUID           id,
        UUID           orderId,
        DeliveryStatus status,
        String         carrier,
        String         trackingNumber,
        LocalDateTime  estimatedDelivery,
        LocalDateTime  assignedAt,
        LocalDateTime  deliveredAt,
        LocalDateTime  updatedAt
) {}