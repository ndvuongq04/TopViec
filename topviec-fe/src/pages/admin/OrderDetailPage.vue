<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
import OrderDetailNav from '@/components/admin/orders/order-detail/OrderDetailNav.vue'
import OrderInfoCard from '@/components/admin/orders/order-detail/OrderInfoCard.vue'
import OrderServiceDetail from '@/components/admin/orders/order-detail/OrderServiceDetail.vue'
import OrderPaymentTimeline from '@/components/admin/orders/order-detail/OrderPaymentTimeline.vue'
import OrderStatusCard from '@/components/admin/orders/order-detail/OrderStatusCard.vue'
import OrderCustomerCard from '@/components/admin/orders/order-detail/OrderCustomerCard.vue'
import OrderActionCard from '@/components/admin/orders/order-detail/OrderActionCard.vue'
import { useAdminOrderStore } from '@/stores/order.store'

const route = useRoute()
const store = useAdminOrderStore()

onMounted(() => {
  store.fetchOrderById(Number(route.params.id))
})
</script>

<template>
  <div class="order-detail-page">

    <!-- Loading -->
    <div v-if="store.loading" class="loading-state">
      <span class="material-symbols-outlined" style="animation: spin 1s linear infinite">progress_activity</span>
      Đang tải đơn hàng...
    </div>

    <!-- Error -->
    <div v-else-if="store.error" class="error-state">
      <span class="material-symbols-outlined">error</span>
      {{ store.error }}
    </div>

    <template v-else-if="store.selectedOrder">
      <OrderDetailNav :order-code="store.selectedOrder.orderCode" />

      <div class="detail-layout">
        <!-- Left Column -->
        <div class="detail-left">
          <OrderInfoCard :order="store.selectedOrder" />
          <OrderServiceDetail :order="store.selectedOrder" />
          <OrderPaymentTimeline :order="store.selectedOrder" />
        </div>

        <!-- Right Column -->
        <div class="detail-right">
          <OrderStatusCard :order="store.selectedOrder" />
          <OrderCustomerCard :order="store.selectedOrder" />
          <OrderActionCard />
          <p class="meta-note">
            * Hóa đơn được tạo tự động bởi hệ thống thanh toán tích hợp. Mọi thay đổi trạng thái sẽ được ghi lại trong nhật ký hệ thống.
          </p>
        </div>
      </div>
    </template>

  </div>
</template>

<style scoped>
.order-detail-page {
  margin: 0 auto;
}

.loading-state,
.error-state {
  display: flex; align-items: center; justify-content: center;
  gap: 10px; padding: 5rem;
  font-size: 0.875rem; font-weight: 500; color: #64748b;
}
.error-state { color: #dc2626; }
@keyframes spin { to { transform: rotate(360deg); } }

.detail-layout {
  display: flex;
  gap: 32px;
}

.detail-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.detail-right {
  width: 320px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  flex-shrink: 0;
}

.meta-note {
  padding: 0 8px;
  font-size: 0.75rem;
  color: #94a3b8;
  line-height: 1.6;
  font-style: italic;
}

@media (max-width: 1024px) {
  .detail-layout {
    flex-direction: column;
  }
  .detail-right {
    width: 100%;
  }
}
</style>