<template>
  <aside class="w-64 border-r border-slate-200 dark:border-slate-800 bg-white dark:bg-background-dark flex flex-col shrink-0">
    <!-- Logo -->
    <div class="p-6 flex items-center gap-3">
      <div class="bg-primary size-10 rounded-lg flex items-center justify-center text-white">
        <span class="material-symbols-outlined">rocket_launch</span>
      </div>
      <div>
        <h1 class="font-bold text-lg leading-tight">TopViec</h1>
        <p class="text-xs text-slate-500 dark:text-slate-400">Employer Portal</p>
      </div>
    </div>

    <!-- Nav -->
    <nav class="flex-1 px-4 space-y-1 overflow-y-auto custom-scrollbar">
      <router-link
        v-for="item in navItems"
        :key="item.to"
        :to="item.to"
        class="flex items-center gap-3 px-3 py-2.5 rounded-lg transition-colors"
        :class="isActive(item.to)
          ? 'bg-primary/10 text-primary font-semibold'
          : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800'"
      >
        <span class="material-symbols-outlined">{{ item.icon }}</span>
        {{ item.label }}
      </router-link>
    </nav>

    <!-- User info -->
    <div class="p-4 border-t border-slate-200 dark:border-slate-800">
      <div class="flex items-center gap-3 p-2 rounded-xl bg-slate-50 dark:bg-slate-800/50">
        <div
          class="size-10 rounded-full bg-cover bg-center bg-slate-200 shrink-0"
          :style="profile?.companyLogoUrl ? `background-image: url('${profile.companyLogoUrl}')` : ''"
        >
          <div v-if="!profile?.companyLogoUrl" class="w-full h-full flex items-center justify-center rounded-full bg-primary/10">
            <span class="text-sm font-bold text-primary uppercase">{{ avatarInitial }}</span>
          </div>
        </div>
        <div class="flex-1 min-w-0">
          <p class="text-sm font-bold truncate">{{ displayName }}</p>
          <p class="text-xs text-slate-500 truncate">{{ roleLabel }}</p>
        </div>
        <GlobalDropdown align="left" direction="up" :offset="12">
          <template #trigger="{ toggle, isOpen }">
            <button
              class="text-slate-400 hover:text-primary transition-colors cursor-pointer"
              :class="{ 'text-primary': isOpen }"
              @click.stop="toggle"
            >
              <span class="material-symbols-outlined text-xl">settings</span>
            </button>
          </template>

          <template #default="{ close }">
            <GlobalDropdownItem
              icon="person_outline"
              label="Thông tin cá nhân"
              @click="() => { close(); showProfileModal = true }"
            />

            <GlobalDropdownItem
              icon="lock_open"
              label="Đổi mật khẩu"
              @click="() => { close(); showPasswordModal = true }"
            />
            <div class="h-px bg-slate-100 my-1 mx-2"></div>
            <GlobalDropdownItem
              icon="logout"
              label="Đăng xuất"
              danger
              @click="() => { close(); authStore.logout() }"
            />
          </template>
        </GlobalDropdown>
      </div>
    </div>
  </aside>

  <EmployerProfileModal :visible="showProfileModal" @close="showProfileModal = false" />
  <ChangePasswordModal :visible="showPasswordModal" @close="showPasswordModal = false" />
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import EmployerProfileModal from '@/components/recruiter/profile/EmployerProfileModal.vue'
import ChangePasswordModal from '@/components/recruiter/profile/ChangePasswordModal.vue'
import GlobalDropdown from '@/components/ui/GlobalDropdown.vue'
import GlobalDropdownItem from '@/components/ui/GlobalDropdownItem.vue'
import { MEMBER_ROLE } from '@/constants/companyMember.constants'
import { employerProfileService } from '@/services/employerProfile.service'
import { useAuthStore } from '@/stores/auth.store'
import type { ResEmployerProfileDTO } from '@/types/companyMember.types'

const route = useRoute()
const authStore = useAuthStore()
const showProfileModal = ref(false)
const showPasswordModal = ref(false)

const profile = ref<ResEmployerProfileDTO | null>(null)

