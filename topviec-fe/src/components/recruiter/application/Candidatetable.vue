<template>
  <div class="table-card">

    <!-- Empty state -->
    <div v-if="candidates.length === 0" class="empty-state">
      <span class="material-symbols-outlined empty-icon">folder_open</span>
      <p>Không có hồ sơ ứng tuyển nào</p>
    </div>

    <table v-else class="candidate-table">
      <thead>
        <tr class="table-head-row">
          <th class="th">Ứng viên</th>
          <th class="th">Vị trí ứng tuyển</th>
          <th class="th">Ngày ứng tuyển</th>
          <th class="th">Đánh giá</th>
          <th class="th">Trạng thái</th>
          <th class="th th--right">Thao tác</th>
        </tr>
      </thead>
      <tbody class="table-body">
        <tr
          v-for="c in candidates"
          :key="c.id"
          class="table-row"
          @click="goToDetail(c.id)"
        >
          <!-- Candidate info -->
          <td class="td">
            <div class="candidate-info">
              <img
                v-if="c.candidateAvatar"
                :src="c.candidateAvatar"
                :alt="c.candidateName"
                class="candidate-avatar"
              />
              <div v-else class="candidate-avatar avatar-placeholder">
                {{ initials(c.candidateName) }}
              </div>
              <div>
                <p class="candidate-name">{{ c.candidateName }}</p>
                <p class="candidate-email">{{ c.candidateEmail }}</p>
              </div>
            </div>
          </td>

          <!-- Position -->
          <td class="td">
            <p class="cell-text">{{ c.jobTitle }}</p>
          </td>

          <!-- Applied at -->
          <td class="td">
            <p class="cell-text cell-text--muted">{{ formatDate(c.createdAt) }}</p>
          </td>

          <!-- Rating -->
          <td class="td">
            <div class="stars">
              <span
                v-for="n in 5"
                :key="n"
                class="material-symbols-outlined star-icon"
                :style="c.recruiterRating && n <= c.recruiterRating ? { fontVariationSettings: `'FILL' 1` } : {}"
              >star</span>
            </div>
          </td>

          <!-- Status -->
          <td class="td">
            <span :class="['status-badge', `status-badge--${c.status}`]">
              {{ STATUS_LABEL[c.status] ?? c.status }}
            </span>
          </td>

          <!-- Actions -->
          <td class="td td--right">
            <div class="action-group">
              <button
                class="action-btn"
                title="Xem chi tiết"
                @click.stop="goToDetail(c.id)"
              >
                <span class="material-symbols-outlined">visibility</span>
              </button>

              <button
                v-if="c.cvPreviewUrl || c.cvPdfUrl || c.cvFileUrl"
                class="action-btn"
                title="Tải CV"
                @click.stop="downloadCv(c.cvPreviewUrl || c.cvPdfUrl || c.cvFileUrl || '')"
              >
                <span class="material-symbols-outlined">download</span>
              </button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Pagination -->
    <div v-if="meta.totals > 0" class="pagination">
      <p class="pagination-info">
        Hiển thị
        <strong>{{ pageStart }}-{{ pageEnd }}</strong>
        trong
        <strong>{{ meta.totals.toLocaleString() }}</strong>
        hồ sơ
      </p>
      <div class="pagination-controls">
        <button
          class="page-btn page-btn--nav"
          :disabled="meta.page === 0"
          @click="$emit('page-change', meta.page - 1)"
        >
          <span class="material-symbols-outlined">chevron_left</span>
        </button>

        <template v-for="(p, idx) in visiblePages" :key="idx">
          <span v-if="p === '...'" class="page-ellipsis">...</span>
          <button
            v-else
            :class="['page-btn', p === meta.page ? 'page-btn--active' : '']"
            @click="$emit('page-change', p as number)"
          >
            {{ (p as number) + 1 }}
          </button>
        </template>

        <button
          class="page-btn page-btn--nav"
          :disabled="meta.page >= meta.pages - 1"
          @click="$emit('page-change', meta.page + 1)"
        >
          <span class="material-symbols-outlined">chevron_right</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import type { ResEmployerApplicationDTO } from '@/types/employerApplication.types'
import type { PaginationMeta } from '@/types/common.types'

const props = defineProps<{
  candidates: ResEmployerApplicationDTO[]
  meta: PaginationMeta
}>()

const emit = defineEmits<{
  'page-change': [page: number]
}>()

const router = useRouter()
const route = useRoute()

const STATUS_LABEL: Record<string, string> = {
  pending:          'Chờ xem',
  invited:          'Được mời',
  seen:             'Đã xem',
  considering:      'Cân nhắc',
  cv_passed:        'Đạt vòng CV',
  schedule_pending: 'Chờ chọn lịch',
  overdue:          'Quá hạn',
  interviewing:     'Phỏng vấn',
  offered:          'Gửi đề nghị',
  hired:            'Đã tuyển',
  rejected:         'Từ chối',
  withdrawn:        'Rút đơn',
  expired:          'Hết hạn',
}

const pageStart = computed(() => props.meta.page * props.meta.pageSize + 1)
const pageEnd   = computed(() => Math.min((props.meta.page + 1) * props.meta.pageSize, props.meta.totals))

