<template>
  <tr class="table-row">
    <!-- Job info -->
    <td class="td">
      <div class="job-info">
        <span class="job-info__spacer" />

        <div>
          <div class="job-info__title-row">
            <h4 class="job-info__title" :class="{ 'job-info__title--draft': job.status === 'draft' }">
              {{ job.title }}
            </h4>
            <span v-if="job.isUrgent" class="badge badge--urgent">TUYỂN GẤP</span>
            <span v-if="job.isFeatured" class="badge badge--featured">Nổi bật</span>
            <span v-if="job.isHot" class="badge badge--hot">HOT</span>
          </div>
          <p class="job-info__meta">
            {{ job.status === 'draft' ? 'Lưu nháp' : 'Đăng ngày' }}: {{ job.postedAt }}
            • Mã: <span class="job-info__meta-code">{{ job.code }}</span>
          </p>
        </div>
      </div>
    </td>

    <!-- Assignee -->
    <td class="td">
      <div 
        v-if="(job as any).assignedRecruiter" 
        class="group flex items-center gap-3 p-1.5 -ml-1.5 rounded-lg transition-colors"
        :class="canAssign ? 'cursor-pointer hover:bg-slate-50' : 'cursor-not-allowed opacity-75'"
        @click="canAssign && $emit('assign', job.id)"
        :title="!canAssign ? 'Cho phép: published, paused, renewed, interviewing, scheduled, closed' : 'Đổi người phụ trách'"
      >
        <div class="relative">
          <div class="w-8 h-8 rounded-full flex items-center justify-center font-bold text-xs bg-primary/10 text-primary border border-primary/20 shrink-0">
            {{ (job as any).assignedRecruiter.email?.split('@')[0].slice(0, 2).toUpperCase() || 'U' }}
          </div>
        </div>
        <div>
          <p class="font-bold text-slate-900 text-[13px] leading-tight group-hover:text-primary transition-colors">
            {{ (job as any).assignedRecruiter.email?.split('@')[0] }}
          </p>
          <p class="text-[11px] text-slate-400 leading-tight">{{ (job as any).assignedRecruiter.email }}</p>
        </div>
      </div>
      <button 
        v-else
        class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-primary/10 text-primary transition-colors font-bold text-xs border border-primary/20"
        :class="canAssign ? 'hover:bg-primary hover:text-white cursor-pointer' : 'opacity-50 cursor-not-allowed'"
        :disabled="!canAssign"
        :title="!canAssign ? 'Cho phép: published, paused, renewed, interviewing, scheduled, closed' : undefined"
        @click="canAssign && $emit('assign', job.id)"
      >
        <span class="material-symbols-outlined text-[14px]">person_add</span>
        Giao việc
      </button>
    </td>

    <!-- Status -->
    <td class="td">
      <span class="status-chip" :class="statusChipClass">
        <span v-if="job.status === 'active'" class="status-chip__dot status-chip__dot--pulse" />
        {{ statusLabel }}
      </span>
    </td>

    <!-- Performance -->
    <td class="td td--center">
      <div class="performance">
        <div class="performance__item">
          <p class="performance__value">{{ job.views ?? '-' }}</p>
          <p class="performance__label">Lượt xem</p>
        </div>
        <div v-if="job.status !== 'draft'" class="performance__item">
          <p class="performance__value performance__value--primary">{{ job.applicants ?? '-' }}</p>
          <p class="performance__label">Ứng viên</p>
        </div>
      </div>
    </td>

    <!-- Deadline -->
    <td class="td">
      <template v-if="job.deadline">
        <p class="deadline__date" :class="{ 'deadline__date--error': job.status === 'expiring' }">
          {{ job.deadline }}
        </p>
        <p
          v-if="job.daysLeft !== undefined"
          class="deadline__remaining"
          :class="{ 'deadline__remaining--urgent': job.daysLeft <= 3 }"
        >
          Còn {{ job.daysLeft }} ngày
        </p>
      </template>
      <p v-else class="deadline__date deadline__date--muted">-</p>
    </td>

    <!-- Actions -->
    <td class="td td--right">
      <div class="actions">
        <GlobalDropdown>
          <template #default="{ close }">
            <!-- Xem chi tiết: luôn bật -->
            <GlobalDropdownItem
              icon="visibility"
              label="Xem chi tiết"
              @click="handleAction('view', job.id, close)"
            />
            <!-- Danh sách ứng viên: tắt khi draft -->
            <GlobalDropdownItem
              icon="group"
              label="Danh sách ứng viên"
              :disabled="!canViewApplications"
              :tooltip="!canViewApplications ? 'Tin nháp chưa có ứng viên nộp đơn' : undefined"
              @click="handleAction('applications', job.id, close)"
            />
            <!-- Phân công: tất cả trạng thái trừ xóa -->
            <GlobalDropdownItem
              v-if="job.status !== 'deleted'"
              icon="person_add"
              :label="(job as any).assignedRecruiter ? 'Đổi người phụ trách' : 'Phân công'"
              :disabled="!canAssign"
              :tooltip="!canAssign ? 'Cho phép: published, paused, renewed, interviewing, scheduled, closed' : undefined"
              @click="handleAction('assign', job.id, close)"
            />
            <div class="dropdown-divider-v2" />
            <!-- Chỉnh sửa: DRAFT/REJECTED/RENEWED luôn được; PUBLISHED chỉ khi editCount < 1 -->
            <GlobalDropdownItem
              v-if="job.status !== 'deleted'"
              icon="edit"
              label="Chỉnh sửa tin"
              :disabled="!canEdit"
              :tooltip="!canEdit ? editDisabledReason : undefined"
              @click="handleAction('edit', job.id, close)"
            />
            <!-- Gửi duyệt: DRAFT hoặc REJECTED -->
            <GlobalDropdownItem
              v-if="job.status !== 'deleted'"
              icon="send"
              label="Gửi tin cho duyệt"
              :disabled="!canSubmit"
              :tooltip="!canSubmit ? 'Chỉ có thể gửi duyệt khi tin đang ở trạng thái Nháp hoặc Bị từ chối' : undefined"
              @click="handleAction('submit', job.id, close)"
            />
            <GlobalDropdownItem
              v-if="job.status !== 'deleted' && job.status !== 'paused'"
              icon="pause_circle"
              label="Tạm dừng tin tuyển dụng"
              :disabled="!canPause"
              :tooltip="!canPause && job.status !== 'paused' ? 'Chỉ có thể tạm dừng khi tin đang ở trạng thái Đang tuyển' : undefined"
              @click="handleAction('pause', job.id, close)"
            />
            <!-- Tiếp tục đăng: chỉ khi PAUSED -->
            <GlobalDropdownItem
              v-if="job.status === 'paused' || job.status === 'hidden'"
              icon="play_circle"
              label="Tiếp tục đăng tin"
              :disabled="!canResume"
              :tooltip="!canResume ? 'Chỉ có thể tiếp tục khi tin đang ở trạng thái Tạm dừng hoặc Đã ẩn' : undefined"
              @click="handleAction('resume', job.id, close)"
            />
            <!-- Gia hạn: chỉ khi EXPIRED -->
            <GlobalDropdownItem
              v-if="job.status !== 'deleted'"
              icon="update"
              label="Gia hạn tin tuyển dụng"
              :disabled="!canExtend"
              :tooltip="!canExtend ? 'Chỉ có thể gia hạn khi tin đã ở trạng thái Hết hạn' : undefined"
              @click="handleAction('extend', job.id, close)"
            />
            <!-- Làm mới: PUBLISHED hoặc RENEWED -->
            <GlobalDropdownItem
              v-if="job.status !== 'deleted'"
              icon="refresh"
              label="Làm mới tin tuyển dụng"
              :disabled="!canRefresh"
              :tooltip="!canRefresh ? 'Chỉ có thể làm mới khi tin đang ở trạng thái Đang tuyển hoặc Đã gia hạn' : undefined"
              @click="handleAction('refresh', job.id, close)"
            />
            <!-- Bắt đầu phỏng vấn: chỉ khi CLOSED -->
            <GlobalDropdownItem
              v-if="job.status !== 'deleted'"
              icon="groups"
              label="Bắt đầu phỏng vấn"
            
    
              @click="handleAction('interview', job.id, close)"
            />
            <div class="dropdown-divider-v2" v-if="job.status !== 'deleted'" />
            <!-- Đóng tin: PUBLISHED, PAUSED hoặc INTERVIEWING -->
            <GlobalDropdownItem
              v-if="job.status !== 'deleted'"
              icon="block"
              label="Đóng tin tuyển dụng"
              :disabled="!canClose"
              :tooltip="!canClose ? 'Chỉ có thể đóng tin khi đang ở trạng thái Đang tuyển, Tạm dừng hoặc Đang phỏng vấn' : undefined"
              @click="handleAction('close', job.id, close)"
            />
            <!-- Xóa tin: tất cả trừ PUBLISHED -->
            <GlobalDropdownItem
              v-if="job.status !== 'deleted'"
              icon="delete"
              label="Xóa tin"
              :disabled="!canDelete"
              :tooltip="!canDelete ? 'Không thể xóa tin đang ở trạng thái Đang tuyển, hãy đóng tin trước' : undefined"
              danger
              @click="handleAction('delete', job.id, close)"
            />
            <!-- Khôi phục: chỉ khi DELETED -->
            <GlobalDropdownItem
              v-if="job.status === 'deleted'"
              icon="restore_from_trash"
              label="Khôi phục tin"
              @click="handleAction('restore', job.id, close)"
            />
          </template>
        </GlobalDropdown>
      </div>
    </td>
  </tr>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import GlobalDropdown from '@/components/ui/GlobalDropdown.vue'
