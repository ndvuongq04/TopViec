import axiosInstance from './axios'
import type { RestResponse, ResultPaginationDTO } from '@/types/common.types'
import type {
    ReqAddonServiceDTO,
    ResAddonServiceDTO,
    AdminAddonServiceQueryParams,
} from '@/types/serviceCatalog.types'

const BASE_URL = '/admin/addon-services'

export const adminAddonServiceService = {
    async getAll(params?: AdminAddonServiceQueryParams): Promise<ResultPaginationDTO<ResAddonServiceDTO>> {
        const res = await axiosInstance.get<RestResponse<ResultPaginationDTO<ResAddonServiceDTO>>>(BASE_URL, { params })
        return res.data.data
    },

    async getById(id: number): Promise<ResAddonServiceDTO> {
        const res = await axiosInstance.get<RestResponse<ResAddonServiceDTO>>(`${BASE_URL}/${id}`)
        return res.data.data
    },

    async create(payload: ReqAddonServiceDTO): Promise<ResAddonServiceDTO> {
        const res = await axiosInstance.post<RestResponse<ResAddonServiceDTO>>(BASE_URL, payload)
        return res.data.data
    },

    async update(id: number, payload: ReqAddonServiceDTO): Promise<ResAddonServiceDTO> {
        const res = await axiosInstance.put<RestResponse<ResAddonServiceDTO>>(`${BASE_URL}/${id}`, payload)
        return res.data.data
    },
}
