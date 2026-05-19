<template>
  <div class="space-y-8 pt-6" style="font-family: 'Manrope', sans-serif;">
    <!-- Title -->
    <div class="flex flex-col md:flex-row md:items-end justify-between gap-4">
      <div>
        <h2 class="text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight">Nhật ký hoạt động công ty</h2>
        <p class="text-slate-500 mt-1 text-base">
          Theo dõi lịch sử thao tác của các thành viên trong doanh nghiệp trên tin tuyển dụng,
          ứng viên, phân quyền và dịch vụ.
        </p>
      </div>
      <div class="flex items-center gap-3">
        <button class="inline-flex items-center gap-2 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-700 dark:text-slate-200 px-5 py-2.5 rounded-xl font-extrabold text-base transition-all cursor-pointer">
          <span class="material-symbols-outlined text-sm">download</span>
          Xuất Excel
        </button>
        <button class="inline-flex items-center gap-2 bg-primary hover:bg-primary/90 text-white px-5 py-2.5 rounded-xl font-extrabold text-base transition-all shadow-lg shadow-primary/20 hover:scale-[1.02] active:scale-95 cursor-pointer">
          <span class="material-symbols-outlined text-sm">save</span>
          Lưu bộ lọc
        </button>
      </div>
    </div>

    <!-- KPI Row -->
    <ActivityLogKpiCards :stats="kpiStats" />

    <!-- Tabs Section -->
    <div class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 overflow-hidden shadow-sm">
      <div class="flex border-b border-slate-200 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-800/30">
        <button
          v-for="tab in logTypeTabs"
          :key="tab.value"
          class="px-8 py-4 text-sm font-extrabold border-b-2 transition-all cursor-pointer flex items-center gap-2"
          :class="activeLogType === tab.value
            ? 'border-primary text-primary bg-white dark:bg-slate-900'
            : 'text-slate-500 hover:text-slate-700 dark:hover:text-slate-300 border-transparent'"
          @click="handleTabChange(tab.value as any)"
        >
          <span class="material-symbols-outlined text-lg">{{ tab.icon }}</span>
          {{ tab.label }}
          <span v-if="tab.count > 0" class="ml-1 text-[10px] px-1.5 py-0.5 rounded-full bg-slate-100 dark:bg-slate-800 text-slate-500 font-bold">
            {{ tab.count }}
          </span>
        </button>
      </div>

      <div class="relative flex flex-col min-h-[400px]">
        <!-- Loading Overlay -->
        <div v-if="loading" class="absolute inset-0 bg-white/50 dark:bg-slate-900/50 z-10 flex items-center justify-center backdrop-blur-[1px]">
          <div class="flex flex-col items-center gap-2">
            <span class="material-symbols-outlined animate-spin text-4xl text-primary">progress_activity</span>
            <span class="text-sm font-bold text-slate-500">Đang tải dữ liệu...</span>
          </div>
        </div>

        <ActivityLogFilters v-model="filters" :active-type="activeLogType" @apply="handleApplyFilters" @reset="handleReset" />
        
        <ActivityLogTable 
          :logs="currentLogs" 
          :type="activeLogType"
          @view="handleView" 
        />
        
        <ActivityLogPagination
          :current-page="currentMeta.page + 1"
          :total-records="currentMeta.totals"
          :per-page="currentMeta.pageSize"
          @change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useEmployerLogStore } from '@/stores/employerLog.store'
import type { LogQueryParams } from '@/types/logs.types'
import ActivityLogKpiCards from '@/components/recruiter/activity-log/ActivityLogKpiCards.vue'
import ActivityLogFilters from '@/components/recruiter/activity-log/ActivityLogFilters.vue'
import ActivityLogTable from '@/components/recruiter/activity-log/ActivityLogTable.vue'
import ActivityLogPagination from '@/components/recruiter/activity-log/ActivityLogPagination.vue'

const router = useRouter()
const logStore = useEmployerLogStore()
const { auditLogs, businessLogs, auditMeta, businessMeta, statistics, loading } = storeToRefs(logStore)

const activeLogType = ref<'AUDIT' | 'BUSINESS'>('AUDIT')

const logTypeTabs = computed(() => [
  { label: 'Nhật ký hệ thống', value: 'AUDIT', icon: 'security', count: auditMeta.value.totals },
  { label: 'Nhật ký nghiệp vụ', value: 'BUSINESS', icon: 'business_center', count: businessMeta.value.totals },
])

const currentLogs = computed(() => activeLogType.value === 'AUDIT' ? auditLogs.value : businessLogs.value)
const currentMeta = computed(() => activeLogType.value === 'AUDIT' ? auditMeta.value : businessMeta.value)

const filters = ref<LogQueryParams>({
  userId: null as any,
  action: '',
  category: '',
  severity: '',
  status: '',
  keyword: '',
  startDate: '',
  endDate: '',
  page: 0,
  size: 20
})

async function fetchData() {
  const params = {
    ...filters.value,
    page: currentMeta.value.page,
    size: currentMeta.value.pageSize
  }
  
  if (activeLogType.value === 'AUDIT') {
    await logStore.fetchAuditLogs(params)
  } else {
    await logStore.fetchBusinessLogs(params)
  }
}

onMounted(() => {
  fetchData()
  logStore.fetchLogStatistics()
})

function handleTabChange(type: 'AUDIT' | 'BUSINESS') {
  activeLogType.value = type
  currentMeta.value.page = 0
  fetchData()
}

function handleApplyFilters() {
  currentMeta.value.page = 0
  fetchData()
}

function handlePageChange(page: number) {
  currentMeta.value.page = page - 1
  fetchData()
}

function handleReset() {
  filters.value = {
    userId: null as any,
    action: '',
    category: '',
    severity: '',
    status: '',
    keyword: '',
    startDate: '',
    endDate: '',
    page: 0,
    size: 20
  }
  currentMeta.value.page = 0
  fetchData()
}

function handleView(id: number) {
  router.push({ name: 'recruiter-activity-log-detail', params: { id, type: activeLogType.value.toLowerCase() } })
}

// KPI Stats — mapped from BE API
const kpiStats = computed(() => {
  const s = statistics.value
  return [
    { 
      label: 'Tổng hoạt động',      
      value: s ? s.totalActivity : 0,    
      icon: 'analytics',    
      iconBg: 'bg-primary/10', 
      iconColor: 'text-primary',
      note: 'Quy mô tương tác chung'
    },
    { 
      label: 'Xử lý hồ sơ',      
      value: s ? s.candidateProcessing : 0, 
      icon: 'person_check', 
      iconBg: 'bg-emerald-100 dark:bg-emerald-900/30', 
      iconColor: 'text-emerald-600 dark:text-emerald-400',
      note: 'Duyệt & Chuyển trạng thái'
    },
    { 
      label: 'Cập nhật dữ liệu',     
      value: s ? s.dataUpdates : 0,         
      icon: 'edit_document', 
      iconBg: 'bg-blue-100 dark:bg-blue-900/30', 
      iconColor: 'text-blue-600 dark:text-blue-400',
      note: 'Tin tuyển dụng & Công ty'
    },
    { 
      label: 'Thành viên hoạt động', 
      value: s ? s.activeMembers : 0,       
      icon: 'group',         
      iconBg: 'bg-purple-100 dark:bg-purple-900/30', 
      iconColor: 'text-purple-600 dark:text-purple-400',
      note: 'Employer trong công ty'
    },
  ]
})
</script>