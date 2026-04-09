# ADR-002 — PostgreSQL for all services

**Date** : 2026-04-09
**Status** : Accepted
**Decider** : Laurent

---

## Context

SmartDelivery has 6 microservices, 5 of which require persistent storage. Each service owns its own database schema (database-per-service pattern). A technology must be chosen for these datastores.

In a typical e-commerce microservices reference architecture, it is common to see heterogeneous databases — PostgreSQL for transactional services, MongoDB for product catalogues with flexible schemas, Redis for caching. This approach is sometimes called "polyglot persistence".

---

## Decision

**PostgreSQL** is used as the database engine for all services that require persistent storage.
**Redis** is used as a shared cache and session store (not a primary datastore).

---

## Rationale

### Why not polyglot persistence

**Complexity without benefit** : The standard argument for MongoDB on a product catalogue is that products of different categories have heterogeneous attributes (a shoe has `size`, a phone has `RAM`, a book has `ISBN`). In SmartDelivery, the product model is intentionally simple and homogeneous. There is no flexible schema requirement that would justify a document store.

**Operational overhead** : Using multiple database engines means multiple Docker images to maintain, multiple connection pool configurations, multiple migration strategies (Flyway works natively with PostgreSQL; MongoDB has no equivalent migration tool with the same maturity), and multiple mental models to hold during development.

**Single developer context** : Polyglot persistence is a pattern that makes sense when different teams own different services and can choose the best tool for their domain. In a solo project, the overhead outweighs the benefit.

**PostgreSQL is sufficient** : PostgreSQL supports JSONB columns for semi-structured data if needed in the future, full-text search, and advanced indexing. It covers all use cases of this project without compromise.

### Why PostgreSQL specifically

- Industry standard for relational workloads in Java/Spring Boot ecosystems
- First-class support in Spring Data JPA and Hibernate
- Native support in Flyway for schema migrations
- Excellent Testcontainers support for integration testing
- Free, open source, widely used in both French and Korean IT markets

### Redis role

Redis is used exclusively as a **cache layer** and **session store**, not as a primary datastore. No business data is persisted in Redis — it is treated as ephemeral. If Redis is unavailable, services fall back to PostgreSQL directly.

---

## Consequences

- All services use `spring-boot-starter-data-jpa` and `postgresql` driver
- All schema migrations are managed by Flyway (`src/main/resources/db/migration/`)
- Each service connects to its own PostgreSQL schema — cross-schema queries are forbidden
- Redis is available as a shared cache via `spring-boot-starter-data-redis`
- Integration tests use Testcontainers with the official `postgres` Docker image

---

## Alternatives considered

| Option | Reason rejected |
|---|---|
| MongoDB for product-service | No flexible schema requirement in this project scope |
| MySQL / MariaDB | PostgreSQL preferred for JSONB support and ecosystem maturity |
| H2 in-memory | Not production-representative; replaced by Testcontainers for tests |
| Redis as primary store | Not durable enough for business data |
