<template>
  <div class="table-wrap">
    <table class="table">
      <thead class="table__head">
        <tr>
          <th class="table__th">Mã đơn</th>
          <th class="table__th">Loại</th>
          <th class="table__th">Chi tiết</th>
          <th class="table__th table__th--right">Số tiền</th>
          <th class="table__th">Ngày mua</th>
          <th class="table__th">Trạng thái</th>
          <th class="table__th table__th--right">Thao tác</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="order in orders" :key="order.id" class="table__row">
          <td class="table__td"><span class="table__order-id">{{ order.orderCode }}</span></td>
          <td class="table__td">
            <span :class="['table__type-badge', order.type === OrderType.SUBSCRIPTION ? 'table__type-badge--primary' : 'table__type-badge--default']">
              {{ ORDER_TYPE_LABELS[order.type] }}
            </span>
          </td>
          <td class="table__td">
            <p class="table__detail-name">{{ getDetailName(order) }}</p>
            <p class="table__detail-note">{{ order.note ?? PAYMENT_METHOD_LABELS[order.paymentMethod] }}</p>
          </td>
          <td class="table__td table__td--right">
            <span class="table__amount">{{ formatCurrency(order.totalAmount) }}</span>
          </td>
          <td class="table__td">
            <p class="table__date">{{ formatDate(order.createdAt) }}</p>
            <p class="table__time">{{ formatTime(order.createdAt) }}</p>
          </td>
          <td class="table__td">
            <span :class="['table__status', `table__status--${order.status.toLowerCase().replace(/_/g, '-')}`]">
              <span class="table__status-dot"></span>
              {{ ORDER_STATUS_LABELS[order.status] }}
            </span>
          </td>
          <td class="table__td table__td--right">
            <GlobalDropdown>
              <template #default="{ close }">
                <GlobalDropdownItem
                  label="Xem chi tiết"
                  icon="visibility"
                  @click="close(); $emit('view', order.id)"
                />
                <GlobalDropdownItem
                  v-if="order.status === OrderStatus.PENDING"
                  label="Thanh toán ngay"
                  icon="payments"
                  @click="close(); $emit('pay', order.id)"
                />
                <GlobalDropdownItem
                  v-if="order.refundEligible"
                  label="Yêu cầu hoàn tiền"
                  icon="assignment_return"
                  danger
                  @click="close(); $emit('refund', order.id)"
                />
              </template>
            </GlobalDropdown>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Pagination -->
    <div class="pagination">
      <p class="pagination__info">
        Hiển thị {{ rangeStart }}–{{ rangeEnd }} trong {{ meta.totals }} đơn hàng
      </p>
      <div class="pagination__controls">
        <button class="pagination__btn" :disabled="meta.page === 0" @click="$emit('page-change', meta.page - 1)">
          <span class="material-symbols-outlined">chevron_left</span>
        </button>
        <button
          v-for="p in meta.pages" :key="p"
          :class="['pagination__btn', (p - 1) === meta.page && 'pagination__btn--active']"
          @click="$emit('page-change', p - 1)"
        >{{ p }}</button>
        <button class="pagination__btn" :disabled="meta.page >= meta.pages - 1" @click="$emit('page-change', meta.page + 1)">
          <span class="material-symbols-outlined">chevron_right</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { ResOrderDTO } from '@/types/order.types'
import type { PaginationMeta } from '@/types/common.types'
import GlobalDropdown from '@/components/ui/GlobalDropdown.vue'
import GlobalDropdownItem from '@/components/ui/GlobalDropdownItem.vue'
import {
  OrderType,
  OrderStatus,
  ORDER_TYPE_LABELS,
  ORDER_STATUS_LABELS,
  PAYMENT_METHOD_LABELS,
  BILLING_CYCLE_LABELS,
} from '@/constants/servicePackage.constants'

const props = defineProps<{
  orders: ResOrderDTO[]
  meta:   PaginationMeta
}>()

defineEmits<{ 
  'page-change': [page: number]; 
  'view': [id: number];
  'pay': [id: number];
  'refund': [id: number];
}>()

const rangeStart = computed(() =>
  props.meta.totals === 0 ? 0 : props.meta.page * props.meta.pageSize + 1
)
const rangeEnd = computed(() =>
  Math.min((props.meta.page + 1) * props.meta.pageSize, props.meta.totals)
)

function getDetailName(order: ResOrderDTO): string {
  const first = order.items?.[0]
  if (!first) return ORDER_TYPE_LABELS[order.type]
  if (first.billingCycle) return `${ORDER_TYPE_LABELS[order.type]} · ${BILLING_CYCLE_LABELS[first.billingCycle]}`
  if (first.quantity > 1)  return `${ORDER_TYPE_LABELS[order.type]} × ${first.quantity}`
  return ORDER_TYPE_LABELS[order.type]
}

