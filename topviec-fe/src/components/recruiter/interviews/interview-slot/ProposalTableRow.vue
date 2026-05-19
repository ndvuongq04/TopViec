<template>
  <tr class="row">
    <!-- Candidate -->
    <td class="row__cell">
      <div class="candidate">
        <div class="candidate__avatar">
          <img
            v-if="proposal.candidate?.avatar"
            :src="proposal.candidate.avatar"
            :alt="proposal.candidate.name"
          />
          <span v-else class="candidate__initials">
            {{ getInitials(proposal.candidate?.name) }}
          </span>
        </div>
        <div>
          <div class="candidate__name">{{ proposal.candidate?.name }}</div>
          <div class="candidate__role">{{ proposal.candidate?.position }}</div>
        </div>
      </div>
    </td>

    <!-- Interview Round -->
    <td class="row__cell">
      <span class="round-label">{{ proposal.roundLabel }}</span>
    </td>

    <!-- Slots đã gửi cho UV -->
    <td class="row__cell">
      <div v-if="proposal.sentSlots?.length" class="slots">
        <span
          v-for="(slot, i) in proposal.sentSlots"
          :key="i"
          class="slot slot--clickable"
          @click="openSlotDetail(Number(i))"
        >
          <span class="material-symbols-outlined slot__icon">calendar_month</span>
          {{ slot }}
        </span>
      </div>
      <span v-else class="empty-text">—</span>
    </td>

    <!-- Slot detail modal -->
    <SlotDetailModal
      v-if="proposal.rawSentSlots?.length"
      :open="selectedSlotIndex !== null"
      :slot="selectedSlot!"
      @close="selectedSlotIndex = null"
    />

    <!-- Slot UV đã xác nhận -->
    <td class="row__cell">
      <div v-if="proposal.status === 'confirmed' && proposal.confirmedSlot" class="slots">
        <span class="slot slot--confirmed">{{ proposal.confirmedSlot }}</span>
      </div>
      <span v-else class="empty-text">Chưa xác nhận</span>
    </td>

    <!-- Status -->
    <td class="row__cell">
      <span class="status-badge" :class="statusClass">
        <span class="status-badge__dot" :class="statusDotClass"></span>
        {{ statusLabel }}
      </span>
    </td>

    <!-- Actions -->
    <td class="row__cell row__cell--right">
      <GlobalDropdown>
        <template #trigger="{ toggle, isOpen }">
          <button
            class="btn-dots-v"
            :class="{ 'btn-dots-v--active': isOpen }"
            @click.stop="toggle"
            aria-label="Hành động"
          >
            <span class="dots-v-icon">
              <span></span><span></span><span></span>
            </span>
          </button>
        </template>
        <template #default="{ close }">
          <GlobalDropdownItem
            icon="schedule_send"
            label="Gia hạn thêm"
            :disabled="!isOverdue"
            :tooltip="!isOverdue ? 'Chỉ khả dụng khi ứng viên quá hạn phản hồi' : ''"
            @click="emit('extendDeadline'); close()"
          />
          <GlobalDropdownItem
            icon="edit_calendar"
            label="Đặt lịch hộ"
            :disabled="!isOverdue"
            :tooltip="!isOverdue ? 'Chỉ khả dụng khi ứng viên quá hạn phản hồi' : ''"
            @click="emit('forceSchedule'); close()"
          />
        </template>
      </GlobalDropdown>
    </td>

  </tr>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { INTERVIEW_STATUS_OPTIONS } from '@/constants/interview.constants'
import SlotDetailModal from './SlotDetailModal.vue'
import GlobalDropdown from '@/components/ui/GlobalDropdown.vue'
import GlobalDropdownItem from '@/components/ui/GlobalDropdownItem.vue'
import type { SentSlotDTO } from '@/types/interview.types'

const props = defineProps<{
  proposal: any
}>()

const emit = defineEmits<{
  extendDeadline: []
  forceSchedule:  []
}>()

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

