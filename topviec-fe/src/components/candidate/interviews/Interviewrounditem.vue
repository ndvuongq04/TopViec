<template>
  <div class="round-item">
    <!-- Timeline Node -->
    <div class="round-item__node">
      <div class="round-item__indicator" :class="`round-item__indicator--${round.status.toLowerCase()}`">
        <span v-if="round.status === 'COMPLETED'" class="material-symbols-outlined icon-20">check</span>
        <span v-else-if="round.status === 'LOCKED'" class="material-symbols-outlined icon-20">lock</span>
        <span v-else class="round-item__number">{{ round.roundNumber }}</span>
      </div>
      <div v-if="!isLast" class="round-item__line" :class="`round-item__line--${round.status.toLowerCase()}`" />
    </div>

    <!-- Content -->
    <div class="round-item__content" :class="`round-item__content--${round.status.toLowerCase()}`">
      <div class="round-item__body">
        <div class="round-item__info">
          <div class="round-item__badges">
            <span class="round-item__label" :class="`round-item__label--${round.status.toLowerCase()}`">
              Round {{ round.roundNumber }}
            </span>
            <InterviewStatusBadge :status="round.status" />
          </div>

          <h3 class="round-item__title" :class="{ 'round-item__title--muted': round.status === 'LOCKED' }">
            {{ round.title }}
          </h3>

          <div v-if="round.status !== 'LOCKED' && round.scheduledDate" class="round-item__schedule">
            <span class="round-item__meta" :class="{ 'round-item__meta--bold': round.status === 'PENDING_CONFIRMATION' }">
              <span class="material-symbols-outlined icon-18" :class="{ 'text-primary': round.status === 'PENDING_CONFIRMATION' }">calendar_today</span>
              {{ round.scheduledDate }}
            </span>
            <span v-if="round.scheduledTime" class="round-item__meta" :class="{ 'round-item__meta--bold': round.status === 'PENDING_CONFIRMATION' }">
              <span class="material-symbols-outlined icon-18" :class="{ 'text-primary': round.status === 'PENDING_CONFIRMATION' }">schedule</span>
              {{ round.scheduledTime }}
            </span>
            <span class="round-item__meta" :class="{ 'round-item__meta--bold': round.status === 'PENDING_CONFIRMATION' }">
              <span class="material-symbols-outlined icon-18" :class="{ 'text-primary': round.status === 'PENDING_CONFIRMATION' }">videocam</span>
              {{ round.mode === 'ONLINE' ? 'Online' : 'Trực tiếp' }}
            </span>
          </div>
        </div>

        <!-- Actions -->
        <div v-if="round.status !== 'LOCKED'" class="round-item__actions">
          <button class="round-item__view-btn" @click="$emit('showDetail', round)">
            <span class="material-symbols-outlined">visibility</span>
          </button>
          <button v-if="round.status === 'PENDING_CONFIRMATION'" class="round-item__confirm-btn" @click="$emit('showDetail', round)">
            Xác nhận lịch
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import InterviewStatusBadge from './Interviewstatusbadge.vue'

defineProps<{
  round: {
    id: number
    roundNumber: number
    title: string
    status: string
    scheduledDate?: string
    scheduledTime?: string
    mode?: string
  }
  isLast: boolean
}>()

defineEmits<{
  (e: 'showDetail', round: any): void
}>()
</script>

<style scoped>
.round-item {
  display: flex;
  gap: 1.5rem;
  padding-bottom: 2rem;
}

.round-item:last-child {
  padding-bottom: 0;
}

/* ── Node ── */
.round-item__node {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  flex-shrink: 0;
}

