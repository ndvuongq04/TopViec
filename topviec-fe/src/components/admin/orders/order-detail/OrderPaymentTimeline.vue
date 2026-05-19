<script setup lang="ts">
import { computed } from 'vue'
import type { ResOrderDTO } from '@/types/order.types'
import { OrderStatus, PAYMENT_METHOD_LABELS, ORDER_STATUS_LABELS } from '@/constants/servicePackage.constants'

const props = defineProps<{ order: ResOrderDTO }>()

function fmt(iso: string | null): string {
  if (!iso) return ''
  const d = new Date(iso)
  return `${String(d.getDate()).padStart(2,'0')}/${String(d.getMonth()+1).padStart(2,'0')}/${d.getFullYear()} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
}

const timeline = computed(() => {
  const items = []

  // Bước cuối: trạng thái hiện tại (nếu đã xong)
  if (props.order.status === OrderStatus.PAID && props.order.paidAt) {
    items.push({
      title: 'Thanh toán thành công',
      time: fmt(props.order.paidAt),
      desc: `Giao dịch đã được xác thực qua ${PAYMENT_METHOD_LABELS[props.order.paymentMethod]}.`,
      active: true,
    })
  } else if (props.order.status === OrderStatus.FAILED) {
    items.push({ title: 'Thanh toán thất bại', time: fmt(props.order.paidAt), desc: 'Giao dịch không thành công.', active: true })
  } else if (props.order.status === OrderStatus.REFUNDED) {
    items.push({ title: 'Đã hoàn tiền', time: fmt(props.order.paidAt), desc: '', active: true })
  } else if (props.order.status === OrderStatus.CANCELLED) {
    items.push({ title: 'Đã hủy đơn', time: fmt(props.order.createdAt), desc: '', active: true })
  } else if (props.order.status === OrderStatus.PENDING) {
    items.push({ title: 'Đang chờ thanh toán', time: fmt(props.order.createdAt), desc: `Khách hàng đang được chuyển sang cổng ${PAYMENT_METHOD_LABELS[props.order.paymentMethod]}.`, active: true })
  }

  // Bước khởi tạo luôn có
  items.push({ title: 'Đơn hàng được khởi tạo', time: fmt(props.order.createdAt), desc: '', active: false })

  return items
})
</script>

<template>
  <div class="timeline-card">
    <h3 class="section-title">
      <span class="title-accent"></span>
      Lịch sử thanh toán
    </h3>
    <div class="timeline">
      <div v-for="(item, i) in timeline" :key="i" class="timeline-item">
        <div class="timeline-dot" :class="{ 'dot--active': item.active }"></div>
        <div class="timeline-content">
          <div class="timeline-row">
            <p class="timeline-title" :class="{ 'title--muted': !item.active }">{{ item.title }}</p>
            <p class="timeline-time">{{ item.time }}</p>
          </div>
          <p v-if="item.desc" class="timeline-desc">{{ item.desc }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.timeline-card {
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
  margin-bottom: 32px;
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

.timeline {
  display: flex;
  flex-direction: column;
  gap: 32px;
  position: relative;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 7px;
  top: 8px;
  bottom: 8px;
  width: 2px;
  background: #f0eee7;
}

.timeline-item {
  position: relative;
  padding-left: 40px;
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.timeline-dot {
  position: absolute;
  left: 0;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #d6d3d1;
  border: 4px solid #f5f5f4;
  z-index: 1;
}

.dot--active {
  background: #004638;
  border-color: #E1F5EE;
}

.timeline-content {
  flex: 1;
}

.timeline-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.timeline-title {
  font-size: 0.875rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
}

.title--muted {
  color: #78716c;
}

.timeline-time {
  font-size: 0.75rem;
  font-weight: 600;
  color: #94a3b8;
  margin: 0;
}

.timeline-desc {
  font-size: 0.75rem;
  color: #64748b;
  margin: 0;
}
</style>