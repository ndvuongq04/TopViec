<template>
  <div>
    <div class="order-detail__title-row">
      <h2 class="order-detail__title">Chi tiết đơn hàng</h2>
      <span class="order-detail__badge">{{ isSubscription ? 'Mua gói' : 'Dịch vụ lẻ' }}</span>
    </div>

    <div class="order-detail__card">
      <template v-if="isSubscription && basePackage">
        <!-- Plan Header -->
        <div class="order-detail__plan-header">
          <div>
            <h3 class="order-detail__plan-name">{{ basePackage.name }}</h3>
            <p class="order-detail__plan-desc">{{ basePackage.description || 'Giải pháp tuyển dụng chuyên nghiệp' }}</p>
          </div>
          <div class="order-detail__plan-icon">
            <span class="material-symbols-outlined">verified</span>
          </div>
        </div>

        <!-- Billing Options -->
        <div class="order-detail__billing-grid">
          <label v-if="monthlyPlan" :class="['billing-option', selectedBillingMode === 'monthly' && 'billing-option--active']">
            <input v-model="selectedBillingMode" class="billing-option__radio" name="billing" type="radio" value="monthly" />
            <p class="billing-option__label">Hàng tháng</p>
            <p class="billing-option__price">{{ format(monthlyPlan.price) }}</p>
          </label>

          <label v-if="yearlyPlan" :class="['billing-option', selectedBillingMode === 'yearly' && 'billing-option--active']">
            <input v-model="selectedBillingMode" class="billing-option__radio" name="billing" type="radio" value="yearly" />
            <span class="billing-option__badge">TIẾT KIỆM</span>
            <p class="billing-option__label">Hàng năm</p>
            <p class="billing-option__price">
              {{ format(yearlyPlan.price / 12) }}<span class="billing-option__unit">/tháng</span>
            </p>
            <p v-if="monthlyPlan" class="billing-option__original">{{ format(monthlyPlan.price * 12) }}</p>
          </label>
        </div>

        <!-- Features -->
        <ul class="order-detail__features">
          <li v-for="(f, i) in featuresList" :key="i" class="order-detail__feature-item">
            <span class="material-symbols-outlined order-detail__feature-icon">check_circle</span>
            <span class="order-detail__feature-text">{{ f }}</span>
          </li>
        </ul>
      </template>

      <!-- Addon -->
      <template v-else-if="!isSubscription">
        <div class="order-detail__cart-list">
           <div v-for="item in store.cartItems" :key="item.id" class="cart-item">
              <div class="cart-item__icon" :style="{ background: item.iconBg, color: item.iconColor }">
                 <span class="material-symbols-outlined">{{ item.icon }}</span>
              </div>
              <div class="cart-item__info">
                 <p class="cart-item__name">{{ item.name }}</p>
                 <p class="cart-item__qty">Số lượng: {{ item.qty }}</p>
              </div>
              <p class="cart-item__price">{{ format(item.price * item.qty) }}</p>
           </div>
        </div>
      </template>

      <!-- Info -->
      <div v-if="isSubscription" class="order-detail__info">
        <span class="material-symbols-outlined order-detail__info-icon">info</span>
        <p class="order-detail__info-text">
          Gói của bạn sẽ có hiệu lực ngay sau khi thanh toán thành công.
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useEmployerOrderStore } from '@/stores/order.store'
import { BillingCycle } from '@/constants/servicePackage.constants'

const store = useEmployerOrderStore()
const context = store.checkoutContext

const isSubscription = computed(() => context?.type === 'subscription')

const basePackage = computed(() => {
  if (!isSubscription.value || !context?.packageId) return null
  return store.activeServicePackages.find(p => p.id === context.packageId)
})

const planPrefix = computed(() => {
  if (!basePackage.value) return ''
  return basePackage.value.code.split('_')[0]
})

const relatedPlans = computed(() => {
  return store.activeServicePackages.filter(p => p.code.split('_')[0] === planPrefix.value)
})

const monthlyPlan = computed(() => relatedPlans.value.find(p => p.billingCycle === BillingCycle.MONTHLY))
const yearlyPlan = computed(() => relatedPlans.value.find(p => p.billingCycle === BillingCycle.YEARLY))

const featuresList = computed(() => {
  if (!basePackage.value) return []
  return basePackage.value.details.map(d => {
    const qty  = d.quantity >= 999 ? 'Không giới hạn' : String(d.quantity)
    const unit = d.serviceUnit ? ` ${d.serviceUnit}` : ''
    return `${qty}${unit} ${d.serviceName}`.trim()
  })
})

