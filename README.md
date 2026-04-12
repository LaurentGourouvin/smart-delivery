# SmartDelivery

> E-commerce order management platform built with a microservices architecture.
> Showcase project demonstrating distributed systems patterns, event-driven architecture, and DevOps practices.

![CI](https://github.com/LaurentGourouvin/smart-delivery/actions/workflows/ci-main.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-green)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-KRaft-black)
![Vue](https://img.shields.io/badge/Vue-3.x-brightgreen)
![Docker](https://img.shields.io/badge/Docker%20Swarm-ready-blue)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

---

## Overview

SmartDelivery is a fully simulated K-beauty e-commerce delivery platform. Users can browse products, place orders, track deliveries in real time, and receive notifications — all handled by six independent microservices communicating via REST and Kafka events.

The project is designed as a **technical showcase** covering:
- Microservices architecture with clear domain boundaries
- Event-driven communication (Apache Kafka — KRaft mode)
- Distributed transaction management (Saga pattern — choreography)
- Real-time delivery tracking (WebSocket)
- Centralized authentication and authorization (Keycloak / OAuth2 / JWT RS256)
- Full observability stack (Prometheus, Grafana, Jaeger, ELK)
- Container orchestration (Docker Swarm)

---

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                    Clients                          │
│           Vue 3 + Vite + TypeScript                 │
└────────────────────┬────────────────────────────────┘
                     │ HTTPS
┌────────────────────▼────────────────────────────────┐
│                   Traefik                           │
│         API Gateway · OAuth2 · Rate limiting        │
└──┬──────────┬──────────┬──────────┬─────────────────┘
   │ REST     │ REST     │ REST     │ REST
┌──▼──┐  ┌───▼──┐  ┌────▼──┐  ┌───▼──────┐
│user │  │order │  │product│  │ payment  │
│svc  │  │ svc  │  │  svc  │  │   svc    │
└──┬──┘  └───┬──┘  └────┬──┘  └───┬──────┘
   │         │           │         │
   └─────────┴─────┬─────┴─────────┘
                   │ Kafka events
        ┌──────────▼──────────┐
        │   Apache Kafka      │
        │  order.created      │
        │  payment.succeeded  │
        │  payment.failed     │
        │  delivery.updated   │
        └──────┬──────────────┘
               │
    ┌──────────┴──────────┐
    │                     │
┌───▼────────┐   ┌────────▼───────┐
│  delivery  │   │ notification   │
│    svc     │   │     svc        │
│ (WebSocket)│   │ (email / push) │
└────────────┘   └────────────────┘
```

---

## Services

| Service | Port | Responsibility | Key patterns |
|---|---|---|---|
| `user-service` | 8081 | User accounts, authentication | OAuth2, Keycloak, Spring Security |
| `order-service` | 8082 | Order lifecycle management | CQRS, Kafka producer, Anti-Corruption Layer |
| `product-service` | 8083 | Product catalogue, stock management | Optimistic Lock (`@Version`) |
| `delivery-service` | 8084 | Simulated real-time delivery tracking | WebSocket, `@Scheduled`, Kafka consumer |
| `notification-service` | 8085 | Email / push notifications | Kafka consumer pur, stateless, no DB |
| `payment-service` | 8086 | Simulated payment processing | Saga pattern (choreography), idempotence |

---

## Tech Stack

### Backend
- **Java 21** + **Spring Boot 3.5.x** — all services
- **Spring Data JPA** + **Hibernate** + **Flyway** — persistence and migrations
- **Spring Security** + **Keycloak** — OAuth2 / OIDC / JWT (RS256, JWKS)
- **Spring Kafka** — event-driven communication (KRaft mode, no Zookeeper)
- **Spring WebSocket** — real-time delivery tracking
- **PostgreSQL 16** — all service databases (one schema per service)
- **Redis 7** — shared cache and session store
- **Springdoc OpenAPI** — Swagger UI exposed by every service

### Frontend
- **Vue 3** + **Vite** + **TypeScript**
- **PrimeVue** + **PrimeIcons** — UI component library
- **Pinia** — state management
- **Vue Router 5** — SPA routing
- **Fetch API** — native HTTP client (zero dependency)

### Infrastructure
- **Docker Swarm** — container orchestration
- **Traefik** — API Gateway, routing, SSL termination
- **Apache Kafka** (KRaft) — event bus, no Zookeeper
- **Keycloak 24** — identity provider
- **GitHub Actions** — CI/CD pipelines (unit tests + CodeQL SAST)
- **GitHub Container Registry** — Docker image storage

### Observability
- **Prometheus** + **Grafana** — metrics and dashboards
- **Jaeger** — distributed tracing across services
- **ELK Stack** — centralized logging with correlation ID

---

## Architecture Decision Records

Key technical decisions are documented in [`docs/adr/`](docs/adr/):

- [ADR-001](docs/adr/001-docker-swarm-over-kubernetes.md) — Docker Swarm over Kubernetes
- [ADR-002](docs/adr/002-postgresql-for-all-services.md) — PostgreSQL for all services
- [ADR-003](docs/adr/003-kafka-and-rest-coexistence.md) — Kafka and REST coexistence
- [ADR-004](docs/adr/004-saga-choreography-over-orchestration.md) — Saga choreography over orchestration
- [ADR-005](docs/adr/005-asymmetric-jwt-keycloak.md) — Asymmetric JWT with Keycloak JWKS

---

## Getting Started

### Prerequisites

- Docker Desktop
- Java 21
- Maven 3.9+
- Node.js 20+

### Run locally (Docker Compose)

```bash
# Clone the repository
git clone https://github.com/LaurentGourouvin/smart-delivery
cd smart-delivery

# Start infrastructure (Kafka, PostgreSQL, Keycloak, Redis, Kafka-UI)
docker compose -f infra/docker-compose.yml up -d

# Start each service from IntelliJ or via Maven
cd services/user-service && mvn spring-boot:run

# Start the frontend
cd frontend && npm install && npm run dev
```

Services will be available at:

| Service | URL |
|---|---|
| Frontend | http://localhost:3000 |
| Traefik dashboard | http://localhost:8090 |
| Keycloak | http://localhost:8080 |
| Kafka-UI | http://localhost:9093 |
| Swagger — user-service | http://localhost:8081/swagger-ui.html |
| Swagger — order-service | http://localhost:8082/swagger-ui.html |
| Swagger — product-service | http://localhost:8083/swagger-ui.html |
| Swagger — payment-service | http://localhost:8086/swagger-ui.html |

---

## Key Flows

### Place an order

```
User → POST /api/orders
     → order-service fetches product (REST → product-service)
     → order-service decrements stock (REST → product-service)
     → order-service creates order
     → Kafka: order.created (fat event)
         ├→ payment-service processes payment (Saga choreography)
         │      → Kafka: payment.succeeded (90%)
         │           → order-service confirms order
         │           → delivery-service assigns delivery
         │      → Kafka: payment.failed (10% random)
         │           → product-service restores stock
         │           → order-service cancels order
         └→ notification-service sends email confirmation
```

### Real-time delivery tracking

```
delivery-service @Scheduled job
  every N seconds → status advances
  ASSIGNED → PICKED_UP → IN_TRANSIT → DELIVERED
  → WebSocket push to connected clients
  → Kafka: delivery.updated
       └→ notification-service sends push notification
```

---

## Project Structure

```
smart-delivery/
├── services/
│   ├── user-service/
│   ├── order-service/
│   ├── product-service/
│   ├── delivery-service/
│   ├── notification-service/
│   └── payment-service/
├── frontend/              # Vue 3 + Vite + TypeScript
├── infra/
│   ├── docker-compose.yml
│   ├── docker-stack.yml
│   ├── keycloak/
│   └── monitoring/
├── docs/
│   └── adr/
├── .github/
│   └── workflows/
│       ├── ci-develop.yml  # compile + tests on PR → develop
│       └── ci-main.yml     # compile + tests + CodeQL on PR → main
├── CLAUDE.md
└── README.md
```

---

## Testing

```bash
# Run unit tests for a specific service
cd services/order-service
mvn test

# Run integration tests (requires Docker for Testcontainers)
mvn verify
```

- **Unit tests** : JUnit 5 + Mockito
- **Integration tests** : Testcontainers (PostgreSQL + Kafka)
- **SAST** : CodeQL on every PR → main

---

## Simulation notes

This project is a demo — no real payments or deliveries are processed.

**Payment** : 90% success / 10% random failure to demonstrate the Saga compensation flow.
No banking data is stored — RGPD / PCI-DSS compliant by design.

**Delivery** : automated `@Scheduled` job advances delivery status every N seconds
(configurable via `delivery.simulation.delay-seconds`).

**Notifications** : emails are simulated in logs — no real email provider configured.

---

## Roadmap

- [x] Project architecture and ADR documentation
- [x] Phase 1 — Core services (user, product, order) + Kafka + Docker Compose
- [x] Phase 2 — Payment Saga + Notification service + CI/CD GitHub Actions
- [ ] Phase 3 — Delivery tracking (WebSocket) + Frontend Vue 3
- [ ] Phase 4 — Docker Swarm deployment + Observability (Prometheus, Grafana, Jaeger)

---

## Author

**Laurent** — Fullstack developer (Java / Spring Boot · Vue 3 · TypeScript)
Bordeaux, France · Targeting South Korea 2027

[![GitHub](https://img.shields.io/badge/GitHub-LaurentGourouvin-black)](https://github.com/LaurentGourouvin)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-LaurentGourouvin-blue)](https://www.linkedin.com/in/laurentgourouvin/)

---

## License

MIT — see [LICENSE](LICENSE)
