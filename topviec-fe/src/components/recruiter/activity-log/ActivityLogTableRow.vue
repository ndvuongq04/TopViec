<template>
  <tr class="hover:bg-slate-50/80 dark:hover:bg-slate-800/40 transition-colors group">
    <!-- Thời gian -->
    <td class="px-6 py-4 whitespace-nowrap">
      <p class="text-sm font-bold text-slate-900 dark:text-white">{{ formatDate(log.createdAt).time }}</p>
      <p class="text-xs text-slate-500 dark:text-slate-400 mt-0.5">{{ formatDate(log.createdAt).date }}</p>
    </td>

    <!-- Vai trò -->
    <td class="px-6 py-4 text-center">
      <span 
        v-if="log.userRole"
        class="px-2 py-0.5 rounded-full bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 text-[10px] font-bold uppercase"
      >
        {{ USER_ROLE_LABELS[log.userRole] || log.userRole }}
      </span>
      <span v-else class="text-slate-300">-</span>
    </td>

    <!-- Thành viên -->
    <td class="px-6 py-4 min-w-[200px]">
      <div class="flex items-center gap-3">
        <div class="size-8 rounded-full bg-primary/10 text-primary flex items-center justify-center font-bold text-xs shrink-0 border border-slate-200 dark:border-slate-700">
          {{ (log.userEmail?.[0] || 'S').toUpperCase() }}
        </div>
        <div>
          <p class="text-sm font-bold text-slate-900 dark:text-white truncate max-w-[150px]">{{ log.userEmail || 'Hệ thống' }}</p>
          <p class="text-xs text-slate-400 mt-0.5 italic">{{ USER_ROLE_LABELS[log.userRole] || 'Nhà tuyển dụng' }}</p>
        </div>
      </div>
    </td>

    <!-- Hành động -->
    <td class="px-6 py-4">
      <span class="text-sm font-bold text-slate-700 dark:text-slate-200 whitespace-nowrap">
        {{ LOG_ACTION_TYPE_LABELS[log.action as keyof typeof LOG_ACTION_TYPE_LABELS] || log.action }}
      </span>
      <p class="text-[10px] text-slate-400 font-bold uppercase tracking-widest mt-1">
        {{ LOG_CATEGORY_LABELS[log.category as keyof typeof LOG_CATEGORY_LABELS] || log.category }}
      </p>
    </td>

    <!-- Đối tượng -->
    <td class="px-6 py-4">
      <div class="flex flex-col">
        <p class="text-sm font-bold text-slate-900 dark:text-white truncate max-w-[220px]">
          {{ log.targetName || log.targetEntity || 'N/A' }}
        </p>
        <p class="text-[10px] text-slate-400 font-bold uppercase tracking-tight mt-0.5">
          {{ log.targetEntity }}: {{ log.targetId || '-' }}
        </p>
      </div>
    </td>

    <!-- Mức độ (Chỉ AUDIT) -->
    <td v-if="type === 'AUDIT'" class="px-6 py-4 text-center">
      <span 
        v-if="(log as any).severity"
        :class="[
          'px-2 py-1 rounded text-[10px] font-extrabold uppercase tracking-widest',
          getSeverityClass((log as any).severity)
        ]"
      >
        {{ SEVERITY_LABELS[(log as any).severity as keyof typeof SEVERITY_LABELS] || (log as any).severity }}
      </span>
      <span v-else class="text-slate-300">-</span>
    </td>

    <!-- Kết quả -->
    <td class="px-6 py-4 text-center">
      <div
        class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-[10px] font-extrabold uppercase tracking-widest"
        :class="log.status?.toLowerCase() === 'success' ? 'bg-emerald-100 dark:bg-emerald-900/30 text-emerald-600' : 'bg-rose-100 dark:bg-rose-900/30 text-rose-600'"
      >
        <span class="size-1.5 rounded-full" :class="log.status?.toLowerCase() === 'success' ? 'bg-emerald-500' : 'bg-rose-500'"></span>
        {{ log.status }}
      </div>
    </td>

    <!-- Chi tiết -->
    <td class="px-6 py-4 text-center">
      <button
        class="p-2 text-slate-400 hover:text-primary hover:bg-primary/10 rounded-xl transition-all cursor-pointer"
        title="Xem chi tiết"
        @click="$emit('view')"
      >
        <span class="material-symbols-outlined text-lg">visibility</span>
      </button>
    </td>
  </tr>
</template>

<script setup lang="ts">
import { 
  LOG_ACTION_TYPE_LABELS, 
  LOG_CATEGORY_LABELS, 
  SEVERITY_LABELS,
  USER_ROLE_LABELS
} from '@/constants/logs.constants'

const props = defineProps<{
  log: any,
  type: 'AUDIT' | 'BUSINESS'
}>()

defineEmits<{ view: [] }>()

function formatDate(dateStr: string) {
  if (!dateStr) return { date: '-', time: '-' }
  const d = new Date(dateStr)
  return {
    date: d.toLocaleDateString('vi-VN'),
    time: d.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })
  }
}

function getSeverityClass(severity: string) {
  switch (severity) {
    case 'CRITICAL': return 'bg-rose-100 dark:bg-rose-900/30 text-rose-600 border border-rose-200 dark:border-rose-800'
    case 'HIGH': return 'bg-orange-100 dark:bg-orange-900/30 text-orange-600 border border-orange-200 dark:border-orange-800'
    case 'MEDIUM': return 'bg-amber-100 dark:bg-amber-900/30 text-amber-600 border border-amber-200 dark:border-amber-800'
    default: return 'bg-slate-100 dark:bg-slate-800 text-slate-500 border border-slate-200 dark:border-slate-700'
  }
}
</script>