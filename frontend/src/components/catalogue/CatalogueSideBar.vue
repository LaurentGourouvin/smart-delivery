<script setup lang="ts">
interface Props {
  activeCat: string
  activeSkin: string
}

defineProps<Props>()

const emit = defineEmits<{
  updateCat: [cat: string]
  updateSkin: [skin: string]
}>()

const categories = [
  { slug: 'all',       label: 'Tous les produits',    count: 12 },
  { slug: 'essence',   label: 'Essences & Sérums',    count: 4  },
  { slug: 'masque',    label: 'Masques',               count: 3  },
  { slug: 'creme',     label: 'Crèmes & Hydratants',  count: 3  },
  { slug: 'nettoyant', label: 'Nettoyants',            count: 2  },
]

const skins = ['Toutes', 'Sèche', 'Grasse', 'Mixte', 'Sensible', 'Normale']

const marques = [
  { label: 'COSRX',      count: 3 },
  { label: 'Innisfree',  count: 2 },
  { label: 'Laneige',    count: 2 },
  { label: 'Some By Mi', count: 2 },
  { label: 'Sulwhasoo',  count: 3 },
]
</script>

<template>
  <aside class="sidebar">

    <div class="sidebar-section">
      <div class="sidebar-title">Catégorie</div>
      <div
          v-for="cat in categories"
          :key="cat.slug"
          class="filter-item"
          :class="{ active: activeCat === cat.slug }"
          @click="emit('updateCat', cat.slug)"
      >
        <div class="filter-radio" />
        {{ cat.label }}
        <span class="filter-count">{{ cat.count }}</span>
      </div>
    </div>

    <div class="sidebar-section">
      <div class="sidebar-title">Type de peau</div>
      <div class="skin-chips">
        <span
            v-for="skin in skins"
            :key="skin"
            class="skin-chip"
            :class="{ active: activeSkin === skin }"
            @click="emit('updateSkin', skin)"
        >
          {{ skin }}
        </span>
      </div>
    </div>

    <div class="sidebar-section">
      <div class="sidebar-title">Prix</div>
      <div class="price-row">
        <span>€8</span>
        <span>€65</span>
      </div>
      <div class="price-track">
        <div class="price-fill" />
        <div class="price-handle" style="left: 10%" />
        <div class="price-handle" style="left: 85%" />
      </div>
    </div>

    <div class="sidebar-section">
      <div class="sidebar-title">Marque</div>
      <div
          v-for="marque in marques"
          :key="marque.label"
          class="filter-item"
      >
        <div class="filter-radio" />
        {{ marque.label }}
        <span class="filter-count">{{ marque.count }}</span>
      </div>
    </div>

  </aside>
</template>

<style scoped>
.sidebar {
  border-right: 1px solid rgba(26, 22, 20, 0.1);
  padding: 3rem 2.5rem;
  background: var(--ivory);
  position: sticky;
  top: calc(80px + 45px); /* AppNav + sub-header */
  max-height: calc(100vh - 80px - 45px);
  overflow-y: auto;
}

.sidebar-section {
  margin-bottom: 3rem;
}

.sidebar-title {
  font-size: 0.6rem;
  letter-spacing: 0.4em;
  text-transform: uppercase;
  color: var(--cherry);
  margin-bottom: 1.2rem;
  padding-bottom: 0.8rem;
  border-bottom: 1px solid rgba(196, 72, 90, 0.2);
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.sidebar-title::before {
  content: '';
  width: 20px;
  height: 1px;
  background: var(--cherry);
  flex-shrink: 0;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  padding: 0.5rem 0;
  font-size: 0.8rem;
  color: var(--ink-soft);
  cursor: pointer;
  transition: color 0.2s;
  user-select: none;
}

.filter-item:hover {
  color: var(--ink);
}

.filter-item.active {
  color: var(--ink);
  font-weight: 400;
}

.filter-radio {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 1px solid rgba(26, 22, 20, 0.3);
  flex-shrink: 0;
  transition: border-color 0.2s, background 0.2s;
  position: relative;
}

.filter-item.active .filter-radio {
  background: var(--cherry);
  border-color: var(--cherry);
}

.filter-item.active .filter-radio::after {
  content: '';
  position: absolute;
  inset: 3px;
  border-radius: 50%;
  background: var(--ivory);
}

.filter-count {
  margin-left: auto;
  font-size: 0.65rem;
  color: rgba(74, 63, 58, 0.5);
}

.skin-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
}

.skin-chip {
  font-size: 0.6rem;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  padding: 0.3rem 0.8rem;
  border: 1px solid rgba(26, 22, 20, 0.2);
  color: var(--ink-soft);
  cursor: pointer;
  transition: all 0.2s;
}

.skin-chip:hover {
  border-color: var(--cherry);
  color: var(--cherry);
}

.skin-chip.active {
  background: var(--cherry);
  border-color: var(--cherry);
  color: white;
}

.price-row {
  display: flex;
  justify-content: space-between;
  font-size: 0.75rem;
  color: var(--ink-soft);
  margin-bottom: 0.5rem;
}

.price-track {
  height: 1px;
  background: rgba(26, 22, 20, 0.15);
  position: relative;
  margin: 0.8rem 0;
}

.price-fill {
  position: absolute;
  left: 10%;
  right: 15%;
  height: 100%;
  background: var(--cherry);
}

.price-handle {
  position: absolute;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--cherry);
  top: 50%;
  transform: translateY(-50%);
}
</style>