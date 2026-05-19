<template>
  <div class="complaints-page space-y-8 pt-6">
    <div class="space-y-1">
      <h1 class="text-[1.875rem] font-black tracking-tight text-slate-900 dark:text-white">
        Báo cáo vi phạm nhận được
      </h1>
      <p class="text-base text-slate-500">
        Theo dõi và xử lý các báo cáo vi phạm từ ứng viên để duy trì uy tín tài khoản.
      </p>
    </div>

    <RecruiterViolationScore :score="score" />

    <RecruiterComplaintTable
      :complaints="complaints"
      :total="total"
      :current-page="page"
      :page-size="pageSize"
      @action="onAction"
      @search="onSearch"
      @filter="onFilter"
      @page-change="onPageChange"
    />

    <!-- ── Footer: Hướng dẫn nhóm vi phạm & điểm phạt ── -->
    <footer class="cp-footer">
      <div class="cp-footer__warning">
        <span class="material-symbols-outlined">warning</span>
        <div>
          <strong>Cảnh báo:</strong> Điểm vi phạm tích lũy sẽ ảnh hưởng trực tiếp đến tài khoản của bạn.
          Khi đạt <strong>30 điểm</strong> tài khoản bị <em>hạn chế</em>; đạt <strong>50 điểm</strong> tài khoản bị <em>khoá toàn bộ tính năng đăng tin</em>.
        </div>
      </div>

      <div class="cp-footer__groups">
        <!-- Nhóm A -->
        <div class="cp-footer__group cp-footer__group--a">
          <div class="cp-footer__group-header">
            <span class="cp-footer__badge cp-footer__badge--a">Nhóm A</span>
            <span class="cp-footer__penalty">+10 điểm / vi phạm</span>
          </div>
          <p class="cp-footer__group-desc">
            Vi phạm nhẹ, có thể tự khắc phục bằng cách chỉnh sửa tin tuyển dụng. Hệ thống sẽ tự động đóng báo cáo sau khi bạn sửa xong.
          </p>
          <ul class="cp-footer__list">
            <li v-for="type in groupATypes" :key="type.value">
              <span class="material-symbols-outlined">check_circle</span>
              {{ type.label }}
            </li>
          </ul>
        </div>

        <!-- Nhóm B -->
        <div class="cp-footer__group cp-footer__group--b">
          <div class="cp-footer__group-header">
            <span class="cp-footer__badge cp-footer__badge--b">Nhóm B</span>
            <span class="cp-footer__penalty">+30 điểm / vi phạm</span>
          </div>
          <p class="cp-footer__group-desc">
            Vi phạm nghiêm trọng hơn, cần admin xem xét giải trình. Bạn phải gửi bằng chứng và giải trình trong thời hạn quy định.
          </p>
          <ul class="cp-footer__list">
            <li v-for="type in groupBTypes" :key="type.value">
              <span class="material-symbols-outlined">report</span>
              {{ type.label }}
            </li>
          </ul>
        </div>
      </div>

      <p class="cp-footer__note">
        Mức điểm phạt chính xác có thể thay đổi tùy theo mức độ nghiêm trọng do admin đánh giá.
        Vui lòng xử lý mỗi khiếu nại đúng hạn để tránh bị tăng điểm vi phạm bổ sung.
      </p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { COMPLAINT_TYPE, COMPLAINT_TYPE_OPTIONS } from '@/constants/complaints.constants'
import { useRouter } from 'vue-router'
import RecruiterViolationScore from '@/components/recruiter/complaints/RecruiterViolationScore.vue'
import RecruiterComplaintTable, {
  type RecruiterComplaint,
} from '@/components/recruiter/complaints/RecruiterComplaintTable.vue'
import { useEmployerReportStore } from '@/stores/employerReport.store'

const router = useRouter()
const store = useEmployerReportStore()

const page = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')
const filterGroup = ref('')
const filterType = ref('')
const filterStatus = ref('')

const score = computed(() => ({
  violation: store.myViolationScore?.totalScore ?? 0,
  level: store.myViolationScore?.scoreLevel ?? null,
  pending: store.reports.filter((item) =>
    item.status === 'pending' || item.status === 'processing' || item.status === 'waiting_employer').length,
  thisMonth: store.reports.filter((item) => {
    const createdAt = new Date(item.createdAt)
    const now = new Date()
    return createdAt.getMonth() === now.getMonth() && createdAt.getFullYear() === now.getFullYear()
  }).length,
}))

const complaints = computed<RecruiterComplaint[]>(() =>
  store.reports.map((item) => ({
    id: item.id,
    reportCode: item.reportCode.startsWith('#') ? item.reportCode : `#${item.reportCode}`,
    jobTitle: item.jobPost.title,
    group: item.violationGroup?.toLowerCase() === 'b' ? 'b' : 'a',
    violationType: item.complaintType,
    status: item.status,
    sla: item.remainingHours === null
      ? undefined
      : item.remainingHours < 0
        ? { type: 'overdue', label: `Quá hạn ${Math.abs(item.remainingHours)}h` }
        : { type: 'remaining', label: `Còn ${item.remainingHours}h` },
    action: item.status === 'waiting_employer'
      ? 'fix'
      : item.status === 'resolved' || item.status === 'rejected' || item.status === 'auto_closed'
        ? 'view'
        : 'appeal',
  })),
)

