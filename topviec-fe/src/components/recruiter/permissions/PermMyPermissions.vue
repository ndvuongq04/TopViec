<template>
  <div class="pmp-page">
    <!-- Loading skeleton -->
    <div v-if="store.loading && !store.myPermissions" class="pmp-loading">
      <span class="material-symbols-outlined pmp-spin">progress_activity</span>
    </div>

    <template v-else-if="currentUser">
    <!-- User header -->
    <div class="pmp-user-card">
      <div class="pmp-user-card-bg" />
      <div class="pmp-avatar-wrap">
        <div class="pmp-avatar-initials">{{ currentUser.initials }}</div>
        <div class="pmp-verified">
          <span class="material-symbols-outlined">verified</span>
        </div>
      </div>
      <div class="pmp-user-info">
        <div class="pmp-user-name-row">
          <h2 class="pmp-user-name">{{ currentUser.displayName }}</h2>
          <span :class="['pmp-role-badge', `badge-${currentUser.role}`]">{{ roleLabel[currentUser.role] }}</span>
        </div>
        <p class="pmp-user-email">{{ currentUser.email }}</p>
        <p v-if="currentUser.description" class="pmp-user-desc">{{ currentUser.description }}</p>
      </div>
    </div>

    <div class="pmp-section-title-wrap">
      <h3 class="pmp-section-title">Quyền của tôi</h3>
      <p class="pmp-section-desc">Xem chi tiết các quyền hạn được cấp trên hệ thống dựa theo vai trò hiện tại.</p>
    </div>

    <!-- Bento grid -->
    <div class="pmp-grid">
      <div v-for="group in permGroups" :key="group.label"
        :class="['pmp-card', group.highlight ? 'pmp-card--highlight' : '']">
        <div class="pmp-card-header">
          <div :class="['pmp-card-icon', group.highlight ? 'pmp-card-icon--highlight' : '']">
            <span class="material-symbols-outlined">{{ group.icon }}</span>
          </div>
          <h4 class="pmp-card-title">{{ group.label }}</h4>
        </div>
        <ul class="pmp-perm-list">
          <li v-for="perm in group.perms" :key="perm.label"
            :class="['pmp-perm-item', perm.source === 'granted' ? 'pmp-perm-item--granted' : '', perm.source === 'denied' ? 'pmp-perm-item--denied' : '']">
            <span :class="['material-symbols-outlined pmp-perm-icon', sourceIconClass[perm.source]]">
              {{ sourceIcon[perm.source] }}
            </span>
            <div>
              <p :class="['pmp-perm-label', perm.source === 'granted' ? 'pmp-perm-label--granted' : '', perm.source === 'denied' ? 'pmp-perm-label--denied' : '']">
                {{ perm.label }}
              </p>
              <span :class="['pmp-perm-tag', `tag-${perm.source}`]">{{ sourceTag[perm.source] }}</span>
            </div>
          </li>
        </ul>
      </div>
    </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useEmployerMemberStore } from '@/stores/employerMember.store'

const store = useEmployerMemberStore()
onMounted(() => store.getMyPermissions())

const roleLabel: Record<string, string> = { owner:'Owner', manager:'Manager', recruiter:'Recruiter', viewer:'Viewer' }

const sourceIcon:      Record<string, string> = { role:'check_circle', granted:'add_circle', denied:'lock' }
const sourceIconClass: Record<string, string> = { role:'pmp-icon-role', granted:'pmp-icon-granted', denied:'pmp-icon-denied' }
const sourceTag:       Record<string, string> = { role:'Theo vai trò', granted:'Được cấp thêm', denied:'Bị giới hạn' }

const GROUP_META: Record<string, { label: string; icon: string; highlight?: boolean }> = {
  cv:        { label: 'Quản lý Hồ sơ ứng tuyển', icon: 'description' },
  job:       { label: 'Quản lý Tin tuyển dụng',   icon: 'work' },
  talent:    { label: 'Quản lý Ứng viên',          icon: 'person', highlight: true },
  interview: { label: 'Quản lý Phỏng vấn',         icon: 'event_upcoming' },
  member:    { label: 'Quản lý Thành viên',         icon: 'group' },
  report:    { label: 'Báo cáo & Thống kê',         icon: 'bar_chart' },
}

const currentUser = computed(() => {
  const p = store.myPermissions
  if (!p) return null
  const namePart = p.email.split('@')[0]
  return {
    displayName: namePart,
    email: p.email,
    role: p.roleName as string,
    initials: namePart.split(/[._-]/).map(s => s[0]?.toUpperCase() ?? '').join('').slice(0, 2),
    description: p.jobTitle ?? '',
  }
})

const permGroups = computed(() => {
  const p = store.myPermissions
  if (!p) return []

  const custom = p.customPermissions ?? {}
  const groupMap = new Map<string, { label: string; icon: string; highlight: boolean; perms: { label: string; source: string }[] }>()

  for (const item of p.effectivePermissions) {
    const groupKey = item.code.split(':')[0]

    let source: 'role' | 'granted' | 'denied'
    if (item.code in custom) {
      source = custom[item.code] ? 'granted' : 'denied'
    } else {
      if (!item.enabled) continue
      source = 'role'
    }

    if (!groupMap.has(groupKey)) {
      const meta = GROUP_META[groupKey] ?? { label: groupKey, icon: 'settings' }
      groupMap.set(groupKey, { label: meta.label, icon: meta.icon, highlight: meta.highlight ?? false, perms: [] })
    }
    groupMap.get(groupKey)!.perms.push({ label: item.name, source })
  }

  return [...groupMap.values()].filter(g => g.perms.length > 0)
})
</script>

<style scoped>
.pmp-page { overflow-y: auto; display: flex; flex-direction: column; gap: 24px; }

