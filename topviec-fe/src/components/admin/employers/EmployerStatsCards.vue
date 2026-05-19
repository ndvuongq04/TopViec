<template>
  <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
    <div
      v-for="stat in stats"
      :key="stat.label"
      class="bg-white dark:bg-slate-900 p-4 rounded-xl border border-slate-200 dark:border-slate-800 flex items-center gap-4"
    >
      <div class="p-3 rounded-lg" :class="stat.iconBg">
        <span class="material-symbols-outlined" :class="stat.iconColor">{{ stat.icon }}</span>
      </div>
      <div>
        <p class="text-xs text-slate-500 uppercase font-bold tracking-wider">{{ stat.label }}</p>
        <p class="text-xl font-bold">{{ stat.value }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
export interface StatItem {
  label: string
  value: string | number
  icon: string
  iconBg: string
  iconColor: string
}

defineProps<{
  stats: StatItem[]
}>()
</script>
