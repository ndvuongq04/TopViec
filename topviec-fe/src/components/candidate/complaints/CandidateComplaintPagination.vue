<template>
  <div class="ccp-wrap">
    <!-- Info -->
    <span class="ccp-info">
      Hiển thị <strong>{{ from }}–{{ to }}</strong> / <strong>{{ total }}</strong> kết quả
    </span>

    <!-- Controls -->
    <div class="ccp-controls">
      <button class="ccp-nav-btn" :disabled="!canGoPrev" @click="$emit('change', currentPage - 1)">
        <span class="material-symbols-outlined">chevron_left</span>
      </button>

      <template v-for="p in displayedPages" :key="p">
        <span v-if="p === '...'" class="ccp-ellipsis">…</span>
        <button v-else
          :class="['ccp-page-btn', p === currentPage ? 'active' : '']"
          @click="$emit('change', Number(p))">
          {{ Number(p) + 1 }}
        </button>
      </template>

      <button class="ccp-nav-btn" :disabled="!canGoNext" @click="$emit('change', currentPage + 1)">
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
.ccp-wrap {
  display: flex; flex-wrap: wrap;
  align-items: center; justify-content: space-between;
  gap: 16px; margin-top: 8px;
  padding-top: 20px;
  border-top: 1px solid #f1f5f9;
}

.ccp-info {
  font-size: 0.875rem; color: #64748b;
  font-family: 'Manrope', sans-serif;
}
.ccp-info strong { color: #0f172a; }

.ccp-controls { display: flex; align-items: center; gap: 4px; }

.ccp-nav-btn {
  width: 36px; height: 36px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 8px; border: 1px solid #e2e8f0;
  background: #fff; color: #64748b;
  cursor: pointer; transition: all 0.15s;
}
.ccp-nav-btn:hover:not(:disabled) { background: #f8fafc; border-color: #cbd5e1; color: #005ea4; }
.ccp-nav-btn:disabled { opacity: 0.4; cursor: default; }
.ccp-nav-btn .material-symbols-outlined { font-size: 18px; }

.ccp-page-btn {
  min-width: 36px; height: 36px; padding: 0 4px;
  border-radius: 8px; border: 1px solid #e2e8f0;
  background: #fff; color: #0f172a;
  font-size: 0.8125rem; font-weight: 500;
  font-family: 'Manrope', sans-serif;
  cursor: pointer; transition: all 0.15s;
  display: flex; align-items: center; justify-content: center;
}
.ccp-page-btn:hover { background: #f8fafc; border-color: #cbd5e1; }
.ccp-page-btn.active {
  background: #005ea4; color: #fff;
  border-color: transparent; font-weight: 700;
  box-shadow: 0 2px 6px rgba(0,94,164,0.25);
}

.ccp-ellipsis {
  padding: 0 6px; color: #94a3b8;
  font-size: 0.875rem; line-height: 36px;
  font-family: 'Manrope', sans-serif;
  user-select: none;
}
</style>