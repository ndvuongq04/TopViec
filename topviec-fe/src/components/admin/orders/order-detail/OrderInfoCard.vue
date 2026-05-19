<script setup lang="ts">
import type { ResOrderDTO } from '@/types/order.types'
import { ORDER_TYPE_LABELS, PAYMENT_METHOD_LABELS, BILLING_CYCLE_LABELS } from '@/constants/servicePackage.constants'

const props = defineProps<{ order: ResOrderDTO }>()

function fmt(iso: string | null): string {
  if (!iso) return '—'
  const d = new Date(iso)
  return `${String(d.getDate()).padStart(2,'0')}/${String(d.getMonth()+1).padStart(2,'0')}/${d.getFullYear()} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
}

const billingCycle = props.order.items?.[0]?.billingCycle
</script>

<template>
  <div class="info-card">
    <h3 class="section-title">
      <span class="title-accent"></span>
      Thông tin đơn hàng
    </h3>
    <div class="info-grid">
      <div class="info-item">
        <p class="info-label">Mã đơn hàng</p>
        <p class="info-value info-value--primary">{{ order.orderCode }}</p>
      </div>
      <div class="info-item">
        <p class="info-label">Loại đơn</p>
        <p class="info-value">{{ ORDER_TYPE_LABELS[order.type] }}</p>
      </div>
      <div class="info-item">
        <p class="info-label">Chu kỳ</p>
        <span v-if="billingCycle" class="badge-cycle">{{ BILLING_CYCLE_LABELS[billingCycle] }}</span>
        <span v-else class="info-value">—</span>
      </div>
      <div class="info-item">
        <p class="info-label">Ngày tạo</p>
        <p class="info-value">{{ fmt(order.createdAt) }}</p>
      </div>
      <div class="info-item">
        <p class="info-label">Ngày thanh toán</p>
        <p class="info-value">{{ fmt(order.paidAt) }}</p>
      </div>
      <div class="info-item">
        <p class="info-label">Phương thức</p>
        <p class="info-value method-value">
          <span class="material-symbols-outlined method-icon">account_balance</span>
          {{ PAYMENT_METHOD_LABELS[order.paymentMethod] }}
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.info-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 12px 32px -4px rgba(87,66,64,0.04);
}

.section-title {
  font-size: 0.75rem;
  font-weight: 900;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: #64748b;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-accent {
  width: 4px;
  height: 16px;
  background: #963131;
  border-radius: 9999px;
  display: inline-block;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px 48px;
}

.info-label {
  font-size: 0.75rem;
  color: #94a3b8;
  text-transform: uppercase;
  font-weight: 700;
  letter-spacing: 0.05em;
  margin-bottom: 4px;
}

.info-value {
  font-size: 0.875rem;
  font-weight: 600;
  color: #0f172a;
}

.info-value--primary {
  color: #963131;
  font-weight: 700;
}

.badge-cycle {
  display: inline-flex;
  align-items: center;
  padding: 2px 10px;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 700;
  background: #EEEDFE;
  color: #3C3489;
}

.method-value {
  display: flex;
  align-items: center;
  gap: 4px;
}

.method-icon {
  font-size: 16px;
  color: #2563eb;
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>