<template>
  <div class="interview-page">
    <div class="page-topbar">
      <nav class="breadcrumb">
        <router-link :to="{ name: 'recruiter-interviews' }" class="breadcrumb__link">
          Quản lý lịch phỏng vấn
        </router-link>
        <span class="material-symbols-outlined breadcrumb__sep">chevron_right</span>
        <span class="breadcrumb__current">Thiết lập phỏng vấn</span>
      </nav>
      <button class="btn-overdue" @click="goToOverduePage">
        <span class="material-symbols-outlined">schedule</span>
        Xử lý quá hạn
      </button>
    </div>

    <InterviewStageCards
      :stages="stageCards"
      :active-stage-id="activeStageId ?? 0"
      :total-candidates="totalApps"
      @select="handleSelectStage"
      @rename="handleRenameStage"
      @add="handleAddStage"
      @delete="handleDeleteStage"
    />

    <!-- ── Reschedule Modal ───────────────────────────────── -->
    <GlobalModal
      :visible="isRescheduleVisible"
      title="Đổi lịch phỏng vấn"
      :subtitle="rescheduleCandidate?.name"
      icon="event_repeat"
      confirm-text="Lưu thay đổi"
      confirm-icon="save"
      :loading="isRescheduleLoading"
      @close="isRescheduleVisible = false"
      @confirm="confirmReschedule"
    >
      <div class="reschedule-form">
        <div class="reschedule-form__row">
          <div class="reschedule-form__field">
            <label class="reschedule-form__label">Ngày phỏng vấn <span class="reschedule-form__required">*</span></label>
            <input
              v-model="rescheduleForm.date"
              type="date"
              class="reschedule-form__input"
              :class="{ 'reschedule-form__input--error': rescheduleErrors.date }"
              @change="rescheduleErrors.date = ''"
            />
            <span v-if="rescheduleErrors.date" class="reschedule-form__error">{{ rescheduleErrors.date }}</span>
          </div>
          <div class="reschedule-form__field">
            <label class="reschedule-form__label">Giờ phỏng vấn <span class="reschedule-form__required">*</span></label>
            <input
              v-model="rescheduleForm.time"
              type="time"
              class="reschedule-form__input"
              :class="{ 'reschedule-form__input--error': rescheduleErrors.time }"
              @change="rescheduleErrors.time = ''"
            />
            <span v-if="rescheduleErrors.time" class="reschedule-form__error">{{ rescheduleErrors.time }}</span>
          </div>
        </div>

        <!-- Hình thức -->
        <div class="reschedule-form__field">
          <label class="reschedule-form__label">Hình thức phỏng vấn</label>
          <div class="reschedule-form__mode-toggle">
            <button
              type="button"
              class="reschedule-form__mode-btn"
              :class="{ 'reschedule-form__mode-btn--active': rescheduleForm.interviewType !== 'onsite' }"
              @click="setRescheduleType('online')"
            >
              <span class="material-symbols-outlined">videocam</span>
              Trực tuyến
            </button>
            <button
              type="button"
              class="reschedule-form__mode-btn"
              :class="{ 'reschedule-form__mode-btn--active': rescheduleForm.interviewType === 'onsite' }"
              @click="setRescheduleType('onsite')"
            >
              <span class="material-symbols-outlined">location_on</span>
              Trực tiếp
            </button>
          </div>
        </div>

        <!-- Link hoặc địa điểm tuỳ hình thức -->
        <div v-if="rescheduleForm.interviewType !== 'onsite'" class="reschedule-form__field">
          <label class="reschedule-form__label">Link họp (Google Meet / Zoom) <span class="reschedule-form__required">*</span></label>
          <input
            v-model="rescheduleForm.meetingLink"
            type="url"
            class="reschedule-form__input"
            :class="{ 'reschedule-form__input--error': rescheduleErrors.meetingLink }"
            placeholder="https://meet.google.com/..."
            @input="rescheduleErrors.meetingLink = ''"
          />
          <span v-if="rescheduleErrors.meetingLink" class="reschedule-form__error">{{ rescheduleErrors.meetingLink }}</span>
        </div>
        <div v-else class="reschedule-form__field">
          <label class="reschedule-form__label">Địa điểm phỏng vấn <span class="reschedule-form__required">*</span></label>
          <input
            v-model="rescheduleForm.location"
            type="text"
            class="reschedule-form__input"
            :class="{ 'reschedule-form__input--error': rescheduleErrors.location }"
            placeholder="Nhập địa chỉ văn phòng..."
            @input="rescheduleErrors.location = ''"
          />
          <span v-if="rescheduleErrors.location" class="reschedule-form__error">{{ rescheduleErrors.location }}</span>
        </div>

        <div class="reschedule-form__field">
          <label class="reschedule-form__label">Ghi chú cho người phỏng vấn</label>
          <textarea v-model="rescheduleForm.note" class="reschedule-form__input reschedule-form__textarea" rows="2" placeholder="Nhập ghi chú..." />
        </div>
      </div>
    </GlobalModal>

    <InterviewCandidateTable
      :stage-name="activeStageName"
      :candidates="filteredCandidates"
      :total-count="totalApps"
      :current-page="currentPage + 1"
      :total-pages="totalPages"
      v-model:search-value="searchValue"
      v-model:status-filter="statusFilter"
      @search="handleSearch"
      @export="handleExport"
      @open-link="handleOpenLink"
      @view-detail="handleViewDetail"
      @reschedule="handleReschedule"
      @remind="handleRemind"
      @cancel="handleCancel"
      @schedule="handleSchedule"
      @evaluate="handleEvaluate"
      @offer="handleOffer"
      @save-talent-pool="handleSaveTalentPool"
      @page-change="currentPage = $event - 1"
    />

    <!-- ── Offer Modal ────────────────────────────────────── -->
    <GlobalModal
      :visible="isOfferVisible"
      title="Cập nhật kết quả Offer"
      :subtitle="offerCandidate?.name"
      icon="card_giftcard"
      confirm-text="Xác nhận"
      confirm-icon="check_circle"
      :loading="isOfferLoading"
      @close="isOfferVisible = false"
      @confirm="confirmOffer"
    >
      <div class="reschedule-form">
        <div class="reschedule-form__field">
          <label class="reschedule-form__label">Kết quả từ ứng viên <span class="reschedule-form__required">*</span></label>
          <div class="evaluate-result-toggle">
            <button
              type="button"
              class="evaluate-result-btn evaluate-result-btn--pass"
              :class="{ 'evaluate-result-btn--active': offerForm.result === 'accepted' }"
              @click="offerForm.result = 'accepted'"
            >
              <span class="material-symbols-outlined">check_circle</span>
              Đồng ý (ACCEPTED)
            </button>
            <button
              type="button"
              class="evaluate-result-btn evaluate-result-btn--fail"
              :class="{ 'evaluate-result-btn--active': offerForm.result === 'declined' }"
              @click="offerForm.result = 'declined'"
            >
              <span class="material-symbols-outlined">cancel</span>
              Từ chối (DECLINED)
            </button>
          </div>
          <span v-if="offerErrors.result" class="reschedule-form__error">{{ offerErrors.result }}</span>
        </div>
        <p class="offer-tip">
          <span class="material-symbols-outlined">info</span>
          Lưu ý: Nếu ứng viên từ chối, đơn ứng tuyển sẽ bị chuyển sang trạng thái <strong>Bị từ chối</strong>.
        </p>
      </div>
    </GlobalModal>

    <!-- ── Evaluate Modal ─────────────────────────────────── -->
    <GlobalModal
      :visible="isEvaluateVisible"
      title="Đánh giá phỏng vấn"
      :subtitle="evaluateCandidate?.name"
      icon="rate_review"
      :loading="isEvaluateLoading"
      @close="isEvaluateVisible = false"
    >
      <div class="reschedule-form">
        <!-- Kết quả -->
        <div class="reschedule-form__field">
          <label class="reschedule-form__label">Kết quả <span class="reschedule-form__required">*</span></label>
          <div class="evaluate-result-toggle">
            <button
              type="button"
              class="evaluate-result-btn evaluate-result-btn--pass"
              :class="{ 'evaluate-result-btn--active': evaluateForm.result === 'PASS' }"
              @click="evaluateForm.result = 'PASS'; evaluateErrors.result = ''"
            >
              <span class="material-symbols-outlined">check_circle</span>
              Đạt (PASS)
            </button>
            <button
              type="button"
              class="evaluate-result-btn evaluate-result-btn--fail"
              :class="{ 'evaluate-result-btn--active': evaluateForm.result === 'FAIL' }"
              @click="evaluateForm.result = 'FAIL'; evaluateErrors.result = ''"
            >
              <span class="material-symbols-outlined">cancel</span>
              Không đạt (FAIL)
            </button>
          </div>
          <span v-if="evaluateErrors.result" class="reschedule-form__error">{{ evaluateErrors.result }}</span>
        </div>

        <!-- Điểm đánh giá (1-5 sao) -->
        <div class="reschedule-form__field">
          <label class="reschedule-form__label">Điểm đánh giá</label>
          <div class="evaluate-stars">
            <button
              v-for="star in 5"
              :key="star"
              type="button"
              class="evaluate-star"
              :class="{ 'evaluate-star--active': evaluateForm.rating !== null && star <= evaluateForm.rating }"
              @click="evaluateForm.rating = evaluateForm.rating === star ? null : star; evaluateErrors.rating = ''"
            >
              <span class="material-symbols-outlined">star</span>
            </button>
            <span v-if="evaluateForm.rating" class="evaluate-stars__label">{{ evaluateForm.rating }}/5</span>
          </div>
          <span v-if="evaluateErrors.rating" class="reschedule-form__error">{{ evaluateErrors.rating }}</span>
        </div>

        <!-- Ghi chú -->
        <div class="reschedule-form__field">
          <label class="reschedule-form__label">Ghi chú nhận xét</label>
          <textarea
            v-model="evaluateForm.note"
            class="reschedule-form__input reschedule-form__textarea"
            rows="3"
            placeholder="Nhận xét về ứng viên..."
          />
        </div>

        <!-- Thông báo ứng viên -->
        <div class="reschedule-form__field">
          <label class="reschedule-form__checkbox-label">
            <input v-model="evaluateForm.notifyCandidate" type="checkbox" disabled />
            Gửi email thông báo kết quả cho ứng viên
          </label>
        </div>
      </div>

      <template #footer>
        <button
          class="modal-btn modal-btn--cancel"
          type="button"
          @click="isEvaluateVisible = false"
        >
          Hủy bỏ
        </button>
        <button
          class="modal-btn modal-btn--talent-pool"
          type="button"
          :disabled="isEvaluateLoading"
          @click="handleSaveTalentPoolFromModal(); isEvaluateVisible = false"
        >
          <span class="material-symbols-outlined">group_add</span>
          Lưu vào TalentPool
        </button>
        <button
          class="modal-btn modal-btn--primary"
          type="button"
          :disabled="isEvaluateLoading"
          @click="confirmEvaluate"
        >
          <span v-if="isEvaluateLoading" class="material-symbols-outlined animate-spin">progress_activity</span>
          <span v-else class="material-symbols-outlined">save</span>
          {{ isEvaluateLoading ? 'Đang xử lý...' : 'Lưu đánh giá' }}
        </button>
      </template>
    </GlobalModal>

    <!-- Talent Pool Modal -->
    <SaveToTalentPoolModal
      :visible="showTalentPoolModal"
      :candidate-name="talentPoolCandidate?.name"
      :loading="savingToPool"
      @close="showTalentPoolModal = false"
      @confirm="confirmSaveToTalentPool"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import InterviewStageCards from '@/components/recruiter/interviews/InterviewStageCards.vue'
