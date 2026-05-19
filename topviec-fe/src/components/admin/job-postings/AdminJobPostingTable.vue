<template>
  <div>
    <div class="overflow-x-auto">
      <table class="w-full text-left border-collapse">
        <thead>
          <tr class="bg-slate-50/50 dark:bg-slate-800/50 border-b border-slate-200 dark:border-slate-800">
            <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider w-16">ID</th>
            <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">TIÊU ĐỀ & THÔNG TIN</th>
            <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">LOẠI HÌNH</th>
            <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">TRẠNG THÁI</th>
            <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">NGÀY ĐĂNG / HẠN</th>
            <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-right">HÀNH ĐỘNG</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-200 dark:divide-slate-800">
          <tr
            v-for="job in jobs"
            :key="job.id"
            class="hover:bg-slate-50 dark:hover:bg-slate-800/40 transition-colors"
          >
            <!-- ID -->
            <td class="px-6 py-4 text-sm font-mono text-slate-400">
              #{{ job.id }}
            </td>

            <!-- Info -->
            <td class="px-6 py-4">
              <div class="flex items-center gap-2 flex-wrap">
                <span class="font-bold text-slate-900 dark:text-slate-100 text-sm max-w-sm truncate" :title="job.title">
                  {{ job.title }}
                </span>
                <span
                  v-if="job.isUrgent"
                  class="text-[10px] font-extrabold uppercase tracking-wide text-amber-700 bg-amber-50 border border-amber-300 px-2 py-0.5 rounded-full whitespace-nowrap"
                >
                  TUYỂN GẤP
                </span>
                <span
                  v-if="job.isFeatured"
                  class="text-[10px] font-extrabold uppercase tracking-wide text-blue-700 bg-blue-50 border border-blue-300 px-2 py-0.5 rounded-full whitespace-nowrap"
                >
                  Nổi bật
                </span>
                <span
                  v-if="job.isHot"
                  class="text-[10px] font-extrabold uppercase tracking-wide text-red-700 bg-red-50 border border-red-300 px-2 py-0.5 rounded-full whitespace-nowrap"
                >
                  HOT
                </span>
              </div>
            </td>

            <!-- WorkType -->
            <td class="px-6 py-4 text-sm text-slate-600">
              {{ formatWorkType(job.workType) }}
            </td>

            <!-- Status -->
            <td class="px-6 py-4">
              <span
                class="px-2.5 py-1 text-[10px] font-bold uppercase rounded-full"
                :class="JOB_POSTING_STATUS_BADGE[job.status]"
              >
                {{ JOB_POSTING_STATUS_LABELS[job.status] }}
              </span>
            </td>

            <!-- Dates -->
            <td class="px-6 py-4 text-sm">
              <div class="text-slate-700 dark:text-slate-300">
                Lên: <span class="font-medium">{{ job.publishedAt ? formatDate(job.publishedAt) : (job.status === JobPostingStatus.PENDING_APPROVAL ? '-' : formatDate(job.createdAt)) }}</span>
              </div>
              <div class="text-slate-500 mt-1">
                Hạn: <span class="font-medium text-orange-600">{{ formatDate(job.deadline) }}</span>
              </div>
            </td>

            <!-- Actions -->
            <td class="px-6 py-4 text-right">
              <div class="flex items-center justify-end gap-1">
                <button
                  class="p-1.5 text-slate-400 hover:text-[#963131] transition-colors cursor-pointer"
                  title="Xem chi tiết"
                  @click="$emit('view', job)"
                >
                  <span class="material-symbols-outlined text-lg">visibility</span>
                </button>
                
                <button
                  v-if="job.status === JobPostingStatus.PENDING_APPROVAL"
                  class="p-1.5 text-[#963131] hover:bg-[#963131]/10 rounded transition-colors cursor-pointer"
                  title="Duyệt đăng"
                  @click="$emit('approve', job)"
                >
                  <span class="material-symbols-outlined text-lg">check_circle</span>
                </button>

                <button
                  v-if="job.status === JobPostingStatus.PENDING_APPROVAL"
                  class="p-1.5 text-red-600 hover:bg-red-50 dark:hover:bg-red-900/10 rounded transition-colors cursor-pointer"
                  title="Từ chối"
                  @click="$emit('reject', job)"
                >
                  <span class="material-symbols-outlined text-lg">cancel</span>
                </button>

                <button
                  v-if="[JobPostingStatus.PUBLISHED, JobPostingStatus.PAUSED].includes(job.status)"
                  class="p-1.5 text-red-600 hover:bg-red-50 rounded transition-colors cursor-pointer"
                  title="Gỡ bài"
                  @click="$emit('takedown', job)"
                >
                  <span class="material-symbols-outlined text-lg">gavel</span>
                </button>
              </div>
            </td>
          </tr>

          <tr v-if="jobs.length === 0">
            <td colspan="6" class="px-6 py-12 text-center text-slate-400 text-sm">
              <span class="material-symbols-outlined text-4xl block mb-2">find_in_page</span>
              Không tìm thấy tin tuyển dụng nào
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div class="p-6 border-t border-slate-200 dark:border-slate-800 flex items-center justify-between">
      <p class="text-xs text-slate-500 font-medium">
        Hiển thị <span class="text-slate-900 dark:text-white">{{ rangeStart }} - {{ rangeEnd }}</span>
        trong số <span class="text-slate-900 dark:text-white">{{ total }}</span> tin
      </p>
      <div class="flex items-center gap-2">
        <button
          class="size-8 flex items-center justify-center rounded-lg border border-slate-200 dark:border-slate-700 text-slate-400 hover:bg-slate-50 disabled:opacity-50 cursor-pointer"
          :disabled="currentPage <= 1"
          @click="$emit('page-change', currentPage - 1)"
        >
          <span class="material-symbols-outlined text-xl leading-none">chevron_left</span>
        </button>

        <template v-for="page in visiblePages" :key="page">
          <span v-if="page === '...'" class="px-1 text-slate-400 text-sm">...</span>
          <button
            v-else
            class="size-8 flex items-center justify-center rounded-lg text-xs font-bold transition-colors cursor-pointer"
            :class="page === currentPage
              ? 'bg-[#963131] text-white shadow-sm'
              : 'border border-slate-200 dark:border-slate-700 text-slate-600 hover:bg-slate-50'"
            @click="$emit('page-change', page as number)"
          >
            {{ page }}
          </button>
        </template>

        <button
          class="size-8 flex items-center justify-center rounded-lg border border-slate-200 dark:border-slate-700 text-slate-400 hover:bg-slate-50 disabled:opacity-50 transition-colors cursor-pointer"
          :disabled="currentPage >= totalPages"
          @click="$emit('page-change', currentPage + 1)"
        >
          <span class="material-symbols-outlined text-xl leading-none">chevron_right</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import dayjs from 'dayjs'
