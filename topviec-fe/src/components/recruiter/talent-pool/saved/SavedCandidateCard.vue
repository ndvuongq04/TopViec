<template>
  <div class="sc-card">
    <div class="sc-card__top">
      <div class="sc-card__avatar-wrap">
        <img v-if="candidate.candidateAvatarUrl" :alt="candidate.candidateName" :src="candidate.candidateAvatarUrl" class="sc-card__avatar" />
        <div v-else class="sc-card__avatar-initials">{{ initials }}</div>
      </div>
      <div class="sc-card__info">
        <h3 class="sc-card__name">{{ candidate.candidateName }}</h3>
        <p class="sc-card__role">{{ candidate.preferredJobTitle || '—' }}</p>
      </div>
      <span class="sc-card__label" :class="sourceLabelClass">{{ sourceLabel }}</span>
    </div>

    <div class="sc-card__meta">
      <div class="sc-card__meta-item">
        <span class="material-symbols-outlined">payments</span>{{ salaryDisplay }}
      </div>
      <div v-if="candidate.preferredLocationName" class="sc-card__meta-item sc-card__meta-item--right">
        <span class="material-symbols-outlined">location_on</span>{{ candidate.preferredLocationName }}
      </div>
      <div v-else class="sc-card__meta-item sc-card__meta-item--placeholder"></div>
      <div v-if="candidate.preferredWorkType" class="sc-card__meta-item">
        <span class="material-symbols-outlined">work</span>{{ workTypeLabel }}
      </div>
      <div v-else class="sc-card__meta-item sc-card__meta-item--placeholder"></div>
      <div v-if="candidate.jobSeekingStatus" class="sc-card__meta-item sc-card__meta-item--right">
        <span class="material-symbols-outlined">circle</span>{{ seekingStatusLabel }}
      </div>
      <div v-if="candidate.note" class="sc-card__meta-item sc-card__meta-item--note">
        <span class="material-symbols-outlined">edit_note</span>
        <span class="sc-card__note-text">{{ candidate.note }}</span>
      </div>
    </div>

    <div class="sc-card__footer">
      <span class="sc-card__saved-at">Đã lưu: {{ formatDate(candidate.addedAt) }}</span>
      <div class="sc-card__actions">
        <button class="sc-card__action-btn" title="Xem hồ sơ" @click="emit('view-detail', candidate.talentPoolId)">
          <span class="material-symbols-outlined">visibility</span>
        </button>
        <button class="sc-card__action-btn" title="Gửi email" @click="emit('invite-email', candidate.talentPoolId)">
          <span class="material-symbols-outlined">mail</span>
        </button>
        <button class="sc-card__action-btn" title="Chỉnh sửa ghi chú" @click="emit('edit-note', candidate.talentPoolId, candidate.note ?? '')">
          <span class="material-symbols-outlined">edit_note</span>
        </button>
        <button class="sc-card__action-btn sc-card__action-btn--delete" title="Xóa khỏi danh sách" @click="emit('delete', candidate.talentPoolId)">
          <span class="material-symbols-outlined">delete</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { ResTalentPoolCandidateDTO } from '@/services/employerTalentPool.service'

const props = defineProps<{ candidate: ResTalentPoolCandidateDTO }>()
const emit = defineEmits<{
  (e: 'view-detail', talentPoolId: number): void
  (e: 'invite-email', talentPoolId: number): void
  (e: 'edit-note', talentPoolId: number, currentNote: string): void
  (e: 'delete', talentPoolId: number): void
}>()

