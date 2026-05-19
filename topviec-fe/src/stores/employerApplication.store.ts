import { defineStore } from 'pinia';
import { ref } from 'vue';
import employerApplicationService from '@/services/employerApplication.service';
import type {
  ResEmployerApplicationDTO,
  ReqUpdateApplicationDTO
} from '@/types/employerApplication.types';
import type { PaginationMeta } from '@/types/common.types';

export const useEmployerApplicationStore = defineStore('employerApplication', () => {
  // ─── State ──────────────────────────────────────────────────────────────────
  const applications = ref<ResEmployerApplicationDTO[]>([]);
  const currentApplication = ref<ResEmployerApplicationDTO | null>(null);
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

  /** Fetch applications for a specific job post */
  async function fetchApplicationsByJob(
    jobPostId: number,
    params: { status?: string; page?: number; size?: number; sort?: string }
  ) {
    loading.value = true;
    error.value = null;
    try {
      const data = await employerApplicationService.getApplicationsByJobPost(jobPostId, params);
      applications.value = data.result;
      meta.value = data.meta;
    } catch (err) {
      setError(err);
    } finally {
      loading.value = false;
    }
  }

  /** Fetch details of a single application */
  async function fetchApplicationDetail(applicationId: number) {
    loading.value = true;
    error.value = null;
    try {
      const data = await employerApplicationService.getApplicationDetailByEmployer(applicationId);
      currentApplication.value = data;

      // Update in list if present
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

  /** Update application status and/or evaluation */
  async function updateApplication(applicationId: number, reqData: ReqUpdateApplicationDTO) {
    loading.value = true;
    error.value = null;
    try {
      const data = await employerApplicationService.updateApplication(applicationId, reqData);

      if (currentApplication.value?.id === applicationId) {
        currentApplication.value = data;
      }

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
    currentApplication.value = null;
    meta.value = { page: 0, pageSize: 10, pages: 0, totals: 0 };
    loading.value = false;
    error.value = null;
  }

  return {
    // state
    applications,
    currentApplication,
    meta,
    loading,
    error,
    // actions
    fetchApplicationsByJob,
    fetchApplicationDetail,
    updateApplication,
    reset,
  };
});
