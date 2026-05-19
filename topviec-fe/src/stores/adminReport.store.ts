import { defineStore } from 'pinia';
import { ref } from 'vue';
import adminReportService from '@/services/adminReport.service';
import type { PaginationMeta } from '@/types/common.types';
import type { ResAppeal } from '@/types/appeal.types';
import type {
  ReqConfirmReport,
  ReqGetAdminReports,
  ReqGetReportsByComplaint,
  ReqProcessReport,
  ResReportDetail,
  ResReportSummary,
  ResAdminReportStatisticsDTO,
} from '@/types/report.types';

export const useAdminReportStore = defineStore('adminReport', () => {
  // ─── State ──────────────────────────────────────────────────────────────────
  const reports = ref<ResReportSummary[]>([]);
  const meta = ref<PaginationMeta>({ page: 0, pageSize: 10, pages: 0, totals: 0 });
  const relatedReports = ref<ResReportSummary[]>([]);
  const relatedMeta = ref<PaginationMeta>({ page: 0, pageSize: 10, pages: 0, totals: 0 });
  const currentReport = ref<ResReportDetail | null>(null);
  const currentAppeal = ref<ResAppeal | null>(null);
  const reportStatistics = ref<ResAdminReportStatisticsDTO | null>(null);
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

  async function fetchAll(params: ReqGetAdminReports) {
    loading.value = true;
    error.value = null;
    try {
      const data = await adminReportService.getAll(params);
      reports.value = data.result;
      meta.value = data.meta;
    } catch (err) {
      setError(err);
    } finally {
      loading.value = false;
    }
  }

  async function fetchById(id: number): Promise<ResReportDetail> {
    loading.value = true;
    error.value = null;
    try {
      currentReport.value = await adminReportService.getById(id);
      return currentReport.value;
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  async function processReport(id: number, reqData: ReqProcessReport): Promise<ResReportDetail> {
    loading.value = true;
    error.value = null;
    try {
      const updated = await adminReportService.process(id, reqData);
      currentReport.value = updated;
      const index = reports.value.findIndex((r) => r.id === id);
      if (index !== -1) {
        const existingReport = reports.value[index];
        if (existingReport) {
          reports.value[index] = { ...existingReport, status: updated.status };
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

  async function confirmReport(id: number, reqData: ReqConfirmReport): Promise<ResReportDetail> {
    loading.value = true;
    error.value = null;
    try {
      const updated = await adminReportService.confirm(id, reqData);
      currentReport.value = updated;
      const index = reports.value.findIndex((r) => r.id === id);
      if (index !== -1) {
        const existingReport = reports.value[index];
        if (existingReport) {
          reports.value[index] = { ...existingReport, status: updated.status };
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

  async function fetchAppealByComplaint(id: number): Promise<ResAppeal | null> {
    loading.value = true;
    error.value = null;
    try {
      currentAppeal.value = await adminReportService.getAppealByComplaint(id);
      return currentAppeal.value;
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
      const data = await adminReportService.getReportsByComplaint(id, params);
      relatedReports.value = data.result;
      relatedMeta.value = data.meta;
    } catch (err) {
      setError(err);
    } finally {
      loading.value = false;
    }
  }

  async function fetchStatistics() {
    loading.value = true;
    error.value = null;
    try {
      reportStatistics.value = await adminReportService.getStatistics();
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
    currentAppeal.value = null;
    reportStatistics.value = null;
    loading.value = false;
    error.value = null;
  }

  return {
    reports,
    meta,
    relatedReports,
    relatedMeta,
    currentReport,
    currentAppeal,
    reportStatistics,
    loading,
    error,
    fetchAll,
    fetchById,
    fetchAppealByComplaint,
    processReport,
    confirmReport,
    fetchReportsByComplaint,
    fetchStatistics,
    reset,
  };
});
