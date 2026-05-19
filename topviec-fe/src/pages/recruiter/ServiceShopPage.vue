<template>
  <main class="page-layout">
    <Breadcrumb
      :items="[
        { label: 'Dịch vụ', to: { name: 'recruiter-services' } },
        { label: 'Dịch vụ lẻ' },
      ]"
      hide-home
    />

    <!-- Loading -->
    <div v-if="store.loading" class="page-loading">
      <span class="material-symbols-outlined animate-spin">progress_activity</span>
      Đang tải danh sách dịch vụ...
    </div>

    <div v-else class="content-grid">
      <!-- Left: Service Groups -->
      <div class="left-col">
        <ServiceGroupSection
          v-for="group in serviceGroups"
          :key="group.id"
          :group="group"
          @add-to-cart="handleAddToCart"
        />
        <ServicePromoBanner />
      </div>

      <!-- Right: Cart -->
      <aside class="right-col">
        <ServiceCart
          :items="store.cartItems"
          @remove="store.removeFromCart"
          @clear="store.clearCart"
          @checkout="handleCheckout"
        />
        <ServiceSupportCard />
      </aside>
    </div>
  </main>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Breadcrumb from '@/components/ui/Breadcrumb.vue'
import ServiceGroupSection from '@/components/recruiter/retail-services/ServiceGroupSection.vue'
import ServicePromoBanner from '@/components/recruiter/retail-services/ServicePromoBanner.vue'
import ServiceCart from '@/components/recruiter/retail-services/ServiceCart.vue'
import ServiceSupportCard from '@/components/recruiter/retail-services/ServiceSupportCard.vue'
import { useEmployerOrderStore } from '@/stores/order.store'

const store  = useEmployerOrderStore()
const router = useRouter()

// ─── Icon / màu sắc theo groupCode ───────────────────────────────────────────
const GROUP_VISUAL: Record<string, { icon: string; iconBg: string; iconColor: string }> = {
  JOB_POSTING: { icon: 'campaign',      iconBg: '#fffbeb', iconColor: '#f59e0b' },
  CANDIDATE:   { icon: 'person_search', iconBg: '#eff6ff', iconColor: '#3b82f6' },
  BRANDING:    { icon: 'business',      iconBg: '#ecfdf5', iconColor: '#10b981' },
}
const DEFAULT_VISUAL = { icon: 'extension', iconBg: '#f1f5f9', iconColor: '#94a3b8' }

// ─── Map ResAddonPackageDTO[] → serviceGroups[] ───────────────────────────────
const serviceGroups = computed(() => {
  const map = new Map<string, {
    id: string; label: string
    services: {
      id: number; icon: string; iconBg: string; iconColor: string
      name: string; desc: string; price: number; unit: string
    }[]
  }>()

  for (const pkg of store.activeAddonPackages) {
    const key   = pkg.serviceCategory ?? 'OTHER'
    const label = pkg.serviceCategoryName ?? 'Khác'
    if (!map.has(key)) {
      map.set(key, { id: key, label, services: [] })
    }
    const visual = GROUP_VISUAL[key] ?? DEFAULT_VISUAL
    map.get(key)!.services.push({
      id:        pkg.id,
      icon:      visual.icon,
      iconBg:    visual.iconBg,
      iconColor: visual.iconColor,
      name:      pkg.name,
      desc:      pkg.description ?? pkg.name,
      price:     pkg.price,
      unit:      pkg.durationDays !== null ? `/${pkg.durationDays} ngày` : '/lượt',
    })
  }

  return Array.from(map.values())
})

function handleAddToCart(service: { id: number; icon: string; iconBg: string; iconColor: string; name: string; price: number }, qty: number) {
  store.addToCart({ ...service, qty })
}

function handleCheckout() {
  store.checkoutContext = { type: 'addon' }
  router.push({ name: 'recruiter-checkout' })
}

onMounted(() => store.fetchActiveAddonPackages())
</script>

<style scoped>
.page-layout {
  min-height: 100vh;
  background: #f6f6f8;
}
.page-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 120px 0;
  font-size: 0.875rem;
  color: #94a3b8;
}
.content-grid {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 32px;
  align-items: start;
  margin-top: 24px;
}
.left-col { display: flex; flex-direction: column; gap: 40px; }
.right-col { position: sticky; top: 88px; display: flex; flex-direction: column; gap: 16px; }

@media (max-width: 1024px) {
  .content-grid { grid-template-columns: 1fr; }
  .right-col { position: static; }
}
</style>
