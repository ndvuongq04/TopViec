import axiosInstance from './axios';
import type { RestResponse } from '@/types/common.types';
import type {
  ReqCreateReport,
  ReqGetMyReports,
  ResReportDetail,
  ResReportPagination,
  ResViolationReason,
} from '@/types/report.types';

const BASE_URL = '/candidate/reports';

const candidateReportService = {
  async create(data: ReqCreateReport): Promise<ResReportDetail> {
    const res = await axiosInstance.post<RestResponse<ResReportDetail>>(BASE_URL, data);
    return res.data.data;
  },

  async getViolationReasons(): Promise<ResViolationReason[]> {
    const res = await axiosInstance.get<RestResponse<ResViolationReason[]>>(
      `${BASE_URL}/violation-reasons`,
    );
    return res.data.data;
  },

  async getMyReports(params: ReqGetMyReports): Promise<ResReportPagination> {
    const res = await axiosInstance.get<RestResponse<ResReportPagination>>(`${BASE_URL}/my`, {
      params,
    });
    return res.data.data;
  },
};

export default candidateReportService;
