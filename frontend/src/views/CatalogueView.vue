<script setup lang="ts">
import { ref, computed } from 'vue'
import CatalogueSidebar from '@/components/catalogue/CatalogueSidebar.vue'
import ProductCard from '@/components/catalogue/ProductCard.vue'

// ── Types ──────────────────────────────────────────────────────────────────
interface Product {
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

// ── Données statiques ──────────────────────────────────────────────────────
// Phase statique — sera remplacé par un appel fetch vers product-service
const allProducts: Product[] = [
  { id: 1,  brand: 'COSRX',      name: 'Advanced Snail 96 Mucin Power Essence',   skin: 'Tous types',  price: 24.99,              tag: 'Bestseller', color: 'p1', cat: 'essence'   },
  { id: 2,  brand: 'Laneige',    name: 'Water Sleeping Mask Lavender',             skin: 'Peau sèche',  price: 35.00, oldPrice: 42, tag: 'Nouveau',    color: 'p2', cat: 'masque'    },
  { id: 3,  brand: 'Innisfree',  name: 'Green Tea Seed Serum',                     skin: 'Peau mixte',  price: 29.00,              tag: null,          color: 'p3', cat: 'essence'   },
  { id: 4,  brand: 'Some By Mi', name: 'AHA BHA PHA 30 Days Miracle Toner',        skin: 'Peau grasse', price: 22.50,              tag: 'Bestseller', color: 'p4', cat: 'essence'   },
  { id: 5,  brand: 'Sulwhasoo',  name: 'First Care Activating Serum',              skin: 'Tous types',  price: 64.00,              tag: null,          color: 'p5', cat: 'essence'   },
  { id: 6,  brand: 'COSRX',      name: 'Low pH Good Morning Gel Cleanser',         skin: 'Peau grasse', price: 14.00,              tag: 'Bestseller', color: 'p6', cat: 'nettoyant' },
  { id: 7,  brand: 'Innisfree',  name: 'Firming Sleeping Mask Jeju',               skin: 'Peau mixte',  price: 18.00,              tag: 'Nouveau',    color: 'p1', cat: 'masque'    },
  { id: 8,  brand: 'Missha',     name: 'Time Revolution Night Repair Sérum',       skin: 'Peau sèche',  price: 38.00, oldPrice: 52, tag: null,          color: 'p2', cat: 'essence'   },
  { id: 9,  brand: 'Laneige',    name: 'Water Bank Blue Hyaluronic Cream',         skin: 'Peau sèche',  price: 42.00,              tag: null,          color: 'p3', cat: 'creme'     },
  { id: 10, brand: 'COSRX',      name: 'Ceramide Moisturizing Cream',              skin: 'Peau sèche',  price: 19.00,              tag: null,          color: 'p4', cat: 'creme'     },
  { id: 11, brand: 'Some By Mi', name: 'Clay Mask Pore Minimizer',                 skin: 'Peau grasse', price: 12.00,              tag: null,          color: 'p5', cat: 'masque'    },
  { id: 12, brand: 'Sulwhasoo',  name: 'Concentrated Ginseng Renewing Cream',      skin: 'Tous types',  price: 58.00,              tag: null,          color: 'p6', cat: 'creme'     },
]

// ── State des filtres ──────────────────────────────────────────────────────
const activeCat = ref('all')
const activeSkin = ref('Toutes')
const activeFilters = ref<string[]>(['Innisfree', 'Peau sèche'])
const sortOrder = ref('Nouveautés')

// ── Produits filtrés ───────────────────────────────────────────────────────
const filteredProducts = computed(() => {
  return allProducts.filter(p => {
    const matchCat = activeCat.value === 'all' || p.cat === activeCat.value
    const matchSkin = activeSkin.value === 'Toutes' || p.skin.toLowerCase().includes(activeSkin.value.toLowerCase())
    return matchCat && matchSkin
  })
})

// ── Handlers ───────────────────────────────────────────────────────────────
function onUpdateCat(cat: string) {
  activeCat.value = cat
}

function onUpdateSkin(skin: string) {
  activeSkin.value = skin
}

function removeFilter(filter: string) {
  activeFilters.value = activeFilters.value.filter(f => f !== filter)
}

function clearFilters() {
  activeFilters.value = []
  activeCat.value = 'all'
  activeSkin.value = 'Toutes'
}

function onAddToCart(id: number) {
  // TODO — connecter au store panier (Phase 3)
  console.log('Add to cart:', id)
}
</script>

<template>
  <div class="catalogue-root">

    <!-- Sous-header : breadcrumb + tri (sous AppNav fixe) -->
    <div class="sub-header">
      <div class="breadcrumb">
        <RouterLink to="/">Accueil</RouterLink>
        <span class="sep">/</span>
        <span class="current">Catalogue</span>
      </div>
      <div class="sub-header-right">
        <span class="product-count">
          {{ filteredProducts.length }} produit{{ filteredProducts.length > 1 ? 's' : '' }}
        </span>
        <select v-model="sortOrder" class="sort-select">
          <option>Nouveautés</option>
          <option>Prix croissant</option>
          <option>Prix décroissant</option>
          <option>Mieux notés</option>
        </select>
      </div>
    </div>

