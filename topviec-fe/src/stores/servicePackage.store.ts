import { defineStore } from 'pinia'
import { ref } from 'vue'
import { adminServicePackageService } from '@/services/servicePackage.service'
import type { PaginationMeta } from '@/types/common.types'
import type {
    ReqServicePackageDTO,
    ResServicePackageDTO,
    AdminServicePackageQueryParams,
} from '@/types/servicePackage.types'

export const useServicePackageStore = defineStore('servicePackage', () => {
    // ─── State ──────────────────────────────────────────────────────────────────
    const servicePackages   = ref<ResServicePackageDTO[]>([])
    const selectedPackage   = ref<ResServicePackageDTO | null>(null)
    const meta              = ref<PaginationMeta>({ page: 0, pageSize: 10, pages: 0, totals: 0 })
    const loading           = ref(false)
    const error             = ref<string | null>(null)

    // ─── Helpers ────────────────────────────────────────────────────────────────
    function setError(err: unknown) {
        const data = (err as any)?.response?.data
        if (data?.message && typeof data.message === 'object') {
            error.value = String(Object.values(data.message)[0])
        } else {
            error.value = data?.message ?? 'Có lỗi xảy ra. Vui lòng thử lại.'
        }
    }

    function _updateInList(updated: ResServicePackageDTO) {
        const idx = servicePackages.value.findIndex(p => p.id === updated.id)
        if (idx !== -1) servicePackages.value[idx] = updated
    }

    // ─── Actions ────────────────────────────────────────────────────────────────
    async function fetchServicePackages(params?: AdminServicePackageQueryParams) {
        loading.value = true
        error.value   = null
        try {
            const data = await adminServicePackageService.getAll(params)
            servicePackages.value = data.result
            meta.value            = data.meta
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    async function createServicePackage(payload: ReqServicePackageDTO): Promise<ResServicePackageDTO> {
        loading.value = true
        error.value   = null
        try {
            const created = await adminServicePackageService.create(payload)
            servicePackages.value.unshift(created)
            return created
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    async function updateServicePackage(id: number, payload: ReqServicePackageDTO): Promise<ResServicePackageDTO> {
        loading.value = true
        error.value   = null
        try {
            const updated = await adminServicePackageService.update(id, payload)
            _updateInList(updated)
            if (selectedPackage.value?.id === updated.id) selectedPackage.value = updated
            return updated
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    function reset() {
        servicePackages.value = []
        selectedPackage.value = null
        meta.value            = { page: 0, pageSize: 10, pages: 0, totals: 0 }
        loading.value         = false
        error.value           = null
    }

    return {
        // state
        servicePackages,
        selectedPackage,
        meta,
        loading,
        error,
        // actions
        fetchServicePackages,
        createServicePackage,
        updateServicePackage,
        reset,
    }
})
