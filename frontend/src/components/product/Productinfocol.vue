<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

interface Props {
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
}

const props = defineProps<Props>()

const emit = defineEmits<{
  addToCart: [id: number, qty: number]
}>()

const router = useRouter()
const qty = ref(1)
const wishlist = ref(false)
const openSection = ref<string | null>('description')

const discount = computed(() => {
  if (!props.oldPrice) return null
  return Math.round((1 - props.oldPrice / props.oldPrice) * 100) // placeholder
})

const savePct = computed(() => {
  if (!props.oldPrice) return null
  return Math.round((1 - props.price / props.oldPrice) * 100)
})

const starsDisplay = computed(() => {
  const full = Math.round(props.rating)
  return '★'.repeat(full) + '☆'.repeat(5 - full)
})

function formatPrice(p: number) {
  return p.toFixed(2).replace('.', ',') + ' €'
}

function decrement() { if (qty.value > 1) qty.value-- }
function increment() { if (qty.value < 10) qty.value++ }
function toggleWishlist() { wishlist.value = !wishlist.value }

function toggleSection(section: string) {
  openSection.value = openSection.value === section ? null : section
}

function onAddToCart() {
  emit('addToCart', props.id, qty.value)
}
</script>

<template>
  <div class="info-col">

    <div class="info-brand">{{ brand }}</div>

    <h1 class="info-name" v-html="name" />
    <p class="info-subtitle">{{ subtitle }}</p>

    <div class="info-rating">
      <span class="stars">{{ starsDisplay }}</span>
      <span class="rating-score">{{ rating.toFixed(1).replace('.', ',') }}</span>
      <span class="rating-count">{{ reviewCount.toLocaleString('fr') }} avis</span>
    </div>

    <div class="info-price">
      <span class="price-current">{{ formatPrice(price) }}</span>
      <span v-if="oldPrice" class="price-old">{{ formatPrice(oldPrice) }}</span>
      <span v-if="savePct" class="price-save">−{{ savePct }}%</span>
    </div>

    <div class="info-skin">
      <span class="skin-label">Type de peau</span>
      <div class="skin-chips">
        <span v-for="skin in skinTypes" :key="skin" class="skin-chip">
          {{ skin }}
        </span>
      </div>
    </div>

    <div class="info-qty">
      <span class="qty-label">Quantité</span>
      <div class="qty-control">
        <button class="qty-btn" @click="decrement">−</button>
        <span class="qty-val">{{ qty }}</span>
        <button class="qty-btn" @click="increment">+</button>
      </div>
    </div>

    <div class="info-cta">
      <button class="btn-cart" @click="onAddToCart">Ajouter au panier</button>
      <button
          class="btn-wishlist"
          :class="{ active: wishlist }"
          @click="toggleWishlist"
      >
        {{ wishlist ? '♥' : '♡' }}
      </button>
    </div>

    <div class="accordion">

      <div class="accordion-item">
        <div class="accordion-header" @click="toggleSection('description')">
          Description
          <span class="accordion-icon" :class="{ open: openSection === 'description' }">+</span>
        </div>
        <div v-show="openSection === 'description'" class="accordion-body">
          {{ description }}
        </div>
      </div>

      <div class="accordion-item">
        <div class="accordion-header" @click="toggleSection('ingredients')">
          Ingrédients
          <span class="accordion-icon" :class="{ open: openSection === 'ingredients' }">+</span>
        </div>
        <div v-show="openSection === 'ingredients'" class="accordion-body">
          {{ ingredients }}
        </div>
      </div>

    </div>

  </div>
</template>

<style scoped>
.info-col {
  padding: 4rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
  border-left: 1px solid rgba(26, 22, 20, 0.08);
  overflow-y: auto;
}

