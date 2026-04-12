<script setup lang="ts">
import AppNav from '@/components/layout/AppNav.vue'
import AppFooter from '@/components/layout/AppFooter.vue'
import HeroSection from '@/components/home/HeroSection.vue'
import BrandSection from '@/components/home/BrandSection.vue'
import CategoriesSection from '@/components/home/CategoriesSection.vue'
import FeaturedSection from '@/components/home/FeaturedSection.vue'
import RitualSection from '@/components/home/RitualSection.vue'
</script>

<template>
  <AppNav />
  <HeroSection />
  <BrandSection />
  <CategoriesSection />
  <FeaturedSection />
  <RitualSection />
  <AppFooter />
</template>