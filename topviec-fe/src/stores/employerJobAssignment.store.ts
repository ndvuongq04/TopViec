import { ref } from 'vue'
import { defineStore } from 'pinia'
import { employerJobAssignmentService } from '@/services/employerJobAssignment.service'
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

export const useEmployerJobAssignmentStore = defineStore('employerJobAssignment', () => {

  // ==========================================================================
  // State
  // ==========================================================================

  /** Danh sách NTD kèm số tin đang quản lý */
  const recruiters = ref<ResRecruiterAssignmentPagination | null>(null)

  /** Danh sách tin tuyển dụng kèm thông tin phân công (tab Theo Tin) */
  const jobPostsWithAssignment = ref<ResJobPostWithAssignmentPagination | null>(null)

  /** Danh sách tin đang được giao cho 1 NTD cụ thể (tab Theo Thành viên) */
  const jobPostsByRecruiter = ref<ResJobPostWithAssignmentPagination | null>(null)

  /** Danh sách tin chưa được phân công (dùng cho MemberJobAssignmentModal) */
  const unassignedJobPosts = ref<ResUnassignedJobPostPagination | null>(null)

  /** Thông tin phân công hiện tại của 1 tin cụ thể */
  const currentAssignment = ref<ResJobPostAssignmentDTO | null>(null)

  const loading = ref(false)
  const error = ref<string | null>(null)

  // ==========================================================================
  // Actions – Giao việc
  // ==========================================================================

  /**
   * Phân công tin tuyển dụng cho một NTD.
   * Tự động cập nhật state liên quan sau khi giao thành công.
   */
  async function assignJobPost(payload: ReqAssignJobPostDTO): Promise<ResJobPostAssignmentDTO> {
    loading.value = true
    error.value = null
    try {
      const result = await employerJobAssignmentService.assignJobPost(payload)
      if (currentAssignment.value?.jobPostId === payload.jobPostId) {
        currentAssignment.value = result
      }
      return result
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Không thể phân công. Vui lòng thử lại.'
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * PUT /reassign
   * Đổi người phân công (thu hồi NTD cũ + giao NTD mới trong 1 bước).
   */
  async function reassignJobPost(payload: ReqReassignJobPostDTO): Promise<ResJobPostAssignmentDTO> {
    loading.value = true
    error.value = null
    try {
      const result = await employerJobAssignmentService.reassignJobPost(payload)
      if (currentAssignment.value?.jobPostId === payload.jobPostId) {
        currentAssignment.value = result
      }
      return result
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Không thể đổi người phân công. Vui lòng thử lại.'
      throw err
    } finally {
      loading.value = false
    }
  }

  // ==========================================================================
  // Actions – Theo Recruiter
  // ==========================================================================

  /**
   * GET /recruiters
   * Lấy danh sách NTD trong công ty kèm số tin đang quản lý.
   */
  async function fetchRecruiters(params?: AssignmentRecruiterQueryParams) {
    loading.value = true
    error.value = null
    try {
      recruiters.value = await employerJobAssignmentService.getRecruiters(params)
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Không thể tải danh sách nhà tuyển dụng.'
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * GET /recruiters/{userId}/job-posts
   * Lấy danh sách tin đang được giao cho một NTD.
   */
  async function fetchJobPostsByRecruiter(
    userId: number,
    params?: { page?: number; size?: number },
  ) {
    loading.value = true
    error.value = null
    try {
      jobPostsByRecruiter.value = await employerJobAssignmentService.getJobPostsByRecruiter(userId, params)
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Không thể tải danh sách công việc của thành viên.'
      throw err
    } finally {
      loading.value = false
    }
  }

  // ==========================================================================
  // Actions – Theo Tin tuyển dụng
  // ==========================================================================

  /**
   * GET /job-posts
   * Lấy danh sách tin kèm thông tin phân công.
   */
  async function fetchJobPostsWithAssignment(params?: AssignmentJobPostQueryParams) {
    loading.value = true
    error.value = null
    try {
      jobPostsWithAssignment.value = await employerJobAssignmentService.getJobPostsWithAssignment(params)
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Không thể tải danh sách tin tuyển dụng.'
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * GET /job-posts/{jobPostId}/current
   * Lấy NTD đang quản lý một tin tuyển dụng cụ thể.
   */
  async function fetchCurrentAssignment(jobPostId: number) {
    loading.value = true
    error.value = null
    try {
      currentAssignment.value = await employerJobAssignmentService.getCurrentAssignment(jobPostId)
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Không thể tải thông tin phân công hiện tại.'
      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * GET /job-posts/unassigned
   * Lấy danh sách tin chưa được phân công.
   */
  async function fetchUnassignedJobPosts(params?: UnassignedJobPostQueryParams) {
    loading.value = true
    error.value = null
    try {
      unassignedJobPosts.value = await employerJobAssignmentService.getUnassignedJobPosts(params)
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Không thể tải danh sách tin chưa phân công.'
      throw err
    } finally {
      loading.value = false
    }
  }

  // ==========================================================================
  // Actions – Thu hồi
  // ==========================================================================

  /**
   * PATCH /revoke
   * Thu hồi phân công tin tuyển dụng.
   */
  async function revokeAssignment(payload: ReqRevokeAssignmentDTO): Promise<ResJobPostAssignmentDTO> {
    loading.value = true
    error.value = null
    try {
      const result = await employerJobAssignmentService.revokeAssignment(payload)
      // Clear currentAssignment nếu đang xem đúng tin này
      if (currentAssignment.value?.jobPostId === payload.jobPostId) {
        currentAssignment.value = null
      }
      return result
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Không thể thu hồi phân công. Vui lòng thử lại.'
      throw err
    } finally {
      loading.value = false
    }
  }

  // ==========================================================================
  // Helpers
  // ==========================================================================

  function reset() {
    recruiters.value = null
    jobPostsWithAssignment.value = null
    jobPostsByRecruiter.value = null
    unassignedJobPosts.value = null
    currentAssignment.value = null
    loading.value = false
    error.value = null
  }

  return {
    // State
    recruiters,
    jobPostsWithAssignment,
    jobPostsByRecruiter,
    unassignedJobPosts,
    currentAssignment,
    loading,
    error,

    // Actions
    assignJobPost,
    reassignJobPost,
    fetchRecruiters,
    fetchJobPostsByRecruiter,
    fetchJobPostsWithAssignment,
    fetchCurrentAssignment,
    fetchUnassignedJobPosts,
    revokeAssignment,
    reset,
  }
})
