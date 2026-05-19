import { defineStore } from 'pinia';
import { ref } from 'vue';
import candidateReportService from '@/services/candidateReport.service';
import type { PaginationMeta } from '@/types/common.types';
import type {
  ReqCreateReport,
  ReqGetMyReports,
  ResReportDetail,
  ResReportSummary,
  ResViolationReason,
} from '@/types/report.types';

export const useCandidateReportStore = defineStore('candidateReport', () => {
  // ─── State ──────────────────────────────────────────────────────────────────
  const reports = ref<ResReportSummary[]>([]);
  const meta = ref<PaginationMeta>({ page: 0, pageSize: 10, pages: 0, totals: 0 });
  const violationReasons = ref<ResViolationReason[]>([]);
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

  function normalizeReportSummary(item: ResReportSummary): ResReportSummary {
    const jobPosting = item.jobPosting ?? item.jobPost;
    const company = item.company ?? jobPosting?.company;

    return {
      ...item,
      jobPostId: item.jobPostId ?? jobPosting?.id ?? 0,
      jobPostTitle: item.jobPostTitle ?? jobPosting?.title ?? '',
      companyId: item.companyId ?? company?.id ?? 0,
      companyName: item.companyName ?? company?.name ?? '',
    };
  }

  // ─── Actions ────────────────────────────────────────────────────────────────

  async function fetchMyReports(params: ReqGetMyReports) {
    loading.value = true;
    error.value = null;
    try {
      const data = await candidateReportService.getMyReports(params);
      reports.value = data.result.map(normalizeReportSummary);
      meta.value = data.meta;
    } catch (err) {
      setError(err);
    } finally {
      loading.value = false;
    }
  }

  async function fetchViolationReasons() {
    if (violationReasons.value.length) return;
    loading.value = true;
    error.value = null;
    try {
      violationReasons.value = await candidateReportService.getViolationReasons();
    } catch (err) {
      setError(err);
    } finally {
      loading.value = false;
    }
  }

  async function createReport(reqData: ReqCreateReport): Promise<ResReportDetail> {
    loading.value = true;
    error.value = null;
    try {
      return await candidateReportService.create(reqData);
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      loading.value = false;
    }
  }

  function reset() {
    reports.value = [];
    meta.value = { page: 0, pageSize: 10, pages: 0, totals: 0 };
    violationReasons.value = [];
    loading.value = false;
    error.value = null;
  }

  return {
    reports,
    meta,
    violationReasons,
    loading,
    error,
    fetchMyReports,
    fetchViolationReasons,
    createReport,
    reset,
  };
});
