import { defineStore } from 'pinia';
import { ref } from 'vue';
import employerReportService from '@/services/employerReport.service';
import type { PaginationMeta } from '@/types/common.types';
import type {
  ReqGetEmployerReports,
  ReqGetReportsByComplaint,
  ResEmployerReportDetail,
  ResEmployerReportSummary,
  ResMyViolationScore,
} from '@/types/report.types';

export const useEmployerReportStore = defineStore('employerReport', () => {
  const reports = ref<ResEmployerReportSummary[]>([]);
  const meta = ref<PaginationMeta>({ page: 0, pageSize: 10, pages: 0, totals: 0 });
  const relatedReports = ref<ResEmployerReportSummary[]>([]);
  const relatedMeta = ref<PaginationMeta>({ page: 0, pageSize: 10, pages: 0, totals: 0 });
  const currentReport = ref<ResEmployerReportDetail | null>(null);
  const myViolationScore = ref<ResMyViolationScore | null>(null);
  const loading = ref(false);
  const error = ref<string | null>(null);

  function setError(err: unknown) {
    const data = (err as any)?.response?.data;
    if (data?.message && typeof data.message === 'object') {
      const firstError = Object.values(data.message)[0];
      error.value = String(firstError);
    } else {
      error.value = data?.message ?? 'Có lỗi xảy ra. Vui lòng thử lại.';
    }
  }

  async function fetchMyReports(params: ReqGetEmployerReports) {
    loading.value = true;
    error.value = null;
    try {
      const data = await employerReportService.getMyReports(params);
      reports.value = data.result;
      meta.value = data.meta;
    } catch (err) {
      setError(err);
    } finally {
      loading.value = false;
    }
  }

  async function fetchById(id: number): Promise<ResEmployerReportDetail> {
    loading.value = true;
    error.value = null;
    try {
      currentReport.value = await employerReportService.getById(id);
      return currentReport.value;
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function fetchMyViolationScore(): Promise<ResMyViolationScore> {
    loading.value = true;
    error.value = null;
    try {
      myViolationScore.value = await employerReportService.getMyViolationScore();
      return myViolationScore.value;
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function respondToReport(id: number): Promise<ResEmployerReportDetail> {
    loading.value = true;
    error.value = null;
    try {
      const updated = await employerReportService.respondToReport(id);
      currentReport.value = updated;

      const index = reports.value.findIndex((item) => item.id === id);
      if (index !== -1) {
        const existingReport = reports.value[index];
        if (existingReport) {
          reports.value[index] = {
            ...existingReport,
            status: updated.status,
            employerDeadline: updated.employerDeadline,
            remainingHours: updated.remainingHours,
          };
        }
      }

      return updated;
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function fetchReportsByComplaint(id: number, params: ReqGetReportsByComplaint) {
    loading.value = true;
    error.value = null;
    try {
      const data = await employerReportService.getReportsByComplaint(id, params);
      relatedReports.value = data.result;
      relatedMeta.value = data.meta;
    } catch (err) {
      setError(err);
    } finally {
      loading.value = false;
    }
  }

  function reset() {
    reports.value = [];
    meta.value = { page: 0, pageSize: 10, pages: 0, totals: 0 };
    relatedReports.value = [];
    relatedMeta.value = { page: 0, pageSize: 10, pages: 0, totals: 0 };
    currentReport.value = null;
    myViolationScore.value = null;
    loading.value = false;
    error.value = null;
  }

  return {
    reports,
    meta,
    relatedReports,
    relatedMeta,
    currentReport,
    myViolationScore,
    loading,
    error,
    fetchMyReports,
    fetchById,
    fetchMyViolationScore,
    respondToReport,
    fetchReportsByComplaint,
    reset,
  };
});
