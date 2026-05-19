<template>
  <div class="space-y-8 pt-6">
    <!-- Header with Profile Loading -->
    <div v-if="profileLoading" class="space-y-4">
      <div class="h-8 w-48 bg-slate-100 dark:bg-slate-700/50 animate-pulse rounded-lg"></div>
      <div class="h-4 w-64 bg-slate-100 dark:bg-slate-700/50 animate-pulse rounded-lg"></div>
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

      <!-- Role Switcher for OWNER (Prompt 5 requirement) -->
      <div v-if="isOwner" class="flex items-center bg-slate-100 dark:bg-slate-800 p-1 rounded-xl border border-slate-200 dark:border-slate-700/50">
        <button 
          v-for="(label, role) in DASHBOARD_VIEWS" 
          :key="role"
          @click="activeView = role"
          class="px-4 py-2 text-xs font-bold rounded-lg transition-all"
          :class="activeView === role 
            ? 'bg-white dark:bg-slate-700 text-blue-600 shadow-sm' 
            : 'text-slate-500 hover:text-slate-700 dark:hover:text-slate-300'"
        >
          {{ label }}
        </button>
      </div>
    </div>

    <!-- Dashboard Content Dispatcher -->
    <div v-if="profileLoading" class="space-y-8">
       <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
         <div v-for="i in 4" :key="i" class="h-28 bg-slate-100 dark:bg-slate-700/50 animate-pulse rounded-2xl"></div>
       </div>
       <div class="h-64 bg-slate-100 dark:bg-slate-700/50 animate-pulse rounded-2xl"></div>
    </div>

    <component 
      v-else
      :is="currentDashboardComponent" 
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useEmployerPermission } from '@/composables/useEmployerPermission';
import OwnerDashboard from '@/components/recruiter/dashboard/OwnerDashboard.vue';
import ManagerDashboard from '@/components/recruiter/dashboard/ManagerDashboard.vue';
import RecruiterDashboard from '@/components/recruiter/dashboard/RecruiterDashboard.vue';
import ViewerDashboard from '@/components/recruiter/dashboard/ViewerDashboard.vue';
import type { MemberRole } from '@/constants/companyMember.constants';

const { memberRole, loading: profileLoading, isOwner, init } = useEmployerPermission();

const activeView = ref<MemberRole | null>(null);

const DASHBOARD_VIEWS: Record<MemberRole, string> = {
  owner: 'Owner',
  manager: 'Manager',
  recruiter: 'Recruiter',
  viewer: 'Viewer'
};

const subtitle = computed(() => {
  const roleToShow = isOwner.value ? activeView.value : memberRole.value;
  
  if (isOwner.value && activeView.value !== 'owner') {
    return `Đang xem với vai trò: ${DASHBOARD_VIEWS[roleToShow as MemberRole]}`;
  }
  
  switch (roleToShow) {
    case 'owner': return 'Chào mừng Chủ sở hữu công ty trở lại.';
    case 'manager': return 'Quản lý hoạt động tuyển dụng toàn công ty.';
    case 'recruiter': return 'Theo dõi các tin tuyển dụng được phân công.';
    case 'viewer': return 'Xem báo cáo tổng quan công ty.';
    default: return 'Chào mừng bạn đến với trang quản trị công ty.';
  }
});

const currentDashboardComponent = computed(() => {
  const roleToShow = isOwner.value ? activeView.value : memberRole.value;
  
  switch (roleToShow) {
    case 'owner': return OwnerDashboard;
    case 'manager': return ManagerDashboard;
    case 'recruiter': return RecruiterDashboard;
    case 'viewer': return ViewerDashboard;
    default: return null;
  }
});

watch(memberRole, (newRole) => {
  if (newRole && !activeView.value) {
    activeView.value = newRole;
  }
}, { immediate: true });

onMounted(async () => {
  await init();
  if (memberRole.value) {
    activeView.value = memberRole.value;
  }
});
</script>