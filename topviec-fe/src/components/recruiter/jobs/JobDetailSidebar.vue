<template>
  <div class="space-y-6">
    <!-- Thông tin chung -->
    <section class="bg-white dark:bg-slate-800 p-6 rounded-2xl border border-slate-200 dark:border-slate-700 shadow-sm">
      <h3 class="text-base font-bold text-slate-900 dark:text-white mb-6">Thông tin chung</h3>
      <div class="space-y-5">
        <div
          v-for="(info, i) in generalInfo"
          :key="i"
          class="flex justify-between items-center"
          :class="i < generalInfo.length - 1 ? 'pb-4 border-b border-slate-200 dark:border-slate-700' : ''"
        >
          <div class="flex items-center gap-2 text-slate-500">
            <span class="material-symbols-outlined text-sm">{{ info.icon }}</span>
            <span class="text-sm">{{ info.label }}</span>
          </div>
          <span class="text-sm font-semibold" :class="info.valueClass ?? 'text-slate-900 dark:text-slate-100'">
            {{ info.value }}
          </span>
        </div>
      </div>
    </section>

    <!-- Mốc thời gian -->
    <section class="bg-white dark:bg-slate-800 p-6 rounded-2xl border border-slate-200 dark:border-slate-700 shadow-sm">
      <h3 class="text-base font-bold text-slate-900 dark:text-white mb-6">Mốc thời gian</h3>
      <div class="space-y-6 relative before:absolute before:left-2 before:top-2 before:bottom-2 before:w-0.5 before:bg-slate-200 dark:before:bg-slate-700">
        <div
          v-for="(event, i) in timeline"
          :key="i"
          class="relative pl-8"
        >
          <div
            class="absolute left-0 top-1 w-4 h-4 rounded-full border-4 border-white dark:border-slate-800 shadow-sm"
            :class="event.dotClass ?? 'bg-primary'"
          ></div>
          <p class="text-xs text-slate-500 uppercase tracking-wider font-bold">{{ event.label }}</p>
          <p class="text-sm font-medium mt-1" :class="event.valueClass ?? ''">{{ event.value }}</p>
        </div>
      </div>
    </section>

    <!-- Chỉ số chuyển đổi -->
    <section class="bg-sky-50 dark:bg-sky-900/20 p-6 rounded-2xl border border-primary/20">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-sm font-bold text-sky-800 dark:text-sky-300">Chỉ số chuyển đổi</h3>
        <span class="text-[10px] bg-white dark:bg-slate-800 px-2 py-0.5 rounded-full font-bold text-primary">
          {{ conversionRate }}
        </span>
      </div>
      <div class="h-2 w-full bg-white/40 dark:bg-slate-700 rounded-full overflow-hidden mb-2">
        <div class="h-full bg-primary rounded-full transition-all" :style="{ width: progressWidth }"></div>
      </div>
      <p class="text-[10px] text-sky-700/80 dark:text-sky-400/80 italic">{{ conversionNote }}</p>
    </section>
  </div>
</template>

<script setup lang="ts">
export interface GeneralInfoItem {
  icon: string
  label: string
  value: string
  valueClass?: string
}

export interface TimelineEvent {
  label: string
  value: string
  dotClass?: string
  valueClass?: string
}

defineProps<{
  generalInfo: GeneralInfoItem[]
  timeline: TimelineEvent[]
  conversionRate: string
  progressWidth: string
  conversionNote: string
}>()
</script>
