<template>
  <div class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 overflow-hidden shadow-sm">
    <div class="p-6 h-full flex flex-col">
      <h3 class="text-lg font-bold mb-6 flex items-center gap-2">
        <span class="material-symbols-outlined text-[#963131]">security</span>
        Bảo mật &amp; Đăng nhập
      </h3>

      <!-- 2FA Status -->
      <div class="mb-6 p-4 rounded-lg bg-[#963131]/5 border border-[#963131]/10 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <span class="material-symbols-outlined text-[#963131]">verified_user</span>
          <span class="text-sm font-medium">Xác thực 2 lớp (2FA)</span>
        </div>
        <span class="text-xs font-bold text-[#963131] uppercase">Đã bật</span>
      </div>

      <!-- Login History -->
      <div class="flex-1">
        <p class="text-xs font-semibold text-slate-400 uppercase mb-4">Lịch sử đăng nhập gần nhất</p>
        <div class="space-y-4">
          <div
            v-for="(login, index) in loginHistory"
            :key="index"
            class="flex items-start gap-3"
          >
            <span class="material-symbols-outlined text-slate-400">{{ login.icon }}</span>
            <div class="flex-1">
              <p class="text-sm font-medium">{{ login.device }}</p>
              <div class="flex justify-between items-center text-xs text-slate-500 mt-1">
                <span>{{ login.ip }}</span>
                <span>{{ login.time }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// Placeholder data — BE hiện chưa có API login history
const loginHistory = [
  { icon: 'laptop_mac', device: 'MacBook Pro - Chrome', ip: '1.55.12.89 (Việt Nam)', time: 'Vừa xong' },
  { icon: 'smartphone', device: 'iPhone 13 - App', ip: '1.55.12.89 (Việt Nam)', time: '3 giờ trước' },
  { icon: 'desktop_windows', device: 'Windows PC - Edge', ip: '14.22.10.5 (Việt Nam)', time: 'Hôm qua, 14:20' },
]
</script>
