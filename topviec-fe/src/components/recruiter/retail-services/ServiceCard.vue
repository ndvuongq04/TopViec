<template>
  <div class="card">
    <div class="card-top">
      <div class="icon-wrap" :style="{ background: service.iconBg }">
        <span class="material-symbols-outlined" :style="{ color: service.iconColor }">
          {{ service.icon }}
        </span>
      </div>
      <div class="price-block">
        <span class="price-label">Giá chỉ từ</span>
        <p class="price-value">
          {{ formatPrice(service.price) }}đ<span class="price-unit">{{ service.unit }}</span>
        </p>
      </div>
    </div>

    <h5 class="service-name">{{ service.name }}</h5>
    <p class="service-desc">{{ service.desc }}</p>

    <div class="card-footer">
      <div class="qty-control">
        <button class="qty-btn" @click="decrease">
          <span class="material-symbols-outlined">remove</span>
        </button>
        <span class="qty-value">{{ qty }}</span>
        <button class="qty-btn" @click="increase">
          <span class="material-symbols-outlined">add</span>
        </button>
      </div>
      <button class="add-btn" @click="$emit('add', qty)">Thêm vào giỏ</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

defineProps<{
  service: {
    id: number
    icon: string
    iconBg: string
    iconColor: string
    name: string
    desc: string
    price: number
    unit: string
  }
}>()

const emit = defineEmits<{ add: [qty: number] }>()

const qty = ref(1)
function increase() { qty.value++ }
function decrease() { if (qty.value > 1) qty.value-- }
function formatPrice(n: number) { return n.toLocaleString('vi-VN') }
</script>

<style scoped>
.card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid transparent;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  transition: box-shadow 0.2s, border-color 0.2s;
  display: flex;
  flex-direction: column;
}
.card:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.10);
  border-color: rgba(75, 154, 246, 0.2);
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}
.icon-wrap {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.icon-wrap .material-symbols-outlined { font-size: 28px; }

.price-block { text-align: right; }
.price-label { font-size: 0.75rem; color: #94a3b8; font-weight: 500; }
.price-value { font-size: 1.125rem; font-weight: 800; color: #0f172a; margin: 2px 0 0; }
.price-unit { font-size: 0.75rem; font-weight: 400; color: #64748b; }

.service-name { font-size: 1rem; font-weight: 700; color: #0f172a; margin: 0 0 8px; }
.service-desc { font-size: 0.875rem; color: #64748b; line-height: 1.6; margin: 0 0 16px; flex: 1; }

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 16px;
  border-top: 1px solid #f8fafc;
  margin-top: auto;
}

.qty-control {
  display: flex;
  align-items: center;
  background: #f1f5f9;
  border-radius: 8px;
  padding: 4px;
  gap: 2px;
}
.qty-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.15s;
}
.qty-btn:hover { background: #fff; }
.qty-btn .material-symbols-outlined { font-size: 16px; color: #334155; }
.qty-value { padding: 0 10px; font-size: 14px; font-weight: 700; color: #0f172a; min-width: 24px; text-align: center; }

.add-btn {
  padding: 8px 16px;
  border: 2px solid #4B9AF6;
  color: #4B9AF6;
  background: transparent;
  font-size: 0.75rem;
  font-weight: 700;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.15s, color 0.15s, transform 0.1s;
}
.add-btn:hover { background: #4B9AF6; color: #fff; }
.add-btn:active { transform: scale(0.96); }
</style>