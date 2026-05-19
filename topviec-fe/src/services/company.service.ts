import axiosInstance from './axios'
import type { RestResponse } from '@/types/common.types'
import type {
    ReqCreateCompanyDTO,
    ReqUpdateCompanyDTO,
    ReqAdminUpdateCompanyDTO,
    ResCompanyDTO,
    ResAdminCompanyStatisticsDTO,
    ResCompanyPlanDTO,
    ResSubscriptionHistoryDTO,
    ResultPaginationDTO,
} from '@/types/company.types'
import type { ReqRegisterEmployerDTO } from '@/types/auth.types'

// ─── Employer endpoints (/employer/company) ───────────────────────────────────

const employerCompanyService = {
    /**
     * POST /employer/company
     * Tạo hồ sơ công ty lần đầu.
     */
    async createCompany(payload: ReqCreateCompanyDTO): Promise<ResCompanyDTO> {
        const res = await axiosInstance.post<RestResponse<ResCompanyDTO>>('/employer/company', payload)
        return res.data.data
    },

    /**
     * GET /employer/company/me
     * Xem hồ sơ công ty của chính mình.
     */
    async getMyCompany(): Promise<ResCompanyDTO> {
        const res = await axiosInstance.get<RestResponse<ResCompanyDTO>>('/employer/company/me')
        return res.data.data
    },

    /**
     * PATCH /employer/company
     * Cập nhật hồ sơ công ty.
     */
    async updateMyCompany(payload: ReqUpdateCompanyDTO): Promise<ResCompanyDTO> {
        const res = await axiosInstance.patch<RestResponse<ResCompanyDTO>>('/employer/company', payload)
        return res.data.data
    },
}

// ─── Admin endpoints (/admin/companies) ──────────────────────────────────────

const adminCompanyService = {
    /**
     * GET /admin/companies?status=&page=&size=
     * Lấy danh sách tất cả công ty.
     */
    async getAllCompanies(params?: {
        status?: string
        verificationStatus?: string
        keyword?: string
        page?: number
        size?: number
        sort?: string
    }): Promise<ResultPaginationDTO<ResCompanyDTO>> {
        const res = await axiosInstance.get<RestResponse<ResultPaginationDTO<ResCompanyDTO>>>(
            '/admin/companies',
            { params }
        )
        return res.data.data
    },

    /**
 * POST /admin/companies
 * Admin tạo công ty mới.
 */
    async createCompany(payload: ReqRegisterEmployerDTO): Promise<ResCompanyDTO> {
        const res = await axiosInstance.post<RestResponse<ResCompanyDTO>>('/admin/companies', payload)
        return res.data.data
    },


    /**
     * GET /admin/companies/{id}
     * Xem chi tiết 1 công ty.
     */
    async getById(id: number): Promise<ResCompanyDTO> {
        const res = await axiosInstance.get<RestResponse<ResCompanyDTO>>(`/admin/companies/${id}`)
        return res.data.data
    },

    /**
     * PATCH /admin/companies/{id}
     * Admin cập nhật thông tin, phê duyệt, khóa, mở khóa công ty.
     */
    async adminUpdateCompany(id: number, payload: ReqAdminUpdateCompanyDTO): Promise<ResCompanyDTO> {
        const res = await axiosInstance.patch<RestResponse<ResCompanyDTO>>(
            `/admin/companies/${id}`,
            payload
        )
        return res.data.data
    },

    /**
     * DELETE /admin/companies/{id}
     * Xóa mềm công ty.
     */
    async deleteCompany(id: number): Promise<void> {
        await axiosInstance.delete<RestResponse<null>>(`/admin/companies/${id}`)
    },
    
    /**
     * GET /admin/companies/{id}/statistics
     * Lấy thống kê của 1 công ty.
     */
    async getCompanyStatistics(id: number): Promise<ResAdminCompanyStatisticsDTO> {
        const res = await axiosInstance.get<RestResponse<ResAdminCompanyStatisticsDTO>>(
            `/admin/companies/${id}/statistics`
        )
        return res.data.data
    },

    /**
     * GET /admin/companies/{id}/plan
     * Lấy thông tin gói dịch vụ hiện tại của công ty.
     */
    async getCompanyPlan(id: number): Promise<ResCompanyPlanDTO> {
        const res = await axiosInstance.get<RestResponse<ResCompanyPlanDTO>>(
            `/admin/companies/${id}/plan`
        )
        return res.data.data
    },

    /**
     * GET /admin/companies/{id}/subscriptions
     * Lấy lịch sử gia hạn gói dịch vụ của công ty.
     */
    async getSubscriptionHistory(id: number, params?: { page?: number; size?: number; sort?: string }): Promise<ResultPaginationDTO<ResSubscriptionHistoryDTO>> {
        const res = await axiosInstance.get<RestResponse<ResultPaginationDTO<ResSubscriptionHistoryDTO>>>(
            `/admin/companies/${id}/subscriptions`,
            { params }
        )
        return res.data.data
    },
}

// ─── Public endpoints (/companies) ───────────────────────────────────────────

const publicCompanyService = {
    /**
     * GET /companies
     * Lấy danh sách công ty công khai (có hỗ trợ filter keyword, provinceId, industryId).
     */
    async getPublicCompanies(params?: {
        keyword?: string
        provinceId?: number
        industryId?: number
        isBanner?: boolean
        isTopEmployer?: boolean
        isBrandVerified?: boolean
        page?: number
        size?: number
        sort?: string
    }): Promise<ResultPaginationDTO<ResCompanyDTO>> {
        const res = await axiosInstance.get<RestResponse<ResultPaginationDTO<ResCompanyDTO>>>(
            '/companies',
            { params }
        )
        return res.data.data
    },

    /**
     * GET /companies/{slug}
     * Lấy thông tin công ty theo slug (không cần đăng nhập).
     */
    async getBySlug(slug: string): Promise<ResCompanyDTO> {
        const res = await axiosInstance.get<RestResponse<ResCompanyDTO>>(`/companies/${slug}`)
        return res.data.data
    },

    /**
     * GET /companies/id/{id}
     * Lấy thông tin công ty theo id (không cần đăng nhập).
     */
    async getById(id: number): Promise<ResCompanyDTO> {
        const res = await axiosInstance.get<RestResponse<ResCompanyDTO>>(`/companies/id/${id}`)
        return res.data.data
    },
}

export { employerCompanyService, adminCompanyService, publicCompanyService }