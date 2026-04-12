package com.smartdelivery.delivery_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "deliveries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    // Référence logique vers order-service — pas de FK physique (isolation microservices)
    @Column(name = "order_id", nullable = false, updatable = false)
    private UUID orderId;

    // Stocké pour cibler le push WebSocket sans appel inter-services
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private DeliveryStatus status = DeliveryStatus.ASSIGNED;

    @Column(name = "carrier", nullable = false)
    @Builder.Default
    private String carrier = "SmartExpress";

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

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt  = now;
        this.assignedAt = now;
        this.updatedAt  = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}