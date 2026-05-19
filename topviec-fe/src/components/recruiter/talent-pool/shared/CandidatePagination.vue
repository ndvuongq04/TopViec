<template>
  <div :class="['pagination', centered && 'pagination--centered']">
    <span v-if="showInfo" class="pagination__info">
      Hiển thị {{ from }} - {{ to }} của {{ total?.toLocaleString() }}
    </span>
    <div class="pagination__controls">
      <button class="pagination__btn" :disabled="current === 1" @click="$emit('change', current - 1)">
        <span class="material-symbols-outlined">chevron_left</span>
      </button>
      <template v-for="p in displayPages" :key="p">
        <span v-if="p === '...'" class="pagination__btn pagination__btn--dots">...</span>
        <button
          v-else
          :class="['pagination__btn', p === current && 'pagination__btn--active']"
          @click="$emit('change', Number(p))"
        >
          {{ p }}
        </button>
      </template>
      <button class="pagination__btn" :disabled="current === totalPages" @click="$emit('change', current + 1)">
        <span class="material-symbols-outlined">chevron_right</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  current: number
  totalPages: number
  total?: number
  pageSize?: number
  showInfo?: boolean
  centered?: boolean
}>()

defineEmits<{ change: [page: number] }>()

const from = computed(() => (props.current - 1) * (props.pageSize ?? 10) + 1)
const to = computed(() => Math.min(props.current * (props.pageSize ?? 10), props.total ?? 0))

const displayPages = computed(() => {
  const pages: (number | string)[] = []
  const total = props.totalPages
  if (total <= 5) {
    for (let i = 1; i <= total; i++) pages.push(i)
  } else {
    pages.push(1)
    if (props.current > 3) pages.push('...')
    for (let i = Math.max(2, props.current - 1); i <= Math.min(total - 1, props.current + 1); i++) pages.push(i)
    if (props.current < total - 2) pages.push('...')
    pages.push(total)
  }
  return pages
})
</script>

<style scoped>
.pagination {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.pagination--centered {
  justify-content: center;
}

.pagination__info {
  color: #64748b;
  font-size: 0.875rem;
  font-weight: 600;
}

.pagination__controls {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination__btn {
  display: flex;
  min-width: 32px;
  height: 32px;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
  border: none;
  border-radius: 0.375rem;
  background: #f1f4f9;
  color: #64748b;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  font-size: 0.875rem;
  font-weight: 600;
  transition: background 0.15s;
}

.pagination__btn:hover:not(:disabled):not(.pagination__btn--active):not(.pagination__btn--dots) {
  background: #e9edf5;
}

.pagination__btn--active {
  background: #4b9af6;
  box-shadow: 0 1px 4px rgba(75, 154, 246, 0.3);
  color: #fff;
  font-weight: 700;
}

.pagination__btn--dots {
  background: none;
  cursor: default;
}

.pagination__btn:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.pagination__btn .material-symbols-outlined {
  font-size: 1.125rem;
}
</style>
