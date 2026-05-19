<template>
  <div class="bg-white dark:bg-white/5 rounded-xl border border-slate-200 dark:border-white/10 overflow-hidden">
    <div class="p-6 border-b border-slate-100 dark:border-white/10 flex justify-between items-center">
      <h4 class="font-bold text-lg">Khiếu nại cần xử lý gấp</h4>
      <router-link to="/admin/reports" class="text-[#963131] text-sm font-bold hover:underline flex items-center gap-1">
        Xem tất cả
        <span class="material-symbols-outlined text-sm">arrow_forward</span>
      </router-link>
    </div>

    <div class="overflow-x-auto">
      <table class="w-full text-left">
        <thead>
          <tr class="bg-slate-50 dark:bg-white/5 text-slate-500 text-xs uppercase tracking-wider">
            <th class="px-6 py-4 font-black">Mã</th>
            <th class="px-6 py-4 font-black">Ưu tiên</th>
            <th class="px-6 py-4 font-black">Loại khiếu nại</th>
            <th class="px-6 py-4 font-black">Công ty bị khiếu nại</th>
            <th class="px-6 py-4 font-black">Chờ (giờ)</th>
            <th class="px-6 py-4 font-black">Hành động</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-100 dark:divide-white/10">
          <tr v-for="item in complaints" :key="item.complaintId" class="hover:bg-slate-50/50 dark:hover:bg-white/5 transition-colors">
            <td class="px-6 py-4">
              <span class="font-bold text-slate-700 dark:text-slate-200 text-sm">{{ item.reportCode }}</span>
            </td>
            <td class="px-6 py-4">
              <span class="px-2 py-1 rounded text-[10px] font-bold uppercase tracking-tight" 
                :class="priorityClass(item.priority)">
                {{ priorityLabel(item.priority) }}
              </span>
            </td>
            <td class="px-6 py-4">
              <span class="text-slate-600 dark:text-slate-400 text-sm">{{ item.complaintType }}</span>
            </td>
            <td class="px-6 py-4">
              <span class="text-slate-600 dark:text-slate-400 text-sm font-medium">{{ item.companyName || '-' }}</span>
            </td>
            <td class="px-6 py-4">
              <span class="text-slate-400 text-xs">{{ item.waitingHours }} giờ</span>
            </td>
            <td class="px-6 py-4">
              <button @click="onView(item.complaintId)" class="text-[#963131] hover:text-[#7a2828] text-sm font-bold flex items-center gap-1">
                <span class="material-symbols-outlined text-sm">visibility</span>
                Xử lý
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
import type { UrgentComplaint } from '@/types/dashboard.types';

defineProps<{
  complaints: UrgentComplaint[];
}>();

const router = useRouter();

const priorityLabel = (priority: string) => {
  switch (priority) {
    case 'urgent': return 'Khẩn cấp';
    case 'important': return 'Quan trọng';
    default: return 'Bình thường';
  }
};

const priorityClass = (priority: string) => {
  switch (priority) {
    case 'urgent': return 'bg-rose-100 text-rose-600';
    case 'important': return 'bg-amber-100 text-amber-600';
    default: return 'bg-blue-100 text-blue-600';
  }
};

const onView = (id: number) => {
  router.push(`/admin/reports/${id}`);
};
</script>
