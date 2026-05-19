<template>
  <section class="sqg-section">
    <div class="sqg-header">
      <h3 class="sqg-title">Hạn mức tính năng</h3>
      <p class="sqg-hint">
        <span class="material-symbols-outlined">info</span>
        <span v-if="nextResetAt">Reset {{ nextResetAt }}</span>
        <span v-else>Reset vào ngày gia hạn</span>
      </p>
    </div>
    <div class="sqg-grid">
      <div v-for="q in quotas" :key="q.label" class="sqg-card">
        <div class="sqg-card-top">
          <div class="sqg-icon" :style="{ background: q.iconBg, color: q.iconColor }">
            <span class="material-symbols-outlined">{{ q.icon }}</span>
          </div>
          <span v-if="q.unlimited" class="sqg-badge-unlimited">Không giới hạn</span>
          <span v-else class="sqg-badge-count">Còn {{ q.remaining }}/{{ q.total }}</span>
        </div>
        <h4 class="sqg-label">{{ q.label }}</h4>
        <div v-if="q.unlimited" class="sqg-infinite">
          <span class="material-symbols-outlined">all_inclusive</span>
        </div>
        <div v-else class="sqg-bar-wrap">
          <!-- bar thể hiện % đã dùng: càng dùng nhiều bar càng dài -->
          <div
            class="sqg-bar"
            :class="{ 'sqg-bar--depleted': q.remaining === 0 }"
            :style="{ width: `${usedPercent(q.remaining!, q.total!)}%` }"
          />
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import dayjs from 'dayjs'

export interface QuotaItem {
  label:      string
  icon:       string
  iconBg:     string
  iconColor:  string
  unlimited?: boolean
  remaining?: number
  total?:     number
  resetAt?:   string | null
}

const props = defineProps<{ quotas: QuotaItem[] }>()

// Lấy resetAt sớm nhất trong các usages để hiển thị hint
const nextResetAt = computed(() => {
  const dates = props.quotas
    .map(q => q.resetAt)
    .filter((d): d is string => !!d)
  if (!dates.length) return null
  const earliest = dates.sort()[0]
  return dayjs(earliest).format('DD/MM/YYYY')
})

function usedPercent(remaining: number, total: number): number {
  if (total <= 0) return 0
  return Math.round(((total - remaining) / total) * 100)
}
</script>

<style scoped>
.sqg-section { display: flex; flex-direction: column; gap: 16px; }
.sqg-header { display: flex; align-items: center; justify-content: space-between; }
.sqg-title {
  font-size: 0.75rem; font-weight: 800;
  text-transform: uppercase; letter-spacing: 0.08em; color: #0f172a;
}
.sqg-hint {
  display: flex; align-items: center; gap: 4px;
  font-size: 12px; color: #64748b; font-weight: 500;
}
.sqg-hint .material-symbols-outlined { font-size: 14px; }
.sqg-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }
@media (max-width: 768px) { .sqg-grid { grid-template-columns: 1fr; } }

.sqg-card {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border: 1px solid #f8fafc;
  transition: box-shadow 0.2s;
}
.sqg-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
.sqg-card-top { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px; }
.sqg-icon { padding: 12px; border-radius: 12px; display: flex; align-items: center; }
.sqg-badge-unlimited {
  font-size: 12px; font-weight: 700;
  background: #ecfdf5; color: #059669;
  padding: 4px 8px; border-radius: 8px;
}
.sqg-badge-count { font-size: 12px; font-weight: 700; color: #64748b; }
.sqg-label { font-size: 14px; font-weight: 700; color: #0f172a; margin-bottom: 12px; }
.sqg-infinite { color: #059669; font-weight: 800; font-size: 20px; display: flex; align-items: center; }
.sqg-bar-wrap { width: 100%; height: 8px; background: #f1f5f9; border-radius: 9999px; overflow: hidden; }
.sqg-bar { height: 100%; background: #4B9AF6; border-radius: 9999px; transition: width 0.4s ease; }
.sqg-bar--depleted { background: #ef4444; }
</style>
