<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import ProfileInfos from '@/components/profile/ProfileInfos.vue'
import ProfileAddresses from '@/components/profile/ProfileAddresses.vue'
import ProfileSecurity from '@/components/profile/ProfileSecurity.vue'

// ── Types ──────────────────────────────────────────────────────────────────
type Panel = 'infos' | 'addresses' | 'security'

// ── State ──────────────────────────────────────────────────────────────────
const router = useRouter()
const authStore = useAuthStore()
const activePanel = ref<Panel>('infos')

// ── Données statiques ──────────────────────────────────────────────────────
// Phase statique — sera remplacé par un fetch vers user-service
const user = ref({
  firstName: 'Laurent',
  lastName:  'Gourouvin',
  email:     'laurent@smartdelivery.com',
  phone:     '',
  birthdate: '',
  skinType:  '',
})

const addresses = ref([
  {
    id: 1,
    label: 'Domicile',
    fullName: 'Laurent Gourouvin',
    line1: '12 rue des Chartrons, 33000 Bordeaux',
    city: 'Bordeaux',
    country: 'France',
    isDefault: true,
  },
  {
    id: 2,
    label: 'Bureau',
    fullName: 'Laurent Gourouvin',
    line1: '45 avenue de la Tech, 33700 Mérignac',
    city: 'Mérignac',
    country: 'France',
    isDefault: false,
  },
])

const NAV_ITEMS: { key: Panel; label: string; icon: string }[] = [
  { key: 'infos',     label: 'Informations personnelles', icon: '◈' },
  { key: 'addresses', label: 'Mes adresses',              icon: '◎' },
  { key: 'security',  label: 'Mot de passe & sécurité',  icon: '◉' },
]

// ── Handlers ───────────────────────────────────────────────────────────────
function onSaveInfos(updated: typeof user.value) {
  user.value = updated
  // TODO — PATCH /api/users/me (Phase 3)
  console.log('Save user info:', updated)
}

function onSetDefault(id: number) {
  addresses.value = addresses.value.map(a => ({ ...a, isDefault: a.id === id }))
}

function onRemoveAddress(id: number) {
  addresses.value = addresses.value.filter(a => a.id !== id)
}

function onAddAddress() {
  // TODO — ouvrir modal d'ajout d'adresse (Phase 3)
  console.log('Add address')
}

function onEditAddress(id: number) {
  // TODO — ouvrir modal d'édition (Phase 3)
  console.log('Edit address:', id)
}

function onChangePassword() {
  // TODO — redirect Keycloak account (Phase 3)
  console.log('Change password')
}

function onManageKeycloak() {
  console.log('Manage Keycloak')
}

function onDeleteAccount() {
  // TODO — confirmation + appel user-service (Phase 3)
  console.log('Delete account')
}

function onLogout() {
  authStore.logout()
  router.push('/')
}
</script>

<template>
  <div class="profile-root">

    <!-- Breadcrumb -->
    <div class="breadcrumb">
      <RouterLink to="/">Accueil</RouterLink>
      <span class="sep">/</span>
      <span class="current">Mon compte</span>
    </div>

    <div class="profile-body">

      <!-- Sidebar navigation -->
      <nav class="profile-nav" aria-label="Navigation du compte">

        <div class="profile-avatar">
          <div class="avatar-circle" aria-hidden="true">
            {{ user.firstName.charAt(0) }}
          </div>
          <div class="avatar-name">{{ user.firstName }} {{ user.lastName.charAt(0) }}.</div>
          <div class="avatar-email">{{ user.email }}</div>
        </div>

        <div class="nav-section-label">Mon compte</div>

        <button
            v-for="item in NAV_ITEMS"
            :key="item.key"
            class="nav-item"
            :class="{ active: activePanel === item.key }"
            @click="activePanel = item.key"
        >
          <span class="nav-icon" aria-hidden="true">{{ item.icon }}</span>
          {{ item.label }}
        </button>

        <div class="nav-spacer" />

        <button class="nav-item nav-item--logout" @click="onLogout">
          <span class="nav-icon" aria-hidden="true">→</span>
          Se déconnecter
        </button>

      </nav>

      <!-- Contenu -->
      <div class="profile-content">

        <ProfileInfos
            v-if="activePanel === 'infos'"
            :user="user"
            @save="onSaveInfos"
        />

        <ProfileAddresses
            v-if="activePanel === 'addresses'"
            :addresses="addresses"
            @set-default="onSetDefault"
            @edit="onEditAddress"
            @remove="onRemoveAddress"
            @add="onAddAddress"
        />

        <ProfileSecurity
            v-if="activePanel === 'security'"
            @change-password="onChangePassword"
            @manage-keycloak="onManageKeycloak"
            @delete-account="onDeleteAccount"
        />

      </div>
    </div>

  </div>
