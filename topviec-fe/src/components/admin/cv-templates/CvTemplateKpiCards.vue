<template>
  <div class="kpi-cards">
    <div v-for="stat in stats" :key="stat.label" class="kpi-card">
      <div class="kpi-card__top">
        <div class="kpi-card__icon" :style="{ background: stat.iconBg }">
          <span class="material-symbols-outlined" :style="{ color: stat.iconColor }">{{ stat.icon }}</span>
        </div>
        <p class="kpi-card__label">{{ stat.label }}</p>
      </div>
      <p class="kpi-card__value">{{ stat.value }}</p>
      <div :class="['kpi-card__trend', `kpi-card__trend--${stat.trendVariant}`]">
        <span v-if="stat.trendVariant === 'up' || stat.trendVariant === 'error'" class="material-symbols-outlined">trending_up</span>
        {{ stat.trend }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  stats: {
    label: string; value: string; icon: string
    iconBg: string; iconColor: string
    trend: string; trendVariant: string
  }[]
}>()
</script>

<style scoped>
.kpi-cards {
  display: grid; grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem; margin-bottom: 0;
}
@media (max-width: 1024px) { .kpi-cards { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 640px)  { .kpi-cards { grid-template-columns: 1fr; } }

.kpi-card {
  background: #fff; border-radius: 0.75rem; padding: 1.5rem;
  transition: transform 0.3s;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border: 1px solid rgba(228,226,220,0.5);
}
.kpi-card:hover { transform: translateY(-4px); }

.kpi-card__top { display: flex; align-items: center; gap: 12px; margin-bottom: 1rem; }
.kpi-card__icon {
  width: 32px; height: 32px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.kpi-card__icon .material-symbols-outlined { font-size: 18px; }
.kpi-card__label {
  font-size: 0.75rem; font-weight: 700; text-transform: uppercase;
  letter-spacing: 0.07em; color: #574240;
}

.kpi-card__value { font-size: 1.875rem; font-weight: 700; color: #1b1c18; margin-bottom: 8px; }

.kpi-card__trend {
  display: flex; align-items: center; gap: 4px;
  font-size: 0.75rem; font-weight: 500;
}
.kpi-card__trend .material-symbols-outlined { font-size: 14px; }
.kpi-card__trend--up      { color: #76191d; }
.kpi-card__trend--warn    { color: #574240; }
.kpi-card__trend--neutral { color: #574240; }
.kpi-card__trend--error   { color: #ba1a1a; }
</style>