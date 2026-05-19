<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useAuthStore } from '@/stores/auth.store'
import { useToast } from '@/composables/useToast'
import type { ReqRegisterEmployerDTO } from '@/types/auth.types'

const authStore = useAuthStore()
const toast = useToast()

// ─── Form state ─────────────────────────────────────────────────
const form = ref<ReqRegisterEmployerDTO & { confirmPassword: string }>({
  email: '',
  password: '',
  confirmPassword: '',
  companyName: '',
  companySlug: ''
})

const loading = ref(false)
const showPassword = ref(false)
const agreeTerms = ref(false)
const touched = ref({ 
  email: false, 
  password: false, 
  confirmPassword: false,
  companyName: false 
})

// ─── Helper: Tạo Slug từ Tên công ty ─────────────────────────────
const generateSlug = (text: string) => {
  return text
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[đĐ]/g, 'd')
    .replace(/([^0-9a-z-\s])/g, '')
    .replace(/(\s+)/g, '-')
    .replace(/-+/g, '-')
    .replace(/^-+|-+$/g, '')
}

watch(() => form.value.companyName, (newVal) => {
  form.value.companySlug = generateSlug(newVal)
})

// ─── Validation ─────────────────────────────────────────────────
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

const emailError = computed(() => {
  if (!touched.value.email || form.value.email === '') return null
  if (!emailRegex.test(form.value.email)) return 'Email doanh nghiệp không đúng định dạng'
  return null
})

