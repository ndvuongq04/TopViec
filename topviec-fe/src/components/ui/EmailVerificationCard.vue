<script setup lang="ts">
defineProps<{
  email: string
  attempt: number
  maxAttempts: number
  canResend: boolean
  justResent: boolean
}>()

const emit = defineEmits<{
  resend: []
  goToLogin: []
}>()
</script>

<template>
  <div class="w-full max-w-md bg-white dark:bg-slate-900 rounded-xl shadow-sm border border-slate-200 dark:border-slate-800 overflow-hidden">
    <div class="p-8 text-center">

      <!-- Icon -->
      <div class="mb-8 flex justify-center">
        <div class="w-32 h-32 bg-primary/10 rounded-full flex items-center justify-center relative">
          <span class="material-symbols-outlined text-primary text-6xl">mark_email_read</span>
          <div class="absolute -bottom-1 -right-1 bg-blue-500 text-white rounded-full p-1.5 border-4 border-white dark:border-slate-900 flex items-center justify-center">
            <span class="material-symbols-outlined text-sm">schedule_send</span>
          </div>
        </div>
      </div>

      <!-- Heading -->
      <h1 class="text-2xl font-bold text-slate-900 dark:text-white mb-4">
        Kiểm tra hộp thư của bạn!
      </h1>
      <p class="text-slate-600 dark:text-slate-400 leading-relaxed mb-8">
        Chúng tôi đã gửi liên kết xác thực đến
        <span class="text-primary font-semibold">{{ email }}</span>.
        Vui lòng nhấn vào liên kết để kích hoạt tài khoản.
      </p>

      <!-- Toast gửi lại -->
      <Transition
        enter-active-class="transition duration-300 ease-out"
        enter-from-class="opacity-0 -translate-y-1"
        enter-to-class="opacity-100 translate-y-0"
        leave-active-class="transition duration-200 ease-in"
        leave-from-class="opacity-100"
        leave-to-class="opacity-0"
      >
        <div v-if="justResent" class="mb-6 flex items-center gap-2 p-3 bg-green-50 border border-green-200 rounded-xl text-green-700 text-sm font-medium">
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
          </svg>
          Email đã được gửi lại thành công!
        </div>
      </Transition>

      <!-- Resend -->
      <div class="flex flex-col gap-4 mb-4">
        <p v-if="attempt > maxAttempts" class="text-sm text-red-500 font-medium">
          Bạn đã hết lượt gửi lại. Vui lòng liên hệ hỗ trợ.
        </p>
        <button
          v-else
          :disabled="!canResend"
          class="w-full font-bold py-3.5 px-6 rounded-lg transition-all flex items-center justify-center gap-2"
          :class="canResend
            ? 'bg-primary hover:bg-primary/90 text-white shadow-md shadow-primary/20'
            : 'bg-slate-100 text-slate-400 cursor-not-allowed'"
          @click="emit('resend')"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
          </svg>
          Gửi lại email ({{ attempt }}/{{ maxAttempts }})
        </button>

        <!-- Nút đăng nhập -->
        <button
          class="w-full border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 hover:border-primary hover:text-primary text-slate-600 dark:text-slate-400 font-bold py-3.5 px-6 rounded-lg transition-all flex items-center justify-center gap-2"
          @click="emit('goToLogin')"
        >
          <span>Đã xác thực, đăng nhập ngay</span>
          <span class="material-symbols-outlined text-lg">arrow_forward</span>
        </button>

        <p class="text-xs text-slate-500 dark:text-slate-500">
          Không nhận được email? Kiểm tra thư mục spam.
        </p>
      </div>

    </div>

    <!-- Bottom -->
    <div class="bg-slate-50 dark:bg-slate-800/50 px-8 py-4 border-t border-slate-200 dark:border-slate-800">
      <p class="text-sm text-center text-slate-600 dark:text-slate-400">
        Gặp sự cố?
        <a href="#" class="text-primary font-medium hover:underline">Liên hệ hỗ trợ</a>
      </p>
    </div>
  </div>
</template>