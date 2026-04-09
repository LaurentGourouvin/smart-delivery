# MCD — user-service

## Contexte

`user-service` gère les comptes utilisateurs et leurs adresses de livraison.
L'authentification et les rôles sont délégués à Keycloak (OAuth2/OIDC).
Ce service ne stocke jamais de mot de passe ni de rôle — ces données vivent dans Keycloak.

Le lien entre Keycloak et ce service se fait via `keycloak_id` :
l'UUID que Keycloak injecte dans chaque JWT sous le claim `sub`.

---

## Entités

### users

| Colonne | Type | Contraintes | Description |
|---|---|---|---|
| `id` | UUID | PK, NOT NULL | Identifiant interne du service |
| `keycloak_id` | UUID | UNIQUE, NOT NULL | Identifiant Keycloak (claim `sub` du JWT) |
| `email` | VARCHAR(255) | UNIQUE, NOT NULL | Email de l'utilisateur |
| `first_name` | VARCHAR(100) | NOT NULL | Prénom |
| `last_name` | VARCHAR(100) | NOT NULL | Nom de famille |
| `phone` | VARCHAR(20) | NULL | Numéro de téléphone (optionnel) |
| `active` | BOOLEAN | NOT NULL, default `true` | Compte actif ou désactivé |
| `created_at` | TIMESTAMP | NOT NULL | Date de création du compte |
| `updated_at` | TIMESTAMP | NOT NULL | Date de dernière modification |

### addresses

| Colonne | Type | Contraintes | Description |
|---|---|---|---|
| `id` | UUID | PK, NOT NULL | Identifiant de l'adresse |
| `user_id` | UUID | FK → users.id, NOT NULL | Propriétaire de l'adresse |
| `label` | VARCHAR(50) | NOT NULL | Libellé (ex: "Maison", "Bureau") |
| `street` | VARCHAR(255) | NOT NULL | Rue et numéro |
| `city` | VARCHAR(100) | NOT NULL | Ville |
| `postal_code` | VARCHAR(10) | NOT NULL | Code postal |
| `country` | VARCHAR(2) | NOT NULL, default `'FR'` | Code pays ISO 3166-1 alpha-2 |
| `is_default` | BOOLEAN | NOT NULL, default `false` | Adresse par défaut de l'utilisateur |

---

## Relations

```
users (1) ────< addresses (N)

Un utilisateur peut avoir zéro ou plusieurs adresses.
Une adresse appartient à exactement un utilisateur.
Si l'utilisateur est supprimé, ses adresses sont supprimées (CASCADE).
```

---

## Règles métier

- Un utilisateur ne peut avoir qu'une seule adresse avec `is_default = true`
- `keycloak_id` est fourni par Keycloak au premier login — il ne change jamais
- `email` est synchronisé depuis Keycloak à la création du compte
- `active = false` désactive le compte sans le supprimer (soft delete)
- Ce service ne stocke aucun mot de passe — l'auth est 100% déléguée à Keycloak

---

## Ce que ce service ne stocke PAS

- Mots de passe → Keycloak
- Rôles (ADMIN, CUSTOMER...) → claims JWT Keycloak
- Sessions → Redis
- Provider OAuth2 (Google, GitHub...) → Keycloak Identity Providers
