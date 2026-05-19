<template>
  <div class="pfd-wrap">
    <div class="pfd-header">
      <div class="pfd-header-left">
        <div class="pfd-icon-wrap">
          <span class="material-symbols-outlined">rule_folder</span>
        </div>
        <div>
          <h3 class="pfd-title">{{ action.label }}</h3>
          <div class="pfd-code-row">
            <span class="pfd-code-label">Action ID</span>
            <code class="pfd-code">{{ action.code }}</code>
          </div>
        </div>
      </div>
      <button class="pfd-edit-btn">Chỉnh sửa mô tả</button>
    </div>

    <div class="pfd-body">
      <!-- Default roles -->
      <section class="pfd-section">
        <h4 class="pfd-section-title">
          <span class="material-symbols-outlined">shield</span>
          Vai trò mặc định có quyền này
        </h4>
        <div class="pfd-roles-row">
          <div v-for="r in action.defaultRoles" :key="r" :class="['pfd-role-chip', `chip-${r}`]">
            <span class="material-symbols-outlined">{{ roleIcon[r] }}</span>
            <span>{{ roleLabel[r] }}</span>
          </div>
        </div>
        <p class="pfd-hint">
          Các vai trò trên mặc định có quyền
          <code class="pfd-inline-code">{{ action.code }}</code>.
          Điều chỉnh ở tab "Theo vai trò" nếu muốn thay đổi.
        </p>
      </section>

      <!-- Members table -->
      <section class="pfd-section">
        <div class="pfd-section-header">
          <h4 class="pfd-section-title">
            <span class="material-symbols-outlined">group</span>
            Thành viên được phép thực thi
          </h4>
          <div class="pfd-section-controls">
            <div class="pfd-member-search-wrap">
              <span class="material-symbols-outlined pfd-member-search-icon">search</span>
              <input v-model="memberSearch" type="text" class="pfd-member-search" placeholder="Tìm thành viên..." />
            </div>
            <button class="pfd-grant-btn">
              <span class="material-symbols-outlined">add</span> Cấp thêm
            </button>
          </div>
        </div>

        <div class="pfd-table-wrap">
          <table class="pfd-table">
            <thead>
              <tr>
                <th>Thành viên</th>
                <th>Vai trò</th>
                <th>Nguồn quyền</th>
                <th class="text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="m in filteredMembers" :key="m.id"
                :class="m.source === 'granted' ? 'pfd-row-granted' : ''">
                <td>
                  <div class="pfd-member-cell">
                    <img v-if="m.avatar" :src="m.avatar" :alt="m.name" class="pfd-member-avatar" />
                    <div v-else class="pfd-member-initials">{{ m.initials }}</div>
                    <div>
                      <div class="pfd-member-name">{{ m.name }}</div>
                      <div class="pfd-member-email">{{ m.email }}</div>
                    </div>
                  </div>
                </td>
                <td><span :class="['pfd-role-badge', `badge-${m.role}`]">{{ roleLabel[m.role] }}</span></td>
                <td>
                  <div :class="['pfd-source', `source-${m.source}`]">
                    <span class="material-symbols-outlined">{{ sourceIcon[m.source] }}</span>
                    <span>{{ sourceLabel[m.source] }}</span>
                  </div>
                </td>
                <td class="text-right">
                  <button v-if="m.source === 'granted'" class="pfd-revoke-btn">
                    <span class="material-symbols-outlined">person_remove</span>
                  </button>
                  <button v-else class="pfd-more-btn">
                    <span class="material-symbols-outlined">more_vert</span>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface ActionMember {
  id: string; name: string; email: string; role: string
  avatar?: string; initials?: string; source: 'role' | 'granted'
}
interface ActionDetail {
  label: string; code: string; defaultRoles: string[]; members: ActionMember[]
}

const props = defineProps<{ action: ActionDetail }>()
const memberSearch = ref('')

