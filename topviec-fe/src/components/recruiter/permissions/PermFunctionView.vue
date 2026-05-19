<template>
  <div class="pfv-layout">
    <!-- Left: accordion list -->
    <div class="pfv-left">
      <div class="pfv-search-wrap">
        <span class="material-symbols-outlined pfv-search-icon">search</span>
        <input v-model="search" type="text" class="pfv-search-input" placeholder="Tìm chức năng, mã action..." />
      </div>
      <div class="pfv-list">
        <div v-for="group in filteredGroups" :key="group.label" class="pfv-group">
          <button class="pfv-group-btn" @click="group.open = !group.open">
            <div class="pfv-group-btn-left">
              <span class="material-symbols-outlined pfv-folder-icon">folder</span>
              <span class="pfv-group-name">{{ group.label }}</span>
            </div>
            <span class="material-symbols-outlined pfv-chevron">
              {{ group.open ? 'expand_more' : 'chevron_right' }}
            </span>
          </button>
          <div v-if="group.open" class="pfv-actions">
            <button
              v-for="action in group.actions" :key="action.code"
              :class="['pfv-action-btn', selectedAction?.code === action.code ? 'selected' : '']"
              @click="selectedAction = action"
            >
              <span class="pfv-action-label">{{ action.label }}</span>
              <code v-if="selectedAction?.code === action.code" class="pfv-action-code">{{ action.code }}</code>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Right: detail -->
    <div class="pfv-right">
      <PermFunctionDetail v-if="selectedAction" :action="selectedAction" />
      <div v-else class="pfv-empty">
        <span class="material-symbols-outlined pfv-empty-icon">rule_folder</span>
        <p>Chọn một chức năng để xem chi tiết</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import PermFunctionDetail from './PermFunctionDetail.vue'

const search = ref('')
const selectedAction = ref<any>(null)

const groups = reactive([
  { label: 'Tuyển dụng (Recruitment)', open: true, actions: [
    {
      label: 'Tạo tin tuyển dụng', code: 'job:create',
      defaultRoles: ['owner', 'manager', 'recruiter'],
      members: [
        { id:'1', name:'Trần Giám Đốc', email:'director@horizon.com', role:'owner', source:'role', initials:'TG' },
        { id:'2', name:'Lê Quản Lý',    email:'le.manager@horizon.com', role:'manager', source:'role', initials:'LQ' },
      ]
    },
    {
      label: 'Duyệt CV', code: 'cv:approve',
      defaultRoles: ['owner', 'manager'],
      members: [
        { id:'1', name:'Trần Giám Đốc', email:'director@horizon.com', role:'owner',   source:'role',    initials:'TG' },
        { id:'3', name:'Hoa Nguyễn',    email:'hoa.recruiter@gmail.com', role:'viewer', source:'granted', initials:'HN' },
        { id:'2', name:'Lê Quản Lý',    email:'le.manager@horizon.com', role:'manager', source:'role',   initials:'LQ' },
      ]
    },
  ]},
  { label: 'Ứng viên (Candidates)', open: false, actions: [
    { label: 'Xem hồ sơ ứng viên', code: 'candidate:view', defaultRoles: ['owner','manager','recruiter','viewer'], members: [] },
    { label: 'Xuất danh sách', code: 'candidate:export', defaultRoles: ['owner'], members: [] },
  ]},
  { label: 'Báo cáo (Reports)', open: false, actions: [
    { label: 'Xem báo cáo tổng hợp', code: 'report:view', defaultRoles: ['owner','manager'], members: [] },
  ]},
])

const filteredGroups = computed(() => {
  if (!search.value) return groups
  const q = search.value.toLowerCase()
  return groups.map(g => ({
    ...g,
    actions: g.actions.filter(a => a.label.toLowerCase().includes(q) || a.code.toLowerCase().includes(q))
  })).filter(g => g.actions.length > 0)
})
</script>

<style scoped>
.pfv-layout { display: flex; height: 100%; overflow: hidden; gap: 0; }
.pfv-left {
  width: 35%; flex-shrink: 0; display: flex; flex-direction: column;
  background: #fff; border-right: 1px solid rgba(226,232,240,0.3);
  overflow: hidden;
}
.pfv-search-wrap {
  padding: 16px; border-bottom: 1px solid rgba(226,232,240,0.5);
  background: #f8fafc; position: relative;
}
.pfv-search-icon { position: absolute; left: 28px; top: 50%; transform: translateY(-50%); font-size: 20px; color: #64748b; }
.pfv-search-input {
  width: 100%; padding: 8px 12px 8px 36px;
  border: 1px solid #e2e8f0; border-radius: 8px;
  background: #fff; font-size: 1rem;
  outline: none; font-family: 'Manrope', sans-serif;
  transition: all 0.2s; box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}
.pfv-search-input:focus { border-color: #4B9AF6; box-shadow: 0 0 0 1px #4B9AF6; }

.pfv-list { flex: 1; overflow-y: auto; padding: 8px; display: flex; flex-direction: column; gap: 4px; }
.pfv-group { display: flex; flex-direction: column; }
.pfv-group-btn {
  width: 100%; display: flex; align-items: center; justify-content: space-between;
  padding: 12px; border-radius: 8px; border: none; background: transparent;
  cursor: pointer; font-family: 'Manrope', sans-serif;
  transition: background 0.15s;
}
.pfv-group-btn:hover { background: #f8fafc; }
.pfv-group-btn-left { display: flex; align-items: center; gap: 8px; }
.pfv-folder-icon { font-size: 20px; color: #64748b; }
.pfv-group-name { font-size: 0.875rem; font-weight: 600; color: #0f172a; }
.pfv-chevron { font-size: 20px; color: #64748b; }

.pfv-actions { padding-left: 36px; padding-right: 12px; display: flex; flex-direction: column; gap: 2px; margin-bottom: 4px; }
.pfv-action-btn {
  width: 100%; padding: 8px 10px; border-radius: 8px;
  border: none; background: transparent; text-align: left;
  cursor: pointer; font-family: 'Manrope', sans-serif;
  transition: all 0.15s; display: flex; flex-direction: column; gap: 2px;
}
.pfv-action-btn:hover { background: #f8fafc; }
.pfv-action-btn.selected { background: rgba(75,154,246,0.1); border: 1px solid rgba(75,154,246,0.2); }
.pfv-action-label { font-size: 0.875rem; color: #0f172a; }
.pfv-action-btn.selected .pfv-action-label { font-weight: 700; color: #4B9AF6; }
.pfv-action-code { font-size: 0.75rem; font-family: 'Courier New', monospace; color: rgba(75,154,246,0.7); }

.pfv-right { flex: 1; overflow: hidden; background: #f8fafc; }
.pfv-empty {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  height: 100%; gap: 12px; color: #94a3b8;
}
.pfv-empty-icon { font-size: 64px; opacity: 0.3; }
.pfv-empty p { font-size: 0.875rem; font-weight: 500; }
</style>