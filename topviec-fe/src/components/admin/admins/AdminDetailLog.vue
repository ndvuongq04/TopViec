<template>
  <div class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 overflow-hidden shadow-sm">
    <!-- Header -->
    <div class="p-6 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between">
      <h3 class="text-lg font-bold flex items-center gap-2">
        <span class="material-symbols-outlined text-[#963131]">history</span>
        Nhật ký hoạt động (System Log)
      </h3>
      <button class="text-[#963131] text-sm font-semibold hover:underline">Tải dữ liệu log (.csv)</button>
    </div>

    <!-- Table -->
    <div class="overflow-x-auto">
      <table class="w-full text-left">
        <thead>
          <tr class="bg-slate-50 dark:bg-slate-800/50 text-slate-500 text-xs font-bold uppercase tracking-wider">
            <th class="px-6 py-4">Thời gian</th>
            <th class="px-6 py-4">Hành động</th>
            <th class="px-6 py-4">Đối tượng</th>
            <th class="px-6 py-4">Địa chỉ IP</th>
            <th class="px-6 py-4">Kết quả</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-100 dark:divide-slate-800">
          <tr
            v-for="(log, index) in logs"
            :key="index"
            class="hover:bg-slate-50/50 dark:hover:bg-slate-800/30 transition-colors"
          >
            <td class="px-6 py-4 text-sm text-slate-500">{{ log.time }}</td>
            <td class="px-6 py-4 text-sm font-medium" :class="log.actionClass ?? ''">{{ log.action }}</td>
            <td class="px-6 py-4 text-sm text-slate-700 dark:text-slate-300">{{ log.target }}</td>
            <td class="px-6 py-4 text-sm text-slate-500">{{ log.ip }}</td>
            <td class="px-6 py-4">
              <span
                class="flex items-center gap-1.5 font-medium text-xs px-2 py-1 rounded w-fit"
                :class="log.success
                  ? 'text-green-600 bg-green-50'
                  : 'text-amber-600 bg-amber-50'"
              >
                <span class="material-symbols-outlined text-[14px]">
                  {{ log.success ? 'check_circle' : 'warning' }}
                </span>
                {{ log.success ? 'Thành công' : log.failReason ?? 'Thất bại' }}
              </span>
            </td>
          </tr>
          <tr v-if="logs.length === 0">
            <td colspan="5" class="px-6 py-12 text-center text-slate-400 text-sm">
              Chưa có nhật ký hoạt động nào.
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Footer -->
    <div class="p-4 bg-slate-50 dark:bg-slate-800/50 border-t border-slate-200 dark:border-slate-800 flex justify-center">
      <button class="text-sm font-semibold text-slate-500 hover:text-[#963131] transition-colors flex items-center gap-2">
        Xem tất cả nhật ký
        <span class="material-symbols-outlined text-[18px]">expand_more</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
// Placeholder data — BE hiện chưa có API activity log
const logs = [
  { time: '14:30 24/05/2024', action: 'Duyệt tin tuyển dụng', target: '#123 - Tuyển Dev Fullstack', ip: '1.55.12.89', success: true },
  { time: '11:15 24/05/2024', action: 'Khóa ứng viên', actionClass: 'text-red-600', target: 'Nguyễn Văn A (ID: 5542)', ip: '1.55.12.89', success: true },
  { time: '09:45 24/05/2024', action: 'Cập nhật gói dịch vụ', target: 'Gói Pro - 12 Tháng', ip: '1.55.12.89', success: true },
  { time: '16:20 23/05/2024', action: 'Thay đổi mật khẩu', target: 'Tài khoản cá nhân', ip: '14.22.10.5', success: false, failReason: 'Thất bại (Sai OTP)' },
]
</script>
