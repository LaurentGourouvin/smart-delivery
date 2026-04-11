# MCD — order-service

## Contexte

`order-service` gère le cycle de vie des commandes SmartDelivery.

Ce service applique le pattern **CQRS simplifié** :
- les commandes (write) modifient l'état et publient des événements Kafka
- les queries (read) lisent l'état courant en base

Il ne fait jamais de JOIN avec `user-service` ou `product-service`.
Les données nécessaires sont copiées au moment de la commande — dénormalisation intentionnelle.

---

## Entités

### orders

| Colonne | Type | Contraintes | Description |
|---|---|---|---|
| `id` | UUID | PK, NOT NULL | Identifiant de la commande |
| `user_id` | UUID | NOT NULL | Référence utilisateur (pas de FK inter-service) |
| `user_email` | VARCHAR(255) | NOT NULL | Email copié depuis le JWT |
| `status` | VARCHAR(20) | NOT NULL, default `'PENDING'` | Statut de la commande |
| `total_amount` | NUMERIC(10,2) | NOT NULL | Montant total calculé à la création |
| `delivery_street` | VARCHAR(255) | NOT NULL | Adresse copiée depuis user-service |
| `delivery_city` | VARCHAR(100) | NOT NULL | Ville de livraison |
| `delivery_postal_code` | VARCHAR(10) | NOT NULL | Code postal |
| `delivery_country` | VARCHAR(2) | NOT NULL, default `'FR'` | Pays |
| `created_at` | TIMESTAMP | NOT NULL | Date de création |
| `updated_at` | TIMESTAMP | NOT NULL | Date de dernière modification |

### order_items

| Colonne | Type | Contraintes | Description |
|---|---|---|---|
| `id` | UUID | PK, NOT NULL | Identifiant de la ligne |
| `order_id` | UUID | FK → orders.id, NOT NULL | Commande parente |
| `product_id` | UUID | NOT NULL | Référence produit (pas de FK inter-service) |
| `product_name` | VARCHAR(255) | NOT NULL | Nom copié depuis product-service |
| `unit_price` | NUMERIC(10,2) | NOT NULL | Prix au moment de la commande |
| `quantity` | INTEGER | NOT NULL | Quantité commandée |
| `subtotal` | NUMERIC(10,2) | NOT NULL | unit_price × quantity |

---

## Enumération status

```
PENDING     — commande créée, en attente de paiement
CONFIRMED   — paiement accepté
CANCELLED   — annulée (paiement refusé ou annulation utilisateur)
DELIVERED   — livrée
```

---

## Relations

```
orders (1) ────< order_items (N)

Une commande contient une ou plusieurs lignes.
Une ligne appartient à exactement une commande.
Si la commande est supprimée, ses lignes sont supprimées (CASCADE).
```

---

## Règles métier

- `total_amount` = somme des `subtotal` des lignes — calculé côté service, pas en base
- `subtotal` = `unit_price` × `quantity` — calculé côté service à la création
- `unit_price` et `product_name` sont copiés depuis `product-service` au moment de la commande — ils ne changent jamais après
- `delivery_*` est copié depuis l'adresse par défaut de l'utilisateur au moment de la commande
- Toute transition de statut publie un événement Kafka correspondant
- `user_id` et `product_id` sont des références UUID — jamais de FK inter-service

## Transitions de statut

```
PENDING → CONFIRMED   (événement: order.confirmed)
PENDING → CANCELLED   (événement: order.cancelled) — paiement refusé
CONFIRMED → DELIVERED (événement: order.delivered) — via delivery-service
```

## Événements Kafka publiés

```
order.created    — à la création de la commande
order.confirmed  — quand le paiement est accepté
order.cancelled  — quand le paiement est refusé (Saga compensation)
```

## Ce que ce service ne stocke PAS

- Détails complets du produit (description, images...) → product-service
- Historique des adresses utilisateur → user-service
- Détails du paiement → payment-service
- Statut de livraison → delivery-service
