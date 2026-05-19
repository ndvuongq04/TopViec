<template>
  <div class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 shadow-sm overflow-hidden">
    <div class="tt-toolbar">
      <div class="tt-search-wrap">
        <span class="material-symbols-outlined tt-search-icon">search</span>
        <input
          v-model="searchInput"
          class="tt-search-input"
          placeholder="Tìm theo mã báo cáo hoặc tên tin..."
          type="text"
          @keydown.enter="emitSearch"
        />
        <button v-if="searchInput" class="tt-search-clear" @click="searchInput = ''; emitSearch()">
          <span class="material-symbols-outlined">close</span>
        </button>
      </div>

      <div class="tt-filters">
        <div ref="groupRef" class="tt-dd-wrap">
          <button class="tt-dd-btn" :class="{ 'tt-dd-btn--active': filterGroup }" type="button" @click="showGroup = !showGroup">
            <span class="material-symbols-outlined tt-dd-icon">{{ activeGroupOpt.icon }}</span>
            <span :class="{ 'tt-dd-label--active': filterGroup }">{{ activeGroupOpt.label }}</span>
            <span class="material-symbols-outlined tt-dd-chevron" :class="{ open: showGroup }">expand_more</span>
          </button>
          <Transition name="tt-fade">
            <div v-if="showGroup" class="tt-dropdown">
              <button
                v-for="opt in groupOptions"
                :key="opt.value"
                class="tt-opt"
                :class="{ 'tt-opt--on': filterGroup === opt.value }"
                type="button"
                @click="selectGroup(opt.value)"
              >
                <span class="material-symbols-outlined tt-opt-icon" :class="opt.colorClass">{{ opt.icon }}</span>
                <span class="tt-opt-label">{{ opt.label }}</span>
                <span v-if="filterGroup === opt.value" class="material-symbols-outlined tt-opt-check">check</span>
              </button>
            </div>
          </Transition>
        </div>

        <div ref="typeRef" class="tt-dd-wrap">
          <button class="tt-dd-btn" :class="{ 'tt-dd-btn--active': filterType }" type="button" @click="showType = !showType">
            <span class="material-symbols-outlined tt-dd-icon">{{ activeTypeOpt.icon }}</span>
            <span :class="{ 'tt-dd-label--active': filterType }">{{ activeTypeOpt.label }}</span>
            <span class="material-symbols-outlined tt-dd-chevron" :class="{ open: showType }">expand_more</span>
          </button>
          <Transition name="tt-fade">
            <div v-if="showType" class="tt-dropdown">
              <button
                v-for="opt in typeOptions"
                :key="opt.value"
                class="tt-opt"
                :class="{ 'tt-opt--on': filterType === opt.value }"
                type="button"
                @click="selectType(opt.value)"
              >
                <span class="material-symbols-outlined tt-opt-icon" :class="opt.colorClass">{{ opt.icon }}</span>
                <span class="tt-opt-label">{{ opt.label }}</span>
                <span v-if="filterType === opt.value" class="material-symbols-outlined tt-opt-check">check</span>
              </button>
            </div>
          </Transition>
        </div>

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

        <button
          v-if="filterGroup || filterType || filterStatus || searchInput"
          class="tt-clear-btn"
          title="Xóa bộ lọc"
          @click="clearFilters"
        >
          <span class="material-symbols-outlined">filter_alt_off</span>
        </button>
      </div>
    </div>

    <div class="overflow-x-auto">
      <table class="w-full text-left">
        <thead>
          <tr class="bg-slate-50 dark:bg-slate-800/50 border-b border-slate-200 dark:border-slate-800">
            <th class="px-6 py-4 text-xs font-extrabold uppercase tracking-wider text-slate-500">Mã báo cáo</th>
            <th class="px-6 py-4 text-xs font-extrabold uppercase tracking-wider text-slate-500">Tin tuyển dụng</th>
            <th class="px-6 py-4 text-xs font-extrabold uppercase tracking-wider text-slate-500">Nhóm</th>
            <th class="px-6 py-4 text-xs font-extrabold uppercase tracking-wider text-slate-500">Loại vi phạm</th>
            <th class="px-6 py-4 text-xs font-extrabold uppercase tracking-wider text-slate-500">Trạng thái</th>
            <th class="px-6 py-4 text-xs font-extrabold uppercase tracking-wider text-slate-500">SLA</th>
            <th class="px-6 py-4 text-xs font-extrabold uppercase tracking-wider text-slate-500 text-right">Thao tác</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-200 dark:divide-slate-800">
          <tr
            v-for="row in complaints"
            :key="row.id"
            class="hover:bg-slate-50/80 dark:hover:bg-slate-800/40 transition-colors"
          >
            <td class="px-6 py-4 text-sm font-bold text-slate-500">{{ row.reportCode }}</td>
            <td class="px-6 py-4 max-w-55">
              <p class="text-sm font-bold text-slate-900 dark:text-white line-clamp-2">{{ row.jobTitle }}</p>
            </td>
            <td class="px-6 py-4">
              <span class="px-2.5 py-1 text-xs font-bold rounded-full" :class="groupStyle[row.group]">
                {{ groupLabel[row.group] }}
              </span>
            </td>
            <td class="px-6 py-4">
              <div class="flex items-center gap-2">
                <span class="material-symbols-outlined text-base" :style="{ color: violationColor[row.violationType] }">
                  {{ violationIcon[row.violationType] }}
                </span>
                <span class="text-sm text-slate-600 dark:text-slate-400">{{ violationLabel[row.violationType] }}</span>
              </div>
            </td>
            <td class="px-6 py-4">
              <div class="flex items-center gap-1.5" :class="statusColor[row.status]">
                <span v-if="row.status === 'resolved'" class="material-symbols-outlined text-base">check_circle</span>
                <span
                  v-else
                  class="w-2 h-2 rounded-full shrink-0"
                  :class="statusDot[row.status]"
                  :style="row.status === 'pending' ? 'animation: pulse 2s infinite' : ''"
                />
                <span class="text-xs font-semibold">{{ statusLabel[row.status] }}</span>
              </div>
            </td>
            <td class="px-6 py-4">
              <span
                v-if="row.sla"
                class="inline-flex items-center gap-1 px-2.5 py-1 rounded-md text-xs font-bold"
                :class="row.sla.type === 'overdue'
                  ? 'bg-red-50 text-red-500 border border-red-100'
                  : 'bg-amber-50 text-amber-600 border border-amber-100'"
              >
                <span class="material-symbols-outlined text-sm">{{ row.sla.type === 'overdue' ? 'timer_off' : 'timer' }}</span>
                {{ row.sla.label }}
              </span>
              <span v-else class="text-slate-300 text-sm">—</span>
            </td>
            <td class="px-6 py-4 text-right">
              <button
                class="tt-view-btn"
                type="button"
                title="Xem chi tiết"
                aria-label="Xem chi tiết"
                @click="$emit('action', row)"
              >
                <span class="material-symbols-outlined text-lg">visibility</span>
              </button>
            </td>
          </tr>

          <tr v-if="complaints.length === 0">
            <td colspan="7" class="px-6 py-12 text-center text-slate-400 text-sm">
              <span class="material-symbols-outlined text-4xl block mb-2">search_off</span>
              Không tìm thấy báo cáo nào
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="px-6 py-4 border-t border-slate-200 dark:border-slate-800 flex items-center justify-between gap-4 flex-wrap">
      <p class="text-sm text-slate-500 font-medium">
        Hiển thị <span class="font-bold text-slate-700">{{ rangeStart }}–{{ rangeEnd }}</span>
        / <span class="font-bold text-slate-700">{{ total }}</span> báo cáo
      </p>
      <div class="flex items-center gap-1">
        <button
          class="p-1 rounded border border-slate-200 dark:border-slate-700 text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
          :disabled="currentPage <= 1"
          @click="$emit('page-change', currentPage - 1)"
        >
          <span class="material-symbols-outlined text-sm">chevron_left</span>
        </button>
        <template v-for="p in visiblePages" :key="p">
          <span v-if="p === '...'" class="px-2 text-slate-300 text-sm">...</span>
          <button
            v-else
            class="w-8 h-8 rounded text-sm font-extrabold border transition-colors"
            :class="p === currentPage
              ? 'bg-primary text-white border-primary'
              : 'border-slate-200 dark:border-slate-700 text-slate-600 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800'"
            @click="$emit('page-change', p as number)"
          >
            {{ p }}
          </button>
        </template>
        <button
          class="p-1 rounded border border-slate-200 dark:border-slate-700 text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
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
import { computed, onMounted, onUnmounted, ref } from 'vue'

