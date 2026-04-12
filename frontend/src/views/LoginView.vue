<script setup lang="ts">
import LoginLeft from '@/components/login/LoginLeft.vue'
import LoginRight from '@/components/login/LoginRight.vue'
</script>

<template>
  <div class="login-page">
    <LoginLeft />
    <LoginRight />
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  padding-top: 80px; /* compense AppNav fixe global */
  display: grid;
  grid-template-columns: 1fr 1fr;
}

@media (max-width: 768px) {
  .login-page { grid-template-columns: 1fr; }
}
</style>