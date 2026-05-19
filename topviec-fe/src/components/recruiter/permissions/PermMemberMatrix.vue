<template>
  <div class="pmm-wrap">

    <!-- ── Toolbar ─────────────────────────────────────────── -->
    <div class="pmm-toolbar">
      <div class="pmm-search-wrap">
        <span class="material-symbols-outlined pmm-search-icon">search</span>
        <input v-model="search" type="text" class="pmm-search-input" placeholder="Tìm quyền hạn..." />
      </div>

      <!-- Member picker -->
      <div class="pmm-picker-wrap" ref="pickerRef">
        <button class="pmm-add-btn" @click="showPicker = !showPicker">
          <span class="material-symbols-outlined">person_add</span>
          Chọn thành viên
          <span v-if="selectedMembers.length" class="pmm-add-badge">{{ selectedMembers.length }}</span>
        </button>

        <Transition name="picker-fade">
          <div v-if="showPicker" class="pmm-picker-dropdown">
            <div class="pmm-picker-head">
              <span class="material-symbols-outlined">group</span>
              Thành viên trong nhóm
            </div>
            <div class="pmm-picker-list">
              <div
                v-for="m in members"
                :key="m.id"
                :class="['pmm-picker-item', isSelected(m.id) && 'pmm-picker-item--on']"
                @click="toggleMember(m)"
              >
                <div class="pmm-picker-ava" :style="avaStyle(m)">
                  <span>{{ getInitials(m.email) }}</span>
                </div>
                <div class="pmm-picker-info">
                  <span class="pmm-picker-name">{{ m.email.split('@')[0] }}</span>
                  <span class="pmm-picker-email">{{ m.email }}</span>
                </div>
                <span :class="['pmm-r-tag', `pmm-r-tag--${m.roleName}`]">{{ roleLabel[m.roleName as string] }}</span>
                <div :class="['pmm-picker-check', isSelected(m.id) && 'pmm-picker-check--on']">
                  <span class="material-symbols-outlined">{{ isSelected(m.id) ? 'check' : 'add' }}</span>
                </div>
              </div>
            </div>
          </div>
        </Transition>
      </div>
    </div>

    <!-- ── Table ───────────────────────────────────────────── -->
    <div v-if="selectedMembers.length" class="pmm-table-wrap">
      <table class="pmm-table">
        <thead>
          <tr class="pmm-head-row">
            <th class="pmm-th pmm-th--feature">Quyền hạn</th>
            <th v-for="m in selectedMembers" :key="m.id" class="pmm-th pmm-th--member">
              <div class="pmm-member-head">
                <div class="pmm-member-ava" :style="avaStyle(m)">
                  <span>{{ getInitials(m.email) }}</span>
                </div>
                <div class="pmm-member-meta">
                  <span class="pmm-member-name">{{ m.email.split('@')[0] }}</span>
                  <span :class="['pmm-r-tag', `pmm-r-tag--${m.roleName}`]">{{ roleLabel[m.roleName as string] }}</span>
                </div>
                <button class="pmm-remove-btn" @click="removeMember(m.id)" title="Bỏ khỏi bảng">
                  <span class="material-symbols-outlined">close</span>
                </button>
              </div>
            </th>
          </tr>
        </thead>

        <tbody>
          <template v-for="group in filteredGroups" :key="group.id">

            <!-- Group header -->
            <tr class="pmm-group-row" @click="collapsed[group.id] = !collapsed[group.id]">
              <td :colspan="selectedMembers.length + 1" class="pmm-group-cell">
                <div class="pmm-group-inner">
                  <div class="pmm-group-icon-box">
                    <span class="material-symbols-outlined">{{ group.icon }}</span>
                  </div>
                  <span class="pmm-group-label">{{ group.label }}</span>
                  <span class="pmm-group-sub">({{ group.sub }})</span>
                  <span class="pmm-group-count">{{ group.perms.length }} quyền</span>
                  <div :class="['pmm-chevron', collapsed[group.id] && 'pmm-chevron--collapsed']">
                    <span class="material-symbols-outlined">expand_more</span>
                  </div>
                </div>
              </td>
            </tr>

            <!-- Permission rows -->
            <tr
              v-for="perm in group.perms"
              v-show="!collapsed[group.id]"
              :key="perm.id"
              class="pmm-perm-row"
            >
              <td class="pmm-perm-name-cell">
                <div class="pmm-perm-label-wrap">
                  <span class="pmm-perm-label">{{ perm.label }}</span>
                  <span class="pmm-perm-code">{{ perm.id }}</span>
                </div>
              </td>
              <td v-for="m in selectedMembers" :key="m.id" class="pmm-toggle-cell">
                <div v-if="loadingM[m.id] || savingCell[`${perm.id}|${m.id}`]" class="pmm-cell-loading">
                  <span class="material-symbols-outlined pmm-spin">progress_activity</span>
                </div>
                <div v-else class="pmm-cell-inner">
                  <span
                    v-if="customPerms[m.id]?.[perm.id] === true"
                    class="pmm-tag-custom"
                  >Custom</span>
                  <RPermToggle
                    :model-value="state[`${perm.id}|${m.id}`] ?? false"
                    :locked="m.roleName === 'owner'"
                    @update:model-value="handleToggle(m, perm.id, $event)"
                  />
                </div>
              </td>
            </tr>

          </template>
        </tbody>
      </table>
    </div>

    <!-- ── Empty state ─────────────────────────────────────── -->
    <div v-else class="pmm-empty">
      <div class="pmm-empty-icon-wrap">
        <span class="material-symbols-outlined">group_add</span>
      </div>
      <p class="pmm-empty-title">Chưa có thành viên nào được chọn</p>
      <p class="pmm-empty-desc">Nhấn <strong>Chọn thành viên</strong> để thêm vào bảng và cấu hình quyền hạn.</p>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import RPermToggle from './RPermToggle.vue'
