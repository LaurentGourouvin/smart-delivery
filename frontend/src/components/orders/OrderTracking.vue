<script setup lang="ts">
interface Props {
  steps: string[]
  current: number
  cancelled: boolean
}

defineProps<Props>()
</script>

<template>
  <div v-if="!cancelled" class="order-tracking">
    <div class="tracking-label">Suivi de livraison</div>
    <div class="tracking-steps">
      <div
          v-for="(step, i) in steps"
          :key="step"
          class="tracking-step"
          :class="{
          done:    i < current,
          current: i === current,
        }"
      >
        <div class="step-dot" />
        <div class="step-label">{{ step }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.order-tracking {
  background: var(--ivory-dark);
  padding: 1.2rem 1.5rem;
  margin-bottom: 1.5rem;
}

.tracking-label {
  font-size: 0.55rem;
  letter-spacing: 0.25em;
  text-transform: uppercase;
  color: var(--ink-soft);
  margin-bottom: 1rem;
}

.tracking-steps {
  display: flex;
  align-items: center;
}

.tracking-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.4rem;
  flex: 1;
  position: relative;
}

.tracking-step:not(:last-child)::after {
  content: '';
  position: absolute;
  top: 8px;
  left: 50%;
  right: -50%;
  height: 1px;
  background: rgba(26, 22, 20, 0.15);
  z-index: 0;
}

.tracking-step.done:not(:last-child)::after {
  background: var(--sage);
}

.step-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 1px solid rgba(26, 22, 20, 0.2);
  background: var(--ivory);
  position: relative;
  z-index: 1;
  transition: all 0.3s;
}

.tracking-step.done .step-dot    { background: var(--sage);   border-color: var(--sage); }
.tracking-step.current .step-dot { background: var(--cherry); border-color: var(--cherry); }

.step-label {
  font-size: 0.55rem;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: var(--ink-soft);
  text-align: center;
}

.tracking-step.done .step-label,
.tracking-step.current .step-label { color: var(--ink); }
</style>