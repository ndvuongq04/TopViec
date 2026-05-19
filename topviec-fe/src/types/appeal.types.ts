import type { AppealStatus, ComplaintType, ViolationGroup } from '@/constants/complaints.constants'

export interface ReqCreateAppeal {
  content: string
}

export interface ReqSubmitAppeal {
  complaintId: number
  content: string
}

export interface ReqUnsuspendAppeal {
  appealId: number
  note?: string
}

export interface ResAppealAdminInfo {
  adminUserId: number
  fullName: string
}

export interface ResAppealComplaintInfo {
  id: number
  reportCode: string
  complaintType: ComplaintType
  violationGroup: ViolationGroup | null
  status: string
  jobPostId: number
  jobPostTitle: string
  companyName: string
  createdAt: string
}

export interface ResAppeal {
  id: number
  employerId: number
  complaint: ResAppealComplaintInfo
  content: string
  status: AppealStatus
  adminNote: string | null
  reviewedByAdmin: ResAppealAdminInfo | null
  reviewedAt: string | null
  createdAt: string
  updatedAt: string
}
