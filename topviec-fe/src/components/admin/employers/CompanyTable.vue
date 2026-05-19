<template>
  <div class="bg-white dark:bg-slate-900 rounded-xl shadow-sm border border-[#963131]/5 overflow-hidden">
    <div class="overflow-x-auto">
      <table class="w-full text-left border-collapse">
        <thead>
          <tr class="bg-slate-50 dark:bg-white/5 border-b border-[#963131]/5">
            <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">Mã ID</th>
            <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">Công ty</th>
            <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">Trạng thái</th>
            <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">Xác thực</th>
            <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">Ngày đăng ký</th>
            <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-center">Hành động</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-[#963131]/5">
          <tr
            v-for="company in companies"
            :key="company.id"
            class="hover:bg-[#963131]/5 transition-colors"
          >
            <!-- Mã ID -->
            <td class="px-6 py-4 text-sm font-mono text-slate-500">NTT-{{ String(company.id).padStart(4, '0') }}</td>

            <!-- Công ty -->
            <td class="px-6 py-4">
              <div class="flex items-center gap-3">
                <div class="size-10 rounded-lg bg-slate-100 flex items-center justify-center border border-slate-200 overflow-hidden shrink-0">
                  <img
                    v-if="company.logoUrl"
                    :src="company.logoUrl"
                    :alt="company.name"
                    class="size-8 object-contain"
                  />
                  <span v-else class="material-symbols-outlined text-slate-400">corporate_fare</span>
                </div>
                <div>
                  <div class="flex items-center gap-1.5">
                    <!-- isTopEmployer — badge Top đầu tiên -->
                    <span
                      v-if="company.isTopEmployer"
                      class="text-[11px] font-semibold px-2 py-0.5 rounded-full border shrink-0"
                      style="background:#fff7ed; color:#b45309; border-color:#fcd34d"
                    >Top</span>
                    <p class="font-bold text-slate-900 dark:text-slate-100">{{ company.name }}</p>
                    <!-- isBrandVerified — tích xanh đồng bộ các trang khác -->
                    <div v-if="company.isBrandVerified" class="relative flex items-center group/verified">
                      <span
                        class="material-symbols-outlined text-blue-600 text-[16px] cursor-default"
                        style="font-variation-settings:'FILL' 1"
                      >verified</span>
                      <span class="pointer-events-none absolute bottom-[calc(100%+6px)] left-1/2 -translate-x-1/2 bg-blue-800 text-white text-[11px] font-medium whitespace-nowrap px-2.5 py-1 rounded-md opacity-0 group-hover/verified:opacity-100 transition-opacity z-10">
                        Công ty đã xác minh
                        <span class="absolute top-full left-1/2 -translate-x-1/2 border-4 border-transparent border-t-blue-800"></span>
                      </span>
                    </div>
                  </div>
                  <p class="text-xs text-slate-500">{{ company.email || '—' }}</p>
                </div>
              </div>
            </td>

            <!-- Trạng thái (CompanyStatus) -->
            <td class="px-6 py-4">
              <span
                class="px-2.5 py-1 rounded-full text-xs font-bold flex w-fit items-center gap-1"
                :class="statusBadgeClass(company.status)"
              >
                <span class="size-1.5 rounded-full" :class="statusDotClass(company.status)"></span>
                {{ statusLabel(company.status) }}
              </span>
            </td>

            <!-- Xác thực (VerificationStatus) -->
            <td class="px-6 py-4">
              <span
                class="px-2.5 py-1 rounded-full text-xs font-bold flex w-fit items-center gap-1"
                :class="verificationBadgeClass(company.verificationStatus)"
              >
                <span class="size-1.5 rounded-full" :class="verificationDotClass(company.verificationStatus)"></span>
                {{ verificationLabel(company.verificationStatus) }}
              </span>
            </td>

            <!-- Ngày đăng ký -->
            <td class="px-6 py-4 text-sm text-slate-600 dark:text-slate-400">
              {{ formatDate(company.createdAt) }}
            </td>

            <!-- Hành động -->
            <td class="px-6 py-4">
              <div class="flex items-center justify-center gap-2">
                <button
                  class="p-1.5 hover:bg-[#963131]/10 rounded-lg text-slate-500 hover:text-[#963131] transition-colors cursor-pointer"
                  title="Xem chi tiết"
                  @click="$emit('view', company)"
                >
                  <span class="material-symbols-outlined text-xl">visibility</span>
                </button>
                <!-- <button
                  class="p-1.5 hover:bg-[#963131]/10 rounded-lg transition-colors"
                  :class="company.status === CompanyStatus.SUSPENDED
                    ? 'text-[#963131] hover:text-[#963131]'
                    : 'text-slate-500 hover:text-[#963131]'"
                  :title="company.status === CompanyStatus.SUSPENDED ? 'Mở khóa' : 'Khóa'"
                  @click="$emit('toggle-lock', company)"
                >
                  <span class="material-symbols-outlined text-xl">
                    {{ company.status === CompanyStatus.SUSPENDED ? 'lock' : 'lock_open' }}
                  </span>
                </button>
                <button
                  class="p-1.5 hover:bg-[#963131]/10 rounded-lg text-slate-500 hover:text-[#963131] transition-colors"
                  title="Cảnh báo"
                  @click="$emit('notify', company)"
                >
                  <span class="material-symbols-outlined text-xl">notifications</span>
                </button> -->
                <button
                  v-if="can('company.delete')"
                  class="p-1.5 hover:bg-red-50 rounded-lg text-slate-500 hover:text-red-600 transition-colors cursor-pointer"
                  title="Xóa"
                  @click="$emit('delete', company)"
                >
                  <span class="material-symbols-outlined text-xl">delete</span>
                </button>
              </div>
            </td>
          </tr>

          <!-- Empty state -->
          <tr v-if="companies.length === 0">
            <td colspan="6" class="px-6 py-12 text-center text-slate-400 text-sm">
              <span class="material-symbols-outlined text-4xl block mb-2">corporate_fare</span>
              Không tìm thấy nhà tuyển dụng nào
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div class="p-6 flex items-center justify-between border-t border-[#963131]/5 bg-slate-50/30 dark:bg-white/5">
      <p class="text-sm text-slate-500 font-medium">
        Hiển thị {{ rangeStart }}–{{ rangeEnd }} trên tổng số {{ total }} NTT
      </p>
      <div class="flex items-center gap-2">
        <button
          class="size-9 flex items-center justify-center rounded-lg border border-[#963131]/10 hover:bg-white text-slate-400 hover:text-[#963131] transition-all disabled:opacity-30 disabled:cursor-not-allowed cursor-pointer"
          :disabled="currentPage <= 1"
          @click="$emit('page-change', currentPage - 1)"
        >
          <span class="material-symbols-outlined text-lg">chevron_left</span>
        </button>

        <template v-for="page in visiblePages" :key="page">
          <span v-if="page === '...'" class="px-2 text-slate-400">...</span>
          <button
            v-else
            class="size-9 flex items-center justify-center rounded-lg font-bold text-sm transition-all cursor-pointer"
            :class="page === currentPage
              ? 'bg-[#963131] text-white shadow-md'
              : 'border border-[#963131]/10 hover:bg-white text-slate-600 hover:text-[#963131]'"
            @click="$emit('page-change', page as number)"
          >
            {{ page }}
          </button>
        </template>

        <button
          class="size-9 flex items-center justify-center rounded-lg border border-[#963131]/10 hover:bg-white text-slate-400 hover:text-[#963131] transition-all disabled:opacity-30 disabled:cursor-not-allowed cursor-pointer"
          :disabled="currentPage >= totalPages"
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
import type { ResCompanyDTO } from '@/types/company.types'
import {
  CompanyStatus,
  VerificationStatus,
  COMPANY_STATUS_LABELS,
  VERIFICATION_STATUS_LABELS,
} from '@/constants/company.constants'
import { useAdminPermission } from '@/composables/useAdminPermission'

