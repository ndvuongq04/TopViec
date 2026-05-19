<template>
  <div v-if="loading" class="flex flex-col items-center justify-center py-20 gap-4">
    <span class="material-symbols-outlined animate-spin text-4xl text-primary">progress_activity</span>
    <span class="text-slate-500 font-bold">Đang tải chi tiết...</span>
  </div>

  <div v-else-if="currentLog" class="space-y-6 font-body pt-6 pb-20">
    <!-- Breadcrumb -->
    <Breadcrumb :items="breadcrumbItems" hide-home />

    <!-- Header Section (Premium Style) -->
    <div class="bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800 shadow-sm overflow-hidden">
      <div class="h-2 bg-gradient-to-r from-primary via-blue-500 to-indigo-600"></div>
      <div class="p-6 md:p-8 flex flex-col md:flex-row md:items-center justify-between gap-6">
        <div class="space-y-2">
          <div class="flex items-center gap-3 flex-wrap">
            <span 
              class="px-3 py-1 rounded-full text-[10px] font-extrabold uppercase tracking-widest border"
              :class="logType === 'audit' ? 'bg-blue-50 text-blue-600 border-blue-100 dark:bg-blue-900/20 dark:border-blue-800' : 'bg-indigo-50 text-indigo-600 border-indigo-100 dark:bg-indigo-900/20 dark:border-indigo-800'"
            >
              {{ logType === 'audit' ? 'Audit Log' : 'Business Log' }}
            </span>
            <span class="text-slate-300">|</span>
            <span class="text-slate-500 font-mono text-sm">ID: #{{ currentLog.id }}</span>
          </div>
          <h2 class="text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight">
            {{ LOG_ACTION_TYPE_LABELS[currentLog.action as keyof typeof LOG_ACTION_TYPE_LABELS] || currentLog.action }}
          </h2>
          <p class="text-slate-500 flex items-center gap-2">
            <span class="material-symbols-outlined text-lg">category</span>
            {{ LOG_CATEGORY_LABELS[currentLog.category as keyof typeof LOG_CATEGORY_LABELS] || currentLog.category }}
          </p>
        </div>

        <div class="flex flex-col items-end gap-3">
          <div 
            class="flex items-center gap-2 px-4 py-2 rounded-xl font-bold border"
            :class="currentLog.status?.toLowerCase() === 'success' ? 'bg-emerald-50 text-emerald-600 border-emerald-100' : 'bg-rose-50 text-rose-600 border-rose-100'"
          >
            <span class="size-2 rounded-full animate-pulse" :class="currentLog.status?.toLowerCase() === 'success' ? 'bg-emerald-500' : 'bg-rose-500'"></span>
            {{ currentLog.status }}
          </div>
          <div v-if="logType === 'audit' && (currentLog as any).severity" class="flex items-center gap-2">
             <span class="text-xs text-slate-400 font-bold uppercase tracking-widest">Mức độ rủi ro:</span>
             <span 
              class="px-2 py-0.5 rounded text-[10px] font-extrabold border"
              :class="getSeverityClass((currentLog as any).severity)"
             >
               {{ SEVERITY_LABELS[(currentLog as any).severity as keyof typeof SEVERITY_LABELS] || (currentLog as any).severity }}
             </span>
          </div>
        </div>
      </div>
    </div>

    <!-- Content Cards (Old Style - Optimized) -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <ActivityLogDetailActor :actor="mappedActor" />
      <ActivityLogDetailTarget :target="mappedTarget" />
    </div>

    <div class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm overflow-hidden">
      <div class="p-6 border-b border-slate-100 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-800/30 flex items-center justify-between">
        <h3 class="font-extrabold text-slate-800 dark:text-slate-200 flex items-center gap-2 uppercase text-xs tracking-widest">
          <span class="material-symbols-outlined text-lg text-primary">analytics</span>
          {{ logType === 'audit' ? 'Chi tiết thay đổi' : 'Thông tin nghiệp vụ' }}
        </h3>
        <span class="text-[10px] font-bold text-slate-400 italic">Dữ liệu hệ thống</span>
      </div>
      <div class="p-6">
        <ActivityLogDetailDiff v-if="logType === 'audit'" :changes="[]" />
        <div v-else class="space-y-4">
          <p class="text-sm text-slate-500 italic">Dữ liệu Metadata được ghi lại cho sự kiện này:</p>
          <div class="bg-slate-900 rounded-xl p-6 relative group overflow-hidden">
             <div class="absolute top-0 right-0 p-2 opacity-0 group-hover:opacity-100 transition-opacity">
                <button class="p-1.5 text-slate-400 hover:text-white transition-colors" title="Copy JSON">
                  <span class="material-symbols-outlined text-lg">content_copy</span>
                </button>
             </div>
             <pre class="text-xs font-mono text-emerald-400 whitespace-pre-wrap leading-relaxed">{{ JSON.stringify(selectedBusinessLog?.metadata || {}, null, 2) }}</pre>
          </div>
        </div>
      </div>
    </div>

    <ActivityLogDetailNote v-if="(currentLog as any).description" :note="(currentLog as any).description" />
  </div>

  <div v-else class="py-20 text-center">
    <div class="inline-flex items-center justify-center size-20 rounded-full bg-slate-100 dark:bg-slate-800 mb-4">
      <span class="material-symbols-outlined text-4xl text-slate-300">search_off</span>
    </div>
    <h3 class="text-xl font-bold text-slate-900 dark:text-white">Không tìm thấy nhật ký</h3>
    <p class="text-slate-500 mt-2">Dữ liệu này có thể đã bị xóa hoặc bạn không có quyền truy cập.</p>
    <button @click="router.back()" class="mt-8 px-8 py-3 bg-primary text-white rounded-xl font-bold shadow-lg shadow-primary/20 hover:scale-[1.02] active:scale-95 transition-all">
      Quay lại danh sách
    </button>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useEmployerLogStore } from '@/stores/employerLog.store'
