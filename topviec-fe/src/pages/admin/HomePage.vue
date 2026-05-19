<template>
  <div class="space-y-6">
    <div v-if="roleLoading" class="space-y-4">
      <div class="h-8 w-48 bg-slate-100 dark:bg-white/5 animate-pulse rounded-lg"></div>
      <div class="h-4 w-64 bg-slate-100 dark:bg-white/5 animate-pulse rounded-lg"></div>
    </div>
    
    <div v-else class="flex flex-col md:flex-row md:items-end justify-between gap-4">
      <div>
        <h2 class="text-3xl font-black text-slate-800 dark:text-white tracking-tight">
          Dashboard
        </h2>
        <p class="text-slate-500 text-sm mt-1">
          {{ subtitle }}
        </p>
      </div>

      <!-- Super Admin Role Switcher (Prompt 5 requirement) -->
      <div v-if="isSuperAdmin" class="flex items-center bg-slate-100 dark:bg-white/5 p-1 rounded-xl border border-slate-200 dark:border-white/10">
        <button 
          v-for="(label, role) in DASHBOARD_VIEWS" 
          :key="role"
          @click="activeView = role"
          class="px-4 py-2 text-xs font-bold rounded-lg transition-all"
          :class="activeView === role 
            ? 'bg-white dark:bg-white/10 text-[#963131] shadow-sm' 
            : 'text-slate-500 hover:text-slate-700 dark:hover:text-slate-300'"
        >
          {{ label }}
        </button>
      </div>
    </div>

    <!-- Dashboard Content -->
    <div v-if="roleLoading" class="space-y-8">
       <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
         <div v-for="i in 4" :key="i" class="h-32 bg-slate-100 dark:bg-white/5 animate-pulse rounded-xl"></div>
       </div>
    </div>

    <component 
      v-else
      :is="currentDashboardComponent" 
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useAdminPermission } from '@/composables/useAdminPermission';
import SuperAdminDashboard from '@/components/admin/dashboard/super-admin/SuperAdminDashboard.vue';
import ContentModeratorDashboard from '@/components/admin/dashboard/content-moderator/ContentModeratorDashboard.vue';
import SupportAdminDashboard from '@/components/admin/dashboard/support-admin/SupportAdminDashboard.vue';
import FinanceAdminDashboard from '@/components/admin/dashboard/finance-admin/FinanceAdminDashboard.vue';
import type { AdminRole } from '@/types/adminUser.types';

const { adminRole, loading: roleLoading, isSuperAdmin, init } = useAdminPermission();

const activeView = ref<AdminRole | null>(null);

const DASHBOARD_VIEWS: Record<AdminRole, string> = {
  super_admin: 'Hệ thống',
  content_moderator: 'Nội dung',
  support_admin: 'Hỗ trợ',
  finance_admin: 'Tài chính'
};

const subtitle = computed(() => {
  if (isSuperAdmin.value && activeView.value !== 'super_admin') {
    return `Đang xem chế độ: ${DASHBOARD_VIEWS[activeView.value as AdminRole]}`;
  }
  
  switch (adminRole.value) {
    case 'super_admin': return 'Chào mừng Quản trị viên hệ thống trở lại.';
    case 'content_moderator': return 'Theo dõi và xử lý các nội dung tin đăng, công ty.';
    case 'support_admin': return 'Quản lý khiếu nại và hỗ trợ người dùng.';
    case 'finance_admin': return 'Theo dõi doanh thu và các yêu cầu thanh toán.';
    default: return 'Chào mừng bạn đến với trang quản trị.';
  }
});

const currentDashboardComponent = computed(() => {
  const roleToShow = isSuperAdmin.value ? activeView.value : adminRole.value;
  
  switch (roleToShow) {
    case 'super_admin': return SuperAdminDashboard;
    case 'content_moderator': return ContentModeratorDashboard;
    case 'support_admin': return SupportAdminDashboard;
    case 'finance_admin': return FinanceAdminDashboard;
    default: return null;
  }
});

watch(adminRole, (newRole) => {
  if (newRole && !activeView.value) {
    activeView.value = newRole;
  }
}, { immediate: true });

onMounted(async () => {
  await init();
  if (adminRole.value) {
    activeView.value = adminRole.value;
  }
});
</script>