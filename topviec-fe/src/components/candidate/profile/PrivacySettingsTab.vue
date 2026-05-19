<template>
  <div class="space-y-6">

    <!-- Information Visibility -->
    <section class="bg-white dark:bg-surface-dark p-6 md:p-8 rounded-3xl border border-slate-200 dark:border-slate-800 shadow-sm">
      <h3 class="text-lg font-bold text-text-main dark:text-white flex items-center gap-2 mb-6">
        <span class="material-symbols-outlined text-primary">visibility</span>
        Information Visibility
      </h3>
      <div class="space-y-4">

        <div
          v-for="item in visibilityItems"
          :key="item.key"
          class="flex items-center justify-between py-4 border-b border-slate-100 dark:border-slate-800 last:border-0"
        >
          <div>
            <p class="text-sm font-semibold text-text-main dark:text-white">{{ item.label }}</p>
            <p class="text-xs text-text-muted mt-0.5">{{ item.desc }}</p>
          </div>
          <ToggleSwitch
            :model-value="item.enabled"
            @update:model-value="(value) => handleToggle(item.key, value)"
          />
        </div>

      </div>
    </section>

    <!-- Account Security -->
    <section class="bg-white dark:bg-surface-dark p-6 md:p-8 rounded-3xl border border-slate-200 dark:border-slate-800 shadow-sm">
      <h3 class="text-lg font-bold text-text-main dark:text-white flex items-center gap-2 mb-6">
        <span class="material-symbols-outlined text-primary">shield</span>
        Account Security
      </h3>
      <div class="space-y-3">

        <div
          v-for="action in securityActions"
          :key="action.label"
          class="flex items-center justify-between p-4 rounded-2xl border border-slate-200 dark:border-slate-700 hover:border-primary/40 hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-all group"
        >
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-xl flex items-center justify-center shrink-0"
              :class="action.danger ? 'bg-red-50 dark:bg-red-900/10 text-red-500' : 'bg-primary/10 text-primary'"
            >
              <span class="material-symbols-outlined text-[20px]">{{ action.icon }}</span>
            </div>
            <div>
              <p class="text-sm font-semibold" :class="action.danger ? 'text-red-500' : 'text-text-main dark:text-white'">
                {{ action.label }}
              </p>
              <p v-if="action.desc" class="text-xs text-text-muted mt-0.5">{{ action.desc }}</p>
            </div>
          </div>
          <button
            class="text-sm font-bold px-4 py-2 rounded-xl transition-colors cursor-pointer"
            :class="action.danger
              ? 'text-red-500 hover:bg-red-50 dark:hover:bg-red-900/10'
              : 'text-primary hover:bg-primary/10'"
          >
            {{ action.cta }}
          </button>
        </div>

      </div>
    </section>

  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import ToggleSwitch from '@/components/common/ToggleSwitch.vue'
import { useCandidateProfileStore } from '@/stores/candidateProfile.store'
import { useToast } from '@/composables/useToast'

const profileStore = useCandidateProfileStore()
const toast = useToast()
const isSaving = ref(false)
const savingKey = ref<string | null>(null)

type VisibilityKey = 'phone' | 'email' | 'birthday' | 'salary'

const visibilityItems = reactive([
  { key: 'phone',    label: 'Số điện thoại',    desc: 'Hiển thị số điện thoại trên hồ sơ công khai.',       enabled: true  },
  { key: 'email',    label: 'Địa chỉ email',    desc: 'Hiển thị email trên hồ sơ công khai.',               enabled: true  },
  { key: 'birthday', label: 'Ngày sinh',        desc: 'Hiển thị ngày sinh trên hồ sơ công khai.',           enabled: true  },
  { key: 'salary',   label: 'Mức lương kỳ vọng', desc: 'Hiển thị mức lương mong muốn với nhà tuyển dụng.',  enabled: true  },
])

const securityActions = [
  {
    icon: 'security',
    label: 'Xác thực 2 yếu tố',
    desc: 'Thêm lớp bảo mật bổ sung cho tài khoản của bạn.',
    cta: 'Bật',
    danger: false,
  },
  {
    icon: 'devices',
    label: 'Phiên đăng nhập',
    desc: 'Quản lý các thiết bị đang đăng nhập vào tài khoản.',
    cta: 'Xem',
    danger: false,
  },
  {
    icon: 'delete_forever',
    label: 'Xóa tài khoản',
    desc: 'Xóa vĩnh viễn tài khoản và toàn bộ dữ liệu của bạn.',
    cta: 'Xóa',
    danger: true,
  },
]

function syncVisibilityFromProfile() {
  const profile = profileStore.profile
  if (!profile) return

  for (const item of visibilityItems) {
    if (item.key === 'phone') item.enabled = !profile.hidePhone
    if (item.key === 'email') item.enabled = !profile.hideEmail
    if (item.key === 'birthday') item.enabled = !profile.hideDateOfBirth
    if (item.key === 'salary') item.enabled = !profile.hideExpectedSalary
  }
}

const payload = computed(() => ({
  hidePhone: !visibilityItems.find((item) => item.key === 'phone')!.enabled,
  hideEmail: !visibilityItems.find((item) => item.key === 'email')!.enabled,
  hideDateOfBirth: !visibilityItems.find((item) => item.key === 'birthday')!.enabled,
  hideExpectedSalary: !visibilityItems.find((item) => item.key === 'salary')!.enabled,
}))

async function handleToggle(key: VisibilityKey, value: boolean) {
  const item = visibilityItems.find((entry) => entry.key === key)
  if (!item || isSaving.value || savingKey.value) return

  const previousValue = item.enabled
  item.enabled = value

  const profile = profileStore.profile
  if (
    profile &&
    payload.value.hidePhone === profile.hidePhone &&
    payload.value.hideEmail === profile.hideEmail &&
    payload.value.hideDateOfBirth === profile.hideDateOfBirth &&
    payload.value.hideExpectedSalary === profile.hideExpectedSalary
  ) {
    return
  }

  isSaving.value = true
  savingKey.value = key
  try {
    await profileStore.updateVisibility(payload.value)
    toast.success('Cập nhật thành công', 'Cài đặt quyền riêng tư đã được cập nhật.')
  } catch (err: any) {
    item.enabled = previousValue
    const msg = err?.response?.data?.message ?? profileStore.error ?? 'Không thể cập nhật cài đặt quyền riêng tư.'
    toast.error('Lỗi', msg)
  } finally {
    isSaving.value = false
    savingKey.value = null
  }
}

watch(() => profileStore.profile, syncVisibilityFromProfile, { immediate: true })
</script>
