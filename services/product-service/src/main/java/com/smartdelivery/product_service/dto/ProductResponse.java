package com.smartdelivery.product_service.dto;

import com.smartdelivery.product_service.entity.SkinType;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        UUID categoryId,
        String categoryName,
        String name,
        String description,
        String brand,
        BigDecimal price,
        Integer stock,
        String imageUrl,
        SkinType skinType,
        Integer volumeMl,
        Boolean active
) {}