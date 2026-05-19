<template>
  <div class="ckpi-grid">
    <div v-for="card in cards" :key="card.label" class="ckpi-card">
      <div class="ckpi-bg-circle" :style="{ background: card.circleBg }" />
      <div class="ckpi-top">
        <div class="ckpi-icon-wrap" :style="{ background: card.iconBg, color: card.iconColor }">
          <span class="material-symbols-outlined">{{ card.icon }}</span>
        </div>
        <span class="ckpi-label">{{ card.label }}</span>
      </div>
      <div class="ckpi-value">{{ card.value }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAdminReportStore } from '@/stores/adminReport.store'

const store = useAdminReportStore()

const cards = computed(() => {
  const s = store.reportStatistics
  
  return [
    { 
      label: 'Tổng báo cáo',  
      icon: 'receipt_long',    
      value: s?.totalReports ?? 0,
      iconBg: '#f0eee7', 
      iconColor: '#963131', 
      circleBg: '#f0eee7' 
    },
    { 
      label: 'Chờ xử lý',    
      icon: 'hourglass_empty',  
      value: s?.pendingReports ?? 0,
      iconBg: '#FAEEDA', 
      iconColor: '#633806', 
      circleBg: 'rgba(250,238,218,0.2)' 
    },
    { 
      label: 'Nhóm B (nặng)', 
      icon: 'warning',          
      value: s?.groupBReports ?? 0,
      iconBg: '#ffdad6', 
      iconColor: '#ba1a1a', 
      circleBg: 'rgba(255,218,214,0.2)' 
    },
    { 
      label: 'Quá hạn SLA',   
      icon: 'alarm_off',        
      value: s?.slaOverdueReports ?? 0,
      iconBg: '#ffdad6', 
      iconColor: '#ba1a1a', 
      circleBg: 'rgba(255,218,214,0.2)' 
    },
  ]
})
</script>

<style scoped>
.ckpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}
@media (max-width: 768px) { .ckpi-grid { grid-template-columns: repeat(2, 1fr); } }

.ckpi-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  position: relative;
  overflow: hidden;
}
.ckpi-bg-circle {
  position: absolute;
  width: 96px; height: 96px;
  border-radius: 50%;
  top: -16px; right: -16px;
  opacity: 0.5;
}
.ckpi-top {
  display: flex; align-items: center; gap: 12px;
  margin-bottom: 16px; position: relative; z-index: 1;
}
.ckpi-icon-wrap {
  padding: 8px; border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
}
.ckpi-icon-wrap .material-symbols-outlined { font-size: 22px; }
.ckpi-label {
  font-size: 0.75rem; font-weight: 700; font-family: 'Manrope', sans-serif;
  text-transform: uppercase; letter-spacing: 0.08em;
  color: #574240;
}
.ckpi-value {
  font-size: 1.875rem; font-weight: 700; font-family: 'Manrope', sans-serif;
  color: #1b1c18; position: relative; z-index: 1;
}
</style>