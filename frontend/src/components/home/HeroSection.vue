<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const cursorX = ref(0)
const cursorY = ref(0)
const ringX = ref(0)
const ringY = ref(0)
let animFrame: number

function animateRing() {
  ringX.value += (cursorX.value - ringX.value) * 0.12
  ringY.value += (cursorY.value - ringY.value) * 0.12
  animFrame = requestAnimationFrame(animateRing)
}

function onMouseMove(e: MouseEvent) {
  cursorX.value = e.clientX
  cursorY.value = e.clientY
}

onMounted(() => {
  window.addEventListener('mousemove', onMouseMove)
  animateRing()
})

onUnmounted(() => {
  window.removeEventListener('mousemove', onMouseMove)
  cancelAnimationFrame(animFrame)
})
</script>

<template>
  <div class="cursor" :style="{ left: cursorX + 'px', top: cursorY + 'px' }" />
  <div class="cursor-ring" :style="{ left: ringX + 'px', top: ringY + 'px' }" />

  <section class="hero">
    <div class="hero-left">
      <p class="hero-tag fade-up delay-1">Nouvelle collection · Printemps 2026</p>
      <h1 class="hero-title fade-up delay-2">
        L'art<br>du soin<br><em>coréen</em>
      </h1>
      <p class="hero-desc fade-up delay-3">
        Des formules à la pointe de la recherche cosmétique coréenne,
        sélectionnées pour leur efficacité et leur sensorialité unique.
      </p>
      <div class="hero-cta fade-up delay-4">
        <button class="btn-primary" @click="router.push('/catalogue')">
          <span>Découvrir</span>
        </button>
        <a class="btn-secondary" @click.prevent="router.push('/catalogue')">
          Notre sélection →
        </a>
      </div>
    </div>

    <div class="hero-right">
      <div class="hero-image-bg" />
      <div class="hero-circle circle-1" />
      <div class="hero-circle circle-2" />
      <div class="hero-circle circle-3" />
      <div class="hero-product-card">
        <div class="hero-product-circle" />
        <div class="hero-product-name">COSRX Snail<br>96 Essence</div>
        <div class="hero-product-price">24,99 €</div>
      </div>
      <div class="hero-number">01</div>
    </div>
  </section>
</template>

<style scoped>
.cursor {
  width: 8px; height: 8px;
  background: var(--cherry); border-radius: 50%;
  position: fixed; pointer-events: none; z-index: 9999;
  transform: translate(-50%, -50%);
  transition: transform 0.15s ease;
}
.cursor-ring {
  width: 32px; height: 32px;
  border: 1px solid var(--cherry); border-radius: 50%;
  position: fixed; pointer-events: none; z-index: 9998;
  transform: translate(-50%, -50%); opacity: 0.5;
}

.hero {
  min-height: 100vh;
  display: grid; grid-template-columns: 1fr 1fr;
  overflow: hidden;
}

.hero-left {
  display: flex; flex-direction: column;
  justify-content: flex-end;
  padding: 8rem 4rem 6rem;
  position: relative; z-index: 2;
}

.hero-tag {
  font-size: 0.7rem; letter-spacing: 0.4em;
  text-transform: uppercase; color: var(--cherry);
  margin-bottom: 2rem;
  display: flex; align-items: center; gap: 1rem;
}
.hero-tag::before {
  content: ''; width: 40px; height: 1px; background: var(--cherry);
}

.hero-title {
  font-family: var(--font-display);
  font-size: clamp(3.5rem, 6vw, 7rem);
  font-weight: 300; line-height: 0.95;
  color: var(--ink); margin-bottom: 3rem;
}
.hero-title em { font-style: italic; color: var(--cherry); }

.hero-desc {
  font-size: 0.9rem; line-height: 1.8;
  color: var(--ink-soft); max-width: 320px; margin-bottom: 3rem;
}

.hero-cta { display: flex; gap: 2rem; align-items: center; }

.btn-primary {
  background: var(--cherry); color: white; border: none;
  padding: 1rem 2.5rem; font-family: var(--font-body);
  font-size: 0.75rem; letter-spacing: 0.2em;
  text-transform: uppercase; cursor: pointer; transition: background 0.3s;
}
.btn-primary:hover { background: var(--ink); }

.btn-secondary {
  font-size: 0.75rem; letter-spacing: 0.2em;
  text-transform: uppercase; color: var(--ink-soft);
  cursor: pointer; text-decoration: none;
  border-bottom: 1px solid transparent; padding-bottom: 2px;
  transition: all 0.3s;
}
.btn-secondary:hover { border-color: var(--ink); color: var(--ink); }

.hero-right { position: relative; overflow: hidden; }

.hero-image-bg {
  position: absolute; inset: 0;
  background: linear-gradient(135deg, #E8D5D8 0%, #D4B8BC 50%, #C4A0A8 100%);
}

.hero-circle { position: absolute; border-radius: 50%; }
.circle-1 {
  width: 500px; height: 500px;
  background: radial-gradient(circle, rgba(196,72,90,0.15) 0%, transparent 70%);
  top: 50%; left: 50%; transform: translate(-50%, -50%);
  animation: pulse 4s ease-in-out infinite;
}
.circle-2 {
  width: 300px; height: 300px;
  border: 1px solid rgba(196,72,90,0.3);
  top: 50%; left: 50%; transform: translate(-50%, -50%);
  animation: spin 20s linear infinite;
}
.circle-3 {
  width: 180px; height: 180px;
  border: 1px solid rgba(196,72,90,0.5);
  top: 50%; left: 50%; transform: translate(-50%, -50%);
  animation: spin 12s linear infinite reverse;
}

.hero-product-card {
  position: absolute; top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  width: 160px; height: 220px; background: white;
  box-shadow: 0 40px 80px rgba(26,22,20,0.2);
  display: flex; flex-direction: column;
  align-items: center; justify-content: center; gap: 1rem;
  animation: float 6s ease-in-out infinite;
}
.hero-product-circle {
  width: 80px; height: 80px; border-radius: 50%;
  background: linear-gradient(135deg, #E8D5D8, #C4A0A8);
}
.hero-product-name {
  font-family: var(--font-display);
  font-size: 0.75rem; letter-spacing: 0.1em;
  text-align: center; color: var(--ink); padding: 0 1rem;
}
.hero-product-price { font-size: 0.7rem; color: var(--cherry); }

.hero-number {
  position: absolute;
  font-family: var(--font-display);
  font-size: 20rem; font-weight: 300;
  color: rgba(196,72,90,0.08);
  bottom: -4rem; right: -2rem; line-height: 1;
  pointer-events: none; user-select: none;
}

@media (max-width: 768px) {
  .hero {
    grid-template-columns: 1fr;
    min-height: auto;
  }
  .hero-left {
    padding: 7rem 2rem 3rem;
    justify-content: flex-start;
  }
  .hero-right {
    height: 50vh;
  }
  .hero-number { font-size: 10rem; }
  .cursor, .cursor-ring { display: none; }
}
</style>