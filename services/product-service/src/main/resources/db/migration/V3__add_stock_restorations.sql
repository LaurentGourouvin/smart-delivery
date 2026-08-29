-- Trace des restitutions de stock déjà appliquées.
--
-- Garantit l'idempotence de la compensation Saga : Kafka livre at-least-once,
-- un même payment.failed peut donc être reçu plusieurs fois. Sans cette trace,
-- le stock serait restitué autant de fois que l'événement est relivré.
--
-- La clé primaire sur order_id fait porter la garantie par la base : deux
-- traitements concurrents du même événement ne peuvent pas passer tous les deux.

CREATE TABLE stock_restorations (
    order_id    UUID PRIMARY KEY,
    restored_at TIMESTAMP NOT NULL DEFAULT NOW()
);
