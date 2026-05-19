// src/stores/adminUser.store.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { adminUserService } from '@/services/adminUser.service'
import type {
    ResAdminUser,
    ReqCreateAdmin,
    ReqUpdateAdmin,
} from '@/types/adminUser.types'
import type { PaginationMeta, ResultPaginationDTO } from '@/types/common.types'

export const useAdminUserStore = defineStore('adminUser', () => {
    // ─── State ──────────────────────────────────────────────────────────────────
    const admins = ref<ResAdminUser[]>([])
    const selectedAdmin = ref<ResAdminUser | null>(null)
    const meta = ref<PaginationMeta>({ page: 0, pageSize: 10, pages: 0, totals: 0 })
    const loading = ref(false)
    const error = ref<string | null>(null)

    // ─── Helpers ────────────────────────────────────────────────────────────────
    function setError(err: unknown) {
        const msg = (err as any)?.response?.data?.message
        if (typeof msg === 'object' && msg !== null) {
            error.value = Object.values(msg).join(', ')
        } else {
            error.value = msg ?? 'Có lỗi xảy ra. Vui lòng thử lại.'
        }
    }

    function applyPagination(data: ResultPaginationDTO<ResAdminUser>) {
        admins.value = data.result
        meta.value = data.meta
    }

    // ─── Actions ────────────────────────────────────────────────────────────────

    /** GET /admin/users — danh sách admin */
    async function fetchAllAdmins(params?: {
        keyword?: string
        adminRole?: string
        page?: number
        size?: number
        sort?: string
    }) {
        loading.value = true
        error.value = null
        try {
            const data = await adminUserService.getAllAdmins(params)
            applyPagination(data)
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /** GET /admin/users/{id} — chi tiết admin */
    async function fetchAdminById(id: number) {
        loading.value = true
        error.value = null
        try {
            selectedAdmin.value = await adminUserService.getAdminById(id)
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /** POST /admin/users — tạo admin mới */
    async function createAdmin(payload: ReqCreateAdmin) {
        loading.value = true
        error.value = null
        try {
            const created = await adminUserService.createAdmin(payload)
            admins.value.unshift(created)
            return created
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** PUT /admin/users/{id} — cập nhật admin */
    async function updateAdmin(id: number, payload: ReqUpdateAdmin) {
        loading.value = true
        error.value = null
        try {
            const updated = await adminUserService.updateAdmin(id, payload)
            _updateInList(updated)
            if (selectedAdmin.value?.adminUsersId === id) selectedAdmin.value = updated
            return updated
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** PATCH /admin/users/{id}/toggle-active — khóa / mở khóa */
    async function toggleActive(id: number) {
        loading.value = true
        error.value = null
        try {
            const updated = await adminUserService.toggleActive(id)
            _updateInList(updated)
            if (selectedAdmin.value?.adminUsersId === id) selectedAdmin.value = updated
            return updated
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** DELETE /admin/users/{id} — xóa admin */
    async function deleteAdmin(id: number) {
        loading.value = true
        error.value = null
        try {
            await adminUserService.deleteAdmin(id)
            admins.value = admins.value.filter(a => a.adminUsersId !== id)
            if (selectedAdmin.value?.adminUsersId === id) selectedAdmin.value = null
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    // ─── Private helpers ────────────────────────────────────────────────────────
    function _updateInList(updated: ResAdminUser) {
        const idx = admins.value.findIndex(a => a.adminUsersId === updated.adminUsersId)
        if (idx !== -1) admins.value[idx] = updated
    }

    function reset() {
        admins.value = []
        selectedAdmin.value = null
        meta.value = { page: 0, pageSize: 10, pages: 0, totals: 0 }
        loading.value = false
        error.value = null
    }

    return {
        admins,
        selectedAdmin,
        meta,
        loading,
        error,
        fetchAllAdmins,
        fetchAdminById,
        createAdmin,
        updateAdmin,
        toggleActive,
        deleteAdmin,
        reset,
    }
})
