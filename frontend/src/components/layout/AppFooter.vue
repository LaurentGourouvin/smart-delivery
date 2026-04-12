<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<template>
  <footer class="footer">
    <div>
      <div class="footer-brand">Smart<span>Delivery</span></div>
      <p class="footer-tagline">
        Votre destination K-beauty en France. Des produits authentiques,
        directement sélectionnés en Corée du Sud.
      </p>
    </div>
    <div class="footer-col">
      <h4>Catalogue</h4>
      <ul>
        <li><a href="#">Sérums</a></li>
        <li><a href="#">Masques</a></li>
        <li><a href="#">Toners</a></li>
        <li><a href="#">Crèmes</a></li>
      </ul>
    </div>
    <div class="footer-col">
      <h4>Marques</h4>
      <ul>
        <li><a href="#">COSRX</a></li>
        <li><a href="#">Laneige</a></li>
        <li><a href="#">Innisfree</a></li>
        <li><a href="#">Some By Mi</a></li>
      </ul>
    </div>
    <div class="footer-col">
      <h4>Service</h4>
      <ul>
        <li><a @click.prevent="router.push('/profil')">Mon compte</a></li>
        <li><a @click.prevent="router.push('/commandes')">Mes commandes</a></li>
        <li><a href="#">Livraison</a></li>
        <li><a href="#">Contact</a></li>
      </ul>
    </div>
  </footer>
  <div class="footer-bottom">
    <span>© 2026 SmartDelivery · Tous droits réservés</span>
    <span>Fait avec ♥ à Bordeaux · Livraison depuis Séoul</span>
  </div>
</template>

<style scoped>
.footer {
  background: var(--ink); padding: var(--spacing-lg);
  display: grid; grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: var(--spacing-lg); align-items: start;
}
.footer-brand {
  font-family: var(--font-display);
  font-size: 2rem; font-weight: 300;
  letter-spacing: 0.2em; color: var(--ivory); margin-bottom: 1rem;
}
.footer-brand span { color: var(--cherry); font-style: italic; }
.footer-tagline {
  font-size: 0.8rem; line-height: 1.7;
  color: rgba(247,243,238,0.5); max-width: 280px;
}
.footer-col h4 {
  font-size: 0.65rem; letter-spacing: 0.3em;
  text-transform: uppercase; color: var(--cherry-light); margin-bottom: 1.5rem;
}
.footer-col ul { list-style: none; display: flex; flex-direction: column; gap: 0.8rem; }
.footer-col a {
  font-size: 0.85rem; color: rgba(247,243,238,0.6);
  text-decoration: none; cursor: pointer; transition: color 0.3s;
}
.footer-col a:hover { color: var(--ivory); }
.footer-bottom {
  background: var(--ink); padding: 1.5rem var(--spacing-lg);
  border-top: 1px solid rgba(247,243,238,0.1);
  display: flex; justify-content: space-between; align-items: center;
}
.footer-bottom span {
  font-size: 0.7rem; letter-spacing: 0.15em;
  color: rgba(247,243,238,0.3);
}

@media (max-width: 768px) {
  .footer {
    grid-template-columns: 1fr;
    gap: 2rem;
    padding: 2rem;
  }
  .footer-bottom {
    flex-direction: column; gap: 0.5rem;
    text-align: center; padding: 1.5rem 2rem;
  }
}
</style>