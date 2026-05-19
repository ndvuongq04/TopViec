
// ─── Enums ──────────────────────────────────────────────────────────────────
// ⚠️  Đồng bộ với BE enum: com.topviec.topviec_be.enums.services

export enum SubscriptionStatus {
    ACTIVE    = 'active',
    EXPIRED   = 'expired',
    CANCELLED = 'cancelled',
}

export enum JobPostAddonStatus {
    ACTIVE  = 'active',
    EXPIRED = 'expired',
}

export enum BrandingAddonStatus {
    ACTIVE  = 'ACTIVE',
    EXPIRED = 'EXPIRED',
}

// ─── Labels cho UI (SubscriptionStatus / JobPostAddonStatus) ────────────────

export const SUBSCRIPTION_STATUS_LABELS: Record<SubscriptionStatus, string> = {
    [SubscriptionStatus.ACTIVE]:    'Đang hoạt động',
    [SubscriptionStatus.EXPIRED]:   'Hết hạn',
    [SubscriptionStatus.CANCELLED]: 'Đã huỷ',
}

export const JOB_POST_ADDON_STATUS_LABELS: Record<JobPostAddonStatus, string> = {
    [JobPostAddonStatus.ACTIVE]:  'Đang hoạt động',
    [JobPostAddonStatus.EXPIRED]: 'Hết hạn',
}

export const BRANDING_ADDON_STATUS_LABELS: Record<BrandingAddonStatus, string> = {
    [BrandingAddonStatus.ACTIVE]:  'Đang hoạt động',
    [BrandingAddonStatus.EXPIRED]: 'Hết hạn',
}


export enum OrderItemType {
    SUBSCRIPTION = 'subscription',
    ADDON        = 'addon',
}

export enum OrderStatus {
    PENDING          = 'pending',
    PAID             = 'paid',
    FAILED           = 'failed',
    CANCELLED        = 'cancelled',
    REFUND_REQUESTED = 'refund_requested',
    REFUND_REJECTED  = 'refund_rejected',
    REFUNDED         = 'refunded',
}

export enum OrderType {
    SUBSCRIPTION = 'subscription',
    ADDON        = 'addon',
}

export enum PaymentMethod {
    VNPAY = 'vnpay',
    MOMO  = 'momo',
}

// ─── Labels cho UI (Order) ──────────────────────────────────────────────────

export const ORDER_STATUS_LABELS: Record<OrderStatus, string> = {
    [OrderStatus.PENDING]:          'Chờ thanh toán',
    [OrderStatus.PAID]:             'Đã thanh toán',
    [OrderStatus.FAILED]:           'Thất bại',
    [OrderStatus.CANCELLED]:        'Đã huỷ',
    [OrderStatus.REFUND_REQUESTED]: 'Chờ hoàn tiền',
    [OrderStatus.REFUND_REJECTED]:  'Từ chối hoàn tiền',
    [OrderStatus.REFUNDED]:         'Đã hoàn tiền',
}

export const ORDER_TYPE_LABELS: Record<OrderType, string> = {
    [OrderType.SUBSCRIPTION]: 'Gói đăng ký',
    [OrderType.ADDON]:        'Dịch vụ lẻ',
}

export const ORDER_ITEM_TYPE_LABELS: Record<OrderItemType, string> = {
    [OrderItemType.SUBSCRIPTION]: 'Gói đăng ký',
    [OrderItemType.ADDON]:        'Dịch vụ lẻ',
}

export const PAYMENT_METHOD_LABELS: Record<PaymentMethod, string> = {
    [PaymentMethod.VNPAY]: 'VNPay',
    [PaymentMethod.MOMO]:  'MoMo',
}

export enum BillingCycle {
    MONTHLY = 'monthly',  // Hàng tháng
    YEARLY  = 'yearly',   // Hàng năm
}

export const BILLING_CYCLE_LABELS: Record<BillingCycle, string> = {
    [BillingCycle.MONTHLY]: 'Hàng tháng',
    [BillingCycle.YEARLY]:  'Hàng năm',
}

// ─── Service Package Tiers ───────────────────────────────────────────────────

export enum ServicePackageTier {
    FREE       = 'FREE',
    BASIC      = 'BASIC',
    PRO        = 'PRO',
    PREMIUM    = 'PREMIUM',
    ENTERPRISE = 'ENTERPRISE',
}

export const SERVICE_PACKAGE_TIER_LABELS: Record<ServicePackageTier, string> = {
    [ServicePackageTier.FREE]:       'Gói Miễn Phí',
    [ServicePackageTier.BASIC]:      'Gói Cơ Bản',
    [ServicePackageTier.PRO]:        'Gói Pro',
    [ServicePackageTier.PREMIUM]:    'Gói Premium',
    [ServicePackageTier.ENTERPRISE]: 'Gói Doanh Nghiệp',
}


