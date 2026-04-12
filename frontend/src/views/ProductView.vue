<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import ProductImageCol from '@/components/product/ProductImageCol.vue'
import ProductInfoCol from '@/components/product/ProductInfoCol.vue'

// ── Types ──────────────────────────────────────────────────────────────────
interface Product {
  id: number
  brand: string
  name: string
  subtitle: string
  price: number
  oldPrice?: number
  rating: number
  reviewCount: number
  skinTypes: string[]
  description: string
  ingredients: string
  color: string
  tag?: string | null
}

// ── Données statiques ──────────────────────────────────────────────────────
// Phase statique — sera remplacé par un fetch vers product-service
const PRODUCTS: Product[] = [
  {
    id: 1,
    brand: 'COSRX',
    name: 'Advanced Snail 96<br><em>Mucin Power Essence</em>',
    subtitle: 'Essence réparatrice haute concentration · 100ml',
    price: 24.99,
    oldPrice: 32.00,
    rating: 4.9,
    reviewCount: 2341,
    skinTypes: ['Tous types', 'Sensible'],
    description: "L'essence au mucus d'escargot à 96% de COSRX est une formule légère et ultra-hydratante qui répare, régénère et lisse la texture de la peau en profondeur. Sa texture gel fondante pénètre immédiatement sans laisser de film. Idéale en 4e étape du rituel K-beauty, après le toner et avant le sérum.",
    ingredients: 'Mucus Filtrate (96%), Betaine, 1,2-Hexanediol, Sodium Hyaluronate, Allantoin, Panthenol, Ethyl Hexanediol, Carbomer, Sodium Hydroxide, Phenoxyethanol.',
    color: 'p1',
    tag: 'Bestseller',
  },
  {
    id: 2,
    brand: 'Laneige',
    name: 'Water Sleeping<br><em>Mask Lavender</em>',
    subtitle: 'Masque nuit repulpant · 70ml',
    price: 35.00,
    oldPrice: 42.00,
    rating: 4.7,
    reviewCount: 918,
    skinTypes: ['Peau sèche'],
    description: "Le masque nuit à la lavande de Laneige forme un film protecteur pendant le sommeil pour une hydratation intense. Au réveil, la peau est repulpée, douce et lumineuse. Contient le complexe exclusif SLEEPSCENT™ pour une relaxation optimale.",
    ingredients: 'Water, Butylene Glycol, Glycerin, Cyclopentasiloxane, Dimethicone, Lavandula Angustifolia (Lavender) Flower/Leaf/Stem Extract, Sodium Hyaluronate, Beta-Glucan.',
    color: 'p2',
    tag: 'Nouveau',
  },
]

const route = useRoute()
const activeThumb = ref(0)

// Récupère le produit par id depuis la route — fallback sur le premier
const product = computed<Product>(() => {
  const id = Number(route.params.id)
  return PRODUCTS.find(p => p.id === id) ?? PRODUCTS[0]
})

function onAddToCart(id: number, qty: number) {
  // TODO — connecter au store panier (Phase 3)
  console.log('Add to cart:', id, 'qty:', qty)
}
</script>

<template>
  <div class="product-root">

    <!-- Breadcrumb -->
    <div class="breadcrumb">
      <RouterLink to="/">Accueil</RouterLink>
      <span class="sep">/</span>
      <RouterLink to="/catalogue">Catalogue</RouterLink>
      <span class="sep">/</span>
      <span class="current">{{ product.brand }}</span>
    </div>

    <!-- Layout principal -->
    <div class="product-layout">
      <ProductImageCol
          :brand="product.brand"
          :name="product.name"
          :color="product.color"
          :tag="product.tag"
          v-model:active-thumb="activeThumb"
      />
      <ProductInfoCol
          v-bind="product"
          @add-to-cart="onAddToCart"
      />
    </div>

  </div>
</template>

<style scoped>
.product-root {
  background: var(--ivory);
  padding-top: 80px; /* compense AppNav fixe */
  min-height: 100vh;
}

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

.product-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  min-height: calc(100vh - 120px);
}

/* ── Responsive ── */
@media (max-width: 1024px) {
  .breadcrumb { padding: 1rem 2rem; }
}

@media (max-width: 768px) {
  .breadcrumb { padding: 1rem 1.5rem; }

  .product-layout {
    grid-template-columns: 1fr;
  }
}
</style>