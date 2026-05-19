import { WorkType, JobPostingStatus } from '@/constants/jobPosting.constants'
import type { PaginationMeta, ResultPaginationDTO } from '@/types/common.types'

// Re-export để các file khác chỉ cần import 1 chỗ
export { WorkType, JobPostingStatus }


// ─── Sub-DTOs (Request) ───────────────────────────────────────────────────────

export interface ReqJobPostLocationDTO {
    provinceId: number
    addressDetail?: string
    isRemote: boolean
}

export interface ReqJobPostSkillDTO {
    skillId: number
    isRequired: boolean
    proficiencyMin?: number // 1-5
}


// ─── Request DTOs ─────────────────────────────────────────────────────────────

/** POST /employer/job-postings */
export interface ReqCreateJobPostingDTO {
    title: string
    description: string
    requirements: string
    benefits?: string
    industryId: number
    levelId: number
    experienceYearsMin: number
    experienceYearsMax?: number
    salaryMin?: number
    salaryMax?: number
    salaryNegotiable: boolean
    workType: WorkType | string
    headcount: number
    deadline: string // ISO datetime
    locations: ReqJobPostLocationDTO[]
    skills?: ReqJobPostSkillDTO[]
    isFeatured?: boolean
    isUrgent?: boolean
    isHot?: boolean
}

/** PUT /employer/job-postings/{id} — cùng cấu trúc với Create */
export type ReqUpdateJobPostingDTO = ReqCreateJobPostingDTO

/** PATCH .../extend */
export interface ReqExtendJobPostDTO {
    newDeadline: string // ISO datetime
}

/** PATCH /admin/job-postings/{id}/reject | takedown */
export interface ReqRejectJobPostingDTO {
    rejectionReason: string
    moderationNote?: string
}


// ─── Sub-DTOs (Response) ──────────────────────────────────────────────────────

export interface ResJobPostLocationDTO {
    id: number
    name: string
    addressDetail?: string
    isRemote: boolean
    provinceId?: number
}

export interface ResJobPostSkillDTO {
    id: number
    skillId: number
    skillName: string
    isRequired: boolean
    proficiencyMin?: number
}

export interface JobPostCompanyDTO {
    id: number
    name: string
    slug: string
    logoUrl?: string
    isTopEmployer?: boolean
    isBrandVerified?: boolean
    address?: string
}

export interface JobPostIndustryDTO {
    id: number
    name: string
}

export interface JobPostLevelDTO {
    id: number
    name: string
}

export interface JobPostLocationDTO {
    id: number
    name: string
    addressDetail?: string
    isRemote?: boolean
}


// ─── Response DTOs ────────────────────────────────────────────────────────────

export interface ResJobPostingDetail {
    id: number
    title: string
    slug: string
    description: string
    requirements: string
    benefits?: string
    company: JobPostCompanyDTO
    industry: JobPostIndustryDTO
    level: JobPostLevelDTO
    experienceYearsMin: number
    experienceYearsMax?: number
    salaryMin?: number
    salaryMax?: number
    salaryNegotiable: boolean
    workType: string
    headcount: number
    hiredCount?: number
    deadline: string
    status: JobPostingStatus
    isFeatured: boolean
    isUrgent: boolean
    isHot: boolean
    viewCount: number
    applicationCount: number
    editCount: number
    publishedAt?: string
    createdAt: string
    updatedAt: string
    deletedAt?: string | null
    locations: ResJobPostLocationDTO[]
    skills: ResJobPostSkillDTO[]
    interviewRoundsCount?: number
}

export interface ResJobPostingSummary {
    id: number
    title: string
    slug: string
    company: JobPostCompanyDTO
    industry: JobPostIndustryDTO
    level: JobPostLevelDTO
    workType: string
    status: JobPostingStatus
    salaryMin?: number
    salaryMax?: number
    salaryNegotiable: boolean
    isFeatured: boolean
    isUrgent: boolean
    isHot: boolean
    viewCount: number
    applicationCount: number
    headcount: number
    hiredCount: number
    deadline: string
    publishedAt?: string
    createdAt: string
    deletedAt?: string | null
    interviewRoundsCount: number
    locations?: JobPostLocationDTO[]
}


// ─── Paginated ────────────────────────────────────────────────────────────────

export type { PaginationMeta, ResultPaginationDTO }

export type ResJobPostingDetailPagination  = ResultPaginationDTO<ResJobPostingDetail>
export type ResJobPostingSummaryPagination = ResultPaginationDTO<ResJobPostingSummary>


// ─── Query Params ─────────────────────────────────────────────────────────────

export interface JobPostingQueryParams {
    keyword?: string
    companyId?: number
    industryId?: number
    levelId?: number
    workType?: string
    isFeatured?: boolean
    isUrgent?: boolean
    isHot?: boolean
    salaryMin?: number
    salaryMax?: number
    experienceYearsMin?: number
    experienceYearsMax?: number
    page?: number
    size?: number
    sort?: string
}

export interface EmployerJobPostingQueryParams extends JobPostingQueryParams {
    status?: JobPostingStatus
}

/** GET /employer/company/job-statistics */
export interface ResEmployerJobStatisticsDTO {
    /** Tổng số tin tuyển dụng (chưa xóa mềm) của công ty */
    totalJobPosts: number

    /** Tổng số tin đang hoạt động (trạng thái 'published' hoặc 'interviewing') */
    activeJobPosts: number

    /** Tổng số tin đang chờ duyệt (trạng thái 'pending_approval') */
    pendingJobPosts: number

    /** Tổng số tin sắp hết hạn (còn <= 7 ngày, đang trong trạng thái hoạt động) */
    expiringJobPosts: number
}


// ─── Utilities ────────────────────────────────────────────────────────────────

export interface SalaryInfo {
    salaryMin?: number
    salaryMax?: number
    salaryNegotiable: boolean
}

export function formatSalary(job: SalaryInfo): string {
    if (job.salaryNegotiable) return 'Thỏa thuận'
    if (job.salaryMin && job.salaryMax)
        return `${(job.salaryMin / 1_000_000).toFixed(0)}–${(job.salaryMax / 1_000_000).toFixed(0)} triệu/tháng`
    if (job.salaryMin) return `Từ ${(job.salaryMin / 1_000_000).toFixed(0)} triệu/tháng`
    if (job.salaryMax) return `Đến ${(job.salaryMax / 1_000_000).toFixed(0)} triệu/tháng`
    return 'Thỏa thuận'
}

const WORK_TYPE_LABELS: Record<string, string> = {
    FULL_TIME: 'Toàn thời gian',
    PART_TIME: 'Bán thời gian',
    INTERN:    'Thực tập',
    REMOTE:    'Remote',
}

export function formatWorkType(workType: string): string {
    return WORK_TYPE_LABELS[workType] ?? workType
}
