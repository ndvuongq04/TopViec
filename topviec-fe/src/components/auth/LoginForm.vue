<template>
  <div class="flex w-full flex-col justify-center px-6 py-8 lg:w-1/2 lg:px-12">
    <!-- Logo -->
    <div class="mb-10 flex items-center gap-2">
      <div
        class="flex h-10 w-10 items-center justify-center rounded-lg bg-primary text-white"
      >
        <span class="material-symbols-outlined">work</span>
      </div>
      <span
        class="text-2xl font-bold tracking-tight text-slate-900 dark:text-white"
        >TopViec</span
      >
    </div>

    <!-- Heading -->
    <div class="mb-8">
      <h2
        class="text-3xl font-black tracking-tight text-slate-900 dark:text-white"
      >
        Chào mừng bạn quay lại!
      </h2>
      <p class="mt-2 text-slate-500 dark:text-slate-400">
        Đăng nhập để tiếp tục hành trình sự nghiệp của bạn
      </p>
    </div>

    <!-- Google Login -->
    <button
      @click="$emit('google-login')"
      class="flex w-full items-center justify-center gap-3 rounded-lg border border-slate-200 bg-white py-3 px-4 text-sm font-semibold text-slate-700 shadow-sm transition hover:bg-slate-50 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-200 dark:hover:bg-slate-700"
    >
      <svg class="h-5 w-5" viewBox="0 0 24 24">
        <path
          d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
          fill="#4285F4"
        />
        <path
          d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
          fill="#34A853"
        />
        <path
          d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
          fill="#FBBC05"
        />
        <path
          d="M12 5.38c1.62 0 3.06.56 4.21 1.66l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
          fill="#EA4335"
        />
      </svg>
      Đăng nhập với Google
    </button>

    <!-- Divider -->
    <div class="relative my-8">
      <div class="absolute inset-0 flex items-center">
        <div class="w-full border-t border-slate-200 dark:border-slate-700"></div>
      </div>
      <div class="relative flex justify-center text-xs uppercase">
        <span
          class="bg-white px-2 text-slate-500 dark:bg-slate-900 dark:text-slate-400 font-medium"
        >
          Hoặc đăng nhập với Email
        </span>
      </div>
    </div>

    <!-- Form -->
    <div class="space-y-5">
      <!-- Email -->
      <div>
        <label
          for="email"
          class="block text-sm font-medium text-slate-700 dark:text-slate-300"
          >Email</label
        >
        <div class="mt-1">
          <input
            id="email"
            v-model="form.email"
            type="email"
            placeholder="example@gmail.com"
            @keyup.enter="handleLogin"
            class="block w-full rounded-lg border-slate-200 bg-slate-50 px-4 py-3 text-slate-900 focus:border-primary focus:ring-primary dark:border-slate-700 dark:bg-slate-800 dark:text-white sm:text-sm transition"
          />
        </div>
      </div>

      <!-- Password -->
      <div>
        <div class="flex items-center justify-between">
          <label
            for="password"
            class="block text-sm font-medium text-slate-700 dark:text-slate-300"
            >Mật khẩu</label
          >
<router-link
  to="/forgot-password"
  class="text-sm font-semibold text-primary hover:text-primary/80 transition"
>Quên mật khẩu?</router-link>
        </div>
        <div class="mt-1 relative">
          <input
            id="password"
            v-model="form.password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="••••••••"
            @keyup.enter="handleLogin"
            class="block w-full rounded-lg border-slate-200 bg-slate-50 px-4 py-3 pr-16 text-slate-900 focus:border-primary focus:ring-primary dark:border-slate-700 dark:bg-slate-800 dark:text-white sm:text-sm transition"
          />
          <button
            type="button"
            @click="showPassword = !showPassword"
            class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 transition-colors"
          >
            <svg
              v-if="showPassword"
              xmlns="http://www.w3.org/2000/svg"
              class="h-4 w-4"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              stroke-width="2"
            >
              <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
              <path stroke-linecap="round" stroke-linejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.477 0 8.268 2.943 9.542 7-1.274 4.057-5.065 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
            </svg>
            <svg
              v-else
              xmlns="http://www.w3.org/2000/svg"
              class="h-4 w-4"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              stroke-width="2"
            >
              <path stroke-linecap="round" stroke-linejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.477 0-8.268-2.943-9.542-7a9.97 9.97 0 012.163-3.592M6.7 6.7A9.953 9.953 0 0112 5c4.477 0 8.268 2.943 9.542 7a9.97 9.97 0 01-1.357 2.574M3 3l18 18" />
            </svg>
          </button>
        </div>
      </div>

      <!-- Submit -->
      <button
        @click="handleLogin"
        :disabled="loading || !form.email || !form.password"
        class="flex w-full justify-center rounded-lg bg-primary py-3 px-4 text-sm font-bold text-white shadow-md hover:bg-primary/90 focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2 transition duration-200 disabled:opacity-40 disabled:cursor-not-allowed mt-1"
      >
        <span v-if="loading" class="flex items-center gap-2">
          <svg class="animate-spin h-4 w-4" viewBox="0 0 24 24" fill="none">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
          </svg>
          Đang đăng nhập...
        </span>
        <span v-else>Đăng nhập</span>
      </button>
    </div>

    <!-- Register link -->
    <p class="mt-10 text-center text-sm text-slate-500 dark:text-slate-400">
      Chưa có tài khoản?
      <router-link
        to="/register"
        class="font-bold text-primary hover:text-primary/80 transition underline underline-offset-4"
      >
        Đăng ký ngay
      </router-link>
    </p>

    <!-- Footer links -->
    <div class="mt-auto pt-10 flex justify-center gap-6 text-xs text-slate-400">
      <a href="#" class="hover:text-slate-600 dark:hover:text-slate-200 transition">Điều khoản</a>
      <a href="#" class="hover:text-slate-600 dark:hover:text-slate-200 transition">Bảo mật</a>
      <a href="#" class="hover:text-slate-600 dark:hover:text-slate-200 transition">Hỗ trợ</a>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth.store'
import { useToast } from '@/composables/useToast'

const authStore = useAuthStore()
const toast = useToast()

const emit = defineEmits<{
  (e: 'google-login'): void
}>()

const form = ref({ email: '', password: '' })
const showPassword = ref(false)
const loading = ref(false)

async function handleLogin() {
  if (!form.value.email || !form.value.password) return

  try {
    loading.value = true
    await authStore.login({ ...form.value })
    toast.success('Đăng nhập thành công!', 'Chào mừng bạn quay lại TopViec 👋')
  } catch (err: any) {
    const msg = err.response?.data?.message ?? 'Đăng nhập thất bại, vui lòng thử lại'
    toast.error('Đăng nhập thất bại', msg)
  } finally {
    loading.value = false
  }
}
</script>