<template>
  <div class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm overflow-hidden">

    <!-- Toolbar -->
    <div class="tt-toolbar">
      <!-- Search -->
      <div class="tt-search-wrap">
        <span class="material-symbols-outlined tt-search-icon">search</span>
        <input
          v-model="searchInput"
          class="tt-search-input"
          placeholder="Tìm kiếm theo tên hoặc email..."
          type="text"
          @keyup.enter="$emit('search', searchInput)"
        />
        <button
          v-if="searchInput"
          class="tt-search-clear"
          title="Xóa tìm kiếm"
          @click="searchInput = ''; $emit('search', '')"
        >
          <span class="material-symbols-outlined">close</span>
        </button>
      </div>

      <div class="tt-filters">
        <!-- Role dropdown -->
        <div ref="roleRef" class="tt-dd-wrap">
          <button class="tt-dd-btn" :class="{ 'tt-dd-btn--active': filterRole }" type="button" @click="showRole = !showRole">
            <span class="material-symbols-outlined tt-dd-icon">{{ activeRoleOpt.icon }}</span>
            <span :class="{ 'tt-dd-label--active': filterRole }">{{ activeRoleOpt.label }}</span>
            <span class="material-symbols-outlined tt-dd-chevron" :class="{ open: showRole }">expand_more</span>
          </button>
          <Transition name="tt-fade">
            <div v-if="showRole" class="tt-dropdown">
              <button
                v-for="opt in roleOptions"
                :key="opt.value"
                class="tt-opt"
                :class="{ 'tt-opt--on': filterRole === opt.value }"
                type="button"
                @click="selectRole(opt.value)"
              >
                <span class="material-symbols-outlined tt-opt-icon" :class="opt.colorClass">{{ opt.icon }}</span>
                <span class="tt-opt-label">{{ opt.label }}</span>
                <span v-if="filterRole === opt.value" class="material-symbols-outlined tt-opt-check">check</span>
              </button>
            </div>
          </Transition>
        </div>

        <!-- Status dropdown -->
        <div ref="statusRef" class="tt-dd-wrap">
          <button class="tt-dd-btn" :class="{ 'tt-dd-btn--active': filterStatus }" type="button" @click="showStatus = !showStatus">
            <span class="material-symbols-outlined tt-dd-icon">{{ activeStatusOpt.icon }}</span>
            <span :class="{ 'tt-dd-label--active': filterStatus }">{{ activeStatusOpt.label }}</span>
            <span class="material-symbols-outlined tt-dd-chevron" :class="{ open: showStatus }">expand_more</span>
          </button>
          <Transition name="tt-fade">
            <div v-if="showStatus" class="tt-dropdown">
              <button
                v-for="opt in statusOptions"
                :key="opt.value"
                class="tt-opt"
                :class="{ 'tt-opt--on': filterStatus === opt.value }"
                type="button"
                @click="selectStatus(opt.value)"
              >
                <span class="material-symbols-outlined tt-opt-icon" :class="opt.colorClass">{{ opt.icon }}</span>
                <span class="tt-opt-label">{{ opt.label }}</span>
                <span v-if="filterStatus === opt.value" class="material-symbols-outlined tt-opt-check">check</span>
              </button>
            </div>
          </Transition>
        </div>

        <!-- Clear filters -->
        <button
          v-if="filterRole || filterStatus || searchInput"
          class="tt-clear-btn"
          title="Xóa bộ lọc"
          @click="clearFilters"
        >
          <span class="material-symbols-outlined">filter_alt_off</span>
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="overflow-x-auto">
      <table class="w-full text-left">
        <thead>
          <tr class="bg-slate-50 dark:bg-slate-800/50 border-b border-slate-200 dark:border-slate-800">
            <th class="px-6 py-4 text-xs font-extrabold uppercase tracking-wider text-slate-500">Thành viên</th>
            <th class="px-6 py-4 text-xs font-extrabold uppercase tracking-wider text-slate-500">Vai trò</th>
            <th class="px-6 py-4 text-xs font-extrabold uppercase tracking-wider text-slate-500">Trạng thái</th>
            <th class="px-6 py-4 text-xs font-extrabold uppercase tracking-wider text-slate-500">Ngày tham gia</th>
            <th class="px-6 py-4 text-xs font-extrabold uppercase tracking-wider text-slate-500 text-center">Thao tác</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-200 dark:divide-slate-800">
          <tr
            v-for="member in members"
            :key="member.id"
            class="hover:bg-slate-50/80 dark:hover:bg-slate-800/40 transition-colors"
          >
            <!-- Thành viên -->
            <td class="px-6 py-4">
              <div class="flex items-center gap-3">
                <div
                  class="size-10 rounded-full bg-primary/10 text-primary flex items-center justify-center font-bold text-sm shrink-0 border border-slate-200 dark:border-slate-700"
                  :style="member.avatarUrl ? `background-image: url('${member.avatarUrl}'); background-size: cover;` : ''"
                >
                  <span v-if="!member.avatarUrl">{{ initials(member.name) }}</span>
                </div>
                <div>
                  <p class="text-sm font-bold">{{ member.name }}</p>
                  <p class="text-xs text-slate-500">{{ member.email }}</p>
                </div>
              </div>
            </td>

            <!-- Vai trò -->
            <td class="px-6 py-4">
              <span class="px-2.5 py-1 text-xs font-bold rounded-full" :class="roleStyle(member.role)">
                {{ roleLabel(member.role) }}
              </span>
            </td>

            <!-- Trạng thái -->
            <td class="px-6 py-4">
              <div class="flex items-center gap-1.5" :class="member.status === 'active' ? 'text-emerald-500' : 'text-amber-500'">
                <span
                  class="w-2 h-2 rounded-full"
                  :class="[
                    member.status === 'active' ? 'bg-emerald-500' : 'bg-amber-500',
                    member.status === 'pending' ? 'animate-pulse' : ''
                  ]"
                ></span>
                <span class="text-xs font-medium">
                  {{ member.status === 'active' ? 'Đang hoạt động' : member.status === 'pending' ? 'Chờ xác nhận' : 'Đã khóa' }}
                </span>
              </div>
            </td>

            <!-- Ngày tham gia -->
            <td class="px-6 py-4 text-sm text-slate-600 dark:text-slate-400">{{ member.joinedAt }}</td>

            <!-- Thao tác -->
            <td class="px-6 py-4">
              <div class="flex justify-center items-center gap-2">
                <button
                  class="p-1.5 text-slate-400 hover:text-primary hover:bg-primary/10 rounded-lg transition-all cursor-pointer"
                  title="Chỉnh sửa"
                  @click="$emit('edit', member)"
                >
                  <span class="material-symbols-outlined text-lg">edit</span>
                </button>
                <button
                  class="p-1.5 text-slate-400 hover:text-rose-500 hover:bg-rose-50 dark:hover:bg-rose-950 rounded-lg transition-all cursor-pointer"
                  title="Xóa"
                  @click="$emit('delete', member)"
                >
                  <span class="material-symbols-outlined text-lg">delete</span>
                </button>
              </div>
            </td>
          </tr>

          <!-- Empty state -->
          <tr v-if="members.length === 0">
            <td colspan="5" class="px-6 py-12 text-center text-slate-400 text-sm">
              <span class="material-symbols-outlined text-4xl block mb-2">search_off</span>
              Không tìm thấy thành viên nào
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Pagination -->
    <div class="px-6 py-4 border-t border-slate-200 dark:border-slate-800 flex items-center justify-between">
      <p class="text-sm text-slate-500 font-medium">
        Hiển thị {{ rangeStart }} - {{ rangeEnd }} của {{ total }} thành viên
      </p>
      <div class="flex items-center gap-1">
        <button
          class="p-1 rounded border border-slate-200 dark:border-slate-700 text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800 disabled:opacity-40 disabled:cursor-not-allowed transition-colors cursor-pointer"
          :disabled="currentPage <= 1"
          @click="$emit('page-change', currentPage - 1)"
        >
          <span class="material-symbols-outlined text-sm">chevron_left</span>
        </button>

        <template v-for="page in visiblePages" :key="page">
          <span v-if="page === '...'" class="px-2 text-slate-300 text-sm">...</span>
          <button
            v-else
            class="w-8 h-8 rounded text-sm font-extrabold border transition-colors"
            :class="page === currentPage
              ? 'bg-primary text-white border-primary'
              : 'border-slate-200 dark:border-slate-700 text-slate-600 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800'"
            @click="$emit('page-change', page as number)"
          >
            {{ page }}
          </button>
        </template>

        <button
          class="p-1 rounded border border-slate-200 dark:border-slate-700 text-slate-600 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800 disabled:opacity-40 disabled:cursor-not-allowed transition-colors cursor-pointer"
          :disabled="currentPage >= totalPages"
          @click="$emit('page-change', currentPage + 1)"
        >
          <span class="material-symbols-outlined text-sm">chevron_right</span>
        </button>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { MEMBER_ROLE, MEMBER_STATUS } from '@/constants/companyMember.constants'

