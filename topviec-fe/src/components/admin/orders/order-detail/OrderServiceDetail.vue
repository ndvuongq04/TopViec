<script setup lang="ts">
import type { ResOrderDTO } from '@/types/order.types'
import { ORDER_ITEM_TYPE_LABELS, BILLING_CYCLE_LABELS, OrderItemType } from '@/constants/servicePackage.constants'

defineProps<{ order: ResOrderDTO }>()

function formatCurrency(n: number): string {
  return n.toLocaleString('vi-VN') + ' đ'
}
</script>

<template>
  <div class="service-card">
    <div class="service-header">
      <h3 class="section-title">
        <span class="title-accent"></span>
        Chi tiết dịch vụ
      </h3>
    </div>

    <div class="service-body">
      <div v-for="item in order.items" :key="item.id" class="item-block">

        <!-- Package summary row -->
        <div class="package-summary">
          <div class="package-left">
            <div class="package-icon">
              <span class="material-symbols-outlined" style="font-size: 28px;">stars</span>
            </div>
            <div>
              <!-- Tên gói từ BE, fallback về loại item -->
              <h4 class="package-name">
                {{ item.packageName ?? ORDER_ITEM_TYPE_LABELS[item.itemType] }}
              </h4>
              <p class="package-type">
                {{ ORDER_ITEM_TYPE_LABELS[item.itemType] }}
                <span v-if="item.billingCycle"> · {{ BILLING_CYCLE_LABELS[item.billingCycle] }}</span>
              </p>
              <p class="package-meta">
                <span v-if="item.durationDays">
                  <span class="material-symbols-outlined meta-icon">schedule</span>
                  Thời hạn: <strong>{{ item.durationDays }} ngày</strong>
                </span>
                <span v-if="item.quantity > 1">
                  <span class="material-symbols-outlined meta-icon">layers</span>
                  Số lượng: <strong>× {{ item.quantity }}</strong>
                </span>
              </p>
            </div>
          </div>
          <div class="package-price-wrap">
            <p class="price-unit">
              {{ formatCurrency(item.unitPrice) }}
              <span v-if="item.quantity > 1"> × {{ item.quantity }}</span>
            </p>
            <p class="price-label">Thành tiền</p>
            <p class="price-value">{{ formatCurrency(item.totalPrice) }}</p>
          </div>
        </div>

        <!-- Subscription: iterate details -->
        <template v-if="item.itemType === OrderItemType.SUBSCRIPTION && item.details?.length">
          <p class="features-title">Dịch vụ trong gói</p>
          <div class="features-grid">
            <div v-for="d in item.details" :key="d.serviceId" class="feature-item">
              <span class="material-symbols-outlined feature-icon">check_circle</span>
              <span class="feature-text">
                {{ d.serviceName }}: {{ d.quantity >= 999 ? 'Không giới hạn' : d.quantity }} {{ d.quantity < 999 ? (d.serviceUnit ?? 'lượt') : '' }}
              </span>
            </div>
          </div>
        </template>

        <!-- Addon: hiển thị tên addon + số lượng -->
        <template v-else-if="item.itemType === OrderItemType.ADDON">
          <p class="features-title">Chi tiết dịch vụ lẻ</p>
          <div class="features-grid">
            <div class="feature-item">
              <span class="material-symbols-outlined feature-icon">check_circle</span>
              <span class="feature-text">
                {{ item.packageName ?? 'Dịch vụ lẻ' }} · {{ item.quantity }} lượt
              </span>
            </div>
          </div>
        </template>

      </div>
    </div>
  </div>
</template>

<style scoped>
.service-card {
  background: #ffffff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 12px 32px -4px rgba(87,66,64,0.04);
}

.service-header {
  padding: 32px 32px 24px;
  border-bottom: 1px solid #f0eee7;
}

.section-title {
  font-size: 0.75rem;
  font-weight: 900;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: #64748b;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
}

.title-accent {
  width: 4px;
  height: 16px;
  background: #963131;
  border-radius: 9999px;
  display: inline-block;
}

.service-body {
  padding: 32px;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

/* ── Item block ── */
.item-block {
  border: 1px solid #f0eee7;
  border-radius: 12px;
  overflow: hidden;
}

/* ── Package summary ── */
.package-summary {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  background: #fbf9f2;
  padding: 24px;
  gap: 16px;
}

.package-left {
  display: flex;
  gap: 20px;
  flex: 1;
}

.package-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  background: #76191d;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  flex-shrink: 0;
}

.package-name {
  font-size: 1.125rem;
  font-weight: 900;
  color: #0f172a;
  margin: 0 0 2px;
}

.package-type {
  font-size: 0.75rem;
  font-weight: 700;
  color: #963131;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0 0 8px;
}

.package-meta {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  font-size: 0.8125rem;
  color: #64748b;
  margin: 0;
}

.meta-icon {
  font-size: 14px;
  vertical-align: middle;
  margin-right: 3px;
}

.package-price-wrap {
  text-align: right;
  flex-shrink: 0;
}

.price-unit {
  font-size: 0.8125rem;
  color: #94a3b8;
  margin: 0 0 2px;
}

.price-label {
  font-size: 0.75rem;
  color: #94a3b8;
  text-transform: uppercase;
  font-weight: 700;
  letter-spacing: 0.05em;
  margin: 0 0 2px;
}

.price-value {
  font-size: 1.875rem;
  font-weight: 900;
  color: #963131;
  margin: 0;
}

/* ── Features ── */
.features-title {
  font-size: 0.75rem;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #94a3b8;
  margin: 0;
  padding: 20px 24px 12px;
  background: #fff;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0;
  background: #fff;
  padding: 0 24px 24px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  background: #f8fafc;
  border-radius: 8px;
  margin: 4px;
}

.feature-icon {
  color: #16a34a;
  font-size: 18px;
  flex-shrink: 0;
}

.feature-text {
  font-size: 0.875rem;
  font-weight: 500;
  color: #0f172a;
}
</style>
