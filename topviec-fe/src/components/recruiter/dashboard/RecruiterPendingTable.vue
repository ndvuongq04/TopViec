<template>
  <div class="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700/50 overflow-hidden">
    <div class="p-6 border-b border-slate-50 dark:border-slate-700/50 flex justify-between items-center">
      <h4 class="font-bold text-lg text-slate-800 dark:text-white">Ứng viên cần xử lý</h4>
      <router-link to="/recruiter/jobs" class="text-blue-600 text-sm font-bold hover:underline">
        Xem tất cả
      </router-link>
    </div>

    <div class="overflow-x-auto">
      <table class="w-full text-left">
        <thead>
          <tr class="bg-slate-50/50 dark:bg-slate-700/30 text-slate-400 text-[11px] uppercase font-black tracking-widest">
            <th class="px-6 py-4">Ứng viên</th>
            <th class="px-6 py-4">Vị trí</th>
            <th class="px-6 py-4">Trạng thái</th>
            <th class="px-6 py-4">Ngày nộp</th>
            <th class="px-6 py-4 text-right">Thao tác</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-50 dark:divide-slate-700/50">
          <tr 
            v-for="item in candidates" 
            :key="item.applicationId" 
            class="hover:bg-slate-50 dark:hover:bg-slate-700/30 transition-colors group"
          >
            <td class="px-6 py-4">
              <span class="font-bold text-slate-700 dark:text-slate-200 text-sm">{{ item.candidateName || '-' }}</span>
            </td>
            <td class="px-6 py-4">
              <span class="text-slate-600 dark:text-slate-400 text-sm line-clamp-1">{{ item.jobTitle || '-' }}</span>
            </td>
            <td class="px-6 py-4">
              <span class="px-2 py-1 rounded-lg text-[10px] font-black uppercase tracking-tight" 
                :class="statusClass(item.status)">
                {{ item.status }}
              </span>
            </td>
            <td class="px-6 py-4 text-slate-400 text-xs">
              {{ formatDate(item.createdAt) }}
            </td>
            <td class="px-6 py-4 text-right">
              <button @click="onView(item)" class="text-blue-600 hover:text-blue-700 text-xs font-bold flex items-center gap-1 ml-auto">
                <span class="material-symbols-outlined text-sm">visibility</span>
                Xem
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
import type { PendingCandidateDTO } from '@/types/employerDashboard.types';

defineProps<{
  candidates: PendingCandidateDTO[];
}>();

const router = useRouter();

const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleDateString('vi-VN');
};

const statusClass = (status: string) => {
  switch (status.toLowerCase()) {
    case 'pending': return 'bg-amber-100 text-amber-600';
    case 'seen': return 'bg-purple-100 text-purple-600';
    default: return 'bg-slate-100 text-slate-500';
  }
};

const onView = (item: PendingCandidateDTO) => {
  router.push(`/recruiter/jobs/${item.jobPostId}/applications/${item.applicationId}`);
};
</script>
