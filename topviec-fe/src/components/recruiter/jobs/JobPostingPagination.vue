<template>
  <div class="pagination-bar">
    <p class="pagination-bar__info">
      Hiển thị <strong>{{ rangeStart }}-{{ rangeEnd }}</strong>
      trong số <strong>{{ total }}</strong> tin tuyển dụng
    </p>
    <div class="pagination-controls">
      <button class="page-btn" :disabled="currentPage <= 0" @click="$emit('update:currentPage', currentPage - 1)">
        <span class="material-symbols-outlined icon-xl">chevron_left</span>
      </button>
      <button
        v-for="page in visiblePages"
        :key="page"
        class="page-btn"
        :class="{ 'page-btn--active': page === currentPage }"
        @click="$emit('update:currentPage', page)"
      >
        {{ page + 1 }}
      </button>
      <span v-if="showEllipsis" class="page-separator">...</span>
      <button class="page-btn" :disabled="currentPage >= totalPages - 1 || totalPages === 0" @click="$emit('update:currentPage', currentPage + 1)">
        <span class="material-symbols-outlined icon-xl">chevron_right</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  total: number
  currentPage: number
  perPage?: number
}>()

defineEmits<{ 'update:currentPage': [page: number] }>()

const perPage  = computed(() => props.perPage ?? 10)
const totalPages = computed(() => Math.ceil(props.total / perPage.value))

// Calculations for "Showing X-Y of Z"
const rangeStart = computed(() => {
  if (props.total === 0) return 0
  return props.currentPage * perPage.value + 1
})

const rangeEnd = computed(() => {
  return Math.min((props.currentPage + 1) * perPage.value, props.total)
})

const visiblePages = computed(() => {
  const pages: number[] = []
  // Show first few pages (0, 1, 2 ...)
  const maxToPulse = Math.min(totalPages.value, 3)
  for (let i = 0; i < maxToPulse; i++) {
    pages.push(i)
  }
  return pages
})

const showEllipsis = computed(() => totalPages.value > 3)
</script>

<style scoped>
.pagination-bar {
  padding: 1rem 1.5rem;
  background: rgba(248,250,252,0.3);
  border-top: 1px solid #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.pagination-bar__info { font-size: 0.875rem; font-weight: 500; color: var(--color-on-surface-muted); margin: 0; }
.pagination-bar__info strong { font-weight: 700; color: var(--color-on-surface); }

.pagination-controls { display: flex; align-items: center; gap: 0.25rem; }
.page-btn {
  width: 2rem;
  height: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-sm);
  border: none;
  background: none;
  font-size: 0.875rem;
  font-weight: 700;
  font-family: inherit;
  color: var(--color-on-surface);
  cursor: pointer;
  transition: background 0.15s;
}
.page-btn:hover          { background: #f1f5f9; }
.page-btn--active        { background: var(--color-primary); color: #fff; }
.page-btn--active:hover  { background: var(--color-primary); }
.page-btn:disabled       { opacity: 0.3; cursor: default; }
.page-btn:disabled:hover { background: none; }
.page-separator { color: #cbd5e1; margin: 0 0.25rem; font-size: 0.875rem; }
.icon-xl { font-size: 1.25rem !important; }
</style>
