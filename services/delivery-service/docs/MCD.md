# MCD — delivery-service

## Entités

### Delivery

Représente le cycle de vie d'une livraison, de l'assignation à la livraison effective.

| Champ               | Type         | Contraintes                        | Description                                              |
|---------------------|--------------|------------------------------------|----------------------------------------------------------|
| `id`                | UUID         | PK, NOT NULL, DEFAULT gen_random_uuid() | Identifiant unique de la livraison                  |
| `order_id`          | UUID         | NOT NULL, UNIQUE                   | Référence logique vers order-service (pas de FK physique — isolation microservices) |
| `user_id`           | UUID         | NOT NULL                           | Identifiant Keycloak de l'utilisateur — utilisé pour cibler le push WebSocket |
| `status`            | ENUM         | NOT NULL, DEFAULT 'ASSIGNED'       | Statut courant : `ASSIGNED` → `PICKED_UP` → `IN_TRANSIT` → `DELIVERED` |
| `carrier`           | VARCHAR(100) | NOT NULL                           | Transporteur simulé (ex : "SmartExpress")                |
| `tracking_number`   | VARCHAR(100) | NOT NULL, UNIQUE                   | Numéro de suivi simulé, généré à la création             |
| `estimated_delivery`| TIMESTAMP    | NOT NULL                           | Date de livraison estimée, calculée à la création        |
| `assigned_at`       | TIMESTAMP    | NOT NULL, DEFAULT NOW()            | Date d'assignation de la livraison                       |
| `delivered_at`      | TIMESTAMP    | nullable                           | Date de livraison effective — null tant que non livrée   |
| `version`           | BIGINT       | NOT NULL, DEFAULT 0                | Optimistic Lock — protège contre les mises à jour concurrentes du `@Scheduled` |
| `created_at`        | TIMESTAMP    | NOT NULL, DEFAULT NOW()            | Date de création                                         |
| `updated_at`        | TIMESTAMP    | NOT NULL, DEFAULT NOW()            | Date de dernière mise à jour                             |

---

## Diagramme

```
┌─────────────────────────────────────────────────────┐
│                     Delivery                        │
├─────────────────────────────────────────────────────┤
│ id                UUID        PK                    │
│ order_id          UUID        UNIQUE (idempotence)  │
│ user_id           UUID        pour WebSocket push   │
│ status            ENUM        machine à états       │
│ carrier           VARCHAR     transporteur simulé   │
│ tracking_number   VARCHAR     UNIQUE                │
│ estimated_delivery TIMESTAMP  calculée à la création│
│ assigned_at       TIMESTAMP                         │
│ delivered_at      TIMESTAMP   nullable              │
│ version           BIGINT      Optimistic Lock       │
│ created_at        TIMESTAMP                         │
│ updated_at        TIMESTAMP                         │
└─────────────────────────────────────────────────────┘
```

---

## Machine à états

```
[payment.succeeded]
        │
        ▼
   ASSIGNED  ──── @Scheduled ────▶  PICKED_UP
                                        │
                                   @Scheduled
                                        │
                                        ▼
                                   IN_TRANSIT ──── @Scheduled ────▶  DELIVERED
```

Chaque transition publie :
- Un message WebSocket vers le client (`/topic/delivery/{orderId}`)
- Un événement Kafka `delivery.updated`

La machine à états ne peut avancer que dans un sens — aucune transition inverse possible.

---

## Choix de conception

### Pas de FK physique sur `order_id`
Conformément aux règles d'architecture du projet, chaque microservice possède sa propre base de données isolée. `order_id` est une référence logique — `delivery-service` ne fait jamais de requête SQL dans la base de `order-service`.

### `order_id` UNIQUE — idempotence Kafka
Si Kafka rejoue le message `payment.succeeded` (at-least-once delivery), le service vérifie l'existence via `existsByOrderId` avant de créer une livraison. La contrainte UNIQUE en base est le filet de sécurité.

### Optimistic Lock sur `version`
Le job `@Scheduled` tourne toutes les N secondes et met à jour le statut. Sans Optimistic Lock, deux exécutions concurrentes pourraient avancer le statut deux fois. `@Version` de JPA garantit qu'une seule mise à jour gagne — l'autre lève une `OptimisticLockException` ignorée silencieusement.

### Pas de table `DeliveryEvent`
Un historique détaillé des transitions (chaque changement de statut avec timestamp) serait pertinent en production. Ici, Kafka (`delivery.updated`) joue ce rôle — les événements sont persistés dans le log Kafka et consommables par tout service intéressé (ex : `notification-service`).

### `user_id` stocké dans `Delivery`
Le WebSocket push doit cibler un utilisateur précis. `user_id` est extrait du JWT `payment.succeeded` reçu via Kafka (fat event) et stocké localement pour éviter tout appel inter-services au moment du push.

---

## Topics Kafka

| Direction | Topic              | Déclencheur                        |
|-----------|--------------------|------------------------------------|
| Consumer  | `payment.succeeded`| Crée la livraison en `ASSIGNED`    |
| Producer  | `delivery.updated` | Publié à chaque changement de statut |

---

## Endpoint REST

| Méthode | Path                              | Description                          | Auth     |
|---------|-----------------------------------|--------------------------------------|----------|
| GET     | `/api/deliveries/order/{orderId}` | Récupère la livraison d'une commande | JWT requis |

---

## WebSocket

| Canal                         | Direction      | Description                              |
|-------------------------------|----------------|------------------------------------------|
| `/topic/delivery/{orderId}`   | Server → Client | Push du nouveau statut à chaque transition |