const roleLabel: Record<string, string> = { owner: 'Owner', manager: 'Manager', recruiter: 'Recruiter', viewer: 'Viewer' }
const roleIcon:  Record<string, string> = { owner: 'stars', manager: 'manage_accounts', recruiter: 'person', viewer: 'visibility' }
const sourceLabel: Record<string, string> = { role: 'Mặc định (theo vai trò)', granted: 'Được cấp thêm trực tiếp' }
const sourceIcon:  Record<string, string> = { role: 'verified_user', granted: 'add_circle' }

const filteredMembers = computed(() => {
  if (!memberSearch.value) return props.action.members
  const q = memberSearch.value.toLowerCase()
  return props.action.members.filter(m => m.name.toLowerCase().includes(q) || m.email.toLowerCase().includes(q))
})
</script>

<style scoped>
.pfd-wrap { display: flex; flex-direction: column; height: 100%; overflow: hidden; }
.pfd-header {
  padding: 24px; border-bottom: 1px solid #e2e8f0;
  background: #f8fafc; flex-shrink: 0;
  display: flex; align-items: flex-start; justify-content: space-between; gap: 16px;
}
.pfd-header-left { display: flex; align-items: flex-start; gap: 16px; }
.pfd-icon-wrap {
  width: 40px; height: 40px; border-radius: 10px;
  background: rgba(75,154,246,0.1);
  display: flex; align-items: center; justify-content: center;
  color: #4B9AF6; flex-shrink: 0;
}
.pfd-title { font-size: 20px; font-weight: 800; color: #0f172a; margin-bottom: 6px; }
.pfd-code-row { display: flex; align-items: center; gap: 8px; }
.pfd-code-label {
  font-size: 11px; font-weight: 600; text-transform: uppercase;
  letter-spacing: 0.05em; color: #64748b;
  background: #f1f5f9; padding: 2px 8px; border-radius: 4px;
}
.pfd-code {
  font-size: 13px; font-family: 'Courier New', monospace;
  color: #4B9AF6; background: rgba(75,154,246,0.05);
  padding: 2px 8px; border-radius: 4px;
  border: 1px solid rgba(75,154,246,0.1);
}
.pfd-edit-btn {
  padding: 8px 16px; background: #fff;
  border: 1px solid #e2e8f0; border-radius: 8px;
  font-size: 13px; font-weight: 600; color: #0f172a;
  cursor: pointer; font-family: 'Manrope', sans-serif;
  white-space: nowrap; transition: background 0.15s;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}
.pfd-edit-btn:hover { background: #f1f5f9; }

.pfd-body { flex: 1; overflow-y: auto; padding: 24px; display: flex; flex-direction: column; gap: 32px; }
.pfd-section { display: flex; flex-direction: column; gap: 12px; }
.pfd-section-header { display: flex; align-items: center; justify-content: space-between; }
.pfd-section-title {
  display: flex; align-items: center; gap: 8px;
  font-size: 15px; font-weight: 700; color: #0f172a;
}
.pfd-section-title .material-symbols-outlined { font-size: 20px; color: #4B9AF6; }
.pfd-section-controls { display: flex; align-items: center; gap: 8px; }

.pfd-roles-row { display: flex; gap: 12px; flex-wrap: wrap; }
.pfd-role-chip {
  display: flex; align-items: center; gap: 6px;
  padding: 6px 14px; border-radius: 8px;
  font-size: 13px; font-weight: 600;
}
.chip-owner   { background: #eff6ff; border: 1px solid #bfdbfe; color: #1d4ed8; }
.chip-manager { background: #f8fafc; border: 1px solid #e2e8f0; color: #475569; }
.chip-recruiter{ background: #e0f2fe; border: 1px solid #bae6fd; color: #0369a1; }
.chip-viewer  { background: #f1f5f9; border: 1px solid #e2e8f0; color: #64748b; }
.pfd-role-chip .material-symbols-outlined { font-size: 16px; }
.pfd-hint { font-size: 12px; color: #64748b; }
.pfd-inline-code {
  font-family: 'Courier New', monospace; font-size: 12px;
  background: #f1f5f9; padding: 1px 6px; border-radius: 4px;
}

.pfd-member-search-wrap { position: relative; }
.pfd-member-search-icon { position: absolute; left: 10px; top: 50%; transform: translateY(-50%); font-size: 18px; color: #64748b; }
.pfd-member-search {
  padding: 6px 12px 6px 32px; border: 1px solid #e2e8f0;
  border-radius: 6px; font-size: 13px; width: 192px;
  outline: none; font-family: 'Manrope', sans-serif;
  transition: border 0.2s;
}
.pfd-member-search:focus { border-color: #4B9AF6; box-shadow: 0 0 0 1px #4B9AF6; }
.pfd-grant-btn {
  display: flex; align-items: center; gap: 4px;
  padding: 6px 14px; background: #4B9AF6; color: #fff;
  border: none; border-radius: 6px; font-size: 13px; font-weight: 600;
  cursor: pointer; font-family: 'Manrope', sans-serif;
  transition: background 0.15s;
}
.pfd-grant-btn:hover { background: #3b82f6; }
.pfd-grant-btn .material-symbols-outlined { font-size: 18px; }

.pfd-table-wrap {
  border: 1px solid rgba(226,232,240,0.5);
  border-radius: 12px; overflow: hidden;
  background: #fff;
}
.pfd-table { width: 100%; border-collapse: collapse; font-size: 13px; text-align: left; }
.pfd-table thead { background: #f8fafc; border-bottom: 1px solid rgba(226,232,240,0.5); }
.pfd-table th { padding: 12px 16px; font-weight: 500; font-size: 12px; color: #64748b; }
.pfd-table td { padding: 12px 16px; border-bottom: 1px solid #f8fafc; vertical-align: middle; }
.pfd-table tbody tr:last-child td { border-bottom: none; }
.pfd-table tbody tr { transition: background 0.15s; }
.pfd-table tbody tr:hover { background: rgba(248,250,253,0.5); }
.pfd-row-granted {
  background: rgba(75,154,246,0.05) !important;
  border-left: 2px solid #4B9AF6;
}
.pfd-row-granted:hover { background: rgba(75,154,246,0.08) !important; }

.pfd-member-cell { display: flex; align-items: center; gap: 12px; }
.pfd-member-avatar { width: 32px; height: 32px; border-radius: 50%; object-fit: cover; }
.pfd-member-initials {
  width: 32px; height: 32px; border-radius: 50%;
  background: #f1f5f9; color: #64748b;
  display: flex; align-items: center; justify-content: center;
  font-size: 11px; font-weight: 700;
}
.pfd-member-name  { font-size: 13px; font-weight: 600; color: #0f172a; }
.pfd-member-email { font-size: 11px; color: #64748b; margin-top: 1px; }

.pfd-role-badge {
  font-size: 11px; font-weight: 600; padding: 3px 8px; border-radius: 4px;
}
.badge-owner    { background: #f1f5f9; color: #475569; }
.badge-manager  { background: #f1f5f9; color: #475569; }
.badge-recruiter{ background: #f1f5f9; color: #475569; }
.badge-viewer   { background: #f1f5f9; color: #475569; }

.pfd-source { display: flex; align-items: center; gap: 6px; font-size: 12px; }
.pfd-source .material-symbols-outlined { font-size: 16px; }
.source-role    { color: #64748b; }
.source-granted { color: #4B9AF6; font-weight: 600; }

.pfd-revoke-btn {
  padding: 4px; background: transparent; border: none; border-radius: 6px;
  cursor: pointer; color: #ef4444; transition: background 0.15s;
}
.pfd-revoke-btn:hover { background: rgba(239,68,68,0.1); }
.pfd-more-btn {
  padding: 4px; background: transparent; border: none; border-radius: 6px;
  cursor: pointer; color: #94a3b8; transition: color 0.15s;
}
.pfd-more-btn:hover { color: #64748b; }
.pfd-revoke-btn .material-symbols-outlined,
.pfd-more-btn .material-symbols-outlined { font-size: 18px; }
.text-right { text-align: right; }
</style>