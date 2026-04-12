-- ============================================================
-- V1__init_delivery_service.sql
-- Migration initiale delivery-service
-- Le schéma delivery_service est créé par infra/postgres/init.sql
-- ============================================================

-- ──────────────────────────────────────────
-- TABLE : deliveries
-- ──────────────────────────────────────────
CREATE TABLE deliveries (
                            id                  UUID          NOT NULL DEFAULT gen_random_uuid(),
                            order_id            UUID          NOT NULL,
                            user_id             UUID          NOT NULL,
                            status              VARCHAR(20)   NOT NULL DEFAULT 'ASSIGNED',
                            carrier             VARCHAR(100)  NOT NULL DEFAULT 'SmartExpress',
                            tracking_number     VARCHAR(100)  NOT NULL,
                            estimated_delivery  TIMESTAMP     NOT NULL,
                            assigned_at         TIMESTAMP     NOT NULL DEFAULT NOW(),
                            delivered_at        TIMESTAMP,
                            version             BIGINT        NOT NULL DEFAULT 0,
                            created_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
                            updated_at          TIMESTAMP     NOT NULL DEFAULT NOW(),

                            CONSTRAINT pk_deliveries          PRIMARY KEY (id),
                            CONSTRAINT uq_deliveries_order_id UNIQUE (order_id),
                            CONSTRAINT uq_deliveries_tracking UNIQUE (tracking_number),
                            CONSTRAINT chk_deliveries_status  CHECK (status IN (
                                                                                'ASSIGNED', 'PICKED_UP', 'IN_TRANSIT', 'DELIVERED'
                                ))
);

-- ──────────────────────────────────────────
-- INDEX
-- ──────────────────────────────────────────
CREATE INDEX idx_deliveries_order_id ON deliveries (order_id);
CREATE INDEX idx_deliveries_status   ON deliveries (status);
CREATE INDEX idx_deliveries_user_id  ON deliveries (user_id);