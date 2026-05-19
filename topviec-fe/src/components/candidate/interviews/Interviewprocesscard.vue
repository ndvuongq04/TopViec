<template>
  <section class="process-card" :class="{ 'process-card--expanded': isExpanded }">
    <!-- Header -->
    <div class="process-card__header" @click="$emit('toggle')">
      <div class="process-card__info">
        <div class="process-card__logo">
          <img
            v-if="process.companyLogo"
            :src="process.companyLogo"
            :alt="`${process.companyName} Logo`"
          />
          <span v-else class="process-card__logo-fallback">
            {{ process.companyName.charAt(0) }}
          </span>
        </div>
        <div>
          <h2 class="process-card__job-title">{{ process.jobTitle }}</h2>
          <div class="process-card__details">
            <span class="process-card__company">{{ process.companyName }}</span>
            <div v-if="process.isBrandVerified" class="relative flex items-center group/verified">
              <span
                class="material-symbols-outlined text-blue-600 cursor-default"
                style="font-size:16px; font-variation-settings:'FILL' 1"
              >verified</span>
              <span class="pointer-events-none absolute bottom-[calc(100%+6px)] left-1/2 -translate-x-1/2 bg-blue-800 text-white text-[11px] font-medium whitespace-nowrap px-2.5 py-1 rounded-md opacity-0 group-hover/verified:opacity-100 transition-opacity z-10">
                Công ty đã xác minh
              </span>
            </div>
            <span v-if="process.status" class="process-card__status-badge" :class="`process-card__status-badge--${statusColor}`">
              {{ statusLabel }}
            </span>
          </div>
        </div>
      </div>

      <div class="process-card__right">
        <template v-if="!isExpanded && process.nextStep">
          <div class="process-card__next-step">
            <p class="process-card__next-step-label">Next Step</p>
            <p class="process-card__next-step-value">{{ process.nextStep }}</p>
          </div>
        </template>

        <button v-if="isExpanded" class="process-card__history-btn" @click.stop="$emit('showHistory')">
          <span class="material-symbols-outlined icon-md">history</span>
          Lịch sử phỏng vấn
        </button>

        <span
          class="material-symbols-outlined process-card__chevron"
          :class="{ 'process-card__chevron--up': isExpanded }"
        >
          expand_more
        </span>
      </div>
    </div>

    <!-- Rounds Timeline -->
    <div v-if="isExpanded && process.rounds.length" class="process-card__rounds">
      <InterviewRoundItem
        v-for="(round, idx) in process.rounds"
        :key="round.id"
        :round="round"
        :is-last="idx === process.rounds.length - 1"
        @showDetail="$emit('showDetail', round)"
      />
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import InterviewRoundItem from './Interviewrounditem.vue'
import { APPLICATION_STATUS_OPTIONS } from '@/constants/application.constants'

const props = defineProps<{
  process: {
    id: number
    jobTitle: string
    companyName: string
    companyLogo?: string
    isBrandVerified?: boolean | null
    status?: string
    nextStep?: string
    rounds: any[]
  }
  isExpanded: boolean
}>()

defineEmits<{
  (e: 'toggle'): void
  (e: 'showHistory'): void
  (e: 'showDetail', round: any): void
}>()

const statusOption = computed(() =>
  APPLICATION_STATUS_OPTIONS.find(o => o.value === props.process.status)
)
const statusLabel = computed(() => statusOption.value?.label ?? props.process.status ?? '')
const statusColor = computed(() => statusOption.value?.color ?? 'default')
</script>

<style scoped>
.process-card {
  background: #ffffff;
  border-radius: 0.75rem;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(59, 130, 246, 0.05);
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

.process-card:not(.process-card--expanded):hover {
  background: #f9f9ff;
  border-color: rgba(192, 199, 212, 0.1);
}

.process-card__header {
  padding: 1.5rem;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  cursor: pointer;
}

.process-card--expanded .process-card__header {
  border-bottom: 1px solid rgba(192, 199, 212, 0.1);
}

.process-card__info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.process-card__logo {
  width: 3.5rem;
  height: 3.5rem;
  border-radius: 0.75rem;
  background: #e9edff;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0.5rem;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.06);
  flex-shrink: 0;
}

.process-card__logo img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.process-card__logo-fallback {
  font-size: 1.125rem;
  font-weight: 700;
  color: #005ea4;
}

.process-card__job-title {
  font-size: 1.125rem;
  font-weight: 700;
  color: #071b3b;
  line-height: 1.25;
}

.process-card__details {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-top: 0.25rem;
  flex-wrap: wrap;
}

.process-card__company {
  font-size: 0.875rem;
  font-weight: 600;
  color: #004881;
}

.process-card__dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: #c0c7d4;
}

.process-card__location,
.process-card__rounds-count {
  font-size: 0.875rem;
  color: #404752;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.icon-sm {
  font-size: 16px;
}

.icon-md {
  font-size: 20px;
}

.process-card__right {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.process-card__next-step {
  text-align: right;
  display: none;
}

@media (min-width: 640px) {
  .process-card__next-step {
    display: block;
  }
}

.process-card__next-step-label {
  font-size: 0.75rem;
  font-weight: 700;
  color: #707783;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 0.25rem;
}

.process-card__next-step-value {
  font-size: 0.875rem;
  font-weight: 700;
  color: #005ea4;
}

.process-card__history-btn {
  background: #e0e8ff;
  color: #004881;
  padding: 0.625rem 1.25rem;
  border-radius: 0.75rem;
  font-weight: 700;
  font-size: 0.875rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  border: none;
  cursor: pointer;
  transition: background 0.2s;
}

.process-card__history-btn:hover {
  background: #d8e2ff;
}

.process-card__chevron {
  color: #707783;
  transition: transform 0.3s ease;
}

.process-card__chevron--up {
  transform: rotate(180deg);
}

.process-card:hover .process-card__chevron {
  color: #005ea4;
}

.process-card__rounds {
  padding: 1.5rem;
  background: rgba(249, 249, 255, 0.5);
}

/* ── Application status badge ── */
.process-card__status-badge {
  display: inline-flex;
  align-items: center;
  padding: 0.125rem 0.5rem;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 700;
}
.process-card__status-badge--blue    { background: #dbeafe; color: #1d4ed8; }
.process-card__status-badge--cyan    { background: #cffafe; color: #0e7490; }
.process-card__status-badge--purple  { background: #ede9fe; color: #7c3aed; }
.process-card__status-badge--orange  { background: #ffedd5; color: #c2410c; }
.process-card__status-badge--red     { background: #fee2e2; color: #dc2626; }
.process-card__status-badge--green   { background: #dcfce7; color: #15803d; }
.process-card__status-badge--success { background: #dcfce7; color: #15803d; }
.process-card__status-badge--gray    { background: #f1f5f9; color: #64748b; }
.process-card__status-badge--default { background: #f1f5f9; color: #64748b; }
</style>