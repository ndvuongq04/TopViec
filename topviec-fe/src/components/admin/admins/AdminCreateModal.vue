<template>
  <GlobalModal
    :visible="visible"
    title="Thêm quản trị viên mới"
    subtitle="Vui lòng điền thông tin và cấp quyền cho tài khoản."
    icon="person_add"
    confirm-text="Xác nhận tạo"
    loading-text="Đang tạo..."
    :loading="submitting"
    form-id="create-admin-form"
    variant="danger"
    @close="$emit('close')"
  >
    <form class="space-y-8" @submit.prevent="handleSubmit" id="create-admin-form">
      <!-- Account Info Section -->
      <div class="space-y-5">
        <h4 class="text-sm font-bold text-slate-900 dark:text-slate-100 flex items-center gap-2">
          <span class="material-symbols-outlined text-[18px] text-slate-400">badge</span>
          Thông tin tài khoản
        </h4>
        
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-5">
          <!-- Full Name -->
          <div class="col-span-1 sm:col-span-2">
            <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5" for="admin-name">
              Họ và tên <span class="text-red-500">*</span>
            </label>
            <input
              id="admin-name"
              v-model="form.fullName"
              class="w-full px-4 py-3 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-sm focus:ring-4 focus:ring-[#963131]/10 focus:border-[#963131] transition-all outline-none shadow-sm"
              placeholder="VD: Nguyễn Văn A"
              type="text"
              required
            />
          </div>

          <!-- Email -->
          <div>
            <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5" for="admin-email">
              Email đăng nhập <span class="text-red-500">*</span>
            </label>
            <input
              id="admin-email"
              v-model="form.email"
              class="w-full px-4 py-3 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-sm focus:ring-4 focus:ring-[#963131]/10 focus:border-[#963131] transition-all outline-none shadow-sm"
              placeholder="admin@topviec.vn"
              type="email"
              required
            />
          </div>

          <!-- Password -->
          <div>
            <label class="block text-sm font-semibold text-slate-700 dark:text-slate-300 mb-1.5" for="admin-password">
              Mật khẩu <span class="text-red-500">*</span>
            </label>
            <div class="relative">
              <input
                id="admin-password"
                v-model="form.password"
                class="w-full pl-4 pr-12 py-3 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-sm focus:ring-4 focus:ring-[#963131]/10 focus:border-[#963131] transition-all outline-none shadow-sm"
                placeholder="••••••••"
                :type="showPassword ? 'text' : 'password'"
                required
                minlength="6"
              />
              <button
                class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 p-1.5 rounded-lg transition-colors"
                type="button"
                tabindex="-1"
                @click="showPassword = !showPassword"
              >
                <span class="material-symbols-outlined text-[20px]">
                  {{ showPassword ? 'visibility_off' : 'visibility' }}
                </span>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Role Section -->
      <div class="space-y-4">
        <h4 class="text-sm font-bold text-slate-900 dark:text-slate-100 flex items-center gap-2">
          <span class="material-symbols-outlined text-[18px] text-slate-400">shield_person</span>
          Phân quyền truy cập <span class="text-red-500">*</span>
        </h4>
        
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
          <label
            v-for="role in roles"
            :key="role.value"
            class="relative flex items-start gap-4 p-4 rounded-xl border-2 cursor-pointer transition-all duration-200"
            :class="form.adminRole === role.value 
              ? 'border-[#963131] bg-[#963131]/5 shadow-sm shadow-[#963131]/10' 
              : 'border-slate-100 dark:border-slate-800 bg-white dark:bg-slate-900 hover:border-slate-300 dark:hover:border-slate-700'"
          >
            <div class="pt-0.5">
              <div 
                class="flex items-center justify-center size-5 rounded-full border-2 transition-colors"
                :class="form.adminRole === role.value ? 'border-[#963131]' : 'border-slate-300'"
              >
                <div 
                  class="size-2.5 rounded-full bg-[#963131] transition-transform"
                  :class="form.adminRole === role.value ? 'scale-100' : 'scale-0'"
                ></div>
              </div>
            </div>
            <input
              v-model="form.adminRole"
              class="sr-only"
              name="role"
              type="radio"
              :value="role.value"
            />
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-1">
                <span class="material-symbols-outlined text-[18px]" :class="form.adminRole === role.value ? 'text-[#963131]' : 'text-slate-400'">
                  {{ role.icon }}
                </span>
                <p class="text-sm font-bold" :class="form.adminRole === role.value ? 'text-[#963131]' : 'text-slate-900 dark:text-slate-100'">
                  {{ role.label }}
                </p>
              </div>
              <p class="text-xs text-slate-500 leading-snug">{{ role.desc }}</p>
            </div>
          </label>
        </div>
      </div>
    </form>
  </GlobalModal>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import GlobalModal from '@/components/ui/GlobalModal.vue'
import type { AdminRole, ReqCreateAdmin } from '@/types/adminUser.types'

const props = defineProps<{
  visible: boolean
  submitting?: boolean
}>()

const emit = defineEmits<{
  close: []
  submit: [payload: ReqCreateAdmin]
}>()

const showPassword = ref(false)

const roles = [
  { value: 'super_admin' as AdminRole, label: 'Super Admin', desc: 'Toàn quyền kiểm soát và cấu hình hệ thống TopViec.', icon: 'stars' },
  { value: 'content_moderator' as AdminRole, label: 'Content Moderator', desc: 'Quản lý, kiểm duyệt tin tuyển dụng và hồ sơ.', icon: 'article' },
  { value: 'finance_admin' as AdminRole, label: 'Finance Admin', desc: 'Quản lý thanh toán, hóa đơn và gói dịch vụ.', icon: 'payments' },
  { value: 'support_admin' as AdminRole, label: 'Support Admin', desc: 'Hỗ trợ giải đáp thắc mắc của ứng viên/NTD.', icon: 'support_agent' },
]

const form = reactive<ReqCreateAdmin>({
  email: '',
  password: '',
  adminRole: 'super_admin',
  fullName: '',
})

function resetForm() {
  form.email = ''
  form.password = ''
  form.adminRole = 'super_admin'
  form.fullName = ''
  showPassword.value = false
}

watch(() => props.visible, (val) => {
  if (!val) {
    // Delay slightly to wait for closing animation (if any in the future)
    setTimeout(resetForm, 200)
  } else {
    resetForm()
  }
})

function handleSubmit() {
  if (!form.email || !form.password || !form.fullName) return
  emit('submit', { ...form })
}
</script>
