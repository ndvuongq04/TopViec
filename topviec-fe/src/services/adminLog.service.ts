import axiosInstance from './axios'
import type { 
  ResAuditLogPagination, 
  ResAuditLogDetailDTO, 
  ResBusinessEventLogPagination, 
  ResBusinessEventLogDetailDTO,
  ResAdminLogStatisticsDTO,
  LogQueryParams
} from '@/types/logs.types'

const cleanParams = (params: LogQueryParams) => {
  const cleaned: any = { ...params }
  Object.keys(cleaned).forEach(key => {
    if (cleaned[key] === '' || cleaned[key] === undefined || cleaned[key] === null) {
      delete cleaned[key]
    }
  })
  return cleaned
}

const adminLogService = {
  /**
   * GET /admin/logs/audit
   */
  async getAuditLogs(params: LogQueryParams): Promise<ResAuditLogPagination> {
    const response = await axiosInstance.get('/admin/logs/audit', { 
      params: cleanParams(params) 
    })
    return response.data.data
  },

  /**
   * GET /admin/logs/audit/{id}
   */
  async getAuditLogDetail(id: number): Promise<ResAuditLogDetailDTO> {
    const response = await axiosInstance.get(`/admin/logs/audit/${id}`)
    return response.data.data
  },

  /**
   * GET /admin/logs/business
   */
  async getBusinessLogs(params: LogQueryParams): Promise<ResBusinessEventLogPagination> {
    const response = await axiosInstance.get('/admin/logs/business', { 
      params: cleanParams(params) 
    })
    return response.data.data
  },

  /**
   * GET /admin/logs/business/{id}
   */
  async getBusinessLogDetail(id: number): Promise<ResBusinessEventLogDetailDTO> {
    const response = await axiosInstance.get(`/admin/logs/business/${id}`)
    return response.data.data
  },

  /**
   * GET /admin/logs/statistics
   */
  async getLogStatistics(): Promise<ResAdminLogStatisticsDTO> {
    const response = await axiosInstance.get('/admin/logs/statistics')
    return response.data.data
  }
}

export default adminLogService