const selectedBillingMode = computed({
  get: () => context?.billingCycle || BillingCycle.YEARLY,
  set: (val) => {
    if (context) {
      context.billingCycle = val
      const newPlan = val === BillingCycle.MONTHLY ? monthlyPlan.value : yearlyPlan.value
      if (newPlan) context.packageId = newPlan.id
    }
  }
})

function format(n: number) {
  return n.toLocaleString('vi-VN') + 'đ'
}
</script>

<style scoped>
/* Title row */
.order-detail__title-row { display: flex; align-items: center; justify-content: space-between; margin-bottom: 1.5rem; }
.order-detail__title { font-size: 1.125rem; font-weight: 800; }
.order-detail__badge {
  padding: 4px 12px;
  border-radius: 999px;
  background: #d1fae5;
  color: #059669;
  font-size: 0.75rem;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* Card */
.order-detail__card {
  background: #fff;
  border-radius: 0.75rem;
  padding: 1.5rem;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  border: 1px solid #e2e8f0;
}

/* Plan header */
.order-detail__plan-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 2rem; }
.order-detail__plan-name { font-size: 1.125rem; font-weight: 800; margin-bottom: 4px; }
.order-detail__plan-desc { font-size: 0.875rem; color: #64748b; }
.order-detail__plan-icon {
  width: 48px; height: 48px;
  border-radius: 0.75rem;
  background: rgba(75,154,246,0.1);
  display: flex; align-items: center; justify-content: center;
  color: #4B9AF6;
  flex-shrink: 0;
}
.order-detail__plan-icon .material-symbols-outlined { font-size: 1.875rem; }

/* Billing options */
.order-detail__billing-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 2rem; }
@media (max-width: 480px) { .order-detail__billing-grid { grid-template-columns: 1fr; } }

.billing-option {
  position: relative;
  cursor: pointer;
  border: 1px solid #e2e8f0;
  border-radius: 0.75rem;
  padding: 1rem;
  transition: border-color 0.2s;
  display: block;
}
.billing-option:hover { border-color: rgba(75,154,246,0.5); }
.billing-option--active {
  border: 2px solid #4B9AF6;
  background: rgba(219,234,254,0.2);
}
.billing-option__radio { position: absolute; top: 1rem; right: 1rem; accent-color: #4B9AF6; width: 16px; height: 16px; }
.billing-option__badge {
  position: absolute;
  top: -10px; left: 1rem;
  background: #4B9AF6;
  color: #fff;
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  padding: 2px 8px;
  border-radius: 999px;
}
.billing-option__label { font-size: 0.875rem; font-weight: 700; padding-right: 1.5rem; }
.billing-option__price { font-size: 1.125rem; font-weight: 800; margin-top: 4px; }
.billing-option__unit { font-size: 0.75rem; font-weight: 500; color: #64748b; }
.billing-option__original { font-size: 0.75rem; color: #64748b; text-decoration: line-through; }

/* Features */
.order-detail__features {
  list-style: none;
  padding: 0; margin: 0;
  display: flex; flex-direction: column; gap: 12px;
  padding-top: 1.5rem;
  border-top: 1px solid #e2e8f0;
}
.order-detail__feature-item { display: flex; align-items: center; gap: 12px; }
.order-detail__feature-icon {
  font-size: 1.25rem;
  color: #10b981;
  font-variation-settings: 'FILL' 1;
  flex-shrink: 0;
}
.order-detail__feature-text { font-size: 0.875rem; font-weight: 500; }

/* Cart List (Addon) */
.order-detail__cart-list { display: flex; flex-direction: column; gap: 1rem; }
.cart-item {
  display: flex; align-items: center; gap: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px dashed #e2e8f0;
}
.cart-item:last-child { border-bottom: none; padding-bottom: 0; }
.cart-item__icon {
  width: 40px; height: 40px;
  border-radius: 0.5rem;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.cart-item__info { flex: 1; }
.cart-item__name { font-size: 0.875rem; font-weight: 700; }
.cart-item__qty { font-size: 0.75rem; color: #64748b; margin-top: 2px; }
.cart-item__price { font-size: 1rem; font-weight: 800; color: #4B9AF6; }

/* Info box */
.order-detail__info {
  margin-top: 2rem;
  padding: 1rem;
  background: #f1f4f9;
  border-radius: 0.75rem;
  border: 1px solid rgba(226,232,240,0.5);
  display: flex; align-items: flex-start; gap: 12px;
}
.order-detail__info-icon { color: #64748b; font-size: 1.25rem; flex-shrink: 0; }
.order-detail__info-text { font-size: 0.75rem; font-weight: 500; color: #334155; line-height: 1.5; }
</style>