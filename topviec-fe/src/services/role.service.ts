import axiosInstance from './axios'
import type { RestResponse } from '@/types/common.types'
import type {
    ResRoleDefaultDTO,
    ResRoleSummaryDTO,
    ReqToggleAction,
    ReqRenameAction,
    ReqAddAction,
} from '@/types/role.types'

export const roleService = {
    /**
     * GET /employer/roles
     * Lấy danh sách role summary (id + roleName) dành cho employer
     */
    async getEmployerRoleSummaries(): Promise<ResRoleSummaryDTO[]> {
        const res = await axiosInstance.get<RestResponse<ResRoleSummaryDTO[]>>('/employer/roles')
        return res.data.data
    },

    /**
     * GET /admin/roles/default-permissions
     * Lấy danh sách cấu hình phân quyền mặc định của các role (admin only)
     */
    async getDefaultRoles(): Promise<ResRoleDefaultDTO[]> {
        const res = await axiosInstance.get<RestResponse<ResRoleDefaultDTO[]>>(
            '/admin/roles/default-permissions',
        )
        return res.data.data
    },

/**
     * PATCH /admin/roles/{roleId}/actions/{actionName}
     * Bật / tắt một action của role
     */
    async toggleAction(
        roleId: number,
        actionName: string,
        payload: ReqToggleAction,
    ): Promise<ResRoleDefaultDTO> {
        const res = await axiosInstance.patch<RestResponse<ResRoleDefaultDTO>>(
            `/admin/roles/${roleId}/actions/${encodeURIComponent(actionName)}`,
            payload,
        )
        return res.data.data
    },

    /**
     * PATCH /admin/roles/{roleId}/actions/{actionCode}/name
     * Đổi tên hiển thị của một action
     */
    async renameAction(
        roleId: number,
        actionCode: string,
        payload: ReqRenameAction,
    ): Promise<ResRoleDefaultDTO> {
        const res = await axiosInstance.patch<RestResponse<ResRoleDefaultDTO>>(
            `/admin/roles/${roleId}/actions/${encodeURIComponent(actionCode)}/name`,
            payload,
        )
        return res.data.data
    },

    /**
     * POST /admin/roles/actions
     * Thêm mới một action vào tất cả các role (owner=true, còn lại=false)
     */
    async addAction(payload: ReqAddAction): Promise<ResRoleDefaultDTO[]> {
        const res = await axiosInstance.post<RestResponse<ResRoleDefaultDTO[]>>(
            '/admin/roles/actions',
            payload,
        )
        return res.data.data
    },
}
