package com.smartdelivery.order_service.dto;

import com.smartdelivery.order_service.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID userId,
        String userEmail,
        OrderStatus status,
        BigDecimal totalAmount,
        String deliveryStreet,
        String deliveryCity,
        String deliveryPostalCode,
        String deliveryCountry,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {}