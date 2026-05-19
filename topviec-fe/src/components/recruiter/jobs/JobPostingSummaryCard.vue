<template>
  <div
    class="bg-white dark:bg-slate-900 p-6 rounded-2xl border shadow-sm flex items-center gap-5 hover:shadow-md transition-shadow cursor-default group"
    :class="borderClass"
  >
    <div class="w-14 h-14 rounded-2xl flex items-center justify-center shrink-0 transition-transform group-hover:scale-105" :class="iconBgClass">
      <span class="material-symbols-outlined text-2xl" :class="iconColorClass" style="font-variation-settings: 'FILL' 1;">{{ icon }}</span>
    </div>
    <div class="flex-1 min-w-0">
      <p class="text-xs font-bold uppercase tracking-widest truncate" :class="labelClass">{{ label }}</p>
      <p class="text-3xl font-black mt-0.5" :class="valueClass">{{ formattedValue }}</p>
    </div>
    <div v-if="trend !== undefined" class="shrink-0 text-right">
      <span
        class="text-[10px] font-bold px-2 py-1 rounded-full"
        :class="trend >= 0 ? 'bg-emerald-50 text-emerald-600' : 'bg-red-50 text-red-500'"
      >
        {{ trend >= 0 ? '+' : '' }}{{ trend }}
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  icon: string
  label: string
  value: number
  color?: 'primary' | 'green' | 'amber' | 'red' | 'purple'
  trend?: number
}>()

const formattedValue = computed(() => props.value.toLocaleString('vi-VN'))

const colorMap = {
  primary: {
    border: 'border-primary/20',
    iconBg: 'bg-primary/10',
    iconColor: 'text-primary',
    label: 'text-primary/70',
    value: 'text-primary',
  },
  green: {
    border: 'border-emerald-200',
    iconBg: 'bg-emerald-50',
    iconColor: 'text-emerald-600',
    label: 'text-emerald-600/70',
    value: 'text-emerald-700',
  },
  amber: {
    border: 'border-amber-200',
    iconBg: 'bg-amber-50',
    iconColor: 'text-amber-600',
    label: 'text-amber-600/70',
    value: 'text-amber-700',
  },
  red: {
    border: 'border-red-200',
    iconBg: 'bg-red-50',
    iconColor: 'text-red-500',
    label: 'text-red-500/70',
    value: 'text-red-600',
  },
  purple: {
    border: 'border-purple-200',
    iconBg: 'bg-purple-50',
    iconColor: 'text-purple-600',
    label: 'text-purple-600/70',
    value: 'text-purple-700',
  },
}

const scheme = computed(() => colorMap[props.color ?? 'primary'])
const borderClass = computed(() => scheme.value.border)
const iconBgClass = computed(() => scheme.value.iconBg)
const iconColorClass = computed(() => scheme.value.iconColor)
const labelClass = computed(() => scheme.value.label)
const valueClass = computed(() => scheme.value.value)
</script>
