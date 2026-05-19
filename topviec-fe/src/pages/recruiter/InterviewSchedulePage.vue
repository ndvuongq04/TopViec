<template>
  <div class="schedule-page">
    <!-- Breadcrumb + heading -->
    <div class="schedule-page__top">
      <Breadcrumb :items="breadcrumbItems" :hide-home="true" />
      <h2 class="schedule-page__title">Tạo lịch phỏng vấn mới</h2>
      <p class="schedule-page__subtitle">
        Chủ động thiết lập thời gian và hình thức phỏng vấn cho ứng viên tiềm năng.
      </p>
    </div>

    <!-- Main grid -->
    <div class="schedule-page__grid">
      <!-- Left column -->
      <div class="schedule-page__main">
        <InterviewCandidateSection
          v-model="candidateForm"
          :candidates="candidateOptions"
          :rounds="roundOptions"
          :disabled="true"
        />

        <InterviewScheduleSection v-model="scheduleForm" />

        <InterviewNoteSection v-model="note" />
      </div>

      <!-- Right column -->
      <div class="schedule-page__sidebar">
        <InterviewSummaryCard
          :candidate-name="candidateName"
          :date="scheduleForm.date"
          :time="scheduleForm.time"
          :mode="scheduleForm.mode"
          :loading="submitting"
          @submit="handleSubmit"
        />

        <InterviewTipCard />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Breadcrumb from '@/components/ui/Breadcrumb.vue'
import InterviewCandidateSection from '@/components/recruiter/interviews/interview-schedule/Interviewcandidatesection.vue'
import InterviewScheduleSection from '@/components/recruiter/interviews/interview-schedule/Interviewschedulesection.vue'
import InterviewNoteSection from '@/components/recruiter/interviews/interview-schedule/Interviewnotesection.vue'
import InterviewSummaryCard from '@/components/recruiter/interviews/interview-schedule/Interviewsummarycard.vue'
import InterviewTipCard from '@/components/recruiter/interviews/interview-schedule/Interviewtipcard.vue'
import employerInterviewService from '@/services/employerInterview.service'
import employerApplicationService from '@/services/employerApplication.service'
import { useToast } from '@/composables/useToast'
import type { ResInterviewRoundDTO } from '@/types/interview.types'
import type { ResEmployerApplicationDTO } from '@/types/employerApplication.types'

const route  = useRoute()
const router = useRouter()
const toast  = useToast()

// ── Query params ──────────────────────────────────────────
const jobId        = Number(route.query.jobId)
const applicationId = Number(route.query.applicationId)
const roundId      = Number(route.query.roundId)

const breadcrumbItems = computed(() => [
  { label: 'Thiết lập phỏng vấn', to: `/recruiter/jobs/${jobId}/interview-setup` },
  { label: 'Đặt lịch phỏng vấn thủ công' },
])

// ── API Data ──────────────────────────────────────────────
const application = ref<ResEmployerApplicationDTO | null>(null)
const rounds      = ref<ResInterviewRoundDTO[]>([])

// ── Computed options for the (disabled) section ───────────
const candidateName = computed(() => application.value?.candidateName ?? '')

const candidateOptions = computed(() => {
  if (!application.value) return []
  return [{
    id:    String(application.value.id),
    label: `${application.value.candidateName} - ${application.value.candidateEmail}`,
  }]
})

const roundOptions = computed(() =>
  rounds.value.map(r => ({
    value: String(r.id),
    label: `Vòng ${r.roundNumber} - ${r.roundName}`,
  }))
)

// ── Form state ────────────────────────────────────────────
const candidateForm = ref({
  candidateId: String(applicationId),
  round:       String(roundId),
})

const scheduleForm = ref({
  date:        '',
  time:        '',
  mode:        'online' as 'online' | 'offline',
  meetingLink: '',
  location:    '',
})

const note       = ref('')
const submitting = ref(false)

// ── API Calls ─────────────────────────────────────────────
async function fetchData() {
  try {
    const [appRes, roundsRes] = await Promise.all([
      employerApplicationService.getApplicationDetailByEmployer(applicationId),
      employerInterviewService.getRounds(jobId),
    ])
    application.value = appRes
    rounds.value      = roundsRes
  } catch (err: any) {
    toast.error('Lỗi', err?.response?.data?.message ?? 'Không thể tải thông tin.')
  }
}

onMounted(fetchData)

// ── Submit ────────────────────────────────────────────────
async function handleSubmit() {
  if (!scheduleForm.value.date || !scheduleForm.value.time) {
    toast.error('Thiếu thông tin', 'Vui lòng chọn ngày và giờ phỏng vấn.')
    return
  }

  submitting.value = true
  try {
    const scheduledAt = `${scheduleForm.value.date}T${scheduleForm.value.time}:00`

    await employerInterviewService.createSchedule(roundId, {
      applicationId,
      scheduledAt,
      interviewType: scheduleForm.value.mode === 'offline' ? 'onsite' : 'online',
      meetingLink:   scheduleForm.value.mode === 'online' ? scheduleForm.value.meetingLink || undefined : undefined,
      location:      scheduleForm.value.mode === 'offline' ? scheduleForm.value.location || undefined : undefined,
      interviewerNote: note.value || undefined,
    })

    toast.success('Đặt lịch thành công!', `Đã tạo lịch phỏng vấn cho ${candidateName.value}.`)
    router.push(`/recruiter/jobs/${jobId}/interview-setup`)
  } catch (err: any) {
    toast.error('Lỗi', err?.response?.data?.message ?? 'Không thể tạo lịch phỏng vấn. Vui lòng thử lại.')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.schedule-page {
  margin: 0 auto;
  width: 100%;
  font-family: 'Manrope', sans-serif;
}

/* Top */
.schedule-page__top {
  margin-bottom: 2rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.schedule-page__title {
  font-size: 1.875rem;
  font-weight: 800;
  letter-spacing: -0.03em;
  color: #0f172a;
  margin: 0 0 0.25rem;
}

.schedule-page__subtitle {
  font-size: 0.875rem;
  color: #64748b;
  margin: 0;
}

/* Grid */
.schedule-page__grid {
  display: grid;
  grid-template-columns: 1fr 20rem;
  gap: 2rem;
  align-items: start;
}

@media (max-width: 900px) {
  .schedule-page__grid {
    grid-template-columns: 1fr;
  }
}

/* Main column */
.schedule-page__main {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

/* Sidebar */
.schedule-page__sidebar {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}
</style>
