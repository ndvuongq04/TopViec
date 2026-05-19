<template>
  <div class="role-change">
    <div class="role-wrap">
      <span class="role-label">Từ</span>
      <span class="role-pill" :class="`role-${oldRole}`">{{ roleLabel[oldRole] ?? oldRole }}</span>
    </div>

    <span class="arrow material-symbols-outlined">arrow_forward</span>

    <div class="role-wrap">
      <span class="role-label">Sang</span>
      <span class="role-pill" :class="`role-${newRole}`">{{ roleLabel[newRole] ?? newRole }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ oldRole: string; newRole: string }>()

const roleLabel: Record<string, string> = {
  owner:     'Owner',
  manager:   'Manager',
  recruiter: 'Recruiter',
  viewer:    'Viewer',
}
</script>

<style scoped>
.role-change {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  background: #F8FAFC;
  border: 1px solid #E2E8F0;
  border-radius: 10px;
}

.role-wrap {
  display: flex;
  align-items: center;
  gap: 6px;
}

.role-label {
  font-size: 0.75rem;
  color: #94A3B8;
  font-weight: 600;
}

.role-pill {
  font-size: 0.8125rem;
  font-weight: 700;
  padding: 3px 12px;
  border-radius: 20px;
}

/* Role colors */
.role-owner {
  background: #FEE2E2;
  color: #991B1B;
  border: 1px solid #FECACA;
}
.role-manager {
  background: #DBEAFE;
  color: #1E40AF;
  border: 1px solid #BFDBFE;
}
.role-recruiter {
  background: #D1FAE5;
  color: #065F46;
  border: 1px solid #A7F3D0;
}
.role-viewer {
  background: #F1F5F9;
  color: #475569;
  border: 1px solid #CBD5E1;
}

.arrow {
  font-size: 18px;
  color: #94A3B8;
}
</style>
