<template>
  <div class="stats-grid">
    <div v-for="stat in STATS" :key="stat.key" class="stat-card">
      <div class="stat-top">
        <div>
          <p class="stat-label">{{ stat.label }}</p>
          <h3 class="stat-number">{{ String(stat.value).padStart(2, '0') }}</h3>
        </div>
        <div class="stat-icon" :class="stat.iconColor">
          <span class="material-symbols-outlined filled">{{ stat.icon }}</span>
        </div>
      </div>
      <div class="stat-footer" :class="stat.footerColor">
        <span class="material-symbols-outlined">{{ stat.footerIcon }}</span>
        <span>{{ stat.footerText }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useEmployerInterviewStore } from '@/stores/employerInterview.store'

const store = useEmployerInterviewStore()

const STATS = computed(() => {
  const s = store.interviewStatistics
  
  return [
    {
      key: 'total',
      label: 'Tổng lịch phỏng vấn',
      value: s?.totalSchedules ?? 0,
      icon: 'work',
      iconColor: 'blue',
      footerIcon: 'trending_up',
      footerText: 'Tất cả các vòng',
      footerColor: 'green',
    },
    {
      key: 'pending-new',
      label: 'Ứng viên chưa có lịch',
      value: s?.pendingNewSchedules ?? 0,
      icon: 'person_add',
      iconColor: 'blue',
      footerIcon: 'info',
      footerText: 'Cần lên lịch phỏng vấn',
      footerColor: 'blue',
    },
    {
      key: 'pending',
      label: 'Chờ ứng viên xác nhận',
      value: s?.unconfirmedSchedules ?? 0,
      icon: 'pending_actions',
      iconColor: 'amber',
      footerIcon: 'schedule',
      footerText: 'Ứng viên chưa bấm Confirm',
      footerColor: 'amber',
    },
    {
      key: 'overdue',
      label: 'Lịch quá hạn (Overdue)',
      value: s?.overdueSchedules ?? 0,
      icon: 'error',
      iconColor: 'red',
      footerIcon: 'priority_high',
      footerText: 'Vui lòng kiểm tra lại',
      footerColor: 'red',
    },
  ]
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Manrope:wght@500;700;800&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@24,400,0,0&display=swap');

.material-symbols-outlined {
  font-family: 'Material Symbols Outlined';
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  font-size: 24px;
  line-height: 1;
  display: inline-block;
  vertical-align: middle;
}
.material-symbols-outlined.filled {
  font-variation-settings: 'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 24;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  font-family: 'Manrope', sans-serif;
  background: #fff;
  border-radius: 0.75rem;
  padding: 1.5rem;
  border: 1px solid #f1f5f9;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  transition: box-shadow 0.2s;
}
.stat-card:hover { box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08); }

.stat-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.stat-label {
  font-size: 0.75rem;
  font-weight: 700;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.stat-number {
  font-size: 1.875rem;
  font-weight: 800;
  color: #0f172a;
  margin-top: 0.5rem;
}

.stat-icon {
  padding: 0.75rem;
  border-radius: 0.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
}
.stat-icon.blue  { background: #eff6ff; color: #4B9AF6; }
.stat-icon.amber { background: #fffbeb; color: #d97706; }
.stat-icon.red   { background: #fef2f2; color: #dc2626; }

.stat-footer {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 1rem;
  font-size: 0.75rem;
  font-weight: 500;
}
.stat-footer .material-symbols-outlined { font-size: 16px; }
.stat-footer.green { color: #059669; }
.stat-footer.amber { color: #d97706; }
.stat-footer.red   { color: #dc2626; }
</style>