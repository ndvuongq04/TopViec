<template>
  <main class="flex flex-col flex-1 gap-6 min-w-0 max-w-[1440px] mx-auto w-full px-4 md:px-10 py-6">

    <!-- Loading toàn trang -->
    <div v-if="store.loading && !store.profile" class="flex items-center justify-center py-20">
      <span class="material-symbols-outlined animate-spin text-primary text-4xl">progress_activity</span>
    </div>

    <template v-else>
      <!-- Tiêu đề trang -->
      <div class="flex flex-col md:flex-row md:items-end justify-between gap-4">
        <div>
          <h1 class="text-3xl font-bold text-text-main dark:text-white tracking-tight">Hồ sơ của tôi</h1>
          <p class="text-text-muted mt-1 text-base">Quản lý thông tin cá nhân và sở thích nghề nghiệp.</p>
        </div>
        <div class="flex items-center gap-3 bg-white dark:bg-surface-dark px-4 py-2 rounded-2xl border border-slate-200 dark:border-slate-800 shadow-sm">
          <span class="text-base font-medium text-text-muted">Độ hoàn thiện:</span>
          <div class="w-32 h-2 bg-slate-100 dark:bg-slate-700 rounded-full overflow-hidden">
            <div class="h-full bg-green-500 rounded-full transition-all" :style="{ width: profileStrength + '%' }"></div>
          </div>
          <span class="text-base font-bold text-text-main dark:text-white">{{ profileStrength }}%</span>
        </div>
      </div>

      <!-- Error banner -->
      <div v-if="store.error" class="flex items-center gap-3 px-5 py-3 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-2xl text-red-600 dark:text-red-400 text-sm">
        <span class="material-symbols-outlined text-[18px]">error</span>
        {{ store.error }}
      </div>

      <!-- Tabs -->
      <div class="border-b border-slate-200 dark:border-slate-800">
        <nav class="-mb-px flex space-x-8">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            class="whitespace-nowrap border-b-[3px] py-4 px-1 text-sm font-medium transition-colors cursor-pointer"
            :class="activeTab === tab.key
              ? 'border-primary text-primary font-bold'
              : 'border-transparent text-text-muted hover:border-slate-300 hover:text-text-main dark:hover:text-white'"
            @click="activeTab = tab.key"
          >
            {{ tab.label }}
          </button>
        </nav>
      </div>

      <!-- Nội dung -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div class="lg:col-span-1">
          <ProfileCard
            :full-name="store.profile?.fullName"
            :avatar-url="store.profile?.avatarUrl ?? ''"
            @switch-tab="activeTab = $event"
          />
        </div>
        <div class="lg:col-span-2">
          <PersonalInfoTab v-if="activeTab === 'personal'" />
          <CvsTab v-else-if="activeTab === 'cvs'" />
          <JobAlertsTab v-else-if="activeTab === 'alerts'" />
          <FollowedCompaniesTab v-else-if="activeTab === 'followed'" />
          <PrivacySettingsTab v-else-if="activeTab === 'privacy'" />
        </div>
      </div>
    </template>

  </main>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import ProfileCard from '@/components/candidate/profile/ProfileCard.vue'
import PersonalInfoTab from '@/components/candidate/profile/PersonalInfoTab.vue'
import CvsTab from '@/components/candidate/profile/CvsTab.vue'
import JobAlertsTab from '@/components/candidate/profile/JobAlertsTab.vue'
import FollowedCompaniesTab from '@/components/candidate/profile/FollowedCompaniesTab.vue'
import PrivacySettingsTab from '@/components/candidate/profile/PrivacySettingsTab.vue'
import { useCandidateProfileStore } from '@/stores/candidateProfile.store'
import { useCvsStore } from '@/stores/cvs.store'
import { useCandidateCompanyFollowStore } from '@/stores/candidateCompanyFollow.store'

const store = useCandidateProfileStore()
const cvsStore = useCvsStore()
const followStore = useCandidateCompanyFollowStore()
const route = useRoute()

const activeTab = ref<'personal' | 'cvs' | 'alerts' | 'followed' | 'privacy'>('personal')

const tabs = [
  { key: 'personal', label: 'Thông tin cá nhân' },
  { key: 'cvs',      label: 'CV của tôi' },
  { key: 'alerts',   label: 'Thông báo việc làm' },
  { key: 'followed', label: 'Công ty theo dõi' },
  { key: 'privacy',  label: 'Cài đặt quyền riêng tư' },
] as const

/** Đồng bộ tab từ URL query param */
function syncTabFromQuery() {
  const tab = route.query.tab as string
  if (tab && ['personal', 'cvs', 'alerts', 'followed', 'privacy'].includes(tab)) {
    activeTab.value = tab as any
  }
}

/** Dùng trực tiếp từ BE — BE đã tính sẵn profileCompletionPct */
const profileStrength = computed(() => store.profile?.profileCompletionPct ?? 0)

onMounted(() => {
  store.fetchMyProfile()
  cvsStore.fetchMyCvs()
  syncTabFromQuery()
})

// Khi ở trang profile mà ấn menu ở sidebar -> cập nhật tab
watch(() => route.query.tab, () => {
  syncTabFromQuery()
})
</script>