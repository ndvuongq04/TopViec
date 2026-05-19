<template>
  <aside class="w-72 bg-[#963131] text-white flex flex-col fixed h-full shadow-xl z-50">

    <!-- Logo -->
    <div class="p-6 flex items-center gap-3">
      <div class="bg-white rounded-lg p-1">
        <span class="material-symbols-outlined text-[#963131] text-3xl">work</span>
      </div>
      <div>
        <h1 class="text-xl font-bold tracking-tight">TopViec Admin</h1>
        <p class="text-xs text-white/70">Recruitment System</p>
      </div>
    </div>

    <!-- Nav -->
    <nav class="flex-1 px-4 py-6 space-y-1 overflow-y-auto">
      <template v-for="item in navItems" :key="item.label">

        <!-- Dropdown item -->
        <div v-if="item.children">
          <button
            class="flex w-full items-center gap-3 px-4 py-3 rounded-lg text-white/80 hover:text-white hover:bg-white/5 transition-colors"
            :class="{ '!bg-white/10 !text-white font-medium': isDropdownActive(item) }"
            @click="toggleDropdown(item.label)"
          >
            <span class="material-symbols-outlined">{{ item.icon }}</span>
            <span class="flex-1 text-left">{{ item.label }}</span>
            <span
              class="material-symbols-outlined text-xl transition-transform duration-200"
              :class="{ 'rotate-180': openDropdowns.has(item.label) }"
            >expand_more</span>
          </button>

          <div v-show="openDropdowns.has(item.label)" class="mt-1 ml-3 space-y-3 pb-1">
            <div v-for="group in item.children" :key="group.label">
              <p class="px-4 pt-2 pb-1 text-[11px] uppercase tracking-widest text-white/40 font-semibold">
                {{ group.label }}
              </p>
              <router-link
                v-for="sub in group.items"
                :key="sub.to"
                :to="sub.to"
                class="flex items-center gap-3 px-4 py-2 rounded-lg text-white/70 hover:text-white hover:bg-white/5 transition-colors text-sm"
                active-class="!bg-white/10 !text-white font-medium"
              >
                <span class="material-symbols-outlined text-[18px]">{{ sub.icon }}</span>
                <span>{{ sub.label }}</span>
              </router-link>
            </div>
          </div>
        </div>

        <!-- Regular item -->
        <router-link
          v-else
          :to="item.to!"
          class="flex items-center gap-3 px-4 py-3 rounded-lg text-white/80 hover:text-white hover:bg-white/5 transition-colors"
          :active-class="item.to === '/admin' ? '' : '!bg-white/10 !text-white font-medium'"
          exact-active-class="!bg-white/10 !text-white font-medium"
        >
          <span class="material-symbols-outlined">{{ item.icon }}</span>
          <span>{{ item.label }}</span>
        </router-link>

      </template>
    </nav>

    <!-- User info + settings dropdown -->
    <div class="p-4 border-t border-white/10">
      <div class="flex items-center gap-3 p-2 rounded-xl bg-white/5">
        <!-- Avatar -->
        <div class="size-9 rounded-full bg-white/20 flex items-center justify-center shrink-0 text-sm font-bold text-white uppercase">
          {{ avatarInitial }}
        </div>

        <!-- Name + role -->
        <div class="flex-1 min-w-0">
          <p class="text-sm font-bold text-white truncate">{{ displayName }}</p>
          <p class="text-xs text-white/50 truncate">{{ roleLabel }}</p>
        </div>

        <!-- Settings trigger -->
        <GlobalDropdown align="left" direction="up" :offset="12">
          <template #trigger="{ toggle, isOpen }">
            <button
              class="text-white/50 hover:text-white transition-colors cursor-pointer"
              :class="{ 'text-white': isOpen }"
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
              @click="() => { close(); handleLogout() }"
            />
          </template>
        </GlobalDropdown>
      </div>
    </div>

  </aside>

  <AdminProfileModal :visible="showProfileModal" @close="showProfileModal = false" />
  <ChangePasswordModal :visible="showPasswordModal" @close="showPasswordModal = false" />
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'
import { useAdminPermission } from '@/composables/useAdminPermission'
import GlobalDropdown from '@/components/ui/GlobalDropdown.vue'
import GlobalDropdownItem from '@/components/ui/GlobalDropdownItem.vue'
import ChangePasswordModal from '@/components/admin/profile/ChangePasswordModal.vue'
import AdminProfileModal from '@/components/admin/profile/AdminProfileModal.vue'
import { adminUserService } from '@/services/adminUser.service'
import type { ResAdminUser } from '@/types/adminUser.types'
import { ADMIN_ROLE_LABELS } from '@/types/adminUser.types'

const authStore = useAuthStore()
const route = useRoute()
const showPasswordModal = ref(false)
const showProfileModal = ref(false)
const { canAccessRoute, init: initPermission } = useAdminPermission()

const profile = ref<ResAdminUser | null>(null)

const displayName = computed(() => profile.value?.fullName || 'Admin')
const roleLabel = computed(() =>
  profile.value ? (ADMIN_ROLE_LABELS[profile.value.adminRole] ?? 'Quản trị viên') : 'Quản trị viên'
)
const avatarInitial = computed(() => displayName.value.charAt(0).toUpperCase())

