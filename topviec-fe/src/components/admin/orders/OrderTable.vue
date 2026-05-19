<template>
  <div>
    <!-- Loading -->
    <div v-if="loading" class="flex items-center justify-center gap-3 py-20 text-slate-400">
      <span class="material-symbols-outlined animate-spin">progress_activity</span>
      <span class="text-sm font-medium">Đang tải dữ liệu...</span>
    </div>

    <!-- Empty -->
    <div v-else-if="!orders.length" class="flex flex-col items-center justify-center py-20 text-slate-400 gap-3">
      <span class="material-symbols-outlined text-4xl">inbox</span>
      <p class="text-sm font-medium">Không có đơn hàng nào</p>
    </div>

    <template v-else>
      <div class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="bg-slate-50/50 dark:bg-slate-800/50 border-b border-slate-200 dark:border-slate-800">
              <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider whitespace-nowrap">Mã đơn</th>
              <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider whitespace-nowrap">Công ty</th>
              <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider whitespace-nowrap">Loại</th>
              <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">Chi tiết</th>
              <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider whitespace-nowrap">Thanh toán</th>
              <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-right whitespace-nowrap">Giá trị</th>
              <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider whitespace-nowrap">Ngày tạo</th>
              <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider whitespace-nowrap">Trạng thái</th>
              <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-center whitespace-nowrap">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-200 dark:divide-slate-800">
            <tr
              v-for="order in orders"
              :key="order.id"
              class="hover:bg-slate-50 dark:hover:bg-slate-800/40 transition-colors group"
            >
              <!-- Mã đơn -->
              <td class="px-6 py-4 text-sm font-bold text-[#963131] whitespace-nowrap">
                {{ order.orderCode }}
              </td>

              <!-- Công ty -->
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center gap-2.5">
                  <img
                    v-if="order.company?.logoUrl"
                    :src="order.company.logoUrl"
                    :alt="order.company.name"
                    class="size-7 rounded-md object-contain border border-slate-100 bg-white shrink-0"
                  />
                  <div
                    v-else
                    class="size-7 rounded-md bg-slate-100 flex items-center justify-center shrink-0"
                  >
                    <span class="material-symbols-outlined text-slate-400" style="font-size:16px">business</span>
                  </div>
                  <span class="text-sm font-medium text-slate-700 dark:text-slate-300 max-w-40 truncate">
                    {{ order.company?.name ?? '—' }}
                  </span>
                </div>
              </td>

              <!-- Loại -->
              <td class="px-6 py-4">
                <span
                  class="inline-block px-3 py-1 rounded-full text-xs font-bold whitespace-nowrap"
                  :class="order.type === OrderType.SUBSCRIPTION
                    ? 'bg-emerald-50 text-emerald-700'
                    : 'bg-violet-50 text-violet-700'"
                >{{ ORDER_TYPE_LABELS[order.type] }}</span>
              </td>

              <!-- Chi tiết -->
              <td class="px-6 py-4 whitespace-nowrap">
                <p class="text-sm font-medium text-slate-700 dark:text-slate-300">{{ getDetailName(order) }}</p>
                <p v-if="order.note" class="text-xs text-slate-400 mt-0.5">{{ order.note }}</p>
              </td>

              <!-- Thanh toán -->
              <td class="px-6 py-4 text-sm text-slate-500 whitespace-nowrap">
                {{ PAYMENT_METHOD_LABELS[order.paymentMethod] }}
              </td>

              <!-- Giá trị -->
              <td class="px-6 py-4 text-sm font-bold text-slate-900 dark:text-slate-100 text-right whitespace-nowrap">
                {{ formatCurrency(order.totalAmount) }}
              </td>

              <!-- Ngày tạo -->
              <td class="px-6 py-4 text-sm text-slate-500 whitespace-nowrap">
                {{ formatDateTime(order.createdAt) }}
              </td>

              <!-- Trạng thái -->
              <td class="px-6 py-4">
                <span
                  class="inline-block px-3 py-1 rounded-full text-xs font-bold whitespace-nowrap"
                  :class="statusClass(order.status)"
                >{{ ORDER_STATUS_LABELS[order.status] }}</span>
              </td>

              <!-- Thao tác -->
              <td class="px-6 py-4">
                <div class="flex items-center justify-center gap-1">
                  <button
                    class="p-1.5 text-slate-400 hover:text-[#963131] transition-colors cursor-pointer"
                    title="Xem chi tiết"
                    @click="router.push({ name: 'admin-order-detail', params: { id: order.id } })"
                  >
                    <span class="material-symbols-outlined text-lg">visibility</span>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div class="p-6 border-t border-slate-200 dark:border-slate-800 flex items-center justify-between">
        <p class="text-xs text-slate-500 font-medium">
          Hiển thị
          <span class="text-slate-900 dark:text-white font-bold">{{ rangeStart }}–{{ rangeEnd }}</span>
          trong số
          <span class="text-slate-900 dark:text-white font-bold">{{ meta.totals.toLocaleString() }}</span> đơn
        </p>
        <div class="flex items-center gap-2">
          <!-- Prev -->
          <button
            class="size-8 flex items-center justify-center rounded-lg border border-slate-200 dark:border-slate-700 text-slate-400 hover:bg-slate-50 disabled:opacity-30 cursor-pointer"
            :disabled="meta.page === 0"
            @click="$emit('page-change', meta.page - 1)"
          >
            <span class="material-symbols-outlined text-xl leading-none">chevron_left</span>
          </button>

          <!-- Page numbers -->
          <template v-for="p in visiblePages" :key="p">
            <span v-if="p === '...'" class="px-1 text-slate-400 text-sm">...</span>
            <button
              v-else
              class="size-8 flex items-center justify-center rounded-lg text-xs font-bold transition-colors cursor-pointer"
              :class="meta.page === (p as number)
                ? 'bg-[#963131] text-white shadow-sm'
                : 'border border-slate-200 dark:border-slate-700 text-slate-600 hover:bg-slate-50'"
              @click="$emit('page-change', p as number)"
            >{{ (p as number) + 1 }}</button>
          </template>

          <!-- Next -->
          <button
            class="size-8 flex items-center justify-center rounded-lg border border-slate-200 dark:border-slate-700 text-slate-400 hover:bg-slate-50 disabled:opacity-30 cursor-pointer"
            :disabled="meta.page >= meta.pages - 1"
            @click="$emit('page-change', meta.page + 1)"
          >
            <span class="material-symbols-outlined text-xl leading-none">chevron_right</span>
          </button>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import type { ResOrderDTO } from '@/types/order.types'
