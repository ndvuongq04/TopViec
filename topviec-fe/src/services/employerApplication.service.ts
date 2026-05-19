import axiosInstance from './axios';
import type { RestResponse } from '@/types/common.types';
import type {
  ResEmployerApplicationDTO,
  ResEmployerApplicationPagination,
  ReqUpdateApplicationDTO
} from '@/types/employerApplication.types';

const BASE_URL = '/employer/applications';

const employerApplicationService = {
  /**
   * Lấy danh sách hồ sơ ứng tuyển của 1 tin tuyển dụng.
   */
  async getApplicationsByJobPost(
    jobPostId: number,
    params: { status?: string; page?: number; size?: number; sort?: string; search?: string }
  ): Promise<ResEmployerApplicationPagination> {
    const res = await axiosInstance.get<RestResponse<ResEmployerApplicationPagination>>(
      `${BASE_URL}/job/${jobPostId}`,
      { params }
    );
    return res.data.data;
  },

  /**
   * Xem chi tiết 1 hồ sơ ứng tuyển. Tự động chuyển status PENDING -> SEEN.
   */
  async getApplicationDetailByEmployer(applicationId: number): Promise<ResEmployerApplicationDTO> {
    const res = await axiosInstance.get<RestResponse<ResEmployerApplicationDTO>>(`${BASE_URL}/${applicationId}`);
    return res.data.data;
  },

  /**
   * NTD cập nhật trạng thái và/hoặc đánh giá (cho điểm, ghi chú, gán tag) CV ứng tuyển.
   */
  async updateApplication(
    applicationId: number,
    data: ReqUpdateApplicationDTO
  ): Promise<ResEmployerApplicationDTO> {
    const res = await axiosInstance.patch<RestResponse<ResEmployerApplicationDTO>>(
      `${BASE_URL}/${applicationId}`,
      data
    );
    return res.data.data;
  },
};

export default employerApplicationService;