    <!-- Body -->
    <div class="catalogue-body">

      <CatalogueSidebar
          :active-cat="activeCat"
          :active-skin="activeSkin"
          @update-cat="onUpdateCat"
          @update-skin="onUpdateSkin"
      />

      <main class="main-content">

        <!-- Header éditorial -->
        <div class="page-header">
          <div class="page-eyebrow">Collection Printemps 2026</div>
          <h1 class="page-title">Beauté <em>coréenne</em></h1>
          <p class="page-desc">
            Des formules à la pointe de la recherche cosmétique coréenne,
            sélectionnées pour leur efficacité et leur sensorialité unique.
          </p>
        </div>

        <!-- Filtres actifs -->
        <div v-if="activeFilters.length" class="active-filters">
          <span
              v-for="filter in activeFilters"
              :key="filter"
              class="filter-tag"
          >
            {{ filter }}
            <span class="filter-tag-x" @click="removeFilter(filter)">×</span>
          </span>
          <span class="clear-all" @click="clearFilters">Effacer tout</span>
        </div>

        <!-- Grille produits -->
        <div class="product-grid">
          <ProductCard
              v-for="product in filteredProducts"
              :key="product.id"
              v-bind="product"
              @add-to-cart="onAddToCart"
          />
        </div>

      </main>
    </div>

  </div>
</template>

<style scoped>
.catalogue-root {
  background: var(--ivory);
  color: var(--ink);
  padding-top: 80px; /* compense AppNav fixe */
}

/* ── Sous-header sticky sous la nav ── */
.sub-header {
  position: sticky;
  top: 80px;
  z-index: 9;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.9rem 4rem;
  border-bottom: 1px solid rgba(26, 22, 20, 0.1);
  background: var(--ivory);
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  font-size: 0.65rem;
  letter-spacing: 0.25em;
  text-transform: uppercase;
  color: var(--ink-soft);
}

.breadcrumb a {
  color: var(--ink-soft);
  text-decoration: none;
  transition: color 0.2s;
}

.breadcrumb a:hover { color: var(--cherry); }
.breadcrumb .sep { opacity: 0.4; }
.breadcrumb .current { color: var(--cherry); }

.sub-header-right {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.product-count {
  font-size: 0.65rem;
  letter-spacing: 0.25em;
  text-transform: uppercase;
  color: var(--ink-soft);
}

.sort-select {
  font-family: var(--font-body);
  font-size: 0.65rem;
  letter-spacing: 0.15em;
  text-transform: uppercase;
  border: 1px solid rgba(26, 22, 20, 0.2);
  background: transparent;
  color: var(--ink);
  padding: 0.4rem 0.8rem;
  outline: none;
  cursor: pointer;
}

/* ── Body layout ── */
.catalogue-body {
  display: grid;
  grid-template-columns: 240px 1fr;
  align-items: start;
}

/* ── Main ── */
.main-content {
  padding: 3rem 4rem;
}

/* ── Header éditorial ── */
.page-header {
  margin-bottom: 3rem;
}

.page-eyebrow {
  font-size: 0.6rem;
  letter-spacing: 0.4em;
  text-transform: uppercase;
  color: var(--cherry);
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  gap: 1rem;
}

.page-eyebrow::before {
  content: '';
  width: 30px;
  height: 1px;
  background: var(--cherry);
}

.page-title {
  font-family: var(--font-display);
  font-size: clamp(2.5rem, 4vw, 3.5rem);
  font-weight: 300;
  line-height: 1.05;
  color: var(--ink);
}

.page-title em {
  font-style: italic;
  color: var(--cherry);
}

.page-desc {
  font-size: 0.8rem;
  color: var(--ink-soft);
  margin-top: 0.8rem;
  line-height: 1.8;
  max-width: 400px;
}

/* ── Filtres actifs ── */
.active-filters {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  margin-bottom: 2.5rem;
  flex-wrap: wrap;
}

.filter-tag {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.6rem;
  letter-spacing: 0.15em;
  text-transform: uppercase;
  padding: 0.3rem 0.8rem;
  border: 1px solid var(--cherry-light);
  color: var(--cherry);
}

.filter-tag-x {
  cursor: pointer;
  opacity: 0.6;
}

.filter-tag-x:hover {
  opacity: 1;
}

.clear-all {
  font-size: 0.6rem;
  letter-spacing: 0.15em;
  text-transform: uppercase;
  color: var(--ink-soft);
  text-decoration: underline;
  cursor: pointer;
  margin-left: 0.5rem;
}

/* ── Grille produits ── */
.product-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 2.5rem 2rem;
}

/* ── Responsive ── */
@media (max-width: 1024px) {
  .catalogue-body {
    grid-template-columns: 200px 1fr;
  }

  .main-content {
    padding: 2rem;
  }

  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .catalogue-root {
    padding-top: 70px;
  }

  .sub-header {
    padding: 0.8rem 1.5rem;
  }

  .catalogue-body {
    grid-template-columns: 1fr;
  }

  .main-content {
    padding: 2rem 1.5rem;
  }

  .product-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 1.5rem 1rem;
  }
}
</style>