export interface TeamMember {
  id: number
  name: string
  email: string
  role: 'owner' | 'manager' | 'recruiter' | 'viewer'
  status: 'active' | 'pending' | 'deactivated'
  joinedAt: string
  actions?: Record<string, boolean>
  avatarUrl?: string
}

const props = defineProps<{
  members: TeamMember[]
  total: number
  currentPage: number
  pageSize: number
}>()

const emit = defineEmits<{
  edit: [member: TeamMember]
  delete: [member: TeamMember]
  search: [query: string]
  filter: [filters: { role: string, status: string }]
  'page-change': [page: number]
}>()

const searchInput = ref('')
const filterRole   = ref('')
const filterStatus = ref('')

// ─── Dropdown state ───────────────────────────────────────────────────────────
const showRole   = ref(false)
const showStatus = ref(false)
const roleRef    = ref<HTMLElement | null>(null)
const statusRef  = ref<HTMLElement | null>(null)

const roleOptions = [
  { value: '',                    label: 'Tất cả vai trò',   icon: 'groups',              colorClass: 'ico-slate'     },
  { value: MEMBER_ROLE.OWNER,     label: 'Chủ sở hữu',       icon: 'admin_panel_settings', colorClass: 'ico-red'       },
  { value: MEMBER_ROLE.MANAGER,   label: 'Quản lý',           icon: 'manage_accounts',      colorClass: 'ico-blue'      },
  { value: MEMBER_ROLE.RECRUITER, label: 'Nhà tuyển dụng',   icon: 'person_search',        colorClass: 'ico-green'     },
  { value: MEMBER_ROLE.VIEWER,    label: 'Người xem',         icon: 'visibility',           colorClass: 'ico-slate'     },
]

