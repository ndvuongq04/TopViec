<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div v-if="open" class="modal-backdrop" @mousedown.self="handleClose">
        <Transition name="modal-slide">
          <div v-if="open" class="modal" role="dialog" aria-modal="true">

            <!-- Header -->
            <div class="modal__header">
              <div class="modal__header-left">
                <span class="modal__icon material-symbols-outlined">calendar_add_on</span>
                <div>
                  <p class="modal__label">{{ roundName || 'Vòng phỏng vấn' }}</p>
                  <h3 class="modal__title">Tạo batch slots mới</h3>
                </div>
              </div>
              <button class="modal__close" @click="handleClose">
                <span class="material-symbols-outlined">close</span>
              </button>
            </div>

            <!-- Error banner -->
            <div v-if="fetchError" class="error-banner">
              <span class="material-symbols-outlined">error</span>
              {{ fetchError }}
            </div>

            <!-- Body -->
            <div class="modal__body">
              <div class="modal__grid">

                <!-- ── LEFT: form ── -->
                <div class="form-col">

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
                        <div class="deadline-row">
                          <div :class="['date-field', errors.deadline && 'date-field--error']" @click="openDatePicker">
                            <span class="material-symbols-outlined date-field__icon">calendar_today</span>
                            <span class="date-field__display">{{ formatDMY(deadlineDate) || 'dd/mm/yyyy' }}</span>
                            <input
                              v-model="deadlineDate"
                              type="date"
                              class="date-field__native"
                              @change="clearError('deadline')"
                            />
                          </div>
                          <input
                            v-model="deadlineTime"
                            type="time"
                            :class="['field__input deadline-row__time', errors.deadline && 'field__input--error']"
                            @change="clearError('deadline')"
                          />
                        </div>
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
                    <div class="mode-toggle">
                      <button
                        :class="['mode-toggle__btn', recipientMode === 'all' && 'mode-toggle__btn--active']"
                        @click="recipientMode = 'all'"
                      >
                        <span class="material-symbols-outlined">groups</span>
                        Toàn bộ ứng viên
                      </button>
                      <button
                        :class="['mode-toggle__btn', recipientMode === 'exclude' && 'mode-toggle__btn--active']"
                        @click="recipientMode = 'exclude'"
                      >
                        <span class="material-symbols-outlined">person_remove</span>
                        Trừ một số ứng viên
                      </button>
                    </div>
                    <template v-if="recipientMode === 'exclude'">
                      <div class="exclusion-list">
                        <div v-for="(_, idx) in excludedIds" :key="idx" class="exclusion-row">
                          <div class="field exclusion-row__field">
                            <label class="field__label">Không gửi cho</label>
                            <SearchableSelect
                              :model-value="excludedIds[idx]"
                              :options="exclusionOptionsFor(idx)"
                              placeholder="Tìm và chọn ứng viên..."
                              @update:model-value="excludedIds[idx] = $event as number"
                            />
                          </div>
                          <button
                            v-if="excludedIds.length > 1"
                            class="exclusion-row__remove"
                            @click="removeExclusion(idx)"
                          >
                            <span class="material-symbols-outlined">close</span>
                          </button>
                        </div>
                      </div>
                      <button class="btn-add-row" @click="addExclusion">
                        <span class="material-symbols-outlined">add</span>
                        Thêm ứng viên cần loại trừ
                      </button>
                    </template>
                    <div class="recipient-summary">
                      <span class="material-symbols-outlined recipient-summary__icon">send</span>
                      <span>Sẽ gửi tới: <strong>{{ recipientSummary }}</strong></span>
                    </div>
                  </div>

                  <!-- ③ Lịch hẹn -->
                  <div class="card">
                    <div class="card__header card__header--between">
                      <div class="card__header-left">
                        <div class="step-badge">3</div>
                        <h3 class="card__title">Các lịch hẹn đề xuất</h3>
                      </div>
                      <button class="btn-add-slot" @click="addSlot">
                        <span class="material-symbols-outlined">add_circle</span>
                        Thêm lịch hẹn
                      </button>
                    </div>
                    <div class="slots">
                      <div v-for="(slot, idx) in slots" :key="idx" class="slot-card">
                        <div class="slot-card__head">
                          <span class="slot-card__num">Lịch hẹn #{{ idx + 1 }}</span>
                          <button v-if="slots.length > 1" class="slot-card__remove" @click="removeSlot(idx)">
                            <span class="material-symbols-outlined">delete</span>
                          </button>
                        </div>
                        <div class="slot-card__body">
                          <div class="slot-row">
                            <div class="field slot-row__date">
                              <label class="field__label">Ngày phỏng vấn</label>
                              <div :class="['date-field', errors.slots[idx]?.date && 'date-field--error']" @click="openDatePicker">
                                <span class="material-symbols-outlined date-field__icon">calendar_today</span>
                                <span class="date-field__display">{{ formatDMY(slot.date) || 'dd/mm/yyyy' }}</span>
                                <input
                                  v-model="slot.date"
                                  type="date"
                                  class="date-field__native"
                                  @change="clearSlotError(idx, 'date')"
                                />
                              </div>
                            </div>
                            <div class="field slot-row__interviewer">
                              <label class="field__label">Người phỏng vấn</label>
                              <input v-model="slot.interviewerName" type="text" placeholder="Nhập tên..." :class="['field__input', errors.slots[idx]?.interviewerName && 'field__input--error']" @input="clearSlotError(idx, 'interviewerName')" />
                            </div>
                          </div>
                          <div class="slot-row">
                            <div class="field slot-row__time">
                              <label class="field__label">Giờ bắt đầu</label>
                              <input v-model="slot.startTime" type="time" :class="['field__input', errors.slots[idx]?.startTime && 'field__input--error']" @change="clearSlotError(idx, 'startTime')" />
                            </div>
                            <div class="field slot-row__time">
                              <label class="field__label">Giờ kết thúc</label>
                              <input v-model="slot.endTime" type="time" :class="['field__input', errors.slots[idx]?.endTime && 'field__input--error']" @change="clearSlotError(idx, 'endTime')" />
                            </div>
                            <div class="field slot-row__capacity">
                              <label class="field__label">Số UV tối đa</label>
                              <input
  v-model.number="slot.capacity"
  type="number"
  min="1"
  class="field__input"
  @blur="slot.capacity = slot.capacity >= 1 ? slot.capacity : 1"