import { employerMemberService } from '@/services/employerMember.service'
import type { ResCompanyMember } from '@/types/companyMember.types'
import { useToast } from '@/composables/useToast'

const toast = useToast()

interface Perm  { id: string; label: string }
interface Group { id: string; label: string; sub: string; icon: string; perms: Perm[] }

const props = defineProps<{ members: ResCompanyMember[] }>()

const roleLabel: Record<string, string> = {
  owner: 'Owner', manager: 'Manager', recruiter: 'Recruiter', viewer: 'Viewer',
}

const ROLE_COLORS: Record<string, string> = {
  owner:     'linear-gradient(135deg,#ef4444,#dc2626)',
  manager:   'linear-gradient(135deg,#4B9AF6,#2563eb)',
  recruiter: 'linear-gradient(135deg,#10b981,#059669)',
  viewer:    'linear-gradient(135deg,#94a3b8,#64748b)',
}

const GROUP_META: Record<string, { label: string; sub: string; icon: string }> = {
  cv:        { label: 'Quản lý Hồ sơ ứng tuyển', sub: 'CV',        icon: 'description' },
  job:       { label: 'Quản lý Tin tuyển dụng',   sub: 'Job',       icon: 'work' },
  talent:    { label: 'Quản lý Ứng viên',          sub: 'Talent',    icon: 'person' },
  member:    { label: 'Quản lý Thành viên',         sub: 'Member',    icon: 'group' },
  report:    { label: 'Báo cáo & Thống kê',         sub: 'Report',    icon: 'bar_chart' },
  company:   { label: 'Quản lý Công ty',            sub: 'Company',   icon: 'business' },
  service:   { label: 'Dịch vụ',                    sub: 'Service',   icon: 'inventory_2' },
}

// Danh sách action lấy từ BE (effectivePermissions của lần fetch đầu tiên)
const allActions = ref<{ code: string; name: string }[]>([])

const groups = computed<Group[]>(() => {
  const groupMap = new Map<string, Group>()
  for (const a of allActions.value) {
    const prefix = a.code.split(':')[0]
    if (!groupMap.has(prefix)) {
      const meta = GROUP_META[prefix] ?? { label: prefix, sub: prefix, icon: 'settings' }
      groupMap.set(prefix, { id: prefix, ...meta, perms: [] })
    }
    groupMap.get(prefix)!.perms.push({ id: a.code, label: a.name })
  }
  return [...groupMap.values()]
})

const selectedMembers = ref<ResCompanyMember[]>([])
const state       = reactive<Record<string, boolean>>({})
const customPerms = reactive<Record<number, Record<string, boolean>>>({}) // memberId → customPermissions
const loadingM    = reactive<Record<number, boolean>>({})
const savingCell  = reactive<Record<string, boolean>>({})
const search   = ref('')
const showPicker = ref(false)
const pickerRef  = ref<HTMLElement | null>(null)
const collapsed  = reactive<Record<string, boolean>>({})

const isSelected = (id: number) => selectedMembers.value.some(m => m.id === id)

