CREATE SCHEMA IF NOT EXISTS user_service;
CREATE SCHEMA IF NOT EXISTS order_service;
CREATE SCHEMA IF NOT EXISTS product_service;
CREATE SCHEMA IF NOT EXISTS delivery_service;
CREATE SCHEMA IF NOT EXISTS payment_service;

CREATE USER user_svc WITH PASSWORD 'user_svc_pass';
CREATE USER order_svc WITH PASSWORD 'order_svc_pass';
CREATE USER product_svc WITH PASSWORD 'product_svc_pass';
CREATE USER delivery_svc WITH PASSWORD 'delivery_svc_pass';
CREATE USER payment_svc WITH PASSWORD 'payment_svc_pass';

GRANT CONNECT ON DATABASE smartdelivery TO user_svc;
GRANT CONNECT ON DATABASE smartdelivery TO order_svc;
GRANT CONNECT ON DATABASE smartdelivery TO product_svc;
GRANT CONNECT ON DATABASE smartdelivery TO delivery_svc;
GRANT CONNECT ON DATABASE smartdelivery TO payment_svc;

GRANT ALL PRIVILEGES ON SCHEMA user_service TO user_svc;
GRANT ALL PRIVILEGES ON SCHEMA order_service TO order_svc;
GRANT ALL PRIVILEGES ON SCHEMA product_service TO product_svc;
GRANT ALL PRIVILEGES ON SCHEMA delivery_service TO delivery_svc;
GRANT ALL PRIVILEGES ON SCHEMA payment_service TO payment_svc;

ALTER DEFAULT PRIVILEGES IN SCHEMA user_service GRANT ALL ON TABLES TO user_svc;
ALTER DEFAULT PRIVILEGES IN SCHEMA order_service GRANT ALL ON TABLES TO order_svc;
ALTER DEFAULT PRIVILEGES IN SCHEMA product_service GRANT ALL ON TABLES TO product_svc;
ALTER DEFAULT PRIVILEGES IN SCHEMA delivery_service GRANT ALL ON TABLES TO delivery_svc;
ALTER DEFAULT PRIVILEGES IN SCHEMA payment_service GRANT ALL ON TABLES TO payment_svc;