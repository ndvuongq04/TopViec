<template>
  <div class="job-postings-page">

    <!-- Page header -->
    <div class="page-header">
      <div>
        <h2 class="page-header__title">Tin tuyển dụng</h2>
        <p class="page-header__subtitle">Quản lý và theo dõi hiệu suất các vị trí đang tuyển.</p>
      </div>
      <button class="btn-export" @click="handleExport">
        <span class="material-symbols-outlined icon-xl">file_download</span>
        Xuất báo cáo
      </button>
    </div>

    <!-- Stats -->
    <JobPostingStatsGrid :stats="stats" />

    <!-- Listing panel -->
    <div class="listing-panel">
      <JobPostingFilters
        v-model="activeFilter"
        v-model:searchValue="searchValue"
        @search="handleSearch"
      />
      <JobPostingTable
        :jobs="jobs"
        @view="handleView"
        @edit="handleEdit"
        @copy="handleCopy"
        @submit="handleSubmit"
        @pause="handlePause"
        @resume="handleResume"
        @extend="handleExtend"
        @refresh="handleRefresh"
        @interview="handleInterview"
        @close="handleClose"
        @delete="handleDelete"
        @restore="handleRestore"
        @applications="handleViewApplications"
        @assign="handleAssign"
      />
      <JobPostingPagination
        v-model:currentPage="currentPage"
        :total="totalJobs"
        :per-page="8"
      />
    </div>

    <!-- ── Extend Job Modal ─────────────────────────── -->
    <GlobalModal
      :visible="isExtendModalVisible"
      title="Gia hạn tin tuyển dụng"
      :subtitle="`Tin: ${extendingJob?.title}`"
      variant="primary"
      icon="update"
      confirm-text="Xác nhận gia hạn"
      confirm-icon="check_circle"
      :loading="isExtendLoading"
      @close="isExtendModalVisible = false"
      @confirm="confirmExtend"
    >
      <div class="space-y-4">
        <p class="text-slate-600 dark:text-slate-400">
          Hãy chọn hạn nộp hồ sơ mới cho tin tuyển dụng này. 
          Tin sẽ được gia hạn từ thời điểm hiện tại.
        </p>
        
        <div class="flex flex-col gap-2">
          <label for="new-deadline" class="text-sm font-bold text-slate-700 dark:text-slate-300">
            Hạn nộp hồ sơ mới
          </label>
          <input
            id="new-deadline"
            type="date"
            v-model="newDeadline"
            class="w-full px-4 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-primary/20 focus:border-primary outline-none transition-all dark:bg-slate-800 dark:border-slate-700"
            :min="minDate"
          />
        </div>
      </div>
    </GlobalModal>

    <!-- ── Submit for Approval Modal ──────────────────── -->
    <GlobalModal
      :visible="isSubmitModalVisible"
      title="Gửi tin chờ duyệt"
      :subtitle="`Tin: ${submittingJob?.title}`"
      variant="primary"
      icon="send"
      confirm-text="Gửi duyệt ngay"
      confirm-icon="check_circle"
      :loading="isSubmitLoading"
      @close="isSubmitModalVisible = false"
      @confirm="confirmSubmit"
    >
      <p class="text-slate-600 dark:text-slate-400">
        Tin tuyển dụng sẽ được chuyển sang trạng thái <strong>Chờ duyệt</strong>. 
        Quản trị viên sẽ sớm kiểm tra và phê duyệt yêu cầu của bạn.
      </p>
    </GlobalModal>

    <!-- ── Delete Job Modal ─────────────────────────── -->
    <GlobalModal
      :visible="isDeleteModalVisible"
      title="Xóa tin tuyển dụng"
      :subtitle="`Tin: ${deletingJob?.title}`"
      variant="primary"
      icon="delete"
      confirm-text="Xác nhận xóa"
      confirm-icon="delete_forever"
      :loading="isDeleteLoading"
      @close="isDeleteModalVisible = false"
      @confirm="confirmDelete"
    >
      <div class="space-y-3">
        <p class="text-slate-600 dark:text-slate-400">
          Bạn có chắc chắn muốn xóa tin tuyển dụng này không? 
          Hành động này sẽ chuyển tin vào thùng rác.
        </p>
        <div class="p-3 bg-blue-50 border border-blue-100 rounded-lg flex gap-3">
            <span class="material-symbols-outlined text-blue-500">info</span>
            <p class="text-xs text-blue-700 font-medium">
                Lưu ý: Chỉ có bộ phận quản trị hoặc bạn mới có thể khôi phục tin này sau khi xóa.
            </p>
        </div>
      </div>
    </GlobalModal>

    <!-- ── Start Interviewing Modal ───────────────────────── -->
    <GlobalModal
      :visible="isInterviewModalVisible"
      title="Bắt đầu phỏng vấn"
      :subtitle="`Tin: ${interviewingJob?.title}`"
      variant="primary"
      icon="groups"
      confirm-text="Bắt đầu phỏng vấn"
      confirm-icon="play_arrow"
      :loading="isInterviewLoading"
      @close="isInterviewModalVisible = false"
      @confirm="confirmStartInterviewing"
    >
      <div class="space-y-3">
        <p class="text-slate-600 dark:text-slate-400">
          Tất cả ứng viên đã qua CV sẽ được chuyển sang trạng thái <strong>Đang phỏng vấn</strong>.
          Tin tuyển dụng cũng sẽ chuyển sang giai đoạn phỏng vấn.
        </p>
        <div class="p-3 bg-blue-50 border border-blue-100 rounded-lg flex gap-3">
          <span class="material-symbols-outlined text-blue-500">info</span>
          <p class="text-xs text-blue-700 font-medium">
            Lưu ý: Hành động này không thể hoàn tác. Hãy đảm bảo bạn đã hoàn thành vòng sàng lọc CV.
          </p>
        </div>
      </div>
    </GlobalModal>

    <!-- ── Assign Recruiter Modal ─────────────────────────── -->
    <JobAssignmentModal
      ref="assignModalRef"
      :visible="isAssignModalVisible"
      :job="assigningJob"
      @close="isAssignModalVisible = false"
      @assigned="handleAssignSuccess"
    />

  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { employerJobPostingService } from '@/services/employerJobPosting.service'
