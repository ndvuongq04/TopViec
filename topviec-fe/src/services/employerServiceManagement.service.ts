import axiosInstance from './axios'
import type { RestResponse } from '@/types/common.types'
import type {
    ReqApplyAddonDTO,
    ReqRenewSubscriptionDTO,
    ResCompanySubscriptionDTO,
    ResCompanyAddonDTO,
    ResJobPostAddonDTO,
    ResCompanyBrandingDTO,
    ResSubscriptionRenewalDTO,
} from '@/types/servicePackage.types'

const BASE_URL = '/employer/services'

export const employerServiceManagementService = {
    /**
     * Lấy thông tin gói dịch vụ hiện tại NTD đang dùng và hạn mức còn lại
     */
    async getMySubscription(): Promise<ResCompanySubscriptionDTO> {
        const res = await axiosInstance.get<RestResponse<ResCompanySubscriptionDTO>>(
            `${BASE_URL}/subscription`,
        )
        return res.data.data
    },

    /**
     * Gia hạn gói subscription hiện tại
     */
    async renewSubscription(payload: ReqRenewSubscriptionDTO): Promise<ResSubscriptionRenewalDTO> {
        const res = await axiosInstance.post<RestResponse<ResSubscriptionRenewalDTO>>(
            `${BASE_URL}/subscription/renew`,
            payload,
        )
        return res.data.data
    },

    /**
     * Lấy danh sách các dịch vụ lẻ mà NTD đã mua và số lượng còn lại
     */
    async getMyAddons(): Promise<ResCompanyAddonDTO[]> {
        const res = await axiosInstance.get<RestResponse<ResCompanyAddonDTO[]>>(
            `${BASE_URL}/addons`,
        )
        return res.data.data
    },

    /**
     * Áp dụng dịch vụ lẻ cho một tin tuyển dụng
     * @param jobPostingId  ID tin tuyển dụng
     * @param payload       { companyAddonId }
     */
    async applyAddonToJobPost(
        jobPostingId: number,
        payload: ReqApplyAddonDTO,
    ): Promise<ResJobPostAddonDTO> {
        const res = await axiosInstance.post<RestResponse<ResJobPostAddonDTO>>(
            `${BASE_URL}/job-posts/${jobPostingId}/apply-addon`,
            payload,
        )
        return res.data.data
    },

    /**
     * Áp dụng dịch vụ BRANDING cho công ty (Banner trang chủ, Top Employer, ...)
     * Service code được tự động xác định từ companyAddonId
     */
    async applyBrandingToCompany(payload: ReqApplyAddonDTO): Promise<ResCompanyBrandingDTO> {
        const res = await axiosInstance.post<RestResponse<ResCompanyBrandingDTO>>(
            `${BASE_URL}/company/apply-branding`,
            payload,
        )
        return res.data.data
    },
}
