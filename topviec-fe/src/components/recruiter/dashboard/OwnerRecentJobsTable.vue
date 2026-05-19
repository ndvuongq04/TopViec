<template>
  <div class="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700/50 overflow-hidden">
    <div class="p-6 border-b border-slate-50 dark:border-slate-700/50 flex justify-between items-center">
      <h4 class="font-bold text-lg text-slate-800 dark:text-white">Tin tuyển dụng gần đây</h4>
      <router-link to="/recruiter/jobs" class="text-blue-600 text-sm font-bold hover:underline flex items-center gap-1">
        Xem tất cả
        <span class="material-symbols-outlined text-sm">arrow_forward</span>
      </router-link>
    </div>

    <div class="overflow-x-auto">
      <table class="w-full text-left">
        <thead>
          <tr class="bg-slate-50/50 dark:bg-slate-700/30 text-slate-400 text-[11px] uppercase font-black tracking-widest">
            <th class="px-6 py-4">Vị trí công việc</th>
            <th class="px-6 py-4">Trạng thái</th>
            <th class="px-6 py-4 text-center">Ứng viên</th>
            <th class="px-6 py-4">Ngày tạo</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-50 dark:divide-slate-700/50">
          <tr 
            v-for="job in jobs" 
            :key="job.jobId" 
            @click="onRowClick(job.jobId)"
            class="hover:bg-slate-50 dark:hover:bg-slate-700/30 transition-colors cursor-pointer group"
          >
            <td class="px-6 py-4">
              <span class="font-bold text-slate-700 dark:text-slate-200 text-sm group-hover:text-blue-600 transition-colors">
                {{ job.title }}
              </span>
            </td>
            <td class="px-6 py-4">
              <span class="px-2 py-1 rounded-lg text-[10px] font-black uppercase tracking-tight" 
                :class="statusClass(job.status)">
                {{ job.status }}
              </span>
            </td>
            <td class="px-6 py-4 text-center">
              <span class="font-bold text-slate-700 dark:text-slate-200 text-sm">{{ job.applicationCount }}</span>
            </td>
            <td class="px-6 py-4 text-slate-400 text-xs">
              {{ formatDate(job.createdAt) }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import type { RecentJobSummary } from '@/types/employerDashboard.types';

defineProps<{
  jobs: RecentJobSummary[];
}>();

const router = useRouter();

const onRowClick = (jobId: number) => {
  router.push(`/recruiter/jobs/${jobId}`);
};

const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleDateString('vi-VN');
};

const statusClass = (status: string) => {
  switch (status.toLowerCase()) {
    case 'published': return 'bg-emerald-100 text-emerald-600';
    case 'interviewing': return 'bg-blue-100 text-blue-600';
    case 'closed': return 'bg-slate-100 text-slate-500';
    default: return 'bg-amber-100 text-amber-600';
  }
};
</script>
