<template>
  <div class="bg-white dark:bg-slate-900 rounded-xl shadow-sm border border-[#963131]/5 overflow-hidden relative">
    <!-- Loading Overlay -->
    <div v-if="loading" class="absolute inset-0 bg-white/50 dark:bg-slate-900/50 z-10 flex items-center justify-center">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-[#963131]"></div>
    </div>

    <div class="overflow-x-auto">
      <table class="w-full text-left border-collapse">
        <thead>
          <tr class="bg-slate-50 dark:bg-white/5 border-b border-[#963131]/5">
            <th class="px-6 py-4 text-[0.75rem] font-bold text-slate-500 uppercase tracking-wider">Mã ID</th>
            <th class="px-6 py-4 text-[0.75rem] font-bold text-slate-500 uppercase tracking-wider">Ứng viên</th>
            <th class="px-6 py-4 text-[0.75rem] font-bold text-slate-500 uppercase tracking-wider">Trạng thái</th>
            <th class="px-6 py-4 text-[0.75rem] font-bold text-slate-500 uppercase tracking-wider">Ngày tham gia</th>
            <th class="px-6 py-4 text-[0.75rem] font-bold text-slate-500 uppercase tracking-wider text-center">Hành động</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-[#963131]/5">
          <tr
            v-for="candidate in candidates"
            :key="candidate.id"
            class="hover:bg-[#963131]/5 transition-colors"
          >
            <td class="px-6 py-4 text-[0.875rem] font-mono text-slate-500">UV-{{ String(candidate.id).padStart(4, '0') }}</td>
            <td class="px-6 py-4">
              <div class="flex items-center gap-3">
                <div class="size-10 rounded-full bg-slate-100 flex items-center justify-center border border-slate-200 overflow-hidden shrink-0">
                  <img v-if="candidate.avatarUrl" :src="candidate.avatarUrl" class="size-full object-cover" />
                  <span v-else class="material-symbols-outlined text-slate-400">person</span>
                </div>
                <div>
                  <p class="font-bold text-slate-900 dark:text-slate-100 text-[0.875rem]">{{ candidate.fullName }}</p>
                  <p class="text-[0.75rem] text-slate-500">{{ candidate.email || '—' }}</p>
                </div>
              </div>
            </td>
            <td class="px-6 py-4">
              <span class="px-2.5 py-1 rounded-full text-[0.75rem] font-bold flex w-fit items-center gap-1" :class="statusBadgeClass(candidate.status)">
                <span class="size-1.5 rounded-full" :class="statusDotClass(candidate.status)"></span>
                {{ statusLabel(candidate.status) }}
              </span>
            </td>
            <td class="px-6 py-4 text-[0.875rem] text-slate-600 dark:text-slate-400">
              {{ formatDate(candidate.createdAt) }}
            </td>
            <td class="px-6 py-4">
              <div class="flex items-center justify-center gap-2">
                <button class="p-1.5 hover:bg-[#963131]/10 rounded-lg text-slate-500 hover:text-[#963131] transition-colors cursor-pointer" title="Xem chi tiết" @click="$emit('view', candidate)">
                  <span class="material-symbols-outlined text-xl">visibility</span>
                </button>
                <button class="p-1.5 hover:bg-red-50 rounded-lg text-slate-500 hover:text-red-600 transition-colors cursor-pointer" title="Xóa" @click="$emit('delete', candidate)">
                  <span class="material-symbols-outlined text-xl">delete</span>
                </button>
              </div>
            </td>
          </tr>
          <tr v-if="candidates.length === 0 && !loading">
            <td colspan="5" class="px-6 py-12 text-center text-slate-400 text-[0.875rem]">
              <span class="material-symbols-outlined text-4xl block mb-2">group</span>
              Không tìm thấy ứng viên nào
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination UI -->
    <div class="p-6 flex items-center justify-between border-t border-[#963131]/5 bg-slate-50/30 dark:bg-white/5">
      <p class="text-[0.875rem] text-slate-500 font-medium">
        Hiển thị {{ rangeStart }}–{{ rangeEnd }} trên tổng số {{ total }} ứng viên
      </p>
      <div class="flex items-center gap-2">
        <button
          class="size-9 flex items-center justify-center rounded-lg border border-[#963131]/10 hover:bg-white text-slate-400 hover:text-[#963131] transition-all disabled:opacity-30 disabled:cursor-not-allowed cursor-pointer"
          :disabled="currentPage === 0"
          @click="$emit('page-change', currentPage - 1)"
        >
          <span class="material-symbols-outlined text-lg">chevron_left</span>
        </button>

        <template v-for="page in visiblePages" :key="page">
          <span v-if="page === '...'" class="px-2 text-slate-400">...</span>
          <button
            v-else
            class="size-9 flex items-center justify-center rounded-lg font-bold text-sm transition-all cursor-pointer"
            :class="(page as number) === currentPage + 1
              ? 'bg-[#963131] text-white shadow-md'
              : 'border border-[#963131]/10 hover:bg-white text-slate-600 hover:text-[#963131]'"
            @click="$emit('page-change', (page as number) - 1)"
          >
            {{ page }}
          </button>
        </template>

        <button
          class="size-9 flex items-center justify-center rounded-lg border border-[#963131]/10 hover:bg-white text-slate-400 hover:text-[#963131] transition-all disabled:opacity-30 disabled:cursor-not-allowed cursor-pointer"
          :disabled="currentPage >= totalPages - 1 || totalPages === 0"
          @click="$emit('page-change', currentPage + 1)"
        >
          <span class="material-symbols-outlined text-lg">chevron_right</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { UserStatus, USER_STATUS_LABELS, USER_STATUS_STYLES } from '@/constants/user.constants'

const props = defineProps<{
  candidates: any[]
  total: number
  currentPage: number // 0-based
  pageSize: number
  loading?: boolean
}>()

const emit = defineEmits<{
  'view':        [candidate: any]
  'delete':      [candidate: any]
  'page-change': [page: number] // 0-based
}>()

// ─── Status helpers ──────────────────────────────────────────────────────────────
function statusLabel(status: string) {
  return USER_STATUS_LABELS[status as UserStatus] || status
}

function statusBadgeClass(status: string) {
  return USER_STATUS_STYLES[status as UserStatus]?.bg || ''
}

function statusDotClass(status: string) {
  return USER_STATUS_STYLES[status as UserStatus]?.dot || ''
}

// ─── Date formatting ─────────────────────────────────────────────────────────────
function formatDate(iso: string): string {
  if (!iso) return '—'
  const d = new Date(iso)
  return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
}

// ─── Pagination Logic (0-based) ──────────────────────────────────────────────────
const totalPages = computed(() => Math.ceil(props.total / props.pageSize))
const rangeStart = computed(() => props.total === 0 ? 0 : props.currentPage * props.pageSize + 1)
const rangeEnd   = computed(() => Math.min((props.currentPage + 1) * props.pageSize, props.total))

const visiblePages = computed(() => {
  const pages: (number | string)[] = []
  const total = totalPages.value
  const cur   = props.currentPage + 1 // Convert to 1-based for UI logic
  
  if (total <= 5) {
    for (let i = 1; i <= total; i++) pages.push(i)
    return pages
  }
  
  pages.push(1)
  if (cur > 3) pages.push('...')
  
  for (let i = Math.max(2, cur - 1); i <= Math.min(total - 1, cur + 1); i++) {
    pages.push(i)
  }
  
  if (cur < total - 2) pages.push('...')
  pages.push(total)
  return pages
})
</script>
