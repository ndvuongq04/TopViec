<template>
  <div class="ald-risk">
    <div class="ald-risk__accent"></div>
    <h3 class="ald-risk__title">
      <span class="material-symbols-outlined">warning</span>
      Phân loại rủi ro
    </h3>

    <div class="ald-risk__level-badge">
      <span class="ald-risk__level-text">{{ SEVERITY_LABELS[risk.level as keyof typeof SEVERITY_LABELS] || risk.level }}</span>
    </div>

    <ul class="ald-risk__reasons">
      <li v-for="r in risk.reasons" :key="r.text" class="ald-risk__reason">
        <span class="material-symbols-outlined ald-risk__reason-icon">{{ r.icon }}</span>
        <span class="ald-risk__reason-text">{{ r.text }}</span>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { SEVERITY_LABELS } from '@/constants/logs.constants'

defineProps<{ risk: { level: string; reasons: { icon: string; text: string }[] } }>()
</script>

<style scoped>
.ald-risk {
  background: #fff; border-radius: 1rem; padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border: 1px solid rgba(186,26,26,0.2);
  position: relative; overflow: hidden;
}
.ald-risk__accent {
  position: absolute; top: 0; left: 0; width: 100%; height: 4px; background: #ba1a1a;
}

.ald-risk__title {
  display: flex; align-items: center; gap: 8px;
  font-size: 1.125rem; font-weight: 700; color: #1b1c18; margin-bottom: 1.25rem;
}
.ald-risk__title .material-symbols-outlined { color: #ba1a1a; font-size: 20px; }

.ald-risk__level-badge {
  background: rgba(186,26,26,0.05); border: 1px solid rgba(186,26,26,0.1);
  border-radius: 0.75rem; padding: 1rem; text-align: center; margin-bottom: 1.25rem;
}
.ald-risk__level-text {
  font-size: 1.25rem; font-weight: 900; letter-spacing: 0.1em; color: #ba1a1a;
}

.ald-risk__reasons {
  list-style: none; padding: 0; margin: 0;
  border-top: 1px solid rgba(228,226,220,0.3);
  padding-top: 1.25rem; display: flex; flex-direction: column; gap: 14px;
}
.ald-risk__reason { display: flex; align-items: flex-start; gap: 10px; }
.ald-risk__reason-icon { font-size: 18px; color: #76191d; flex-shrink: 0; margin-top: 1px; }
.ald-risk__reason-text { font-size: 0.875rem; color: #574240; line-height: 1.5; }
</style>