import InterviewCandidateTable from '@/components/recruiter/interviews/InterviewCandidateTable.vue'
import employerInterviewService from '@/services/employerInterview.service'
import employerTalentPoolService from '@/services/employerTalentPool.service'
import SaveToTalentPoolModal from '@/components/recruiter/talent-pool/SaveToTalentPoolModal.vue'
import type { ResInterviewRoundDTO, ResInterviewScheduleDTO } from '@/types/interview.types'
import { INTERVIEW_STATUS, INTERVIEW_TYPE, OFFER_RESULT } from '@/constants/interview.constants'
import { useToast } from '@/composables/useToast'
import { useConfirm } from '@/composables/useConfirm'
import GlobalModal from '@/components/ui/GlobalModal.vue'

const router = useRouter()
const route  = useRoute()
const toast  = useToast()
const { confirm } = useConfirm()

const jobId = computed(() => Number(route.params.id))

// ── Offer Modal State ──────────────────────────────────────
const isOfferVisible  = ref(false)
const isOfferLoading  = ref(false)
const offerApplicationId = ref<number | null>(null)
const offerCandidate  = ref<{ name: string } | null>(null)
const offerForm = ref({
  result: '' as typeof OFFER_RESULT[keyof typeof OFFER_RESULT] | '',
})
const offerErrors = ref<Record<string, string>>({})

