<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<template>
  <div class="app">
    <RouterView />
  </div>
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Cormorant+Garamond:ital,wght@0,300;0,400;0,600;1,300;1,400&family=DM+Sans:wght@300;400;500&display=swap');

:root {
  --ivory: #F7F3EE;
  --ivory-dark: #EDE8E0;
  --cherry: #C4485A;
  --cherry-light: #E8A0AA;
  --ink: #1A1614;
  --ink-soft: #4A3F3A;
  --sage: #8A9E8C;
  --gold: #C9A96E;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  background: var(--ivory);
  color: var(--ink);
  font-family: 'DM Sans', sans-serif;
  font-weight: 300;
}

.app {
  min-height: 100vh;
}
</style>