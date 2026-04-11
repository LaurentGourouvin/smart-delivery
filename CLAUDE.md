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
Phase 1 — Core services

### Fait
- [x] Structure du repo
- [x] docker-compose.yml (Phase 1) — ports liés à 127.0.0.1
- [x] Documentation (README, ADR, LICENSE)
- [x] Keycloak configuré et exporté
  - realm `smartdelivery`
  - client `smart-delivery-app` (confidential, direct access grants)
  - import automatique au démarrage via `--import-realm`
  - realm JSON commité dans `infra/keycloak/smartdelivery-realm.json`

### Avancement user-service
- [x] Généré via Spring Initializr (Spring Boot 3.5.x, Java 21)
- [x] `application.yml` — datasource, Flyway, Keycloak JWKS, Actuator, Springdoc
- [x] `V1__init_user_service.sql` — tables users + addresses, gen_random_uuid()
- [x] `docs/MCD.md` — modèle conceptuel de données documenté
- [x] `entity/User.java` + `entity/Address.java`
- [x] `repository/UserRepository.java` + `repository/AddressRepository.java`
- [x] `dto/` — UserResponse, AddressResponse, UpdateUserRequest, CreateAddressRequest
- [x] `service/UserService.java` — provisioning JWT, gestion profil et adresses
- [x] `controller/UserController.java` — endpoints REST (/me, /me/addresses)
- [x] `config/SecurityConfig.java` — stateless, CSRF disabled, OAuth2 JWT
- [x] `exception/` — UserNotFoundException, AddressNotFoundException, GlobalExceptionHandler
- [x] Tests unitaires — 6 tests Mockito, mvn test sans Docker
- [x] maven-failsafe-plugin — séparation unit/intégration
- [x] Validé end-to-end — JWT Keycloak → provisioning automatique → réponse JSON
- [ ] Tests d'intégration Testcontainers (`*IT.java`) — mvn verify

### Avancement payment-service
- [x] Généré via Spring Initializr (Spring Boot 3.5.x, Java 21)
- [x] `application.yml` — datasource, Flyway, Keycloak JWKS, Kafka consumer + producer
- [x] `V1__init_payment_service.sql` — table payments, contraintes UNIQUE sur order_id
- [x] `docs/MCD.md` — modèle conceptuel documenté
- [x] `entity/Payment.java` + `entity/PaymentStatus.java`
- [x] `repository/PaymentRepository.java` — findByOrderId, existsByOrderId
- [x] `event/` — OrderCreatedEvent, OrderItemEvent, PaymentSucceededEvent, PaymentFailedEvent
- [x] `service/PaymentService.java` — @KafkaListener, Saga 90/10, idempotence
- [x] `controller/PaymentController.java` — GET /api/payments/order/:orderId
- [x] `config/SecurityConfig.java`
- [x] `exception/` — PaymentNotFoundException, PaymentAlreadyExistsException, GlobalExceptionHandler
- [x] Validé end-to-end — 5 commandes traitées : 4 payment.succeeded + 1 payment.failed
- [x] Tests unitaires — 4 tests Mockito (idempotence, success, failure)

### Avancement notification-service
- [x] Généré via Spring Initializr (Spring Boot 3.5.x, Java 21)
- [x] `application.yml` — Kafka consumer uniquement, pas de BDD, pas de sécurité
- [x] `event/` — OrderCreatedEvent, PaymentSucceededEvent, PaymentFailedEvent
- [x] `service/NotificationService.java` — 3 @KafkaListener, emails simulés en logs
- [x] `config/KafkaConsumerConfig.java` — 3 factories typées par topic
- [x] Tests unitaires — 3 tests assertThatNoException
- [x] Validé end-to-end — rattrapé tous les anciens messages au démarrage

## Règles Frontend

### Structure des fichiers
```
src/
├── assets/
│   └── styles/
│       ├── variables.css    ← CSS variables K-beauty (couleurs, typo, spacing)
│       ├── animations.css   ← keyframes réutilisables
│       └── base.css         ← reset + styles globaux
├── components/
│   ├── layout/
│   │   ├── AppNav.vue       ← navigation globale
│   │   └── AppFooter.vue    ← footer global
│   └── [feature]/           ← composants par fonctionnalité
│       └── FeatureSection.vue
├── views/                   ← assemblage uniquement, zéro CSS
│   └── HomeView.vue
├── stores/                  ← Pinia stores
├── services/                ← API calls via Fetch natif
└── router/
```