const toggleMember = async (m: ResCompanyMember) => {
  if (isSelected(m.id)) {
    removeMember(m.id)
    return
  }
  selectedMembers.value.push(m)
  loadingM[m.id] = true
  try {
    const res = await employerMemberService.getBatchMemberPermissions({ userIds: [m.userId] })
    const detail = res.data?.[0]
    if (detail) {
      // Lấy định nghĩa action từ lần fetch đầu tiên
      if (allActions.value.length === 0) {
        allActions.value = detail.effectivePermissions.map(p => ({ code: p.code, name: p.name }))
      }
      // Map toggle state và custom permissions
      customPerms[m.id] = detail.customPermissions ?? {}
      for (const item of detail.effectivePermissions) {
        state[`${item.code}|${m.id}`] = item.enabled
      }
    }
  } catch {
    // fallback: tắt hết khi lỗi
    for (const a of allActions.value) {
      state[`${a.code}|${m.id}`] = false
    }
  } finally {
    loadingM[m.id] = false
  }
}

const removeMember = (id: number) => {
  selectedMembers.value = selectedMembers.value.filter(m => m.id !== id)
  delete customPerms[id]
}

async function handleToggle(m: ResCompanyMember, permCode: string, newValue: boolean) {
  const key = `${permCode}|${m.id}`
  const prev = state[key]
  state[key] = newValue
  savingCell[key] = true
  try {
    const res = await employerMemberService.toggleMemberActionPermission(m.userId, permCode, { enabled: newValue })
    const detail = res.data
    if (detail) {
      customPerms[m.id] = detail.customPermissions ?? {}
      for (const item of detail.effectivePermissions) {
        state[`${item.code}|${m.id}`] = item.enabled
      }
    }
    toast.success('Cập nhật quyền thành công')
  } catch (err: any) {
    state[key] = prev
    toast.error(err?.response?.data?.message || 'Cập nhật quyền thất bại')
  } finally {
    savingCell[key] = false
  }
}

const getInitials = (email: string) =>
  email.split('@')[0].split(/[._-]/).map(p => p[0]?.toUpperCase() ?? '').join('').slice(0, 2)

const avaStyle = (m: ResCompanyMember) => ({
  background: ROLE_COLORS[m.roleName as string] ?? ROLE_COLORS.viewer,
})

const filteredGroups = computed(() => {
  const q = search.value.toLowerCase().trim()
  if (!q) return groups.value
  return groups.value
    .map(g => ({ ...g, perms: g.perms.filter(p => p.label.toLowerCase().includes(q) || p.id.includes(q)) }))
    .filter(g => g.perms.length > 0)
})

const onDocClick = (e: MouseEvent) => {
  if (pickerRef.value && !pickerRef.value.contains(e.target as Node)) {
    showPicker.value = false
  }
}
onMounted(()  => document.addEventListener('mousedown', onDocClick))
onUnmounted(() => document.removeEventListener('mousedown', onDocClick))
</script>

<style scoped>
.pmm-wrap { display: flex; flex-direction: column; gap: 16px; padding: 20px; }

