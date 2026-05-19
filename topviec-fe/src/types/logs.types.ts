import type { ResultPaginationDTO } from './common.types'
import { LogCategory, Severity, LogActionType } from '@/constants/logs.constants'

// ─── Audit Log Types ───────────────────────────────────────────────
export interface ResAuditLogDTO {
  id: number
  userId: number
  userEmail: string
  userRole: string
  action: LogActionType
  category: LogCategory
  severity: Severity
  targetEntity: string
  targetId: number
  targetName?: string
  status: string
  durationMs: number
  createdAt: string
}

export interface ResAuditLogDetailDTO extends ResAuditLogDTO {
  description: string
  ipAddress: string
  userAgent: string
  errorMessage: string
}

export type ResAuditLogPagination = ResultPaginationDTO<ResAuditLogDTO>

// ─── Business Event Log Types ──────────────────────────────────────
export interface ResBusinessEventLogDTO {
  id: number
  userId: number
  userEmail: string
  userRole: string
  action: LogActionType
  category: LogCategory
  targetEntity: string
  targetId: number
  targetName?: string
  status: string
  durationMs: number
  createdAt: string
}

export interface ResBusinessEventLogDetailDTO extends ResBusinessEventLogDTO {
  metadata: Record<string, any>
}

export type ResBusinessEventLogPagination = ResultPaginationDTO<ResBusinessEventLogDTO>

// ─── Query Parameters ─────────────────────────────────────────────
export interface LogQueryParams {
  userId?: number
  memberId?: number // Cho employer side
  action?: string
  category?: string
  severity?: string
  status?: string
  keyword?: string
  userRole?: string
  startDate?: string // YYYY-MM-DD
  endDate?: string   // YYYY-MM-DD
  page?: number
  size?: number
  sort?: string
}

// ─── Admin Log Statistics ─────────────────────────────────────────
export interface ResAdminLogStatisticsDTO {
  totalLogs: number
  criticalLogs: number
  systemErrors: number
  activeAdmins: number
}

// ─── Employer Log Statistics ──────────────────────────────────────
export interface ResEmployerLogStatisticsDTO {
  totalActivity: number
  candidateProcessing: number
  dataUpdates: number
  activeMembers: number
}
