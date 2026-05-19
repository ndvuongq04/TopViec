import { defineStore } from 'pinia';
import { ref } from 'vue';
import { adminDashboardService } from '@/services/adminDashboard.service';
import type { 
  ResSuperAdminDashboardDTO, 
  ResContentModeratorDashboardDTO,
  ResSupportAdminDashboardDTO,
  ResFinanceAdminDashboardDTO
} from '@/types/dashboard.types';

export const useAdminDashboardStore = defineStore('adminDashboard', () => {
  const superAdminData = ref<ResSuperAdminDashboardDTO | null>(null);
  const contentModeratorData = ref<ResContentModeratorDashboardDTO | null>(null);
  const supportAdminData = ref<ResSupportAdminDashboardDTO | null>(null);
  const financeAdminData = ref<ResFinanceAdminDashboardDTO | null>(null);
  
  const loading = ref(false);
  const error = ref<string | null>(null);

  async function fetchSuperAdminDashboard() {
    loading.value = true;
    error.value = null;
    try {
      superAdminData.value = await adminDashboardService.getSuperAdminDashboard();
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch super admin dashboard';
    } finally {
      loading.value = false;
    }
  }

  async function fetchContentModeratorDashboard() {
    loading.value = true;
    error.value = null;
    try {
      contentModeratorData.value = await adminDashboardService.getContentModeratorDashboard();
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch content moderator dashboard';
    } finally {
      loading.value = false;
    }
  }

  async function fetchSupportAdminDashboard() {
    loading.value = true;
    error.value = null;
    try {
      supportAdminData.value = await adminDashboardService.getSupportAdminDashboard();
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch support admin dashboard';
    } finally {
      loading.value = false;
    }
  }

  async function fetchFinanceAdminDashboard() {
    loading.value = true;
    error.value = null;
    try {
      financeAdminData.value = await adminDashboardService.getFinanceAdminDashboard();
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch finance admin dashboard';
    } finally {
      loading.value = false;
    }
  }

  return {
    superAdminData,
    contentModeratorData,
    supportAdminData,
    financeAdminData,
    loading,
    error,
    fetchSuperAdminDashboard,
    fetchContentModeratorDashboard,
    fetchSupportAdminDashboard,
    fetchFinanceAdminDashboard
  };
});
