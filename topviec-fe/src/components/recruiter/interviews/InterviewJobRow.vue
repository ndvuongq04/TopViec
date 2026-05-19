<template>
  <div class="job-row" :class="{ active: isActive }" @click="$emit('click')">
    <!-- Icon -->
    <div class="job-icon" :class="job.iconVariant">
      <span class="material-symbols-outlined">{{ job.icon }}</span>
    </div>

    <!-- Info grid -->
    <div class="job-info">
      <!-- Title + sub -->
      <div class="job-name-group">
        <div class="job-title">{{ job.title }}</div>
        <div class="job-sub">{{ job.department }} • {{ job.level }}</div>
      </div>

      <!-- Candidate count -->
      <div class="job-meta">
        <span class="material-symbols-outlined">groups</span>
        {{ job.candidateCount }} ứng viên
      </div>

      <!-- Status badge -->
      <div>
        <span class="status-badge" :class="statusBadgeClass">{{ statusLabel }}</span>
      </div>

      <!-- Actions: GlobalDropdown -->
      <div class="action-cell">
        <GlobalDropdown>
          <template #default="{ close }">
            <!-- Primary action -->
            <GlobalDropdownItem
              icon="visibility"
              label="Chi tiết phỏng vấn"
              :disabled="!canViewDetail"
              :tooltip="!canViewDetail ? 'Bạn cần tạo vòng phỏng vấn trước' : undefined"
              @click="handleAction('view-detail', close)"
            />

            <GlobalDropdownItem
              icon="settings"
              label="Cấu hình vòng PV"
              @click="handleAction('setup-rounds', close)"
            />
 
            <GlobalDropdownItem
              icon="description"
              label="Xem tin tuyển dụng"
              @click="handleAction('view-job-detail', close)"
            />

            <div class="dropdown-divider-v2"></div>

            <GlobalDropdownItem
              icon="verified"
              label="Hoàn thành tuyển dụng"
              :disabled="job.status !== JobPostingStatus.INTERVIEWING"
              :tooltip="job.status !== JobPostingStatus.INTERVIEWING ? 'Chỉ có thể hoàn thành khi đang ở trạng thái Phỏng vấn' : undefined"
              danger
              @click="handleAction('finish-interview', close)"
            />
          </template>
        </GlobalDropdown>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import GlobalDropdown from '@/components/ui/GlobalDropdown.vue'
import GlobalDropdownItem from '@/components/ui/GlobalDropdownItem.vue'
import { JobPostingStatus, JOB_POSTING_STATUS_LABELS, JOB_POSTING_STATUS_BADGE } from '@/constants/jobPosting.constants'

interface JobPostingRow {
  id: number
  title: string
  department: string
  level: string
  candidateCount: number
  icon: string
  iconVariant: 'blue-light' | 'blue-solid' | 'orange' | 'purple' | 'green' | 'rose'
  status: JobPostingStatus
  interviewRoundsCount: number
}

const props = defineProps<{
  job: JobPostingRow
  isActive?: boolean
}>()

const statusLabel = computed(() => JOB_POSTING_STATUS_LABELS[props.job.status])
const statusBadgeClass = computed(() => JOB_POSTING_STATUS_BADGE[props.job.status])

const canViewDetail = computed(() => (props.job.interviewRoundsCount ?? 0) > 0)

const emit = defineEmits<{
  (e: 'click'): void
  (e: 'view-detail'): void
  (e: 'setup-rounds'): void
  (e: 'finish-interview'): void
  (e: 'view-job-detail'): void
}>()

function handleAction(event: 'view-detail' | 'setup-rounds' | 'finish-interview' | 'view-job-detail', close: () => void) {
  close()
  emit(event as any)
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Manrope:wght@500;600;700&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@24,400,0,0&display=swap');

.material-symbols-outlined {
  font-family: 'Material Symbols Outlined';
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  font-size: 24px;
  line-height: 1;
  display: inline-block;
  vertical-align: middle;
}

.job-row {
  font-family: 'Manrope', sans-serif;
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  border-bottom: 1px solid #f1f5f9;
  cursor: pointer;
  transition: background 0.15s;
}
.job-row:first-child { border-top-left-radius: 1rem; border-top-right-radius: 1rem; }
.job-row:last-child { border-bottom-left-radius: 1rem; border-bottom-right-radius: 1rem; border-bottom: none; }
.job-row:hover       { background: #f8fafc; }
.job-row.active      { background: rgba(219, 234, 254, 0.2); }

.job-icon {
  width: 3rem;
  height: 3rem;
  border-radius: 0.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.job-icon.blue-light { background: #eff6ff; color: #4B9AF6; }
.job-icon.blue-solid { background: #4B9AF6; color: #fff; }
.job-icon.orange     { background: #fff7ed; color: #ea580c; }
.job-icon.purple     { background: #f5f3ff; color: #9333ea; }
.job-icon.green      { background: #f0fdf4; color: #16a34a; }
.job-icon.rose       { background: #fff1f2; color: #e11d48; }

.job-info {
  flex: 1;
  min-width: 0;
  display: grid;
  grid-template-columns: 3fr 1.5fr 1.5fr 1fr;
  align-items: center;
  gap: 1rem;
}

.job-title {
  font-size: 1.125rem;
  font-weight: 700;
  color: #0f172a;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: color 0.15s;
}
.job-row:hover .job-title,
.job-row.active .job-title { color: #4B9AF6; }

.job-sub {
  font-size: 0.75rem;
  color: #64748b;
  margin-top: 0.125rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.job-meta {
  font-size: 0.875rem;
  font-weight: 600;
  color: #0f172a;
  display: flex;
  align-items: center;
  gap: 0.375rem;
}
.job-meta .material-symbols-outlined { font-size: 18px; color: #94a3b8; }

.status-badge {
  display: inline-block;
  padding: 0.125rem 0.625rem;
  font-size: 0.75rem;
  font-weight: 700;
  border-radius: 9999px;
  letter-spacing: 0.05em;
}

.action-cell { display: flex; justify-content: flex-end; }

/* Divider inside global dropdown content */
.dropdown-divider-v2 {
  height: 1px;
  background: #f1f5f9;
  margin: 0.375rem 0.25rem;
}

@media (max-width: 768px) {
  .job-info { grid-template-columns: 1fr; }
  .job-meta, .badge-active, .btn-outline, .btn-solid-sm { display: none; }
}
</style>