<template>
  <div class="space-y-8 pt-6" style="font-family: 'Manrope', sans-serif;">
    <div v-if="store.loading && !store.company" class="flex justify-center py-20">
      <span class="material-symbols-outlined animate-spin text-primary text-4xl">progress_activity</span>
    </div>

    <template v-else>
      <div
        v-if="store.company"
        class="rounded-2xl p-4 flex items-center justify-between"
        :class="statusBarStyle.bg"
      >
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-full flex items-center justify-center" :class="statusBarStyle.iconBg">
            <span class="material-symbols-outlined" :class="statusBarStyle.iconColor">{{ statusBarStyle.icon }}</span>
          </div>
          <div>
            <p class="text-sm font-bold" :class="statusBarStyle.titleColor">{{ statusBarStyle.title }}</p>
            <p class="text-xs" :class="statusBarStyle.descColor">{{ statusBarStyle.desc }}</p>
          </div>
        </div>
        <span class="px-3 py-1 text-xs font-bold uppercase tracking-wider rounded-full shrink-0" :class="statusBarStyle.badge">
          {{ statusBarStyle.badgeText }}
        </span>
      </div>

      <CompanyBasicInfo
        v-model:company-name="form.companyName"
        v-model:industry="form.industry"
        v-model:company-size="form.companySize"
        v-model:tax-code="form.taxCode"
        v-model:founded-year="form.foundedYear"
        v-model:cover-url="form.coverUrl"
        v-model:logo-url="form.logoUrl"
        :errors="errors"
        @cover-file-change="draftFiles.coverFile = $event"
        @logo-file-change="draftFiles.logoFile = $event"
      />

      <CompanyAbout
        v-model:description="form.description"
        v-model:culture="form.culture"
        v-model:benefits="form.benefits"
        :errors="errors"
      />

      <CompanyVerification
        v-model:license-file="form.licenseFile"
        @license-file-change="draftFiles.licenseFile = $event"
      />

      <CompanyContact
        v-model:email="form.email"
        v-model:phone="form.phone"
        v-model:website="form.website"
        v-model:province-id="form.provinceId"
        v-model:address="form.address"
        v-model:linkedin="form.linkedin"
        v-model:twitter="form.twitter"
        v-model:facebook="form.facebook"
        :errors="errors"
      />

      <div class="flex justify-end gap-3 pt-6 pb-12">
        <button
          class="px-8 py-3 border border-slate-300 dark:border-slate-700 rounded-xl text-base font-extrabold hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors cursor-pointer"
          :disabled="store.loading || savingAssets"
          @click="discardDraft"
        >
          Hủy bỏ
        </button>
        <button
          class="px-10 py-3 bg-primary text-white rounded-xl text-base font-extrabold shadow-xl shadow-primary/30 hover:bg-primary/90 transition-all hover:-translate-y-0.5 disabled:opacity-60 disabled:cursor-not-allowed flex items-center gap-2 cursor-pointer"
          :disabled="store.loading || savingAssets"
          @click="publishProfile"
        >
          <span v-if="store.loading || savingAssets" class="material-symbols-outlined animate-spin text-[18px]">progress_activity</span>
          {{ store.loading ? 'Đang lưu...' : savingAssets ? 'Đang tải tệp...' : (store.company ? 'Lưu thay đổi' : 'Đăng hồ sơ') }}
        </button>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import CompanyAbout from '@/components/recruiter/company/CompanyAbout.vue'
import CompanyBasicInfo from '@/components/recruiter/company/CompanyBasicInfo.vue'
import CompanyContact from '@/components/recruiter/company/CompanyContact.vue'
import CompanyVerification from '@/components/recruiter/company/CompanyVerification.vue'
import { useToast } from '@/composables/useToast'
import { FILE_UPLOAD_TYPE } from '@/constants/fileUpload.constants'
import fileUploadService from '@/services/fileUpload.service'
import { useEmployerCompanyStore } from '@/stores/employercompany.store'
import {
  CompanySize,
  CompanyStatus,
  VerificationStatus,
  parseBenefits,
  parseSocialLinks,
  stringifyBenefits,
  stringifySocialLinks,
} from '@/types/company.types'
import type { LicenseFile } from '@/components/recruiter/company/CompanyVerification.vue'

const store = useEmployerCompanyStore()
const toast = useToast()
const savingAssets = ref(false)

const form = reactive({
  companyName: '',
  industry: '',
  companySize: '' as CompanySize | '',
  taxCode: '',
  foundedYear: '' as number | '',
  coverUrl: '',
  logoUrl: '',
  description: '',
  culture: '',
  benefits: [] as string[],
  businessLicenseUrl: '',
  licenseFile: null as LicenseFile | null,
  email: '',
  phone: '',
  website: '',
  provinceId: '' as number | '',
  address: '',
  linkedin: '',
  twitter: '',
  facebook: '',
})

