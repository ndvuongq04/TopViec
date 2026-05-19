import { defineStore } from 'pinia'
import { ref } from 'vue'
import { adminViolationScoreService } from '@/services/adminViolationScore.service'
import type { ResAppeal } from '@/types/appeal.types'
import type {
  ReqAdjustViolationScore,
  ReqResetViolationScore,
  ReqUnsuspendViolationScore,
  ResViolationScore,
} from '@/types/violationScore.types'

export const useAdminViolationScoreStore = defineStore('adminViolationScore', () => {
  const violationScore = ref<ResViolationScore | null>(null)
  const appeals = ref<ResAppeal[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  function setError(err: unknown) {
    const msg = (err as any)?.response?.data?.message
    if (typeof msg === 'object' && msg !== null) {
      error.value = Object.values(msg).join(', ')
    } else {
      error.value = msg ?? 'Có lỗi xảy ra. Vui lòng thử lại.'
    }
  }

  async function fetchByEmployerId(employerId: number): Promise<ResViolationScore> {
    loading.value = true
    error.value = null
    try {
      violationScore.value = await adminViolationScoreService.getByEmployerId(employerId)
      return violationScore.value
    } catch (err) {
      setError(err)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function resetScore(
    employerId: number,
    payload: ReqResetViolationScore,
  ): Promise<ResViolationScore> {
    loading.value = true
    error.value = null
    try {
      violationScore.value = await adminViolationScoreService.resetByEmployerId(employerId, payload)
      return violationScore.value
    } catch (err) {
      setError(err)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function adjustScore(
    employerId: number,
    payload: ReqAdjustViolationScore,
  ): Promise<ResViolationScore> {
    loading.value = true
    error.value = null
    try {
      violationScore.value = await adminViolationScoreService.adjustByEmployerId(employerId, payload)
      return violationScore.value
    } catch (err) {
      setError(err)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function fetchAppealsByEmployerId(employerId: number): Promise<ResAppeal[]> {
    loading.value = true
    error.value = null
    try {
      appeals.value = await adminViolationScoreService.getAppealsByEmployerId(employerId)
      return appeals.value
    } catch (err) {
      setError(err)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function unsuspendEmployer(
    employerId: number,
    payload: ReqUnsuspendViolationScore,
  ): Promise<ResAppeal> {
    loading.value = true
    error.value = null
    try {
      const updatedAppeal = await adminViolationScoreService.unsuspendByEmployerId(
        employerId,
        payload,
      )
      appeals.value = appeals.value.map((item) => (item.id === updatedAppeal.id ? updatedAppeal : item))
      return updatedAppeal
    } catch (err) {
      setError(err)
      throw err
    } finally {
      loading.value = false
    }
  }

  function reset() {
    violationScore.value = null
    appeals.value = []
    loading.value = false
    error.value = null
  }

  return {
    violationScore,
    appeals,
    loading,
    error,
    fetchByEmployerId,
    resetScore,
    adjustScore,
    fetchAppealsByEmployerId,
    unsuspendEmployer,
    reset,
  }
})
