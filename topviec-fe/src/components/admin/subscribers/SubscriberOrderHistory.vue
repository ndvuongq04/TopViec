<script setup lang="ts">
import { computed } from 'vue'
import { useAdminCompanyStore } from '@/stores/adminCompany.store'
import {
  OrderType,
  OrderStatus,
  ORDER_TYPE_LABELS,
  ORDER_STATUS_LABELS,
} from '@/constants/servicePackage.constants'

const store = useAdminCompanyStore()

const formatCurrency = (amount: number) => {
  return amount.toLocaleString('vi-VN') + ' đ'
}

const statusClass = (status: string) => {
  return {
    [OrderStatus.PAID]:      'bg-emerald-50 text-emerald-700',
    [OrderStatus.PENDING]:   'bg-amber-50 text-amber-700',
    [OrderStatus.FAILED]:    'bg-red-50 text-red-700',
    [OrderStatus.CANCELLED]: 'bg-slate-100 text-slate-600',
    [OrderStatus.REFUNDED]:  'bg-slate-100 text-slate-600',
  }[status as OrderStatus] || 'bg-slate-100 text-slate-600'
}

const typeClass = (type: string) => {
  return type === OrderType.SUBSCRIPTION
    ? 'bg-emerald-50 text-emerald-700'
    : 'bg-violet-50 text-violet-700'
}

const orders = computed(() => {
  const items = store.companyOrders?.result || []
  return items.map(o => {
    const detail = o.items?.map(i => i.packageName || 'Dịch vụ').join(', ') || 'Chưa có thông tin'
    return {
      code: o.orderCode,
      type: ORDER_TYPE_LABELS[o.type as OrderType] || o.type,
      typeCss: typeClass(o.type),
      detail: detail,
      amount: formatCurrency(o.totalAmount),
      status: ORDER_STATUS_LABELS[o.status as OrderStatus] || o.status,
      statusCss: statusClass(o.status)
    }
  })
})
</script>

<template>
  <section class="orders-section">
    <div class="orders-header">
      <h3 class="section-heading">Lịch sử đơn hàng</h3>
      <a href="#" class="view-all">Xem tất cả</a>
    </div>

    <div class="table-wrapper">
      <table class="data-table">
        <thead>
          <tr>
            <th>Mã đơn</th>
            <th>Loại</th>
            <th>Chi tiết</th>
            <th>Số tiền</th>
            <th>Trạng thái</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="!orders.length">
            <td colspan="5" class="text-center py-8 text-slate-500">Chưa có đơn hàng nào</td>
          </tr>
          <tr v-for="(o, i) in orders" :key="i">
            <td class="cell-code">{{ o.code }}</td>
            <td>
              <span class="inline-block px-3 py-1 rounded-full text-xs font-bold whitespace-nowrap" :class="o.typeCss">{{ o.type }}</span>
            </td>
            <td>{{ o.detail }}</td>
            <td class="cell-amount">{{ o.amount }}</td>
            <td>
              <span class="inline-block px-3 py-1 rounded-full text-xs font-bold whitespace-nowrap" :class="o.statusCss">{{ o.status }}</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<style scoped>
.orders-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-heading {
  font-size: 1.125rem;
  font-weight: 700;
  color: #1b1c18;
  letter-spacing: -0.02em;
  margin: 0;
}

.view-all {
  font-size: 0.875rem;
  font-weight: 700;
  color: #963131;
  text-decoration: none;
}

.view-all:hover {
  text-decoration: underline;
}

.table-wrapper {
  background: #ffffff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.data-table {
  width: 100%;
  text-align: left;
  border-collapse: collapse;
}

.data-table thead tr {
  background: #f6f4ec;
}

.data-table th {
  padding: 16px 24px;
  font-size: 0.75rem;
  font-weight: 700;
  color: #574240;
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

.data-table tbody tr {
  border-top: 1px solid #f0eee7;
  transition: background 0.15s;
}

.data-table tbody tr:hover {
  background: #f6f4ec;
}

.data-table td {
  padding: 16px 24px;
  font-size: 0.875rem;
  color: #1b1c18;
}

.cell-code {
  font-weight: 700;
  color: #963131;
}

.cell-type {
  font-size: 0.75rem;
  font-weight: 500;
  color: #574240;
}

.cell-amount {
  font-weight: 700;
}
</style>