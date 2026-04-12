<script setup lang="ts">
import { useRouter } from 'vue-router'

interface Props {
  id: number
  brand: string
  name: string
  skin: string
  price: number
  oldPrice?: number
  tag?: string | null
  color: string
  cat: string
}

const props = defineProps<Props>()
const emit = defineEmits<{
  addToCart: [id: number]
}>()

const router = useRouter()

function formatPrice(p: number): string {
  return p.toFixed(2).replace('.', ',') + ' €'
}
</script>

<template>
  <div class="product-card" @click="router.push(`/produits/${id}`)">
    <div class="product-img" :class="color">
      <div class="product-img-inner">
        <div class="product-shape" />
      </div>
      <div v-if="tag" class="product-badge">{{ tag }}</div>
      <div class="product-wishlist" @click.stop>♡</div>
    </div>

    <div class="product-brand">{{ brand }}</div>
    <div class="product-name">{{ name }}</div>
    <div class="product-skin">{{ skin }}</div>

    <div class="product-footer">
      <div>
        <span class="product-price">{{ formatPrice(price) }}</span>
        <span v-if="oldPrice" class="product-price-old">{{ formatPrice(oldPrice) }}</span>
      </div>
      <button class="product-add" @click.stop="emit('addToCart', id)">+</button>
    </div>

    <button class="product-add-label" @click.stop="emit('addToCart', id)">
      Ajouter au panier
    </button>
  </div>
</template>

<style scoped>
.product-card {
  cursor: pointer;
}

.product-img {
  aspect-ratio: 3/4;
  position: relative;
  overflow: hidden;
  margin-bottom: 1.2rem;
}

.p1 { background: linear-gradient(160deg, #EDD5D8, #D4A8B0); }
.p2 { background: linear-gradient(160deg, #D8EDD5, #A8D4B0); }
.p3 { background: linear-gradient(160deg, #D5D8ED, #A8B0D4); }
.p4 { background: linear-gradient(160deg, #EDE8D5, #D4C8A8); }
.p5 { background: linear-gradient(160deg, #F0E6E8, #DCC4C8); }
.p6 { background: linear-gradient(160deg, #E6EDF0, #C4D0DC); }

.product-img-inner {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-shape {
  width: 60px;
  height: 90px;
  background: white;
  border-radius: 30px;
  box-shadow: 0 20px 40px rgba(26, 22, 20, 0.15);
  transition: transform 0.5s ease;
}

.product-card:hover .product-shape {
  transform: translateY(-8px);
}

.product-badge {
  position: absolute;
  top: 1rem;
  left: 1rem;
  background: var(--cherry);
  color: white;
  font-size: 0.55rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  padding: 0.25rem 0.6rem;
}

.product-wishlist {
  position: absolute;
  top: 0.8rem;
  right: 0.8rem;
  width: 28px;
  height: 28px;
  border: 1px solid rgba(26, 22, 20, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  background: rgba(247, 243, 238, 0.8);
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s;
}

.product-card:hover .product-wishlist {
  opacity: 1;
}

.product-brand {
  font-size: 0.6rem;
  letter-spacing: 0.3em;
  text-transform: uppercase;
  color: var(--cherry);
  margin-bottom: 0.35rem;
}

.product-name {
  font-family: var(--font-display);
  font-size: 1.05rem;
  font-weight: 400;
  color: var(--ink);
  line-height: 1.3;
  margin-bottom: 0.3rem;
}

.product-skin {
  font-size: 0.6rem;
  letter-spacing: 0.15em;
  text-transform: uppercase;
  color: var(--sage);
  margin-bottom: 0.8rem;
}

.product-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.product-price {
  font-family: var(--font-display);
  font-size: 1.1rem;
  color: var(--ink);
}

.product-price-old {
  font-family: var(--font-display);
  font-size: 0.75rem;
  color: rgba(74, 63, 58, 0.4);
  text-decoration: line-through;
  margin-left: 0.4rem;
}

.product-add {
  width: 30px;
  height: 30px;
  border: 1px solid var(--ink);
  background: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 1rem;
  color: var(--ink);
  transition: all 0.3s;
}

.product-card:hover .product-add {
  background: var(--ink);
  color: var(--ivory);
}

.product-add-label {
  display: block;
  width: 100%;
  margin-top: 1rem;
  padding: 0.6rem;
  border: 1px solid rgba(26, 22, 20, 0.15);
  background: transparent;
  color: var(--ink-soft);
  font-family: var(--font-body);
  font-size: 0.6rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s, background 0.2s, color 0.2s;
  text-align: center;
}

.product-card:hover .product-add-label {
  opacity: 1;
}

.product-add-label:hover {
  background: var(--ink);
  color: var(--ivory);
}
</style>