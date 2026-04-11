-- ============================================================
-- V1__init_order_service.sql
-- Migration initiale order-service
-- Crée les tables orders et order_items
-- ============================================================

-- ──────────────────────────────────────────
-- TABLE : orders
-- ──────────────────────────────────────────
CREATE TABLE orders (
    id                   UUID           NOT NULL DEFAULT gen_random_uuid(),
    user_id              UUID           NOT NULL,
    user_email           VARCHAR(255)   NOT NULL,
    status               VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    total_amount         NUMERIC(10,2)  NOT NULL,
    delivery_street      VARCHAR(255)   NOT NULL,
    delivery_city        VARCHAR(100)   NOT NULL,
    delivery_postal_code VARCHAR(10)    NOT NULL,
    delivery_country     VARCHAR(2)     NOT NULL DEFAULT 'FR',
    created_at           TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP      NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_orders PRIMARY KEY (id),
    CONSTRAINT chk_orders_status CHECK (status IN (
        'PENDING', 'CONFIRMED', 'CANCELLED', 'DELIVERED'
    )),
    CONSTRAINT chk_orders_total CHECK (total_amount >= 0)
);

-- ──────────────────────────────────────────
-- TABLE : order_items
-- ──────────────────────────────────────────
CREATE TABLE order_items (
    id           UUID          NOT NULL DEFAULT gen_random_uuid(),
    order_id     UUID          NOT NULL,
    product_id   UUID          NOT NULL,
    product_name VARCHAR(255)  NOT NULL,
    unit_price   NUMERIC(10,2) NOT NULL,
    quantity     INTEGER       NOT NULL,
    subtotal     NUMERIC(10,2) NOT NULL,

    CONSTRAINT pk_order_items PRIMARY KEY (id),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id)
        REFERENCES orders (id)
        ON DELETE CASCADE,
    CONSTRAINT chk_order_items_quantity CHECK (quantity > 0),
    CONSTRAINT chk_order_items_unit_price CHECK (unit_price > 0),
    CONSTRAINT chk_order_items_subtotal CHECK (subtotal > 0)
);

-- ──────────────────────────────────────────
-- INDEX
-- ──────────────────────────────────────────
CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_orders_created_at ON orders (created_at DESC);
CREATE INDEX idx_order_items_order_id ON order_items (order_id);
CREATE INDEX idx_order_items_product_id ON order_items (product_id);