export interface RecruiterComplaint {
  id: number
  reportCode: string
  jobTitle: string
  group: 'a' | 'b'
  violationType: 'fraudulent' | 'spam' | 'wrong_info' | 'inappropriate' | 'payment_issue' | 'other'
  status: 'pending' | 'processing' | 'waiting_employer' | 'resolved' | 'rejected' | 'auto_closed'
  sla?: { type: 'remaining' | 'overdue'; label: string }
  action: 'appeal' | 'fix' | 'view'
}

const props = defineProps<{
  complaints: RecruiterComplaint[]
  total: number
  currentPage: number
  pageSize: number
}>()

const emit = defineEmits<{
  action: [RecruiterComplaint]
  search: [string]
  filter: [{ group: string; type: string; status: string }]
  'page-change': [number]
}>()

const searchInput = ref('')
const filterGroup = ref('')
const filterType = ref('')
const filterStatus = ref('')

const showGroup = ref(false)
const showType = ref(false)
const showStatus = ref(false)

const groupRef = ref<HTMLElement | null>(null)
const typeRef = ref<HTMLElement | null>(null)
const statusRef = ref<HTMLElement | null>(null)

const groupOptions = [
  { value: '', label: 'Tất cả nhóm', icon: 'layers', colorClass: 'ico-slate' },
  { value: 'a', label: 'Nhóm A (Nhẹ)', icon: 'info', colorClass: 'ico-amber' },
  { value: 'b', label: 'Nhóm B (Nặng)', icon: 'warning', colorClass: 'ico-red' },
]

