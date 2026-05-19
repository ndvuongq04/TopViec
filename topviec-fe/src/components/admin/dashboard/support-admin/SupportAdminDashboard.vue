<template>
  <div class="space-y-8">
    <!-- KPI Cards -->
    <div v-if="loading" class="grid grid-cols-1 md:grid-cols-3 gap-6">
       <div v-for="i in 3" :key="i" class="h-32 bg-slate-100 dark:bg-white/5 animate-pulse rounded-xl"></div>
    </div>
    <SAKpiCards
      v-else-if="supportAdminData"
      :pending-complaints="supportAdminData.pendingComplaints"
      :pending-appeals="supportAdminData.pendingAppeals"
      :restricted-employers="supportAdminData.restrictedEmployers"
    />

    <!-- Status Chart -->
    <div v-if="loading" class="h-80 bg-slate-100 dark:bg-white/5 animate-pulse rounded-xl"></div>
    <SAComplaintStatusChart
      v-else-if="supportAdminData"
      :data="supportAdminData.complaintsByStatus"
    />

    <!-- Urgent Complaints Table -->
    <div v-if="loading" class="h-96 bg-slate-100 dark:bg-white/5 animate-pulse rounded-xl"></div>
    <SAUrgentComplaintsTable
      v-else-if="supportAdminData"
      :complaints="supportAdminData.urgentComplaints"
    />

    <!-- Error State -->
    <div v-if="error" class="p-8 text-center bg-rose-50 dark:bg-rose-500/10 rounded-xl border border-rose-100 dark:border-rose-500/20">
      <span class="material-symbols-outlined text-rose-500 text-4xl mb-2">error</span>
      <p class="text-rose-600 dark:text-rose-400 font-medium">{{ error }}</p>
      <button @click="store.fetchSupportAdminDashboard()" class="mt-4 px-4 py-2 bg-rose-600 text-white rounded-lg text-sm font-bold hover:bg-rose-700 transition-colors">
        Thử lại
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed } from 'vue';
import { useAdminDashboardStore } from '@/stores/adminDashboard.store';
import SAKpiCards from './SAKpiCards.vue';
import SAComplaintStatusChart from './SAComplaintStatusChart.vue';
import SAUrgentComplaintsTable from './SAUrgentComplaintsTable.vue';

const store = useAdminDashboardStore();
const supportAdminData = computed(() => store.supportAdminData);
const loading = computed(() => store.loading);
const error = computed(() => store.error);

onMounted(() => {
  if (!supportAdminData.value) {
    store.fetchSupportAdminDashboard();
  }
});
</script>
