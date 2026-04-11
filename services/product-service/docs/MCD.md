# MCD — product-service

## Contexte

`product-service` gère le catalogue produits et le stock de SmartDelivery,
une plateforme e-commerce spécialisée en K-beauty (cosmétiques coréens).

Ce service est le seul autorisé à lire et modifier le stock.
`order-service` appelle son API REST pour vérifier et décrémenter le stock —
il ne touche jamais directement cette base.

Le stock est protégé par un **Optimistic Lock** (`@Version`) pour gérer
les accès concurrents sans verrouillage pessimiste.

---

## Entités

### categories

| Colonne | Type | Contraintes | Description |
|---|---|---|---|
| `id` | UUID | PK, NOT NULL | Identifiant de la catégorie |
| `name` | VARCHAR(100) | UNIQUE, NOT NULL | Nom affiché (ex: "Sérum", "Crème") |
| `slug` | VARCHAR(100) | UNIQUE, NOT NULL | Identifiant URL (ex: "serum", "creme") |
| `description` | TEXT | NULL | Description de la catégorie |
| `created_at` | TIMESTAMP | NOT NULL | Date de création |

### products

| Colonne | Type | Contraintes | Description |
|---|---|---|---|
| `id` | UUID | PK, NOT NULL | Identifiant du produit |
| `category_id` | UUID | FK → categories.id, NOT NULL | Catégorie du produit |
| `name` | VARCHAR(255) | NOT NULL | Nom du produit |
| `description` | TEXT | NULL | Description détaillée |
| `brand` | VARCHAR(100) | NOT NULL | Marque coréenne (ex: COSRX, Laneige) |
| `price` | NUMERIC(10,2) | NOT NULL | Prix en euros |
| `stock` | INTEGER | NOT NULL, default 0 | Quantité en stock |
| `version` | BIGINT | NOT NULL, default 0 | Optimistic Lock — géré par Hibernate |
| `image_url` | VARCHAR(500) | NULL | URL de l'image produit |
| `skin_type` | VARCHAR(20) | NOT NULL, default 'ALL' | Type de peau ciblé |
| `volume_ml` | INTEGER | NULL | Contenance en ml (optionnel) |
| `active` | BOOLEAN | NOT NULL, default true | Produit visible ou archivé |
| `created_at` | TIMESTAMP | NOT NULL | Date de création |
| `updated_at` | TIMESTAMP | NOT NULL | Date de dernière modification |

---

## Enumération skin_type

```
OILY        — Peau grasse
DRY         — Peau sèche
COMBINATION — Peau mixte
SENSITIVE   — Peau sensible
ALL         — Tous types de peau
```

---

## Relations

```
categories (1) ────< products (N)

Une catégorie contient zéro ou plusieurs produits.
Un produit appartient à exactement une catégorie.
Si une catégorie est supprimée, ses produits sont bloqués (RESTRICT).
```

---

## Règles métier

- `stock` ne peut jamais être négatif — contrainte CHECK en base
- `price` doit être strictement positif — contrainte CHECK en base
- `version` est géré exclusivement par Hibernate (`@Version`) — jamais modifié manuellement
- Toute décrémentation de stock passe par `order-service` via REST — jamais en direct
- `active = false` archive le produit sans le supprimer
- `slug` est généré automatiquement depuis `name` côté service (lowercase, tirets)

---

## Ce que ce service ne stocke PAS

- Historique des commandes → `order-service`
- Avis clients → hors scope Phase 1
- Prix promotionnels → hors scope Phase 1
- Stocks par entrepôt → hors scope Phase 1