const initials = computed(() => {
  const parts = (props.candidate.candidateName ?? '').trim().split(' ')
  return parts.length >= 2
    ? (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
    : (props.candidate.candidateName ?? '??').slice(0, 2).toUpperCase()
})

const SOURCE_MAP: Record<string, { label: string; cls: string }> = {
  'Duyệt CV': { label: 'Duyệt CV', cls: 'sc-card__label--review' },
  'PV UV': { label: 'Phỏng vấn', cls: 'sc-card__label--interview' },
  'Tìm kiếm UV trong DB': { label: 'Tìm kiếm', cls: 'sc-card__label--search' },
}

const sourceLabel = computed(() => SOURCE_MAP[props.candidate.source]?.label ?? props.candidate.source)
const sourceLabelClass = computed(() => SOURCE_MAP[props.candidate.source]?.cls ?? 'sc-card__label--default')

const WORK_TYPE_MAP: Record<string, string> = {
  full_time: 'Toàn thời gian',
  part_time: 'Bán thời gian',
  remote: 'Làm từ xa',
  hybrid: 'Kết hợp',
}
const workTypeLabel = computed(() => WORK_TYPE_MAP[props.candidate.preferredWorkType ?? ''] ?? props.candidate.preferredWorkType ?? '')

const SEEKING_MAP: Record<string, string> = {
  active: 'Đang tìm việc gấp',
  passive: 'Sẵn sàng nếu có cơ hội',
  not_looking: 'Không tìm việc',
}
const seekingStatusLabel = computed(() => SEEKING_MAP[props.candidate.jobSeekingStatus ?? ''] ?? props.candidate.jobSeekingStatus ?? '')

const salaryDisplay = computed(() => {
  const { expectedSalaryMin, expectedSalaryMax, salaryNegotiable } = props.candidate
  if (salaryNegotiable) return 'Thương lượng'
  if (expectedSalaryMin && expectedSalaryMax)
    return `${expectedSalaryMin.toLocaleString('vi-VN')} - ${expectedSalaryMax.toLocaleString('vi-VN')} VNĐ`
  if (expectedSalaryMin) return `Từ ${expectedSalaryMin.toLocaleString('vi-VN')} VNĐ`
  if (expectedSalaryMax) return `Đến ${expectedSalaryMax.toLocaleString('vi-VN')} VNĐ`
  return 'Chưa cập nhật'
})

function formatDate(iso: string): string {
  if (!iso) return '—'
  const d = new Date(iso)
  return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
}
</script>

<style scoped>
.sc-card {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 1.25rem;
  border: 1px solid #f8fafd;
  border-radius: 0.75rem;
  background: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.2s;
}
.sc-card:hover { box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08); }

.sc-card__top { display: flex; align-items: center; gap: 12px; }

.sc-card__avatar {
  width: 48px; height: 48px; flex-shrink: 0;
  border-radius: 50%; object-fit: cover;
}
.sc-card__avatar-initials {
  display: flex; width: 48px; height: 48px; flex-shrink: 0;
  align-items: center; justify-content: center;
  border-radius: 50%; background: #f1f4f9;
  color: #4b9af6; font-size: 1.125rem; font-weight: 700;
}

.sc-card__info { flex: 1; min-width: 0; }
.sc-card__name { color: #0f172a; font-size: 1.125rem; font-weight: 700; line-height: 1.3; }
.sc-card__role { margin-top: 2px; color: #64748b; font-size: 0.875rem; }

.sc-card__label {
  margin-left: auto; flex-shrink: 0;
  padding: 2px 8px; border-radius: 4px;
  font-size: 0.75rem; font-weight: 800;
  letter-spacing: 0.04em; text-transform: uppercase;
}
.sc-card__label--review    { background: #dbeafe; color: #1d4ed8; }
.sc-card__label--interview { background: #d1fae5; color: #065f46; }
.sc-card__label--search    { background: #fef3c7; color: #b45309; }
.sc-card__label--default   { background: #e9edf5; color: #64748b; }

.sc-card__meta {
  display: grid; grid-template-columns: 1fr 1fr;
  gap: 8px; color: #64748b; font-size: 0.875rem;
}
.sc-card__meta-item { display: flex; align-items: center; gap: 6px; }
.sc-card__meta-item .material-symbols-outlined { font-size: 0.875rem; }
.sc-card__meta-item--placeholder { visibility: hidden; }
.sc-card__meta-item--right { grid-column: 2; }
.sc-card__meta-item--note {
  grid-column: 1 / -1;
  align-items: flex-start;
  background: #fffbeb;
  border-left: 3px solid #fbbf24;
  border-radius: 0 6px 6px 0;
  padding: 6px 10px;
  gap: 8px;
}
.sc-card__meta-item--note .material-symbols-outlined { color: #f59e0b; margin-top: 1px; flex-shrink: 0; }
.sc-card__note-text {
  color: #78716c;
  font-size: 0.8125rem;
  line-height: 1.5;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.sc-card__footer {
  display: flex; align-items: center; justify-content: space-between;
  padding-top: 12px; border-top: 1px solid rgba(226, 232, 240, 0.5);
}
.sc-card__saved-at { color: #64748b; font-size: 0.75rem; font-weight: 600; }

.sc-card__actions { display: flex; gap: 8px; }
.sc-card__action-btn {
  display: flex; align-items: center; justify-content: center;
  padding: 6px; border: none; border-radius: 6px;
  background: #f1f4f9; color: #64748b; cursor: pointer; transition: color 0.15s;
}
.sc-card__action-btn:hover { color: #4b9af6; }
.sc-card__action-btn--delete:hover { color: #ef4444; }
.sc-card__action-btn .material-symbols-outlined { font-size: 1rem; }
</style>
