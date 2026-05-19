import axiosInstance from './axios';
import type { RestResponse, ResultPaginationDTO } from '@/types/common.types';
import type { ReqAddMember, ReqBatchMemberPermission, ReqToggleMemberAction, ReqUpdatePermission, ResCompanyMember, ResMemberPermissionDetail, ResPermissionChangeLogDTO, ResEmployerMemberStatisticsDTO } from '@/types/companyMember.types';

export const employerMemberService = {
    /**
     * GET /employer/member
     * Lấy danh sách thành viên công ty với phân trang và bộ lọc
     */
    async getMembers(params: any): Promise<RestResponse<ResultPaginationDTO<ResCompanyMember>>> {
        const res = await axiosInstance.get<RestResponse<ResultPaginationDTO<ResCompanyMember>>>('/employer/member', { params });
        return res.data;
    },

    /**
     * POST /employer/member
     * Thêm thành viên mới vào công ty
     */
    async addMember(data: ReqAddMember): Promise<RestResponse<ResCompanyMember>> {
        const res = await axiosInstance.post<RestResponse<ResCompanyMember>>('/employer/member', data);
        return res.data;
    },

    /**
     * PATCH /employer/member/{targetUserId}/permission
     * Cập nhật quyền và vai trò của thành viên
     */
    async updateMemberPermission(targetUserId: number, data: ReqUpdatePermission): Promise<RestResponse<ResCompanyMember>> {
        const res = await axiosInstance.patch<RestResponse<ResCompanyMember>>(`/employer/member/${targetUserId}/permission`, data);
        return res.data;
    },

    /**
     * DELETE /employer/member/{targetUserId}
     * Xóa thành viên khỏi công ty
     */
    async removeMember(targetUserId: number): Promise<RestResponse<void>> {
        const res = await axiosInstance.delete<RestResponse<void>>(`/employer/member/${targetUserId}`);
        return res.data;
    },

    /**
     * POST /employer/permissions/batch
     * Lấy thông tin quyền chi tiết của nhiều thành viên cùng lúc (tối đa 5)
     */
    async getBatchMemberPermissions(data: ReqBatchMemberPermission): Promise<RestResponse<ResMemberPermissionDetail[]>> {
        const res = await axiosInstance.post<RestResponse<ResMemberPermissionDetail[]>>('/employer/member/permissions/batch', data);
        return res.data;
    },

    /**
     * GET /employer/me/permissions
     * Lấy thông tin quyền hạn của chính mình trong công ty
     */
    async getMyPermissions(): Promise<RestResponse<ResMemberPermissionDetail>> {
        const res = await axiosInstance.get<RestResponse<ResMemberPermissionDetail>>('/employer/member/me/permissions');
        return res.data;
    },

    /**
     * GET /employer/member/{targetUserId}/permissions/history
     * Lấy lịch sử thay đổi quyền của một thành viên cụ thể
     */
    async getMemberPermissionHistory(targetUserId: number): Promise<RestResponse<ResPermissionChangeLogDTO[]>> {
        const res = await axiosInstance.get<RestResponse<ResPermissionChangeLogDTO[]>>(
            `/employer/member/${targetUserId}/permissions/history`
        );
        return res.data;
    },

    /**
     * GET /employer/member/permissions/history
     * Lấy lịch sử thay đổi quyền toàn công ty (có phân trang)
     */
    async getCompanyPermissionHistory(params?: Record<string, unknown>): Promise<RestResponse<ResultPaginationDTO<ResPermissionChangeLogDTO>>> {
        const res = await axiosInstance.get<RestResponse<ResultPaginationDTO<ResPermissionChangeLogDTO>>>(
            '/employer/member/permissions/history',
            { params }
        );
        return res.data;
    },

    /**
     * PATCH /employer/member/{targetUserId}/permissions/{actionCode}
     * Bật/tắt một quyền cụ thể của thành viên
     */
    async toggleMemberActionPermission(
        targetUserId: number,
        actionCode: string,
        data: ReqToggleMemberAction
    ): Promise<RestResponse<ResMemberPermissionDetail>> {
        const res = await axiosInstance.patch<RestResponse<ResMemberPermissionDetail>>(
            `/employer/member/${targetUserId}/permissions/${encodeURIComponent(actionCode)}`,
            data
        );
        return res.data;
    },

    /**
     * GET /employer/member/statistics
     * Lấy thống kê thành viên của công ty.
     */
    async getMemberStatistics(): Promise<ResEmployerMemberStatisticsDTO> {
        const res = await axiosInstance.get<RestResponse<ResEmployerMemberStatisticsDTO>>('/employer/member/statistics');
        return res.data.data;
    },
};
