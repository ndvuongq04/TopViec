<template>
  <div class="job-detail">
    <!-- Summary card -->
    <div class="job-detail__summary">
      <div class="job-detail__summary-left">
        <h2 class="job-detail__title">{{ job.title }}</h2>
        <p class="job-detail__meta">ID: {{ job.code }}</p>
      </div>
      <span class="job-detail__status" :class="JOB_POSTING_STATUS_BADGE[job.status as JobPostingStatus] ?? 'bg-slate-100 text-slate-500'">
        {{ JOB_POSTING_STATUS_LABELS[job.status as JobPostingStatus] ?? job.status }}
      </span>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="job-detail__card" style="display:flex;align-items:center;justify-content:center;padding:2rem;">
      <span class="material-symbols-outlined animate-spin" style="font-size:32px;color:#cbd5e1;">progress_activity</span>
    </div>

    <!-- Assigned manager -->
    <div v-else-if="assignment" class="job-detail__card">
      <div class="job-detail__accent"></div>
      <div class="job-detail__card-body">
        <p class="job-detail__card-label">Người đang quản lý tin này</p>
        <div class="job-detail__manager">
          <div class="job-detail__manager-avatar-wrap">
            <span style="font-size:1.25rem;font-weight:700;">{{ initials(assignment.userEmail) }}</span>
          </div>
          <div>
            <p class="job-detail__manager-name">{{ assignment.userEmail.split('@')[0] }}</p>
            <p class="job-detail__manager-role">{{ assignment.userEmail }}</p>
            <p class="job-detail__manager-meta">Giao lúc: {{ formatDate(assignment.assignedAt) }}</p>
          </div>
          
        </div>
      </div>
    </div>

    <!-- Empty state -->
    <JobAssignmentEmpty v-else :can-assign="canAssign" @assign="$emit('assign')" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { JOB_POSTING_STATUS_LABELS, JOB_POSTING_STATUS_BADGE, JobPostingStatus } from '@/constants/jobPosting.constants'
import JobAssignmentEmpty from './JobAssignmentEmpty.vue'

const props = defineProps<{ job: any; assignment?: any; loading?: boolean }>()
defineEmits(['assign'])

const canAssign = computed(() => {
  const allowed = [
    JobPostingStatus.PUBLISHED,
    JobPostingStatus.PAUSED,
    JobPostingStatus.RENEWED,
    JobPostingStatus.INTERVIEWING,
    JobPostingStatus.SCHEDULED,
    JobPostingStatus.CLOSED
  ]
  return allowed.includes(props.job?.status as JobPostingStatus)
})

function initials(email: string) {
  return email.split('@')[0].slice(0, 2).toUpperCase()
}

function formatDate(dateStr: string) {
  return new Date(dateStr).toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' })
}
</script>

<style scoped>
.job-detail { display: flex; flex-direction: column; gap: 1.5rem; }

/* Summary */
.job-detail__summary {
  background: #fff; border-radius: 0.75rem; padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border: 1px solid rgba(226,232,240,0.3);
  display: flex; justify-content: space-between; align-items: flex-start;
}
.job-detail__title { font-size: 1.125rem; font-weight: 800; color: #0f172a; margin-bottom: 4px; }
.job-detail__meta { font-size: 0.875rem; color: #64748b; }
.job-detail__status {
  padding: 4px 12px; border-radius: 999px;
  font-size: 0.875rem; font-weight: 600;
  background: #d1fae5; color: #065f46;
}

/* Card */
.job-detail__card {
  background: #fff; border-radius: 0.75rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border: 1px solid rgba(226,232,240,0.3);
  position: relative; overflow: hidden;
}
.job-detail__accent {
  height: 4px; background: linear-gradient(to right, #3b82f6, #2563eb);
}
.job-detail__card-body { padding: 1.5rem; }
.job-detail__card-label {
  font-size: 0.75rem; font-weight: 700; text-transform: uppercase;
  letter-spacing: 0.07em; color: #0f172a; margin-bottom: 1rem;
}

.job-detail__manager {
  display: flex; align-items: center; gap: 1rem;
  background: #f1f4f9; padding: 1rem; border-radius: 0.75rem;
}
.job-detail__manager-avatar-wrap {
  width: 48px; height: 48px; border-radius: 50%;
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  display: flex; align-items: center; justify-content: center;
  color: #fff; font-size: 1.125rem; font-weight: 800;
  flex-shrink: 0; border: 2px solid #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
}
.job-detail__manager-meta { font-size: 0.75rem; color: #94a3b8; margin-top: 2px; }
.job-detail__manager-name { font-size: 1.125rem; font-weight: 800; color: #0f172a; }
.job-detail__manager-role { font-size: 0.875rem; color: #64748b; }
.job-detail__manager-action {
  margin-left: auto; width: 36px; height: 36px;
  background: #fff; border: none; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  color: #4B9AF6; cursor: pointer; box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  transition: background 0.15s;
}
.job-detail__manager-action:hover { background: #f1f4f9; }
.job-detail__manager-action .material-symbols-outlined { font-size: 18px; }

.job-detail__view-all {
  display: inline-block; margin-top: 1rem;
  font-size: 1rem; font-weight: 700; color: #4B9AF6;
  text-decoration: none; transition: color 0.15s;
}
.job-detail__view-all:hover { color: #2563eb; }
</style>