/>
                            </div>
                          </div>
                          <div class="slot-row slot-row--type">
                            <div class="field">
                              <label class="field__label">Hình thức</label>
                              <div class="type-toggle">
                                <button :class="['type-toggle__btn', slot.type === INTERVIEW_TYPE.ONLINE && 'type-toggle__btn--active']" @click="slot.type = INTERVIEW_TYPE.ONLINE">Online</button>
                                <button :class="['type-toggle__btn', slot.type === INTERVIEW_TYPE.ONSITE && 'type-toggle__btn--active']" @click="slot.type = INTERVIEW_TYPE.ONSITE">Trực tiếp</button>
                              </div>
                            </div>
                            <div class="field slot-row__link">
                              <label class="field__label">{{ slot.type === INTERVIEW_TYPE.ONLINE ? 'Link họp' : 'Địa điểm' }}</label>
                              <input v-model="slot.link" type="text" :placeholder="slot.type === INTERVIEW_TYPE.ONLINE ? 'meet.google.com/...' : 'Tầng 5, 123 Nguyễn Huệ...'" class="field__input" />
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

                <!-- ── RIGHT: sidebar ── -->
                <div class="sidebar-col">
                  <div class="confirm-card">
                    <h3 class="confirm-card__title">Xác nhận gửi đề xuất</h3>
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
                        <span class="confirm-card__label">Tổng sức chứa</span>
                        <span
                          class="confirm-card__value"
                          :class="recipientCount > totalCapacity ? 'confirm-card__value--danger' : 'confirm-card__value--ok'"
                        >{{ totalCapacity }} chỗ</span>
                      </div>
                      <div v-if="recipientCount > totalCapacity" class="capacity-warning">
                        <span class="material-symbols-outlined capacity-warning__icon">warning</span>
                        <span>Số UV ({{ recipientCount }}) vượt quá tổng sức chứa ({{ totalCapacity }}). Hãy thêm slot hoặc tăng số chỗ.</span>
                      </div>
                      <div class="confirm-card__row">
                        <span class="confirm-card__label">Vòng PV</span>
                        <span class="confirm-card__value">{{ roundName }}</span>
                      </div>
                    </div>
                    <div class="confirm-card__actions">
                      <button class="btn-submit" :disabled="loading" @click="handleSubmit">
                        <span v-if="loading" class="material-symbols-outlined btn-submit__spin">progress_activity</span>
                        {{ loading ? 'Đang gửi...' : 'Gửi đề xuất ngay' }}
                      </button>
                      <button class="btn-cancel" @click="handleClose">Hủy bỏ</button>
                    </div>
                  </div>

                  <div class="flow-card">
                    <h4 class="flow-card__title">
                      <span class="material-symbols-outlined flow-card__icon">lightbulb</span>
                      Luồng thực hiện
                    </h4>
                    <ol class="flow-card__list">
                      <li v-for="(step, i) in FLOW_STEPS" :key="i" class="flow-card__item">
                        <div class="flow-card__num">{{ i + 1 }}</div>
                        <span class="flow-card__text">{{ step }}</span>
                      </li>
                    </ol>
                  </div>
                </div>

              </div>
            </div>

          </div>
        </Transition>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useToast } from '@/composables/useToast'
