package com.smartdelivery.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAddressRequest(
        @NotBlank String label,
        @NotBlank String street,
        @NotBlank String city,
        @NotBlank @Size(min = 4, max = 10) String postalCode,
        String country,
        Boolean isDefault
) {}