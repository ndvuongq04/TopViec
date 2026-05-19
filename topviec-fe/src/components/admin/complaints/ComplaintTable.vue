<template>
  <div class="ct-wrap">
    <table class="ct-table">
      <thead>
        <tr>
          <th class="ct-th">Mã</th>
          <th class="ct-th">Người báo cáo</th>
          <th class="ct-th">Tin bị báo cáo</th>
          <th class="ct-th">Loại vi phạm</th>
          <th class="ct-th">Nhóm</th>
          <th class="ct-th">SLA</th>
          <th class="ct-th">Trạng thái</th>
          <th class="ct-th ct-th--right">Thao tác</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="row in complaints"
          :key="row.id"
          class="ct-row"
          @click="$emit('select', row)"
        >
          <td class="ct-td">
            <span :class="['ct-id', isUrgent(row) ? 'ct-id--active' : '']">{{ row.reportCode }}</span>
            <div class="ct-date">{{ formatDate(row.createdAt) }}</div>
          </td>

          <td class="ct-td">
            <div class="ct-sender">
              <div class="ct-avatar-initials" :style="{ background: avatarColor(row.reporterName) }">
                {{ initials(row.reporterName) }}
              </div>
              <span class="ct-sender-name">{{ row.reporterName }}</span>
            </div>
          </td>

          <td class="ct-td">
            <div class="ct-job-title">{{ row.jobPostTitle }}</div>
            <div class="ct-employer-name">{{ row.companyName }}</div>
          </td>

          <td class="ct-td">
            <span class="ct-category">{{ typeLabel[row.complaintType] ?? row.complaintType }}</span>
          </td>

          <td class="ct-td">
            <span v-if="row.violationGroup" :class="['ct-group-badge', `group-${row.violationGroup.toLowerCase()}`]">
              Nhóm {{ row.violationGroup }}
            </span>
            <span v-else class="ct-no-evidence">-</span>
          </td>

          <td class="ct-td">
            <template v-if="resolvedStatuses.includes(row.status)">
              <span class="ct-sla-done">-</span>
            </template>
            <template v-else-if="row.remainingProcessingHours !== null && row.remainingProcessingHours > 0">
              <span :class="['ct-sla', slaClass(row.remainingProcessingHours)]">
                Còn {{ row.remainingProcessingHours }}h
              </span>
            </template>
            <template v-else-if="row.remainingProcessingHours !== null">
              <span class="ct-sla sla-overdue">Quá hạn {{ Math.abs(row.remainingProcessingHours) }}h</span>
            </template>
            <template v-else>
              <span class="ct-sla-done">-</span>
            </template>
          </td>

          <td class="ct-td">
            <span :class="['ct-badge', statusCss(row.status)]">{{ statusLabel[row.status] ?? row.status }}</span>
          </td>

          <td class="ct-td ct-td--right">
            <button
              class="ct-detail-btn"
              type="button"
              title="Xem chi tiết"
              aria-label="Xem chi tiết"
              @click.stop="$emit('select', row)"
            >
              <span class="material-symbols-outlined text-lg">visibility</span>
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import type { ResReportSummary } from '@/types/report.types'

defineProps<{ complaints: ResReportSummary[] }>()
defineEmits<{ select: [ResReportSummary] }>()

const resolvedStatuses = ['resolved', 'auto_closed', 'rejected']

const typeLabel: Record<string, string> = {
  fraudulent:    'Lừa đảo',
  spam:          'Spam / Trùng lặp',
  wrong_info:    'Thông tin sai lệch',
  inappropriate: 'Nội dung không phù hợp',
  payment_issue: 'Yêu cầu phí bất hợp lý',
  other:         'Khác',
}

const statusLabel: Record<string, string> = {
  pending:          'Chờ xử lý',
  processing:       'Đang xử lý',
  waiting_employer: 'Chờ NTD',
  resolved:         'Đã giải quyết',
  rejected:         'Từ chối',
  auto_closed:      'Tự động đóng',
}

const statusCssMap: Record<string, string> = {
  pending:          'status-pending',
  processing:       'status-processing',
  waiting_employer: 'status-waiting',
  resolved:         'status-resolved',
  rejected:         'status-rejected',
  auto_closed:      'status-auto-closed',
}

