<template>
  <div class="min-h-screen bg-slate-50 dark:bg-slate-950 flex items-center justify-center p-6">
    <div class="max-w-md w-full text-center">
      <!-- Icon/Animation area -->
      <div class="relative mb-12">
        <div class="absolute inset-0 bg-[#963131]/10 blur-3xl rounded-full scale-150"></div>
        <div class="relative flex justify-center">
          <div class="size-32 rounded-3xl bg-white dark:bg-slate-900 border border-[#963131]/20 shadow-2xl flex items-center justify-center transform rotate-12 transition-transform hover:rotate-0 duration-500">
            <span class="material-symbols-outlined text-6xl text-[#963131] select-none">search_off</span>
          </div>
        </div>
        <div class="absolute -top-4 -right-2 transform -rotate-12">
          <div class="bg-amber-400 text-slate-900 text-[10px] font-black px-2 py-0.5 rounded shadow-sm">404 ERROR</div>
        </div>
      </div>

      <!-- Text content -->
      <h1 class="text-4xl font-black text-slate-900 dark:text-slate-100 tracking-tight mb-4">
        Trang không tồn tại
      </h1>
      <p class="text-slate-500 dark:text-slate-400 mb-10 leading-relaxed">
        Có vẻ như đường dẫn bạn đang truy cập không tồn tại hoặc đã bị gỡ bỏ. Hãy kiểm tra lại URL hoặc quay lại trang chủ.
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
          @click="$router.back()"
          class="w-full sm:w-auto px-8 py-3 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-slate-700 dark:text-slate-300 font-bold rounded-xl hover:bg-slate-50 dark:hover:bg-slate-800 transition-all"
        >
          Quay lại
        </button>
      </div>

      <!-- Footer -->
      <div class="mt-16 pt-8 border-t border-slate-200 dark:border-slate-800">
        <p class="text-xs text-slate-400 font-medium uppercase tracking-widest">
          TopViec &copy; 2026 • Advanced Recruitment Platform
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// NotFoundPage - Trang 404
</script>
