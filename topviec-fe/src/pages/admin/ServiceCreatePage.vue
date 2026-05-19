<template>
  <div class="space-y-8">
    <!-- Header -->
    <div class="flex flex-wrap items-center justify-between gap-4">
      <div>
        <h2 class="text-3xl font-black text-slate-900 dark:text-slate-100 tracking-tight">Chi tiết dịch vụ</h2>
        <p class="text-slate-500 text-sm mt-1">Điền thông tin chi tiết để cấu hình gói dịch vụ lẻ mới trong hệ thống.</p>
      </div>
    </div>

    <ServiceCreateForm
      :groups="serviceGroups"
      @submit="handleSubmit"
    />

    <!-- Table Loading State -->
    <div v-if="serviceStore.loading && serviceItems.length === 0" class="flex flex-col items-center justify-center py-20 bg-white rounded-xl border border-slate-200">
      <span class="material-symbols-outlined animate-spin text-4xl text-slate-400 mb-2">progress_activity</span>
      <span class="text-slate-500 font-medium">Đang tải danh sách dịch vụ...</span>
    </div>

    <ServiceListTable
      v-else
      :items="serviceItems"
      @toggle="handleToggle"
      @edit="handleEdit"
      @view="handleView"
    >
      <template #filter>
        <div class="flex gap-4">
          <div class="flex-1 flex items-center bg-slate-50 border border-[#963131]/10 rounded-lg px-4 focus-within:ring-2 focus-within:ring-[#963131]/20 transition-all">
            <span class="material-symbols-outlined text-slate-400 text-xl">search</span>
            <input 
              v-model="filters.keyword" 
              @keyup.enter="handleSearch"
              class="bg-transparent border-none focus:ring-0 text-sm w-full py-3 outline-none ml-2 text-slate-800 placeholder:text-slate-400"
              placeholder="Tìm kiếm dịch vụ theo tên hoặc mã (Enter để tìm)..."
            />
          </div>
          <div class="relative w-64">
            <select 
              v-model="filters.category"
              @change="handleSearch"
              class="w-full bg-slate-50 border border-[#963131]/10 rounded-lg text-sm px-4 py-3 focus:ring-2 focus:ring-[#963131]/20 outline-none cursor-pointer text-slate-800 transition-all appearance-none bg-none"
            >
              <option value="">Tất cả nhóm dịch vụ</option>
              <option v-for="g in serviceGroups" :key="g.value" :value="g.value">
                {{ g.label }}
              </option>
            </select>
            <span class="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none">expand_content</span>
          </div>
        </div>
      </template>
    </ServiceListTable>

    <ServiceTipCards />

    <!-- Edit Modal -->
    <ServiceEditModal
      :visible="showEditModal"
      :service="selectedService"
      :submitting="isEditing"
      @close="showEditModal = false"
      @submit="handleEditSubmit"
    />

    <!-- Detail Modal -->
    <ServiceDetailModal
      :visible="showDetailModal"
      :service="serviceStore.currentService"
      :loading="isFetchingDetail"
      @close="showDetailModal = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import ServiceCreateForm from '@/components/admin/create-services/ServiceCreateForm.vue'
import ServiceListTable from '@/components/admin/create-services/ServiceListTable.vue'
import ServiceTipCards from '@/components/admin/create-services/ServiceTipCards.vue'
import ServiceEditModal from '@/components/admin/create-services/ServiceEditModal.vue'
import ServiceDetailModal from '@/components/admin/create-services/ServiceDetailModal.vue'
import {
  ServiceCategory,
  SERVICE_CATEGORY_LABELS,
  SERVICE_CATEGORY_ICON_MAP,
} from '@/constants/serviceCatalog.constants'
import { useServiceCatalogStore } from '@/stores/serviceCatalog.store'
import { useToast } from '@/composables/useToast'
import type { ResServiceDTO, ReqServiceDTO } from '@/types/serviceCatalog.types'

const serviceStore = useServiceCatalogStore()
const toast = useToast()

const serviceGroups = ref([
  {
    value: ServiceCategory.JOB_POSTING,
    icon:  SERVICE_CATEGORY_ICON_MAP[ServiceCategory.JOB_POSTING].icon,
    label: SERVICE_CATEGORY_LABELS[ServiceCategory.JOB_POSTING],
  },
  {
    value: ServiceCategory.CANDIDATE,
    icon:  SERVICE_CATEGORY_ICON_MAP[ServiceCategory.CANDIDATE].icon,
    label: SERVICE_CATEGORY_LABELS[ServiceCategory.CANDIDATE],
  },
  {
    value: ServiceCategory.BRANDING,
    icon:  SERVICE_CATEGORY_ICON_MAP[ServiceCategory.BRANDING].icon,
    label: SERVICE_CATEGORY_LABELS[ServiceCategory.BRANDING],
  },
  {
    value: ServiceCategory.ADDON_PACKAGE,
    icon:  SERVICE_CATEGORY_ICON_MAP[ServiceCategory.ADDON_PACKAGE].icon,
    label: SERVICE_CATEGORY_LABELS[ServiceCategory.ADDON_PACKAGE],
  },
])

