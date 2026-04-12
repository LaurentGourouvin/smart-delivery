<script setup lang="ts">
interface Props {
  id: number
  brand: string
  name: string
  variant: string
  price: number
  qty: number
  color: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  changeQty: [id: number, delta: number]
  remove: [id: number]
}>()

function formatPrice(p: number) {
  return p.toFixed(2).replace('.', ',') + ' €'
}
</script>

<template>
  <div class="cart-item">

    <div class="item-info">
      <div class="item-img" :class="color">
        <div class="item-shape" />
      </div>
      <div>
        <div class="item-brand">{{ brand }}</div>
        <div class="item-name">{{ name }}</div>
        <div class="item-variant">{{ variant }}</div>
      </div>
    </div>

    <div class="qty-control">
      <button class="qty-btn" @click="emit('changeQty', id, -1)">−</button>
      <span class="qty-val">{{ qty }}</span>
      <button class="qty-btn" @click="emit('changeQty', id, +1)">+</button>
    </div>

    <div class="item-price">{{ formatPrice(price * qty) }}</div>

    <button class="item-delete" @click="emit('remove', id)">×</button>

  </div>
</template>

<style scoped>
.cart-item {
  display: grid;
  grid-template-columns: 3fr 1fr 1fr 40px;
  gap: 1rem;
  align-items: center;
  padding: 1.5rem 0;
  border-bottom: 1px solid rgba(26, 22, 20, 0.08);
}

.item-info {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.item-img {
  width: 72px;
  height: 96px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.p1 { background: linear-gradient(160deg, #EDD5D8, #D4A8B0); }
.p2 { background: linear-gradient(160deg, #D8EDD5, #A8D4B0); }
.p3 { background: linear-gradient(160deg, #D5D8ED, #A8B0D4); }
.p4 { background: linear-gradient(160deg, #EDE8D5, #D4C8A8); }
.p5 { background: linear-gradient(160deg, #F0E6E8, #DCC4C8); }
.p6 { background: linear-gradient(160deg, #E6EDF0, #C4D0DC); }

.item-shape {
  width: 28px;
  height: 48px;
  background: white;
  border-radius: 14px;
  box-shadow: 0 8px 24px rgba(26, 22, 20, 0.12);
}

.item-brand {
  font-size: 0.55rem;
  letter-spacing: 0.3em;
  text-transform: uppercase;
  color: var(--cherry);
  margin-bottom: 0.3rem;
}

.item-name {
  font-family: var(--font-display);
  font-size: 1rem;
  color: var(--ink);
  line-height: 1.3;
  margin-bottom: 0.2rem;
}

.item-variant {
  font-size: 0.65rem;
  color: var(--ink-soft);
  letter-spacing: 0.1em;
}

.qty-control {
  display: flex;
  align-items: center;
  border: 1px solid rgba(26, 22, 20, 0.2);
  width: fit-content;
}

.qty-btn {
  width: 30px;
  height: 30px;
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 0.9rem;
  color: var(--ink);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.qty-btn:hover { background: var(--ivory-dark); }

.qty-val {
  width: 32px;
  text-align: center;
  font-family: var(--font-display);
  font-size: 0.95rem;
  color: var(--ink);
}

.item-price {
  font-family: var(--font-display);
  font-size: 1.1rem;
  color: var(--ink);
}

.item-delete {
  width: 32px;
  height: 32px;
  background: transparent;
  border: none;
  cursor: pointer;
  color: rgba(74, 63, 58, 0.4);
  font-size: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s;
}

.item-delete:hover { color: var(--cherry); }

@media (max-width: 768px) {
  .cart-item {
    grid-template-columns: 1fr 40px;
    grid-template-rows: auto auto;
    gap: 1rem;
  }

  .item-info { grid-column: 1 / -1; }
  .item-price { font-size: 1rem; }
}
</style>