-- ─────────────────────────────────────────────────────────────
-- SmartDelivery — initialisation PostgreSQL
--
-- Une instance PostgreSQL unique (contrainte de RAM sur le VPS),
-- une BASE DE DONNÉES par service (isolation structurelle).
--
-- PostgreSQL ne sait pas joindre deux bases distinctes : une requête
-- cross-service est donc impossible, et non simplement interdite.
-- Voir docs/006-single-instance-database-per-service.md
-- ─────────────────────────────────────────────────────────────

-- Un rôle par service
CREATE USER user_svc     WITH PASSWORD 'user_svc_pass';
CREATE USER order_svc    WITH PASSWORD 'order_svc_pass';
CREATE USER product_svc  WITH PASSWORD 'product_svc_pass';
CREATE USER delivery_svc WITH PASSWORD 'delivery_svc_pass';
CREATE USER payment_svc  WITH PASSWORD 'payment_svc_pass';

-- Une base par service, dont le rôle du service est propriétaire.
-- Le propriétaire d'une base est membre implicite de pg_database_owner,
-- ce qui lui donne les droits de création dans le schéma public (PG 15+).
CREATE DATABASE user_service     OWNER user_svc;
CREATE DATABASE order_service    OWNER order_svc;
CREATE DATABASE product_service  OWNER product_svc;
CREATE DATABASE delivery_service OWNER delivery_svc;
CREATE DATABASE payment_service  OWNER payment_svc;

-- Retire le droit de connexion par défaut accordé à PUBLIC.
-- Sans cela, n'importe quel rôle de service pourrait se connecter
-- à la base d'un autre. Le propriétaire conserve ses droits.
REVOKE CONNECT ON DATABASE user_service     FROM PUBLIC;
REVOKE CONNECT ON DATABASE order_service    FROM PUBLIC;
REVOKE CONNECT ON DATABASE product_service  FROM PUBLIC;
REVOKE CONNECT ON DATABASE delivery_service FROM PUBLIC;
REVOKE CONNECT ON DATABASE payment_service  FROM PUBLIC;

-- notification-service n'a pas de base : consumer Kafka pur, sans état.