const ROLE_LABELS: Record<string, string> = {
  [MEMBER_ROLE.OWNER]: 'Chủ sở hữu',
  [MEMBER_ROLE.MANAGER]: 'Quản lý',
  [MEMBER_ROLE.RECRUITER]: 'Tuyển dụng',
  [MEMBER_ROLE.VIEWER]: 'Xem',
}

const displayName = computed(() => {
  if (!profile.value?.email) return 'Recruiter'
  return profile.value.email.split('@')[0]
})

const roleLabel = computed(() =>
  profile.value ? (ROLE_LABELS[profile.value.roleName] ?? profile.value.roleName) : 'HR Manager',
)

const avatarInitial = computed(() => displayName.value.charAt(0).toUpperCase())

onMounted(async () => {
  try {
    profile.value = await employerProfileService.getMyProfile()
  } catch {
    // Keep fallback values when profile API fails.
  }
})

// Routes nằm dưới /recruiter/jobs/* nhưng thuộc nhóm Phỏng vấn.
const interviewJobRouteNames = new Set(['recruiter-job-interview-setup'])

function isActive(to: string): boolean {
  if (to === '/recruiter') return route.path === '/recruiter'

  if (to === '/recruiter/permissions') {
    return route.path === '/recruiter/permissions'
  }

  if (to === '/recruiter/permissions/log') {
    return route.path.startsWith('/recruiter/permissions/log')
  }

  if (to === '/recruiter/interviews') {
    return route.path.startsWith('/recruiter/interviews') ||
      interviewJobRouteNames.has(String(route.name))
  }

  if (to === '/recruiter/jobs') {
    return route.path.startsWith('/recruiter/jobs') &&
      !interviewJobRouteNames.has(String(route.name))
  }

  return route.path.startsWith(to)
}

const navItems = [
  { to: '/recruiter', icon: 'dashboard', label: 'Dashboard' },
  { to: '/recruiter/jobs', icon: 'work', label: 'Tin tuyển dụng' },
  // { to: '/recruiter/candidates', icon: 'group', label: 'Ứng viên' },
  { to: '/recruiter/interviews', icon: 'event', label: 'Phỏng vấn' },
  { to: '/recruiter/talent-pool', icon: 'person_search', label: 'Ứng viên tiềm năng' },
  // { to: '/recruiter/offers', icon: 'handshake', label: 'Mời làm việc' },
  // { to: '/recruiter/search-cv', icon: 'person_search', label: 'Tìm CV' },
  // { to: '/recruiter/reports', icon: 'bar_chart', label: 'Báo cáo' },
  { to: '/recruiter/team',       icon: 'manage_accounts',     label: 'Quản lý nhóm' },
  { to: '/recruiter/assignment', icon: 'assignment_ind',      label: 'Quản lý phân công' },
  { to: '/recruiter/permissions', icon: 'admin_panel_settings', label: 'Phân quyền' },
  { to: '/recruiter/permissions/log', icon: 'history', label: 'Lịch sử phân quyền' },
  { to: '/recruiter/company-profile', icon: 'business', label: 'Thông tin công ty' },
  { to: '/recruiter/messages', icon: 'chat', label: 'Tin nhắn' },
  { to: '/recruiter/activity-log', icon: 'local_activity', label: 'Nhật kí hoạt động' },
  { to: '/recruiter/complaints', icon: 'flag', label: 'Báo cáo vi phạm' },
  { to: '/recruiter/services', icon: 'diamond', label: 'Dịch vụ' },
  { to: '/recruiter/billing', icon: 'receipt_long', label: 'Lịch sử đơn hàng' },
]
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #cbd5e1;
  border-radius: 20px;
}
.dark .custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #334155;
}

/* Hide scrollbar by default, show on hover */
.custom-scrollbar {
  scrollbar-width: thin;
  scrollbar-color: transparent transparent;
}

.custom-scrollbar:hover {
  scrollbar-color: #cbd5e1 transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: transparent;
}

.custom-scrollbar:hover::-webkit-scrollbar-thumb {
  background-color: #cbd5e1;
}
</style>
