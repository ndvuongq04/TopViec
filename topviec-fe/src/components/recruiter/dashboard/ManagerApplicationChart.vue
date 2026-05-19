<template>
  <div class="bg-white dark:bg-slate-800 p-6 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700/50">
    <h4 class="font-bold text-lg text-slate-800 dark:text-white mb-6">Đơn ứng tuyển theo trạng thái</h4>
    
    <div class="flex flex-col md:flex-row items-center gap-12">
      <!-- Donut Chart -->
      <div class="relative w-48 h-48 rounded-full flex items-center justify-center" 
        :style="donutStyle">
        <!-- Inner Circle for Donut effect -->
        <div class="w-32 h-32 bg-white dark:bg-slate-800 rounded-full flex flex-col items-center justify-center shadow-sm">
          <span class="text-3xl font-black text-slate-800 dark:text-white">{{ totalCount }}</span>
          <span class="text-[10px] text-slate-400 uppercase font-bold tracking-widest">Tổng đơn</span>
        </div>
      </div>

      <!-- Legend -->
      <div class="flex-1 grid grid-cols-1 sm:grid-cols-2 gap-4 w-full">
        <div v-for="status in statusList" :key="status.key" class="flex items-center justify-between p-2 rounded-xl hover:bg-slate-50 dark:hover:bg-slate-700/30 transition-colors">
          <div class="flex items-center gap-3">
            <span class="w-3 h-3 rounded-full shadow-sm" :style="{ backgroundColor: status.color }"></span>
            <span class="text-xs font-bold text-slate-600 dark:text-slate-400">{{ status.label }}</span>
          </div>
          <span class="text-xs font-black text-slate-800 dark:text-white bg-slate-100 dark:bg-slate-700 px-2 py-0.5 rounded-lg">{{ status.value }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { EmployerApplicationStatusCounts } from '@/types/employerDashboard.types';

const props = defineProps<{
  data: EmployerApplicationStatusCounts;
}>();

type StatusConfig = { label: string, color: string };

const STATUS_CONFIG: Record<keyof EmployerApplicationStatusCounts, StatusConfig> = {
  pending: { label: 'Chờ xử lý', color: '#3b82f6' },
  seen: { label: 'Đã xem', color: '#8b5cf6' },
  cv_passed: { label: 'Duyệt CV', color: '#22c55e' },
  interviewing: { label: 'Đang phỏng vấn', color: '#f97316' },
  hired: { label: 'Đã tuyển', color: '#10b981' },
  rejected: { label: 'Từ chối', color: '#ef4444' },
};

const totalCount = computed(() => Object.values(props.data).reduce((a, b) => a + b, 0));

const statusList = computed(() => {
  return (Object.entries(STATUS_CONFIG) as Array<[keyof EmployerApplicationStatusCounts, StatusConfig]>).map(([key, config]) => ({
    key,
    ...config,
    value: props.data[key] || 0
  }));
});

const donutStyle = computed(() => {
  let currentPercentage = 0;
  const gradients = statusList.value
    .filter(s => s.value > 0)
    .map(status => {
      const percentage = (status.value / totalCount.value) * 100;
      const start = currentPercentage;
      currentPercentage += percentage;
      return `${status.color} ${start}% ${currentPercentage}%`;
    });
  
  if (gradients.length === 0) return { background: '#f1f5f9' };
  
  return {
    background: `conic-gradient(${gradients.join(', ')})`
  };
});
</script>
