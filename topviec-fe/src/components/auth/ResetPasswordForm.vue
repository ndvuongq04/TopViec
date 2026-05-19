<template>
  <div class="font-display bg-background-light min-h-screen flex items-center justify-center p-4 sm:p-6 lg:p-8 relative">

    <!-- Background blobs -->
    <div class="fixed top-0 right-0 -z-10 opacity-10 pointer-events-none">
      <div class="w-[500px] h-[500px] bg-primary rounded-full blur-[120px] -mr-48 -mt-48"></div>
    </div>
    <div class="fixed bottom-0 left-0 -z-10 opacity-10 pointer-events-none">
      <div class="w-[500px] h-[500px] bg-primary rounded-full blur-[120px] -ml-48 -mb-48"></div>
    </div>

    <div class="w-full max-w-md">
      <!-- Card -->
      <div class="bg-white rounded-xl shadow-xl border border-slate-200 overflow-hidden">

        <!-- Card Header -->
        <div class="pt-10 pb-6 px-8 text-center">
          <div class="inline-flex items-center justify-center w-16 h-16 bg-primary/10 rounded-full mb-6 text-primary">
            <span class="material-symbols-outlined text-4xl">lock_open</span>
          </div>
          <h2 class="text-2xl font-bold text-slate-900 mb-2">Đặt lại mật khẩu</h2>
          <p class="text-slate-500 text-sm leading-relaxed">
            Nhập mật khẩu mới cho tài khoản của bạn.
          </p>
        </div>

        <!-- Token invalid state -->
        <div v-if="tokenInvalid" class="px-8 pb-10 space-y-5">
          <div class="flex gap-4 p-4 bg-red-50 border border-red-200 rounded-lg">
            <div class="shrink-0 w-11 h-11 bg-red-500 rounded-full flex items-center justify-center text-white">
              <span class="material-symbols-outlined text-xl" style="font-variation-settings: 'FILL' 1">error</span>
            </div>
            <div>
              <p class="text-sm font-bold text-red-900 mb-1">Link không hợp lệ!</p>
              <p class="text-sm text-red-700 leading-relaxed">
                Link đặt lại mật khẩu đã hết hạn hoặc không hợp lệ.
                Vui lòng gửi lại yêu cầu.
              </p>
            </div>
          </div>
          <router-link
            to="/forgot-password"
            class="w-full bg-primary hover:bg-primary/90 text-white font-bold py-3.5 rounded-lg shadow-lg shadow-primary/20 transition-all flex items-center justify-center gap-2"
          >
            <span class="material-symbols-outlined text-lg">refresh</span>
            Gửi lại yêu cầu
          </router-link>
        </div>

        <!-- Success state -->
        <div v-else-if="resetSuccess" class="px-8 pb-10 space-y-5">
          <div class="flex gap-4 p-4 bg-blue-50 border border-blue-200 rounded-lg">
            <div class="shrink-0 w-11 h-11 bg-primary rounded-full flex items-center justify-center text-white">
              <span class="material-symbols-outlined text-xl" style="font-variation-settings: 'FILL' 1">check_circle</span>
            </div>
            <div>
              <p class="text-sm font-bold text-blue-900 mb-1">Đặt lại mật khẩu thành công!</p>
              <p class="text-sm text-blue-700 leading-relaxed">
                Mật khẩu của bạn đã được cập nhật. Vui lòng đăng nhập lại.
              </p>
            </div>
          </div>
          <router-link
            to="/login"
            class="w-full bg-primary hover:bg-primary/90 text-white font-bold py-3.5 rounded-lg shadow-lg shadow-primary/20 transition-all flex items-center justify-center gap-2 group"
          >
            <span>Đăng nhập ngay</span>
            <span class="material-symbols-outlined text-lg group-hover:translate-x-1 transition-transform">arrow_forward</span>
          </router-link>
        </div>

        <!-- Form -->
        <form v-else class="px-8 pb-10 space-y-5" @submit.prevent="handleSubmit" novalidate>

          <!-- New password field -->
          <div>
            <label class="block text-sm font-semibold text-slate-700 mb-2" for="newPassword">
              Mật khẩu mới
            </label>
            <div class="relative">
              <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 text-xl">lock</span>
              <input
                id="newPassword"
                v-model="form.newPassword"
                :type="showPassword ? 'text' : 'password'"
                class="w-full pl-11 pr-11 py-3 bg-slate-50 border rounded-lg outline-none transition-all text-slate-900 placeholder:text-slate-400 text-sm"
                :class="errors.newPassword
                  ? 'border-red-400 ring-2 ring-red-100'
                  : 'border-slate-200 focus:ring-2 focus:ring-primary/20 focus:border-primary'"
                placeholder="Tối thiểu 8 ký tự"
                autocomplete="new-password"
                :disabled="loading"
                @blur="validateNewPassword()"
                @input="errors.newPassword = ''"
              />
              <button
                type="button"
                class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 transition-colors"
                @click="showPassword = !showPassword"
              >
                <span class="material-symbols-outlined text-xl">{{ showPassword ? 'visibility_off' : 'visibility' }}</span>
              </button>
            </div>
            <p v-if="errors.newPassword" class="flex items-center gap-1.5 text-xs text-red-500 font-medium mt-1.5">
              <span class="material-symbols-outlined text-sm">error</span>
              {{ errors.newPassword }}
            </p>
          </div>

          <!-- Confirm password field -->
          <div>
            <label class="block text-sm font-semibold text-slate-700 mb-2" for="confirmPassword">
              Xác nhận mật khẩu
            </label>
            <div class="relative">
              <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 text-xl">lock_clock</span>
              <input
                id="confirmPassword"
                v-model="form.confirmPassword"
                :type="showConfirmPassword ? 'text' : 'password'"
                class="w-full pl-11 pr-11 py-3 bg-slate-50 border rounded-lg outline-none transition-all text-slate-900 placeholder:text-slate-400 text-sm"
                :class="errors.confirmPassword
                  ? 'border-red-400 ring-2 ring-red-100'
                  : 'border-slate-200 focus:ring-2 focus:ring-primary/20 focus:border-primary'"
                placeholder="Nhập lại mật khẩu mới"
                autocomplete="new-password"
                :disabled="loading"
                @blur="validateConfirmPassword()"
                @input="errors.confirmPassword = ''"
              />
              <button
                type="button"
                class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 transition-colors"
                @click="showConfirmPassword = !showConfirmPassword"
              >
                <span class="material-symbols-outlined text-xl">{{ showConfirmPassword ? 'visibility_off' : 'visibility' }}</span>
              </button>
            </div>
            <p v-if="errors.confirmPassword" class="flex items-center gap-1.5 text-xs text-red-500 font-medium mt-1.5">
              <span class="material-symbols-outlined text-sm">error</span>
              {{ errors.confirmPassword }}
            </p>
          </div>

          <!-- API error -->
          <div v-if="apiError" class="flex items-center gap-2 p-3 bg-red-50 border border-red-200 rounded-lg text-red-600 text-sm">
            <span class="material-symbols-outlined text-base shrink-0">error</span>
            {{ apiError }}
          </div>

          <!-- Submit button -->
          <button
            type="submit"
            class="w-full bg-primary hover:bg-primary/90 text-white font-bold py-3.5 rounded-lg shadow-lg shadow-primary/20 transition-all flex items-center justify-center gap-2 group disabled:opacity-70 disabled:cursor-not-allowed"
            :disabled="loading"
          >
            <span v-if="loading" class="material-symbols-outlined text-lg animate-spin">progress_activity</span>
            <span>{{ loading ? 'Đang xử lý...' : 'Xác nhận đặt lại' }}</span>
            <span v-if="!loading" class="material-symbols-outlined text-lg group-hover:translate-x-1 transition-transform">check</span>
          </button>

          <!-- Back link -->
          <div class="pt-4 text-center border-t border-slate-100">
            <router-link to="/login" class="inline-flex items-center gap-1.5 text-sm font-semibold text-primary hover:underline">
              <span class="material-symbols-outlined text-lg">arrow_back</span>
              Quay lại Đăng nhập
            </router-link>
          </div>
        </form>
      </div>

      <!-- Footer -->
      <div class="mt-8 text-center">
        <p class="text-xs text-slate-400 uppercase tracking-widest font-medium">
          © 2024 TopViec. All rights reserved.
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import authService from '@/services/auth.service'