import employerInterviewService from '@/services/employerInterview.service'
import { useToast } from '@/composables/useToast'
import { useEmployerJobPostingStore } from '@/stores/employerJobPosting.store'
import { useEmployerJobAssignmentStore } from '@/stores/employerJobAssignment.store'
import { JobPostingStatus } from '@/constants/jobPosting.constants'
import type { ResJobPostingDetail } from '@/types/jobPosting.types'
import JobPostingStatsGrid  from '@/components/recruiter/jobs/JobPostingStatsGrid.vue'
import JobPostingFilters    from '@/components/recruiter/jobs/JobPostingFilters.vue'
import JobPostingTable      from '@/components/recruiter/jobs/JobPostingTable.vue'
import JobPostingPagination from '@/components/recruiter/jobs/JobPostingPagination.vue'
import JobAssignmentModal   from '@/components/recruiter/jobs/JobAssignmentModal.vue'
import GlobalModal          from '@/components/ui/GlobalModal.vue'
import type { JobPostingFilterTab } from '@/components/recruiter/jobs/JobPostingFilters.vue'
import type { JobPostingRow, JobPostingStats } from '@/types/employerJobPosting.types'

// ── State ────────────────────────────────────────────────
const activeFilter = ref<JobPostingFilterTab>('all')
const currentPage  = ref(0)
const searchValue  = ref('')
const router = useRouter()
const toast  = useToast()
const store  = useEmployerJobPostingStore()
const assignmentStore = useEmployerJobAssignmentStore()

const jobs      = ref<JobPostingRow[]>([])
const totalJobs = ref(0)
const stats     = computed<JobPostingStats>(() => ({
  total:    store.jobStatistics?.totalJobPosts ?? 0,
  active:   store.jobStatistics?.activeJobPosts ?? 0,
  pending:  store.jobStatistics?.pendingJobPosts ?? 0,
  expiring: store.jobStatistics?.expiringJobPosts ?? 0,
}))

// Extend Modal State
const isExtendModalVisible = ref(false)
const isExtendLoading      = ref(false)
const extendingJob         = ref<{ id: number; title: string } | null>(null)
const newDeadline          = ref('')
const minDate              = ref(new Date().toISOString().split('T')[0])

// Submit Modal State
const isSubmitModalVisible = ref(false)
const isSubmitLoading      = ref(false)
const submittingJob        = ref<{ id: number; title: string } | null>(null)

// Delete Modal State
const isDeleteModalVisible = ref(false)
const isDeleteLoading      = ref(false)
const deletingJob          = ref<{ id: number; title: string } | null>(null)

// Start Interviewing Modal State
const isInterviewModalVisible = ref(false)
const isInterviewLoading      = ref(false)
const interviewingJob         = ref<{ id: number; title: string } | null>(null)

// Assign Modal State
const isAssignModalVisible = ref(false)
const assigningJob         = ref<{ id: number; title: string; code?: string } | null>(null)
const assignModalRef       = ref<any>(null)

