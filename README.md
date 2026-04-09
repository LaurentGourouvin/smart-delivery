# SmartDelivery

> E-commerce order management platform built with a microservices architecture.
> Showcase project demonstrating distributed systems patterns, event-driven architecture, and DevOps practices.

![CI](https://github.com/[username]/smart-delivery/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.x-black)
![Docker](https://img.shields.io/badge/Docker%20Swarm-ready-blue)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

---

## Overview

SmartDelivery is a fully simulated e-commerce delivery platform. Users can browse products, place orders, track deliveries in real time, and receive notifications — all handled by six independent microservices communicating via REST and Kafka events.

The project is designed as a **technical showcase** covering:
- Microservices architecture with clear domain boundaries
- Event-driven communication (Apache Kafka)
- Distributed transaction management (Saga pattern)
- Real-time delivery tracking (WebSocket)
- Centralized authentication and authorization (Keycloak / OAuth2 / JWT)
- Full observability stack (Prometheus, Grafana, Jaeger, ELK)
- Container orchestration (Docker Swarm)

---

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                    Clients                          │
│              React / Angular 19                     │
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
        │  payment.processed  │
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
| `user-service` | 8081 | User accounts, authentication, RBAC | OAuth2, Keycloak, Spring Security |
| `order-service` | 8082 | Order lifecycle management | CQRS, Event Sourcing, Kafka producer |
| `product-service` | 8083 | Product catalogue, stock management | Optimistic Lock (`@Version`) |
| `delivery-service` | 8084 | Simulated real-time delivery tracking | WebSocket, `@Scheduled`, Kafka consumer |
| `notification-service` | 8085 | Email / push notifications | Kafka consumer, stateless |
| `payment-service` | 8086 | Simulated payment processing | Saga pattern (choreography) |

---

## Tech Stack

### Backend
- **Java 21** + **Spring Boot 3.x** — all services
- **Spring Data JPA** + **Hibernate** + **Flyway** — persistence and migrations
- **Spring Security** + **Keycloak** — OAuth2 / OIDC / JWT (RS256)
- **Spring Kafka** — event-driven communication
- **Spring WebSocket** — real-time delivery tracking
- **PostgreSQL** — all service databases (one schema per service)
- **Redis** — shared cache and session store
- **Springdoc OpenAPI** — API documentation per service

### Frontend
- **React** or **Angular 19** (depending on target market)

### Infrastructure
- **Docker Swarm** — container orchestration
- **Traefik** — API Gateway, routing, SSL termination
- **Apache Kafka** (Bitnami) — event bus
- **Keycloak** — identity provider
- **GitHub Actions** — CI/CD pipelines
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

- Docker Desktop (with Swarm mode or Compose)
- Java 21
- Maven 3.9+

### Run locally (Docker Compose)

```bash
# Clone the repository
git clone https://github.com/LaurentGourouvin/smart-delivery
cd smart-delivery

# Start all services and infrastructure
docker compose -f infra/docker-compose.yml up -d

# Check running services
docker compose -f infra/docker-compose.yml ps
```

Services will be available at:

| Service | URL |
|---|---|
| Frontend | http://localhost:3000 |
| Traefik dashboard | http://localhost:8090 |
| Keycloak | http://localhost:8080 |
| Grafana | http://localhost:3001 |
| Jaeger UI | http://localhost:16686 |
| Swagger (order-service) | http://localhost:8082/swagger-ui.html |

### Deploy on Docker Swarm

```bash
# Initialize Swarm (once)
docker swarm init

# Deploy the full stack
docker stack deploy -c infra/docker-stack.yml smartdelivery

# Check services
docker service ls
```

---

## Key Flows

### Place an order

```
User → POST /api/orders
     → order-service checks stock (REST → product-service)
     → order-service creates order
     → Kafka: order.created
         ├→ payment-service processes payment (Saga)
         │      → Kafka: payment.succeeded
         │           → order-service confirms order
         │           → delivery-service assigns delivery
         │      → Kafka: payment.failed (10% random)
         │           → product-service restores stock
         │           → order-service cancels order
         └→ notification-service sends email
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
├── frontend/
├── infra/
│   ├── docker-compose.yml
│   ├── docker-stack.yml
│   ├── traefik/
│   └── monitoring/
├── shared/                  # Shared Kafka event DTOs (Maven local dependency)
├── docs/
│   ├── architecture.md
│   └── adr/
├── .github/
│   └── workflows/
├── pom.xml                  # Parent POM
├── CLAUDE.md                # AI assistant context
└── README.md
```

---

## Testing

```bash
# Run tests for a specific service
cd services/order-service
mvn test

# Run integration tests (requires Docker for Testcontainers)
mvn verify
```

- **Unit tests** : JUnit 5 + Mockito
- **Integration tests** : Testcontainers (PostgreSQL + Kafka in ephemeral containers)
- **Target coverage** : 80%

---

## Simulation notes

This project is a demo — no real payments or deliveries are processed.

**Payment** : 90% success / 10% random failure to demonstrate the Saga compensation flow. No banking data is stored — RGPD / PCI-DSS compliant by design.

**Delivery** : automated `@Scheduled` job advances delivery status every N seconds (configurable via `delivery.simulation.delay-seconds`).

---

## Roadmap

- [x] Project architecture and documentation
- [ ] Phase 1 — Core services (user, order, product) + Kafka + Docker Compose
- [ ] Phase 2 — Delivery tracking (WebSocket) + Payment Saga + Observability
- [ ] Phase 3 — CI/CD GitHub Actions + Docker Swarm deployment + Frontend

---

## Author

**Laurent** — Fullstack developer (Java / Spring Boot · TypeScript · Angular · React)
Bordeaux, France · Targeting South Korea 2027

[![GitHub](https://img.shields.io/badge/GitHub-GourouvinLaurent-black)](https://github.com/LaurentGourouvin)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-GourouvinLaurent-blue)](https://www.linkedin.com/in/laurentgourouvin/)

---

## License

MIT — see [LICENSE](LICENSE)
