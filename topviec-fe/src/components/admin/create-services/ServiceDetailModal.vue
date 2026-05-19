<template>
  <div v-if="visible" class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-sm p-4">
    <div class="bg-white rounded-xl shadow-2xl w-full max-w-lg overflow-hidden animate-in fade-in zoom-in-95 duration-200">
      
      <!-- Header -->
      <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
        <h3 class="text-lg font-bold text-slate-900 tracking-tight">Chi tiết dịch vụ</h3>
        <button class="text-slate-400 hover:text-slate-600 transition-colors cursor-pointer" @click="$emit('close')">
          <span class="material-symbols-outlined">close</span>
        </button>
      </div>

      <!-- Body -->
      <div v-if="loading" class="p-12 flex flex-col items-center justify-center text-slate-500">
        <span class="material-symbols-outlined animate-spin text-3xl mb-2">progress_activity</span>
        <span class="text-sm">Đang tải dữ liệu...</span>
      </div>
      
      <div v-else-if="service" class="p-6 space-y-4">
        <div class="flex flex-col gap-1">
          <span class="text-xs font-bold text-slate-400 uppercase tracking-widest">Mã dịch vụ</span>
          <span class="text-sm font-semibold text-slate-900 border border-slate-100 bg-slate-50 rounded-lg px-3 py-2 font-mono">{{ service.code }}</span>
        </div>

        <div class="flex flex-col gap-1">
          <span class="text-xs font-bold text-slate-400 uppercase tracking-widest">Tên dịch vụ</span>
          <span class="text-sm font-semibold text-slate-900">{{ service.name }}</span>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div class="flex flex-col gap-1">
            <span class="text-xs font-bold text-slate-400 uppercase tracking-widest">Nhóm dịch vụ</span>
            <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-sm font-medium bg-[#76191d]/5 text-[#76191d] w-fit">
              {{ service.categoryName || service.category || 'Không xác định' }}
            </span>
          </div>
          <div class="flex flex-col gap-1">
            <span class="text-xs font-bold text-slate-400 uppercase tracking-widest">Trạng thái</span>
            <span class="inline-flex px-2.5 py-1 rounded-md text-sm font-bold w-fit" :class="service.isActive ? 'bg-emerald-50 text-emerald-700' : 'bg-slate-100 text-slate-500'">
              {{ service.isActive ? 'Đang bật' : 'Đã tắt' }}
            </span>
          </div>
        </div>

        <div class="flex flex-col gap-1">
          <span class="text-xs font-bold text-slate-400 uppercase tracking-widest">Đơn vị</span>
          <span class="text-sm text-slate-800">{{ service.unit || 'Không xác định' }}</span>
        </div>

        <div class="flex flex-col gap-1 mt-2">
          <span class="text-xs font-bold text-slate-400 uppercase tracking-widest">Mô tả chi tiết</span>
          <p class="text-sm text-slate-600 bg-slate-50 p-3 rounded-lg border border-slate-100 min-h-[60px] whitespace-pre-wrap">
            {{ service.description || 'Chưa có mô tả' }}
          </p>
        </div>
        
        <div class="text-xs text-slate-400 flex justify-between mt-4 pb-2 border-b border-dashed border-slate-200">
          <span>Ngày tạo: {{ formatDate(service.createdAt) }}</span>
          <span>Cập nhật: {{ formatDate(service.updatedAt) }}</span>
        </div>
      </div>

      <!-- Footer -->
      <div class="px-6 py-4 border-t border-slate-100 bg-slate-50 flex items-center justify-end">
        <button
          class="px-5 py-2 text-sm font-semibold text-slate-600 bg-white border border-slate-200 rounded-lg hover:bg-slate-50 transition-colors cursor-pointer"
          @click="$emit('close')"
        >
          Đóng
        </button>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import type { ResServiceDTO } from '@/types/serviceCatalog.types'

defineProps<{
  visible: boolean
  service: ResServiceDTO | null
  loading: boolean
}>()

defineEmits<{ close: [] }>()

function formatDate(dateStr?: string) {
  if (!dateStr) return '---'
  const d = new Date(dateStr)
  return d.toLocaleDateString('vi-VN', {
    hour: '2-digit', minute: '2-digit',
    day: '2-digit', month: '2-digit', year: 'numeric'
  })
}
</script>
