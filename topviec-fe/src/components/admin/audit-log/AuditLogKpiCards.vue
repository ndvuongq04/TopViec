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
      <p v-if="stat.note" class="kpi-card__note">{{ stat.note }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  stats: {
    label: string; value: string; icon: string
    iconBg: string; iconColor: string
    note?: string
  }[]
}>()
</script>

<style scoped>
.kpi-cards {
  display: grid; grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem; margin-bottom: 2.5rem;
}
@media (max-width: 1024px) { .kpi-cards { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 640px)  { .kpi-cards { grid-template-columns: 1fr; } }

.kpi-card {
  background: white; padding: 1.5rem; border-radius: 1.25rem;
  border: 1px solid #e2e8f0; transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex; flex-direction: column;
}
:deep(.dark) .kpi-card { background: #1e293b; border-color: #334155; }
.kpi-card:hover { transform: translateY(-4px); box-shadow: 0 10px 25px -5px rgba(0,0,0,0.1); border-color: #cbd5e1; }

.kpi-card__top { display: flex; align-items: center; gap: 0.75rem; margin-bottom: 1.25rem; }
.kpi-card__icon { 
  width: 40px; height: 40px; border-radius: 12px; 
  display: flex; align-items: center; justify-content: center; 
  transition: transform 0.3s ease;
}
.kpi-card:hover .kpi-card__icon { transform: scale(1.1); }
.kpi-card__label { font-size: 0.875rem; font-weight: 600; color: #64748b; }
:deep(.dark) .kpi-card__label { color: #94a3b8; }

.kpi-card__value { font-size: 2rem; font-weight: 800; color: #0f172a; line-height: 1; margin-bottom: 0.25rem; }
:deep(.dark) .kpi-card__value { color: #f1f5f9; }

.kpi-card__note { margin-top: 0.5rem; font-size: 0.75rem; color: #94a3b8; font-weight: 500; }
:deep(.dark) .kpi-card__note { color: #64748b; }
</style>
