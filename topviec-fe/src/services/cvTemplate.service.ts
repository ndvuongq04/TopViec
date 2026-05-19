import axiosInstance from './axios'
import type { RestResponse, ResultPaginationDTO } from '@/types/common.types'
import type {
    CvTemplateDetail,
    CvTemplateListItem,
    ReqAdminCvTemplatePreview,
    ReqCreateCvTemplate,
    ReqGetAdminCvTemplates,
    ReqUpdateCvTemplateContent,
    ReqUpdateCvTemplateMetadata,
    ResAdminCvTemplatePreview,
    CvOnlineExtraData,
} from '@/types/cvOnline.types'

const PUBLIC_BASE_URL = '/cv-templates'
const ADMIN_BASE_URL = '/admin/cv-templates'

const cvTemplateService = {
    async getActiveTemplates(): Promise<CvTemplateListItem[]> {
        const res = await axiosInstance.get<RestResponse<CvTemplateListItem[]>>(PUBLIC_BASE_URL)
        return res.data.data
    },

    async getActiveTemplateById(id: number): Promise<CvTemplateDetail> {
        const res = await axiosInstance.get<RestResponse<CvTemplateDetail>>(`${PUBLIC_BASE_URL}/${id}`)
        return res.data.data
    },

    async getAdminTemplates(params: ReqGetAdminCvTemplates): Promise<ResultPaginationDTO<CvTemplateListItem>> {
        const res = await axiosInstance.get<RestResponse<ResultPaginationDTO<CvTemplateListItem>>>(
            ADMIN_BASE_URL,
            { params },
        )
        return res.data.data
    },

    async getAdminTemplateById(id: number): Promise<CvTemplateDetail> {
        const res = await axiosInstance.get<RestResponse<CvTemplateDetail>>(`${ADMIN_BASE_URL}/${id}`)
        return res.data.data
    },

    async getAdminTemplateSampleData(): Promise<CvOnlineExtraData> {
        const res = await axiosInstance.get<RestResponse<CvOnlineExtraData>>(
            `${ADMIN_BASE_URL}/sample-data`,
        )
        return res.data.data
    },

    async previewAdminTemplate(payload: ReqAdminCvTemplatePreview): Promise<ResAdminCvTemplatePreview> {
        const res = await axiosInstance.post<RestResponse<ResAdminCvTemplatePreview>>(
            `${ADMIN_BASE_URL}/preview`,
            payload,
        )
        return res.data.data
    },

    async createTemplate(payload: ReqCreateCvTemplate): Promise<CvTemplateDetail> {
        const formData = new FormData()
        formData.append('name', payload.name)
        formData.append('slug', payload.slug)
        formData.append('description', payload.description)
        formData.append('htmlContent', payload.htmlContent)
        formData.append('cssContent', payload.cssContent)
        formData.append('isActive', String(payload.isActive))
        formData.append('isDefault', String(payload.isDefault))
        if (payload.thumbnail) {
            formData.append('thumbnail', payload.thumbnail)
        }

        const res = await axiosInstance.post<RestResponse<CvTemplateDetail>>(ADMIN_BASE_URL, formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
        })
        return res.data.data
    },

    async updateTemplateMetadata(id: number, payload: ReqUpdateCvTemplateMetadata): Promise<CvTemplateDetail> {
        const res = await axiosInstance.put<RestResponse<CvTemplateDetail>>(
            `${ADMIN_BASE_URL}/${id}`,
            payload,
        )
        return res.data.data
    },

    async updateTemplateContent(id: number, payload: ReqUpdateCvTemplateContent): Promise<CvTemplateDetail> {
        const res = await axiosInstance.put<RestResponse<CvTemplateDetail>>(
            `${ADMIN_BASE_URL}/${id}/content`,
            payload,
        )
        return res.data.data
    },

    async activateTemplate(id: number): Promise<CvTemplateDetail> {
        const res = await axiosInstance.patch<RestResponse<CvTemplateDetail>>(
            `${ADMIN_BASE_URL}/${id}/activate`,
        )
        return res.data.data
    },

    async deactivateTemplate(id: number): Promise<CvTemplateDetail> {
        const res = await axiosInstance.patch<RestResponse<CvTemplateDetail>>(
            `${ADMIN_BASE_URL}/${id}/deactivate`,
        )
        return res.data.data
    },

    async setDefaultTemplate(id: number): Promise<CvTemplateDetail> {
        const res = await axiosInstance.patch<RestResponse<CvTemplateDetail>>(
            `${ADMIN_BASE_URL}/${id}/default`,
        )
        return res.data.data
    },
}

export default cvTemplateService
