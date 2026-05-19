<template>
  <main class="proposal-page">
    <!-- Breadcrumb -->
    <nav class="breadcrumb">
      <router-link :to="{ name: 'recruiter-interviews' }" class="breadcrumb__link">
        Quản lý phỏng vấn
      </router-link>
      <span class="material-symbols-outlined breadcrumb__sep">chevron_right</span>
      <router-link
        :to="{ name: 'recruiter-job-interview-setup', params: { id: jobId } }"
        class="breadcrumb__link"
      >
        Thiết lập phỏng vấn
      </router-link>
      <span class="material-symbols-outlined breadcrumb__sep">chevron_right</span>
      <span class="breadcrumb__current">{{ roundName }}</span>
    </nav>

    <!-- Page Header -->
    <div class="proposal-page__header">
      <div class="proposal-page__title-group">
        <div class="proposal-page__meta">
          <span class="material-symbols-outlined proposal-page__meta-icon">work</span>
          <span class="proposal-page__job-name">{{ jobTitle || '...' }}</span>
          <span class="proposal-page__meta-divider">·</span>
          <span class="material-symbols-outlined proposal-page__meta-icon">layers</span>
          <span class="proposal-page__round-badge">{{ roundName }}</span>
        </div>
        <h2 class="proposal-page__title">Quản lý slot phỏng vấn</h2>
        <p class="proposal-page__subtitle">
          Theo dõi trạng thái các lời mời phỏng vấn đã gửi qua hệ thống.
        </p>
      </div>
      <div class="proposal-page__actions">
        <button class="btn btn--primary" @click="showCreateModal = true">
          <span class="material-symbols-outlined">add</span>
          Tạo slots mới
        </button>
      </div>
    </div>

    <!-- Table -->
    <ProposalTable
      :proposals="mappedProposals"
      :total="store.schedules.length"
      v-model:search-value="searchValue"
      @search="handleSearch"
      @extend-deadline="onExtendDeadline"
      @force-schedule="onForceSchedule"
    />

    <!-- Create Slots Modal -->
    <CreateSlotsModal
      :open="showCreateModal"
      :job-id="jobId"
      :round-id="roundId ?? null"
      :round-name="String(route.query.roundName ?? '')"
      @close="showCreateModal = false"
      @created="onSlotsCreated"
    />

    <!-- Extend Deadline Modal -->
    <ExtendDeadlineModal
      :visible="showExtendModal"
      :candidate-name="selectedItem?.candidate?.name"
      :loading="isExtending"
      @close="showExtendModal = false"
      @confirm="confirmExtend"
    />

    <!-- Force Schedule Modal -->
    <ForceScheduleModal
      :visible="showForceScheduleModal"
      :candidate-name="selectedItem?.candidate?.name"
      :loading="isForceScheduling"
      @close="showForceScheduleModal = false"
      @confirm="confirmForceSchedule"
    />
  </main>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import ProposalTable from '@/components/recruiter/interviews/interview-slot/ProposalTable.vue'
import CreateSlotsModal from '@/components/recruiter/interviews/interview-slot/CreateSlotsModal.vue'
import ExtendDeadlineModal from '@/components/recruiter/interviews/interview-overdue/ExtendDeadlineModal.vue'
import ForceScheduleModal from '@/components/recruiter/interviews/interview-overdue/ForceScheduleModal.vue'
import { useEmployerInterviewStore } from '@/stores/employerInterview.store'
import { publicJobPostingService } from '@/services/jobPosting.service'
import { useToast } from '@/composables/useToast'
import type { ReqForceScheduleDTO } from '@/types/interview.types'

const store = useEmployerInterviewStore()
const route = useRoute()
const toast = useToast()

const showCreateModal        = ref(false)
const searchValue            = ref('')
const jobTitle               = ref('')
const selectedItem           = ref<any>(null)
const showExtendModal        = ref(false)
const showForceScheduleModal = ref(false)
const isExtending            = ref(false)
const isForceScheduling      = ref(false)

const jobId     = computed(() => Number(route.query.jobId))
const roundId   = computed(() => route.query.roundId ? Number(route.query.roundId) : undefined)
const roundName = computed(() => String(route.query.roundName ?? ''))

async function fetchData() {
  if (jobId.value) {
    await store.fetchSchedules(jobId.value, {
      roundId: roundId.value,
    })
  }
}

async function fetchJobTitle() {
  if (jobId.value) {
    try {
      const job = await publicJobPostingService.getById(jobId.value)
      jobTitle.value = job.title
    } catch {
      // ignore, breadcrumb sẽ hiển thị id fallback
    }
  }
}

function handleSearch() {
  fetchData()
}

onMounted(() => {
  fetchData()
  fetchJobTitle()
})

async function onSlotsCreated() {
  await fetchData()
}

function onExtendDeadline(item: any) {
  selectedItem.value = item
  showExtendModal.value = true
}

function onForceSchedule(item: any) {
  selectedItem.value = item
  showForceScheduleModal.value = true
}

