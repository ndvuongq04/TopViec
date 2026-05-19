<template>
  <div class="min-h-screen bg-slate-50 dark:bg-slate-950 flex items-center justify-center p-6">
    <div class="max-w-md w-full text-center">
      <!-- Icon area -->
      <div class="relative mb-12">
        <div class="absolute inset-0 bg-[#963131]/10 blur-3xl rounded-full scale-150"></div>
        <div class="relative flex justify-center">
          <div class="size-32 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-2xl flex items-center justify-center relative overflow-hidden">
            <!-- Animated scanline effect -->
            <div class="absolute inset-0 bg-gradient-to-b from-transparent via-[#963131]/10 to-transparent h-1/2 w-full animate-pulse"></div>
            <span class="material-symbols-outlined text-6xl text-[#963131] select-none relative z-10">GPP_BAD</span>
          </div>
        </div>
        <div class="absolute -top-4 -right-2 transform -rotate-12">
          <div class="bg-[#963131] text-white text-[10px] font-black px-2 py-0.5 rounded shadow-sm uppercase">Access Denied</div>
        </div>
      </div>

      <!-- Text content -->
      <h1 class="text-4xl font-black text-slate-900 dark:text-slate-100 tracking-tight mb-4">
        Truy cập bị từ chối
      </h1>
      <p class="text-slate-500 dark:text-slate-400 mb-10 leading-relaxed">
        Tài khoản của bạn không có đủ quyền hạn để truy cập vào khu vực này. Vui lòng liên hệ quản trị viên nếu bạn tin rằng đây là một sự nhầm lẫn.
      </p>

      <!-- Actions -->
      <div class="flex flex-col sm:flex-row items-center justify-center gap-4">
        <router-link
          to="/"
          class="w-full sm:w-auto px-8 py-3 bg-[#963131] hover:bg-[#963131]/90 text-white font-bold rounded-xl shadow-lg shadow-[#963131]/20 transition-all flex items-center justify-center gap-2"
        >
          <span class="material-symbols-outlined text-lg">home</span>
          Về trang chủ
        </router-link>
        <button
          @click="handleLogout"
          class="w-full sm:w-auto px-8 py-3 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-slate-700 dark:text-slate-300 font-bold rounded-xl hover:bg-red-50 dark:hover:bg-red-950/20 hover:text-red-600 transition-all flex items-center justify-center gap-2"
        >
          <span class="material-symbols-outlined text-lg">logout</span>
          Đăng xuất
        </button>
      </div>

      <!-- System Log Info -->
      <div class="mt-12 p-4 rounded-xl bg-slate-100 dark:bg-slate-900/50 border border-slate-200 dark:border-slate-800">
        <div class="flex items-center justify-center gap-2 text-slate-400 text-[10px] font-bold uppercase tracking-widest">
          <span class="size-1.5 rounded-full bg-red-500 animate-ping"></span>
          Security Event Logged: #{{ Math.floor(Math.random() * 90000) + 10000 }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth.store'

const authStore = useAuthStore()

function handleLogout() {
  authStore.logout()
}
</script>
