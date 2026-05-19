// src/router/guards.ts
import type { Router } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'
import { adminUserService } from '@/services/adminUser.service'
import { ROUTE_PERMISSIONS } from '@/constants/adminPermissions.constants'
import type { AdminRole } from '@/types/adminUser.types'

// Cache adminRole tại module-level để tránh gọi API nhiều lần trong guard
let _cachedAdminRole: AdminRole | null = null
let _fetchingAdminRole = false
let _adminRoleFetched = false

export function resetAdminRoleCache(): void {
    _cachedAdminRole = null
    _fetchingAdminRole = false
    _adminRoleFetched = false
}

async function getAdminRole(): Promise<AdminRole | null> {
    if (_adminRoleFetched) return _cachedAdminRole
    if (_fetchingAdminRole) {
        // Đợi nếu đang fetch
        await new Promise<void>(resolve => {
            const interval = setInterval(() => {
                if (!_fetchingAdminRole) { clearInterval(interval); resolve() }
            }, 50)
        })
        return _cachedAdminRole
    }
    _fetchingAdminRole = true
    try {
        const profile = await adminUserService.getMyProfile()
        _cachedAdminRole = profile.adminRole
        _adminRoleFetched = true
    } catch {
        _adminRoleFetched = true
    } finally {
        _fetchingAdminRole = false
    }
    return _cachedAdminRole
}

export function setupGuards(router: Router) {
    router.beforeEach(async (to) => {
        const authStore = useAuthStore()

        // 1. Check Auth
        if (to.meta.requiresAuth && !authStore.isAuthenticated) {
            return { name: 'login', query: { redirect: to.fullPath } }
        }

        // 2. Check Guest (Redirect if already logged in)
        if (to.meta.requiresGuest && authStore.isAuthenticated) {
            const roleHome: Record<string, string> = {
                ADMIN: 'admin-home',
                EMPLOYER: 'recruiter-dashboard',
                CANDIDATE: 'home',
            }
            const role = authStore.userRole ?? 'CANDIDATE'
            return { name: roleHome[role] ?? 'home' }
        }

        // 3. Check Role Permission (user-level role: ADMIN / EMPLOYER / CANDIDATE)
        if (to.meta.role && authStore.isAuthenticated && authStore.userRole !== to.meta.role) {
            return { name: 'forbidden' }
        }

        // 4. Check Admin sub-role permission (adminRole) cho ADMIN routes
        if (authStore.isAuthenticated && authStore.userRole === 'ADMIN' && to.name) {
            const routeName = String(to.name)
            const allowedRoles = ROUTE_PERMISSIONS[routeName]
            if (allowedRoles) {
                // Logout thì reset cache
                const adminRole = await getAdminRole()
                if (!adminRole || !allowedRoles.includes(adminRole)) {
                    return { name: 'forbidden' }
                }
            }
        }
    })
}