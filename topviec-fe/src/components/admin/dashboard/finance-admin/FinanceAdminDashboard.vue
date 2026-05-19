<template>
  <div class="space-y-8">
    <!-- KPI Cards -->
    <div v-if="loading" class="grid grid-cols-1 md:grid-cols-3 gap-6">
       <div v-for="i in 3" :key="i" class="h-32 bg-slate-100 dark:bg-white/5 animate-pulse rounded-xl"></div>
    </div>
    <FAKpiCards
      v-else-if="financeAdminData"
      :monthly-revenue="financeAdminData.monthlyRevenue"
      :pending-orders="financeAdminData.pendingOrders"
      :refund-requests="financeAdminData.refundRequests"
    />

    <!-- Revenue Chart -->
    <div v-if="loading" class="h-80 bg-slate-100 dark:bg-white/5 animate-pulse rounded-xl"></div>
    <FARevenueChart
      v-else-if="financeAdminData"
      :data="financeAdminData.revenueByMonth"
    />

    <!-- Actionable Orders Table -->
    <div v-if="loading" class="h-96 bg-slate-100 dark:bg-white/5 animate-pulse rounded-xl"></div>
    <FAActionableOrdersTable
      v-else-if="financeAdminData"
      :orders="financeAdminData.actionableOrders"
    />

    <!-- Error State -->
    <div v-if="error" class="p-8 text-center bg-rose-50 dark:bg-rose-500/10 rounded-xl border border-rose-100 dark:border-rose-500/20">
      <span class="material-symbols-outlined text-rose-500 text-4xl mb-2">error</span>
      <p class="text-rose-600 dark:text-rose-400 font-medium">{{ error }}</p>
      <button @click="store.fetchFinanceAdminDashboard()" class="mt-4 px-4 py-2 bg-rose-600 text-white rounded-lg text-sm font-bold hover:bg-rose-700 transition-colors">
        Thử lại
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed } from 'vue';
import { useAdminDashboardStore } from '@/stores/adminDashboard.store';
import FAKpiCards from './FAKpiCards.vue';
import FARevenueChart from './FARevenueChart.vue';
import FAActionableOrdersTable from './FAActionableOrdersTable.vue';

const store = useAdminDashboardStore();
const financeAdminData = computed(() => store.financeAdminData);
const loading = computed(() => store.loading);
const error = computed(() => store.error);

onMounted(() => {
  if (!financeAdminData.value) {
    store.fetchFinanceAdminDashboard();
  }
});
</script>
