<template>
  <div class="tabs">
    <button
      v-for="tab in tabs" :key="tab.value"
      :class="['tabs__item', modelValue === tab.value && 'tabs__item--active']"
      @click="$emit('update:modelValue', tab.value)"
    >{{ tab.label }}</button>
  </div>
</template>

<script setup lang="ts">
defineProps<{ modelValue: string }>()
defineEmits(['update:modelValue'])

const tabs = [
  { value: 'orders', label: 'Đơn hàng' },
  { value: 'history', label: 'Lịch sử sử dụng' },
]
</script>

<style scoped>
.tabs {
  display: flex; gap: 2.5rem;
  border-bottom: 1px solid #e2e8f0;
  margin-top: 2.5rem;
}
.tabs__item {
  padding-bottom: 1rem;
  font-size: 0.875rem; font-weight: 700;
  color: #64748b; background: none; border: none;
  cursor: pointer; position: relative;
  transition: color 0.2s;
}
.tabs__item:hover { color: #334155; }
.tabs__item--active {
  font-weight: 800; color: #4B9AF6;
}
.tabs__item--active::after {
  content: '';
  position: absolute; bottom: 0; left: 0;
  width: 100%; height: 2px;
  background: #4B9AF6;
  border-radius: 2px 2px 0 0;
}
</style>