const total = computed(() => store.meta.totals)

// Nhóm A: vi phạm nhẹ — có thể tự xử lý
const groupATypes = COMPLAINT_TYPE_OPTIONS.filter((t) =>
  [COMPLAINT_TYPE.WRONG_INFO, COMPLAINT_TYPE.SPAM, COMPLAINT_TYPE.INAPPROPRIATE].includes(
    t.value as (typeof COMPLAINT_TYPE)[keyof typeof COMPLAINT_TYPE],
  ),
)

// Nhóm B: vi phạm nghiêm trọng — cần admin xét duyệt
const groupBTypes = COMPLAINT_TYPE_OPTIONS.filter((t) =>
  [COMPLAINT_TYPE.FRAUDULENT, COMPLAINT_TYPE.PAYMENT_ISSUE, COMPLAINT_TYPE.OTHER].includes(
    t.value as (typeof COMPLAINT_TYPE)[keyof typeof COMPLAINT_TYPE],
  ),
)

async function fetchData() {
  await store.fetchMyReports({
    search: searchQuery.value.trim() || undefined,
    status: filterStatus.value || undefined,
    group: filterGroup.value ? filterGroup.value.toUpperCase() : undefined,
    complaintType: filterType.value || undefined,
    page: page.value - 1,
    size: pageSize.value,
    sort: 'createdAt,desc',
  })
}

function onSearch(query: string) {
  searchQuery.value = query
  page.value = 1
  fetchData()
}

function onFilter(filters: { group: string; type: string; status: string }) {
  filterGroup.value = filters.group
  filterType.value = filters.type
  filterStatus.value = filters.status
  page.value = 1
  fetchData()
}

function onPageChange(nextPage: number) {
  page.value = nextPage
  fetchData()
}

async function onAction(complaint: RecruiterComplaint) {
  const reportId = complaint.id

  await store.fetchById(reportId)
  await router.push({
    name: 'recruiter-complaint-detail',
    params: { id: reportId },
  })
}

onMounted(async () => {
  await Promise.all([
    store.fetchMyViolationScore().catch(() => null),
    fetchData(),
  ])
})
</script>

<style scoped>
.complaints-page {
  font-family: 'Manrope', sans-serif;
}

/* ── Footer ── */
.cp-footer {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;

}

.cp-footer__warning {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  padding: 1rem 1.25rem;
  border-radius: 0.625rem;
  background: #fff7ed;
  border: 1px solid #fed7aa;
  color: #9a3412;
  font-size: 0.875rem;
  line-height: 1.6;
}

.cp-footer__warning .material-symbols-outlined {
  font-size: 1.25rem;
  color: #ea580c;
  flex-shrink: 0;
  margin-top: 1px;
}

.cp-footer__groups {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.25rem;
}

@media (max-width: 768px) {
  .cp-footer__groups {
    grid-template-columns: 1fr;
  }
}

.cp-footer__group {
  border-radius: 0.75rem;
  padding: 1.25rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  border: 1px solid;
}

.cp-footer__group--a {
  background: #f0fdf4;
  border-color: #bbf7d0;
}

.cp-footer__group--b {
  background: #fff1f2;
  border-color: #fecdd3;
}

.cp-footer__group-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.5rem;
}

.cp-footer__badge {
  display: inline-flex;
  align-items: center;
  padding: 0.2rem 0.75rem;
  border-radius: 999px;
  font-size: 0.8125rem;
  font-weight: 800;
  letter-spacing: 0.02em;
}

.cp-footer__badge--a {
  background: #dcfce7;
  color: #15803d;
}

.cp-footer__badge--b {
  background: #fee2e2;
  color: #b91c1c;
}

.cp-footer__penalty {
  font-size: 0.875rem;
  font-weight: 800;
  color: #0f172a;
}

.cp-footer__group--a .cp-footer__penalty { color: #15803d; }
.cp-footer__group--b .cp-footer__penalty { color: #b91c1c; }

.cp-footer__group-desc {
  font-size: 0.8125rem;
  color: #64748b;
  line-height: 1.6;
  margin: 0;
}

.cp-footer__list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 0.375rem;
}

.cp-footer__list li {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  font-weight: 600;
  color: #334155;
}

.cp-footer__list .material-symbols-outlined {
  font-size: 1rem;
}

.cp-footer__group--a .cp-footer__list .material-symbols-outlined { color: #16a34a; }
.cp-footer__group--b .cp-footer__list .material-symbols-outlined { color: #dc2626; }

.cp-footer__note {
  margin: 0;
  font-size: 0.75rem;
  color: #94a3b8;
  line-height: 1.6;
  text-align: center;
}
</style>
