import axiosInstance from './axios'
import type { 
  ResAuditLogPagination, 
  ResAuditLogDetailDTO, 
  ResBusinessEventLogPagination, 
  ResBusinessEventLogDetailDTO,
  ResEmployerLogStatisticsDTO,
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

const employerLogService = {
  /**
   * GET /employer/logs/audit
   */
  async getAuditLogs(params: LogQueryParams): Promise<ResAuditLogPagination> {
    const response = await axiosInstance.get('/employer/logs/audit', { 
      params: cleanParams(params) 
    })
    return response.data.data
  },

  /**
   * GET /employer/logs/audit/{id}
   */
  async getAuditLogDetail(id: number): Promise<ResAuditLogDetailDTO> {
    const response = await axiosInstance.get(`/employer/logs/audit/${id}`)
    return response.data.data
  },

  /**
   * GET /employer/logs/business
   */
  async getBusinessLogs(params: LogQueryParams): Promise<ResBusinessEventLogPagination> {
    const response = await axiosInstance.get('/employer/logs/business', { 
      params: cleanParams(params) 
    })
    return response.data.data
  },

  /**
   * GET /employer/logs/business/{id}
   */
  async getBusinessLogDetail(id: number): Promise<ResBusinessEventLogDetailDTO> {
    const response = await axiosInstance.get(`/employer/logs/business/${id}`)
    return response.data.data
  },

  /**
   * GET /employer/logs/statistics
   */
  async getLogStatistics(): Promise<ResEmployerLogStatisticsDTO> {
    const response = await axiosInstance.get('/employer/logs/statistics')
    return response.data.data
  }
}

export default employerLogService
