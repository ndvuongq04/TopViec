<template>
  <div class="brand-card" @click="router.push(`/companies/${company.slug}`)">
    <div class="brand-card__top">
      <div class="brand-card__logo">
        <img v-if="company.logoUrl" :src="company.logoUrl" :alt="company.name" class="brand-card__logo-img" />
        <span v-else class="brand-card__logo-fallback">{{ initials }}</span>
      </div>
      <div class="brand-card__info">
        <div class="brand-card__name-row">
          <h3 class="brand-card__name">{{ company.name }}</h3>
          <div v-if="company.isBrandVerified" class="brand-card__verified-wrap">
            <span class="material-symbols-outlined brand-card__verified-icon">verified</span>
            <span class="brand-card__verified-tooltip">Công ty đã xác minh</span>
          </div>
        </div>
        <p v-if="company.industryName" class="brand-card__industry">{{ company.industryName }}</p>
      </div>
    </div>

    <div class="brand-card__footer">
      <span v-if="company.jobCount != null" class="brand-card__job-count">
        <span class="material-symbols-outlined" style="font-size:14px;vertical-align:middle">work</span>
        {{ company.jobCount }} việc làm
      </span>
      <span v-if="company.isTopEmployer" class="brand-card__badge brand-card__badge--top">Top </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const props = defineProps<{
  company: {
    id: number
    slug: string
    name: string
    logoUrl: string | null
    industryName?: string | null
    jobCount?: number | null
    isTopEmployer?: boolean | null
    isBrandVerified?: boolean | null
  }
}>()

const initials = computed(() =>
  props.company.name.split(' ').slice(0, 2).map(w => w[0]).join('').toUpperCase()
)
</script>

<style scoped>
.brand-card {
  background: #fff;
  border-radius: 0.75rem; padding: 1rem;
  display: flex; flex-direction: column;
  border: 1px solid rgba(192,199,212,0.4);
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  cursor: pointer;
  transition: transform 200ms ease, box-shadow 200ms ease, border-color 200ms ease;
}
.brand-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(180,83,9,0.1);
  border-color: #f59e0b;
}

.brand-card__top { display: flex; align-items: center; gap: 12px; margin-bottom: 0.75rem; }
.brand-card__info { flex: 1; min-width: 0; }

.brand-card__logo {
  width: 56px; height: 56px; flex-shrink: 0;
  border-radius: 0.5rem;
  display: flex; align-items: center; justify-content: center;
  border: 1px solid rgba(192,199,212,0.2);
  background: #f1f3ff;
  overflow: hidden;
}
.brand-card__logo-img { width: 100%; height: 100%; object-fit: contain; padding: 4px; }
.brand-card__logo-fallback {
  font-size: 0.875rem; font-weight: 800; color: #005ea4;
}

.brand-card__name-row { display: flex; align-items: flex-start; gap: 4px; margin-bottom: 4px; }
.brand-card__name {
  font-size: 0.875rem; font-weight: 700; color: #071b3b;
  text-transform: uppercase;
  display: -webkit-box; -webkit-line-clamp: 2;
  -webkit-box-orient: vertical; overflow: hidden; line-height: 1.3;
  font-family: 'Manrope', sans-serif;
}

.brand-card__verified-wrap {
  position: relative; flex-shrink: 0; display: flex; align-items: center;
}
.brand-card__verified-icon {
  font-size: 16px; color: #2563eb; cursor: default;
  font-variation-settings: 'FILL' 1;
}
.brand-card__verified-tooltip {
  display: none;
  position: absolute; bottom: calc(100% + 6px); left: 50%; transform: translateX(-50%);
  background: #1e40af; color: #fff;
  font-size: 0.75rem; font-weight: 500; font-family: 'Manrope', sans-serif;
  white-space: nowrap; padding: 4px 10px; border-radius: 6px;
  pointer-events: none; z-index: 10;
}
.brand-card__verified-tooltip::after {
  content: ''; position: absolute; top: 100%; left: 50%; transform: translateX(-50%);
  border: 5px solid transparent; border-top-color: #1e40af;
}
.brand-card__verified-wrap:hover .brand-card__verified-tooltip { display: block; }

.brand-card__industry { font-size: 0.75rem; color: #404752; margin-bottom: 6px; font-family: 'Manrope', sans-serif; }

.brand-card__footer { display: flex; flex-wrap: wrap; align-items: center; gap: 6px; }
.brand-card__job-count { font-size: 0.75rem; font-weight: 600; color: #404752; margin-right: 4px; }
.brand-card__badge {
  display: inline-flex; align-items: center; gap: 2px;
  font-size: 0.75rem; font-weight: 600;
  padding: 2px 8px; border-radius: 99px;
  font-family: 'Manrope', sans-serif;
}
.brand-card__badge--top {
  background: #fff7ed; color: #b45309; border: 1px solid #fcd34d;
}
</style>
