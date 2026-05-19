<template>
  <div class="space-y-8">
    <!-- KPI Cards -->
    <div v-if="loading" class="grid grid-cols-1 md:grid-cols-3 gap-6">
       <div v-for="i in 3" :key="i" class="h-28 bg-slate-100 dark:bg-slate-700/50 animate-pulse rounded-2xl"></div>
    </div>
    <div v-else-if="recruiterData" class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <MetricCard
        icon="assignment"
        label="Tin được phân công"
        :value="recruiterData.assignedActiveJobs"
        icon-bg-class="bg-indigo-100 dark:bg-indigo-500/10"
        icon-color-class="text-indigo-600 dark:text-indigo-400"
      />
      <MetricCard
        icon="mark_email_unread"
        label="Ứng viên chờ xem"
        :value="recruiterData.pendingApplications"
        icon-bg-class="bg-amber-100 dark:bg-amber-500/10"
        icon-color-class="text-amber-600 dark:text-amber-400"
      />
      <MetricCard
        icon="event_upcoming"
        label="Phỏng vấn sắp tới"
        :value="recruiterData.upcomingInterviews"
        icon-bg-class="bg-emerald-100 dark:bg-emerald-500/10"
        icon-color-class="text-emerald-600 dark:text-emerald-400"
      />
    </div>

    <!-- Chart: Applications By Job -->
    <div v-if="loading" class="h-80 bg-slate-100 dark:bg-slate-700/50 animate-pulse rounded-2xl"></div>
    <RecruiterJobChart v-else-if="recruiterData" :data="recruiterData.applicationsByJob" />

    <!-- Pending Candidates Table -->
    <div v-if="loading" class="h-96 bg-slate-100 dark:bg-slate-700/50 animate-pulse rounded-2xl"></div>
    <RecruiterPendingTable v-else-if="recruiterData" :candidates="recruiterData.pendingCandidates" />

    <!-- Error State -->
    <div v-if="error" class="p-12 text-center bg-rose-50 dark:bg-rose-500/10 rounded-2xl border border-rose-100 dark:border-rose-500/20">
      <span class="material-symbols-outlined text-rose-500 text-4xl mb-2">error</span>
      <p class="text-rose-600 dark:text-rose-400 font-medium">{{ error }}</p>
      <button @click="store.fetchRecruiterDashboard()" class="mt-4 px-6 py-2 bg-rose-600 text-white rounded-xl text-sm font-bold hover:bg-rose-700 transition-colors">
        Thử lại
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed } from 'vue';
import { useEmployerDashboardStore } from '@/stores/employerDashboard.store';
import MetricCard from './MetricCard.vue';
import RecruiterJobChart from './RecruiterJobChart.vue';
import RecruiterPendingTable from './RecruiterPendingTable.vue';

const store = useEmployerDashboardStore();
const recruiterData = computed(() => store.recruiterData);
const loading = computed(() => store.loading);
const error = computed(() => store.error);

onMounted(() => {
  if (!recruiterData.value) {
    store.fetchRecruiterDashboard();
  }
});
</script>
