import axiosInstance from './axios'
import type { ResSavedJobDTO, ResSavedJobPagination } from '@/types/savedJob.types'
import type { RestResponse } from '@/types/common.types'

const API_URL = '/saved-jobs'

export const savedJobService = {
  /**
   * Toggle save/unsave a job posting
   */
  async toggleSave(jobPostId: number) {
    const res = await axiosInstance.post<RestResponse<ResSavedJobDTO>>(`${API_URL}/${jobPostId}/toggle`)
    return res.data.data
  },

  /**
   * Check if a job posting is saved by the current user
   */
  async isSaved(jobPostId: number) {
    const res = await axiosInstance.get<RestResponse<{ isSaved: boolean }>>(`${API_URL}/${jobPostId}/status`)
    return res.data.data
  },

  /**
   * Get paginated list of saved jobs
   */
  async getSavedJobs(page = 0, size = 10) {
    const res = await axiosInstance.get<RestResponse<ResSavedJobPagination>>(API_URL, {
      params: { page, size },
    })
    return res.data.data
  },

  /**
   * Unsave a job posting
   */
  async unsave(jobPostId: number) {
    await axiosInstance.delete(`${API_URL}/${jobPostId}`)
  },
}
