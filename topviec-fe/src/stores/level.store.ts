import { defineStore } from 'pinia'
import { ref } from 'vue'
import { levelService } from '@/services/level.service'
import type { ResLevelDTO, MasterDataQueryParams } from '@/types/masterData.types'
import type { PaginationMeta } from '@/types/common.types'

export const useLevelStore = defineStore('level', () => {
    // ─── State ──────────────────────────────────────────────────────────────────
    const levels = ref<ResLevelDTO[]>([])
    const selectedLevel = ref<ResLevelDTO | null>(null)
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

    /** Lấy danh sách Level */
    async function fetchLevels(params?: MasterDataQueryParams) {
        loading.value = true
        error.value = null
        try {
            const data = await levelService.getLevels(params)
            levels.value = data.result
            meta.value = data.meta
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /** Lấy chi tiết Level */
    async function fetchLevelById(id: number | string) {
        loading.value = true
        error.value = null
        try {
            selectedLevel.value = await levelService.getLevelById(id)
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /** Reset store */
    function reset() {
        levels.value = []
        selectedLevel.value = null
        meta.value = { page: 0, pageSize: 10, pages: 0, totals: 0 }
        loading.value = false
        error.value = null
    }

    return {
        // state
        levels,
        selectedLevel,
        meta,
        loading,
        error,
        // actions
        fetchLevels,
        fetchLevelById,
        reset,
    }
})
