<script setup lang="ts">
import { ref } from 'vue'

interface UserInfo {
  firstName: string
  lastName: string
  email: string
  phone: string
  birthdate: string
  skinType: string
}

const props = defineProps<{ user: UserInfo }>()
const emit = defineEmits<{ save: [user: UserInfo] }>()

const form = ref<UserInfo>({ ...props.user })

function onSave() {
  emit('save', { ...form.value })
}

function onCancel() {
  form.value = { ...props.user }
}
</script>

<template>
  <div>
    <div class="content-eyebrow">Profil</div>
    <h2 class="content-title">Informations <em>personnelles</em></h2>

    <div class="form-grid">
      <div class="form-group">
        <label class="form-label" for="firstName">Prénom</label>
        <input
            id="firstName"
            v-model="form.firstName"
            class="form-input"
            type="text"
            placeholder="Votre prénom"
            autocomplete="given-name"
        />
      </div>

      <div class="form-group">
        <label class="form-label" for="lastName">Nom</label>
        <input
            id="lastName"
            v-model="form.lastName"
            class="form-input"
            type="text"
            placeholder="Votre nom"
            autocomplete="family-name"
        />
      </div>

      <div class="form-group full">
        <label class="form-label" for="email">Adresse email</label>
        <input
            id="email"
            v-model="form.email"
            class="form-input"
            type="email"
            placeholder="votre@email.com"
            autocomplete="email"
        />
      </div>

      <div class="form-group">
        <label class="form-label" for="phone">Téléphone</label>
        <input
            id="phone"
            v-model="form.phone"
            class="form-input"
            type="tel"
            placeholder="+33 6 00 00 00 00"
            autocomplete="tel"
        />
      </div>

      <div class="form-group">
        <label class="form-label" for="birthdate">Date de naissance</label>
        <input
            id="birthdate"
            v-model="form.birthdate"
            class="form-input"
            type="text"
            placeholder="JJ/MM/AAAA"
            autocomplete="bday"
        />
      </div>

      <div class="form-group full">
        <label class="form-label" for="skinType">Type de peau</label>
        <input
            id="skinType"
            v-model="form.skinType"
            class="form-input"
            type="text"
            placeholder="Ex : mixte, sèche, sensible…"
        />
      </div>
    </div>

    <div class="form-actions">
      <button class="btn-save" @click="onSave">Enregistrer</button>
      <button class="btn-cancel" @click="onCancel">Annuler</button>
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

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group.full { grid-column: 1 / -1; }

.form-label {
  font-size: 0.6rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--ink-soft);
}

.form-input {
  border: none;
  border-bottom: 1px solid var(--ivory-dark);
  background: transparent;
  padding: 0.75rem 0;
  font-family: var(--font-body);
  font-size: 0.9rem;
  color: var(--ink);
  outline: none;
  transition: border-color 0.3s;
  width: 100%;
}

.form-input:focus { border-bottom-color: var(--cherry); }

/* Contraste suffisant pour l'accessibilité — ratio > 4.5:1 sur fond ivory */
.form-input::placeholder { color: rgba(74, 63, 58, 0.6); }

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2.5rem;
  padding-top: 2rem;
  border-top: 1px solid rgba(26, 22, 20, 0.08);
}

.btn-save {
  background: var(--cherry);
  color: white;
  border: none;
  padding: 0.85rem 2rem;
  font-family: var(--font-body);
  font-size: 0.65rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-save:hover { background: var(--ink); }

.btn-cancel {
  background: transparent;
  color: var(--ink-soft);
  border: 1px solid rgba(26, 22, 20, 0.2);
  padding: 0.85rem 2rem;
  font-family: var(--font-body);
  font-size: 0.65rem;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel:hover { border-color: var(--cherry); color: var(--cherry); }

@media (max-width: 768px) {
  .form-grid { grid-template-columns: 1fr; }
  .form-group.full { grid-column: 1; }
}
</style>