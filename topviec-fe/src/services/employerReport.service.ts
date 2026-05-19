import axiosInstance from './axios';
import type { RestResponse } from '@/types/common.types';
import type {
  ReqGetEmployerReports,
  ReqGetReportsByJobPost,
  ResEmployerReportDetail,
  ResEmployerReportPagination,
  ResMyViolationScore,
} from '@/types/report.types';
import type { ResAppeal } from '@/types/appeal.types';

const BASE_URL = '/employer/me';

const employerReportService = {
  async getMyReports(params: ReqGetEmployerReports): Promise<ResEmployerReportPagination> {
    const res = await axiosInstance.get<RestResponse<ResEmployerReportPagination>>(
      `${BASE_URL}/reports`,
      { params },
    );
    return res.data.data;
  },

  async getById(id: number): Promise<ResEmployerReportDetail> {
    const res = await axiosInstance.get<RestResponse<ResEmployerReportDetail>>(
      `${BASE_URL}/reports/${id}`,
    );
    return res.data.data;
  },

  async getReportsByJobPost(jobPostId: number, params: ReqGetReportsByJobPost): Promise<ResEmployerReportPagination> {
    const res = await axiosInstance.get<RestResponse<ResEmployerReportPagination>>(
      `${BASE_URL}/job-posts/${jobPostId}/reports`,
      { params },
    );
    return res.data.data;
  },

  async getAppealByComplaint(reportId: number): Promise<ResAppeal | null> {
    const res = await axiosInstance.get<RestResponse<ResAppeal | null>>(
      `${BASE_URL}/reports/${reportId}/appeal`,
    );
    return res.data.data;
  },

  async getMyAppeals(): Promise<ResAppeal[]> {
    const res = await axiosInstance.get<RestResponse<ResAppeal[]>>(`${BASE_URL}/appeals`);
    return res.data.data;
  },

  async getMyViolationScore(): Promise<ResMyViolationScore> {
    const res = await axiosInstance.get<RestResponse<ResMyViolationScore>>(
      `${BASE_URL}/violation-score`,
    );
    return res.data.data;
  },

  async respondToReport(id: number): Promise<ResEmployerReportDetail> {
    const res = await axiosInstance.post<RestResponse<ResEmployerReportDetail>>(
      `${BASE_URL}/reports/${id}/respond`,
    );
    return res.data.data;
  },
};

export default employerReportService;
