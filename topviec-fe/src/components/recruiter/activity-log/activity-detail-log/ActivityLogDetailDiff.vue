<template>
  <div class="space-y-6">
    <div v-if="changes.length > 0" class="divide-y divide-slate-100 dark:divide-slate-800">
      <div v-for="(change, idx) in changes" :key="idx" class="py-4 first:pt-0 last:pb-0">
        <div class="flex flex-col sm:flex-row sm:items-center gap-4">
          <div class="sm:w-1/4">
            <p class="text-xs font-bold text-slate-400 uppercase tracking-widest">{{ change.field }}</p>
          </div>
          <div class="flex-1 flex items-center gap-4">
            <div class="flex-1 p-3 rounded-lg bg-slate-50 dark:bg-slate-800/50 border border-slate-100 dark:border-slate-800 text-sm text-slate-500 line-through decoration-slate-300">
              {{ change.before.label }}
            </div>
            <span class="material-symbols-outlined text-slate-300">arrow_forward</span>
            <div class="flex-1 p-3 rounded-lg bg-primary/5 dark:bg-primary/10 border border-primary/20 text-sm font-bold text-primary">
              {{ change.after.label }}
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else class="flex flex-col items-center justify-center py-12 border-2 border-dashed border-slate-100 dark:border-slate-800 rounded-2xl">
      <div class="size-16 rounded-full bg-slate-50 dark:bg-slate-800 flex items-center justify-center mb-4">
        <span class="material-symbols-outlined text-3xl text-slate-300">compare_arrows</span>
      </div>
      <p class="text-slate-500 font-bold">Dữ liệu so sánh hiện chưa hỗ trợ</p>
      <p class="text-xs text-slate-400 mt-1 italic text-center max-w-xs">
        Hệ thống đang được nâng cấp để ghi lại chi tiết các thay đổi trước và sau khi thực hiện thao tác này.
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  changes: Array<{
    field: string
    before: { label: string; style?: string }
    after: { label: string; style?: string }
  }>
}>()
</script>