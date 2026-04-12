<script setup lang="ts">
const emit = defineEmits<{
  changePassword: []
  manageKeycloak: []
  deleteAccount: []
}>()
</script>

<template>
  <div>
    <div class="content-eyebrow">Sécurité</div>
    <h2 class="content-title">Mot de passe <em>&amp; sécurité</em></h2>

    <div class="security-items">

      <div class="security-item">
        <div class="security-info">
          <div class="security-label">Mot de passe</div>
          <div class="security-desc">Dernière modification il y a 3 mois</div>
        </div>
        <button class="btn-action" @click="emit('changePassword')">
          Modifier
        </button>
      </div>

      <div class="security-item">
        <div class="security-info">
          <div class="security-label">Authentification Keycloak</div>
          <div class="security-desc">Compte lié — laurent@smartdelivery.com</div>
        </div>
        <button class="btn-action" @click="emit('manageKeycloak')">
          Gérer
        </button>
      </div>

      <div class="security-item security-item--danger">
        <div class="security-info">
          <div class="security-label">Supprimer le compte</div>
          <div class="security-desc">Cette action est irréversible et supprimera toutes vos données</div>
        </div>
        <button class="btn-action btn-action--danger" @click="emit('deleteAccount')">
          Supprimer
        </button>
      </div>

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

.security-items {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.security-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.5rem;
  border: 1px solid rgba(26, 22, 20, 0.08);
  gap: 2rem;
}

.security-item--danger {
  border-color: rgba(196, 72, 90, 0.15);
  background: rgba(196, 72, 90, 0.02);
}

.security-label {
  font-size: 0.65rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--ink);
  margin-bottom: 0.3rem;
}

.security-desc {
  font-size: 0.75rem;
  color: var(--ink-soft);
  line-height: 1.5;
}

.btn-action {
  background: transparent;
  color: var(--ink-soft);
  border: 1px solid rgba(26, 22, 20, 0.2);
  padding: 0.6rem 1.2rem;
  font-family: var(--font-body);
  font-size: 0.6rem;
  letter-spacing: 0.15em;
  text-transform: uppercase;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
  flex-shrink: 0;
}

.btn-action:hover {
  border-color: var(--cherry);
  color: var(--cherry);
}

.btn-action--danger:hover {
  border-color: var(--cherry);
  color: var(--cherry);
  background: rgba(196, 72, 90, 0.06);
}

@media (max-width: 768px) {
  .security-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
}
</style>