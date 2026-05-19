import axiosInstance from './axios'
import type { RestResponse } from '@/types/common.types'
import type {
    ReqCreateJobPostingDTO,
    ReqUpdateJobPostingDTO,
    ReqExtendJobPostDTO,
    ReqRejectJobPostingDTO,
    ResJobPostingDetail,
    ResJobPostingDetailPagination,
    ResJobPostingSummaryPagination,
    JobPostingQueryParams,
    EmployerJobPostingQueryParams,
} from '@/types/jobPosting.types'

// ─── Public endpoints (/job-postings) ────────────────────────────────────────

const publicJobPostingService = {
    /**
     * Lấy danh sách tin đang published (public, không cần đăng nhập).
     * GET /api/v1/job-postings
     */
    async getList(params?: JobPostingQueryParams): Promise<ResJobPostingSummaryPagination> {
        const res = await axiosInstance.get<RestResponse<ResJobPostingSummaryPagination>>(
            '/job-postings',
            { params },
        )
        return res.data.data
    },

    /**
     * Lấy chi tiết tin tuyển dụng. Tự động tăng viewCount.
     * GET /api/v1/job-postings/{id}
     */
    async getById(id: number | string): Promise<ResJobPostingDetail> {
        const res = await axiosInstance.get<RestResponse<ResJobPostingDetail>>(`/job-postings/${id}`)
        return res.data.data
    },

    /**
     * Lấy danh sách tin của một công ty cụ thể (public).
     * GET /api/v1/job-postings/company/{companyId}
     */
    async getByCompany(
        companyId: number | string,
        params?: { keyword?: string; page?: number; size?: number; sort?: string },
    ): Promise<ResJobPostingSummaryPagination> {
        const res = await axiosInstance.get<RestResponse<ResJobPostingSummaryPagination>>(
            `/job-postings/company/${companyId}`,
            { params },
        )
        return res.data.data
    },
}

// ─── Employer endpoints (/employer/job-postings) ─────────────────────────────

const employerJobPostingService = {
    /**
     * Tạo tin tuyển dụng mới. Status mặc định = "draft".
     * POST /api/v1/employer/job-postings
     */
    async createJob(payload: ReqCreateJobPostingDTO): Promise<ResJobPostingDetail> {
        const res = await axiosInstance.post<RestResponse<ResJobPostingDetail>>(
            '/employer/job-postings',
            payload,
        )
        return res.data.data
    },

    /**
     * Lấy danh sách tin của công ty đang đăng nhập.
     * GET /api/v1/employer/job-postings
     */
    async getList(params?: EmployerJobPostingQueryParams): Promise<ResJobPostingDetailPagination> {
        const res = await axiosInstance.get<RestResponse<ResJobPostingDetailPagination>>(
            '/employer/job-postings',
            { params },
        )
        return res.data.data
    },

    /**
     * Xem chi tiết 1 tin (dùng chung public endpoint do BE không có GET /employer/job-postings/{id}).
     * GET /api/v1/job-postings/{id}
     */
    async getById(id: number | string): Promise<ResJobPostingDetail> {
        const res = await axiosInstance.get<RestResponse<ResJobPostingDetail>>(
            `/job-postings/${id}`,
        )
        return res.data.data
    },

    /**
     * Cập nhật nội dung tin.
     * PUT /api/v1/employer/job-postings/{id}
     */
    async updateJob(id: number | string, payload: ReqUpdateJobPostingDTO): Promise<ResJobPostingDetail> {
        const res = await axiosInstance.put<RestResponse<ResJobPostingDetail>>(
            `/employer/job-postings/${id}`,
            payload,
        )
        return res.data.data
    },

    /**
     * Tạm dừng tin.
     * PATCH /api/v1/employer/job-postings/{id}/pause
     */
    async pauseJob(id: number | string): Promise<ResJobPostingDetail> {
        const res = await axiosInstance.patch<RestResponse<ResJobPostingDetail>>(
            `/employer/job-postings/${id}/pause`,
        )
        return res.data.data
    },

    /**
     * Mở lại tin đã tạm dừng.
     * PATCH /api/v1/employer/job-postings/{id}/resume
     */
    async resumeJob(id: number | string): Promise<ResJobPostingDetail> {
        const res = await axiosInstance.patch<RestResponse<ResJobPostingDetail>>(
            `/employer/job-postings/${id}/resume`,
        )
        return res.data.data
    },

    /**
     * Đóng tin.
     * PATCH /api/v1/employer/job-postings/{id}/close
     */
    async closeJob(id: number | string): Promise<ResJobPostingDetail> {
        const res = await axiosInstance.patch<RestResponse<ResJobPostingDetail>>(
            `/employer/job-postings/${id}/close`,
        )
        return res.data.data
    },

    /**
     * Gia hạn tin.
     * PATCH /api/v1/employer/job-postings/{id}/extend
     */
    async extendJob(id: number | string, payload: ReqExtendJobPostDTO): Promise<ResJobPostingDetail> {
        const res = await axiosInstance.patch<RestResponse<ResJobPostingDetail>>(
            `/employer/job-postings/${id}/extend`,
            payload,
        )
        return res.data.data
    },

    /**
     * Làm mới tin — đẩy lên đầu danh sách.
     * PATCH /api/v1/employer/job-postings/{id}/refresh
     */
    async refreshJob(id: number | string): Promise<ResJobPostingDetail> {
        const res = await axiosInstance.patch<RestResponse<ResJobPostingDetail>>(
            `/employer/job-postings/${id}/refresh`,
        )
        return res.data.data
    },
}

// ─── Admin endpoints (/admin/job-postings) ────────────────────────────────────

const adminJobPostingService = {
    /**
     * Lấy toàn bộ tin của mọi công ty.
     * GET /api/v1/admin/job-postings
     */
    async getList(params?: EmployerJobPostingQueryParams): Promise<ResJobPostingDetailPagination> {
        const res = await axiosInstance.get<RestResponse<ResJobPostingDetailPagination>>(
            '/admin/job-postings',
            { params },
        )
        return res.data.data
    },

    /**
     * Xem chi tiết bất kỳ tin nào.
     * GET /api/v1/admin/job-postings/{id}
     */
    async getById(id: number | string): Promise<ResJobPostingDetail> {
        const res = await axiosInstance.get<RestResponse<ResJobPostingDetail>>(
            `/admin/job-postings/${id}`,
        )
        return res.data.data
    },

    /**
     * Duyệt tin tuyển dụng.
     * PATCH /api/v1/admin/job-postings/{id}/approve
     */
    async approveJob(id: number | string): Promise<ResJobPostingDetail> {
        const res = await axiosInstance.patch<RestResponse<ResJobPostingDetail>>(
            `/admin/job-postings/${id}/approve`,
        )
        return res.data.data
    },

    /**
     * Từ chối tin tuyển dụng.
     * PATCH /api/v1/admin/job-postings/{id}/reject
     */
    async rejectJob(id: number | string, payload: ReqRejectJobPostingDTO): Promise<ResJobPostingDetail> {
        const res = await axiosInstance.patch<RestResponse<ResJobPostingDetail>>(
            `/admin/job-postings/${id}/reject`,
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
            `/admin/job-postings/${id}/takedown`,
            payload,
        )
        return res.data.data
    },
}

export { publicJobPostingService, employerJobPostingService, adminJobPostingService }
