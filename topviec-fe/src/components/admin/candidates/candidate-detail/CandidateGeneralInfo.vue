<template>
  <div class="space-y-5">
    <!-- Section 1: Thông tin cá nhân -->
    <div class="info-card">
      <div class="card-title-row">
        <span class="material-symbols-outlined card-icon">person</span>
        <h3 class="card-title">Thông tin cá nhân</h3>
      </div>
      <div class="fields-grid">
        <div>
          <p class="field-label">Ngày sinh</p>
          <p class="field-value">{{ formatDate(candidate.dateOfBirth) }}</p>
        </div>
        <div>
          <p class="field-label">Giới tính</p>
          <p class="field-value">{{ genderLabel }}</p>
        </div>
        <div>
          <p class="field-label">Email</p>
          <p class="field-value">{{ candidate.email || '—' }}</p>
        </div>
        <div>
          <p class="field-label">Số điện thoại</p>
          <p class="field-value">{{ candidate.phoneDisplay || '—' }}</p>
        </div>
        <div class="col-span-2">
          <p class="field-label">Liên kết cá nhân</p>
          <div class="flex items-center gap-2 mt-1">
            <!-- LinkedIn -->
            <a
              v-if="candidate.linkedinUrl"
              :href="candidate.linkedinUrl"
              target="_blank"
              class="social-icon-btn text-blue-600 hover:bg-blue-50"
              title="LinkedIn"
            >
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-4 h-4">
                <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433a2.062 2.062 0 0 1-2.063-2.065 2.064 2.064 0 1 1 2.063 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z"/>
              </svg>
            </a>
            <span v-else class="social-icon-btn text-slate-300 cursor-default" title="LinkedIn (chưa cập nhật)">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-4 h-4">
                <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433a2.062 2.062 0 0 1-2.063-2.065 2.064 2.064 0 1 1 2.063 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z"/>
              </svg>
            </span>

            <!-- GitHub -->
            <a
              v-if="candidate.githubUrl"
              :href="candidate.githubUrl"
              target="_blank"
              class="social-icon-btn text-slate-700 hover:bg-slate-100 dark:text-slate-300 dark:hover:bg-slate-700"
              title="GitHub"
            >
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-4 h-4">
                <path d="M12 .297c-6.63 0-12 5.373-12 12 0 5.303 3.438 9.8 8.205 11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61C4.422 18.07 3.633 17.7 3.633 17.7c-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305.76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22 0 1.606-.015 2.896-.015 3.286 0 .315.21.69.825.57C20.565 22.092 24 17.592 24 12.297c0-6.627-5.373-12-12-12"/>
              </svg>
            </a>
            <span v-else class="social-icon-btn text-slate-300 cursor-default" title="GitHub (chưa cập nhật)">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="w-4 h-4">
                <path d="M12 .297c-6.63 0-12 5.373-12 12 0 5.303 3.438 9.8 8.205 11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61C4.422 18.07 3.633 17.7 3.633 17.7c-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305.76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22 0 1.606-.015 2.896-.015 3.286 0 .315.21.69.825.57C20.565 22.092 24 17.592 24 12.297c0-6.627-5.373-12-12-12"/>
              </svg>
            </span>

            <!-- Website cá nhân -->
            <a
              v-if="candidate.personalWebsite"
              :href="candidate.personalWebsite"
              target="_blank"
              class="social-icon-btn text-emerald-600 hover:bg-emerald-50"
              title="Website cá nhân"
            >
              <span class="material-symbols-outlined" style="font-size:16px;">language</span>
            </a>
            <span v-else class="social-icon-btn text-slate-300 cursor-default" title="Website cá nhân (chưa cập nhật)">
              <span class="material-symbols-outlined" style="font-size:16px;">language</span>
            </span>
          </div>
        </div>
      </div>

      <!-- Bio -->
      <div class="mt-5 pt-5 border-t border-slate-100">
        <p class="field-label">Giới thiệu bản thân (Bio)</p>
        <p v-if="candidate.bio" class="field-value mt-1 leading-relaxed text-slate-600 dark:text-slate-400 whitespace-pre-line">{{ candidate.bio }}</p>
        <p v-else class="field-value text-slate-400 italic mt-1">Chưa cập nhật</p>
      </div>
    </div>

    <!-- Section 2: Mong muốn nghề nghiệp -->
    <div class="info-card">
      <div class="card-title-row">
        <span class="material-symbols-outlined card-icon">work</span>
        <h3 class="card-title">Mong muốn nghề nghiệp</h3>
      </div>
      <div class="fields-grid">
        <div>
          <p class="field-label">Trạng thái tìm việc</p>
          <p class="field-value">{{ jobSeekingLabel }}</p>
        </div>
        <div>
          <p class="field-label">Vị trí mong muốn</p>
          <p class="field-value">{{ candidate.preferredJobTitle || 'Chưa cập nhật' }}</p>
        </div>
        <div>
          <p class="field-label">Địa điểm làm việc</p>
          <p class="field-value">{{ candidate.preferredLocationName || 'Chưa cập nhật' }}</p>
        </div>
        <div>
          <p class="field-label">Hình thức làm việc</p>
          <p class="field-value">{{ workTypeLabel }}</p>
        </div>
        <div class="col-span-2">
          <p class="field-label">Mức lương mong muốn</p>
          <p class="field-value">{{ salaryDisplay }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { ResAdminCandidateDetailDTO } from '@/types/adminCandidate.types'
import { 
  JOB_SEEKING_STATUS_LABELS, 
  JobSeekingStatus,
  PREFERRED_WORK_TYPE_LABELS,
  PreferredWorkType
} from '@/constants/candidateProfile.constants'

const props = defineProps<{
  candidate: ResAdminCandidateDetailDTO
}>()

// ─── Formatters ──────────────────────────────────────────────────────────────

function formatDate(raw: string | null | undefined): string {
  if (!raw) return 'Chưa cập nhật'
  // Handle both 'YYYY-MM-DD' and full ISO datetime strings
  const d = new Date(raw)
  if (isNaN(d.getTime())) return raw
  return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
}

const GENDER_LABELS: Record<string, string> = {
  male: 'Nam',
  female: 'Nữ',
  other: 'Khác',
}

const genderLabel = computed(() =>
  props.candidate.gender ? (GENDER_LABELS[props.candidate.gender] ?? props.candidate.gender) : 'Chưa cập nhật'
)

const jobSeekingLabel = computed(() =>
  JOB_SEEKING_STATUS_LABELS[props.candidate.jobSeekingStatus as JobSeekingStatus] || 'Chưa cập nhật'
)

const workTypeLabel = computed(() =>
  PREFERRED_WORK_TYPE_LABELS[props.candidate.preferredWorkType as PreferredWorkType] || 'Chưa cập nhật'
)

const salaryDisplay = computed(() => {
  const { expectedSalaryMin, expectedSalaryMax, salaryNegotiable } = props.candidate
  if (salaryNegotiable) return 'Thỏa thuận'
  if (expectedSalaryMin && expectedSalaryMax)
    return `${expectedSalaryMin.toLocaleString('vi-VN')} – ${expectedSalaryMax.toLocaleString('vi-VN')} VNĐ`
  if (expectedSalaryMin) return `Từ ${expectedSalaryMin.toLocaleString('vi-VN')} VNĐ`
  if (expectedSalaryMax) return `Đến ${expectedSalaryMax.toLocaleString('vi-VN')} VNĐ`
  return 'Chưa cập nhật'
})

</script>

<style scoped>
.info-card {
  background: #fff;
  border-radius: 14px;
  padding: 22px 24px;
  border: 1px solid #f1eeee;
  box-shadow: 0 2px 12px -4px rgba(87, 66, 64, 0.07);
}

.dark .info-card {
  background: rgb(15 23 42);
  border-color: rgb(30 41 59);
}

.card-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 18px;
}

.card-icon {
  font-size: 20px;
  color: #963131;
}

.card-title {
  font-size: 16px;
  font-weight: 700;
  color: #1b1c18;
  margin: 0;
}

.dark .card-title { color: #f1f5f9; }

.fields-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px 32px;
}

.col-span-2 { grid-column: span 2; }

.field-label {
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #96505050;
  color: #9a7676;
  margin-bottom: 3px;
}

.field-value {
  font-size: 14px;
  font-weight: 500;
  color: #1b1c18;
}

.dark .field-value { color: #cbd5e1; }

.social-icon-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  transition: background 0.15s;
  flex-shrink: 0;
}
</style>