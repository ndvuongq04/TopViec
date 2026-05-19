<template>
  <div class="stats-card">
    <h3 class="card-title">Thống kê hoạt động</h3>
    <div class="stats-grid">
      <div v-for="stat in statItems" :key="stat.label" class="stat-item">
        <p class="stat-value" :style="{ color: stat.color }">{{ stat.value }}</p>
        <p class="stat-label">{{ stat.label }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  stats: { totalCvs: number; applications: number; followedCompanies: number; savedJobs: number }
}>()

const statItems = computed(() => [
  { label: 'Tổng CV', value: props.stats.totalCvs, color: '#76191d' },
  { label: 'Lần ứng tuyển', value: props.stats.applications, color: '#004638' },
  { label: 'Cty theo dõi', value: props.stats.followedCompanies, color: '#5f5e5b' },
  { label: 'Việc làm đã lưu', value: props.stats.savedJobs, color: '#5f5e5b' },
])
</script>

<style scoped>
.stats-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 20px -4px rgba(87, 66, 64, 0.05);
}
.card-title {
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: #1b1c18;
  margin: 0 0 16px;
}
.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
.stat-item {
  background: #f6f4ec;
  border-radius: 8px;
  padding: 16px;
  text-align: center;
}
.stat-value { font-size: 26px; font-weight: 800; margin: 0 0 4px; }
.stat-label {
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #574240;
  margin: 0;
}
</style>