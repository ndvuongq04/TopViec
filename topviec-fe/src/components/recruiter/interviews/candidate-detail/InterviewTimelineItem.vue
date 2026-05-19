<template>
  <div class="tl-item" :class="{ 'tl-item--pending': item.status === 'pending' }">
    <!-- Dot -->
    <div class="tl-item__dot" :class="`tl-item__dot--${item.status}`">
      <span
        class="material-symbols-outlined"
        :style="item.status === 'passed' ? `font-variation-settings: 'FILL' 1;` : ''"
      >{{ dotIcon }}</span>
    </div>

    <!-- Body -->
    <div class="tl-item__body">
      <!-- Header -->
      <div class="tl-item__header">
        <div>
          <h5 class="tl-item__title">{{ item.roundLabel }}</h5>
          <p class="tl-item__status-text" :class="`tl-item__status-text--${item.status}`">
            {{ statusLabel }}
          </p>
        </div>

        <!-- Rating badge (passed) -->
        <div v-if="item.status === 'passed' && item.rating" class="tl-item__rating">
          <span class="tl-item__rating-label">Rating:</span>
          <div class="tl-item__stars">
            <span
              v-for="n in 5"
              :key="n"
              class="material-symbols-outlined tl-item__star"
              :class="n <= item.rating ? 'tl-item__star--filled' : 'tl-item__star--empty'"
              :style="n <= item.rating ? `font-variation-settings: 'FILL' 1;` : ''"
            >star</span>
          </div>
        </div>

        <!-- Upcoming countdown badge -->
        <div v-if="item.status === 'upcoming' && item.countdown" class="tl-item__countdown">
          <span class="material-symbols-outlined">schedule</span>
          {{ item.countdown }}
        </div>
      </div>

      <!-- Content panel -->
      <div class="tl-item__panel" :class="`tl-item__panel--${item.status}`">
        <!-- Passed / Upcoming: show interviewer + time -->
        <template v-if="item.status !== 'pending'">
          <div class="tl-item__grid">
            <div>
              <label class="tl-item__meta-label" :class="item.status === 'upcoming' ? 'tl-item__meta-label--blue' : ''">
                Người phỏng vấn
              </label>
              <p class="tl-item__meta-value">{{ item.interviewer }}</p>
            </div>
            <div>
              <label class="tl-item__meta-label" :class="item.status === 'upcoming' ? 'tl-item__meta-label--blue' : ''">
                {{ item.status === 'upcoming' ? 'Thời gian dự kiến' : 'Thời gian' }}
              </label>
              <p class="tl-item__meta-value">{{ item.scheduledAt }}</p>
            </div>
          </div>

          <!-- Note (passed only) -->
          <div v-if="item.status === 'passed' && item.note" class="tl-item__note-row">
            <label class="tl-item__meta-label">Ghi chú</label>
            <p class="tl-item__note">{{ item.note }}</p>
          </div>

          <!-- Prep doc button (upcoming only) -->
          <button v-if="item.status === 'upcoming'" class="tl-item__prep-btn">
            Xem tài liệu chuẩn bị
          </button>
        </template>

        <!-- Pending placeholder -->
        <template v-else>
          <p class="tl-item__pending-hint">Thông tin sẽ hiển thị sau khi hoàn thành vòng trước</p>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  item: {
    roundLabel: string
    status: 'passed' | 'upcoming' | 'pending'
    rating?: number
    interviewer?: string
    scheduledAt?: string
    note?: string
    countdown?: string
  }
}>()

const dotIcon = computed(() => {
  if (props.item.status === 'passed') return 'check'
  if (props.item.status === 'upcoming') return 'event'
  return 'pending'
})

const statusLabel = computed(() => {
  if (props.item.status === 'passed') return 'Đã vượt qua'
  if (props.item.status === 'upcoming') return 'Sắp diễn ra'
  return 'Đang chờ'
})
</script>