const filters = ref({
  keyword: '',
  category: ''
})

const { services } = storeToRefs(serviceStore)

const serviceItems = computed(() => {
  return services.value.map(s => {
    const groupDef = serviceGroups.value.find(g => g.value === s.category)
    return {
      id: s.id,
      name: s.name,
      group: groupDef?.label || s.categoryName || 'Khác',
      groupIcon: groupDef?.icon || 'star',
      active: s.isActive
    }
  })
})

function handleSearch() {
  serviceStore.fetchServices({
    keyword: filters.value.keyword.trim() || undefined,
    category: filters.value.category as ServiceCategory || undefined,
    size: 20
  })
}

onMounted(() => {
  handleSearch()
})

const isSubmitting = ref(false)
const showEditModal = ref(false)
const selectedService = ref<ResServiceDTO | null>(null)
const isEditing = ref(false)

const showDetailModal = ref(false)
const isFetchingDetail = ref(false)

// Optional helper to slugify unicode string -> code (e.g., 'Tạo tin' -> 'TAO_TIN')
function generateCode(name: string): string {
  return name.trim().normalize("NFD").replace(/[\u0300-\u036f]/g, "").replace(/Đ/g, "D").replace(/đ/g, "d").replace(/[^a-zA-Z0-9\s]/g, "").replace(/\s+/g, "_").toUpperCase()
}

async function handleSubmit(payload: { group: string; name: string; description: string; code: string; unit: string }) {
  if (!payload.name.trim()) {
    toast.error('Lỗi', 'Vui lòng nhập tên dịch vụ')
    return
  }
  if (!payload.code.trim()) {
    toast.error('Lỗi', 'Vui lòng nhập mã code dịch vụ')
    return
  }
  if (!payload.unit.trim()) {
    toast.error('Lỗi', 'Vui lòng nhập đơn vị tính')
    return
  }

  isSubmitting.value = true
  try {
    const newService = await serviceStore.createService({
      code: payload.code,
      name: payload.name.trim(),
      category: payload.group as ServiceCategory,
      unit: payload.unit.trim(),
      description: payload.description ? payload.description.trim() : null,
      isActive: true,
    })

    toast.success('Thành công', `Đã tạo dịch vụ "${newService?.name || payload.name}"`)
    
    // Refresh list from BE
    handleSearch()
  } catch (error: any) {
    toast.error('Lỗi', serviceStore.error || 'Lỗi khi tạo dịch vụ')
  } finally {
    isSubmitting.value = false
  }
}

async function handleToggle(id: number) {
  const item = services.value.find(i => i.id === id)
  if (item) {
    try {
      await serviceStore.updateService(item.id, {
        code: item.code,
        name: item.name,
        category: item.category,
        unit: item.unit,
        description: item.description,
        isActive: !item.isActive
      })
      toast.success('Thành công', 'Cập nhật trạng thái dịch vụ')
    } catch {
      toast.error('Lỗi', 'Không thể cập nhật trạng thái')
    }
  }
}

function handleEdit(id: number) {
  const item = services.value.find(i => i.id === id)
  if (item) {
    selectedService.value = item
    showEditModal.value = true
  }
}

async function handleEditSubmit(id: number, payload: ReqServiceDTO) {
  if (!payload.name.trim()) {
    toast.error('Lỗi', 'Vui lòng nhập tên dịch vụ')
    return
  }

  isEditing.value = true
  try {
    await serviceStore.updateService(id, payload)
    toast.success('Thành công', 'Đã cập nhật dịch vụ')
    showEditModal.value = false
    // Tự động refresh table (vì có thể sort theo tên hoặc thay đổi nhóm)
    handleSearch()
  } catch (error: any) {
    toast.error('Lỗi', serviceStore.error || 'Lỗi khi cập nhật dịch vụ')
  } finally {
    isEditing.value = false
  }
}

async function handleView(id: number) {
  showDetailModal.value = true
  isFetchingDetail.value = true
  try {
    await serviceStore.fetchServiceById(id)
  } catch (err: any) {
    toast.error('Lỗi', serviceStore.error || 'Không thể lấy thông tin chi tiết')
    showDetailModal.value = false
  } finally {
    isFetchingDetail.value = false
  }
}
</script>