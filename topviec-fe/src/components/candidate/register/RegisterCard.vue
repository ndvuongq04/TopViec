<script setup lang="ts">
import { ref, computed } from 'vue'
import { useAuthStore } from '@/stores/auth.store'
import { useToast } from '@/composables/useToast'
import type { ReqRegisterCandidateDTO } from '@/types/auth.types'

const authStore = useAuthStore()
const toast = useToast()

// ─── Form state ─────────────────────────────────────────────────
const form = ref<ReqRegisterCandidateDTO & { confirmPassword: string }>({
  email: '',
  password: '',
  confirmPassword: '',
})

const loading = ref(false)
const showPassword = ref(false)
const agreeTerms = ref(false)
const touched = ref({ email: false, password: false, confirmPassword: false })

// ─── Validation ─────────────────────────────────────────────────
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

const emailError = computed(() => {
  if (!touched.value.email || form.value.email === '') return null
  if (!emailRegex.test(form.value.email)) return 'Email không đúng định dạng (vd: example@email.com)'
  return null
})

const passwordError = computed(() => {
  if (!touched.value.password || form.value.password === '') return null
  if (form.value.password.length < 6) return 'Mật khẩu phải có ít nhất 6 ký tự'
  return null
})

const passwordMismatch = computed(
  () =>
    form.value.confirmPassword !== '' &&
    form.value.password !== form.value.confirmPassword
)

const canSubmit = computed(
  () =>
    !loading.value &&
    !passwordMismatch.value &&
    !emailError.value &&
    !passwordError.value &&
    form.value.email !== '' &&
    form.value.password !== '' &&
    form.value.confirmPassword !== '' &&
    agreeTerms.value
)

