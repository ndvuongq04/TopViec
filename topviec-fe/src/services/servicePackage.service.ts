import axiosInstance from './axios'
import type { RestResponse, ResultPaginationDTO } from '@/types/common.types'
import type {
    ReqServicePackageDTO,
    ResServicePackageDTO,
    AdminServicePackageQueryParams,
} from '@/types/servicePackage.types'

const BASE_URL = '/admin/service-packages'

export const adminServicePackageService = {
    async getAll(params?: AdminServicePackageQueryParams): Promise<ResultPaginationDTO<ResServicePackageDTO>> {
        const res = await axiosInstance.get<RestResponse<ResultPaginationDTO<ResServicePackageDTO>>>(BASE_URL, { params })
        return res.data.data
    },

    async getById(id: number): Promise<ResServicePackageDTO> {
        const res = await axiosInstance.get<RestResponse<ResServicePackageDTO>>(`${BASE_URL}/${id}`)
        return res.data.data
    },

    async create(payload: ReqServicePackageDTO): Promise<ResServicePackageDTO> {
        const res = await axiosInstance.post<RestResponse<ResServicePackageDTO>>(BASE_URL, payload)
        return res.data.data
    },

    async update(id: number, payload: ReqServicePackageDTO): Promise<ResServicePackageDTO> {
        const res = await axiosInstance.put<RestResponse<ResServicePackageDTO>>(`${BASE_URL}/${id}`, payload)
        return res.data.data
    },
}
