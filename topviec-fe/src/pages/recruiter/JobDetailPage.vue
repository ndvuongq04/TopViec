<template>
  <div class="page-wrapper">
    <div class="page-inner">

      <template v-if="job">
        <JobDetailHeader :title="job.title" :location="headerLocation" :status="String(job.status)" />
        <JobDetailStats  :stats="statCards" />
        <div class="body-grid">
          <div class="col-main">
            <JobDetailContent
              :description="job.description"
              :requirements="job.requirements"
              :benefits-html="job.benefits"
            />
          </div>
          <div class="col-side">
            <JobDetailSideInfo
              :info-items="infoItems"
              :timeline="timeline"
              :conversion-percent="conversionPercent"
            />
          </div>
        </div>

        <!-- Footer: Dịch vụ áp dụng cho tin -->
        <JobServicePanel :job-posting-id="Number(route.params.id)" />
      </template>

      <!-- Loading skeleton -->
      <div v-else-if="isLoading" class="loading">Đang tải...</div>

      <!-- Error -->
      <div v-else class="loading">Không tìm thấy tin tuyển dụng.</div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { publicJobPostingService } from '@/services/jobPosting.service'
import type { ResJobPostingDetail } from '@/types/jobPosting.types'
import { WORK_TYPE_LABELS } from '@/constants/jobPosting.constants'
import JobDetailHeader   from '@/components/recruiter/jobs/JobDetailHeader.vue'
import JobDetailStats    from '@/components/recruiter/jobs/JobDetailStats.vue'
import JobDetailContent  from '@/components/recruiter/jobs/JobDetailContent.vue'
import JobDetailSideInfo from '@/components/recruiter/jobs/JobDetailSideInfo.vue'
import JobServicePanel   from '@/components/recruiter/jobs/JobServicePanel.vue'

const route     = useRoute()
const job       = ref<ResJobPostingDetail | null>(null)
const isLoading = ref(true)

onMounted(async () => {
  try {
    job.value = await publicJobPostingService.getById(Number(route.params.id))
  } finally {
    isLoading.value = false
  }
})

// ── Header ───────────────────────────────────────────────
const headerLocation = computed(() => {
  const locs = job.value?.locations ?? []
  if (!locs.length) return ''
  const first = locs[0]
  if (first.isRemote) return 'Remote'
  const namePart = first.name ? `${first.name}: ` : ''
  return namePart + (first.addressDetail ?? '')
})

// ── Stats cards ──────────────────────────────────────────
const statCards = computed(() => {
  const j = job.value!
  const deadline = j.deadline ? new Date(j.deadline) : null
  const daysLeft = deadline
    ? Math.max(0, Math.ceil((deadline.getTime() - Date.now()) / 86_400_000))
    : 0
  return [
    { label: 'Lượt xem',     value: String(j.viewCount ?? 0),         icon: 'visibility',  iconBg: '#eff6ff', iconColor: '#2563eb' },
    { label: 'Ứng viên',     value: String(j.applicationCount ?? 0),  icon: 'groups',      iconBg: '#fff7ed', iconColor: '#ea580c' },
    { label: 'Số lần sửa',   value: `${j.editCount ?? 0}/1`,          icon: 'history_edu', iconBg: '#f5f3ff', iconColor: '#7c3aed' },
    { label: 'Ngày còn lại', value: `${daysLeft} ngày`,               icon: 'schedule',    iconBg: '#fef2f2', iconColor: '#dc2626' },
  ]
})

// ── Side info ────────────────────────────────────────────
const infoItems = computed(() => {
  const j = job.value!
  const salary = j.salaryNegotiable || (!j.salaryMin && !j.salaryMax)
    ? 'Thỏa thuận'
    : `${((j.salaryMin ?? 0) / 1_000_000).toFixed(0)} – ${((j.salaryMax ?? 0) / 1_000_000).toFixed(0)} triệu`

  const exp = j.experienceYearsMax
    ? `${j.experienceYearsMin} – ${j.experienceYearsMax} năm`
    : `${j.experienceYearsMin} năm trở lên`

  return [
    { label: 'Ngành nghề',  value: j.industry?.name ?? '',                               icon: 'category' },
    { label: 'Cấp độ',      value: j.level?.name ?? '',                                  icon: 'bar_chart' },
    { label: 'Kinh nghiệm', value: exp,                                                   icon: 'history' },
    { label: 'Mức lương',   value: salary,                                                icon: 'monetization_on', highlight: true },
    { label: 'Hình thức',   value: WORK_TYPE_LABELS[j.workType as keyof typeof WORK_TYPE_LABELS] ?? j.workType,    icon: 'work' },
    { label: 'Số lượng',    value: `${j.headcount} người`,                               icon: 'person_add' },
  ]
})

const timeline = computed(() => {
  const j = job.value!
  const fmt = (d: string) => new Date(d).toLocaleDateString('vi-VN')
  const events = []
  if (j.createdAt)   events.push({ label: 'Ngày tạo',  date: fmt(j.createdAt) })
  if (j.publishedAt) events.push({ label: 'Ngày đăng', date: fmt(j.publishedAt) })
  if (j.deadline)    events.push({ label: 'Hạn cuối',  date: fmt(j.deadline), isDeadline: true })
  return events
})

const conversionPercent = computed(() => {
  const j = job.value!
  if (!j.headcount || !j.applicationCount) return 0
  return Math.min(100, Math.round((j.applicationCount / (j.headcount * 30)) * 100))
})
</script>

<style scoped>
.page-wrapper {
  /* Bỏ padding hoàn toàn vì RecruiterLayout parent đã có px-8 pb-8 */
  width: 100%;
}

.page-inner {
  width: 100%;
  /* Bỏ max-width để full màn hình tĩnh 100% giống JobPostingsPage */
}

/* Two-col body */
.body-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 2rem;
  margin-top: 2rem;
}
@media (min-width: 1024px) {
  .body-grid {
    grid-template-columns: 2fr 1fr;
  }
}

.col-main { min-width: 0; }
.col-side  { min-width: 0; }
</style>
