<template>
  <div class="sp-page">
    <!-- Loading skeleton -->
    <div v-if="store.loading" class="sp-loading">
      <div class="sp-skeleton" v-for="n in 3" :key="n" />
    </div>

    <template v-else>
      <!-- API 1a: Gói subscription hiện tại -->
      <ServiceCurrentPlan :subscription="store.subscription" @renew="showRenewModal = true" />

      <!-- API 1b: Hạn mức tính năng từ usages[] -->
      <ServiceQuotaGrid v-if="quotaItems.length" :quotas="quotaItems" />

      <!-- API 2: Dịch vụ lẻ đang có -->
      <ServiceActiveList :services="activeAddonItems" @apply="handleApply" />

      <ServicePromoSection />
    </template>

  </div>

  <!-- Modal gia hạn -->
  <ServiceRenewModal
    v-if="store.subscription"
    :visible="showRenewModal"
    :loading="store.renewing"
    :package-name="store.subscription.packageName ?? store.subscription.packageCode ?? 'Gói dịch vụ'"
    :billing-cycle="store.subscription.billingCycle"
    @close="showRenewModal = false"
    @confirm="handleRenew"
  />
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import ServiceCurrentPlan from '@/components/recruiter/services/ServiceCurrentPlan.vue'
import ServiceQuotaGrid, { type QuotaItem } from '@/components/recruiter/services/ServiceQuotaGrid.vue'
import ServiceActiveList, { type ActiveService } from '@/components/recruiter/services/ServiceActiveList.vue'
import ServicePromoSection from '@/components/recruiter/services/ServicePromoSection.vue'
import ServiceRenewModal from '@/components/recruiter/services/ServiceRenewModal.vue'
import { useToast } from '@/composables/useToast'
import { useEmployerServiceManagementStore } from '@/stores/employerServiceManagement.store'
import { SubscriptionStatus, PaymentMethod } from '@/constants/servicePackage.constants'
import { SERVICE_CATEGORY_ICON_MAP, ServiceCategory } from '@/constants/serviceCatalog.constants'

const FEATURE_CODE_ICON: Record<string, { icon: string; iconBg: string; iconColor: string }> = {
    hot_job_quota:   { icon: 'campaign',      iconBg: '#eff6ff', iconColor: '#2563eb' },
    cv_search_quota: { icon: 'person_search', iconBg: '#ecfdf5', iconColor: '#059669' },
    top_brand_badge: { icon: 'verified',      iconBg: '#fdf4ff', iconColor: '#9333ea' },
    unlimited_post:  { icon: 'work',          iconBg: '#fff7ed', iconColor: '#ea580c' },
    cv_access_quota: { icon: 'lock_open',     iconBg: '#ecfdf5', iconColor: '#059669' },
    extend_job:      { icon: 'event_repeat',  iconBg: '#eff6ff', iconColor: '#2563eb' },
}

const store             = useEmployerServiceManagementStore()
const router            = useRouter()
const toast             = useToast()
const showRenewModal    = ref(false)

// ─── Computed: map usages[] → QuotaItem[] ────────────────────────────────────
const quotaItems = computed<QuotaItem[]>(() => {
    const usages = store.subscription?.usages ?? []
    return usages.map(u => {
        const meta      = FEATURE_CODE_ICON[u.featureCode]
        const unlimited = u.quantityTotal === -1
        return {
            label:     u.featureName   ?? u.featureCode,
            icon:      meta?.icon      ?? 'star',
            iconBg:    meta?.iconBg    ?? '#f1f5f9',
            iconColor: meta?.iconColor ?? '#64748b',
            unlimited,
            remaining: unlimited ? undefined : u.quantityRemaining,
            total:     unlimited ? undefined : u.quantityTotal,
            resetAt:   u.resetAt,
        }
    })
})

// ─── Computed: map addons[] → ActiveService[] ─────────────────────────────────
const activeAddonItems = computed<ActiveService[]>(() =>
    store.addons.map(addon => {
        const iconMeta = addon.serviceCategory
            ? SERVICE_CATEGORY_ICON_MAP[addon.serviceCategory]
            : null

        return {
            id:          addon.id,
            name:        addon.addonName ?? addon.addonCode ?? 'Dịch vụ lẻ',
            description: (addon as any).addonDescription ?? null,
            icon:        iconMeta?.icon      ?? 'star',
            iconBg:      iconMeta?.iconBg    ?? '#f1f5f9',
            iconColor:   iconMeta?.iconColor ?? '#64748b',
            remaining:   `Còn ${addon.quantityRemaining} lượt`,
            expireDate:  addon.expiredAt
                ? dayjs(addon.expiredAt).format('DD/MM/YYYY')
                : null,
            status:   addon.status === SubscriptionStatus.ACTIVE ? 'active' : 'expired',
            category: addon.serviceCategory ?? '',
        }
    }),
)

// ─── Handlers ─────────────────────────────────────────────────────────────────

async function handleRenew(paymentMethod: PaymentMethod) {
  try {
    const result = await store.renewSubscription({ paymentMethod })
    showRenewModal.value = false
    const newExpiry = dayjs(result.subscription.newExpiredAt).format('DD/MM/YYYY')
    toast.success(
      'Gia hạn thành công',
      `Gói "${result.subscription.packageName}" đã được gia hạn đến ${newExpiry}.`,
    )
  } catch {
    toast.error('Gia hạn thất bại', store.error ?? 'Vui lòng thử lại.')
  }
}

async function handleApply(svc: ActiveService) {
    if (svc.category === ServiceCategory.JOB_POSTING) {
        router.push({ name: 'recruiter-jobs' })
        return
    }

    try {
        await store.applyBrandingToCompany({ companyAddonId: svc.id })
        await store.fetchMyAddons()
        toast.success('Áp dụng thành công', `"${svc.name}" đã được kích hoạt.`)
    } catch {
        toast.error('Áp dụng thất bại', store.error ?? 'Vui lòng thử lại.')
    }
}

onMounted(() => {
    store.fetchMySubscription()
    store.fetchMyAddons()
})
</script>

<style scoped>
.sp-page {
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 32px;
}
.sp-loading {
  display: flex;
  flex-direction: column;
  gap: 32px;
}
.sp-skeleton {
  background: #f1f5f9;
  border-radius: 12px;
  height: 120px;
  animation: pulse 1.5s ease-in-out infinite;
}
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50%       { opacity: 0.5; }
}
</style>
