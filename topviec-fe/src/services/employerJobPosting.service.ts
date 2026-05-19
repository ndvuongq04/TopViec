import axiosInstance from './axios'
import type { RestResponse } from '@/types/common.types'
import type {
  ReqCreateJobPostingDTO,
  ReqUpdateJobPostingDTO,
  ReqExtendJobPostDTO,
  ResJobPostingDetail,
  ResJobPostingDetailPagination,
  EmployerJobPostingQueryParams,
  ResEmployerJobStatisticsDTO,
} from '@/types/jobPosting.types'

const BASE_URL = '/employer/job-postings'

export const employerJobPostingService = {
  /**
   * POST /api/v1/employer/job-postings
   * Tạo tin mới. Status mặc định = "draft".
   */
  async createJob(payload: ReqCreateJobPostingDTO): Promise<ResJobPostingDetail> {
    const res = await axiosInstance.post<RestResponse<ResJobPostingDetail>>(BASE_URL, payload)
    return res.data.data
  },

  /**
   * GET /api/v1/employer/job-postings
   * Lấy danh sách tin của công ty đang đăng nhập. Hỗ trợ filter + phân trang.
   */
  async getList(params?: EmployerJobPostingQueryParams): Promise<ResJobPostingDetailPagination> {
    const res = await axiosInstance.get<RestResponse<ResJobPostingDetailPagination>>(BASE_URL, { params })
    return res.data.data
  },

  /**
   * GET /api/v1/employer/job-postings/{id}
   */
  async getById(id: number | string): Promise<ResJobPostingDetail> {
    const res = await axiosInstance.get<RestResponse<ResJobPostingDetail>>(`${BASE_URL}/${id}`)
    return res.data.data
  },

  /**
   * PUT /api/v1/employer/job-postings/{id}
   * Cập nhật tin.
   */
  async updateJob(id: number | string, payload: ReqUpdateJobPostingDTO): Promise<ResJobPostingDetail> {
    const res = await axiosInstance.put<RestResponse<ResJobPostingDetail>>(`${BASE_URL}/${id}`, payload)
    return res.data.data
  },

  /**
   * PATCH /api/v1/employer/job-postings/{id}/pause
   */
  async pauseJob(id: number | string): Promise<ResJobPostingDetail> {
    const res = await axiosInstance.patch<RestResponse<ResJobPostingDetail>>(`${BASE_URL}/${id}/pause`)
    return res.data.data
  },

  /**
   * PATCH /api/v1/employer/job-postings/{id}/resume
   */
  async resumeJob(id: number | string): Promise<ResJobPostingDetail> {
    const res = await axiosInstance.patch<RestResponse<ResJobPostingDetail>>(`${BASE_URL}/${id}/resume`)
    return res.data.data
  },

  /**
   * PATCH /api/v1/employer/job-postings/{id}/close
   */
  async closeJob(id: number | string): Promise<ResJobPostingDetail> {
    const res = await axiosInstance.patch<RestResponse<ResJobPostingDetail>>(`${BASE_URL}/${id}/close`)
    return res.data.data
  },

  /**
   * PATCH /employer/job-postings/{id}/pending-approval
   * Gửi duyệt tin tuyển dụng.
   */
  async pendingApproval(id: number | string): Promise<ResJobPostingDetail> {
    const res = await axiosInstance.patch<RestResponse<ResJobPostingDetail>>(`${BASE_URL}/${id}/pending-approval`)
    return res.data.data
  },

  /**
   * PATCH /api/v1/employer/job-postings/{id}/extend
   */
  async extendJob(id: number | string, payload: ReqExtendJobPostDTO): Promise<ResJobPostingDetail> {
    const res = await axiosInstance.patch<RestResponse<ResJobPostingDetail>>(`${BASE_URL}/${id}/extend`, payload)
    return res.data.data
  },

  /**
   * PATCH /api/v1/employer/job-postings/{id}/refresh
   * Làm mới tin (đẩy lên đầu).
   */
  async refreshJob(id: number | string): Promise<ResJobPostingDetail> {
    const res = await axiosInstance.patch<RestResponse<ResJobPostingDetail>>(`${BASE_URL}/${id}/refresh`)
    return res.data.data
  },
  /**
   * DELETE /employer/job-postings/{id}
   */
  async deleteJob(id: number | string): Promise<void> {
    await axiosInstance.delete(`${BASE_URL}/${id}`)
  },
  /**
   * PATCH /employer/job-postings/{id}/restore
   */
  async restoreJob(id: number | string): Promise<ResJobPostingDetail> {
    const res = await axiosInstance.patch<RestResponse<ResJobPostingDetail>>(`${BASE_URL}/${id}/restore`)
    return res.data.data
  },

  /**
   * GET /employer/company/job-statistics
   * Lấy thống kê tin tuyển dụng.
   */
  async getJobStatistics(): Promise<ResEmployerJobStatisticsDTO> {
    const res = await axiosInstance.get<RestResponse<ResEmployerJobStatisticsDTO>>(
      '/employer/company/job-statistics'
    )
    return res.data.data
  },
}
