import { defineStore } from 'pinia';
import { ref } from 'vue';
import applicationService from '@/services/application.service';
import type { 
  ResApplication, 
  ReqApplyJob, 
  ReqBulkApply, 
  ReqWithdrawApplication,
  ReqUpdateApplicationCv 
} from '@/types/application.types';
import type { PaginationMeta } from '@/types/common.types';

export const useApplicationStore = defineStore('application', () => {
  // ─── State ──────────────────────────────────────────────────────────────────
  const applications = ref<ResApplication[]>([]);
  const meta = ref<PaginationMeta>({ page: 0, pageSize: 10, pages: 0, totals: 0 });
  const loading = ref(false);
  const error = ref<string | null>(null);

  // ─── Helpers ────────────────────────────────────────────────────────────────
  function setError(err: unknown) {
    const data = (err as any)?.response?.data;
    if (data?.message && typeof data.message === 'object') {
      const firstError = Object.values(data.message)[0];
      error.value = String(firstError);
    } else {
      error.value = data?.message ?? 'Có lỗi xảy ra. Vui lòng thử lại.';
    }
  }

  // ─── Actions ────────────────────────────────────────────────────────────────

  /** Fetch paginated applications */
  async function fetchMyApplications(params: { status?: string; page?: number; size?: number; sort?: string }) {
    loading.value = true;
    error.value = null;
    try {
      const data = await applicationService.getMyApplications(params);
      applications.value = data.result;
      meta.value = data.meta;
    } catch (err) {
      setError(err);
    } finally {
      loading.value = false;
    }
  }

  /** Fetch applications có ít nhất 1 lịch PV (trang Lịch PV của tôi) */
  async function fetchMyApplicationsWithInterviews() {
    loading.value = true;
    error.value = null;
    try {
      applications.value = await applicationService.getMyApplicationsWithInterviews();
    } catch (err) {
      setError(err);
    } finally {
      loading.value = false;
    }
  }

  /** Apply for a job */
  async function apply(jobPostId: number, reqData: ReqApplyJob) {
    loading.value = true;
    error.value = null;
    try {
      return await applicationService.apply(jobPostId, reqData);
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  /** Quick apply for a job */
  async function quickApply(jobPostId: number) {
    loading.value = true;
    error.value = null;
    try {
      return await applicationService.quickApply(jobPostId);
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  /** Bulk apply for multiple jobs */
  async function bulkApply(reqData: ReqBulkApply) {
    loading.value = true;
    error.value = null;
    try {
      return await applicationService.bulkApply(reqData);
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  /** Withdraw an application */
  async function withdraw(applicationId: number, reqData?: ReqWithdrawApplication) {
    loading.value = true;
    error.value = null;
    try {
      const data = await applicationService.withdraw(applicationId, reqData);
      
      // Update local state if the application is in the current list
      const index = applications.value.findIndex(app => app.id === applicationId);
      if (index !== -1) {
        applications.value[index] = data;
      }
      
      return data;
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  /** Change application CV */
  async function updateApplicationCv(applicationId: number, cvId: number) {
    loading.value = true;
    error.value = null;
    try {
      const data = await applicationService.updateApplicationCv(applicationId, { cvId });
      
      const index = applications.value.findIndex(app => app.id === applicationId);
      if (index !== -1) {
        applications.value[index] = data;
      }
      
      return data;
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function acceptInvite(applicationId: number) {
    loading.value = true;
    error.value = null;
    try {
      const data = await applicationService.acceptInvite(applicationId);
      const index = applications.value.findIndex(app => app.id === applicationId);
      if (index !== -1) {
        applications.value[index] = data;
      }
      return data;
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function declineInvite(applicationId: number) {
    loading.value = true;
    error.value = null;
    try {
      const data = await applicationService.declineInvite(applicationId);
      const index = applications.value.findIndex(app => app.id === applicationId);
      if (index !== -1) {
        applications.value[index] = data;
      }
      return data;
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  /** Reset store */
  function reset() {
    applications.value = [];
    meta.value = { page: 0, pageSize: 10, pages: 0, totals: 0 };
    loading.value = false;
    error.value = null;
  }

  return {
    // state
    applications,
    meta,
    loading,
    error,
    // actions
    fetchMyApplications,
    fetchMyApplicationsWithInterviews,
    apply,
    quickApply,
    bulkApply,
    withdraw,
    updateApplicationCv,
    acceptInvite,
    declineInvite,
    reset,
  };
});
