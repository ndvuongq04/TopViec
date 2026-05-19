// src/stores/publicInterview.store.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import publicInterviewService from '@/services/publicInterview.service'
import type {
    ResInterviewScheduleDTO,
    ResInterviewHistoryDTO,
    ResInterviewRoundDTO,
    ResConfirmUpdateInfoDTO,
    ResSlotSelectionPageDTO,
} from '@/types/interview.types'

export const usePublicInterviewStore = defineStore('publicInterview', () => {
    // ─── State ──────────────────────────────────────────────────────────────────
    const myInterviews = ref<ResInterviewScheduleDTO[]>([])
    const interviewHistory = ref<ResInterviewHistoryDTO | null>(null)
    const roundDetail = ref<ResInterviewRoundDTO | null>(null)
    const confirmUpdateInfo = ref<ResConfirmUpdateInfoDTO | null>(null)
    const slotSelectionPage = ref<ResSlotSelectionPageDTO | null>(null)
    const loading = ref(false)
    const error = ref<string | null>(null)

    // ─── Helpers ────────────────────────────────────────────────────────────────
    function setError(err: unknown) {
        error.value =
            (err as any)?.response?.data?.message ?? 'Có lỗi xảy ra. Vui lòng thử lại.'
    }

    // ─── Actions ────────────────────────────────────────────────────────────────

    /** Lấy danh sách lịch phỏng vấn của ứng viên trong đơn ứng tuyển */
    async function fetchMyInterviews(applicationId: number) {
        loading.value = true
        error.value = null
        try {
            myInterviews.value = await publicInterviewService.getMyInterviews(applicationId)
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Lấy lịch sử phỏng vấn của ứng viên trong đơn ứng tuyển */
    async function fetchMyInterviewHistory(applicationId: number) {
        loading.value = true
        error.value = null
        try {
            interviewHistory.value = await publicInterviewService.getMyInterviewHistory(applicationId)
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Lấy thông tin chi tiết 1 vòng phỏng vấn */
    async function fetchRoundDetail(roundId: number) {
        loading.value = true
        error.value = null
        try {
            roundDetail.value = await publicInterviewService.getRoundDetail(roundId)
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Lấy thông tin lịch PV trước khi UV xác nhận (không cần đăng nhập) */
    async function fetchConfirmUpdateInfo(token: string) {
        loading.value = true
        error.value = null
        try {
            confirmUpdateInfo.value = await publicInterviewService.getConfirmUpdateInfo(token)
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** UV đã đăng nhập xác nhận lịch PV trực tiếp trên hệ thống */
    async function confirmScheduleByCandidate(scheduleId: number) {
        loading.value = true
        error.value = null
        try {
            return await publicInterviewService.confirmScheduleByCandidate(scheduleId)
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Ứng viên xác nhận chọn slot PV (không cần đăng nhập) */
    async function confirmSlot(token: string, slotId: number) {
        loading.value = true
        error.value = null
        try {
            return await publicInterviewService.confirmSlot(token, slotId)
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Lấy danh sách slot còn chỗ để hiển thị trang chọn lịch (không cần đăng nhập) */
    async function fetchSlotsByToken(token: string) {
        loading.value = true
        error.value = null
        try {
            slotSelectionPage.value = await publicInterviewService.getSlotsByToken(token)
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** UV xác nhận lịch phỏng vấn đã cập nhật qua link email (không cần đăng nhập) */
    async function confirmUpdatedSchedule(token: string) {
        loading.value = true
        error.value = null
        try {
            return await publicInterviewService.confirmUpdatedSchedule(token)
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Reset store (dùng khi logout) */
    function reset() {
        myInterviews.value = []
        interviewHistory.value = null
        roundDetail.value = null
        slotSelectionPage.value = null
        loading.value = false
        error.value = null
    }

    return {
        // state
        myInterviews,
        interviewHistory,
        roundDetail,
        confirmUpdateInfo,
        slotSelectionPage,
        loading,
        error,
        // actions
        fetchMyInterviews,
        fetchMyInterviewHistory,
        fetchRoundDetail,
        fetchConfirmUpdateInfo,
        fetchSlotsByToken,
        confirmSlot,
        confirmUpdatedSchedule,
        confirmScheduleByCandidate,
        reset,
    }
})