.pmp-loading { display: flex; justify-content: center; align-items: center; padding: 4rem; }
.pmp-spin { font-size: 36px; color: #4B9AF6; animation: pmp-spin 0.8s linear infinite; }
@keyframes pmp-spin { to { transform: rotate(360deg); } }

/* User card */
.pmp-user-card {
  background: #fff; border-radius: 12px; padding: 32px;
  display: flex; align-items: center; gap: 24px;
  position: relative; overflow: hidden;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}
.pmp-user-card-bg {
  position: absolute; top: 0; left: 0; right: 0; height: 96px;
  background: linear-gradient(to right, #3b82f6, #2563eb); opacity: 0.1;
}
.pmp-avatar-wrap { position: relative; z-index: 1; flex-shrink: 0; }
.pmp-avatar { width: 96px; height: 96px; border-radius: 50%; object-fit: cover; border: 4px solid #fff; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.pmp-avatar-initials {
  width: 96px; height: 96px; border-radius: 50%;
  background: #4B9AF6; color: #fff;
  display: flex; align-items: center; justify-content: center;
  font-size: 28px; font-weight: 700;
  border: 4px solid #fff;
}
.pmp-verified {
  position: absolute; bottom: 2px; right: 2px;
  background: #fff; border-radius: 50%; padding: 2px;
}
.pmp-verified .material-symbols-outlined { color: #4B9AF6; font-size: 22px; display: block; }

.pmp-user-info { flex: 1; z-index: 1; }
.pmp-user-name-row { display: flex; align-items: center; gap: 12px; margin-bottom: 4px; }
.pmp-user-name { font-size: 1.875rem; font-weight: 800; color: #0f172a; }
.pmp-role-badge { font-size: 0.75rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.05em; padding: 4px 12px; border-radius: 9999px; }
.badge-recruiter { background: rgba(75,154,246,0.1); color: #4B9AF6; border: 1px solid rgba(75,154,246,0.2); }
.badge-owner     { background: #fef3c7; color: #b45309; }
.badge-manager   { background: #f3e8ff; color: #7e22ce; }
.badge-viewer    { background: #f1f5f9; color: #64748b; }
.pmp-user-email { font-size: 0.875rem; color: #64748b; font-weight: 500; margin-bottom: 8px; }
.pmp-user-desc  { font-size: 0.875rem; color: #64748b; max-width: 560px; }

.pmp-request-btn {
  display: flex; align-items: center; gap: 8px;
  padding: 10px 20px; background: #4B9AF6; color: #fff;
  border: none; border-radius: 8px; font-size: 1rem; font-weight: 600;
  cursor: pointer; font-family: 'Manrope', sans-serif;
  box-shadow: 0 4px 12px rgba(75,154,246,0.3);
  transition: all 0.2s; z-index: 1; align-self: flex-start;
}
.pmp-request-btn:hover { background: #3b82f6; }
.pmp-request-btn .material-symbols-outlined { font-size: 18px; }

.pmp-section-title-wrap { margin-top: 8px; }
.pmp-section-title { font-size: 1.375rem; font-weight: 800; color: #0f172a; margin-bottom: 4px; }
.pmp-section-desc  { font-size: 0.875rem; color: #64748b; }

/* Bento grid */
.pmp-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }
@media (max-width: 1024px) { .pmp-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 640px)  { .pmp-grid { grid-template-columns: 1fr; } }

.pmp-card {
  background: #fff; border-radius: 12px; padding: 24px;
  display: flex; flex-direction: column; gap: 16px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}
.pmp-card--highlight {
  border: 1px solid rgba(75,154,246,0.2);
  position: relative; overflow: hidden;
}
.pmp-card--highlight::before {
  content: '';
  position: absolute; top: 0; right: 0;
  width: 96px; height: 96px;
  background: rgba(75,154,246,0.05);
  border-radius: 0 0 0 100%;
}
.pmp-card-header { display: flex; align-items: center; gap: 12px; }
.pmp-card-icon {
  width: 40px; height: 40px; border-radius: 10px;
  background: #f8fafc; display: flex; align-items: center; justify-content: center;
  color: #4B9AF6;
}
.pmp-card-icon--highlight { background: rgba(75,154,246,0.1); }
.pmp-card-title { font-size: 16px; font-weight: 700; color: #0f172a; }

.pmp-perm-list { display: flex; flex-direction: column; gap: 12px; margin: 0; padding: 0; list-style: none; }
.pmp-perm-item { display: flex; align-items: flex-start; gap: 12px; }
.pmp-perm-item--granted {
  background: #f8fafc; padding: 8px; margin: 0 -8px;
  border-radius: 8px;
}
.pmp-perm-item--denied { opacity: 0.6; }
.pmp-perm-icon { font-size: 20px; margin-top: 2px; }
.pmp-icon-role    { color: #10b981; }
.pmp-icon-granted { color: #4B9AF6; }
.pmp-icon-denied  { color: #94a3b8; }
.pmp-perm-label { font-size: 0.875rem; font-weight: 600; color: #0f172a; margin-bottom: 4px; }
.pmp-perm-label--granted { font-weight: 800; color: #4B9AF6; }
.pmp-perm-label--denied  { color: #94a3b8; font-weight: 500; }
.pmp-perm-tag {
  display: inline-block; font-size: 0.75rem; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.05em;
  padding: 2px 8px; border-radius: 4px;
}
.tag-role    { background: #f1f5f9; color: #64748b; }
.tag-granted { background: rgba(75,154,246,0.1); color: #4B9AF6; border: 1px solid rgba(75,154,246,0.2); }
.tag-denied  { background: #fef3c7; color: #b45309; border: 1px solid #fde68a; }
</style>