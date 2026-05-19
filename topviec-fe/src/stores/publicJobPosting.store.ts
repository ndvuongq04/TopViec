import { defineStore } from 'pinia'
import { ref } from 'vue'
import { publicJobPostingService } from '@/services/jobPosting.service'
import type {
    ResJobPostingDetail,
    ResJobPostingSummary,
    JobPostingQueryParams,
    PaginationMeta,
} from '@/types/jobPosting.types'

export const usePublicJobPostingStore = defineStore('publicJobPosting', () => {
    // ─── State ──────────────────────────────────────────────────────────────────
    const jobs = ref<ResJobPostingSummary[]>([])
    const selectedJob = ref<ResJobPostingDetail | null>(null)
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

    /** Lấy danh sách tin đang published */
    async function fetchJobs(params?: JobPostingQueryParams) {
        loading.value = true
        error.value = null
        try {
            const data = await publicJobPostingService.getList(params)
            jobs.value = data.result
            meta.value = data.meta
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /** Lấy chi tiết 1 tin tuyển dụng */
    async function fetchJobById(id: number | string) {
        loading.value = true
        error.value = null
        try {
            selectedJob.value = await publicJobPostingService.getById(id)
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /** Reset store (dùng khi unmount / navigate) */
    function reset() {
        jobs.value = []
        selectedJob.value = null
        meta.value = { page: 0, pageSize: 10, pages: 0, totals: 0 }
        loading.value = false
        error.value = null
    }

    return {
        // state
        jobs,
        selectedJob,
        meta,
        loading,
        error,
        // actions
        fetchJobs,
        fetchJobById,
        reset,
    }
})
