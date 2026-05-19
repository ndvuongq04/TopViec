<template>
  <div class="space-y-8">
    <!-- Breadcrumb -->
    <Breadcrumb :items="breadcrumbItems" :hide-home="true" />

    <!-- Page Header -->
    <div class="flex justify-between items-start">
      <div class="flex flex-col gap-1">
        <div class="flex items-center gap-3">
          <h1 class="page-title">{{ jobTitle || '...' }}</h1>
          <span v-if="jobStatus" :class="['job-status-badge', jobStatusBadgeClass]">
            {{ jobStatusLabel }}
          </span>
        </div>
        <p class="page-subtitle">Mã tin: #{{ jobPostId }}</p>
      </div>

      <div class="header-actions">
        <button class="btn-outline">
          <span class="material-symbols-outlined">download</span>
          Xuất báo cáo
        </button>
      </div>
    </div>

    <div class="filters-table-container">
      <!-- Filters -->
      <CandidateFiltersBar
        v-model:activeTab="filters.activeTab"
        v-model:searchValue="filters.search"
        :tabs="tabs"
      />

      <!-- Loading state -->
      <div v-if="loading" class="loading-state">
        <span class="material-symbols-outlined loading-spin">progress_activity</span>
        <p>Đang tải dữ liệu...</p>
      </div>

      <!-- Table -->
      <CandidateTable
        v-else
        :candidates="candidates"
        :meta="meta"
        @page-change="onPageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import CandidateFiltersBar from '@/components/recruiter/application/Candidatefiltersbar.vue'
import CandidateTable from '@/components/recruiter/application/Candidatetable.vue'
import Breadcrumb from '@/components/ui/Breadcrumb.vue'
import employerApplicationService from '@/services/employerApplication.service'
import { publicJobPostingService } from '@/services/jobPosting.service'
import type { ResEmployerApplicationDTO } from '@/types/employerApplication.types'
import type { PaginationMeta } from '@/types/common.types'
import { JOB_POSTING_STATUS_LABELS, JOB_POSTING_STATUS_BADGE, type JobPostingStatus } from '@/constants/jobPosting.constants'

const route = useRoute()
const jobPostId = computed(() => Number(route.params.id))

const loading    = ref(false)
const candidates = ref<ResEmployerApplicationDTO[]>([])
const meta       = ref<PaginationMeta>({ page: 0, pageSize: 10, pages: 0, totals: 0 })
const currentPage = ref(0)
const jobTitle   = ref('')
const jobStatus  = ref<JobPostingStatus | null>(null)

const jobStatusLabel      = computed(() => jobStatus.value ? JOB_POSTING_STATUS_LABELS[jobStatus.value] : '')
const jobStatusBadgeClass = computed(() => jobStatus.value ? JOB_POSTING_STATUS_BADGE[jobStatus.value] : '')

const breadcrumbItems = [
  { label: 'Quản lý tin tuyển dụng', to: '/recruiter/jobs' },
  { label: 'Danh sách ứng viên' },
]

const tabs = ref([
  { label: 'Tất cả',        value: '',                count: 0 },
  { label: 'Chờ xem',       value: 'pending',         count: 0 },
  { label: 'Đã xem',        value: 'seen',            count: 0 },
  { label: 'Cân nhắc',      value: 'considering',     count: 0 },
  { label: 'Đạt vòng CV',   value: 'cv_passed',       count: 0 },
  { label: 'Phỏng vấn',     value: 'interviewing',    count: 0 },
  { label: 'Từ chối',       value: 'rejected',        count: 0 },
])

const filters = ref({
  activeTab: '',
  search:    '',
})

async function fetchApplications() {
  loading.value = true
  try {
    const params: Record<string, any> = {
      page: currentPage.value,
      size: 10,
      sort: 'createdAt,desc',
    }
    if (filters.value.activeTab) params.status = filters.value.activeTab
    if (filters.value.search)    params.search = filters.value.search

    const data = await employerApplicationService.getApplicationsByJobPost(jobPostId.value, params)

    candidates.value = data.result
    meta.value = data.meta

    // Cập nhật count cho tab đang chọn
    const activeTabIdx = tabs.value.findIndex(t => t.value === filters.value.activeTab)
    if (activeTabIdx !== -1) {
      tabs.value[activeTabIdx].count = data.meta.totals
    }


  } catch (err) {
    console.error('Failed to fetch applications', err)
  } finally {
    loading.value = false
  }
}

// Khi đổi tab (status filter) → reset page và gọi lại API
watch(() => filters.value.activeTab, () => {
  currentPage.value = 0
  fetchApplications()
})

// Khi search thay đổi (chỉ khi nhấn Enter hoặc xóa) → reset page và gọi lại API
watch(() => filters.value.search, () => {
  currentPage.value = 0
  fetchApplications()
})

function onPageChange(page: number) {
  currentPage.value = page
  fetchApplications()
}

async function fetchJobDetail() {
  try {
    const job = await publicJobPostingService.getById(jobPostId.value)
    jobTitle.value  = job.title
    jobStatus.value = job.status
  } catch (err) {
    console.error('Failed to fetch job detail', err)
  }
}

onMounted(() => {
  fetchJobDetail()
  fetchApplications()
})
</script>

<style scoped>
.page-title    { font-size: 1.875rem; font-weight: 800; color: var(--color-on-surface, #0f172a); }
.page-subtitle { font-size: 1rem; color: #64748b; margin-top: 0.25rem; }

.btn-outline {
  display: inline-flex; align-items: center; gap: 0.5rem;
  padding: 0.5rem 1rem; border: 1px solid #cbd5e1;
  border-radius: 0.5rem; font-size: 1rem; font-weight: 600;
  transition: background .15s;
  cursor: pointer;
}
.btn-outline:hover { background: #f1f5f9; }

.job-status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 700;
}

.filters-table-container {
  border-radius: 0.75rem;
  border: 1px solid #f1f5f9;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0,0,0,.06);
  margin-top: 2rem;
}
.filters-table-container > * {
  border: none !important;
  box-shadow: none !important;
  border-radius: 0 !important;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  gap: 0.75rem;
  color: #94a3b8;
  background: #fff;
}
.loading-state p { font-size: 0.9375rem; }

@keyframes spin { to { transform: rotate(360deg); } }
.loading-spin {
  font-size: 2rem !important;
  animation: spin 0.8s linear infinite;
  color: #4B9AF6;
}
</style>
