<template>
  <div v-if="visible" class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/50 backdrop-blur-sm p-4">
    <div class="bg-white dark:bg-slate-900 rounded-xl w-full max-w-md shadow-2xl overflow-hidden border border-slate-200 dark:border-slate-800">
      
      <!-- Header -->
      <div class="px-6 py-4 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between bg-red-50 dark:bg-red-900/20">
        <div class="flex items-center gap-3">
          <div class="flex items-center justify-center w-10 h-10 rounded-full bg-red-100 text-red-600">
            <span class="material-symbols-outlined">{{ actionType === 'reject' ? 'cancel' : 'gavel' }}</span>
          </div>
          <div>
            <h3 class="text-lg font-bold text-red-900 dark:text-red-100">
              {{ actionType === 'reject' ? 'Từ chối bài đăng' : 'Gỡ bài đăng vi phạm' }}
            </h3>
            <p class="text-xs text-red-600 dark:text-red-400">
              ID: #{{ jobId }}
            </p>
          </div>
        </div>
        <button class="text-slate-400 hover:text-slate-600 rounded-full p-1 cursor-pointer" @click="$emit('close')">
          <span class="material-symbols-outlined">close</span>
        </button>
      </div>

      <!-- Body -->
      <div class="p-6 space-y-4">
        <div>
          <label class="block text-sm font-bold text-slate-700 dark:text-slate-300 mb-1">
            Lý do (hiển thị cho Nhà tuyển dụng) <span class="text-red-500">*</span>
          </label>
          <textarea
            v-model="form.rejectionReason"
            class="w-full px-4 py-2 border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 rounded-lg text-sm focus:ring-[#963131]/20 focus:border-[#963131] resize-none h-24"
            placeholder="Nhập lý do cụ thể..."
          ></textarea>
        </div>
        <div>
          <label class="block text-sm font-bold text-slate-700 dark:text-slate-300 mb-1">
            Ghi chú nội bộ (không bắt buộc)
          </label>
          <textarea
            v-model="form.moderationNote"
            class="w-full px-4 py-2 border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 rounded-lg text-sm focus:ring-[#963131]/20 focus:border-[#963131] resize-none h-20"
            placeholder="Ghi chú dành cho đội ngũ admin..."
          ></textarea>
        </div>
      </div>

      <!-- Footer -->
      <div class="px-6 py-4 bg-slate-50 dark:bg-slate-800/50 border-t border-slate-200 dark:border-slate-800 flex justify-end gap-3">
        <button
          class="px-4 py-2 text-sm font-semibold text-slate-700 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 transition-colors cursor-pointer"
          @click="$emit('close')"
          :disabled="submitting"
        >
          Hủy bỏ
        </button>
        <button
          class="px-4 py-2 text-sm font-bold text-white rounded-lg transition-colors flex items-center gap-2 bg-red-600 hover:bg-red-700 cursor-pointer"
          @click="handleSubmit"
          :disabled="submitting || !form.rejectionReason.trim()"
        >
          <span v-if="submitting" class="material-symbols-outlined animate-spin text-sm">progress_activity</span>
          Xác nhận {{ actionType === 'reject' ? 'từ chối' : 'gỡ bài' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, watch } from 'vue'
import type { ReqRejectJobPostingDTO } from '@/types/jobPosting.types'

const props = defineProps<{
  visible: boolean
  submitting: boolean
  actionType: 'reject' | 'takedown'
  jobId: number | string
}>()

const emit = defineEmits<{
  close: []
  submit: [payload: ReqRejectJobPostingDTO]
}>()

const form = reactive({
  rejectionReason: '',
  moderationNote: ''
})

watch(() => props.visible, (val) => {
  if (val) {
    form.rejectionReason = ''
    form.moderationNote = ''
  }
})

function handleSubmit() {
  if (!form.rejectionReason.trim()) return
  emit('submit', { ...form })
}
</script>
