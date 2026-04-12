<script setup lang="ts">
interface Address {
  id: number
  label: string
  fullName: string
  line1: string
  city: string
  country: string
  isDefault: boolean
}

defineProps<{ addresses: Address[] }>()

const emit = defineEmits<{
  setDefault: [id: number]
  edit: [id: number]
  remove: [id: number]
  add: []
}>()
</script>

<template>
  <div>
    <div class="content-eyebrow">Livraison</div>
    <h2 class="content-title">Mes <em>adresses</em></h2>

    <div class="addresses-grid">
      <div
          v-for="address in addresses"
          :key="address.id"
          class="address-card"
          :class="{ 'address-card--default': address.isDefault }"
      >
        <div v-if="address.isDefault" class="default-badge">Par défaut</div>

        <div class="address-label">{{ address.label }}</div>
        <address class="address-text">
          {{ address.fullName }}<br>
          {{ address.line1 }}<br>
          {{ address.city }}<br>
          {{ address.country }}
        </address>

        <div class="address-actions">
          <button class="btn-address" @click="emit('edit', address.id)">
            Modifier
          </button>
          <button
              v-if="!address.isDefault"
              class="btn-address"
              @click="emit('setDefault', address.id)"
          >
            Définir par défaut
          </button>
          <button
              v-if="!address.isDefault"
              class="btn-address btn-address--delete"
              @click="emit('remove', address.id)"
          >
            Supprimer
          </button>
        </div>
      </div>

      <button class="btn-add" @click="emit('add')">
        + Ajouter une adresse
      </button>
    </div>
  </div>
</template>

<style scoped>
.content-eyebrow {
  font-size: 0.6rem;
  letter-spacing: 0.4em;
  text-transform: uppercase;
  color: var(--cherry);
  margin-bottom: 0.8rem;
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.content-eyebrow::before {
  content: '';
  width: 20px;
  height: 1px;
  background: var(--cherry);
}

.content-title {
  font-family: var(--font-display);
  font-size: clamp(1.8rem, 2.5vw, 2.5rem);
  font-weight: 300;
  line-height: 1.1;
  color: var(--ink);
  margin-bottom: 2.5rem;
}

.content-title em { font-style: italic; color: var(--cherry); }

.addresses-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
}

.address-card {
  border: 1px solid rgba(26, 22, 20, 0.1);
  padding: 1.5rem;
  position: relative;
  transition: border-color 0.2s;
}

.address-card:hover { border-color: rgba(196, 72, 90, 0.3); }
.address-card--default { border-color: var(--cherry); }

.default-badge {
  position: absolute;
  top: 1rem;
  right: 1rem;
  font-size: 0.5rem;
  letter-spacing: 0.15em;
  text-transform: uppercase;
  background: var(--cherry);
  color: white;
  padding: 0.2rem 0.5rem;
}

.address-label {
  font-family: var(--font-display);
  font-size: 1rem;
  color: var(--ink);
  margin-bottom: 0.5rem;
}

.address-text {
  font-size: 0.78rem;
  color: var(--ink-soft);
  line-height: 1.7;
  font-style: normal;
}

.address-actions {
  display: flex;
  gap: 0.8rem;
  margin-top: 1rem;
  flex-wrap: wrap;
}

.btn-address {
  font-size: 0.6rem;
  letter-spacing: 0.15em;
  text-transform: uppercase;
  color: var(--ink-soft);
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 0;
  transition: color 0.2s;
}

.btn-address:hover { color: var(--cherry); }
.btn-address--delete:hover { color: var(--cherry); }

.btn-add {
  border: 1px dashed rgba(26, 22, 20, 0.2);
  padding: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  color: var(--ink-soft);
  font-family: var(--font-body);
  font-size: 0.65rem;
  letter-spacing: 0.15em;
  text-transform: uppercase;
  background: transparent;
}

.btn-add:hover { border-color: var(--cherry); color: var(--cherry); }

@media (max-width: 768px) {
  .addresses-grid { grid-template-columns: 1fr; }
}
</style>