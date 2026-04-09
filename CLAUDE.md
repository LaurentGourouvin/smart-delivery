# CLAUDE.md — smart-delivery

Fichier de contexte pour Claude Code et Claude.ai.
Lis ce fichier en entier avant toute action sur le projet.

---

## Présentation du projet

**SmartDelivery** est une plateforme de gestion de commandes e-commerce avec livraison simulée en temps réel, construite en architecture microservices.

Objectif principal : projet vitrine pour le marché de l'emploi (France et Corée du Sud).
Niveau cible : mid+ développeur fullstack orienté Java/Spring Boot.

---

## Status

### Phase actuelle
Phase 0 — Documentation et infrastructure de base

### Fait
- [x] Repo GitHub créé : `github.com/LaurentGourouvin/smart-delivery`
- [x] Structure de dossiers initialisée (`services/`, `infra/`, `docs/`, `shared/`, `.github/`)
- [x] `CLAUDE.md` — contexte complet du projet
- [x] `README.md` — vitrine GitHub en anglais, badges, Swagger tous services
- [x] `LICENSE` — MIT
- [x] `docs/adr/001` — Docker Swarm over Kubernetes
- [x] `docs/adr/002` — PostgreSQL for all services
- [x] `docs/adr/003` — Kafka and REST coexistence
- [x] `docs/adr/004` — Saga choreography over orchestration
- [x] `docs/adr/005` — Asymmetric JWT with Keycloak JWKS
- [x] `infra/docker-compose.yml` — stack Phase 1 (Traefik, Keycloak, PostgreSQL, Redis, Kafka, Kafka-UI)
- [x] `infra/postgres/init.sql` — création des schemas isolés par service
- [x] `infra/monitoring/prometheus.yml` — config scraping des services Spring Boot

### En cours
- [ ] user-service — repositories OK, couche service + controllers à faire

### Bloquant
- Aucun

### Prochaine étape
Tests unitaires + intégration Testcontainers pour `user-service`

### Avancement user-service
- [x] Généré via Spring Initializr (Spring Boot 3.5.x, Java 21)
- [x] `application.yml` — datasource, Flyway, Keycloak JWKS, Actuator, Springdoc
- [x] `V1__init_user_service.sql` — tables users + addresses, contraintes, index
- [x] `docs/MCD.md` — modèle conceptuel de données documenté
- [x] `entity/User.java` — entité JPA avec Lombok, @PrePersist, @OneToMany
- [x] `entity/Address.java` — entité JPA avec @ManyToOne LAZY
- [x] `repository/UserRepository.java` — findByEmail, findByKeycloakId, existsByEmail
- [x] `repository/AddressRepository.java` — findByUserId, findByUserIdAndIsDefaultTrue
- [x] `dto/UserResponse.java` — record lecture utilisateur
- [x] `dto/AddressResponse.java` — record lecture adresse
- [x] `dto/UpdateUserRequest.java` — modifier prénom, nom, téléphone
- [x] `dto/CreateAddressRequest.java` — ajouter une adresse
- [x] `service/UserService.java` — provisioning JWT, gestion profil et adresses
- [x] `controller/UserController.java` — endpoints REST (/me, /me/addresses)
- [x] `config/SecurityConfig.java` — stateless, CSRF disabled, OAuth2 JWT
- [x] `exception/UserNotFoundException.java`
- [x] `exception/AddressNotFoundException.java`
- [x] `exception/GlobalExceptionHandler.java` — ProblemDetail RFC 9457
- [ ] Tests unitaires + intégration Testcontainers

### Décisions prises en session (non couvertes par les ADR)
- ELK Stack reporté en Phase 2 — trop lourd pour démarrer
- Prometheus + Grafana + Jaeger reportés en Phase 2
- `docker-stack.yml` (Swarm) reporté après que les services tournent en Compose
- Frontend (React vs Angular 19) — décision non encore prise
- `notification-service` n'a pas de base PostgreSQL — service stateless, Kafka consumer pur
- Pas de pom.xml parent pour l'instant — chaque service généré indépendamment via Spring Initializr
- Keycloak comme source de vérité unique pour les rôles et l'auth — pas de duplication locale
- Package name généré en `user_service` (underscore) par Spring Initializr — conservé tel quel

---

## Stack technique