// ─── Submit ──────────────────────────────────────────────────────
async function handleRegister() {
  touched.value.email = true
  touched.value.password = true
  touched.value.confirmPassword = true

  if (!emailRegex.test(form.value.email)) {
    toast.error('Email không hợp lệ', 'Vui lòng nhập đúng định dạng email')
    return
  }

  if (form.value.password.length < 6) {
    toast.error('Mật khẩu quá ngắn', 'Mật khẩu phải có ít nhất 6 ký tự')
    return
  }

  if (passwordMismatch.value) {
    toast.error('Mật khẩu không khớp', 'Vui lòng kiểm tra lại mật khẩu xác nhận')
    return
  }

  if (!agreeTerms.value) {
    toast.warning('Chưa đồng ý điều khoản', 'Bạn cần đồng ý với điều khoản dịch vụ')
    return
  }

  try {
    loading.value = true
    const { confirmPassword, ...payload } = form.value
    await authStore.register(payload)
    toast.success('Đăng ký thành công!', 'Vui lòng kiểm tra email để xác thực tài khoản')
  } catch (err: any) {
    const msg = err.response?.data?.message ?? 'Đăng ký thất bại, vui lòng thử lại'
    toast.error('Đăng ký thất bại', msg)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="flex w-full max-w-[1000px] bg-white dark:bg-slate-900 rounded-2xl shadow-xl overflow-hidden min-h-[600px] relative">

    <!-- Panel trái: hình ảnh minh họa -->
    <div class="w-1/2 p-8 lg:p-12 flex-col justify-center relative bg-slate-50 dark:bg-slate-800/50 hidden md:flex border-r border-slate-100 dark:border-slate-800">
      <div class="absolute inset-0 opacity-10 dark:opacity-5 bg-[radial-gradient(circle_at_center,_var(--tw-gradient-stops))] from-primary via-transparent to-transparent"></div>
      <div class="relative z-10 flex flex-col items-center text-center">
        <div
          class="w-64 h-64 mb-8 bg-cover bg-center rounded-2xl shadow-lg"
          style="background-image: url('https://lh3.googleusercontent.com/aida-public/AB6AXuA3w4vemPVq2cpacnUYnl2eqcgmtVAOBM8lRbtj5tt5FBdaUGvXMjAYUSmkF193Xqri1DcpUISzcfotdeUXpBPQ3Cio0h3VakmNQMINzymwJMlzpg9hntJZ6yiNXkscfaYrtH77hI5x1zt_4JMKLtV0BgPMnnrDF3lN7e1NdQ25Uq0jzN0c-FqkOhxlB97gBnm5tDYyQA95eW-YjjJHfra10h0csBYyr9BUYeeFky_Gp31bD4HW64-dEyCS3pXSWYh4e3frhtNg68s6');"
        ></div>
        <h3 class="text-2xl font-bold mb-4">Khám Phá Cơ Hội Tiếp Theo</h3>
        <p class="text-slate-500 dark:text-slate-400">
          Hàng nghìn ứng viên đã tìm được công việc mơ ước qua nền tảng kết nối thông minh của TopViec.
        </p>
      </div>
    </div>

    <!-- Panel phải: form đăng ký -->
    <div class="w-full md:w-1/2 p-8 lg:p-12 flex flex-col justify-center">
      <div class="max-w-md w-full mx-auto">

        <!-- Tiêu đề -->
        <div class="mb-8">
          <h1 class="text-3xl font-bold leading-tight mb-2">Tạo tài khoản</h1>
          <p class="text-slate-500 dark:text-slate-400 text-sm">Đăng ký TopViec để tìm việc làm phù hợp ngay hôm nay.</p>
        </div>

        <!-- Nút Google -->
        <button
          type="button"
          class="w-full flex cursor-pointer items-center justify-center rounded-lg h-11 px-4 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors gap-3 text-sm font-bold mb-6 border border-slate-200 dark:border-slate-700"
        >
          <svg class="w-5 h-5" viewBox="0 0 24 24">
            <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
            <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
            <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05"/>
            <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
          </svg>
          <span>Đăng ký với Google</span>
        </button>

        <!-- Divider -->
        <div class="flex items-center mb-6">
          <div class="flex-1 h-px bg-slate-200 dark:bg-slate-700"></div>
          <span class="px-4 text-xs font-medium text-slate-400 dark:text-slate-500 uppercase">Hoặc đăng ký bằng email</span>
          <div class="flex-1 h-px bg-slate-200 dark:bg-slate-700"></div>
        </div>

        <!-- Form fields -->
        <div class="space-y-4">

          <!-- Email -->
          <div>
            <label class="block text-sm font-medium mb-1.5" for="email">Địa chỉ Email</label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                <span class="material-symbols-outlined text-[20px]">mail</span>
              </div>
              <input
                v-model="form.email"
                id="email"
                type="email"
                placeholder="example@email.com"
                required
                @blur="touched.email = true"
                :class="[
                  'block w-full pl-10 pr-10 py-2.5 bg-white dark:bg-slate-800 border rounded-lg text-sm focus:ring-2 focus:border-transparent placeholder-slate-400 dark:placeholder-slate-500 transition-colors',
                  emailError
                    ? 'border-red-400 focus:ring-red-400/30'
                    : 'border-slate-300 dark:border-slate-600 focus:ring-primary focus:border-primary'
                ]"
              />
              <div v-if="touched.email && form.email !== ''" class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none">
                <span class="material-symbols-outlined text-[18px]" :class="emailError ? 'text-red-400' : 'text-green-500'">
                  {{ emailError ? 'cancel' : 'check_circle' }}
                </span>
              </div>
            </div>
            <p v-if="emailError" class="text-red-500 text-xs mt-1.5 flex items-center gap-1">
              <span class="material-symbols-outlined text-[13px]">error</span>
              {{ emailError }}
            </p>
          </div>

          <!-- Password -->
          <div>
            <label class="block text-sm font-medium mb-1.5" for="password">Mật khẩu</label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                <span class="material-symbols-outlined text-[20px]">lock</span>
              </div>
              <input
                v-model="form.password"
                id="password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="Ít nhất 6 ký tự"
                required
                @blur="touched.password = true"
                :class="[
                  'block w-full pl-10 pr-10 py-2.5 bg-white dark:bg-slate-800 border rounded-lg text-sm focus:ring-2 focus:border-transparent placeholder-slate-400 dark:placeholder-slate-500 transition-colors',
                  passwordError
                    ? 'border-red-400 focus:ring-red-400/30'
                    : 'border-slate-300 dark:border-slate-600 focus:ring-primary focus:border-primary'
                ]"
              />
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="absolute inset-y-0 right-0 pr-3 flex items-center text-slate-400 hover:text-slate-600 dark:hover:text-slate-300"
              >
                <span class="material-symbols-outlined text-[20px]">
                  {{ showPassword ? 'visibility' : 'visibility_off' }}
                </span>
              </button>
            </div>
            <p v-if="passwordError" class="text-red-500 text-xs mt-1.5 flex items-center gap-1">
              <span class="material-symbols-outlined text-[13px]">error</span>
              {{ passwordError }}
            </p>
          </div>

          <!-- Confirm Password -->
          <div>
            <label class="block text-sm font-medium mb-1.5" for="confirmPassword">Xác nhận mật khẩu</label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                <span class="material-symbols-outlined text-[20px]">lock</span>
              </div>
              <input
                v-model="form.confirmPassword"
                id="confirmPassword"
                :type="showPassword ? 'text' : 'password'"
                placeholder="Nhập lại mật khẩu"
                required
                @blur="touched.confirmPassword = true"
                :class="[
                  'block w-full pl-10 pr-10 py-2.5 bg-white dark:bg-slate-800 border rounded-lg text-sm focus:ring-2 focus:border-transparent placeholder-slate-400 dark:placeholder-slate-500 transition-colors',
                  passwordMismatch
                    ? 'border-red-400 focus:ring-red-400/30'
                    : 'border-slate-300 dark:border-slate-600 focus:ring-primary focus:border-primary'
                ]"
              />
            </div>
            <p v-if="passwordMismatch" class="text-red-500 text-xs mt-1.5 flex items-center gap-1">
              <span class="material-symbols-outlined text-[13px]">error</span>
              Mật khẩu xác nhận không khớp
            </p>
          </div>

          <!-- Terms checkbox -->
          <div class="flex items-start mt-4">
            <div class="flex items-center h-5">
              <input
                v-model="agreeTerms"
                id="terms"
                type="checkbox"
                class="w-4 h-4 rounded border-slate-300 dark:border-slate-600 text-primary focus:ring-primary dark:bg-slate-800 cursor-pointer"
              />
            </div>
            <label class="ml-2 text-sm text-slate-500 dark:text-slate-400 cursor-pointer" for="terms">
              Tôi đồng ý với
              <a class="text-primary hover:underline font-medium" href="#">Điều khoản dịch vụ</a>
              và
              <a class="text-primary hover:underline font-medium" href="#">Chính sách bảo mật</a>
              của TopViec
            </label>
          </div>

          <!-- Submit button -->
          <button
            @click="handleRegister"
            :disabled="!canSubmit"
            class="w-full mt-6 bg-primary hover:bg-blue-600 disabled:opacity-40 disabled:cursor-not-allowed text-white font-bold py-3 px-4 rounded-lg shadow-md hover:shadow-lg transition-all text-sm flex items-center justify-center gap-2"
          >
            <span v-if="loading" class="flex items-center gap-2">
              <svg class="animate-spin h-4 w-4" viewBox="0 0 24 24" fill="none">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
              </svg>
              Đang đăng ký...
            </span>
            <span v-else class="flex items-center gap-2">
              Tạo tài khoản
              <span class="material-symbols-outlined text-[18px]">arrow_forward</span>
            </span>
          </button>
        </div>

        <!-- Link đăng nhập -->
        <p class="mt-8 text-center text-sm text-slate-500 dark:text-slate-400">
          Đã có tài khoản?
          <router-link to="/login" class="text-primary font-bold hover:underline">Đăng nhập tại đây</router-link>
        </p>

      </div>
    </div>
  </div>
</template>