function statusCss(s: string) { return statusCssMap[s] ?? '' }

function isUrgent(row: ResReportSummary) {
  return row.priority === 'urgent' || (row.remainingProcessingHours !== null && row.remainingProcessingHours <= 4)
}

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

const AVATAR_COLORS = ['#963131', '#1e5e4e', '#2c4a8e', '#633806', '#4a2c8e', '#1a6b6b']
function avatarColor(name: string) {
  return AVATAR_COLORS[name.charCodeAt(0) % AVATAR_COLORS.length]
}

function initials(name: string) {
  const parts = name.trim().split(' ').filter(Boolean)
  const first = parts[0]
  const last = parts[parts.length - 1]

  if (first && last && parts.length >= 2) {
    return `${first[0]}${last[0]}`.toUpperCase()
  }

  return name.slice(0, 2).toUpperCase()
}

function slaClass(hours: number): string {
  if (hours <= 4)  return 'sla-critical'
  if (hours <= 24) return 'sla-warning'
  return 'sla-ok'
}

</script>

<style scoped>
.ct-wrap { overflow-x: auto; }
.ct-table { width: 100%; border-collapse: collapse; text-align: left; }

.ct-th {
  padding: 12px 16px;
  font-size: 0.75rem;
  font-weight: 700;
  font-family: 'Manrope', sans-serif;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #574240;
  white-space: nowrap;
}
.ct-th--right { text-align: right; }
.ct-th--center { text-align: center; }

.ct-row { transition: background 0.15s; cursor: pointer; }
.ct-row:hover { background: #f6f4ec; }
.ct-row--highlighted { background: #f6f4ec; }

.ct-td {
  padding: 14px 16px;
  font-size: 0.875rem;
  color: #1b1c18;
  font-family: 'Manrope', sans-serif;
  vertical-align: middle;
}
.ct-td--right { text-align: right; }
.ct-td--center { text-align: center; }

.ct-id { font-weight: 600; }
.ct-id--active { color: #963131; }
.ct-date { font-size: 0.75rem; color: #574240; margin-top: 2px; }

.ct-sender { display: flex; align-items: center; gap: 10px; }
.ct-avatar { width: 30px; height: 30px; border-radius: 50%; object-fit: cover; }
.ct-avatar-initials {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 700;
  color: #fff;
}
.ct-sender-name { font-weight: 600; font-size: 0.875rem; white-space: nowrap; }

.ct-job-title { font-weight: 600; color: #1b1c18; }
.ct-employer-name { font-size: 0.75rem; color: #574240; margin-top: 2px; }

.ct-category { font-size: 0.875rem; color: #574240; }

.ct-group-badge {
  padding: 3px 10px;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 700;
  display: inline-block;
}
.group-a { background: #e4e2dc; color: #574240; }
.group-b { background: #ffdad6; color: #ba1a1a; }

.ct-sla {
  font-size: 0.75rem;
  font-weight: 700;
  padding: 3px 8px;
  border-radius: 6px;
  white-space: nowrap;
}
.sla-ok { background: #e1f5ee; color: #085041; }
.sla-warning { background: #faeeda; color: #633806; }
.sla-critical { background: #ffdad6; color: #ba1a1a; }
.sla-overdue { background: #ffdad6; color: #ba1a1a; }
.ct-sla-done { color: #aaa; }

.ct-evidence-icon { font-size: 18px; color: #963131; }
.ct-no-evidence { color: #ccc; }

.ct-badge {
  padding: 4px 10px;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 700;
  display: inline-block;
  white-space: nowrap;
}
.status-pending    { background: #e4e2dc; color: #574240; }
.status-processing { background: #faeeda; color: #633806; }
.status-waiting    { background: #e8f0fe; color: #1a56db; }
.status-resolved   { background: #e1f5ee; color: #085041; }
.status-rejected   { background: #ffdad6; color: #ba1a1a; }
.status-auto-closed { background: #e4e2dc; color: #574240; }

.ct-detail-btn {
  padding: 6px;
  background: transparent;
  border: none;
  border-radius: 8px;
  color: #94a3b8;
  cursor: pointer;
  transition: color 0.15s, background 0.15s;
}
.ct-detail-btn:hover {
  color: #963131;
  background: rgba(150, 49, 49, 0.08);
}
</style>