.info-brand {
  font-size: 0.6rem;
  letter-spacing: 0.4em;
  text-transform: uppercase;
  color: var(--cherry);
  margin-bottom: 0.8rem;
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.info-brand::before {
  content: '';
  width: 20px;
  height: 1px;
  background: var(--cherry);
}

.info-name {
  font-family: var(--font-display);
  font-size: clamp(1.8rem, 3vw, 2.8rem);
  font-weight: 300;
  line-height: 1.1;
  color: var(--ink);
  margin-bottom: 0.5rem;
}

.info-subtitle {
  font-size: 0.8rem;
  color: var(--ink-soft);
  margin-bottom: 1.5rem;
}

.info-rating {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid rgba(26, 22, 20, 0.08);
}

.stars {
  color: var(--cherry);
  letter-spacing: -1px;
  font-size: 0.85rem;
}

.rating-score {
  font-family: var(--font-display);
  font-size: 1.1rem;
  color: var(--ink);
}

.rating-count {
  font-size: 0.7rem;
  color: var(--ink-soft);
  letter-spacing: 0.1em;
}

.info-price {
  display: flex;
  align-items: baseline;
  gap: 1rem;
  margin-bottom: 2rem;
}

.price-current {
  font-family: var(--font-display);
  font-size: 2.2rem;
  font-weight: 300;
  color: var(--ink);
}

.price-old {
  font-family: var(--font-display);
  font-size: 1.2rem;
  color: rgba(74, 63, 58, 0.4);
  text-decoration: line-through;
}

.price-save {
  font-size: 0.6rem;
  letter-spacing: 0.15em;
  text-transform: uppercase;
  color: var(--cherry);
  border: 1px solid var(--cherry-light);
  padding: 0.2rem 0.6rem;
}

.info-skin {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.skin-label {
  font-size: 0.6rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--ink-soft);
  white-space: nowrap;
}

.skin-chips {
  display: flex;
  gap: 0.4rem;
  flex-wrap: wrap;
}

.skin-chip {
  font-size: 0.6rem;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  padding: 0.25rem 0.7rem;
  border: 1px solid rgba(138, 158, 140, 0.5);
  color: var(--sage);
}

.info-qty {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.qty-label {
  font-size: 0.6rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--ink-soft);
}

.qty-control {
  display: flex;
  align-items: center;
  border: 1px solid rgba(26, 22, 20, 0.2);
}

.qty-btn {
  width: 36px;
  height: 36px;
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  color: var(--ink);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.qty-btn:hover { background: var(--ivory-dark); }

.qty-val {
  width: 40px;
  text-align: center;
  font-family: var(--font-display);
  font-size: 1rem;
  color: var(--ink);
}

.info-cta {
  display: flex;
  gap: 1rem;
  margin-bottom: 2.5rem;
}

.btn-cart {
  flex: 1;
  background: var(--cherry);
  color: white;
  border: none;
  padding: 1rem 2rem;
  font-family: var(--font-body);
  font-size: 0.7rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-cart:hover { background: var(--ink); }

.btn-wishlist {
  width: 50px;
  height: 50px;
  border: 1px solid rgba(26, 22, 20, 0.2);
  background: transparent;
  font-size: 1.1rem;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--ink);
}

.btn-wishlist:hover,
.btn-wishlist.active {
  border-color: var(--cherry);
  color: var(--cherry);
}

/* ── Accordion ── */
.accordion {
  border-top: 1px solid rgba(26, 22, 20, 0.1);
}

.accordion-item {
  border-bottom: 1px solid rgba(26, 22, 20, 0.1);
}

.accordion-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 0;
  cursor: pointer;
  font-size: 0.65rem;
  letter-spacing: 0.25em;
  text-transform: uppercase;
  color: var(--ink);
  user-select: none;
}

.accordion-icon {
  font-size: 1rem;
  color: var(--cherry);
  transition: transform 0.3s;
}

.accordion-icon.open {
  transform: rotate(45deg);
}

.accordion-body {
  font-size: 0.8rem;
  line-height: 1.8;
  color: var(--ink-soft);
  padding-bottom: 1.2rem;
}

/* ── Responsive ── */
@media (max-width: 768px) {
  .info-col {
    padding: 2rem 1.5rem;
  }
}
</style>