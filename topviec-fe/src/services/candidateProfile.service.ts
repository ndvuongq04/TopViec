// src/services/candidateProfile.service.ts
import axiosInstance from './axios'
import type { RestResponse } from '@/types/common.types'
import type {
    ReqCreateCandidateProfileDTO,
    ReqUpdateCandidateProfileDTO,
    ReqUpdateCandidateProfileVisibilityDTO,
    ResCandidateProfileDTO,
} from '@/types/candidateProfile.types'

const BASE_URL = '/candidate/profile'

const candidateProfileService = {
    /**
     * Lấy profile của user đang đăng nhập
     * GET /candidate/profile/me
     */
    async getMyProfile(): Promise<ResCandidateProfileDTO> {
        const res = await axiosInstance.get<RestResponse<ResCandidateProfileDTO>>(`${BASE_URL}/me`)
        return res.data.data
    },

    /**
     * Tạo mới profile
     * POST /candidate/profile
     */
    async createProfile(payload: ReqCreateCandidateProfileDTO): Promise<ResCandidateProfileDTO> {
        const res = await axiosInstance.post<RestResponse<ResCandidateProfileDTO>>(BASE_URL, payload)
        return res.data.data
    },

    /**
     * Cập nhật profile
     * PUT /candidate/profile
     */
    async updateProfile(payload: ReqUpdateCandidateProfileDTO): Promise<ResCandidateProfileDTO> {
        const res = await axiosInstance.put<RestResponse<ResCandidateProfileDTO>>(BASE_URL, payload)
        return res.data.data
    },

    /**
     * Cập nhật trạng thái ẩn/hiện thông tin nhạy cảm
     * PATCH /candidate/profile/visibility
     */
    async updateVisibility(payload: ReqUpdateCandidateProfileVisibilityDTO): Promise<ResCandidateProfileDTO> {
        const res = await axiosInstance.patch<RestResponse<ResCandidateProfileDTO>>(`${BASE_URL}/visibility`, payload)
        return res.data.data
    },

    /**
     * Xóa mềm profile
     * DELETE /candidate/profile
     */
    async deleteProfile(): Promise<void> {
        await axiosInstance.delete<RestResponse<null>>(BASE_URL)
    },
}

export default candidateProfileService
