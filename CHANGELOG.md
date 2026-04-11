# Changelog

All notable changes to SmartDelivery are documented here.
Format based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

---

## [Unreleased]

### In progress
- `delivery-service` — WebSocket real-time tracking, dernier service

---

## [0.5.0] — 2026-04-11

### Added
- `notification-service` — Kafka consumer pur
  - 3 `@KafkaListener` — `order.created`, `payment.succeeded`, `payment.failed`
  - `KafkaConsumerConfig` — 3 factories typées par topic
  - Emails simulés dans les logs avec format ╔══ bordure
  - Pas de base de données, pas d'API REST, pas d'OAuth2
  - 3 tests unitaires
  - Validé end-to-end — rattrapé tous les anciens messages au démarrage

---

## [0.4.0] — 2026-04-11

### Added
- `payment-service` — Saga pattern complet
  - `@KafkaListener` sur `order.created` — premier vrai consumer Kafka
  - Simulation 90% succès / 10% échec aléatoire
  - Idempotence via `existsByOrderId` — protection contre les doublons Kafka
  - Publie `payment.succeeded` ou `payment.failed`
  - `PaymentController` — GET `/api/payments/order/:orderId`
  - 4 tests unitaires Mockito (idempotence, success, failure)
  - Validé end-to-end : 5 commandes → 4 succeeded + 1 failed
- Architecture hexagonale — discussion et documentation du pattern Ports & Adapters
- Anti-Corruption Layer pattern documenté (`ProductServiceClient`, `StripeClient` concept)

### Fixed
- Kafka deserialization : `spring.json.value.default.type` ajouté pour les consumers sans type headers

---

## [0.3.0] — 2026-04-11

### Added
- `order-service` — gestion du cycle de vie des commandes
  - Entités `Order`, `OrderItem`, `OrderStatus` (enum)
  - CQRS simplifié — commandes (write) et queries (read) séparées
  - `ProductServiceClient` — Anti-Corruption Layer vers `product-service`
  - Token propagation — JWT utilisateur propagé aux appels inter-services
  - Fat events Kafka — `OrderCreatedEvent`, `OrderStatusChangedEvent`
  - Topics Kafka : `order.created`, `order.status-changed`
  - Endpoints REST : `GET /me`, `GET /:id`, `POST /`, `PATCH /:id/status`
  - Swagger UI sur `http://localhost:8082/swagger-ui.html`
  - 6 tests unitaires Mockito (KafkaTemplate + ProductServiceClient mockés)
  - Validé end-to-end : commande créée → stock décrémenté → event visible Kafka-UI
- Kafka-UI fonctionnel — listener INTERNAL ajouté pour communication Docker
- Collection Postman complète — scripts automatiques pour token, product_id, order_id, category_id, address_id

### Fixed
- Kafka double listener : `PLAINTEXT://localhost:9092` (local) + `INTERNAL://kafka:29092` (Docker)

---

## [0.2.0] — 2026-04-11

### Added
- `product-service` — catalogue K-beauty complet
  - Entités `Product`, `Category`, `SkinType` (enum)
  - Optimistic Lock (`@Version`) sur le stock
  - 8 catégories K-beauty seedées via Flyway
  - Endpoints REST : catalogue, filtres (catégorie, marque, type de peau, stock)
  - Gestion stock : `updateStock`, `decrementStock`, `restoreStock` (Saga pattern)
  - Swagger UI sur `http://localhost:8083/swagger-ui.html`
  - 8 tests unitaires Mockito
- GitHub Actions CI/CD
  - `ci-develop.yml` : compile + tests unitaires sur PR → develop
  - `ci-main.yml` : compile + tests + CodeQL SAST sur PR → main
  - Matrix strategy : exécution parallèle par service
- Collection Postman complète (user-service + product-service)
- `CHANGELOG.md` ajouté

### Fixed
- `user-service` + `product-service` : `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` sur toutes les entités JPA
- CSRF : remplacement du `disable` total par exclusion sélective sur endpoints publics (fix CodeQL High)

### Infrastructure
- Docker ports liés à `127.0.0.1` — sécurité dev local
- Kafka migré en mode KRaft (suppression de Zookeeper)
- Keycloak : export realm `smartdelivery` + import automatique au démarrage

---

## [0.1.0] — 2026-04-10

### Added
- `user-service` — gestion des comptes utilisateurs
  - Entités `User`, `Address`
  - Provisioning automatique au premier login (JWT Keycloak → claims)
  - Endpoints REST : profil (`/me`), adresses (`/me/addresses`)
  - Swagger UI sur `http://localhost:8081/swagger-ui.html`
  - 6 tests unitaires Mockito
  - Validé end-to-end : JWT → provisioning → réponse JSON
- Keycloak configuré
  - Realm `smartdelivery`
  - Client `smart-delivery-app` (confidential, direct access grants)
  - Utilisateur de test : `laurent@smartdelivery.com`
- Infrastructure Docker Compose Phase 1
  - Traefik, Keycloak, PostgreSQL, Redis, Kafka (KRaft), Kafka-UI
  - Schemas PostgreSQL isolés par service
  - Scripts d'initialisation avec permissions par service

---

## [0.0.1] — 2026-04-09

### Added
- Initialisation du projet SmartDelivery
- Structure monorepo : `services/`, `infra/`, `docs/`, `shared/`, `.github/`
- `README.md` — vitrine GitHub en anglais
- `CLAUDE.md` — contexte IA du projet
- `LICENSE` — MIT
- Architecture Decision Records (ADR) :
  - ADR-001 : Docker Swarm over Kubernetes
  - ADR-002 : PostgreSQL for all services
  - ADR-003 : Kafka and REST coexistence
  - ADR-004 : Saga choreography over orchestration
  - ADR-005 : Asymmetric JWT with Keycloak JWKS
