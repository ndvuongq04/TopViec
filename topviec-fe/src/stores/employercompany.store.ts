import { defineStore } from 'pinia'
import { ref } from 'vue'
import { employerCompanyService } from '@/services/company.service'
import type {
    ResCompanyDTO,
    ReqCreateCompanyDTO,
    ReqUpdateCompanyDTO,
} from '@/types/company.types'

export const useEmployerCompanyStore = defineStore('employerCompany', () => {
    // ─── State ──────────────────────────────────────────────────────────────────
    const company = ref<ResCompanyDTO | null>(null)
    const loading = ref(false)
    const error = ref<string | null>(null)

    // ─── Helpers ────────────────────────────────────────────────────────────────
    function setError(err: unknown) {
        const data = (err as any)?.response?.data
        if (data?.message && typeof data.message === 'object') {
            // Lấy lỗi đầu tiên nếu message là object (validation errors)
            const firstError = Object.values(data.message)[0]
            error.value = String(firstError)
        } else {
            error.value = data?.message ?? 'Có lỗi xảy ra. Vui lòng thử lại.'
        }
    }

    // ─── Actions ────────────────────────────────────────────────────────────────

    /** GET /employer/company/me */
    async function fetchMyCompany() {
        loading.value = true
        error.value = null
        try {
            company.value = await employerCompanyService.getMyCompany()
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /** POST /employer/company */
    async function createCompany(payload: ReqCreateCompanyDTO) {
        loading.value = true
        error.value = null
        try {
            company.value = await employerCompanyService.createCompany(payload)
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** PATCH /employer/company */
    async function updateMyCompany(payload: ReqUpdateCompanyDTO) {
        loading.value = true
        error.value = null
        try {
            company.value = await employerCompanyService.updateMyCompany(payload)
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** PATCH /employer/company */
    async function updateMyCompany(payload: ReqUpdateCompanyDTO) {
        loading.value = true
        error.value = null
        try {
            company.value = await employerCompanyService.updateMyCompany(payload)
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** Reset khi logout */
    function reset() {
        company.value = null
        loading.value = false
        error.value = null
    }

    return {
        company,
        loading,
        error,
        fetchMyCompany,
        createCompany,
        updateMyCompany,
        reset,
    }
})