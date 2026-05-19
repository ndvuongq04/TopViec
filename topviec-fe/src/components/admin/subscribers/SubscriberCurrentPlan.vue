<script setup lang="ts">
import { computed } from 'vue'
import { useAdminCompanyStore } from '@/stores/adminCompany.store'
import { BILLING_CYCLE_LABELS, BillingCycle } from '@/constants/servicePackage.constants'

const store = useAdminCompanyStore()

const plan = computed(() => {
  const current = store.companyPlan?.currentPackage
  if (!current) return null

  const expiredDate = new Date(current.expiredAt)
  const diffTime = Math.max(0, expiredDate.getTime() - new Date().getTime())
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

  return {
    tier: current.packageName,
    expiresAt: expiredDate.toLocaleDateString('vi-VN'),
    daysLeft: diffDays,
    billingCycle: BILLING_CYCLE_LABELS[(current.billingCycle || '').toLowerCase() as BillingCycle] || current.billingCycle || 'N/A',
    activatedAt: new Date(current.startedAt).toLocaleDateString('vi-VN'),
    orderCode: current.orderCode || '#N/A',
    orderId: current.orderId,
  }
})
</script>

<template>
  <section class="plan-section">
    <h3 class="section-heading">Gói dịch vụ hiện tại</h3>
    
    <div v-if="plan" class="plan-card">
      <div class="plan-glow"></div>

      <div class="plan-top">
        <div>
          <span class="tier-badge">{{ plan.tier }}</span>
          <p class="expires-label">Hết hạn vào</p>
          <p class="expires-value">{{ plan.expiresAt }}</p>
        </div>
        <span class="days-badge">Còn {{ plan.daysLeft }} ngày</span>
      </div>

      <div class="plan-details">
        <div class="detail-item">
          <p class="detail-label">Kỳ thanh toán</p>
          <p class="detail-value">{{ plan.billingCycle }}</p>
        </div>
        <div class="detail-item">
          <p class="detail-label">Ngày kích hoạt</p>
          <p class="detail-value">{{ plan.activatedAt }}</p>
        </div>
      </div>

      <div class="plan-order-link">
        <span class="material-symbols-outlined link-icon">link</span>
        <span>Mã đơn gốc: 
          <router-link
            v-if="plan.orderId"
            :to="{ name: 'admin-order-detail', params: { id: plan.orderId } }"
            class="order-link"
          >
            {{ plan.orderCode }}
          </router-link>
          <span v-else class="order-link">{{ plan.orderCode }}</span>
        </span>
      </div>
    </div>
    
    <div v-else class="plan-card empty-card text-center">
      <p>Chưa đăng ký gói dịch vụ nào</p>
    </div>
  </section>
</template>

<style scoped>
.section-heading {
  font-size: 1.125rem;
  font-weight: 700;
  color: #1b1c18;
  letter-spacing: -0.02em;
  margin: 0 0 16px;
}

.plan-card {
  background: linear-gradient(to bottom right, #ffffff, #f6f4ec);
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.5);
  position: relative;
  overflow: hidden;
}

.plan-glow {
  position: absolute;
  right: -32px;
  top: -32px;
  width: 128px;
  height: 128px;
  background: rgba(150, 49, 49, 0.05);
  border-radius: 50%;
  filter: blur(24px);
  transition: transform 0.3s;
}

.plan-card:hover .plan-glow {
  transform: scale(1.5);
}

.plan-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
  position: relative;
  z-index: 1;
}

.tier-badge {
  display: inline-block;
  padding: 4px 12px;
  background: #EEEDFE;
  color: #3C3489;
  font-size: 0.75rem;
  font-weight: 700;
  border-radius: 9999px;
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

.expires-label {
  margin: 16px 0 2px;
  font-size: 0.75rem;
  color: rgba(87, 66, 64, 0.7);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  font-weight: 600;
}

.expires-value {
  font-size: 1.875rem;
  font-weight: 800;
  color: #1b1c18;
  letter-spacing: -0.03em;
  margin: 0;
}

.days-badge {
  padding: 4px 12px;
  background: #FAECE7;
  color: #712B13;
  font-size: 0.75rem;
  font-weight: 700;
  border-radius: 9999px;
}

.plan-details {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  border-top: 1px solid rgba(221, 192, 190, 0.1);
  padding-top: 24px;
  position: relative;
  z-index: 1;
}

.detail-label {
  font-size: 0.75rem;
  color: rgba(87, 66, 64, 0.7);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  font-weight: 600;
  margin: 0 0 2px;
}

.detail-value {
  font-size: 0.875rem;
  font-weight: 600;
  color: #1b1c18;
  margin: 0;
}

.plan-order-link {
  margin-top: 24px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.75rem;
  font-weight: 500;
  color: #574240;
  background: rgba(228, 226, 220, 0.5);
  padding: 8px;
  border-radius: 8px;
}

.link-icon {
  font-size: 0.875rem;
}

.order-link {
  color: #963131;
  font-weight: 700;
  text-decoration: none;
}

.order-link:hover {
  text-decoration: underline;
}

.empty-card {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #574240;
  font-weight: 500;
  min-height: 200px;
}
</style>