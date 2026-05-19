<template>
  <div :class="['pmc-col', alt ? 'pmc-col--alt' : '']">
    <div class="pmc-avatar-wrap">
      <img v-if="member.avatar" :src="member.avatar" :alt="member.name" class="pmc-avatar" />
      <div v-else class="pmc-avatar-initials">{{ member.initials }}</div>
      <div :class="['pmc-role-dot', `dot-${member.role}`]" />
    </div>
    <p class="pmc-name">{{ member.name }}</p>
    <p class="pmc-email">{{ member.email }}</p>
    <span :class="['pmc-badge', `badge-${member.role}`]">{{ roleLabel[member.role] }}</span>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  member: { id: string; name: string; email: string; role: string; avatar?: string; initials?: string }
  alt?: boolean
}>()

const roleLabel: Record<string, string> = {
  owner: 'Owner', manager: 'Manager', recruiter: 'Recruiter', viewer: 'Viewer'
}
</script>

<style scoped>
.pmc-col {
  width: 192px; flex-shrink: 0;
  padding: 16px; border-right: 1px solid rgba(226,232,240,0.5);
  display: flex; flex-direction: column;
  align-items: center; gap: 6px; text-align: center;
}
.pmc-col--alt { background: rgba(248,250,253,0.3); }
.pmc-avatar-wrap { position: relative; }
.pmc-avatar {
  width: 40px; height: 40px; border-radius: 50%;
  border: 2px solid #fff; object-fit: cover;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}
.pmc-avatar-initials {
  width: 40px; height: 40px; border-radius: 50%;
  background: #f1f5f9; color: #64748b;
  display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: 700;
  border: 2px solid #fff; box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}
.pmc-role-dot {
  position: absolute; bottom: -2px; right: -2px;
  width: 14px; height: 14px; border-radius: 50%; border: 2px solid #fff;
}
.dot-owner    { background: #f59e0b; }
.dot-manager  { background: #a855f7; }
.dot-recruiter{ background: #4B9AF6; }
.dot-viewer   { background: #94a3b8; }
.pmc-name  { font-size: 13px; font-weight: 700; color: #0f172a; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; width: 128px; }
.pmc-email { font-size: 11px; color: #64748b; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; width: 128px; }
.pmc-badge {
  font-size: 10px; font-weight: 700; text-transform: uppercase;
  letter-spacing: 0.05em; padding: 2px 8px; border-radius: 9999px;
}
.badge-owner    { background: #fef3c7; color: #b45309; }
.badge-manager  { background: #f3e8ff; color: #7e22ce; }
.badge-recruiter{ background: #e0f2fe; color: #0369a1; }
.badge-viewer   { background: #f1f5f9; color: #64748b; }
</style>