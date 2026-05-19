<template>
  <div class="timeline-card">
    <h4 class="timeline-card__title">
      <span class="material-symbols-outlined">history</span>
      Lịch sử Phỏng vấn
    </h4>

    <div class="timeline-card__list">
      <!-- Vertical line chạy dọc theo list -->
      <div class="timeline-card__line" />

      <InterviewTimelineItem
        v-for="(item, i) in items"
        :key="i"
        :item="item"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import InterviewTimelineItem from './InterviewTimelineItem.vue'

defineProps<{
  items: {
    roundLabel: string
    status: 'passed' | 'upcoming' | 'pending'
    rating?: number
    interviewer?: string
    scheduledAt?: string
    note?: string
    countdown?: string
  }[]
}>()
</script>

<style scoped>
.timeline-card {
  background: #fff;
  border-radius: 0.75rem;
  padding: 2rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  border: 1px solid rgba(203,213,225,0.3);
  height: 100%;
}

.timeline-card__title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.125rem;
  font-weight: 800;
  color: #0f172a;
  margin: 0 0 2.5rem;
}

.timeline-card__title .material-symbols-outlined {
  color: #4B9AF6;
  font-size: 1.3rem;
}

/* Timeline wrapper — chứa line + items */
.timeline-card__list {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 3rem;
}

/* Vertical connecting line */
.timeline-card__line {
  position: absolute;
  left: 1.2rem;           /* căn giữa dot (2.5rem / 2 - border) */
  top: 0.5rem;
  bottom: 0.5rem;
  width: 2px;
  background: #f1f5f9;
  pointer-events: none;
  z-index: 0;
}
</style>