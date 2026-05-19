import axiosInstance from './axios'
import type { RestResponse, ResultPaginationDTO } from '@/types/common.types'
import type { ResLocationDTO, MasterDataQueryParams } from '@/types/masterData.types'

export const locationService = {
    /**
     * Lấy danh sách Location
     * GET /api/v1/locations
     */
    async getLocations(params?: MasterDataQueryParams): Promise<ResultPaginationDTO<ResLocationDTO>> {
        const res = await axiosInstance.get<RestResponse<ResultPaginationDTO<ResLocationDTO>>>(
            '/locations',
            { params }
        )
        return res.data.data
    },

    /**
     * Lấy chi tiết Location theo ID
     * GET /api/v1/locations/{id}
     */
    async getLocationById(id: number | string): Promise<ResLocationDTO> {
        const res = await axiosInstance.get<RestResponse<ResLocationDTO>>(`/locations/${id}`)
        return res.data.data
    }
}
