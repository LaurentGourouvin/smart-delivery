-- ============================================================
-- V1__init_product_service.sql
-- Migration initiale product-service
-- Crée les tables categories et products
-- ============================================================

-- ──────────────────────────────────────────
-- TABLE : categories
-- ──────────────────────────────────────────
CREATE TABLE categories (
        id          UUID         NOT NULL DEFAULT gen_random_uuid(),
        name        VARCHAR(100) NOT NULL,
        slug        VARCHAR(100) NOT NULL,
        description TEXT,
        created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),

        CONSTRAINT pk_categories PRIMARY KEY (id),
        CONSTRAINT uq_categories_name UNIQUE (name),
        CONSTRAINT uq_categories_slug UNIQUE (slug)
);

-- ──────────────────────────────────────────
-- TABLE : products
-- ──────────────────────────────────────────
CREATE TABLE products (
          id          UUID           NOT NULL DEFAULT gen_random_uuid(),
          category_id UUID           NOT NULL,
          name        VARCHAR(255)   NOT NULL,
          description TEXT,
          brand       VARCHAR(100)   NOT NULL,
          price       NUMERIC(10,2)  NOT NULL,
          stock       INTEGER        NOT NULL DEFAULT 0,
          version     BIGINT         NOT NULL DEFAULT 0,
          image_url   VARCHAR(500),
          skin_type   VARCHAR(20)    NOT NULL DEFAULT 'ALL',
          volume_ml   INTEGER,
          active      BOOLEAN        NOT NULL DEFAULT TRUE,
          created_at  TIMESTAMP      NOT NULL DEFAULT NOW(),
          updated_at  TIMESTAMP      NOT NULL DEFAULT NOW(),

          CONSTRAINT pk_products PRIMARY KEY (id),
          CONSTRAINT fk_products_category FOREIGN KEY (category_id)
              REFERENCES categories (id)
              ON DELETE RESTRICT,
          CONSTRAINT chk_products_stock CHECK (stock >= 0),
          CONSTRAINT chk_products_price CHECK (price > 0),
          CONSTRAINT chk_products_skin_type CHECK (skin_type IN (
             'OILY', 'DRY', 'COMBINATION', 'SENSITIVE', 'ALL'
          ))
);

-- ──────────────────────────────────────────
-- INDEX
-- ──────────────────────────────────────────
CREATE INDEX idx_products_category_id ON products (category_id);
CREATE INDEX idx_products_active ON products (active);
CREATE INDEX idx_products_brand ON products (brand);
CREATE INDEX idx_products_skin_type ON products (skin_type);

-- ──────────────────────────────────────────
-- DONNÉES INITIALES — catégories K-beauty
-- ──────────────────────────────────────────
INSERT INTO categories (name, slug, description) VALUES
     ('Sérum', 'serum', 'Sérums concentrés pour cibler des problèmes spécifiques'),
     ('Crème', 'creme', 'Crèmes hydratantes et nourrissantes'),
     ('Toner', 'toner', 'Toniques pour préparer la peau aux soins suivants'),
     ('Masque', 'masque', 'Masques en tissu et masques de nuit'),
     ('Essence', 'essence', 'Essences légères pour booster l''hydratation'),
     ('Nettoyant', 'nettoyant', 'Nettoyants doux respectueux du microbiome'),
     ('Crème solaire', 'creme-solaire', 'Protections solaires SPF coréennes'),
     ('Contour des yeux', 'contour-yeux', 'Soins spécifiques pour le contour des yeux');