<template>
  <div class="cs-item">
    <div class="cs-item__avatar-wrap">
      <img
        v-if="candidate.avatarUrl"
        :alt="candidate.fullName"
        :src="candidate.avatarUrl"
        class="cs-item__avatar"
      />
      <div v-else class="cs-item__avatar cs-item__avatar--initials">
        {{ initials }}
      </div>
    </div>

    <div class="cs-item__body">
      <div class="cs-item__header">
        <div>
          <h4 class="cs-item__name">{{ candidate.fullName }}</h4>
          <p class="cs-item__role">{{ candidate.preferredJobTitle ?? 'Chưa cập nhật vị trí' }}</p>
        </div>
        <span
          v-if="statusInfo"
          :class="['cs-item__status', `cs-item__status--${statusInfo.variant}`]"
        >
          {{ statusInfo.label }}
        </span>
      </div>

      <div class="cs-item__meta">
        <span v-if="workTypeLabel" class="cs-item__meta-item">
          <span class="material-symbols-outlined">work</span>{{ workTypeLabel }}
        </span>
        <span v-if="candidate.preferredLocationName" class="cs-item__meta-item">
          <span class="material-symbols-outlined">location_on</span>{{ candidate.preferredLocationName }}
        </span>
        <span v-if="salaryLabel" class="cs-item__meta-item">
          <span class="material-symbols-outlined">payments</span>{{ salaryLabel }}
        </span>
      </div>
    </div>

    <div class="cs-item__actions">
      <button
        class="cs-item__btn cs-item__btn--ghost"
        @click="$emit('view-detail', candidate)"
      >
        Xem chi tiết
      </button>
      <button
        class="cs-item__btn"
        :class="candidate.alreadyInPool ? 'cs-item__btn--saved' : 'cs-item__btn--primary'"
        :disabled="candidate.alreadyInPool || saving"
        @click="$emit('save', candidate)"
      >
        <span v-if="saving" class="material-symbols-outlined cs-item__spin">autorenew</span>
        <span v-else class="material-symbols-outlined">
          {{ candidate.alreadyInPool ? 'bookmark' : 'bookmark_add' }}
        </span>
        {{ candidate.alreadyInPool ? 'Đã lưu' : 'Lưu Pool' }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { ResCandidateSearchResultDTO } from '@/services/employerTalentPool.service'

const props = defineProps<{
  candidate: ResCandidateSearchResultDTO
  saving?: boolean
}>()

defineEmits<{
  (e: 'save', candidate: ResCandidateSearchResultDTO): void
  (e: 'view-detail', candidate: ResCandidateSearchResultDTO): void
}>()

const initials = computed(() =>
  props.candidate.fullName
    .split(' ')
    .slice(-2)
    .map(w => w[0])
    .join('')
    .toUpperCase(),
)

const WORK_TYPE_MAP: Record<string, string> = {
  FULL_TIME: 'Toàn thời gian',
  PART_TIME: 'Bán thời gian',
  REMOTE: 'Làm từ xa',
  HYBRID: 'Hybrid',
  FREELANCE: 'Freelance',
  INTERNSHIP: 'Thực tập',
}

const STATUS_MAP: Record<string, { label: string; variant: string }> = {
  ACTIVELY_LOOKING: { label: 'Đang tìm việc', variant: 'seeking' },
  OPEN_TO_OFFERS: { label: 'Sẵn sàng nghe đề xuất', variant: 'open' },
  NOT_LOOKING: { label: 'Không tìm việc', variant: 'inactive' },
}

const workTypeLabel = computed(() => {
  if (!props.candidate.preferredWorkType) return null
  const key = props.candidate.preferredWorkType.toUpperCase()
  return WORK_TYPE_MAP[key] ?? props.candidate.preferredWorkType
})

const statusInfo = computed(() =>
  props.candidate.jobSeekingStatus
    ? (STATUS_MAP[props.candidate.jobSeekingStatus] ?? null)
    : null,
)

const salaryLabel = computed(() => {
  const { salaryNegotiable, expectedSalaryMin, expectedSalaryMax } = props.candidate
  if (salaryNegotiable) return 'Thỏa thuận'
  const fmt = (v: number) => {
    if (v >= 1_000_000) return `${(v / 1_000_000).toFixed(0)}M`
    if (v >= 1_000) return `${(v / 1_000).toFixed(0)}K`
    return String(v)
  }
  if (expectedSalaryMin && expectedSalaryMax)
    return `${fmt(expectedSalaryMin)} - ${fmt(expectedSalaryMax)}`
  if (expectedSalaryMin) return `Từ ${fmt(expectedSalaryMin)}`
  return null
})
</script>

<style scoped>
.cs-item {
  display: flex;
  align-items: flex-start;
  gap: 1.5rem;
  padding: 1.5rem;
  border: 1px solid rgba(226, 232, 240, 0.3);
  border-radius: 0.75rem;
  background: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.2s;
}

.cs-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.cs-item__avatar-wrap {
  flex-shrink: 0;
}

.cs-item__avatar {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 0 0 2px #f1f4f9;
}

.cs-item__avatar--initials {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #4b9af6, #2563eb);
  color: #fff;
  font-size: 1.125rem;
  font-weight: 700;
  letter-spacing: 0.05em;
}

.cs-item__body {
  flex: 1;
  min-width: 0;
}

.cs-item__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.cs-item__name {
  color: #0f172a;
  font-size: 1.125rem;
  font-weight: 700;
}

.cs-item__role {
  margin-top: 2px;
  color: #64748b;
  font-size: 0.875rem;
}

.cs-item__status {
  flex-shrink: 0;
  padding: 4px 12px;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 800;
  letter-spacing: 0.03em;
  text-transform: uppercase;
}

.cs-item__status--open {
  background: #dbeafe;
  color: #2563eb;
}

.cs-item__status--seeking {
  background: #d1fae5;
  color: #065f46;
}

.cs-item__status--inactive {
  background: #f1f5f9;
  color: #64748b;
}

.cs-item__meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  color: #64748b;
  font-size: 0.875rem;
}

.cs-item__meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-right: 1rem;
  padding-right: 1rem;
  border-right: 1px solid rgba(226, 232, 240, 0.5);
}

.cs-item__meta-item:last-child {
  margin-right: 0;
  border-right: none;
}

.cs-item__meta-item .material-symbols-outlined {
  font-size: 1.125rem;
}

.cs-item__actions {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 12px;
  flex-shrink: 0;
  margin-left: 8px;
  padding-left: 1.5rem;
  border-left: 1px solid rgba(226, 232, 240, 0.3);
}

.cs-item__btn {
  display: flex;
  width: 128px;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 0.5rem;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  font-size: 1rem;
  font-weight: 700;
  transition: all 0.15s;
}

.cs-item__btn--ghost {
  border: 1px solid #cbd5e1;
  background: none;
  color: #0f172a;
}

.cs-item__btn--ghost:hover {
  background: #f1f5f9;
}

.cs-item__btn--primary {
  border: none;
  background: #4b9af6;
  box-shadow: 0 1px 4px rgba(75, 154, 246, 0.25);
  color: #fff;
}

.cs-item__btn--primary:hover:not(:disabled) {
  background: #2563eb;
}

.cs-item__btn--saved {
  border: 1px solid #cbd5e1;
  background: #f8fafc;
  color: #64748b;
  cursor: default;
}

.cs-item__btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.cs-item__btn .material-symbols-outlined,
.cs-item__spin {
  font-size: 1.125rem;
}

.cs-item__spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