<style scoped>
.tl-item {
  position: relative;
  padding-left: 3rem;
  display: flex;
  gap: 0;
}

.tl-item--pending {
  opacity: 0.5;
}

/* Dot */
.tl-item__dot {
  position: absolute;
  left: 0;
  top: 0.25rem;
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 50%;
  border: 4px solid #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  z-index: 1;
}

.tl-item__dot .material-symbols-outlined {
  font-size: 0.9rem;
  font-weight: 700;
}

.tl-item__dot--passed {
  background: #f0fdf4;
  color: #10b981;
}

.tl-item__dot--upcoming {
  background: #eff6ff;
  color: #4B9AF6;
}

.tl-item__dot--pending {
  background: #f8fafc;
  color: #94a3b8;
}

/* Body */
.tl-item__body {
  flex: 1;
  min-width: 0;
}

/* Header */
.tl-item__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
}

.tl-item__title {
  font-size: 1.125rem;
  font-weight: 800;
  color: #0f172a;
  margin: 0 0 0.15rem;
}

.tl-item__status-text {
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  margin: 0;
}

.tl-item__status-text--passed { color: #10b981; }
.tl-item__status-text--upcoming { color: #4B9AF6; }
.tl-item__status-text--pending { color: #94a3b8; }

/* Rating */
.tl-item__rating {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.25rem 0.75rem;
  background: #f8fafc;
  border: 1px solid rgba(203,213,225,0.3);
  border-radius: 0.5rem;
}

.tl-item__rating-label {
  font-size: 0.75rem;
  font-weight: 700;
  color: #0f172a;
}

.tl-item__stars {
  display: flex;
  gap: 0.1rem;
}

.tl-item__star {
  font-size: 1rem;
}

.tl-item__star--filled { color: #f59e0b; }
.tl-item__star--empty  { color: #e2e8f0; }

/* Countdown */
.tl-item__countdown {
  display: flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.25rem 0.75rem;
  background: #eff6ff;
  border-radius: 0.5rem;
  font-size: 0.75rem;
  font-weight: 700;
  color: #1d4ed8;
}

.tl-item__countdown .material-symbols-outlined {
  font-size: 0.9rem;
}

/* Panel */
.tl-item__panel {
  border-radius: 0.75rem;
  padding: 1.25rem;
}

.tl-item__panel--passed {
  background: #f8fafd;
  border: 1px solid rgba(203,213,225,0.2);
}

.tl-item__panel--upcoming {
  background: rgba(239,246,255,0.35);
  border: 1px solid #bfdbfe;
}

.tl-item__panel--pending {
  border: 2px dashed #e2e8f0;
  text-align: center;
}

/* Grid inside panel */
.tl-item__grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin-bottom: 0;
}

@media (max-width: 480px) {
  .tl-item__grid { grid-template-columns: 1fr; }
}

.tl-item__meta-label {
  display: block;
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #94a3b8;
  margin-bottom: 0.2rem;
}

.tl-item__meta-label--blue { color: #93c5fd; }

.tl-item__meta-value {
  font-size: 0.875rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
}

/* Note */
.tl-item__note-row {
  margin-top: 0.875rem;
}

.tl-item__note {
  font-size: 0.875rem;
  color: #64748b;
  font-style: italic;
  line-height: 1.65;
  margin: 0.2rem 0 0;
}

/* Prep button */
.tl-item__prep-btn {
  width: 100%;
  margin-top: 1rem;
  padding: 0.5rem;
  background: #fff;
  border: 1px solid #bfdbfe;
  border-radius: 0.5rem;
  font-size: 0.75rem;
  font-weight: 700;
  color: #4B9AF6;
  font-family: inherit;
  cursor: pointer;
  transition: background 0.18s;
}

.tl-item__prep-btn:hover {
  background: #eff6ff;
}

/* Pending hint */
.tl-item__pending-hint {
  font-size: 0.75rem;
  color: #94a3b8;
  font-style: italic;
  margin: 0;
}
</style>