const PAGE_SIZE = 10

// ── State ─────────────────────────────────────────────────
const activeStageId = ref<number | null>(null)
const currentPage   = ref(0) // 0-based per skill-Pagination.md
const searchValue   = ref('')
const statusFilter  = ref('all')

// ── Evaluate Modal State ───────────────────────────────────
const isEvaluateVisible  = ref(false)
const isEvaluateLoading  = ref(false)
const evaluateScheduleId = ref<number | null>(null)
const evaluateCandidate  = ref<{ name: string; candidateUserId: number } | null>(null)
const evaluateForm = ref({
  result:          '' as 'PASS' | 'FAIL' | '',
  rating:          null as number | null,
  note:            '',
  notifyCandidate: true,
})
const evaluateErrors = ref<Record<string, string>>({})

// ── Reschedule Modal State ─────────────────────────────────
const isRescheduleVisible = ref(false)
const isRescheduleLoading = ref(false)
const rescheduleScheduleId = ref<number | null>(null)
const rescheduleCandidate  = ref<{ name: string } | null>(null)
const rescheduleForm = ref({
  date:          '',
  time:          '',
  interviewType: 'online' as string,
  meetingLink:   '',
  location:      '',
  note:          '',
})
const rescheduleErrors = ref<Record<string, string>>({})

// ── API Data ───────────────────────────────────────────────
const rounds         = ref<ResInterviewRoundDTO[]>([])
const roundSchedules = ref<ResInterviewScheduleDTO[]>([])

