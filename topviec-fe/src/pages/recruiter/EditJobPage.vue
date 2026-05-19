<template>
  <div class="page-wrapper">
    <div class="page-inner">

      <!-- ── Loading skeleton ──────────────────────────── -->
      <div v-if="loading" class="loading-wrap">
        <div class="skeleton skeleton-title"></div>
        <div class="skeleton skeleton-sub"></div>
        <div v-for="n in 4" :key="n" class="skeleton skeleton-card"></div>
      </div>

      <template v-else>
        <!-- ── Header ───────────────────────────────────── -->
        <div class="page-header">
          <div>
            <nav class="breadcrumb">
              <router-link to="/recruiter/jobs" class="breadcrumb-link">Tin tuyển dụng</router-link>
              <span class="material-symbols-outlined breadcrumb-sep">chevron_right</span>
              <router-link :to="`/recruiter/jobs/${jobId}`" class="breadcrumb-link">Chi tiết tin</router-link>
              <span class="material-symbols-outlined breadcrumb-sep">chevron_right</span>
              <span class="breadcrumb-current">Chỉnh sửa</span>
            </nav>

            <div class="title-row">
              <h2 class="page-title">Cập nhật tin tuyển dụng</h2>
              <span class="edit-badge">
                <span class="material-symbols-outlined">edit_note</span>
                Đang chỉnh sửa
              </span>
            </div>

            <p class="page-subtitle">Cập nhật thông tin để tin tuyển dụng luôn chính xác và hấp dẫn nhất.</p>
          </div>

          <div class="header-actions">
            <router-link :to="`/recruiter/jobs/${jobId}`" class="btn-outline">Hủy bỏ</router-link>
            <button class="btn-primary" type="button" :disabled="submitting" @click="handleUpdate">
              <span v-if="submitting" class="spinner"></span>
              <span v-else>Cập nhật tin</span>
              <span v-if="!submitting" class="material-symbols-outlined">save</span>
            </button>
          </div>
        </div>

        <!-- ── Edit notice ───────────────────────────────── -->
        <div class="edit-notice">
          <span class="material-symbols-outlined notice-icon">info</span>
          <div>
            <p class="notice-title">Lưu ý khi chỉnh sửa</p>
            <p class="notice-desc">
              Tin tuyển dụng sau khi cập nhật sẽ cần được duyệt lại bởi quản trị viên.
              Hạn nộp hồ sơ hiện không thể thay đổi trực tiếp (Vui lòng liên hệ hỗ trợ hoặc sử dụng tính năng Gia hạn).
              Tin này đã được chỉnh sửa <strong>{{ editCount }} lần</strong>.
            </p>
          </div>
        </div>

        <!-- ── Sections ──────────────────────────────────── -->
        <div class="sections">
          <CreateJobBasicInfo :disabled-deadline="true" />
          <CreateJobContent />
          <CreateJobSkills />
          <CreateJobSalary />
          <CreateJobLocation />
          <CreateJobAdvanced />
        </div>

        <!-- ── Footer actions ───────────────────────────── -->
        <div class="page-footer">
          <router-link :to="`/recruiter/jobs/${jobId}`" class="btn-outline">Hủy bỏ</router-link>
          <button class="btn-primary" type="button" :disabled="submitting" @click="handleUpdate">
            <span v-if="submitting" class="spinner"></span>
            <span v-else>Cập nhật tin</span>
            <span v-if="!submitting" class="material-symbols-outlined">save</span>
          </button>
        </div>
      </template>

    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, provide, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { publicJobPostingService } from '@/services/jobPosting.service'
import { employerJobPostingService } from '@/services/employerJobPosting.service'
import { useToast } from '@/composables/useToast'
import { WorkType } from '@/constants/jobPosting.constants'
import {
  CREATE_JOB_FORM_KEY,
  CREATE_JOB_ERRORS_KEY,
  createInitialForm,
  validateCreateJobForm,
} from '@/composables/useCreateJobForm'
import type { ReqUpdateJobPostingDTO } from '@/types/jobPosting.types'
import CreateJobBasicInfo from '@/components/recruiter/jobs/CreateJobBasicInfo.vue'
import CreateJobContent   from '@/components/recruiter/jobs/CreateJobContent.vue'
import CreateJobSkills    from '@/components/recruiter/jobs/CreateJobSkills.vue'
import CreateJobSalary    from '@/components/recruiter/jobs/CreateJobSalary.vue'
import CreateJobLocation  from '@/components/recruiter/jobs/CreateJobLocation.vue'
import CreateJobAdvanced  from '@/components/recruiter/jobs/CreateJobAdvanced.vue'

