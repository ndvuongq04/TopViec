<template>
  <div class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 overflow-hidden">
    <div class="overflow-x-auto">
      <table class="w-full text-left border-collapse">
        <thead>
          <tr class="bg-slate-50/50 dark:bg-slate-800/50 border-b border-slate-200 dark:border-slate-800">
            <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider whitespace-nowrap">Thời gian</th>
            <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider whitespace-nowrap">Admin</th>
            <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider whitespace-nowrap">
              Vai trò
            </th>
            <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider whitespace-nowrap">Danh mục</th>
            <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider whitespace-nowrap">Hành động</th>
            <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider whitespace-nowrap">Đối tượng</th>
            <th v-if="type === 'AUDIT'" class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider whitespace-nowrap">Mức độ</th>
            <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider whitespace-nowrap">Log ID</th>
            <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-center whitespace-nowrap">Kết quả</th>
            <th class="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-center whitespace-nowrap">Thao tác</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-200 dark:divide-slate-800">
          <tr 
            v-if="logs.length === 0"
            class="text-center py-10"
          >
            <td :colspan="type === 'AUDIT' ? 11 : 10" class="px-6 py-10 text-slate-400 italic">Không tìm thấy nhật ký nào</td>
          </tr>
          <tr 
            v-for="log in (logs as any[])" 
            :key="log.id" 
            class="hover:bg-slate-50 dark:hover:bg-slate-800/40 transition-colors group"
          >
            <td class="px-6 py-4 text-sm text-slate-500 whitespace-nowrap">{{ formatDate(log.createdAt) }}</td>
            <td class="px-6 py-4 text-sm font-medium text-slate-700 dark:text-slate-300">
              <div class="flex flex-col">
                <span>{{ log.userEmail || 'System' }}</span>
                <span class="text-[10px] text-slate-400">ID: {{ log.userId || 'N/A' }}</span>
              </div>
            </td>
            <td class="px-6 py-4">
              <span 
                v-if="log.userRole"
                class="px-2 py-0.5 rounded-full bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 text-[10px] font-bold uppercase"
              >
                {{ USER_ROLE_LABELS[log.userRole] || log.userRole }}
              </span>
              <span v-else class="text-xs text-slate-400 italic">N/A</span>
            </td>
            <td class="px-6 py-4 text-sm text-slate-500 whitespace-nowrap">
              {{ LOG_CATEGORY_LABELS[log.category as keyof typeof LOG_CATEGORY_LABELS] || log.category }}
            </td>
            <td class="px-6 py-4 text-sm font-medium text-slate-700 dark:text-slate-300 whitespace-nowrap">
              {{ LOG_ACTION_TYPE_LABELS[log.action as keyof typeof LOG_ACTION_TYPE_LABELS] || log.action }}
            </td>
            <td class="px-6 py-4 text-sm font-medium text-slate-700 dark:text-slate-300">
              <div class="flex flex-col">
                <span class="whitespace-nowrap text-[10px] text-slate-400 uppercase tracking-tight">{{ log.targetEntity }}: {{ log.targetId }}</span>
                <span v-if="log.targetName" class="font-bold text-slate-900 dark:text-white">{{ log.targetName }}</span>
                <span v-else class="text-xs italic text-slate-400 font-normal">Không có tên đối tượng</span>
              </div>
            </td>
            <td v-if="type === 'AUDIT'" class="px-6 py-4">
              <span 
                v-if="log.severity"
                class="inline-block px-3 py-1 rounded-full text-xs font-bold whitespace-nowrap uppercase"
                :class="SEVERITY_COLORS[log.severity as keyof typeof SEVERITY_COLORS]"
              >{{ SEVERITY_LABELS[log.severity as keyof typeof SEVERITY_LABELS] || log.severity }}</span>
            </td>
            <td class="px-6 py-4 text-sm font-mono text-slate-500 whitespace-nowrap">#{{ log.id }}</td>
            <td class="px-6 py-4 text-center">
              <div 
                class="w-6 h-6 rounded-full inline-flex items-center justify-center"
                :class="statusClass(log.status)"
              >
                <span class="material-symbols-outlined text-[16px]">{{ log.status === 'SUCCESS' ? 'check' : 'close' }}</span>
              </div>
            </td>
            <td class="px-6 py-4 text-center">
              <div class="flex items-center justify-center gap-1">
                <button
                  class="p-1.5 text-slate-400 hover:text-[#963131] transition-colors cursor-pointer"
                  title="Xem chi tiết"
                  @click="$emit('view', log.id)"
                >
                  <span class="material-symbols-outlined text-lg">visibility</span>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ResAuditLogDTO, ResBusinessEventLogDTO } from '@/types/logs.types'
import { 
  LOG_ACTION_TYPE_LABELS, 
  LOG_CATEGORY_LABELS, 
  SEVERITY_LABELS, 
  SEVERITY_COLORS,
  USER_ROLE_LABELS
} from '@/constants/logs.constants'

defineProps<{
  logs: (ResAuditLogDTO | ResBusinessEventLogDTO)[]
  type: 'AUDIT' | 'BUSINESS'
}>()

defineEmits<{
  view: [id: number]
}>()

function formatDate(dateStr: string) {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return d.toLocaleString('vi-VN', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  })
}

function statusClass(status: string) {
  return status === 'SUCCESS' 
    ? 'bg-emerald-50 text-emerald-700' 
    : 'bg-red-50 text-red-700'
}
</script>