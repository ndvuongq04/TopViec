<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from '@/composables/useToast'
import { INTERVIEW_TYPE } from '@/constants/interview.constants'
import type { InterviewType } from '@/constants/interview.constants'
import type { ReqCreateInterviewSlotsDTO, SlotDTO, ResInterviewSlotDTO } from '@/types/interview.types'
import type { ResEmployerApplicationDTO } from '@/types/employerApplication.types'
import Breadcrumb from '@/components/ui/Breadcrumb.vue'
import SearchableSelect from '@/components/ui/SearchableSelect.vue'
import type { SelectOption } from '@/components/ui/SearchableSelect.vue'
import employerInterviewService from '@/services/employerInterview.service'
import employerApplicationService from '@/services/employerApplication.service'
import { employerJobPostingService } from '@/services/jobPosting.service'

const route  = useRoute()
const router = useRouter()
const toast  = useToast()

// ── Query params ──────────────────────────────────────────
const jobId   = Number(route.query.jobId)   || null
const roundId = Number(route.query.roundId) || null
const roundName = String(route.query.roundName || 'Chưa xác định')

// ── Remote data ───────────────────────────────────────────
const jobTitle    = ref('Đang tải...')
const candidates  = ref<ResEmployerApplicationDTO[]>([])
const fetchError  = ref('')
const alreadySent = ref(false)   // round đã có slots từ trước

onMounted(async () => {
  if (!jobId || !roundId) {
    fetchError.value = 'Thiếu thông tin vòng phỏng vấn. Vui lòng quay lại trang trước.'
    return
  }
  try {
    const [job, appPage, existingSlots] = await Promise.all([
      employerJobPostingService.getById(jobId),
      employerApplicationService.getApplicationsByJobPost(jobId, { status: 'INTERVIEWING', size: 100 }),
      employerInterviewService.getSlots(roundId),
    ])
    jobTitle.value   = job.title
    candidates.value = appPage.result ?? []

    if (existingSlots.length > 0) {
      alreadySent.value = true
      // Khôi phục deadline từ slot đầu tiên (tất cả cùng deadline)
      if (existingSlots[0].slotDeadline) {
        deadline.value = existingSlots[0].slotDeadline.slice(0, 16)
      }
      slots.value = existingSlots.map((s: ResInterviewSlotDTO) => ({
        date:            s.startTime.slice(0, 10),
        interviewerName: s.interviewerName ?? '',
        startTime:       s.startTime.slice(11, 16),
        endTime:         s.endTime.slice(11, 16),
        type:            s.interviewType as InterviewType,
        link:            s.meetingLink ?? s.location ?? '',
        capacity:        s.maxCandidates,
      }))
    }
  } catch (err: any) {
    fetchError.value = err?.response?.data?.message ?? 'Không thể tải dữ liệu.'
    toast.error('Lỗi', fetchError.value)
  }
})

// ── Recipient mode ────────────────────────────────────────
type RecipientMode = 'all' | 'exclude'
const recipientMode = ref<RecipientMode>('all')
const excludedIds   = ref<(number | null)[]>([null])

function addExclusion() { excludedIds.value.push(null) }
function removeExclusion(idx: number) { excludedIds.value.splice(idx, 1) }

const candidateOptions = computed<SelectOption[]>(() =>
  candidates.value.map(c => ({ id: c.id, name: `${c.candidateName} (${c.candidateEmail})` }))
)

function exclusionOptionsFor(idx: number): SelectOption[] {
  const otherSelected = excludedIds.value.filter((id, i) => i !== idx && id !== null)
  return candidateOptions.value.filter(o => !otherSelected.includes(o.id as number))
}

const recipientSummary = computed(() => {
  const total = candidates.value.length
  if (recipientMode.value === 'all') return `Toàn bộ ${total} ứng viên`
  const excluded = excludedIds.value.filter(Boolean).length
  return `${total - excluded} ứng viên (trừ ${excluded} người)`
})

// ── Slot state ────────────────────────────────────────────
interface Slot {
  date:            string
  interviewerName: string
  startTime:       string
  endTime:         string
  type:            InterviewType
  link:            string
  capacity:        number
}

