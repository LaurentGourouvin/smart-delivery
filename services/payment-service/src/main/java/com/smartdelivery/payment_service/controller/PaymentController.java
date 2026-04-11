package com.smartdelivery.payment_service.controller;

import com.smartdelivery.payment_service.entity.Payment;
import com.smartdelivery.payment_service.exception.PaymentNotFoundException;
import com.smartdelivery.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentRepository paymentRepository;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Payment> getPaymentByOrderId(@PathVariable UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new PaymentNotFoundException(orderId));
    }
}