const companyError = computed(() => {
  if (!touched.value.companyName || form.value.companyName === '') return null
  if (form.value.companyName.length < 3) return 'Tên công ty quá ngắn'
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
    form.value.companyName.length >= 3 &&
    form.value.email !== '' &&
    form.value.password !== '' &&
    agreeTerms.value
)

// ─── Submit ──────────────────────────────────────────────────────
async function handleRegister() {
  touched.value.email = true
  touched.value.password = true
  touched.value.companyName = true

  if (!canSubmit.value) return

  try {
    loading.value = true
    // Tách confirmPassword ra khỏi payload gửi lên server
    const { confirmPassword, ...payload } = form.value
    await authStore.registerEmployer(payload)
        toast.success('Đăng ký thành công!', 'Vui lòng kiểm tra email để xác thực tài khoản')
  } catch (err: any) {
    const msg = err.response?.data?.message ?? 'Đăng ký thất bại, vui lòng thử lại'
    toast.error('Lỗi đăng ký', msg)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="flex w-full max-w-[1000px] bg-white dark:bg-slate-900 rounded-2xl shadow-xl overflow-hidden min-h-[650px] relative">

    <div class="w-1/2 p-8 lg:p-12 flex-col justify-center relative bg-slate-50 dark:bg-slate-800/50 hidden md:flex border-r border-slate-100 dark:border-slate-800">
      <div class="absolute inset-0 opacity-10 dark:opacity-5 bg-[radial-gradient(circle_at_center,_var(--tw-gradient-stops))] from-primary via-transparent to-transparent"></div>
      <div class="relative z-10 flex flex-col items-center text-center">
        <div
          class="w-72 h-72 mb-8 bg-cover bg-center rounded-2xl shadow-2xl transform -rotate-2 hover:rotate-0 transition-transform duration-500"
          style="background-image: url('https://img.freepik.com/free-vector/job-interview-concept-illustration_114360-1677.jpg');"
        ></div>
        <h3 class="text-2xl font-bold mb-4 text-slate-800 dark:text-white">Tuyển dụng Nhân tài vượt trội</h3>
        <p class="text-slate-500 dark:text-slate-400 leading-relaxed">
          Đồng hành cùng TopViec để tiếp cận nguồn ứng viên chất lượng và xây dựng đội ngũ chuyên nghiệp cho doanh nghiệp của bạn.
        </p>
        
        <div class="mt-8 flex gap-4">
          <div class="bg-white dark:bg-slate-800 p-3 rounded-lg shadow-sm border border-slate-100 dark:border-slate-700">
            <p class="text-primary font-bold text-xl">30k+</p>
            <p class="text-[10px] uppercase tracking-wider text-slate-400">Ứng viên mới/tháng</p>
          </div>
          <div class="bg-white dark:bg-slate-800 p-3 rounded-lg shadow-sm border border-slate-100 dark:border-slate-700">
            <p class="text-primary font-bold text-xl">500+</p>
            <p class="text-[10px] uppercase tracking-wider text-slate-400">Doanh nghiệp tin dùng</p>
          </div>
        </div>
      </div>
    </div>

    <div class="w-full md:w-1/2 p-8 lg:p-12 flex flex-col justify-center">
      <div class="max-w-md w-full mx-auto">

        <div class="mb-8">
          <h1 class="text-3xl font-bold leading-tight mb-2">Đăng ký Tuyển dụng</h1>
          <p class="text-slate-500 dark:text-slate-400 text-sm">Bắt đầu hành trình tìm kiếm cộng sự tài năng cùng TopViec.</p>
        </div>

        <div class="space-y-4">

          <div>
            <label class="block text-sm font-medium mb-1.5" for="companyName">Tên công ty / Doanh nghiệp</label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                <span class="material-symbols-outlined text-[20px]">corporate_fare</span>
              </div>
              <input
                v-model="form.companyName"
                id="companyName"
                type="text"
                placeholder="VD: Công ty Công nghệ TopViec"
                @blur="touched.companyName = true"
                :class="[
                  'block w-full pl-10 pr-4 py-2.5 bg-white dark:bg-slate-800 border rounded-lg text-sm focus:ring-2 focus:border-transparent transition-colors',
                  companyError ? 'border-red-400 focus:ring-red-400/30' : 'border-slate-300 dark:border-slate-600 focus:ring-primary'
                ]"
              />
            </div>
            <p v-if="companyError" class="text-red-500 text-xs mt-1.5">{{ companyError }}</p>
          </div>

          <div>
            <label class="block text-sm font-medium mb-1.5" for="email">Email doanh nghiệp</label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                <span class="material-symbols-outlined text-[20px]">mail</span>
              </div>
              <input
                v-model="form.email"
                id="email"
                type="email"
                placeholder="hr@congty.com"
                @blur="touched.email = true"
                :class="[
                  'block w-full pl-10 pr-10 py-2.5 bg-white dark:bg-slate-800 border rounded-lg text-sm focus:ring-2 focus:border-transparent transition-colors',
                  emailError ? 'border-red-400 focus:ring-red-400/30' : 'border-slate-300 dark:border-slate-600 focus:ring-primary'
                ]"
              />
            </div>
            <p v-if="emailError" class="text-red-500 text-xs mt-1.5">{{ emailError }}</p>
          </div>

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
                placeholder="Tối thiểu 6 ký tự"
                @blur="touched.password = true"
                :class="[
                  'block w-full pl-10 pr-10 py-2.5 bg-white dark:bg-slate-800 border rounded-lg text-sm focus:ring-2 focus:border-transparent transition-colors',
                  passwordError ? 'border-red-400 focus:ring-red-400/30' : 'border-slate-300 dark:border-slate-600 focus:ring-primary'
                ]"
              />
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="absolute inset-y-0 right-0 pr-3 flex items-center text-slate-400 hover:text-slate-600 cursor-pointer"
              >
                <span class="material-symbols-outlined text-[20px]">
                  {{ showPassword ? 'visibility' : 'visibility_off' }}
                </span>
              </button>
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium mb-1.5" for="confirmPassword">Xác nhận mật khẩu</label>
            <div class="relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
                <span class="material-symbols-outlined text-[20px]">verified_user</span>
              </div>
              <input
                v-model="form.confirmPassword"
                id="confirmPassword"
                :type="showPassword ? 'text' : 'password'"
                placeholder="Nhập lại mật khẩu"
                :class="[
                  'block w-full pl-10 pr-10 py-2.5 bg-white dark:bg-slate-800 border rounded-lg text-sm focus:ring-2 focus:border-transparent transition-colors',
                  passwordMismatch ? 'border-red-400 focus:ring-red-400/30' : 'border-slate-300 dark:border-slate-600 focus:ring-primary'
                ]"
              />
            </div>
          </div>

          <div class="flex items-start mt-4">
            <div class="flex items-center h-5">
              <input
                v-model="agreeTerms"
                id="terms"
                type="checkbox"
                class="w-4 h-4 rounded border-slate-300 text-primary focus:ring-primary cursor-pointer"
              />
            </div>
            <label class="ml-2 text-sm text-slate-500 dark:text-slate-400 cursor-pointer" for="terms">
              Tôi cam kết tuân thủ 
              <a class="text-primary hover:underline font-bold" href="#">Quy định đăng tin</a> và 
              <a class="text-primary hover:underline font-bold" href="#">Chính sách bảo mật doanh nghiệp</a>
            </label>
          </div>

          <button
            @click="handleRegister"
            :disabled="!canSubmit"
            class="w-full mt-6 bg-primary hover:bg-blue-600 disabled:opacity-40 disabled:cursor-not-allowed text-white font-bold py-3.5 rounded-lg shadow-lg hover:shadow-blue-500/30 transition-all text-sm flex items-center justify-center gap-2 cursor-pointer"
          >
            <span v-if="loading" class="flex items-center gap-2">
              <svg class="animate-spin h-4 w-4" viewBox="0 0 24 24" fill="none">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
              </svg>
              Đang thiết lập hệ thống...
            </span>
            <span v-else class="flex items-center gap-2">
              Kích hoạt tài khoản Tuyển dụng
              <span class="material-symbols-outlined text-[18px]">rocket_launch</span>
            </span>
          </button>
        </div>

        <p class="mt-8 text-center text-sm text-slate-500 dark:text-slate-400 border-t border-slate-100 dark:border-slate-800 pt-6">
          Đã có tài khoản đối tác?
          <router-link to="/login" class="text-primary font-bold hover:underline">Đăng nhập tại đây</router-link>
        </p>

      </div>
    </div>
  </div>
</template>

<style scoped>
/* Bạn có thể thêm các hiệu ứng transition riêng nếu cần */
</style>