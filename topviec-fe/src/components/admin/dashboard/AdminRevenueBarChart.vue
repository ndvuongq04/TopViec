<template>
  <div class="bg-white dark:bg-white/5 p-6 rounded-xl border border-slate-200 dark:border-white/10">
    <div class="flex justify-between items-center mb-6">
      <h4 class="font-bold text-lg">Doanh thu 6 tháng gần nhất</h4>
      <div class="text-xs text-slate-500">Đơn vị: VNĐ</div>
    </div>

    <div class="h-64 flex flex-col justify-between">
      <div class="relative flex-1 flex items-end justify-between gap-2 px-2">
        <div
          v-for="(item, i) in data"
          :key="i"
          class="flex-1 flex flex-col items-center group relative"
        >
          <!-- Tooltip -->
          <div class="absolute bottom-full mb-2 opacity-0 group-hover:opacity-100 transition-opacity bg-slate-800 text-white text-[10px] py-1 px-2 rounded pointer-events-none whitespace-nowrap z-10">
            {{ formatFullCurrency(item.totalAmount) }}
          </div>
          
          <!-- Bar -->
          <div
            class="w-full bg-[#963131]/20 group-hover:bg-[#963131]/40 transition-colors rounded-t-md relative flex justify-center"
            :style="{ height: `${(item.totalAmount / maxRevenue) * 100}%` }"
          >
             <div class="absolute -top-6 text-[10px] font-bold text-slate-600 dark:text-slate-400">
               {{ formatShortCurrency(item.totalAmount) }}
             </div>
          </div>
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
  const max = Math.max(...props.data.map(d => d.totalAmount), 1);
  return max * 1.2; // Add some padding
});

const formatMonth = (monthStr: string) => {
  const month = monthStr.split('-')[1] ?? monthStr;
  return `Tháng ${Number.parseInt(month, 10) || month}`;
};

const formatShortCurrency = (val: number) => {
  if (val >= 1000000000) return (val / 1000000000).toFixed(1) + ' tỷ';
  if (val >= 1000000) return (val / 1000000).toFixed(0) + ' tr';
  return val.toLocaleString();
};

const formatFullCurrency = (val: number) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
};
</script>
