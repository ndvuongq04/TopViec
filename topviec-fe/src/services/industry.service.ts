import axiosInstance from './axios'
import type { ResultPaginationDTO, RestResponse } from '@/types/common.types'
import type { ResIndustryDTO, MasterDataQueryParams } from '@/types/masterData.types'

export const industryService = {
  async getIndustries(params?: MasterDataQueryParams): Promise<ResultPaginationDTO<ResIndustryDTO>> {
    const res = await axiosInstance.get<RestResponse<ResultPaginationDTO<ResIndustryDTO>>>('/industries', { params })
    return res.data.data
  },
  async getIndustryById(id: number | string): Promise<ResIndustryDTO> {
    const res = await axiosInstance.get<RestResponse<ResIndustryDTO>>(`/industries/${id}`)
    return res.data.data
  }
}
