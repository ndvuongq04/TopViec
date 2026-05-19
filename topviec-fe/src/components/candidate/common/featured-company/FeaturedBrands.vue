<template>
  <div class="featured-brands">
    <FeaturedBrandsHeader />
    <FeaturedBrandsFilter v-model="activeCategory" :categories="categories" />

    <div class="featured-brands__body">
      <div class="featured-brands__cards">
        <FeaturedBrandCard v-for="c in store.companies" :key="c.id" :company="c" />
      </div>

      <div class="featured-brands__footer">
        <button class="featured-brands__view-all">
          Xem tất cả
          <span class="material-symbols-outlined">arrow_forward</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import FeaturedBrandsHeader from './FeaturedBrandsHeader.vue'
import FeaturedBrandsFilter from './FeaturedBrandsFilter.vue'
import FeaturedBrandCard from './FeaturedBrandCard.vue'
import { useCandidateCompanyStore } from '@/stores/candidateCompany.store'
import { useIndustryStore } from '@/stores/industry.store'

const store = useCandidateCompanyStore()
const industryStore = useIndustryStore()

const activeCategory = ref('Tất cả')

const categories = computed(() => [
  'Tất cả',
  ...industryStore.industries.map(i => i.name),
])

onMounted(() => {
  industryStore.fetchIndustries({ size: 100 })
  store.fetchPublicCompanies({ isTopEmployer: true, size: 6, page: 0 })
})
</script>

<style scoped>
.featured-brands {
  background: #fff;
  border-radius: 0.75rem;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  border: 1px solid rgba(192,199,212,0.2);
  overflow: hidden;
}
.featured-brands__body { padding: 1.5rem; background: #fff; }

.featured-brands__cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  margin-bottom: 1.5rem;
}
@media (max-width: 1024px) { .featured-brands__cards { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 640px) { .featured-brands__cards { grid-template-columns: 1fr; } }

.featured-brands__footer { display: flex; justify-content: center; padding-bottom: 8px; margin-top: 1.5rem; }
.featured-brands__view-all {
  display: flex; align-items: center; gap: 8px;
  padding: 10px 32px;
  border: 1px solid #005ea4; border-radius: 999px;
  background: none; color: #005ea4;
  font-size: 0.875rem; font-weight: 600; cursor: pointer;
  font-family: 'Inter', sans-serif;
  transition: background 0.2s, color 0.2s;
}
.featured-brands__view-all:hover { background: #005ea4; color: #fff; }
.featured-brands__view-all .material-symbols-outlined { font-size: 18px; }
</style>