.round-item__indicator {
  z-index: 1;
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.round-item__indicator--completed {
  background: #006d32;
  color: #fff;
  box-shadow: 0 4px 6px rgba(0, 109, 50, 0.2);
}

.round-item__indicator--pending {
  background: #b47800;
  color: #fff;
  box-shadow: 0 4px 12px rgba(180, 120, 0, 0.15);
  outline: 4px solid rgba(180, 120, 0, 0.1);
}

.round-item__indicator--pending_confirmation {
  background: #0077ce;
  color: #fdfcff;
  box-shadow: 0 4px 12px rgba(0, 96, 168, 0.15);
  outline: 4px solid rgba(0, 96, 168, 0.1);
}

.round-item__indicator--confirmed {
  background: #16a34a;
  color: #fff;
  box-shadow: 0 4px 12px rgba(22, 163, 74, 0.15);
  outline: 4px solid rgba(22, 163, 74, 0.1);
}

.round-item__indicator--locked {
  background: #e9edff;
  color: #707783;
}

.icon-18 { font-size: 18px; }
.icon-20 { font-size: 20px; }

.round-item__number {
  font-size: 0.875rem;
  font-weight: 900;
}

.round-item__line {
  position: absolute;
  top: 2.5rem;
  width: 2px;
  height: calc(100% - 2.5rem + 2rem);
}

.round-item__line--completed {
  background: rgba(0, 109, 50, 0.3);
}

.round-item__line--pending {
  background: rgba(180, 120, 0, 0.2);
}

.round-item__line--confirmed {
  background: rgba(22, 163, 74, 0.25);
}

.round-item__line--pending_confirmation,
.round-item__line--locked {
  background: rgba(192, 199, 212, 0.3);
}

/* ── Content ── */
.round-item__content {
  flex: 1;
  padding: 1.25rem;
  border-radius: 0.75rem;
  transition: all 0.2s ease;
}

.round-item__content--completed {
  background: #fff;
  border: 1px solid rgba(192, 199, 212, 0.2);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
}

.round-item__content--completed:hover {
  background: #f8fafc;
}

.round-item__content--pending {
  background: #fff;
  border: 1px solid rgba(180, 120, 0, 0.2);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
}

.round-item__content--pending:hover {
  border-color: rgba(180, 120, 0, 0.4);
}

.round-item__content--pending_confirmation {
  background: #fff;
  border: 2px solid rgba(0, 96, 168, 0.2);
  box-shadow: 0 4px 12px rgba(0, 96, 168, 0.05);
}

.round-item__content--pending_confirmation:hover {
  border-color: rgba(0, 96, 168, 0.4);
}

.round-item__content--confirmed {
  background: #fff;
  border: 1px solid rgba(22, 163, 74, 0.25);
  box-shadow: 0 1px 4px rgba(22, 163, 74, 0.06);
}

.round-item__content--confirmed:hover {
  border-color: rgba(22, 163, 74, 0.45);
}

.round-item__content--locked {
  background: rgba(233, 237, 255, 0.3);
  border: 1px dashed rgba(192, 199, 212, 0.4);
  opacity: 0.7;
}

.round-item__body {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
}

.round-item__badges {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.25rem;
}

.round-item__label {
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.05em;
  text-transform: uppercase;
}

.round-item__label--completed { color: #006d32; }
.round-item__label--confirmed { color: #16a34a; }
.round-item__label--pending { color: #b47800; }
.round-item__label--pending_confirmation { color: #005ea4; }
.round-item__label--locked { color: #707783; }

.round-item__title {
  font-size: 1.125rem;
  font-weight: 700;
  color: #071b3b;
}

.round-item__title--muted {
  color: #707783;
}

.round-item__schedule {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-top: 0.5rem;
}

.round-item__meta {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  font-size: 0.875rem;
  color: #404752;
}

.round-item__meta--bold {
  color: #071b3b;
  font-weight: 600;
}

.text-primary {
  color: #005ea4;
}

/* ── Actions ── */
.round-item__actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-shrink: 0;
}

.round-item__view-btn {
  padding: 0.625rem;
  color: #404752;
  background: none;
  border: none;
  cursor: pointer;
  border-radius: 0.5rem;
  transition: all 0.2s;
}

.round-item__view-btn:hover {
  color: #005ea4;
  background: #e9edff;
}

.round-item__confirm-btn {
  background: #005ea4;
  color: #fff;
  padding: 0.625rem 1.5rem;
  border-radius: 0.75rem;
  font-weight: 700;
  font-size: 0.875rem;
  border: none;
  cursor: pointer;
  box-shadow: 0 4px 6px rgba(0, 94, 164, 0.2);
  transition: all 0.2s;
}

.round-item__confirm-btn:hover {
  box-shadow: 0 6px 12px rgba(0, 94, 164, 0.3);
  transform: translateY(-1px);
}

.round-item__confirm-btn:active {
  transform: scale(0.95);
}

@media (max-width: 768px) {
  .round-item { gap: 1rem; }
  .round-item__body { flex-direction: column; }
  .round-item__actions { width: 100%; justify-content: flex-end; }
}
</style>