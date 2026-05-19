<template>
  <div class="bg-white dark:bg-white/5 p-6 rounded-xl border border-slate-200 dark:border-white/10">
    <h4 class="font-bold text-lg mb-6">Khiếu nại theo trạng thái</h4>
    
    <div class="flex flex-col md:flex-row items-center gap-8">
      <!-- Donut Chart -->
      <div class="relative w-48 h-48 rounded-full flex items-center justify-center shadow-inner" 
        :style="donutStyle">
        <div class="w-32 h-32 bg-white dark:bg-[#1e293b] rounded-full flex flex-col items-center justify-center shadow-lg">
          <span class="text-3xl font-black text-slate-800 dark:text-white">{{ totalCount }}</span>
          <span class="text-[10px] text-slate-400 uppercase font-bold">Tổng cộng</span>
        </div>
      </div>

      <!-- Legend -->
      <div class="flex-1 grid grid-cols-1 sm:grid-cols-2 gap-3 w-full">
        <div v-for="status in statusList" :key="status.key" class="flex items-center justify-between p-2 rounded-lg hover:bg-slate-50 dark:hover:bg-white/5 transition-colors">
          <div class="flex items-center gap-2">
            <span class="w-2.5 h-2.5 rounded-full" :style="{ backgroundColor: status.color }"></span>
            <span class="text-xs font-medium text-slate-600 dark:text-slate-400">{{ status.label }}</span>
          </div>
          <span class="text-xs font-bold text-slate-800 dark:text-white">{{ status.value }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps<{
  data: Record<string, number>;
}>();

const STATUS_CONFIG: Record<string, { label: string, color: string }> = {
  pending: { label: 'Chờ xử lý', color: '#3b82f6' },
  processing: { label: 'Đang xử lý', color: '#f97316' },
  waiting_employer: { label: 'Chờ NTD phản hồi', color: '#8b5cf6' },
  resolved: { label: 'Đã giải quyết', color: '#22c55e' },
  rejected: { label: 'Đã bác bỏ', color: '#ef4444' },
  auto_closed: { label: 'Tự động đóng', color: '#94a3b8' },
};

const totalCount = computed(() => Object.values(props.data).reduce((a, b) => a + b, 0));

const statusList = computed(() => {
  return Object.entries(STATUS_CONFIG).map(([key, config]) => ({
    key,
    ...config,
    value: props.data[key] || 0
  }));
});

const donutStyle = computed(() => {
  if (totalCount.value === 0) {
    return {
      background: 'conic-gradient(#e2e8f0 0% 100%)',
    };
  }

  let currentPercentage = 0;
  const gradients = statusList.value
    .filter(s => s.value > 0)
    .map(status => {
      const percentage = (status.value / totalCount.value) * 100;
      const start = currentPercentage;
      currentPercentage += percentage;
      return `${status.color} ${start}% ${currentPercentage}%`;
    });
  
  return {
    background: `conic-gradient(${gradients.join(', ')})`
  };
});
</script>
