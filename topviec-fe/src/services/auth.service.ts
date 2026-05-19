// src/services/auth.service.ts
import axiosInstance from './axios'
import type { RestResponse } from '@/types/common.types'
import type {
    ReqLoginDTO,
    ReqRegisterCandidateDTO,
    ReqRegisterEmployerDTO,
    ReqForgotPasswordDTO,
    ReqResetPasswordDTO,
    ReqChangePasswordDTO,
    ResLoginDTO
} from '@/types/auth.types'

const authService = {
    async login(credentials: ReqLoginDTO): Promise<ResLoginDTO> {
        const res = await axiosInstance.post<RestResponse<ResLoginDTO>>('/auth/login', credentials)
        return res.data.data
    },

    async register(payload: ReqRegisterCandidateDTO): Promise<void> {
        await axiosInstance.post<RestResponse<null>>('/auth/register/candidate', payload)
    },
    async registerEmployer(payload: ReqRegisterEmployerDTO): Promise<void> {
        await axiosInstance.post<RestResponse<null>>('/auth/register/employer', payload)
    },

    async refresh(): Promise<ResLoginDTO> {
        const res = await axiosInstance.post<RestResponse<ResLoginDTO>>('/auth/refresh')
        return res.data.data
    },

    async logout(): Promise<void> {
        await axiosInstance.post<RestResponse<null>>('/auth/logout')
    },

    async forgotPassword(payload: ReqForgotPasswordDTO): Promise<void> {
        await axiosInstance.post<RestResponse<null>>('/auth/forgot-password', payload)
    },

    async resetPassword(payload: ReqResetPasswordDTO): Promise<void> {
        await axiosInstance.post<RestResponse<null>>('/auth/reset-password', payload)
    },

    async verifyEmail(token: string): Promise<void> {
        await axiosInstance.get<RestResponse<null>>('/auth/verify-email', { params: { token } })
    },

    async resendVerifyEmail(payload: ReqForgotPasswordDTO): Promise<void> {
        await axiosInstance.post<RestResponse<null>>('/auth/resend-verify-email', payload)
    },

    async changePassword(payload: ReqChangePasswordDTO): Promise<void> {
        await axiosInstance.post<RestResponse<null>>('/auth/change-password', payload)
    },
}

export default authService