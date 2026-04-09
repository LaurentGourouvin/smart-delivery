# ADR-005 — Asymmetric JWT with Keycloak JWKS

**Date** : 2026-04-09
**Status** : Accepted
**Decider** : Laurent

---

## Context

SmartDelivery uses JWT (JSON Web Tokens) for stateless authentication across all services. JWTs are issued by Keycloak after a successful login and must be verified by each service that receives a request.

JWT signatures can be either symmetric (HMAC, using a shared secret) or asymmetric (RSA or EC, using a public/private key pair). The choice has significant security and operational implications in a distributed system.

---

## Decision

**Asymmetric JWT signing (RS256)** is used. Keycloak holds the private key exclusively and signs all tokens. Each service retrieves the corresponding public key automatically via Keycloak's **JWKS endpoint** (JSON Web Key Set).

No shared secret is distributed to any service.

---

## Rationale

### Why not symmetric JWT (HMAC/HS256)

With symmetric signing, every service that needs to verify a token must possess the same secret that was used to sign it. In a microservices architecture this means:

**Secret distribution problem** : the secret must be injected into every service via environment variables or a secrets manager. Each service that holds the secret is a potential attack vector. If any one service is compromised, an attacker can forge valid tokens for the entire system.

**Rotation complexity** : rotating a shared secret requires redeploying all services simultaneously, or implementing a grace period during which both old and new secrets are valid.

**No separation of concerns** : any service that can verify a token could theoretically also create one. The authority to issue tokens should belong exclusively to the identity provider.

### Why asymmetric JWT (RS256)

**Private key never leaves Keycloak** : only Keycloak can sign tokens. Services can verify signatures but cannot issue new tokens — even if a service is fully compromised.

**JWKS endpoint eliminates manual distribution** : Keycloak exposes its public keys at a well-known endpoint:
```
http://keycloak:8080/realms/smartdelivery/protocol/openid-connect/certs
```
Each Spring Boot service fetches this endpoint automatically at startup and on key rotation. No manual key distribution, no environment variables containing key material.

**Automatic key rotation support** : when Keycloak rotates its signing keys, it publishes the new public key at the JWKS endpoint while keeping the old one available for a transition period. Services pick up the new key automatically on the next fetch cycle — zero downtime, zero redeployment.

**Industry standard** : RS256 with JWKS is the standard approach for OAuth2 resource servers. It is the default configuration in Spring Security's OAuth2 resource server support.

### Spring Boot configuration

Each service is configured as an OAuth2 resource server with a single property:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: http://keycloak:8080/realms/smartdelivery/protocol/openid-connect/certs
```

Spring Security handles signature verification, expiration checking, and claims extraction automatically. No custom JWT parsing code is required.

### Service-to-service authentication

When a service calls another service's REST API (e.g. `order-service` calling `product-service`), the original user's JWT is propagated in the `Authorization: Bearer <token>` header. The receiving service validates it against the same JWKS endpoint.

This approach (token propagation) is used for the demo. In a production system, the OAuth2 Client Credentials Flow would be preferred — each service has its own machine-to-machine credentials with Keycloak, separate from user tokens.

---

## Consequences

- Keycloak must be started before any service attempts to verify a token
- All services declare `spring-boot-starter-oauth2-resource-server` as a dependency
- The JWKS URI is externalized in `application.yml` and overridable via environment variable (`SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI`)
- No JWT secret is stored anywhere in the codebase or Docker configuration
- Integration tests use a Keycloak Testcontainer or a WireMock stub that serves a static JWKS response

---

## Alternatives considered

| Option | Reason rejected |
|---|---|
| HMAC/HS256 with shared secret | Secret distribution and compromise risk in multi-service environment |
| Opaque tokens with Keycloak introspection | Each request requires a network call to Keycloak — adds latency and Keycloak becomes a runtime dependency for every request |
| Custom JWT implementation | Reinvents what Spring Security and Keycloak already provide; maintenance burden |
| No authentication between services | Acceptable only in a fully private network; incompatible with security showcase objective |