const statusOptions = [
  { value: '',                        label: 'Tất cả trạng thái', icon: 'radio_button_unchecked', colorClass: 'ico-slate' },
  { value: MEMBER_STATUS.ACTIVE,      label: 'Đang hoạt động',    icon: 'check_circle',           colorClass: 'ico-green' },
  { value: MEMBER_STATUS.PENDING,     label: 'Chờ xác nhận',      icon: 'schedule',               colorClass: 'ico-amber' },
  { value: MEMBER_STATUS.DEACTIVATED, label: 'Đã khóa',           icon: 'block',                  colorClass: 'ico-red'   },
]

const activeRoleOpt   = computed(() => roleOptions.find((o) => o.value === filterRole.value)   ?? roleOptions[0])
const activeStatusOpt = computed(() => statusOptions.find((o) => o.value === filterStatus.value) ?? statusOptions[0])

function selectRole(value: string) {
  filterRole.value = value
  showRole.value = false
  emit('filter', { role: filterRole.value, status: filterStatus.value })
}

function selectStatus(value: string) {
  filterStatus.value = value
  showStatus.value = false
  emit('filter', { role: filterRole.value, status: filterStatus.value })
}

function clearFilters() {
  searchInput.value = ''
  filterRole.value   = ''
  filterStatus.value = ''
  emit('search', '')
  emit('filter', { role: '', status: '' })
}

function onDocClick(e: MouseEvent) {
  if (roleRef.value   && !roleRef.value.contains(e.target as Node))   showRole.value   = false
  if (statusRef.value && !statusRef.value.contains(e.target as Node)) showStatus.value = false
}

onMounted(() => document.addEventListener('mousedown', onDocClick))
onUnmounted(() => document.removeEventListener('mousedown', onDocClick))

// ─── Role helpers ─────────────────────────────────────────────────────────────
function roleLabel(role: TeamMember['role']) {
  return { 
    owner: 'Chủ sở hữu', 
    manager: 'Quản lý', 
    recruiter: 'Nhà tuyển dụng', 
    viewer: 'Người xem' 
  }[role]
}

function roleStyle(role: TeamMember['role']) {
  return {
    owner:     'bg-primary/10 text-primary',
    manager:   'bg-blue-100 dark:bg-blue-900/30 text-blue-600',
    recruiter: 'bg-emerald-100 dark:bg-emerald-900/30 text-emerald-600',
    viewer:    'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300',
  }[role]
}

function initials(name: string) {
  return name.split(' ').slice(-2).map(w => w[0]).join('').toUpperCase()
}

// ─── Pagination ──────────────────────────────────────────────────────────────
const totalPages = computed(() => Math.ceil(props.total / props.pageSize))
const rangeStart = computed(() => (props.currentPage - 1) * props.pageSize + 1)
const rangeEnd   = computed(() => Math.min(props.currentPage * props.pageSize, props.total))

const visiblePages = computed(() => {
  const pages: (number | string)[] = []
  const total = totalPages.value
  const cur   = props.currentPage

  if (total <= 5) {
    for (let i = 1; i <= total; i++) pages.push(i)
    return pages
  }

  pages.push(1)
  if (cur > 3) pages.push('...')
  for (let i = Math.max(2, cur - 1); i <= Math.min(total - 1, cur + 1); i++) pages.push(i)
  if (cur < total - 2) pages.push('...')
  pages.push(total)

  return pages
})
</script>

