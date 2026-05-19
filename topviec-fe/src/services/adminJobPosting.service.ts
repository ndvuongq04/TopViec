import axiosInstance from './axios'
import type { RestResponse } from '@/types/common.types'
import type {
    ReqRejectJobPostingDTO,
    ResJobPostingDetail,
    ResJobPostingDetailPagination,
    EmployerJobPostingQueryParams,
} from '@/types/jobPosting.types'

const BASE_URL = '/admin/job-postings'

const adminJobPostingService = {
    /**
     * Lấy toàn bộ tin tuyển dụng của mọi công ty.
     * GET /api/v1/admin/job-postings
     */
    async getList(params?: EmployerJobPostingQueryParams): Promise<ResJobPostingDetailPagination> {
        const res = await axiosInstance.get<RestResponse<ResJobPostingDetailPagination>>(
            BASE_URL,
            { params },
        )
        return res.data.data
    },

    /**
     * Xem chi tiết bất kỳ tin nào.
     * GET /api/v1/admin/job-postings/{id}
     */
    async getById(id: number | string): Promise<ResJobPostingDetail> {
        const res = await axiosInstance.get<RestResponse<ResJobPostingDetail>>(`${BASE_URL}/${id}`)
        return res.data.data
    },

    /**
     * Duyệt tin tuyển dụng.
     * PATCH /api/v1/admin/job-postings/{id}/approve
     */
    async approveJob(id: number | string): Promise<ResJobPostingDetail> {
        const res = await axiosInstance.patch<RestResponse<ResJobPostingDetail>>(
            `${BASE_URL}/${id}/approve`,
        )
        return res.data.data
    },

    /**
     * Từ chối tin tuyển dụng.
     * PATCH /api/v1/admin/job-postings/{id}/reject
     */
    async rejectJob(id: number | string, payload: ReqRejectJobPostingDTO): Promise<ResJobPostingDetail> {
        const res = await axiosInstance.patch<RestResponse<ResJobPostingDetail>>(
            `${BASE_URL}/${id}/reject`,
            payload,
        )
        return res.data.data
    },

    /**
     * Gỡ tin vi phạm.
     * PATCH /api/v1/admin/job-postings/{id}/takedown
     */
    async takedownJob(id: number | string, payload: ReqRejectJobPostingDTO): Promise<ResJobPostingDetail> {
        const res = await axiosInstance.patch<RestResponse<ResJobPostingDetail>>(
            `${BASE_URL}/${id}/takedown`,
            payload,
        )
        return res.data.data
    },
}

export { adminJobPostingService }
