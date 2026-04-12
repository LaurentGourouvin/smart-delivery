<template>
  <div class="brand-wrap">
    <div class="brand-track">
      <template v-for="_ in 2" :key="_">
        <span class="brand-item">COSRX</span>
        <span class="brand-item brand-dot">✦</span>
        <span class="brand-item">Laneige</span>
        <span class="brand-item brand-dot">✦</span>
        <span class="brand-item">Innisfree</span>
        <span class="brand-item brand-dot">✦</span>
        <span class="brand-item">Some By Mi</span>
        <span class="brand-item brand-dot">✦</span>
        <span class="brand-item">Sulwhasoo</span>
        <span class="brand-item brand-dot">✦</span>
        <span class="brand-item">Missha</span>
        <span class="brand-item brand-dot">✦</span>
        <span class="brand-item">Etude House</span>
        <span class="brand-item brand-dot">✦</span>
        <span class="brand-item">Klairs</span>
        <span class="brand-item brand-dot">✦</span>
      </template>
    </div>
  </div>
</template>

<style scoped>
.brand-wrap {
  background: var(--ink);
  padding: 1rem 0;
  overflow: hidden;
  white-space: nowrap;
}
.brand-track {
  display: inline-flex;
  animation: marquee 25s linear infinite;
}
.brand-item {
  font-family: var(--font-display);
  font-size: 1.2rem; font-style: italic;
  color: var(--ivory); letter-spacing: 0.3em;
  padding: 0 3rem; opacity: 0.7;
  text-transform: uppercase;
}
.brand-dot {
  color: var(--cherry);
  opacity: 1 !important;
  font-style: normal;
}
</style>