const isOverdue = computed(() => props.proposal.applicationStatus === 'overdue')

const activeStatusOption = computed(() =>
  INTERVIEW_STATUS_OPTIONS.find(o => o.value === props.proposal.scheduleStatus)
)

const statusClass    = computed(() =>
  isOverdue.value ? 'status-badge--error' : (COLOR_CLASS[activeStatusOption.value?.color ?? 'default'] ?? '')
)
const statusDotClass = computed(() =>
  isOverdue.value ? 'status-badge__dot--error' : (DOT_COLOR_CLASS[activeStatusOption.value?.color ?? 'default'] ?? '')
)
const statusLabel    = computed(() =>
  isOverdue.value ? 'Quá hạn' : (activeStatusOption.value?.label ?? props.proposal.scheduleStatus ?? '')
)

const selectedSlotIndex = ref<number | null>(null)
const selectedSlot = computed<SentSlotDTO | null>(() =>
  selectedSlotIndex.value !== null
    ? (props.proposal.rawSentSlots?.[selectedSlotIndex.value] ?? null)
    : null
)

function openSlotDetail(index: number) {
  selectedSlotIndex.value = index
}

const getInitials = (name?: string) => {
  if (!name) return '?'
  return name
    .split(' ')
    .map((w) => w[0])
    .slice(0, 2)
    .join('')
    .toUpperCase()
}
</script>

<style scoped>
.row {
  transition: background 0.15s;
}
.row:hover {
  background: rgb(248 250 252 / 0.4);
}

.row__cell {
  padding: 1.25rem 1.5rem;
  vertical-align: middle;
  border-bottom: 1px solid #f8fafc;
}

.row__cell--right {
  text-align: right;
}

/* ── 3-dot vertical button ── */
.btn-dots-v {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 2.25rem;
  height: 2.25rem;
  border-radius: 0.5rem;
  border: none;
  background: transparent;
  cursor: pointer;
  color: #94a3b8;
  transition: all 0.15s;
}
.btn-dots-v:hover,
.btn-dots-v--active {
  background: #f1f5f9;
  color: #4b9af6;
}
.dots-v-icon {
  display: flex;
  flex-direction: column;
  gap: 3px;
  align-items: center;
}
.dots-v-icon span {
  display: block;
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: currentColor;
}

/* ── Candidate ── */
.candidate {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.candidate__avatar {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 9999px;
  background: #dbeafe;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.candidate__avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.candidate__initials {
  font-size: 0.75rem;
  font-weight: 700;
  color: #3b82f6;
}

.candidate__name {
  font-weight: 700;
  color: #0f172a;
  font-size: 0.875rem;
}

.candidate__role {
  font-size: 0.75rem;
  color: #64748b;
}

/* ── Round ── */
.round-label {
  font-size: 0.875rem;
  font-weight: 500;
  color: #0f172a;
}

/* ── Slots ── */
.slots {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.slot {
  font-size: 0.75rem;
  padding: 0.25rem 0.5rem;
  background: #f1f4f9;
  border: 1px solid rgb(226 232 240 / 0.3);
  border-radius: 0.375rem;
  color: #0f172a;
}

.slot--clickable {
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s, box-shadow 0.15s;
}
.slot--clickable:hover {
  background: #e0eaff;
  border-color: rgb(75 154 246 / 0.35);
  box-shadow: 0 1px 4px rgb(75 154 246 / 0.12);
}

.slot__icon {
  font-size: 0.75rem;
  vertical-align: middle;
  margin-right: 0.2rem;
  color: #64748b;
}

.slot--confirmed {
  background: #ecfdf5;
  color: #047857;
  font-weight: 700;
  border-color: #d1fae5;
}

.empty-text {
  font-size: 0.75rem;
  color: #94a3b8;
  font-style: italic;
}

/* ── Status badge ── */
.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 0.125rem 0.625rem;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 700;
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

</style>