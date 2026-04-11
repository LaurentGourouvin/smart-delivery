package com.smartdelivery.product_service.dto;

import com.smartdelivery.product_service.entity.SkinType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductRequest(
        UUID categoryId,
        @NotBlank String name,
        String description,
        @NotBlank String brand,
        @Positive BigDecimal price,
        String imageUrl,
        SkinType skinType,
        Integer volumeMl,
        Boolean active
) {}