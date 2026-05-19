<template>
  <div v-if="loading" class="flex flex-col items-center justify-center py-20 gap-4">
    <span class="material-symbols-outlined animate-spin text-4xl text-[#963131]">progress_activity</span>
    <span class="text-slate-500 font-bold">Đang tải chi tiết nhật ký...</span>
  </div>

  <div v-else-if="mappedLog" class="space-y-6 font-body">
    <!-- Breadcrumb -->
    <Breadcrumb :items="breadcrumbItems" hide-home />

    <AuditLogDetailHeader :log="mappedLog" :type="logType" />
    <AuditLogDetailHero :log="mappedLog" :type="logType" />

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 items-start">
      <div class="lg:col-span-2 flex flex-col gap-6">
        <!-- Business Log Metadata -->
        <div v-if="logType === 'business'" class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6">
          <h3 class="text-lg font-bold mb-4 flex items-center gap-2">
            <span class="material-symbols-outlined">data_object</span>
            Dữ liệu nghiệp vụ (Metadata)
          </h3>
          <div class="bg-slate-50 dark:bg-white/5 p-4 rounded-lg">
            <pre class="text-xs font-mono text-slate-700 dark:text-slate-300 overflow-x-auto whitespace-pre-wrap">{{ JSON.stringify(selectedBusinessLog?.metadata || {}, null, 2) }}</pre>
          </div>
        </div>

        <!-- Audit Log Diff (Currently placeholder) -->
        <div v-else class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6">
          <h3 class="text-lg font-bold mb-4 flex items-center gap-2">
            <span class="material-symbols-outlined">difference</span>
            Thay đổi dữ liệu (Chưa hỗ trợ)
          </h3>
          <div class="py-10 text-center border-2 border-dashed border-slate-100 dark:border-slate-800 rounded-lg text-slate-400 italic">
            Dữ liệu so sánh Before/After hiện chưa được API cung cấp
          </div>
        </div>
        
        <AuditLogDetailRequestContext 
          v-if="logType === 'audit'"
          :context="mappedLog.context" 
        />
        
        <!-- [NOTE] Timeline hiện chưa có dữ liệu từ API -->
        <div v-if="logType === 'audit'" class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6">
          <h3 class="text-lg font-bold mb-4 flex items-center gap-2">
            <span class="material-symbols-outlined">timeline</span>
            Dòng thời gian sự kiện (Chưa hỗ trợ)
          </h3>
          <div class="py-10 text-center border-2 border-dashed border-slate-100 dark:border-slate-800 rounded-lg text-slate-400 italic">
            Dữ liệu dòng thời gian hiện chưa được API cung cấp
          </div>
        </div>
      </div>
      <div class="flex flex-col gap-6">
        <!-- Risk Panel (Chỉ quan trọng cho Audit) -->
        <AuditLogDetailRiskPanel 
          v-if="logType === 'audit'"
          :risk="mappedLog.risk" 
        />
        
        <AuditLogDetailRelatedInfo :related="mappedLog.related" />
        
        <!-- [NOTE] Nearby logs hiện chưa hỗ trợ -->
        <div v-if="logType === 'audit'" class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6">
          <h3 class="text-lg font-bold mb-4 flex items-center gap-2">
            <span class="material-symbols-outlined">history</span>
            Hoạt động lân cận (Chưa hỗ trợ)
          </h3>
          <div class="py-6 text-center border-2 border-dashed border-slate-100 dark:border-slate-800 rounded-lg text-slate-400 italic text-sm">
            Tính năng này đang được phát triển
          </div>
        </div>
      </div>
    </div>
  </div>

  <div v-else class="py-20 text-center">
    <span class="material-symbols-outlined text-6xl text-slate-200">error</span>
    <p class="mt-4 text-slate-500 font-medium">Không tìm thấy thông tin nhật ký này</p>
    <button 
      class="mt-6 px-6 py-2 bg-[#963131] text-white rounded-lg font-bold"
      @click="router.back()"
    >Quay lại</button>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useAdminLogStore } from '@/stores/adminLog.store'
