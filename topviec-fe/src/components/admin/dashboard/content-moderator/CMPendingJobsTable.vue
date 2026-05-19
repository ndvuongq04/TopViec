<template>
  <div class="bg-white dark:bg-white/5 rounded-xl border border-slate-200 dark:border-white/10 overflow-hidden">
    <div class="p-6 border-b border-slate-100 dark:border-white/10 flex justify-between items-center">
      <h4 class="font-bold text-lg">Tin chờ duyệt lâu nhất</h4>
      <router-link to="/admin/moderation" class="text-[#963131] text-sm font-bold hover:underline flex items-center gap-1">
        Xem tất cả
        <span class="material-symbols-outlined text-sm">arrow_forward</span>
      </router-link>
    </div>

    <div class="overflow-x-auto">
      <table class="w-full text-left">
        <thead>
          <tr class="bg-slate-50 dark:bg-white/5 text-slate-500 text-xs uppercase tracking-wider">
            <th class="px-6 py-4 font-black">Tiêu đề tin</th>
            <th class="px-6 py-4 font-black">Công ty</th>
            <th class="px-6 py-4 font-black">Ngày đăng</th>
            <th class="px-6 py-4 font-black">Thời gian chờ</th>
            <th class="px-6 py-4 font-black">Thao tác</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-100 dark:divide-white/10">
          <tr v-for="job in jobs" :key="job.jobId" class="hover:bg-slate-50/50 dark:hover:bg-white/5 transition-colors">
            <td class="px-6 py-4">
              <span class="font-bold text-slate-700 dark:text-slate-200 text-sm line-clamp-1">{{ job.title }}</span>
            </td>
            <td class="px-6 py-4">
              <span class="text-slate-600 dark:text-slate-400 text-sm">{{ job.companyName || '-' }}</span>
            </td>
            <td class="px-6 py-4">
              <span class="text-slate-400 text-xs">{{ formatDate(job.createdAt) }}</span>
            </td>
            <td class="px-6 py-4">
              <span class="px-2 py-1 rounded text-[10px] font-bold" 
                :class="job.waitingDays >= 3 ? 'bg-rose-100 text-rose-600' : 'bg-amber-100 text-amber-600'">
                {{ job.waitingDays }} ngày
              </span>
            </td>
            <td class="px-6 py-4">
              <button @click="onView(job.jobId)" class="text-[#963131] hover:text-[#7a2828] text-sm font-bold flex items-center gap-1">
                <span class="material-symbols-outlined text-sm">visibility</span>
                Duyệt
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import type { OldestPendingJob } from '@/types/dashboard.types';

defineProps<{
  jobs: OldestPendingJob[];
}>();

const router = useRouter();

const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleDateString('vi-VN');
};

const onView = (jobId: number) => {
  router.push(`/admin/moderation/${jobId}`);
};
</script>
