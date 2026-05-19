<template>
  <div>
    <h2 class="payment__title">Phương thức thanh toán</h2>
    <div class="payment__card">
      <div class="payment__methods">
        <div
          v-for="m in methods" :key="m.id"
          :class="['payment__method', selectedMethod === m.id && 'payment__method--active', `payment__method--${m.id}`]"
          @click="selectedMethod = m.id"
        >
          <div class="payment__method-left">
            <div :class="['payment__method-logo', `payment__method-logo--${m.id}`]">
              <span class="payment__method-logo-text">{{ m.logoText }}</span>
            </div>
            <div>
              <p class="payment__method-name">{{ m.name }}</p>
              <p class="payment__method-desc">{{ m.desc }}</p>
            </div>
          </div>
          <span v-if="selectedMethod === m.id" class="material-symbols-outlined payment__method-check">check_circle</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

defineProps<{ subtotal: number; total: number }>()

const modelValue = defineModel<string>({ default: 'vnpay' })
const methods = [
  { id: 'vnpay', logoText: 'VNPAY', name: 'Cổng thanh toán VNPay', desc: 'Thanh toán qua ứng dụng ngân hàng' },
  { id: 'momo',  logoText: 'MOMO',  name: 'Ví điện tử MoMo',       desc: 'Nhanh chóng và tiện lợi' },
]

const selectedMethod = computed({
  get: () => modelValue.value,
  set: (val) => { modelValue.value = val }
})
</script>

<style scoped>
.payment__title { font-size: 1.125rem; font-weight: 800; margin-bottom: 1.5rem; }
.payment__card {
  background: #fff;
  border-radius: 0.75rem;
  padding: 1.5rem;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  border: 1px solid #e2e8f0;
  display: flex; flex-direction: column; gap: 1.5rem;
}
.payment__methods { display: flex; flex-direction: column; gap: 12px; }
.payment__method {
  cursor: pointer;
  border: 1px solid #e2e8f0;
  border-radius: 0.75rem;
  padding: 1rem;
  display: flex; align-items: center; justify-content: space-between;
  transition: border-color 0.2s;
}
.payment__method:hover { border-color: rgba(75,154,246,0.4); }
.payment__method--active { border: 2px solid #4B9AF6; background: rgba(219,234,254,0.1); }
.payment__method--momo:hover { border-color: rgba(165,0,100,0.4); }

.payment__method-left { display: flex; align-items: center; gap: 1rem; }
.payment__method-logo {
  width: 40px; height: 40px;
  border-radius: 0.5rem;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  flex-shrink: 0;
}
.payment__method-logo--vnpay { background: #fff; border: 1px solid #e2e8f0; }
.payment__method-logo--momo  { background: #A50064; }
.payment__method-logo-text { font-size: 0.75rem; font-weight: 900; }
.payment__method-logo--vnpay .payment__method-logo-text { color: #1d4ed8; }
.payment__method-logo--momo  .payment__method-logo-text { color: #fff; }
.payment__method-name { font-size: 0.875rem; font-weight: 800; }
.payment__method-desc { font-size: 0.75rem; color: #64748b; }
.payment__method-check { color: #4B9AF6; font-variation-settings: 'FILL' 1; font-size: 1.25rem; }
</style>