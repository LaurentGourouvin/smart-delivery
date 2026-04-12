<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import CartItem from '@/components/cart/CartItem.vue'
import CartSummary from '@/components/cart/CartSummary.vue'

// ── Types ──────────────────────────────────────────────────────────────────
interface CartItem {
  id: number
  brand: string
  name: string
  variant: string
  price: number
  qty: number
  color: string
}

// ── Données statiques ──────────────────────────────────────────────────────
// Phase statique — sera remplacé par un store Pinia panier (Phase 3)
const items = ref<CartItem[]>([
  { id: 1, brand: 'COSRX',     name: 'Advanced Snail 96 Mucin Power Essence', variant: '100ml', price: 24.99, qty: 2, color: 'p1' },
  { id: 2, brand: 'Laneige',   name: 'Water Sleeping Mask Lavender',           variant: '70ml',  price: 35.00, qty: 1, color: 'p2' },
  { id: 3, brand: 'Innisfree', name: 'Green Tea Seed Serum',                   variant: '80ml',  price: 29.00, qty: 1, color: 'p3' },
])

const router = useRouter()

// ── Computed ───────────────────────────────────────────────────────────────
const totalCount = computed(() => items.value.reduce((s, i) => s + i.qty, 0))
const subtotal   = computed(() => items.value.reduce((s, i) => s + i.price * i.qty, 0))
const isEmpty    = computed(() => items.value.length === 0)

// ── Handlers ───────────────────────────────────────────────────────────────
function changeQty(id: number, delta: number) {
  const item = items.value.find(i => i.id === id)
  if (!item) return
  item.qty = Math.max(1, Math.min(10, item.qty + delta))
}

function removeItem(id: number) {
  items.value = items.value.filter(i => i.id !== id)
}

function onOrder() {
  // TODO — connecter au order-service (Phase 3)
  console.log('Order placed')
  router.push('/commandes')
}
</script>

<template>
  <div class="cart-root">

    <!-- Breadcrumb -->
    <div class="breadcrumb">
      <RouterLink to="/">Accueil</RouterLink>
      <span class="sep">/</span>
      <span class="current">Mon panier</span>
    </div>

    <!-- En-tête -->
    <div class="cart-header">
      <div>
        <div class="cart-eyebrow">Récapitulatif</div>
        <h1 class="cart-title">Mon <em>panier</em></h1>
      </div>
      <span class="cart-count">
        {{ totalCount }} article{{ totalCount > 1 ? 's' : '' }}
      </span>
    </div>

    <!-- Panier vide -->
    <div v-if="isEmpty" class="cart-empty">
      <h2 class="empty-title">Votre panier est <em>vide</em></h2>
      <p class="empty-desc">
        Découvrez notre sélection de soins coréens<br>
        et commencez votre rituel K-beauty.
      </p>
      <button class="btn-catalogue" @click="router.push('/catalogue')">
        Voir le catalogue
      </button>
    </div>

    <!-- Contenu panier -->
    <div v-else class="cart-body">

      <div class="cart-items">
        <div class="cart-cols">
          <span>Produit</span>
          <span>Quantité</span>
          <span>Prix</span>
          <span />
        </div>

        <CartItem
            v-for="item in items"
            :key="item.id"
            v-bind="item"
            @change-qty="changeQty"
            @remove="removeItem"
        />
      </div>

      <CartSummary
          :subtotal="subtotal"
          @order="onOrder"
      />

    </div>

  </div>
</template>

<style scoped>
.cart-root {
  background: var(--ivory);
  padding-top: 80px; /* compense AppNav fixe */
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* ── Breadcrumb ── */
.breadcrumb {
  padding: 1rem 4rem;
  font-size: 0.6rem;
  letter-spacing: 0.25em;
  text-transform: uppercase;
  color: var(--ink-soft);
  border-bottom: 1px solid rgba(26, 22, 20, 0.1);
  display: flex;
  align-items: center;
  gap: 0.6rem;
}

.breadcrumb a {
  color: var(--ink-soft);
  text-decoration: none;
  transition: color 0.2s;
}

.breadcrumb a:hover { color: var(--cherry); }
.breadcrumb .sep { opacity: 0.4; }
.breadcrumb .current { color: var(--cherry); }

/* ── En-tête ── */
.cart-header {
  padding: 3rem 4rem 2rem;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
}

.cart-eyebrow {
  font-size: 0.6rem;
  letter-spacing: 0.4em;
  text-transform: uppercase;
  color: var(--cherry);
  margin-bottom: 0.8rem;
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.cart-eyebrow::before {
  content: '';
  width: 20px;
  height: 1px;
  background: var(--cherry);
}

.cart-title {
  font-family: var(--font-display);
  font-size: clamp(2rem, 3vw, 3rem);
  font-weight: 300;
  line-height: 1.1;
  color: var(--ink);
}

.cart-title em { font-style: italic; color: var(--cherry); }

.cart-count {
  font-size: 0.65rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--ink-soft);
  padding-bottom: 0.4rem;
}

/* ── Body ── */
.cart-body {
  display: grid;
  grid-template-columns: 1fr 360px;
  align-items: start;
  flex: 1;
}

/* ── Liste articles ── */
.cart-items {
  padding: 0 4rem 4rem;
}

.cart-cols {
  display: grid;
  grid-template-columns: 3fr 1fr 1fr 40px;
  gap: 1rem;
  align-items: center;
  padding: 0.8rem 0;
  border-bottom: 1px solid rgba(26, 22, 20, 0.08);
  font-size: 0.6rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--ink-soft);
}

/* ── Panier vide ── */
.cart-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  padding: 6rem;
  text-align: center;
  gap: 1.5rem;
}

.empty-title {
  font-family: var(--font-display);
  font-size: 2rem;
  font-weight: 300;
  color: var(--ink);
}

.empty-title em { font-style: italic; color: var(--cherry); }

.empty-desc {
  font-size: 0.8rem;
  color: var(--ink-soft);
  line-height: 1.8;
}

.btn-catalogue {
  background: var(--cherry);
  color: white;
  border: none;
  padding: 0.9rem 2.5rem;
  font-family: var(--font-body);
  font-size: 0.7rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  cursor: pointer;
  transition: background 0.3s;
  margin-top: 0.5rem;
}

.btn-catalogue:hover { background: var(--ink); }

/* ── Responsive ── */
@media (max-width: 1024px) {
  .cart-body { grid-template-columns: 1fr 300px; }
  .cart-items { padding: 0 2rem 3rem; }
  .cart-header { padding: 2rem 2rem 1.5rem; }
  .breadcrumb { padding: 1rem 2rem; }
}

@media (max-width: 768px) {
  .cart-body { grid-template-columns: 1fr; }
  .cart-items { padding: 0 1.5rem 2rem; }
  .cart-cols { display: none; }
  .cart-header { padding: 2rem 1.5rem 1rem; }
  .breadcrumb { padding: 1rem 1.5rem; }
}
</style>