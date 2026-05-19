// src/composables/useAdminPermission.ts
// Composable tập trung quản lý adminRole và kiểm tra quyền truy cập.
// Cache profile 1 lần duy nhất bằng module-level singleton (tránh gọi API nhiều lần).

import { ref, computed } from 'vue'
import type { AdminRole } from '@/types/adminUser.types'
import { adminUserService } from '@/services/adminUser.service'
import { ROUTE_PERMISSIONS, ACTION_PERMISSIONS } from '@/constants/adminPermissions.constants'

// ─── Singleton state (module-level, share across all component instances) ─────
const _adminRole = ref<AdminRole | null>(null)
const _loading = ref(false)
const _initialized = ref(false)

/** Load adminRole từ API, chỉ gọi 1 lần duy nhất */
async function _loadAdminRole(): Promise<void> {
    if (_initialized.value || _loading.value) return
    _loading.value = true
    try {
        const profile = await adminUserService.getMyProfile()
        _adminRole.value = profile.adminRole
        _initialized.value = true
    } catch {
        // Fallback: không block UI nếu API lỗi
        _initialized.value = true
    } finally {
        _loading.value = false
    }
}

/** Reset cache khi logout */
export function resetAdminPermissionCache(): void {
    _adminRole.value = null
    _initialized.value = false
    _loading.value = false
}

// ─── Composable ───────────────────────────────────────────────────────────────
export function useAdminPermission() {
    const adminRole = computed(() => _adminRole.value)
    const loading = computed(() => _loading.value)
    const isSuperAdmin = computed(() => _adminRole.value === 'super_admin')

    /**
     * Kiểm tra action-level permission.
     * @example can('admin.create') → true nếu role là super_admin
     */
    function can(action: string): boolean {
        if (!_adminRole.value) return false
        const allowed = ACTION_PERMISSIONS[action]
        if (!allowed) return false
        return allowed.includes(_adminRole.value)
    }

    /**
     * Kiểm tra route-level permission theo route name.
     * @example canAccessRoute('admin-orders') → false nếu role là content_moderator
     */
    function canAccessRoute(routeName: string): boolean {
        if (!_adminRole.value) return false
        const allowed = ROUTE_PERMISSIONS[routeName]
        // Nếu route không có trong map → cho phép tất cả (an toàn về phía forward-compat)
        if (!allowed) return true
        return allowed.includes(_adminRole.value)
    }

    /**
     * Khởi tạo: load adminRole nếu chưa có.
     * Gọi trong onMounted hoặc setup() của layout component.
     */
    async function init(): Promise<void> {
        await _loadAdminRole()
    }

    return {
        adminRole,
        loading,
        isSuperAdmin,
        can,
        canAccessRoute,
        init,
    }
}
