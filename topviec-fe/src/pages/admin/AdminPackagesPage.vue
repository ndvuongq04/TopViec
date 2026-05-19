<template>
  <div class="space-y-8">

    <!-- Header -->
    <div class="flex flex-wrap items-center justify-between gap-4">
      <div>
        <h2 class="text-3xl font-black text-slate-900 dark:text-slate-100 tracking-tight">Gói dịch vụ</h2>
        <p class="text-slate-500 text-sm mt-1">Quản lý các gói subscription và cấu hình đặc quyền cho từng cấp độ người dùng</p>
      </div>
      <button
        v-if="can('package.create')"
        class="bg-[#963131] hover:bg-[#963131]/90 text-white px-5 py-2.5 rounded-lg font-bold text-sm flex items-center gap-2 shadow-lg shadow-[#963131]/20 transition-all cursor-pointer"
        @click="showCreateModal = true"
      >
        <span class="material-symbols-outlined text-lg">add</span>
        Thêm gói dịch vụ
      </button>
    </div>

    <!-- KPI Cards -->
    <PackageKpiCards v-if="can('package.detail')" />

    <!-- Filter -->
    <PackageFilters :packages="store.servicePackages" @filter="onFilter" />

    <!-- Table Card -->
    <div class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 overflow-hidden">

      <!-- Loading -->
      <div v-if="store.loading" class="flex items-center justify-center py-20">
        <div class="text-slate-400 text-sm flex items-center gap-2">
          <span class="material-symbols-outlined animate-spin">progress_activity</span>
          Đang tải dữ liệu...
        </div>
      </div>

      <!-- Error -->
      <div v-else-if="fetchError" class="flex flex-col items-center justify-center py-20 text-red-500 gap-2">
        <span class="material-symbols-outlined text-4xl">error</span>
        <p class="text-sm font-semibold">{{ fetchError }}</p>
        <button
          class="mt-2 text-xs text-slate-500 hover:text-slate-700 underline cursor-pointer"
          @click="loadPackages"
        >
          Thử lại
        </button>
      </div>

      <!-- Table -->
      <PackageTable v-else :packages="filtered" :toggling-id="togglingId" @edit="onEdit" @toggle="onToggle" />

    </div>

    <!-- Create Modal -->
    <ServicePackageCreateModal
      :visible="showCreateModal"
      :submitting="creating"
      :services="services"
      @close="showCreateModal = false"
      @submit="onCreateSubmit"
    />

    <!-- Edit Modal -->
    <ServicePackageEditModal
      :visible="showEditModal"
      :submitting="editing"
      :package="selectedPackage"
      :services="services"
      @close="showEditModal = false"
      @submit="onEditSubmit"
    />

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAdminPermission } from '@/composables/useAdminPermission'
import PackageFilters from '@/components/admin/packages/PackageFilters.vue'
import PackageTable from '@/components/admin/packages/PackageTable.vue'
import PackageKpiCards from '@/components/admin/packages/PackageKpiCards.vue'
import ServicePackageCreateModal from '@/components/admin/packages/ServicePackageCreateModal.vue'
import ServicePackageEditModal from '@/components/admin/packages/ServicePackageEditModal.vue'
import { useServicePackageStore } from '@/stores/servicePackage.store'
import { useServiceCatalogStore } from '@/stores/serviceCatalog.store'
import { useToast } from '@/composables/useToast'
import { serviceCatalogService } from '@/services/serviceCatalog.service'
import type { ReqServicePackageDTO, ResServicePackageDTO } from '@/types/servicePackage.types'
import type { ResServiceDTO } from '@/types/serviceCatalog.types'

const store = useServicePackageStore()
const catalogStore = useServiceCatalogStore()
const toast = useToast()
const { can } = useAdminPermission()

const showCreateModal = ref(false)
const creating        = ref(false)
const showEditModal   = ref(false)
const editing         = ref(false)
const selectedPackage = ref<ResServicePackageDTO | null>(null)
const filterState     = ref({ search: '', status: '' })
const fetchError      = ref<string | null>(null)
const togglingId      = ref<number | null>(null)
const services        = ref<ResServiceDTO[]>([])

const filtered = computed(() => {
  const { search, status } = filterState.value
  return store.servicePackages.filter(p => {
    const matchSearch = !search
      || p.name.toLowerCase().includes(search.toLowerCase())
      || p.code.toLowerCase().includes(search.toLowerCase())
    const matchStatus = !status
      || (status === 'active' ? p.isActive : !p.isActive)
    return matchSearch && matchStatus
  })
})

async function loadPackages() {
  fetchError.value = null
  await store.fetchServicePackages({ size: 100, sort: 'sortOrder,asc' })
  if (store.error) fetchError.value = store.error
}

async function loadServices() {
  try {
    const res = await serviceCatalogService.getAllServices({ size: 200 })
    services.value = res.result ?? []
  } catch {
    // services list is non-critical, fail silently
  }
}

onMounted(() => {
  loadPackages()
  loadServices()
  catalogStore.fetchStatistics()
})

const onFilter = (f: typeof filterState.value) => { filterState.value = f }

function onEdit(pkg: ResServicePackageDTO) {
  selectedPackage.value = pkg
  showEditModal.value   = true
}

async function onCreateSubmit(payload: ReqServicePackageDTO) {
  creating.value = true
  try {
    await store.createServicePackage(payload)
    showCreateModal.value = false
    toast.success('Thành công!', `Đã tạo gói dịch vụ "${payload.name}"`)
  } catch {
    toast.error('Lỗi!', store.error || 'Không thể tạo gói dịch vụ')
  } finally {
    creating.value = false
  }
}

async function onToggle(pkg: ResServicePackageDTO) {
  togglingId.value = pkg.id
  try {
    await store.updateServicePackage(pkg.id, {
      name:         pkg.name,
      code:         pkg.code,
      billingCycle: pkg.billingCycle,
      price:        pkg.price,
      details:      (pkg.details ?? []).map(d => ({ serviceId: d.serviceId, quantity: d.quantity })),
      description:  pkg.description,
      isActive:     !pkg.isActive,
      sortOrder:    pkg.sortOrder,
    })
    toast.success('Thành công!', `Đã ${!pkg.isActive ? 'bật' : 'tắt'} gói "${pkg.name}"`)
  } catch {
    toast.error('Lỗi!', store.error || 'Không thể cập nhật trạng thái')
  } finally {
    togglingId.value = null
  }
}

async function onEditSubmit(id: number, payload: ReqServicePackageDTO) {
  editing.value = true
  try {
    await store.updateServicePackage(id, payload)
    showEditModal.value = false
    toast.success('Thành công!', `Đã cập nhật gói dịch vụ "${payload.name}"`)
  } catch {
    toast.error('Lỗi!', store.error || 'Không thể cập nhật gói dịch vụ')
  } finally {
    editing.value = false
  }
}
</script>
