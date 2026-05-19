<template>
  <div class="stats-grid">
    <div class="stat-card">
      <p class="stat-card__label">Tổng số tin</p>
      <p class="stat-card__value">{{ stats.total }}</p>
      <span class="material-symbols-outlined stat-card__icon">list_alt</span>
    </div>
    <div class="stat-card stat-card--active">
      <p class="stat-card__label">Đang tuyển</p>
      <p class="stat-card__value">{{ stats.active }}</p>
      <span class="material-symbols-outlined stat-card__icon">rocket_launch</span>
    </div>
    <div class="stat-card stat-card--pending">
      <p class="stat-card__label">Chờ duyệt</p>
      <p class="stat-card__value">{{ String(stats.pending).padStart(2, '0') }}</p>
      <span class="material-symbols-outlined stat-card__icon">hourglass_empty</span>
    </div>
    <div class="stat-card stat-card--expiring">
      <p class="stat-card__label">Sắp hết hạn</p>
      <p class="stat-card__value">{{ String(stats.expiring).padStart(2, '0') }}</p>
      <span class="material-symbols-outlined stat-card__icon">alarm</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { JobPostingStats } from '@/types/employerJobPosting.types'

defineProps<{
  stats: JobPostingStats
}>()
</script>

<style scoped>
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
}
@media (max-width: 900px) { .stats-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 520px)  { .stats-grid { grid-template-columns: 1fr; } }

.stat-card {
  background: var(--color-surface);
  padding: 1.5rem;
  border-radius: var(--radius-lg);
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  border: 1px solid var(--color-border-light);
  position: relative;
  overflow: hidden;
}
.stat-card__label {
  font-size: 0.875rem;
  font-weight: 600;
  color: #64748b;
  margin: 0 0 0.25rem;
}
.stat-card__value {
  font-size: 1.875rem;
  font-weight: 800;
  color: var(--color-on-surface);
  margin: 0;
  line-height: 1.2;
}
.stat-card__icon {
  position: absolute;
  right: -0.5rem;
  bottom: -0.5rem;
  font-size: 5rem !important;
  opacity: 0.05;
  transition: transform 0.5s;
}
.stat-card:hover .stat-card__icon { transform: scale(1.1); }

.stat-card--active   { border-left: 4px solid var(--color-primary); }
.stat-card--active   .stat-card__label { color: var(--color-primary); }
.stat-card--active   .stat-card__icon  { opacity: 0.1; color: var(--color-primary); }

.stat-card--pending  { border-left: 4px solid var(--color-tertiary); }
.stat-card--pending  .stat-card__label { color: var(--color-tertiary); }
.stat-card--pending  .stat-card__icon  { opacity: 0.1; color: var(--color-tertiary); }

.stat-card--expiring { border-left: 4px solid var(--color-error); }
.stat-card--expiring .stat-card__label { color: var(--color-error); }
.stat-card--expiring .stat-card__icon  { opacity: 0.1; color: var(--color-error); }
</style>
