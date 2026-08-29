package com.smartdelivery.product_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Fait accompli : le stock de la commande a été restitué.
 *
 * Publié après la compensation, pour que d'autres services puissent réagir
 * sans que product-service ait à les connaître.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockRestoredEvent {
    private UUID orderId;
    private List<OrderItemEvent> items;
}
