import axiosInstance from './axios';
import type { RestResponse } from '@/types/common.types';
import type {
  ResApplication,
  ResApplicationPagination,
  ReqApplyJob,
  ReqBulkApply,
  ReqWithdrawApplication,
  ReqUpdateApplicationCv,
  ResTalentPoolInviteInfo,
} from '@/types/application.types';

const BASE_URL = '/applications';

const applicationService = {
  /**
   * CN-UV-010: Nộp đơn đầy đủ
   */
  async apply(jobPostId: number, data: ReqApplyJob): Promise<ResApplication> {
    const res = await axiosInstance.post<RestResponse<ResApplication>>(`${BASE_URL}/${jobPostId}`, data);
    return res.data.data;
  },

  /**
   * CN-UV-011: Ứng tuyển nhanh (CV mặc định)
   */
  async quickApply(jobPostId: number): Promise<ResApplication> {
    const res = await axiosInstance.post<RestResponse<ResApplication>>(`${BASE_URL}/${jobPostId}/quick`);
    return res.data.data;
  },

  /**
   * CN-UV-012: Ứng tuyển hàng loạt (tối đa 10 tin)
   */
  async bulkApply(data: ReqBulkApply): Promise<ResApplication[]> {
    const res = await axiosInstance.post<RestResponse<ResApplication[]>>(`${BASE_URL}/bulk`, data);
    return res.data.data;
  },

  /**
   * CN-UV-013: Theo dõi trạng thái đơn ứng tuyển
   */
  async getMyApplications(params: { status?: string; page?: number; size?: number; sort?: string }): Promise<ResApplicationPagination> {
    const res = await axiosInstance.get<RestResponse<ResApplicationPagination>>(`${BASE_URL}/me`, { params });
    return res.data.data;
  },

  /**
   * Lấy danh sách đơn của UV có ít nhất 1 lịch PV (dùng cho trang "Lịch PV của tôi")
   */
  async getMyApplicationsWithInterviews(): Promise<ResApplication[]> {
    const res = await axiosInstance.get<RestResponse<ResApplication[]>>(`${BASE_URL}/me/with-interviews`);
    return res.data.data;
  },

  /**
   * CN-UV-015: Rút đơn ứng tuyển
   */
  async withdraw(applicationId: number, data?: ReqWithdrawApplication): Promise<ResApplication> {
    const res = await axiosInstance.patch<RestResponse<ResApplication>>(`${BASE_URL}/${applicationId}/withdraw`, data);
    return res.data.data;
  },

  /**
   * CN-UV-016: Thay đổi CV ứng tuyển
   */
  async updateApplicationCv(applicationId: number, data: ReqUpdateApplicationCv): Promise<ResApplication> {
    const res = await axiosInstance.patch<RestResponse<ResApplication>>(`${BASE_URL}/${applicationId}/cv`, data);
    return res.data.data;
  },

  /**
   * UV chấp nhận lời mời từ talent pool (INVITED → PENDING)
   */
  async acceptInvite(applicationId: number): Promise<ResApplication> {
    const res = await axiosInstance.patch<RestResponse<ResApplication>>(
      `${BASE_URL}/${applicationId}/accept-invite`,
    );
    return res.data.data;
  },

  /**
   * UV từ chối lời mời từ talent pool (INVITED → WITHDRAWN)
   */
  async declineInvite(applicationId: number): Promise<ResApplication> {
    const res = await axiosInstance.patch<RestResponse<ResApplication>>(
      `${BASE_URL}/${applicationId}/decline-invite`,
    );
    return res.data.data;
  },

  /**
   * Public: xác thực token từ email mời, trả về thông tin lời mời
   */
  async verifyInviteToken(token: string): Promise<ResTalentPoolInviteInfo> {
    const res = await axiosInstance.get<RestResponse<ResTalentPoolInviteInfo>>(
      '/talent-pool-invite/verify',
      { params: { token } },
    );
    return res.data.data;
  },
};

export default applicationService;
