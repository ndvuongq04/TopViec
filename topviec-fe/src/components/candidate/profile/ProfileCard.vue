<template>
  <div class="space-y-6">
    <div class="bg-white dark:bg-surface-dark p-6 rounded-3xl border border-slate-200 dark:border-slate-800 shadow-sm flex flex-col items-center text-center">
      <div class="relative mb-4">
        <div
          class="w-28 h-28 rounded-full bg-cover bg-center border-4 border-white dark:border-slate-700 shadow-md bg-slate-100 dark:bg-slate-800"
          :style="avatarPreview ? `background-image: url('${avatarPreview}')` : ''"
        >
          <div v-if="!avatarPreview" class="w-full h-full flex items-center justify-center text-slate-400">
            <span class="material-symbols-outlined text-5xl">person</span>
          </div>
        </div>
        <button
          class="absolute bottom-0 right-0 p-2 bg-primary text-white rounded-full shadow-lg hover:bg-primary-hover transition-colors cursor-pointer"
          title="Đổi ảnh đại diện"
          @click="triggerAvatarUpload"
        >
          <span class="material-symbols-outlined text-[18px]">photo_camera</span>
        </button>
        <input ref="avatarInput" type="file" accept="image/*" class="hidden" @change="onAvatarChange" />
      </div>

      <h2 class="text-xl font-bold text-text-main dark:text-white">
        {{ profile?.fullName || 'Chưa cập nhật' }}
      </h2>

      <span
        v-if="profile?.jobSeekingStatus"
        class="mt-2 inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold"
        :class="jobStatusStyle"
      >
        <span class="w-1.5 h-1.5 rounded-full bg-current"></span>
        {{ jobStatusLabel }}
      </span>

      <p v-if="profile?.phoneDisplay && !profile?.hidePhone" class="text-text-muted text-sm mt-2 flex items-center gap-1">
        <span class="material-symbols-outlined text-[14px]">phone</span>
        {{ profile.phoneDisplay }}
      </p>

      <div v-if="locationLabel" class="flex items-center gap-1 text-text-muted text-xs mt-1">
        <span class="material-symbols-outlined text-[14px]">location_on</span>
        {{ locationLabel }}
      </div>

      <div v-if="workTypeLabel" class="flex items-center gap-1 text-text-muted text-xs mt-1">
        <span class="material-symbols-outlined text-[14px]">work</span>
        {{ workTypeLabel }}
      </div>

      <div v-if="salaryLabel" class="flex items-center gap-1 text-text-muted text-xs mt-1">
        <span class="material-symbols-outlined text-[14px]">payments</span>
        <span>{{ salaryLabel }}</span>
        <span v-if="profile?.salaryNegotiable" class="text-primary">(thương lượng)</span>
      </div>

      <div class="flex items-center gap-3 mt-4">
        <a
          v-if="profile?.linkedinUrl"
          :href="profile.linkedinUrl"
          target="_blank"
          class="text-text-muted hover:text-blue-600 transition-colors"
          title="LinkedIn"
        >
          <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
            <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433a2.062 2.062 0 0 1-2.063-2.065 2.064 2.064 0 1 1 2.063 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z"/>
          </svg>
        </a>
        <a
          v-if="profile?.githubUrl"
          :href="profile.githubUrl"
          target="_blank"
          class="text-text-muted hover:text-gray-900 dark:hover:text-white transition-colors"
          title="GitHub"
        >
          <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
            <path d="M12 .297c-6.63 0-12 5.373-12 12 0 5.303 3.438 9.8 8.205 11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61C4.422 18.07 3.633 17.7 3.633 17.7c-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305.76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22 0 1.606-.015 2.896-.015 3.286 0 .315.21.69.825.57C20.565 22.092 24 17.592 24 12.297c0-6.627-5.373-12-12-12"/>
          </svg>
        </a>
        <a
          v-if="profile?.personalWebsite"
          :href="profile.personalWebsite"
          target="_blank"
          class="text-text-muted hover:text-primary transition-colors"
          title="Website cá nhân"
        >
          <span class="material-symbols-outlined text-[20px]">language</span>
        </a>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useToast } from '@/composables/useToast'
import { JobSeekingStatus, PreferredWorkType } from '@/constants/candidateProfile.constants'
import { locationService } from '@/services/location.service'
import { useCandidateProfileStore } from '@/stores/candidateProfile.store'
import type { ResLocationDTO } from '@/types/masterData.types'

const store = useCandidateProfileStore()
const toast = useToast()

defineEmits(['switch-tab'])

const profile = computed(() => store.profile)
const avatarInput = ref<HTMLInputElement | null>(null)
const locationMap = ref<Record<number, string>>({})

const avatarPreview = computed(() =>
  store.pendingAvatarPreviewUrl || profile.value?.avatarUrl || null,
)

const jobStatusLabel = computed(() => {
  switch (profile.value?.jobSeekingStatus) {
    case JobSeekingStatus.ACTIVE:
      return 'Đang tìm việc gấp'
    case JobSeekingStatus.PASSIVE:
      return 'Sẵn sàng nếu có cơ hội'
    case JobSeekingStatus.NOT_LOOKING:
      return 'Không tìm việc'
    default:
      return ''
  }
})

const jobStatusStyle = computed(() => {
  switch (profile.value?.jobSeekingStatus) {
    case JobSeekingStatus.ACTIVE:
      return 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400'
    case JobSeekingStatus.PASSIVE:
      return 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400'
    case JobSeekingStatus.NOT_LOOKING:
      return 'bg-slate-100 text-slate-500 dark:bg-slate-800 dark:text-slate-400'
    default:
      return ''
  }
})

const workTypeLabel = computed(() => {
  switch (profile.value?.preferredWorkType) {
    case PreferredWorkType.FULL_TIME:
      return 'Toàn thời gian'
    case PreferredWorkType.PART_TIME:
      return 'Bán thời gian'
    case PreferredWorkType.REMOTE:
      return 'Làm việc từ xa'
    case PreferredWorkType.HYBRID:
      return 'Kết hợp'
    default:
      return ''
  }
})

const locationLabel = computed(() =>
  profile.value?.preferredLocationId
    ? locationMap.value[profile.value.preferredLocationId] ?? ''
    : '',
)

const salaryLabel = computed(() => {
  const min = profile.value?.expectedSalaryMin
  const max = profile.value?.expectedSalaryMax

  if (min && max) return `$${min.toLocaleString()} – $${max.toLocaleString()}/tháng`
  if (min) return `Từ $${min.toLocaleString()}/tháng`
  if (max) return `Đến $${max.toLocaleString()}/tháng`
  return ''
})

onMounted(async () => {
  try {
    const res = await locationService.getLocations({ size: 100 })
    const map: Record<number, string> = {}
    res.result.forEach((location: ResLocationDTO) => {
      map[location.id] = location.name
    })
    locationMap.value = map
  } catch {
    // Optional sidebar metadata.
  }
})

function triggerAvatarUpload() {
  avatarInput.value?.click()
}

function onAvatarChange(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) return

  store.setPendingAvatar(file, URL.createObjectURL(file))
  input.value = ''
  toast.success('Đã chọn ảnh', 'Ảnh sẽ được lưu khi bạn bấm "Lưu thay đổi".')
}
</script>
