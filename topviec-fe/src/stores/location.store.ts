import { defineStore } from 'pinia'
import { ref } from 'vue'
import { locationService } from '@/services/location.service'
import type { ResLocationDTO, MasterDataQueryParams } from '@/types/masterData.types'
import type { PaginationMeta } from '@/types/common.types'

export const useLocationStore = defineStore('location', () => {
    // ─── State ──────────────────────────────────────────────────────────────────
    const locations = ref<ResLocationDTO[]>([])
    const selectedLocation = ref<ResLocationDTO | null>(null)
    const meta = ref<PaginationMeta>({ page: 0, pageSize: 10, pages: 0, totals: 0 })
    const loading = ref(false)
    const error = ref<string | null>(null)

    // ─── Helpers ────────────────────────────────────────────────────────────────
    function setError(err: unknown) {
        const data = (err as any)?.response?.data
        if (data?.message && typeof data.message === 'object') {
            const firstError = Object.values(data.message)[0]
            error.value = String(firstError)
        } else {
            error.value = data?.message ?? 'Có lỗi xảy ra. Vui lòng thử lại.'
        }
    }

    // ─── Actions ────────────────────────────────────────────────────────────────

    /** Lấy danh sách Location */
    async function fetchLocations(params?: MasterDataQueryParams) {
        loading.value = true
        error.value = null
        try {
            const data = await locationService.getLocations(params)
            locations.value = data.result
            meta.value = data.meta
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /** Lấy chi tiết Location */
    async function fetchLocationById(id: number | string) {
        loading.value = true
        error.value = null
        try {
            selectedLocation.value = await locationService.getLocationById(id)
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /** Reset store */
    function reset() {
        locations.value = []
        selectedLocation.value = null
        meta.value = { page: 0, pageSize: 10, pages: 0, totals: 0 }
        loading.value = false
        error.value = null
    }

    return {
        // state
        locations,
        selectedLocation,
        meta,
        loading,
        error,
        // actions
        fetchLocations,
        fetchLocationById,
        reset,
    }
})
