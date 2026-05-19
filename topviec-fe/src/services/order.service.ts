import axiosInstance from './axios'
import type { RestResponse, ResultPaginationDTO } from '@/types/common.types'
import type {
    ReqCreateOrderDTO,
    ReqUpdateOrderStatusDTO,
    ResOrderDTO,
    AdminOrderQueryParams,
    EmployerOrderQueryParams,
    EmployerAddonPackageQueryParams,
    ResAdminOrderStatisticsDTO,
    ResPaymentUrlDTO,
    ReqRefundOrderDTO,
} from '@/types/order.types'
import type { ResServicePackageDTO } from '@/types/servicePackage.types'
import type { ResAddonServiceDTO } from '@/types/serviceCatalog.types'

// ─── Admin Order ──────────────────────────────────────────────────────────────

const ADMIN_ORDERS_URL    = '/admin/orders'

export const adminOrderService = {
    async getAll(params?: AdminOrderQueryParams): Promise<ResultPaginationDTO<ResOrderDTO>> {
        const res = await axiosInstance.get<RestResponse<ResultPaginationDTO<ResOrderDTO>>>(ADMIN_ORDERS_URL, { params })
        return res.data.data
    },

    async getById(id: number): Promise<ResOrderDTO> {
        const res = await axiosInstance.get<RestResponse<ResOrderDTO>>(`${ADMIN_ORDERS_URL}/${id}`)
        return res.data.data
    },

    async updateStatus(id: number, payload: ReqUpdateOrderStatusDTO): Promise<ResOrderDTO> {
        const res = await axiosInstance.patch<RestResponse<ResOrderDTO>>(`${ADMIN_ORDERS_URL}/${id}/status`, payload)
        return res.data.data
    },

    /** GET /admin/orders/statistics */
    async getStatistics(): Promise<ResAdminOrderStatisticsDTO> {
        const res = await axiosInstance.get<RestResponse<ResAdminOrderStatisticsDTO>>(
            `${ADMIN_ORDERS_URL}/statistics`
        )
        return res.data.data
    },

    /** GET /admin/orders/company/{companyId} */
    async getOrdersByCompanyId(companyId: number, params?: { page?: number; size?: number; sort?: string }): Promise<ResultPaginationDTO<ResOrderDTO>> {
        const res = await axiosInstance.get<RestResponse<ResultPaginationDTO<ResOrderDTO>>>(
            `${ADMIN_ORDERS_URL}/company/${companyId}`,
            { params }
        )
        return res.data.data
    },
}

// ─── Employer Order ───────────────────────────────────────────────────────────

const EMPLOYER_ORDERS_URL = '/employer/orders'

export const employerOrderService = {
    async getMyOrders(params?: EmployerOrderQueryParams): Promise<ResultPaginationDTO<ResOrderDTO>> {
        const res = await axiosInstance.get<RestResponse<ResultPaginationDTO<ResOrderDTO>>>(EMPLOYER_ORDERS_URL, { params })
        return res.data.data
    },

    async getById(id: number): Promise<ResOrderDTO> {
        const res = await axiosInstance.get<RestResponse<ResOrderDTO>>(`${EMPLOYER_ORDERS_URL}/${id}`)
        return res.data.data
    },

    async createOrder(payload: ReqCreateOrderDTO): Promise<ResOrderDTO> {
        const res = await axiosInstance.post<RestResponse<ResOrderDTO>>(EMPLOYER_ORDERS_URL, payload)
        return res.data.data
    },

    async cancelOrder(id: number): Promise<ResOrderDTO> {
        const res = await axiosInstance.patch<RestResponse<ResOrderDTO>>(`${EMPLOYER_ORDERS_URL}/${id}/cancel`)
        return res.data.data
    },

    async getPaymentUrl(id: number): Promise<ResPaymentUrlDTO> {
        const res = await axiosInstance.post<RestResponse<ResPaymentUrlDTO>>(`${EMPLOYER_ORDERS_URL}/${id}/pay`)
        return res.data.data
    },

    async requestRefund(id: number, payload: ReqRefundOrderDTO): Promise<ResOrderDTO> {
        const res = await axiosInstance.post<RestResponse<ResOrderDTO>>(`${EMPLOYER_ORDERS_URL}/${id}/refund`, payload)
        return res.data.data
    },
}

// ─── Employer Packages (public active) ───────────────────────────────────────

const EMPLOYER_PACKAGES_URL = '/employer/packages'

export const employerPackageService = {
    async getActiveServicePackages(): Promise<ResServicePackageDTO[]> {
        const res = await axiosInstance.get<RestResponse<ResServicePackageDTO[]>>(`${EMPLOYER_PACKAGES_URL}/services`)
        return res.data.data
    },

    async getActiveAddonServices(params?: EmployerAddonPackageQueryParams): Promise<ResAddonServiceDTO[]> {
        const res = await axiosInstance.get<RestResponse<ResAddonServiceDTO[]>>(`${EMPLOYER_PACKAGES_URL}/addons`, { params })
        return res.data.data
    },
}
