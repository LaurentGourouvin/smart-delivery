<script setup lang="ts">
import { RouterView } from 'vue-router'
import AppNav from '@/components/layout/AppNav.vue'
</script>

<template>
  <div class="app">
    <AppNav />
    <RouterView />
  </div>
</template>

<style>
.app {
  min-height: 100vh;
}
</style>