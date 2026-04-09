package com.smartdelivery.user_service.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phone,
        Boolean active,
        LocalDateTime createdAt,
        List<AddressResponse> addresses
) {}