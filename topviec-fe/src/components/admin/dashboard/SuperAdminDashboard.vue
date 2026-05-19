<template>
  <div class="space-y-8">
    <!-- KPI Cards -->
    <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
       <div v-for="i in 4" :key="i" class="h-32 bg-slate-100 dark:bg-white/5 animate-pulse rounded-xl"></div>
    </div>
    <div v-else-if="superAdminData" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <AdminKpiCard
        icon="group"
        label="Tổng người dùng"
        :value="superAdminData.totalActiveUsers.toLocaleString()"
        badge="Active"
        note="Candidate + Employer đang hoạt động"
      />
      <AdminKpiCard
        icon="corporate_fare"
        label="Tổng công ty"
        :value="superAdminData.totalActiveCompanies.toLocaleString()"
        badge="Active"
        note="Công ty đang hoạt động"
      />
      <AdminKpiCard
        icon="work"
        label="Tin đang tuyển"
        :value="superAdminData.totalPublishedJobs.toLocaleString()"
        badge="Live"
        badge-type="info"
        note="Tin published + interviewing"
      />
      <AdminKpiCard
        icon="monetization_on"
        label="Doanh thu tháng"
        :value="formatCurrency(superAdminData.monthlyRevenue)"
        badge="Tháng này"
        badge-type="success"
        note="Doanh thu đơn PAID tháng này"
      />
    </div>

    <!-- Charts -->
    <div v-if="loading" class="grid grid-cols-1 lg:grid-cols-2 gap-8">
       <div class="h-80 bg-slate-100 dark:bg-white/5 animate-pulse rounded-xl"></div>
       <div class="h-80 bg-slate-100 dark:bg-white/5 animate-pulse rounded-xl"></div>
    </div>
    <div v-else-if="superAdminData" class="grid grid-cols-1 lg:grid-cols-2 gap-8">
      <AdminUserGrowthChart :data="superAdminData.userGrowth" />
      <AdminRevenueBarChart :data="superAdminData.revenueByMonth" />
    </div>

    <!-- Recent Activities -->
    <div v-if="loading" class="h-96 bg-slate-100 dark:bg-white/5 animate-pulse rounded-xl"></div>
    <RecentActivityTable v-else-if="superAdminData" :activities="superAdminData.recentActivities" />
    
    <div v-if="error" class="p-8 text-center bg-rose-50 dark:bg-rose-500/10 rounded-xl border border-rose-100 dark:border-rose-500/20">
      <span class="material-symbols-outlined text-rose-500 text-4xl mb-2">error</span>
      <p class="text-rose-600 dark:text-rose-400 font-medium">{{ error }}</p>
      <button @click="store.fetchSuperAdminDashboard()" class="mt-4 px-4 py-2 bg-rose-600 text-white rounded-lg text-sm font-bold hover:bg-rose-700 transition-colors">
        Thử lại
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed } from 'vue';
import { useAdminDashboardStore } from '@/stores/adminDashboard.store';
import AdminKpiCard from '@/components/admin/dashboard/AdminKpiCard.vue';
import AdminUserGrowthChart from '@/components/admin/dashboard/AdminUserGrowthChart.vue';
import AdminRevenueBarChart from '@/components/admin/dashboard/AdminRevenueBarChart.vue';
import RecentActivityTable from '@/components/admin/dashboard/RecentActivityTable.vue';

const store = useAdminDashboardStore();
const superAdminData = computed(() => store.superAdminData);
const loading = computed(() => store.loading);
const error = computed(() => store.error);

const formatCurrency = (val: number) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
};

onMounted(() => {
  if (!superAdminData.value) {
    store.fetchSuperAdminDashboard();
  }
});
</script>
