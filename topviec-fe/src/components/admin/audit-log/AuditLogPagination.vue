<template>
  <div class="p-6 border-t border-slate-200 dark:border-slate-800 flex items-center justify-between">
    <p class="text-sm text-slate-500 font-medium">
      Hiển thị
      <span class="text-slate-900 dark:text-white font-bold">{{ rangeStart }}–{{ rangeEnd }}</span>
      trong số
      <span class="text-slate-900 dark:text-white font-bold">{{ total.toLocaleString() }}</span> kết quả
    </p>
    <div class="flex items-center gap-2">
      <!-- Prev -->
      <button
        class="size-8 flex items-center justify-center rounded-lg border border-slate-200 dark:border-slate-700 text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800 disabled:opacity-30 cursor-pointer"
        :disabled="current === 1"
        @click="$emit('change', current - 1)"
      >
        <span class="material-symbols-outlined text-xl leading-none">chevron_left</span>
      </button>

      <!-- Page numbers -->
      <template v-for="p in displayPages" :key="p">
        <span v-if="p === '...'" class="px-1 text-slate-400 text-sm">...</span>
        <button
          v-else
          class="size-8 flex items-center justify-center rounded-lg text-sm font-bold transition-colors cursor-pointer"
          :class="current === (p as number)
            ? 'bg-[#963131] text-white shadow-sm'
            : 'border border-slate-200 dark:border-slate-700 text-slate-600 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800'"
          @click="$emit('change', p as number)"
        >{{ p }}</button>
      </template>

      <!-- Next -->
      <button
        class="size-8 flex items-center justify-center rounded-lg border border-slate-200 dark:border-slate-700 text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800 disabled:opacity-30 cursor-pointer"
        :disabled="current === totalPages"
        @click="$emit('change', current + 1)"
      >
        <span class="material-symbols-outlined text-xl leading-none">chevron_right</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{ 
  current: number;    // 1-based
  totalPages: number; 
  total: number; 
  shown: number 
}>()

const emit = defineEmits<{ change: [page: number] }>()

const rangeStart = computed(() => {
  if (props.total === 0) return 0
  // Chúng ta không có pageSize trực tiếp, nhưng có thể ước lượng hoặc dùng shown
  // Tuy nhiên theo skill-Pagination: rangeStart = currentPage * pageSize + 1
  // Ở đây current là 1-based, nên currentPage = current - 1
  // Giả định pageSize = 20 (theo mặc định BE)
  return (props.current - 1) * 20 + 1
})

const rangeEnd = computed(() => {
  return Math.min(props.current * 20, props.total)
})

const displayPages = computed(() => {
  const total = props.totalPages
  const current = props.current
  const pages: (number | string)[] = []

  if (total <= 7) {
    for (let i = 1; i <= total; i++) pages.push(i)
  } else {
    pages.push(1)
    if (current > 4) pages.push('...')
    
    const start = Math.max(2, current - 1)
    const end = Math.min(total - 1, current + 1)
    
    for (let i = start; i <= end; i++) {
      if (!pages.includes(i)) pages.push(i)
    }
    
    if (current < total - 3) pages.push('...')
    if (!pages.includes(total)) pages.push(total)
  }
  
  return pages
})
</script>