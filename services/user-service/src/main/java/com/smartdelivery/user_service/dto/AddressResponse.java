package com.smartdelivery.user_service.dto;

import java.util.UUID;

public record AddressResponse(
        UUID id,
        String label,
        String street,
        String city,
        String postalCode,
        String country,
        Boolean isDefault
) {}