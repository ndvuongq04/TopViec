<template>
  <div class="cd-impact">
    <h2 class="cd-impact__title">Ảnh hưởng tài khoản</h2>

    <div class="cd-impact__score-row">
      <div class="cd-impact__score">
        <span class="cd-impact__score-value">{{ impact.currentScore }}</span>
        <span class="cd-impact__score-unit">điểm</span>
      </div>
      <div class="cd-impact__projected">
        <span class="material-symbols-outlined">trending_up</span>
        <span>Dự kiến: {{ impact.projectedScore }}</span>
      </div>
    </div>

    <div class="cd-impact__bar">
      <div class="cd-impact__bar-seg cd-impact__bar-seg--normal"></div>
      <div class="cd-impact__bar-seg cd-impact__bar-seg--limited"></div>
      <div class="cd-impact__bar-seg cd-impact__bar-seg--locked"></div>
    </div>
    <div class="cd-impact__bar-labels">
      <span>Bình thường (0-19)</span>
      <span>Hạn chế (20-49)</span>
      <span>Khóa (50+)</span>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ impact: { currentScore: number; projectedScore: number } }>()
</script>

<style scoped>
.cd-impact {
  background: #fff; border-radius: 0.75rem; padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border: 1px solid rgba(221,192,190,0.3);
}
.cd-impact__title { font-size: 1.125rem; font-weight: 700; color: #1b1c18; margin-bottom: 1rem; }

.cd-impact__score-row { display: flex; align-items: flex-end; justify-content: space-between; margin-bottom: 1rem; }
.cd-impact__score { display: flex; align-items: baseline; gap: 4px; }
.cd-impact__score-value { font-size: 2.25rem; font-weight: 900; color: #1b1c18; line-height: 1; }
.cd-impact__score-unit { font-size: 0.875rem; color: #574240; font-weight: 500; }

.cd-impact__projected {
  display: flex; align-items: center; gap: 4px;
  font-size: 0.875rem; font-weight: 700; color: #ba1a1a;
}
.cd-impact__projected .material-symbols-outlined { font-size: 18px; }

.cd-impact__bar {
  height: 12px; width: 100%; display: flex;
  border-radius: 999px; overflow: hidden;
  background: #f0eee7; margin-bottom: 8px;
}
.cd-impact__bar-seg { height: 100%; }
.cd-impact__bar-seg--normal  { width: 40%; background: #94d3bf; }
.cd-impact__bar-seg--limited { width: 30%; background: #faeeda; }
.cd-impact__bar-seg--locked  { width: 30%; background: #ffdad6; }

.cd-impact__bar-labels {
  display: flex; justify-content: space-between;
  font-size: 10px; font-weight: 700; text-transform: uppercase;
  color: #574240; letter-spacing: 0.04em;
}
</style>
