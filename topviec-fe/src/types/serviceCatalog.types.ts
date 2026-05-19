import type { ResultPaginationDTO } from '@/types/common.types'
import { ServiceCategory } from '@/constants/serviceCatalog.constants'

export { ServiceCategory }

export interface ReqServiceDTO {
    code: string
    name: string
    category: ServiceCategory | null
    unit: string | null
    description: string | null
    isActive: boolean | null
}

export interface ResServiceDTO {
    id: number
    code: string
    name: string
    category: ServiceCategory | null
    categoryName: string | null
    unit: string | null
    description: string | null
    isActive: boolean
    createdAt: string
    updatedAt: string
}

export type ResServicePagination = ResultPaginationDTO<ResServiceDTO>

export interface AdminServiceQueryParams {
    category?: ServiceCategory
    keyword?: string
    page?: number
    size?: number
    sort?: string
}

export interface ReqAddonServiceDTO {
    serviceId: number
    name: string
    code: string
    quantity: number
    durationDays: number | null
    price: number
    description: string | null
    isActive: boolean | null
}

export interface ResAddonServiceDTO {
    id: number
    serviceId: number
    serviceCode: string
    serviceName: string
    serviceCategory: ServiceCategory | null
    serviceCategoryName: string | null
    name: string
    code: string
    quantity: number
    durationDays: number | null
    price: number
    description: string | null
    isActive: boolean
    createdAt: string
    updatedAt: string
}

export interface AdminAddonServiceQueryParams {
    category?: ServiceCategory
    keyword?:  string
    page?:     number
    size?:     number
    sort?:     string
}

export interface ResServicePackageDetailDTO {
    id: number
    serviceId: number
    serviceCode: string
    serviceName: string
    serviceCategory: ServiceCategory | null
    serviceCategoryName: string | null
    serviceUnit: string | null
    quantity: number
}

/** GET /admin/service-catalog/statistics */
export interface ResAdminServiceStatisticsDTO {
    /** Tổng số gói dịch vụ trong hệ thống */
    totalServicePackages: number

    /** Doanh thu trung bình trên mỗi đơn hàng đã thanh toán */
    averageRevenue: number

    /**
     * Tỉ lệ chuyển đổi (%)
     * = (Số công ty có ít nhất 1 đơn hàng đã thanh toán / Tổng số công ty) × 100
     */
    conversionRate: number
}