const route  = useRoute()
const router = useRouter()
const toast  = useToast()

const jobId    = Number(route.params.id)
const loading  = ref(true)
const submitting = ref(false)
const editCount  = ref(0)

const form   = reactive(createInitialForm())
const errors = ref<Record<string, string>>({})

provide(CREATE_JOB_FORM_KEY, form)
provide(CREATE_JOB_ERRORS_KEY, errors)

// ── Helpers ────────────────────────────────────────────────────────────────
function toDateInput(iso: string): string {
  return iso ? iso.split('T')[0] : ''
}

function toSalaryString(val?: number): string {
  return val != null ? String(val) : ''
}

function stripHtml(html: string): string {
  return html.replace(/<[^>]*>/g, '').trim()
}

// ── Load existing job ───────────────────────────────────────────────────────
onMounted(async () => {
  try {
    // Calling public API to get job detail
    const job = await publicJobPostingService.getById(jobId)

    editCount.value = job.editCount

    // Basic info
    form.title      = job.title
    form.industryId = job.industry?.id ?? null
    form.levelId    = job.level?.id ?? null
    form.headcount  = job.headcount
    form.deadline   = toDateInput(job.deadline)

    // Content
    form.description  = job.description
    form.requirements = job.requirements
    form.benefits     = job.benefits ?? ''

    // Skills
    form.skills = job.skills.map(s => ({
      skillId:   s.skillId,
      skillName: s.skillName,
      isRequired: s.isRequired,
    }))
    form.experienceYearsMin = job.experienceYearsMin ?? null
    form.experienceYearsMax = job.experienceYearsMax ?? null

    // Salary
    form.salaryMin        = toSalaryString(job.salaryMin)
    form.salaryMax        = toSalaryString(job.salaryMax)
    form.salaryNegotiable = job.salaryNegotiable
    form.workType         = (job.workType as WorkType) ?? WorkType.FULL_TIME

    // Locations
    form.locations = job.locations.length
      ? job.locations.map(l => ({
          provinceId:    l.provinceId || l.id,
          addressDetail: l.addressDetail ?? '',
          isRemote:      l.isRemote,
        }))
      : [{ provinceId: null, addressDetail: '', isRemote: false }]

    // Advanced
    form.isFeatured = job.isFeatured
    form.isUrgent   = job.isUrgent
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Không thể tải dữ liệu tin tuyển dụng.'
    toast.error('Tải dữ liệu thất bại', msg)
    router.push({ name: 'recruiter-jobs' })
  } finally {
    loading.value = false
  }
})

// ── Build payload ──────────────────────────────────────────────────────────
function buildPayload(): ReqUpdateJobPostingDTO {
  return {
    title:               form.title.trim(),
    description:         form.description,
    requirements:        form.requirements,
    benefits:            stripHtml(form.benefits) ? form.benefits : undefined,
    industryId:          form.industryId!,
    levelId:             form.levelId!,
    experienceYearsMin:  form.experienceYearsMin ?? 0,
    experienceYearsMax:  form.experienceYearsMax ?? undefined,
    salaryMin: form.salaryNegotiable || !form.salaryMin
      ? undefined
      : Number(form.salaryMin.replace(/\D/g, '')),
    salaryMax: form.salaryNegotiable || !form.salaryMax
      ? undefined
      : Number(form.salaryMax.replace(/\D/g, '')),
    salaryNegotiable: form.salaryNegotiable,
    workType:         form.workType,
    headcount:        form.headcount,
    deadline:         new Date(form.deadline).toISOString(),
    locations: form.locations
      .filter(l => l.provinceId !== null || l.isRemote)
      .map(l => ({
        provinceId:    l.provinceId ?? 0,
        addressDetail: l.addressDetail || undefined,
        isRemote:      l.isRemote,
      })),
    skills: form.skills.length
      ? form.skills.map(s => ({ skillId: s.skillId, isRequired: s.isRequired }))
      : undefined,
    isFeatured: form.isFeatured,
    isUrgent:   form.isUrgent,
  }
}

