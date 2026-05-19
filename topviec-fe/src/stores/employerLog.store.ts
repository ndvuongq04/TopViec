import { defineStore } from 'pinia'
import { ref } from 'vue'
import employerLogService from '@/services/employerLog.service'
import type { 
  ResAuditLogDTO, 
  ResAuditLogDetailDTO, 
  ResBusinessEventLogDTO, 
  ResBusinessEventLogDetailDTO,
  ResEmployerLogStatisticsDTO,
  LogQueryParams
} from '@/types/logs.types'
import type { PaginationMeta } from '@/types/common.types'

export const useEmployerLogStore = defineStore('employerLog', () => {
  // ─── State ──────────────────────────────────────────────────────────────────
  const auditLogs = ref<ResAuditLogDTO[]>([])
  const businessLogs = ref<ResBusinessEventLogDTO[]>([])
  const selectedAuditLog = ref<ResAuditLogDetailDTO | null>(null)
  const selectedBusinessLog = ref<ResBusinessEventLogDetailDTO | null>(null)
  
  const auditMeta = ref<PaginationMeta>({ page: 0, pageSize: 20, pages: 0, totals: 0 })
  const businessMeta = ref<PaginationMeta>({ page: 0, pageSize: 20, pages: 0, totals: 0 })
  
  const loading = ref(false)
  const error = ref<string | null>(null)

  const statistics = ref<ResEmployerLogStatisticsDTO | null>(null)

  // ─── Helpers ────────────────────────────────────────────────────────────────
  function setError(err: unknown) {
    const msg = (err as any)?.response?.data?.message
    if (typeof msg === 'object' && msg !== null) {
      error.value = Object.values(msg).join(', ')
    } else {
      error.value = msg ?? 'Có lỗi xảy ra. Vui lòng thử lại.'
    }
  }

  // ─── Actions ────────────────────────────────────────────────────────────────

  /** Audit Logs */
  async function fetchAuditLogs(params: LogQueryParams) {
    loading.value = true
    error.value = null
    try {
      const data = await employerLogService.getAuditLogs(params)
      auditLogs.value = data.result
      auditMeta.value = data.meta
    } catch (err) {
      setError(err)
    } finally {
      loading.value = false
    }
  }

  async function fetchAuditLogDetail(id: number) {
    loading.value = true
    error.value = null
    try {
      selectedAuditLog.value = await employerLogService.getAuditLogDetail(id)
    } catch (err) {
      setError(err)
    } finally {
      loading.value = false
    }
  }

  /** Business Logs */
  async function fetchBusinessLogs(params: LogQueryParams) {
    loading.value = true
    error.value = null
    try {
      const data = await employerLogService.getBusinessLogs(params)
      businessLogs.value = data.result
      businessMeta.value = data.meta
    } catch (err) {
      setError(err)
    } finally {
      loading.value = false
    }
  }

  async function fetchBusinessLogDetail(id: number) {
    loading.value = true
    error.value = null
    try {
      selectedBusinessLog.value = await employerLogService.getBusinessLogDetail(id)
    } catch (err) {
      setError(err)
    } finally {
      loading.value = false
    }
  }

  function reset() {
    auditLogs.value = []
    businessLogs.value = []
    selectedAuditLog.value = null
    selectedBusinessLog.value = null
    auditMeta.value = { page: 0, pageSize: 20, pages: 0, totals: 0 }
    businessMeta.value = { page: 0, pageSize: 20, pages: 0, totals: 0 }
    loading.value = false
    error.value = null
  }

  /** Statistics */
  async function fetchLogStatistics() {
    try {
      statistics.value = await employerLogService.getLogStatistics()
    } catch (err) {
      setError(err)
    }
  }

  return {
    auditLogs,
    businessLogs,
    selectedAuditLog,
    selectedBusinessLog,
    auditMeta,
    businessMeta,
    statistics,
    loading,
    error,
    fetchAuditLogs,
    fetchAuditLogDetail,
    fetchBusinessLogs,
    fetchBusinessLogDetail,
    fetchLogStatistics,
    reset
  }
})
