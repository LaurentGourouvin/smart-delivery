# ADR-004 — Saga choreography over orchestration

**Date** : 2026-04-09
**Status** : Accepted
**Decider** : Laurent

---

## Context

SmartDelivery involves a distributed transaction spanning multiple services: placing an order requires checking stock (`product-service`), processing payment (`payment-service`), confirming the order (`order-service`), and assigning a delivery (`delivery-service`). Each of these steps touches a different database.

A classical ACID transaction cannot span multiple databases. The **Saga pattern** is the standard solution for managing consistency across distributed services. Two implementation styles exist:

- **Choreography** : each service reacts to events and publishes new events — no central coordinator
- **Orchestration** : a central Saga orchestrator (a dedicated service or state machine) tells each participant what to do next

---

## Decision

**Saga choreography** via Kafka events is used for the order placement distributed transaction.

---

## Rationale

### Why Saga (over no pattern)

Without Saga, a failure in step 3 (payment) after steps 1 and 2 have already succeeded (stock decremented, order created) leaves the system in an inconsistent state. There is no automatic rollback mechanism across service boundaries.

Saga solves this by defining a **compensating transaction** for each step. If payment fails, `payment-service` publishes a `payment.failed` event, which triggers `product-service` to restore the stock and `order-service` to cancel the order. The system converges to a consistent state without a global rollback.

### Why choreography over orchestration

**No single point of failure** : an orchestrator is an additional service that must be deployed, monitored, and maintained. If it goes down, no distributed transactions can proceed. With choreography, each service is autonomous — the only shared infrastructure is Kafka, which is already part of the stack.

**Lower complexity for a solo project** : implementing an orchestrator (e.g. using Temporal, Conductor, or a hand-rolled state machine) introduces significant boilerplate and operational overhead. Choreography via Kafka events is sufficient for the 4-step transaction in SmartDelivery.

**Alignment with existing Kafka infrastructure** : all services already produce and consume Kafka events (ADR-003). Choreography is a natural extension of the existing event-driven model.

**Acknowledged trade-off** : choreography distributes the transaction logic across services, making it harder to visualize the full flow from a single place. This is mitigated by Jaeger distributed tracing, which allows the complete saga execution to be observed end-to-end.

### The happy path

```
order-service     publishes  order.created
payment-service   consumes   order.created      → processes payment
                  publishes  payment.succeeded
order-service     consumes   payment.succeeded  → confirms order
                  publishes  order.confirmed
delivery-service  consumes   order.confirmed    → assigns delivery
                  publishes  delivery.assigned
notification-svc  consumes   order.confirmed    → sends confirmation email
```

### The compensation path (payment failure)

```
payment-service   consumes   order.created      → payment fails (10% random)
                  publishes  payment.failed
product-service   consumes   payment.failed     → restores stock
order-service     consumes   payment.failed     → cancels order
                  publishes  order.cancelled
notification-svc  consumes   order.cancelled    → sends failure email
```

### Idempotency

Each Kafka consumer must be idempotent — processing the same event twice must produce the same result as processing it once. This is achieved by storing processed event IDs and checking for duplicates before applying state changes.

---

## Consequences

- Each service involved in the Saga must implement compensating transaction logic
- Event DTOs must include enough context for compensation (e.g. `payment.failed` must carry `orderId` and `productId` with quantity)
- Each consumer must implement idempotency checks
- The 10% random payment failure in `PaymentSimulator` is intentional — it allows the compensation flow to be demonstrated
- Jaeger tracing provides end-to-end visibility of the full Saga execution

---

## Alternatives considered

| Option | Reason rejected |
|---|---|
| No distributed transaction handling | Leaves system in inconsistent state on partial failures |
| Saga orchestration (Temporal / Conductor) | Significant operational overhead; overkill for solo project |
| Two-phase commit (2PC) | Not supported across independent databases; known performance and availability issues |
| Outbox pattern (full) | Considered for the future — would improve delivery guarantees but adds complexity beyond current scope |
