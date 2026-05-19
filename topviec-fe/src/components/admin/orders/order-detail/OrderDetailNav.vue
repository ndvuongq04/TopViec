<script setup lang="ts">
import { computed } from 'vue'
import Breadcrumb from '@/components/ui/Breadcrumb.vue'

const props = defineProps<{ orderCode: string }>()

const emit = defineEmits<{
  exportInvoice: []
  printOrder: []
}>()

const breadcrumbs = computed(() => [
  { label: 'Đơn hàng', to: '/admin/orders' },
  { label: props.orderCode },
])
</script>

<template>
  <div class="detail-nav">
    <Breadcrumb :items="breadcrumbs" :hide-home="true" />
    <div class="nav-right">
      <button class="btn-outline" @click="emit('exportInvoice')">XUẤT HÓA ĐƠN</button>
      <button class="btn-primary" @click="emit('printOrder')">IN ĐƠN HÀNG</button>
    </div>
  </div>
</template>

<style scoped>
.detail-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 32px;
  padding: 8px 0;
  border-bottom: 1px solid #e2e8f0;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.75rem;
  font-weight: 600;
  color: #94a3b8;
  background: none;
  border: none;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  transition: color 0.2s;
}

.nav-link:hover {
  color: #963131;
}

.icon-sm {
  font-size: 14px;
}

.nav-divider {
  width: 1px;
  height: 16px;
  background: #e7e5e4;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn-outline {
  background: #ffffff;
  color: #475569;
  font-size: 0.75rem;
  font-weight: 700;
  padding: 8px 16px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
  transition: background 0.2s;
}

.btn-outline:hover {
  background: #f8fafc;
}

.btn-primary {
  background: linear-gradient(135deg, #76191d 0%, #963131 100%);
  color: #ffffff;
  font-size: 0.75rem;
  font-weight: 700;
  padding: 8px 16px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  box-shadow: 0 4px 6px rgba(118,25,29,0.15);
}
</style>