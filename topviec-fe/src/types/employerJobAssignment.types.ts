import type { ResultPaginationDTO } from './common.types'

// ============================================================================
// REQUEST DTOs
// ============================================================================

/**
 * POST /employer/job-assignments
 * Phân công tin tuyển dụng cho một NTD.
 */
export interface ReqAssignJobPostDTO {
  jobPostId: number
  userId: number
  note?: string
}

/**
 * PUT /employer/job-assignments/reassign
 * Đổi người phân công: thu hồi NTD cũ + giao cho NTD mới trong 1 bước.
 */
export interface ReqReassignJobPostDTO {
  jobPostId: number
  userId: number
  note?: string
}

/**
 * PATCH /employer/job-assignments/revoke
 * Thu hồi phân công tin tuyển dụng.
 */
export interface ReqRevokeAssignmentDTO {
  jobPostId: number
  note?: string
}

// ============================================================================
// QUERY PARAMS
// ============================================================================

export interface AssignmentJobPostQueryParams {
  keyword?: string
  status?: string
  /** true = đã phân công, false = chưa phân công, undefined = tất cả */
  assigned?: boolean
  page?: number
  size?: number
}

export interface AssignmentRecruiterQueryParams {
  keyword?: string
  /** Truyền jobPostId để BE đánh dấu isCurrentAssignee cho NTD đang quản lý tin này */
  jobPostId?: number
  page?: number
  size?: number
}

// ============================================================================
// RESPONSE DTOs
// ============================================================================

/**
 * Response khi tạo / xem / thu hồi phân công.
 * Map từ ResJobPostAssignmentDTO (BE).
 */
export interface ResJobPostAssignmentDTO {
  id: number
  jobPostId: number
  jobPostTitle: string
  jobPostStatus: string
  userId: number
  userEmail: string
  assignedBy: number
  assignedByEmail: string
  assignedAt: string
  revokedAt: string | null
  revokedBy: number | null
  revokedByEmail: string | null
  note: string | null
}

/**
 * Mỗi item trong danh sách NTD kèm số lượng tin đang quản lý.
 * Map từ ResRecruiterWithAssignmentCountDTO (BE).
 */
export interface ResRecruiterWithAssignmentCountDTO {
  userId: number
  email: string
  roleName: string
  jobTitle: string | null
  status: string
  assignedJobCount: number
  /** true nếu đây là NTD đang quản lý tin cụ thể (chỉ có giá trị khi truyền jobPostId) */
  isCurrentAssignee?: boolean
}

// ============================================================================
// PAGINATION ALIASES
// ============================================================================

export type ResRecruiterAssignmentPagination = ResultPaginationDTO<ResRecruiterWithAssignmentCountDTO>

/** Dùng để lấy danh sách job-posts kèm thông tin assignment (từ endpoint /job-posts) */
export type ResJobPostWithAssignmentPagination = ResultPaginationDTO<ResJobPostAssignmentDTO>

// ============================================================================
// UNASSIGNED JOB POSTS
// ============================================================================

export interface ResJobPostingSummaryAssignedRecruiter {
  userId: number
  email: string
  jobTitle: string | null
}

export interface ResJobPostingSummary {
  id: number
  title: string
  slug: string
  status: string
  deadline: string | null
  publishedAt: string | null
  createdAt: string
  assignedRecruiter: ResJobPostingSummaryAssignedRecruiter | null
}

export interface UnassignedJobPostQueryParams {
  keyword?: string
  status?: string
  page?: number
  size?: number
}

export type ResUnassignedJobPostPagination = ResultPaginationDTO<ResJobPostingSummary>
