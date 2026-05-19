<template>
  <GlobalModal
    :visible="visible"
    title="Chi tiết đơn hàng"
    :subtitle="order?.orderCode ?? ''"
    icon="receipt_long"
    max-width="2xl"
    :show-default-footer="false"
    @close="$emit('close')"
  >
    <!-- Loading -->
    <div v-if="loading" class="flex items-center justify-center gap-3 py-16 text-slate-400">
      <span class="material-symbols-outlined animate-spin">progress_activity</span>
      <span class="text-sm font-medium">Đang tải...</span>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="flex items-center justify-center gap-2 py-16 text-red-500 text-sm">
      <span class="material-symbols-outlined">error</span>
      {{ error }}
    </div>

    <!-- Content -->
    <div v-else-if="order" class="space-y-6">

      <!-- Status + Meta -->
      <div class="flex items-start justify-between gap-4">
        <div>
          <p class="text-xs font-800 text-slate-400 uppercase tracking-wider mb-1">Mã đơn hàng</p>
          <p class="text-lg font-800 text-slate-900">{{ order.orderCode }}</p>
        </div>
        <span :class="['od-status', `od-status--${order.status.toLowerCase()}`]">
          <span class="od-status__dot"></span>
          {{ ORDER_STATUS_LABELS[order.status] }}
        </span>
      </div>

      <!-- Info Grid -->
      <div class="od-grid">
        <div class="od-info">
          <span class="od-info__label">Loại đơn</span>
          <span class="od-info__value">{{ ORDER_TYPE_LABELS[order.type] }}</span>
        </div>
        <div class="od-info">
          <span class="od-info__label">Phương thức thanh toán</span>
          <span class="od-info__value">{{ PAYMENT_METHOD_LABELS[order.paymentMethod] }}</span>
        </div>
        <div class="od-info">
          <span class="od-info__label">Ngày tạo</span>
          <span class="od-info__value">{{ formatDateTime(order.createdAt) }}</span>
        </div>
        <div class="od-info">
          <span class="od-info__label">Ngày thanh toán</span>
          <span class="od-info__value">{{ order.paidAt ? formatDateTime(order.paidAt) : '—' }}</span>
        </div>
        <div v-if="order.note" class="od-info od-info--full">
          <span class="od-info__label">Ghi chú</span>
          <span class="od-info__value">{{ order.note }}</span>
        </div>
      </div>

      <!-- Items Table -->
      <div>
        <p class="text-xs font-800 text-slate-400 uppercase tracking-wider mb-3">Chi tiết dịch vụ</p>
        <div class="od-table-wrap">
          <table class="od-table">
            <thead>
              <tr>
                <th class="od-th">Loại dịch vụ</th>
                <th class="od-th od-th--right">Số lượng</th>
                <th class="od-th od-th--right">Đơn giá</th>
                <th class="od-th od-th--right">Thành tiền</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in order.items" :key="item.id" class="od-row">
                <td class="od-td">
                  <p class="od-item-type">{{ ORDER_ITEM_TYPE_LABELS[item.itemType] }}</p>
                  <p v-if="item.billingCycle" class="od-item-sub">{{ BILLING_CYCLE_LABELS[item.billingCycle] }}</p>
                  <p v-if="item.durationDays" class="od-item-sub">{{ item.durationDays }} ngày</p>
                </td>
                <td class="od-td od-td--right">{{ item.quantity }}</td>
                <td class="od-td od-td--right">{{ formatCurrency(item.unitPrice) }}</td>
                <td class="od-td od-td--right od-item-total">{{ formatCurrency(item.totalPrice) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Total -->
      <div class="od-total">
        <span class="od-total__label">Tổng thanh toán</span>
        <span class="od-total__value">{{ formatCurrency(order.totalAmount) }}</span>
      </div>

    </div>

    <!-- Footer -->
    <template #footer>
      <button class="od-btn od-btn--close" @click="$emit('close')">Đóng</button>
      <button
        v-if="order"
        class="od-btn od-btn--download"
        :disabled="downloading"
        @click="downloadInvoice"
      >
        <span class="material-symbols-outlined" style="font-size:18px">
          {{ downloading ? 'progress_activity' : 'download' }}
        </span>
        {{ downloading ? 'Đang xử lý...' : 'Tải hóa đơn' }}
      </button>
    </template>
  </GlobalModal>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import GlobalModal from '@/components/ui/GlobalModal.vue'
import { useEmployerOrderStore } from '@/stores/order.store'
import {
  ORDER_STATUS_LABELS,
  ORDER_TYPE_LABELS,
  ORDER_ITEM_TYPE_LABELS,
  PAYMENT_METHOD_LABELS,
  BILLING_CYCLE_LABELS,
} from '@/constants/servicePackage.constants'

const props = defineProps<{
  visible:  boolean
  orderId:  number | null
}>()

defineEmits<{ close: [] }>()

const store       = useEmployerOrderStore()
const order       = ref(store.selectedOrder)
const loading     = ref(false)
const error       = ref<string | null>(null)
const downloading = ref(false)

watch(() => props.visible, async (val) => {
  if (!val || !props.orderId) return
  loading.value = true
  error.value   = null
  order.value   = null
  try {
    await store.fetchOrderById(props.orderId)
    order.value = store.selectedOrder
  } catch {
    error.value = 'Không thể tải chi tiết đơn hàng. Vui lòng thử lại.'
  } finally {
    loading.value = false
  }
})

// ─── Helpers ──────────────────────────────────────────────────────────────────

function formatCurrency(amount: number): string {
  return amount.toLocaleString('vi-VN') + ' đ'
}

function formatDateTime(iso: string): string {
  const d = new Date(iso)
  const date = `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
  const time = `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
  return `${date} lúc ${time}`
}

// ─── Download Invoice (client-side print to PDF) ──────────────────────────────

function downloadInvoice() {
  if (!order.value) return
  downloading.value = true

  const o = order.value
  const itemRows = o.items.map(item => `
    <tr>
      <td>${ORDER_ITEM_TYPE_LABELS[item.itemType]}${item.billingCycle ? ' · ' + BILLING_CYCLE_LABELS[item.billingCycle] : ''}</td>
      <td style="text-align:right">${item.quantity}</td>
      <td style="text-align:right">${formatCurrency(item.unitPrice)}</td>
      <td style="text-align:right">${formatCurrency(item.totalPrice)}</td>
    </tr>
  `).join('')

  const html = `<!DOCTYPE html><html lang="vi"><head>
    <meta charset="UTF-8"/>
    <title>Hóa đơn ${o.orderCode}</title>
    <style>
      * { margin:0; padding:0; box-sizing:border-box; }
      body { font-family: 'Segoe UI', sans-serif; font-size: 14px; color: #1e293b; padding: 40px; }
      .header { display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:32px; padding-bottom:24px; border-bottom:2px solid #e2e8f0; }
      .brand { font-size:22px; font-weight:800; color:#4B9AF6; }
      .brand-sub { font-size:12px; color:#94a3b8; margin-top:2px; }
      .order-code { font-size:28px; font-weight:800; color:#0f172a; }
      .order-sub { font-size:12px; color:#94a3b8; margin-top:4px; }
      .status { display:inline-block; padding:4px 14px; border-radius:999px; font-size:12px; font-weight:700; margin-top:8px; }
      .status-paid { background:#f0fdf4; color:#16a34a; border:1px solid #bbf7d0; }
      .status-pending { background:#fffbeb; color:#d97706; border:1px solid #fde68a; }
      .status-failed { background:#fef2f2; color:#dc2626; border:1px solid #fecaca; }
      .status-refunded, .status-cancelled { background:#f8fafc; color:#64748b; border:1px solid #e2e8f0; }
      .info-grid { display:grid; grid-template-columns:1fr 1fr; gap:20px; margin-bottom:32px; }
      .info-item label { font-size:11px; font-weight:700; text-transform:uppercase; letter-spacing:0.05em; color:#94a3b8; display:block; margin-bottom:4px; }
      .info-item span { font-size:14px; font-weight:600; color:#1e293b; }
      table { width:100%; border-collapse:collapse; margin-bottom:24px; }
      th { font-size:11px; font-weight:700; text-transform:uppercase; letter-spacing:0.05em; color:#94a3b8; padding:10px 16px; background:#f8fafc; border-bottom:2px solid #e2e8f0; }
      td { padding:12px 16px; border-bottom:1px solid #f1f5f9; }
      th:not(:first-child), td:not(:first-child) { text-align:right; }
      .total-row { display:flex; justify-content:flex-end; align-items:center; gap:20px; padding:16px; background:#f0f9ff; border-radius:12px; }
      .total-label { font-size:14px; font-weight:600; color:#475569; }
      .total-value { font-size:22px; font-weight:800; color:#4B9AF6; }
      .footer { margin-top:40px; padding-top:20px; border-top:1px solid #e2e8f0; font-size:12px; color:#94a3b8; text-align:center; }
      @media print { body { padding:20px; } }
    </style>
  </head><body>
    <div class="header">
      <div>
        <div class="brand">TopViec</div>
        <div class="brand-sub">Employer Portal</div>
      </div>
      <div style="text-align:right">
        <div class="order-code">${o.orderCode}</div>
        <div class="order-sub">Hóa đơn điện tử</div>
        <div class="status status-${o.status.toLowerCase()}">${ORDER_STATUS_LABELS[o.status]}</div>
      </div>
    </div>
    <div class="info-grid">
      <div class="info-item"><label>Loại đơn</label><span>${ORDER_TYPE_LABELS[o.type]}</span></div>
      <div class="info-item"><label>Thanh toán qua</label><span>${PAYMENT_METHOD_LABELS[o.paymentMethod]}</span></div>
      <div class="info-item"><label>Ngày tạo</label><span>${formatDateTime(o.createdAt)}</span></div>
      <div class="info-item"><label>Ngày thanh toán</label><span>${o.paidAt ? formatDateTime(o.paidAt) : '—'}</span></div>
    </div>
    <table>
      <thead><tr><th>Dịch vụ</th><th>Số lượng</th><th>Đơn giá</th><th>Thành tiền</th></tr></thead>
      <tbody>${itemRows}</tbody>
    </table>
    <div class="total-row">
      <span class="total-label">Tổng thanh toán</span>
      <span class="total-value">${formatCurrency(o.totalAmount)}</span>
    </div>
    <div class="footer">TopViec · topviec.vn · Hóa đơn được tạo tự động</div>
  </body></html>`

  const win = window.open('', '_blank')
  if (win) {
    win.document.write(html)
    win.document.close()
    win.focus()
    setTimeout(() => { win.print(); downloading.value = false }, 500)
  } else {
    downloading.value = false
  }
}
</script>

<style scoped>
/* Status badge */
.od-status {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 5px 14px; border-radius: 999px;
  font-size: 0.75rem; font-weight: 700; white-space: nowrap;
}
.od-status__dot { width: 6px; height: 6px; border-radius: 50%; flex-shrink: 0; }
.od-status--paid     { background: #f0fdf4; color: #16a34a; border: 1px solid #bbf7d0; }
.od-status--paid     .od-status__dot { background: #16a34a; }
.od-status--pending  { background: #fffbeb; color: #d97706; border: 1px solid #fde68a; }
.od-status--pending  .od-status__dot { background: #d97706; }
.od-status--failed   { background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; }
.od-status--failed   .od-status__dot { background: #dc2626; }
.od-status--refunded,
.od-status--cancelled { background: #f8fafc; color: #64748b; border: 1px solid #e2e8f0; }
.od-status--refunded .od-status__dot,
.od-status--cancelled .od-status__dot { background: #94a3b8; }

/* Info grid */
.od-grid {
  display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;
  background: #f8fafc; border-radius: 1rem; padding: 1.25rem;
}
.od-info { display: flex; flex-direction: column; gap: 4px; }
.od-info--full { grid-column: 1 / -1; }
.od-info__label {
  font-size: 0.75rem; font-weight: 800; text-transform: uppercase;
  letter-spacing: 0.05em; color: #94a3b8;
}
.od-info__value { font-size: 0.875rem; font-weight: 600; color: #1e293b; }

/* Items table */
.od-table-wrap {
  border: 1px solid #f1f5f9; border-radius: 0.75rem; overflow: hidden;
}
.od-table { width: 100%; border-collapse: collapse; }
.od-th {
  padding: 10px 16px; font-size: 0.75rem; font-weight: 800;
  text-transform: uppercase; letter-spacing: 0.05em; color: #94a3b8;
  background: #f8fafc; text-align: left;
}
.od-th--right { text-align: right; }
.od-row + .od-row { border-top: 1px solid #f8fafc; }
.od-td { padding: 12px 16px; font-size: 0.875rem; color: #334155; vertical-align: middle; }
.od-td--right { text-align: right; }
.od-item-type { font-weight: 600; color: #1e293b; }
.od-item-sub { font-size: 0.75rem; color: #94a3b8; margin-top: 2px; }
.od-item-total { font-weight: 700; color: #0f172a; }

/* Total */
.od-total {
  display: flex; align-items: center; justify-content: flex-end; gap: 1rem;
  background: rgba(75,154,246,0.05); border: 1px solid rgba(75,154,246,0.1);
  border-radius: 0.75rem; padding: 1rem 1.25rem;
}
.od-total__label { font-size: 0.875rem; font-weight: 600; color: #475569; }
.od-total__value { font-size: 1.25rem; font-weight: 800; color: #4B9AF6; letter-spacing: -0.01em; }

/* Footer buttons */
.od-btn {
  display: inline-flex; align-items: center; gap: 8px;
  padding: 10px 20px; border-radius: 0.75rem;
  font-size: 0.875rem; font-weight: 700; cursor: pointer;
  transition: background 0.2s, border-color 0.2s;
  border: none;
}
.od-btn--close {
  background: #fff; color: #334155;
  border: 1px solid #e2e8f0;
}
.od-btn--close:hover { background: #f8fafc; border-color: #cbd5e1; }
.od-btn--download {
  background: #0f172a; color: #fff;
  box-shadow: 0 2px 6px rgba(0,0,0,0.12);
}
.od-btn--download:hover { background: #1e293b; }
.od-btn--download:disabled { opacity: 0.6; cursor: not-allowed; }
</style>
