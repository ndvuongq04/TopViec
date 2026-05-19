// src/types/adminUser.types.ts

// ─── Admin Role Enum ─────────────────────────────────────────────────────────
export type AdminRole = 'super_admin' | 'content_moderator' | 'support_admin' | 'finance_admin'

export const ADMIN_ROLE_LABELS: Record<AdminRole, string> = {
    super_admin: 'Super Admin',
    content_moderator: 'Content Moderator',
    support_admin: 'Support Admin',
    finance_admin: 'Finance Admin',
}

export const ADMIN_ROLE_STYLES: Record<AdminRole, { bg: string; text: string; border: string }> = {
    super_admin: {
        bg: 'bg-purple-100 dark:bg-purple-900/30',
        text: 'text-purple-700 dark:text-purple-400',
        border: 'border-purple-200 dark:border-purple-800',
    },
    content_moderator: {
        bg: 'bg-blue-100 dark:bg-blue-900/30',
        text: 'text-blue-700 dark:text-blue-400',
        border: 'border-blue-200 dark:border-blue-800',
    },
    support_admin: {
        bg: 'bg-yellow-100 dark:bg-yellow-900/30',
        text: 'text-yellow-700 dark:text-yellow-400',
        border: 'border-yellow-200 dark:border-yellow-800',
    },
    finance_admin: {
        bg: 'bg-teal-100 dark:bg-teal-900/30',
        text: 'text-teal-700 dark:text-teal-400',
        border: 'border-teal-200 dark:border-teal-800',
    },
}

// ─── Response DTO ─────────────────────────────────────────────────────────────
export interface ResAdminUser {
    userId: number
    email: string
    adminUsersId: number
    adminRole: AdminRole
    fullName: string
    department: string | null
    isActive: boolean
    createdAt: string          // ISO date-time from BE
    createdBy: number | null
    updatedAt: string | null
    updatedBy: number | null
}

// ─── Request DTOs ─────────────────────────────────────────────────────────────
export interface ReqCreateAdmin {
    email: string
    password: string
    adminRole: AdminRole
    fullName: string
    department?: string
}

export interface ReqUpdateAdmin {
    adminRole?: AdminRole
    fullName?: string
    department?: string
}
