<script setup lang="ts">
import Breadcrumb from '@/components/ui/Breadcrumb.vue'
import SubscriberInfoCard from '@/components/admin/subscribers/SubscriberInfoCard.vue'
import SubscriberCurrentPlan from '@/components/admin/subscribers/SubscriberCurrentPlan.vue'
import SubscriberQuotas from '@/components/admin/subscribers/SubscriberQuotas.vue'
import SubscriberAddons from '@/components/admin/subscribers/SubscriberAddons.vue'
import SubscriberOrderHistory from '@/components/admin/subscribers/SubscriberOrderHistory.vue'
import SubscriberRenewalTimeline from '@/components/admin/subscribers/SubscriberRenewalTimeline.vue'

const breadcrumbItems = [
  { label: 'Admin', to: '/admin' },
  { label: 'Giám sát NTT' }
]
</script>

<template>
  <div class="subscriber-page">
    <Breadcrumb :items="breadcrumbItems" />
    <SubscriberInfoCard />

    <div class="plan-quota-grid">
      <SubscriberCurrentPlan />
      <SubscriberQuotas />
    </div>

    <SubscriberAddons />

    <div class="bottom-grid">
      <SubscriberOrderHistory />
      <SubscriberRenewalTimeline />
    </div>
  </div>
</template>

<style scoped>
.subscriber-page {

  display: flex;
  flex-direction: column;
  gap: 32px;

}

.plan-quota-grid {
  display: grid;
  grid-template-columns: 4fr 8fr;
  gap: 32px;
}

.bottom-grid {
  display: grid;
  grid-template-columns: 8fr 4fr;
  gap: 32px;
  padding-bottom: 48px;
}

@media (max-width: 1024px) {
  .plan-quota-grid,
  .bottom-grid {
    grid-template-columns: 1fr;
  }
}
</style>