const typeOptions = [
  { value: '', label: 'Tất cả loại', icon: 'category', colorClass: 'ico-slate' },
  { value: 'fraudulent', label: 'Lừa đảo', icon: 'gavel', colorClass: 'ico-red' },
  { value: 'spam', label: 'Spam', icon: 'block', colorClass: 'ico-amber' },
  { value: 'wrong_info', label: 'Thông tin sai lệch', icon: 'description', colorClass: 'ico-blue' },
  { value: 'inappropriate', label: 'Nội dung không phù hợp', icon: 'report', colorClass: 'ico-red' },
  { value: 'payment_issue', label: 'Vấn đề thanh toán', icon: 'payments', colorClass: 'ico-amber' },
  { value: 'other', label: 'Khác', icon: 'more_horiz', colorClass: 'ico-slate' },
]

const statusOptions = [
  { value: '', label: 'Tất cả trạng thái', icon: 'radio_button_unchecked', colorClass: 'ico-slate' },
  { value: 'pending', label: 'Chờ xử lý', icon: 'schedule', colorClass: 'ico-amber' },
  { value: 'processing', label: 'Đang xử lý', icon: 'autorenew', colorClass: 'ico-blue' },
  { value: 'waiting_employer', label: 'Chờ NTD phản hồi', icon: 'hourglass_top', colorClass: 'ico-purple' },
  { value: 'resolved', label: 'Đã giải quyết', icon: 'check_circle', colorClass: 'ico-green' },
  { value: 'rejected', label: 'Từ chối', icon: 'cancel', colorClass: 'ico-red' },
  { value: 'auto_closed', label: 'Tự động đóng', icon: 'lock_clock', colorClass: 'ico-slate' },
]

const activeGroupOpt = computed(() => groupOptions.find((o) => o.value === filterGroup.value) ?? groupOptions[0]!)
const activeTypeOpt = computed(() => typeOptions.find((o) => o.value === filterType.value) ?? typeOptions[0]!)
const activeStatusOpt = computed(() => statusOptions.find((o) => o.value === filterStatus.value) ?? statusOptions[0]!)

function emitFilter() {
  emit('filter', { group: filterGroup.value, type: filterType.value, status: filterStatus.value })
}

function emitSearch() {
  emit('search', searchInput.value)
}

function selectGroup(value: string) {
  filterGroup.value = value
  showGroup.value = false
  emitFilter()
}

function selectType(value: string) {
  filterType.value = value
  showType.value = false
  emitFilter()
}

function selectStatus(value: string) {
  filterStatus.value = value
  showStatus.value = false
  emitFilter()
}

function clearFilters() {
  searchInput.value = ''
  filterGroup.value = ''
  filterType.value = ''
  filterStatus.value = ''
  emitSearch()
  emitFilter()
}

function onDocClick(event: MouseEvent) {
  if (groupRef.value && !groupRef.value.contains(event.target as Node)) showGroup.value = false
  if (typeRef.value && !typeRef.value.contains(event.target as Node)) showType.value = false
  if (statusRef.value && !statusRef.value.contains(event.target as Node)) showStatus.value = false
}

onMounted(() => document.addEventListener('mousedown', onDocClick))
onUnmounted(() => document.removeEventListener('mousedown', onDocClick))

const groupLabel: Record<'a' | 'b', string> = { a: 'Nhóm A', b: 'Nhóm B' }
const groupStyle: Record<'a' | 'b', string> = {
  a: 'bg-amber-100 text-amber-700',
  b: 'bg-red-100 text-red-700',
}

