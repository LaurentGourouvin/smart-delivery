-- ============================================================
-- V1__init_delivery_service.sql
-- Migration initiale delivery-service
-- Crée le schéma delivery_service et la table deliveries
-- ============================================================

CREATE SCHEMA IF NOT EXISTS delivery_service;

SET search_path TO delivery_service;

-- ──────────────────────────────────────────
-- ENUM : statuts de livraison
-- ──────────────────────────────────────────
CREATE TYPE delivery_status AS ENUM (
    'ASSIGNED',
    'PICKED_UP',
    'IN_TRANSIT',
    'DELIVERED'
);

-- ──────────────────────────────────────────
-- TABLE : deliveries
-- ──────────────────────────────────────────
CREATE TABLE deliveries (
    id                  UUID            NOT NULL DEFAULT gen_random_uuid(),
    order_id            UUID            NOT NULL,
    user_id             UUID            NOT NULL,
    status              delivery_status NOT NULL DEFAULT 'ASSIGNED',
    carrier             VARCHAR(100)    NOT NULL DEFAULT 'SmartExpress',
    tracking_number     VARCHAR(100)    NOT NULL,
    estimated_delivery  TIMESTAMP       NOT NULL,
    assigned_at         TIMESTAMP       NOT NULL DEFAULT NOW(),
    delivered_at        TIMESTAMP,
    version             BIGINT          NOT NULL DEFAULT 0,
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_deliveries            PRIMARY KEY (id),
    CONSTRAINT uq_deliveries_order_id   UNIQUE (order_id),
    CONSTRAINT uq_deliveries_tracking   UNIQUE (tracking_number)
);

-- ──────────────────────────────────────────
-- INDEX
-- ──────────────────────────────────────────
CREATE INDEX idx_deliveries_order_id ON deliveries (order_id);
CREATE INDEX idx_deliveries_status   ON deliveries (status);
CREATE INDEX idx_deliveries_user_id  ON deliveries (user_id);
