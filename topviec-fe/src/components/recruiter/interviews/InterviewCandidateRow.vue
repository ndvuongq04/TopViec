<template>
  <tr class="row">
    <!-- Candidate Info -->
    <td class="cell">
      <div class="candidate-info">
        <img
          class="candidate-avatar"
          :src="candidate.avatarUrl"
          :alt="candidate.name"
        />
        <div>
          <div class="candidate-name">{{ candidate.name }}</div>
          <div class="candidate-role">{{ candidate.role }}</div>
        </div>
      </div>
    </td>

    <!-- Date & Time -->
    <td class="cell">
      <div class="datetime">
        <span
          class="datetime__primary"
          :class="{ 'datetime__primary--overdue': candidate.status === 'overdue' }"
        >
          {{ candidate.dateLabel }}
        </span>
        <span class="datetime__secondary">{{ candidate.dateFormatted }}</span>
      </div>
    </td>

    <!-- Interviewer -->
    <td class="cell">
      <div class="interviewer">
        <div class="interviewer__avatar">
          <img
            v-if="candidate.interviewer.avatarUrl"
            class="interviewer__avatar-img"
            :src="candidate.interviewer.avatarUrl"
            :alt="candidate.interviewer.name"
          />
          <span v-else class="interviewer__avatar-fallback">
            {{ candidate.interviewer.initials }}
          </span>
        </div>
        <span class="interviewer__name">{{ candidate.interviewer.name }}</span>
      </div>
    </td>

    <!-- Interview Format -->
    <td class="cell">
      <div class="format">
        <span class="material-symbols-outlined format__icon">{{ formatIcon }}</span>
        <span class="format__label">{{ candidate.format }}</span>
      </div>
    </td>

    <!-- Status -->
    <td class="cell">
      <span class="status-badge" :class="statusClass">
        <span class="status-badge__dot" :class="statusDotClass"></span>
        {{ statusLabel }}
      </span>
    </td>

    <!-- Actions: GlobalDropdown -->
    <td class="cell cell--right">
      <GlobalDropdown>
        <template #default="{ close }">
          <!-- Xem chi tiết -->
          <GlobalDropdownItem
            icon="visibility"
            label="Xem chi tiết PV"
            @click="handleAction('viewDetail', close)"
          />

          <!-- Mở link -->
          <!-- <GlobalDropdownItem
            icon="link"
            label="Mở link phỏng vấn"
            :disabled="candidate.status !== 'confirmed'"
            :tooltip="candidate.status !== 'confirmed' ? 'Chỉ khả dụng khi lịch đã được xác nhận' : ''"
            @click="handleAction('openLink', close)"
          /> -->

          <!-- Đặt lịch phỏng vấn -->
          <GlobalDropdownItem
            icon="calendar_add_on"
            label="Đặt lịch phỏng vấn"
            :disabled="candidate.hasSchedule"
            :tooltip="candidate.hasSchedule ? 'Ứng viên đã có lịch phỏng vấn trong vòng này' : undefined"
            @click="handleAction('schedule', close)"
          />

          <div class="dropdown-divider-v2"></div>

          <!-- Nhắc nhở chọn lịch -->
          <GlobalDropdownItem
            icon="notifications"
            label="Nhắc nhở chọn lịch"
            :disabled="candidate.scheduleStatus !== 'scheduled'"
            :tooltip="candidate.scheduleStatus !== 'scheduled' ? (candidate.scheduleStatus === 'confirmed' ? 'Ứng viên đã xác nhận lịch' : 'Lịch chưa được xếp hoặc đã kết thúc') : ''"
            @click="handleAction('remind', close)"
          />

          <!-- Đổi lịch -->
          <GlobalDropdownItem
            icon="event_repeat"
            label="Đổi lịch phỏng vấn"
            :disabled="candidate.status === 'completed'"
            :tooltip="candidate.status === 'completed' ? 'Phỏng vấn đã kết thúc, không thể đổi lịch' : ''"
            @click="handleAction('reschedule', close)"
          />

          <!-- Đánh giá phỏng vấn -->
          <GlobalDropdownItem
            icon="rate_review"
            label="Đánh giá phỏng vấn"
            :disabled="candidate.scheduleStatus !== 'confirmed'"
            :tooltip="candidate.scheduleStatus !== 'confirmed' ? 'Chỉ đánh giá được khi ứng viên đã xác nhận lịch' : ''"
            @click="handleAction('evaluate', close)"
          />

          <div class="dropdown-divider-v2"></div>

          <!-- Offer -->
          <GlobalDropdownItem
            icon="card_giftcard"
            label="Phát hành Offer"
            :disabled="!candidate.isFinalRound || candidate.scheduleStatus !== 'completed' || ['offered', 'hired', 'rejected', 'completed'].includes(candidate.applicationStatus || '')"
            :tooltip="!candidate.isFinalRound ? 'Chỉ được offer ở vòng phỏng vấn cuối' : (candidate.scheduleStatus !== 'completed' ? 'Cần hoàn thành phỏng vấn trước khi offer' : (['offered', 'hired', 'rejected', 'completed'].includes(candidate.applicationStatus || '') ? 'Ứng viên đã có kết quả offer hoặc đã kết thúc quy trình' : ''))"
            @click="handleAction('offer', close)"
          />

          <!-- Lưu vào TalentPool -->
          <GlobalDropdownItem
            icon="group_add"
            label="Lưu vào TalentPool"
            @click="handleAction('saveTalentPool', close)"
          />

          <div class="dropdown-divider-v2"></div>

          <!-- Hủy lịch -->
          <GlobalDropdownItem
            icon="cancel"
            label="Hủy lịch phỏng vấn"
            danger
            :disabled="candidate.status === 'overdue'"
            :tooltip="candidate.status === 'overdue' ? 'Lịch đã quá hạn, không cần hủy' : ''"
            @click="handleAction('cancel', close)"
          />
        </template>
      </GlobalDropdown>
    </td>
  </tr>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import GlobalDropdown from '@/components/ui/GlobalDropdown.vue'
