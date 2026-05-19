import type { ResultPaginationDTO } from './common.types';
import type { ApplicationStatus, ApplyMethod } from '@/constants/application.constants';

// ─── Request ──────────────────────────────────────────────

export interface ReqApplyJob {
  cvId: number;
  applyMethod: ApplyMethod | string;
}

export interface ReqBulkApply {
  cvId: number;
  jobPostIds: number[];
}

export interface ReqWithdrawApplication {
  withdrawalReason?: string;
}

export interface ReqUpdateApplicationCv {
  cvId: number;
}

export interface ReqInviteFromTalentPool {
  jobPostId: number;
}

// ─── Response ─────────────────────────────────────────────

export interface ResCompanyInfo {
  id: number;
  name: string;
  logoUrl: string;
  isBrandVerified?: boolean | null;
}

export interface ResJobInfo {
  id: number;
  title: string;
  slug: string;
  status: string;
  deadline: string;
  company: ResCompanyInfo;
}

import type { ResCv } from './cvs.types';

export interface ResApplication {
  id: number;
  jobPostId: number;
  candidateUserId: number;
  cvId: number;
  status: ApplicationStatus;
  applyMethod: ApplyMethod;
  withdrawalReason?: string;
  withdrawnAt?: string;
  rejectedAt?: string;
  rejectionReason?: string;
  expiredAt?: string;
  hiredAt?: string;
  viewedAt?: string;
  createdAt: string;
  updatedAt: string;
  jobPosting?: ResJobInfo;
  cv?: ResCv;
}

export type ResApplicationPagination = ResultPaginationDTO<ResApplication>;

export interface ResTalentPoolInviteInfo {
  applicationId: number;
  jobPostId: number;
  jobTitle: string;
  companyName: string;
  companyLogoUrl: string;
}
