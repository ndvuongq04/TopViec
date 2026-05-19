import axiosInstance from './axios'
import type { RestResponse } from '@/types/common.types'
import type { ReqUnsuspendAppeal, ResAppeal } from '@/types/appeal.types'
import type {
  ReqAdjustViolationScore,
  ReqResetViolationScore,
  ReqUnsuspendViolationScore,
  ResViolationScore,
} from '@/types/violationScore.types'

const adminViolationScoreService = {
  async getByEmployerId(employerId: number): Promise<ResViolationScore> {
    const res = await axiosInstance.get<RestResponse<ResViolationScore>>(
      `/admin/employers/${employerId}/violation-score`,
    )
    return res.data.data
  },

  async resetByEmployerId(
    employerId: number,
    payload: ReqResetViolationScore,
  ): Promise<ResViolationScore> {
    const res = await axiosInstance.post<RestResponse<ResViolationScore>>(
      `/admin/employers/${employerId}/violation-score/reset`,
      payload,
    )
    return res.data.data
  },

  async adjustByEmployerId(
    employerId: number,
    payload: ReqAdjustViolationScore,
  ): Promise<ResViolationScore> {
    const res = await axiosInstance.patch<RestResponse<ResViolationScore>>(
      `/admin/employers/${employerId}/violation-score/adjust`,
      payload,
    )
    return res.data.data
  },

  async getAppealsByEmployerId(employerId: number): Promise<ResAppeal[]> {
    const res = await axiosInstance.get<RestResponse<ResAppeal[]>>(
      `/admin/employers/${employerId}/appeals`,
    )
    return res.data.data
  },

  async unsuspendByEmployerId(
    employerId: number,
    payload: ReqUnsuspendViolationScore | ReqUnsuspendAppeal,
  ): Promise<ResAppeal> {
    const res = await axiosInstance.post<RestResponse<ResAppeal>>(
      `/admin/employers/${employerId}/unsuspend`,
      payload,
    )
    return res.data.data
  },
}

export { adminViolationScoreService }
