<template>
  <div class="space-y-6">
    <section class="bg-white dark:bg-surface-dark p-6 md:p-8 rounded-3xl border border-slate-200 dark:border-slate-800 shadow-sm">
      <h3 class="text-lg font-bold text-text-main dark:text-white flex items-center gap-2 mb-6">
        <span class="material-symbols-outlined text-primary">person</span>
        Thông tin cơ bản
      </h3>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="space-y-2">
          <label class="text-sm font-bold text-text-muted uppercase tracking-wider">Họ và tên</label>
          <input
            v-model="form.fullName"
            type="text"
            class="w-full rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-100 dark:bg-slate-800 px-4 py-2.5 text-text-main dark:text-white placeholder:text-text-muted dark:placeholder:text-gray-500 focus:border-primary focus:ring-1 focus:ring-primary text-base font-medium outline-none transition-all"
            placeholder="Nhập họ và tên"
          />
        </div>

        <div class="space-y-2">
          <label class="text-sm font-bold text-text-muted uppercase tracking-wider">Số điện thoại</label>
          <input
            :value="form.phoneDisplay"
            type="tel"
            maxlength="12"
            class="w-full rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-100 dark:bg-slate-800 px-4 py-2.5 text-text-main dark:text-white placeholder:text-text-muted dark:placeholder:text-gray-500 focus:border-primary focus:ring-1 focus:ring-primary text-base font-medium outline-none transition-all"
            placeholder="0909 123 456"
            @input="onPhoneInput"
          />
        </div>

        <div class="space-y-2">
          <label class="text-sm font-bold text-text-muted uppercase tracking-wider">Ngày sinh</label>
          <input
            v-model="form.dateOfBirth"
            type="date"
            class="w-full rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-100 dark:bg-slate-800 px-4 py-2.5 text-text-main dark:text-white placeholder:text-text-muted dark:placeholder:text-gray-500 focus:border-primary focus:ring-1 focus:ring-primary text-base font-medium outline-none transition-all"
          />
        </div>

        <div class="space-y-2">
          <label class="text-sm font-bold text-text-muted uppercase tracking-wider">Giới tính</label>
          <select
            v-model="form.gender"
            class="w-full rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-100 dark:bg-slate-800 px-4 py-2.5 text-text-main dark:text-white placeholder:text-text-muted dark:placeholder:text-gray-500 focus:border-primary focus:ring-1 focus:ring-primary text-base font-medium outline-none transition-all"
          >
            <option value="">-- Chọn giới tính --</option>
            <option value="male">Nam</option>
            <option value="female">Nữ</option>
            <option value="other">Khác</option>
          </select>
        </div>

        <div class="space-y-2">
          <label class="text-sm font-bold text-text-muted uppercase tracking-wider">LinkedIn</label>
          <input
            v-model="form.linkedinUrl"
            type="url"
            class="w-full rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-100 dark:bg-slate-800 px-4 py-2.5 text-text-main dark:text-white placeholder:text-text-muted dark:placeholder:text-gray-500 focus:border-primary focus:ring-1 focus:ring-primary text-base font-medium outline-none transition-all"
            placeholder="https://linkedin.com/in/..."
          />
        </div>

        <div class="space-y-2">
          <label class="text-sm font-bold text-text-muted uppercase tracking-wider">GitHub</label>
          <input
            v-model="form.githubUrl"
            type="url"
            class="w-full rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-100 dark:bg-slate-800 px-4 py-2.5 text-text-main dark:text-white placeholder:text-text-muted dark:placeholder:text-gray-500 focus:border-primary focus:ring-1 focus:ring-primary text-base font-medium outline-none transition-all"
            placeholder="https://github.com/..."
          />
        </div>

        <div class="space-y-2 md:col-span-2">
          <label class="text-sm font-bold text-text-muted uppercase tracking-wider">Website cá nhân</label>
          <input
            v-model="form.personalWebsite"
            type="url"
            class="w-full rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-100 dark:bg-slate-800 px-4 py-2.5 text-text-main dark:text-white placeholder:text-text-muted dark:placeholder:text-gray-500 focus:border-primary focus:ring-1 focus:ring-primary text-base font-medium outline-none transition-all"
            placeholder="https://yourwebsite.com"
          />
        </div>
      </div>
    </section>

    <section class="bg-white dark:bg-surface-dark p-6 md:p-8 rounded-3xl border border-slate-200 dark:border-slate-800 shadow-sm">
      <h3 class="text-lg font-bold text-text-main dark:text-white flex items-center gap-2 mb-6">
        <span class="material-symbols-outlined text-primary">format_quote</span>
        Giới thiệu bản thân
      </h3>
      <div class="space-y-2">
        <textarea
          v-model="form.bio"
          class="w-full rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-100 dark:bg-slate-800 px-4 py-2.5 text-text-main dark:text-white placeholder:text-text-muted dark:placeholder:text-gray-500 focus:border-primary focus:ring-1 focus:ring-primary text-sm font-medium outline-none transition-all resize-none leading-relaxed"
          placeholder="Viết giới thiệu ngắn về bản thân..."
          rows="4"
          maxlength="1000"
        ></textarea>
        <div class="flex justify-end">
          <span class="text-xs text-text-muted">{{ form.bio.length }} / 1000 ký tự</span>
        </div>
      </div>
    </section>

    <section class="bg-white dark:bg-surface-dark p-6 md:p-8 rounded-3xl border border-slate-200 dark:border-slate-800 shadow-sm">
      <h3 class="text-lg font-bold text-text-main dark:text-white flex items-center gap-2 mb-6">
        <span class="material-symbols-outlined text-primary">work_history</span>
        Kỳ vọng công việc
      </h3>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="space-y-2 md:col-span-2">
          <label class="text-sm font-bold text-text-muted uppercase tracking-wider">Vị trí làm việc mong muốn</label>
          <input
            v-model="form.preferredJobTitle"
            type="text"
            maxlength="255"
            class="w-full rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-100 dark:bg-slate-800 px-4 py-2.5 text-text-main dark:text-white placeholder:text-text-muted dark:placeholder:text-gray-500 focus:border-primary focus:ring-1 focus:ring-primary text-base font-medium outline-none transition-all"
            placeholder="VD: Intern Java, Frontend Developer, Product Manager..."
          />
        </div>

        <div class="space-y-2 md:col-span-2">
          <label class="text-sm font-bold text-text-muted uppercase tracking-wider">
            Mức lương mong muốn (VNĐ/tháng)
          </label>
          <div class="flex items-center gap-3">
            <div class="relative w-full">
              <input
                :value="salaryMinDisplay"
                type="text"
                inputmode="numeric"
                class="w-full rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-100 dark:bg-slate-800 px-4 py-2.5 pr-14 text-text-main dark:text-white placeholder:text-text-muted dark:placeholder:text-gray-500 focus:border-primary focus:ring-1 focus:ring-primary text-base font-medium outline-none transition-all"
                placeholder="Tối thiểu"
                @input="onSalaryMinInput"
              />
              <span class="absolute right-3 top-1/2 -translate-y-1/2 text-sm text-text-muted font-medium pointer-events-none">VNĐ</span>
            </div>
            <span class="text-text-muted font-medium shrink-0">—</span>
            <div class="relative w-full">
              <input
                :value="salaryMaxDisplay"
                type="text"
                inputmode="numeric"
                class="w-full rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-100 dark:bg-slate-800 px-4 py-2.5 pr-14 text-text-main dark:text-white placeholder:text-text-muted dark:placeholder:text-gray-500 focus:border-primary focus:ring-1 focus:ring-primary text-base font-medium outline-none transition-all"
                placeholder="Tối đa"
                @input="onSalaryMaxInput"
              />
              <span class="absolute right-3 top-1/2 -translate-y-1/2 text-sm text-text-muted font-medium pointer-events-none">VNĐ</span>
            </div>
          </div>
          <label class="flex items-center gap-2 mt-2 cursor-pointer">
            <input v-model="form.salaryNegotiable" type="checkbox" class="rounded text-primary focus:ring-primary" />
            <span class="text-base text-text-muted">Mức lương có thể thương lượng</span>
          </label>
        </div>

        <div class="space-y-2">
          <label class="text-sm font-bold text-text-muted uppercase tracking-wider">Hình thức làm việc</label>
          <select
            v-model="form.preferredWorkType"
            class="w-full rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-100 dark:bg-slate-800 px-4 py-2.5 text-text-main dark:text-white placeholder:text-text-muted dark:placeholder:text-gray-500 focus:border-primary focus:ring-1 focus:ring-primary text-base font-medium outline-none transition-all"
          >
            <option value="">-- Chọn hình thức --</option>
            <option :value="PreferredWorkType.FULL_TIME">Toàn thời gian</option>
            <option :value="PreferredWorkType.PART_TIME">Bán thời gian</option>
            <option :value="PreferredWorkType.REMOTE">Làm việc từ xa</option>
            <option :value="PreferredWorkType.HYBRID">Kết hợp</option>
          </select>
        </div>

        <div class="space-y-2">
          <label class="text-sm font-bold text-text-muted uppercase tracking-wider">Địa điểm mong muốn</label>
          <SearchableSelect
            v-model="form.preferredLocationId"
            :options="locationOptions"
            placeholder="-- Chọn địa điểm --"
          />
        </div>

        <div class="space-y-3 md:col-span-2">
          <label class="text-sm font-bold text-text-muted uppercase tracking-wider">Trạng thái tìm việc</label>
          <div class="flex flex-wrap gap-3">
            <label v-for="status in jobStatusOptions" :key="status.value" class="cursor-pointer">
              <input v-model="form.jobSeekingStatus" type="radio" :value="status.value" class="sr-only peer" />
              <div class="px-4 py-2 rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800 text-text-muted dark:text-gray-400 text-base font-medium hover:bg-slate-50 dark:hover:bg-slate-700 peer-checked:bg-primary/10 peer-checked:text-primary peer-checked:border-primary transition-all">
                {{ status.label }}
              </div>
            </label>
          </div>
        </div>
      </div>
    </section>

    <Transition name="fade">
      <div
        v-if="toast.show"
        class="fixed bottom-6 right-6 z-50 flex items-center gap-3 px-5 py-3 rounded-2xl shadow-xl text-sm font-medium"
        :class="toast.type === 'success' ? 'bg-green-500 text-white' : 'bg-red-500 text-white'"
      >
        <span class="material-symbols-outlined text-[18px]">
          {{ toast.type === 'success' ? 'check_circle' : 'error' }}
        </span>
        {{ toast.message }}
      </div>
    </Transition>

    <div class="flex justify-end gap-3 pt-2">
      <button
        type="button"
        class="px-6 py-2.5 rounded-2xl border border-slate-200 dark:border-slate-700 text-text-muted dark:text-gray-400 font-bold text-base hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors cursor-pointer"
        :disabled="store.loading"
        @click="resetForm"
      >
        Hủy
      </button>
      <button
        type="button"
        class="px-6 py-2.5 rounded-2xl bg-primary hover:bg-primary-hover text-white font-bold text-base shadow-lg shadow-blue-500/30 transition-colors disabled:opacity-60 disabled:cursor-not-allowed cursor-pointer flex items-center gap-2"
        :disabled="store.loading"
        @click="saveChanges"
      >
        <span v-if="store.loading" class="material-symbols-outlined animate-spin text-[18px]">progress_activity</span>
        {{ store.loading ? 'Đang lưu...' : 'Lưu thay đổi' }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import SearchableSelect from '@/components/ui/SearchableSelect.vue'
import { JobSeekingStatus, PreferredWorkType } from '@/constants/candidateProfile.constants'
import { FILE_UPLOAD_TYPE } from '@/constants/fileUpload.constants'
import fileUploadService from '@/services/fileUpload.service'
import { locationService } from '@/services/location.service'
import { useCandidateProfileStore } from '@/stores/candidateProfile.store'
import type { ResLocationDTO } from '@/types/masterData.types'

const store = useCandidateProfileStore()
const locations = ref<ResLocationDTO[]>([])
const toast = ref({ show: false, type: 'success' as 'success' | 'error', message: '' })

const locationOptions = computed(() => locations.value.map((location) => ({ id: location.id, name: location.name })))

const form = reactive({
  fullName: '',
  phoneDisplay: '',
  dateOfBirth: '',
  gender: '' as 'male' | 'female' | 'other' | '',
  linkedinUrl: '',
  githubUrl: '',
  personalWebsite: '',
  bio: '',
  expectedSalaryMin: null as number | null,
  expectedSalaryMax: null as number | null,
  salaryNegotiable: false,
  preferredJobTitle: '',
  preferredWorkType: '' as PreferredWorkType | '',
  preferredLocationId: null as number | null,
  jobSeekingStatus: JobSeekingStatus.ACTIVE,
})

onMounted(async () => {
  try {
    const res = await locationService.getLocations({ size: 100 })
    locations.value = res.result
  } catch {
    // Optional supporting data.
  }
})

function formatPhone(raw: string): string {
  const digits = raw.replace(/[^\d+]/g, '')

  if (digits.startsWith('+84')) {
    const num = digits.slice(3).replace(/\D/g, '')
    if (num.length <= 3) return `+84 ${num}`
    if (num.length <= 6) return `+84 ${num.slice(0, 3)} ${num.slice(3)}`
    return `+84 ${num.slice(0, 3)} ${num.slice(3, 6)} ${num.slice(6, 9)}`
  }

  const num = digits.replace(/\D/g, '')
  if (num.length <= 4) return num
  if (num.length <= 7) return `${num.slice(0, 4)} ${num.slice(4)}`
  return `${num.slice(0, 4)} ${num.slice(4, 7)} ${num.slice(7, 10)}`
}

function onPhoneInput(event: Event) {
  const input = event.target as HTMLInputElement
  const formatted = formatPhone(input.value)
  form.phoneDisplay = formatted
  input.value = formatted
}

function formatVND(value: number | null): string {
  if (value === null || value === undefined) return ''
  return value.toLocaleString('vi-VN')
}

function parseVND(display: string): number | null {
  const raw = display.replace(/\D/g, '')
  if (!raw) return null
  return parseInt(raw, 10)
}

const salaryMinDisplay = computed(() => formatVND(form.expectedSalaryMin))
const salaryMaxDisplay = computed(() => formatVND(form.expectedSalaryMax))

function onSalaryMinInput(event: Event) {
  const input = event.target as HTMLInputElement
  const num = parseVND(input.value)
  form.expectedSalaryMin = num
  input.value = formatVND(num)
}

function onSalaryMaxInput(event: Event) {
  const input = event.target as HTMLInputElement
  const num = parseVND(input.value)
  form.expectedSalaryMax = num
  input.value = formatVND(num)
}

function showToast(type: 'success' | 'error', message: string) {
  toast.value = { show: true, type, message }
  setTimeout(() => {
    toast.value.show = false
  }, 3000)
}

function syncFromStore() {
  const profile = store.profile
  if (!profile) return

  form.fullName = profile.fullName ?? ''
  form.phoneDisplay = profile.phoneDisplay ? formatPhone(profile.phoneDisplay) : ''
  form.dateOfBirth = profile.dateOfBirth ?? ''
  form.gender = (profile.gender ?? '') as typeof form.gender
  form.linkedinUrl = profile.linkedinUrl ?? ''
  form.githubUrl = profile.githubUrl ?? ''
  form.personalWebsite = profile.personalWebsite ?? ''
  form.bio = profile.bio ?? ''
  form.expectedSalaryMin = profile.expectedSalaryMin
  form.expectedSalaryMax = profile.expectedSalaryMax
  form.salaryNegotiable = profile.salaryNegotiable ?? false
  form.preferredJobTitle = profile.preferredJobTitle ?? ''
  form.preferredWorkType = (profile.preferredWorkType ?? '') as typeof form.preferredWorkType
  form.preferredLocationId = profile.preferredLocationId
  form.jobSeekingStatus = profile.jobSeekingStatus ?? JobSeekingStatus.ACTIVE
}

syncFromStore()
watch(() => store.profile, syncFromStore)

const jobStatusOptions = [
  { value: JobSeekingStatus.ACTIVE, label: 'Đang tìm việc gấp' },
  { value: JobSeekingStatus.PASSIVE, label: 'Sẵn sàng nếu có cơ hội tốt' },
  { value: JobSeekingStatus.NOT_LOOKING, label: 'Không tìm việc' },
]

function resetForm() {
  store.clearPendingAvatar()
  syncFromStore()
}

async function saveChanges() {
  const rawPhone = form.phoneDisplay.replace(/\s/g, '') || null
  let avatarUrl = store.profile?.avatarUrl ?? null

  try {
    if (store.pendingAvatarFile) {
      const res = await fileUploadService.uploadFile(store.pendingAvatarFile, FILE_UPLOAD_TYPE.AVATAR)
      avatarUrl = res.fileUrl
    }

    const payload = {
      fullName: form.fullName,
      avatarUrl,
      phoneDisplay: rawPhone,
      dateOfBirth: form.dateOfBirth || null,
      gender: (form.gender || null) as 'male' | 'female' | 'other' | null,
      linkedinUrl: form.linkedinUrl || null,
      githubUrl: form.githubUrl || null,
      personalWebsite: form.personalWebsite || null,
      bio: form.bio || null,
      expectedSalaryMin: form.expectedSalaryMin,
      expectedSalaryMax: form.expectedSalaryMax,
      salaryNegotiable: form.salaryNegotiable,
      preferredJobTitle: form.preferredJobTitle || null,
      preferredWorkType: (form.preferredWorkType || null) as PreferredWorkType | null,
      preferredLocationId: form.preferredLocationId,
      jobSeekingStatus: form.jobSeekingStatus,
    }

    if (store.profile) {
      await store.updateProfile(payload)
    } else {
      await store.createProfile(payload)
    }

    showToast('success', 'Lưu thông tin thành công!')
  } catch (err: any) {
    showToast('error', err?.response?.data?.message || store.error || 'Lưu thất bại, vui lòng thử lại.')
  }
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(8px);
}
</style>
