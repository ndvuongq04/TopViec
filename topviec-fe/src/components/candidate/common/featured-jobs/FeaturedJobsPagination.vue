<template>
  <div class="pagination">
    <button class="page-btn" :disabled="current === 0" @click="$emit('prev')">
      <span class="material-symbols-outlined">chevron_left</span>
    </button>

    <div class="page-info">
      <span class="current-page">{{ current + 1 }}</span>
      <span class="sep">/</span>
      <span class="total-page">{{ total }} trang</span>
    </div>

    <button class="page-btn" :disabled="current === total - 1" @click="$emit('next')">
      <span class="material-symbols-outlined">chevron_right</span>
    </button>
  </div>
</template>

<script setup lang="ts">
defineProps<{ current: number; total: number }>()
defineEmits<{ prev: []; next: [] }>()
</script>

<style scoped>
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
}

.page-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 1px solid rgba(192, 199, 212, 0.4);
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #404752;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
  transition: background 0.15s, color 0.15s;
}
.page-btn:hover:not(:disabled) {
  background: #e9edff;
  color: #005ea4;
}
.page-btn:disabled {
  opacity: 0.35;
  cursor: not-allowed;
}
.page-btn .material-symbols-outlined { font-size: 18px; }

.page-info {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.875rem;
  font-weight: 600;
}
.current-page { color: #005ea4; font-weight: 700; }
.sep { color: #707783; }
.total-page { color: #707783; }
</style>