</template>

<style scoped>
.profile-root {
  background: var(--ivory);
  padding-top: 80px; /* compense AppNav fixe */
  min-height: 100vh;
}

/* ── Breadcrumb ── */
.breadcrumb {
  padding: 1rem 4rem;
  font-size: 0.6rem;
  letter-spacing: 0.25em;
  text-transform: uppercase;
  color: var(--ink-soft);
  border-bottom: 1px solid rgba(26, 22, 20, 0.1);
  display: flex;
  align-items: center;
  gap: 0.6rem;
}

.breadcrumb a {
  color: var(--ink-soft);
  text-decoration: none;
  transition: color 0.2s;
}

.breadcrumb a:hover { color: var(--cherry); }
.breadcrumb .sep { opacity: 0.4; }
.breadcrumb .current { color: var(--cherry); }

/* ── Layout ── */
.profile-body {
  display: grid;
  grid-template-columns: 280px 1fr;
  min-height: calc(100vh - 120px);
  align-items: start;
}

/* ── Sidebar ── */
.profile-nav {
  border-right: 1px solid rgba(26, 22, 20, 0.08);
  padding: 3rem 0;
  position: sticky;
  top: 80px;
  max-height: calc(100vh - 80px);
  overflow-y: auto;
}

.profile-avatar {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 2rem 2.5rem;
  border-bottom: 1px solid rgba(26, 22, 20, 0.08);
  margin-bottom: 2rem;
  text-align: center;
}

.avatar-circle {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: linear-gradient(160deg, #EDD5D8, #D4A8B0);
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-display);
  font-size: 1.8rem;
  font-weight: 300;
  color: var(--ink);
  margin-bottom: 1rem;
}

.avatar-name {
  font-family: var(--font-display);
  font-size: 1.1rem;
  color: var(--ink);
  margin-bottom: 0.2rem;
}

.avatar-email {
  font-size: 0.7rem;
  color: var(--ink-soft);
}

.nav-section-label {
  font-size: 0.55rem;
  letter-spacing: 0.35em;
  text-transform: uppercase;
  color: var(--ink-soft);
  padding: 0 2rem;
  margin-bottom: 0.5rem;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  width: 100%;
  padding: 0.7rem 2rem;
  font-family: var(--font-body);
  font-size: 0.78rem;
  font-weight: 300;
  color: var(--ink-soft);
  background: transparent;
  border: none;
  border-left: 2px solid transparent;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
  margin-bottom: 0.1rem;
}

.nav-item:hover {
  color: var(--ink);
  background: rgba(26, 22, 20, 0.03);
}

.nav-item.active {
  color: var(--cherry);
  border-left-color: var(--cherry);
  background: rgba(196, 72, 90, 0.04);
}

.nav-item--logout { color: var(--cherry); margin-top: 0.5rem; }
.nav-item--logout:hover { background: rgba(196, 72, 90, 0.06); }

.nav-icon {
  font-size: 0.85rem;
  width: 16px;
  text-align: center;
  flex-shrink: 0;
}

.nav-spacer { height: 1.5rem; }

/* ── Contenu ── */
.profile-content { padding: 3rem 4rem; }

/* ── Responsive ── */
@media (max-width: 1024px) {
  .profile-body { grid-template-columns: 240px 1fr; }
  .profile-content { padding: 2rem; }
}

@media (max-width: 768px) {
  .profile-body { grid-template-columns: 1fr; }

  .profile-nav {
    position: static;
    max-height: none;
    border-right: none;
    border-bottom: 1px solid rgba(26, 22, 20, 0.08);
    padding: 1.5rem 0;
  }

  .breadcrumb { padding: 1rem 1.5rem; }
  .profile-content { padding: 2rem 1.5rem; }
}
</style>