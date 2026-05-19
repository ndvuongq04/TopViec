<template>
  <div class="activity-log">
    <h3 class="activity-log__heading">Lịch sử tương tác</h3>
    <div class="activity-log__list">
      <div
        v-for="(item, index) in activities"
        :key="index"
        class="activity-item"
      >
        <div
          class="activity-item__dot"
          :class="index === 0 ? 'activity-item__dot--active' : ''"
        ></div>
        <div class="activity-item__content">
          <p class="activity-item__text">{{ item.text }}</p>
          <p class="activity-item__time">{{ item.time }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Activity {
  text: string
  time: string
}

interface Props {
  activities: Activity[]
}

defineProps<Props>()
</script>

<style scoped>
.activity-log {
  background: #fff;
  border-radius: 0.75rem;
  border: 1px solid #e2e8f0;
  padding: 1.25rem;
}

.activity-log__heading {
  font-size: 0.75rem; /* 12px – label */
  font-weight: 800;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  border-bottom: 1px solid #f8fafc;
  padding-bottom: 0.5rem;
  margin-bottom: 1rem;
}

.activity-log__list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
}

.activity-item__dot {
  width: 0.375rem;
  height: 0.375rem;
  border-radius: 9999px;
  background: #cbd5e1;
  margin-top: 0.375rem;
  flex-shrink: 0;
}
.activity-item__dot--active { background: #10b981; }

.activity-item__text {
  font-size: 0.75rem; /* 12px */
  font-weight: 700;
  color: #334155;
}
.activity-item__time {
  font-size: 0.625rem; /* ~10px – capped ở min dùng được */
  color: #94a3b8;
  margin-top: 0.125rem;
}
</style>