const visiblePages = computed(() => {
  const total   = props.meta.pages
  const current = props.meta.page
  if (total <= 7) return Array.from({ length: total }, (_, i) => i)

  const pages: (number | string)[] = []
  if (current > 2) { pages.push(0); pages.push('...') }
  for (let i = Math.max(0, current - 1); i <= Math.min(total - 1, current + 1); i++) {
    pages.push(i)
  }
  if (current < total - 3) { pages.push('...'); pages.push(total - 1) }
  return pages
})

function initials(name: string): string {
  return name
    .split(' ')
    .filter(Boolean)
    .slice(-2)
    .map(w => w[0].toUpperCase())
    .join('')
}

function formatDate(iso: string): string {
  if (!iso) return '-'
  const d = new Date(iso)
  return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
}

function downloadCv(url: string) {
  window.open(url, '_blank')
}

const goToDetail = (candidateId: number) => {
  router.push({
    name: 'recruiter-application-detail',
    params: {
      id: route.params.id,
      applicationId: candidateId,
    },
  })
}
</script>

<style scoped>
.table-card {
  background: #fff;
  border-radius: 0 0 0.75rem 0.75rem;
  border: 1px solid #f1f5f9;
  border-top: none;
  box-shadow: 0 1px 3px rgba(0,0,0,.06);
  overflow: hidden;
}

/* Empty state */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  color: #94a3b8;
  gap: 0.75rem;
}
.empty-icon { font-size: 3rem !important; }
.empty-state p { font-size: 0.9375rem; }

.candidate-table { width: 100%; border-collapse: collapse; text-align: left; }

.table-head-row { background: rgba(248,250,252,.6); border-bottom: 1px solid #f1f5f9; }
.th {
  padding: 1rem 1.5rem;
  font-size: 0.75rem;
  font-weight: 700;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: .05em;
}
.th--right { text-align: right; }

.table-body .table-row { border-bottom: 1px solid #f8fafc; transition: background .15s; cursor: pointer; }
.table-body .table-row:hover { background: rgba(248,250,252,.6); }

.td { padding: 1rem 1.5rem; vertical-align: middle; }
.td--right { text-align: right; }

/* Candidate info */
.candidate-info { display: flex; align-items: center; gap: 0.75rem; }
.candidate-avatar {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 9999px;
  object-fit: cover;
  flex-shrink: 0;
}
.avatar-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e0e7ff;
  color: #4338ca;
  font-size: 0.875rem;
  font-weight: 700;
  flex-shrink: 0;
}
.candidate-name  { font-size: 1.125rem; font-weight: 700; color: #0f172a; }
.candidate-email { font-size: 0.75rem; color: #64748b; margin-top: 0.125rem; }

/* Cells */
.cell-text       { font-size: 0.875rem; font-weight: 500; color: #0f172a; }
.cell-text--muted{ color: #64748b; }

/* Stars */
.stars     { display: flex; gap: 0.1rem; color: #fbbf24; }
.star-icon { font-size: 1.125rem; }

/* Status badges */
.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 700;
}
.status-badge--pending          { background: #dbeafe; color: #2563eb; }
.status-badge--invited          { background: #cffafe; color: #0e7490; }
.status-badge--seen             { background: #e0e7ff; color: #4338ca; }
.status-badge--considering      { background: #fef3c7; color: #d97706; }
.status-badge--cv_passed        { background: #d1fae5; color: #059669; }
.status-badge--schedule_pending { background: #ffedd5; color: #ea580c; }
.status-badge--overdue          { background: #fee2e2; color: #dc2626; }
.status-badge--interviewing     { background: #fce7f3; color: #db2777; }
.status-badge--offered          { background: #d1fae5; color: #047857; }
.status-badge--hired            { background: #dcfce7; color: #16a34a; }
.status-badge--rejected         { background: #fee2e2; color: #dc2626; }
.status-badge--withdrawn        { background: #f1f5f9; color: #64748b; }
.status-badge--expired          { background: #f1f5f9; color: #94a3b8; }

/* Actions */
.action-group { display: inline-flex; gap: 0.25rem; }
.action-btn {
  padding: 0.375rem;
  border-radius: 0.375rem;
  color: #94a3b8;
  transition: color .15s, background .15s;
  cursor: pointer;
}
.action-btn:hover { color: #4B9AF6; background: #eff6ff; }

/* Pagination */
.pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 1.5rem;
  border-top: 1px solid #f1f5f9;
  background: rgba(248,250,252,.4);
}
.pagination-info { font-size: 0.875rem; color: #64748b; }
.pagination-info strong { font-weight: 700; color: #0f172a; }

.pagination-controls { display: flex; gap: 0.5rem; align-items: center; }
.page-btn {
  padding: 0.5rem 1rem;
  border-radius: 0.5rem;
  font-size: 0.875rem;
  font-weight: 500;
  border: 1px solid transparent;
  transition: background .15s, border-color .15s;
  cursor: pointer;
}
.page-btn:hover:not(:disabled):not(.page-btn--active) {
  background: #fff;
  border-color: #e2e8f0;
}
.page-btn--active { background: #4B9AF6; color: #fff; font-weight: 700; }
.page-btn--nav    { padding: 0.5rem; border: 1px solid #e2e8f0; }
.page-btn:disabled{ opacity: .4; cursor: not-allowed; }
.page-ellipsis    { font-size: 0.875rem; color: #64748b; align-self: center; }
</style>
