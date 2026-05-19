<template>
  <div class="perm-page">
    <!-- Loading -->
    <div v-if="loading" class="perm-page__loading">
      <span class="material-symbols-outlined perm-page__loading-icon">sync</span>
      Đang tải cấu hình quyền...
    </div>

    <template v-else>
      <div class="perm-page__header">
        <div>
          <h1 class="perm-page__title">Cấu hình quyền mặc định</h1>
          <p class="perm-page__subtitle">Thiết lập các quyền hạn mặc định cho từng vai trò trong hệ thống Nhà Tuyển Dụng</p>
        </div>
        <div class="perm-page__actions">
          <button class="perm-page__btn perm-page__btn--ghost cursor-pointer" @click="isModalVisible = true">
            <span class="material-symbols-outlined">add</span>
            Thêm mới hành động
          </button>
        </div>
      </div>

      <div class="perm-page__banner">
        <span class="material-symbols-outlined perm-page__banner-icon">warning</span>
        <p class="perm-page__banner-text">
          <strong>Lưu ý:</strong> Thay đổi quyền mặc định sẽ ảnh hưởng đến tất cả thành viên mới được thêm vào. Các thành viên đã có tùy chỉnh riêng sẽ không bị ảnh hưởng.
        </p>
      </div>

      <PermissionTable :groups="groups" v-model:state="state" @toggle="handleToggle" @rename="openRenameModal" />

      <div class="perm-page__legend">
        <div class="legend-item"><span class="legend-dot legend-dot--on"></span> Đã bật</div>
        <div class="legend-item"><span class="legend-dot legend-dot--off"></span> Đã tắt</div>
        <div class="legend-item"><span class="legend-dot legend-dot--locked"></span> Cố định (Owner)</div>
      </div>

      <ActionCreateModal
        :visible="isModalVisible"
        :groups="groups"
        @close="isModalVisible = false"
        @submit="handleAddAction"
      />

      <RenameActionModal
        :visible="renameModal.visible"
        :perm-id="renameModal.permId"
        :current-label="renameModal.currentLabel"
        @close="renameModal.visible = false"
        @submit="handleRenameSubmit"
      />
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import PermissionTable from '@/components/admin/permissions/PermissionTable.vue'
import ActionCreateModal from '@/components/admin/permissions/ActionCreateModal.vue'
import RenameActionModal from '@/components/admin/permissions/RenameActionModal.vue'
import { roleService } from '@/services/role.service'
import type { ResRoleDefaultDTO } from '@/types/role.types'
import { useToast } from '@/composables/useToast'

const toast = useToast()

const GROUP_META: Record<string, { label: string; sub: string; icon: string }> = {
  cv:      { label: 'Quản lý Hồ sơ ứng tuyển', sub: 'CV',      icon: 'description' },
  job:     { label: 'Quản lý Tin tuyển dụng',   sub: 'Job',     icon: 'work'        },
  talent:  { label: 'Quản lý Ứng viên',          sub: 'Talent',  icon: 'person'      },
  company: { label: 'Quản lý Công ty',           sub: 'Company', icon: 'apartment'   },
  member:  { label: 'Quản lý Thành viên',        sub: 'Member',  icon: 'group'       },
  report:  { label: 'Báo cáo & Thống kê',        sub: 'Report',  icon: 'bar_chart'   },
  service: { label: 'Dịch vụ & Thanh toán',      sub: 'Service', icon: 'credit_card' },
}

const CHAR_TO_ROLE: Record<string, string> = { m: 'manager', r: 'recruiter', v: 'viewer' }

const rolesData = ref<ResRoleDefaultDTO[]>([])
const groups    = reactive<any[]>([])
const state     = reactive<Record<string, boolean>>({})
const loading   = ref(false)
const isModalVisible = ref(false)
const renameModal    = reactive({ visible: false, permId: '', currentLabel: '' })

