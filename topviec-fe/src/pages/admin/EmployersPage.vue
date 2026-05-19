<template>
  <div class="space-y-6">

    <!-- Page Header -->
    <div class="flex justify-between items-end">
      <div>
        <h2 class="text-3xl font-black text-slate-900 dark:text-slate-100 tracking-tight">Quản lý Nhà Tuyển Dụng</h2>
        <p class="text-slate-500 text-sm mt-1">Xem và quản lý danh sách các công ty trên nền tảng</p>
      </div>
      <button
        v-if="can('company.create')"
        class="bg-[#963131] hover:bg-[#963131]/90 text-white px-5 py-2.5 rounded-lg flex items-center gap-2 font-semibold text-sm transition-all shadow-sm cursor-pointer"
        style="box-shadow: 0 4px 14px rgba(150,49,49,0.25)"
        @click="onAddNew"
      >
        <span class="material-symbols-outlined text-lg">add</span>
        Thêm NTD mới
      </button>
    </div>

    <!-- Tabs + Search + Filters -->
    <CompanyStatusTabs
      v-model:active-tab="filters.statusTab"
      v-model:search="filters.search"
      v-model:verification-filter="filters.verificationStatus"
      :companies="store.companies"
    />

    <!-- Table -->
    <CompanyTable
      :companies="store.companies"
      :total="store.meta.totals"
      :current-page="currentPage"
      :page-size="pageSize"
      @view="onView"
      @toggle-lock="onToggleLock"
      @notify="onNotify"
      @delete="onDelete"
      @page-change="onPageChange"
    />

    <!-- Modals -->
    <EmployerCreateModal
      :visible="showCreateModal"
      :loading="store.loading"
      @close="showCreateModal = false"
      @submit="handleCreate"
    />

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAdminPermission } from '@/composables/useAdminPermission'
import CompanyStatusTabs from '@/components/admin/employers/CompanyStatusTabs.vue'
import CompanyTable from '@/components/admin/employers/CompanyTable.vue'
import EmployerCreateModal from '@/components/admin/employers/EmployerCreateModal.vue'
import { useAdminCompanyStore } from '@/stores/adminCompany.store'
import { useToast } from '@/composables/useToast'
import type { ResCompanyDTO } from '@/types/company.types'
import type { ReqRegisterEmployerDTO } from '@/types/auth.types'

const router = useRouter()
const store = useAdminCompanyStore()
const toast = useToast()
const { can } = useAdminPermission()

// ─── State ───────────────────────────────────────────────────────────────────────
const currentPage     = ref(1)
const pageSize        = ref(10)
const showCreateModal = ref(false)

const filters = reactive({
  statusTab:          '',   // CompanyStatus value or '' for all
  search:             '',
  verificationStatus: '',   // VerificationStatus value or '' for all
})

// ─── Fetch Data ──────────────────────────────────────────────────────────────────
async function fetchData() {
  await store.fetchAllCompanies({
    page: currentPage.value - 1, // API backend dùng 0-index (trang đầu tiên là 0)
    size: pageSize.value,
    status: filters.statusTab || undefined,
    verificationStatus: filters.verificationStatus || undefined,
    keyword: filters.search || undefined
  })
  
  if (store.error) {
    toast.error('Lỗi tải dữ liệu', store.error)
  }
}

watch(filters, () => {
  currentPage.value = 1
  fetchData()
}, { deep: true })

onMounted(() => {
  fetchData()
})

// ─── Event handlers ──────────────────────────────────────────────────────────────
function onAddNew() {
  showCreateModal.value = true
}

async function handleCreate(payload: ReqRegisterEmployerDTO) {
  try {
    await store.createCompany(payload)
    toast.success('Thành công', `Đã thêm nhà tuyển dụng`)
    showCreateModal.value = false
    fetchData()
  } catch (err: any) {
    toast.error('Lỗi', store.error || 'Không thể tạo nhà tuyển dụng mới.')
  }
}

function onView(company: ResCompanyDTO) {
  router.push({ name: 'admin-employer-detail', params: { id: company.id } })
}

async function onToggleLock(company: ResCompanyDTO) {
  try {
    if (company.status === 'suspended') {
      await store.unsuspendCompany(company.id)
      toast.success('Thành công', `Đã mở khóa công ty ${company.name}`)
    } else {
      await store.suspendCompany(company.id, 'Admin khóa tài khoản')
      toast.success('Thành công', `Đã khóa công ty ${company.name}`)
    }
  } catch (err: any) {
    toast.error('Thất bại', store.error || 'Có lỗi xảy ra khi cập nhật trạng thái.')
  }
}

function onNotify(company: ResCompanyDTO) {
  toast.info('Tính năng', `Tính năng gửi cảnh báo cho ${company.name} đang được phát triển.`)
}

async function onDelete(company: ResCompanyDTO) {
  if (!confirm(`Bạn có chắc chắn muốn xóa công ty ${company.name}?`)) return
  try {
    await store.deleteCompany(company.id)
    toast.success('Thành công', `Đã xóa công ty ${company.name}`)
    fetchData()
  } catch (err: any) {
    toast.error('Thất bại', store.error || 'Không thể xóa công ty này.')
  }
}

function onPageChange(page: number) {
  currentPage.value = page
  fetchData()
}
</script>
