<template>
  <div>
    <!-- Breadcrumbs -->
    <nav class="flex text-sm text-slate-500 mb-4 items-center gap-2">
      <router-link :to="{ name: 'admin-manage-admins' }" class="hover:text-primary">Quản lý Admin</router-link>
      <span class="material-symbols-outlined text-xs">chevron_right</span>
      <span class="text-slate-900 dark:text-slate-100 font-medium">Chỉnh sửa tài khoản</span>
    </nav>

    <!-- Header -->
    <div class="flex items-center justify-between mb-8">
      <h2 class="text-3xl font-black tracking-tight">Chỉnh sửa tài khoản Admin</h2>
      <div class="flex gap-3">
        <router-link
          :to="{ name: 'admin-detail', params: { id: route.params.id } }"
          class="px-6 py-2 border border-slate-300 rounded-lg text-sm font-semibold hover:bg-slate-50 transition-colors"
        >
          Hủy
        </router-link>
        <button
          class="px-6 py-2 bg-primary text-white rounded-lg text-sm font-semibold hover:bg-primary/90 transition-shadow shadow-md disabled:opacity-50"
          :disabled="saving"
          @click="handleSave"
        >
          {{ saving ? 'Đang lưu...' : 'Lưu thay đổi' }}
        </button>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="store.loading && !admin" class="flex items-center justify-center py-32">
      <div class="text-slate-400 text-sm flex items-center gap-2">
        <span class="material-symbols-outlined animate-spin">progress_activity</span>
        Đang tải dữ liệu...
      </div>
    </div>

    <!-- Error -->
    <div v-else-if="store.error && !admin" class="flex flex-col items-center justify-center py-32 text-red-500">
      <span class="material-symbols-outlined text-4xl mb-2">error</span>
      <p class="text-sm">{{ store.error }}</p>
    </div>

    <!-- Form Sections -->
    <div v-else-if="admin" class="grid gap-6">
      <!-- Section 1: General Info -->
      <AdminEditForm
        v-model:fullName="form.fullName"
        v-model:department="form.department"
        :email="admin.email"
      />

      <!-- Section 2: Role -->
      <AdminEditRole v-model="form.adminRole" />

      <!-- Section 3: Security -->
      <AdminEditSecurity />

      <!-- Mobile Save Button -->
      <div class="flex flex-col gap-3 md:hidden">
        <button
          class="w-full py-3 bg-primary text-white rounded-lg font-bold shadow-lg disabled:opacity-50"
          :disabled="saving"
          @click="handleSave"
        >
          {{ saving ? 'Đang lưu...' : 'Lưu thay đổi' }}
        </button>
        <router-link
          :to="{ name: 'admin-detail', params: { id: route.params.id } }"
          class="w-full py-3 border border-slate-300 rounded-lg font-bold text-center"
        >
          Hủy
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAdminUserStore } from '@/stores/adminUser.store'
import AdminEditForm from '@/components/admin/admins/AdminEditForm.vue'
import AdminEditRole from '@/components/admin/admins/AdminEditRole.vue'
import AdminEditSecurity from '@/components/admin/admins/AdminEditSecurity.vue'
import { useToast } from '@/composables/useToast'
import type { AdminRole, ReqUpdateAdmin } from '@/types/adminUser.types'

const route = useRoute()
const router = useRouter()
const store = useAdminUserStore()
const toast = useToast()

const saving = ref(false)
const admin = computed(() => store.selectedAdmin)

const form = reactive<{
  fullName: string
  department: string
  adminRole: AdminRole
}>({
  fullName: '',
  department: '',
  adminRole: 'super_admin',
})

// Populate form when admin data loads
watch(admin, (val) => {
  if (val) {
    form.fullName = val.fullName
    form.department = val.department ?? ''
    form.adminRole = val.adminRole
  }
}, { immediate: true })

onMounted(() => {
  const id = Number(route.params.id)
  if (id) {
    // Only fetch if we don't already have this admin loaded
    if (!admin.value || admin.value.adminUsersId !== id) {
      store.fetchAdminById(id)
    }
  }
})

async function handleSave() {
  if (!admin.value) return

  saving.value = true
  const payload: ReqUpdateAdmin = {
    fullName: form.fullName,
    department: form.department || undefined,
    adminRole: form.adminRole,
  }

  try {
    await store.updateAdmin(admin.value.adminUsersId, payload)
    toast.success('Thành công!', 'Đã cập nhật thông tin tài khoản')
    router.push({ name: 'admin-detail', params: { id: admin.value.adminUsersId } })
  } catch {
    toast.error('Lỗi!', store.error || 'Không thể cập nhật thông tin')
  } finally {
    saving.value = false
  }
}
</script>