### Backend
- **Langage** : Java 21
- **Framework** : Spring Boot 3.x (tous les services sans exception)
- **Build** : Maven — monorepo avec `pom.xml` parent à la racine
- **Base de données** : PostgreSQL pour tous les services (pas de MongoDB, pas de mix)
- **Cache** : Redis (sessions + cache produits)
- **Messaging** : Apache Kafka (Bitnami image)
- **Auth** : Keycloak — OAuth2/OIDC, signature asymétrique RS256, JWKS endpoint
- **ORM** : Spring Data JPA + Hibernate
- **Migrations** : Flyway

### Frontend
- **Framework** : React (marché France / startups coréennes) OU Angular 19 (IT shops coréennes)
- **Décision finale** : à confirmer selon le marché ciblé en priorité

### Infrastructure
- **Orchestration** : Docker Swarm (pas Kubernetes — décision volontaire pour maîtriser la complexité)
- **Gateway** : Traefik (routing, OAuth2 middleware, rate limiting)
- **Service Discovery** : DNS natif Docker Swarm — pas d'Eureka, pas de Consul
- **Dev local** : Docker Compose (`docker-compose.yml`)
- **Déploiement** : Docker Stack (`docker-stack.yml`)
- **CI/CD** : GitHub Actions + GitHub Container Registry (gratuit)
- **Images** : stockées sur `ghcr.io/[username]/smart-delivery`

### Observabilité
- **Métriques** : Prometheus + Grafana
- **Tracing distribué** : Jaeger (distributed tracing inter-services)
- **Logs** : ELK Stack avec correlation ID sur chaque requête
- **OpenAPI** : chaque service expose `/api-docs` via Springdoc

---

## Services

| Service | Port | Responsabilité | Patterns |
|---|---|---|---|
| `user-service` | 8081 | Comptes, auth, RBAC | OAuth2, Keycloak, Spring Security |
| `order-service` | 8082 | Cycle de vie commandes | CQRS, Event Sourcing, Kafka producer |
| `product-service` | 8083 | Catalogue, stock | Optimistic Lock (`@Version`) |
| `delivery-service` | 8084 | Tracking livraison simulé | WebSocket, `@Scheduled`, Kafka consumer |
| `notification-service` | 8085 | Email/push/SMS simulés | Kafka consumer pur, pas de BDD |
| `payment-service` | 8086 | Paiement simulé | Saga pattern (chorégraphie), Kafka |
| `frontend` | 3000 | Interface utilisateur | React ou Angular 19 |

Infrastructure transverse : Traefik (80/443), Keycloak (8080), Kafka (9092), PostgreSQL (5432), Redis (6379), Prometheus (9090), Grafana (3001), Jaeger (16686).

---

## Règles d'architecture — à ne jamais enfreindre

### Isolation des données
- Chaque service a sa propre base PostgreSQL, schéma isolé
- Un service ne fait **jamais** de requête SQL directe dans la base d'un autre service
- Pour lire les données d'un autre service → appel REST à son API
- Jamais de JOIN inter-services

### Communication inter-services
- **Synchrone REST** : quand une réponse immédiate est nécessaire (vérifier stock, valider utilisateur)
- **Asynchrone Kafka** : pour tout événement "quelque chose s'est passé" (commande créée, paiement traité, livraison mise à jour)
- Ne jamais supprimer Kafka pour "simplifier" — REST et Kafka sont complémentaires, pas alternatifs

### Sécurité
- JWT signé en RS256 par Keycloak uniquement
- Chaque service vérifie le JWT via le JWKS endpoint de Keycloak — jamais de secret partagé
- Configuration dans `application.yml` :
  ```yaml
  spring.security.oauth2.resourceserver.jwt.jwk-set-uri: http://keycloak:8080/realms/smartdelivery/protocol/openid-connect/certs
  ```
- Communication service-to-service : propagation du JWT utilisateur (approche démo)

### Gestion du stock
- Toujours utiliser `@Version` (Optimistic Lock) sur l'entité `Product`
- Ne jamais décrémenter le stock sans vérifier la version
- En cas de `OptimisticLockException` → retourner HTTP 409 à `order-service`

---

## Patterns importants

### Saga (payment-service)
- Chorégraphie via Kafka — pas d'orchestrateur central
- Chaque étape a une transaction compensatoire
- Flux succès : `order.created` → `payment.requested` → `payment.succeeded` → `order.confirmed` → `delivery.assigned`
- Flux échec : `payment.failed` → `stock.restored` → `order.cancelled`
- Simulation : 90% succès / 10% échec aléatoire pour tester les compensations

