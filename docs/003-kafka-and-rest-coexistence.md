# ADR-003 — Kafka and REST coexistence

**Date** : 2026-04-09
**Status** : Accepted
**Decider** : Laurent

---

## Context

In a microservices architecture, services need to communicate. Two broad strategies exist: synchronous communication (typically REST over HTTP) and asynchronous communication (typically via a message broker such as Kafka or RabbitMQ).

A common misconception is that these two strategies are mutually exclusive — that choosing Kafka means removing REST, or that using REST everywhere eliminates the need for a message broker. This ADR documents the decision to use both, and defines clear rules for when each applies.

---

## Decision

**Both REST and Kafka are used**, serving distinct communication patterns:

- **REST (synchronous)** : when a service needs an immediate response to continue its work
- **Kafka (asynchronous)** : when a service needs to broadcast that something happened, without waiting for consumers

---

## Rationale

### Why not REST only

If all inter-service communication were REST, `order-service` would need to synchronously call `payment-service`, `delivery-service`, and `notification-service` every time an order is confirmed. This creates:

**Tight coupling** : `order-service` must know the address and API contract of every downstream service. Adding a new `analytics-service` that reacts to orders requires modifying `order-service`.

**Cascading failures** : if `notification-service` is temporarily unavailable, the order confirmation fails entirely — even though sending an email is not critical to the order being valid.

**Increased latency** : the user's response is blocked until all downstream services have responded sequentially.

### Why not Kafka only

Some operations genuinely require an immediate response:

- Checking product stock before accepting an order — the order cannot proceed without knowing whether stock is available
- Validating a user's identity and permissions — the gateway cannot route a request without a valid JWT response
- Retrieving product details for the order summary

These are request-response interactions by nature. Modeling them as events would require implementing a reply-topic mechanism (request/reply over Kafka), which is significantly more complex than a simple HTTP call and provides no benefit.

### The decision rule

> "Does this interaction require an immediate response to continue?"

- **Yes** → REST
- **No** → Kafka

### Applied to SmartDelivery

| Interaction | Protocol | Reason |
|---|---|---|
| Check product stock | REST | order-service needs the answer now |
| Validate JWT / user | REST (via Traefik + Keycloak) | Cannot proceed without auth result |
| Fetch product details | REST | Needed synchronously for order creation |
| Order confirmed → trigger payment | Kafka (`order.created`) | payment-service reacts asynchronously |
| Payment succeeded → confirm order | Kafka (`payment.succeeded`) | order-service reacts, no immediate reply needed |
| Payment failed → restore stock | Kafka (`payment.failed`) | Saga compensation, asynchronous |
| Order confirmed → send email | Kafka (`order.confirmed`) | Email delivery is not time-critical |
| Delivery status change → notify user | Kafka (`delivery.updated`) | Notification is a side effect, not a blocker |

---

## Consequences

- `spring-kafka` is a dependency in all services that produce or consume events
- Event schema (DTOs) are defined in the `shared/` module and imported as a local Maven dependency
- Kafka topics follow the naming convention: `{domain}.{past-tense-verb}` (e.g. `order.created`, `payment.failed`)
- Each Kafka consumer group is named after the consuming service (e.g. `notification-service`, `delivery-service`)
- REST calls between services propagate the original user JWT in the `Authorization` header

---

## Alternatives considered

| Option | Reason rejected |
|---|---|
| REST only | Tight coupling, cascading failures, blocking latency for non-critical operations |
| Kafka only | Request-reply over Kafka is complex and unnatural for synchronous queries |
| RabbitMQ instead of Kafka | Kafka chosen for its message retention (consumers can catch up after downtime), partition model, and stronger ecosystem in Java/Spring |
| gRPC for synchronous calls | Adds proto compilation complexity; REST is sufficient for this project scale |

> **Note (2026-04-09)** : Kafka is deployed in KRaft mode (no Zookeeper). Zookeeper was removed from the stack — Kafka manages its own coordination natively since version 3.x. One less dependency to operate.
