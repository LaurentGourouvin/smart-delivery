package com.smartdelivery.delivery_service.controller;

import com.smartdelivery.delivery_service.dto.DeliveryResponse;
import com.smartdelivery.delivery_service.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryResponse> getByOrderId(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(deliveryService.getByOrderId(orderId));
    }
}