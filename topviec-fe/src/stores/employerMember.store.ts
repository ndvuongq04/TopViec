import { ref } from 'vue';
import { defineStore } from 'pinia';
import { employerMemberService } from '@/services/employerMember.service';
import type { ResCompanyMember, ReqAddMember, ReqUpdatePermission, ResMemberPermissionDetail, ResPermissionChangeLogDTO, ResEmployerMemberStatisticsDTO } from '@/types/companyMember.types';
import type { ResultPaginationDTO } from '@/types/common.types';

export const useEmployerMemberStore = defineStore('employerMember', () => {
    // State
    const members = ref<ResultPaginationDTO<ResCompanyMember> | null>(null);
    const memberPermissions = ref<ResMemberPermissionDetail[]>([]);
    const myPermissions = ref<ResMemberPermissionDetail | null>(null);
    const companyPermissionHistory = ref<ResultPaginationDTO<ResPermissionChangeLogDTO> | null>(null);
    const memberPermissionHistory = ref<ResPermissionChangeLogDTO[]>([]);
    const memberStatistics = ref<ResEmployerMemberStatisticsDTO | null>(null);
    const loading = ref(false);
    const error = ref<string | null>(null);

    /**
     * Lấy danh sách thành viên
     */
    async function getMembers(params: any = { page: 0, size: 10 }) {
        loading.value = true;
        error.value = null;
        try {
            const res = await employerMemberService.getMembers(params);
            members.value = res.data;
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Không thể tải danh sách thành viên';
            throw err;
        } finally {
            loading.value = false;
        }
    }

    /**
     * Thêm thành viên mới
     */
    async function addMember(data: ReqAddMember) {
        loading.value = true;
        error.value = null;
        try {
            const res = await employerMemberService.addMember(data);
            return res.data;
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Không thể thêm thành viên';
            throw err;
        } finally {
            loading.value = false;
        }
    }

    /**
     * Cập nhật quyền thành viên
     */
    async function updateMemberPermission(targetUserId: number, data: ReqUpdatePermission) {
        loading.value = true;
        error.value = null;
        try {
            const res = await employerMemberService.updateMemberPermission(targetUserId, data);
            return res.data;
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Không thể cập nhật quyền';
            throw err;
        } finally {
            loading.value = false;
        }
    }

    /**
     * Xóa thành viên
     */
    async function removeMember(targetUserId: number) {
        loading.value = true;
        error.value = null;
        try {
            await employerMemberService.removeMember(targetUserId);
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Không thể xóa thành viên';
            throw err;
        } finally {
            loading.value = false;
        }
    }

    /**
     * Lấy quyền chi tiết của nhiều thành viên cùng lúc (tối đa 5)
     */
    async function getBatchMemberPermissions(userIds: number[]) {
        loading.value = true;
        error.value = null;
        try {
            const res = await employerMemberService.getBatchMemberPermissions({ userIds });
            memberPermissions.value = res.data;
            return res.data;
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Không thể lấy thông tin quyền thành viên';
            throw err;
        } finally {
            loading.value = false;
        }
    }

    /**
     * Lấy quyền hạn của chính mình trong công ty
     */
    async function getMyPermissions() {
        loading.value = true;
        error.value = null;
        try {
            const res = await employerMemberService.getMyPermissions();
            myPermissions.value = res.data;
            return res.data;
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Không thể lấy thông tin quyền hạn của bạn';
            throw err;
        } finally {
            loading.value = false;
        }
    }

    /**
     * Lấy lịch sử thay đổi quyền toàn công ty (có phân trang)
     */
    async function getCompanyPermissionHistory(params?: Record<string, unknown>) {
        loading.value = true;
        error.value = null;
        try {
            const res = await employerMemberService.getCompanyPermissionHistory(params);
            companyPermissionHistory.value = res.data;
            return res.data;
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Không thể tải lịch sử thay đổi quyền';
            throw err;
        } finally {
            loading.value = false;
        }
    }

    /**
     * Lấy lịch sử thay đổi quyền của một thành viên cụ thể
     */
    async function getMemberPermissionHistory(targetUserId: number) {
        loading.value = true;
        error.value = null;
        try {
            const res = await employerMemberService.getMemberPermissionHistory(targetUserId);
            memberPermissionHistory.value = res.data;
            return res.data;
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Không thể tải lịch sử thay đổi quyền thành viên';
            throw err;
        } finally {
            loading.value = false;
        }
    }

    /**
     * Lấy thống kê thành viên
     */
    async function fetchStatistics() {
        loading.value = true;
        error.value = null;
        try {
            const data = await employerMemberService.getMemberStatistics();
            memberStatistics.value = data;
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Không thể tải thống kê thành viên';
        } finally {
            loading.value = false;
        }
    }

    function reset() {
        members.value = null;
        memberPermissions.value = [];
        myPermissions.value = null;
        companyPermissionHistory.value = null;
        memberPermissionHistory.value = [];
        loading.value = false;
        error.value = null;
    }

    return {
        // State
        members,
        memberPermissions,
        myPermissions,
        companyPermissionHistory,
        memberPermissionHistory,
        loading,
        error,

        // Actions
        getMembers,
        addMember,
        updateMemberPermission,
        removeMember,
        getBatchMemberPermissions,
        getMyPermissions,
        getCompanyPermissionHistory,
        getMemberPermissionHistory,
        fetchStatistics,
        memberStatistics,
        reset
    };
});
