<template>
  <div class="tabs-wrap">
    <nav class="tabs-nav">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        class="tab-btn"
        :class="{ active: modelValue === tab.value }"
        @click="$emit('update:modelValue', tab.value)"
      >
        {{ tab.label }}
      </button>
    </nav>
  </div>
</template>

<script setup lang="ts">
defineProps<{ modelValue: string }>()
defineEmits<{ 'update:modelValue': [value: string] }>()

const tabs = [
  { value: 'profile', label: 'Hồ sơ' },
  { value: 'cvs', label: 'Danh sách CV' },
  { value: 'applications', label: 'Lịch sử ứng tuyển' },
  { value: 'followed', label: 'Công ty đã follow' },
  { value: 'saved', label: 'Tin đã lưu' },
]
</script>

<style scoped>
.tabs-wrap { border-bottom: 1px solid #e4e2dc; }
.tabs-nav { display: flex; gap: 0; margin-bottom: -1px; }

.tab-btn {
  padding: 14px 4px;
  margin-right: 32px;
  font-size: 13px;
  font-weight: 500;
  color: #574240;
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  cursor: pointer;
  font-family: inherit;
  white-space: nowrap;
  transition: color 0.15s, border-color 0.15s;
}
.tab-btn:hover:not(.active) {
  color: #1b1c18;
  border-bottom-color: #8a7170;
}
.tab-btn.active {
  color: #76191d;
  border-bottom-color: #76191d;
  font-weight: 600;
}
</style>