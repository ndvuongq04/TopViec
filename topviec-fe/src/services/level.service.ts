import axiosInstance from './axios'
import type { RestResponse, ResultPaginationDTO } from '@/types/common.types'
import type { ResLevelDTO, MasterDataQueryParams } from '@/types/masterData.types'

export const levelService = {
    /**
     * Lấy danh sách Level
     * GET /api/v1/levels
     */
    async getLevels(params?: MasterDataQueryParams): Promise<ResultPaginationDTO<ResLevelDTO>> {
        const res = await axiosInstance.get<RestResponse<ResultPaginationDTO<ResLevelDTO>>>(
            '/levels',
            { params }
        )
        return res.data.data
    },

    /**
     * Lấy chi tiết Level theo ID
     * GET /api/v1/levels/{id}
     */
    async getLevelById(id: number | string): Promise<ResLevelDTO> {
        const res = await axiosInstance.get<RestResponse<ResLevelDTO>>(`/levels/${id}`)
        return res.data.data
    }
}