import { INTERVIEW_TYPE } from '@/constants/interview.constants'
import type { InterviewType } from '@/constants/interview.constants'
import type { ReqCreateInterviewSlotsDTO, SlotDTO, ResInterviewScheduleDTO } from '@/types/interview.types'
import SearchableSelect from '@/components/ui/SearchableSelect.vue'
import type { SelectOption } from '@/components/ui/SearchableSelect.vue'
import employerInterviewService from '@/services/employerInterview.service'
import { employerJobPostingService } from '@/services/jobPosting.service'

const props = defineProps<{
  open:      boolean
  jobId:     number | null
  roundId:   number | null
  roundName: string
}>()

const emit = defineEmits<{
  close:   []
  created: []
}>()

const toast = useToast()

const FLOW_STEPS = [
  'NTD gửi đề xuất khung giờ',
  'Ứng viên nhận email & link chọn lịch',
  'Ứng viên chốt lịch phỏng vấn',
  'Lịch hiển thị tại Bảng quản lý',
]

// ── Remote data ───────────────────────────────────────────
const jobTitle   = ref('Đang tải...')
const candidates = ref<ResInterviewScheduleDTO[]>([])
const fetchError = ref('')

async function loadData() {
  if (!props.jobId || !props.roundId) {
    fetchError.value = 'Thiếu thông tin vòng phỏng vấn.'
    return
  }
  fetchError.value = ''
  try {
    const [job, pending] = await Promise.all([
      employerJobPostingService.getById(props.jobId),
      employerInterviewService.getPendingCandidates(props.roundId),
    ])
    jobTitle.value   = job.title
    candidates.value = pending
  } catch (err: any) {
    fetchError.value = err?.response?.data?.message ?? 'Không thể tải dữ liệu.'
    toast.error('Lỗi', fetchError.value)
  }
}

watch(() => props.open, (val) => {
  if (val) { resetForm(); loadData() }
})

// ── Recipient mode ────────────────────────────────────────
type RecipientMode = 'all' | 'exclude'
const recipientMode = ref<RecipientMode>('all')
const excludedIds   = ref<(number | null)[]>([null])

function addExclusion()               { excludedIds.value.push(null) }
function removeExclusion(idx: number) { excludedIds.value.splice(idx, 1) }

const candidateOptions = computed<SelectOption[]>(() =>
  candidates.value.map((c: ResInterviewScheduleDTO) => ({ id: c.applicationId, name: `${c.candidateName} (${c.candidateEmail})` }))
)

function exclusionOptionsFor(idx: number): SelectOption[] {
  const others = excludedIds.value.filter((id, i) => i !== idx && id !== null)
  return candidateOptions.value.filter(o => !others.includes(o.id as number))
}

