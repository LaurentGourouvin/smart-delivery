<script setup lang="ts">
import { RouterLink } from 'vue-router'
</script>

<template>
  <div class="login-right">
    <div class="login-form-wrap">
      <p class="form-tag">Connexion</p>
      <h1 class="form-title">Bon retour<br><em>parmi nous</em></h1>

      <div class="form">
        <div class="form-group">
          <label>Adresse email</label>
          <input type="email" placeholder="laurent@smartdelivery.com" />
        </div>
        <div class="form-group">
          <label>Mot de passe</label>
          <input type="password" placeholder="••••••••" />
        </div>
        <div class="form-options">
          <label class="checkbox-wrap">
            <input type="checkbox" />
            <span>Se souvenir de moi</span>
          </label>
          <a href="#" class="forgot-link">Mot de passe oublié ?</a>
        </div>
        <button class="btn-login">
          <span>Se connecter</span>
        </button>
      </div>

      <div class="form-divider"><span>ou</span></div>

      <button class="btn-keycloak">
        🔐 Continuer avec Keycloak
      </button>

      <p class="form-register">
        Pas encore de compte ?
        <RouterLink to="/register">Créer un compte</RouterLink>
      </p>
    </div>
  </div>
</template>

<style scoped>
.login-right {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4rem;
  background: var(--ivory);
}

.login-form-wrap { width: 100%; max-width: 420px; }

.form-tag {
  font-size: 0.7rem; letter-spacing: 0.4em;
  text-transform: uppercase; color: var(--cherry);
  margin-bottom: 1rem;
  display: flex; align-items: center; gap: 1rem;
}
.form-tag::before {
  content: ''; width: 30px; height: 1px; background: var(--cherry);
}

.form-title {
  font-family: var(--font-display);
  font-size: clamp(2rem, 3vw, 3rem);
  font-weight: 300; line-height: 1.1;
  color: var(--ink); margin-bottom: 3rem;
}
.form-title em { font-style: italic; color: var(--cherry); }

.form { display: flex; flex-direction: column; gap: 1.5rem; }

.form-group { display: flex; flex-direction: column; gap: 0.5rem; }

.form-group label {
  font-size: 0.7rem; letter-spacing: 0.2em;
  text-transform: uppercase; color: var(--ink-soft);
}

.form-group input {
  border: none;
  border-bottom: 1px solid var(--ivory-dark);
  background: transparent;
  padding: 0.75rem 0;
  font-family: var(--font-body);
  font-size: 0.9rem; color: var(--ink);
  outline: none;
  transition: border-color 0.3s;
}
.form-group input:focus { border-bottom-color: var(--cherry); }
.form-group input::placeholder { color: rgba(74,63,58,0.4); }

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.checkbox-wrap {
  display: flex; align-items: center; gap: 0.5rem;
  font-size: 0.8rem; color: var(--ink-soft); cursor: pointer;
}
.checkbox-wrap input { accent-color: var(--cherry); }

.forgot-link {
  font-size: 0.75rem; color: var(--cherry);
  text-decoration: none; letter-spacing: 0.1em;
}
.forgot-link:hover { text-decoration: underline; }

.btn-login {
  background: var(--cherry); color: white; border: none;
  padding: 1rem; width: 100%;
  font-family: var(--font-body);
  font-size: 0.75rem; letter-spacing: 0.2em;
  text-transform: uppercase; cursor: pointer;
  transition: background 0.3s; margin-top: 0.5rem;
}
.btn-login:hover { background: var(--ink); }

.form-divider {
  display: flex; align-items: center; gap: 1rem;
  margin: 2rem 0;
}
.form-divider::before, .form-divider::after {
  content: ''; flex: 1; height: 1px; background: var(--ivory-dark);
}
.form-divider span {
  font-size: 0.75rem; color: var(--ink-soft); letter-spacing: 0.2em;
}

.btn-keycloak {
  width: 100%; padding: 1rem;
  border: 1px solid var(--ivory-dark);
  background: transparent; cursor: pointer;
  font-family: var(--font-body);
  font-size: 0.8rem; letter-spacing: 0.1em;
  color: var(--ink); transition: all 0.3s;
  display: flex; align-items: center; justify-content: center; gap: 0.75rem;
}
.btn-keycloak:hover { border-color: var(--cherry); color: var(--cherry); }

.form-register {
  text-align: center; margin-top: 2rem;
  font-size: 0.8rem; color: var(--ink-soft);
}
.form-register a {
  color: var(--cherry); text-decoration: none;
  border-bottom: 1px solid var(--cherry); padding-bottom: 1px;
}

@media (max-width: 768px) {
  .login-right { padding: 6rem 2rem 2rem; align-items: flex-start; }
}
</style>