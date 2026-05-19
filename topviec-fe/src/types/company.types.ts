import { CompanySize, CompanyStatus, VerificationStatus } from '@/constants/company.constants'
import { BillingCycle, SubscriptionStatus } from '@/constants/servicePackage.constants'
import type { PaginationMeta, ResultPaginationDTO } from '@/types/common.types'

// Re-export để các file khác chỉ cần import từ 1 chỗ
export { CompanySize, CompanyStatus, VerificationStatus }

// ─── Request DTOs ─────────────────────────────────────────────────────────────

/** POST /employer/company */
export interface ReqCreateCompanyDTO {
    slug: string
    name: string
    password?: string
    logoUrl?: string | null
    coverUrl?: string | null
    description: string
    industryId: number
    companySize: CompanySize
    foundedYear?: number | null
    website?: string | null
    email?: string | null
    phone?: string | null
    address?: string | null
    provinceId?: number | null
    taxCode?: string | null
    businessLicenseUrl?: string | null
    culture?: string | null
    benefits?: string | null
    socialLinks?: string | null
}

/** PATCH /employer/companies/{id} */
export interface ReqUpdateCompanyDTO {
    slug?: string | null
    name?: string | null
    logoUrl?: string | null
    coverUrl?: string | null
    description?: string | null
    industryId?: number | null
    companySize?: CompanySize | null
    foundedYear?: number | null
    website?: string | null
    email?: string | null
    phone?: string | null
    address?: string | null
    provinceId?: number | null
    taxCode?: string | null
    businessLicenseUrl?: string | null
    culture?: string | null
    benefits?: string | null
    socialLinks?: string | null
}

/**  PATCH /admin/companies/{id} */
export interface ReqAdminUpdateCompanyDTO {
    slug?: string | null
    name?: string | null
    logoUrl?: string | null
    coverUrl?: string | null
    description?: string | null
    industryId?: number | null
    companySize?: CompanySize | null
    foundedYear?: number | null
    website?: string | null
    email?: string | null
    phone?: string | null
    address?: string | null
    provinceId?: number | null
    taxCode?: string | null
    businessLicenseUrl?: string | null
    culture?: string | null
    benefits?: string | null
    socialLinks?: string | null

    action?: 'verify' | 'suspend' | 'unsuspend'
    approved?: boolean
    rejectionReason?: string | null
    suspendedReason?: string | null
}


/** PATCH /admin/companies/{id}/verify */
export interface ReqVerifyCompanyDTO {
    approved: boolean
    rejectionReason?: string | null
}

/** PATCH /admin/companies/{id}/suspend */
export interface ReqSuspendCompanyDTO {
    suspendedReason: string
}

// ─── Response DTO ─────────────────────────────────────────────────────────────

export interface ResCompanyDTO {
    id: number
    slug: string
    name: string
    logoUrl: string | null
    coverUrl: string | null
    description: string
    industryId: number | null
    industryName: string | null
    jobCount: number | null
    companySize: CompanySize | null
    foundedYear: number | null

    website: string | null
    email: string | null
    phone: string | null
    address: string | null
    provinceId: number | null

    taxCode: string | null
    businessLicenseUrl: string | null

    culture: string | null
    benefits: string | null
    socialLinks: string | null


    // Dịch vụ Branding đang active
    isBanner: boolean | null
    isTopEmployer: boolean | null
    isBrandVerified: boolean | null

    verificationStatus: VerificationStatus
    verifiedAt: string | null
    verifiedBy: number | null
    rejectionReason: string | null


    status: CompanyStatus
    violationScore: number | null
    suspendedAt: string | null
    suspendedReason: string | null


    createdBy: number | null
    createdAt: string
    updatedAt: string
}

/** GET /admin/companies/{id}/statistics */
export interface ResAdminCompanyStatisticsDTO {
    /** Tổng số tin tuyển dụng đã đăng (chưa bị xóa mềm) */
    totalJobPostings: number

    /** Tổng số CV/đơn ứng tuyển đã nhận (chưa bị xóa mềm) */
    totalApplicationsReceived: number

    /** Danh sách gói dịch vụ đang sử dụng (status = ACTIVE) */
    activeSubscriptions: ActiveSubscriptionDTO[]
}

export interface ActiveSubscriptionDTO {
    subscriptionId: number
    servicePackageId: number
    packageName: string
    packageCode: string
    billingCycle: BillingCycle
    status: SubscriptionStatus
    startedAt: string
    expiredAt: string
}

/** GET /admin/companies/{id}/plan */
export interface UsageDTO {
    featureCode: string
    featureName: string
    total: number
    used: number
}

export interface CurrentPackageDTO {
    subscriptionId: number
    servicePackageId: number
    packageName: string
    packageCode: string
    billingCycle: string
    status: string
    startedAt: string
    expiredAt: string
    orderId: number
    orderCode: string
    usages: UsageDTO[]
}

export interface CurrentAddonDTO {
    addonId: number
    addonServiceId: number
    addonName: string
    addonCode: string
    serviceCategory: string
    serviceCategoryName: string
    status: string
    total: number
    used: number
    startedAt: string
    expiredAt: string
    orderId: number
    orderCode: string
}

export interface ResCompanyPlanDTO {
    currentPackage: CurrentPackageDTO | null
    currentAddons: CurrentAddonDTO[]
}

export interface ResSubscriptionHistoryDTO {
    subscriptionId: number
    companyId: number
    orderId: number
    servicePackageId: number
    packageName: string
    packageCode: string
    status: string
    billingCycle: string
    startedAt: string
    expiredAt: string
    purchasedAt: string
    packagePrice: number
}

export type { PaginationMeta, ResultPaginationDTO }




export function parseBenefits(raw: string | null): string[] {
    if (!raw) return []
    try { return JSON.parse(raw) } catch { return [] }
}


export function parseSocialLinks(raw: string | null): Record<string, string> {
    if (!raw) return {}
    try { return JSON.parse(raw) } catch { return {} }
}


export function stringifyBenefits(arr: string[]): string {
    return JSON.stringify(arr)
}


export function stringifySocialLinks(obj: Record<string, string>): string {
    return JSON.stringify(obj)
}