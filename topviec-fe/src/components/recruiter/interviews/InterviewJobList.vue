<template>
  <div>
    <!-- Loading -->
    <div v-if="isLoading" class="list-state">
      <span class="material-symbols-outlined spinning">autorenew</span>
      <p>Đang tải...</p>
    </div>

    <!-- Empty -->
    <div v-else-if="jobPostings.length === 0" class="list-state list-state--empty">
      <span class="material-symbols-outlined">inbox</span>
      <p>Không có tin tuyển dụng nào</p>
    </div>

    <!-- Table rows -->
    <div v-else class="job-list">
      <InterviewJobRow
        v-for="job in jobPostings"
        :key="job.id"
        :job="job"
        :is-active="activeId === job.id"
        @click="activeId = job.id"
        @view-detail="handleViewDetail(job.id)"
        @setup-rounds="handleSetupRounds(job.id)"
        @finish-interview="handleFinishInterview(job.id)"
        @view-job-detail="handleViewJobDetail(job.id)"
      />
    </div>

    <!-- ── Hoàn thành tuyển dụng Modal ────────────────────── -->
    <GlobalModal
      :visible="isCompleteVisible"
      title="Hoàn thành quy trình tuyển dụng"
      subtitle="Chốt danh sách ứng viên và đóng tin tuyển dụng"
      icon="verified"
      confirm-text="Xác nhận hoàn thành"
      confirm-icon="check_circle"
      :loading="isCompleteLoading"
      max-width="md"
      @close="isCompleteVisible = false"
      @confirm="confirmCompleteRecruitment"
    >
      <div class="complete-recruitment">
        <div class="complete-alert">
          <span class="material-symbols-outlined">warning</span>
          <div class="complete-alert__content">
            <p class="complete-alert__title">Hành động này không thể hoàn tác!</p>
            <p class="complete-alert__desc">Các ứng viên được chọn sẽ là <strong>HIRED</strong>. Toàn bộ ứng viên còn lại sẽ bị <strong>REJECTED</strong> và Tin tuyển dụng sẽ đóng lại.</p>
          </div>
        </div>

        <div class="offered-list">
          <label class="offered-list__label">Danh sách ứng viên PASS vòng cuối (OFFERED)</label>
          <div v-if="offeredCandidates.length === 0" class="offered-list__empty">
            Chưa có ứng viên nào ở trạng thái OFFERED.
          </div>
          <div v-else class="offered-list__items">
            <label
              v-for="app in offeredCandidates"
              :key="app.id"
              class="offered-item"
              :class="{ 'offered-item--selected': selectedHireIds.includes(app.id) }"
            >
              <input
                type="checkbox"
                :value="app.id"
                v-model="selectedHireIds"
                class="offered-item__checkbox"
              />
              <div class="offered-item__info">
                <p class="offered-item__name">{{ app.candidateName }}</p>
                <p class="offered-item__email">{{ app.candidateEmail }}</p>
              </div>
              <span v-if="selectedHireIds.includes(app.id)" class="material-symbols-outlined offered-item__check">check_circle</span>
            </label>
          </div>
        </div>
      </div>
    </GlobalModal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import InterviewJobRow from './InterviewJobRow.vue'
import { employerJobPostingService } from '@/services/employerJobPosting.service'
import employerInterviewService from '@/services/employerInterview.service'
import employerApplicationService from '@/services/employerApplication.service'
import { JobPostingStatus } from '@/constants/jobPosting.constants'
import type { ResJobPostingDetail } from '@/types/jobPosting.types'
import type { ResEmployerApplicationDTO } from '@/types/employerApplication.types'
import GlobalModal from '@/components/ui/GlobalModal.vue'
import { useToast } from '@/composables/useToast'

type FilterTab = 'interviewing' | 'completed'

type IconVariant = 'blue-light' | 'blue-solid' | 'orange' | 'purple' | 'green' | 'rose'
const ICON_VARIANTS: IconVariant[] = ['blue-light', 'blue-solid', 'orange', 'purple', 'green', 'rose']

const STATUS_MAP: Record<FilterTab, JobPostingStatus> = {
  interviewing: JobPostingStatus.INTERVIEWING,
  completed: JobPostingStatus.COMPLETED,
}

const props = defineProps<{
  statusFilter: FilterTab
  keyword: string
  currentPage: number
}>()

const emit = defineEmits<{
  'update:total': [total: number]
}>()

const router = useRouter()

const rawJobs = ref<ResJobPostingDetail[]>([])
const isLoading = ref(false)
const activeId = ref<number | null>(null)
const toast = useToast()

// ── Complete Recruitment State ─────────────────────────────
const isCompleteVisible  = ref(false)
const isCompleteLoading  = ref(false)
const currentJobId       = ref<number | null>(null)
const offeredCandidates  = ref<ResEmployerApplicationDTO[]>([])
const selectedHireIds    = ref<number[]>([])

const jobPostings = computed(() =>
  rawJobs.value.map((job, index) => ({
    id: job.id,
    title: job.title,
    department: job.industry?.name ?? '',
    level: job.level?.name ?? '',
    candidateCount: job.applicationCount,
    icon: 'work',
    iconVariant: ICON_VARIANTS[index % ICON_VARIANTS.length],
    status: job.status,
    interviewRoundsCount: job.interviewRoundsCount ?? 0,
  }))
)

