import { defineStore } from 'pinia'
import { ref } from 'vue'
import { industryService } from '@/services/industry.service'
import type { ResIndustryDTO, MasterDataQueryParams } from '@/types/masterData.types'
import type { PaginationMeta } from '@/types/common.types'

export const useIndustryStore = defineStore('industry', () => {
  const industries = ref<ResIndustryDTO[]>([])
  const meta = ref<PaginationMeta | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function fetchIndustries(params?: MasterDataQueryParams) {
    loading.value = true
    error.value = null
    try {
      const result = await industryService.getIndustries(params)
      // Append if fetching more pages, or replace if new fetch
      if (params?.page && params.page > 0) {
        industries.value.push(...result.result)
      } else {
        industries.value = result.result
      }
      meta.value = result.meta
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Lỗi khi tải danh sách ngành nghề'
    } finally {
      loading.value = false
    }
  }

  return {
    industries,
    meta,
    loading,
    error,
    fetchIndustries
  }
})
