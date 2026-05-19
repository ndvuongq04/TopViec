<template>
  <div class="space-y-8">

    <!-- Header -->
    <div class="flex flex-wrap items-center justify-between gap-4">
      <div>
        <h2 class="text-2xl font-black text-slate-900 dark:text-slate-100 tracking-tight">Quản lý tài khoản Admin</h2>
        <p class="text-slate-500 text-sm mt-1">Quản lý phân quyền và giám sát hoạt động của các nhân sự vận hành hệ thống.</p>
      </div>
      <button
        v-if="can('admin.create')"
        class="bg-[#963131] hover:bg-[#963131]/90 text-white px-5 py-2.5 rounded-lg font-bold text-sm flex items-center gap-2 shadow-lg shadow-[#963131]/20 transition-all cursor-pointer"
        @click="showCreateModal = true"
      >
        <span class="material-symbols-outlined text-lg">person_add</span>
        Thêm Admin mới
      </button>
    </div>

    <!-- Table Card -->
    <div class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 overflow-hidden">

      <!-- Filters & Tabs -->
      <AdminListFilters
        :search="filters.search"
        @search="onSearch"
        v-model:activeTab="filters.adminRole"
        v-model:sort="filters.sort"
        :total-count="store.meta.totals"
      />

      <!-- Loading -->
      <div v-if="store.loading" class="flex items-center justify-center py-20">
        <div class="text-slate-400 text-sm flex items-center gap-2">
          <span class="material-symbols-outlined animate-spin">progress_activity</span>
          Đang tải dữ liệu...
        </div>
      </div>

      <!-- Error -->
      <div v-else-if="store.error" class="flex flex-col items-center justify-center py-20 text-red-500">
        <span class="material-symbols-outlined text-4xl mb-2">error</span>
        <p class="text-sm">{{ store.error }}</p>
        <button class="mt-4 text-sm text-[#963131] font-semibold hover:underline" @click="loadAdmins">
          Thử lại
        </button>
      </div>

      <!-- Table -->
      <AdminListTable
        v-else
        :admins="store.admins"
        :total="store.meta.totals"
        :current-page="currentPage"
        :page-size="pageSize"
        @view="onView"
        @edit="onEdit"
        @toggle-lock="onToggleLock"
        @delete="onDelete"
        @page-change="onPageChange"
      />
    </div>

    <!-- Create Modal -->
    <AdminCreateModal
      :visible="showCreateModal"
      :submitting="creating"
      @close="showCreateModal = false"
      @submit="onCreateAdmin"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAdminUserStore } from '@/stores/adminUser.store'
import { useToast } from '@/composables/useToast'
import { useConfirm } from '@/composables/useConfirm'
import { useAdminPermission } from '@/composables/useAdminPermission'
import AdminListFilters from '@/components/admin/admins/AdminListFilters.vue'
import AdminListTable from '@/components/admin/admins/AdminListTable.vue'
import AdminCreateModal from '@/components/admin/admins/AdminCreateModal.vue'
import type { ResAdminUser, ReqCreateAdmin } from '@/types/adminUser.types'

const router = useRouter()
const store = useAdminUserStore()
const toast = useToast()
const { confirm } = useConfirm()
const { can } = useAdminPermission()

const pageSize = ref(10)
const currentPage = ref(1)
const showCreateModal = ref(false)
const creating = ref(false)

const filters = ref({
  search: '',
  adminRole: '',
  sort: 'createdAt,desc',
})

function onSearch(val: string) {
  filters.value.search = val
  currentPage.value = 1
  loadAdmins()
}

watch(() => filters.value.adminRole, () => {
  currentPage.value = 1
  loadAdmins()
})

watch(() => filters.value.sort, () => {
  currentPage.value = 1
  loadAdmins()
})

onMounted(() => {
  loadAdmins()
})

async function loadAdmins() {
  await store.fetchAllAdmins({
    keyword: filters.value.search || undefined,
    adminRole: filters.value.adminRole || undefined,
    page: currentPage.value - 1, // Spring page is 0-indexed
    size: pageSize.value,
    sort: filters.value.sort || undefined,
  })
}

function onPageChange(page: number) {
  currentPage.value = page
  loadAdmins()
}

function onView(admin: ResAdminUser) {
  router.push({ name: 'admin-detail', params: { id: admin.adminUsersId } })
}

function onEdit(admin: ResAdminUser) {
  router.push({ name: 'admin-edit', params: { id: admin.adminUsersId } })
}

async function onToggleLock(admin: ResAdminUser) {
  const action = admin.isActive ? 'Khóa' : 'Mở khóa'
  const isConfirmed = await confirm({
    title: `Xác nhận ${action.toLowerCase()} tài khoản`,
    message: `Bạn có chắc muốn ${action.toLowerCase()} tài khoản "${admin.fullName}" không?`,
    confirmText: action,
    confirmColor: admin.isActive ? 'red' : 'primary',
    icon: admin.isActive ? 'block' : 'lock_open'
  })
  if (!isConfirmed) return

  try {
    await store.toggleActive(admin.adminUsersId)
    toast.success('Thành công!', `Đã ${action.toLowerCase()} tài khoản "${admin.fullName}"`)
  } catch {
    toast.error('Lỗi!', store.error || `Không thể ${action.toLowerCase()} tài khoản`)
  }
}

async function onDelete(admin: ResAdminUser) {
  const isConfirmed = await confirm({
    title: 'Xác nhận xóa tài khoản',
    message: `Bạn có chắc chắn muốn xóa tài khoản "${admin.fullName}"? Thao tác này sẽ xóa vĩnh viễn dữ liệu và không thể hoàn tác.`,
    confirmText: 'Xác nhận xóa',
    confirmColor: 'red',
    icon: 'warning'
  })
  if (!isConfirmed) return

  try {
    await store.deleteAdmin(admin.adminUsersId)
    toast.success('Thành công!', `Đã xóa tài khoản "${admin.fullName}"`)
  } catch {
    toast.error('Lỗi!', store.error || 'Không thể xóa tài khoản')
  }
}

async function onCreateAdmin(payload: ReqCreateAdmin) {
  creating.value = true
  try {
    await store.createAdmin(payload)
    showCreateModal.value = false
    loadAdmins()
    toast.success('Thành công!', 'Đã tạo tài khoản admin mới')
  } catch {
    toast.error('Lỗi!', store.error || 'Không thể tạo tài khoản admin')
  } finally {
    creating.value = false
  }
}
</script>