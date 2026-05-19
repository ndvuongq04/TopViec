import type { MemberRole } from '@/constants/companyMember.constants'

export interface ActionItem {
    name: string
    code: string
    enabled: boolean
}

export interface ResRoleDefaultDTO {
    id: number
    roleName: MemberRole
    actions: ActionItem[]
    updatedAt: string
}

// Request DTOs
export interface ReqToggleAction {
    enabled: boolean
}

export interface ReqRenameAction {
    name: string
}

export interface ReqAddAction {
    name: string
    code: string
}

export interface ResRoleSummaryDTO {
    id: number
    roleName: MemberRole
}

// Alias giữ backward-compat với store cũ
export type ResRoleDefault = ResRoleDefaultDTO
