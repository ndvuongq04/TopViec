import {
    OrderItemType,
    OrderStatus,
    OrderType,
    PaymentMethod,
} from '@/constants/servicePackage.constants'
import { BillingCycle } from '@/constants/servicePackage.constants'
import type { PaginationMeta, ResultPaginationDTO } from '@/types/common.types'
import type { ResServicePackageDetailDTO } from '@/types/serviceCatalog.types'

export type { ResServicePackageDetailDTO }

export { OrderItemType, OrderStatus, OrderType, PaymentMethod, BillingCycle }
export type { PaginationMeta, ResultPaginationDTO }

// ─── Request DTOs ─────────────────────────────────────────────────────────────

export interface ReqCreateOrderItemDTO {
    packageId: number
    quantity:  number
}

export interface ReqCreateOrderDTO {
    type:          OrderType
    packageId?:    number            // Legacy single-item (backward compat)
    quantity?:     number            // Legacy single-item (backward compat)
    items?:        ReqCreateOrderItemDTO[]  // New multi-item support
    paymentMethod: PaymentMethod
    payNow?:       boolean
}

export interface ReqRefundOrderDTO {
    reason: string
}

export interface ReqUpdateOrderStatusDTO {
    status: OrderStatus
}

// ─── Response DTOs ────────────────────────────────────────────────────────────

export interface ResOrderItemDTO {
    id:               number
    itemType:         OrderItemType
    servicePackageId: number | null
    addonServiceId:   number | null
    quantity:         number
    unitPrice:        number
    totalPrice:       number
    billingCycle:     BillingCycle | null
    durationDays:     number | null
    packageName:      string | null
    details:          ResServicePackageDetailDTO[] | null
}

export interface ResOrderCompanyInfo {
    name:    string
    logoUrl: string | null
    email:   string | null
    phone:   string | null
}

export interface ResOrderDTO {
    id:                   number
    orderCode:            string
    type:                 OrderType
    totalAmount:          number
    status:               OrderStatus
    paymentMethod:        PaymentMethod
    paymentTransactionId: string | null
    paymentUrl?:          string | null
    paidAt:               string | null
    note:                 string | null
    createdAt:            string
    items:                ResOrderItemDTO[]
    company:              ResOrderCompanyInfo | null

    // Refund fields
    refundEligible?:    boolean
    refundReason?:      string | null
    refundRequestedAt?: string | null
    refundApprovedAt?:  string | null
}

export type ResOrderPagination = ResultPaginationDTO<ResOrderDTO>

// ─── Query Params ─────────────────────────────────────────────────────────────

export interface AdminOrderQueryParams {
    search?:          string
    type?:            string   // BE nhận uppercase: SUBSCRIPTION | ADDON
    status?:          string   // BE nhận uppercase: PAID | PENDING | FAILED | CANCELLED | REFUNDED
    dateFilter?:      string
    startDate?:       string
    endDate?:         string
    failedOrPending?: boolean
    page?:            number
    size?:            number
    sort?:            string
}

export interface EmployerOrderQueryParams {
    search?:     string
    type?:       string   // BE nhận uppercase: SUBSCRIPTION | ADDON
    status?:     string   // BE nhận uppercase: PAID | PENDING | FAILED | CANCELLED | REFUNDED
    dateFilter?: string
    startDate?:  string
    endDate?:    string
    page?:       number
    size?:       number
    sort?:       string
}

export interface EmployerAddonPackageQueryParams {
    category?: string
}

/** GET /admin/orders/statistics */
export interface ResAdminOrderStatisticsDTO {
    /** Tổng số đơn hàng trong hệ thống */
    totalOrders: number

    /** Tổng đơn hàng đã thanh toán (status = PAID) */
    paidOrders: number

    /** Tổng đơn hàng đang chờ xử lý (status = PENDING) */
    pendingOrders: number

    /** Tổng giá trị tất cả đơn hàng đã thanh toán */
    totalRevenue: number
}

export interface ResPaymentUrlDTO {
    paymentUrl: string
}
