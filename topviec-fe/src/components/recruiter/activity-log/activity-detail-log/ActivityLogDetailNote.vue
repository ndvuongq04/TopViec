<template>
  <div class="bg-amber-50 dark:bg-amber-900/10 border border-amber-100 dark:border-amber-900/30 rounded-xl p-6 relative overflow-hidden">
    <div class="absolute -right-4 -bottom-4 opacity-10">
      <span class="material-symbols-outlined text-8xl text-amber-500">sticky_note_2</span>
    </div>
    
    <div class="relative z-10">
      <div class="flex items-center gap-2 mb-3">
        <span class="material-symbols-outlined text-amber-500 text-lg">description</span>
        <h3 class="text-xs font-extrabold text-amber-600 dark:text-amber-400 uppercase tracking-widest">Mô tả chi tiết</h3>
      </div>
      <p class="text-slate-700 dark:text-slate-300 text-sm leading-relaxed font-medium">
        {{ note }}
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ note: string }>()
</script>