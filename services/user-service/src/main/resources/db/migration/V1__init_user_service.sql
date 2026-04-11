-- ============================================================
-- V1__init_user_service.sql
-- Migration initiale user-service
-- Crée les tables users et addresses
-- ============================================================

-- ──────────────────────────────────────────
-- TABLE : users
-- ──────────────────────────────────────────
CREATE TABLE users (
    id           UUID         NOT NULL DEFAULT gen_random_uuid(),
    keycloak_id  UUID         NOT NULL,
    email        VARCHAR(255) NOT NULL,
    first_name   VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    phone        VARCHAR(20),
    active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_keycloak_id UNIQUE (keycloak_id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

-- ──────────────────────────────────────────
-- TABLE : addresses
-- ──────────────────────────────────────────
CREATE TABLE addresses (
    id          UUID         NOT NULL DEFAULT gen_random_uuid(),
    user_id     UUID         NOT NULL,
    label       VARCHAR(50)  NOT NULL,
    street      VARCHAR(255) NOT NULL,
    city        VARCHAR(100) NOT NULL,
    postal_code VARCHAR(10)  NOT NULL,
    country     VARCHAR(2)   NOT NULL DEFAULT 'FR',
    is_default  BOOLEAN      NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_addresses PRIMARY KEY (id),
    CONSTRAINT fk_addresses_user FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);

-- ──────────────────────────────────────────
-- INDEX
-- ──────────────────────────────────────────

-- Recherche fréquente par keycloak_id (à chaque requête authentifiée)
CREATE INDEX idx_users_keycloak_id ON users (keycloak_id);

-- Recherche des adresses d'un utilisateur
CREATE INDEX idx_addresses_user_id ON addresses (user_id);

-- Retrouver rapidement l'adresse par défaut d'un utilisateur
CREATE INDEX idx_addresses_user_default ON addresses (user_id, is_default);
