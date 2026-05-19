<template>
  <div class="bg-white dark:bg-white/5 p-6 rounded-xl border border-slate-200 dark:border-white/10 hover:shadow-lg transition-shadow">
    <div class="flex justify-between items-start mb-4">
      <div class="p-2 bg-[#963131]/10 rounded-lg text-[#963131]">
        <span class="material-symbols-outlined">{{ icon }}</span>
      </div>
      <span class="text-xs font-bold px-2 py-1 rounded" :class="badgeClass">
        {{ badge }}
      </span>
    </div>
    <p class="text-slate-500 text-sm font-medium">{{ label }}</p>
    <h3 class="text-2xl font-bold mt-1">{{ value }}</h3>
    <p class="text-xs text-slate-400 mt-2">{{ note }}</p>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  icon: string
  label: string
  value: string | number
  badge: string
  note: string
  badgeType?: 'success' | 'warning' | 'info'
}>()

const badgeClass = computed(() => {
  switch (props.badgeType) {
    case 'warning': return 'text-amber-600 bg-amber-50'
    case 'info':    return 'text-blue-600 bg-blue-50'
    default:        return 'text-emerald-600 bg-emerald-50'
  }
})
</script>