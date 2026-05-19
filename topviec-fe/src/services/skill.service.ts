import axiosInstance from './axios'
import type { RestResponse, ResultPaginationDTO } from '@/types/common.types'
import type { ResSkillDTO, MasterDataQueryParams } from '@/types/masterData.types'

export const skillService = {
    /**
     * Lấy danh sách Skill
     * GET /api/v1/skills
     */
    async getSkills(params?: MasterDataQueryParams): Promise<ResultPaginationDTO<ResSkillDTO>> {
        const res = await axiosInstance.get<RestResponse<ResultPaginationDTO<ResSkillDTO>>>(
            '/skills',
            { params }
        )
        return res.data.data
    },

    /**
     * Lấy chi tiết Skill theo ID
     * GET /api/v1/skills/{id}
     */
    async getSkillById(id: number | string): Promise<ResSkillDTO> {
        const res = await axiosInstance.get<RestResponse<ResSkillDTO>>(`/skills/${id}`)
        return res.data.data
    }
}