### Règles de composants
- **Views** — assemblage uniquement, zéro CSS, zéro logique métier
- **Components** — un composant = une responsabilité, CSS scoped
- **Max 200 lignes par fichier** — au-delà, découper en sous-composants
- **CSS global** — uniquement dans `assets/styles/`, jamais dans App.vue
- **CSS scoped** — toujours `<style scoped>` dans les composants
- **PrimeVue** — pour les composants fonctionnels (inputs, tables, modals, toasts, forms)
- **CSS custom** — pour le design K-beauty (hero, sections éditoriales)

### Règles de responsive
- **Mobile first** — écrire d'abord le CSS mobile, puis les breakpoints desktop
- **Breakpoints** :
  - Mobile : < 768px
  - Tablet : 768px - 1024px
  - Desktop : > 1024px
- **Toutes les pages doivent avoir une version mobile**
- **Navigation mobile** — hamburger menu sur mobile
- **Grid** — passer de multi-colonnes à 1 colonne sur mobile

### HTTP Client
- **Fetch API natif uniquement** — pas d'Axios (supply chain risk)
- Les appels API sont dans `src/services/` uniquement
- Jamais d'appel fetch directement dans un composant ou une view

### Conventions
- Noms de composants : PascalCase (`AppNav.vue`, `HeroSection.vue`)
- Noms de fichiers CSS : kebab-case (`variables.css`)
- Props TypeScript : toujours typées
- Emits TypeScript : toujours typés
- [x] Initialisé — Vue 3 + Vite + TypeScript + Vue Router + Pinia
- [x] PrimeVue + PrimeIcons installés
- [x] `main.ts` — PrimeVue Aura theme + Pinia + Router configurés
- [x] `App.vue` — layout principal + CSS variables K-beauty
- [x] `router/index.ts` — 7 routes + auth guard (requiresAuth)
- [x] `stores/auth.ts` — token JWT, isAuthenticated, setToken, logout
- [x] Views vides créées — HomeView, CatalogueView, ProductView, CartView, OrdersView, ProfileView, LoginView
- [ ] HomeView — maquette K-beauty luxury editorial
- [ ] CatalogueView — liste produits avec filtres
- [ ] ProductView — fiche produit
- [ ] CartView — panier + commande
- [ ] OrdersView — historique commandes
- [ ] ProfileView — compte utilisateur
- [ ] LoginView — authentification Keycloak
- [ ] Services API Fetch natif (auth, product, order, user)

### Avancement order-service
- [x] Généré via Spring Initializr (Spring Boot 3.5.x, Java 21)
- [x] `application.yml` — datasource, Flyway, Keycloak JWKS, Kafka, Actuator, Springdoc
- [x] `V1__init_order_service.sql` — tables orders + order_items, contraintes, index
- [x] `docs/MCD.md` — modèle conceptuel de données documenté
- [x] `entity/Order.java` + `entity/OrderItem.java` + `entity/OrderStatus.java`
- [x] `repository/OrderRepository.java` + `repository/OrderItemRepository.java`
- [x] `dto/` — OrderResponse, OrderItemResponse, CreateOrderRequest, UpdateOrderStatusRequest, DeliveryAddressRequest, OrderItemRequest
- [x] `event/` — OrderCreatedEvent, OrderItemEvent, OrderStatusChangedEvent (fat events)
- [x] `client/ProductServiceClient.java` — Anti-Corruption Layer vers product-service
- [x] `service/OrderService.java` — CQRS simplifié, Kafka producer, token propagation
- [x] `controller/OrderController.java` — endpoints REST
- [x] `config/SecurityConfig.java` + `config/RestClientConfig.java`
- [x] `exception/` — OrderNotFoundException, InsufficientStockException, GlobalExceptionHandler
- [x] Tests unitaires — 6 tests Mockito
- [x] Validé end-to-end — commande créée, stock décrémenté, order.created visible dans Kafka-UI

