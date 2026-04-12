package com.smartdelivery.delivery_service.repository;

import com.smartdelivery.delivery_service.entity.Delivery;
import com.smartdelivery.delivery_service.entity.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {

    boolean existsByOrderId(UUID orderId);
    Optional<Delivery> findByOrderId(UUID orderId);
    List<Delivery> findAllByStatusNot(DeliveryStatus status);
}