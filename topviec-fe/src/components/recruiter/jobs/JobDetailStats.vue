<template>
  <div class="stats-grid">
    <div
      v-for="stat in stats"
      :key="stat.label"
      class="stat-card"
    >
      <div class="stat-icon" :style="{ background: stat.iconBg }">
        <span class="material-symbols-outlined" :style="{ color: stat.iconColor }">{{ stat.icon }}</span>
      </div>
      <div>
        <p class="stat-label">{{ stat.label }}</p>
        <p class="stat-value">{{ stat.value }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">


interface Stat {
  label: string
  value: string
  icon: string
  iconBg: string
  iconColor: string
}

withDefaults(defineProps<{
  stats?: Stat[]
}>(), {
  stats: () => [
    { label: 'Lượt xem',     value: '1,240',  icon: 'visibility',   iconBg: '#eff6ff', iconColor: '#2563eb' },
    { label: 'Ứng viên',     value: '42',     icon: 'groups',       iconBg: '#fff7ed', iconColor: '#ea580c' },
    { label: 'Số lần sửa',   value: '1/2',    icon: 'history_edu',  iconBg: '#f5f3ff', iconColor: '#7c3aed' },
    { label: 'Ngày còn lại', value: '15 ngày',icon: 'schedule',     iconBg: '#fef2f2', iconColor: '#dc2626' },
  ],
})
</script>

<style scoped>
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
  margin-bottom: 2.5rem;
}
@media (max-width: 1024px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 640px) {
  .stats-grid { grid-template-columns: 1fr; }
}

.stat-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 0.75rem;
  padding: 1.25rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 1px 3px rgba(0,0,0,.05);
  transition: box-shadow 0.15s;
}
.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,.08);
}

.stat-icon {
  width: 3rem;
  height: 3rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.stat-icon .material-symbols-outlined { font-size: 1.375rem; }

.stat-label {
  font-size: 0.75rem; /* Tiny text 12px chuẩn */
  font-weight: 500;
  color: #64748b;
  margin-bottom: 0.125rem;
}

.stat-value {
  font-size: 1.875rem; /* Tiêu đề lớn 30px chuẩn */
  font-weight: 800;
  color: #0f172a;
  line-height: 1.1;
}
</style>
