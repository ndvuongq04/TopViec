<template>
  <GlobalModal
    :visible="visible"
    title="Thêm nhà tuyển dụng mới"
    subtitle="Tạo tài khoản và hồ sơ công ty cho nhà tuyển dụng mới."
    icon="corporate_fare"
    confirm-text="Tạo tài khoản"
    loading-text="Đang xử lý..."
    :loading="loading"
    form-id="create-employer-form"
    variant="danger"
    :disabled-confirm="!canSubmit"
    @close="$emit('close')"
  >
    <form id="create-employer-form" class="space-y-4 pt-2" @submit.prevent="handleSubmit">
      
      <!-- Company Name -->
      <div>
        <label class="block text-sm font-medium mb-1.5 text-slate-700 dark:text-slate-300" for="companyName">
          Tên công ty / Doanh nghiệp <span class="text-red-500">*</span>
        </label>
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
              'block w-full pl-10 pr-4 py-2.5 bg-white dark:bg-slate-900 border rounded-lg text-sm focus:ring-2 focus:border-transparent transition-colors outline-none',
              companyError ? 'border-red-400 focus:ring-red-400/30' : 'border-slate-200 dark:border-slate-700 focus:ring-[#963131]/20 focus:border-[#963131]'
            ]"
          />
        </div>
        <p v-if="companyError" class="text-red-500 text-xs mt-1.5">{{ companyError }}</p>
      </div>

      <!-- Email -->
      <div>
        <label class="block text-sm font-medium mb-1.5 text-slate-700 dark:text-slate-300" for="email">
          Email doanh nghiệp <span class="text-red-500">*</span>
        </label>
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
              'block w-full pl-10 pr-10 py-2.5 bg-white dark:bg-slate-900 border rounded-lg text-sm focus:ring-2 focus:border-transparent transition-colors outline-none',
              emailError ? 'border-red-400 focus:ring-red-400/30' : 'border-slate-200 dark:border-slate-700 focus:ring-[#963131]/20 focus:border-[#963131]'
            ]"
          />
        </div>
        <p v-if="emailError" class="text-red-500 text-xs mt-1.5">{{ emailError }}</p>
      </div>

      <!-- Password -->
      <div>
        <label class="block text-sm font-medium mb-1.5 text-slate-700 dark:text-slate-300" for="password">
          Mật khẩu <span class="text-red-500">*</span>
        </label>
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
              'block w-full pl-10 pr-10 py-2.5 bg-white dark:bg-slate-900 border rounded-lg text-sm focus:ring-2 focus:border-transparent transition-colors outline-none',
              passwordError ? 'border-red-400 focus:ring-red-400/30' : 'border-slate-200 dark:border-slate-700 focus:ring-[#963131]/20 focus:border-[#963131]'
            ]"
          />
          <button
            type="button"
            @click="showPassword = !showPassword"
            class="absolute inset-y-0 right-0 pr-3 flex items-center text-slate-400 hover:text-slate-600"
          >
            <span class="material-symbols-outlined text-[20px]">
              {{ showPassword ? 'visibility' : 'visibility_off' }}
            </span>
          </button>
        </div>
        <p v-if="passwordError" class="text-red-500 text-xs mt-1.5">{{ passwordError }}</p>
      </div>

      <!-- Confirm Password -->
      <div>
        <label class="block text-sm font-medium mb-1.5 text-slate-700 dark:text-slate-300" for="confirmPassword">
          Xác nhận mật khẩu <span class="text-red-500">*</span>
        </label>
        <div class="relative">
          <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-400">
            <span class="material-symbols-outlined text-[20px]">verified_user</span>
          </div>
          <input
            v-model="form.confirmPassword"
            id="confirmPassword"
            :type="showPassword ? 'text' : 'password'"
            placeholder="Nhập lại mật khẩu"
            @blur="touched.confirmPassword = true"
            :class="[
              'block w-full pl-10 pr-10 py-2.5 bg-white dark:bg-slate-900 border rounded-lg text-sm focus:ring-2 focus:border-transparent transition-colors outline-none',
              passwordMismatch ? 'border-red-400 focus:ring-red-400/30' : 'border-slate-200 dark:border-slate-700 focus:ring-[#963131]/20 focus:border-[#963131]'
            ]"
          />
        </div>
        <p v-if="passwordMismatch && touched.confirmPassword" class="text-red-500 text-xs mt-1.5">Mật khẩu xác nhận không khớp</p>
      </div>

    </form>
  </GlobalModal>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import GlobalModal from '@/components/ui/GlobalModal.vue'
import type { ReqRegisterEmployerDTO } from '@/types/auth.types'

const props = defineProps<{
  visible: boolean
  loading?: boolean
}>()

const emit = defineEmits<{
  close: []
  submit: [payload: ReqRegisterEmployerDTO]
}>()

// ─── Form State ─────────────────────────────────────────────────────────────────
const form = reactive<ReqRegisterEmployerDTO & { confirmPassword: string }>({
  email: '',
  password: '',
  confirmPassword: '',
  companyName: '',
  companySlug: ''
})

const showPassword = ref(false)
const touched = reactive({ 
  email: false, 
  password: false, 
  confirmPassword: false,
  companyName: false 
})

// ─── Helper: Slug Generation ────────────────────────────────────────────────────
const generateSlug = (text: string) => {
  return text
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[đĐ]/g, 'd')
    .replace(/([^0-9a-z-\s])/g, '')
    .trim()
    .replace(/(\s+)/g, '-')
    .replace(/-+/g, '-')
    .replace(/^-+|-+$/g, '')
}

watch(() => form.companyName, (newVal) => {
  form.companySlug = generateSlug(newVal)
})

// ─── Validation ─────────────────────────────────────────────────────────────────
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

const emailError = computed(() => {
  if (!touched.email || form.email === '') return null
  if (!emailRegex.test(form.email)) return 'Email doanh nghiệp không đúng định dạng'
  return null
})

const companyError = computed(() => {
  if (!touched.companyName || form.companyName === '') return null
  if (form.companyName.length < 3) return 'Tên công ty quá ngắn (tối thiểu 3 ký tự)'
  return null
})

const passwordError = computed(() => {
  if (!touched.password || form.password === '') return null
  if (form.password.length < 6) return 'Mật khẩu phải có ít nhất 6 ký tự'
  return null
})

const passwordMismatch = computed(
  () =>
    touched.confirmPassword &&
    form.confirmPassword !== '' &&
    form.password !== form.confirmPassword
)

const canSubmit = computed(
  () =>
    !props.loading &&
    !passwordMismatch.value &&
    !emailError.value &&
    !passwordError.value &&
    !companyError.value &&
    form.companyName.length >= 3 &&
    form.email !== '' &&
    form.password !== '' &&
    form.confirmPassword !== ''
)

// ─── Functions ──────────────────────────────────────────────────────────────────
function resetForm() {
  form.email = ''
  form.password = ''
  form.confirmPassword = ''
  form.companyName = ''
  form.companySlug = ''
  
  touched.email = false
  touched.password = false
  touched.confirmPassword = false
  touched.companyName = false
  showPassword.value = false
}

function handleSubmit() {
  if (!canSubmit.value) return
  
  // Create a clean payload without confirmPassword
  const { confirmPassword, ...payload } = form
  emit('submit', payload)
}

// Reset form when modal closes/opens
watch(() => props.visible, (val) => {
  if (val) resetForm()
})
</script>
