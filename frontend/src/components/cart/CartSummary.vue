<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

interface Props {
  subtotal: number
}

const props = defineProps<Props>()
const emit = defineEmits<{ order: [] }>()

const router = useRouter()

const SHIPPING_OPTIONS = [
  { id: 'standard', label: 'Standard (5-7 jours)', price: 0,    display: 'Offerte' },
  { id: 'express',  label: 'Express (2-3 jours)',  price: 4.99, display: '4,99 €' },
  { id: 'chrono',   label: '24h chrono',           price: 9.99, display: '9,99 €' },
]

const selectedShipping = ref('standard')

const shippingOption = computed(() =>
    SHIPPING_OPTIONS.find(o => o.id === selectedShipping.value)!
)

const total = computed(() => props.subtotal + shippingOption.value.price)

function fmt(p: number) {
  return p.toFixed(2).replace('.', ',') + ' €'
}
</script>

<template>
  <aside class="cart-summary">

    <div class="summary-title">Résumé</div>

    <div class="summary-line">
      <span>Sous-total</span>
      <span>{{ fmt(subtotal) }}</span>
    </div>

    <div class="summary-shipping">
      <div class="shipping-label">Livraison estimée</div>
      <div class="shipping-options">
        <div
            v-for="option in SHIPPING_OPTIONS"
            :key="option.id"
            class="shipping-option"
            :class="{ active: selectedShipping === option.id }"
            @click="selectedShipping = option.id"
        >
          <span class="shipping-radio" />
          <span class="shipping-name">{{ option.label }}</span>
          <span class="shipping-price">{{ option.display }}</span>
        </div>
      </div>
    </div>

    <div class="summary-line">
      <span>Livraison</span>
      <span>{{ shippingOption.display }}</span>
    </div>

    <div class="summary-line total">
      <span class="total-label">Total</span>
      <span class="total-value">{{ fmt(total) }}</span>
    </div>

    <button class="btn-order" @click="emit('order')">Commander →</button>

    <div class="summary-secure">🔒 Paiement 100% sécurisé</div>

  </aside>
</template>

<style scoped>
.cart-summary {
  border-left: 1px solid rgba(26, 22, 20, 0.08);
  padding: 3rem 2.5rem;
  position: sticky;
  top: 80px;
  max-height: calc(100vh - 80px);
  overflow-y: auto;
}

.summary-title {
  font-size: 0.6rem;
  letter-spacing: 0.4em;
  text-transform: uppercase;
  color: var(--cherry);
  margin-bottom: 2rem;
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.summary-title::before {
  content: '';
  width: 20px;
  height: 1px;
  background: var(--cherry);
}

.summary-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.8rem 0;
  border-bottom: 1px solid rgba(26, 22, 20, 0.08);
  font-size: 0.8rem;
  color: var(--ink-soft);
}

.summary-line.total {
  border-bottom: none;
  padding-top: 1.2rem;
}

.total-label {
  font-family: var(--font-display);
  font-size: 1.1rem;
  color: var(--ink);
}

.total-value {
  font-family: var(--font-display);
  font-size: 1.6rem;
  color: var(--ink);
}

.summary-shipping {
  margin: 1.5rem 0;
  padding: 1rem;
  background: rgba(138, 158, 140, 0.1);
  border: 1px solid rgba(138, 158, 140, 0.3);
}

.shipping-label {
  font-size: 0.6rem;
  letter-spacing: 0.15em;
  text-transform: uppercase;
  color: var(--sage);
  margin-bottom: 0.4rem;
}

.shipping-options {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-top: 0.8rem;
}

.shipping-option {
  display: flex;
  align-items: center;
  font-size: 0.75rem;
  color: var(--ink-soft);
  cursor: pointer;
  padding: 0.3rem 0;
  transition: color 0.2s;
}

.shipping-option.active { color: var(--ink); }

.shipping-radio {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 1px solid rgba(26, 22, 20, 0.3);
  margin-right: 0.6rem;
  flex-shrink: 0;
  position: relative;
  display: inline-block;
  transition: border-color 0.2s, background 0.2s;
}

.shipping-option.active .shipping-radio {
  background: var(--cherry);
  border-color: var(--cherry);
}

.shipping-option.active .shipping-radio::after {
  content: '';
  position: absolute;
  inset: 3px;
  border-radius: 50%;
  background: var(--ivory);
}

.shipping-name {
  flex: 1;
}

.shipping-price {
  color: var(--ink);
}

.btn-order {
  width: 100%;
  background: var(--cherry);
  color: white;
  border: none;
  padding: 1.1rem;
  margin-top: 1.5rem;
  font-family: var(--font-body);
  font-size: 0.7rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-order:hover { background: var(--ink); }

.summary-secure {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  margin-top: 1rem;
  font-size: 0.65rem;
  color: var(--ink-soft);
  letter-spacing: 0.1em;
}
</style>