const recipientSummary = computed(() => {
  const total    = candidates.value.length
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

const deadlineDate = ref('')
const deadlineTime = ref('')
const slots    = ref<Slot[]>([
  { date: '', interviewerName: '', startTime: '09:00', endTime: '10:00', type: INTERVIEW_TYPE.ONLINE, link: '', capacity: 1 },
])

const totalCapacity  = computed(() => slots.value.reduce((sum, s) => sum + (s.capacity || 1), 0))
const recipientCount = computed(() => {
  const excluded = excludedIds.value.filter((id): id is number => id !== null)
  return recipientMode.value === 'all'
    ? candidates.value.length
    : candidates.value.filter((c: ResInterviewScheduleDTO) => !excluded.includes(c.applicationId)).length
})

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

function openDatePicker(e: MouseEvent) {
  const input = (e.currentTarget as HTMLElement).querySelector<HTMLInputElement>('input[type="date"]')
  input?.showPicker?.()
}

/** YYYY-MM-DD → dd/mm/yyyy */
function formatDMY(iso: string): string {
  if (!iso) return ''
  const [y, m, d] = iso.split('-')
  return `${d}/${m}/${y}`
}



// ── Validation ────────────────────────────────────────────
interface SlotErrors { date: boolean; interviewerName: boolean; startTime: boolean; endTime: boolean }

const errors = ref({ deadline: false, slots: [] as SlotErrors[] })

function resetSlotErrors() {
  errors.value.slots = slots.value.map(() => ({ date: false, interviewerName: false, startTime: false, endTime: false }))
}

function clearError(field: 'deadline') { errors.value[field] = false }

function clearSlotError(idx: number, field: keyof SlotErrors) {
  if (errors.value.slots[idx]) errors.value.slots[idx][field] = false
}

function resetForm() {
  deadlineDate.value  = ''
  deadlineTime.value  = ''
  recipientMode.value = 'all'
  excludedIds.value   = [null]
  slots.value         = [
    { date: '', interviewerName: '', startTime: '09:00', endTime: '10:00', type: INTERVIEW_TYPE.ONLINE, link: '', capacity: 1 },
  ]
  errors.value = { deadline: false, slots: [] }
}

// ── Submit ────────────────────────────────────────────────
const loading = ref(false)

async function handleSubmit() {
  if (!props.roundId) { toast.error('Lỗi', 'Thiếu thông tin vòng phỏng vấn.'); return }

  errors.value.deadline = false
  resetSlotErrors()
  let hasError = false

  if (!deadlineDate.value || !deadlineTime.value) { errors.value.deadline = true; hasError = true }

  for (let i = 0; i < slots.value.length; i++) {
    const s = slots.value[i]
    if (!s.date)                   { errors.value.slots[i].date            = true; hasError = true }
    if (!s.interviewerName.trim()) { errors.value.slots[i].interviewerName = true; hasError = true }
    if (!s.startTime)              { errors.value.slots[i].startTime       = true; hasError = true }
    if (!s.endTime)                { errors.value.slots[i].endTime         = true; hasError = true }
  }

  if (hasError) { toast.error('Thiếu thông tin', 'Vui lòng điền đầy đủ các trường được đánh dấu đỏ.'); return }

  // Hạn chót phải trước ngày phỏng vấn sớm nhất
  if (deadlineDate.value && deadlineTime.value) {
    const deadlineMs = new Date(`${deadlineDate.value}T${deadlineTime.value}:00`).getTime()
    const violatedSlot = slots.value.find(s => s.date && s.startTime && new Date(`${s.date}T${s.startTime}:00`).getTime() <= deadlineMs)
    if (violatedSlot) {
      errors.value.deadline = true
      toast.error('Hạn chót không hợp lệ', 'Hạn chót phải trước giờ bắt đầu của tất cả các lịch hẹn.')
      return
    }
  }

  const confirmedExcluded = excludedIds.value.filter((id): id is number => id !== null)
  const recipientIds = recipientMode.value === 'all'
    ? candidates.value.map((c: ResInterviewScheduleDTO) => c.applicationId)
    : candidates.value.filter((c: ResInterviewScheduleDTO) => !confirmedExcluded.includes(c.applicationId)).map((c: ResInterviewScheduleDTO) => c.applicationId)

  if (recipientIds.length === 0) { toast.error('Không có người nhận', 'Danh sách ứng viên trống.'); return }

  const totalCap = slots.value.reduce((sum, s) => sum + (s.capacity || 1), 0)
  if (recipientIds.length > totalCap) {
    toast.error('Không đủ sức chứa', `Số UV nhận (${recipientIds.length}) vượt quá tổng sức chứa của các slot (${totalCap}). Hãy thêm slot hoặc tăng số chỗ.`)
    return
  }

  const payload: ReqCreateInterviewSlotsDTO = {
    applicationIds: recipientIds,
    deadline:       `${deadlineDate.value}T${deadlineTime.value}:00`,
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
    await employerInterviewService.createSlots(props.roundId, payload)
    toast.success('Đã gửi đề xuất', `Gửi ${slots.value.length} khung giờ tới ${recipientSummary.value}.`)
    emit('created')
    emit('close')
  } catch (err: any) {
    const msg: string = err?.response?.data?.message ?? ''
    if (msg.toLowerCase().includes('đã có slot')) {
      toast.error('Đã tồn tại lịch hẹn', 'Vòng phỏng vấn này đã được gửi đề xuất trước đó.')
    } else {
      toast.error('Lỗi', msg || 'Không thể gửi đề xuất. Vui lòng thử lại.')
    }
  } finally {
    loading.value = false
  }
}

function handleClose() {
  if (loading.value) return
  emit('close')
}
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgb(15 23 42 / 0.5);
  backdrop-filter: blur(3px);
  z-index: 9999;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 2rem 1rem;
  overflow-y: auto;
}

.modal {
  background: #f8fafc;
  border-radius: 1.25rem;
  width: 100%;
  max-width: 62rem;
  box-shadow: 0 32px 80px rgb(15 23 42 / 0.22), 0 4px 20px rgb(15 23 42 / 0.08);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  font-family: 'Manrope', sans-serif;
}

/* ── Header ── */
.modal__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.25rem 1.75rem;
  background: #fff;
  border-bottom: 1px solid #f1f5f9;
}