const draftFiles = reactive({
  coverFile: null as File | null,
  logoFile: null as File | null,
  licenseFile: null as File | null,
})

const errors = reactive<Record<string, string>>({})

function clearErrors() {
  Object.keys(errors).forEach((key) => delete errors[key])
}

function clearDraftFiles() {
  draftFiles.coverFile = null
  draftFiles.logoFile = null
  draftFiles.licenseFile = null
}

function syncFromStore() {
  clearErrors()
  clearDraftFiles()

  const company = store.company
  if (!company) return

  form.companyName = company.name ?? ''
  form.companySize = company.companySize ?? ''
  form.taxCode = company.taxCode ?? ''
  form.foundedYear = company.foundedYear ?? ''
  form.coverUrl = company.coverUrl ?? ''
  form.logoUrl = company.logoUrl ?? ''
  form.description = company.description ?? ''
  form.culture = company.culture ?? ''
  form.benefits = parseBenefits(company.benefits)
  form.businessLicenseUrl = company.businessLicenseUrl ?? ''
  form.email = company.email ?? ''
  form.phone = company.phone ?? ''
  form.website = company.website ?? ''
  form.provinceId = company.provinceId ?? ''
  form.address = company.address ?? ''

  const social = parseSocialLinks(company.socialLinks)
  form.linkedin = social.linkedin ?? ''
  form.twitter = social.twitter ?? ''
  form.facebook = social.facebook ?? ''

  form.licenseFile = company.businessLicenseUrl
    ? {
        name: company.businessLicenseUrl.split('/').pop() ?? 'Giấy phép kinh doanh',
        size: '—',
        uploadedAt: company.updatedAt?.slice(0, 10) ?? '',
      }
    : null

  form.industry = company.industryId ? String(company.industryId) : ''
}

syncFromStore()
watch(() => store.company, syncFromStore)

const statusBarStyle = computed(() => {
  const verificationStatus = store.company?.verificationStatus
  const companyStatus = store.company?.status

  if (companyStatus === CompanyStatus.SUSPENDED) {
    return {
      bg: 'bg-red-50 dark:bg-red-950/30 border border-red-200 dark:border-red-900/50',
      iconBg: 'bg-red-100 dark:bg-red-900/50',
      iconColor: 'text-red-600 dark:text-red-400',
      icon: 'block',
      titleColor: 'text-red-900 dark:text-red-100',
      title: 'Tài khoản bị tạm khóa',
      descColor: 'text-red-700 dark:text-red-300',
      desc: store.company?.suspendedReason ?? '',
      badge: 'bg-red-200 dark:bg-red-900/80 text-red-900 dark:text-red-100',
      badgeText: 'Bị khóa',
    }
  }

  if (verificationStatus === VerificationStatus.REJECTED) {
    return {
      bg: 'bg-red-50 dark:bg-red-950/30 border border-red-200 dark:border-red-900/50',
      iconBg: 'bg-red-100 dark:bg-red-900/50',
      iconColor: 'text-red-600 dark:text-red-400',
      icon: 'cancel',
      titleColor: 'text-red-900 dark:text-red-100',
      title: 'Hồ sơ bị từ chối',
      descColor: 'text-red-700 dark:text-red-300',
      desc: store.company?.rejectionReason ?? 'Vui lòng cập nhật và gửi lại hồ sơ.',
      badge: 'bg-red-200 dark:bg-red-900/80 text-red-900 dark:text-red-100',
      badgeText: 'Từ chối',
    }
  }

  if (verificationStatus === VerificationStatus.VERIFIED) {
    return {
      bg: 'bg-emerald-50 dark:bg-emerald-950/30 border border-emerald-200 dark:border-emerald-900/50',
      iconBg: 'bg-emerald-100 dark:bg-emerald-900/50',
      iconColor: 'text-emerald-600 dark:text-emerald-400',
      icon: 'verified',
      titleColor: 'text-emerald-900 dark:text-emerald-100',
      title: 'Hồ sơ đã được xác minh',
      descColor: 'text-emerald-700 dark:text-emerald-300',
      desc: 'Công ty của bạn đã được xác minh và đang hoạt động.',
      badge: 'bg-emerald-200 dark:bg-emerald-900/80 text-emerald-900 dark:text-emerald-100',
      badgeText: 'Đã xác minh',
    }
  }

  return {
    bg: 'bg-amber-50 dark:bg-amber-950/30 border border-amber-200 dark:border-amber-900/50',
    iconBg: 'bg-amber-100 dark:bg-amber-900/50',
    iconColor: 'text-amber-600 dark:text-amber-400',
    icon: 'pending_actions',
    titleColor: 'text-amber-900 dark:text-amber-100',
    title: 'Đang chờ xác minh',
    descColor: 'text-amber-700 dark:text-amber-300',
    desc: 'Đội ngũ của chúng tôi đang xem xét hồ sơ. Quá trình này thường mất 24–48 giờ.',
    badge: 'bg-amber-200 dark:bg-amber-900/80 text-amber-900 dark:text-amber-100',
    badgeText: 'Đang xử lý',
  }
})