// ── Status mapping ───────────────────────────────────────
const tabToStatus: Partial<Record<JobPostingFilterTab, JobPostingStatus>> = {
  active:       JobPostingStatus.PUBLISHED,
  pending:      JobPostingStatus.PENDING_APPROVAL,
  draft:        JobPostingStatus.DRAFT,
  hidden:       JobPostingStatus.HIDDEN,
  closed:       JobPostingStatus.CLOSED,
  expired:      JobPostingStatus.EXPIRED,
  interviewing: JobPostingStatus.INTERVIEWING,
  completed:    JobPostingStatus.COMPLETED,
  deleted:      JobPostingStatus.DELETED,
}

function mapStatus(apiStatus: string): JobPostingRow['status'] {
  const map: Record<string, JobPostingRow['status']> = {
    [JobPostingStatus.PUBLISHED]:        'active',
    [JobPostingStatus.RENEWED]:          'active',
    [JobPostingStatus.SCHEDULED]:        'pending',
    [JobPostingStatus.PENDING_APPROVAL]: 'pending',
    [JobPostingStatus.DRAFT]:            'draft',
    [JobPostingStatus.HIDDEN]:           'hidden',
    [JobPostingStatus.PAUSED]:           'paused',
    [JobPostingStatus.CLOSED]:           'closed',
    [JobPostingStatus.EXPIRED]:          'expired',
    [JobPostingStatus.REJECTED]:         'rejected',
    [JobPostingStatus.INTERVIEWING]:     'interviewing',
    [JobPostingStatus.COMPLETED]:        'completed',
    [JobPostingStatus.DELETED]:          'deleted',
  }
  return map[apiStatus] ?? 'draft'
}