import GlobalDropdownItem from '@/components/ui/GlobalDropdownItem.vue'
import type { JobPostingRow } from '@/types/employerJobPosting.types'
import { JobPostingStatus } from '@/constants/jobPosting.constants'

const props = defineProps<{ job: JobPostingRow }>()

const emit = defineEmits<{
  view:      [id: number]
  edit:      [id: number]
  copy:      [id: number]
  submit:    [id: number]
  pause:     [id: number]
  resume:    [id: number]
  extend:    [id: number]
  refresh:   [id: number]
  interview: [id: number]
  close:     [id: number]
  delete:    [id: number]
  restore:   [id: number]
  applications: [id: number]
  assign:    [id: number]
}>()

function handleAction(event: any, id: number, close: () => void) {
  close()
  emit(event, id)
}

const canAssign = computed(() => {
  const allowed = [
    JobPostingStatus.PUBLISHED,
    JobPostingStatus.PAUSED,
    JobPostingStatus.RENEWED,
    JobPostingStatus.INTERVIEWING,
    JobPostingStatus.SCHEDULED,
    JobPostingStatus.CLOSED
  ]
  return allowed.includes(props.job.rawStatus as JobPostingStatus)
})

// Xem chi tiết: luôn bật — không cần computed

// Danh sách ứng viên: tắt khi draft
const canViewApplications = computed(() => props.job.status !== 'draft')

