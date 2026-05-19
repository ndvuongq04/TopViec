<template>
  <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">

    <!-- Document Viewer (2/3) -->
    <div class="lg:col-span-2 space-y-4">
      <div class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 overflow-hidden">
        <div class="p-4 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between">
          <h3 class="font-bold flex items-center gap-2">
            <span class="material-symbols-outlined text-slate-400">task</span>
            Giấy phép kinh doanh.pdf
          </h3>
          <div class="flex gap-2">
            <button class="p-1.5 rounded bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 transition-colors">
              <span class="material-symbols-outlined text-sm">zoom_in</span>
            </button>
            <button class="p-1.5 rounded bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 transition-colors">
              <span class="material-symbols-outlined text-sm">download</span>
            </button>
          </div>
        </div>
        <!-- Simulated PDF viewer -->
        <div class="bg-slate-100 dark:bg-slate-950 p-8 min-h-[500px] flex items-center justify-center">
          <div class="w-full max-w-md aspect-[1/1.4] bg-white shadow-2xl rounded-sm p-8 border border-slate-200">
            <div class="w-full h-full border-2 border-slate-200 p-6 flex flex-col items-center gap-4 text-slate-900">
              <div class="text-center">
                <p class="text-[10px] font-bold uppercase">Cộng hòa xã hội chủ nghĩa Việt Nam</p>
                <p class="text-[8px] italic">Độc lập - Tự do - Hạnh phúc</p>
              </div>
              <div class="w-12 h-0.5 bg-slate-300"></div>
              <h4 class="text-sm font-bold mt-4">GIẤY CHỨNG NHẬN ĐĂNG KÝ DOANH NGHIỆP</h4>
              <p class="text-[10px]">Mã số doanh nghiệp: {{ company.taxCode || '0102030405' }}</p>
              <div class="w-full mt-6 space-y-4">
                <div class="h-2 bg-slate-100 w-3/4"></div>
                <div class="h-2 bg-slate-100 w-full"></div>
                <div class="h-2 bg-slate-100 w-5/6"></div>
                <div class="h-2 bg-slate-100 w-2/3 pt-8"></div>
                <div class="h-2 bg-slate-100 w-full"></div>
              </div>
              <div class="mt-auto w-full flex justify-end">
                <div class="w-16 h-16 rounded-full border-4 border-red-200 flex items-center justify-center opacity-30 rotate-12">
                  <div class="w-12 h-12 rounded-full border-2 border-red-200"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Approval Actions Panel (1/3) -->
    <div class="space-y-6">

      <!-- Approval Card -->
      <div v-if="can('company.update')" class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6 shadow-sm">
        <h3 class="text-lg font-bold mb-4">Phê duyệt yêu cầu</h3>
        <p class="text-sm text-slate-500 mb-6">Xác nhận tính hợp lệ của giấy phép kinh doanh để cho phép NTT hoạt động đầy đủ tính năng.</p>
        <div class="space-y-4">
          <button
            class="w-full py-3 bg-[#963131] text-white rounded-lg font-bold flex items-center justify-center gap-2 hover:bg-[#963131]/90 transition-opacity shadow-lg shadow-[#963131]/20 disabled:opacity-50 disabled:cursor-not-allowed"
            :disabled="!isActionable"
            @click="$emit('approve')"
          >
            <span class="material-symbols-outlined">check_circle</span>
            Phê duyệt ngay
          </button>
          <div class="pt-4 border-t border-slate-100 dark:border-slate-800">
            <label class="block text-sm font-bold text-slate-700 dark:text-slate-300 mb-2">Lý do từ chối (nếu có)</label>
            <textarea
              v-model="rejectionReason"
              class="w-full rounded-lg border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 text-sm focus:ring-[#963131] focus:border-[#963131] disabled:opacity-50 disabled:cursor-not-allowed"
              placeholder="Nhập lý do hoặc hướng dẫn bổ sung cho NTT..."
              rows="3"
              :disabled="!isActionable"
            ></textarea>
          </div>
          <div class="grid grid-cols-2 gap-3">
            <button
              class="py-2.5 px-4 bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-200 rounded-lg font-bold text-sm hover:bg-slate-200 flex items-center justify-center gap-2 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              :disabled="!isActionable"
              @click="$emit('request-supplement')"
            >
              <span class="material-symbols-outlined text-base">edit_note</span>
              Yêu cầu bổ sung
            </button>
            <button
              class="py-2.5 px-4 bg-red-50 dark:bg-red-900/10 text-red-600 rounded-lg font-bold text-sm hover:bg-red-100 flex items-center justify-center gap-2 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              :disabled="!isActionable"
              @click="$emit('reject', rejectionReason)"
            >
              <span class="material-symbols-outlined text-base">cancel</span>
              Từ chối
            </button>
          </div>
        </div>
      </div>
      <!-- Thông báo không có quyền -->
      <div v-else class="bg-slate-50 dark:bg-slate-800/50 rounded-xl border border-dashed border-slate-300 dark:border-slate-700 p-6 text-center">
        <span class="material-symbols-outlined text-slate-300 text-4xl block mb-2">lock</span>
        <p class="text-sm text-slate-400">Bạn không có quyền phê duyệt / từ chối.</p>
      </div>

      <!-- Metadata -->
      <div class="bg-slate-50 dark:bg-slate-800/50 rounded-xl p-4 border border-dashed border-slate-300 dark:border-slate-700">
        <h4 class="text-xs font-bold text-slate-400 uppercase tracking-widest mb-3">Thông tin hồ sơ</h4>
        <ul class="space-y-2">
          <li class="flex justify-between text-sm">
            <span class="text-slate-500">Ngày tải lên:</span>
            <span class="font-medium">12/10/2023 14:30</span>
          </li>
          <li class="flex justify-between text-sm">
            <span class="text-slate-500">Kích thước:</span>
            <span class="font-medium">2.4 MB</span>
          </li>
          <li class="flex justify-between text-sm">
            <span class="text-slate-500">Định dạng:</span>
            <span class="font-medium">PDF (Digital Scan)</span>
          </li>
          <li class="flex justify-between text-sm">
            <span class="text-slate-500">Người phụ trách:</span>
            <span class="text-[#963131] font-bold">Admin_NguyenVanA</span>
          </li>
        </ul>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { ResCompanyDTO } from '@/types/company.types'
import { VerificationStatus } from '@/constants/company.constants'
import { useAdminPermission } from '@/composables/useAdminPermission'

const { can } = useAdminPermission()

const props = defineProps<{
  company: ResCompanyDTO
}>()

const emit = defineEmits<{
  'approve': []
  'reject': [reason: string]
  'request-supplement': []
}>()

const rejectionReason = ref('')

const isActionable = computed(() => {
  return props.company?.verificationStatus === VerificationStatus.PENDING
})
</script>