function mapToRow(job: ResJobPostingDetail): JobPostingRow {
  const deadline = job.deadline ? new Date(job.deadline) : null
  const now      = new Date()
  const daysLeft = deadline
    ? Math.ceil((deadline.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
    : undefined
  const uiStatus = mapStatus(job.status)

  return {
    id:         job.id,
    title:      job.title,
    code:       `JOB-${String(job.id).padStart(5, '0')}`,
    status:     daysLeft !== undefined && daysLeft <= 3 && uiStatus === 'active' ? 'expiring' : uiStatus,
    postedAt:   job.publishedAt
      ? new Date(job.publishedAt).toLocaleDateString('vi-VN')
      : new Date(job.createdAt).toLocaleDateString('vi-VN'),
    deadline:   deadline ? deadline.toLocaleDateString('vi-VN') : undefined,
    daysLeft:   daysLeft !== undefined && daysLeft > 0 ? daysLeft : undefined,
    views:      job.viewCount,
    applicants: job.applicationCount ?? 0,
    editCount:  job.editCount,
    isUrgent:   job.isUrgent,
    isFeatured: job.isFeatured,
    isHot:      job.isHot,
    rawStatus:  job.status,
    assignedRecruiter: (job as any).assignedRecruiter ?? null,
  }
}

// ── API calls ────────────────────────────────────────────
async function fetchJobs() {
  const res = await employerJobPostingService.getList({
    status:  tabToStatus[activeFilter.value],
    keyword: searchValue.value || undefined,
    page:    currentPage.value,
    size:    8,
  })
  jobs.value      = res.result.map(mapToRow)
  totalJobs.value = res.meta.totals
}

async function fetchStats() {
  await store.fetchStatistics()
}

// ── Watchers ─────────────────────────────────────────────
watch(activeFilter, () => {
  if (currentPage.value !== 0) {
    currentPage.value = 0 // triggers currentPage watcher → fetchJobs
  } else {
    fetchJobs()
  }
})

function handleSearch() {
  if (currentPage.value !== 0) {
    currentPage.value = 0
  } else {
    fetchJobs()
  }
}

watch(currentPage, fetchJobs)

onMounted(() => {
  fetchJobs()
  fetchStats()
})

// ── Handlers ─────────────────────────────────────────────
const handleExport = () => console.log('export')
const handleView   = (id: number) => router.push({ name: 'recruiter-jobs-detail', params: { id } })
const handleEdit   = (id: number) => router.push({ name: 'recruiter-jobs-edit', params: { id } })
const handleCopy   = (id: number) => console.log('copy', id)

const handlePause = async (id: number) => {
  try {
    const updated = await employerJobPostingService.pauseJob(id)
    const idx = jobs.value.findIndex(j => j.id === id)
    if (idx !== -1) {
      jobs.value[idx] = mapToRow(updated)
    }
    toast.success('Đã tạm dừng!', `Tin tuyển dụng "${updated.title}" đã được tạm dừng.`)
    fetchStats() // Update stats too
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Không thể tạm dừng tin. Vui lòng thử lại.'
    toast.error('Lỗi', msg)
  }
}

const handleResume = async (id: number) => {
  try {
    const updated = await employerJobPostingService.resumeJob(id)
    const idx = jobs.value.findIndex(j => j.id === id)
    if (idx !== -1) {
      jobs.value[idx] = mapToRow(updated)
    }
    toast.success('Đã tiếp tục!', `Tin tuyển dụng "${updated.title}" đã được hiển thị trở lại.`)
    fetchStats()
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Không thể tiếp tục đăng tin. Vui lòng thử lại.'
    toast.error('Lỗi', msg)
  }
}

const handleExtend = (id: number) => {
  const job = jobs.value.find(j => j.id === id)
  if (job) {
    extendingJob.value = { id: job.id, title: job.title }
    // Default to today + 30 days
    const date = new Date()
    date.setDate(date.getDate() + 30)
    newDeadline.value = date.toISOString().split('T')[0]
    isExtendModalVisible.value = true
  }
}

const handleRefresh = async (id: number) => {
  // We can use a generic loading state or specific one. 
  // For simplicity using a toast + local update.
  try {
    const updated = await employerJobPostingService.refreshJob(id)
    const idx = jobs.value.findIndex(j => j.id === id)
    if (idx !== -1) {
      jobs.value[idx] = mapToRow(updated)
    }
    toast.success('Đã làm mới!', `Tin tuyển dụng "${updated.title}" đã được đẩy lên đầu danh sách.`)
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Không thể làm mới tin. Vui lòng thử lại.'
    toast.error('Lỗi', msg)
  }
}

const confirmExtend = async () => {
  if (!extendingJob.value || !newDeadline.value) return

  isExtendLoading.value = true
  try {
    const id = extendingJob.value.id
    const updated = await employerJobPostingService.extendJob(id, {
      newDeadline: new Date(newDeadline.value).toISOString(),
    })

    const idx = jobs.value.findIndex(j => j.id === id)
    if (idx !== -1) {
      jobs.value[idx] = mapToRow(updated)
    }

    toast.success('Gia hạn thành công!', `Tin tuyển dụng "${updated.title}" đã được gia hạn đến ngày ${new Date(newDeadline.value).toLocaleDateString('vi-VN')}.`)
    fetchStats()
    isExtendModalVisible.value = false
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Không thể gia hạn tin. Vui lòng thử lại.'
    toast.error('Lỗi', msg)
  } finally {
    isExtendLoading.value = false
  }
}

const handleSubmit = (id: number) => {
  const job = jobs.value.find(j => j.id === id)
  if (job) {
    submittingJob.value = { id: job.id, title: job.title }
    isSubmitModalVisible.value = true
  }
}

const confirmSubmit = async () => {
  if (!submittingJob.value) return

  isSubmitLoading.value = true
  try {
    const id = submittingJob.value.id
    const updated = await employerJobPostingService.pendingApproval(id)

    const idx = jobs.value.findIndex(j => j.id === id)
    if (idx !== -1) {
      jobs.value[idx] = mapToRow(updated)
    }

    toast.success('Gửi duyệt thành công!', `Tin tuyển dụng "${updated.title}" đã được chuyển sang hàng đợi duyệt.`)
    fetchStats()
    isSubmitModalVisible.value = false
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Không thể gửi duyệt tin. Vui lòng thử lại.'
    toast.error('Lỗi', msg)
  } finally {
    isSubmitLoading.value = false
  }
}

const handleClose = async (id: number) => {
  try {
    const updated = await employerJobPostingService.closeJob(id)
    const idx = jobs.value.findIndex(j => j.id === id)
    if (idx !== -1) {
      jobs.value[idx] = mapToRow(updated)
    }
    toast.success('Đã đóng tin!', `Tin tuyển dụng "${updated.title}" đã được đóng thành công.`)
    fetchStats()
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Không thể đóng tin. Vui lòng thử lại.'
    toast.error('Lỗi', msg)
  }
}

const handleDelete = (id: number) => {
  const job = jobs.value.find(j => j.id === id)
  if (job) {
    deletingJob.value = { id: job.id, title: job.title }
    isDeleteModalVisible.value = true
  }
}

const confirmDelete = async () => {
  if (!deletingJob.value) return

  isDeleteLoading.value = true
  try {
    const id = deletingJob.value.id
    await employerJobPostingService.deleteJob(id)

    // Remove from local list
    jobs.value = jobs.value.filter(j => j.id !== id)
    
    toast.success('Đã xóa tin!', `Tin tuyển dụng "${deletingJob.value.title}" đã được xóa thành công.`)
    fetchStats()
    isDeleteModalVisible.value = false
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Không thể xóa tin. Vui lòng thử lại.'
    toast.error('Lỗi', msg)
  } finally {
    isDeleteLoading.value = false
  }
}

const handleRestore = async (id: number) => {
  try {
    const updated = await employerJobPostingService.restoreJob(id)
    
    // Update local jobs (it will disappear from 'deleted' tab if active)
    if (activeFilter.value === 'deleted') {
      jobs.value = jobs.value.filter(j => j.id !== id)
    } else {
      const idx = jobs.value.findIndex(j => j.id === id)
      if (idx !== -1) {
        jobs.value[idx] = mapToRow(updated)
      }
    }

    toast.success('Khôi phục thành công!', `Tin tuyển dụng "${updated.title}" đã được khôi phục về trạng thái Nháp.`)
    fetchStats()
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Không thể khôi phục tin. Vui lòng thử lại.'
    toast.error('Lỗi', msg)
  }
}
const handleViewApplications = (id: number) => {
  router.push({ name: 'recruiter-job-applications', params: { id } })
}

const handleInterview = (id: number) => {
  const job = jobs.value.find(j => j.id === id)
  if (!job) return

  interviewingJob.value = { id: job.id, title: job.title }
  isInterviewModalVisible.value = true
}

const confirmStartInterviewing = async () => {
  if (!interviewingJob.value) return

  isInterviewLoading.value = true
  try {
    const id = interviewingJob.value.id
    await employerInterviewService.startInterviewing(id)

    const idx = jobs.value.findIndex(j => j.id === id)
    if (idx !== -1) {
      jobs.value[idx] = { ...jobs.value[idx], status: 'interviewing' }
    }

    toast.success('Bắt đầu phỏng vấn!', `Tin tuyển dụng "${interviewingJob.value.title}" đã chuyển sang giai đoạn phỏng vấn.`)
    fetchStats()
    isInterviewModalVisible.value = false
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Không thể bắt đầu phỏng vấn. Vui lòng thử lại.'
    toast.error('Lỗi', msg)
  } finally {
    isInterviewLoading.value = false
  }
}

const handleAssign = (id: number) => {
  const job = jobs.value.find(j => j.id === id)
  if (!job) return
  assigningJob.value = {
    id: job.id,
    title: job.title,
    code: job.code,
    assignedRecruiter: (job as any).assignedRecruiter ?? null,
  }
  isAssignModalVisible.value = true
}

/**
 * Gọi sau khi JobAssignmentModal emit 'assigned'
 * Reload lại trang hiện tại để cập nhật cột assignedRecruiter
 */
async function handleAssignSuccess() {
  isAssignModalVisible.value = false
  await fetchJobs()
}
</script>

<style scoped>
.job-postings-page {
  /* Design system tokens missing from Tailwind */
  --color-surface: #ffffff;
  --color-on-surface: #0f172a;
  --color-on-surface-muted: #64748b;
  --color-border: #e2e8f0;
  --color-border-light: #f1f5f9;
  --color-primary: #137fec;
  --color-primary-text: #1e40af;
  --color-primary-light: #dbeafe;
  --color-tertiary: #f59e0b;
  --color-tertiary-text: #b45309;
  --color-tertiary-light: #fef3c7;
  --color-error: #ef4444;
  --color-error-text: #b91c1c;
  --color-error-light: #fee2e2;

  --radius-full: 9999px;
  --radius-lg: 1rem;
  --radius-md: 0.75rem;
  --radius-sm: 0.5rem;


  display: flex;
  flex-direction: column;
  gap: 2rem;
  min-height: 100vh;
 
  font-family: 'Manrope', sans-serif;
}

.page-header { display: flex; align-items: flex-end; justify-content: space-between; }
.page-header__title { font-size: 1.875rem; font-weight: 800; letter-spacing: -0.02em; margin: 0 0 0.25rem; }
.page-header__subtitle { color: var(--color-on-surface-muted); margin: 0; font-size: 1rem; }

.btn-export {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.625rem 1.25rem;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  font-family: inherit;
  font-weight: 800;
  font-size: 1rem;
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: background 0.15s;
}
.btn-export:hover { background: #f8fafc; }

.listing-panel {
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  border: 1px solid var(--color-border-light);
  overflow: hidden;
}

.icon-xl { font-size: 1.25rem !important; }
</style>
