<script setup lang="ts">
import type { ResOrderDTO } from '@/types/order.types'
import { OrderStatus, ORDER_STATUS_LABELS } from '@/constants/servicePackage.constants'

const props = defineProps<{ order: ResOrderDTO }>()

const statusConfig: Record<OrderStatus, { icon: string; iconColor: string; badgeBg: string; badgeColor: string; borderColor: string }> = {
  [OrderStatus.PAID]:      { icon: 'check_circle',  iconColor: '#004638', badgeBg: '#E1F5EE', badgeColor: '#085041', borderColor: '#004638' },
  [OrderStatus.PENDING]:   { icon: 'pending',        iconColor: '#d97706', badgeBg: '#fffbeb', badgeColor: '#b45309', borderColor: '#d97706' },
  [OrderStatus.FAILED]:    { icon: 'cancel',         iconColor: '#dc2626', badgeBg: '#fef2f2', badgeColor: '#b91c1c', borderColor: '#dc2626' },
  [OrderStatus.REFUNDED]:  { icon: 'replay',         iconColor: '#64748b', badgeBg: '#f1f5f9', badgeColor: '#475569', borderColor: '#94a3b8' },
  [OrderStatus.CANCELLED]: { icon: 'do_not_disturb', iconColor: '#64748b', badgeBg: '#f1f5f9', badgeColor: '#475569', borderColor: '#94a3b8' },
}

const cfg = statusConfig[props.order.status]
</script>

<template>
  <div class="status-card" :style="{ borderTopColor: cfg.borderColor }">
    <div class="status-top">
      <span class="material-symbols-outlined status-icon" :style="{ color: cfg.iconColor }" style="font-variation-settings: 'FILL' 1;">{{ cfg.icon }}</span>
      <span class="status-badge" :style="{ background: cfg.badgeBg, color: cfg.badgeColor }">{{ ORDER_STATUS_LABELS[order.status] }}</span>
    </div>
    <div class="status-total">
      <p class="total-label">Tổng cộng</p>
      <p class="total-value">{{ order.totalAmount.toLocaleString('vi-VN') }} đ</p>
    </div>
    <div class="status-txn">
      <p class="txn-label">Transaction ID</p>
      <p class="txn-value">{{ order.paymentTransactionId ?? '—' }}</p>
    </div>
  </div>
</template>

<style scoped>
.status-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 12px 32px -4px rgba(87,66,64,0.04);
  border-top: 4px solid #004638;
}

.status-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.status-icon {
  font-size: 36px;
  color: #004638;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 16px;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 900;
  background: #E1F5EE;
  color: #085041;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.status-total {
  margin-bottom: 24px;
}

.total-label {
  font-size: 0.75rem;
  color: #94a3b8;
  text-transform: uppercase;
  font-weight: 700;
  letter-spacing: 0.05em;
  margin: 0 0 4px;
}

.total-value {
  font-size: 30px;
  font-weight: 900;
  color: #1b1c18;
  letter-spacing: -0.02em;
  margin: 0;
}

.status-txn {
  padding-top: 16px;
  border-top: 1px solid #e2e8f0;
}

.txn-label {
  font-size: 0.75rem;
  color: #94a3b8;
  text-transform: uppercase;
  font-weight: 700;
  letter-spacing: 0.05em;
  margin: 0 0 4px;
}

.txn-value {
  font-size: 0.75rem;
  font-family: monospace;
  font-weight: 700;
  color: #1b1c18;
  word-break: break-all;
  margin: 0;
}
</style>