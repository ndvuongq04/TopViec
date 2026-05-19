import axiosInstance from './axios'
import type { RestResponse } from '@/types/common.types'
import type { ReqCreateAppeal, ResAppeal } from '@/types/appeal.types'

const employerAppealService = {
  async submitAppeal(reportId: number, payload: ReqCreateAppeal): Promise<ResAppeal> {
    const res = await axiosInstance.post<RestResponse<ResAppeal>>(
      `/employer/me/reports/${reportId}/appeal`,
      payload,
    )
    return res.data.data
  },

  async create(payload: { complaintId: number; content: string }): Promise<ResAppeal> {
    return this.submitAppeal(payload.complaintId, { content: payload.content })
  },
}

export { employerAppealService }