import type { PaginationMeta } from '@/types/common.types'
import {
  OrderType,
  OrderStatus,
  ORDER_TYPE_LABELS,
  ORDER_STATUS_LABELS,
  PAYMENT_METHOD_LABELS,
  BILLING_CYCLE_LABELS,
} from '@/constants/servicePackage.constants'

const props = defineProps<{
  orders:  ResOrderDTO[]
  meta:    PaginationMeta
  loading: boolean
}>()

defineEmits<{ 'page-change': [page: number] }>()

const router = useRouter()

// ─── Helpers ──────────────────────────────────────────────────────────────────

function getDetailName(order: ResOrderDTO): string {
  const first = order.items?.[0]
  if (!first) return ORDER_TYPE_LABELS[order.type]
  if (first.billingCycle) return `${ORDER_TYPE_LABELS[order.type]} · ${BILLING_CYCLE_LABELS[first.billingCycle]}`
  if (first.quantity > 1) return `${ORDER_TYPE_LABELS[order.type]} × ${first.quantity}`
  return ORDER_TYPE_LABELS[order.type]
}

function formatCurrency(amount: number): string {
  return amount.toLocaleString('vi-VN') + ' đ'
}

function formatDateTime(iso: string): string {
  const d = new Date(iso)
  const date = `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
  const time = `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
  return `${date} ${time}`
}

function statusClass(status: OrderStatus) {
  return {
    [OrderStatus.PAID]:      'bg-emerald-50 text-emerald-700',
    [OrderStatus.PENDING]:   'bg-amber-50 text-amber-700',
    [OrderStatus.FAILED]:    'bg-red-50 text-red-700',
    [OrderStatus.CANCELLED]: 'bg-slate-100 text-slate-600',
    [OrderStatus.REFUNDED]:  'bg-slate-100 text-slate-600',
  }[status]
}

// ─── Pagination (0-based, per skill-Pagination.md) ────────────────────────────

const rangeStart = computed(() =>
  props.meta.totals === 0 ? 0 : props.meta.page * props.meta.pageSize + 1
)
const rangeEnd = computed(() =>
  Math.min((props.meta.page + 1) * props.meta.pageSize, props.meta.totals)
)

// Hiển thị tối đa 5 nút trang, có ellipsis nếu nhiều trang
const visiblePages = computed<(number | string)[]>(() => {
  const total = props.meta.pages   // tổng số trang
  const cur   = props.meta.page    // trang hiện tại (0-based)
  if (total <= 7) return Array.from({ length: total }, (_, i) => i)

  const pages: (number | string)[] = [0]
  if (cur > 2)          pages.push('...')
  for (let i = Math.max(1, cur - 1); i <= Math.min(total - 2, cur + 1); i++) pages.push(i)
  if (cur < total - 3)  pages.push('...')
  pages.push(total - 1)
  return pages
})
</script>
