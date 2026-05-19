<template>
  <div class="space-y-8">
    <!-- KPI Cards -->
    <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
       <div v-for="i in 4" :key="i" class="h-28 bg-slate-100 dark:bg-slate-700/50 animate-pulse rounded-2xl"></div>
    </div>
    <div v-else-if="ownerData" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <MetricCard
        icon="work"
        label="Tin đang tuyển"
        :value="ownerData.activeJobs"
        :trend="5"
        icon-bg-class="bg-blue-100 dark:bg-blue-500/10"
        icon-color-class="text-blue-600 dark:text-blue-400"
      />
      <MetricCard
        icon="description"
        label="Ứng viên mới (tháng)"
        :value="ownerData.newApplicationsThisMonth"
        icon-bg-class="bg-purple-100 dark:bg-purple-500/10"
        icon-color-class="text-purple-600 dark:text-purple-400"
      />
      <MetricCard
        icon="groups"
        label="Nhân sự công ty"
        :value="ownerData.activeMembers"
        icon-bg-class="bg-emerald-100 dark:bg-emerald-500/10"
        icon-color-class="text-emerald-600 dark:text-emerald-400"
      />
      <MetricCard
        icon="card_membership"
        label="Gói dịch vụ active"
        :value="ownerData.activeSubscriptions"
        icon-bg-class="bg-amber-100 dark:bg-amber-500/10"
        icon-color-class="text-amber-600 dark:text-amber-400"
      />
    </div>

    <!-- Charts & Summary Row -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <div class="lg:col-span-2">
        <div v-if="loading" class="h-80 bg-slate-100 dark:bg-slate-700/50 animate-pulse rounded-2xl"></div>
        <OwnerWeeklyChart v-else-if="ownerData" :data="ownerData.weeklyApplications" />
      </div>
      
      <!-- Side Summary Placeholder -->
      <div class="space-y-6">
        <div class="bg-gradient-to-br from-blue-600 to-indigo-700 p-6 rounded-2xl text-white shadow-lg shadow-blue-500/20">
          <h5 class="font-bold text-lg mb-2">Tăng trưởng nhanh</h5>
          <p class="text-blue-100 text-sm leading-relaxed mb-4">
            Số lượng ứng viên trong tháng này tăng 15% so với tháng trước. Hãy kiểm tra các tin tuyển dụng hot!
          </p>
          <button class="w-full py-2 bg-white/10 hover:bg-white/20 transition-colors rounded-xl text-sm font-bold border border-white/20">
            Xem báo cáo chi tiết
          </button>
        </div>
      </div>
    </div>

    <!-- Recent Jobs -->
    <div v-if="loading" class="h-96 bg-slate-100 dark:bg-slate-700/50 animate-pulse rounded-2xl"></div>
    <OwnerRecentJobsTable v-else-if="ownerData" :jobs="ownerData.recentJobs" />

    <!-- Error State -->
    <div v-if="error" class="p-12 text-center bg-rose-50 dark:bg-rose-500/10 rounded-2xl border border-rose-100 dark:border-rose-500/20">
      <span class="material-symbols-outlined text-rose-500 text-4xl mb-2">error</span>
      <p class="text-rose-600 dark:text-rose-400 font-medium">{{ error }}</p>
      <button @click="store.fetchOwnerDashboard()" class="mt-4 px-6 py-2 bg-rose-600 text-white rounded-xl text-sm font-bold hover:bg-rose-700 transition-colors">
        Thử lại
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed } from 'vue';
import { useEmployerDashboardStore } from '@/stores/employerDashboard.store';
import MetricCard from './MetricCard.vue';
import OwnerWeeklyChart from './OwnerWeeklyChart.vue';
import OwnerRecentJobsTable from './OwnerRecentJobsTable.vue';

const store = useEmployerDashboardStore();
const ownerData = computed(() => store.ownerData);
const loading = computed(() => store.loading);
const error = computed(() => store.error);

onMounted(() => {
  if (!ownerData.value) {
    store.fetchOwnerDashboard();
  }
});
</script>
