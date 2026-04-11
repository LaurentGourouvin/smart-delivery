package com.smartdelivery.order_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DeliveryAddressRequest(
        @NotBlank String street,
        @NotBlank String city,
        @NotBlank @Size(min = 4, max = 10) String postalCode,
        String country
) {}