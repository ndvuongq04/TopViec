<template>
  <div class="space-y-8">
    <!-- Header -->
    <div class="flex flex-wrap items-center justify-between gap-4">
      <div>
        <h2 class="text-2xl font-black text-slate-900 dark:text-slate-100 tracking-tight">Kiểm duyệt Nội dung - Job Postings</h2>
        <p class="text-slate-500 text-sm mt-1">Quản lý và kiểm duyệt các tin tuyển dụng trên hệ thống.</p>
      </div>
    </div>

    <!-- Table Card -->
    <div class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 overflow-hidden">
      <!-- Filters -->
      <AdminJobPostingFilters
        :search="filters.keyword"
        v-model:activeStatus="filters.status"
        v-model:sort="filters.sort"
        :total-count="store.meta.totals"
        @search="onSearch"
      />

      <!-- Loading -->
      <div v-if="store.loading" class="flex items-center justify-center py-20">
        <div class="text-slate-400 text-sm flex items-center gap-2">
          <span class="material-symbols-outlined animate-spin">progress_activity</span>
          Đang tải dữ liệu...
        </div>
      </div>

      <!-- Error -->
      <div v-else-if="store.error" class="flex flex-col items-center justify-center py-20 text-red-500">
        <span class="material-symbols-outlined text-4xl mb-2">error</span>
        <p class="text-sm">{{ store.error }}</p>
        <button class="mt-4 text-sm text-[#963131] font-semibold hover:underline" @click="loadData">
          Thử lại
        </button>
      </div>

      <!-- Table -->
      <AdminJobPostingTable
        v-else
        :jobs="store.jobs"
        :total="store.meta.totals"
        :current-page="currentPage"
        :page-size="pageSize"
        @view="onView"
        @approve="onApprove"
        @reject="onOpenReject"
        @takedown="onOpenTakedown"
        @page-change="onPageChange"
      />
    </div>

    <!-- Action Modal -->
    <AdminJobPostingActionModal
      :visible="modal.visible"
      :submitting="modal.submitting"
      :action-type="modal.actionType"
      :job-id="modal.jobId"
      @close="closeModal"
      @submit="onModalSubmit"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAdminJobPostingStore } from '@/stores/adminJobPosting.store'
import { useToast } from '@/composables/useToast'
import { useConfirm } from '@/composables/useConfirm'

import AdminJobPostingFilters from '@/components/admin/job-postings/AdminJobPostingFilters.vue'
import AdminJobPostingTable from '@/components/admin/job-postings/AdminJobPostingTable.vue'
import AdminJobPostingActionModal from '@/components/admin/job-postings/AdminJobPostingActionModal.vue'

import type { ResJobPostingDetail, ReqRejectJobPostingDTO, EmployerJobPostingQueryParams } from '@/types/jobPosting.types'

const router = useRouter()
const store = useAdminJobPostingStore()
const toast = useToast()
const { confirm } = useConfirm()

const pageSize = ref(10)
const currentPage = ref(1)

const filters = ref({
  keyword: '',
  status: '',
  sort: 'createdAt,desc',
})

const modal = ref({
  visible: false,
  submitting: false,
  actionType: 'reject' as 'reject' | 'takedown',
  jobId: '' as string | number
})

function onSearch(val: string) {
  filters.value.keyword = val
  currentPage.value = 1
  loadData()
}

watch(() => filters.value.status, () => {
  currentPage.value = 1
  loadData()
})

watch(() => filters.value.sort, () => {
  currentPage.value = 1
  loadData()
})

onMounted(() => {
  loadData()
})

async function loadData() {
  const params: EmployerJobPostingQueryParams = {
    page: currentPage.value - 1,
    size: pageSize.value,
  }
  if (filters.value.keyword) params.keyword = filters.value.keyword
  if (filters.value.status) params.status = filters.value.status as any
  if (filters.value.sort) params.sort = filters.value.sort

  await store.fetchJobs(params)
}

function onPageChange(page: number) {
  currentPage.value = page
  loadData()
}

function onView(job: ResJobPostingDetail) {
  router.push({ name: 'admin-job-posting-detail', params: { id: job.id } })
}

async function onApprove(job: ResJobPostingDetail) {
  // Confirm returns boolean
  const isConfirmed = await confirm({
    title: 'Duyệt bài đăng',
    message: `Bạn có chắc muốn duyệt bài đăng "${job.title}"?`,
    confirmText: 'Duyệt ngay',
    confirmColor: 'blue',
    icon: 'check_circle'
  })
  if (!isConfirmed) return

  try {
    await store.approveJob(job.id)
    toast.success('Thành công', 'Bài đăng đã được duyệt')
  } catch {
    toast.error('Lỗi', store.error || 'Không thể duyệt bài')
  }
}

function onOpenReject(job: ResJobPostingDetail) {
  modal.value.actionType = 'reject'
  modal.value.jobId = job.id
  modal.value.visible = true
}

function onOpenTakedown(job: ResJobPostingDetail) {
  modal.value.actionType = 'takedown'
  modal.value.jobId = job.id
  modal.value.visible = true
}

function closeModal() {
  modal.value.visible = false
}

async function onModalSubmit(payload: ReqRejectJobPostingDTO) {
  modal.value.submitting = true
  try {
    if (modal.value.actionType === 'reject') {
      await store.rejectJob(modal.value.jobId, payload)
      toast.success('Thành công', 'Đã từ chối bài đăng')
    } else {
      await store.takedownJob(modal.value.jobId, payload)
      toast.success('Thành công', 'Đã gỡ bài đăng vi phạm')
    }
    closeModal()
  } catch {
    toast.error('Lỗi', store.error || 'Đã có lỗi xảy ra')
  } finally {
    modal.value.submitting = false
  }
}
</script>
