<template>
  <div class="page-wrapper">
    <div class="page-inner">

      <!-- ── Header ─────────────────────────────────── -->
      <div class="page-header">
        <div>
          <nav class="breadcrumb">
            <router-link to="/recruiter/jobs" class="breadcrumb-link">Tin tuyển dụng</router-link>
            <span class="material-symbols-outlined breadcrumb-sep">chevron_right</span>
            <span class="breadcrumb-current">Đăng tin mới</span>
          </nav>
          <h2 class="page-title">Tạo tin tuyển dụng mới</h2>
          <p class="page-subtitle">Điền đầy đủ thông tin để thu hút các ứng viên tiềm năng nhất.</p>
        </div>
        <div class="header-actions">
          <button class="btn-outline" type="button" :disabled="submitting" @click="saveDraft">
            Lưu nháp
          </button>
          <button class="btn-primary" type="button" :disabled="submitting" @click="publish">
            <span v-if="submitting" class="spinner"></span>
            <span v-else>Đăng tin ngay</span>
            <span v-if="!submitting" class="material-symbols-outlined">send</span>
          </button>
        </div>
      </div>

      <!-- ── Sections ───────────────────────────────── -->
      <div class="sections">
        <CreateJobBasicInfo />
        <CreateJobContent />
        <CreateJobSkills />
        <CreateJobSalary />
        <CreateJobLocation />
        <CreateJobAdvanced />
      </div>

      <!-- ── Footer actions ─────────────────────────── -->
      <div class="page-footer">
        <button class="btn-outline" type="button" :disabled="submitting" @click="saveDraft">
          Lưu nháp
        </button>
        <button class="btn-primary" type="button" :disabled="submitting" @click="publish">
          <span v-if="submitting" class="spinner"></span>
          <span v-else>Đăng tin ngay</span>
          <span v-if="!submitting" class="material-symbols-outlined">send</span>
        </button>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, provide } from 'vue'
import { useRouter } from 'vue-router'
import { employerJobPostingService } from '@/services/employerJobPosting.service'
import { useToast } from '@/composables/useToast'
import {
  CREATE_JOB_FORM_KEY,
  CREATE_JOB_ERRORS_KEY,
  createInitialForm,
  validateCreateJobForm,
} from '@/composables/useCreateJobForm'
import type { ReqCreateJobPostingDTO } from '@/types/jobPosting.types'
import CreateJobBasicInfo from '@/components/recruiter/jobs/CreateJobBasicInfo.vue'
import CreateJobContent from '@/components/recruiter/jobs/CreateJobContent.vue'
import CreateJobSkills from '@/components/recruiter/jobs/CreateJobSkills.vue'
import CreateJobSalary from '@/components/recruiter/jobs/CreateJobSalary.vue'
import CreateJobLocation from '@/components/recruiter/jobs/CreateJobLocation.vue'
import CreateJobAdvanced from '@/components/recruiter/jobs/CreateJobAdvanced.vue'

const router = useRouter()
const toast = useToast()

const form = reactive(createInitialForm())
const errors = ref<Record<string, string>>({})
const submitting = ref(false)

provide(CREATE_JOB_FORM_KEY, form)
provide(CREATE_JOB_ERRORS_KEY, errors)

function stripHtml(html: string): string {
  return html.replace(/<[^>]*>/g, '').trim()
}

function buildPayload(): ReqCreateJobPostingDTO {
  return {
    title: form.title.trim(),
    description: form.description,
    requirements: form.requirements,
    benefits: stripHtml(form.benefits) ? form.benefits : undefined,
    industryId: form.industryId!,
    levelId: form.levelId!,
    experienceYearsMin: form.experienceYearsMin ?? 0,
    experienceYearsMax: form.experienceYearsMax ?? undefined,
    salaryMin: form.salaryNegotiable || !form.salaryMin
      ? undefined
      : Number(form.salaryMin.replace(/\D/g, '')),
    salaryMax: form.salaryNegotiable || !form.salaryMax
      ? undefined
      : Number(form.salaryMax.replace(/\D/g, '')),
    salaryNegotiable: form.salaryNegotiable,
    workType: form.workType,
    headcount: form.headcount,
    deadline: new Date(form.deadline).toISOString(),
    locations: form.locations
      .filter(l => l.provinceId !== null || l.isRemote)
      .map(l => ({
        provinceId: l.provinceId ?? 0,
        addressDetail: l.addressDetail || undefined,
        isRemote: l.isRemote,
      })),
    skills: form.skills.length
      ? form.skills.map(s => ({ skillId: s.skillId, isRequired: s.isRequired }))
      : undefined,
    isFeatured: form.isFeatured,
    isUrgent: form.isUrgent,
  }
}

