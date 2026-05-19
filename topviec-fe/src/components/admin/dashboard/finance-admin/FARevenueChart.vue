<template>
  <div class="bg-white dark:bg-white/5 p-6 rounded-xl border border-slate-200 dark:border-white/10">
    <div class="flex justify-between items-center mb-6">
      <h4 class="font-bold text-lg">Doanh thu theo tháng</h4>
      <div class="text-xs text-slate-500">Đơn vị: VNĐ</div>
    </div>

    <div class="h-64 flex flex-col justify-between">
      <div class="relative flex-1">
        <svg class="w-full h-full" preserveAspectRatio="none" viewBox="0 0 400 100">
          <defs>
            <linearGradient id="revenueAreaGradient" x1="0" x2="0" y1="0" y2="1">
              <stop offset="0%" stop-color="#963131" stop-opacity="0.3" />
              <stop offset="100%" stop-color="#963131" stop-opacity="0" />
            </linearGradient>
          </defs>
          
          <!-- Area -->
          <path :d="areaPath" fill="url(#revenueAreaGradient)" />
          
          <!-- Line -->
          <path
            :d="linePath"
            fill="none"
            stroke="#963131"
            stroke-width="2.5"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
          
          <!-- Interaction Points -->
          <circle
            v-for="(pt, i) in points"
            :key="i"
            :cx="pt.x"
            :cy="pt.y"
            r="4"
            fill="#963131"
            class="cursor-pointer hover:r-6 transition-all"
          />
        </svg>

        <!-- Value markers on points (optional but helpful) -->
        <div v-for="(item, i) in data" :key="i" 
          class="absolute text-[9px] font-bold text-slate-500 dark:text-slate-400 -translate-x-1/2"
          :style="{ 
            left: `${pointLeft(i)}%`, 
            top: `${(100 - (item.totalAmount / maxRevenue) * 90) - 15}%` 
          }">
          {{ formatShortCurrency(item.totalAmount) }}
        </div>
      </div>

      <div class="flex justify-between text-[10px] font-bold text-slate-400 mt-4 uppercase tracking-tighter">
        <span v-for="item in data" :key="item.month" class="flex-1 text-center">{{ formatMonth(item.month) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { MonthlyRevenue } from '@/types/dashboard.types';

const props = defineProps<{
  data: MonthlyRevenue[];
}>();

const maxRevenue = computed(() => {
  const max = Math.max(...props.data.map(d => d.totalAmount), 1000000);
  return max * 1.2;
});

const points = computed(() => {
  if (props.data.length === 0) return [];
  const denominator = Math.max(props.data.length - 1, 1);
  return props.data.map((d, i) => ({
    x: (i / denominator) * 400,
    y: 100 - (d.totalAmount / maxRevenue.value) * 90,
  }));
});

const linePath = computed(() =>
  points.value.map((p, i) => `${i === 0 ? 'M' : 'L'}${p.x},${p.y}`).join(' ')
);

const areaPath = computed(() => {
  if (points.value.length === 0) return '';
  const line = linePath.value;
  const lastPoint = points.value[points.value.length - 1];
  if (!lastPoint) return '';
  return `${line} L${lastPoint.x},100 L0,100 Z`;
});

const formatMonth = (monthStr: string) => {
  const [year = '', month = monthStr] = monthStr.split('-');
  return `${month}/${year.substring(2)}`;
};

const pointLeft = (index: number) => {
  const denominator = Math.max(props.data.length - 1, 1);
  return (index / denominator) * 100;
};

const formatShortCurrency = (val: number) => {
  if (val >= 1000000000) return (val / 1000000000).toFixed(1) + 'B';
  if (val >= 1000000) return (val / 1000000).toFixed(0) + 'M';
  return val.toLocaleString();
};
</script>
