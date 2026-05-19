<template>
  <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
    <AdminKpiCard
      v-for="card in cards"
      :key="card.label"
      :icon="card.icon"
      :label="card.label"
      :value="card.value"
      :badge="card.badge"
      :note="card.note"
      :badge-type="card.badgeType"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import AdminKpiCard from '@/components/admin/dashboard/AdminKpiCard.vue'
import { useAdminOrderStore } from '@/stores/order.store'

const store = useAdminOrderStore()

const formatCurrency = (val: number) => {
  if (val >= 1000000000) return (Math.floor(val / 10000000) / 100).toLocaleString('vi-VN', { minimumFractionDigits: 1, maximumFractionDigits: 2 }) + 'B'
  if (val >= 1000000) return (Math.floor(val / 10000) / 100).toLocaleString('vi-VN', { minimumFractionDigits: 1, maximumFractionDigits: 2 }) + 'M'
  if (val >= 1000) {
    const kValue = Math.floor(val / 100) / 10
    return kValue.toLocaleString('vi-VN', { minimumFractionDigits: 1, maximumFractionDigits: 1 }) + 'K'
  }
  return val.toLocaleString('vi-VN')
}

const cards = computed(() => {
  const s = store.orderStatistics
  const paidRate = s?.totalOrders ? ((s.paidOrders / s.totalOrders) * 100).toFixed(1) : '0'

  return [
    {
      icon: 'receipt_long',
      label: 'Tổng đơn hàng',
      value: s?.totalOrders.toLocaleString() ?? '0',
      badge: 'Tất cả',
      note: 'Tổng số đơn trong hệ thống',
      badgeType: 'info' as const,
    },
    {
      icon: 'check_circle',
      label: 'Đã thanh toán',
      value: s?.paidOrders.toLocaleString() ?? '0',
      badge: `${paidRate}%`,
      note: 'Tỉ lệ thanh toán thành công',
      badgeType: 'success' as const,
    },
    {
      icon: 'pending',
      label: 'Chờ xử lý',
      value: s?.pendingOrders.toLocaleString() ?? '0',
      badge: 'Pending',
      note: 'Đơn đang chờ thanh toán',
      badgeType: 'warning' as const,
    },
    {
      icon: 'monetization_on',
      label: 'Tổng doanh thu',
      value: s ? `${formatCurrency(s.totalRevenue)} đ` : '0 đ',
      badge: 'Paid Only',
      note: 'Doanh thu từ đơn đã PAID',
      badgeType: 'success' as const,
    },
  ]
})
</script>
