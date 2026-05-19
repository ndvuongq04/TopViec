<template>
  <div class="rcp-wrap">
    <button class="rcp-nav-btn" :disabled="current === 1" @click="$emit('change', current - 1)">
      <span class="material-symbols-outlined">chevron_left</span>
    </button>
    <template v-for="p in displayedPages" :key="p">
      <span v-if="p === '...'" class="rcp-ellipsis">...</span>
      <button v-else :class="['rcp-page-btn', p === current ? 'active' : '']"
        @click="$emit('change', Number(p))">{{ p }}</button>
    </template>
    <button class="rcp-nav-btn" :disabled="current === totalPages" @click="$emit('change', current + 1)">
      <span class="material-symbols-outlined">chevron_right</span>
    </button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{ current: number; total: number; perPage?: number }>()
defineEmits<{ change: [number] }>()

const perPage    = computed(() => props.perPage ?? 10)
const totalPages = computed(() => Math.ceil(props.total / perPage.value))

const displayedPages = computed(() => {
  const pages: (number | string)[] = []
  if (totalPages.value <= 5) {
    for (let i = 1; i <= totalPages.value; i++) pages.push(i)
  } else {
    pages.push(1, 2, 3, '...', totalPages.value)
  }
  return pages
})
</script>

<style scoped>
.rcp-wrap {
  display: flex; align-items: center;
  justify-content: center; gap: 8px;
  margin-top: 32px;
}
.rcp-nav-btn {
  padding: 8px; border-radius: 8px;
  background: transparent; border: none;
  cursor: pointer; color: #64748b;
  display: flex; align-items: center;
  transition: background 0.15s;
}
.rcp-nav-btn:hover:not(:disabled) { background: #f1f5f9; }
.rcp-nav-btn:disabled { opacity: 0.4; cursor: default; }
.rcp-nav-btn .material-symbols-outlined { font-size: 22px; }

.rcp-page-btn {
  width: 40px; height: 40px; border-radius: 8px;
  background: #fff; color: #0f172a;
  border: 1px solid #e2e8f0;
  font-size: 13px; font-weight: 500;
  cursor: pointer; font-family: 'Manrope', sans-serif;
  transition: all 0.15s;
}
.rcp-page-btn:hover { background: #f1f5f9; }
.rcp-page-btn.active {
  background: #005ea4; color: #fff;
  border-color: transparent; font-weight: 700;
  box-shadow: 0 2px 6px rgba(0,94,164,0.25);
}
.rcp-ellipsis { padding: 0 8px; color: #64748b; font-size: 13px; }
</style>