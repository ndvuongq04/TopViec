import type { MemberRole, MemberStatus, PermissionChangeType } from '@/constants/companyMember.constants';
import type { ActionItem } from '@/types/role.types';

export interface ResEmployerProfileDTO {
  // Tài khoản
  userId: number
  email: string
  accountStatus: string
  emailVerifiedAt: string | null
  lastLoginAt: string | null
  // Thành viên công ty
  memberId: number
  roleName: MemberRole
  memberStatus: string
  memberCreatedAt: string
  // Công ty
  companyId: number
  companyName: string
  companySlug: string
  companyLogoUrl: string | null
}

export interface ReqAddMember {
    email: string;
    tempPassword: string;
    roleId: number;
}

export interface ReqUpdatePermission {
    roleId: number;
    reason?: string;
}

export interface ResCompanyMember {
    id: number;
    companyId: number;
    userId: number;
    email: string;
    roleId: number;
    roleName: MemberRole | string;
    status: MemberStatus;
    actions: Record<string, boolean>;
    jobTitle: string | null;
    createdAt: string; // ISO 8601
}

export interface ReqBatchMemberPermission {
    userIds: number[];
}

export interface ReqToggleMemberAction {
    enabled: boolean;
}

export interface ResActionSummaryDTO {
    code: string;
    name: string;
}

export interface ResPermissionChangeLogDTO {
    id: number;
    targetUserId: number;
    targetEmail: string;
    changedBy: number;
    changedByEmail: string;
    changeType: PermissionChangeType;
    oldRole: MemberRole | null;
    newRole: MemberRole | null;
    oldPermissions: { grant: ResActionSummaryDTO[]; revoke: ResActionSummaryDTO[] } | null;
    newPermissions: { grant: ResActionSummaryDTO[]; revoke: ResActionSummaryDTO[] } | null;
    reason: string | null;
    createdAt: string;
}

export interface ResMemberPermissionDetail {
    userId: number;
    email: string;
    roleId: number;
    roleName: MemberRole;
    status: string;
    jobTitle: string | null;
    createdAt: string;
    customPermissions: Record<string, boolean>;
    effectivePermissions: ActionItem[];
}

/** GET /employer/member/statistics */
export interface ResEmployerMemberStatisticsDTO {
    /** Tổng số thành viên trong công ty */
    totalMembers: number

    /** Tổng số thành viên đang hoạt động (active) */
    activeMembers: number

    /** Tổng số thành viên chờ xác nhận (pending) */
    pendingMembers: number

    /** Tổng số thành viên đang bị khóa (deactivated) */
    lockedMembers: number
}
