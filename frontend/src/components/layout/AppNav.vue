<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const menuOpen = ref(false)

function toggleMenu() {
  menuOpen.value = !menuOpen.value
}
</script>

<template>
  <nav class="nav">
    <div class="nav-logo" @click="router.push('/')">
      Smart<span>Delivery</span>
    </div>

    <!-- Desktop links -->
    <ul class="nav-links">
      <li><a @click.prevent="router.push('/catalogue')">Catalogue</a></li>
      <li><a href="#">Rituels</a></li>
      <li><a href="#">Marques</a></li>
      <li><a href="#">À propos</a></li>
    </ul>

    <!-- Desktop actions -->
    <div class="nav-actions">
      <div class="nav-cart" @click="router.push('/panier')">Panier (0)</div>
      <div v-if="!authStore.isAuthenticated" class="nav-login" @click="router.push('/login')">
        Connexion
      </div>
      <div v-else class="nav-login" @click="authStore.logout()">
        Déconnexion
      </div>
    </div>

    <!-- Mobile hamburger -->
    <button class="hamburger" @click="toggleMenu" :class="{ open: menuOpen }">
      <span /><span /><span />
    </button>

    <!-- Mobile menu -->
    <div class="mobile-menu" :class="{ open: menuOpen }">
      <ul>
        <li @click="router.push('/catalogue'); menuOpen = false">Catalogue</li>
        <li @click="router.push('/panier'); menuOpen = false">Panier</li>
        <li v-if="!authStore.isAuthenticated" @click="router.push('/login'); menuOpen = false">
          Connexion
        </li>
        <li v-else @click="authStore.logout(); menuOpen = false">Déconnexion</li>
      </ul>
    </div>
  </nav>
</template>

<style scoped>
.nav {
  position: fixed; top: 0; left: 0; right: 0;
  z-index: 100;
  display: flex; align-items: center; justify-content: space-between;
  padding: 2rem 4rem;
  background: transparent;
}

.nav-logo {
  font-family: var(--font-display);
  font-size: 1.5rem; font-weight: 300;
  letter-spacing: 0.3em; text-transform: uppercase;
  color: var(--ink); cursor: pointer;
}
.nav-logo span { color: var(--cherry); font-style: italic; }

.nav-links {
  display: flex; gap: 3rem; list-style: none;
}
.nav-links a {
  font-size: 0.75rem; letter-spacing: 0.2em;
  text-transform: uppercase; color: var(--ink-soft);
  cursor: pointer; transition: color 0.3s;
  text-decoration: none;
}
.nav-links a:hover { color: var(--cherry); }

.nav-actions { display: flex; gap: 2rem; align-items: center; }

.nav-cart, .nav-login {
  font-size: 0.75rem; letter-spacing: 0.2em;
  text-transform: uppercase; cursor: pointer;
  color: var(--ink);
}
.nav-cart {
  border-bottom: 1px solid var(--cherry);
  padding-bottom: 2px;
}

/* Hamburger */
.hamburger {
  display: none;
  flex-direction: column; gap: 5px;
  background: none; border: none; cursor: pointer;
  padding: 4px;
}
.hamburger span {
  display: block; width: 24px; height: 1px;
  background: var(--ink); transition: all 0.3s;
}
.hamburger.open span:nth-child(1) { transform: translateY(6px) rotate(45deg); }
.hamburger.open span:nth-child(2) { opacity: 0; }
.hamburger.open span:nth-child(3) { transform: translateY(-6px) rotate(-45deg); }

/* Mobile menu */
.mobile-menu {
  display: none;
  position: fixed; top: 0; left: 0; right: 0; bottom: 0;
  background: var(--ivory);
  flex-direction: column; align-items: center; justify-content: center;
  z-index: 99;
}
.mobile-menu.open { display: flex; }
.mobile-menu ul { list-style: none; display: flex; flex-direction: column; gap: 2rem; text-align: center; }
.mobile-menu li {
  font-family: var(--font-display);
  font-size: 2rem; font-weight: 300;
  color: var(--ink); cursor: pointer;
  transition: color 0.3s;
}
.mobile-menu li:hover { color: var(--cherry); }

/* Responsive */
@media (max-width: 768px) {
  .nav {
    padding: 1.5rem 2rem;
  }
  .nav-links { display: none; }
  .nav-actions { display: none; }
  .hamburger { display: flex; }
}
</style>