const route = useRoute()
const router = useRouter()

const form = reactive({ newPassword: '', confirmPassword: '' })
const errors = reactive({ newPassword: '', confirmPassword: '' })

const loading = ref(false)
const apiError = ref('')
const resetSuccess = ref(false)
const tokenInvalid = ref(false)
const showPassword = ref(false)
const showConfirmPassword = ref(false)

// Lấy token từ query param: /reset-password?token=xxx
const token = ref('')

onMounted(() => {
  const t = route.query.token
  if (!t || typeof t !== 'string') {
    tokenInvalid.value = true
    return
  }
  token.value = t
})

function validateNewPassword(): boolean {
  errors.newPassword = ''
  if (!form.newPassword) {
    errors.newPassword = 'Vui lòng nhập mật khẩu mới'
    return false
  }
  if (form.newPassword.length < 6) {
    errors.newPassword = 'Mật khẩu phải có ít nhất 6 ký tự'
    return false
  }
  return true
}

function validateConfirmPassword(): boolean {
  errors.confirmPassword = ''
  if (!form.confirmPassword) {
    errors.confirmPassword = 'Vui lòng xác nhận mật khẩu'
    return false
  }
  if (form.confirmPassword !== form.newPassword) {
    errors.confirmPassword = 'Mật khẩu xác nhận không khớp'
    return false
  }
  return true
}

async function handleSubmit() {
  apiError.value = ''
  const validPw = validateNewPassword()
  const validConfirm = validateConfirmPassword()
  if (!validPw || !validConfirm) return

  loading.value = true
  try {
    await authService.resetPassword({ token: token.value, newPassword: form.newPassword })
    resetSuccess.value = true
  } catch (err: any) {
    const status = err?.response?.status
    if (status === 400 || status === 404) {
      // Token hết hạn hoặc không hợp lệ
      tokenInvalid.value = true
    } else {
      apiError.value = 'Có lỗi xảy ra. Vui lòng thử lại sau.'
    }
  } finally {
    loading.value = false
  }
}
</script>