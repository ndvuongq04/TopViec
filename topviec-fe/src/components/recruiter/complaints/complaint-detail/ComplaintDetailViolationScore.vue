<template>
  <section class="cd-score">
    <div class="cd-score__bg-deco"></div>
    <h3 class="cd-score__title">Điểm vi phạm tài khoản</h3>

    <div class="cd-score__display">
      <span class="cd-score__value">{{ score.current }}</span>
      <span :class="['cd-score__status', `cd-score__status--${score.statusVariant}`]">
        {{ score.status }}
      </span>
    </div>

    <div class="cd-score__meta">
      <div class="cd-score__meta-row">
        <span class="cd-score__meta-label">Trạng thái hiện tại</span>
        <span class="cd-score__meta-value cd-score__meta-value--error">{{ score.statusLabel }}</span>
      </div>
      <div class="cd-score__progress-bar">
        <div class="cd-score__progress-fill" :style="{ width: `${score.progressPercent}%` }"></div>
      </div>
      <div class="cd-score__penalty-row">
        <span class="cd-score__penalty-label">Dự kiến phạt (nếu không đạt)</span>
        <span class="cd-score__penalty-value">{{ score.penaltyIfFail }}</span>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  score: {
    current: number; status: string; statusVariant: string
    statusLabel: string; progressPercent: number; penaltyIfFail: string
  }
}>()
</script>

<style scoped>
.cd-score {
  background: #fff; border-radius: 0.75rem; padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05); border: 1px solid rgba(226,232,240,0.2);
  position: relative; overflow: hidden;
}
.cd-score__bg-deco {
  position: absolute; top: 0; right: 0; width: 128px; height: 128px;
  background: linear-gradient(135deg, rgba(239,68,68,0.05), transparent);
  border-radius: 0 0 0 100%;pointer-events: none;
}
.cd-score__title {
  font-size: 1rem; font-weight: 800; color: #0f172a;
  margin-bottom: 1.25rem; position: relative; z-index: 1;
}

.cd-score__display {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 1.5rem; background: #f8fafd; border-radius: 0.75rem;
  border: 1px solid rgba(226,232,240,0.3); margin-bottom: 1rem; gap: 10px;
}
.cd-score__value { font-size: 3rem; font-weight: 900; color: #0f172a; line-height: 1; }
.cd-score__status {
  padding: 4px 12px; border-radius: 999px; font-size: 0.75rem; font-weight: 700;
}
.cd-score__status--safe { background: #dcfce7; color: #15803d; }
.cd-score__status--warning { background: #fef3c7; color: #b45309; }
.cd-score__status--error { background: #fee2e2; color: #b91c1c; }

.cd-score__meta { display: flex; flex-direction: column; gap: 10px; }
.cd-score__meta-row { display: flex; justify-content: space-between; font-size: 0.875rem; }
.cd-score__meta-label { color: #64748b; font-weight: 500; }
.cd-score__meta-value--error { font-weight: 700; color: #ef4444; }

.cd-score__progress-bar {
  width: 100%; height: 10px; background: #e2e7f0; border-radius: 999px; overflow: hidden;
}
.cd-score__progress-fill { height: 100%; background: #ef4444; border-radius: 999px; transition: width 0.5s; }

.cd-score__penalty-row {
  display: flex; justify-content: space-between; align-items: center;
  padding-top: 1rem; margin-top: 4px; border-top: 1px solid #e2e7f0;
}
.cd-score__penalty-label { font-size: 0.875rem; font-weight: 600; color: #0f172a; }
.cd-score__penalty-value { font-size: 1rem; font-weight: 800; color: #ef4444; }
</style>
