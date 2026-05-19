<template>
  <div class="space-y-6 font-body">
    <!-- Breadcrumb -->
    <nav class="flex items-center gap-2 text-[0.875rem]">
      <router-link
        :to="{ name: 'admin-candidates' }"
        class="text-slate-500 transition-colors hover:text-[#963131]"
      >
        Quản lý Ứng Viên
      </router-link>
      <span class="material-symbols-outlined text-[0.75rem] text-slate-400">chevron_right</span>
      <span class="font-medium text-slate-900 dark:text-slate-100">Chi tiết ứng viên</span>
    </nav>

    <!-- Loading State -->
    <div v-if="store.loading" class="flex flex-col items-center justify-center py-20 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-[#963131]"></div>
      <p class="mt-4 text-slate-500 font-medium">Đang tải thông tin ứng viên...</p>
    </div>

    <!-- Main Content (Only when candidate exists) -->
    <template v-else-if="candidate">
      <!-- Header & Action Buttons -->
      <div class="bg-white dark:bg-slate-900 rounded-xl p-6 border border-slate-200 dark:border-slate-800">
        <div class="flex flex-col lg:flex-row items-start justify-between gap-6">
          <!-- Left: Avatar + Info -->
          <div class="flex gap-6">
            <div class="w-24 h-24 rounded-full border border-slate-200 dark:border-slate-800 bg-white overflow-hidden flex items-center justify-center shrink-0 relative">
              <img
                v-if="candidate.avatarUrl"
                :src="candidate.avatarUrl"
                :alt="candidate.fullName"
                class="w-full h-full object-cover"
              />
              <span v-else class="material-symbols-outlined text-4xl text-slate-300">person</span>
              <div v-if="candidate.status === UserStatus.ACTIVE" class="absolute bottom-0 right-0 w-6 h-6 bg-green-600 rounded-full border-2 border-white flex items-center justify-center" title="Verified User">
                <span class="material-symbols-outlined text-white text-[0.75rem] font-bold">check</span>
              </div>
            </div>
            <div class="space-y-1">
              <div class="flex items-center gap-2">
                <h2 class="text-[1.875rem] font-extrabold tracking-tight leading-tight">{{ candidate.fullName }}</h2>
                <span v-if="candidate.status === UserStatus.ACTIVE" class="bg-blue-100 text-blue-700 text-[0.75rem] font-bold px-2 py-0.5 rounded-full flex items-center gap-1 uppercase tracking-wider">
                  <span class="material-symbols-outlined text-[0.875rem]">verified</span>
                  {{ USER_STATUS_LABELS[UserStatus.ACTIVE] }}
                </span>
              </div>
              <div class="flex flex-col gap-1 pt-2">
                <div class="flex items-center gap-2 text-[1rem] text-slate-600 dark:text-slate-400">
                  <span class="material-symbols-outlined text-[1.125rem]">mail</span>
                  {{ candidate.email }}
                </div>
                <div class="flex items-center gap-2 text-[1rem] text-slate-600 dark:text-slate-400">
                  <span class="material-symbols-outlined text-[1.125rem]">call</span>
                  {{ candidate.phoneDisplay || '—' }}
                </div>
                <div class="flex items-center gap-2 text-[0.875rem] text-slate-500 dark:text-slate-500 font-medium">
                  <span class="material-symbols-outlined text-[1rem]">calendar_today</span>
                  Ngày tham gia: {{ formatDate(candidate.createdAt) }}
                </div>
              </div>
            </div>
          </div>

          <!-- Right: Action buttons -->
          <div class="flex flex-wrap gap-3 shrink-0">
            <button
              class="px-5 py-2.5 rounded-lg bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-200 text-[1rem] font-bold flex items-center gap-2 hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors cursor-pointer"
              @click="handleResetPassword"
            >
              <span class="material-symbols-outlined text-[1.125rem]">lock_reset</span>
              Reset mật khẩu
            </button>
            <button
              v-if="candidate.status === UserStatus.LOCKED_PERM"
              class="px-5 py-2.5 rounded-lg bg-emerald-100 dark:bg-emerald-900/30 text-emerald-700 dark:text-emerald-400 text-[1rem] font-bold flex items-center gap-2 hover:bg-emerald-200 transition-colors cursor-pointer"
              @click="handleLockAccount"
            >
              <span class="material-symbols-outlined text-[1.125rem]">lock_open</span>
              Mở khóa tài khoản
            </button>
            <button
              v-else
              class="px-5 py-2.5 rounded-lg bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400 text-[1rem] font-bold flex items-center gap-2 hover:bg-red-200 transition-colors cursor-pointer"
              @click="handleLockAccount"
            >
              <span class="material-symbols-outlined text-[1.125rem]">block</span>
              Khóa tài khoản
            </button>
          </div>
        </div>
      </div>

      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div v-for="stat in stats" :key="stat.label" class="bg-white dark:bg-slate-900 rounded-xl p-4 border border-slate-200 dark:border-slate-800 flex items-center gap-4">
          <div :class="['w-12 h-12 rounded-lg flex items-center justify-center shrink-0', stat.iconBg]">
            <span :class="['material-symbols-outlined text-[1.5rem]', stat.iconColor]">{{ stat.icon }}</span>
          </div>
          <div>
            <p class="text-[0.75rem] text-slate-500 font-bold uppercase tracking-wider">{{ stat.label }}</p>
            <p class="text-[1.875rem] font-extrabold text-slate-900 dark:text-slate-100 leading-none mt-1">{{ stat.value }}</p>
          </div>
        </div>
      </div>

      <!-- Tabs -->
      <div class="border-b border-slate-200 dark:border-slate-800">
        <nav class="flex gap-8">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            class="pb-4 text-[0.875rem] font-bold transition-colors relative cursor-pointer"
            :class="activeTab === tab.key ? 'text-[#963131]' : 'text-slate-500 hover:text-slate-700'"
            @click="activeTab = tab.key"
          >
            {{ tab.label }}
            <div v-if="activeTab === tab.key" class="absolute bottom-0 left-0 w-full h-0.5 bg-[#963131]"></div>
          </button>
        </nav>
      </div>

      <!-- Tab Content -->
      <div class="mt-4">
        <CandidateProfileTab
          v-if="activeTab === 'profile'"
          :candidate="candidate"
        />
        <CandidateCvsTab
          v-else-if="activeTab === 'cvs'"
          :cvs="store.cvs"
        />
        <CandidateApplicationsTab
          v-else-if="activeTab === 'applications'"
          :applications="store.applications"
        />
        <CandidateFollowedTab
          v-else-if="activeTab === 'followed'"
          :companies="store.followedCompanies"
        />
        <CandidateSavedJobsTab
          v-else-if="activeTab === 'saved'"
          :jobs="store.savedJobs"
        />
      </div>
    </template>

    <!-- Error State -->
    <div v-else-if="store.error" class="bg-red-50 border border-red-200 text-red-700 px-6 py-12 rounded-xl text-center">
      <span class="material-symbols-outlined text-5xl mb-2">error</span>
      <p class="font-bold text-lg">Đã có lỗi xảy ra</p>
      <p>{{ store.error }}</p>
      <button @click="loadCandidateDetail" class="mt-4 px-4 py-2 bg-red-600 text-white rounded-lg font-bold">Thử lại</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import CandidateProfileTab from '@/components/admin/candidates/candidate-detail/CandidateProfileTab.vue'
