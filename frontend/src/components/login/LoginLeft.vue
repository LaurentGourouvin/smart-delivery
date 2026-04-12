<template>
  <div class="login-left">
    <div class="deco-circle circle-1" />
    <div class="deco-circle circle-2" />
    <div class="deco-circle circle-3" />
    <div class="deco-content">
      <p class="deco-tag">Bienvenue</p>
      <h2 class="deco-title">Votre monde<br><em>K-beauty</em><br>vous attend</h2>
      <p class="deco-quote">"La peau est le reflet de l'âme."</p>
    </div>
  </div>
</template>

<style scoped>
.login-left {
  background: linear-gradient(160deg, #E8D5D8 0%, #D4B8BC 50%, #C4A0A8 100%);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.deco-circle {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(196,72,90,0.3);
}
.circle-1 {
  width: 500px; height: 500px;
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  animation: spin 30s linear infinite;
}
.circle-2 {
  width: 350px; height: 350px;
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  animation: spin 20s linear infinite reverse;
}
.circle-3 {
  width: 200px; height: 200px;
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  animation: spin 15s linear infinite;
}

.deco-content {
  position: relative; z-index: 2; padding: 4rem;
}

.deco-tag {
  font-size: 0.7rem; letter-spacing: 0.4em;
  text-transform: uppercase; color: var(--cherry);
  margin-bottom: 1.5rem;
  display: flex; align-items: center; gap: 1rem;
}
.deco-tag::before {
  content: ''; width: 30px; height: 1px; background: var(--cherry);
}

.deco-title {
  font-family: var(--font-display);
  font-size: clamp(2.5rem, 4vw, 4rem);
  font-weight: 300; line-height: 1.1;
  color: var(--ink); margin-bottom: 3rem;
}
.deco-title em { font-style: italic; color: var(--cherry); }

.deco-quote {
  font-family: var(--font-display);
  font-size: 1rem; font-style: italic;
  color: var(--ink-soft);
}

@media (max-width: 768px) {
  .login-left { display: none; }
}
</style>