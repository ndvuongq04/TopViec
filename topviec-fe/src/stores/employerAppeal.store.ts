import { defineStore } from 'pinia'
import { ref } from 'vue'
import { employerAppealService } from '@/services/employerAppeal.service'
import employerReportService from '@/services/employerReport.service'
import type { ReqSubmitAppeal, ResAppeal } from '@/types/appeal.types'

export const useEmployerAppealStore = defineStore('employerAppeal', () => {
  const currentAppeal = ref<ResAppeal | null>(null)
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

  async function createAppeal(complaintId: number, payload: Omit<ReqSubmitAppeal, 'complaintId'>): Promise<ResAppeal> {
    loading.value = true
    error.value = null
    try {
      currentAppeal.value = await employerAppealService.create({ complaintId, ...payload })
      return currentAppeal.value
    } catch (err) {
      setError(err)
      throw err
    } finally {
      loading.value = false
    }
  }

  async function fetchCurrentAppeal(complaintId: number): Promise<ResAppeal | null> {
    loading.value = true
    error.value = null
    try {
      currentAppeal.value = await employerReportService.getAppealByComplaint(complaintId)
      return currentAppeal.value
    } catch (err) {
      setError(err)
      throw err
    } finally {
      loading.value = false
    }
  }

  function reset() {
    currentAppeal.value = null
    loading.value = false
    error.value = null
  }

  return {
    currentAppeal,
    loading,
    error,
    createAppeal,
    fetchCurrentAppeal,
    reset,
  }
})
