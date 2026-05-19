<template>
  <div class="checkout-layout">
    <header class="checkout-header">
      <span class="checkout-header__logo">TopViec</span>
      <CheckoutStepper :current="1" />
      <a href="#" class="checkout-header__cancel" @click.prevent="handleCancel">Huỷ</a>
    </header>

    <main class="checkout-main">
      <div class="checkout-grid">
        <div class="checkout-grid__left">
          <OrderDetail />
        </div>
        <div class="checkout-grid__right">
          <PaymentMethod 
            v-model="selectedPaymentMethod"
            :subtotal="subtotal" 
            :total="total" 
          />
          <OrderSummary 
            :subtotal="subtotal" 
            :total="total"
            :payment-method="selectedPaymentMethod"
          />
        </div>
      </div>
    </main>

    <footer class="checkout-footer">
      <div class="checkout-footer__inner">
        <p>© 2024 TopViec. All rights reserved.</p>
        <div class="checkout-footer__links">
          <a href="#">Điều khoản dịch vụ</a>
          <a href="#">Chính sách bảo mật</a>
          <a href="#">Trợ giúp</a>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useEmployerOrderStore } from '@/stores/order.store'

import CheckoutStepper from '@/components/recruiter/checkout/CheckoutStepper.vue'
import OrderDetail from '@/components/recruiter/checkout/OrderDetail.vue'
import PaymentMethod from '@/components/recruiter/checkout/PaymentMethod.vue'
import OrderSummary from '@/components/recruiter/checkout/OrderSummary.vue'

const store = useEmployerOrderStore()
const router = useRouter()
const context = store.checkoutContext
const selectedPaymentMethod = ref('vnpay')

const subtotal = computed(() => {
  if (context?.type === 'subscription' && context.packageId) {
    const pkg = store.activeServicePackages.find(p => p.id === context.packageId)
    return pkg ? pkg.price : 0
  } else {
    return store.cartItems.reduce((sum, item) => sum + (item.price * item.qty), 0)
  }
})

const total = computed(() => subtotal.value)

function handleCancel() {
  router.back()
}
</script>

<style scoped>
.checkout-layout {
  min-height: 100vh;
  background: #f6f6f8;
  color: #0f172a;
  font-family: 'Manrope', sans-serif;
  display: flex;
  flex-direction: column;
}

/* Header */
.checkout-header {
  position: fixed;
  top: 0; left: 0;
  width: 100%;
  z-index: 50;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 1.5rem;
  background: rgba(255,255,255,0.8);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid #e2e8f0;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  box-sizing: border-box;
}
.checkout-header__logo {
  font-size: 1.25rem;
  font-weight: 800;
  color: #4B9AF6;
  letter-spacing: -0.02em;
}
.checkout-header__cancel {
  font-size: 0.875rem;
  font-weight: 500;
  color: #64748b;
  text-decoration: none;
  transition: color 0.2s;
}
.checkout-header__cancel:hover { color: #ef4444; }

/* Main */
.checkout-main {
  flex: 1;
  padding: 6rem 1.5rem 3rem;
  max-width: 1152px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
}
.checkout-grid {
  display: grid;
  grid-template-columns: 7fr 5fr;
  gap: 2rem;
}
@media (max-width: 1024px) {
  .checkout-grid { grid-template-columns: 1fr; }
}
.checkout-grid__left,
.checkout-grid__right { display: flex; flex-direction: column; gap: 1.5rem; }

/* Footer */
.checkout-footer {
  border-top: 1px solid #e2e8f0;
  background: #fff;
  padding: 2rem 0;
  margin-top: 3rem;
}
.checkout-footer__inner {
  max-width: 1152px;
  margin: 0 auto;
  padding: 0 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
  font-size: 0.75rem;
  font-weight: 500;
  color: #64748b;
}
.checkout-footer__links { display: flex; gap: 1.5rem; }
.checkout-footer__links a {
  text-decoration: none;
  color: #64748b;
  transition: color 0.2s;
}
.checkout-footer__links a:hover { color: #4B9AF6; }
</style>