async function confirmExtend(days: number) {
  if (!selectedItem.value) return
  isExtending.value = true
  try {
    await store.extendDeadline(Number(selectedItem.value.candidateId), { extendDays: days })
    toast.success('Thành công', `Đã gia hạn thêm ${days} ngày cho ứng viên.`)
    showExtendModal.value = false
    await fetchData()
  } catch (e: any) {
    toast.error('Lỗi', e?.response?.data?.message ?? 'Không thể gia hạn thời gian.')
  } finally {
    isExtending.value = false
  }
}

async function confirmForceSchedule(form: { date: string; time: string; interviewType: 'online' | 'onsite'; meetingLink: string; location: string }) {
  if (!selectedItem.value) return
  const payload: ReqForceScheduleDTO = {
    scheduledAt:   new Date(`${form.date}T${form.time}:00`).toISOString(),
    interviewType: form.interviewType,
    meetingLink:   form.interviewType === 'online' ? form.meetingLink : undefined,
    location:      form.interviewType === 'onsite' ? form.location   : undefined,
  }
  isForceScheduling.value = true
  try {
    await store.forceSchedule(Number(selectedItem.value.candidateId), payload)
    toast.success('Thành công', `Đã đặt lịch phỏng vấn hộ cho ${selectedItem.value.candidate?.name}.`)
    showForceScheduleModal.value = false
    await fetchData()
  } catch (e: any) {
    toast.error('Lỗi', e?.response?.data?.message ?? 'Không thể đặt lịch phỏng vấn.')
  } finally {
    isForceScheduling.value = false
  }
}

// ── Helpers ───────────────────────────────────────────────
function formatSlotTime(start?: string, end?: string): string {
  if (!start) return ''
  const d    = new Date(start)
  const days = ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7']
  const day  = days[d.getDay()]
  const date = `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}`
  const s    = `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
  const e    = end ? (() => {
    const ed = new Date(end)
    return `${String(ed.getHours()).padStart(2, '0')}:${String(ed.getMinutes()).padStart(2, '0')}`
  })() : ''
  return `${day}, ${date} – ${s}–${e}`
}

function mapStatus(status: string): 'pending' | 'confirmed' | 'expired' {
  if (status === 'scheduled') return 'pending'
  if (status === 'confirmed') return 'confirmed'
  return 'expired'
}

// ── Table data ────────────────────────────────────────────
const mappedProposals = computed(() =>
  store.schedules.map(s => ({
    id:             String(s.id),
    candidateId:    String(s.applicationId),
    candidate:      { name: s.candidateName, position: s.candidateEmail },
    roundLabel:     `Vòng ${s.roundNumber} – ${s.roundName}`,
    status:         mapStatus(s.status),
    scheduleStatus: s.status,
    sentSlots:      (s.sentSlots ?? []).map(slot => formatSlotTime(slot.startTime, slot.endTime)),
    rawSentSlots:      s.sentSlots ?? [],
    confirmedSlot:     s.slotStartTime ? formatSlotTime(s.slotStartTime, s.slotEndTime ?? undefined) : null,
    applicationStatus: s.applicationStatus ?? '',
  }))
)


</script>

<style scoped>
.proposal-page {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

/* ── Breadcrumb ──────────────────────────────────────────── */
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.875rem;
  font-weight: 500;
  font-family: Manrope, sans-serif;
}

.breadcrumb__link {
  color: #6b7280;
  text-decoration: none;
  cursor: pointer;
  transition: color 0.15s;
}

.breadcrumb__link:hover {
  color: #4b9af6;
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

/* ── Header ──────────────────────────────────────────────── */
.proposal-page__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.proposal-page__title-group {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.proposal-page__meta {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  margin-bottom: 0.25rem;
}

.proposal-page__meta-icon {
  font-size: 1rem;
  color: #4b9af6;
}

.proposal-page__job-name {
  font-size: 0.875rem;
  font-weight: 600;
  color: #374151;
  font-family: Manrope, sans-serif;
}

.proposal-page__meta-divider {
  color: #cbd5e1;
  font-size: 1rem;
  margin: 0 0.125rem;
}

.proposal-page__round-badge {
  display: inline-flex;
  align-items: center;
  padding: 0.125rem 0.625rem;
  background: #eff6ff;
  color: #2563eb;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 700;
  font-family: Manrope, sans-serif;
  letter-spacing: 0.01em;
}

.proposal-page__title {
  font-size: 1.875rem;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.025em;
  font-family: Manrope, sans-serif;
}

.proposal-page__subtitle {
  color: #64748b;
  margin-top: 0.25rem;
  font-size: 1rem;
  font-family: Manrope, sans-serif;
}

.proposal-page__actions {
  display: flex;
  gap: 0.75rem;
}

.btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border-radius: 0.75rem;
  font-size: 1rem;
  font-weight: 700;
  font-family: Manrope, sans-serif;
  cursor: pointer;
  transition: all 0.2s;
}

.btn .material-symbols-outlined { font-size: 1.125rem; }

.btn--primary {
  border: none;
  background: #4b9af6;
  color: #fff;
  box-shadow: 0 4px 12px rgb(75 154 246 / 0.2);
}

.btn--primary:hover {
  box-shadow: 0 8px 20px rgb(75 154 246 / 0.3);
}
</style>