### CQRS (order-service)
- Commandes (write) et queries (read) séparées
- Chaque changement d'état publie un événement Kafka

### Livraison simulée (delivery-service)
- Cycle : `ASSIGNED` → `PICKED_UP` → `IN_TRANSIT` → `DELIVERED`
- Délai configurable via `application.yml` :
  ```yaml
  delivery.simulation.delay-seconds: 5
  ```
- Push WebSocket à chaque changement de statut

### Paiement simulé (payment-service)
- Aucune donnée bancaire réelle — conformité RGPD/PCI-DSS
- Stocke uniquement : `paymentIntentId` (UUID), statut, montant, timestamp
- Simule un délai de traitement de 2 secondes

---

## Structure du monorepo

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
│   ├── docker-compose.yml       ← dev local
│   ├── docker-stack.yml         ← déploiement Swarm
│   ├── traefik/
│   └── monitoring/
├── shared/                      ← DTOs Kafka partagés entre services Java
├── docs/
│   ├── architecture.md
│   └── adr/                     ← Architecture Decision Records
├── .github/
│   └── workflows/
├── pom.xml                      ← pom parent Maven
└── CLAUDE.md                    ← ce fichier
```

---

## Conventions de code

### Nommage
- Classes Java : PascalCase
- Variables et méthodes : camelCase
- Topics Kafka : kebab-case (ex: `order-created`, `payment-failed`)
- Endpoints REST : kebab-case (ex: `/api/orders`, `/api/product-service`)
- Variables d'environnement : UPPER_SNAKE_CASE

### Structure d'un service Spring Boot
```
src/main/java/com/smartdelivery/{service}/
├── controller/      ← endpoints REST
├── service/         ← logique métier
├── repository/      ← Spring Data JPA
├── entity/          ← entités JPA
├── dto/             ← objets de transfert (request/response)
├── event/           ← objets événements Kafka
├── config/          ← configuration Spring (Security, Kafka, etc.)
└── exception/       ← exceptions métier et handlers
```

### Tests
- Unitaires : JUnit 5 + Mockito
- Intégration : Testcontainers (PostgreSQL + Kafka dans containers éphémères)
- Cible coverage : 80%
- Ne jamais mocker la base de données pour les tests d'intégration — utiliser Testcontainers

### Injection de dépendances
- Toujours par constructeur — jamais `@Autowired` sur les champs
- Utiliser `@RequiredArgsConstructor` (Lombok) pour réduire le boilerplate

---

## Ce que Claude ne doit pas faire

- Ne pas introduire de nouvelle technologie sans validation explicite
- Ne pas utiliser MongoDB — PostgreSQL partout
- Ne pas utiliser Eureka ou Consul — DNS Swarm natif
- Ne pas utiliser Spring Cloud Gateway — Traefik gère le routing
- Ne pas utiliser Kubernetes — Docker Swarm uniquement
- Ne pas partager de secrets JWT entre services — JWKS endpoint uniquement
- Ne pas faire de requêtes SQL cross-services
- Ne pas mélanger Node.js dans les services backend — Spring Boot uniquement
- Ne pas utiliser `@Autowired` sur les champs
- Ne pas créer de transaction qui span plusieurs services — utiliser Saga

---

## Commandes utiles

```bash
# Démarrer l'environnement de dev local
docker compose -f infra/docker-compose.yml up -d

# Initialiser Docker Swarm (une seule fois)
docker swarm init

# Déployer sur Swarm
docker stack deploy -c infra/docker-stack.yml smartdelivery

# Build d'un service spécifique
cd services/order-service && mvn clean package -DskipTests

# Lancer les tests d'un service
cd services/order-service && mvn test

# Voir les logs d'un service (Swarm)
docker service logs smartdelivery_order-service -f
```

---

## Contexte développeur

- Développeur fullstack, background JavaScript/TypeScript, montée en compétence Java/Spring Boot
- Projet en parallèle : Library App (fil rouge Spring Boot, Blocs 0-1 complétés)
- Environnement : macOS, zsh, VS Code
- Objectif : poste mid+ Java en France ou en Corée du Sud (2027)
- Toujours expliquer le "pourquoi" des choix techniques, pas seulement le "comment"
- Privilégier la clarté et la progression pédagogique sur la complexité inutile