const deadline = ref('')

const slots = ref<Slot[]>([
  { date: '', interviewerName: '', startTime: '09:00', endTime: '10:00', type: INTERVIEW_TYPE.ONLINE, link: '', capacity: 1 },
])

function addSlot() {
  const last = slots.value[slots.value.length - 1]
  slots.value.push({
    date:            last?.date ?? '',
    interviewerName: last?.interviewerName ?? '',
    startTime:       '10:00',
    endTime:         '11:00',
    type:            last?.type ?? INTERVIEW_TYPE.ONLINE,
    link:            last?.link ?? '',
    capacity:        1,
  })
}

function removeSlot(idx: number) {
  if (slots.value.length > 1) slots.value.splice(idx, 1)
}

// ── Validation errors ─────────────────────────────────────
interface SlotErrors { date: boolean; interviewerName: boolean; startTime: boolean; endTime: boolean }

const errors = ref({
  deadline:   false,
  recipients: false,
  slots:      [] as SlotErrors[],
})

function resetSlotErrors() {
  errors.value.slots = slots.value.map(() => ({
    date: false, interviewerName: false, startTime: false, endTime: false,
  }))
}

// clear field error on input
function clearError(field: keyof typeof errors.value) {
  (errors.value as any)[field] = false
}
function clearSlotError(idx: number, field: keyof SlotErrors) {
  if (errors.value.slots[idx]) errors.value.slots[idx][field] = false
}

// ── Submit ────────────────────────────────────────────────
const loading = ref(false)

async function handleSubmit() {
  if (!roundId) {
    toast.error('Lỗi', 'Thiếu thông tin vòng phỏng vấn.')
    return
  }

  // reset
  errors.value.deadline   = false
  errors.value.recipients = false
  resetSlotErrors()

  let hasError = false

  if (!deadline.value) {
    errors.value.deadline = true
    hasError = true
  }

  for (let i = 0; i < slots.value.length; i++) {
    const s = slots.value[i]
    if (!s.date)                    { errors.value.slots[i].date            = true; hasError = true }
    if (!s.interviewerName.trim())  { errors.value.slots[i].interviewerName = true; hasError = true }
    if (!s.startTime)               { errors.value.slots[i].startTime       = true; hasError = true }
    if (!s.endTime)                 { errors.value.slots[i].endTime         = true; hasError = true }
  }

  if (hasError) {
    toast.error('Thiếu thông tin', 'Vui lòng điền đầy đủ các trường được đánh dấu đỏ.')
    return
  }

  const confirmedExcluded = excludedIds.value.filter((id): id is number => id !== null)
  const recipientIds = recipientMode.value === 'all'
    ? candidates.value.map(c => c.id)
    : candidates.value.filter(c => !confirmedExcluded.includes(c.id)).map(c => c.id)

  if (recipientIds.length === 0) {
    toast.error('Không có người nhận', 'Danh sách ứng viên trống hoặc tất cả đã bị loại trừ.')
    return
  }

  const payload: ReqCreateInterviewSlotsDTO = {
    applicationIds: recipientIds,
    deadline:       `${deadline.value}:00`,
    slots: slots.value.map((s): SlotDTO => ({
      startTime:       `${s.date}T${s.startTime}:00`,
      endTime:         `${s.date}T${s.endTime}:00`,
      maxCandidates:   s.capacity,
      interviewType:   s.type,
      location:        s.type === INTERVIEW_TYPE.ONSITE ? s.link || undefined : undefined,
      meetingLink:     s.type === INTERVIEW_TYPE.ONLINE  ? s.link || undefined : undefined,
      interviewerName: s.interviewerName.trim() || undefined,
    })),
  }

  loading.value = true
  try {
    await employerInterviewService.createSlots(roundId, payload)
    toast.success('Đã gửi đề xuất', `Gửi ${slots.value.length} khung giờ tới ${recipientSummary.value}.`)
    router.push(jobId ? `/recruiter/jobs/${jobId}/interview-setup` : '/recruiter/interviews')
  } catch (err: any) {
    const msg: string = err?.response?.data?.message ?? ''
    if (msg.toLowerCase().includes('đã có slot')) {
      toast.error('Đã tồn tại lịch hẹn', 'Vòng phỏng vấn này đã được gửi đề xuất trước đó. Tính năng xem và chỉnh sửa slot sẽ sớm được bổ sung.')
    } else {
      toast.error('Lỗi', msg || 'Không thể gửi đề xuất. Vui lòng thử lại.')
    }
  } finally {
    loading.value = false
  }
}

