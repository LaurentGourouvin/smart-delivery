CREATE SCHEMA IF NOT EXISTS user_service;
CREATE SCHEMA IF NOT EXISTS order_service;
CREATE SCHEMA IF NOT EXISTS product_service;
CREATE SCHEMA IF NOT EXISTS delivery_service;
CREATE SCHEMA IF NOT EXISTS payment_service;

-- Un utilisateur dédié par service
CREATE USER user_svc WITH PASSWORD 'user_svc_pass';
CREATE USER order_svc WITH PASSWORD 'order_svc_pass';
CREATE USER product_svc WITH PASSWORD 'product_svc_pass';
CREATE USER delivery_svc WITH PASSWORD 'delivery_svc_pass';
CREATE USER payment_svc WITH PASSWORD 'payment_svc_pass';

-- Chaque utilisateur n'accède qu'à son propre schema
GRANT ALL PRIVILEGES ON SCHEMA user_service TO user_svc;
GRANT ALL PRIVILEGES ON SCHEMA order_service TO order_svc;
GRANT ALL PRIVILEGES ON SCHEMA product_service TO product_svc;
GRANT ALL PRIVILEGES ON SCHEMA delivery_service TO delivery_svc;
GRANT ALL PRIVILEGES ON SCHEMA payment_service TO payment_svc;