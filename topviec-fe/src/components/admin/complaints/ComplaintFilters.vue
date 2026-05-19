<template>
  <div class="bg-white rounded-xl shadow-sm border border-[#963131]/5 overflow-hidden">

    <!-- Tabs -->
    <div class="flex border-b border-[#963131]/5 overflow-x-auto">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        class="px-6 py-4 text-sm font-medium transition-colors whitespace-nowrap cursor-pointer"
        :class="activeTab === tab.key
          ? 'font-bold border-b-2 border-[#963131] text-[#963131] bg-[#963131]/5'
          : 'text-slate-500 hover:text-[#963131]'"
        @click="onTabClick(tab.key)"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- Search + Filters -->
    <div class="p-4 flex flex-wrap gap-3 items-center">

      <!-- Search (Enter để tìm) -->
      <div class="flex-1 min-w-64 flex items-center bg-slate-50 rounded-lg px-4 border border-[#963131]/5 focus-within:ring-2 focus-within:ring-[#963131]/20 transition-all">
        <span class="material-symbols-outlined text-slate-400 text-xl">search</span>
        <input
          v-model="search"
          type="text"
          class="bg-transparent border-none focus:ring-0 text-sm w-full py-3 outline-none ml-2"
          placeholder="Tìm theo mã, người báo cáo, công ty... (Enter để tìm)"
          @keyup.enter="emitFilter"
        />
      </div>

      <!-- Loại vi phạm -->
      <select
        v-model="complaintType"
        class="shrink-0 bg-slate-50 border border-[#963131]/5 rounded-lg text-sm pl-4 pr-8 py-2.5 focus:ring-2 focus:ring-[#963131]/20 outline-none cursor-pointer text-slate-700"
        @change="emitFilter"
      >
        <option value="">Loại vi phạm: Tất cả</option>
        <option value="fraudulent">Lừa đảo</option>
        <option value="spam">Spam / Trùng lặp</option>
        <option value="wrong_info">Thông tin sai lệch</option>
        <option value="inappropriate">Nội dung không phù hợp</option>
        <option value="payment_issue">Yêu cầu phí bất hợp lý</option>
        <option value="other">Khác</option>
      </select>

      <!-- Nhóm -->
      <select
        v-model="group"
        class="shrink-0 bg-slate-50 border border-[#963131]/5 rounded-lg text-sm pl-4 pr-8 py-2.5 focus:ring-2 focus:ring-[#963131]/20 outline-none cursor-pointer text-slate-700"
        @change="emitFilter"
      >
        <option value="">Nhóm: Tất cả</option>
        <option value="A">Nhóm A (vi phạm nhẹ)</option>
        <option value="B">Nhóm B (vi phạm nặng)</option>
      </select>

      <!-- Trạng thái -->
      <select
        v-model="status"
        class="shrink-0 bg-slate-50 border border-[#963131]/5 rounded-lg text-sm pl-4 pr-8 py-2.5 focus:ring-2 focus:ring-[#963131]/20 outline-none cursor-pointer text-slate-700"
        @change="emitFilter"
      >
        <option value="">Trạng thái: Tất cả</option>
        <option value="pending">Chờ xử lý</option>
        <option value="processing">Đang xử lý</option>
        <option value="waiting_employer">Chờ NTD phản hồi</option>
        <option value="resolved">Đã giải quyết</option>
        <option value="rejected">Từ chối</option>
        <option value="auto_closed">Tự động đóng</option>
      </select>

      <!-- Date range -->
      <div class="flex items-center gap-2 bg-slate-50 border border-[#963131]/5 rounded-lg px-4 py-2 focus-within:ring-2 focus-within:ring-[#963131]/20 transition-all">
        <span class="text-xs font-bold text-slate-500 uppercase tracking-wide">Từ</span>
        <input v-model="fromDate" type="date" class="bg-transparent border-none text-sm text-slate-700 outline-none" />
        <span class="text-xs font-bold text-slate-500 uppercase tracking-wide">Đến</span>
        <input v-model="toDate" type="date" class="bg-transparent border-none text-sm text-slate-700 outline-none" />
      </div>

      <!-- Lọc (chỉ áp dụng date range) -->
      <button
        class="shrink-0 bg-[#963131] hover:bg-[#963131]/90 text-white px-5 py-2.5 rounded-lg text-sm font-bold transition-all shadow-sm cursor-pointer"
        @click="emitFilter"
      >
        Lọc
      </button>

      <!-- Xóa (chỉ xóa date range) -->
      <button
        class="shrink-0 text-sm font-medium text-slate-500 hover:text-[#963131] transition-colors cursor-pointer"
        @click="clearDateRange"
      >
        Xoá bộ lọc
      </button>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { ReqGetAdminReports } from '@/types/report.types'

type FilterParams = Omit<ReqGetAdminReports, 'page' | 'size' | 'sort'>

const emit = defineEmits<{ filter: [FilterParams] }>()

const search        = ref('')
const complaintType = ref('')
const group         = ref('')
const status        = ref('')
const fromDate      = ref('')
const toDate        = ref('')
const activeTab     = ref('all')

const tabs = [
  { key: 'all',      label: 'Tất cả' },
  { key: 'group-b',  label: 'Nhóm B (nặng)' },
  { key: 'group-a',  label: 'Nhóm A (nhẹ)' },
  { key: 'pending',  label: 'Chờ xử lý' },
  { key: 'resolved', label: 'Đã giải quyết' },
]

function onTabClick(key: string) {
  activeTab.value = key
  group.value  = ''
  status.value = ''

  if (key === 'group-a')  group.value  = 'A'
  if (key === 'group-b')  group.value  = 'B'
  if (key === 'pending')  status.value = 'pending'
  if (key === 'resolved') status.value = 'resolved'

  emitFilter()
}

function emitFilter() {
  const params: FilterParams = {}
  if (search.value)        params.search        = search.value
  if (complaintType.value) params.complaintType = complaintType.value as any
  if (group.value)         params.group         = group.value as any
  if (status.value)        params.status        = status.value as any
  if (fromDate.value)      params.fromDate      = fromDate.value
  if (toDate.value)        params.toDate        = toDate.value
  emit('filter', params)
}

function clearDateRange() {
  fromDate.value = ''
  toDate.value   = ''
  emitFilter()
}
</script>
