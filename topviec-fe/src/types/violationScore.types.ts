import type { CompanyStatus } from '@/constants/company.constants'
import type {
  ComplaintType,
  ViolationSource,
} from '@/constants/complaints.constants'

export interface ReqResetViolationScore {
  note: string
}

export interface ReqAdjustViolationScore {
  pointsToDecrease: number
  note: string
}

export interface ReqUnsuspendViolationScore {
  appealId: number
  note?: string
}

export interface ResViolationScoreCompanyInfo {
  id: number
  name: string
  logoUrl: string | null
  status: CompanyStatus
}

export interface ResViolationScoreInfo {
  totalScore: number
  scoreLevel: 'normal' | 'limited' | 'suspended' | string
  lastGroupBViolationAt: string | null
  lastResetAt: string | null
  resetByAdminName: string | null
  canResetScore: boolean
}

export interface ResViolationLogInfo {
  id: number
  violationType: ComplaintType
  points: number
  source: ViolationSource
  complaintId: number | null
  note: string | null
  createdByAdminName: string | null
  createdAt: string
}

export interface ResViolationScore {
  employerId: number
  employerEmail: string
  company: ResViolationScoreCompanyInfo | null
  score: ResViolationScoreInfo
  history: ResViolationLogInfo[]
}
