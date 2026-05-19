import type { ResultPaginationDTO } from './common.types'

// ─── List Response DTO ─────────────────────────────────────────────
export interface ResAdminCandidateDTO {
    id: number // User ID
    fullName: string
    email: string
    phoneDisplay: string
    status: string // UserStatus string
    avatarUrl: string
    jobSeekingStatus: string
    createdAt: string // LocalDateTime mapped to ISO string
}

// ─── Detail Response DTO ───────────────────────────────────────────
export interface ResAdminCandidateDetailDTO {
    id: number // User ID
    fullName: string
    email: string
    phoneDisplay: string
    status: string // UserStatus
    avatarUrl: string
    jobSeekingStatus: string
    dateOfBirth: string // LocalDate: "YYYY-MM-DD"
    gender: string
    bio: string
    linkedinUrl: string
    githubUrl: string
    personalWebsite: string
    expectedSalaryMin: number
    expectedSalaryMax: number
    salaryNegotiable: boolean
    preferredJobTitle: string
    preferredWorkType: string
    preferredLocationId: number
    preferredLocationName: string
    profileCompletionPct: number
    isCvPublic: boolean
    createdAt: string // LocalDateTime: ISO string
}

// ─── Pagination Types ─────────────────────────────────────────────
export type ResAdminCandidatePagination = ResultPaginationDTO<ResAdminCandidateDTO>

// ─── Query Params ─────────────────────────────────────────────────
export interface AdminCandidateQueryParams {
    status?: string
    keyword?: string
    page?: number
    size?: number
    sort?: string
}

// ─── Statistics DTO ───────────────────────────────────────────────
export interface ResAdminCandidateStatisticsDTO {
    totalCvs: number
    totalApplications: number
    totalFollowedCompanies: number
    totalSavedJobs: number
}
