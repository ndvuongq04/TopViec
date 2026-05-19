<template>
  <div class="cd-overview">
    <h2 class="cd-overview__title">Tổng quan khiếu nại</h2>
    <div class="cd-overview__grid">
      <div v-for="item in fields" :key="item.label" class="cd-overview__item">
        <p class="cd-overview__label">{{ item.label }}</p>
        <p :class="['cd-overview__value', item.highlight && 'cd-overview__value--highlight']">
          <span v-if="item.icon" class="material-symbols-outlined">{{ item.icon }}</span>
          {{ item.value }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  overview: {
    createdAt: string; updatedAt: string
    channel: string; evidenceStatus: string
  }
}>()

const fields = computed(() => [
  { label: 'Ngày tạo',        value: props.overview.createdAt,      highlight: false, icon: '' },
  { label: 'Cập nhật cuối',   value: props.overview.updatedAt,      highlight: false, icon: '' },
  { label: 'Kênh gửi',        value: props.overview.channel,        highlight: false, icon: '' },
  { label: 'Bằng chứng',      value: props.overview.evidenceStatus, highlight: true,  icon: 'check_circle' },
])
</script>

<style scoped>
.cd-overview {
  background: #fff; border-radius: 0.75rem; padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  transition: border-color 0.15s; border: 1px solid transparent;
}
.cd-overview:hover { border-color: #f0eee7; }

.cd-overview__title { font-size: 1.125rem; font-weight: 700; color: #1b1c18; margin-bottom: 1rem; }
.cd-overview__grid {
  display: grid; grid-template-columns: repeat(4, 1fr); gap: 1.5rem 1rem;
}
@media (max-width: 768px) { .cd-overview__grid { grid-template-columns: repeat(2, 1fr); } }

.cd-overview__label {
  font-size: 11px; font-weight: 700; text-transform: uppercase;
  letter-spacing: 0.07em; color: #574240; margin-bottom: 4px;
}
.cd-overview__value {
  font-size: 0.8125rem; font-weight: 500; color: #1b1c18;
  display: flex; align-items: center; gap: 4px;
}
.cd-overview__value--highlight { color: #085041; }
.cd-overview__value .material-symbols-outlined { font-size: 16px; }
</style>
