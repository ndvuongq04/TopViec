<template>
  <div class="complaint-detail">
    <header class="complaint-detail__header">
      <Breadcrumb :items="breadcrumbItems" :hide-home="true" class="complaint-detail__breadcrumb" />

      <div class="complaint-detail__header-body">
        <div class="complaint-detail__header-main">
          <h1 class="complaint-detail__title">{{ pageTitle }}</h1>
          <div v-if="report" class="complaint-detail__badges">
            <span
              v-for="badge in complaint.badges"
              :key="badge.label"
              :class="['complaint-detail__badge', `complaint-detail__badge--${badge.variant}`]"
            >
              <span v-if="badge.icon" class="material-symbols-outlined">{{ badge.icon }}</span>
              {{ badge.label }}
            </span>
          </div>
        </div>

        <div v-if="report" class="complaint-detail__header-actions">
          <button class="complaint-detail__btn complaint-detail__btn--ghost" type="button">
            <span class="material-symbols-outlined">support_agent</span>
            Liên hệ Admin
          </button>
          <router-link
            v-if="report.violationGroup?.toUpperCase() === 'A'"
            :to="`/recruiter/jobs/${report.jobPost.id}/edit`"
            class="complaint-detail__btn complaint-detail__btn--primary"
          >
            <span class="material-symbols-outlined">edit</span>
            Sửa tin tuyển dụng
          </router-link>
          <button
            class="complaint-detail__btn complaint-detail__btn--primary"
            type="button"
            @click="scrollToAppealSection"
          >
            <span class="material-symbols-outlined">send</span>
            Gửi giải trình
          </button>
        </div>
      </div>
    </header>

    <div v-if="store.loading && !report" class="complaint-detail__state">
      Đang tải chi tiết báo cáo...
    </div>

    <div v-else-if="!report" class="complaint-detail__state complaint-detail__state--error">
      {{ store.error ?? 'Không tải được chi tiết báo cáo.' }}
    </div>

    <div v-else class="complaint-detail__canvas">
      <ComplaintDetailAlert :complaint="complaint" />

      <div class="complaint-detail__grid">
        <div class="complaint-detail__left">
          <ComplaintDetailJobInfo :job="complaint.job" />
          <ComplaintDetailContent :content="complaint.content" />
          <ComplaintDetailResponseForm />
        </div>

        <div class="complaint-detail__right">
          <ComplaintDetailViolationScore :score="complaint.score" />
          <ComplaintDetailImpacts :impacts="complaint.impacts" />
          <ComplaintDetailChecklist :items="complaint.checklist" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import Breadcrumb from '@/components/ui/Breadcrumb.vue'
import ComplaintDetailAlert from '@/components/recruiter/complaints/complaint-detail/ComplaintDetailAlert.vue'
import ComplaintDetailChecklist from '@/components/recruiter/complaints/complaint-detail/ComplaintDetailChecklist.vue'
import ComplaintDetailContent from '@/components/recruiter/complaints/complaint-detail/ComplaintDetailContent.vue'
import ComplaintDetailImpacts from '@/components/recruiter/complaints/complaint-detail/ComplaintDetailImpacts.vue'
import ComplaintDetailJobInfo from '@/components/recruiter/complaints/complaint-detail/ComplaintDetailJobInfo.vue'
import ComplaintDetailResponseForm from '@/components/recruiter/complaints/complaint-detail/ComplaintDetailResponseForm.vue'
import ComplaintDetailViolationScore from '@/components/recruiter/complaints/complaint-detail/ComplaintDetailViolationScore.vue'
import {
  COMPLAINT_PRIORITY_OPTIONS,
  COMPLAINT_STATUS,
  COMPLAINT_STATUS_OPTIONS,
  COMPLAINT_TYPE_OPTIONS,
  VIOLATION_GROUP_OPTIONS,
} from '@/constants/complaints.constants'
import { JOB_POSTING_STATUS_LABELS } from '@/constants/jobPosting.constants'
import { useEmployerAppealStore } from '@/stores/employerAppeal.store'
import { useEmployerReportStore } from '@/stores/employerReport.store'

const route = useRoute()
const store = useEmployerReportStore()
const appealStore = useEmployerAppealStore()

const reportId = computed(() => {
  const rawId = Array.isArray(route.params.id) ? route.params.id[0] : route.params.id
  const parsed = Number(rawId)
  return Number.isFinite(parsed) ? parsed : null
})

const report = computed(() => store.currentReport)
const scoreData = computed(() => store.myViolationScore)

