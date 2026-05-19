<template>
  <div class="tab-card">
    <div v-if="applications.length > 0" class="timeline">
      <div
        v-for="app in applications"
        :key="app.id"
        class="timeline-item"
      >
        <div class="timeline-dot" :class="isDotActive(app.status) ? 'dot-active' : 'dot-inactive'"></div>
        <div class="timeline-body">
          <p class="app-date text-[10px]">{{ formatDate(app.createdAt) }}</p>
          <h4 class="app-position text-[14px] font-bold">{{ app.jobPosting?.title || 'Công việc không xác định' }}</h4>
          <p class="app-company text-[13px] text-slate-600">{{ app.jobPosting?.company?.name || 'Công ty không xác định' }}</p>
          
          <div class="mt-2 flex flex-wrap gap-2">
            <span 
              class="app-status px-2 py-0.5 rounded text-[11px] font-bold" 
              :style="getStatusStyle(app.status)"
            >
              {{ getStatusLabel(app.status) }}
            </span>
            <span class="text-[11px] text-slate-400 self-center">
              Phương thức: {{ getApplyMethodLabel(app.applyMethod) }}
            </span>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else class="text-center py-10 text-slate-400">
      <span class="material-symbols-outlined text-4xl block mb-2">send</span>
      Ứng viên chưa ứng tuyển công việc nào
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ResApplication } from '@/types/application.types'
import { APPLICATION_STATUS_OPTIONS, APPLY_METHOD_OPTIONS } from '@/constants/application.constants'

defineProps<{
  applications: ResApplication[]
}>()

const getStatusLabel = (status: string) => {
  return APPLICATION_STATUS_OPTIONS.find(opt => opt.value === status)?.label || status
}

const getApplyMethodLabel = (method: string) => {
  return APPLY_METHOD_OPTIONS.find(opt => opt.value === method)?.label || method
}

const getStatusStyle = (status: string) => {
  const option = APPLICATION_STATUS_OPTIONS.find(opt => opt.value === status)
  const colors: Record<string, string> = {
    blue:    'background: #dbeafe; color: #1e40af;',
    cyan:    'background: #cffafe; color: #155e75;',
    purple:  'background: #f3e8ff; color: #6b21a8;',
    success: 'background: #dcfce7; color: #166534;',
    orange:  'background: #ffedd5; color: #9a3412;',
    red:     'background: #fee2e2; color: #991b1b;',
    gray:    'background: #f1f5f9; color: #475569;',
    default: 'background: #f8fafc; color: #64748b;',
  }
  return colors[option?.color || 'default']
}

const isDotActive = (status: string) => {
  return ['hired', 'interviewing', 'cv_passed', 'offered'].includes(status)
}

const formatDate = (iso: string) => {
  if (!iso) return '—'
  const d = new Date(iso)
  const months = ['Thg 01', 'Thg 02', 'Thg 03', 'Thg 04', 'Thg 05', 'Thg 06', 'Thg 07', 'Thg 08', 'Thg 09', 'Thg 10', 'Thg 11', 'Thg 12']
  return `${d.getDate()} ${months[d.getMonth()]}, ${d.getFullYear()}`
}
</script>

<style scoped>
.tab-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 20px -4px rgba(87, 66, 64, 0.05);
}
.timeline {
  position: relative;
  border-left: 1px solid #e4e2dc;
  margin-left: 12px;
  display: flex;
  flex-direction: column;
  gap: 32px;
}
.timeline-item { position: relative; padding-left: 28px; }
.timeline-dot {
  position: absolute;
  left: -6px; top: 4px;
  width: 12px; height: 12px;
  border-radius: 50%;
  border: 3px solid #fff;
}
.dot-active { background: #004638; box-shadow: 0 0 0 2px #004638; }
.dot-inactive { background: #dcdad3; box-shadow: 0 0 0 2px #dcdad3; }

.app-date {
  font-size: 10px; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.08em;
  color: #574240; margin: 0 0 4px;
}
.app-position { font-size: 14px; font-weight: 700; color: #1b1c18; margin: 0 0 2px; }
.app-company { font-size: 13px; color: #574240; margin: 0 0 8px; }

.app-status {
  display: inline-block;
  padding: 4px 10px;
  font-size: 11px; font-weight: 700;
  border-radius: 4px;
}
.status-interview { background: #e4e2dc; color: #574240; }
.status-rejected { background: #ffdad6; color: #93000a; }
</style>