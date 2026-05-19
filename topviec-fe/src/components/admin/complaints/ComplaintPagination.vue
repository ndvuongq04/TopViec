<template>
  <div class="cp-wrap">
    <span class="cp-info">
      Hiển thị <strong>{{ from }}-{{ to }}</strong> / <strong>{{ total }}</strong> kết quả
    </span>
    <div class="cp-controls">
      <button class="cp-nav-btn" :disabled="!canGoPrev" @click="$emit('change', currentPage - 1)">
        <span class="material-symbols-outlined">chevron_left</span>
      </button>
      <template v-for="p in displayedPages" :key="p">
        <span v-if="p === '...'" class="cp-ellipsis">…</span>
        <button v-else :class="['cp-page-btn', p === currentPage ? 'active' : '']"
          @click="$emit('change', Number(p))">{{ Number(p) + 1 }}</button>
      </template>
      <button class="cp-nav-btn" :disabled="!canGoNext" @click="$emit('change', currentPage + 1)">
        <span class="material-symbols-outlined">chevron_right</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{ currentPage: number; total: number; perPage?: number }>()
defineEmits<{ change: [number] }>()

const perPage    = computed(() => props.perPage ?? 10)
const totalPages = computed(() => Math.ceil(props.total / perPage.value))

const canGoPrev = computed(() => props.currentPage > 0)
const canGoNext = computed(() => props.currentPage < totalPages.value - 1)

const from = computed(() => props.total > 0 ? props.currentPage * perPage.value + 1 : 0)
const to   = computed(() => Math.min((props.currentPage + 1) * perPage.value, props.total))

const displayedPages = computed(() => {
  const pages: (number | string)[] = []
  if (totalPages.value <= 7) {
    for (let i = 0; i < totalPages.value; i++) pages.push(i)
    return pages
  }
  const cur  = props.currentPage
  const last = totalPages.value - 1
  const set  = new Set<number>(
    [0, cur - 1, cur, cur + 1, last].filter(i => i >= 0 && i <= last),
  )
  const sorted = Array.from(set).sort((a, b) => a - b)
  sorted.forEach((p, i) => {
    if (i > 0 && p - sorted[i - 1] > 1) pages.push('...')
    pages.push(p)
  })
  return pages
})
</script>

<style scoped>
.cp-wrap {
  display: flex; flex-wrap: wrap;
  justify-content: space-between; align-items: center;
  margin-top: 24px; padding-top: 24px;
  border-top: 1px solid #fbf9f2; gap: 16px;
}
.cp-info { font-size: 0.875rem; color: #574240; font-family: 'Manrope', sans-serif; }
.cp-info strong { color: #1b1c18; }
.cp-controls { display: flex; align-items: center; gap: 4px; }

.cp-nav-btn {
  padding: 8px; background: transparent; border: none;
  border-radius: 8px; cursor: pointer; color: #574240;
  display: flex; align-items: center;
  transition: background 0.15s;
}
.cp-nav-btn:hover:not(:disabled) { background: #f6f4ec; }
.cp-nav-btn:disabled { opacity: 0.4; cursor: default; }
.cp-nav-btn .material-symbols-outlined { font-size: 18px; }

.cp-page-btn {
  width: 32px; height: 32px; border-radius: 8px;
  border: none; background: transparent; cursor: pointer;
  font-size: 0.875rem; font-weight: 500; color: #574240;
  font-family: 'Manrope', sans-serif;
  display: flex; align-items: center; justify-content: center;
  transition: background 0.15s;
}
.cp-page-btn:hover { background: #f6f4ec; }
.cp-page-btn.active {
  background: #963131; color: #fff; font-weight: 700;
  box-shadow: 0 2px 4px rgba(150,49,49,0.2);
}
.cp-ellipsis { padding: 0 4px; color: #574240; font-size: 0.875rem; font-family: 'Manrope', sans-serif; }
</style>