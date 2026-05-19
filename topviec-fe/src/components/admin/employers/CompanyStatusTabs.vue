<template>
  <div class="bg-white dark:bg-slate-900 rounded-xl shadow-sm border border-[#963131]/5 overflow-hidden">

    <!-- Tabs theo CompanyStatus -->
    <div class="flex border-b border-[#963131]/5">
      <button
        v-for="tab in statusTabs"
        :key="tab.value"
        class="px-6 py-4 text-sm font-medium transition-colors whitespace-nowrap cursor-pointer"
        :class="activeTab === tab.value
          ? 'font-bold border-b-2 border-[#963131] text-[#963131] bg-[#963131]/5'
          : 'text-slate-500 hover:text-[#963131]'"
        @click="$emit('update:activeTab', tab.value)"
      >
        {{ tab.label }}
        <span
          class="ml-1 text-xs"
          :class="activeTab === tab.value ? 'text-[#963131]' : 'text-slate-400'"
        >({{ tab.count }})</span>
      </button>
    </div>

    <!-- Search + Filter VerificationStatus -->
    <div class="p-4 flex gap-4">
      <div class="flex-1 flex items-center bg-slate-50 dark:bg-white/5 rounded-lg px-4 border border-[#963131]/5 focus-within:ring-2 focus-within:ring-[#963131]/20 transition-all">
        <button class="flex items-center justify-center p-1 -ml-1 hover:bg-[#963131]/10 rounded-md transition-colors cursor-pointer" @click="triggerSearch">
          <span class="material-symbols-outlined text-slate-400 hover:text-[#963131] transition-colors">search</span>
        </button>
        <input
          v-model="localSearch"
          class="bg-transparent border-none focus:ring-0 text-sm w-full py-3 outline-none"
          placeholder="Tìm theo Tên công ty, Mã số thuế..."
          type="text"
          @keyup.enter="triggerSearch"
        />
      </div>

      <!-- Dropdown VerificationStatus -->
      <select
        :value="verificationFilter"
        class="shrink-0 min-w-44 bg-slate-50 dark:bg-white/5 border border-[#963131]/5 rounded-lg text-sm px-4 py-2 focus:ring-2 focus:ring-[#963131]/20 outline-none cursor-pointer text-slate-700 dark:text-slate-300"
        @change="$emit('update:verificationFilter', ($event.target as HTMLSelectElement).value)"
      >
        <option value="">Tất cả Xác thực</option>
        <option
          v-for="vs in verificationOptions"
          :key="vs.value"
          :value="vs.value"
        >
          {{ vs.label }}
        </option>
      </select>

      <!-- <button
        class="shrink-0 px-4 py-2 border border-[#963131]/20 rounded-lg text-[#963131] font-semibold text-sm flex items-center gap-2 hover:bg-[#963131]/5 transition-colors whitespace-nowrap"
      >
        <span class="material-symbols-outlined text-lg">filter_list</span>
        Bộ lọc nâng cao
      </button> -->
    </div>

  </div>
</template>

<script setup lang="ts">
import {
  CompanyStatus,
  VerificationStatus,
  COMPANY_STATUS_LABELS,
  VERIFICATION_STATUS_LABELS,
} from '@/constants/company.constants'
import type { ResCompanyDTO } from '@/types/company.types'
import { computed, ref, watch } from 'vue'

const props = defineProps<{
  activeTab: string        // '' = tất cả, hoặc CompanyStatus value
  search: string
  verificationFilter: string  // '' = tất cả, hoặc VerificationStatus value
  companies: ResCompanyDTO[]  // list gốc để đếm count
}>()

const emit = defineEmits<{
  'update:activeTab':          [value: string]
  'update:search':             [value: string]
  'update:verificationFilter': [value: string]
}>()

// ─── Search handling ─────────────────────────────────────────────────────────────
const localSearch = ref(props.search)

watch(() => props.search, (newVal) => {
  localSearch.value = newVal
})

function triggerSearch() {
  emit('update:search', localSearch.value)
}

// ─── Tabs ────────────────────────────────────────────────────────────────────────
const statusTabs = computed(() => {
  const all = props.companies
  return [
    { value: '',                        label: 'Tất cả',             count: all.length },
    { value: CompanyStatus.ACTIVE,      label: COMPANY_STATUS_LABELS[CompanyStatus.ACTIVE],    count: all.filter(c => c.status === CompanyStatus.ACTIVE).length },
    { value: CompanyStatus.PENDING,     label: COMPANY_STATUS_LABELS[CompanyStatus.PENDING],   count: all.filter(c => c.status === CompanyStatus.PENDING).length },
    { value: CompanyStatus.SUSPENDED,   label: COMPANY_STATUS_LABELS[CompanyStatus.SUSPENDED], count: all.filter(c => c.status === CompanyStatus.SUSPENDED).length },
    { value: CompanyStatus.DELETED,     label: COMPANY_STATUS_LABELS[CompanyStatus.DELETED],   count: all.filter(c => c.status === CompanyStatus.DELETED).length },
  ]
})

// ─── Verification filter options ─────────────────────────────────────────────────
const verificationOptions = Object.values(VerificationStatus).map(v => ({
  value: v,
  label: VERIFICATION_STATUS_LABELS[v],
}))
</script>
