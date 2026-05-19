// src/types/router.types.ts
// Mở rộng meta của Vue Router để hỗ trợ auth guard và phân quyền.

/**
 * Custom route meta (được merge vào `vue-router` RouteMeta qua module augmentation)
 * Dùng trong định nghĩa route: meta: { requiresAuth: true }
 */
export interface CustomRouteMeta {
  /** Route chỉ dành cho user đã đăng nhập; chưa login thì redirect về /login */
  requiresAuth?: boolean
  /** Route chỉ dành cho khách (chưa login); đã login thì redirect về trang chủ */
  requiresGuest?: boolean
  /** Danh sách role được phép truy cập (vd: ['admin', 'recruiter']). Nếu có, ngoài requiresAuth còn check role */
  roles?: string[]
}