.modal__header-left { display: flex; align-items: center; gap: 0.875rem; }

.modal__icon {
  font-size: 1.5rem;
  color: #4b9af6;
  background: #eff6ff;
  padding: 0.5rem;
  border-radius: 0.75rem;
}

.modal__label { font-size: 0.75rem; font-weight: 600; color: #94a3b8; text-transform: uppercase; letter-spacing: 0.05em; margin: 0; }
.modal__title { font-size: 1.125rem; font-weight: 800; color: #0f172a; margin: 0.1rem 0 0; }

.modal__close {
  width: 2.25rem; height: 2.25rem;
  display: flex; align-items: center; justify-content: center;
  border: none; background: #f1f5f9; border-radius: 9999px;
  cursor: pointer; color: #64748b; transition: background 0.15s, color 0.15s;
}
.modal__close:hover { background: #e2e8f0; color: #0f172a; }
.modal__close .material-symbols-outlined { font-size: 1.125rem; }

/* ── Error banner ── */
.error-banner {
  display: flex; align-items: center; gap: 0.5rem;
  background: #fee2e2; color: #b91c1c;
  font-size: 0.875rem; font-weight: 600;
  padding: 0.75rem 1.75rem;
  border-bottom: 1px solid #fecaca;
}

/* ── Body ── */
.modal__body { padding: 1.75rem; overflow-y: auto; }

.modal__grid {
  display: grid;
  grid-template-columns: 1fr 19rem;
  gap: 1.75rem;
  align-items: start;
}

@media (max-width: 860px) { .modal__grid { grid-template-columns: 1fr; } }

.form-col    { display: flex; flex-direction: column; gap: 1.25rem; min-width: 0; }
.sidebar-col { display: flex; flex-direction: column; gap: 1.25rem; position: sticky; top: 0; }

/* ── Card ── */
.card {
  background: #fff; border: 1px solid #f1f5f9;
  border-radius: 1rem; box-shadow: 0 1px 2px rgba(0,0,0,.04);
  padding: 1.25rem; display: flex; flex-direction: column; gap: 1rem;
}

.card__header           { display: flex; align-items: center; gap: 0.75rem; }
.card__header--between  { justify-content: space-between; }
.card__header-left      { display: flex; align-items: center; gap: 0.75rem; }

.step-badge {
  width: 1.75rem; height: 1.75rem; border-radius: 50%;
  background: rgba(75,154,246,0.1); color: #4b9af6;
  font-size: 0.75rem; font-weight: 800;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}

.card__title { font-size: 1.125rem; font-weight: 700; color: #0f172a; margin: 0; }

/* ── Form grid ── */
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 0.875rem; }
.field--full { grid-column: 1 / -1; }

/* ── Field ── */
.field          { display: flex; flex-direction: column; gap: 0.375rem; }
.field__label   { font-size: 0.75rem; font-weight: 800; color: #94a3b8; text-transform: uppercase; letter-spacing: 0.07em; }
.field__input   {
  height: 2.5rem; padding: 0 0.875rem;
  background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 0.625rem;
  font-size: 1rem; font-family: inherit; color: #0f172a;
  outline: none; transition: border-color 0.15s, box-shadow 0.15s;
  width: 100%; box-sizing: border-box;
}
.field__input:focus         { border-color: #4b9af6; box-shadow: 0 0 0 3px rgba(75,154,246,0.1); background: #fff; }
.field__input--disabled     { background: #f1f5f9; color: #64748b; cursor: not-allowed; }
.field__input--error        { border-color: #ef4444 !important; background: #fff5f5; box-shadow: 0 0 0 3px rgba(239,68,68,0.1); }

/* ── Recipient ── */
.recipient-notice           { display: flex; align-items: center; gap: 0.375rem; font-size: 0.75rem; font-weight: 600; color: #64748b; margin: 0; }
.recipient-notice__icon     { font-size: 0.875rem !important; color: #94a3b8; }

.mode-toggle                { display: flex; gap: 0.625rem; }
.mode-toggle__btn           { flex: 1; height: 2.75rem; display: flex; align-items: center; justify-content: center; gap: 0.5rem; border: 1.5px solid #e2e8f0; border-radius: 0.75rem; background: #f8fafc; font-size: 0.875rem; font-weight: 700; font-family: inherit; color: #64748b; cursor: pointer; transition: all 0.15s; }
.mode-toggle__btn .material-symbols-outlined { font-size: 1.1rem !important; }
.mode-toggle__btn--active   { border-color: #4b9af6; background: rgba(75,154,246,0.06); color: #4b9af6; }
.mode-toggle__btn:hover:not(.mode-toggle__btn--active) { background: #f1f5f9; }

.exclusion-list             { display: flex; flex-direction: column; gap: 0.5rem; }
.exclusion-row              { display: flex; align-items: flex-end; gap: 0.5rem; }
.exclusion-row__field       { flex: 1; }
.exclusion-row__remove      { flex-shrink: 0; width: 2.5rem; height: 2.5rem; border: 1px solid #e2e8f0; border-radius: 0.625rem; background: #fff; color: #94a3b8; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: color 0.15s, background 0.15s, border-color 0.15s; }
.exclusion-row__remove:hover { color: #ef4444; background: rgba(239,68,68,0.06); border-color: rgba(239,68,68,0.3); }
.exclusion-row__remove .material-symbols-outlined { font-size: 1rem !important; }

.btn-add-row                { display: flex; align-items: center; gap: 0.375rem; background: none; border: 1.5px dashed #cbd5e1; border-radius: 0.625rem; padding: 0.5rem 0.875rem; font-size: 0.875rem; font-weight: 700; font-family: inherit; color: #64748b; cursor: pointer; transition: border-color 0.15s, color 0.15s; align-self: flex-start; }
.btn-add-row:hover          { border-color: #4b9af6; color: #4b9af6; }
.btn-add-row .material-symbols-outlined { font-size: 1rem !important; }

.recipient-summary          { display: flex; align-items: center; gap: 0.5rem; background: rgba(75,154,246,0.06); border: 1px solid rgba(75,154,246,0.2); border-radius: 0.625rem; padding: 0.625rem 0.875rem; font-size: 0.875rem; color: #334155; }
.recipient-summary__icon    { color: #4b9af6; font-size: 1rem !important; flex-shrink: 0; }

/* ── Slot builder ── */
.btn-add-slot               { display: flex; align-items: center; gap: 0.375rem; background: none; border: none; font-size: 0.875rem; font-weight: 700; font-family: inherit; color: #4b9af6; cursor: pointer; padding: 0; transition: opacity 0.15s; }
.btn-add-slot:hover         { opacity: 0.75; }
.btn-add-slot .material-symbols-outlined { font-size: 1.1rem !important; }

.slots                      { display: flex; flex-direction: column; gap: 0.875rem; }
.slot-card                  { border: 1px solid #e2e8f0; border-radius: 0.875rem; overflow: hidden; transition: border-color 0.15s; }
.slot-card:hover            { border-color: rgba(75,154,246,0.35); }
.slot-card__head            { display: flex; align-items: center; justify-content: space-between; padding: 0.5rem 0.875rem; background: #f8fafc; border-bottom: 1px solid #f1f5f9; }
.slot-card__num             { font-size: 0.75rem; font-weight: 800; color: #64748b; text-transform: uppercase; letter-spacing: 0.06em; }
.slot-card__remove          { width: 1.75rem; height: 1.75rem; border: none; background: none; border-radius: 0.375rem; color: #cbd5e1; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: color 0.15s, background 0.15s; }
.slot-card__remove:hover    { color: #ef4444; background: rgba(239,68,68,0.08); }
.slot-card__remove .material-symbols-outlined { font-size: 1rem !important; }
.slot-card__body            { padding: 0.875rem; display: flex; flex-direction: column; gap: 0.75rem; }

.slot-row                   { display: flex; gap: 0.625rem; align-items: flex-end; }
.deadline-row               { display: flex; gap: 0.5rem; }
.deadline-row__time         { flex: 0 0 8rem; }

/* ── Custom date field ── */
.date-field {
  position: relative;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  height: 2.5rem;
  padding: 0 0.75rem;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 0.625rem;
  cursor: pointer;
  transition: border-color 0.15s, box-shadow 0.15s;
}
.date-field:hover          { border-color: #94a3b8; }
.date-field:focus-within   { border-color: #4b9af6; box-shadow: 0 0 0 3px rgb(75 154 246 / 0.12); background: #fff; }
.date-field--error         { border-color: #f87171 !important; }
.date-field__icon          { font-size: 1rem !important; color: #94a3b8; flex-shrink: 0; pointer-events: none; }
.date-field__display       { font-size: 0.875rem; color: #0f172a; flex: 1; pointer-events: none; user-select: none; }
.date-field__display:empty::before,
.date-field .date-field__display[data-empty] { color: #94a3b8; }
.date-field__native {
  position: absolute;
  inset: 0;
  opacity: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}
.slot-row__date             { flex: 2; }
.slot-row__interviewer      { flex: 2; }
.slot-row__time             { flex: 1; min-width: 6rem; }
.slot-row__capacity         { width: 6rem; flex-shrink: 0; }
.slot-row__link             { flex: 1; }
.slot-row--type .field:first-child { flex-shrink: 0; width: 12rem; }

.type-toggle                { display: flex; gap: 0.375rem; }
.type-toggle__btn           { flex: 1; height: 2.5rem; border-radius: 0.5rem; border: 1.5px solid #e2e8f0; background: #f8fafc; font-size: 0.875rem; font-weight: 700; font-family: inherit; color: #64748b; cursor: pointer; transition: all 0.15s; }
.type-toggle__btn--active   { border-color: #4b9af6; background: rgba(75,154,246,0.06); color: #4b9af6; }
.type-toggle__btn:hover:not(.type-toggle__btn--active) { background: #f1f5f9; }

.info-tip                   { display: flex; gap: 0.625rem; align-items: flex-start; background: #fffbeb; border: 1px solid #fde68a; border-radius: 0.75rem; padding: 0.625rem 0.875rem; }
.info-tip__icon             { color: #f59e0b; font-size: 1.1rem !important; flex-shrink: 0; margin-top: 0.05rem; }
.info-tip__text             { font-size: 0.875rem; color: #92400e; margin: 0; line-height: 1.5; }

/* ── Confirm card ── */
.confirm-card               { background: #0f172a; border-radius: 1rem; padding: 1.25rem; display: flex; flex-direction: column; gap: 1.125rem; overflow: hidden; position: relative; }
.confirm-card::after        { content: ''; position: absolute; right: -2rem; bottom: -2rem; width: 8rem; height: 8rem; background: rgba(75,154,246,0.2); border-radius: 50%; filter: blur(2rem); pointer-events: none; }
.confirm-card__title        { font-size: 1.125rem; font-weight: 700; color: #fff; margin: 0; }
.confirm-card__summary      { border-top: 1px solid rgba(255,255,255,0.08); border-bottom: 1px solid rgba(255,255,255,0.08); padding: 0.875rem 0; display: flex; flex-direction: column; gap: 0.625rem; }
.confirm-card__row          { display: flex; justify-content: space-between; align-items: flex-start; gap: 0.5rem; font-size: 0.875rem; }
.confirm-card__label        { color: rgba(255,255,255,0.45); flex-shrink: 0; }
.confirm-card__value             { font-weight: 700; color: #fff; text-align: right; }
.confirm-card__value--highlight  { color: #60a5fa; text-decoration: underline; }
.confirm-card__value--ok         { color: #4ade80; }
.confirm-card__value--danger     { color: #f87171; }

.capacity-warning {
  display: flex;
  align-items: flex-start;
  gap: 0.375rem;
  background: rgb(239 68 68 / 0.15);
  border: 1px solid rgb(239 68 68 / 0.4);
  border-radius: 0.5rem;
  padding: 0.5rem 0.625rem;
  font-size: 0.75rem;
  color: #fca5a5;
  line-height: 1.4;
}
.capacity-warning__icon { font-size: 1rem !important; flex-shrink: 0; margin-top: 0.05rem; }
.confirm-card__actions      { display: flex; flex-direction: column; gap: 0.5rem; position: relative; z-index: 1; }

.btn-submit                 { width: 100%; height: 3rem; background: linear-gradient(135deg, #4b9af6 0%, #3b82f6 100%); border: none; border-radius: 0.75rem; font-size: 1rem; font-weight: 800; font-family: inherit; color: #fff; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 0.5rem; box-shadow: 0 4px 12px rgba(75,154,246,0.4); transition: opacity 0.15s, transform 0.1s; }
.btn-submit:hover:not(:disabled) { opacity: 0.92; transform: translateY(-1px); }
.btn-submit:disabled        { opacity: 0.5; cursor: not-allowed; }
.btn-submit__spin           { animation: spin 0.8s linear infinite; font-size: 1rem !important; }

@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }

.btn-cancel                 { width: 100%; height: 2.5rem; background: rgba(255,255,255,0.06); border: none; border-radius: 0.75rem; font-size: 0.875rem; font-weight: 700; font-family: inherit; color: rgba(255,255,255,0.6); cursor: pointer; transition: color 0.15s, background 0.15s; }
.btn-cancel:hover           { color: #fff; background: rgba(255,255,255,0.1); }

/* ── Flow card ── */
.flow-card                  { background: #fff; border: 1px solid #f1f5f9; border-radius: 1rem; padding: 1.125rem; }
.flow-card__title           { display: flex; align-items: center; gap: 0.5rem; font-size: 0.875rem; font-weight: 700; color: #0f172a; margin: 0 0 0.875rem; }
.flow-card__icon            { color: #4b9af6; font-size: 1.1rem !important; }
.flow-card__list            { list-style: none; padding: 0; margin: 0; display: flex; flex-direction: column; gap: 0.75rem; }
.flow-card__item            { display: flex; align-items: flex-start; gap: 0.625rem; }
.flow-card__num             { width: 1.25rem; height: 1.25rem; border-radius: 50%; background: #f1f5f9; font-size: 0.75rem; font-weight: 800; color: #94a3b8; display: flex; align-items: center; justify-content: center; flex-shrink: 0; margin-top: 0.1rem; }
.flow-card__text            { font-size: 0.75rem; font-weight: 700; color: #64748b; text-transform: uppercase; letter-spacing: 0.05em; line-height: 1.4; }

/* ── Transitions ── */
.modal-fade-enter-active,
.modal-fade-leave-active    { transition: opacity 0.22s ease; }
.modal-fade-enter-from,
.modal-fade-leave-to        { opacity: 0; }

.modal-slide-enter-active   { transition: transform 0.25s cubic-bezier(0.34, 1.3, 0.64, 1), opacity 0.22s ease; }
.modal-slide-leave-active   { transition: transform 0.18s ease, opacity 0.18s ease; }
.modal-slide-enter-from     { transform: translateY(2rem) scale(0.97); opacity: 0; }
.modal-slide-leave-to       { transform: translateY(1rem) scale(0.98); opacity: 0; }
</style>
