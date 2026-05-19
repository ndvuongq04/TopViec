<template>
  <div class="cd-reporter">
    <div class="cd-reporter__header">
      <h2 class="cd-reporter__title">Người báo cáo</h2>
      <span v-if="reporter.credible" class="cd-reporter__badge">
        <span class="material-symbols-outlined">verified</span> Đáng tin cậy
      </span>
    </div>

    <div class="cd-reporter__profile">
      <div class="cd-reporter__avatar">{{ reporter.initials }}</div>
      <div>
        <h3 class="cd-reporter__name">{{ reporter.name }}</h3>
        <p class="cd-reporter__id">Mã: {{ reporter.id }}</p>
      </div>
    </div>

    <div class="cd-reporter__footer">
      <span class="cd-reporter__footer-label">Số báo cáo hôm nay</span>
      <span class="cd-reporter__footer-value">{{ reporter.reportsToday }} / {{ reporter.reportsLimit }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  reporter: {
    name: string; id: string; initials: string
    credible: boolean; reportsToday: number; reportsLimit: number
  }
}>()
</script>

<style scoped>
.cd-reporter {
  background: #fff; border-radius: 0.75rem; padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}
.cd-reporter__header {
  display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1rem;
}
.cd-reporter__title { font-size: 1.125rem; font-weight: 700; color: #1b1c18; }
.cd-reporter__badge {
  display: inline-flex; align-items: center; gap: 4px;
  padding: 4px 8px; background: #eeedfe; color: #3c3489;
  border-radius: 6px; font-size: 0.75rem; font-weight: 700;
}
.cd-reporter__badge .material-symbols-outlined { font-size: 14px; }

.cd-reporter__profile { display: flex; align-items: center; gap: 12px; margin-bottom: 1rem; }
.cd-reporter__avatar {
  width: 40px; height: 40px; border-radius: 50%;
  background: #eae8e1; color: #76191d;
  display: flex; align-items: center; justify-content: center;
  font-weight: 700; font-size: 0.875rem; flex-shrink: 0;
}
.cd-reporter__name { font-size: 0.875rem; font-weight: 700; color: #1b1c18; }
.cd-reporter__id {
  font-size: 11px; color: #574240;
  text-transform: uppercase; letter-spacing: 0.06em; margin-top: 2px;
}

.cd-reporter__footer {
  display: flex; justify-content: space-between; align-items: center;
  font-size: 0.8125rem; border-top: 1px solid #f0eee7; padding-top: 12px;
}
.cd-reporter__footer-label { color: #574240; }
.cd-reporter__footer-value { font-weight: 700; color: #1b1c18; }
</style>    
