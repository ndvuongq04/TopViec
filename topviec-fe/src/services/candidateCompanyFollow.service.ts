import axiosInstance from './axios'
import type { RestResponse } from '@/types/common.types'

// ─── Candidate endpoints (/candidate/companies/{companyId}/follow) ────────────

const candidateCompanyFollowService = {
    /**
     * POST /candidate/companies/{companyId}/follow
     * Thêm công ty vào danh sách theo dõi
     */
    async followCompany(companyId: number): Promise<void> {
        await axiosInstance.post<RestResponse<null>>(`/candidate/companies/${companyId}/follow`)
    },

    /**
     * DELETE /candidate/companies/{companyId}/follow
     * Bỏ theo dõi công ty
     */
    async unfollowCompany(companyId: number): Promise<void> {
        await axiosInstance.delete<RestResponse<null>>(`/candidate/companies/${companyId}/follow`)
    },

    /**
     * GET /candidate/companies/{companyId}/follow/status
     * Kiểm tra ứng viên đã theo dõi công ty này chưa
     */
    async checkFollowStatus(companyId: number): Promise<{ isFollowing: boolean }> {
        const res = await axiosInstance.get<RestResponse<{ isFollowing: boolean }>>(
            `/candidate/companies/${companyId}/follow/status`
        )
        return res.data.data
    },

    /**
     * GET /candidate/companies/followed
     * Lấy danh sách các công ty mà ứng viên đang theo dõi
     */
    async getFollowedCompanies(params: any): Promise<RestResponse<any>> {
        const res = await axiosInstance.get<RestResponse<any>>('/candidate/companies/followed', { params })
        return res.data
    },
}

export { candidateCompanyFollowService }