// Chỉnh sửa: DRAFT/REJECTED/RENEWED luôn được; PUBLISHED/EXPIRING chỉ khi editCount < 1
const canEdit = computed(() => {
  const s = props.job.status
  if (['draft', 'rejected', 'renewed'].includes(s)) return true
  if (s === 'active' || s === 'expiring') return (props.job.editCount ?? 0) < 1
  return false
})
const editDisabledReason = computed(() => {
  const s = props.job.status
  if (s === 'active' || s === 'expiring')
    return 'Tin đã được chỉnh sửa 1 lần sau khi đăng, không thể chỉnh sửa thêm'
  return 'Không thể chỉnh sửa tin ở trạng thái này'
})

// Gửi duyệt: DRAFT hoặc REJECTED
const canSubmit = computed(() => ['draft', 'rejected'].includes(props.job.status))

// Tạm dừng: chỉ khi PUBLISHED (active/expiring)
const canPause = computed(() => ['active', 'expiring'].includes(props.job.status))

// Tiếp tục: chỉ khi PAUSED
const canResume = computed(() => ['paused', 'hidden'].includes(props.job.status))

// Gia hạn: chỉ khi EXPIRED
const canExtend = computed(() => props.job.status === 'expired')

// Làm mới: PUBLISHED (active/expiring) hoặc RENEWED
const canRefresh = computed(() => ['active', 'expiring', 'renewed'].includes(props.job.status))

