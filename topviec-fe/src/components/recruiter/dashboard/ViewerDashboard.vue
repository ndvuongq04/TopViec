<template>
  <div class="space-y-12 py-12">
    <!-- Centered KPI Cards -->
    <div class="max-w-2xl mx-auto grid grid-cols-1 md:grid-cols-2 gap-6">
      <div v-if="loading" v-for="i in 2" :key="i" class="h-28 bg-slate-100 dark:bg-slate-700/50 animate-pulse rounded-2xl"></div>
      
      <template v-else-if="viewerData">
        <MetricCard
          icon="work"
          label="Tin đang tuyển"
          :value="viewerData.activeJobs"
          icon-bg-class="bg-blue-100 dark:bg-blue-500/10"
          icon-color-class="text-blue-600 dark:text-blue-400"
        />
        <MetricCard
          icon="description"
          label="Tổng ứng viên (tháng)"
          :value="viewerData.totalApplicationsThisMonth"
          icon-bg-class="bg-purple-100 dark:bg-purple-500/10"
          icon-color-class="text-purple-600 dark:text-purple-400"
        />
      </template>
    </div>

    <!-- Empty State / Restriction Message -->
    <div class="text-center space-y-4 max-w-md mx-auto py-12">
      <div class="w-16 h-16 bg-slate-100 dark:bg-slate-800 rounded-full flex items-center justify-center mx-auto shadow-inner">
        <span class="material-symbols-outlined text-3xl text-slate-400">lock</span>
      </div>
      <div class="space-y-1">
        <h3 class="text-slate-600 dark:text-slate-300 font-bold">Chế độ Viewer</h3>
        <p class="text-slate-400 text-sm leading-relaxed px-6">
          Bạn đang xem ở chế độ Viewer. Liên hệ quản trị viên công ty để được cấp thêm quyền xem biểu đồ và danh sách chi tiết.
        </p>
      </div>
    </div>

    <!-- Error State -->
    <div v-if="error" class="p-8 text-center bg-rose-50 dark:bg-rose-500/10 rounded-2xl border border-rose-100 dark:border-rose-500/20 max-w-2xl mx-auto">
      <span class="material-symbols-outlined text-rose-500 text-2xl mb-2">error</span>
      <p class="text-rose-600 dark:text-rose-400 font-medium text-sm">{{ error }}</p>
      <button @click="store.fetchViewerDashboard()" class="mt-4 px-4 py-1.5 bg-rose-600 text-white rounded-xl text-xs font-bold hover:bg-rose-700 transition-colors">
        Thử lại
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed } from 'vue';
import { useEmployerDashboardStore } from '@/stores/employerDashboard.store';
import MetricCard from './MetricCard.vue';

const store = useEmployerDashboardStore();
const viewerData = computed(() => store.viewerData);
const loading = computed(() => store.loading);
const error = computed(() => store.error);

onMounted(() => {
  if (!viewerData.value) {
    store.fetchViewerDashboard();
  }
});
</script>