import CandidateCvsTab from '@/components/admin/candidates/candidate-detail/CandidateCvsTab.vue'
import CandidateApplicationsTab from '@/components/admin/candidates/candidate-detail/CandidateApplicationsTab.vue'
import CandidateFollowedTab from '@/components/admin/candidates/candidate-detail/CandidateFollowedTab.vue'
import CandidateSavedJobsTab from '@/components/admin/candidates/candidate-detail/CandidateSavedJobsTab.vue'
import { useToast } from '@/composables/useToast'
import { useAdminCandidateStore } from '@/stores/adminCandidate.store'
import { UserStatus, USER_STATUS_LABELS } from '@/constants/user.constants'

const toast = useToast()
const route = useRoute()
const store = useAdminCandidateStore()
const candidateId = Number(route.params.id)

// ─── Tab State ──────────────────────────────────────────────────────────────────
const activeTab = ref('profile')
const tabs = [
  { key: 'profile', label: 'Hồ sơ' },
  { key: 'cvs', label: 'Danh sách CV' },
  { key: 'applications', label: 'Lịch sử ứng tuyển' },
  { key: 'followed', label: 'Công ty đã follow' },
  { key: 'saved', label: 'Tin đã lưu' },
]

// ─── Actions ────────────────────────────────────────────────────────────────────
async function loadCandidateDetail() {
  if (candidateId) {
    await store.fetchCandidateDetail(candidateId)
    store.fetchCandidateStatistics(candidateId)
  }
}

onMounted(() => {
  loadCandidateDetail()
})

const candidate = computed(() => store.selectedCandidate)

// Watch activeTab to fetch data for current tab
watch(activeTab, (newTab) => {
  if (!candidateId) return
  
  if (newTab === 'cvs') {
    store.fetchCandidateCvs(candidateId)
  } else if (newTab === 'applications') {
    store.fetchCandidateApplications(candidateId)
  } else if (newTab === 'followed') {
    store.fetchCandidateFollowedCompanies(candidateId)
  } else if (newTab === 'saved') {
    store.fetchCandidateSavedJobs(candidateId)
  }
}, { immediate: true })

const formatDate = (iso: string) => {
  if (!iso) return '—'
  const d = new Date(iso)
  return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
}


const stats = computed(() => [
  {
    label: 'Tổng CV',
    value: store.statistics?.totalCvs ?? '—',
    icon: 'description',
    iconBg: 'bg-blue-50 dark:bg-blue-900/20',
    iconColor: 'text-blue-600',
  },
  {
    label: 'Lần ứng tuyển',
    value: store.statistics?.totalApplications ?? '—',
    icon: 'send',
    iconBg: 'bg-green-50 dark:bg-green-900/20',
    iconColor: 'text-green-600',
  },
  {
    label: 'Công ty theo dõi',
    value: store.statistics?.totalFollowedCompanies ?? '—',
    icon: 'corporate_fare',
    iconBg: 'bg-purple-50 dark:bg-purple-900/20',
    iconColor: 'text-purple-600',
  },
  {
    label: 'Việc làm đã lưu',
    value: store.statistics?.totalSavedJobs ?? '—',
    icon: 'bookmark',
    iconBg: 'bg-[#963131]/10',
    iconColor: 'text-[#963131]',
  },
])

function handleResetPassword() {
  toast.info('Tính năng', 'Reset mật khẩu đang được phát triển.')
}

async function handleLockAccount() {
  if (!candidate.value) return
  const isLocked = candidate.value.status === UserStatus.LOCKED_PERM
  const actionText = isLocked ? 'mở khóa' : 'khóa'
  
  try {
    await store.toggleCandidateStatus(candidateId)
    toast.success('Thành công', `Đã ${actionText} tài khoản ứng viên.`)
  } catch (error) {
    toast.error('Lỗi', `Không thể ${actionText} tài khoản.`)
  }
}
</script>

<style scoped>
/* Không cần style scoped vì đã dùng Tailwind đồng bộ với EmployerDetailPage */
</style>