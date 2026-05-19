<script setup lang="ts">
defineProps<{
  status: 'loading' | 'success' | 'error'
  errorMessage: string
}>()

defineEmits<{ goToLogin: [] }>()
</script>

<template>
  <div class="w-full max-w-md bg-white dark:bg-slate-900 rounded-xl shadow-sm border border-slate-200 dark:border-slate-800 overflow-hidden">
    <div class="p-8 text-center">

      <!-- LOADING -->
      <template v-if="status === 'loading'">
        <div class="mb-8 flex justify-center">
          <div class="w-32 h-32 bg-primary/10 rounded-full flex items-center justify-center">
            <svg class="w-16 h-16 text-primary animate-spin" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" />
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z" />
            </svg>
          </div>
        </div>
        <h1 class="text-2xl font-bold text-slate-900 dark:text-white mb-4">Đang xác thực...</h1>
        <p class="text-slate-600 dark:text-slate-400 leading-relaxed">
          Vui lòng đợi trong giây lát.
        </p>
      </template>

      <!-- SUCCESS -->
      <template v-else-if="status === 'success'">
        <div class="mb-8 flex justify-center">
          <div class="w-32 h-32 bg-primary/10 rounded-full flex items-center justify-center relative">
            <span class="material-symbols-outlined text-primary text-6xl">mark_email_read</span>
            <div class="absolute -bottom-1 -right-1 bg-green-500 text-white rounded-full p-1.5 border-4 border-white dark:border-slate-900 flex items-center justify-center">
              <span class="material-symbols-outlined text-sm">check</span>
            </div>
          </div>
        </div>
        <h1 class="text-2xl font-bold text-slate-900 dark:text-white mb-4">Xác thực thành công!</h1>
        <p class="text-slate-600 dark:text-slate-400 leading-relaxed mb-8">
          Chào mừng bạn đến với <span class="text-primary font-semibold">TopViec</span>!
          Tài khoản của bạn đã được kích hoạt. Bạn có thể đăng nhập ngay bây giờ.
        </p>
        <div class="flex flex-col gap-4">
          <button
            class="w-full bg-primary hover:bg-primary/90 text-white font-bold py-3.5 px-6 rounded-lg transition-all shadow-md shadow-primary/20 flex items-center justify-center gap-2"
            @click="$emit('goToLogin')"
          >
            <span>Đăng nhập ngay</span>
            <span class="material-symbols-outlined text-lg">arrow_forward</span>
          </button>
          <p class="text-xs text-slate-500 dark:text-slate-500">
            Bạn sẽ được chuyển đến trang đăng nhập sau 5 giây.
          </p>
        </div>
      </template>

      <!-- ERROR -->
      <template v-else>
        <div class="mb-8 flex justify-center">
          <div class="w-32 h-32 bg-red-50 rounded-full flex items-center justify-center relative">
            <span class="material-symbols-outlined text-red-400 text-6xl">mark_email_unread</span>
            <div class="absolute -bottom-1 -right-1 bg-red-500 text-white rounded-full p-1.5 border-4 border-white dark:border-slate-900 flex items-center justify-center">
              <span class="material-symbols-outlined text-sm">close</span>
            </div>
          </div>
        </div>
        <h1 class="text-2xl font-bold text-slate-900 dark:text-white mb-4">Xác thực thất bại</h1>
        <p class="text-slate-600 dark:text-slate-400 leading-relaxed mb-8">
          {{ errorMessage }}
        </p>
        <div class="flex flex-col gap-4">
          <button
            class="w-full bg-primary hover:bg-primary/90 text-white font-bold py-3.5 px-6 rounded-lg transition-all shadow-md shadow-primary/20 flex items-center justify-center gap-2"
            @click="$emit('goToLogin')"
          >
            <span>Quay lại đăng nhập</span>
            <span class="material-symbols-outlined text-lg">arrow_forward</span>
          </button>
        </div>
      </template>

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