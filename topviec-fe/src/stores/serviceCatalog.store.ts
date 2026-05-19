import { defineStore } from 'pinia'
import { ref } from 'vue'
import { serviceCatalogService } from '@/services/serviceCatalog.service'
import type { ResServiceDTO, AdminServiceQueryParams, ResAdminServiceStatisticsDTO } from '@/types/serviceCatalog.types'
import type { PaginationMeta } from '@/types/common.types'

export const useServiceCatalogStore = defineStore('serviceCatalog', () => {
    const services = ref<ResServiceDTO[]>([])
    const meta = ref<PaginationMeta | null>(null)
    const currentService = ref<ResServiceDTO | null>(null)
    const serviceStatistics = ref<ResAdminServiceStatisticsDTO | null>(null)
    const loading = ref(false)
    const error = ref<string | null>(null)

    async function fetchServices(params?: AdminServiceQueryParams) {
        loading.value = true
        error.value = null
        try {
            const data = await serviceCatalogService.getAllServices(params)
            services.value = data.result
            meta.value = data.meta
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Không thể tải danh sách dịch vụ'
            throw err
        } finally {
            loading.value = false
        }
    }

    async function fetchServiceById(id: number) {
        loading.value = true
        error.value = null
        try {
            const data = await serviceCatalogService.getServiceById(id)
            currentService.value = data
            return data
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Không thể tải thông tin dịch vụ'
            throw err
        } finally {
            loading.value = false
        }
    }

    async function createService(payload: import('@/types/serviceCatalog.types').ReqServiceDTO) {
        loading.value = true
        error.value = null
        try {
            const data = await serviceCatalogService.createService(payload)
            return data
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Không thể tạo dịch vụ mới'
            throw err
        } finally {
            loading.value = false
        }
    }

    async function updateService(id: number, payload: import('@/types/serviceCatalog.types').ReqServiceDTO) {
        loading.value = true
        error.value = null
        try {
            const data = await serviceCatalogService.updateService(id, payload)
            const index = services.value.findIndex(s => s.id === id)
            if (index !== -1) {
                services.value[index] = data
            }
            if (currentService.value?.id === id) {
                currentService.value = data
            }
            return data
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Không thể cập nhật dịch vụ'
            throw err
        } finally {
            loading.value = false
        }
    }

    async function fetchStatistics() {
        loading.value = true
        error.value = null
        try {
            serviceStatistics.value = await serviceCatalogService.getStatistics()
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Không thể tải thống kê dịch vụ'
            throw err
        } finally {
            loading.value = false
        }
    }

    return {
        services,
        meta,
        currentService,
        loading,
        error,
        fetchServices,
        fetchServiceById,
        createService,
        updateService,
        fetchStatistics,
        serviceStatistics
    }
})
