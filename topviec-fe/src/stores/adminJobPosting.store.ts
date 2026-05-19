import { defineStore } from 'pinia'
import { ref } from 'vue'
import { adminJobPostingService } from '@/services/jobPosting.service'
import type {
    ReqRejectJobPostingDTO,
    ResJobPostingDetail,
    EmployerJobPostingQueryParams,
    PaginationMeta,
} from '@/types/jobPosting.types'

export const useAdminJobPostingStore = defineStore('adminJobPosting', () => {
    // ─── State ──────────────────────────────────────────────────────────────────
    const jobs = ref<ResJobPostingDetail[]>([])
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

    // ─── Private ────────────────────────────────────────────────────────────────

    /** Thay thế 1 item trong list theo id */
    function _updateInList(updated: ResJobPostingDetail) {
        const idx = jobs.value.findIndex(j => j.id === updated.id)
        if (idx !== -1) jobs.value[idx] = updated
    }

    // ─── Actions ────────────────────────────────────────────────────────────────

    /** Lấy toàn bộ tin của mọi công ty */
    async function fetchJobs(params?: EmployerJobPostingQueryParams) {
        loading.value = true
        error.value = null
        try {
            const data = await adminJobPostingService.getList(params)
            jobs.value = data.result
            meta.value = data.meta
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /** Xem chi tiết bất kỳ tin nào */
    async function fetchJobById(id: number | string) {
        loading.value = true
        error.value = null
        try {
            selectedJob.value = await adminJobPostingService.getById(id)
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /** Duyệt tin tuyển dụng */
    async function approveJob(id: number | string) {
        loading.value = true
        error.value = null
        try {
            const updated = await adminJobPostingService.approveJob(id)
            _updateInList(updated)
            if (selectedJob.value?.id === updated.id) selectedJob.value = updated
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Từ chối tin tuyển dụng */
    async function rejectJob(id: number | string, payload: ReqRejectJobPostingDTO) {
        loading.value = true
        error.value = null
        try {
            const updated = await adminJobPostingService.rejectJob(id, payload)
            _updateInList(updated)
            if (selectedJob.value?.id === updated.id) selectedJob.value = updated
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Gỡ tin vi phạm */
    async function takedownJob(id: number | string, payload: ReqRejectJobPostingDTO) {
        loading.value = true
        error.value = null
        try {
            const updated = await adminJobPostingService.takedownJob(id, payload)
            _updateInList(updated)
            if (selectedJob.value?.id === updated.id) selectedJob.value = updated
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Reset store (dùng khi logout) */
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
        approveJob,
        rejectJob,
        takedownJob,
        reset,
    }
})
