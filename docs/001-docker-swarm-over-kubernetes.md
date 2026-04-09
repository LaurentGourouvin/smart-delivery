# ADR-001 — Docker Swarm over Kubernetes

**Date** : 2026-04-09
**Status** : Accepted
**Decider** : Laurent

---

## Context

SmartDelivery is a solo showcase project developed over approximately 3 months.
The architecture requires container orchestration to manage 6 microservices, infrastructure components (Kafka, PostgreSQL, Redis, Keycloak, Traefik), and the observability stack (Prometheus, Grafana, Jaeger, ELK).

Two main orchestration options were evaluated: Kubernetes (K8s) and Docker Swarm.

---

## Decision

**Docker Swarm** is chosen as the container orchestration platform.

---

## Rationale

### Arguments for Docker Swarm

**Cognitive load** : Docker Swarm introduces a single new concept (the `docker stack deploy` command) on top of an already familiar Docker Compose mental model. Kubernetes introduces dozens of new primitives (Pod, Deployment, ReplicaSet, Service, Ingress, ConfigMap, Secret, Namespace, HPA, Helm charts...) that would consume a significant portion of the available development time.

**Service discovery is equivalent** : Both platforms provide native DNS-based service discovery. A service calls `http://order-service:8082` and the platform resolves it — no Eureka or Consul needed in either case.

**Cost** : Docker Swarm runs entirely on a local machine at zero cost. A production-grade Kubernetes cluster (even managed, e.g. GKE, EKS) incurs infrastructure costs incompatible with a fully free project constraint.

**Sufficient for the demo scope** : The features uniquely provided by Kubernetes (Horizontal Pod Autoscaler, Helm ecosystem, fine-grained RBAC at the cluster level, multi-node production scheduling) are not required to demonstrate the microservices architecture patterns targeted by this project.

**Faster iteration** : With Docker Swarm, the full stack can be started with a single command (`docker compose up`for local dev, `docker stack deploy` for Swarm mode). This keeps the feedback loop short during development.

### Arguments against (acknowledged trade-offs)

**Market signal** : Kubernetes is the industry standard. Not using it means this project cannot be used to demonstrate K8s knowledge.

**Missing features** : No HPA, no Helm, no fine-grained pod scheduling, smaller ecosystem.

**Mitigation** : The architecture is designed to be K8s-compatible. All services are stateless containers, all configuration is externalized via environment variables, and the `docker-stack.yml` structure maps directly to Kubernetes manifests. A migration to K8s is a deployment concern, not an architecture concern. This can be stated clearly in interviews.

---

## Consequences

- All services must be stateless and configurable via environment variables
- No Helm charts — configuration is managed via Docker Swarm configs and secrets
- `docker-compose.yml` is used for local development
- `docker-stack.yml` is used for Swarm deployment
- If a future employer requires K8s knowledge, this project can be extended with a `k8s/` directory containing equivalent manifests

---

## Alternatives considered

| Option | Reason rejected |
|---|---|
| Kubernetes (local, minikube) | Too high cognitive overhead for solo project within 3-month timeline |
| Kubernetes (managed, GKE/EKS) | Not free — violates project constraint |
| No orchestration (Docker Compose only) | Cannot demonstrate scaling, rolling updates, or service resilience |
