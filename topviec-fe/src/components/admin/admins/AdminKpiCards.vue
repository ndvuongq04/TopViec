<template>
  <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
    <div
      v-for="kpi in kpis"
      :key="kpi.label"
      class="bg-white dark:bg-slate-900 p-6 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm"
    >
      <div class="flex justify-between items-start">
        <div>
          <p class="text-sm text-slate-500 dark:text-slate-400 font-medium">{{ kpi.label }}</p>
          <h3 class="text-2xl font-bold mt-1">{{ kpi.value }}</h3>
        </div>
        <div class="p-2 rounded-lg" :class="[kpi.iconBg]">
          <span class="material-symbols-outlined" :class="kpi.iconColor">{{ kpi.icon }}</span>
        </div>
      </div>
      <div class="mt-4 flex items-center gap-1 text-sm" :class="kpi.noteColor">
        <span
          class="material-symbols-outlined text-xs"
          :class="kpi.noteIconClass ?? kpi.noteColor"
        >{{ kpi.noteIcon }}</span>
        <span>{{ kpi.note }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
export interface KpiItem {
  label: string
  value: number | string
  note: string
  noteColor: string
  noteIcon: string
  noteIconClass?: string
  iconBg: string
  iconColor: string
  icon: string
}

defineProps<{ kpis: KpiItem[] }>()
</script>