<style scoped>
/* ── Toolbar ── */
.tt-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-bottom: 1px solid #e2e8f0;
  flex-wrap: wrap;
  font-family: 'Manrope', sans-serif;
}

/* Search */
.tt-search-wrap {
  position: relative;
  flex: 1;
  min-width: 200px;
  max-width: 400px;
}
.tt-search-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 18px;
  color: #94a3b8;
  pointer-events: none;
}
.tt-search-input {
  width: 100%;
  background: #f1f4f9;
  border: none;
  border-radius: 999px;
  padding: 9px 40px 9px 42px;
  font-size: 0.875rem;
  font-family: 'Manrope', sans-serif;
  color: #0f172a;
  outline: none;
  box-sizing: border-box;
  transition: box-shadow 0.15s;
}
.tt-search-input::placeholder { color: #94a3b8; }
.tt-search-input:focus { box-shadow: 0 0 0 2px rgba(75, 154, 246, 0.25); }
.tt-search-clear {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  padding: 2px;
  border: none;
  background: none;
  color: #94a3b8;
  cursor: pointer;
  border-radius: 50%;
  transition: color 0.15s, background 0.15s;
}
.tt-search-clear:hover { color: #475569; background: #e2e8f0; }
.tt-search-clear .material-symbols-outlined { font-size: 16px; }

/* Filters row */
.tt-filters {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  margin-left: auto;
}

/* Shared dropdown wrapper */
.tt-dd-wrap { position: relative; }

.tt-dd-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  background: #fff;
  box-shadow: 0 1px 2px rgba(0,0,0,0.04);
  color: #475569;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  font-size: 0.875rem;
  font-weight: 500;
  white-space: nowrap;
  transition: border-color 0.15s, box-shadow 0.15s;
}
.tt-dd-btn:hover { border-color: #cbd5e1; box-shadow: 0 2px 6px rgba(0,0,0,0.06); }
.tt-dd-btn--active { border-color: #93c5fd; background: #eff6ff; color: #1d4ed8; }
.tt-dd-icon { font-size: 16px; color: #94a3b8; }
.tt-dd-btn--active .tt-dd-icon { color: #2563eb; }
.tt-dd-label--active { color: #1d4ed8; font-weight: 600; }
.tt-dd-chevron { font-size: 17px; color: #94a3b8; transition: transform 0.15s; margin-left: 2px; }
.tt-dd-chevron.open { transform: rotate(180deg); }

.tt-dropdown {
  position: absolute;
  top: calc(100% + 6px);
  left: 0;
  z-index: 20;
  min-width: 190px;
  padding: 5px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 8px 24px rgba(0,0,0,0.1), 0 2px 6px rgba(0,0,0,0.05);
}

.tt-opt {
  display: flex;
  align-items: center;
  gap: 9px;
  width: 100%;
  padding: 7px 10px;
  border: 1px solid transparent;
  border-radius: 8px;
  background: transparent;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  font-size: 0.875rem;
  font-weight: 500;
  color: #334155;
  text-align: left;
  transition: background 0.12s;
}
.tt-opt:hover { background: #f8fafc; }
.tt-opt--on { border-color: #dbeafe; background: #eff6ff; color: #1d4ed8; font-weight: 600; }
.tt-opt-icon { font-size: 16px; flex-shrink: 0; }
.tt-opt--on .tt-opt-icon { color: #2563eb !important; }
.tt-opt-label { flex: 1; }
.tt-opt-check { font-size: 16px; color: #2563eb; flex-shrink: 0; }

/* Icon colors */
.ico-slate { color: #94a3b8; }
.ico-blue  { color: #3b82f6; }
.ico-green { color: #10b981; }
.ico-amber { color: #f59e0b; }
.ico-red   { color: #ef4444; }

/* Clear button */
.tt-clear-btn {
  display: flex;
  align-items: center;
  padding: 7px;
  border: 1px solid #fecaca;
  border-radius: 9px;
  background: #fff;
  color: #ef4444;
  cursor: pointer;
  transition: background 0.15s;
}
.tt-clear-btn:hover { background: #fef2f2; }
.tt-clear-btn .material-symbols-outlined { font-size: 16px; }

/* Transitions */
.tt-fade-enter-active, .tt-fade-leave-active { transition: opacity 0.13s, transform 0.13s; }
.tt-fade-enter-from, .tt-fade-leave-to { opacity: 0; transform: translateY(-4px); }

@media (max-width: 640px) {
  .tt-search-wrap { max-width: 100%; }
  .tt-filters { margin-left: 0; width: 100%; }
  .tt-dd-wrap, .tt-dd-btn { width: 100%; }
  .tt-dropdown { width: 100%; }
}
</style>