// Bắt đầu phỏng vấn: chỉ khi CLOSED
const canInterview = computed(() => props.job.status === 'closed')

// Đóng tin: PUBLISHED, PAUSED hoặc INTERVIEWING
const canClose = computed(() => ['active', 'expiring', 'paused', 'interviewing'].includes(props.job.status))

// Xóa tin: tất cả trừ PUBLISHED (active/expiring) và DELETED
const canDelete = computed(() => !['active', 'expiring', 'deleted'].includes(props.job.status))

// Khôi phục: chỉ khi DELETED
const canRestore = computed(() => props.job.status === 'deleted')

const statusChipClass = computed(() => ({
  'status-chip--active':       props.job.status === 'active',
  'status-chip--pending':      props.job.status === 'pending',
  'status-chip--expiring':     props.job.status === 'expiring',
  'status-chip--draft':        props.job.status === 'draft',
  'status-chip--hidden':       props.job.status === 'hidden',
  'status-chip--closed':       props.job.status === 'closed',
  'status-chip--expired':      props.job.status === 'expired',
  'status-chip--paused':       props.job.status === 'paused',
  'status-chip--rejected':     props.job.status === 'rejected',
  'status-chip--interviewing': props.job.status === 'interviewing',
  'status-chip--completed':    props.job.status === 'completed',
  'status-chip--deleted':      props.job.status === 'deleted',
}))

const statusLabel = computed(() => ({
  active:       'Đang tuyển',
  pending:      'Chờ duyệt',
  expiring:     'Sắp hết hạn',
  draft:        'Nháp',
  hidden:       'Đã ẩn',
  closed:       'Đã đóng',
  expired:      'Hết hạn',
  paused:       'Tạm dừng',
  rejected:     'Bị từ chối',
  interviewing: 'Đang phỏng vấn',
  completed:    'Đã hoàn thành',
  deleted:      'Đã xóa',
}[props.job.status] ?? props.job.status))
</script>

<style scoped>
.table-row { transition: background 0.15s; }
.table-row:hover { background: rgba(248,250,252,0.5); }

.td {
  padding: 1.25rem 1.5rem;
  vertical-align: middle;
}
.td--center { text-align: center; }
.td--right  { text-align: right; }

/* Job info */
.job-info { display: flex; align-items: flex-start; gap: 0.75rem; }
.job-info__icon { margin-top: 0.125rem; flex-shrink: 0; font-size: 1.25rem !important; color: var(--color-primary); }
.job-info__icon--star { color: var(--color-tertiary); }
.job-info__spacer { width: 1.25rem; flex-shrink: 0; }
.job-info__title-row { display: flex; align-items: center; gap: 0.5rem; flex-wrap: wrap; }
.job-info__title { font-weight: 700; font-size: 1.125rem; color: var(--color-on-surface); margin: 0; }
.job-info__title--draft { font-style: italic; opacity: 0.7; }
.job-info__meta { font-size: 0.875rem; color: var(--color-on-surface-muted); margin: 0.25rem 0 0; }
.job-info__meta-code { font-family: monospace; }

