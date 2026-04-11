package com.smartdelivery.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(
        @NotNull @Valid DeliveryAddressRequest deliveryAddress,
        @NotEmpty @Valid List<OrderItemRequest> items
) {}