// ── Breadcrumb ────────────────────────────────────────────
const breadcrumbItems = computed(() => [
  { label: 'Quản lý lịch phỏng vấn', to: '/recruiter/interviews' },
  { label: 'Thiết lập phỏng vấn',    to: jobId ? `/recruiter/jobs/${jobId}/interview-setup` : '/recruiter/interviews' },
  { label: 'Đặt lịch thông minh' },
])
</script>

<template>
  <div class="propose-page">

    <!-- Header -->
    <div class="propose-page__top">
      <Breadcrumb :items="breadcrumbItems" :hide-home="true" />
      <h1 class="propose-page__title">Đặt lịch thông minh</h1>
      <p class="propose-page__subtitle">
        Gửi danh sách khung giờ để ứng viên chủ động chọn thời gian phù hợp.
      </p>
    </div>

    <!-- Grid -->
    <div class="propose-page__grid">
      <div class="propose-page__main">

        <!-- ① Thông tin cơ bản -->
        <div class="card">
          <div class="card__header">
            <div class="step-badge">1</div>
            <h3 class="card__title">Thông tin cơ bản</h3>
          </div>
          <div class="form-grid">
            <div class="field">
              <label class="field__label">Tin tuyển dụng</label>
              <input :value="jobTitle" disabled class="field__input field__input--disabled" />
            </div>
            <div class="field">
              <label class="field__label">Vòng phỏng vấn</label>
              <input :value="roundName" disabled class="field__input field__input--disabled" />
            </div>
            <div class="field field--full">
              <label class="field__label">Hạn chót ứng viên chọn lịch</label>
              <input v-model="deadline" type="datetime-local" :disabled="alreadySent" :class="['field__input', alreadySent && 'field__input--disabled', errors.deadline && 'field__input--error']" @change="clearError('deadline')" />
            </div>
          </div>
        </div>

        <!-- ② Người nhận -->
        <div class="card">
          <div class="card__header">
            <div class="step-badge">2</div>
            <h3 class="card__title">Người nhận đề xuất</h3>
          </div>

          <p class="recipient-notice">
            <span class="material-symbols-outlined recipient-notice__icon">info</span>
            Danh sách chỉ bao gồm ứng viên <strong>chưa có lịch phỏng vấn</strong> trong vòng này.
          </p>

          <!-- Mode toggle -->
          <div class="mode-toggle">
            <button
              :class="['mode-toggle__btn', recipientMode === 'all' && 'mode-toggle__btn--active']"
              :disabled="alreadySent"
              @click="recipientMode = 'all'"
            >
              <span class="material-symbols-outlined">groups</span>
              Toàn bộ ứng viên
            </button>
            <button
              :class="['mode-toggle__btn', recipientMode === 'exclude' && 'mode-toggle__btn--active']"
              :disabled="alreadySent"
              @click="recipientMode = 'exclude'"
            >
              <span class="material-symbols-outlined">person_remove</span>
              Trừ một số ứng viên
            </button>
          </div>

          <!-- Exclusion list -->
          <template v-if="recipientMode === 'exclude'">
            <div class="exclusion-list">
              <div v-for="(_, idx) in excludedIds" :key="idx" class="exclusion-row">
                <div class="field exclusion-row__field">
                  <label class="field__label">Không gửi cho</label>
                  <SearchableSelect
                    :model-value="excludedIds[idx]"
                    :options="exclusionOptionsFor(idx)"
                    placeholder="Tìm và chọn ứng viên..."
                    :disabled="alreadySent"
                    @update:model-value="excludedIds[idx] = $event as number"
                  />
                </div>
                <button
                  v-if="excludedIds.length > 1 && !alreadySent"
                  class="exclusion-row__remove"
                  @click="removeExclusion(idx)"
                >
                  <span class="material-symbols-outlined">close</span>
                </button>
              </div>
            </div>
            <button v-if="!alreadySent" class="btn-add-row" @click="addExclusion">
              <span class="material-symbols-outlined">add</span>
              Thêm ứng viên cần loại trừ
            </button>
          </template>

          <div class="recipient-summary">
            <span class="material-symbols-outlined recipient-summary__icon">send</span>
            <span class="recipient-summary__text">Sẽ gửi tới: <strong>{{ recipientSummary }}</strong></span>
          </div>
        </div>

        <!-- ③ Lịch hẹn -->
        <div class="card">
          <div class="card__header card__header--between">
            <div class="card__header-left">
              <div class="step-badge">3</div>
              <h3 class="card__title">Các lịch hẹn đề xuất</h3>
            </div>
            <button v-if="!alreadySent" class="btn-add-slot" @click="addSlot">
              <span class="material-symbols-outlined">add_circle</span>
              Thêm lịch hẹn
            </button>
          </div>

          <div class="slots">
            <div v-for="(slot, idx) in slots" :key="idx" class="slot-card">
              <!-- Slot header -->
              <div class="slot-card__head">
                <span class="slot-card__num">Lịch hẹn #{{ idx + 1 }}</span>
                <button
                  v-if="slots.length > 1 && !alreadySent"
                  class="slot-card__remove"
                  @click="removeSlot(idx)"
                >
                  <span class="material-symbols-outlined">delete</span>
                </button>
              </div>

              <!-- Slot fields -->
              <div class="slot-card__body">
                <!-- Row 1: date + interviewer -->
                <div class="slot-row">
                  <div class="field slot-row__date">
                    <label class="field__label">Ngày phỏng vấn</label>
                    <input v-model="slot.date" type="date" :disabled="alreadySent" :class="['field__input', alreadySent && 'field__input--disabled', errors.slots[idx]?.date && 'field__input--error']" @change="clearSlotError(idx, 'date')" />
                  </div>
                  <div class="field slot-row__interviewer">
                    <label class="field__label">Người phỏng vấn</label>
                    <input v-model="slot.interviewerName" type="text" placeholder="Nhập tên người phỏng vấn..." :disabled="alreadySent" :class="['field__input', alreadySent && 'field__input--disabled', errors.slots[idx]?.interviewerName && 'field__input--error']" @input="clearSlotError(idx, 'interviewerName')" />
                  </div>
                </div>

                <!-- Row 2: time range + capacity -->
                <div class="slot-row">
                  <div class="field slot-row__time">
                    <label class="field__label">Giờ bắt đầu</label>
                    <input v-model="slot.startTime" type="time" :disabled="alreadySent" :class="['field__input', alreadySent && 'field__input--disabled', errors.slots[idx]?.startTime && 'field__input--error']" @change="clearSlotError(idx, 'startTime')" />
                  </div>
                  <div class="field slot-row__time">
                    <label class="field__label">Giờ kết thúc</label>
                    <input v-model="slot.endTime" type="time" :disabled="alreadySent" :class="['field__input', alreadySent && 'field__input--disabled', errors.slots[idx]?.endTime && 'field__input--error']" @change="clearSlotError(idx, 'endTime')" />
                  </div>
                  <div class="field slot-row__capacity">
                    <label class="field__label">Số UV tối đa</label>
                    <input v-model.number="slot.capacity" type="number" min="1" max="20" :disabled="alreadySent" :class="['field__input', alreadySent && 'field__input--disabled']" />
                  </div>
                </div>

                <!-- Row 3: type + link/location -->
                <div class="slot-row slot-row--type">
                  <div class="field">
                    <label class="field__label">Hình thức</label>
                    <div class="type-toggle">
                      <button
                        :class="['type-toggle__btn', slot.type === INTERVIEW_TYPE.ONLINE && 'type-toggle__btn--active']"
                        :disabled="alreadySent"
                        @click="slot.type = INTERVIEW_TYPE.ONLINE"
                      >Online</button>
                      <button
                        :class="['type-toggle__btn', slot.type === INTERVIEW_TYPE.ONSITE && 'type-toggle__btn--active']"
                        :disabled="alreadySent"
                        @click="slot.type = INTERVIEW_TYPE.ONSITE"
                      >Trực tiếp</button>
                    </div>
                  </div>
                  <div class="field slot-row__link">
                    <label class="field__label">
                      {{ slot.type === INTERVIEW_TYPE.ONLINE ? 'Link họp (Meet, Zoom...)' : 'Địa điểm' }}
                    </label>
                    <input
                      v-model="slot.link"
                      type="text"
                      :placeholder="slot.type === INTERVIEW_TYPE.ONLINE ? 'meet.google.com/...' : 'Tầng 5, 123 Nguyễn Huệ...'"
                      :disabled="alreadySent"
                      :class="['field__input', alreadySent && 'field__input--disabled']"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="info-tip">
            <span class="material-symbols-outlined info-tip__icon">info</span>
            <p class="info-tip__text">Cung cấp nhiều khung giờ khác nhau để tăng khả năng ứng viên sắp xếp được.</p>
          </div>
        </div>

      </div>

      <!-- Sidebar -->
      <div class="propose-page__sidebar">
        <div class="sticky-sidebar">

          <!-- Confirm card -->
          <div class="confirm-card">
            <h3 class="confirm-card__title">
              {{ alreadySent ? 'Đề xuất đã gửi' : 'Xác nhận gửi đề xuất' }}
            </h3>

            <!-- Banner đã gửi -->
            <div v-if="alreadySent" class="sent-badge">
              <span class="material-symbols-outlined">check_circle</span>
              Khung giờ đã được gửi tới ứng viên
            </div>

            <div class="confirm-card__summary">
              <div class="confirm-card__row">
                <span class="confirm-card__label">Lịch hẹn</span>
                <span class="confirm-card__value confirm-card__value--highlight">{{ slots.length }} khung giờ</span>
              </div>
              <div class="confirm-card__row">
                <span class="confirm-card__label">Người nhận</span>
                <span class="confirm-card__value">{{ recipientSummary }}</span>
              </div>
              <div class="confirm-card__row">
                <span class="confirm-card__label">Vòng PV</span>
                <span class="confirm-card__value">{{ roundName }}</span>
              </div>
            </div>
            <div class="confirm-card__actions">
              <button v-if="!alreadySent" class="btn-submit" :disabled="loading" @click="handleSubmit">
                <span v-if="loading" class="material-symbols-outlined btn-submit__spin">progress_activity</span>
                {{ loading ? 'Đang gửi...' : 'Gửi đề xuất ngay' }}
              </button>
              <button class="btn-cancel" @click="router.back()">
                {{ alreadySent ? 'Quay lại' : 'Hủy bỏ' }}
              </button>
            </div>
          </div>

          <!-- Flow guide -->
          <div class="flow-card">
            <h4 class="flow-card__title">
              <span class="material-symbols-outlined flow-card__icon">lightbulb</span>
              Luồng thực hiện
            </h4>
            <ol class="flow-card__list">
              <li v-for="(step, i) in [
                'NTD gửi đề xuất khung giờ',
                'Ứng viên nhận email & link chọn lịch',
                'Ứng viên chốt lịch phỏng vấn',
                'Lịch hiển thị tại Bảng quản lý',
              ]" :key="i" class="flow-card__item">
                <div class="flow-card__num">{{ i + 1 }}</div>
                <span class="flow-card__text">{{ step }}</span>
              </li>
            </ol>
          </div>

        </div>
      </div>
    </div>

  </div>