function validateForm() {
  clearErrors()
  let isValid = true

  const setError = (field: string, message: string) => {
    errors[field] = message
    if (isValid) {
      toast.error(message)
      isValid = false
    }
  }

  if (!form.companyName.trim()) setError('companyName', 'Vui lòng nhập tên công ty')
  if (!form.industry) setError('industry', 'Vui lòng chọn lĩnh vực hoạt động')
  if (!form.companySize) setError('companySize', 'Vui lòng chọn quy mô công ty')
  if (!form.taxCode.trim()) setError('taxCode', 'Vui lòng nhập mã số thuế')

  if (form.foundedYear) {
    const currentYear = new Date().getFullYear()
    if (form.foundedYear < 1900) {
      setError('foundedYear', 'Năm thành lập không hợp lệ (tối thiểu 1900)')
    } else if (form.foundedYear > currentYear) {
      setError('foundedYear', `Năm thành lập không được lớn hơn năm hiện tại (${currentYear})`)
    }
  }

  if (form.description.length < 100) setError('description', 'Mô tả công ty tối thiểu 100 ký tự')

  if (!form.email.trim()) {
    setError('email', 'Vui lòng nhập email liên hệ')
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    setError('email', 'Email không hợp lệ')
  }

  if (!form.phone.trim()) setError('phone', 'Vui lòng nhập số điện thoại')
  if (!form.address.trim()) setError('address', 'Vui lòng nhập địa chỉ công ty')

  return isValid
}

function discardDraft() {
  if (savingAssets.value || store.loading) return
  syncFromStore()
}

async function publishProfile() {
  if (!validateForm()) return

  savingAssets.value = true

  try {
    let coverUrl = store.company?.coverUrl ?? null
    let logoUrl = store.company?.logoUrl ?? null
    let businessLicenseUrl = store.company?.businessLicenseUrl ?? null

    if (draftFiles.coverFile) {
      const res = await fileUploadService.uploadFile(draftFiles.coverFile, FILE_UPLOAD_TYPE.COMPANY_COVER)
      coverUrl = res.fileUrl
    }

    if (draftFiles.logoFile) {
      const res = await fileUploadService.uploadFile(draftFiles.logoFile, FILE_UPLOAD_TYPE.COMPANY_LOGO)
      logoUrl = res.fileUrl
    }

    if (draftFiles.licenseFile) {
      const res = await fileUploadService.uploadFile(draftFiles.licenseFile, FILE_UPLOAD_TYPE.BUSINESS_LICENSE)
      businessLicenseUrl = res.fileUrl
    } else if (!form.licenseFile) {
      businessLicenseUrl = null
    }

    const payload = {
      name: form.companyName,
      companySize: form.companySize || null,
      taxCode: form.taxCode || null,
      foundedYear: form.foundedYear ? Number(form.foundedYear) : null,
      coverUrl,
      logoUrl,
      description: form.description || null,
      culture: form.culture || null,
      industryId: form.industry ? Number(form.industry) : null,
      email: form.email || null,
      phone: form.phone || null,
      website: form.website || null,
      provinceId: form.provinceId ? Number(form.provinceId) : null,
      address: form.address || null,
      businessLicenseUrl,
      benefits: form.benefits.length ? stringifyBenefits(form.benefits) : null,
      socialLinks: stringifySocialLinks({
        ...(form.linkedin ? { linkedin: form.linkedin } : {}),
        ...(form.twitter ? { twitter: form.twitter } : {}),
        ...(form.facebook ? { facebook: form.facebook } : {}),
      }) || null,
    }

    if (store.company) {
      await store.updateMyCompany(payload as any)
    } else {
      await store.createCompany({
        ...payload,
        slug: form.companyName.toLowerCase().replace(/\s+/g, '-').replace(/[^a-z0-9-]/g, ''),
        name: form.companyName,
        description: form.description,
        industryId: Number(form.industry),
        companySize: form.companySize,
      } as any)
    }

    toast.success('Lưu hồ sơ thành công!')
  } catch (err: any) {
    toast.error(err?.response?.data?.message || store.error || 'Lưu thất bại, vui lòng thử lại.')
  } finally {
    savingAssets.value = false
  }
}

onMounted(() => {
  store.fetchMyCompany()
})
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s, transform 0.3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(8px); }
</style>
