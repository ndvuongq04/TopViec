<template>
  <div class="bg-white dark:bg-slate-800 p-6 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700/50">
    <h4 class="font-bold text-lg text-slate-800 dark:text-white mb-6">Ứng viên theo tin phân công</h4>
    
    <div class="space-y-4">
      <div v-for="(item, i) in data" :key="item.jobId" class="space-y-2">
        <div class="flex justify-between text-xs font-bold">
          <span class="text-slate-600 dark:text-slate-400 truncate max-w-[80%]">{{ item.jobTitle || 'Tin tuyển dụng' }}</span>
          <span class="text-blue-600">{{ item.applicationCount }} UV</span>
        </div>
        
        <div class="h-3 w-full bg-slate-100 dark:bg-slate-700 rounded-full overflow-hidden relative">
          <!-- Animated Progress Bar -->
          <div 
            class="h-full bg-gradient-to-r from-blue-500 to-indigo-600 rounded-full transition-all duration-1000 ease-out"
            :style="{ width: `${(item.applicationCount / maxCount) * 100}%`, opacity: 1 - (i * 0.15) }"
          ></div>
        </div>
      </div>

      <div v-if="data.length === 0" class="py-12 text-center text-slate-400 text-sm">
        Chưa có tin tuyển dụng nào được phân công.
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { JobApplicationCount } from '@/types/employerDashboard.types';

const props = defineProps<{
  data: JobApplicationCount[];
}>();

const maxCount = computed(() => {
  const max = Math.max(...props.data.map(d => d.applicationCount), 0);
  return max * 1.1 || 10;
});
</script>
