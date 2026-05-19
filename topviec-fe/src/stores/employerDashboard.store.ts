import { defineStore } from 'pinia';
import { ref } from 'vue';
import { employerDashboardService } from '@/services/employerDashboard.service';
import type { 
  ResOwnerDashboardDTO, 
  ResManagerDashboardDTO,
  ResRecruiterDashboardDTO,
  ResViewerDashboardDTO
} from '@/types/employerDashboard.types';

export const useEmployerDashboardStore = defineStore('employerDashboard', () => {
  const ownerData = ref<ResOwnerDashboardDTO | null>(null);
  const managerData = ref<ResManagerDashboardDTO | null>(null);
  const recruiterData = ref<ResRecruiterDashboardDTO | null>(null);
  const viewerData = ref<ResViewerDashboardDTO | null>(null);
  
  const loading = ref(false);
  const error = ref<string | null>(null);

  async function fetchOwnerDashboard() {
    loading.value = true;
    error.value = null;
    try {
      ownerData.value = await employerDashboardService.getOwnerDashboard();
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch owner dashboard';
    } finally {
      loading.value = false;
    }
  }

  async function fetchManagerDashboard() {
    loading.value = true;
    error.value = null;
    try {
      managerData.value = await employerDashboardService.getManagerDashboard();
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch manager dashboard';
    } finally {
      loading.value = false;
    }
  }

  async function fetchRecruiterDashboard() {
    loading.value = true;
    error.value = null;
    try {
      recruiterData.value = await employerDashboardService.getRecruiterDashboard();
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch recruiter dashboard';
    } finally {
      loading.value = false;
    }
  }

  async function fetchViewerDashboard() {
    loading.value = true;
    error.value = null;
    try {
      viewerData.value = await employerDashboardService.getViewerDashboard();
    } catch (err: any) {
      error.value = err.message || 'Failed to fetch viewer dashboard';
    } finally {
      loading.value = false;
    }
  }

  return {
    ownerData,
    managerData,
    recruiterData,
    viewerData,
    loading,
    error,
    fetchOwnerDashboard,
    fetchManagerDashboard,
    fetchRecruiterDashboard,
    fetchViewerDashboard
  };
});
