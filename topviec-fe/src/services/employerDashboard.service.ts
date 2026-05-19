import axiosInstance from './axios';
import type { RestResponse } from '@/types/common.types';
import type {
  ResOwnerDashboardDTO,
  ResManagerDashboardDTO,
  ResRecruiterDashboardDTO,
  ResViewerDashboardDTO,
} from '@/types/employerDashboard.types';

const BASE_URL = '/employer/dashboard';

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

export const employerDashboardService = {
  async getOwnerDashboard(): Promise<ResOwnerDashboardDTO> {
    const res = await axiosInstance.get<RestResponse<ResOwnerDashboardDTO> | ResOwnerDashboardDTO>(
      `${BASE_URL}/owner`,
    );
    return unwrapDashboardResponse(res.data);
  },

  async getManagerDashboard(): Promise<ResManagerDashboardDTO> {
    const res = await axiosInstance.get<RestResponse<ResManagerDashboardDTO> | ResManagerDashboardDTO>(
      `${BASE_URL}/manager`,
    );
    return unwrapDashboardResponse(res.data);
  },

  async getRecruiterDashboard(): Promise<ResRecruiterDashboardDTO> {
    const res = await axiosInstance.get<RestResponse<ResRecruiterDashboardDTO> | ResRecruiterDashboardDTO>(
      `${BASE_URL}/recruiter`,
    );
    return unwrapDashboardResponse(res.data);
  },

  async getViewerDashboard(): Promise<ResViewerDashboardDTO> {
    const res = await axiosInstance.get<RestResponse<ResViewerDashboardDTO> | ResViewerDashboardDTO>(
      `${BASE_URL}/viewer`,
    );
    return unwrapDashboardResponse(res.data);
  },
};