async function saveDraft() {
  const draftErrs: Record<string, string> = {}
  if (!form.title.trim()) {
    draftErrs.title = 'Tiêu đề tin tuyển dụng là bắt buộc.'
  } else if (form.title.trim().length < 10) {
    draftErrs.title = 'Tiêu đề phải có ít nhất 10 ký tự.'
  }
  if (!form.industryId) draftErrs.industryId = 'Vui lòng chọn ngành nghề.'
  if (!form.levelId) draftErrs.levelId = 'Vui lòng chọn cấp bậc.'
  if (!form.deadline) draftErrs.deadline = 'Hạn nộp hồ sơ là bắt buộc.'

  errors.value = draftErrs
  if (Object.keys(draftErrs).length > 0) {
    toast.warning('Thiếu thông tin', 'Vui lòng điền đầy đủ thông tin cơ bản trước khi lưu nháp.')
    return
  }

  submitting.value = true
  try {
    const payload = buildPayload()
    const result = await employerJobPostingService.createJob(payload)
    toast.success('Đã lưu nháp!', `Tin "${result.title}" đã được lưu vào bản nháp.`)
    router.push({ name: 'recruiter-jobs' })
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Không thể lưu nháp. Vui lòng thử lại.'
    toast.error('Lưu nháp thất bại', msg)
  } finally {
    submitting.value = false
  }
}

async function publish() {
  errors.value = validateCreateJobForm(form)
  if (Object.keys(errors.value).length > 0) {
    toast.warning('Thông tin chưa hợp lệ', 'Vui lòng kiểm tra lại các trường được đánh dấu.')
    // Scroll to first error
    setTimeout(() => {
      const el = document.querySelector('.field-error')
      el?.scrollIntoView({ behavior: 'smooth', block: 'center' })
    }, 100)
    return
  }

  submitting.value = true
  try {
    const payload = buildPayload()
    const result = await employerJobPostingService.createJob(payload)
    // Gửi duyệt ngay sau khi tạo
    await employerJobPostingService.pendingApproval(result.id)
    toast.success('Đăng tin thành công!', `Tin "${result.title}" đã được gửi để duyệt.`)
    router.push({ name: 'recruiter-jobs' })
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Không thể đăng tin. Vui lòng thử lại.'
    toast.error('Đăng tin thất bại', msg)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.page-wrapper {
  width: 100%;
}
.page-inner {
  width: 100%;
}

/* Header */
.page-header {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 2rem;
}
@media (min-width: 768px) {
  .page-header {
    flex-direction: row;
    align-items: flex-end;
    justify-content: space-between;
  }
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  color: #64748b;
  margin-bottom: 0.5rem;
}
.breadcrumb-link {
  color: #64748b;
  text-decoration: none;
  transition: color 0.15s;
}
.breadcrumb-link:hover { color: #4B9AF6; }
.breadcrumb-sep { font-size: 0.75rem; }
.breadcrumb-current { color: #0f172a; font-weight: 500; }

.page-title {
  font-size: 1.875rem;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.025em;
}
.page-subtitle { color: #64748b; margin-top: 0.25rem; font-size: 1rem; }

.header-actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

/* Buttons */
.btn-outline {
  padding: 0.625rem 1.5rem;
  border-radius: 0.75rem;
  border: 1px solid #e2e8f0;
  background: #fff;
  color: #374151;
  font-weight: 600;
  font-size: 1rem;
  font-family: inherit;
  cursor: pointer;
  transition: background 0.15s, transform 0.1s;
}
.btn-outline:hover:not(:disabled) { background: #f8fafc; }
.btn-outline:active:not(:disabled) { transform: scale(0.97); }
.btn-outline:disabled { opacity: 0.5; cursor: not-allowed; }

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
.btn-primary:hover:not(:disabled) {
  opacity: 0.92;
  box-shadow: 0 6px 20px rgba(75,154,246,.4);
}
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

/* Sections */
.sections { display: flex; flex-direction: column; gap: 2rem; }

/* Footer */
.page-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  padding-top: 1rem;
  margin-top: 0.5rem;
  border-top: 1px solid #e2e8f0;
}
</style>
