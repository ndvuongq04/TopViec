<template>
  <div>
    <!-- Tabs -->
    <div class="flex border-b border-slate-200 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-800/30 overflow-x-auto">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        class="whitespace-nowrap px-6 py-4 text-sm font-medium border-b-2 transition-colors cursor-pointer"
        :class="activeStatus === tab.value
          ? 'font-bold border-[#963131] text-[#963131]'
          : 'text-slate-500 hover:text-slate-700 dark:hover:text-slate-300 border-transparent'"
        @click="$emit('update:activeStatus', tab.value)"
      >
        {{ tab.label }}
        <span v-if="tab.value === '' && totalCount > 0"> ({{ totalCount }})</span>
      </button>
    </div>

    <!-- Search & Sort -->
    <div class="p-4 border-b border-slate-200 dark:border-slate-800 flex flex-wrap gap-4 items-center justify-between">
      <div class="flex gap-2">
        <div class="relative w-80">
          <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 text-lg">search</span>
          <input
            :value="search"
            class="w-full pl-10 pr-4 py-2 text-sm border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 rounded-lg focus:ring-[#963131]/20 focus:border-[#963131]"
            placeholder="Tìm theo tiêu đề, ID... (Nhấn Enter)"
            type="text"
            @keyup.enter="$emit('search', ($event.target as HTMLInputElement).value)"
          />
        </div>
      </div>
      <div class="flex items-center gap-2">
        <span class="text-xs text-slate-500 font-medium">Sắp xếp theo:</span>
        <select
          :value="sort"
          class="text-sm border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 rounded-lg focus:ring-[#963131]/20"
          @change="$emit('update:sort', ($event.target as HTMLSelectElement).value)"
        >
          <option value="createdAt,desc">Mới nhất</option>
          <option value="createdAt,asc">Cũ nhất</option>
          <option value="deadline,asc">Hạn nộp gần nhất</option>
          <option value="title,asc">Tiêu đề (A-Z)</option>
        </select>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { JobPostingStatus, JOB_POSTING_STATUS_LABELS } from '@/constants/jobPosting.constants'

defineProps<{
  search: string
  activeStatus: string
  sort: string
  totalCount: number
}>()

defineEmits<{
  'update:activeStatus': [value: string]
  'update:sort': [value: string]
  'search': [value: string]
}>()

const tabs = [
  { label: 'Tất cả', value: '' },
  { label: JOB_POSTING_STATUS_LABELS[JobPostingStatus.PENDING_APPROVAL], value: JobPostingStatus.PENDING_APPROVAL },
  { label: JOB_POSTING_STATUS_LABELS[JobPostingStatus.PUBLISHED], value: JobPostingStatus.PUBLISHED },
  { label: JOB_POSTING_STATUS_LABELS[JobPostingStatus.HIDDEN], value: JobPostingStatus.HIDDEN },
  { label: JOB_POSTING_STATUS_LABELS[JobPostingStatus.REJECTED], value: JobPostingStatus.REJECTED },
  { label: JOB_POSTING_STATUS_LABELS[JobPostingStatus.PAUSED], value: JobPostingStatus.PAUSED },
  { label: JOB_POSTING_STATUS_LABELS[JobPostingStatus.CLOSED], value: JobPostingStatus.CLOSED },
  { label: JOB_POSTING_STATUS_LABELS[JobPostingStatus.EXPIRED], value: JobPostingStatus.EXPIRED },
]
</script>
