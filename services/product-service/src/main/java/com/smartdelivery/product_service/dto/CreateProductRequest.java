package com.smartdelivery.product_service.dto;

import com.smartdelivery.product_service.entity.SkinType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductRequest(
        @NotNull UUID categoryId,
        @NotBlank String name,
        String description,
        @NotBlank String brand,
        @NotNull @Positive BigDecimal price,
        @NotNull @Min(0) Integer stock,
        String imageUrl,
        SkinType skinType,
        Integer volumeMl
) {}