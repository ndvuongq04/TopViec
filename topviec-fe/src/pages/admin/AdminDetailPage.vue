<template>
  <div>
    <!-- Header Section -->
    <div class="flex flex-wrap items-center justify-between gap-4 mb-8">
      <div class="flex items-center gap-4">
        <router-link
          :to="{ name: 'admin-manage-admins' }"
          class="text-slate-400 hover:text-primary transition-colors"
        >
          <span class="material-symbols-outlined">arrow_back</span>
        </router-link>
        <div>
          <h2 class="text-2xl font-bold tracking-tight">Chi tiết tài khoản Admin</h2>
          <p v-if="admin" class="text-sm text-slate-500">
            ID: AD-{{ admin.adminUsersId }} • Cập nhật lần cuối: {{ lastUpdated }}
          </p>
        </div>
      </div>
      <div v-if="admin" class="flex items-center gap-3">
        <router-link
          :to="{ name: 'admin-edit', params: { id: admin.adminUsersId } }"
          class="flex items-center gap-2 px-4 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg text-sm font-semibold transition-all"
        >
          <span class="material-symbols-outlined text-[18px]">edit</span>
          Chỉnh sửa
        </router-link>
        <button
          class="flex items-center gap-2 px-4 py-2 bg-primary text-white hover:bg-primary/90 rounded-lg text-sm font-semibold transition-all shadow-sm"
          @click="handleToggleActive"
        >
          <span class="material-symbols-outlined text-[18px]">
            {{ admin.isActive ? 'block' : 'lock_open' }}
          </span>
          {{ admin.isActive ? 'Khóa tài khoản' : 'Mở khóa' }}
        </button>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="store.loading" class="flex items-center justify-center py-32">
      <div class="text-slate-400 text-sm flex items-center gap-2">
        <span class="material-symbols-outlined animate-spin">progress_activity</span>
        Đang tải dữ liệu...
      </div>
    </div>

    <!-- Error -->
    <div v-else-if="store.error" class="flex flex-col items-center justify-center py-32 text-red-500">
      <span class="material-symbols-outlined text-4xl mb-2">error</span>
      <p class="text-sm">{{ store.error }}</p>
    </div>

    <!-- Content -->
    <div v-else-if="admin" class="mx-auto space-y-6">
      <!-- Top Grid: Info & Security -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <AdminDetailInfo :admin="admin" />
        <AdminDetailSecurity />
      </div>

      <!-- System Log -->
      <AdminDetailLog />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/vi'
import { useAdminUserStore } from '@/stores/adminUser.store'
import { useToast } from '@/composables/useToast'
import { useConfirm } from '@/composables/useConfirm'
import AdminDetailInfo from '@/components/admin/admins/AdminDetailInfo.vue'
import AdminDetailSecurity from '@/components/admin/admins/AdminDetailSecurity.vue'
import AdminDetailLog from '@/components/admin/admins/AdminDetailLog.vue'

dayjs.extend(relativeTime)
dayjs.locale('vi')

const route = useRoute()
const store = useAdminUserStore()
const toast = useToast()
const { confirm } = useConfirm()

const admin = computed(() => store.selectedAdmin)

const lastUpdated = computed(() => {
  if (!admin.value?.updatedAt) return '—'
  return dayjs(admin.value.updatedAt).fromNow()
})

onMounted(() => {
  const id = Number(route.params.id)
  if (id) store.fetchAdminById(id)
})

async function handleToggleActive() {
  if (!admin.value) return
  const action = admin.value.isActive ? 'Khóa' : 'Mở khóa'
  const isConfirmed = await confirm({
    title: `Xác nhận ${action.toLowerCase()} tài khoản`,
    message: `Bạn có chắc muốn ${action.toLowerCase()} tài khoản "${admin.value.fullName}" không?`,
    confirmText: action,
    confirmColor: admin.value.isActive ? 'red' : 'primary',
    icon: admin.value.isActive ? 'block' : 'lock_open'
  })
  if (!isConfirmed) return

  try {
    await store.toggleActive(admin.value.adminUsersId)
    toast.success('Thành công!', `Đã ${action.toLowerCase()} tài khoản "${admin.value.fullName}"`)
  } catch {
    toast.error('Lỗi!', store.error || `Không thể ${action.toLowerCase()} tài khoản`)
  }
}
</script>