function buildFromRoles(roles: ResRoleDefaultDTO[]) {
  const actionMap: Record<string, { name: string; m: boolean; r: boolean; v: boolean }> = {}

  for (const role of roles) {
    const char = { manager: 'm', recruiter: 'r', viewer: 'v' }[role.roleName as string]
    if (!char) continue
    for (const action of role.actions) {
      if (!actionMap[action.code]) {
        actionMap[action.code] = { name: action.name, m: false, r: false, v: false }
      }
      ;(actionMap[action.code] as any)[char] = action.enabled
    }
  }

  const groupMap: Record<string, { id: string; label: string; sub: string; icon: string; perms: any[] }> = {}
  for (const [code, data] of Object.entries(actionMap)) {
    const prefix = code.split(':')[0]
    if (!groupMap[prefix]) {
      const meta = GROUP_META[prefix] ?? { label: prefix, sub: prefix, icon: 'lock' }
      groupMap[prefix] = { id: prefix, ...meta, perms: [] }
    }
    groupMap[prefix].perms.push({ id: code, label: data.name, m: data.m, r: data.r, v: data.v })
  }

  const ordered = [
    ...Object.keys(GROUP_META).filter(k => groupMap[k]),
    ...Object.keys(groupMap).filter(k => !GROUP_META[k]),
  ]
  groups.splice(0, groups.length, ...ordered.map(k => groupMap[k]))

  for (const k of Object.keys(state)) delete (state as any)[k]
  for (const g of groups) {
    for (const p of g.perms) {
      for (const c of ['m', 'r', 'v']) state[`${p.id}|${c}`] = p[c]
    }
  }
}

function getRoleId(char: string): number | null {
  return rolesData.value.find(r => r.roleName === CHAR_TO_ROLE[char])?.id ?? null
}

async function loadRoles() {
  loading.value = true
  try {
    rolesData.value = await roleService.getDefaultRoles()
    buildFromRoles(rolesData.value)
  } catch (e: any) {
    toast.error('Tải thất bại', e.response?.data?.message || 'Không thể tải cấu hình quyền')
  } finally {
    loading.value = false
  }
}

onMounted(loadRoles)

const handleToggle = async (payload: { permId: string; role: string; val: boolean }) => {
  const { permId, role, val } = payload
  const roleId = getRoleId(role)
  if (roleId == null) return

  try {
    await roleService.toggleAction(roleId, permId, { enabled: val })
    const roleName = { m: 'Manager', r: 'Recruiter', v: 'Viewer' }[role]
    toast.success('Cập nhật thành công', `${roleName} — ${permId}: ${val ? 'Bật' : 'Tắt'}`)
  } catch (e: any) {
    // Revert state nếu API thất bại
    state[`${permId}|${role}`] = !val
    toast.error('Cập nhật thất bại', e.response?.data?.message || 'Vui lòng thử lại')
  }
}

const openRenameModal = (payload: { id: string; label: string }) => {
  renameModal.permId = payload.id
  renameModal.currentLabel = payload.label
  renameModal.visible = true
}

const handleRenameSubmit = async (payload: { permId: string; newName: string }) => {
  renameModal.visible = false
  try {
    await Promise.all(
      (['m', 'r', 'v'] as const).map(async char => {
        const roleId = getRoleId(char)
        if (roleId == null) return
        await roleService.renameAction(roleId, payload.permId, { name: payload.newName })
      })
    )
    for (const g of groups) {
      const p = g.perms.find((p: any) => p.id === payload.permId)
      if (p) { p.label = payload.newName; break }
    }
    toast.success('Đổi tên thành công', `"${payload.permId}" → "${payload.newName}"`)
  } catch (e: any) {
    toast.error('Đổi tên thất bại', e.response?.data?.message || 'Vui lòng thử lại')
  }
}

const handleAddAction = async (payload: { moduleId: string; perm: { id: string; label: string; m: boolean; r: boolean; v: boolean } }) => {
  const { perm } = payload
  try {
    const updatedRoles = await roleService.addAction({ name: perm.label, code: perm.id })

    // Sync rolesData với response mới nhất từ BE
    for (const updated of updatedRoles) {
      const idx = rolesData.value.findIndex(r => r.id === updated.id)
      if (idx !== -1) rolesData.value[idx] = updated
    }

    // Lấy giá trị enabled thực tế từ BE (owner=true, còn lại=false)
    const newPerm = {
      id: perm.id,
      label: perm.label,
      m: updatedRoles.find(r => r.roleName === 'manager')?.actions.find(a => a.code === perm.id)?.enabled ?? false,
      r: updatedRoles.find(r => r.roleName === 'recruiter')?.actions.find(a => a.code === perm.id)?.enabled ?? false,
      v: updatedRoles.find(r => r.roleName === 'viewer')?.actions.find(a => a.code === perm.id)?.enabled ?? false,
    }

    const g = groups.find(g => g.id === payload.moduleId)
    if (g) {
      g.perms.push(newPerm)
      for (const c of ['m', 'r', 'v']) state[`${perm.id}|${c}`] = (newPerm as any)[c]
    }
    toast.success('Thêm thành công', `Đã thêm hành động "${perm.label}" (${perm.id})`)
  } catch (e: any) {
    toast.error('Thêm thất bại', e.response?.data?.message || 'Vui lòng thử lại')
  }
  isModalVisible.value = false
}
</script>

