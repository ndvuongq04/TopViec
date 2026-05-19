import type { ResultPaginationDTO } from './common.types';
import type {
  ComplaintStatus,
  ComplaintPriority,
  ComplaintType,
  EvidenceFileType,
  ViolationGroup,
} from '@/constants/complaints.constants';

// ─── Request ──────────────────────────────────────────────────

export interface ReqCreateReport {
  jobPostId: number;
  complaintType: ComplaintType;
  description?: string;
  evidences?: ReqEvidenceItem[];
}

export interface ReqEvidenceItem {
  fileUrl: string;
  fileType: EvidenceFileType;
}

export interface ReqProcessReport {
  decision: 'approve' | 'reject';
  resolutionNote?: string;
}

export interface ReqConfirmReport {
  approved: boolean;
}

export interface ReqGetAdminReports {
  search?: string;
  status?: ComplaintStatus;
  group?: ViolationGroup;
  complaintType?: ComplaintType;
  fromDate?: string;
  toDate?: string;
  page?: number;
  size?: number;
  sort?: string;
}

export interface ReqGetMyReports {
  status?: ComplaintStatus;
  page?: number;
  size?: number;
  sort?: string;
}

export interface ReqGetEmployerReports {
  search?: string;
  status?: ComplaintStatus;
  group?: ViolationGroup;
  complaintType?: ComplaintType;
  page?: number;
  size?: number;
  sort?: string;
}

export interface ReqGetReportsByComplaint {
  status?: ComplaintStatus;
  group?: ViolationGroup;
  complaintType?: ComplaintType;
  page?: number;
  size?: number;
  sort?: string;
}

export interface ReqGetReportsByJobPost {
  status?: ComplaintStatus;
  group?: ViolationGroup;
  complaintType?: ComplaintType;
  page?: number;
  size?: number;
  sort?: string;
}

// ─── Response ─────────────────────────────────────────────────

export interface ResReporterInfo {
  userId: number;
  fullName: string;
  email: string;
}

export interface ResCompanyInfo {
  id: number;
  name: string;
  logoUrl: string;
  status: string;
  violationScore: number;
}

export interface ResJobInfo {
  id: number;
  title: string;
  status: string;
  company: ResCompanyInfo;
}

export interface ResAssignedAdminInfo {
  adminUserId: number;
  fullName: string;
  adminRole: string;
}

export interface ResEvidenceInfo {
  id: number;
  fileUrl: string;
  fileType: EvidenceFileType;
  createdAt: string;
}

export interface ResReportDetail {
  id: number;
  reportCode: string;
  complaintType: ComplaintType;
  violationGroup: ViolationGroup | null;
  priority: ComplaintPriority;
  status: ComplaintStatus;
  description: string | null;
  resolutionNote: string | null;
  createdAt: string;
  updatedAt: string;
  resolvedAt: string | null;
  emailSentAt: string | null;
  employerDeadline: string | null;
  employerRespondedAt: string | null;
  remainingProcessingHours: number | null;
  totalAllowedProcessingHours: number | null;
  reporter: ResReporterInfo;
  jobPosting: ResJobInfo;
  assignedAdmin: ResAssignedAdminInfo | null;
  evidences: ResEvidenceInfo[];
}

export interface ResReportSummary {
  id: number;
  reportCode: string;
  reporterUserId: number;
  reporterName: string;
  jobPostId: number;
  jobPostTitle: string;
  companyId: number;
  companyName: string;
  complaintType: ComplaintType;
  violationGroup: ViolationGroup | null;
  priority: ComplaintPriority;
  status: ComplaintStatus;
  createdAt: string;
  processingDeadline: string | null;
  remainingProcessingHours: number | null;
  totalAllowedProcessingHours: number | null;
  assignedAdminName: string | null;
  jobPosting?: ResJobInfo;
  jobPost?: ResJobInfo;
  company?: ResCompanyInfo;
}

export interface ResViolationReason {
  code: string;
  name: string;
  group: ViolationGroup;
  requiresEvidence: boolean;
  priority: ComplaintPriority;
}

export interface ResEmployerReportJobPostInfo {
  id: number;
  title: string;
  status: string;
}

export interface ResEmployerReportSummary {
  id: number;
  reportCode: string;
  jobPost: ResEmployerReportJobPostInfo;
  complaintType: ComplaintType;
  violationGroup: ViolationGroup | null;
  priority: ComplaintPriority;
  status: ComplaintStatus;
  employerDeadline: string | null;
  remainingHours: number | null;
  createdAt: string;
}

export interface ResEmployerReportDetail {
  id: number;
  reportCode: string;
  jobPost: ResEmployerReportJobPostInfo;
  complaintType: ComplaintType;
  violationGroup: ViolationGroup | null;
  priority: ComplaintPriority;
  status: ComplaintStatus;
  description: string | null;
  emailSentAt: string | null;
  employerDeadline: string | null;
  remainingHours: number | null;
  employerRespondedAt: string | null;
  resolutionNote: string | null;
  resolvedAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface ResMyViolationScore {
  totalScore: number;
  scoreLevel: 'normal' | 'limited' | 'suspended' | string;
  restrictionDescription: string | null;
  lastGroupBViolationAt: string | null;
  canRequestReset: boolean;
  companyStatus: string | null;
}

export interface ResCandidateReportJobPostInfo {
  id: number;
  title: string;
}

export interface ResCandidateReportCompanyInfo {
  id: number;
  name: string;
  logoUrl: string | null;
}

export interface ResCandidateReportSummary {
  id: number;
  reportCode: string;
  jobPost: ResCandidateReportJobPostInfo;
  company: ResCandidateReportCompanyInfo;
  complaintType: ComplaintType;
  status: ComplaintStatus;
  createdAt: string;
}

export type ResReportPagination = ResultPaginationDTO<ResReportSummary>;
export type ResEmployerReportPagination = ResultPaginationDTO<ResEmployerReportSummary>;
export type ResCandidateReportPagination = ResultPaginationDTO<ResCandidateReportSummary>;

/** GET /admin/reports/statistics */
export interface ResAdminReportStatisticsDTO {
  /** Tổng số báo cáo trong hệ thống (chưa bị xóa mềm) */
  totalReports: number;

  /** Số báo cáo đang chờ xử lý (status = pending) */
  pendingReports: number;

  /** Số báo cáo nhóm B — vi phạm nặng (violation_group = B, chưa xóa mềm) */
  groupBReports: number;

  /** Số báo cáo quá hạn SLA — chưa xử lý xong mà đã quá deadline */
  slaOverdueReports: number;
}
