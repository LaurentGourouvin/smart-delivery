package com.smartdelivery.product_service.repository;

import com.smartdelivery.product_service.entity.Product;
import com.smartdelivery.product_service.entity.SkinType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByCategoryIdAndActiveTrue(UUID categoryId);
    List<Product> findByBrandAndActiveTrue(String brand);
    List<Product> findBySkinTypeAndActiveTrue(SkinType skinType);
    List<Product> findByActiveTrue();
    Optional<Product> findByIdAndActiveTrue(UUID id);
    @Query("SELECT p FROM Product p WHERE p.stock > 0 AND p.active = true")
    List<Product> findAllInStock();
}