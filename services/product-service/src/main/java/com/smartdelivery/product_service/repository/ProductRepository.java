package com.smartdelivery.product_service.repository;

import com.smartdelivery.product_service.entity.Product;
import com.smartdelivery.product_service.entity.SkinType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByIdAndActiveTrue(UUID id);

    /**
     * Recherche multicritère. Chaque paramètre à null neutralise sa condition,
     * ce qui rend les filtres combinables — contrairement aux finders unitaires
     * qu'elle remplace, où un seul critère pouvait s'appliquer à la fois.
     */
    @Query("""
            SELECT p FROM Product p
            WHERE p.active = true
              AND (:categoryId IS NULL OR p.category.id = :categoryId)
              AND (:brand      IS NULL OR p.brand      = :brand)
              AND (:skinType   IS NULL OR p.skinType   = :skinType)
              AND (:minPrice   IS NULL OR p.price     >= :minPrice)
              AND (:maxPrice   IS NULL OR p.price     <= :maxPrice)
              AND (:inStock    IS NULL OR :inStock = FALSE OR p.stock > 0)
            """)
    List<Product> search(UUID categoryId,
                         String brand,
                         SkinType skinType,
                         BigDecimal minPrice,
                         BigDecimal maxPrice,
                         Boolean inStock);
}