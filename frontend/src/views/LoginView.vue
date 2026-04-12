<script setup lang="ts">
import AppNav from '@/components/layout/AppNav.vue'
import LoginLeft from '@/components/login/LoginLeft.vue'
import LoginRight from '@/components/login/LoginRight.vue'
</script>

<template>
  <AppNav />
  <div class="login-page">
    <LoginLeft />
    <LoginRight />
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1fr 1fr;
}

@media (max-width: 768px) {
  .login-page { grid-template-columns: 1fr; }
}
</style>