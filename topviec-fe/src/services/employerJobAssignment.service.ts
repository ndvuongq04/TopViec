import axiosInstance from './axios'
import type { RestResponse } from '@/types/common.types'
import type {
  ReqAssignJobPostDTO,
  ReqReassignJobPostDTO,
  ReqRevokeAssignmentDTO,
  ResJobPostAssignmentDTO,
  ResRecruiterAssignmentPagination,
  ResJobPostWithAssignmentPagination,
  AssignmentJobPostQueryParams,
  AssignmentRecruiterQueryParams,
  ResUnassignedJobPostPagination,
  UnassignedJobPostQueryParams,
} from '@/types/employerJobAssignment.types'

const BASE_URL = '/employer/job-assignments'

export const employerJobAssignmentService = {

  // ==========================================================================
  // Giao việc
  // ==========================================================================

  /**
   * POST /employer/job-assignments
   * Phân công tin tuyển dụng cho một NTD.
   */
  async assignJobPost(payload: ReqAssignJobPostDTO): Promise<ResJobPostAssignmentDTO> {
    const res = await axiosInstance.post<RestResponse<ResJobPostAssignmentDTO>>(BASE_URL, payload)
    return res.data.data
  },

  /**
   * PUT /employer/job-assignments/reassign
   * Đổi người phân công (thu hồi NTD cũ + giao NTD mới trong 1 bước).
   */
  async reassignJobPost(payload: ReqReassignJobPostDTO): Promise<ResJobPostAssignmentDTO> {
    const res = await axiosInstance.put<RestResponse<ResJobPostAssignmentDTO>>(`${BASE_URL}/reassign`, payload)
    return res.data.data
  },

  // ==========================================================================
  // Quản lý theo Recruiter
  // ==========================================================================

  /**
   * GET /employer/job-assignments/recruiters
   * Lấy danh sách NTD trong công ty kèm số tin đang quản lý.
   */
  async getRecruiters(params?: AssignmentRecruiterQueryParams): Promise<ResRecruiterAssignmentPagination> {
    const res = await axiosInstance.get<RestResponse<ResRecruiterAssignmentPagination>>(
      `${BASE_URL}/recruiters`,
      { params },
    )
    return res.data.data
  },

  /**
   * GET /employer/job-assignments/recruiters/{userId}/job-posts
   * Lấy danh sách tin đang được phân công cho một NTD.
   */
  async getJobPostsByRecruiter(
    userId: number,
    params?: { page?: number; size?: number },
  ): Promise<ResJobPostWithAssignmentPagination> {
    const res = await axiosInstance.get<RestResponse<ResJobPostWithAssignmentPagination>>(
      `${BASE_URL}/recruiters/${userId}/job-posts`,
      { params },
    )
    return res.data.data
  },

  // ==========================================================================
  // Quản lý theo Tin tuyển dụng
  // ==========================================================================

  /**
   * GET /employer/job-assignments/job-posts/unassigned
   * Lấy danh sách tin chưa được phân công cho member nào.
   */
  async getUnassignedJobPosts(
    params?: UnassignedJobPostQueryParams,
  ): Promise<ResUnassignedJobPostPagination> {
    const res = await axiosInstance.get<RestResponse<ResUnassignedJobPostPagination>>(
      `${BASE_URL}/job-posts/unassigned`,
      { params },
    )
    return res.data.data
  },

  async getJobPostsWithAssignment(
    params?: AssignmentJobPostQueryParams,
  ): Promise<ResJobPostWithAssignmentPagination> {
    const res = await axiosInstance.get<RestResponse<ResJobPostWithAssignmentPagination>>(
      `${BASE_URL}/job-posts`,
      { params },
    )
    return res.data.data
  },

  /**
   * GET /employer/job-assignments/job-posts/{jobPostId}/current
   * Lấy NTD đang được phân công quản lý một tin tuyển dụng cụ thể.
   */
  async getCurrentAssignment(jobPostId: number): Promise<ResJobPostAssignmentDTO> {
    const res = await axiosInstance.get<RestResponse<ResJobPostAssignmentDTO>>(
      `${BASE_URL}/job-posts/${jobPostId}/current`,
    )
    return res.data.data
  },

  // ==========================================================================
  // Thu hồi phân công
  // ==========================================================================

  /**
   * PATCH /employer/job-assignments/revoke
   * Thu hồi phân công tin tuyển dụng.
   */
  async revokeAssignment(payload: ReqRevokeAssignmentDTO): Promise<ResJobPostAssignmentDTO> {
    const res = await axiosInstance.patch<RestResponse<ResJobPostAssignmentDTO>>(
      `${BASE_URL}/revoke`,
      payload,
    )
    return res.data.data
  },
}
