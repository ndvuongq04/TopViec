import axiosInstance from './axios'
import type { 
    ResAdminCandidateDetailDTO, 
    AdminCandidateQueryParams, 
    ResAdminCandidatePagination,
    ResAdminCandidateStatisticsDTO
} from '@/types/adminCandidate.types'
import type { ResultPaginationDTO } from '@/types/common.types'
import type { ResCv } from '@/types/cvs.types'
import type { ResApplication } from '@/types/application.types'
import type { ResCompanyDTO } from '@/types/company.types'
import type { ResSavedJobDTO } from '@/types/savedJob.types'

const adminCandidateService = {
    /**
     * GET /admin/candidates
     */
    async getCandidates(params: AdminCandidateQueryParams): Promise<ResAdminCandidatePagination> {
        const response = await axiosInstance.get('/admin/candidates', { params })
        return response.data.data
    },

    /**
     * GET /admin/candidates/{id}
     */
    async getCandidateDetail(userId: number): Promise<ResAdminCandidateDetailDTO> {
        const response = await axiosInstance.get(`/admin/candidates/${userId}`)
        return response.data.data
    },

    /**
     * GET /admin/candidates/{userId}/cvs
     */
    async getCandidateCvs(userId: number, params?: { page?: number; size?: number }): Promise<ResultPaginationDTO<ResCv>> {
        const response = await axiosInstance.get(`/admin/candidates/${userId}/cvs`, { params })
        return response.data.data
    },

    /**
     * GET /admin/candidates/{userId}/applications
     */
    async getCandidateApplications(userId: number, params?: { page?: number; size?: number }): Promise<ResultPaginationDTO<ResApplication>> {
        const response = await axiosInstance.get(`/admin/candidates/${userId}/applications`, { params })
        return response.data.data
    },

    /**
     * GET /admin/candidates/{userId}/followed-companies
     */
    async getCandidateFollowedCompanies(userId: number, params?: { page?: number; size?: number }): Promise<ResultPaginationDTO<ResCompanyDTO>> {
        const response = await axiosInstance.get(`/admin/candidates/${userId}/followed-companies`, { params })
        return response.data.data
    },

    /**
     * GET /admin/candidates/{userId}/saved-jobs
     */
    async getCandidateSavedJobs(userId: number, params?: { page?: number; size?: number }): Promise<ResultPaginationDTO<ResSavedJobDTO>> {
        const response = await axiosInstance.get(`/admin/candidates/${userId}/saved-jobs`, { params })
        return response.data.data
    },

    /**
     * GET /admin/candidates/{userId}/statistics
     */
    async getCandidateStatistics(userId: number): Promise<ResAdminCandidateStatisticsDTO> {
        const response = await axiosInstance.get(`/admin/candidates/${userId}/statistics`)
        return response.data.data
    },

    /**
     * PATCH /admin/candidates/{userId}/toggle-status
     * Returns new status string ('active' | 'locked_perm')
     */
    async toggleCandidateStatus(userId: number): Promise<string> {
        const response = await axiosInstance.patch(`/admin/candidates/${userId}/toggle-status`)
        return response.data.data
    }
}

export default adminCandidateService
