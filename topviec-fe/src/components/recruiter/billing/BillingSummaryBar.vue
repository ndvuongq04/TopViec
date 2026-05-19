<template>
  <div class="summary-bar">
    <div class="summary-bar__left">
      <div class="summary-bar__stat">
        <span class="summary-bar__dot summary-bar__dot--gray"></span>
        <span class="summary-bar__text">Tìm thấy <strong>{{ totalOrders }}</strong> đơn</span>
      </div>
      <div class="summary-bar__divider"></div>
      <div class="summary-bar__stat">
        <span class="summary-bar__dot summary-bar__dot--green"></span>
        <span class="summary-bar__text">Đã thanh toán: <strong>{{ paidOrders }}</strong></span>
      </div>
    </div>
    <div class="summary-bar__total">
      <span class="summary-bar__total-label">Tổng giá trị:</span>
      <span class="summary-bar__total-value">{{ totalAmount }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ totalOrders: number; paidOrders: number; totalAmount: string }>()
</script>

<style scoped>
.summary-bar {
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 8px; margin-top: 2rem; flex-wrap: wrap; gap: 1rem;
}
.summary-bar__left { display: flex; align-items: center; gap: 1.5rem; }
.summary-bar__stat { display: flex; align-items: center; gap: 8px; }
.summary-bar__dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; }
.summary-bar__dot--gray  { background: #94a3b8; }
.summary-bar__dot--green { background: #10b981; }
.summary-bar__text { font-size: 0.875rem; font-weight: 500; color: #475569; }
.summary-bar__text strong { color: #0f172a; }
.summary-bar__divider { width: 1px; height: 16px; background: #e2e8f0; }

.summary-bar__total {
  display: flex; align-items: center; gap: 8px;
  background: rgba(75,154,246,0.05);
  border: 1px solid rgba(75,154,246,0.1);
  padding: 8px 16px; border-radius: 999px;
}
.summary-bar__total-label { font-size: 0.875rem; font-weight: 500; color: #475569; }
.summary-bar__total-value { font-size: 1rem; font-weight: 800; color: #4B9AF6; letter-spacing: -0.01em; }
</style>