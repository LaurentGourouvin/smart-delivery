<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const products = [
  { id: 1, brand: 'COSRX', name: 'Advanced Snail 96 Mucin Power Essence', skin: 'Tous types', price: 24.99, tag: 'Bestseller', color: 'p1' },
  { id: 2, brand: 'Laneige', name: 'Water Sleeping Mask Lavender', skin: 'Peau sèche', price: 35.00, tag: 'Nouveau', color: 'p2' },
  { id: 3, brand: 'Innisfree', name: 'Green Tea Seed Serum', skin: 'Peau mixte', price: 29.00, tag: null, color: 'p3' },
  { id: 4, brand: 'Some By Mi', name: 'AHA BHA PHA 30 Days Miracle Toner', skin: 'Peau grasse', price: 22.50, tag: 'Bestseller', color: 'p4' },
]
</script>

<template>
  <section class="section-featured">
    <div class="featured-header">
      <div>
        <p class="section-label">Sélection</p>
        <h2 class="section-title">Produits<br><em>vedettes</em></h2>
      </div>
      <a class="featured-link" @click.prevent="router.push('/catalogue')">
        Voir tout le catalogue →
      </a>
    </div>
    <div class="products-grid">
      <div
          v-for="product in products"
          :key="product.id"
          class="product-card"
          @click="router.push(`/produits/${product.id}`)"
      >
        <div class="product-img" :class="product.color">
          <div class="product-img-inner">
            <div class="product-shape" />
          </div>
          <div v-if="product.tag" class="product-tag">{{ product.tag }}</div>
        </div>
        <div class="product-brand">{{ product.brand }}</div>
        <div class="product-name">{{ product.name }}</div>
        <div class="product-skin">{{ product.skin }}</div>
        <div class="product-footer">
          <div class="product-price">
            {{ product.price.toFixed(2).replace('.', ',') }} €
          </div>
          <button class="product-add" @click.stop>+</button>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.section-featured { padding: var(--spacing-lg); background: var(--ivory-dark); }
.featured-header {
  display: flex; justify-content: space-between; align-items: flex-end;
  margin-bottom: 4rem;
}
.featured-link {
  font-size: 0.75rem; letter-spacing: 0.2em;
  text-transform: uppercase; color: var(--cherry);
  text-decoration: none; cursor: pointer;
  border-bottom: 1px solid var(--cherry); padding-bottom: 2px;
}
.products-grid {
  display: grid; grid-template-columns: repeat(4, 1fr); gap: 2rem;
}
.product-card { cursor: pointer; }
.product-img { aspect-ratio: 3/4; position: relative; overflow: hidden; margin-bottom: 1.5rem; }
.p1 { background: linear-gradient(160deg, #EDD5D8, #D4A8B0); }
.p2 { background: linear-gradient(160deg, #D8EDD5, #A8D4B0); }
.p3 { background: linear-gradient(160deg, #D5D8ED, #A8B0D4); }
.p4 { background: linear-gradient(160deg, #EDE8D5, #D4C8A8); }
.product-img-inner {
  position: absolute; inset: 0;
  display: flex; align-items: center; justify-content: center;
}
.product-shape {
  width: 80px; height: 120px; background: white;
  border-radius: 40px;
  box-shadow: 0 20px 40px rgba(26,22,20,0.15);
  transition: transform 0.5s ease;
}
.product-card:hover .product-shape { transform: translateY(-8px); }
.product-tag {
  position: absolute; top: 1rem; left: 1rem;
  background: var(--cherry); color: white;
  font-size: 0.6rem; letter-spacing: 0.2em;
  text-transform: uppercase; padding: 0.3rem 0.8rem;
}
.product-brand {
  font-size: 0.65rem; letter-spacing: 0.3em;
  text-transform: uppercase; color: var(--cherry); margin-bottom: 0.4rem;
}
.product-name {
  font-family: var(--font-display); font-size: 1.1rem;
  color: var(--ink); margin-bottom: 0.5rem; line-height: 1.3;
}
.product-skin {
  font-size: 0.65rem; letter-spacing: 0.15em;
  text-transform: uppercase; color: var(--sage); margin-bottom: 1rem;
}
.product-footer { display: flex; justify-content: space-between; align-items: center; }
.product-price { font-family: var(--font-display); font-size: 1.2rem; color: var(--ink); }
.product-add {
  width: 32px; height: 32px; border: 1px solid var(--ink);
  background: transparent; display: flex; align-items: center;
  justify-content: center; cursor: pointer; font-size: 1.2rem;
  color: var(--ink); transition: all 0.3s;
}
.product-card:hover .product-add { background: var(--ink); color: var(--ivory); }

@media (max-width: 768px) {
  .section-featured { padding: 4rem 2rem; }
  .products-grid { grid-template-columns: repeat(2, 1fr); gap: 1rem; }
  .featured-header { flex-direction: column; align-items: flex-start; gap: 1rem; }
}
</style>