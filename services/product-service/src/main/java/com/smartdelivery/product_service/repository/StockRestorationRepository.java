package com.smartdelivery.product_service.repository;

import com.smartdelivery.product_service.entity.StockRestoration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StockRestorationRepository extends JpaRepository<StockRestoration, UUID> {
}
