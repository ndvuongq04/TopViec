<template>
  <div class="bg-white dark:bg-slate-900 rounded-xl shadow-sm border border-[#963131]/10 p-6">
    <h3 class="text-lg font-bold mb-6 flex items-center gap-2">
      <span class="material-symbols-outlined text-[#963131]">security</span>
      Bảo mật &amp; Tài khoản
    </h3>

    <!-- 2FA Toggle -->
    <div class="flex flex-col md:flex-row justify-between items-start md:items-center p-4 bg-[#963131]/5 rounded-lg mb-4">
      <div>
        <h4 class="text-sm font-bold">Xác thực 2 yếu tố (2FA)</h4>
        <p class="text-xs text-slate-600">Tăng cường bảo mật tài khoản bằng mã OTP.</p>
      </div>
      <div class="mt-4 md:mt-0 flex items-center">
        <label class="inline-flex items-center cursor-pointer">
          <input checked type="checkbox" class="sr-only peer" />
          <div class="relative w-11 h-6 bg-slate-200 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-[#963131]"></div>
        </label>
      </div>
    </div>

    <!-- Reset Password -->
    <div class="flex flex-col md:flex-row justify-between items-start md:items-center p-4 border border-slate-100 rounded-lg">
      <div>
        <h4 class="text-sm font-bold">Đặt lại mật khẩu</h4>
        <p class="text-xs text-slate-600">Gửi email đặt lại mật khẩu cho tài khoản này.</p>
      </div>
      <button class="mt-4 md:mt-0 px-4 py-2 border border-[#963131] text-[#963131] rounded-lg text-xs font-bold hover:bg-[#963131] hover:text-white transition-colors">
        Reset Password
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
// Security settings — placeholder UI
// BE chưa có API 2FA / reset password cho admin
</script>
