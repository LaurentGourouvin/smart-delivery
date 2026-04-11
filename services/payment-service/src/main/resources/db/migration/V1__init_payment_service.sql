-- ============================================================
-- V1__init_payment_service.sql
-- Migration initiale payment-service
-- Crée la table payments
-- ============================================================

CREATE TABLE payments (
    id                 UUID          NOT NULL DEFAULT gen_random_uuid(),
    order_id           UUID          NOT NULL,
    user_id            UUID          NOT NULL,
    payment_intent_id  VARCHAR(100)  NOT NULL,
    amount             NUMERIC(10,2) NOT NULL,
    status             VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    failure_reason     VARCHAR(255),
    created_at         TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMP     NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_payments PRIMARY KEY (id),
    CONSTRAINT uq_payments_order_id UNIQUE (order_id),
    CONSTRAINT uq_payments_intent_id UNIQUE (payment_intent_id),
    CONSTRAINT chk_payments_status CHECK (status IN (
        'PENDING', 'SUCCEEDED', 'FAILED'
    )),
    CONSTRAINT chk_payments_amount CHECK (amount > 0)
);

-- ──────────────────────────────────────────
-- INDEX
-- ──────────────────────────────────────────
CREATE INDEX idx_payments_order_id ON payments (order_id);
CREATE INDEX idx_payments_user_id ON payments (user_id);
CREATE INDEX idx_payments_status ON payments (status);
