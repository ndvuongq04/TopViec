<template>
  <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
    <AdminKpiCard
      icon="monetization_on"
      label="Doanh thu tháng"
      :value="formatCurrency(monthlyRevenue)"
      badge="Tháng này"
      badge-type="success"
      note="Doanh thu đơn PAID tháng hiện tại"
    />
    <AdminKpiCard
      icon="hourglass_top"
      label="Đơn chờ thanh toán"
      :value="pendingOrders"
      badge="Pending"
      badge-type="warning"
      note="Số đơn hàng đang chờ thanh toán"
    />
    <AdminKpiCard
      icon="currency_exchange"
      label="Yêu cầu hoàn tiền"
      :value="refundRequests"
      badge="Refund"
      badge-type="warning"
      note="Yêu cầu REFUND_REQUESTED mới"
    />
  </div>
</template>

<script setup lang="ts">
import AdminKpiCard from '@/components/admin/dashboard/AdminKpiCard.vue';

defineProps<{
  monthlyRevenue: number;
  pendingOrders: number;
  refundRequests: number;
}>();

const formatCurrency = (val: number) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
};
</script>
