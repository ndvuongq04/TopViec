<template>
  <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 font-['Manrope']">
    <div
      v-for="stat in stats"
      :key="stat.label"
      class="bg-white dark:bg-slate-900 p-6 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm transition-transform hover:-translate-y-1 hover:shadow-lg hover:shadow-primary/5 cursor-default"
    >
      <div class="flex items-center gap-4">
        <div class="p-3 rounded-lg" :class="stat.iconBg">
          <span class="material-symbols-outlined" :class="stat.iconColor">{{ stat.icon }}</span>
        </div>
        <div>
          <p class="text-xs font-bold uppercase tracking-wider text-slate-500">{{ stat.label }}</p>
          <h3 class="text-[1.875rem] font-extrabold leading-tight text-slate-900 dark:text-white">{{ stat.value }}</h3>
          <p v-if="stat.note" class="text-xs text-slate-400 mt-1 font-medium">{{ stat.note }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  stats: Array<{
    label: string; value: number | string; icon: string
    iconBg: string; iconColor: string
    note?: string
  }>
}>()
</script>