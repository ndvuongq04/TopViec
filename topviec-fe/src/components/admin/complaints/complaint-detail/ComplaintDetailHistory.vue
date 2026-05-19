<template>
  <div class="cd-history">
    <h2 class="cd-history__title">Lịch sử gần đây</h2>
    <div class="cd-history__list">
      <div v-for="item in history" :key="item.id" class="cd-history__item">
        <div class="cd-history__item-header">
          <span class="cd-history__item-id">{{ item.id }}</span>
          <span class="cd-history__item-date">{{ item.date }}</span>
        </div>
        <p class="cd-history__item-desc">{{ item.desc }}</p>
        <span class="cd-history__item-points">{{ item.points }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  history: { id: string; date: string; desc: string; points: string }[]
}>()
</script>

<style scoped>
.cd-history {
  background: #fff; border-radius: 0.75rem; padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}
.cd-history__title { font-size: 0.875rem; font-weight: 700; color: #1b1c18; margin-bottom: 1rem; }

.cd-history__list { display: flex; flex-direction: column; gap: 12px; }
.cd-history__item {
  padding: 12px; background: #fbf9f2;
  border-radius: 0.5rem; border: 1px solid #e4e2dc;
}
.cd-history__item-header {
  display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 4px;
}
.cd-history__item-id { font-size: 11px; font-weight: 700; color: #1b1c18; }
.cd-history__item-date { font-size: 10px; color: #574240; }
.cd-history__item-desc { font-size: 0.75rem; color: #574240; margin-bottom: 8px; line-height: 1.4; }
.cd-history__item-points {
  display: inline-block;
  font-size: 10px; font-weight: 600;
  padding: 2px 10px; border-radius: 4px;
  background: #eae8e1; color: #1b1c18;
}
</style>
