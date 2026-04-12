<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const categories = [
  { name: 'Sérum', slug: 'serum', count: 24, color: 'cat-1' },
  { name: 'Masque', slug: 'masque', count: 18, color: 'cat-2' },
  { name: 'Toner', slug: 'toner', count: 16, color: 'cat-3' },
  { name: 'Crème', slug: 'creme', count: 21, color: 'cat-4' },
]
</script>

<template>
  <section class="section-categories">
    <div class="categories-left">
      <p class="section-label">Collections</p>
      <h2 class="section-title">Nos<br><em>catégories</em></h2>
    </div>
    <div class="categories-grid">
      <div
          v-for="(cat, i) in categories"
          :key="cat.slug"
          class="category-card"
          :class="[cat.color, { 'offset-top': i === 1, 'offset-bottom': i === 3 }]"
          @click="router.push('/catalogue')"
      >
        <div class="category-bg">
          <div class="category-inner">
            <div class="category-circle-deco" />
            <div class="category-arrow">→</div>
            <div class="category-name">{{ cat.name }}</div>
            <div class="category-count">{{ cat.count }} produits</div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.section-categories {
  padding: var(--spacing-xl) var(--spacing-lg);
  display: grid; grid-template-columns: 1fr 2fr;
  gap: 6rem; align-items: start;
}
.categories-grid {
  display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem;
}
.category-card { cursor: pointer; }
.offset-top { margin-top: 3rem; }
.offset-bottom { margin-top: -3rem; }
.category-bg { aspect-ratio: 3/4; position: relative; overflow: hidden; }
.cat-1 .category-bg { background: linear-gradient(160deg, #F0E6E8, #DCC4C8); }
.cat-2 .category-bg { background: linear-gradient(160deg, #E8EDE6, #C8D4C4); }
.cat-3 .category-bg { background: linear-gradient(160deg, #EDE8E2, #D4C8B8); }
.cat-4 .category-bg { background: linear-gradient(160deg, #E8E6F0, #C8C4D4); }
.category-inner {
  position: absolute; inset: 0;
  display: flex; flex-direction: column;
  justify-content: flex-end; padding: 2rem;
}
.category-circle-deco {
  position: absolute; top: 50%; left: 50%;
  transform: translate(-50%, -60%);
  width: 100px; height: 100px; border-radius: 50%;
  background: radial-gradient(circle, rgba(196,72,90,0.3), transparent);
  opacity: 0.6; transition: transform 0.5s ease;
}
.category-card:hover .category-circle-deco {
  transform: translate(-50%, -60%) scale(2);
}
.category-arrow {
  position: absolute; top: 1.5rem; right: 1.5rem;
  width: 36px; height: 36px;
  border: 1px solid var(--ink); border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 0.9rem; transition: all 0.3s; color: var(--ink);
}
.category-card:hover .category-arrow { background: var(--ink); color: var(--ivory); }
.category-name {
  font-family: var(--font-display);
  font-size: 1.8rem; font-weight: 300;
  color: var(--ink); margin-bottom: 0.5rem;
}
.category-count {
  font-size: 0.7rem; letter-spacing: 0.2em;
  text-transform: uppercase; color: var(--ink-soft);
}

@media (max-width: 768px) {
  .section-categories {
    grid-template-columns: 1fr;
    padding: 4rem 2rem;
    gap: 3rem;
  }
  .offset-top, .offset-bottom { margin-top: 0; }
}
</style>