<script setup lang="ts">
import { computed } from 'vue'
import { useAdminCompanyStore } from '@/stores/adminCompany.store'

const store = useAdminCompanyStore()

const formatCurrency = (amount: number) => amount.toLocaleString('vi-VN') + ' đ'

const events = computed(() => {
  const items = store.companySubscriptions?.result || []
  return items.map((sub, index) => {
    const isFirst = index === 0
    return {
      title: 'Đăng ký ' + sub.packageName,
      date: sub.purchasedAt ? new Date(sub.purchasedAt).toLocaleDateString('vi-VN') : 'N/A',
      orderCode: sub.orderId ? `#ORD-${sub.orderId.toString().padStart(5, '0')}` : '',
      amount: sub.packagePrice != null ? formatCurrency(sub.packagePrice) : '',
      note: sub.status === 'ACTIVE' ? 'Đang sử dụng' : 'Đã hết hạn',
      opacity: isFirst ? 1 : 0.6,
    }
  })
})
</script>

<template>
  <section class="timeline-section">
    <h3 class="section-heading">Lịch sử gia hạn gói</h3>
    <div v-if="events.length" class="timeline-card">
      <div class="timeline-line"></div>
      <div class="timeline-items">
        <div v-for="(e, i) in events" :key="i" class="timeline-item">
          <div class="timeline-dot" :style="{ opacity: e.opacity }"></div>
          <div class="timeline-content">
            <p class="event-title">{{ e.title }}</p>
            <div class="event-meta">
              <span class="event-date">{{ e.date }}</span>
              <template v-if="e.orderCode">
                <span class="meta-sep">•</span>
                <a href="#" class="event-order">{{ e.orderCode }}</a>
              </template>
            </div>
            <p v-if="e.amount" class="event-amount">{{ e.amount }}</p>
            <p v-if="e.note" class="event-note">{{ e.note }}</p>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="timeline-card empty-state text-center text-slate-500 py-8">
      Không có lịch sử gia hạn
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

.timeline-card {
  background: #ffffff;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  position: relative;
  overflow: hidden;
}

.timeline-line {
  position: absolute;
  left: 31px;
  top: 32px;
  bottom: 32px;
  width: 2px;
  background: rgba(150, 49, 49, 0.1);
}

.timeline-items {
  display: flex;
  flex-direction: column;
  gap: 32px;
  position: relative;
}

.timeline-item {
  display: flex;
  gap: 16px;
  position: relative;
}

.timeline-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #963131;
  box-shadow: 0 0 0 4px rgba(150, 49, 49, 0.2);
  margin-top: 4px;
  z-index: 1;
  flex-shrink: 0;
}

.timeline-content {
  flex: 1;
}

.event-title {
  font-size: 0.875rem;
  font-weight: 700;
  color: #1b1c18;
  margin: 0;
}

.event-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 4px;
}

.event-date {
  font-size: 0.75rem;
  font-weight: 500;
  color: #574240;
}

.meta-sep {
  font-size: 0.75rem;
  color: rgba(87, 66, 64, 0.4);
}

.event-order {
  font-size: 0.75rem;
  font-weight: 700;
  color: #963131;
  text-decoration: none;
}

.event-order:hover {
  text-decoration: underline;
}

.event-amount {
  font-size: 0.875rem;
  font-weight: 700;
  color: #1b1c18;
  margin: 8px 0 0;
}

.event-note {
  font-size: 0.75rem;
  font-weight: 500;
  color: rgba(87, 66, 64, 0.7);
  font-style: italic;
  margin: 8px 0 0;
}
</style>