async function fetchJobs() {
  isLoading.value = true
  try {
    const res = await employerJobPostingService.getList({
      status: STATUS_MAP[props.statusFilter],
      keyword: props.keyword || undefined,
      page: props.currentPage,
      size: 10,
    })
    rawJobs.value = res.result
    emit('update:total', res.meta.totals)
    if (res.result.length > 0) activeId.value = res.result[0].id
    else activeId.value = null
  } catch (err) {
    console.error('Failed to fetch jobs:', err)
  } finally {
    isLoading.value = false
  }
}

watch(
  [() => props.statusFilter, () => props.keyword, () => props.currentPage],
  fetchJobs,
  { immediate: true }
)

function handleViewDetail(jobId: number) {
  router.push({ name: 'recruiter-job-interview-setup', params: { id: jobId } })
}

function handleSetupRounds(jobId: number) {
  router.push({ name: 'recruiter-job-interview-stages', params: { id: jobId } })
}

function handleViewJobDetail(jobId: number) {
  router.push({ name: 'recruiter-jobs-detail', params: { id: jobId } })
}

async function handleFinishInterview(jobId: number) {
  currentJobId.value = jobId
  isCompleteLoading.value = true
  try {
    const res = await employerApplicationService.getApplicationsByJobPost(jobId, { status: 'offered' })
    offeredCandidates.value = res.result
    selectedHireIds.value   = res.result.map(i => i.id)
    isCompleteVisible.value = true
  } catch (err: any) {
    toast.error('Lỗi', 'Không thể lấy danh sách ứng viên offered.')
  } finally {
    isCompleteLoading.value = false
  }
}

async function confirmCompleteRecruitment() {
  if (offeredCandidates.value.length > 0 && selectedHireIds.value.length === 0) {
    toast.warning('Chú ý', 'Vui lòng chọn ít nhất 1 ứng viên để tuyển dụng.')
    return
  }

  isCompleteLoading.value = true
  try {
    await employerInterviewService.completeRecruitment(currentJobId.value!, {
      applicationIds: selectedHireIds.value
    })
    toast.success('Thành công!', 'Đã hoàn thành quy trình tuyển dụng.')
    isCompleteVisible.value = false
    await fetchJobs()
  } catch (err: any) {
    toast.error('Lỗi', err?.response?.data?.message ?? 'Không thể hoàn thành tuyển dụng.')
  } finally {
    isCompleteLoading.value = false
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Manrope:wght@700&display=swap');

.job-list {
  background: #fff;
}

/* Loading / empty state */
.list-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 3rem 1.5rem;
  color: #94a3b8;
  font-size: 0.875rem;
  font-family: 'Manrope', sans-serif;
}
.list-state .material-symbols-outlined { font-size: 2.5rem; }
.list-state--empty .material-symbols-outlined { color: #cbd5e1; }

@keyframes spin { to { transform: rotate(360deg); } }
.spinning { animation: spin 1s linear infinite; }

/* Modal styles */
.complete-recruitment { display: flex; flex-direction: column; gap: 1.5rem; padding: 0.25rem 0; width: 100%; box-sizing: border-box; }
.complete-alert {
  display: flex; gap: 1rem; padding: 1.25rem;
  background: #fff1f2; border: 1px solid #fecaca; border-radius: 1rem;
  color: #991b1b;
}
.complete-alert .material-symbols-outlined { color: #dc2626; font-size: 1.5rem; }
.complete-alert__title { font-weight: 800; font-size: 0.875rem; margin-bottom: 0.25rem; }
.complete-alert__desc { font-size: 0.8125rem; line-height: 1.5; opacity: 0.9; }

.offered-list { display: flex; flex-direction: column; gap: 0.75rem; }
.offered-list__label { font-size: 0.75rem; font-weight: 700; color: #64748b; text-transform: uppercase; letter-spacing: 0.05em; }
.offered-list__empty { padding: 2rem; text-align: center; background: #f8fafc; border-radius: 1rem; color: #94a3b8; font-size: 0.875rem; border: 2px dashed #e2e8f0; }

.offered-list__items { display: flex; flex-direction: column; gap: 0.5rem; max-height: 260px; overflow-y: auto; padding-right: 4px; }
.offered-item {
  display: flex; align-items: center; gap: 1rem; padding: 1rem;
  background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 1rem;
  cursor: pointer; transition: all 0.2s;
}
.offered-item:hover { border-color: #cbd5e1; background: #f1f5f9; }
.offered-item--selected { border-color: #4b9af6; background: #eff6ff; }
.offered-item__checkbox { width: 1.25rem; height: 1.25rem; accent-color: #4b9af6; cursor: pointer; }
.offered-item__info { flex: 1; }
.offered-item__name { font-size: 0.875rem; font-weight: 700; color: #1e293b; }
.offered-item__email { font-size: 0.75rem; color: #64748b; }
.offered-item__check { color: #4b9af6; font-size: 1.25rem; }
</style>
