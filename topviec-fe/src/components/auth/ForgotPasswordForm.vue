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
            <span class="material-symbols-outlined text-4xl">lock_reset</span>
          </div>
          <h2 class="text-2xl font-bold text-slate-900 mb-2">Quên mật khẩu?</h2>
          <p class="text-slate-500 text-sm leading-relaxed">
            Nhập email của bạn để nhận liên kết đặt lại mật khẩu.
          </p>
        </div>

        <!-- Success State -->
        <div v-if="emailSent" class="px-8 pb-10 space-y-5">
          <div class="flex gap-4 p-4 bg-blue-50 border border-blue-200 rounded-lg">
            <div class="shrink-0 w-11 h-11 bg-primary rounded-full flex items-center justify-center text-white">
              <span class="material-symbols-outlined text-xl" style="font-variation-settings: 'FILL' 1">check_circle</span>
            </div>
            <div>
              <p class="text-sm font-bold text-blue-900 mb-1">Yêu cầu đã được gửi!</p>
              <p class="text-sm text-blue-700 leading-relaxed">
                Nếu <strong class="font-semibold text-blue-900">{{ submittedEmail }}</strong> tồn tại trong hệ thống,
                chúng tôi đã gửi link đặt lại mật khẩu. Vui lòng kiểm tra hộp thư (kể cả thư mục spam).
              </p>
            </div>
          </div>

          <button
            class="w-full border-2 border-primary text-primary font-bold py-3 rounded-lg text-sm transition-colors flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-primary hover:text-white"
            :disabled="resendCooldown > 0 || loading"
            @click="handleResend"
          >
            <span v-if="loading" class="material-symbols-outlined text-lg animate-spin">progress_activity</span>
            <span v-if="resendCooldown > 0">Gửi lại sau {{ resendCooldown }}s</span>
            <span v-else-if="loading">Đang gửi...</span>
            <span v-else>Gửi lại email</span>
          </button>

          <div class="pt-4 text-center border-t border-slate-100">
            <router-link to="/login" class="inline-flex items-center gap-1.5 text-sm font-semibold text-primary hover:underline">
              <span class="material-symbols-outlined text-lg">arrow_back</span>
              Quay lại Đăng nhập
            </router-link>
          </div>
        </div>

        <!-- Form -->
        <form v-else class="px-8 pb-10 space-y-6" @submit.prevent="handleSubmit" novalidate>

          <!-- Email field -->
          <div>
            <label class="block text-sm font-semibold text-slate-700 mb-2" for="email">
              Địa chỉ Email
            </label>
            <div class="relative">
              <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 text-xl">mail</span>
              <input
                id="email"
                v-model="form.email"
                type="email"
                class="w-full pl-11 pr-4 py-3 bg-slate-50 border rounded-lg outline-none transition-all text-slate-900 placeholder:text-slate-400 text-sm"
                :class="errors.email
                  ? 'border-red-400 ring-2 ring-red-100'
                  : 'border-slate-200 focus:ring-2 focus:ring-primary/20 focus:border-primary'"
                placeholder="example@gmail.com"
                autocomplete="email"
                :disabled="loading"
                @blur="validateEmail()"
                @input="errors.email = ''"
              />
            </div>
            <p v-if="errors.email" class="flex items-center gap-1.5 text-xs text-red-500 font-medium mt-1.5">
              <span class="material-symbols-outlined text-sm">error</span>
              {{ errors.email }}
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
            <span>{{ loading ? 'Đang gửi...' : 'Gửi yêu cầu' }}</span>
            <span v-if="!loading" class="material-symbols-outlined text-lg group-hover:translate-x-1 transition-transform">send</span>
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
import { ref, reactive, onUnmounted } from 'vue'
import authService from '@/services/auth.service'

const form = reactive({ email: '' })
const errors = reactive({ email: '' })

const loading = ref(false)
const apiError = ref('')
const emailSent = ref(false)
const submittedEmail = ref('')
const resendCooldown = ref(0)

let cooldownTimer: ReturnType<typeof setInterval> | null = null

function validateEmail(): boolean {
  errors.email = ''
  if (!form.email.trim()) {
    errors.email = 'Vui lòng nhập địa chỉ email'
    return false
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(form.email)) {
    errors.email = 'Địa chỉ email không hợp lệ'
    return false
  }
  return true
}

async function handleSubmit() {
  apiError.value = ''
  if (!validateEmail()) return
  loading.value = true
  try {
    await authService.forgotPassword({ email: form.email })
    // BE luôn trả 200 dù email tồn tại hay không (anti email enumeration)
    submittedEmail.value = form.email
    emailSent.value = true
    startCooldown()
  } catch {
    apiError.value = 'Có lỗi xảy ra. Vui lòng thử lại sau.'
  } finally {
    loading.value = false
  }
}

async function handleResend() {
  if (resendCooldown.value > 0 || loading.value) return
  loading.value = true
  try {
    await authService.forgotPassword({ email: submittedEmail.value })
    startCooldown()
  } catch {
    // Silent fail — không lộ thông tin
  } finally {
    loading.value = false
  }
}

function startCooldown() {
  resendCooldown.value = 60
  cooldownTimer = setInterval(() => {
    resendCooldown.value--
    if (resendCooldown.value <= 0 && cooldownTimer) {
      clearInterval(cooldownTimer)
      cooldownTimer = null
    }
  }, 1000)
}

onUnmounted(() => {
  if (cooldownTimer) clearInterval(cooldownTimer)
})
</script>