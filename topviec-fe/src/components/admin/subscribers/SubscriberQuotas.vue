<script setup lang="ts">
import { computed } from 'vue'
import { useAdminCompanyStore } from '@/stores/adminCompany.store'

const store = useAdminCompanyStore()

const featureMeta: Record<string, any> = {
  'URGENT_JOB': { icon: 'campaign', iconBg: '#ffedd5', iconColor: '#c2410c', barColor: '#ea580c' },
  'VIEW_CV': { icon: 'visibility', iconBg: '#e0e7ff', iconColor: '#4338ca', barColor: '#4f46e5' },
  'TOP_JOB': { icon: 'arrow_upward', iconBg: '#fce7f3', iconColor: '#be185d', barColor: '#db2777' },
  'VERIFY_BADGE': { icon: 'verified', iconBg: '#dcfce7', iconColor: '#15803d', barColor: '#16a34a' },
  'CONNECT_MESSAGE': { icon: 'chat', iconBg: '#f3e8ff', iconColor: '#7e22ce', barColor: '#9333ea' },
}

const quotas = computed(() => {
  const current = store.companyPlan?.currentPackage
  if (!current?.usages) return []

  const resetDate = new Date(current.expiredAt).toLocaleDateString('vi-VN')

  return current.usages.map(u => {
    const meta = featureMeta[u.featureCode] || { icon: 'check_circle', iconBg: '#f3f4f6', iconColor: '#6b7280', barColor: '#6b7280' }
    return {
      icon: meta.icon,
      label: u.featureName || meta.name || u.featureCode,
      used: u.used,
      total: u.total,
      resetAt: resetDate,
      iconBg: meta.iconBg,
      iconColor: meta.iconColor,
      barColor: meta.barColor
    }
  })
})

const percent = (used: number, total: number) =>
  total > 0 ? `${Math.round((used / total) * 100)}%` : '0%'
</script>

<template>
  <section class="quotas-section">
    <h3 class="section-heading">Quota tính năng theo gói</h3>
    <div v-if="quotas.length" class="quotas-grid">
      <div v-for="(q, i) in quotas" :key="i" class="quota-card">
        <div class="quota-row">
          <div class="quota-icon" :style="{ background: q.iconBg, color: q.iconColor }">
            <span class="material-symbols-outlined">{{ q.icon }}</span>
          </div>
          <div class="quota-info">
            <div class="quota-header">
              <p class="quota-label">{{ q.label }}</p>
              <p class="quota-count">{{ q.used.toLocaleString() }} / {{ q.total.toLocaleString() }}</p>
            </div>
            <div class="progress-track">
              <div class="progress-bar" :style="{ width: percent(q.used, q.total), background: q.barColor }"></div>
            </div>
          </div>
        </div>
        <p class="quota-reset">Reset lúc: {{ q.resetAt }}</p>
      </div>
    </div>
    <div v-else class="empty-state text-center text-slate-500 py-8 bg-white rounded-2xl border border-slate-100">
      Không có dữ liệu sử dụng
    </div>
  </section>
</template>

<style scoped>
.section-heading {
  font-size: 1.125rem;
  font-weight: 700;
  color: #1b1c18;
  letter-spacing: -0.02em;
  margin: 0 0 16px;
}

.quotas-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.quota-card {
  background: #ffffff;
  padding: 20px;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.quota-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.quota-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.quota-icon .material-symbols-outlined {
  font-size: 1.375rem;
}

.quota-info {
  flex: 1;
}

.quota-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.quota-label {
  font-size: 0.875rem;
  font-weight: 700;
  color: #1b1c18;
  margin: 0;
}

.quota-count {
  font-size: 0.875rem;
  font-weight: 700;
  color: #1b1c18;
  margin: 0;
}

.progress-track {
  width: 100%;
  height: 6px;
  background: #e4e2dc;
  border-radius: 9999px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  border-radius: 9999px;
  transition: width 0.3s;
}

.quota-reset {
  font-size: 0.75rem;
  color: rgba(87, 66, 64, 0.6);
  font-weight: 500;
  font-style: italic;
  margin: 0;
}

@media (max-width: 768px) {
  .quotas-grid {
    grid-template-columns: 1fr;
  }
}
</style>