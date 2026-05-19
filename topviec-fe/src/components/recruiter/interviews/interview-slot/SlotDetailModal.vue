<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="open" class="modal-backdrop" @mousedown.self="$emit('close')">
        <div class="modal" role="dialog" aria-modal="true">

          <!-- Header -->
          <div class="modal__header">
            <div class="modal__header-left">
              <span class="modal__icon material-symbols-outlined">event_available</span>
              <div>
                <p class="modal__label">Chi tiết Slot</p>
                <h3 class="modal__title">Batch {{ slot.batchNumber }}</h3>
              </div>
            </div>
            <button class="modal__close" @click="$emit('close')">
              <span class="material-symbols-outlined">close</span>
            </button>
          </div>

          <!-- Body -->
          <div class="modal__body">

            <!-- Thời gian -->
            <div class="info-section">
              <p class="info-section__heading">Thời gian phỏng vấn</p>
              <div class="info-row">
                <span class="material-symbols-outlined info-row__icon">schedule</span>
                <div>
                  <p class="info-row__value">{{ formatTime(slot.startTime) }} – {{ formatEndTime(slot.endTime) }}</p>
                  <p class="info-row__sub">{{ formatDate(slot.startTime) }}</p>
                </div>
              </div>
            </div>

            <div class="divider" />

            <!-- Hạn chót -->
            <div class="info-section">
              <p class="info-section__heading">Hạn chót xác nhận</p>
              <div class="info-row">
                <span class="material-symbols-outlined info-row__icon">timer</span>
                <div>
                  <p class="info-row__value">{{ formatDateTime(slot.deadline) }}</p>
                </div>
              </div>
            </div>

            <div class="divider" />

            <!-- Hình thức & địa điểm -->
            <div class="info-section">
              <p class="info-section__heading">Hình thức & Địa điểm</p>
              <div class="info-row">
                <span class="material-symbols-outlined info-row__icon">
                  {{ slot.interviewType === 'online' ? 'videocam' : 'apartment' }}
                </span>
                <div>
                  <p class="info-row__value">{{ interviewTypeLabel }}</p>
                  <p v-if="slot.location" class="info-row__sub">{{ slot.location }}</p>
                </div>
              </div>
              <div v-if="slot.meetingLink" class="info-row info-row--link">
                <span class="material-symbols-outlined info-row__icon">link</span>
                <a :href="slot.meetingLink" target="_blank" rel="noopener" class="info-row__link">
                  {{ slot.meetingLink }}
                </a>
              </div>
            </div>

            <div class="divider" />

            <!-- Người phỏng vấn & sức chứa -->
            <div class="info-section info-section--grid">
              <div>
                <p class="info-section__heading">Người phỏng vấn</p>
                <div class="info-row">
                  <span class="material-symbols-outlined info-row__icon">person</span>
                  <p class="info-row__value">{{ slot.interviewerName || '—' }}</p>
                </div>
              </div>
              <div>
                <p class="info-section__heading">Đã đăng ký / Sức chứa</p>
                <div class="info-row">
                  <span class="material-symbols-outlined info-row__icon">group</span>
                  <p class="info-row__value">
                    <span :class="['capacity', { 'capacity--full': slot.registeredCount >= slot.maxCandidates }]">
                      {{ slot.registeredCount }}
                    </span>
                    <span class="capacity__sep">/</span>
                    {{ slot.maxCandidates }}
                  </p>
                </div>
              </div>
            </div>

          </div>

          <!-- Footer -->
          <div class="modal__footer">
            <button class="modal__btn modal__btn--close" @click="$emit('close')">Đóng</button>
          </div>

        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { SentSlotDTO } from '@/types/interview.types'

const props = defineProps<{
  open: boolean
  slot: SentSlotDTO
}>()

defineEmits<{ close: [] }>()

const INTERVIEW_TYPE_LABEL: Record<string, string> = {
  online: 'Trực tuyến (Online)',
  onsite: 'Trực tiếp (Onsite)',
  phone:  'Điện thoại',
}

const interviewTypeLabel = computed(
  () => INTERVIEW_TYPE_LABEL[props.slot.interviewType] ?? props.slot.interviewType
)

function pad(n: number) { return String(n).padStart(2, '0') }

function formatTime(iso: string) {
  const d = new Date(iso)
  return `${pad(d.getHours())}:${pad(d.getMinutes())}`
}

