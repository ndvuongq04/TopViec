<template>
  <div class="bg-white dark:bg-white/5 rounded-xl border border-slate-200 dark:border-white/10 overflow-hidden">
    <div class="p-6 border-b border-slate-100 dark:border-white/10 flex justify-between items-center">
      <h4 class="font-bold text-lg">Đơn hàng cần xử lý</h4>
      <router-link to="/admin/orders" class="text-[#963131] text-sm font-bold hover:underline flex items-center gap-1">
        Xem tất cả
        <span class="material-symbols-outlined text-sm">arrow_forward</span>
      </router-link>
    </div>

    <div class="overflow-x-auto">
      <table class="w-full text-left">
        <thead>
          <tr class="bg-slate-50 dark:bg-white/5 text-slate-500 text-xs uppercase tracking-wider">
            <th class="px-6 py-4 font-black">Mã đơn</th>
            <th class="px-6 py-4 font-black">Công ty</th>
            <th class="px-6 py-4 font-black">Số tiền</th>
            <th class="px-6 py-4 font-black">Trạng thái</th>
            <th class="px-6 py-4 font-black">Ngày tạo</th>
            <th class="px-6 py-4 font-black">Hành động</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-100 dark:divide-white/10">
          <tr v-for="order in orders" :key="order.orderId" class="hover:bg-slate-50/50 dark:hover:bg-white/5 transition-colors">
            <td class="px-6 py-4">
              <span class="font-bold text-slate-700 dark:text-slate-200 text-sm">{{ order.orderCode }}</span>
            </td>
            <td class="px-6 py-4">
              <span class="text-slate-600 dark:text-slate-400 text-sm line-clamp-1">{{ order.companyName || '-' }}</span>
            </td>
            <td class="px-6 py-4">
              <span class="font-bold text-emerald-600 dark:text-emerald-400 text-sm">{{ formatCurrency(order.totalAmount) }}</span>
            </td>
            <td class="px-6 py-4">
              <span class="px-2 py-1 rounded text-[10px] font-bold uppercase" 
                :class="statusClass(order.status)">
                {{ statusLabel(order.status) }}
              </span>
            </td>
            <td class="px-6 py-4">
              <span class="text-slate-400 text-xs">{{ formatDate(order.createdAt) }}</span>
            </td>
            <td class="px-6 py-4">
              <button @click="onView(order.orderId)" class="text-[#963131] hover:text-[#7a2828] text-sm font-bold flex items-center gap-1">
                <span class="material-symbols-outlined text-sm">payments</span>
                Chi tiết
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import type { ActionableOrder } from '@/types/dashboard.types';

defineProps<{
  orders: ActionableOrder[];
}>();

const router = useRouter();

const formatCurrency = (val: number) => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
};

const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleDateString('vi-VN');
};

const statusClass = (status: string) => {
  switch (status) {
    case 'pending': return 'bg-amber-100 text-amber-600';
    case 'refund_requested': return 'bg-rose-100 text-rose-600';
    default: return 'bg-slate-100 text-slate-600';
  }
};

const statusLabel = (status: string) => {
  switch (status) {
    case 'pending': return 'Pending';
    case 'refund_requested': return 'Refund requested';
    default: return status;
  }
};

const onView = (id: number) => {
  router.push(`/admin/orders/${id}`);
};
</script>