/* Badges */
.badge { display: inline-flex; align-items: center; padding: 0.125rem 0.5rem; border-radius: 0.25rem; font-size: 0.75rem; font-weight: 800; text-transform: uppercase; letter-spacing: 0.04em; }
.badge--urgent  { background: #fef3c7; color: #b45309; border: 1.5px solid #f59e0b; border-radius: var(--radius-full); padding: 0.25rem 0.75rem; }
.badge--featured { background: var(--color-primary-light); color: var(--color-primary-text); border: 1.5px solid #93c5fd; border-radius: var(--radius-full); padding: 0.25rem 0.75rem; }
.badge--hot { background: #fee2e2; color: #b91c1c; border: 1.5px solid #fca5a5; border-radius: var(--radius-full); padding: 0.25rem 0.75rem; }

/* Status chips */
.status-chip { display: inline-flex; align-items: center; gap: 0.375rem; padding: 0.25rem 0.75rem; border-radius: var(--radius-full); font-size: 0.875rem; font-weight: 700; white-space: nowrap; }
.status-chip__dot { width: 0.375rem; height: 0.375rem; border-radius: 50%; background: currentColor; }
.status-chip__dot--pulse { animation: pulse 1.5s ease-in-out infinite; }
@keyframes pulse { 0%,100% { opacity:1; } 50% { opacity:0.3; } }
.status-chip--active       { background: #dbeafe; color: #2563eb; }
.status-chip--pending      { background: var(--color-tertiary-light); color: var(--color-tertiary-text); }
.status-chip--expiring     { background: var(--color-error-light);   color: var(--color-error-text); }
.status-chip--draft        { background: #f1f5f9; color: #64748b; }
.status-chip--hidden       { background: #fef3c7; color: #92400e; }
.status-chip--closed       { background: #f1f5f9; color: #94a3b8; }
.status-chip--expired      { background: #fee2e2; color: #b91c1c; }
.status-chip--paused       { background: #ffedd5; color: #c2410c; }
.status-chip--rejected     { background: #ffe4e6; color: #be123c; }
.status-chip--interviewing { background: #e0f2fe; color: #0369a1; }
.status-chip--completed    { background: #e0e7ff; color: #4338ca; }

/* Performance */
.performance { display: flex; justify-content: center; gap: 1.5rem; }
.performance__item { text-align: center; }
.performance__value { font-size: 0.875rem; font-weight: 700; color: var(--color-on-surface); margin: 0; }
.performance__value--primary { color: var(--color-primary); }
.performance__label { font-size: 0.75rem; color: var(--color-on-surface-muted); font-weight: 700; margin: 0; }

/* Deadline */
.deadline__date { font-size: 0.875rem; font-weight: 700; color: var(--color-on-surface); margin: 0; }
.deadline__date--error  { color: var(--color-error); }
.deadline__date--muted  { color: var(--color-on-surface-muted); }
.deadline__remaining { font-size: 0.75rem; color: var(--color-on-surface-muted); font-weight: 700; margin: 0.125rem 0 0; }
.deadline__remaining--urgent { color: var(--color-error); font-style: italic; }

/* Actions */
.actions { display: flex; align-items: center; justify-content: flex-end; gap: 0.5rem; }
.btn-action { width: 2rem; height: 2rem; display: flex; align-items: center; justify-content: center; border-radius: var(--radius-sm); border: none; background: none; color: var(--color-on-surface-muted); cursor: pointer; transition: background 0.15s; }
.btn-action:hover { background: #f1f5f9; }
.btn-action--danger:hover { background: rgba(239,68,68,0.08); color: var(--color-error); }
.btn-submit { padding: 0.375rem 0.75rem; background: rgba(75,154,246,0.1); color: var(--color-primary); font-family: inherit; font-size: 0.75rem; font-weight: 800; text-transform: uppercase; letter-spacing: 0.05em; border-radius: var(--radius-sm); border: none; cursor: pointer; transition: background 0.15s, color 0.15s; }
.btn-submit:hover { background: var(--color-primary); color: #fff; }
.btn-extend { padding: 0.375rem 0.75rem; background: none; color: var(--color-on-surface); font-family: inherit; font-size: 0.75rem; font-weight: 800; text-transform: uppercase; letter-spacing: 0.05em; border-radius: var(--radius-sm); border: 1px solid var(--color-border); cursor: pointer; transition: background 0.15s; }
.btn-extend:hover { background: #f1f5f9; }
.icon-xl { font-size: 1.25rem !important; }

/* Divider inside global dropdown content */
.dropdown-divider-v2 {
  height: 1px;
  background: #f1f5f9;
  margin: 0.375rem 0.25rem;
}
</style>