// ── Submit ─────────────────────────────────────────────────────────────────
async function handleUpdate() {
  errors.value = validateCreateJobForm(form)
  if (Object.keys(errors.value).length > 0) {
    toast.warning('Thông tin chưa hợp lệ', 'Vui lòng kiểm tra lại các trường được đánh dấu.')
    setTimeout(() => {
      document.querySelector('.field-error')?.scrollIntoView({ behavior: 'smooth', block: 'center' })
    }, 100)
    return
  }

  submitting.value = true
  try {
    const payload = buildPayload()
    const result  = await employerJobPostingService.updateJob(jobId, payload)
    toast.success('Cập nhật thành công!', `Tin "${result.title}" đã được cập nhật và gửi duyệt lại.`)
    router.push({ name: 'recruiter-jobs-detail', params: { id: jobId } })
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Không thể cập nhật tin. Vui lòng thử lại.'
    toast.error('Cập nhật thất bại', msg)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.page-wrapper { width: 100%; }
.page-inner   { width: 100%; }

/* Loading skeleton */
.loading-wrap { display: flex; flex-direction: column; gap: 1.5rem; }
.skeleton {
  background: linear-gradient(90deg, #f1f5f9 25%, #e2e8f0 50%, #f1f5f9 75%);
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite;
  border-radius: 1rem;
}
.skeleton-title  { height: 2.5rem; width: 40%; }
.skeleton-sub    { height: 1rem; width: 60%; border-radius: 0.5rem; }
.skeleton-card   { height: 12rem; }
@keyframes shimmer { to { background-position: -200% 0; } }

/* Header */
.page-header {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 1.5rem;
}
@media (min-width: 768px) {
  .page-header { flex-direction: row; align-items: flex-end; justify-content: space-between; }
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  color: #64748b;
  margin-bottom: 0.5rem;
}
.breadcrumb-link { color: #64748b; text-decoration: none; transition: color 0.15s; }
.breadcrumb-link:hover { color: #4B9AF6; }
.breadcrumb-sep { font-size: 0.75rem; }
.breadcrumb-current { color: #0f172a; font-weight: 500; }

.title-row { display: flex; align-items: center; gap: 0.75rem; flex-wrap: wrap; }
.page-title { font-size: 1.875rem; font-weight: 800; color: #0f172a; letter-spacing: -0.025em; }

.edit-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.25rem 0.75rem;
  background: #fef3c7;
  color: #b45309;
  font-size: 0.75rem;
  font-weight: 700;
  border-radius: 9999px;
  margin-top: 0.25rem;
}
.edit-badge .material-symbols-outlined { font-size: 1rem; }
.page-subtitle { color: #64748b; margin-top: 0.25rem; font-size: 1rem; }

/* Edit notice */
.edit-notice {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  padding: 1rem;
  background: #fffbeb;
  border: 1px solid #fde68a;
  border-radius: 0.75rem;
  margin-bottom: 2rem;
}
.notice-icon  { color: #f59e0b; flex-shrink: 0; margin-top: 0.125rem; }
.notice-title { font-size: 0.875rem; font-weight: 600; color: #92400e; margin-bottom: 0.25rem; }
.notice-desc  { font-size: 0.875rem; color: #b45309; line-height: 1.5; }

.header-actions { display: flex; align-items: center; gap: 0.75rem; }

/* Buttons */
.btn-outline {
  display: inline-flex;
  align-items: center;
  padding: 0.625rem 1.5rem;
  border-radius: 0.75rem;
  border: 1px solid #e2e8f0;
  background: #fff;
  color: #374151;
  font-weight: 600;
  font-size: 1rem;
  font-family: inherit;
  text-decoration: none;
  cursor: pointer;
  transition: background 0.15s, transform 0.1s;
}
.btn-outline:hover { background: #f8fafc; }
.btn-outline:active { transform: scale(0.97); }

.btn-primary {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.625rem 2rem;
  border-radius: 0.75rem;
  border: none;
  background: #4B9AF6;
  color: #fff;
  font-weight: 700;
  font-size: 1rem;
  font-family: inherit;
  cursor: pointer;
  box-shadow: 0 4px 14px rgba(75,154,246,.3);
  transition: opacity 0.15s, box-shadow 0.15s, transform 0.1s;
}
.btn-primary:hover:not(:disabled) { opacity: 0.92; box-shadow: 0 6px 20px rgba(75,154,246,.4); }
.btn-primary:active:not(:disabled) { transform: scale(0.97); }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; box-shadow: none; }
.btn-primary .material-symbols-outlined { font-size: 1.125rem; }

.spinner {
  width: 1rem;
  height: 1rem;
  border: 2px solid rgba(255,255,255,.4);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

.sections { display: flex; flex-direction: column; gap: 2rem; }

.page-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  padding-top: 1rem;
  margin-top: 0.5rem;
  border-top: 1px solid #e2e8f0;
}
</style>
