<template>
  <GlobalModal
    :visible="visible"
    title="Gia hạn gói dịch vụ"
    subtitle="Chọn phương thức thanh toán để gia hạn"
    icon="event_repeat"
    max-width="md"
    confirm-text="Xác nhận gia hạn"
    confirm-icon="event_repeat"
    :loading="loading"
    loading-text="Đang xử lý..."
    @close="$emit('close')"
    @confirm="handleConfirm"
  >
    <!-- Thông tin gói hiện tại -->
    <div class="srm-plan-info">
      <div class="srm-plan-icon">
        <span class="material-symbols-outlined">workspace_premium</span>
      </div>
      <div>
        <p class="srm-plan-name">{{ packageName }}</p>
        <p class="srm-plan-cycle">{{ billingCycleLabel }} • Gia hạn thêm {{ cycleText }}</p>
      </div>
    </div>

    <!-- Chọn phương thức thanh toán -->
    <p class="srm-section-label">Phương thức thanh toán</p>
    <div class="srm-methods">
      <label
        v-for="m in methods"
        :key="m.value"
        :class="['srm-method', selectedMethod === m.value && 'srm-method--active']"
      >
        <input
          class="srm-method__radio"
          type="radio"
          :value="m.value"
          v-model="selectedMethod"
        />
        <img :src="m.logo" :alt="m.label" class="srm-method__logo" />
        <span class="srm-method__label">{{ m.label }}</span>
      </label>
    </div>

    <p class="srm-note">
      <span class="material-symbols-outlined">info</span>
      Gói dịch vụ sẽ được gia hạn ngay sau khi xác nhận.
    </p>
  </GlobalModal>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import GlobalModal from '@/components/ui/GlobalModal.vue'
import { PaymentMethod, BillingCycle, BILLING_CYCLE_LABELS } from '@/constants/servicePackage.constants'

const props = defineProps<{
  visible: boolean
  loading: boolean
  packageName: string
  billingCycle: BillingCycle
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'confirm', paymentMethod: PaymentMethod): void
}>()

const selectedMethod = ref<PaymentMethod>(PaymentMethod.VNPAY)

const billingCycleLabel = computed(() => BILLING_CYCLE_LABELS[props.billingCycle])
const cycleText = computed(() =>
  props.billingCycle === BillingCycle.YEARLY ? '1 năm' : '1 tháng',
)

const methods = [
  {
    value: PaymentMethod.VNPAY,
    label: 'VNPay',
    logo: 'https://vnpay.vn/s1/statics/img/logo-new.svg',
  },
  {
    value: PaymentMethod.MOMO,
    label: 'MoMo',
    logo: 'https://upload.wikimedia.org/wikipedia/vi/f/fe/MoMo_Logo.png',
  },
]

function handleConfirm() {
  emit('confirm', selectedMethod.value)
}
</script>

<style scoped>
.srm-plan-info {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  margin-bottom: 24px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.srm-plan-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: 10px;
  background: #eeedfe;
  flex-shrink: 0;
}

.srm-plan-icon .material-symbols-outlined {
  font-size: 1.5rem;
  color: #3c3489;
}

.srm-plan-name {
  font-size: 1rem;
  font-weight: 800;
  color: #0f172a;
}

.srm-plan-cycle {
  margin-top: 2px;
  font-size: 0.875rem;
  color: #64748b;
  font-weight: 500;
}

.srm-section-label {
  margin-bottom: 12px;
  font-size: 0.875rem;
  font-weight: 700;
  color: #0f172a;
}

.srm-methods {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.srm-method {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  border-radius: 12px;
  border: 2px solid #e2e8f0;
  background: #fff;
  cursor: pointer;
  transition: border-color 0.15s, background 0.15s;
}

.srm-method--active {
  border-color: #4b9af6;
  background: #eff6ff;
}

.srm-method__radio {
  display: none;
}

.srm-method__logo {
  width: 40px;
  height: 24px;
  object-fit: contain;
}

.srm-method__label {
  font-size: 0.9rem;
  font-weight: 700;
  color: #0f172a;
}

.srm-note {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.8125rem;
  color: #64748b;
}

.srm-note .material-symbols-outlined {
  font-size: 1rem;
  color: #94a3b8;
}
</style>
