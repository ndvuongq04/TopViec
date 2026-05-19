// src/services/cvs.service.ts
import axiosInstance from './axios'
import type { RestResponse } from '@/types/common.types'
import type { ReqRenameCv, ReqShareCv, ReqUploadCv, ResCv, ResCvPdfExport } from '@/types/cvs.types'

const BASE_URL = '/cvs'

const cvsService = {
    /**
     * Upload CV từ máy tính (PDF/DOCX)
     * POST /cvs/upload
     */
    async uploadCv(file: File, payload: ReqUploadCv): Promise<ResCv> {
        const formData = new FormData()
        formData.append('file', file)
        formData.append('title', payload.title)
        formData.append('isDefault', String(payload.isDefault ?? false))

        const res = await axiosInstance.post<RestResponse<ResCv>>(
            `${BASE_URL}/upload`,
            formData,
            { headers: { 'Content-Type': 'multipart/form-data' } },
        )
        return res.data.data
    },

    /**
     * Lấy danh sách CV của user đang đăng nhập
     * GET /cvs
     */
    async getMyCvs(): Promise<ResCv[]> {
        const res = await axiosInstance.get<RestResponse<ResCv[]>>(BASE_URL)
        return res.data.data
    },

    /**
     * Đổi tên CV
     * PATCH /cvs/:id/rename
     */
    async renameCv(id: number, payload: ReqRenameCv): Promise<ResCv> {
        const res = await axiosInstance.patch<RestResponse<ResCv>>(
            `${BASE_URL}/${id}/rename`,
            payload,
        )
        return res.data.data
    },

    /**
     * Đặt CV làm mặc định
     * PATCH /cvs/:id/default
     */
    async setDefaultCv(id: number): Promise<ResCv> {
        const res = await axiosInstance.patch<RestResponse<ResCv>>(`${BASE_URL}/${id}/default`)
        return res.data.data
    },

    /**
     * Sao chép CV
     * POST /cvs/:id/duplicate
     */
    async duplicateCv(id: number): Promise<ResCv> {
        const res = await axiosInstance.post<RestResponse<ResCv>>(`${BASE_URL}/${id}/duplicate`)
        return res.data.data
    },

    /**
     * Xóa CV (Soft delete)
     * DELETE /cvs/:id
     */
    async deleteCv(id: number): Promise<void> {
        await axiosInstance.delete<RestResponse<null>>(`${BASE_URL}/${id}`)
    },

    /**
     * update visibility của CV (public/private)
     * PATCH /cvs/:id/share
     */
    async shareCv(id: number, payload: ReqShareCv): Promise<ResCv> {
        const res = await axiosInstance.patch<RestResponse<ResCv>>(
            `${BASE_URL}/${id}/share`,
            payload,
        )
        return res.data.data
    },

    /**
     * Xem CV công khai qua token
     * GET /cvs/public/:shareToken
     */
    async getPublicCv(shareToken: string): Promise<ResCv> {
        const res = await axiosInstance.get<RestResponse<ResCv>>(`${BASE_URL}/public/${shareToken}`)
        return res.data.data
    },

    /**
     * Lấy chi tiết CV theo ID của user đang đăng nhập
     * GET /cvs/:id
     */
    async getCvById(id: number): Promise<ResCv> {
        const res = await axiosInstance.get<RestResponse<ResCv>>(`${BASE_URL}/${id}`)
        return res.data.data
    },

    async exportPdf(id: number): Promise<ResCvPdfExport> {
        const res = await axiosInstance.post<RestResponse<ResCvPdfExport>>(`${BASE_URL}/${id}/export-pdf`)
        return res.data.data
    },

    async downloadPdf(id: number): Promise<Blob> {
        const res = await axiosInstance.get(`${BASE_URL}/${id}/download-pdf`, {
            responseType: 'blob',
        })
        return res.data
    },
}

export default cvsService
