import { type FileUploadType } from '@/constants/fileUpload.constants'
import type { RestResponse } from '@/types/common.types'
import axiosInstance from './axios'

export interface ResFileUpload {
  fileUrl: string
  type: FileUploadType
}

const fileUploadService = {
  async uploadFile(file: File, type: FileUploadType): Promise<ResFileUpload> {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('type', type)

    const res = await axiosInstance.post<RestResponse<ResFileUpload>>(
      '/files/upload',
      formData,
      { headers: { 'Content-Type': 'multipart/form-data' } },
    )

    return res.data.data
  },
}

export default fileUploadService
