package com.smartdelivery.delivery_service.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "deliveries",
        schema = "delivery_service",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_deliveries_order_id",  columnNames = "order_id"),
                @UniqueConstraint(name = "uq_deliveries_tracking",  columnNames = "tracking_number")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @Column(nullable = false, updatable = false)
    private UUID id;

    // Référence logique vers order-service — pas de FK physique
    @Column(name = "order_id", nullable = false, updatable = false)
    private UUID orderId;

    // Stocké pour cibler le push WebSocket sans appel inter-services
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "delivery_status")
    private DeliveryStatus status;

    @Column(nullable = false)
    private String carrier;

    @Column(name = "tracking_number", nullable = false)
    private String trackingNumber;

    @Column(name = "estimated_delivery", nullable = false)
    private LocalDateTime estimatedDelivery;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    // Optimistic Lock — protège contre les mises à jour concurrentes du @Scheduled
    @Version
    @Column(nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt  = now;
        this.assignedAt = now;
        this.updatedAt  = now;
    }
}