// ── Computed: Stage cards ──────────────────────────────────
const stageCards = computed(() =>
  rounds.value.map(r => ({
    id:    r.id,
    label: `Vòng ${r.roundNumber}`,
    name:  r.roundName,

  }))
)

const activeStageName = computed(() => {
  const r = rounds.value.find(r => r.id === activeStageId.value)
  return r ? `Vòng ${r.roundNumber} - ${r.roundName}` : ''
})

// ── Computed: Active round interviewers ────────────────────
const activeRoundInterviewers = computed(() =>
  rounds.value.find(r => r.id === activeStageId.value)?.interviewers ?? []
)

// ── Computed: Map schedules → candidate rows ───────────────
const filteredCandidates = computed(() => {
  const isFinal = rounds.value.find(r => r.id === activeStageId.value)?.isFinal ?? false
  return roundSchedules.value.map(s => mapToCandidate(s, activeRoundInterviewers.value, isFinal))
})

// ── Computed: Pagination ───────────────────────────────────
const totalApps  = computed(() => filteredCandidates.value.length)
const totalPages = computed(() => Math.ceil(totalApps.value / PAGE_SIZE))

// ── Helpers ────────────────────────────────────────────────
function mapApiStatusToUi(status: string): 'confirmed' | 'pending' | 'overdue' {
  if (status === INTERVIEW_STATUS.CONFIRMED || status === INTERVIEW_STATUS.COMPLETED) return 'confirmed'
  if (status === INTERVIEW_STATUS.NO_SHOW)                                            return 'overdue'
  return 'pending'
}

function mapInterviewType(type: string): { label: string; formatType: 'online' | 'offline' } {
  if (type === INTERVIEW_TYPE.ONLINE) return { label: 'Trực tuyến', formatType: 'online' }
  if (type === INTERVIEW_TYPE.PHONE)  return { label: 'Điện thoại', formatType: 'online' }
  return { label: 'Trực tiếp', formatType: 'offline' }
}

function formatDateLabel(iso: string): string {
  const scheduled = new Date(iso)
  const today     = new Date()
  const diffDays  = Math.floor(
    (new Date(scheduled).setHours(0,0,0,0) - new Date(today).setHours(0,0,0,0)) / 86_400_000
  )
  const time = scheduled.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })
  if (diffDays === 0)  return `Hôm nay, ${time}`
  if (diffDays === 1)  return `Ngày mai, ${time}`
  if (diffDays === -1) return `Hôm qua, ${time}`
  return scheduled.toLocaleDateString('vi-VN', { day: 'numeric', month: 'long' }) + `, ${time}`
}

