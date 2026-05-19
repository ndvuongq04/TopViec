<template>
  <main class="flex flex-col flex-1 gap-6 min-w-0 max-w-[1440px] mx-auto w-full px-4 md:px-10 py-6">

    <!-- Header -->
    <div class="pt-4 mt-2">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4">
        <div>
          <h2 class="text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight">Đổi mật khẩu</h2>
          <p class="text-slate-500 dark:text-gray-400 text-base mt-1">Cập nhật mật khẩu để bảo vệ tài khoản của bạn.</p>
        </div>
      </div>
    </div>

    <!-- Form card -->
    <div class="max-w-lg w-full mx-auto">
      <div class="bg-white dark:bg-surface-dark rounded-2xl border border-slate-200 dark:border-slate-800 p-6 md:p-8 shadow-sm">

        <!-- Icon + title -->
        <div class="flex items-center gap-3 mb-6">
          <div class="w-11 h-11 rounded-xl bg-primary/10 flex items-center justify-center shrink-0">
            <span class="material-symbols-outlined text-primary text-xl">lock_reset</span>
          </div>
          <div>
            <p class="font-bold text-text-main dark:text-white text-base">Thay đổi mật khẩu</p>
            <p class="text-xs text-slate-500">Mật khẩu mới phải có ít nhất 8 ký tự</p>
          </div>
        </div>

        <form class="space-y-5" @submit.prevent="handleSubmit">

          <!-- Current password -->
          <div>
            <label class="block text-sm font-semibold text-text-main dark:text-white mb-1.5">
              Mật khẩu hiện tại <span class="text-red-500">*</span>
            </label>
            <div class="relative">
              <input
                v-model="form.currentPassword"
                :type="show.current ? 'text' : 'password'"
                placeholder="Nhập mật khẩu hiện tại"
                class="w-full px-4 py-2.5 pr-11 rounded-xl border text-sm transition-colors focus:outline-none"
                :class="errors.currentPassword
                  ? 'border-red-400 bg-red-50 dark:bg-red-900/10 focus:border-red-400'
                  : 'border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 focus:border-primary'"
                @blur="validateCurrentPassword"
                @input="errors.currentPassword = ''"
              />
              <button
                type="button"
                class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 cursor-pointer"
                @click="show.current = !show.current"
              >
                <span class="material-symbols-outlined text-[20px]">{{ show.current ? 'visibility_off' : 'visibility' }}</span>
              </button>
            </div>
            <p v-if="errors.currentPassword" class="mt-1.5 text-xs text-red-500 flex items-center gap-1">
              <span class="material-symbols-outlined text-[14px]">error</span>
              {{ errors.currentPassword }}
            </p>
          </div>

          <!-- New password -->
          <div>
            <label class="block text-sm font-semibold text-text-main dark:text-white mb-1.5">
              Mật khẩu mới <span class="text-red-500">*</span>
            </label>
            <div class="relative">
              <input
                v-model="form.newPassword"
                :type="show.new ? 'text' : 'password'"
                placeholder="Tối thiểu 8 ký tự"
                class="w-full px-4 py-2.5 pr-11 rounded-xl border text-sm transition-colors focus:outline-none"
                :class="errors.newPassword
                  ? 'border-red-400 bg-red-50 dark:bg-red-900/10 focus:border-red-400'
                  : 'border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 focus:border-primary'"
                @blur="validateNewPassword"
                @input="errors.newPassword = ''; if (form.confirmPassword) validateConfirmPassword()"
              />
              <button
                type="button"
                class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 cursor-pointer"
                @click="show.new = !show.new"
              >
                <span class="material-symbols-outlined text-[20px]">{{ show.new ? 'visibility_off' : 'visibility' }}</span>
              </button>
            </div>
            <p v-if="errors.newPassword" class="mt-1.5 text-xs text-red-500 flex items-center gap-1">
              <span class="material-symbols-outlined text-[14px]">error</span>
              {{ errors.newPassword }}
            </p>
            <!-- Strength indicator -->
            <div v-if="form.newPassword" class="mt-2 flex gap-1">
              <div
                v-for="i in 4"
                :key="i"
                class="h-1 flex-1 rounded-full transition-colors"
                :class="i <= passwordStrength.score ? passwordStrength.color : 'bg-slate-200 dark:bg-slate-700'"
              />
            </div>
            <p v-if="form.newPassword" class="mt-1 text-xs" :class="passwordStrength.textColor">
              {{ passwordStrength.label }}
            </p>
          </div>

          <!-- Confirm password -->
          <div>
            <label class="block text-sm font-semibold text-text-main dark:text-white mb-1.5">
              Xác nhận mật khẩu mới <span class="text-red-500">*</span>
            </label>
            <div class="relative">
              <input
                v-model="form.confirmPassword"
                :type="show.confirm ? 'text' : 'password'"
                placeholder="Nhập lại mật khẩu mới"
                class="w-full px-4 py-2.5 pr-11 rounded-xl border text-sm transition-colors focus:outline-none"
                :class="errors.confirmPassword
                  ? 'border-red-400 bg-red-50 dark:bg-red-900/10 focus:border-red-400'
                  : form.confirmPassword && !errors.confirmPassword
                    ? 'border-green-400 bg-green-50 dark:bg-green-900/10 focus:border-green-400'
                    : 'border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 focus:border-primary'"
                @blur="validateConfirmPassword"
                @input="errors.confirmPassword = ''"
              />
              <button
                type="button"
                class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 cursor-pointer"
                @click="show.confirm = !show.confirm"
              >
                <span class="material-symbols-outlined text-[20px]">{{ show.confirm ? 'visibility_off' : 'visibility' }}</span>
              </button>
            </div>
            <p v-if="errors.confirmPassword" class="mt-1.5 text-xs text-red-500 flex items-center gap-1">
              <span class="material-symbols-outlined text-[14px]">error</span>
              {{ errors.confirmPassword }}
            </p>
            <p v-else-if="form.confirmPassword" class="mt-1.5 text-xs text-green-600 flex items-center gap-1">
              <span class="material-symbols-outlined text-[14px]">check_circle</span>
              Mật khẩu khớp
            </p>
          </div>

          <!-- Submit -->
          <div class="pt-2">
            <button
              type="submit"
              :disabled="loading"
              class="w-full flex items-center justify-center gap-2 py-3 px-6 rounded-xl bg-primary hover:bg-primary-hover text-white font-bold text-sm shadow-lg shadow-blue-500/30 transition-colors disabled:opacity-60 disabled:cursor-not-allowed cursor-pointer"
            >
              <span v-if="loading" class="material-symbols-outlined animate-spin text-[18px]">progress_activity</span>
              <span v-else class="material-symbols-outlined text-[18px]">lock_reset</span>
              {{ loading ? 'Đang xử lý...' : 'Đổi mật khẩu' }}
            </button>
          </div>

        </form>
      </div>
    </div>

  </main>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import authService from '@/services/auth.service'
