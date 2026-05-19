<template>
  <!-- Trạng thái: chưa đăng ký gói -->
  <section v-if="!subscription" class="scp-card scp-empty">
    <div class="scp-bg-icon">
      <span class="material-symbols-outlined">workspace_premium</span>
    </div>
    <div class="scp-left">
      <span class="scp-label">GÓI HIỆN TẠI</span>
      <p class="scp-empty-text">Bạn chưa đăng ký gói dịch vụ nào.</p>
    </div>
    <div class="scp-actions">
      <button class="scp-btn-primary" @click="router.push('/recruiter/pricing')">Xem gói dịch vụ</button>
    </div>
  </section>

  <!-- Trạng thái: đã có gói -->
  <section v-else class="scp-card" :class="{ 'scp-expired': isExpired }">
    <div class="scp-bg-icon">
      <span class="material-symbols-outlined">workspace_premium</span>
    </div>
    <div class="scp-left">
      <div class="scp-top-row">
        <span class="scp-label">GÓI HIỆN TẠI</span>
        <span class="scp-badge">{{ subscription.packageName ?? subscription.packageCode }}</span>
        <span class="scp-status-badge" :class="isExpired ? 'expired' : 'active'">
          {{ isExpired ? 'Hết hạn' : 'Đang hoạt động' }}
        </span>
      </div>
      <div class="scp-controls">
        <div class="scp-toggle">
          <button
            :class="['scp-toggle-btn', billingCycle === 'yearly' ? 'active' : '']"
            :disabled="billingCycle !== 'yearly'"
          >Hàng năm</button>
          <button
            :class="['scp-toggle-btn', billingCycle === 'monthly' ? 'active' : '']"
            :disabled="billingCycle !== 'monthly'"
          >Hàng tháng</button>
        </div>
        <div class="scp-expire" :class="{ 'scp-expire--warning': daysLeft <= 7 && !isExpired }">
          <span class="material-symbols-outlined">event_repeat</span>
          <span v-if="isExpired">Đã hết hạn {{ formattedExpireDate }}</span>
          <span v-else-if="subscription.expiredAt">
            Còn {{ daysLeft }} ngày • Hết hạn {{ formattedExpireDate }}
          </span>
          <span v-else>Không có thời hạn</span>
        </div>
      </div>
    </div>
    <div class="scp-actions">
      <button class="scp-btn-outline" @click="router.push('/recruiter/pricing')">Đổi gói</button>
      <button class="scp-btn-primary" :disabled="!isExpired && daysLeft > 30" @click="$emit('renew')">Gia hạn</button>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import type { ResCompanySubscriptionDTO } from '@/types/servicePackage.types'
import { SubscriptionStatus, BillingCycle } from '@/constants/servicePackage.constants'

const props = defineProps<{
  subscription: ResCompanySubscriptionDTO | null
}>()

defineEmits<{ (e: 'renew'): void }>()

const router       = useRouter()
const billingCycle = computed<'yearly' | 'monthly'>(() =>
  props.subscription?.billingCycle === BillingCycle.YEARLY ? 'yearly' : 'monthly',
)

const isExpired = computed(() =>
  props.subscription?.status === SubscriptionStatus.EXPIRED ||
  (!!props.subscription?.expiredAt && dayjs(props.subscription.expiredAt).isBefore(dayjs())),
)

const daysLeft = computed(() => {
  if (!props.subscription?.expiredAt) return 0
  return Math.max(0, dayjs(props.subscription.expiredAt).diff(dayjs(), 'day'))
})

const formattedExpireDate = computed(() => {
  if (!props.subscription?.expiredAt) return ''
  return dayjs(props.subscription.expiredAt).format('DD/MM/YYYY')
})
</script>

<style scoped>
.scp-card {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border: 1px solid #f8fafc;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
  overflow: hidden;
}
.scp-card.scp-expired { border-color: #fee2e2; background: #fffafa; }
.scp-card.scp-empty   { opacity: 0.85; }

.scp-bg-icon {
  position: absolute;
  top: 0; right: 0;
  padding: 48px;
  opacity: 0.05;
  pointer-events: none;
}
.scp-bg-icon .material-symbols-outlined { font-size: 120px; color: #4B9AF6; }

.scp-left { display: flex; flex-direction: column; gap: 16px; position: relative; z-index: 1; }
.scp-top-row { display: flex; align-items: center; gap: 12px; }

.scp-label {
  font-size: 0.75rem;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: #64748b;
}
.scp-badge {
  padding: 6px 16px;
  background: #EEEDFE;
  color: #3C3489;
  font-size: 14px;
  font-weight: 800;
  border-radius: 9999px;
}
.scp-status-badge {
  padding: 4px 10px;
  border-radius: 9999px;
  font-size: 12px;
  font-weight: 700;
}
.scp-status-badge.active  { background: #ecfdf5; color: #059669; }
.scp-status-badge.expired { background: #fee2e2; color: #dc2626; }

.scp-empty-text { font-size: 0.9rem; color: #64748b; font-weight: 500; }

.scp-controls { display: flex; align-items: center; gap: 24px; }
.scp-toggle {
  display: flex;
  align-items: center;
  background: #f1f4f9;
  border: 1px solid #e2e8f0;
  border-radius: 9999px;
  padding: 4px;
}
.scp-toggle-btn {
  padding: 6px 20px;
  border-radius: 9999px;
  font-size: 12px;
  font-weight: 700;
  border: none;
  background: transparent;
  color: #64748b;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  transition: all 0.2s;
}
.scp-toggle-btn.active {
  background: #fff;
  color: #4B9AF6;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}
.scp-toggle-btn:disabled {
  opacity: 0.35;
  cursor: not-allowed;
}

.scp-expire {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.875rem;
  font-weight: 600;
  color: #633806;
}
.scp-expire--warning { color: #dc2626; }
.scp-expire .material-symbols-outlined { font-size: 18px; }

.scp-actions { display: flex; align-items: center; gap: 12px; position: relative; z-index: 1; }
.scp-btn-outline {
  padding: 12px 32px;
  border: 1px solid #cbd5e1;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 700;
  background: transparent;
  color: #0f172a;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  transition: background 0.15s;
}
.scp-btn-outline:hover { background: #f8fafc; }
.scp-btn-primary {
  padding: 12px 40px;
  background: #4B9AF6;
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 800;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  box-shadow: 0 4px 12px rgba(75,154,246,0.25);
  transition: transform 0.15s, opacity 0.15s;
}
.scp-btn-primary:hover:not(:disabled) { transform: scale(0.98); }
.scp-btn-primary:disabled { opacity: 0.45; cursor: not-allowed; box-shadow: none; }
</style>
