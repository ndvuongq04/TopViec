<template>
  <div class="bg-white dark:bg-slate-900 rounded-xl p-6 border border-slate-200 dark:border-slate-800">
    <div class="flex flex-col lg:flex-row items-start justify-between gap-6">

      <!-- Left: Logo + Info -->
      <div class="flex gap-6">
        <div class="w-24 h-24 rounded-xl border border-slate-200 dark:border-slate-800 bg-white overflow-hidden flex items-center justify-center shrink-0">
          <img
            v-if="company.logoUrl"
            :src="company.logoUrl"
            :alt="company.name"
            class="w-full h-full object-contain"
          />
          <span v-else class="material-symbols-outlined text-4xl text-slate-300">corporate_fare</span>
        </div>
        <div class="space-y-1">
          <div class="flex items-center gap-2">
            <h2 class="text-2xl font-bold">{{ company.name }}</h2>
            <span
              v-if="company.verificationStatus === VerificationStatus.VERIFIED"
              class="bg-blue-100 text-blue-700 text-[10px] font-bold px-2 py-0.5 rounded-full flex items-center gap-1 uppercase tracking-wider"
            >
              <span class="material-symbols-outlined text-xs">verified</span>
              Đã xác thực
            </span>
            <span
              v-else-if="company.verificationStatus === VerificationStatus.PENDING"
              class="bg-amber-100 text-amber-700 text-[10px] font-bold px-2 py-0.5 rounded-full flex items-center gap-1 uppercase tracking-wider"
            >
              <span class="material-symbols-outlined text-xs">pending</span>
              Chờ xác thực
            </span>
            <span
              v-else
              class="bg-red-100 text-red-700 text-[10px] font-bold px-2 py-0.5 rounded-full flex items-center gap-1 uppercase tracking-wider"
            >
              <span class="material-symbols-outlined text-xs">cancel</span>
              Bị từ chối
            </span>
          </div>
          <div class="flex items-center gap-3 text-sm text-slate-500 dark:text-slate-400 font-medium mt-1">
            <span v-if="companyIndustryLabel" class="flex items-center gap-1">
              <span class="material-symbols-outlined text-[16px]">business_center</span>
              {{ companyIndustryLabel }}
            </span>

          </div>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-1 pt-2">
            <div v-if="company.website" class="flex items-center gap-2 text-sm text-slate-600 dark:text-slate-400">
              <span class="material-symbols-outlined text-sm">language</span>
              {{ company.website.replace(/^https?:\/\//, '') }}
            </div>
            <div v-if="company.email" class="flex items-center gap-2 text-sm text-slate-600 dark:text-slate-400">
              <span class="material-symbols-outlined text-sm">mail</span>
              {{ company.email }}
            </div>
            <div v-if="company.phone" class="flex items-center gap-2 text-sm text-slate-600 dark:text-slate-400">
              <span class="material-symbols-outlined text-sm">call</span>
              {{ company.phone }}
            </div>
            <div v-if="company.address" class="flex items-center gap-2 text-sm text-slate-600 dark:text-slate-400">
              <span class="material-symbols-outlined text-sm">location_on</span>
              {{ company.address }}
            </div>
          </div>
        </div>
      </div>

      <!-- Right: Action buttons -->
      <div class="flex flex-wrap gap-3 shrink-0">
        <button
          v-if="can('company.update') && company.status === CompanyStatus.SUSPENDED"
          class="px-4 py-2 rounded-lg bg-emerald-100 dark:bg-emerald-900/30 text-emerald-700 dark:text-emerald-400 text-sm font-bold flex items-center gap-2 hover:bg-emerald-200 transition-colors cursor-pointer"
          @click="$emit('suspend')"
        >
          <span class="material-symbols-outlined text-sm">lock_open</span>
          Mở khóa tài khoản
        </button>
        <button
          v-else-if="can('company.update')"
          class="px-4 py-2 rounded-lg bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400 text-sm font-bold flex items-center gap-2 hover:bg-red-200 transition-colors cursor-pointer"
          @click="$emit('suspend')"
        >
          <span class="material-symbols-outlined text-sm">block</span>
          Khóa tài khoản
        </button>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import type { ResCompanyDTO } from '@/types/company.types'
import { CompanyStatus, VerificationStatus } from '@/constants/company.constants'
import { useAdminPermission } from '@/composables/useAdminPermission'
import { computed } from 'vue'

const { can } = useAdminPermission()

const props = defineProps<{
  company: ResCompanyDTO
}>()

defineEmits<{
  'reset-password': []
  'warn': []
  'suspend': []
}>()

// ─── Constants for UI Mapping ────────────────────────────────────────────────────
// TODO: Replace with actual industry lookup when available
const INDUSTRY_MAP: Record<number, string> = {
  1: 'Công nghệ phần mềm / IT',
  2: 'Bán lẻ / Tiêu dùng',
  3: 'Tài chính / Ngân hàng',
}

const companyIndustryLabel = computed(() => {
  return props.company.industryId ? INDUSTRY_MAP[props.company.industryId] : null
})
</script>
