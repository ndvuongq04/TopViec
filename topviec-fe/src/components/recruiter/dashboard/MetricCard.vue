<template>
  <div class="bg-white dark:bg-slate-800 p-6 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700/50">
    <div class="flex justify-between items-start mb-4">
      <!-- Icon -->
      <div class="p-3 rounded-xl" :class="iconBgClass">
        <span class="material-symbols-outlined" :class="iconColorClass">{{ icon }}</span>
      </div>
      <!-- Trend badge -->
      <span
        v-if="trend !== undefined"
        class="text-sm font-bold flex items-center px-2 py-1 rounded-lg"
        :class="trend >= 0
          ? 'text-green-500 bg-green-50 dark:bg-green-900/20'
          : 'text-red-500 bg-red-50 dark:bg-red-900/20'"
      >
        {{ trend >= 0 ? '+' : '' }}{{ trend }}%
        <span class="material-symbols-outlined text-xs ml-1">
          {{ trend >= 0 ? 'trending_up' : 'trending_down' }}
        </span>
      </span>
    </div>
    <p class="text-slate-500 dark:text-slate-400 text-sm font-medium">{{ label }}</p>
    <h3 class="text-3xl font-extrabold mt-1">{{ formattedValue }}</h3>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  icon: string
  label: string
  value: number | string
  trend?: number
  iconBgClass?: string
  iconColorClass?: string
}>(), {
  iconBgClass: 'bg-primary/10',
  iconColorClass: 'text-primary',
})

const formattedValue = computed(() =>
  typeof props.value === 'number'
    ? props.value.toLocaleString('vi-VN')
    : props.value
)
</script>