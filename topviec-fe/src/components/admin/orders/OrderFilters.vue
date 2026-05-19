<template>
  <div class="bg-white dark:bg-slate-900 rounded-xl shadow-sm border border-[#963131]/5 overflow-hidden">

    <!-- Tabs -->
    <div class="flex border-b border-[#963131]/5 overflow-x-auto">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        class="px-6 py-4 text-sm font-medium transition-colors whitespace-nowrap flex items-center gap-2 cursor-pointer"
        :class="activeTab === tab.key
          ? 'font-bold border-b-2 border-[#963131] text-[#963131] bg-[#963131]/5'
          : 'text-slate-500 hover:text-[#963131]'"
        @click="onTabClick(tab.key)"
      >
        {{ tab.label }}
        <span
          v-if="tab.badge"
          class="text-xs font-black px-1.5 py-0.5 rounded-full bg-[#963131] text-white"
        >{{ tab.badge }}</span>
      </button>
    </div>

    <!-- Search + Filters -->
    <div class="p-4 flex flex-wrap gap-3 items-center">

      <!-- Search (chỉ gọi API khi nhấn Enter) -->
      <div class="flex-1 min-w-64 flex items-center bg-slate-50 dark:bg-white/5 rounded-lg px-4 border border-[#963131]/5 focus-within:ring-2 focus-within:ring-[#963131]/20 transition-all">
        <span class="material-symbols-outlined text-slate-400 text-xl">search</span>
        <input
          v-model="search"
          type="text"
          class="bg-transparent border-none focus:ring-0 text-sm w-full py-3 outline-none ml-2"
          placeholder="Tìm tên công ty, mã đơn hàng... (Enter để tìm)"
          @keyup.enter="emitFilter"
        />
      </div>

      <!-- Loại đơn -->
      <select
        v-model="orderType"
        class="shrink-0 bg-slate-50 dark:bg-white/5 border border-[#963131]/5 rounded-lg text-sm pl-4 pr-8 py-2.5 focus:ring-2 focus:ring-[#963131]/20 outline-none cursor-pointer text-slate-700 dark:text-slate-300"
      >
        <option value="">Loại đơn: Tất cả</option>
        <option :value="OrderType.SUBSCRIPTION">Gói đăng ký</option>
        <option :value="OrderType.ADDON">Dịch vụ lẻ</option>
      </select>

      <!-- Trạng thái -->
      <select
        v-model="status"
        class="shrink-0 bg-slate-50 dark:bg-white/5 border border-[#963131]/5 rounded-lg text-sm pl-4 pr-8 py-2.5 focus:ring-2 focus:ring-[#963131]/20 outline-none cursor-pointer text-slate-700 dark:text-slate-300"
      >
        <option value="">Trạng thái: Tất cả</option>
        <option :value="OrderStatus.PAID">Đã thanh toán</option>
        <option :value="OrderStatus.PENDING">Chờ thanh toán</option>
        <option :value="OrderStatus.FAILED">Thất bại</option>
        <option :value="OrderStatus.CANCELLED">Đã huỷ</option>
        <option :value="OrderStatus.REFUNDED">Đã hoàn tiền</option>
      </select>

      <!-- Date range -->
      <div class="flex items-center gap-2 bg-slate-50 dark:bg-white/5 border border-[#963131]/5 rounded-lg px-4 py-2 focus-within:ring-2 focus-within:ring-[#963131]/20 transition-all">
        <span class="text-xs font-bold text-slate-500 uppercase tracking-wide">Từ</span>
        <input v-model="startDate" type="date" class="bg-transparent border-none text-sm text-slate-700 outline-none" />
        <span class="text-xs font-bold text-slate-500 uppercase tracking-wide">Đến</span>
        <input v-model="endDate" type="date" class="bg-transparent border-none text-sm text-slate-700 outline-none" />
      </div>

      <!-- Actions -->
      <button
        class="shrink-0 bg-[#963131] hover:bg-[#963131]/90 text-white px-5 py-2.5 rounded-lg text-sm font-bold transition-all shadow-sm cursor-pointer"
        @click="emitFilter"
      >
        Lọc
      </button>
      <button
        class="shrink-0 text-sm font-medium text-slate-500 hover:text-[#963131] transition-colors cursor-pointer"
        @click="clearFilters"
      >
        Xoá bộ lọc
      </button>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { OrderStatus, OrderType } from '@/constants/servicePackage.constants'
import type { AdminOrderQueryParams } from '@/types/order.types'

const search    = ref('')
const orderType = ref<OrderType | ''>('')
const status    = ref<OrderStatus | ''>('')
const startDate = ref('')
const endDate   = ref('')
const activeTab = ref('all')

// dateFilter và failedOrPending được điều khiển bởi tab
const dateFilter      = ref<string | undefined>(undefined)
const failedOrPending = ref<boolean | undefined>(undefined)

const tabs: { key: string; label: string; badge?: number }[] = [
  { key: 'all',     label: 'Tất cả' },
  { key: 'today',   label: 'Hôm nay' },
  { key: '7days',   label: '7 ngày qua' },
  { key: 'month',   label: 'Tháng này' },
  { key: 'pending', label: 'Chờ xử lý' },
  { key: 'failed',  label: 'Thất bại' },
]

const emit = defineEmits<{
  filter: [params: AdminOrderQueryParams]
}>()

function onTabClick(key: string) {
  activeTab.value = key
  // Reset tab-driven params
  dateFilter.value      = undefined
  failedOrPending.value = undefined

  switch (key) {
    case 'today':   dateFilter.value = 'today';  break
    case '7days':   dateFilter.value = '7days';  break
    case 'month':   dateFilter.value = 'month';  break
    case 'pending': failedOrPending.value = true; break
    case 'failed':  status.value = OrderStatus.FAILED; break
  }

  emitFilter()
}

function emitFilter() {
  const params: AdminOrderQueryParams = {}
  if (search.value)       params.search          = search.value
  if (orderType.value)    params.type            = orderType.value.toUpperCase()
  if (status.value)       params.status          = status.value.toUpperCase()
  if (dateFilter.value)   params.dateFilter      = dateFilter.value
  if (startDate.value)    params.startDate       = startDate.value
  if (endDate.value)      params.endDate         = endDate.value
  if (failedOrPending.value !== undefined) params.failedOrPending = failedOrPending.value
  emit('filter', params)
}

function clearFilters() {
  search.value          = ''
  orderType.value       = ''
  status.value          = ''
  startDate.value       = ''
  endDate.value         = ''
  activeTab.value       = 'all'
  dateFilter.value      = undefined
  failedOrPending.value = undefined
  emit('filter', {})
}
</script>