onMounted(async () => {
  // Khởi tạo permission cache (gọi cùng API, composable sẽ dedup)
  await initPermission()
  try {
    profile.value = await adminUserService.getMyProfile()
  } catch {
    // Giữ fallback nếu API lỗi
  }
})

type SubItem = { to: string; icon: string; label: string; routeName?: string }
type Group   = { label: string; items: SubItem[] }
type NavItem =
  | { to: string; icon: string; label: string; routeName?: string; children?: undefined }
  | { to?: undefined; icon: string; label: string; children: Group[] }

const allNavItems: NavItem[] = [
  { to: '/admin',            icon: 'dashboard',            label: 'Dashboard',                  routeName: 'admin-home' },
  { to: '/admin/admins',     icon: 'admin_panel_settings', label: 'Quản lý Admin',               routeName: 'admin-manage-admins' },
  { to: '/admin/employers',  icon: 'corporate_fare',       label: 'Quản lý Nhà Tuyển Dụng',     routeName: 'admin-employers' },
  { to: '/admin/candidates', icon: 'group',                label: 'Quản lý Ứng Viên',           routeName: 'admin-candidates' },
  { to: '/admin/cv-templates', icon: 'description',        label: 'Quản lý CV',                 routeName: 'admin-cv-templates' },
  { to: '/admin/moderation', icon: 'fact_check',           label: 'Kiểm duyệt Nội dung',        routeName: 'admin-moderation' },
  { to: '/admin/reports',    icon: 'report',               label: 'Khiếu nại',                  routeName: 'admin-complaints' },
  {
    icon: 'payments',
    label: 'Gói dịch vụ & Thanh toán',
    children: [
      {
        label: 'Định nghĩa dịch vụ',
        items: [
          { to: '/admin/service-packages',     icon: 'inventory_2',  label: 'Gói dịch vụ',   routeName: 'admin-service-packages' },
          { to: '/admin/individual-services',  icon: 'add_box',      label: 'Dịch vụ lẻ',    routeName: 'admin-individual-services' },
          { to: '/admin/services/create',      icon: 'add_circle',   label: 'Tạo dịch vụ',   routeName: 'admin-service-create' },
        ],
      },
      {
        label: 'Vận hành',
        items: [
          { to: '/admin/orders',           icon: 'receipt_long',   label: 'Đơn hàng',      routeName: 'admin-orders' },
          { to: '/admin/employer-monitor', icon: 'monitor_heart',  label: 'Giám sát NTT',  routeName: 'admin-employer-monitor' },
        ],
      },
    ],
  },
  { to: '/admin/statistics', icon: 'bar_chart', label: 'Thống kê', routeName: 'admin-statistics' },
  {
    icon: 'settings',
    label: 'Cài đặt',
    children: [
      {
        label: 'Hệ thống',
        items: [
          { to: '/admin/settings/permissions', icon: 'admin_panel_settings', label: 'Cài đặt quyền',    routeName: 'admin-settings-permissions' },
          { to: '/admin/audit-logs',           icon: 'history',              label: 'Nhật kí hệ thống', routeName: 'admin-audit-logs' },
        ],
      },
    ],
  },
]

// Lọc menu theo quyền
const navItems = computed<NavItem[]>(() => {
  return allNavItems
    .map(item => {
      if (item.children) {
        // Lọc items trong từng group
        const filteredChildren = item.children
          .map(group => ({
            ...group,
            items: group.items.filter(sub =>
              !sub.routeName || canAccessRoute(sub.routeName)
            ),
          }))
          .filter(group => group.items.length > 0)

        if (filteredChildren.length === 0) return null
        return { ...item, children: filteredChildren }
      }
      // Item đơn
      if (item.routeName && !canAccessRoute(item.routeName)) return null
      return item
    })
    .filter((item): item is NavItem => item !== null)
})

// Collect all sub-routes belonging to dropdown items
const dropdownRoutes = computed(() => {
  const map = new Map<string, string[]>()
  for (const item of navItems.value) {
    if (item.children) {
      map.set(item.label, item.children.flatMap(g => g.items.map(i => i.to)))
    }
  }
  return map
})

const openDropdowns = ref<Set<string>>(new Set())

function toggleDropdown(label: string) {
  if (openDropdowns.value.has(label)) {
    openDropdowns.value.delete(label)
    // trigger reactivity
    openDropdowns.value = new Set(openDropdowns.value)
  } else {
    openDropdowns.value = new Set([...openDropdowns.value, label])
  }
}

function isDropdownActive(item: NavItem): boolean {
  if (!item.children) return false
  const routes = dropdownRoutes.value.get(item.label) ?? []
  return routes.some(r => route.path.startsWith(r))
}

// Auto-open dropdown when navigating to a child route
watch(
  () => route.path,
  (path) => {
    for (const [label, routes] of dropdownRoutes.value) {
      if (routes.some(r => path.startsWith(r))) {
        openDropdowns.value = new Set([...openDropdowns.value, label])
      }
    }
  },
  { immediate: true },
)

async function handleLogout() {
  await authStore.logout()
}
</script>