function formatEndTime(iso: string) {
  return formatTime(iso)
}

function formatDate(iso: string) {
  const d = new Date(iso)
  const days = ['Chủ nhật', 'Thứ hai', 'Thứ ba', 'Thứ tư', 'Thứ năm', 'Thứ sáu', 'Thứ bảy']
  return `${days[d.getDay()]}, ${pad(d.getDate())}/${pad(d.getMonth() + 1)}/${d.getFullYear()}`
}

function formatDateTime(iso: string) {
  const d = new Date(iso)
  return `${pad(d.getHours())}:${pad(d.getMinutes())} – ${pad(d.getDate())}/${pad(d.getMonth() + 1)}/${d.getFullYear()}`
}
</script>

<style scoped>
/* ── Backdrop ── */
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgb(15 23 42 / 0.45);
  backdrop-filter: blur(2px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: 1rem;
}

/* ── Modal box ── */
.modal {
  background: #fff;
  border-radius: 1.25rem;
  width: 100%;
  max-width: 28rem;
  box-shadow: 0 24px 60px rgb(15 23 42 / 0.18), 0 4px 16px rgb(15 23 42 / 0.08);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* ── Header ── */
.modal__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.25rem 1.5rem 1rem;
  border-bottom: 1px solid #f1f5f9;
}

.modal__header-left {
  display: flex;
  align-items: center;
  gap: 0.875rem;
}

.modal__icon {
  font-size: 1.5rem;
  color: #4b9af6;
  background: #eff6ff;
  padding: 0.5rem;
  border-radius: 0.75rem;
}

.modal__label {
  font-size: 0.75rem;
  font-weight: 600;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.modal__title {
  font-size: 1.125rem;
  font-weight: 800;
  color: #0f172a;
  margin-top: 0.1rem;
}

.modal__close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 2rem;
  border: none;
  background: #f1f5f9;
  border-radius: 9999px;
  cursor: pointer;
  color: #64748b;
  transition: background 0.15s, color 0.15s;
}
.modal__close:hover {
  background: #e2e8f0;
  color: #0f172a;
}
.modal__close .material-symbols-outlined {
  font-size: 1.125rem;
}

/* ── Body ── */
.modal__body {
  padding: 1.25rem 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.divider {
  height: 1px;
  background: #f1f5f9;
}

.info-section__heading {
  font-size: 0.6875rem;
  font-weight: 700;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  margin-bottom: 0.5rem;
}

.info-section--grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.info-row {
  display: flex;
  align-items: flex-start;
  gap: 0.625rem;
}

.info-row--link {
  margin-top: 0.5rem;
}

.info-row__icon {
  font-size: 1.125rem;
  color: #94a3b8;
  flex-shrink: 0;
  margin-top: 0.05rem;
}

.info-row__value {
  font-size: 0.9375rem;
  font-weight: 600;
  color: #0f172a;
  line-height: 1.4;
}

.info-row__sub {
  font-size: 0.8125rem;
  color: #64748b;
  margin-top: 0.125rem;
}

.info-row__link {
  font-size: 0.8125rem;
  color: #4b9af6;
  word-break: break-all;
  text-decoration: none;
}
.info-row__link:hover {
  text-decoration: underline;
}

/* ── Capacity ── */
.capacity {
  font-weight: 800;
  color: #059669;
}
.capacity--full {
  color: #ef4444;
}
.capacity__sep {
  color: #94a3b8;
  margin: 0 0.2rem;
}

/* ── Footer ── */
.modal__footer {
  padding: 1rem 1.5rem;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: flex-end;
}

.modal__btn {
  padding: 0.5rem 1.25rem;
  border-radius: 0.625rem;
  font-size: 0.875rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.15s;
}

.modal__btn--close {
  border: 1px solid #e2e8f0;
  background: transparent;
  color: #0f172a;
}
.modal__btn--close:hover {
  background: #f8fafc;
}

/* ── Transitions ── */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.2s ease;
}
.modal-enter-active .modal,
.modal-leave-active .modal {
  transition: transform 0.22s cubic-bezier(0.34, 1.36, 0.64, 1), opacity 0.2s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
.modal-enter-from .modal {
  transform: translateY(1.5rem) scale(0.97);
  opacity: 0;
}
.modal-leave-to .modal {
  transform: translateY(0.5rem) scale(0.98);
  opacity: 0;
}
</style>
