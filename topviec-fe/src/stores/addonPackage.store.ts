import { defineStore } from 'pinia'
import { ref } from 'vue'
import { adminAddonServiceService } from '@/services/addonPackage.service'
import type { PaginationMeta } from '@/types/common.types'
import type {
    ReqAddonServiceDTO,
    ResAddonServiceDTO,
    AdminAddonServiceQueryParams,
} from '@/types/serviceCatalog.types'

export const useAddonPackageStore = defineStore('addonPackage', () => {
    // ─── State ──────────────────────────────────────────────────────────────────
    const addonPackages  = ref<ResAddonServiceDTO[]>([])
    const selectedAddon  = ref<ResAddonServiceDTO | null>(null)
    const meta           = ref<PaginationMeta>({ page: 0, pageSize: 10, pages: 0, totals: 0 })
    const loading        = ref(false)
    const error          = ref<string | null>(null)

    // ─── Helpers ────────────────────────────────────────────────────────────────
    function setError(err: unknown) {
        const data = (err as any)?.response?.data
        if (data?.message && typeof data.message === 'object') {
            error.value = String(Object.values(data.message)[0])
        } else {
            error.value = data?.message ?? 'Có lỗi xảy ra. Vui lòng thử lại.'
        }
    }

    function _updateInList(updated: ResAddonServiceDTO) {
        const idx = addonPackages.value.findIndex(a => a.id === updated.id)
        if (idx !== -1) addonPackages.value[idx] = updated
    }

    // ─── Actions ────────────────────────────────────────────────────────────────
    async function fetchAddonPackages(params?: AdminAddonServiceQueryParams) {
        loading.value = true
        error.value   = null
        try {
            const data = await adminAddonServiceService.getAll(params)
            addonPackages.value = data.result
            meta.value          = data.meta
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    async function createAddonPackage(payload: ReqAddonServiceDTO): Promise<ResAddonServiceDTO> {
        loading.value = true
        error.value   = null
        try {
            const created = await adminAddonServiceService.create(payload)
            addonPackages.value.unshift(created)
            return created
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    async function updateAddonPackage(id: number, payload: ReqAddonServiceDTO): Promise<ResAddonServiceDTO> {
        loading.value = true
        error.value   = null
        try {
            const updated = await adminAddonServiceService.update(id, payload)
            _updateInList(updated)
            if (selectedAddon.value?.id === updated.id) selectedAddon.value = updated
            return updated
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    function reset() {
        addonPackages.value = []
        selectedAddon.value = null
        meta.value          = { page: 0, pageSize: 10, pages: 0, totals: 0 }
        loading.value       = false
        error.value         = null
    }

    return {
        // state
        addonPackages,
        selectedAddon,
        meta,
        loading,
        error,
        // actions
        fetchAddonPackages,
        createAddonPackage,
        updateAddonPackage,
        reset,
    }
})
