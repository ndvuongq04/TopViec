<template>
  <div :class="['ccc-card', isDimmed(complaint.status) ? 'ccc-card--dimmed' : '']">
    <div class="ccc-top">
      <div class="ccc-id-row">
        <span class="ccc-id">{{ complaint.reportCode }}</span>
        <span class="ccc-date">{{ formatDate(complaint.createdAt) }}</span>
      </div>
      <span class="ccc-status" :style="statusStyle[complaint.status]">
        {{ statusLabel[complaint.status] ?? complaint.status }}
      </span>
    </div>

    <div class="ccc-body">
      <h3 class="ccc-job-title">{{ getJobTitle(props.complaint) }}</h3>
      <p class="ccc-company">
        <span class="material-symbols-outlined">business</span>
        {{ getCompanyName(props.complaint) }}
      </p>
    </div>

    <div class="ccc-footer">
      <div class="ccc-reason">
        <span class="material-symbols-outlined ccc-reason-icon">{{ typeIcon[complaint.complaintType] ?? 'gavel' }}</span>
        <span class="ccc-reason-text">{{ typeLabel[complaint.complaintType] ?? complaint.complaintType }}</span>
      </div>
      <button class="ccc-detail-btn" @click="$emit('view', complaint)">
        Xem chi tiết
        <span class="material-symbols-outlined">arrow_forward</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ResReportSummary } from '@/types/report.types'

const props = defineProps<{ complaint: ResReportSummary }>()
defineEmits<{ view: [ResReportSummary] }>()

function isDimmed(status: string) { return status === 'auto_closed' || status === 'rejected' }

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

function getJobTitle(complaint: ResReportSummary) {
  return complaint.jobPostTitle || complaint.jobPosting?.title || complaint.jobPost?.title || 'Tin tuyển dụng'
}

function getCompanyName(complaint: ResReportSummary) {
  return complaint.companyName
    || complaint.company?.name
    || complaint.jobPosting?.company?.name
    || complaint.jobPost?.company?.name
    || 'Công ty chưa cập nhật'
}

const statusLabel: Record<string, string> = {
  pending:          'Chờ xử lý',
  processing:       'Đang xử lý',
  waiting_employer: 'Chờ NTD phản hồi',
  resolved:         'Đã giải quyết',
  rejected:         'Từ chối',
  auto_closed:      'Tự động đóng',
}

const statusStyle: Record<string, { background: string; color: string }> = {
  pending:          { background: '#e2e7f0', color: '#404752' },
  processing:       { background: '#fff8e1', color: '#795548' },
  waiting_employer: { background: '#e8f0fe', color: '#1a56db' },
  resolved:         { background: '#75fd99', color: '#005224' },
  rejected:         { background: '#ffe4e4', color: '#ba1a1a' },
  auto_closed:      { background: '#e0e8ff', color: '#404752' },
}

const typeLabel: Record<string, string> = {
  fraudulent:    'Lừa đảo',
  spam:          'Spam / Trùng lặp',
  wrong_info:    'Thông tin sai lệch',
  inappropriate: 'Nội dung không phù hợp',
  payment_issue: 'Yêu cầu phí bất hợp lý',
  other:         'Vi phạm khác',
}

const typeIcon: Record<string, string> = {
  fraudulent:    'security',
  spam:          'block',
  wrong_info:    'warning',
  inappropriate: 'report',
  payment_issue: 'money_off',
  other:         'gavel',
}
</script>

<style scoped>
.ccc-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid rgba(192,199,212,0.2);
  box-shadow: 0 8px 32px rgba(0,94,164,0.06);
  display: flex; flex-direction: column; gap: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.ccc-card:hover {
  background: #f9f9ff;
  transform: translateY(-4px);
}
.ccc-card--dimmed { opacity: 0.75; }

.ccc-top {
  display: flex; align-items: center;
  justify-content: space-between;
}
.ccc-id-row { display: flex; align-items: center; gap: 12px; }
.ccc-id   { font-size: 0.875rem; font-weight: 600; color: #071b3b; font-family: 'Manrope', sans-serif; }
.ccc-date { font-size: 0.875rem; color: #404752; font-family: 'Manrope', sans-serif; }

/* Status badges */
.ccc-status {
  padding: 4px 12px; border-radius: 9999px;
  font-size: 0.75rem; font-weight: 700; font-family: 'Manrope', sans-serif;
}
.status-pending    { background: #e2e7f0; color: #404752; }
.status-processing { background: #fff8e1; color: #795548; }
.status-resolved   { background: #75fd99; color: #005224; }
.status-closed     { background: #e0e8ff; color: #404752; }

.ccc-body { display: flex; flex-direction: column; gap: 6px; }
.ccc-job-title {
  font-size: 1.125rem; font-weight: 700; color: #071b3b; font-family: 'Manrope', sans-serif;
  transition: color 0.2s;
  margin: 0;
}
.ccc-card:hover .ccc-job-title { color: #005ea4; }
.ccc-company {
  display: flex; align-items: center; gap: 8px;
  font-size: 0.875rem; color: #404752; margin: 0; font-family: 'Manrope', sans-serif;
}
.ccc-company .material-symbols-outlined { font-size: 16px; }

.ccc-footer {
  display: flex; align-items: center;
  justify-content: space-between;
}
.ccc-reason { display: flex; align-items: center; gap: 8px; }
.ccc-reason-icon { font-size: 18px; color: #ba1a1a; }
.ccc-reason-text { font-size: 0.875rem; font-weight: 500; color: #071b3b; font-family: 'Manrope', sans-serif; }

.ccc-detail-btn {
  display: flex; align-items: center; gap: 4px;
  font-size: 0.875rem; font-weight: 600; color: #005ea4;
  background: transparent; border: none; cursor: pointer;
  font-family: 'Manrope', sans-serif;
  transition: all 0.2s;
}
.ccc-detail-btn .material-symbols-outlined { font-size: 16px; transition: transform 0.2s; }
.ccc-card:hover .ccc-detail-btn { color: #0077ce; }
.ccc-card:hover .ccc-detail-btn .material-symbols-outlined { transform: translateX(4px); }
</style>
