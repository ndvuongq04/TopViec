import { defineStore } from 'pinia'
import { ref } from 'vue'
import { employerJobPostingService } from '@/services/employerJobPosting.service'
import type {
    ReqCreateJobPostingDTO,
    ReqUpdateJobPostingDTO,
    ReqExtendJobPostDTO,
    ResJobPostingDetail,
    EmployerJobPostingQueryParams,
    PaginationMeta,
    ResEmployerJobStatisticsDTO,
} from '@/types/jobPosting.types'

export const useEmployerJobPostingStore = defineStore('employerJobPosting', () => {
    // ─── State ──────────────────────────────────────────────────────────────────
    const jobs = ref<ResJobPostingDetail[]>([])
    const selectedJob = ref<ResJobPostingDetail | null>(null)
    const jobStatistics = ref<ResEmployerJobStatisticsDTO | null>(null)
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

    /** Tạo tin tuyển dụng mới */
    async function createJob(payload: ReqCreateJobPostingDTO): Promise<ResJobPostingDetail> {
        loading.value = true
        error.value = null
        try {
            const created = await employerJobPostingService.createJob(payload)
            jobs.value.unshift(created)
            return created
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Lấy danh sách tin của công ty đang đăng nhập */
    async function fetchJobs(params?: EmployerJobPostingQueryParams) {
        loading.value = true
        error.value = null
        try {
            const data = await employerJobPostingService.getList(params)
            jobs.value = data.result
            meta.value = data.meta
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /** Lấy chi tiết 1 tin */
    async function fetchJobById(id: number | string) {
        loading.value = true
        error.value = null
        try {
            selectedJob.value = await employerJobPostingService.getById(id)
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /** Cập nhật nội dung tin */
    async function updateJob(id: number | string, payload: ReqUpdateJobPostingDTO): Promise<ResJobPostingDetail> {
        loading.value = true
        error.value = null
        try {
            const updated = await employerJobPostingService.updateJob(id, payload)
            _updateInList(updated)
            if (selectedJob.value?.id === updated.id) selectedJob.value = updated
            return updated
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Gửi duyệt tin */
    async function pendingApproval(id: number | string) {
        loading.value = true
        error.value = null
        try {
            const updated = await employerJobPostingService.pendingApproval(id)
            _updateInList(updated)
            if (selectedJob.value?.id === updated.id) selectedJob.value = updated
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Tạm dừng tin */
    async function pauseJob(id: number | string) {
        loading.value = true
        error.value = null
        try {
            const updated = await employerJobPostingService.pauseJob(id)
            _updateInList(updated)
            if (selectedJob.value?.id === updated.id) selectedJob.value = updated
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Mở lại tin đã tạm dừng */
    async function resumeJob(id: number | string) {
        loading.value = true
        error.value = null
        try {
            const updated = await employerJobPostingService.resumeJob(id)
            _updateInList(updated)
            if (selectedJob.value?.id === updated.id) selectedJob.value = updated
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Đóng tin */
    async function closeJob(id: number | string) {
        loading.value = true
        error.value = null
        try {
            const updated = await employerJobPostingService.closeJob(id)
            _updateInList(updated)
            if (selectedJob.value?.id === updated.id) selectedJob.value = updated
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Gia hạn tin */
    async function extendJob(id: number | string, payload: ReqExtendJobPostDTO) {
        loading.value = true
        error.value = null
        try {
            const updated = await employerJobPostingService.extendJob(id, payload)
            _updateInList(updated)
            if (selectedJob.value?.id === updated.id) selectedJob.value = updated
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Làm mới tin — đẩy lên đầu danh sách */
    async function refreshJob(id: number | string) {
        loading.value = true
        error.value = null
        try {
            const updated = await employerJobPostingService.refreshJob(id)
            _updateInList(updated)
            if (selectedJob.value?.id === updated.id) selectedJob.value = updated
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Xóa mềm tin tuyển dụng */
    async function deleteJob(id: number | string) {
        loading.value = true
        error.value = null
        try {
            await employerJobPostingService.deleteJob(id)
            jobs.value = jobs.value.filter(j => j.id !== Number(id))
            if (selectedJob.value?.id === Number(id)) selectedJob.value = null
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Khôi phục tin đã xóa mềm */
    async function restoreJob(id: number | string) {
        loading.value = true
        error.value = null
        try {
            const updated = await employerJobPostingService.restoreJob(id)
            _updateInList(updated)
            if (selectedJob.value?.id === updated.id) selectedJob.value = updated
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Lấy thống kê tin tuyển dụng */
    async function fetchStatistics() {
        loading.value = true
        error.value = null
        try {
            jobStatistics.value = await employerJobPostingService.getJobStatistics()
        } catch (err) {
            setError(err)
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
        createJob,
        fetchJobs,
        fetchJobById,
        updateJob,
        pendingApproval,
        pauseJob,
        resumeJob,
        closeJob,
        extendJob,
        refreshJob,
        deleteJob,
        restoreJob,
        fetchStatistics,
        jobStatistics,
        reset,
    }
})
