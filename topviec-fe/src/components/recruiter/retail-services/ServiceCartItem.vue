<template>
  <div class="cart-item">
    <div class="item-icon" :style="{ background: item.iconBg }">
      <span class="material-symbols-outlined" :style="{ color: item.iconColor }">{{ item.icon }}</span>
    </div>
    <div class="item-info">
      <div class="item-row">
        <span class="item-name">{{ item.name }}</span>
        <button class="remove-btn" @click="$emit('remove')">
          <span class="material-symbols-outlined">close</span>
        </button>
      </div>
      <div class="item-row">
        <span class="item-qty">Số lượng: <strong>{{ item.qty }}</strong></span>
        <span class="item-price">{{ formatPrice(item.price * item.qty) }}đ</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  item: { id: number; name: string; icon: string; iconBg: string; iconColor: string; price: number; qty: number }
}>()
defineEmits<{ remove: [] }>()
function formatPrice(n: number) { return n.toLocaleString('vi-VN') }
</script>

<style scoped>
.cart-item { display: flex; gap: 12px; }
.item-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}
.item-icon .material-symbols-outlined { font-size: 20px; }
.item-info { flex: 1; min-width: 0; }
.item-row { display: flex; justify-content: space-between; align-items: center; }
.item-name { font-size: 0.875rem; font-weight: 700; color: #0f172a; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 180px; }
.remove-btn { background: none; border: none; cursor: pointer; color: #cbd5e1; padding: 0; transition: color 0.15s; }
.remove-btn:hover { color: #ef4444; }
.remove-btn .material-symbols-outlined { font-size: 18px; }
.item-qty { font-size: 0.75rem; color: #64748b; margin-top: 4px; }
.item-qty strong { color: #334155; }
.item-price { font-size: 0.875rem; font-weight: 800; color: #0f172a; }
</style>