<template>
  <div class="cart">
    <div class="cart-header">
      <div class="cart-title">
        <span class="title-text">Giỏ hàng</span>
        <span class="badge">{{ items.length }}</span>
      </div>
      <button class="clear-btn" @click="$emit('clear')">Xóa giỏ hàng</button>
    </div>

    <div class="cart-body">
      <ServiceCartItem
        v-for="item in items"
        :key="item.id"
        :item="item"
        @remove="$emit('remove', item.id)"
      />
      <p v-if="!items.length" class="empty-msg">Giỏ hàng trống</p>
    </div>

    <div class="cart-footer">
      <div class="total-row">
        <span class="total-label">Tổng thanh toán</span>
        <span class="total-value">{{ formatTotal }}đ</span>
      </div>
      <button class="checkout-btn" :disabled="!items.length" @click="$emit('checkout')">
        Thanh toán ngay
        <span class="material-symbols-outlined">arrow_forward</span>
      </button>
      <p class="terms">
        Bằng việc thanh toán, bạn đồng ý với
        <a href="#">Điều khoản dịch vụ</a> của Azure Horizon.
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import ServiceCartItem from './ServiceCartItem.vue'

const props = defineProps<{
  items: Array<{ id: number; name: string; icon: string; iconBg: string; iconColor: string; price: number; qty: number }>
}>()

defineEmits<{ remove: [id: number]; clear: []; checkout: [] }>()

const formatTotal = computed(() =>
  props.items.reduce((s, i) => s + i.price * i.qty, 0).toLocaleString('vi-VN')
)
</script>

<style scoped>
.cart {
  background: #fff;
  border-radius: 16px;
  border: 1px solid #f1f5f9;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}
.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f8fafd;
  border-bottom: 1px solid #e2e8f0;
}
.cart-title { display: flex; align-items: center; gap: 8px; }
.title-text { font-size: 0.875rem; font-weight: 700; color: #0f172a; }
.badge {
  background: #4B9AF6;
  color: #fff;
  font-size: 0.75rem;
  font-weight: 700;
  padding: 2px 7px;
  border-radius: 99px;
}
.clear-btn {
  font-size: 0.75rem;
  color: #ef4444;
  font-weight: 600;
  background: none;
  border: none;
  cursor: pointer;
}
.clear-btn:hover { text-decoration: underline; }

.cart-body {
  padding: 20px;
  max-height: 400px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.empty-msg { font-size: 0.875rem; color: #94a3b8; text-align: center; padding: 24px 0; }

.cart-footer {
  padding: 20px 24px;
  background: #f8fafc;
  border-top: 1px solid #f1f5f9;
}
.total-row { display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 16px; }
.total-label { font-size: 0.875rem; color: #64748b; }
.total-value { font-size: 1.875rem; font-weight: 800; color: #4B9AF6; }

.checkout-btn {
  width: 100%;
  padding: 14px;
  background: #4B9AF6;
  color: #fff;
  font-size: 1rem;
  font-weight: 700;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: background 0.15s, transform 0.1s;
  box-shadow: 0 4px 12px rgba(75, 154, 246, 0.25);
}
.checkout-btn:hover { background: #2563eb; }
.checkout-btn:active { transform: scale(0.97); }
.checkout-btn:disabled {
  background: #cbd5e1;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}
.checkout-btn .material-symbols-outlined { font-size: 18px; }

.terms { font-size: 0.75rem; color: #94a3b8; text-align: center; margin-top: 12px; }
.terms a { text-decoration: underline; color: inherit; }
</style>