import Breadcrumb from '@/components/ui/Breadcrumb.vue'
import AuditLogDetailHeader from '@/components/admin/audit-log/audit-log-detail/AuditLogDetailHeader.vue'
import AuditLogDetailHero from '@/components/admin/audit-log/audit-log-detail/AuditLogDetailHero.vue'
import AuditLogDetailDiff from '@/components/admin/audit-log/audit-log-detail/AuditLogDetailDiff.vue'
import AuditLogDetailRequestContext from '@/components/admin/audit-log/audit-log-detail/AuditLogDetailRequestContext.vue'
import AuditLogDetailTimeline from '@/components/admin/audit-log/audit-log-detail/AuditLogDetailTimeline.vue'
import AuditLogDetailRiskPanel from '@/components/admin/audit-log/audit-log-detail/AuditLogDetailRiskPanel.vue'
import AuditLogDetailRelatedInfo from '@/components/admin/audit-log/audit-log-detail/AuditLogDetailRelatedInfo.vue'
import AuditLogDetailNearbyLogs from '@/components/admin/audit-log/audit-log-detail/AuditLogDetailNearbyLogs.vue'

import { 
  LOG_ACTION_TYPE_LABELS, 
  LOG_CATEGORY_LABELS, 
  SEVERITY_LABELS,
  USER_ROLE_LABELS
} from '@/constants/logs.constants'

const route = useRoute()
const router = useRouter()
const logStore = useAdminLogStore()
const { selectedAuditLog, selectedBusinessLog, loading } = storeToRefs(logStore)

const logId = computed(() => Number(route.params.id))
const logType = computed(() => route.params.type as 'audit' | 'business')

const breadcrumbItems = computed(() => [
  { label: 'Nhật kí hoạt động', to: { name: 'admin-audit-logs' } },
  { label: logType.value === 'audit' ? 'Chi tiết Audit Log' : 'Chi tiết Business Log' }
])

onMounted(async () => {
  if (logId.value) {
    if (logType.value === 'audit') {
      await logStore.fetchAuditLogDetail(logId.value)
    } else {
      await logStore.fetchBusinessLogDetail(logId.value)
    }
  }
})

// Chuyển đổi từ DTO sang cấu trúc UI yêu cầu
const mappedLog = computed(() => {
  const item = logType.value === 'audit' ? selectedAuditLog.value : selectedBusinessLog.value
  if (!item) return null
  
  return {
    auditId: `#${item.id}`,
    traceId: 'N/A', 
    time: formatDate(item.createdAt),
    admin: { 
      name: item.userEmail || 'System', 
      email: item.userEmail || '-', 
      initials: (item.userEmail?.[0] || 'S').toUpperCase(), 
      role: (item.userRole && USER_ROLE_LABELS[item.userRole]) ? USER_ROLE_LABELS[item.userRole] : (item.userRole || 'ADMIN')
    },
    action: LOG_ACTION_TYPE_LABELS[item.action as keyof typeof LOG_ACTION_TYPE_LABELS] || item.action,
    category: LOG_CATEGORY_LABELS[item.category as keyof typeof LOG_CATEGORY_LABELS] || item.category,
    severity: (item as any).severity || 'LOW', 
    severityLabel: SEVERITY_LABELS[(item as any).severity as keyof typeof SEVERITY_LABELS] || (item as any).severity || 'Thấp',
    resource: { 
      type: item.targetEntity || 'N/A', 
      name: item.targetName || `${item.targetEntity} (ID: ${item.targetId})`, 
      id: String(item.targetId) 
    },
    result: { 
      status: item.status?.toLowerCase() === 'success' ? 'success' : 'error', 
      note: (item as any).description || (item as any).errorMessage || '-'
    },
    context: { 
      ip: (item as any).ipAddress || 'Unknown', 
      userAgent: (item as any).userAgent || 'Unknown', 
      source: 'WEB' 
    },
    risk: {
      level: (item as any).severity || 'LOW',
      reasons: [
        { icon: 'info', text: `Hành động: ${LOG_ACTION_TYPE_LABELS[item.action as keyof typeof LOG_ACTION_TYPE_LABELS] || item.action}` },
        { icon: 'policy', text: `Danh mục: ${LOG_CATEGORY_LABELS[item.category as keyof typeof LOG_CATEGORY_LABELS] || item.category}` },
        { icon: 'history', text: `Thời gian thực hiện: ${item.durationMs}ms` }
      ]
    },
    related: {
      stats: [
        { label: 'Thời gian xử lý', value: `${item.durationMs}ms` },
        { label: 'Tên đối tượng', value: item.targetName || '-' },
        { label: 'Loại đối tượng', value: item.targetEntity || '-' }
      ],
      links: []
    }
  }
})

function formatDate(dateStr: string) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('vi-VN')
}
</script>