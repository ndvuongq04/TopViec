<template>
  <GlobalModal
    :visible="visible"
    title="Thông tin cá nhân"
    subtitle="Thông tin tài khoản và vai trò của bạn trong công ty"
    icon="person_outline"
    max-width="lg"
    :show-default-footer="false"
    @close="$emit('close')"
  >
    <!-- Loading -->
    <div v-if="loading" class="flex items-center justify-center py-12">
      <span class="material-symbols-outlined animate-spin text-primary text-4xl">progress_activity</span>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="flex items-center gap-3 px-4 py-3 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-xl text-red-600 dark:text-red-400 text-sm">
      <span class="material-symbols-outlined text-[18px]">error</span>
      {{ error }}
    </div>

    <!-- Content -->
    <div v-else-if="profile" class="space-y-6">

      <!-- Company header -->
      <div class="flex items-center gap-4 p-4 bg-slate-50 dark:bg-slate-800/50 rounded-2xl">
        <div
          class="size-14 rounded-xl bg-slate-200 dark:bg-slate-700 shrink-0 bg-cover bg-center flex items-center justify-center"
          :style="profile.companyLogoUrl ? `background-image: url('${profile.companyLogoUrl}')` : ''"
        >
          <span v-if="!profile.companyLogoUrl" class="material-symbols-outlined text-slate-400 text-2xl">business</span>
        </div>
        <div>
          <p class="font-bold text-text-main dark:text-white text-base">{{ profile.companyName }}</p>
          <p class="text-sm text-text-muted">{{ profile.companySlug }}</p>
          <span class="inline-flex items-center gap-1 mt-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-primary/10 text-primary">
            {{ roleLabel }}
          </span>
        </div>
      </div>

      <!-- Account info -->
      <div>
        <h4 class="text-xs font-bold text-text-muted uppercase tracking-wider mb-3">Tài khoản</h4>
        <div class="divide-y divide-slate-100 dark:divide-slate-800">

          <div class="flex items-center gap-3 py-3">
            <span class="material-symbols-outlined text-[18px] text-text-muted shrink-0">email</span>
            <span class="text-xs text-text-muted w-40 shrink-0">Email</span>
            <span class="text-sm font-medium text-text-main dark:text-white">{{ profile.email }}</span>
          </div>

          <div class="flex items-center gap-3 py-3">
            <span class="material-symbols-outlined text-[18px] text-text-muted shrink-0">verified_user</span>
            <span class="text-xs text-text-muted w-40 shrink-0">Trạng thái tài khoản</span>
            <span class="text-xs font-semibold px-2.5 py-0.5 rounded-full" :class="accountStatusColor">
              {{ accountStatusLabel }}
            </span>
          </div>

          <div class="flex items-center gap-3 py-3">
            <span class="material-symbols-outlined text-[18px] text-text-muted shrink-0">mark_email_read</span>
            <span class="text-xs text-text-muted w-40 shrink-0">Xác thực email</span>
            <span class="text-sm font-medium text-text-main dark:text-white">
              {{ profile.emailVerifiedAt ? formatDate(profile.emailVerifiedAt) : 'Chưa xác thực' }}
            </span>
          </div>

          <div class="flex items-center gap-3 py-3">
            <span class="material-symbols-outlined text-[18px] text-text-muted shrink-0">login</span>
            <span class="text-xs text-text-muted w-40 shrink-0">Đăng nhập gần nhất</span>
            <span class="text-sm font-medium text-text-main dark:text-white">
              {{ profile.lastLoginAt ? formatDate(profile.lastLoginAt) : '—' }}
            </span>
          </div>

        </div>
      </div>

      <!-- Member info -->
      <div>
        <h4 class="text-xs font-bold text-text-muted uppercase tracking-wider mb-3">Thành viên công ty</h4>
        <div class="divide-y divide-slate-100 dark:divide-slate-800">

          <div class="flex items-center gap-3 py-3">
            <span class="material-symbols-outlined text-[18px] text-text-muted shrink-0">badge</span>
            <span class="text-xs text-text-muted w-40 shrink-0">Vai trò</span>
            <span class="text-sm font-medium text-text-main dark:text-white">{{ roleLabel }}</span>
          </div>

          <div class="flex items-center gap-3 py-3">
            <span class="material-symbols-outlined text-[18px] text-text-muted shrink-0">toggle_on</span>
            <span class="text-xs text-text-muted w-40 shrink-0">Trạng thái thành viên</span>
            <span class="text-sm font-medium text-text-main dark:text-white">{{ profile.memberStatus }}</span>
          </div>

          <div class="flex items-center gap-3 py-3">
            <span class="material-symbols-outlined text-[18px] text-text-muted shrink-0">calendar_today</span>
            <span class="text-xs text-text-muted w-40 shrink-0">Tham gia từ</span>
            <span class="text-sm font-medium text-text-main dark:text-white">{{ formatDate(profile.memberCreatedAt) }}</span>
          </div>

        </div>
      </div>

    </div>
  </GlobalModal>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import GlobalModal from '@/components/ui/GlobalModal.vue'
import { employerProfileService } from '@/services/employerProfile.service'
import type { ResEmployerProfileDTO } from '@/types/companyMember.types'
import { MEMBER_ROLE } from '@/constants/companyMember.constants'

const props = defineProps<{ visible: boolean }>()
defineEmits<{ close: [] }>()

const profile = ref<ResEmployerProfileDTO | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)

const ROLE_LABELS: Record<string, string> = {
  [MEMBER_ROLE.OWNER]:     'Chủ sở hữu',
  [MEMBER_ROLE.MANAGER]:   'Quản lý',
  [MEMBER_ROLE.RECRUITER]: 'Tuyển dụng',
  [MEMBER_ROLE.VIEWER]:    'Xem',
}

const roleLabel = computed(() =>
  profile.value ? (ROLE_LABELS[profile.value.roleName] ?? profile.value.roleName) : ''
)

const accountStatusLabel = computed(() => {
  const map: Record<string, string> = { ACTIVE: 'Hoạt động', INACTIVE: 'Không hoạt động', BANNED: 'Bị khóa' }
  return profile.value ? (map[profile.value.accountStatus] ?? profile.value.accountStatus) : ''
})

const accountStatusColor = computed(() => {
  const map: Record<string, string> = {
    ACTIVE:   'text-green-600 bg-green-50 dark:bg-green-900/20',
    INACTIVE: 'text-slate-500 bg-slate-100 dark:bg-slate-800',
    BANNED:   'text-red-600 bg-red-50 dark:bg-red-900/20',
  }
  return profile.value ? (map[profile.value.accountStatus] ?? 'text-slate-500 bg-slate-100') : ''
})

function formatDate(iso: string) {
  return new Date(iso).toLocaleString('vi-VN', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  })
}

async function fetchProfile() {
  loading.value = true
  error.value = null
  try {
    profile.value = await employerProfileService.getMyProfile()
  } catch {
    error.value = 'Không thể tải thông tin. Vui lòng thử lại.'
  } finally {
    loading.value = false
  }
}

watch(() => props.visible, (val) => {
  if (val && !profile.value) fetchProfile()
})
</script>
