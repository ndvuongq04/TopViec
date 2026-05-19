<template>
  <div class="px-6 py-4 border-t border-slate-200 dark:border-slate-800 flex items-center justify-between font-['Manrope']">
    <p class="text-sm text-slate-500 font-medium">
      Hiển thị {{ from }} - {{ to }} của {{ totalRecords }} bản ghi
    </p>

    <div class="flex items-center gap-1">
      <button
        class="p-1 rounded border border-slate-200 dark:border-slate-700 text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800 disabled:opacity-40 disabled:cursor-not-allowed transition-colors cursor-pointer"
        :disabled="currentPage <= 1"
        @click="$emit('change', currentPage - 1)"
      >
        <span class="material-symbols-outlined text-sm">chevron_left</span>
      </button>

      <template v-for="p in visiblePages" :key="p">
        <span v-if="p === '...'" class="px-2 text-slate-300 text-sm">...</span>
        <button
          v-else
          class="w-8 h-8 rounded text-sm font-extrabold border transition-colors cursor-pointer"
          :class="p === currentPage
            ? 'bg-primary text-white border-primary'
            : 'border-slate-200 dark:border-slate-700 text-slate-600 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800'"
          @click="$emit('change', p as number)"
        >
          {{ p }}
        </button>
      </template>

      <button
        class="p-1 rounded border border-slate-200 dark:border-slate-700 text-slate-600 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800 disabled:opacity-40 disabled:cursor-not-allowed transition-colors cursor-pointer"
        :disabled="currentPage >= totalPages"
        @click="$emit('change', currentPage + 1)"
      >
        <span class="material-symbols-outlined text-sm">chevron_right</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  currentPage: number
  totalRecords: number
  perPage: number
}>()

defineEmits<{ change: [page: number] }>()

const totalPages = computed(() => Math.ceil(props.totalRecords / props.perPage))
const from = computed(() => (props.currentPage - 1) * props.perPage + 1)
const to = computed(() => Math.min(props.currentPage * props.perPage, props.totalRecords))

const visiblePages = computed(() => {
  const pages: (number | string)[] = []
  const total = totalPages.value
  const cur = props.currentPage
  if (total <= 5) {
    for (let i = 1; i <= total; i++) pages.push(i)
  } else {
    pages.push(1)
    if (cur > 3) pages.push('...')
    for (let i = Math.max(2, cur - 1); i <= Math.min(total - 1, cur + 1); i++) pages.push(i)
    if (cur < total - 2) pages.push('...')
    pages.push(total)
  }
  return pages
})
</script>