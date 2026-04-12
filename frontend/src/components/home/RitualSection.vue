<script setup lang="ts">
const steps = [
  { num: '01', title: 'Double nettoyage', desc: 'Commencez par une huile démaquillante, suivie d\'un nettoyant moussant pour éliminer toutes les impuretés.' },
  { num: '02', title: 'Toner & Essence', desc: 'Équilibrez le pH de votre peau et préparez-la à absorber tous les actifs des soins suivants.' },
  { num: '03', title: 'Sérum & Masque', desc: 'Ciblez vos préoccupations spécifiques avec des formules hautement concentrées en actifs.' },
  { num: '04', title: 'Crème & SPF', desc: 'Hydratez et protégez. La protection solaire est le secret anti-âge numéro un en Corée.' },
]
</script>

<template>
  <section class="section-ritual">
    <div class="ritual-content">
      <p class="section-label">Méthode</p>
      <h2 class="section-title">Le rituel<br><em>coréen</em></h2>
      <div class="ritual-steps">
        <div v-for="step in steps" :key="step.num" class="ritual-step">
          <div class="step-num">{{ step.num }}</div>
          <div>
            <div class="step-title">{{ step.title }}</div>
            <div class="step-desc">{{ step.desc }}</div>
          </div>
        </div>
      </div>
    </div>
    <div class="ritual-visual">
      <div class="ritual-bg">
        <div class="ritual-circles">
          <div class="r-circle" />
          <div class="r-circle" />
          <div class="r-circle" />
        </div>
        <div class="ritual-quote">
          "La beauté coréenne, c'est la science au service de la sensorialité."
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.section-ritual {
  padding: var(--spacing-xl) var(--spacing-lg);
  display: grid; grid-template-columns: 1fr 1fr;
  gap: 8rem; align-items: center;
}
.ritual-steps { display: flex; flex-direction: column; gap: 3rem; margin-top: 3rem; }
.ritual-step {
  display: grid; grid-template-columns: 3rem 1fr;
  gap: 2rem; align-items: start;
  padding-bottom: 3rem;
  border-bottom: 1px solid var(--ivory-dark);
}
.ritual-step:last-child { border-bottom: none; padding-bottom: 0; }
.step-num {
  font-family: var(--font-display);
  font-size: 2rem; color: var(--cherry); line-height: 1;
}
.step-title {
  font-family: var(--font-display);
  font-size: 1.4rem; color: var(--ink); margin-bottom: 0.5rem;
}
.step-desc { font-size: 0.85rem; line-height: 1.7; color: var(--ink-soft); }
.ritual-bg {
  aspect-ratio: 3/4;
  background: linear-gradient(160deg, #E8D5D8, #C4A0A8);
  position: relative; overflow: hidden;
}
.ritual-circles {
  position: absolute; inset: 0;
  display: flex; align-items: center; justify-content: center;
}
.r-circle {
  position: absolute; border-radius: 50%;
  border: 1px solid rgba(196,72,90,0.3);
}
.r-circle:nth-child(1) { width: 200px; height: 200px; animation: spin 15s linear infinite; }
.r-circle:nth-child(2) { width: 300px; height: 300px; animation: spin 25s linear infinite reverse; }
.r-circle:nth-child(3) { width: 400px; height: 400px; animation: spin 35s linear infinite; }
.ritual-quote {
  position: absolute; bottom: 3rem; left: 3rem; right: 3rem;
  font-family: var(--font-display);
  font-size: 1.5rem; font-style: italic; font-weight: 300;
  color: var(--ink); line-height: 1.4;
}

@media (max-width: 768px) {
  .section-ritual {
    grid-template-columns: 1fr;
    padding: 4rem 2rem;
    gap: 3rem;
  }
  .ritual-visual { display: none; }
}
</style>