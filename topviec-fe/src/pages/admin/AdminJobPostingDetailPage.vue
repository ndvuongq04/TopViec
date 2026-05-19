<template>
  <div>
    <!-- Header Section -->
    <div class="flex flex-wrap items-center justify-between gap-4 mb-8">
      <div class="flex items-center gap-4">
        <button
          class="text-slate-400 hover:text-[#963131] transition-colors cursor-pointer"
          @click="router.back()"
        >
          <span class="material-symbols-outlined">arrow_back</span>
        </button>
        <div>
          <h2 class="text-2xl font-bold tracking-tight">Chi tiết tin tuyển dụng</h2>
          <p v-if="job" class="text-sm text-slate-500">
            ID: #{{ job.id }} • Đăng lúc: {{ formatDate(job.publishedAt || job.createdAt) }}
          </p>
        </div>
      </div>

      <div v-if="job" class="flex items-center gap-3">
        <template v-if="job.status === JobPostingStatus.PENDING_APPROVAL">
          <button
            class="flex items-center gap-2 px-4 py-2 bg-red-50 dark:bg-red-900/10 text-red-600 hover:bg-red-100 rounded-lg text-sm font-semibold transition-colors shadow-sm cursor-pointer"
            @click="onOpenReject"
          >
            <span class="material-symbols-outlined text-[18px]">cancel</span>
            Từ chối
          </button>
          <button
            class="flex items-center gap-2 px-4 py-2 bg-[#963131] text-white hover:bg-[#963131]/90 rounded-lg text-sm font-semibold transition-opacity shadow-lg shadow-[#963131]/20 cursor-pointer"
            @click="onApprove"
          >
            <span class="material-symbols-outlined text-[18px]">check_circle</span>
            Duyệt tin
          </button>
        </template>
        
        <template v-if="[JobPostingStatus.PUBLISHED, JobPostingStatus.PAUSED].includes(job.status)">
          <button
            class="flex items-center gap-2 px-4 py-2 bg-red-100 text-red-700 hover:bg-red-200 dark:bg-red-900/30 dark:text-red-400 dark:hover:bg-red-900/50 rounded-lg text-sm font-semibold transition-all shadow-sm cursor-pointer"
            @click="onOpenTakedown"
          >
            <span class="material-symbols-outlined text-[18px]">gavel</span>
            Gỡ bài vi phạm
          </button>
        </template>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="store.loading && !job" class="flex flex-col items-center justify-center py-20 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800">
      <span class="material-symbols-outlined animate-spin text-4xl text-slate-400 mb-4">progress_activity</span>
      <p class="text-slate-500 font-medium">Đang tải thông tin tin tuyển dụng...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="store.error && !job" class="flex flex-col items-center justify-center py-20 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 text-red-500">
      <span class="material-symbols-outlined text-5xl mb-4">error</span>
      <p class="font-bold mb-1">Lỗi tải dữ liệu</p>
      <p class="text-sm text-red-400 mb-6">{{ store.error }}</p>
      <button class="bg-red-50 text-red-600 px-6 py-2 rounded-lg font-bold hover:bg-red-100 transition-colors cursor-pointer" @click="loadData">
        Thử lại
      </button>
    </div>

    <!-- Content -->
    <template v-else-if="job">
      <div class="mx-auto space-y-6">
      
        <!-- Full Header Card -->
        <div class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 overflow-hidden shadow-sm">
          
          <div class="p-6 md:p-8 flex flex-col md:flex-row gap-6 md:items-start justify-between">
            <div class="flex-1 space-y-4">
            <!-- Badge & ID -->
            <div class="flex items-center gap-3">
              <span class="text-xs font-mono text-slate-400">#{{ job.id }}</span>
              <span
                class="px-2.5 py-1 text-[10px] font-black uppercase rounded-full"
                :class="JOB_POSTING_STATUS_BADGE[job.status]"
              >
                {{ JOB_POSTING_STATUS_LABELS[job.status] }}
              </span>
              <span v-if="job.isUrgent" class="px-2.5 py-1 text-[10px] font-black uppercase rounded-full bg-red-100 text-red-700">Tuyển gấp</span>
              <span v-if="job.isFeatured" class="px-2.5 py-1 text-[10px] font-black uppercase rounded-full bg-yellow-100 text-yellow-700">Nổi bật</span>
            </div>

            <!-- Title -->
            <h1 class="text-2xl md:text-3xl font-black text-slate-900 dark:text-slate-100 leading-tight">
              {{ job.title }}
            </h1>

            <div class="flex flex-wrap gap-x-6 gap-y-2 text-sm text-slate-600 dark:text-slate-400">
              <div class="flex items-center gap-2">
                <span class="material-symbols-outlined text-lg opacity-70">corporate_fare</span>
                Công ty: <span class="font-semibold">{{ job.company?.name || '---' }}</span>
              </div>
              <div class="flex items-center gap-2">
                <span class="material-symbols-outlined text-lg opacity-70">work</span>
                {{ WORK_TYPE_LABELS[job.workType as WorkType] || job.workType }}
              </div>
              <div class="flex items-center gap-2">
                <span class="material-symbols-outlined text-lg opacity-70">payments</span>
                {{ formatSalary(job) }}
              </div>
              <div class="flex items-center gap-2">
                <span class="material-symbols-outlined text-lg opacity-70">schedule</span>
                Hạn nộp: <span class="text-orange-600 font-semibold">{{ formatDate(job.deadline) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Detail Grid -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        <!-- Left Column: JD -->
        <div class="lg:col-span-2 space-y-6">
          <div v-if="job.skills && job.skills.length" class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6 md:p-8">
            <h3 class="text-lg font-black text-slate-900 dark:text-slate-100 mb-4 flex items-center gap-2">
              <span class="material-symbols-outlined text-[#963131]">psychology</span>
              Kỹ năng chuyên môn
            </h3>
            <div class="flex flex-wrap gap-2">
              <span
                v-for="s in job.skills"
                :key="s.id"
                class="px-3 py-1.5 bg-slate-50 dark:bg-slate-800/50 text-slate-700 dark:text-slate-300 rounded-lg text-sm font-medium border border-slate-200 dark:border-slate-700"
              >
                {{ s.skillName || `ID: ${s.skillId}` }}
              </span>
            </div>
          </div>

          <div class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6 md:p-8">
            <h3 class="text-lg font-black text-slate-900 dark:text-slate-100 mb-4 flex items-center gap-2">
              <span class="material-symbols-outlined text-[#963131]">description</span>
              Mô tả công việc
            </h3>
            <div
              class="prose prose-slate dark:prose-invert max-w-none prose-sm sm:prose-base tiptap-content"
              v-html="job.description"
            />
          </div>

          <div class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6 md:p-8">
            <h3 class="text-lg font-black text-slate-900 dark:text-slate-100 mb-4 flex items-center gap-2">
              <span class="material-symbols-outlined text-[#963131]">assignment_turned_in</span>
              Yêu cầu ứng viên
            </h3>
            <div
              class="prose prose-slate dark:prose-invert max-w-none prose-sm sm:prose-base tiptap-content"
              v-html="job.requirements"
            />
          </div>

          <div v-if="job.benefits" class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6 md:p-8">
            <h3 class="text-lg font-black text-slate-900 dark:text-slate-100 mb-4 flex items-center gap-2">
              <span class="material-symbols-outlined text-[#963131]">redeem</span>
              Quyền lợi
            </h3>
            <div
              class="prose prose-slate dark:prose-invert max-w-none prose-sm sm:prose-base tiptap-content"
              v-html="job.benefits"
            />
          </div>
        </div>

        <!-- Right Column: Meta info -->
        <div class="space-y-6">
           <!-- General Info -->
          <div class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6">
            <h3 class="text-base font-bold text-slate-900 dark:text-slate-100 mb-4 pb-4 border-b border-slate-100 dark:border-slate-800">Thông tin chung</h3>
            
            <dl class="space-y-4 text-sm">
              <div class="flex justify-between gap-4">
                <dt class="text-slate-500 font-medium whitespace-nowrap">Ngành nghề</dt>
                <dd class="text-slate-900 dark:text-slate-100 font-medium text-right">{{ job.industry?.name || '---' }}</dd>
              </div>
              <div class="flex justify-between gap-4">
                <dt class="text-slate-500 font-medium whitespace-nowrap">Cấp bậc</dt>
                <dd class="text-slate-900 dark:text-slate-100 font-medium text-right">{{ job.level?.name || '---' }}</dd>
              </div>
              <div class="flex justify-between gap-4">
                <dt class="text-slate-500 font-medium whitespace-nowrap">Số lượng tuyển</dt>
                <dd class="text-slate-900 dark:text-slate-100 font-medium text-right">{{ job.headcount }}</dd>
              </div>
              <div class="flex justify-between gap-4">
                <dt class="text-slate-500 font-medium whitespace-nowrap">Kinh nghiệm</dt>
                <dd class="text-slate-900 dark:text-slate-100 font-medium text-right">
                  {{ job.experienceYearsMin }} - {{ job.experienceYearsMax || 'Không giới hạn' }} năm
                </dd>
              </div>
            </dl>
          </div>

          <!-- Stats -->
          <div class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6">
             <h3 class="text-base font-bold text-slate-900 dark:text-slate-100 mb-4 pb-4 border-b border-slate-100 dark:border-slate-800">Thống kê nội bộ</h3>
             <dl class="space-y-4 text-sm">
              <div class="flex justify-between gap-4">
                <dt class="text-slate-500 font-medium">Lượt xem</dt>
                <dd class="text-slate-900 dark:text-slate-100 font-bold">{{ job.viewCount }}</dd>
              </div>
              <div class="flex justify-between gap-4">
                <dt class="text-slate-500 font-medium">Lượt chỉnh sửa</dt>
                <dd class="text-slate-900 dark:text-slate-100 font-medium">{{ job.editCount }}</dd>
              </div>
              <div class="flex justify-between gap-4">
                <dt class="text-slate-500 font-medium">Ngày tạo gốc</dt>
                <dd class="text-slate-900 dark:text-slate-100 font-medium">{{ formatDate(job.createdAt) }}</dd>
              </div>
               <div class="flex justify-between gap-4">
                <dt class="text-slate-500 font-medium">Cập nhật lúc</dt>
                <dd class="text-slate-900 dark:text-slate-100 font-medium">{{ formatDate(job.updatedAt) }}</dd>
              </div>
             </dl>
          </div>
        </div>

      </div>
      </div>
    </template>

    <!-- Modal Actions -->
    <AdminJobPostingActionModal
      :visible="modal.visible"
      :submitting="modal.submitting"
      :action-type="modal.actionType"
      :job-id="modal.jobId"
      @close="modal.visible = false"
      @submit="onModalSubmit"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAdminJobPostingStore } from '@/stores/adminJobPosting.store'
import { useToast } from '@/composables/useToast'
import { useConfirm } from '@/composables/useConfirm'
import dayjs from 'dayjs'

import { JobPostingStatus, JOB_POSTING_STATUS_LABELS, JOB_POSTING_STATUS_BADGE, WORK_TYPE_LABELS, WorkType } from '@/constants/jobPosting.constants'
import type { ReqRejectJobPostingDTO } from '@/types/jobPosting.types'
import { formatSalary } from '@/types/jobPosting.types'
import AdminJobPostingActionModal from '@/components/admin/job-postings/AdminJobPostingActionModal.vue'

const route = useRoute()
const router = useRouter()
const store = useAdminJobPostingStore()
const toast = useToast()
const { confirm } = useConfirm()

const jobId = computed(() => route.params.id as string)
const job = computed(() => store.selectedJob)

const modal = ref({
  visible: false,
  submitting: false,
  actionType: 'reject' as 'reject' | 'takedown',
  jobId: '' as string | number
})

onMounted(() => {
  loadData()
})

async function loadData() {
  if (jobId.value) {
    await store.fetchJobById(jobId.value)
  }
}

function formatDate(val?: string) {
  if (!val) return '—'
  return dayjs(val).format('DD/MM/YYYY HH:mm')
}


async function onApprove() {
  if (!job.value) return
  const isConfirmed = await confirm({
    title: 'Duyệt bài đăng',
    message: `Bạn có chắc muốn duyệt bài đăng "${job.value.title}"?`,
    confirmText: 'Duyệt ngay',
    confirmColor: 'blue',
    icon: 'check_circle'
  })
  if (!isConfirmed) return

  try {
    await store.approveJob(job.value.id)
    toast.success('Thành công', 'Bài đăng đã được duyệt')
  } catch {
    toast.error('Lỗi', store.error || 'Không thể duyệt bài')
  }
}

function onOpenReject() {
  if (!job.value) return
  modal.value.actionType = 'reject'
  modal.value.jobId = job.value.id
  modal.value.visible = true
}

function onOpenTakedown() {
  if (!job.value) return
  modal.value.actionType = 'takedown'
  modal.value.jobId = job.value.id
  modal.value.visible = true
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
    modal.value.visible = false
  } catch {
    toast.error('Lỗi', store.error || 'Đã có lỗi xảy ra')
  } finally {
    modal.value.submitting = false
  }
}
</script>
