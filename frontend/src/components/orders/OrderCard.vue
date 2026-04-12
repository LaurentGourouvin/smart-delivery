<script setup lang="ts">
import { ref } from 'vue'
import OrderTracking from '@/components/orders/OrderTracking.vue'

interface OrderItem {
  brand: string
  name: string
  meta: string
  price: string
  color: string
}

interface Tracking {
  steps: string[]
  current: number
}

interface Props {
  id: string
  date: string
  total: string
  status: 'delivered' | 'transit' | 'processing' | 'cancelled'
  statusLabel: string
  items: OrderItem[]
  tracking: Tracking
}

const props = defineProps<Props>()
const emit = defineEmits<{ reorder: [id: string] }>()

const open = ref(false)

const STATUS_CLASS: Record<string, string> = {
  delivered: 'status--delivered',
  transit:   'status--transit',
  processing:'status--processing',
  cancelled: 'status--cancelled',
}
</script>

<template>
  <div class="order-card" :class="{ 'order-card--open': open }">

    <!-- En-tête -->
    <div class="order-head" @click="open = !open">
      <div>
        <div class="head-label">Commande</div>
        <div class="head-value head-value--num">{{ id }}</div>
      </div>
      <div>
        <div class="head-label">Date</div>
        <div class="head-value">{{ date }}</div>
      </div>
      <div>
        <div class="head-label">Total</div>
        <div class="head-value">{{ total }}</div>
      </div>
      <div>
        <div class="head-label">Statut</div>
        <div class="status-badge" :class="STATUS_CLASS[status]">
          <span class="status-dot" />
          {{ statusLabel }}
        </div>
      </div>
      <button class="toggle-btn" :class="{ open }" @click.stop="open = !open">▾</button>
    </div>

    <!-- Détail (v-show pour garder le DOM et éviter le flash) -->
    <div v-show="open" class="order-detail">

      <OrderTracking
          :steps="tracking.steps"
          :current="tracking.current"
          :cancelled="status === 'cancelled'"
      />

      <div class="detail-items">
        <div
            v-for="item in items"
            :key="item.name"
            class="detail-item"
        >
          <div class="detail-img" :class="item.color">
            <div class="detail-shape" />
          </div>
          <div class="detail-info">
            <div class="detail-brand">{{ item.brand }}</div>
            <div class="detail-name">{{ item.name }}</div>
            <div class="detail-meta">{{ item.meta }}</div>
          </div>
          <div class="detail-price">{{ item.price }}</div>
        </div>
      </div>

      <div class="detail-footer">
        <button class="btn-invoice">Facture PDF</button>
        <button
            v-if="status !== 'cancelled'"
            class="btn-reorder"
            @click="emit('reorder', id)"
        >
          Commander à nouveau
        </button>
      </div>

    </div>
  </div>
</template>

<style scoped>
.order-card {
  border: 1px solid rgba(26, 22, 20, 0.1);
  background: var(--ivory);
  transition: border-color 0.2s;
}

.order-card:hover,
.order-card--open { border-color: rgba(196, 72, 90, 0.3); }

/* ── En-tête ── */
.order-head {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 1fr auto;
  gap: 1rem;
  align-items: center;
  padding: 1.5rem 2rem;
  border-bottom: 1px solid rgba(26, 22, 20, 0.06);
  cursor: pointer;
  user-select: none;
}

.head-label {
  font-size: 0.55rem;
  letter-spacing: 0.25em;
  text-transform: uppercase;
  color: var(--ink-soft);
  margin-bottom: 0.3rem;
}

.head-value {
  font-family: var(--font-display);
  font-size: 1rem;
  color: var(--ink);
}

.head-value--num {
  font-family: var(--font-body);
  font-size: 0.8rem;
  letter-spacing: 0.05em;
}

/* ── Badge statut ── */
.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  font-size: 0.55rem;
  letter-spacing: 0.15em;
  text-transform: uppercase;
  padding: 0.3rem 0.7rem;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex-shrink: 0;
}

.status--delivered  { background: rgba(138,158,140,0.15); color: var(--sage); }
.status--delivered .status-dot  { background: var(--sage); }
.status--transit    { background: rgba(201,169,110,0.15); color: #9A7A3A; }
.status--transit .status-dot    { background: var(--gold); }
.status--processing { background: rgba(196,72,90,0.08);   color: var(--cherry); }
.status--processing .status-dot { background: var(--cherry); }
.status--cancelled  { background: rgba(26,22,20,0.06);    color: var(--ink-soft); }
.status--cancelled .status-dot  { background: var(--ink-soft); }

/* ── Toggle ── */
.toggle-btn {
  width: 28px;
  height: 28px;
  background: transparent;
  border: none;
  cursor: pointer;
  color: var(--ink-soft);
  font-size: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s, color 0.2s;
}

.toggle-btn.open {
  transform: rotate(180deg);
  color: var(--cherry);
}

/* ── Détail ── */
.order-detail { padding: 1.5rem 2rem; }

.detail-items {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid rgba(26, 22, 20, 0.06);
}

.detail-item:last-child { border-bottom: none; padding-bottom: 0; }

.detail-img {
  width: 52px;
  height: 68px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.p1 { background: linear-gradient(160deg, #EDD5D8, #D4A8B0); }
.p2 { background: linear-gradient(160deg, #D8EDD5, #A8D4B0); }
.p3 { background: linear-gradient(160deg, #D5D8ED, #A8B0D4); }
.p4 { background: linear-gradient(160deg, #EDE8D5, #D4C8A8); }

.detail-shape {
  width: 20px;
  height: 34px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(26, 22, 20, 0.12);
}

.detail-info { flex: 1; }

.detail-brand {
  font-size: 0.55rem;
  letter-spacing: 0.25em;
  text-transform: uppercase;
  color: var(--cherry);
  margin-bottom: 0.2rem;
}

.detail-name {
  font-family: var(--font-display);
  font-size: 0.95rem;
  color: var(--ink);
  margin-bottom: 0.1rem;
}

.detail-meta {
  font-size: 0.65rem;
  color: var(--ink-soft);
}

.detail-price {
  font-family: var(--font-display);
  font-size: 1rem;
  color: var(--ink);
}

/* ── Footer ── */
.detail-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.btn-invoice {
  background: transparent;
  color: var(--ink);
  border: 1px solid rgba(26, 22, 20, 0.2);
  padding: 0.7rem 1.5rem;
  font-family: var(--font-body);
  font-size: 0.6rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-invoice:hover { border-color: var(--cherry); color: var(--cherry); }

.btn-reorder {
  background: var(--cherry);
  color: white;
  border: none;
  padding: 0.7rem 1.5rem;
  font-family: var(--font-body);
  font-size: 0.6rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-reorder:hover { background: var(--ink); }

/* ── Responsive ── */
@media (max-width: 768px) {
  .order-head {
    grid-template-columns: 1fr 1fr;
    grid-template-rows: auto auto;
    gap: 1rem;
    padding: 1.2rem 1.5rem;
  }

  .toggle-btn { grid-column: 2; justify-self: end; grid-row: 1; }

  .order-detail { padding: 1.2rem 1.5rem; }
}
</style>