import Breadcrumb from '@/components/ui/Breadcrumb.vue'
import ActivityLogDetailActor from '@/components/recruiter/activity-log/activity-detail-log/ActivityLogDetailActor.vue'
import ActivityLogDetailTarget from '@/components/recruiter/activity-log/activity-detail-log/ActivityLogDetailTarget.vue'
import ActivityLogDetailDiff from '@/components/recruiter/activity-log/activity-detail-log/ActivityLogDetailDiff.vue'
import ActivityLogDetailNote from '@/components/recruiter/activity-log/activity-detail-log/ActivityLogDetailNote.vue'

import { 
  LOG_ACTION_TYPE_LABELS, 
  LOG_CATEGORY_LABELS, 
  SEVERITY_LABELS,
  USER_ROLE_LABELS
} from '@/constants/logs.constants'

const route = useRoute()
const router = useRouter()
const logStore = useEmployerLogStore()
const { selectedAuditLog, selectedBusinessLog, loading } = storeToRefs(logStore)

const logId = computed(() => Number(route.params.id))
const logType = computed(() => route.params.type as 'audit' | 'business')

const breadcrumbItems = computed(() => [
  { label: 'Nhật ký hoạt động', to: { name: 'recruiter-activity-log' } },
  { label: 'Chi tiết nhật ký' }
])

const currentLog = computed(() => logType.value === 'audit' ? selectedAuditLog.value : selectedBusinessLog.value)

const mappedActor = computed(() => {
  if (!currentLog.value) return null
  const log = currentLog.value as any
  return {
    name: log.userEmail || 'Hệ thống',
    role: USER_ROLE_LABELS[log.userRole] || 'Nhà tuyển dụng',
    avatar: '',
    initials: (log.userEmail?.[0] || 'S').toUpperCase(),
    time: formatDate(log.createdAt),
    // Business logs don't have IP/UA in DTO
    ip: logType.value === 'audit' ? (log.ipAddress || 'Không xác định') : null,
    userAgent: logType.value === 'audit' ? (log.userAgent || 'Không xác định') : null
  }
})

const mappedTarget = computed(() => {
  if (!currentLog.value) return null
  const log = currentLog.value
  return {
    type: log.targetEntity || 'N/A',
    name: log.targetName || log.targetEntity || 'Đối tượng hệ thống',
    targetId: String(log.targetId || '-'),
    subInfo: `Phân loại: ${LOG_CATEGORY_LABELS[log.category as keyof typeof LOG_CATEGORY_LABELS] || log.category}`
  }
})

onMounted(async () => {
  if (logId.value) {
    if (logType.value === 'audit') {
      await logStore.fetchAuditLogDetail(logId.value)
    } else {
      await logStore.fetchBusinessLogDetail(logId.value)
    }
  }
})

function formatDate(dateStr: string) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('vi-VN', {
    hour: '2-digit', minute: '2-digit', day: '2-digit', month: '2-digit', year: 'numeric'
  })
}

function getSeverityClass(severity: string) {
  switch (severity) {
    case 'CRITICAL': return 'bg-rose-50 text-rose-600 border-rose-100'
    case 'HIGH': return 'bg-orange-50 text-orange-600 border-orange-100'
    case 'MEDIUM': return 'bg-amber-50 text-amber-600 border-amber-100'
    default: return 'bg-slate-50 text-slate-500 border-slate-100'
  }
}
</script>