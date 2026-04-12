<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import OrderCard from '@/components/orders/OrderCard.vue'

// ── Types ──────────────────────────────────────────────────────────────────
type OrderStatus = 'delivered' | 'transit' | 'processing' | 'cancelled'

interface OrderItem {
  brand: string
  name: string
  meta: string
  price: string
  color: string
}

interface Order {
  id: string
  date: string
  total: string
  status: OrderStatus
  statusLabel: string
  items: OrderItem[]
  tracking: { steps: string[]; current: number }
}

// ── Données statiques ──────────────────────────────────────────────────────
// Phase statique — sera remplacé par un fetch vers order-service
const TRACKING_STEPS = ['Confirmée', 'Préparée', 'Expédiée', 'Livrée']

const allOrders: Order[] = [
  {
    id: 'SD-2026-0412', date: '12 avr. 2026', total: '83,98 €',
    status: 'transit', statusLabel: 'En transit',
    items: [
      { brand: 'COSRX',     name: 'Advanced Snail 96 Mucin Power Essence', meta: '100ml · x2', price: '49,98 €', color: 'p1' },
      { brand: 'Innisfree', name: 'Green Tea Seed Serum',                   meta: '80ml · x1',  price: '29,00 €', color: 'p3' },
    ],
    tracking: { steps: TRACKING_STEPS, current: 2 },
  },
  {
    id: 'SD-2026-0401', date: '1 avr. 2026', total: '35,00 €',
    status: 'delivered', statusLabel: 'Livrée',
    items: [
      { brand: 'Laneige', name: 'Water Sleeping Mask Lavender', meta: '70ml · x1', price: '35,00 €', color: 'p2' },
    ],
    tracking: { steps: TRACKING_STEPS, current: 3 },
  },
  {
    id: 'SD-2026-0320', date: '20 mars 2026', total: '64,00 €',
    status: 'delivered', statusLabel: 'Livrée',
    items: [
      { brand: 'Sulwhasoo', name: 'First Care Activating Serum', meta: '90ml · x1', price: '64,00 €', color: 'p4' },
    ],
    tracking: { steps: TRACKING_STEPS, current: 3 },
  },
  {
    id: 'SD-2026-0308', date: '8 mars 2026', total: '22,50 €',
    status: 'cancelled', statusLabel: 'Annulée',
    items: [
      { brand: 'Some By Mi', name: 'AHA BHA PHA 30 Days Miracle Toner', meta: '150ml · x1', price: '22,50 €', color: 'p1' },
    ],
    tracking: { steps: TRACKING_STEPS, current: 0 },
  },
]

// ── State ──────────────────────────────────────────────────────────────────
const router = useRouter()
const activeFilter = ref<OrderStatus | 'all'>('all')

const TABS: { key: OrderStatus | 'all'; label: string }[] = [
  { key: 'all',        label: 'Toutes' },
  { key: 'delivered',  label: 'Livrées' },
  { key: 'transit',    label: 'En cours' },
  { key: 'processing', label: 'En traitement' },
  { key: 'cancelled',  label: 'Annulées' },
]

// ── Computed ───────────────────────────────────────────────────────────────
const filteredOrders = computed(() =>
    activeFilter.value === 'all'
        ? allOrders
        : allOrders.filter(o => o.status === activeFilter.value)
)

// ── Handlers ───────────────────────────────────────────────────────────────
function onReorder(id: string) {
  // TODO — connecter au store panier (Phase 3)
  console.log('Reorder:', id)
  router.push('/panier')
}
</script>

<template>
  <div class="orders-root">

    <!-- Breadcrumb -->
    <div class="breadcrumb">
      <RouterLink to="/">Accueil</RouterLink>
      <span class="sep">/</span>
      <span class="current">Mes commandes</span>
    </div>

    <!-- En-tête -->
    <div class="orders-header">
      <div class="orders-eyebrow">Historique</div>
      <h1 class="orders-title">Mes <em>commandes</em></h1>
    </div>

    <!-- Tabs filtre -->
    <div class="orders-tabs">
      <div
          v-for="tab in TABS"
          :key="tab.key"
          class="tab"
          :class="{ active: activeFilter === tab.key }"
          @click="activeFilter = tab.key"
      >
        {{ tab.label }}
      </div>
    </div>

    <!-- Liste -->
    <div v-if="filteredOrders.length" class="orders-list">
      <OrderCard
          v-for="order in filteredOrders"
          :key="order.id"
          v-bind="order"
          @reorder="onReorder"
      />
    </div>

    <!-- État vide -->
    <div v-else class="orders-empty">
      <p class="empty-title">Aucune commande <em>ici</em></p>
      <p class="empty-desc">Vous n'avez pas encore de commande dans cette catégorie.</p>
      <button class="btn-catalogue" @click="router.push('/catalogue')">
        Découvrir le catalogue
      </button>
    </div>

  </div>
</template>

<style scoped>
.orders-root {
  background: var(--ivory);
  padding-top: 80px; /* compense AppNav fixe */
  min-height: 100vh;
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
.orders-header { padding: 3rem 4rem 2rem; }

.orders-eyebrow {
  font-size: 0.6rem;
  letter-spacing: 0.4em;
  text-transform: uppercase;
  color: var(--cherry);
  margin-bottom: 0.8rem;
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.orders-eyebrow::before {
  content: '';
  width: 20px;
  height: 1px;
  background: var(--cherry);
}

.orders-title {
  font-family: var(--font-display);
  font-size: clamp(2rem, 3vw, 3rem);
  font-weight: 300;
  line-height: 1.1;
  color: var(--ink);
}

.orders-title em { font-style: italic; color: var(--cherry); }

/* ── Tabs ── */
.orders-tabs {
  display: flex;
  padding: 0 4rem;
  border-bottom: 1px solid rgba(26, 22, 20, 0.1);
  margin-bottom: 2.5rem;
}

.tab {
  font-size: 0.6rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  padding: 0.9rem 1.5rem;
  cursor: pointer;
  color: var(--ink-soft);
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
  transition: all 0.2s;
  user-select: none;
}

.tab:hover { color: var(--ink); }
.tab.active { color: var(--cherry); border-bottom-color: var(--cherry); }

/* ── Liste ── */
.orders-list {
  padding: 0 4rem 4rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

/* ── État vide ── */
.orders-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 6rem;
  text-align: center;
  gap: 1rem;
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
@media (max-width: 768px) {
  .breadcrumb,
  .orders-header,
  .orders-list  { padding-left: 1.5rem; padding-right: 1.5rem; }

  .orders-tabs  { padding: 0 1.5rem; overflow-x: auto; }
  .tab          { white-space: nowrap; padding: 0.9rem 1rem; }
}
</style>