package com.smartdelivery.order_service.client;

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
        String skinType,
        Integer volumeMl,
        Boolean active
) {}