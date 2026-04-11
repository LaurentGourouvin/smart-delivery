# MCD — payment-service

## Contexte

`payment-service` gère les paiements simulés de SmartDelivery.

Ce service est un **Kafka consumer** — il écoute `order.created` et déclenche
le paiement automatiquement. Il publie ensuite `payment.succeeded` ou `payment.failed`
pour orchestrer le Saga pattern.

**Conformité RGPD / PCI-DSS** : aucune donnée bancaire n'est stockée.
Ni numéro de carte, ni IBAN, ni CVV. Uniquement les métadonnées de transaction.

---

## Entités

### payments

| Colonne | Type | Contraintes | Description |
|---|---|---|---|
| `id` | UUID | PK, NOT NULL | Identifiant interne du paiement |
| `order_id` | UUID | UNIQUE, NOT NULL | Référence commande (pas de FK inter-service) |
| `user_id` | UUID | NOT NULL | Référence utilisateur |
| `payment_intent_id` | VARCHAR(100) | UNIQUE, NOT NULL | ID simulé (serait un ID Stripe en prod) |
| `amount` | NUMERIC(10,2) | NOT NULL | Montant du paiement |
| `status` | VARCHAR(20) | NOT NULL, default `'PENDING'` | Statut du paiement |
| `failure_reason` | VARCHAR(255) | NULL | Raison de l'échec si applicable |
| `created_at` | TIMESTAMP | NOT NULL | Date de création |
| `updated_at` | TIMESTAMP | NOT NULL | Date de mise à jour |

---

## Enumération status

```
PENDING    — paiement en cours de traitement
SUCCEEDED  — paiement accepté
FAILED     — paiement refusé
```

---

## Règles métier

- Un `order_id` ne peut avoir qu'un seul paiement — contrainte UNIQUE
- `payment_intent_id` est un UUID généré localement (simulation Stripe)
- 90% de succès / 10% d'échec aléatoire — pour démontrer le Saga en démo
- `amount` est copié depuis l'événement `order.created` — pas d'appel vers `order-service`
- En cas d'échec, `failure_reason` est renseigné avec la raison simulée

## Flux Saga

```
Kafka: order.created  →  payment-service consomme
                      →  simule le paiement (2s délai)
                      →  90% : publie payment.succeeded
                             → order-service confirme la commande
                             → delivery-service assigne une livraison
                      →  10% : publie payment.failed
                             → product-service restore le stock
                             → order-service annule la commande
```

## Événements Kafka

```
Consomme : order.created
Publie   : payment.succeeded
Publie   : payment.failed
```

## Ce que ce service ne stocke PAS

- Numéros de carte bancaire → PSP externe (Stripe en prod)
- IBAN / coordonnées bancaires → PSP externe
- CVV / codes de sécurité → jamais stockés nulle part
- Données personnelles détaillées → user-service
