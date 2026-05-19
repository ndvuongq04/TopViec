// src/stores/cvs.store.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import cvsService from '@/services/cvs.service'
import type { ResCv, ReqRenameCv, ReqShareCv, ReqUploadCv, ResCvPdfExport } from '@/types/cvs.types'

export const useCvsStore = defineStore('cvs', () => {
    // ─── State ──────────────────────────────────────────────────────────────────
    const cvs = ref<ResCv[]>([])
    const currentCv = ref<ResCv | null>(null)
    const loading = ref(false)
    const error = ref<string | null>(null)

    // ─── Helpers ────────────────────────────────────────────────────────────────
    function setError(err: unknown) {
        error.value =
            (err as any)?.response?.data?.message ?? 'Có lỗi xảy ra. Vui lòng thử lại.'
    }

    // ─── Actions ────────────────────────────────────────────────────────────────

    /** Lấy danh sách CV của user đang đăng nhập */
    async function fetchMyCvs() {
        loading.value = true
        error.value = null
        try {
            cvs.value = await cvsService.getMyCvs()
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /** Upload CV từ máy tính */
    async function uploadCv(file: File, payload: ReqUploadCv) {
        loading.value = true
        error.value = null
        try {
            const newCv = await cvsService.uploadCv(file, payload)
            cvs.value.unshift(newCv)
            return newCv
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Đổi tên CV */
    async function renameCv(id: number, payload: ReqRenameCv) {
        loading.value = true
        error.value = null
        try {
            const updated = await cvsService.renameCv(id, payload)
            _replaceInList(updated)
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Đặt CV làm mặc định */
    async function setDefaultCv(id: number) {
        loading.value = true
        error.value = null
        try {
            const updated = await cvsService.setDefaultCv(id)
            // Bỏ default của tất cả, rồi set lại cv vừa update
            cvs.value = cvs.value.map((cv) => ({ ...cv, isDefault: cv.id === updated.id }))
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Sao chép CV */
    async function duplicateCv(id: number) {
        loading.value = true
        error.value = null
        try {
            const duplicated = await cvsService.duplicateCv(id)
            cvs.value.unshift(duplicated)
            return duplicated
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Xóa CV */
    async function deleteCv(id: number) {
        loading.value = true
        error.value = null
        try {
            await cvsService.deleteCv(id)
            cvs.value = cvs.value.filter((cv) => cv.id !== id)
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Chia sẻ CV */
    async function shareCv(id: number, payload: ReqShareCv) {
        loading.value = true
        error.value = null
        try {
            const updated = await cvsService.shareCv(id, payload)
            _replaceInList(updated)
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Lấy CV công khai (không cần login) */
    async function getPublicCv(shareToken: string) {
        try {
            return await cvsService.getPublicCv(shareToken)
        } catch (err) {
            setError(err)
            throw err
        }
    }

    /** Lấy chi tiết CV theo ID của user đang đăng nhập */
    async function fetchCvById(id: number) {
        loading.value = true
        error.value = null
        try {
            currentCv.value = await cvsService.getCvById(id)
            return currentCv.value
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Tao/cap nhat PDF cho CV online khi user xem truoc */
    async function exportPdf(id: number) {
        loading.value = true
        error.value = null
        try {
            const exported = await cvsService.exportPdf(id)
            _patchPdfState(id, exported)
            return exported
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    async function downloadPdf(id: number) {
        loading.value = true
        error.value = null
        try {
            const blob = await cvsService.downloadPdf(id)
            const existingCv = cvs.value.find((cv) => cv.id === id)
            const currentPdfUrl = currentCv.value?.id === id ? currentCv.value.pdfUrl : existingCv?.pdfUrl ?? null
            _patchPdfState(id, { pdfUrl: currentPdfUrl, pdfDirty: false })
            return blob
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Reset store (dùng khi logout) */
    function reset() {
        cvs.value = []
        currentCv.value = null
        loading.value = false
        error.value = null
    }

    // ─── Private ────────────────────────────────────────────────────────────────

    /** Thay thế 1 item trong list theo id */
    function _replaceInList(updated: ResCv) {
        const idx = cvs.value.findIndex((cv) => cv.id === updated.id)
        if (idx !== -1) cvs.value[idx] = updated
    }

    function _patchPdfState(id: number, exported: ResCvPdfExport) {
        const patch = {
            pdfUrl: exported.pdfUrl ?? null,
            pdfDirty: exported.pdfDirty ?? false,
            ...(exported.updatedAt ? { updatedAt: exported.updatedAt } : {}),
        }

        cvs.value = cvs.value.map((cv) => (cv.id === id ? { ...cv, ...patch } : cv))

        if (currentCv.value?.id === id) {
            currentCv.value = { ...currentCv.value, ...patch }
        }
    }

    return {
        // state
        cvs,
        currentCv,
        loading,
        error,
        // actions
        fetchMyCvs,
        uploadCv,
        renameCv,
        setDefaultCv,
        duplicateCv,
        deleteCv,
        shareCv,
        getPublicCv,
        fetchCvById,
        exportPdf,
        downloadPdf,
        reset,
    }
})
