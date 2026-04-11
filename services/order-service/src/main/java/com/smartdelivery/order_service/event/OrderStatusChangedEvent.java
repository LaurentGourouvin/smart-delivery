package com.smartdelivery.order_service.event;

import com.smartdelivery.order_service.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusChangedEvent {

    private UUID orderId;
    private UUID userId;
    private OrderStatus oldStatus;
    private OrderStatus newStatus;
}