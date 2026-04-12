<script setup lang="ts">
interface Props {
  brand: string
  name: string
  color: string
  tag?: string | null
}

defineProps<Props>()

const thumbs = [
  { shape: 'pill' },
  { shape: 'square' },
  { shape: 'circle' },
]

const activeThumb = defineModel<number>('activeThumb', { default: 0 })
</script>

<template>
  <div class="image-col" :class="color">
    <div class="deco-circle c1" />
    <div class="deco-circle c2" />

    <div v-if="tag" class="image-badge">{{ tag }}</div>

    <div class="image-inner">
      <div class="product-shape" />
    </div>

    <div class="image-thumbs">
      <div
          v-for="(thumb, i) in thumbs"
          :key="i"
          class="thumb"
          :class="{ active: activeThumb === i }"
          @click="activeThumb = i"
      >
        <div class="thumb-shape" :class="`thumb-shape--${thumb.shape}`" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.image-col {
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 600px;
}

.p1 { background: linear-gradient(160deg, #EDD5D8, #D4A8B0); }
.p2 { background: linear-gradient(160deg, #D8EDD5, #A8D4B0); }
.p3 { background: linear-gradient(160deg, #D5D8ED, #A8B0D4); }
.p4 { background: linear-gradient(160deg, #EDE8D5, #D4C8A8); }
.p5 { background: linear-gradient(160deg, #F0E6E8, #DCC4C8); }
.p6 { background: linear-gradient(160deg, #E6EDF0, #C4D0DC); }

.deco-circle {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(196, 72, 90, 0.25);
  top: 50%;
  left: 50%;
}

.c1 {
  width: 400px;
  height: 400px;
  transform: translate(-50%, -50%);
  animation: spin 30s linear infinite;
}

.c2 {
  width: 260px;
  height: 260px;
  transform: translate(-50%, -50%);
  animation: spin 20s linear infinite reverse;
}

@keyframes spin {
  from { transform: translate(-50%, -50%) rotate(0deg); }
  to   { transform: translate(-50%, -50%) rotate(360deg); }
}

.image-badge {
  position: absolute;
  top: 2rem;
  left: 2rem;
  background: var(--cherry);
  color: white;
  font-size: 0.55rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  padding: 0.3rem 0.8rem;
  z-index: 3;
}

.image-inner {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-shape {
  width: 120px;
  height: 180px;
  background: white;
  border-radius: 60px;
  box-shadow: 0 40px 80px rgba(26, 22, 20, 0.2);
}

.image-thumbs {
  position: absolute;
  bottom: 2rem;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 0.8rem;
  z-index: 3;
}

.thumb {
  width: 48px;
  height: 48px;
  border: 1.5px solid rgba(26, 22, 20, 0.2);
  cursor: pointer;
  transition: border-color 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(247, 243, 238, 0.7);
}

.thumb.active {
  border-color: var(--cherry);
}

.thumb-shape {
  width: 18px;
  height: 28px;
  background: white;
  box-shadow: 0 4px 12px rgba(26, 22, 20, 0.15);
}

.thumb-shape--pill  { border-radius: 9px; }
.thumb-shape--square { border-radius: 2px; width: 22px; height: 22px; }
.thumb-shape--circle { border-radius: 50%; width: 22px; height: 22px; }
</style>