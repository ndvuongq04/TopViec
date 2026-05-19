<template>
  <div class="bg-white dark:bg-white/5 p-6 rounded-xl border border-slate-200 dark:border-white/10">
    <div class="flex justify-between items-center mb-6">
      <h4 class="font-bold text-lg">Tin xử lý theo ngày (7 ngày qua)</h4>
      <div class="flex items-center gap-4">
        <div class="flex items-center gap-1.5">
          <span class="w-3 h-3 rounded-full bg-emerald-500"></span>
          <span class="text-xs text-slate-500">Duyệt</span>
        </div>
        <div class="flex items-center gap-1.5">
          <span class="w-3 h-3 rounded-full bg-rose-500"></span>
          <span class="text-xs text-slate-500">Từ chối</span>
        </div>
      </div>
    </div>

    <div class="h-64 flex flex-col justify-between">
      <div class="relative flex-1 flex items-end justify-between gap-4 px-2">
        <div
          v-for="(item, i) in data"
          :key="i"
          class="flex-1 flex items-end gap-1 h-full group relative"
        >
          <!-- Approved Bar -->
          <div
            class="flex-1 bg-emerald-500/20 group-hover:bg-emerald-500/40 transition-colors rounded-t-sm relative"
            :style="{ height: `${(item.approvedCount / maxValue) * 100}%` }"
          >
             <div class="absolute -top-5 left-1/2 -translate-x-1/2 text-[9px] font-bold text-emerald-600 dark:text-emerald-400 opacity-0 group-hover:opacity-100 transition-opacity">
               {{ item.approvedCount }}
             </div>
          </div>
          
          <!-- Rejected Bar -->
          <div
            class="flex-1 bg-rose-500/20 group-hover:bg-rose-500/40 transition-colors rounded-t-sm relative"
            :style="{ height: `${(item.rejectedCount / maxValue) * 100}%` }"
          >
             <div class="absolute -top-5 left-1/2 -translate-x-1/2 text-[9px] font-bold text-rose-600 dark:text-rose-400 opacity-0 group-hover:opacity-100 transition-opacity">
               {{ item.rejectedCount }}
             </div>
          </div>

          <!-- Tooltip on hover -->
          <div class="absolute bottom-full mb-6 left-1/2 -translate-x-1/2 opacity-0 group-hover:opacity-100 transition-opacity bg-slate-800 text-white text-[10px] py-1 px-2 rounded pointer-events-none whitespace-nowrap z-10 shadow-xl">
            {{ formatDate(item.date) }}: {{ item.approvedCount }} Duyệt, {{ item.rejectedCount }} Từ chối
          </div>
        </div>
      </div>

      <div class="flex justify-between text-[10px] font-bold text-slate-400 mt-4 uppercase tracking-tighter">
        <span v-for="item in data" :key="item.date" class="flex-1 text-center">{{ formatDay(item.date) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { DailyModerationStat } from '@/types/dashboard.types';

const props = defineProps<{
  data: DailyModerationStat[];
}>();

const maxValue = computed(() => {
  const allValues = props.data.flatMap(d => [d.approvedCount, d.rejectedCount]);
  return Math.max(...allValues, 10) * 1.2;
});

const formatDay = (dateStr: string) => {
  const date = new Date(dateStr);
  return `${date.getDate()}/${date.getMonth() + 1}`;
};

const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleDateString('vi-VN');
};
</script>
