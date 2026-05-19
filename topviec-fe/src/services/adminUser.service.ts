// src/services/adminUser.service.ts
import axiosInstance from './axios'
import type { RestResponse, ResultPaginationDTO } from '@/types/common.types'
import type { ResAdminUser, ReqCreateAdmin, ReqUpdateAdmin } from '@/types/adminUser.types'

const adminUserService = {
    /**
     * GET /admin/users?keyword=&adminRole=&page=&size=&sort=
     * Lấy danh sách admin (phân trang, tìm kiếm, lọc theo role)
     */
    async getAllAdmins(params?: {
        keyword?: string
        adminRole?: string
        page?: number
        size?: number
        sort?: string
    }): Promise<ResultPaginationDTO<ResAdminUser>> {
        const res = await axiosInstance.get<RestResponse<ResultPaginationDTO<ResAdminUser>>>(
            '/admin/users',
            { params },
        )
        return res.data.data
    },

    /**
     * GET /admin/users/{id}
     * Xem chi tiết admin
     */
    async getAdminById(id: number): Promise<ResAdminUser> {
        const res = await axiosInstance.get<RestResponse<ResAdminUser>>(`/admin/users/${id}`)
        return res.data.data
    },

    /**
     * POST /admin/users
     * Tạo admin mới (chỉ super_admin)
     */
    async createAdmin(payload: ReqCreateAdmin): Promise<ResAdminUser> {
        const res = await axiosInstance.post<RestResponse<ResAdminUser>>('/admin/users', payload)
        return res.data.data
    },

    /**
     * PUT /admin/users/{id}
     * Cập nhật thông tin admin (chỉ super_admin)
     */
    async updateAdmin(id: number, payload: ReqUpdateAdmin): Promise<ResAdminUser> {
        const res = await axiosInstance.put<RestResponse<ResAdminUser>>(`/admin/users/${id}`, payload)
        return res.data.data
    },

    /**
     * PATCH /admin/users/{id}/toggle-active
     * Khóa / mở khóa tài khoản admin (chỉ super_admin)
     */
    async toggleActive(id: number): Promise<ResAdminUser> {
        const res = await axiosInstance.patch<RestResponse<ResAdminUser>>(
            `/admin/users/${id}/toggle-active`,
        )
        return res.data.data
    },

    /**
     * DELETE /admin/users/{id}
     * Xóa admin (chỉ super_admin)
     */
    async deleteAdmin(id: number): Promise<void> {
        await axiosInstance.delete<RestResponse<null>>(`/admin/users/${id}`)
    },

    /**
     * GET /admin/users/me
     * Admin xem thông tin cá nhân của chính mình
     */
    async getMyProfile(): Promise<ResAdminUser> {
        const res = await axiosInstance.get<RestResponse<ResAdminUser>>('/admin/users/me')
        return res.data.data
    },
}

export { adminUserService }
