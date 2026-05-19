<template>
  <div v-if="!dismissed" :class="['search-banner', `search-banner--${variant}`]">
    <span class="material-symbols-outlined search-banner__icon">{{ icon }}</span>
    <div class="search-banner__body">
      <p class="search-banner__text">
        Bạn đã dùng hết <strong>{{ used }}/{{ limit }} lượt</strong> tìm kiếm trong tháng này.
      </p>
      <p class="search-banner__sub">Nâng cấp gói dịch vụ để tìm kiếm thêm.</p>
    </div>
    <button class="search-banner__action" @click="$emit('upgrade')">Nâng cấp ngay</button>
    <button class="search-banner__dismiss" @click="dismissed = true">
      <span class="material-symbols-outlined">close</span>
    </button>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

const props = defineProps<{ used: number; limit: number }>()
defineEmits(['upgrade'])

const dismissed = ref(false)
const ratio = computed(() => props.used / props.limit)
const variant = computed(() => ratio.value >= 1 ? 'error' : ratio.value >= 0.8 ? 'warning' : 'info')
const icon = computed(() => ratio.value >= 1 ? 'error' : 'warning')
</script>

<style scoped>
.search-banner {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 1rem 1.25rem;
  border-radius: 0.75rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.search-banner--warning {
  border: 1px solid #f59e0b;
  background: #fef3c7;
}

.search-banner--error {
  border: 1px solid #ef4444;
  background: #fee2e2;
}

.search-banner--info {
  border: 1px solid #4b9af6;
  background: #e0f2fe;
}

.search-banner__icon {
  margin-top: 2px;
  flex-shrink: 0;
  font-size: 1.25rem;
}

.search-banner--warning .search-banner__icon {
  color: #f59e0b;
}

.search-banner--error .search-banner__icon {
  color: #ef4444;
}

.search-banner--info .search-banner__icon {
  color: #4b9af6;
}

.search-banner__body {
  flex: 1;
}

.search-banner__text {
  font-size: 0.875rem;
  font-weight: 600;
}

.search-banner--warning .search-banner__text {
  color: #92400e;
}

.search-banner--error .search-banner__text {
  color: #b91c1c;
}

.search-banner--info .search-banner__text {
  color: #0369a1;
}

.search-banner__text strong {
  font-weight: 700;
}

.search-banner__sub {
  margin-top: 2px;
  font-size: 0.875rem;
  opacity: 0.85;
}

.search-banner--warning .search-banner__sub {
  color: #92400e;
}

.search-banner--error .search-banner__sub {
  color: #b91c1c;
}

.search-banner--info .search-banner__sub {
  color: #0369a1;
}

.search-banner__action {
  flex-shrink: 0;
  border: none;
  background: none;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  font-size: 1rem;
  font-weight: 700;
  transition: opacity 0.15s;
}

.search-banner--warning .search-banner__action {
  color: #d97706;
}

.search-banner--error .search-banner__action {
  color: #dc2626;
}

.search-banner--info .search-banner__action {
  color: #4b9af6;
}

.search-banner__action:hover {
  opacity: 0.7;
}

.search-banner__dismiss {
  display: flex;
  align-items: center;
  padding: 0;
  border: none;
  background: none;
  cursor: pointer;
  opacity: 0.5;
  transition: opacity 0.15s;
}

.search-banner__dismiss:hover {
  opacity: 1;
}

.search-banner__dismiss .material-symbols-outlined {
  color: #64748b;
  font-size: 1.125rem;
}
</style>
