export const MEMBER_ROLE = {
    OWNER: 'owner',
    MANAGER: 'manager',
    RECRUITER: 'recruiter',
    VIEWER: 'viewer',
} as const;

export type MemberRole = (typeof MEMBER_ROLE)[keyof typeof MEMBER_ROLE];

export const MEMBER_STATUS = {
    PENDING: 'pending',
    ACTIVE: 'active',
    DEACTIVATED: 'deactivated',
} as const;

export type MemberStatus = (typeof MEMBER_STATUS)[keyof typeof MEMBER_STATUS];

export const PERMISSION_CHANGE_TYPE = {
    ROLE_CHANGE: 'role_change',
    PERMISSION_UPDATE: 'permission_update',
    STATUS_CHANGE: 'status_change',
} as const;

export type PermissionChangeType = (typeof PERMISSION_CHANGE_TYPE)[keyof typeof PERMISSION_CHANGE_TYPE];

