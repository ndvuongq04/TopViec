import axiosInstance from './axios';
import type { RestResponse } from '@/types/common.types';
import type {
  ResSuperAdminDashboardDTO,
  ResContentModeratorDashboardDTO,
  ResSupportAdminDashboardDTO,
  ResFinanceAdminDashboardDTO,
} from '@/types/dashboard.types';

const BASE_URL = '/admin/dashboard';

const isRestResponse = <T>(payload: RestResponse<T> | T): payload is RestResponse<T> => {
  return (
    payload !== null &&
    typeof payload === 'object' &&
    'statusCode' in payload &&
    'data' in payload
  );
};

const unwrapDashboardResponse = <T>(payload: RestResponse<T> | T): T => {
  return isRestResponse(payload) ? payload.data : payload;
};

export const adminDashboardService = {
  async getSuperAdminDashboard(): Promise<ResSuperAdminDashboardDTO> {
    const res = await axiosInstance.get<RestResponse<ResSuperAdminDashboardDTO> | ResSuperAdminDashboardDTO>(
      `${BASE_URL}/super-admin`,
    );
    return unwrapDashboardResponse(res.data);
  },

  async getContentModeratorDashboard(): Promise<ResContentModeratorDashboardDTO> {
    const res = await axiosInstance.get<
      RestResponse<ResContentModeratorDashboardDTO> | ResContentModeratorDashboardDTO
    >(`${BASE_URL}/content-moderator`);
    return unwrapDashboardResponse(res.data);
  },

  async getSupportAdminDashboard(): Promise<ResSupportAdminDashboardDTO> {
    const res = await axiosInstance.get<RestResponse<ResSupportAdminDashboardDTO> | ResSupportAdminDashboardDTO>(
      `${BASE_URL}/support-admin`,
    );
    return unwrapDashboardResponse(res.data);
  },

  async getFinanceAdminDashboard(): Promise<ResFinanceAdminDashboardDTO> {
    const res = await axiosInstance.get<RestResponse<ResFinanceAdminDashboardDTO> | ResFinanceAdminDashboardDTO>(
      `${BASE_URL}/finance-admin`,
    );
    return unwrapDashboardResponse(res.data);
  },
};
