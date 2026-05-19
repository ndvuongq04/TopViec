<template>
  <div class="bg-white dark:bg-slate-800 p-6 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700/50">
    <div class="flex justify-between items-center mb-6">
      <h4 class="font-bold text-lg text-slate-800 dark:text-white">Ứng viên theo tuần</h4>
      <div class="text-xs text-slate-400 font-medium">4 tuần gần nhất</div>
    </div>

    <div class="h-64 flex flex-col justify-between">
      <div class="relative flex-1 flex items-end justify-around gap-4 px-2">
        <div
          v-for="(item, i) in data"
          :key="i"
          class="flex-1 flex flex-col items-center group relative h-full justify-end"
        >
          <!-- Value on top -->
          <div class="mb-2 text-xs font-bold text-slate-600 dark:text-slate-400 opacity-0 group-hover:opacity-100 transition-opacity">
            {{ item.count }}
          </div>
          
          <!-- Bar -->
          <div
            class="w-full max-w-[60px] bg-blue-500/10 group-hover:bg-blue-500/30 transition-all rounded-t-xl relative overflow-hidden"
            :style="{ height: `${(item.count / maxVal) * 100}%` }"
          >
            <!-- Gradient Overlay -->
            <div class="absolute inset-0 bg-gradient-to-t from-blue-500/20 to-transparent"></div>
          </div>
        </div>
      </div>

      <div class="flex justify-around text-[10px] font-bold text-slate-400 mt-4 uppercase tracking-wider">
        <span v-for="item in data" :key="item.weekLabel" class="flex-1 text-center">{{ item.weekLabel }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { WeeklyApplicationStat } from '@/types/employerDashboard.types';

const props = defineProps<{
  data: WeeklyApplicationStat[];
}>();

const maxVal = computed(() => {
  const max = Math.max(...props.data.map(d => d.count), 0);
  return max * 1.2 || 10;
});
</script>
