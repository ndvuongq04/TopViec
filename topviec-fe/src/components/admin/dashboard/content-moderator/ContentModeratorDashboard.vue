<template>
  <div class="space-y-8">
    <!-- KPI Cards -->
    <div v-if="loading" class="grid grid-cols-1 md:grid-cols-3 gap-6">
       <div v-for="i in 3" :key="i" class="h-32 bg-slate-100 dark:bg-white/5 animate-pulse rounded-xl"></div>
    </div>
    <CMKpiCards
      v-else-if="contentModeratorData"
      :pending-approval-jobs="contentModeratorData.pendingApprovalJobs"
      :pending-verify-companies="contentModeratorData.pendingVerifyCompanies"
      :rejected-jobs-this-month="contentModeratorData.rejectedJobsThisMonth"
    />

    <!-- Moderation Chart -->
    <div v-if="loading" class="h-80 bg-slate-100 dark:bg-white/5 animate-pulse rounded-xl"></div>
    <CMModerationChart
      v-else-if="contentModeratorData"
      :data="contentModeratorData.moderationStats"
    />

    <!-- Pending Jobs Table -->
    <div v-if="loading" class="h-96 bg-slate-100 dark:bg-white/5 animate-pulse rounded-xl"></div>
    <CMPendingJobsTable
      v-else-if="contentModeratorData"
      :jobs="contentModeratorData.oldestPendingJobs"
    />

    <!-- Error State -->
    <div v-if="error" class="p-8 text-center bg-rose-50 dark:bg-rose-500/10 rounded-xl border border-rose-100 dark:border-rose-500/20">
      <span class="material-symbols-outlined text-rose-500 text-4xl mb-2">error</span>
      <p class="text-rose-600 dark:text-rose-400 font-medium">{{ error }}</p>
      <button @click="store.fetchContentModeratorDashboard()" class="mt-4 px-4 py-2 bg-rose-600 text-white rounded-lg text-sm font-bold hover:bg-rose-700 transition-colors">
        Thử lại
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed } from 'vue';
import { useAdminDashboardStore } from '@/stores/adminDashboard.store';
import CMKpiCards from './CMKpiCards.vue';
import CMModerationChart from './CMModerationChart.vue';
import CMPendingJobsTable from './CMPendingJobsTable.vue';

const store = useAdminDashboardStore();
const contentModeratorData = computed(() => store.contentModeratorData);
const loading = computed(() => store.loading);
const error = computed(() => store.error);

onMounted(() => {
  if (!contentModeratorData.value) {
    store.fetchContentModeratorDashboard();
  }
});
</script>