function getInitials(name: string): string {
  const parts = name.trim().split(' ')
  return parts.length >= 2
    ? (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
    : name.slice(0, 2).toUpperCase()
}

function mapToCandidate(
  schedule: ResInterviewScheduleDTO,
  interviewers: { id: number; name: string }[] = [],
  isFinalRound: boolean = false
) {
  const firstInterviewer = interviewers[0]
  const { label, formatType } = mapInterviewType(schedule.interviewType)
  return {
    id:        schedule.applicationId,
    name:      schedule.candidateName,
    role:      schedule.candidateEmail,
    avatarUrl: '',
    interviewer: firstInterviewer
      ? { id: firstInterviewer.id, name: firstInterviewer.name, initials: getInitials(firstInterviewer.name) }
      : { id: 0, name: '—', initials: '?' },
    hasSchedule:    true,
    scheduleStatus: schedule.status,
    dateLabel:      formatDateLabel(schedule.scheduledAt),
    dateFormatted:  new Date(schedule.scheduledAt).toLocaleDateString('vi-VN', {
      day: 'numeric', month: 'long', year: 'numeric',
    }),
    format:     schedule.interviewType === INTERVIEW_TYPE.ONLINE && schedule.meetingLink
      ? (schedule.meetingLink.includes('meet.google') ? 'Google Meet' : 'Trực tuyến')
      : label,
    formatType,
    status: mapApiStatusToUi(schedule.status),
    applicationStatus: schedule.applicationStatus,
    isFinalRound,
  }
}

// ── API Calls ──────────────────────────────────────────────
async function fetchRounds() {
  try {
    rounds.value = await employerInterviewService.getRounds(jobId.value)
    if (rounds.value.length > 0 && activeStageId.value === null) {
      activeStageId.value = rounds.value[0].id
    }
  } catch (err: any) {
    toast.error('Lỗi', err?.response?.data?.message ?? 'Không thể tải danh sách vòng phỏng vấn.')
  }
}

async function fetchSchedules() {
  if (activeStageId.value === null) return
  try {
    roundSchedules.value = await employerInterviewService.getSchedules(jobId.value, {
      roundId: activeStageId.value,
      status:  statusFilter.value !== 'all' ? statusFilter.value : undefined,
      search:  searchValue.value.trim() || undefined,
    })
  } catch (err: any) {
    toast.error('Lỗi', err?.response?.data?.message ?? 'Không thể tải lịch phỏng vấn.')
  }
}

// ── Watchers ───────────────────────────────────────────────
watch(activeStageId, () => { fetchSchedules() })
watch(statusFilter,  () => { fetchSchedules() })

onMounted(async () => {
  await fetchRounds()
  await fetchSchedules()
})

// ── Handlers ───────────────────────────────────────────────
function handleSelectStage(stageId: number) {
  activeStageId.value = stageId
}

function handleSearch() {
  fetchSchedules()
}

function handleExport() {
  const round = rounds.value.find(r => r.id === activeStageId.value)
  router.push({
    name: 'recruiter-interview-sent-proposals',
    query: {
      jobId: jobId.value,
      roundId: activeStageId.value ?? undefined,
      roundName: round ? `Vòng ${round.roundNumber} – ${round.roundName}` : undefined,
    },
  })
}

function handleOpenLink(candidateId: number) {
  console.log('Open meeting link for candidate:', candidateId)
}

function handleViewDetail(applicationId: number) {
  const roundNum = rounds.value.find(r => r.id === activeStageId.value)?.roundNumber
  router.push({
    name: 'recruiter-job-interview-candidate-detail',
    params: { id: jobId.value, applicationId },
    query: { roundNumber: roundNum }
  })
}

function setRescheduleType(type: 'online' | 'onsite') {
  rescheduleForm.value.interviewType = type
}

function handleReschedule(applicationId: number) {
  const schedule = roundSchedules.value.find(s => s.applicationId === applicationId)
  if (!schedule) return

  const dt = new Date(schedule.scheduledAt)
  rescheduleScheduleId.value = schedule.id
  rescheduleCandidate.value  = { name: schedule.candidateName }
  rescheduleForm.value = {
    date:          dt.toISOString().split('T')[0],
    time:          dt.toTimeString().slice(0, 5),
    interviewType: schedule.interviewType,
    meetingLink:   schedule.meetingLink ?? '',
    location:      schedule.location ?? '',
    note:          schedule.interviewerNote ?? '',
  }
  rescheduleErrors.value = {}
  isRescheduleVisible.value = true
}

function validateReschedule(): boolean {
  const errors: Record<string, string> = {}
  const f = rescheduleForm.value

  if (!f.date) {
    errors.date = 'Vui lòng chọn ngày phỏng vấn.'
  } else if (!f.time) {
    errors.time = 'Vui lòng chọn giờ phỏng vấn.'
  } else {
    const scheduledAt = new Date(`${f.date}T${f.time}`)
    if (scheduledAt <= new Date()) {
      errors.date = 'Thời gian phỏng vấn phải ở tương lai.'
    }
  }

  if (f.interviewType !== 'onsite' && !f.meetingLink.trim()) {
    errors.meetingLink = 'Vui lòng nhập link họp.'
  }
  if (f.interviewType === 'onsite' && !f.location.trim()) {
    errors.location = 'Vui lòng nhập địa điểm phỏng vấn.'
  }

  rescheduleErrors.value = errors
  return Object.keys(errors).length === 0
}

async function confirmReschedule() {
  if (!validateReschedule()) return

  isRescheduleLoading.value = true
  try {
    const isOnsite = rescheduleForm.value.interviewType === 'onsite'
    const updated = await employerInterviewService.updateSchedule(rescheduleScheduleId.value!, {
      scheduledAt:     `${rescheduleForm.value.date}T${rescheduleForm.value.time}:00`,
      interviewType:   rescheduleForm.value.interviewType as 'online' | 'onsite' | 'phone',
      meetingLink:     !isOnsite ? rescheduleForm.value.meetingLink || undefined : undefined,
      location:        isOnsite  ? rescheduleForm.value.location    || undefined : undefined,
      interviewerNote: rescheduleForm.value.note || undefined,
    })

    const idx = roundSchedules.value.findIndex(s => s.id === rescheduleScheduleId.value)
    if (idx !== -1) roundSchedules.value[idx] = updated

    toast.success('Đổi lịch thành công!', `Lịch phỏng vấn của ${rescheduleCandidate.value?.name} đã được cập nhật.`)
    isRescheduleVisible.value = false
  } catch (err: any) {
    toast.error('Lỗi', err?.response?.data?.message ?? 'Không thể đổi lịch. Vui lòng thử lại.')
  } finally {
    isRescheduleLoading.value = false
  }
}

async function handleRemind(applicationId: number) {
  const schedule = roundSchedules.value.find(s => s.applicationId === applicationId)
  if (!schedule) return

  try {
    await employerInterviewService.remindConfirmSchedule(schedule.id)
    toast.success('Đã gửi nhắc nhở', `Email nhắc nhở xác nhận lịch phỏng vấn đã được gửi đến ${schedule.candidateName}.`)
  } catch (err: any) {
    toast.error('Lỗi', err?.response?.data?.message ?? 'Không thể gửi nhắc nhở. Vui lòng thử lại.')
  }
}

async function handleCancel(applicationId: number) {
  const schedule = roundSchedules.value.find(s => s.applicationId === applicationId)
  if (!schedule) return

  const ok = await confirm({
    title: 'Hủy lịch phỏng vấn',
    message: `Bạn có chắc muốn hủy lịch phỏng vấn của ${schedule.candidateName}?`,
    confirmText: 'Hủy lịch',
    confirmColor: 'red',
    icon: 'event_busy',
  })
  if (!ok) return

  try {
    await employerInterviewService.deleteSchedule(schedule.id)
    roundSchedules.value = roundSchedules.value.filter(s => s.id !== schedule.id)
    toast.success('Đã hủy lịch', `Lịch phỏng vấn của ${schedule.candidateName} đã được hủy.`)
  } catch (err: any) {
    toast.error('Lỗi', err?.response?.data?.message ?? 'Không thể hủy lịch. Vui lòng thử lại.')
  }
}

function handleSchedule(applicationId: number) {
  router.push({
    name: 'recruiter-interview-create',
    query: { jobId: jobId.value, applicationId, roundId: activeStageId.value ?? undefined },
  })
}

function handleEvaluate(applicationId: number) {
  const schedule = roundSchedules.value.find(s => s.applicationId === applicationId)
  if (!schedule) return

  evaluateScheduleId.value = schedule.id
  evaluateCandidate.value  = { name: schedule.candidateName, candidateUserId: schedule.candidateUserId }
  evaluateForm.value = { result: '', rating: null, note: '', notifyCandidate: true }
  evaluateErrors.value = {}
  isEvaluateVisible.value = true
}

async function confirmEvaluate() {
  const errors: Record<string, string> = {}
  if (!evaluateForm.value.result) {
    errors.result = 'Vui lòng chọn kết quả phỏng vấn.'
  }
  if (evaluateForm.value.rating !== null && (evaluateForm.value.rating < 1 || evaluateForm.value.rating > 5)) {
    errors.rating = 'Điểm đánh giá phải từ 1 đến 5.'
  }
  if (Object.keys(errors).length > 0) {
    evaluateErrors.value = errors
    return
  }

  isEvaluateLoading.value = true
  try {
    await employerInterviewService.createResult(evaluateScheduleId.value!, {
      result:          evaluateForm.value.result as 'PASS' | 'FAIL',
      rating:          evaluateForm.value.rating ?? undefined,
      note:            evaluateForm.value.note || undefined,
      notifyCandidate: evaluateForm.value.notifyCandidate,
    })

    await fetchSchedules()
    toast.success('Đánh giá thành công!', `Đã lưu kết quả phỏng vấn của ${evaluateCandidate.value?.name}.`)
    isEvaluateVisible.value = false
  } catch (err: any) {
    toast.error('Lỗi', err?.response?.data?.message ?? 'Không thể lưu kết quả. Vui lòng thử lại.')
  } finally {
    isEvaluateLoading.value = false
  }
}

function handleOffer(applicationId: number) {
  const schedule = roundSchedules.value.find(s => s.applicationId === applicationId)
  if (!schedule) return

  offerApplicationId.value = applicationId
  offerCandidate.value     = { name: schedule.candidateName }
  offerForm.value          = { result: '' }
  offerErrors.value        = {}
  isOfferVisible.value     = true
}

async function confirmOffer() {
  if (!offerForm.value.result) {
    offerErrors.value = { result: 'Vui lòng chọn kết quả từ ứng viên.' }
    return
  }

  isOfferLoading.value = true
  try {
    await employerInterviewService.updateOffer(offerApplicationId.value!, {
      result: offerForm.value.result as any
    })

    await fetchSchedules()
    toast.success('Thành công!', `Đã cập nhật kết quả offer của ${offerCandidate.value?.name}.`)
    isOfferVisible.value = false
  } catch (err: any) {
    toast.error('Lỗi', err?.response?.data?.message ?? 'Không thể cập nhật offer. Vui lòng thử lại.')
  } finally {
    isOfferLoading.value = false
  }
}

async function handleRenameStage(stageId: number, newName: string) {
  try {
    const updated = await employerInterviewService.updateRound(stageId, { roundName: newName })
    const idx = rounds.value.findIndex(r => r.id === stageId)
    if (idx !== -1) rounds.value[idx] = updated
    toast.success('Đổi tên thành công!', `Vòng phỏng vấn đã được đổi thành "${newName}".`)
  } catch (err: any) {
    toast.error('Lỗi', err?.response?.data?.message ?? 'Không thể đổi tên vòng. Vui lòng thử lại.')
  }
}

async function handleAddStage(data: { roundName: string; description?: string; expectedDuration?: number; isFinal?: boolean }) {
  try {
    const created = await employerInterviewService.createRound(jobId.value, data)
    rounds.value.push(created)
    toast.success('Thêm vòng thành công!', `Đã thêm vòng phỏng vấn "${data.roundName}".`)
  } catch (err: any) {
    toast.error('Lỗi', err?.response?.data?.message ?? 'Không thể thêm vòng. Vui lòng thử lại.')
  }
}

async function handleDeleteStage(stageId: number) {
  try {
    await employerInterviewService.deleteRound(stageId)
    const deleted = rounds.value.find(r => r.id === stageId)
    rounds.value = rounds.value.filter(r => r.id !== stageId)
    if (activeStageId.value === stageId) {
      activeStageId.value = rounds.value.length > 0 ? rounds.value[0].id : null
    }
    toast.success('Đã xóa!', `Vòng phỏng vấn "${deleted?.roundName ?? ''}" đã được xóa.`)
  } catch (err: any) {
    toast.error('Lỗi', err?.response?.data?.message ?? 'Không thể xóa vòng. Vui lòng thử lại.')
  }
}

// ── Talent Pool Modal ────────────────────────────────────────────────────────
const showTalentPoolModal    = ref(false)
const savingToPool           = ref(false)
const talentPoolCandidate    = ref<{ userId: number; name: string } | null>(null)

function handleSaveTalentPool(applicationId: number) {
  const schedule = roundSchedules.value.find(s => s.applicationId === applicationId)
  if (!schedule?.candidateUserId) return
  talentPoolCandidate.value = { userId: schedule.candidateUserId, name: schedule.candidateName }
  showTalentPoolModal.value = true
}

function handleSaveTalentPoolFromModal() {
  if (!evaluateCandidate.value?.candidateUserId) return
  talentPoolCandidate.value = { userId: evaluateCandidate.value.candidateUserId, name: evaluateCandidate.value.name }
  showTalentPoolModal.value = true
}

async function confirmSaveToTalentPool(note: string) {
  if (!talentPoolCandidate.value) return
  savingToPool.value = true
  try {
    await employerTalentPoolService.addToTalentPool({
      candidateUserId: talentPoolCandidate.value.userId,
      source: 'INTERVIEW',
      note: note || undefined,
    })
    showTalentPoolModal.value = false
    toast.success('Đã lưu vào Talent Pool!', `Ứng viên ${talentPoolCandidate.value.name} đã được thêm vào Talent Pool.`)
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Không thể lưu vào Talent Pool. Vui lòng thử lại.'
    toast.error('Lỗi', typeof msg === 'string' ? msg : msg?.[0])
  } finally {
    savingToPool.value = false
  }
}

function goToOverduePage() {
  router.push({ name: 'recruiter-interview-overdue', query: { jobId: jobId.value } })
}
</script>

<style scoped>
.interview-page {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  min-height: 100vh;
  background: #f6f6f8;
  font-family: 'Manrope', sans-serif;
}

.page-topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.btn-overdue {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.5rem 1.125rem;
  background: #fff7ed;
  color: #c2410c;
  border: 1px solid #fed7aa;
  border-radius: 0.625rem;
  font-size: 0.875rem;
  font-weight: 700;
  font-family: inherit;
  cursor: pointer;
  white-space: nowrap;
  transition: background 0.18s, border-color 0.18s;
}
.btn-overdue .material-symbols-outlined { font-size: 1.1rem; }
.btn-overdue:hover { background: #ffedd5; border-color: #fdba74; }

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.875rem;
  font-weight: 500;
}

.breadcrumb__link {
  color: #6b7280;
  text-decoration: none;
  cursor: pointer;
}

.breadcrumb__link:hover {
  color: #4B9AF6;
  text-decoration: underline;
}

.breadcrumb__sep {
  font-size: 1.125rem;
  color: #9ca3af;
  user-select: none;
}

.breadcrumb__current {
  color: #0f172a;
  font-weight: 600;
}

/* Reschedule form */
.reschedule-form { display: flex; flex-direction: column; gap: 1rem; padding: 0.25rem 0; }
.reschedule-form__row { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
.reschedule-form__field { display: flex; flex-direction: column; gap: 0.375rem; }
.reschedule-form__label { font-size: 0.75rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.08em; color: #64748b; }
.reschedule-form__input {
  width: 100%; padding: 0.75rem 1rem; box-sizing: border-box;
  background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 0.625rem;
  font-size: 0.875rem; font-family: inherit; color: #0f172a;
  outline: none; transition: border-color 0.18s, box-shadow 0.18s;
}
.reschedule-form__input:focus { border-color: #4b9af6; box-shadow: 0 0 0 3px rgba(75,154,246,0.15); }
.reschedule-form__textarea { resize: none; }
.reschedule-form__mode-toggle {
  display: flex; gap: 0.25rem; padding: 0.25rem;
  background: #f8fafc; border-radius: 0.75rem; width: fit-content;
}
.reschedule-form__mode-btn {
  display: flex; align-items: center; gap: 0.4rem;
  padding: 0.5rem 1.25rem; border-radius: 0.625rem; border: none;
  background: transparent; font-size: 0.875rem; font-weight: 500;
  font-family: inherit; color: #64748b; cursor: pointer;
  transition: background 0.18s, color 0.18s, box-shadow 0.18s;
}
.reschedule-form__mode-btn .material-symbols-outlined { font-size: 1.1rem; }
.reschedule-form__mode-btn--active {
  background: #fff; color: #4b9af6; font-weight: 700;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}
.reschedule-form__required { color: #ef4444; margin-left: 2px; }
.reschedule-form__input--error { border-color: #ef4444 !important; }
.reschedule-form__input--error:focus { box-shadow: 0 0 0 3px rgba(239,68,68,0.15) !important; }
.reschedule-form__error { font-size: 0.75rem; color: #ef4444; font-weight: 500; margin-top: 2px; }
.reschedule-form__checkbox-label {
  display: flex; align-items: center; gap: 0.5rem;
  font-size: 0.875rem; color: #334155; cursor: pointer;
}
.reschedule-form__checkbox-label input[type="checkbox"] { width: 1rem; height: 1rem; cursor: pointer; accent-color: #4b9af6; }

/* ── Evaluate result toggle ── */
.evaluate-result-toggle { display: flex; gap: 0.75rem; }
.evaluate-result-btn {
  flex: 1; display: flex; align-items: center; justify-content: center; gap: 0.5rem;
  padding: 0.75rem 1rem; border-radius: 0.75rem; border: 2px solid #e2e8f0;
  background: #f8fafc; font-size: 0.875rem; font-weight: 600;
  font-family: inherit; color: #94a3b8; cursor: pointer;
  transition: all 0.18s;
}
.evaluate-result-btn .material-symbols-outlined { font-size: 1.2rem; }
.evaluate-result-btn--pass:hover { border-color: #22c55e; color: #16a34a; background: #f0fdf4; }
.evaluate-result-btn--pass.evaluate-result-btn--active {
  border-color: #22c55e; color: #16a34a; background: #f0fdf4;
  box-shadow: 0 0 0 3px rgba(34,197,94,0.15);
}
.evaluate-result-btn--fail:hover { border-color: #ef4444; color: #dc2626; background: #fef2f2; }
.evaluate-result-btn--fail.evaluate-result-btn--active {
  border-color: #ef4444; color: #dc2626; background: #fef2f2;
  box-shadow: 0 0 0 3px rgba(239,68,68,0.15);
}

/* ── Star rating ── */
.evaluate-stars { display: flex; align-items: center; gap: 0.25rem; }
.evaluate-star {
  background: none; border: none; padding: 0.125rem; cursor: pointer;
  color: #cbd5e1; transition: color 0.15s, transform 0.15s;
}
.evaluate-star .material-symbols-outlined { font-size: 1.75rem; font-variation-settings: 'FILL' 0; }
.evaluate-star--active .material-symbols-outlined { font-variation-settings: 'FILL' 1; }
.evaluate-star--active { color: #f59e0b; }
.evaluate-star:hover { color: #f59e0b; transform: scale(1.15); }
.evaluate-stars__label { margin-left: 0.5rem; font-size: 0.875rem; font-weight: 700; color: #f59e0b; }

.offer-tip {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1rem;
  background: #f0f9ff;
  border-radius: 0.75rem;
  color: #0369a1;
  font-size: 0.8125rem;
  line-height: 1.4;
}
.offer-tip .material-symbols-outlined { font-size: 1.1rem; color: #0ea5e9; }

/* ── Modal footer buttons ── */
.modal-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.625rem 1.25rem;
  border-radius: 0.75rem;
  font-size: 0.875rem;
  font-weight: 700;
  font-family: inherit;
  cursor: pointer;
  transition: background 0.15s, color 0.15s, transform 0.1s;
}
.modal-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.modal-btn .material-symbols-outlined { font-size: 1.1rem; }

.modal-btn--cancel {
  background: #fff;
  color: #64748b;
  border: 1px solid #e2e8f0;
}
.modal-btn--cancel:hover { background: #f8fafc; }

.modal-btn--talent-pool {
  background: #fff;
  color: #059669;
  border: 2px solid #d1fae5;
}
.modal-btn--talent-pool:hover:not(:disabled) { background: #059669; color: #fff; border-color: #059669; }

.modal-btn--primary {
  background: #4b9af6;
  color: #fff;
  border: none;
  box-shadow: 0 2px 8px rgba(75, 154, 246, 0.25);
}
.modal-btn--primary:hover:not(:disabled) { background: #2563eb; transform: scale(1.02); }
</style>
