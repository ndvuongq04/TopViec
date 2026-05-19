<script setup lang="ts">
import type { ResOrderDTO } from '@/types/order.types'

const props = defineProps<{ order: ResOrderDTO }>()

const company = props.order.company
</script>

<template>
  <div class="customer-card">
    <h3 class="card-label">Thông tin khách hàng</h3>

    <!-- No data -->
    <div v-if="!company" class="pending-notice">
      <span class="material-symbols-outlined" style="font-size:20px; color:#94a3b8">info</span>
      <p>Không có thông tin công ty cho đơn hàng này.</p>
    </div>

    <template v-else>
      <!-- Logo + tên -->
      <div class="customer-info">
        <div class="customer-avatar">
          <img v-if="company.logoUrl" :src="company.logoUrl" :alt="company.name" />
          <div v-else class="avatar-placeholder">
            <span class="material-symbols-outlined" style="font-size:22px; color:#94a3b8">business</span>
          </div>
        </div>
        <div>
          <p class="customer-name">{{ company.name }}</p>
          <p class="customer-tier">Nhà tuyển dụng</p>
        </div>
      </div>

      <!-- Liên hệ -->
      <div class="contact-list">
        <div v-if="company.email" class="contact-item">
          <span class="material-symbols-outlined contact-icon">mail</span>
          <span class="contact-text">{{ company.email }}</span>
        </div>
        <div v-if="company.phone" class="contact-item">
          <span class="material-symbols-outlined contact-icon">call</span>
          <span class="contact-text">{{ company.phone }}</span>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.customer-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 12px 32px -4px rgba(87,66,64,0.04);
}

.card-label {
  font-size: 0.75rem;
  font-weight: 900;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: #94a3b8;
  margin: 0 0 24px;
}

.customer-info {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.customer-avatar {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  flex-shrink: 0;
  background: #f8fafc;
}

.customer-avatar img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.customer-name {
  font-size: 0.875rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 2px;
}

.customer-tier {
  font-size: 0.75rem;
  font-weight: 500;
  color: #64748b;
  margin: 0;
}

.contact-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.contact-icon {
  font-size: 18px;
  color: #a8a29e;
}

.contact-text {
  font-size: 0.75rem;
  font-weight: 500;
  color: #0f172a;
}

.pending-notice {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  background: #f8fafc;
  border-radius: 8px;
  padding: 16px;
  font-size: 0.75rem;
  color: #94a3b8;
  line-height: 1.6;
}
</style>
