package com.smartdelivery.product_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Marque une commande dont le stock a déjà été restitué.
 *
 * L'identifiant est l'orderId lui-même : l'unicité de la clé primaire suffit
 * à rendre la compensation idempotente, sans verrou applicatif.
 */
@Entity
@Table(name = "stock_restorations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StockRestoration {

    @Id
    @Column(name = "order_id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private UUID orderId;

    @Column(name = "restored_at", nullable = false)
    private LocalDateTime restoredAt;

    @PrePersist
    protected void onCreate() {
        this.restoredAt = LocalDateTime.now();
    }
}
