import type { InjectionKey, Reactive, Ref } from 'vue'
import { WorkType } from '@/constants/jobPosting.constants'

export interface CreateJobFormLocation {
  provinceId: number | null
  addressDetail: string
  isRemote: boolean
}

export interface CreateJobFormSkill {
  skillId: number
  skillName: string
  isRequired: boolean
}

export interface CreateJobFormState {
  // Basic info
  title: string
  industryId: number | null
  levelId: number | null
  headcount: number
  deadline: string
  // Content
  description: string
  requirements: string
  benefits: string
  // Skills
  skills: CreateJobFormSkill[]
  experienceYearsMin: number | null
  experienceYearsMax: number | null
  // Salary
  salaryMin: string
  salaryMax: string
  salaryNegotiable: boolean
  workType: WorkType
  // Locations
  locations: CreateJobFormLocation[]
  // Advanced
  isFeatured: boolean
  isUrgent: boolean
}

export const CREATE_JOB_FORM_KEY: InjectionKey<Reactive<CreateJobFormState>> = Symbol('createJobForm')
export const CREATE_JOB_ERRORS_KEY: InjectionKey<Ref<Record<string, string>>> = Symbol('createJobErrors')

export function createInitialForm(): CreateJobFormState {
  return {
    title: '',
    industryId: null,
    levelId: null,
    headcount: 1,
    deadline: '',
    description: '',
    requirements: '',
    benefits: '',
    skills: [],
    experienceYearsMin: null,
    experienceYearsMax: null,
    salaryMin: '',
    salaryMax: '',
    salaryNegotiable: false,
    workType: WorkType.FULL_TIME,
    locations: [{ provinceId: null, addressDetail: '', isRemote: false }],
    isFeatured: false,
    isUrgent: false,
  }
}

function stripHtml(html: string): string {
  return html.replace(/<[^>]*>/g, '').trim()
}

export function validateCreateJobForm(form: CreateJobFormState): Record<string, string> {
  const errors: Record<string, string> = {}

  if (!form.title.trim()) {
    errors.title = 'Tiêu đề tin tuyển dụng là bắt buộc.'
  } else if (form.title.trim().length < 10) {
    errors.title = 'Tiêu đề phải có ít nhất 10 ký tự.'
  }

  if (!form.industryId) {
    errors.industryId = 'Vui lòng chọn ngành nghề.'
  }

  if (!form.levelId) {
    errors.levelId = 'Vui lòng chọn cấp bậc.'
  }

  if (!form.headcount || form.headcount < 1) {
    errors.headcount = 'Số lượng tuyển phải ít nhất là 1.'
  }

  if (!form.deadline) {
    errors.deadline = 'Hạn nộp hồ sơ là bắt buộc.'
  } else {
    const deadline = new Date(form.deadline)
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    if (deadline <= today) {
      errors.deadline = 'Hạn nộp hồ sơ phải là ngày trong tương lai.'
    }
  }

  const descriptionText = stripHtml(form.description)
  if (!descriptionText) {
    errors.description = 'Mô tả công việc là bắt buộc.'
  } else if (descriptionText.length < 20) {
    errors.description = 'Mô tả công việc phải có ít nhất 20 ký tự.'
  }

  const requirementsText = stripHtml(form.requirements)
  if (!requirementsText) {
    errors.requirements = 'Yêu cầu công việc là bắt buộc.'
  } else if (requirementsText.length < 20) {
    errors.requirements = 'Yêu cầu công việc phải có ít nhất 20 ký tự.'
  }

  if (form.experienceYearsMin !== null && form.experienceYearsMax !== null) {
    if (form.experienceYearsMax < form.experienceYearsMin) {
      errors.experienceYearsMax = 'Số năm kinh nghiệm tối đa phải lớn hơn tối thiểu.'
    }
  }

  if (!form.salaryNegotiable) {
    const min = form.salaryMin ? Number(form.salaryMin.replace(/\D/g, '')) : 0
    const max = form.salaryMax ? Number(form.salaryMax.replace(/\D/g, '')) : 0
    if (min > 0 && max > 0 && max < min) {
      errors.salaryMax = 'Lương tối đa phải lớn hơn lương tối thiểu.'
    }
  }

  const hasValidLocation = form.locations.some(loc => loc.provinceId !== null || loc.isRemote)
  if (!hasValidLocation) {
    errors.locations = 'Vui lòng chọn ít nhất một địa điểm làm việc.'
  }

  return errors
}