import { JobPostingStatus, JOB_POSTING_STATUS_LABELS, JOB_POSTING_STATUS_BADGE, WORK_TYPE_LABELS, WorkType } from '@/constants/jobPosting.constants'
import type { ResJobPostingDetail } from '@/types/jobPosting.types'

const props = defineProps<{
  jobs: ResJobPostingDetail[]
  total: number
  currentPage: number
  pageSize: number
}>()

defineEmits<{
  view: [job: ResJobPostingDetail]
  approve: [job: ResJobPostingDetail]
  reject: [job: ResJobPostingDetail]
  takedown: [job: ResJobPostingDetail]
  'page-change': [page: number]
}>()

function formatWorkType(val: string) {
  return WORK_TYPE_LABELS[val as WorkType] || val
}

function formatDate(date: string) {
  return date ? dayjs(date).format('DD/MM/YYYY') : '—'
}

const totalPages = computed(() => Math.max(1, Math.ceil(props.total / props.pageSize)))
const rangeStart = computed(() => props.total === 0 ? 0 : (props.currentPage - 1) * props.pageSize + 1)
const rangeEnd = computed(() => Math.min(props.currentPage * props.pageSize, props.total))

const visiblePages = computed(() => {
  const pages: (number | string)[] = []
  const total = totalPages.value
  const cur = props.currentPage
  if (total <= 5) {
    for (let i = 1; i <= total; i++) pages.push(i)
    return pages
  }
  pages.push(1)
  if (cur > 3) pages.push('...')
  for (let i = Math.max(2, cur - 1); i <= Math.min(total - 1, cur + 1); i++) pages.push(i)
  if (cur < total - 2) pages.push('...')
  pages.push(total)
  return pages
})
</script>