import { useAuthStore } from '@/stores/auth.store'
import { useToast } from '@/composables/useToast'

const router = useRouter()
const authStore = useAuthStore()
const toast = useToast()

const loading = ref(false)
const form = reactive({ currentPassword: '', newPassword: '', confirmPassword: '' })
const errors = reactive({ currentPassword: '', newPassword: '', confirmPassword: '' })
const show = reactive({ current: false, new: false, confirm: false })

// --- Validation ---
function validateCurrentPassword() {
  errors.currentPassword = form.currentPassword.trim() ? '' : 'Mật khẩu hiện tại không được để trống'
}

function validateNewPassword() {
  if (!form.newPassword) {
    errors.newPassword = 'Mật khẩu mới không được để trống'
  } else if (form.newPassword.length < 8) {
    errors.newPassword = 'Mật khẩu mới phải có ít nhất 8 ký tự'
  } else if (form.newPassword === form.currentPassword) {
    errors.newPassword = 'Mật khẩu mới phải khác mật khẩu hiện tại'
  } else {
    errors.newPassword = ''
  }
}

function validateConfirmPassword() {
  if (!form.confirmPassword) {
    errors.confirmPassword = 'Vui lòng xác nhận mật khẩu mới'
  } else if (form.confirmPassword !== form.newPassword) {
    errors.confirmPassword = 'Mật khẩu xác nhận không khớp'
  } else {
    errors.confirmPassword = ''
  }
}

function validateAll(): boolean {
  validateCurrentPassword()
  validateNewPassword()
  validateConfirmPassword()
  return !errors.currentPassword && !errors.newPassword && !errors.confirmPassword
}

// --- Password strength ---
const passwordStrength = computed(() => {
  const p = form.newPassword
  let score = 0
  if (p.length >= 8) score++
  if (/[A-Z]/.test(p)) score++
  if (/[0-9]/.test(p)) score++
  if (/[^A-Za-z0-9]/.test(p)) score++

  const levels = [
    { label: 'Rất yếu',    color: 'bg-red-500',    textColor: 'text-red-500' },
    { label: 'Yếu',        color: 'bg-orange-400',  textColor: 'text-orange-500' },
    { label: 'Trung bình', color: 'bg-yellow-400',  textColor: 'text-yellow-600' },
    { label: 'Mạnh',       color: 'bg-green-500',   textColor: 'text-green-600' },
  ]
  return { score, ...(levels[score - 1] ?? levels[0]) }
})

// --- Submit ---
async function handleSubmit() {
  if (!validateAll()) return

  loading.value = true
  try {
    await authService.changePassword({
      currentPassword: form.currentPassword,
      newPassword: form.newPassword,
      confirmPassword: form.confirmPassword,
    })
    toast.success('Đổi mật khẩu thành công!', 'Bạn sẽ được đăng xuất ngay bây giờ.')
    await authStore.logout()
    await router.push({ name: 'login' })
  } catch (err: any) {
    const msg: string = err?.response?.data?.message ?? 'Đổi mật khẩu thất bại. Vui lòng thử lại.'
    toast.error('Thất bại', msg)
  } finally {
    loading.value = false
  }
}
</script>
