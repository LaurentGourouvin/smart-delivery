package com.smartdelivery.delivery_service.controller;

import com.smartdelivery.delivery_service.dto.DeliveryResponse;
import com.smartdelivery.delivery_service.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Tag(name = "Deliveries", description = "Delivery tracking endpoints")
@SecurityRequirement(name = "bearerAuth")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get delivery by order ID")
    public ResponseEntity<DeliveryResponse> getByOrderId(@PathVariable UUID orderId) {
        return ResponseEntity.ok(deliveryService.getByOrderId(orderId));
    }
}