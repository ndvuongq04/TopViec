<template>
  <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
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
import { useServiceCatalogStore } from '@/stores/serviceCatalog.store'

const store = useServiceCatalogStore()

const formatCurrency = (val: number) => {
  if (val >= 1000000) return (Math.floor(val / 10000) / 100).toLocaleString('vi-VN', { minimumFractionDigits: 1, maximumFractionDigits: 2 }) + 'M'
  if (val >= 1000) {
    const kValue = Math.floor(val / 100) / 10
    return kValue.toLocaleString('vi-VN', { minimumFractionDigits: 1, maximumFractionDigits: 1 }) + 'K'
  }
  return val.toLocaleString('vi-VN')
}

const cards = computed(() => {
  const s = store.serviceStatistics
  
  return [
    {
      icon: 'inventory_2',
      label: 'Tổng số gói',
      value: s?.totalServicePackages.toString().padStart(2, '0') ?? '00',
      badge: 'Active',
      note: 'Các gói đang kinh doanh',
      badgeType: 'success' as const,
    },
    {
      icon: 'payments',
      label: 'Doanh thu TB',
      value: s ? formatCurrency(s.averageRevenue) : '0',
      badge: '/ order',
      note: 'Tính trên mỗi đơn hàng',
      badgeType: 'info' as const,
    },
    {
      icon: 'trending_up',
      label: 'Conversion Rate',
      value: (s?.conversionRate ?? 0).toFixed(1) + '%',
      badge: 'User Base',
      note: 'Tỉ lệ khách hàng trả phí',
      badgeType: 'success' as const,
    },
  ]
})
</script>