/* ── Toolbar ──────────────────────────────────────────────── */
.pmm-toolbar {
  display: flex; align-items: center;
  justify-content: space-between; gap: 12px; flex-wrap: wrap;
}
.pmm-search-wrap { position: relative; display: flex; align-items: center; width: 300px; }
.pmm-search-icon { position: absolute; left: 12px; font-size: 18px; color: #94a3b8; }
.pmm-search-input {
  width: 100%; padding: 9px 12px 9px 38px;
  border: 1px solid #e2e8f0; border-radius: 10px;
  font-size: 0.875rem; background: #f8fafc; color: #0f172a;
  outline: none; font-family: 'Manrope', sans-serif; transition: all 0.2s;
}
.pmm-search-input:focus { border-color: #4B9AF6; background: #fff; box-shadow: 0 0 0 3px rgba(75,154,246,0.12); }

/* Picker button */
.pmm-picker-wrap { position: relative; }
.pmm-add-btn {
  display: inline-flex; align-items: center; gap: 8px;
  padding: 9px 16px; background: #4B9AF6; color: #fff;
  border: none; border-radius: 10px; font-size: 0.875rem; font-weight: 600;
  cursor: pointer; font-family: 'Manrope', sans-serif;
  box-shadow: 0 2px 8px rgba(75,154,246,0.3); transition: background 0.15s;
}
.pmm-add-btn:hover { background: #3b82f6; }
.pmm-add-btn .material-symbols-outlined { font-size: 18px; }
.pmm-add-badge {
  display: inline-flex; align-items: center; justify-content: center;
  width: 20px; height: 20px; border-radius: 50%;
  background: rgba(255,255,255,0.3); font-size: 0.75rem; font-weight: 700;
}

/* Picker dropdown */
.pmm-picker-dropdown {
  position: absolute; top: calc(100% + 8px); right: 0; z-index: 9;
  width: 340px; background: #fff;
  border: 1px solid #e2e8f0; border-radius: 14px;
  box-shadow: 0 10px 32px rgba(0,0,0,0.12), 0 2px 8px rgba(0,0,0,0.06);
  overflow: hidden;
}
.pmm-picker-head {
  display: flex; align-items: center; gap: 8px;
  padding: 14px 16px; border-bottom: 1px solid #f1f5f9;
  font-size: 0.8rem; font-weight: 700; color: #475569;
  text-transform: uppercase; letter-spacing: 0.05em;
  background: #f8fafc;
}
.pmm-picker-head .material-symbols-outlined { font-size: 18px; color: #4B9AF6; }
.pmm-picker-list { max-height: 300px; overflow-y: auto; padding: 6px; }

.pmm-picker-item {
  display: flex; align-items: center; gap: 10px;
  padding: 9px 10px; border-radius: 10px; cursor: pointer;
  transition: background 0.15s; border: 1px solid transparent;
}
.pmm-picker-item:hover { background: #f8fafc; }
.pmm-picker-item--on { background: #eff6ff; border-color: #dbeafe; }

.pmm-picker-ava {
  width: 36px; height: 36px; border-radius: 50%; flex-shrink: 0;
  display: flex; align-items: center; justify-content: center;
  font-size: 0.75rem; font-weight: 700; color: #fff;
  overflow: hidden; position: relative;
}
.pmm-ava-img { position: absolute; inset: 0; width: 100%; height: 100%; object-fit: cover; border-radius: 50%; }

.pmm-picker-info { flex: 1; min-width: 0; }
.pmm-picker-name { display: block; font-size: 0.875rem; font-weight: 600; color: #0f172a; }
.pmm-picker-email { display: block; font-size: 0.75rem; color: #94a3b8; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.pmm-picker-check {
  width: 26px; height: 26px; border-radius: 50%; flex-shrink: 0;
  display: flex; align-items: center; justify-content: center;
  background: #f1f5f9; color: #94a3b8; transition: all 0.15s;
}
.pmm-picker-check .material-symbols-outlined { font-size: 16px; }
.pmm-picker-check--on { background: #4B9AF6; color: #fff; }

/* Picker transition */
.picker-fade-enter-active, .picker-fade-leave-active { transition: opacity 0.15s, transform 0.15s; }
.picker-fade-enter-from, .picker-fade-leave-to { opacity: 0; transform: translateY(-6px); }

/* ── Role tags ────────────────────────────────────────────── */
.pmm-r-tag {
  font-size: 0.7rem; padding: 2px 8px; border-radius: 10px; font-weight: 700;
  white-space: nowrap; flex-shrink: 0;
}
.pmm-r-tag--owner    { background: #fee2e2; color: #991b1b; border: 1px solid #fecaca; }
.pmm-r-tag--manager  { background: #dbeafe; color: #1e40af; border: 1px solid #bfdbfe; }
.pmm-r-tag--recruiter{ background: #d1fae5; color: #065f46; border: 1px solid #a7f3d0; }
.pmm-r-tag--viewer   { background: #f1f5f9; color: #475569; border: 1px solid #e2e8f0; }

/* ── Table ────────────────────────────────────────────────── */
.pmm-table-wrap {
  background: #fff; border: 1px solid #e2e8f0;
  border-radius: 12px; overflow: hidden;
}
.pmm-table { width: 100%; border-collapse: collapse; font-size: 0.875rem; }

.pmm-head-row { position: sticky; top: 0;  }
.pmm-th {
  background: #f8fafc; border-bottom: 1px solid #e2e8f0; padding: 14px 16px;
}
.pmm-th--feature {
  width: 340px; text-align: left; padding: 14px 24px;
  font-size: 0.75rem; font-weight: 700; color: #475569;
  text-transform: uppercase; letter-spacing: 0.06em;
}
.pmm-th--member { text-align: center; min-width: 160px; }

.pmm-member-head {
  display: flex; flex-direction: column; align-items: center; gap: 6px;
  position: relative; padding: 4px 28px 4px 8px;
}
.pmm-member-ava {
  width: 40px; height: 40px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 0.8rem; font-weight: 700; color: #fff;
  overflow: hidden; position: relative;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}
.pmm-member-meta { display: flex; flex-direction: column; align-items: center; gap: 4px; }
.pmm-member-name { font-size: 0.8rem; font-weight: 700; color: #0f172a; white-space: nowrap; }

.pmm-remove-btn {
  position: absolute; top: 0; right: 0;
  width: 22px; height: 22px; border-radius: 50%; border: none;
  background: #e2e8f0; color: #64748b; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.15s;
}
.pmm-remove-btn .material-symbols-outlined { font-size: 14px; }
.pmm-remove-btn:hover { background: #fecaca; color: #dc2626; }

/* Group row */
.pmm-group-row { cursor: pointer; transition: background 0.15s; }
.pmm-group-cell {
  padding: 12px 24px; background: #f8fafc;
  border-top: 1px solid #e2e8f0; border-bottom: 1px solid #e2e8f0;
  transition: background 0.15s;
}
.pmm-group-row:hover .pmm-group-cell { background: #f1f5f9; }
.pmm-group-inner { display: flex; align-items: center; gap: 12px; }

.pmm-group-icon-box {
  width: 32px; height: 32px; border-radius: 8px; flex-shrink: 0;
  display: flex; align-items: center; justify-content: center;
  background: #eff6ff; color: #4B9AF6;
  border: 1px solid #dbeafe;
}
.pmm-group-icon-box .material-symbols-outlined { font-size: 17px; }
.pmm-group-label { font-size: 0.95rem; font-weight: 700; color: #0f172a; }
.pmm-group-sub   { font-size: 0.8rem; color: #64748b; }
.pmm-group-count {
  margin-left: auto; font-size: 0.75rem; color: #475569; font-weight: 600;
  background: #fff; padding: 2px 10px; border-radius: 20px;
  border: 1px solid #cbd5e1;
}
.pmm-chevron {
  width: 28px; height: 28px; display: flex; align-items: center; justify-content: center;
  color: #94a3b8; border-radius: 50%; transition: all 0.2s;
}
.pmm-group-row:hover .pmm-chevron { background: rgba(0,0,0,0.05); color: #475569; }
.pmm-chevron .material-symbols-outlined { font-size: 20px; transition: transform 0.2s ease; }
.pmm-chevron--collapsed .material-symbols-outlined { transform: rotate(-90deg); }

/* Permission row */
.pmm-perm-row { transition: background 0.15s; }
.pmm-perm-row:not(:last-child) td { border-bottom: 1px solid #f1f5f9; }
.pmm-perm-row:hover td { background: #f8fafc; }
.pmm-perm-name-cell { padding: 12px 24px 12px 56px; }
.pmm-perm-label-wrap { display: flex; align-items: center; gap: 10px; }
.pmm-perm-label { font-size: 0.875rem; color: #0f172a; font-weight: 500; }
.pmm-perm-code {
  font-size: 0.7rem; color: #64748b; font-family: monospace;
  background: #f1f5f9; padding: 2px 7px; border-radius: 5px;
  border: 1px solid #e2e8f0;
}
.pmm-toggle-cell { text-align: center; padding: 10px 16px; vertical-align: middle; }

/* ── Empty state ──────────────────────────────────────────── */
.pmm-empty {
  display: flex; flex-direction: column; align-items: center;
  justify-content: center; padding: 5rem 2rem; gap: 12px;
}
.pmm-empty-icon-wrap {
  width: 72px; height: 72px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  background: #eff6ff; color: #4B9AF6;
  box-shadow: 0 0 0 12px #eff6ff80;
  margin-bottom: 8px;
}
.pmm-empty-icon-wrap .material-symbols-outlined { font-size: 36px; }
.pmm-empty-title { font-size: 1rem; font-weight: 700; color: #0f172a; margin: 0; }
.pmm-empty-desc  { font-size: 0.875rem; color: #64748b; text-align: center; margin: 0; line-height: 1.6; }
.pmm-empty-desc strong { color: #4B9AF6; }

.pmm-cell-loading { display: flex; align-items: center; justify-content: center; height: 32px; }
.pmm-spin { font-size: 20px; color: #4B9AF6; animation: pmm-spin 0.8s linear infinite; }
@keyframes pmm-spin { to { transform: rotate(360deg); } }

.pmm-cell-inner {
  position: relative;
  display: inline-flex; align-items: center; justify-content: center;
  padding-top: 14px;
}
.pmm-tag-custom {
  position: absolute; top: 0; right: -50px;
  font-size: 0.6rem; font-weight: 700; letter-spacing: 0.04em;
  padding: 1px 5px; border-radius: 4px;
  background: rgba(75,154,246,0.12); color: #3b82f6;
  border: 1px solid rgba(75,154,246,0.3);
  white-space: nowrap; line-height: 1.5;
}
</style>