const { can } = useAdminPermission()

const props = defineProps<{
  companies: ResCompanyDTO[]
  total: number
  currentPage: number
  pageSize: number
}>()

defineEmits<{
  'view':        [company: ResCompanyDTO]
  'toggle-lock': [company: ResCompanyDTO]
  'notify':      [company: ResCompanyDTO]
  'delete':      [company: ResCompanyDTO]
  'page-change': [page: number]
}>()

// ─── Status helpers ──────────────────────────────────────────────────────────────
function statusLabel(status: CompanyStatus) {
  return COMPANY_STATUS_LABELS[status] ?? status
}

function statusBadgeClass(status: CompanyStatus) {
  return {
    [CompanyStatus.ACTIVE]:    'bg-green-100 text-green-700',
    [CompanyStatus.PENDING]:   'bg-amber-100 text-amber-700',
    [CompanyStatus.SUSPENDED]: 'bg-red-100 text-red-700',
    [CompanyStatus.DELETED]:   'bg-slate-100 text-slate-500',
  }[status]
}

function statusDotClass(status: CompanyStatus) {
  return {
    [CompanyStatus.ACTIVE]:    'bg-green-700',
    [CompanyStatus.PENDING]:   'bg-amber-700',
    [CompanyStatus.SUSPENDED]: 'bg-red-700',
    [CompanyStatus.DELETED]:   'bg-slate-500',
  }[status]
}

// ─── Verification helpers ────────────────────────────────────────────────────────
function verificationLabel(status: VerificationStatus) {
  return VERIFICATION_STATUS_LABELS[status] ?? status
}

function verificationBadgeClass(status: VerificationStatus) {
  return {
    [VerificationStatus.VERIFIED]: 'bg-[#963131]/10 text-[#963131]',
    [VerificationStatus.PENDING]:  'bg-amber-100 text-amber-700',
    [VerificationStatus.REJECTED]: 'bg-red-100 text-red-700',
  }[status]
}

function verificationDotClass(status: VerificationStatus) {
  return {
    [VerificationStatus.VERIFIED]: 'bg-[#963131]',
    [VerificationStatus.PENDING]:  'bg-amber-700',
    [VerificationStatus.REJECTED]: 'bg-red-700',
  }[status]
}

// ─── Date formatting ─────────────────────────────────────────────────────────────
function formatDate(iso: string): string {
  const d = new Date(iso)
  return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
}

// ─── Pagination ──────────────────────────────────────────────────────────────────
const totalPages = computed(() => Math.ceil(props.total / props.pageSize))
const rangeStart = computed(() => props.total === 0 ? 0 : (props.currentPage - 1) * props.pageSize + 1)
const rangeEnd   = computed(() => Math.min(props.currentPage * props.pageSize, props.total))

const visiblePages = computed(() => {
  const pages: (number | string)[] = []
  const total = totalPages.value
  const cur   = props.currentPage
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
