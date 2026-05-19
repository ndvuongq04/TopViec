// src/constants/adminPermissions.constants.ts
// Map phân quyền tập trung cho Admin theo adminRole.
// Đồng bộ với bảng quyền BE: /admin/** endpoints.

import type { AdminRole } from '@/types/adminUser.types'

const ALL: AdminRole[] = ['super_admin', 'content_moderator', 'support_admin', 'finance_admin']
const SA: AdminRole[] = ['super_admin']
const SA_CM: AdminRole[] = ['super_admin', 'content_moderator']
const SA_FA: AdminRole[] = ['super_admin', 'finance_admin']
const SA_CM_SA2: AdminRole[] = ['super_admin', 'content_moderator', 'support_admin']
const NO_FA: AdminRole[] = ['super_admin', 'content_moderator', 'support_admin']

// ─── Quyền truy cập theo route name ──────────────────────────────────────────
export const ROUTE_PERMISSIONS: Record<string, AdminRole[]> = {
    // Dashboard
    'admin-home': ALL,

    // Thống kê (ai cũng được xem)
    'admin-statistics': ALL,

    // Quản lý Admin — chỉ Super Admin
    'admin-manage-admins': SA,
    'admin-detail':        SA,
    'admin-edit':          SA,

    // Công ty / NTD — xem: tất cả; sửa/duyệt: SA, CM; xóa: SA; tạo: SA, Support
    'admin-employers':       ALL,
    'admin-employer-detail': ALL,

    // Ứng viên — tất cả trừ FA
    'admin-candidates':       NO_FA,
    'admin-candidate-detail': NO_FA,

    // Kiểm duyệt nội dung (Job Posting) — tất cả
    'admin-moderation':         ALL,
    'admin-job-posting-detail': ALL,

    // Khiếu nại / Báo cáo — xem: SA, CM, Support; process: SA, CM
    'admin-complaints':       NO_FA,
    'admin-complaint-detail': NO_FA,

    // Đơn hàng — SA + FA
    'admin-orders':       SA_FA,
    'admin-order-detail': SA_FA,

    // Gói & Dịch vụ — danh sách: tất cả; chi tiết/tạo/sửa: SA + FA
    'admin-service-packages':        ALL,    // /admin/service-packages GET (list)
    'admin-service-package-detail':  SA_FA,  // /admin/service-packages/{id} GET
    'admin-individual-services':     ALL,    // /admin/addon-services GET (list)
    'admin-addon-detail':            SA_FA,  // /admin/addon-services/{id} GET
    'admin-service-create':          SA_FA,  // /admin/services POST, PUT

    // Giám sát NTT — tất cả
    'admin-employer-monitor': ALL,

    // CV Templates — tất cả
    'admin-cv-templates':       ALL,
    'admin-cv-template-create': ALL,

    // Cài đặt quyền — chỉ SA
    'admin-settings-permissions': SA,

    // Audit logs — tất cả (dữ liệu được lọc ở BE)
    'admin-audit-logs':       ALL,
    'admin-audit-log-detail': ALL,
}

// ─── Quyền thao tác (action-level) ───────────────────────────────────────────
export const ACTION_PERMISSIONS: Record<string, AdminRole[]> = {
    // Quản lý Admin — chỉ SA
    'admin.create':        SA,
    'admin.update':        SA,
    'admin.toggle-active': SA,
    'admin.delete':        SA,

    // Công ty
    'company.create':  ['super_admin', 'support_admin'],  // tạo employer+company
    'company.update':  SA_CM,                              // duyệt/từ chối/suspend/unsuspend
    'company.delete':  SA,

    // Ứng viên
    'candidate.toggle-status': NO_FA,

    // Khiếu nại
    'report.confirm':  SA_CM,
    'report.process':  SA_CM,

    // Vi phạm NTD
    'violation.view-score':    NO_FA,
    'violation.view-appeals':  NO_FA,
    'violation.reset':         SA_CM,
    'violation.adjust':        SA_CM,
    'employer.unsuspend':      SA_CM,

    // Đơn hàng
    'order.change-status': SA_FA,

    // Dịch vụ (Service Catalog)
    'service.create': SA_FA,
    'service.update': SA_FA,
    'service.detail': SA_FA,

    // Gói dịch vụ (Service Package)
    'package.create': SA_FA,
    'package.update': SA_FA,
    'package.detail': SA_FA,

    // Addon service
    'addon.create': SA_FA,
    'addon.update': SA_FA,
    'addon.detail': SA_FA,
}