import GlobalDropdownItem from '@/components/ui/GlobalDropdownItem.vue'
import { INTERVIEW_STATUS_OPTIONS } from '@/constants/interview.constants'

interface Interviewer {
  id: number
  name: string
  avatarUrl?: string
  initials: string
}

interface InterviewCandidate {
  id: number
  name: string
  role: string
  avatarUrl: string
  dateLabel: string
  dateFormatted: string
  interviewer: Interviewer
  format: string
  formatType: 'online' | 'offline'
  status: 'confirmed' | 'pending' | 'overdue'| 'completed' | 'cancelled' | 'no_show'
  hasSchedule?: boolean
  scheduleStatus?: string
  applicationStatus?: string
  isFinalRound?: boolean
}

const props = defineProps<{
  candidate: InterviewCandidate
}>()

const emit = defineEmits<{
  openLink: [candidateId: number]
  viewDetail: [candidateId: number]
  reschedule: [candidateId: number]
  remind: [candidateId: number]
  cancel: [candidateId: number]
  schedule: [candidateId: number]
  evaluate: [candidateId: number]
  offer: [candidateId: number]
  saveTalentPool: [candidateId: number]
}>()

function handleAction(action: 'viewDetail' | 'openLink' | 'reschedule' | 'remind' | 'cancel' | 'schedule' | 'evaluate' | 'offer' | 'saveTalentPool', close: () => void) {
  close()
  emit(action as any, props.candidate.id)
}

// ── Computed ────────────────────────────────────────────────────
const formatIcon = computed(() =>
  props.candidate.formatType === 'online' ? 'videocam' : 'apartment'
)

const COLOR_CLASS: Record<string, string> = {
  default: 'status-badge--warning',
  blue:    'status-badge--info',
  green:   'status-badge--success',
  gray:    'status-badge--neutral',
  red:     'status-badge--error',
  orange:  'status-badge--orange',
}

const DOT_COLOR_CLASS: Record<string, string> = {
  default: 'status-badge__dot--warning',
  blue:    'status-badge__dot--info',
  green:   'status-badge__dot--success',
  gray:    'status-badge__dot--neutral',
  red:     'status-badge__dot--error',
  orange:  'status-badge__dot--orange',
}

