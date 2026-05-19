<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">Lịch sử thay đổi quyền</h1>
      <p class="page-desc">Theo dõi toàn bộ thay đổi vai trò và quyền truy cập của thành viên trong hệ thống.</p>
    </div>

    <PermissionLogFilters
      v-model:tab="activeTab"
      v-model:type="filterType"
      v-model:member="filterMember"
      v-model:date-from="appliedDateFrom"
      v-model:date-to="appliedDateTo"
      :members="members"
      :result-count="resultCount"
      @apply="onApplyDateFilter"
    />

    <div v-if="loading" class="loading-state">
      <span class="material-symbols-outlined loading-spin">progress_activity</span>
      <p>Đang tải dữ liệu...</p>
    </div>

    <div v-else-if="activeTab === 'member' && !filterMember" class="empty-hint">
      <p>Chọn một thành viên ở trên để xem lịch sử thay đổi quyền.</p>
    </div>

    <template v-else>
      <PermissionLogList :logs="pagedLogs" />

      <JobPostingPagination
        v-if="showPagination"
        :total="paginationTotal"
        :current-page="currentPage"
        :per-page="pageSize"
        class="pagination-wrap"
        @update:current-page="onPageChange"
      />
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import PermissionLogFilters from '@/components/recruiter/team/log/PermissionLogFilters.vue'
import PermissionLogList from '@/components/recruiter/team/log/PermissionLogList.vue'
import JobPostingPagination from '@/components/recruiter/jobs/JobPostingPagination.vue'
import { useEmployerMemberStore } from '@/stores/employerMember.store'
import type { ResPermissionChangeLogDTO } from '@/types/companyMember.types'

const activeTab = ref<'company' | 'member'>('company')
const filterType = ref('all')
const filterMember = ref('')
const appliedDateFrom = ref('')
const appliedDateTo = ref('')

const currentPage = ref(0)
const pageSize = 20
const companyTotalItems = ref(0)

const memberStore = useEmployerMemberStore()

onMounted(() => {
  memberStore.getMembers({ page: 0, size: 50 })
  loadCompanyHistory()
})

const members = computed(() => memberStore.members?.result ?? [])
const loading = ref(false)
const rawLogs = ref<ResPermissionChangeLogDTO[]>([])

async function loadCompanyHistory() {
  loading.value = true
  try {
    const params: Record<string, unknown> = {
      page: currentPage.value,
      size: pageSize,
      sort: 'createdAt,desc',
    }
    if (appliedDateFrom.value) params.fromDate = appliedDateFrom.value
    if (appliedDateTo.value)   params.toDate   = appliedDateTo.value

    await memberStore.getCompanyPermissionHistory(params)
    rawLogs.value = memberStore.companyPermissionHistory?.result ?? []
    companyTotalItems.value = memberStore.companyPermissionHistory?.meta?.totals ?? 0
  } catch {
    rawLogs.value = []
    companyTotalItems.value = 0
  } finally {
    loading.value = false
  }
}

function onApplyDateFilter() {
  if (activeTab.value === 'company') {
    currentPage.value = 0
    loadCompanyHistory()
  } else {
    currentPage.value = 0
  }
}

function onPageChange(page: number) {
  currentPage.value = page
  if (activeTab.value === 'company') {
    loadCompanyHistory()
  }
}

async function loadMemberHistory(userId: number) {
  loading.value = true
  try {
    await memberStore.getMemberPermissionHistory(userId)
    rawLogs.value = memberStore.memberPermissionHistory ?? []
  } catch {
    rawLogs.value = []
  } finally {
    loading.value = false
  }
}

watch(activeTab, (tab) => {
  if (tab === 'company') {
    filterMember.value = ''
    currentPage.value = 0
    loadCompanyHistory()
  } else {
    rawLogs.value = []
    currentPage.value = 0
  }
})

watch(filterMember, (email) => {
  if (activeTab.value !== 'member') return
  currentPage.value = 0
  if (!email) {
    rawLogs.value = []
    return
  }
  const found = members.value.find((m) => m.email === email)
  if (found) loadMemberHistory(found.userId)
})

watch(filterType, () => {
  currentPage.value = 0
})

const AV_CLASSES = ['av1', 'av2', 'av3', 'av4'] as const

// ─── Helpers ─────────────────────────────────────────────────────────────────

function getInitials(email: string) {
  return email
    .split('@')[0]
    .split(/[._-]/)
    .map((part) => part[0]?.toUpperCase() ?? '')
    .join('')
    .slice(0, 2)
}

function formatTime(isoStr: string): string {
  const date = new Date(isoStr)
  const now = new Date()
  const diffMs = now.getTime() - date.getTime()
  const hours = Math.floor(diffMs / 3_600_000)
  if (hours < 1) return 'Vừa xong'
  if (hours < 24) return `${hours} giờ trước`
  const days = Math.floor(diffMs / 86_400_000)
  if (days === 1)
    return 'Hôm qua, ' + date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })
  return date.toLocaleDateString('vi-VN')
}