function formatCurrency(amount: number): string {
  return amount.toLocaleString('vi-VN') + ' đ'
}

function formatDate(iso: string): string {
  const d = new Date(iso)
  return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
}

function formatTime(iso: string): string {
  const d = new Date(iso)
  return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}
</script>

<style scoped>
.table-wrap {
  margin-top: 1.5rem;
  background: #fff;
  border-radius: 1rem;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border: 1px solid #f8fafc;
}
.table { width: 100%; border-collapse: collapse; text-align: left; }

.table__head { background: rgba(248,250,252,0.5); }
.table__th {
  padding: 1rem 1.5rem;
  font-size: 0.75rem; font-weight: 800;
  text-transform: uppercase; letter-spacing: 0.05em; color: #94a3b8;
}
.table__th--right { text-align: right; }

.table__row { transition: background 0.15s; }
.table__row:hover { background: rgba(248,250,252,0.5); }
.table__row + .table__row { border-top: 1px solid #f8fafc; }

.table__td { padding: 1.25rem 1.5rem; vertical-align: middle; }
.table__td--right { text-align: right; }

.table__order-id { font-weight: 700; font-size: 0.875rem; color: #0f172a; }

.table__type-badge {
  padding: 4px 10px; border-radius: 6px;
  font-size: 0.75rem; font-weight: 800;
  text-transform: uppercase; letter-spacing: 0.04em;
}
.table__type-badge--primary { background: #dbeafe; color: #2563eb; }
.table__type-badge--default { background: #f1f5f9; color: #475569; }

.table__detail-name { font-size: 0.875rem; font-weight: 700; color: #1e293b; }
.table__detail-note { font-size: 0.75rem; color: #94a3b8; margin-top: 2px; }

.table__amount { font-size: 0.875rem; font-weight: 800; color: #0f172a; }

.table__date { font-size: 0.875rem; font-weight: 500; color: #475569; }
.table__time { font-size: 0.75rem; color: #94a3b8; margin-top: 2px; }

/* Status */
.table__status {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 4px 12px; border-radius: 999px;
  font-size: 0.875rem; font-weight: 700;
}
.table__status-dot { width: 6px; height: 6px; border-radius: 50%; flex-shrink: 0; }

.table__status--paid     { background: #f0fdf4; color: #16a34a; border: 1px solid #bbf7d0; }
.table__status--paid     .table__status-dot { background: #16a34a; }
.table__status--pending  { background: #fffbeb; color: #d97706; border: 1px solid #fde68a; }
.table__status--pending  .table__status-dot { background: #d97706; }
.table__status--failed   { background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; }
.table__status--failed   .table__status-dot { background: #dc2626; }
.table__status--refunded  { background: #f8fafc; color: #64748b; border: 1px solid #e2e8f0; }
.table__status--refunded  .table__status-dot { background: #94a3b8; }
.table__status--cancelled { background: #f8fafc; color: #64748b; border: 1px solid #e2e8f0; }
.table__status--cancelled .table__status-dot { background: #94a3b8; }

.table__status--refund-requested { background: #eff6ff; color: #2563eb; border: 1px solid #bfdbfe; }
.table__status--refund-requested .table__status-dot { background: #2563eb; }
.table__status--refund-rejected  { background: #fff1f2; color: #e11d48; border: 1px solid #fecdd3; }
.table__status--refund-rejected  .table__status-dot { background: #e11d48; }

.table__action-btn {
  padding: 8px; border: none; background: none;
  color: #94a3b8; border-radius: 0.5rem; cursor: pointer;
  transition: color 0.2s, background 0.2s;
}
.table__action-btn:hover { color: #4B9AF6; background: rgba(75,154,246,0.05); }

.table__action-btn:hover { color: #4B9AF6; background: rgba(75,154,246,0.05); }

/* Pagination */
.pagination {
  display: flex; align-items: center; justify-content: space-between;
  padding: 1.25rem 1.5rem;
  background: rgba(248,250,252,0.3);
  border-top: 1px solid #f8fafc;
}
.pagination__info { font-size: 0.875rem; color: #64748b; font-weight: 500; }
.pagination__controls { display: flex; align-items: center; gap: 4px; }
.pagination__btn {
  width: 32px; height: 32px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 0.5rem; border: 1px solid #e2e8f0;
  background: none; font-size: 0.75rem; font-weight: 700;
  color: #475569; cursor: pointer; transition: background 0.15s;
}
.pagination__btn:hover { background: #fff; }
.pagination__btn--active {
  background: #4B9AF6; color: #fff; border-color: #4B9AF6;
  box-shadow: 0 1px 4px rgba(75,154,246,0.3);
}
.pagination__btn .material-symbols-outlined { font-size: 16px; }
</style>