</template>

<style scoped>
/* ============================================
   Font Size Scale (skill-font.md):
   1.875rem (30px) = Page title
   1.125rem (18px) = Card title / Item chính
   1rem     (16px) = Body, input, button cấp 1
   0.875rem (14px) = Data cells, metadata
   0.75rem  (12px) = th, label nhỏ (bold+uppercase)
   ============================================ */

.propose-page {
  width: 100%;
  font-family: 'Manrope', sans-serif;
}

/* ── Top ── */
.propose-page__top {
  margin-bottom: 2rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.propose-page__title {
  font-size: 1.875rem;
  font-weight: 800;
  letter-spacing: -0.03em;
  color: #0f172a;
  margin: 0.25rem 0 0;
}

.propose-page__subtitle {
  font-size: 0.875rem;
  color: #64748b;
  margin: 0;
}

/* ── Grid ── */
.propose-page__grid {
  display: grid;
  grid-template-columns: 1fr 20rem;
  gap: 2rem;
  align-items: start;
}

@media (max-width: 900px) {
  .propose-page__grid { grid-template-columns: 1fr; }
}

.propose-page__main {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

/* ── Card ── */
.card {
  background: #fff;
  border: 1px solid #f1f5f9;
  border-radius: 1rem;
  box-shadow: 0 1px 2px rgba(0,0,0,.04);
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.card__header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.card__header--between {
  justify-content: space-between;
}

.card__header-left {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.step-badge {
  width: 1.75rem;
  height: 1.75rem;
  border-radius: 50%;
  background: rgba(75,154,246,0.1);
  color: #4b9af6;
  font-size: 0.75rem;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.card__title {
  font-size: 1.125rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
}

/* ── Form grid ── */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

/* ── Field ── */
.field {
  display: flex;
  flex-direction: column;
  gap: 0.375rem;
}

.field__label {
  font-size: 0.75rem;
  font-weight: 800;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.07em;
}

.field__input,
.field__select {
  height: 2.5rem;
  padding: 0 0.875rem;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 0.625rem;
  font-size: 1rem;
  font-family: inherit;
  color: #0f172a;
  outline: none;
  transition: border-color 0.15s, box-shadow 0.15s;
  width: 100%;
  box-sizing: border-box;
  appearance: none;
}

.field__input:focus,
.field__select:focus {
  border-color: #4b9af6;
  box-shadow: 0 0 0 3px rgba(75,154,246,0.1);
  background: #fff;
}

.field__input--disabled {
  background: #f1f5f9;
  color: #64748b;
  cursor: not-allowed;
  border-color: #e2e8f0;
}

.field__input--error,
.field__select--error {
  border-color: #ef4444 !important;
  background: #fff5f5;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}

/* ── Mode toggle (recipient) ── */
.mode-toggle {
  display: flex;
  gap: 0.75rem;
}

.mode-toggle__btn {
  flex: 1;
  height: 3rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  border: 1.5px solid #e2e8f0;
  border-radius: 0.75rem;
  background: #f8fafc;
  font-size: 0.875rem;
  font-weight: 700;
  font-family: inherit;
  color: #64748b;
  cursor: pointer;
  transition: all 0.15s;
}

.mode-toggle__btn .material-symbols-outlined {
  font-size: 1.1rem !important;
}

.mode-toggle__btn--active {
  border-color: #4b9af6;
  background: rgba(75,154,246,0.06);
  color: #4b9af6;
}

.mode-toggle__btn:hover:not(.mode-toggle__btn--active) {
  background: #f1f5f9;
}

/* ── Exclusion list ── */
.exclusion-list {
  display: flex;
  flex-direction: column;
  gap: 0.625rem;
}

.exclusion-row {
  display: flex;
  align-items: flex-end;
  gap: 0.5rem;
}

.exclusion-row__field {
  flex: 1;
}

.exclusion-row__remove {
  flex-shrink: 0;
  width: 2.5rem;
  height: 2.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.625rem;
  background: #fff;
  color: #94a3b8;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.15s, background 0.15s, border-color 0.15s;
}

.exclusion-row__remove:hover {
  color: #ef4444;
  background: rgba(239,68,68,0.06);
  border-color: rgba(239,68,68,0.3);
}

.exclusion-row__remove .material-symbols-outlined {
  font-size: 1rem !important;
}

.btn-add-row {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  background: none;
  border: 1.5px dashed #cbd5e1;
  border-radius: 0.625rem;
  padding: 0.5rem 0.875rem;
  font-size: 0.875rem;
  font-weight: 700;
  font-family: inherit;
  color: #64748b;
  cursor: pointer;
  transition: border-color 0.15s, color 0.15s;
  align-self: flex-start;
}

.btn-add-row:hover {
  border-color: #4b9af6;
  color: #4b9af6;
}

.btn-add-row .material-symbols-outlined {
  font-size: 1rem !important;
}

/* ── Recipient summary ── */
.recipient-notice {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  font-size: 0.75rem;
  font-weight: 600;
  color: #64748b;
  margin: 0;
}

.recipient-notice__icon {
  font-size: 0.875rem !important;
  color: #94a3b8;
  flex-shrink: 0;
}

.recipient-summary {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: rgba(75,154,246,0.06);
  border: 1px solid rgba(75,154,246,0.2);
  border-radius: 0.625rem;
  padding: 0.625rem 0.875rem;
  font-size: 0.875rem;
  color: #334155;
}

.recipient-summary__icon {
  color: #4b9af6;
  font-size: 1rem !important;
  flex-shrink: 0;
}

/* ── Slot builder ── */
.btn-add-slot {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  background: none;
  border: none;
  font-size: 0.875rem;
  font-weight: 700;
  font-family: inherit;
  color: #4b9af6;
  cursor: pointer;
  padding: 0;
  transition: opacity 0.15s;
}

.btn-add-slot:hover { opacity: 0.75; }

.btn-add-slot .material-symbols-outlined {
  font-size: 1.1rem !important;
}

.slots {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

/* ── Slot card ── */
.slot-card {
  border: 1px solid #e2e8f0;
  border-radius: 0.875rem;
  overflow: hidden;
  transition: border-color 0.15s;
}

.slot-card:hover {
  border-color: rgba(75,154,246,0.35);
}

.slot-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.625rem 1rem;
  background: #f8fafc;
  border-bottom: 1px solid #f1f5f9;
}

.slot-card__num {
  font-size: 0.75rem;
  font-weight: 800;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.slot-card__remove {
  width: 1.75rem;
  height: 1.75rem;
  border: none;
  background: none;
  border-radius: 0.375rem;
  color: #cbd5e1;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.15s, background 0.15s;
}

.slot-card__remove:hover {
  color: #ef4444;
  background: rgba(239,68,68,0.08);
}

.slot-card__remove .material-symbols-outlined {
  font-size: 1rem !important;
}

.slot-card__body {
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.875rem;
}

/* ── Slot rows ── */
.slot-row {
  display: flex;
  gap: 0.75rem;
  align-items: flex-end;
}

.slot-row__date        { flex: 2; }
.slot-row__interviewer { flex: 2; }
.slot-row__time        { flex: 1; min-width: 7rem; }
.slot-row__capacity    { width: 7rem; flex-shrink: 0; }
.slot-row__link        { flex: 1; }

.slot-row--type .field:first-child {
  flex-shrink: 0;
  width: 13rem;
}

/* ── Type toggle ── */
.type-toggle {
  display: flex;
  gap: 0.375rem;
}

.type-toggle__btn {
  flex: 1;
  height: 2.5rem;
  border-radius: 0.5rem;
  border: 1.5px solid #e2e8f0;
  background: #f8fafc;
  font-size: 0.875rem;
  font-weight: 700;
  font-family: inherit;
  color: #64748b;
  cursor: pointer;
  transition: all 0.15s;
}

.type-toggle__btn--active {
  border-color: #4b9af6;
  background: rgba(75,154,246,0.06);
  color: #4b9af6;
}

.type-toggle__btn:hover:not(.type-toggle__btn--active) {
  background: #f1f5f9;
}

/* ── Info tip ── */
.info-tip {
  display: flex;
  gap: 0.625rem;
  align-items: flex-start;
  background: #fffbeb;
  border: 1px solid #fde68a;
  border-radius: 0.75rem;
  padding: 0.75rem 1rem;
}

.info-tip__icon {
  color: #f59e0b;
  font-size: 1.1rem !important;
  flex-shrink: 0;
  margin-top: 0.05rem;
}

.info-tip__text {
  font-size: 0.875rem;
  color: #92400e;
  margin: 0;
  line-height: 1.5;
}

/* ── Sidebar ── */
.propose-page__sidebar { min-width: 0; }

.sticky-sidebar {
  position: sticky;
  top: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

/* ── Confirm card ── */
.confirm-card {
  background: #0f172a;
  border-radius: 1rem;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  overflow: hidden;
  position: relative;
}

.confirm-card::after {
  content: '';
  position: absolute;
  right: -2rem;
  bottom: -2rem;
  width: 8rem;
  height: 8rem;
  background: rgba(75,154,246,0.2);
  border-radius: 50%;
  filter: blur(2rem);
  pointer-events: none;
}

.sent-badge {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: rgba(34, 197, 94, 0.15);
  border: 1px solid rgba(34, 197, 94, 0.3);
  border-radius: 0.625rem;
  padding: 0.5rem 0.875rem;
  font-size: 0.875rem;
  font-weight: 700;
  color: #86efac;
}

.sent-badge .material-symbols-outlined {
  font-size: 1.1rem !important;
  color: #4ade80;
}

.confirm-card__title {
  font-size: 1.125rem;
  font-weight: 700;
  color: #fff;
  margin: 0;
}

.confirm-card__summary {
  border-top: 1px solid rgba(255,255,255,0.08);
  border-bottom: 1px solid rgba(255,255,255,0.08);
  padding: 1rem 0;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.confirm-card__row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 0.5rem;
  font-size: 0.875rem;
}

.confirm-card__label { color: rgba(255,255,255,0.45); flex-shrink: 0; }

.confirm-card__value {
  font-weight: 700;
  color: #fff;
  text-align: right;
}

.confirm-card__value--highlight {
  color: #60a5fa;
  text-decoration: underline;
}

.confirm-card__actions {
  display: flex;
  flex-direction: column;
  gap: 0.625rem;
  position: relative;
  z-index: 1;
}

.btn-submit {
  width: 100%;
  height: 3rem;
  background: linear-gradient(135deg, #4b9af6 0%, #3b82f6 100%);
  border: none;
  border-radius: 0.75rem;
  font-size: 1rem;
  font-weight: 800;
  font-family: inherit;
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  box-shadow: 0 4px 12px rgba(75,154,246,0.4);
  transition: opacity 0.15s, transform 0.1s;
}

.btn-submit:hover:not(:disabled) {
  opacity: 0.92;
  transform: translateY(-1px);
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-submit__spin {
  animation: spin 0.8s linear infinite;
  font-size: 1rem !important;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to   { transform: rotate(360deg); }
}

.btn-cancel {
  width: 100%;
  height: 2.5rem;
  background: rgba(255,255,255,0.06);
  border: none;
  border-radius: 0.75rem;
  font-size: 0.875rem;
  font-weight: 700;
  font-family: inherit;
  color: rgba(255,255,255,0.6);
  cursor: pointer;
  transition: color 0.15s, background 0.15s;
}

.btn-cancel:hover {
  color: #fff;
  background: rgba(255,255,255,0.1);
}

/* ── Flow card ── */
.flow-card {
  background: #fff;
  border: 1px solid #f1f5f9;
  border-radius: 1rem;
  padding: 1.25rem;
}

.flow-card__title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 1rem;
}

.flow-card__icon {
  color: #4b9af6;
  font-size: 1.1rem !important;
}

.flow-card__list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.875rem;
}

.flow-card__item {
  display: flex;
  align-items: flex-start;
  gap: 0.625rem;
}

.flow-card__num {
  width: 1.25rem;
  height: 1.25rem;
  border-radius: 50%;
  background: #f1f5f9;
  font-size: 0.75rem;
  font-weight: 800;
  color: #94a3b8;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-top: 0.1rem;
}

.flow-card__text {
  font-size: 0.75rem;
  font-weight: 700;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  line-height: 1.4;
}
</style>