### Avancement product-service
- [x] Généré via Spring Initializr (Spring Boot 3.5.x, Java 21)
- [x] `application.yml` — datasource, Flyway, Keycloak JWKS, Actuator, Springdoc
- [x] `V1__init_product_service.sql` — tables categories + products, K-beauty seed
- [x] `docs/MCD.md` — modèle conceptuel de données documenté
- [x] `entity/Category.java` + `entity/Product.java` + `entity/SkinType.java`
- [x] `repository/CategoryRepository.java` + `repository/ProductRepository.java`
- [x] `dto/` — CategoryResponse, ProductResponse, CreateProductRequest, UpdateProductRequest, UpdateStockRequest
- [x] `service/ProductService.java` — logique métier + Optimistic Lock
- [x] `controller/ProductController.java` — endpoints REST
- [x] `config/SecurityConfig.java`
- [x] `exception/` — ProductNotFoundException, CategoryNotFoundException, GlobalExceptionHandler
- [x] Validé end-to-end — JWT Keycloak → GET /api/products/categories → 8 catégories K-beauty
- [x] Tests unitaires — 8 tests Mockito, mvn test sans Docker
- [x] `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` sur Product et Category

### Bloquant
- Aucun

### Prochaine étape
Tests unitaires `product-service` puis passer à `order-service`

### Décisions prises en session (non couvertes par les ADR)
- ELK Stack reporté en Phase 2
- Prometheus + Grafana + Jaeger reportés en Phase 2
- `docker-stack.yml` (Swarm) reporté en Phase 2
- Frontend (React vs Angular 19) — décision non encore prise
- `notification-service` n'a pas de base PostgreSQL — service stateless
- Pas de pom.xml parent — chaque service généré via Spring Initializr
- Keycloak comme source de vérité unique pour les rôles et l'auth
- Package name généré en `user_service` (underscore) par Spring Initializr — conservé
- Kafka en mode KRaft — Zookeeper supprimé
- Ports Docker liés à 127.0.0.1 — sécurité dev local
- Tests d'intégration Testcontainers reportés après validation end-to-end de chaque service
- `gen_random_uuid()` utilisé à la place de `uuid-ossp` (PostgreSQL 16 natif)

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
- **Framework** : Vue 3 + Vite + TypeScript
- **UI Library** : PrimeVue + PrimeIcons
- **State Management** : Pinia
- **Router** : Vue Router 5 (SPA mode)
- **HTTP Client** : Axios
- **Port dev** : 3000

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
| `frontend` | 3000 | Interface utilisateur | Vue 3, Vite, TypeScript, PrimeVue, Pinia |

Infrastructure transverse : Traefik (80/443), Keycloak (8080), Kafka (9092, mode KRaft — sans Zookeeper), PostgreSQL (5432), Redis (6379), Prometheus (9090), Grafana (3001), Jaeger (16686).

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

### Entités JPA
- Toujours ajouter `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` sur toutes les entités JPA
- Toujours marquer `id` avec `@EqualsAndHashCode.Include`
- Raison : évite les références circulaires et les StackOverflowError avec `@Data` + relations JPA

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

## CI/CD

### Stratégie de branches
```
feature/*  →  develop  →  main
```

### Pipelines GitHub Actions

**`ci-develop.yml`** — déclenché sur PR vers `develop`
- Compile + `mvn test` (tests unitaires uniquement, pas de Docker)
- Matrix strategy : tourne en parallèle pour chaque service
- Objectif : feedback rapide, ne pas casser le build

**`ci-main.yml`** — déclenché sur PR vers `main`
- Compile + `mvn test`
- CodeQL SAST (analyse de sécurité statique)
- Objectif : quality gate complète avant prod

### Ajouter un nouveau service aux pipelines
Dans les deux fichiers `ci-develop.yml` et `ci-main.yml`, ajouter le service dans la matrix :
```yaml
matrix:
  service: [user-service, product-service, order-service]
```

### Règle CHANGELOG
- Le fichier `CHANGELOG.md` est à la racine du repo
- Format : [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)
- **Sur chaque PR vers `develop`** : mettre à jour la section `[Unreleased]` avec les changements
- **Sur chaque PR vers `main`** : renommer `[Unreleased]` avec le numéro de version et la date
- Catégories : `Added`, `Fixed`, `Changed`, `Removed`, `Infrastructure`
- CodeQL est lent (5-15 min) — trop lourd pour chaque PR de feature
- Les vulnérabilités sont traitées sereinement avant prod
- `develop` = feedback rapide, `main` = qualité garantie

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