const breadcrumbItems = computed(() => [
  { label: 'Báo cáo vi phạm', to: '/recruiter/complaints' },
  { label: report.value?.reportCode ? `Chi tiết ${report.value.reportCode}` : 'Chi tiết' },
])

const pageTitle = computed(() =>
  report.value?.reportCode ? `Chi tiết khiếu nại ${report.value.reportCode}` : 'Chi tiết khiếu nại',
)

function formatLabel(value?: string | null) {
  if (!value) return 'Không xác định'
  return value
    .split('_')
    .filter(Boolean)
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1).toLowerCase())
    .join(' ')
}

function lookupLabel<T extends string>(
  options: Array<{ value: T; label: string }>,
  value?: string | null,
  fallback = 'Không xác định',
) {
  if (!value) return fallback
  return options.find((option) => option.value === value)?.label ?? formatLabel(value)
}

function formatDateTime(value?: string | null) {
  if (!value) return ''
  return new Date(value).toLocaleString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function scoreStatusVariant(level?: string | null) {
  switch (level) {
    case 'suspended':
      return 'error'
    case 'limited':
      return 'warning'
    default:
      return 'safe'
  }
}

function scoreStatusLabel(level?: string | null) {
  switch (level) {
    case 'suspended':
      return 'Đình chỉ (≥50)'
    case 'limited':
      return 'Hạn chế (20–49)'
    case 'normal':
      return 'An toàn (0–19)'
    default:
      return 'Đang cập nhật'
  }
}

function scoreProgress(current: number) {
  return Math.max(0, Math.min(100, Math.round((current / 30) * 100)))
}

function buildImpacts(status?: string | null, group?: string | null) {
  const impacts = [
    {
      icon: 'schedule',
      iconVariant: 'tertiary',
      title: 'Cần phản hồi đúng hạn',
      desc: 'Nếu quá hạn phản hồi, báo cáo có thể bị đóng bất lợi cho tài khoản tuyển dụng.',
    },
    {
      icon: 'visibility_off',
      iconVariant: 'secondary',
      title: 'Tin tuyển dụng có thể bị hạn chế hiển thị',
      desc: 'Admin có thể tạm ẩn hoặc yêu cầu chỉnh sửa tin tuyển dụng trước khi tiếp tục hiển thị.',
    },
  ]

  if (group === 'B') {
    impacts.push({
      icon: 'lock',
      iconVariant: 'error',
      title: 'Nguy cơ tăng điểm vi phạm',
      desc: 'Nhóm B thường có mức độ nghiêm trọng cao hơn và ảnh hưởng trực tiếp đến trạng thái tài khoản.',
    })
  }

  if (status === COMPLAINT_STATUS.WAITING_EMPLOYER) {
    impacts.unshift({
      icon: 'mark_email_unread',
      iconVariant: 'error',
      title: 'Đang chờ nhà tuyển dụng phản hồi',
      desc: 'Admin đã yêu cầu bạn giải trình. Hãy cung cấp thông tin và bằng chứng sớm nhất có thể.',
    })
  }

  return impacts
}

const complaint = computed(() => {
  const currentReport = report.value
  if (!currentReport) {
    return {
      badges: [],
      alert: { title: '', message: '', highlight: [] as string[] },
      job: { title: '', id: '', company: 'Tin tuyển dụng của bạn', status: '' },
      content: { violationType: '', summary: '', noteTitle: '', note: '' },
      score: {
        current: 0,
        status: 'Đang cập nhật',
        statusVariant: 'safe',
        statusLabel: 'Đang cập nhật',
        progressPercent: 0,
        penaltyIfFail: 'Sẽ do admin quyết định',
      },
      impacts: [] as { icon: string; iconVariant: string; title: string; desc: string }[],
      checklist: [] as string[],
    }
  }

  const complaintTypeLabel = lookupLabel(COMPLAINT_TYPE_OPTIONS, currentReport.complaintType)
  const groupLabel = lookupLabel(VIOLATION_GROUP_OPTIONS, currentReport.violationGroup, 'Chưa phân nhóm')
  const statusLabel = lookupLabel(COMPLAINT_STATUS_OPTIONS, currentReport.status)
  const priorityLabel = lookupLabel(COMPLAINT_PRIORITY_OPTIONS, currentReport.priority)
  const remainingLabel = currentReport.remainingHours === null
    ? 'Không giới hạn thời gian'
    : currentReport.remainingHours < 0
      ? `Quá hạn ${Math.abs(currentReport.remainingHours)} giờ`
      : `Còn ${currentReport.remainingHours} giờ`
  const violationScore = scoreData.value?.totalScore ?? 0

  return {
    badges: [
      { label: groupLabel, variant: currentReport.violationGroup === 'B' ? 'warning' : 'neutral' },
      {
        label: statusLabel,
        variant: currentReport.status === COMPLAINT_STATUS.WAITING_EMPLOYER ? 'error' : 'info',
      },
      { label: remainingLabel, variant: 'error-outline', icon: 'timer' },
      { label: `Ưu tiên: ${priorityLabel}`, variant: 'error-soft' },
    ],
    alert: {
      title: currentReport.violationGroup?.toUpperCase() === 'A'
        ? 'Yêu cầu chỉnh sửa tin tuyển dụng (Nhóm A)'
        : currentReport.status === COMPLAINT_STATUS.WAITING_EMPLOYER
          ? 'Yêu cầu phản hồi từ admin (Nhóm B)'
          : 'Thông tin báo cáo vi phạm',
      message: currentReport.violationGroup?.toUpperCase() === 'A'
        ? `Tin tuyển dụng của bạn bị báo cáo vì "${complaintTypeLabel}". Hãy sửa nội dung vi phạm và xác nhận để hệ thống tự đóng báo cáo. ${currentReport.remainingHours !== null ? `Còn ${remainingLabel} để xử lý.` : ''}`
        : currentReport.remainingHours !== null
          ? `Bạn đang bị báo cáo vì "${complaintTypeLabel}". Hạn phản hồi hiện tại là ${remainingLabel}. Vui lòng cung cấp giải trình đầy đủ để admin xem xét.`
          : `Bạn đang bị báo cáo vì "${complaintTypeLabel}". Vui lòng cung cấp giải trình và thông tin liên quan để admin xem xét.`,
      highlight: [complaintTypeLabel, remainingLabel].filter(Boolean),
    },
    job: {
      title: currentReport.jobPost.title,
      id: String(currentReport.jobPost.id),
      company: 'Nhà tuyển dụng',
      status: JOB_POSTING_STATUS_LABELS[
        currentReport.jobPost.status as keyof typeof JOB_POSTING_STATUS_LABELS
      ] ?? formatLabel(currentReport.jobPost.status),
    },
    content: {
      violationType: complaintTypeLabel,
      summary: currentReport.description?.trim() || 'Chưa có mô tả chi tiết từ bên báo cáo.',
      noteTitle: currentReport.violationGroup?.toUpperCase() === 'A' ? 'Hướng dẫn xử lý Nhóm A' : 'Lưu ý cho Nhóm B',
      note: currentReport.resolutionNote?.trim()
        || (currentReport.violationGroup?.toUpperCase() === 'A'
          ? 'Vi phạm nhóm A có thể tự khắc phục. Hãy sửa nội dung tin tuyển dụng và nhấn "Xác nhận đã sửa" để hệ thống tự đóng báo cáo.'
          : 'Vi phạm nhóm B nghiêm trọng hơn và cần admin xét duyệt. Vui lòng cung cấp giải trình và bằng chứng đầy đủ.'),
    },
    score: {
      current: violationScore,
      status: scoreData.value?.restrictionDescription || 'Tài khoản đang hoạt động',
      statusVariant: scoreStatusVariant(scoreData.value?.scoreLevel),
      statusLabel: scoreStatusLabel(scoreData.value?.scoreLevel),
      progressPercent: scoreProgress(violationScore),
      penaltyIfFail: currentReport.violationGroup === 'B' ? '+30 điểm' : '+10 điểm',
    },
    impacts: buildImpacts(currentReport.status, currentReport.violationGroup),
    checklist: currentReport.violationGroup?.toUpperCase() === 'A'
      ? [
          'Vào trang quản lý tin tuyển dụng và chỉnh sửa nội dung vi phạm',
          'Lưu lại tin sau khi sửa xong',
          'Quay lại trang này và nhấn "Xác nhận đã sửa"',
          currentReport.employerDeadline
            ? `Hoàn tất trước ${formatDateTime(currentReport.employerDeadline)}`
            : 'Xử lý sớm để tránh ảnh hưởng đến tài khoản',
        ]
      : [
          'Kiểm tra lại nội dung tin tuyển dụng liên quan',
          'Chuẩn bị giải trình rõ ràng cho admin',
          'Đính kèm bằng chứng hỗ trợ nếu có',
          currentReport.employerDeadline
            ? `Hoàn tất phản hồi trước ${formatDateTime(currentReport.employerDeadline)}`
            : 'Theo dõi cập nhật từ admin trên hệ thống',
        ],
  }
})

async function loadData() {
  if (!reportId.value) return

  store.currentReport = null
  appealStore.reset()

  try {
    await Promise.all([
      store.fetchById(reportId.value),
      store.myViolationScore ? Promise.resolve(store.myViolationScore) : store.fetchMyViolationScore().catch(() => null),
      appealStore.fetchCurrentAppeal(reportId.value).catch(() => null),
    ])
  } catch {
    // Error message is already normalized in the store.
  }
}

function scrollToAppealSection() {
  document.getElementById('complaint-appeal-section')?.scrollIntoView({
    behavior: 'smooth',
    block: 'start',
  })
}

onMounted(loadData)

watch(reportId, (nextId, prevId) => {
  if (nextId && nextId !== prevId) {
    loadData()
  }
})
</script>

<style scoped>
.complaint-detail {
  display: flex;
  flex-direction: column;
  min-height: 100%;
  font-family: 'Manrope', sans-serif;
}

.complaint-detail__header {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 1.25rem;
}

.complaint-detail__breadcrumb {
  font-size: 1rem;
  font-weight: 500;
}

.complaint-detail__header-body {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 1rem;
}

.complaint-detail__header-main {
  min-width: 0;
}

.complaint-detail__title {
  margin: 0 0 0.75rem;
  font-size: 1.875rem;
  font-weight: 800;
  line-height: 1.15;
  color: #0f172a;
  letter-spacing: -0.02em;
}

.complaint-detail__badges {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.complaint-detail__badge {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.35rem 0.75rem;
  border-radius: 0.5rem;
  font-size: 0.75rem;
  font-weight: 700;
}

.complaint-detail__badge .material-symbols-outlined {
  font-size: 0.875rem;
}

.complaint-detail__badge--warning { background: #fef3c7; color: #b45309; }
.complaint-detail__badge--neutral { background: #e2e8f0; color: #475569; }
.complaint-detail__badge--info { background: #dbeafe; color: #1d4ed8; }
.complaint-detail__badge--error { background: #fee2e2; color: #b91c1c; }
.complaint-detail__badge--error-outline {
  border: 1px solid #ef4444;
  color: #ef4444;
  background: rgba(239, 68, 68, 0.05);
}
.complaint-detail__badge--error-soft { background: rgba(239, 68, 68, 0.1); color: #ef4444; }

.complaint-detail__header-actions {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.complaint-detail__btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;
  border: none;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  transition: all 0.15s;
}

.complaint-detail__btn .material-symbols-outlined {
  font-size: 1.125rem;
}

.complaint-detail__btn--ghost {
  background: none;
  border: 1px solid #cbd5e1;
  color: #0f172a;
}

.complaint-detail__btn--ghost:hover {
  background: #f8fafd;
}

.complaint-detail__btn--primary {
  background: #4b9af6;
  color: #fff;
  box-shadow: 0 2px 8px rgba(75, 154, 246, 0.25);
}

.complaint-detail__btn--primary:hover {
  background: #2563eb;
}

.complaint-detail__btn--primary:active {
  transform: scale(0.97);
}

.complaint-detail__btn--green {
  background: #16a34a;
  color: #fff;
  box-shadow: 0 2px 8px rgba(22, 163, 74, 0.25);
  text-decoration: none;
}

.complaint-detail__btn--green:hover {
  background: #15803d;
}

.complaint-detail__state {
  padding: 2rem;
  border-radius: 0.75rem;
  background: #fff;
  border: 1px solid #e2e8f0;
  color: #475569;
  font-weight: 600;
}

.complaint-detail__state--error {
  color: #b91c1c;
  background: #fff1f2;
  border-color: #fecdd3;
}

.complaint-detail__canvas {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.complaint-detail__grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 1.5rem;
  align-items: start;
}

@media (max-width: 1024px) {
  .complaint-detail__grid {
    grid-template-columns: 1fr;
  }
}

.complaint-detail__left {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.complaint-detail__right {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

@media (max-width: 768px) {
  .complaint-detail__header {
    padding: 1.25rem 1rem 0;
    margin-bottom: 1rem;
  }

  .complaint-detail__canvas {
    padding: 1.25rem 1rem 1.5rem;
  }
}
</style>
