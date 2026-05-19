<template>
  <section class="rvs-card">
    <div class="rvs-body">
      <!-- Stats -->
      <div class="rvs-stats">
        <div class="rvs-stat-item">
          <span class="rvs-stat-label">Điểm vi phạm</span>
          <span class="rvs-stat-value rvs-stat-value--warn">{{ score.violation }}</span>
        </div>
        <div class="rvs-stat-item">
          <span class="rvs-stat-label">Chờ xử lý</span>
          <span class="rvs-stat-value">{{ score.pending }}</span>
        </div>
        <div class="rvs-stat-item">
          <span class="rvs-stat-label">Báo cáo tháng này</span>
          <span class="rvs-stat-value">{{ score.thisMonth }}</span>
        </div>
      </div>

      <!-- Progress -->
      <div class="rvs-progress-wrap">
        <div class="rvs-progress-labels">
          <span>An toàn (0-19)</span>
          <span class="rvs-label-warn">Hạn chế (20-49)</span>
          <span class="rvs-label-error">Đình chỉ (≥50)</span>
        </div>
        <div class="rvs-bar-track">
          <div class="rvs-bar-marker" style="left: 38%" />
          <div class="rvs-bar-fill" :style="{ width: `${Math.min(score.violation / 50 * 100, 100)}%` }" />
        </div>
        <div class="rvs-progress-hint">
          <span class="material-symbols-outlined rvs-hint-icon">info</span>
          <p><strong>Bị hạn chế:</strong> tối đa 3 tin/tuần, tin mới cần Admin duyệt.</p>
        </div>
      </div>
    </div>

    <!-- Warning banner: chỉ hiện khi không phải An toàn -->
    <div v-if="statusInfo.level !== 'normal'" class="rvs-banner" :class="`rvs-banner--${statusInfo.level}`">
      <div class="rvs-banner-left">
        <span class="material-symbols-outlined rvs-banner-icon">{{ statusInfo.icon }}</span>
        <span class="rvs-banner-text">{{ statusInfo.bannerText }}</span>
      </div>
      <button class="rvs-contact-btn">Liên hệ Admin</button>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  score: { violation: number; pending: number; thisMonth: number; level?: string | null }
}>()

const statusInfo = computed(() => {
  const v = props.score.violation
  const level = props.score.level

  // Ưu tiên dùng level từ API nếu có, fallback sang tính theo điểm
  const resolvedLevel = level ?? (v >= 50 ? 'suspended' : v >= 20 ? 'limited' : 'normal')

  switch (resolvedLevel) {
    case 'suspended':
      return {
        level: 'suspended',
        label: 'Đình chỉ (≥50)',
        icon: 'block',
        bannerText: 'Tài khoản của bạn đang bị đình chỉ — tính năng đăng tin bị khoá toàn bộ.',
      }
    case 'limited':
      return {
        level: 'limited',
        label: 'Hạn chế (20–49)',
        icon: 'warning',
        bannerText: 'Tài khoản của bạn đang ở mức Hạn chế — tối đa 3 tin/tuần, tin mới cần Admin duyệt.',
      }
    default:
      return {
        level: 'normal',
        label: 'An toàn (0–19)',
        icon: 'check_circle',
        bannerText: '',
      }
  }
})
</script>

<style scoped>
.rvs-card {
  background: #fff;
  border-radius: 12px;
  border: 1px solid rgba(226,232,240,0.3);
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  overflow: hidden;
  margin-bottom: 48px;
}
.rvs-body {
  display: flex; flex-direction: column;
  gap: 32px; padding: 32px;
}
@media (min-width: 1024px) {
  .rvs-body { flex-direction: row; }
}

/* Stats */
.rvs-stats {
  display: grid; grid-template-columns: repeat(3, 1fr);
  gap: 24px; flex: 1;
  padding-right: 0;
}
@media (min-width: 1024px) {
  .rvs-stats {
    padding-right: 32px;
    border-right: 1px solid rgba(226,232,240,0.5);
  }
}
.rvs-stat-item { display: flex; flex-direction: column; gap: 8px; }
.rvs-stat-label {
  font-size: 12px; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.08em; color: #64748b;
}
.rvs-stat-value { font-size: 36px; font-weight: 800; color: #0f172a; }
.rvs-stat-value--warn { color: #f59e0b; }

/* Progress */
.rvs-progress-wrap { flex: 1.5; display: flex; flex-direction: column; justify-content: center; gap: 12px; }
.rvs-progress-labels {
  display: flex; justify-content: space-between;
  font-size: 12px; font-weight: 700; color: #64748b;
}
.rvs-label-warn  { color: #f59e0b; }
.rvs-label-error { color: #ef4444; }

.rvs-bar-track {
  height: 16px; background: #f1f5f9;
  border-radius: 9999px; overflow: hidden;
  position: relative;
}
.rvs-bar-marker {
  position: absolute; top: 0; bottom: 0;
  width: 1px; background: rgba(255,255,255,0.5); z-index: 1;
}
.rvs-bar-fill {
  height: 100%;
  background: linear-gradient(to right, #10b981, #f59e0b);
  border-radius: 9999px;
  transition: width 0.4s ease;
}
.rvs-progress-hint {
  display: flex; align-items: flex-start; gap: 8px;
  font-size: 13px; color: #64748b;
}
.rvs-hint-icon { font-size: 18px; color: #f59e0b; flex-shrink: 0; }

/* Banner */
.rvs-banner {
  display: flex; flex-wrap: wrap; align-items: center;
  justify-content: space-between; gap: 16px;
  padding: 16px 32px;
  background: rgba(254,243,199,0.5);
  border-top: 1px solid rgba(245,158,11,0.2);
}
.rvs-banner--limited {
  background: rgba(254,243,199,0.5);
  border-top: 1px solid rgba(245,158,11,0.2);
}
.rvs-banner--suspended {
  background: rgba(254,226,226,0.5);
  border-top: 1px solid rgba(239,68,68,0.2);
}
.rvs-banner-left { display: flex; align-items: center; gap: 12px; }
.rvs-banner--limited .rvs-banner-icon { color: #f59e0b; font-size: 22px; }
.rvs-banner--suspended .rvs-banner-icon { color: #ef4444; font-size: 22px; }
.rvs-banner--limited .rvs-banner-text { font-size: 14px; font-weight: 500; color: #b45309; }
.rvs-banner--suspended .rvs-banner-text { font-size: 14px; font-weight: 500; color: #b91c1c; }
.rvs-contact-btn {
  padding: 8px 20px;
  background: #fff; color: #0f172a;
  border: 1px solid #cbd5e1; border-radius: 8px;
  font-size: 13px; font-weight: 700;
  cursor: pointer; font-family: 'Manrope', sans-serif;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
  white-space: nowrap; transition: background 0.15s;
}
.rvs-contact-btn:hover { background: #f1f5f9; }
</style>