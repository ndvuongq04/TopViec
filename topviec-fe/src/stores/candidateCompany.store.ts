import { defineStore } from 'pinia'
import { ref } from 'vue'
import { publicCompanyService } from '@/services/company.service'
import type {
    ResCompanyDTO,
    ResultPaginationDTO,
    PaginationMeta,
} from '@/types/company.types'

export const useCandidateCompanyStore = defineStore('candidateCompany', () => {
    // ─── State ──────────────────────────────────────────────────────────────────
    const companies = ref<ResCompanyDTO[]>([])
    const selectedCompany = ref<ResCompanyDTO | null>(null)
    const meta = ref<PaginationMeta>({ page: 0, pageSize: 10, pages: 0, totals: 0 })
    const loading = ref(false)
    const error = ref<string | null>(null)

    // ─── Helpers ────────────────────────────────────────────────────────────────
    function setError(err: unknown) {
        const msg = (err as any)?.response?.data?.message
        if (typeof msg === 'object' && msg !== null) {
            error.value = Object.values(msg).join(', ')
        } else {
            error.value = msg ?? 'Có lỗi xảy ra. Vui lòng thử lại.'
        }
    }

    function applyPagination(data: ResultPaginationDTO<ResCompanyDTO>) {
        companies.value = data.result
        meta.value = data.meta
    }

    // ─── Actions ────────────────────────────────────────────────────────────────

    /** GET /companies */
    async function fetchPublicCompanies(params?: {
        keyword?: string
        provinceId?: number
        industryId?: number
        isBanner?: boolean
        isTopEmployer?: boolean
        isBrandVerified?: boolean
        page?: number
        size?: number
        sort?: string
    }) {
        loading.value = true
        error.value = null
        try {
            const data = await publicCompanyService.getPublicCompanies(params)
            applyPagination(data)
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /** GET /companies/{slug} */
    async function fetchBySlug(slug: string) {
        loading.value = true
        error.value = null
        try {
            selectedCompany.value = await publicCompanyService.getBySlug(slug)
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /** GET /companies/id/{id} */
    async function fetchById(id: number) {
        loading.value = true
        error.value = null
        try {
            selectedCompany.value = await publicCompanyService.getById(id)
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    function reset() {
        companies.value = []
        selectedCompany.value = null
        meta.value = { page: 0, pageSize: 10, pages: 0, totals: 0 }
        loading.value = false
        error.value = null
    }

    return {
        companies,
        selectedCompany,
        meta,
        loading,
        error,
        fetchPublicCompanies,
        fetchBySlug,
        fetchById,
        reset,
    }
})
