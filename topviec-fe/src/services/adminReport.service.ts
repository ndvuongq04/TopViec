import axiosInstance from './axios';
import type { RestResponse } from '@/types/common.types';
import type {
  ReqConfirmReport,
  ReqGetAdminReports,
  ReqGetReportsByJobPost,
  ReqProcessReport,
  ResReportDetail,
  ResReportPagination,
  ResAdminReportStatisticsDTO,
} from '@/types/report.types';
import type { ResAppeal } from '@/types/appeal.types';

const BASE_URL = '/admin/reports';

const adminReportService = {
  async getAll(params: ReqGetAdminReports): Promise<ResReportPagination> {
    const res = await axiosInstance.get<RestResponse<ResReportPagination>>(BASE_URL, { params });
    return res.data.data;
  },

  async getById(id: number): Promise<ResReportDetail> {
    const res = await axiosInstance.get<RestResponse<ResReportDetail>>(`${BASE_URL}/${id}`);
    return res.data.data;
  },

  async getReportsByJobPost(jobPostId: number, params: ReqGetReportsByJobPost): Promise<ResReportPagination> {
    const res = await axiosInstance.get<RestResponse<ResReportPagination>>(
      `${BASE_URL}/job-posts/${jobPostId}`,
      { params },
    );
    return res.data.data;
  },

  async getAppealByComplaint(id: number): Promise<ResAppeal | null> {
    const res = await axiosInstance.get<RestResponse<ResAppeal | null>>(`${BASE_URL}/${id}/appeal`);
    return res.data.data;
  },

  async confirm(id: number, data: ReqConfirmReport): Promise<ResReportDetail> {
    const res = await axiosInstance.patch<RestResponse<ResReportDetail>>(
      `${BASE_URL}/${id}/confirm`,
      data,
    );
    return res.data.data;
  },

  async process(id: number, data: ReqProcessReport): Promise<ResReportDetail> {
    const res = await axiosInstance.patch<RestResponse<ResReportDetail>>(
      `${BASE_URL}/${id}/process`,
      data,
    );
    return res.data.data;
  },

  /** GET /admin/reports/statistics */
  async getStatistics(): Promise<ResAdminReportStatisticsDTO> {
    const res = await axiosInstance.get<RestResponse<ResAdminReportStatisticsDTO>>(
      `${BASE_URL}/statistics`
    );
    return res.data.data;
  },
};

export default adminReportService;
