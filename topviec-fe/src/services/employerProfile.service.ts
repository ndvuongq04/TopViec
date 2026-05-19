import axiosInstance from './axios'
import type { RestResponse } from '@/types/common.types'
import type { ResEmployerProfileDTO } from '@/types/companyMember.types'

export const employerProfileService = {
  async getMyProfile(): Promise<ResEmployerProfileDTO> {
    const res = await axiosInstance.get<RestResponse<ResEmployerProfileDTO>>('/employer/profile/me')
    return res.data.data
  },
}