function formatCreatedDate(isoStr: string): string {
  return new Date(isoStr).toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  })
}

/** Build map code → name từ tất cả các action trong dto */
function buildCodeNameMap(dto: ResPermissionChangeLogDTO): Record<string, string> {
  const map: Record<string, string> = {}
  for (const perms of [dto.oldPermissions, dto.newPermissions]) {
    for (const item of [...(perms?.grant ?? []), ...(perms?.revoke ?? [])]) {
      map[item.code] = item.name
    }
  }
  return map
}

/** Làm đẹp reason hệ thống: "Toggle action [company:edit] -> revoke" */
function formatReason(reason: string | null, nameMap: Record<string, string>): string | null {
  if (!reason) return null
  const m = reason.match(/Toggle action \[(.+?)\] -> (grant|revoke)/i)
  if (m) {
    const name = nameMap[m[1]] ?? m[1]
    return m[2] === 'grant' ? `Cấp quyền: ${name}` : `Thu hồi quyền: ${name}`
  }
  return reason
}

/**
 * Tính diff quyền:
 * - added  = mới xuất hiện trong grant  (so với old)
 * - removed = mới xuất hiện trong revoke (so với old)
 */
function diffPerms(dto: ResPermissionChangeLogDTO) {
  const oldGrantCodes = new Set((dto.oldPermissions?.grant ?? []).map((p) => p.code))
  const oldRevokeCodes = new Set((dto.oldPermissions?.revoke ?? []).map((p) => p.code))

  const added   = (dto.newPermissions?.grant  ?? []).filter((p) => !oldGrantCodes.has(p.code)).map((p) => p.name)
  const removed = (dto.newPermissions?.revoke ?? []).filter((p) => !oldRevokeCodes.has(p.code)).map((p) => p.name)

  return { added, removed }
}

function mapLog(dto: ResPermissionChangeLogDTO) {
  const nameMap = buildCodeNameMap(dto)
  const { added, removed } = diffPerms(dto)
  return {
    id: dto.id,
    type: dto.changeType.toLowerCase(),
    name: dto.targetEmail.split('@')[0],
    email: dto.targetEmail,
    initials: getInitials(dto.targetEmail),
    avatarClass: AV_CLASSES[dto.id % 4],
    oldRole: dto.oldRole ?? null,
    newRole: dto.newRole ?? null,
    addedPerms: added,
    removedPerms: removed,
    changedBy: dto.changedByEmail,
    time: formatTime(dto.createdAt),
    createdDate: formatCreatedDate(dto.createdAt),
    rawTime: new Date(dto.createdAt).getTime(),
    reason: formatReason(dto.reason, nameMap),
  }
}

const filteredLogs = computed(() => {
  const from = appliedDateFrom.value ? new Date(appliedDateFrom.value).setHours(0, 0, 0, 0) : null
  const to   = appliedDateTo.value   ? new Date(appliedDateTo.value).setHours(23, 59, 59, 999) : null

  return rawLogs.value
    .map(mapLog)
    .filter((log) => {
      if (filterType.value !== 'all' && log.type !== filterType.value) return false
      if (activeTab.value === 'member' && from !== null && log.rawTime < from) return false
      if (activeTab.value === 'member' && to !== null && log.rawTime > to) return false
      return true
    })
})

const pagedLogs = computed(() => {
  if (activeTab.value === 'company') return filteredLogs.value
  const start = currentPage.value * pageSize
  return filteredLogs.value.slice(start, start + pageSize)
})

const resultCount = computed(() => {
  if (activeTab.value === 'company' && filterType.value === 'all') return companyTotalItems.value
  return filteredLogs.value.length
})

const paginationTotal = computed(() =>
  activeTab.value === 'company' ? companyTotalItems.value : filteredLogs.value.length,
)

const showPagination = computed(() =>
  paginationTotal.value > pageSize,
)
</script>

<style scoped>
.page {
  margin: 0 auto;
  font-family: 'Manrope', sans-serif;
}

.page-header {
  margin-bottom: 16px;
}

.page-title {
  margin-bottom: 4px;
  color: #0f172a;
  font-size: 1.875rem;
  font-weight: 800;
  letter-spacing: -0.02em;
}

.page-desc {
  color: #64748b;
  font-size: 0.875rem;
}

.loading-state {
  padding: 48px;
  text-align: center;
  color: #94a3b8;
  font-size: 0.875rem;
}

.empty-hint {
  padding: 48px;
  text-align: center;
  background: #fff;
  border: 1px dashed #cbd5e1;
  border-radius: 16px;
  color: #64748b;
  font-size: 0.875rem;
}

.pagination-wrap {
  margin-top: 16px;
  background: rgba(248, 250, 252, 0.3);
  border: 1px solid #f1f5f9;
  border-radius: 16px;
}
</style>