<style scoped>
/* Loading */
.perm-page__loading {
  display: flex; align-items: center; gap: 10px;
  padding: 3rem; justify-content: center;
  font-size: 0.9375rem; color: #64748b; font-weight: 500;
}
.perm-page__loading-icon {
  font-size: 22px; color: #963131;
  animation: spin 1s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* Error banner */
.perm-page__error-banner {
  display: flex; align-items: center; gap: 10px;
  background: #fef2f2; border: 1px solid #fecaca;
  border-radius: 10px; padding: 12px 16px; margin-bottom: 1rem;
  font-size: 0.875rem; color: #991b1b;
}
.perm-page__error-banner .material-symbols-outlined { font-size: 18px; flex-shrink: 0; }
.perm-page__error-close {
  margin-left: auto; background: none; border: none; cursor: pointer;
  color: #b91c1c; display: flex; align-items: center;
}
.perm-page__error-close .material-symbols-outlined { font-size: 18px; }

/* Header */
.perm-page__header {
  display: flex; align-items: flex-start; justify-content: space-between;
  margin-bottom: 1.5rem; gap: 1rem; flex-wrap: wrap;
}
.perm-page__title { font-size: 1.875rem; font-weight: 700; color: #0f172a; letter-spacing: -0.02em; margin-bottom: 6px; }
.perm-page__subtitle { font-size: 1rem; color: #475569; line-height: 1.5; }
.perm-page__actions { display: flex; align-items: center; gap: 12px; flex-shrink: 0; margin-top: 4px; }

.perm-page__btn {
  display: flex; align-items: center; gap: 6px;
  padding: 10px 20px; border-radius: 10px;
  font-size: 1rem; font-weight: 600; cursor: pointer;
  font-family: 'Be Vietnam Pro', sans-serif; white-space: nowrap;
  border: 1px solid transparent; transition: all 0.2s;
}
.perm-page__btn .material-symbols-outlined { font-size: 20px; }
.perm-page__btn--ghost {
  background: #fff; border-color: #cbd5e1; color: #475569;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}
.perm-page__btn--ghost:hover:not(:disabled) { border-color: #94a3b8; color: #0f172a; transform: translateY(-1px); }
.perm-page__btn--primary {
  background: #963131; color: #fff; border-color: #963131;
  box-shadow: 0 4px 10px rgba(150,49,49,0.2);
}
.perm-page__btn--primary:hover:not(:disabled) { opacity: 0.9; transform: translateY(-1px); }
.perm-page__btn--disabled {
  background: #e2e8f0 !important; border-color: #e2e8f0 !important;
  color: #94a3b8 !important; cursor: not-allowed !important;
  transform: none !important; box-shadow: none !important;
}

/* Banner */
.perm-page__banner {
  display: flex; align-items: flex-start; gap: 12px;
  background: linear-gradient(to right, #fffbeb, #fef3c7);
  border: 1px solid #fcd34d; border-radius: 12px;
  padding: 16px 20px; margin-bottom: 1.5rem;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}
.perm-page__banner-icon { color: #d97706; font-size: 20px; flex-shrink: 0; margin-top: 2px; }
.perm-page__banner-text { font-size: 0.875rem; color: #92400e; line-height: 1.6; }
.perm-page__banner-text strong { font-weight: 700; color: #78350f; }

/* Legend */
.perm-page__legend {
  display: flex; align-items: center; gap: 1.5rem;
  margin-top: 1.25rem; font-size: 0.875rem; color: #475569; font-weight: 500;
  flex-wrap: wrap;
}
.legend-item { display: flex; align-items: center; gap: 8px; }
.legend-dot {
  width: 32px; height: 18px; border-radius: 9px; flex-shrink: 0;
  box-shadow: inset 0 1px 2px rgba(0,0,0,0.1);
}
.legend-dot--on     { background: #10b981; }
.legend-dot--off    { background: #cbd5e1; }
.legend-dot--locked { background: #10b981; opacity: 0.6; }

.perm-page__change-counter {
  margin-left: auto; font-weight: 600;
  padding: 6px 16px; background: #fff;
  border-radius: 20px; border: 1px solid #cbd5e1;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
  display: flex; align-items: center; gap: 8px;
  color: #94a3b8; font-size: 0.875rem;
}
.perm-page__change-counter--dirty {
  color: #b91c1c; border-color: #fecaca; background: #fef2f2;
}
.perm-page__change-dot {
  width: 8px; height: 8px; border-radius: 50%;
  background: #963131; box-shadow: 0 0 0 2px #ffebee; flex-shrink: 0;
}
</style>
