import { defineStore } from 'pinia'
import { ref } from 'vue'
import { adminCompanyService } from '@/services/company.service'
import type {
    ResCompanyDTO,
    ReqAdminUpdateCompanyDTO,
    ResultPaginationDTO,
    PaginationMeta,
    ReqCreateCompanyDTO,
    ResAdminCompanyStatisticsDTO,
    ResCompanyPlanDTO,
    ResSubscriptionHistoryDTO,
} from '@/types/company.types'
import type { ReqRegisterEmployerDTO } from '@/types/auth.types'
import type { ResOrderDTO } from '@/types/order.types'
import { adminOrderService } from '@/services/order.service'

export const useAdminCompanyStore = defineStore('adminCompany', () => {
    // ─── State ──────────────────────────────────────────────────────────────────
    const companies = ref<ResCompanyDTO[]>([])
    const meta = ref<PaginationMeta>({ page: 0, pageSize: 10, pages: 0, totals: 0 })
    const selectedCompany = ref<ResCompanyDTO | null>(null)
    const companyStatistics = ref<ResAdminCompanyStatisticsDTO | null>(null)
    const companyPlan = ref<ResCompanyPlanDTO | null>(null)
    const companySubscriptions = ref<ResultPaginationDTO<ResSubscriptionHistoryDTO> | null>(null)
    const companyOrders = ref<ResultPaginationDTO<ResOrderDTO> | null>(null)
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

    /** POST /admin/companies */
    async function createCompany(payload: ReqRegisterEmployerDTO) {
        loading.value = true
        error.value = null
        try {
            const newCompany = await adminCompanyService.createCompany(payload)
            companies.value.unshift(newCompany)
            return newCompany
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    // ─── Actions ────────────────────────────────────────────────────────────────

    /** GET /admin/companies */
    async function fetchAllCompanies(params?: {
        status?: string
        verificationStatus?: string
        keyword?: string
        page?: number
        size?: number
    }) {
        loading.value = true
        error.value = null
        try {
            const data = await adminCompanyService.getAllCompanies(params)
            applyPagination(data)
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }




    /** GET /admin/companies/{id} */
    async function fetchById(id: number) {
        loading.value = true
        error.value = null
        try {
            selectedCompany.value = await adminCompanyService.getById(id)
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /** PATCH /admin/companies/{id} (action: verify) */
    async function verifyCompany(id: number, approved: boolean, rejectionReason?: string | null) {
        loading.value = true
        error.value = null
        try {
            const updated = await adminCompanyService.adminUpdateCompany(id, {
                action: 'verify',
                approved,
                rejectionReason
            })
            _updateInList(updated)
            if (selectedCompany.value?.id === id) selectedCompany.value = updated
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** PATCH /admin/companies/{id} (action: suspend) */
    async function suspendCompany(id: number, suspendedReason: string) {
        loading.value = true
        error.value = null
        try {
            const updated = await adminCompanyService.adminUpdateCompany(id, {
                action: 'suspend',
                suspendedReason
            })
            _updateInList(updated)
            if (selectedCompany.value?.id === id) selectedCompany.value = updated
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** PATCH /admin/companies/{id} (action: unsuspend) */
    async function unsuspendCompany(id: number) {
        loading.value = true
        error.value = null
        try {
            const updated = await adminCompanyService.adminUpdateCompany(id, {
                action: 'unsuspend'
            })
            _updateInList(updated)
            if (selectedCompany.value?.id === id) selectedCompany.value = updated
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** PATCH /admin/companies/{id} (update details) */
    async function adminUpdateCompany(id: number, payload: ReqAdminUpdateCompanyDTO) {
        loading.value = true
        error.value = null
        try {
            const updated = await adminCompanyService.adminUpdateCompany(id, payload)
            _updateInList(updated)
            if (selectedCompany.value?.id === id) selectedCompany.value = updated
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** DELETE /admin/companies/{id} */
    async function deleteCompany(id: number) {
        loading.value = true
        error.value = null
        try {
            await adminCompanyService.deleteCompany(id)
            companies.value = companies.value.filter(c => c.id !== id)
            if (selectedCompany.value?.id === id) selectedCompany.value = null
        } catch (err) {
            setError(err)
            throw err
        } finally {
            loading.value = false
        }
    }

    /** GET /admin/companies/{id}/statistics */
    async function fetchStatistics(id: number) {
        loading.value = true
        error.value = null
        try {
            companyStatistics.value = await adminCompanyService.getCompanyStatistics(id)
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /** GET /admin/companies/{id}/plan */
    async function fetchCompanyPlan(id: number) {
        loading.value = true
        error.value = null
        try {
            companyPlan.value = await adminCompanyService.getCompanyPlan(id)
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /** GET /admin/companies/{id}/subscriptions */
    async function fetchCompanySubscriptions(id: number, params?: { page?: number; size?: number }) {
        loading.value = true
        error.value = null
        try {
            companySubscriptions.value = await adminCompanyService.getSubscriptionHistory(id, params)
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    /** GET /admin/orders/company/{companyId} */
    async function fetchCompanyOrders(companyId: number, params?: { page?: number; size?: number }) {
        loading.value = true
        error.value = null
        try {
            companyOrders.value = await adminOrderService.getOrdersByCompanyId(companyId, params)
        } catch (err) {
            setError(err)
        } finally {
            loading.value = false
        }
    }

    // ─── Private helpers ─────────────────────────────────────────────────────────
    function _updateInList(updated: ResCompanyDTO) {
        const idx = companies.value.findIndex(c => c.id === updated.id)
        if (idx !== -1) companies.value[idx] = updated
    }

    function reset() {
        companies.value = []
        selectedCompany.value = null
        companyStatistics.value = null
        companyPlan.value = null
        companySubscriptions.value = null
        companyOrders.value = null
        meta.value = { page: 0, pageSize: 10, pages: 0, totals: 0 }
        loading.value = false
        error.value = null
    }

    return {
        companies,
        selectedCompany,
        companyStatistics,
        companyPlan,
        companySubscriptions,
        companyOrders,
        meta,
        loading,
        error,
        fetchAllCompanies,
        createCompany,
        fetchById,
        verifyCompany,
        suspendCompany,
        unsuspendCompany,
        adminUpdateCompany,
        deleteCompany,
        fetchStatistics,
        fetchCompanyPlan,
        fetchCompanySubscriptions,
        fetchCompanyOrders,
        reset,
    }
})