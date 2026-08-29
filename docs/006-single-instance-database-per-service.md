# ADR-006 — Single PostgreSQL instance, database per service

**Date** : 2026-08-29
**Status** : Accepted
**Decider** : Laurent
**Supersedes** : the schema-per-service topology previously implied by ADR-002

---

## Context

ADR-002 established PostgreSQL as the single database engine and stated that each service
owns its own database ("database-per-service pattern"). The implementation diverged: all
five persistent services shared one `smartdelivery` database, separated only by PostgreSQL
schemas (`user_service`, `order_service`, …) and by per-service roles with scoped `GRANT`s.

Two constraints shape the deployment topology:

- **Memory** : the project is intended to run on a modest VPS alongside other projects.
  Running five PostgreSQL containers multiplies the fixed cost (background processes,
  shared buffers, WAL machinery) for no functional gain.
- **Isolation** : the architecture claims strict data ownership per service, and the Saga
  pattern (ADR-004) presupposes that no service can reach another's data.

Schema-based separation satisfied the memory constraint but made isolation a matter of
configuration discipline: a single additional `GRANT` would have made cross-service SQL
possible, and PostgreSQL happily joins across schemas within one database.

---

## Decision

**One PostgreSQL instance. One database per service.**

- A single `postgres:16-alpine` container hosts all application data.
- Each persistent service owns a dedicated database (`user_service`, `order_service`,
  `product_service`, `delivery_service`, `payment_service`), owned by its own role.
- `CONNECT` is revoked from `PUBLIC` on every service database, so a service role cannot
  connect to another service's database at all.
- Tables live in each database's `public` schema. The service role is the database owner
  and therefore a member of `pg_database_owner`, which grants creation rights on `public`
  under PostgreSQL 15+.
- HikariCP pools are capped at `maximum-pool-size: 5` per service.

`notification-service` has no database — it is a stateless Kafka consumer.

---

## Rationale

### Why one instance

The memory cost of PostgreSQL is dominated by per-instance fixed overhead and by
per-connection backend processes, not by the number of databases. A second database inside
an existing instance costs a few megabytes of system catalog; a second *instance* costs the
whole fixed overhead again. On a shared VPS, one instance is the only defensible choice.

For the same reason, connection pools were capped. Five services on HikariCP defaults would
allow 50 backend processes (~5–10 MB each) — an order of magnitude more memory than the
database/schema question this ADR resolves. This is the real lever and it is now explicit.

### Why separate databases rather than schemas

PostgreSQL cannot join across databases. Reaching another service's data would require
installing `postgres_fdw` or `dblink` — a deliberate, reviewable act, not an accidental
`GRANT`. Isolation becomes **structural** rather than merely **configured**.

This matters for credibility as much as for correctness: a reviewer who sees a single shared
database will reasonably ask why a Saga is needed at all. Separate databases remove the
question.

### On the transactional boundary

A clarification worth recording, because it is easy to state incorrectly:

**The shared database was never what made a distributed transaction possible.** Services are
separate JVM processes holding separate connections under separate roles. A `@Transactional`
block in `order-service` could never have rolled back a write performed by `payment-service`,
regardless of database topology — a PostgreSQL transaction is scoped to one connection.

The Saga (ADR-004) is therefore functionally required by the *process* boundary, not by the
*database* boundary. Separating the databases does not create the need for a Saga; it removes
the temptation to bypass service APIs with direct SQL, and aligns the physical topology with
the architecture the project claims.

---

## Consequences

- `infra/postgres/init.sql` creates five roles and five databases, and revokes `CONNECT`
  from `PUBLIC` on each.
- Datasource URLs drop `?currentSchema=…` and target the service database directly:
  `jdbc:postgresql://localhost:5432/user_service`
- `spring.flyway.schemas` is removed; migrations apply to `public` in each database.
- Migration files needed no change — no table was schema-qualified.
- No business code changed. No entity declared `@Table(schema = …)`.
- The `smartdelivery` database remains as the bootstrap database and holds no application data.
- **Recreating the `postgres-data` volume is required**, since `docker-entrypoint-initdb.d`
  only runs on first initialisation. Existing local development data is lost; the product
  seed is restored automatically by `V2__seed_products.sql`.
- Backups become per-service (`pg_dump user_service`) instead of schema-scoped.

---

## Alternatives considered

| Option | Reason rejected |
|---|---|
| One instance per service | Multiplies fixed memory cost; incompatible with the VPS constraint |
| Keep schema-per-service | Isolation depends on `GRANT` discipline; contradicts ADR-002 wording |
| Managed database service | Recurring cost; incompatible with the zero-cost constraint of ADR-001 |
| Keep one database, forbid cross-schema by convention | Convention is not a boundary; nothing prevents regression |

---

## Verification

The `init.sql` privilege model was tested against `postgres:16-alpine` before adoption:

- `user_svc` creates tables in the `public` schema of `user_service` — confirms the
  PostgreSQL 15+ `pg_database_owner` behaviour.
- `order_svc` connecting to `user_service` is rejected by the server:
  `FATAL: permission denied for database "user_service" — User does not have CONNECT privilege.`
- `order_svc` operates normally within `order_service`.
