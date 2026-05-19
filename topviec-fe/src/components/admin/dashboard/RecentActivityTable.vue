<template>
  <div class="bg-white dark:bg-white/5 rounded-xl border border-slate-200 dark:border-white/10 overflow-hidden">
    <div class="p-6 border-b border-slate-100 dark:border-white/10 flex justify-between items-center">
      <h4 class="font-bold text-lg">Hoạt động quản trị gần đây</h4>
      <router-link to="/admin/audit-logs" class="text-[#963131] text-sm font-bold hover:underline flex items-center gap-1">
        Xem tất cả
        <span class="material-symbols-outlined text-sm">arrow_forward</span>
      </router-link>
    </div>

    <div class="overflow-x-auto">
      <table class="w-full text-left">
        <thead>
          <tr class="bg-slate-50 dark:bg-white/5 text-slate-500 text-xs uppercase tracking-wider">
            <th class="px-6 py-4 font-black">Quản trị viên</th>
            <th class="px-6 py-4 font-black">Hành động</th>
            <th class="px-6 py-4 font-black">Đối tượng</th>
            <th class="px-6 py-4 font-black">Thời gian</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-100 dark:divide-white/10">
          <tr v-for="(item, index) in activities" :key="item.id ?? `${item.createdAt}-${item.action}-${index}`" class="hover:bg-slate-50/50 dark:hover:bg-white/5 transition-colors">
            <td class="px-6 py-4">
              <div class="flex items-center gap-3">
                <div class="w-8 h-8 rounded-full bg-slate-100 dark:bg-white/10 flex items-center justify-center text-slate-500 font-bold text-xs">
                  {{ item.adminName.charAt(0) }}
                </div>
                <span class="font-bold text-slate-700 dark:text-slate-200 text-sm">{{ item.adminName }}</span>
              </div>
            </td>
            <td class="px-6 py-4">
              <span class="px-2 py-1 rounded-md bg-slate-100 dark:bg-white/10 text-slate-600 dark:text-slate-400 text-xs font-medium">
                {{ item.action }}
              </span>
            </td>
            <td class="px-6 py-4">
              <span class="text-slate-600 dark:text-slate-400 text-sm">{{ item.targetEntity || '-' }}</span>
            </td>
            <td class="px-6 py-4">
              <span class="text-slate-400 text-xs">{{ formatRelativeTime(item.createdAt) }}</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { RecentAdminActivity } from '@/types/dashboard.types';

defineProps<{
  activities: RecentAdminActivity[];
}>();

const formatRelativeTime = (dateStr: string) => {
  const date = new Date(dateStr);
  const now = new Date();
  const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);

  if (diffInSeconds < 60) return 'Vừa xong';
  if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)} phút trước`;
  if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)} giờ trước`;
  return `${Math.floor(diffInSeconds / 86400)} ngày trước`;
};
</script>