const isOverdue = computed(() => props.candidate.applicationStatus === 'overdue')

const activeStatusOption = computed(() =>
  INTERVIEW_STATUS_OPTIONS.find(o => o.value === props.candidate.scheduleStatus)
)

const statusClass    = computed(() =>
  isOverdue.value ? 'status-badge--error' : (COLOR_CLASS[activeStatusOption.value?.color ?? 'default'] ?? '')
)
const statusDotClass = computed(() =>
  isOverdue.value ? 'status-badge__dot--error' : (DOT_COLOR_CLASS[activeStatusOption.value?.color ?? 'default'] ?? '')
)
const statusLabel    = computed(() =>
  isOverdue.value ? 'Quá hạn' : (activeStatusOption.value?.label ?? props.candidate.scheduleStatus ?? '')
)
</script>

<style scoped>
/* ============================================
   Font Size Scale:
   0.875rem (14px) = Data cells, metadata, chip
   0.75rem  (12px) = th, badge, label nhỏ (bold + uppercase)
   ============================================ */

.row { transition: background 0.15s; }
.row:hover { background: rgba(248, 250, 252, 0.8); }

.cell { padding: 1rem 1.5rem; }
.cell--right { text-align: right; }

/* --- Candidate Info --- */
.candidate-info { display: flex; align-items: center; gap: 0.75rem; }
.candidate-avatar { width: 2.5rem; height: 2.5rem; border-radius: 9999px; object-fit: cover; }
.candidate-name  { font-size: 0.875rem; font-weight: 700; color: #0f172a; }
.candidate-role  { font-size: 0.75rem;  font-weight: 500; color: #64748b; }

/* --- DateTime --- */
.datetime { display: flex; flex-direction: column; }
.datetime__primary          { font-size: 0.875rem; font-weight: 700; color: #0f172a; }
.datetime__primary--overdue { color: #ef4444; }
.datetime__secondary        { font-size: 0.75rem;  font-weight: 500; color: #94a3b8; }

/* --- Interviewer --- */
.interviewer { display: flex; align-items: center; gap: 0.5rem; }
.interviewer__avatar {
  width: 1.5rem; height: 1.5rem; border-radius: 9999px;
  background: #e2e8f0; overflow: hidden; flex-shrink: 0;
}
.interviewer__avatar-img      { width: 100%; height: 100%; object-fit: cover; }
.interviewer__avatar-fallback {
  width: 100%; height: 100%; display: flex; align-items: center; justify-content: center;
  background: #f1f5f9; font-size: 0.75rem; font-weight: 700; color: #64748b;
}
.interviewer__name { font-size: 0.875rem; font-weight: 500; color: #475569; }

/* --- Format --- */
.format        { display: flex; align-items: center; gap: 0.5rem; color: #475569; }
.format__icon  { font-size: 1.125rem; }
.format__label { font-size: 0.875rem; font-weight: 500; }

/* --- Status Badge --- */
.status-badge {
  display: inline-flex; align-items: center;
  padding: 0.25rem 0.625rem; border-radius: 9999px;
  font-size: 0.75rem; font-weight: 700;
}
.status-badge--success { background: #d1fae5; color: #059669; }
.status-badge--warning { background: #fef3c7; color: #d97706; }
.status-badge--error   { background: #fee2e2; color: #ef4444; }
.status-badge--info    { background: #dbeafe; color: #2563eb; }
.status-badge--neutral { background: #f1f5f9; color: #64748b; }
.status-badge--orange  { background: #ffedd5; color: #ea580c; }

.status-badge__dot { width: 0.375rem; height: 0.375rem; border-radius: 9999px; margin-right: 0.5rem; }
.status-badge__dot--success { background: #10b981; }
.status-badge__dot--warning { background: #f59e0b; }
.status-badge__dot--error   { background: #ef4444; }
.status-badge__dot--info    { background: #3b82f6; }
.status-badge__dot--neutral { background: #94a3b8; }
.status-badge__dot--orange  { background: #f97316; }

/* Divider inside global dropdown content */
.dropdown-divider-v2 {
  height: 1px;
  background: #f1f5f9;
  margin: 0.375rem 0.25rem;
}
</style>