const violationLabel: Record<RecruiterComplaint['violationType'], string> = {
  fraudulent: 'Lừa đảo',
  spam: 'Spam',
  wrong_info: 'Thông tin sai lệch',
  inappropriate: 'Nội dung không phù hợp',
  payment_issue: 'Vấn đề thanh toán',
  other: 'Khác',
}

const violationIcon: Record<RecruiterComplaint['violationType'], string> = {
  fraudulent: 'gavel',
  spam: 'block',
  wrong_info: 'description',
  inappropriate: 'report',
  payment_issue: 'payments',
  other: 'more_horiz',
}

const violationColor: Record<RecruiterComplaint['violationType'], string> = {
  fraudulent: '#ef4444',
  spam: '#f59e0b',
  wrong_info: '#3b82f6',
  inappropriate: '#ef4444',
  payment_issue: '#f59e0b',
  other: '#94a3b8',
}

const statusLabel: Record<RecruiterComplaint['status'], string> = {
  pending: 'Chờ xử lý',
  processing: 'Đang xử lý',
  waiting_employer: 'Chờ NTD phản hồi',
  resolved: 'Đã giải quyết',
  rejected: 'Từ chối',
  auto_closed: 'Tự động đóng',
}

const statusColor: Record<RecruiterComplaint['status'], string> = {
  pending: 'text-amber-500',
  processing: 'text-blue-500',
  waiting_employer: 'text-violet-500',
  resolved: 'text-emerald-500',
  rejected: 'text-red-500',
  auto_closed: 'text-slate-500',
}

const statusDot: Record<RecruiterComplaint['status'], string> = {
  pending: 'bg-amber-500',
  processing: 'bg-blue-500',
  waiting_employer: 'bg-violet-500',
  resolved: 'bg-emerald-500',
  rejected: 'bg-red-500',
  auto_closed: 'bg-slate-500',
}

const totalPages = computed(() => Math.max(1, Math.ceil(props.total / props.pageSize)))
const rangeStart = computed(() => (props.total === 0 ? 0 : (props.currentPage - 1) * props.pageSize + 1))
const rangeEnd = computed(() => Math.min(props.currentPage * props.pageSize, props.total))

const visiblePages = computed(() => {
  const pages: (number | string)[] = []
  const total = totalPages.value
  const current = props.currentPage

  if (total <= 5) {
    for (let i = 1; i <= total; i += 1) pages.push(i)
    return pages
  }

  pages.push(1)
  if (current > 3) pages.push('...')
  for (let i = Math.max(2, current - 1); i <= Math.min(total - 1, current + 1); i += 1) pages.push(i)
  if (current < total - 2) pages.push('...')
  pages.push(total)
  return pages
})
</script>

<style scoped>
.tt-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-bottom: 1px solid #e2e8f0;
  flex-wrap: wrap;
  font-family: 'Manrope', sans-serif;
}

.tt-search-wrap { position: relative; flex: 1; min-width: 200px; max-width: 380px; }
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

.tt-filters { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; margin-left: auto; }
.tt-dd-wrap { position: relative; }

.tt-dd-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  background: #fff;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
  color: #475569;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  font-size: 0.875rem;
  font-weight: 500;
  white-space: nowrap;
  transition: border-color 0.15s, box-shadow 0.15s;
}

.tt-dd-btn:hover { border-color: #cbd5e1; box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06); }
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
  min-width: 220px;
  padding: 5px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1), 0 2px 6px rgba(0, 0, 0, 0.05);
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

.ico-slate { color: #94a3b8; }
.ico-blue { color: #3b82f6; }
.ico-green { color: #10b981; }
.ico-amber { color: #f59e0b; }
.ico-red { color: #ef4444; }
.ico-purple { color: #8b5cf6; }

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

.tt-view-btn {
  padding: 6px;
  background: transparent;
  border: none;
  border-radius: 8px;
  color: #94a3b8;
  cursor: pointer;
  transition: color 0.15s, background 0.15s;
}

.tt-view-btn:hover {
  color: #4b9af6;
  background: #eff6ff;
}

.tt-fade-enter-active, .tt-fade-leave-active { transition: opacity 0.13s, transform 0.13s; }
.tt-fade-enter-from, .tt-fade-leave-to { opacity: 0; transform: translateY(-4px); }

@media (max-width: 640px) {
  .tt-search-wrap { max-width: 100%; }
  .tt-filters { margin-left: 0; width: 100%; }
  .tt-dd-wrap, .tt-dd-btn { width: 100%; }
  .tt-dropdown { width: 100%; min-width: 100%; }
}
</style>
