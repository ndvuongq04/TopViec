<template>
  <div class="log-card" :class="typeClass">
    <!-- Accent bar -->
    <div class="accent-bar" />

    <div class="card-inner">
      <!-- ── Header ─────────────────────────────────── -->
      <div class="log-header">
        <div class="avatar" :class="log.avatarClass">{{ log.initials }}</div>

        <div class="log-user">
          <span class="user-name">{{ log.name }}</span>
          <span class="user-email">{{ log.email }}</span>
        </div>

        <span class="badge" :class="typeClass">
          <span class="material-symbols-outlined badge-icon">
            {{ log.type === 'role_change' ? 'manage_accounts' : 'tune' }}
          </span>
          {{ log.type === 'role_change' ? 'Đổi vai trò' : 'Cập nhật quyền' }}
        </span>

        <span class="log-time">
          <span class="material-symbols-outlined time-icon">schedule</span>
          {{ log.time }}
        </span>
      </div>

      <!-- ── Body ──────────────────────────────────── -->
      <div class="log-body">
        <PermissionRoleChange
          v-if="log.oldRole && log.newRole"
          :old-role="log.oldRole"
          :new-role="log.newRole"
        />

        <PermissionChips
          v-if="log.addedPerms.length || log.removedPerms.length"
          :added="log.addedPerms"
          :removed="log.removedPerms"
        />
      </div>

      <!-- ── Footer ────────────────────────────────── -->
      <div class="log-footer">
        <div class="footer-actor">
          <span class="material-symbols-outlined footer-icon">person</span>
          <span class="footer-label">Thực hiện bởi</span>
          <span class="footer-value">{{ log.changedBy }}</span>
        </div>

        <div v-if="log.reason" class="footer-reason">
          <span class="material-symbols-outlined footer-icon">chat_bubble</span>
          <span class="footer-value reason-text">{{ log.reason }}</span>
        </div>

        <div class="footer-created-at">
          <span class="material-symbols-outlined footer-created-icon">event</span>
          <span class="footer-created-label">Ngày tạo</span>
          <span class="footer-created-value">{{ log.createdDate }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import PermissionRoleChange from './PermissionRoleChange.vue'
import PermissionChips from './PermissionChips.vue'

const props = defineProps<{
  log: {
    id: number
    type: string
    name: string
    email: string
    initials: string
    avatarClass: string
    oldRole: string | null
    newRole: string | null
    addedPerms: string[]
    removedPerms: string[]
    changedBy: string
    time: string
    createdDate: string
    reason: string | null
  }
}>()

const typeClass = computed(() =>
  props.log.type === 'role_change' ? 'type-role' : 'type-perm',
)
</script>

<style scoped>
.log-card {
  display: flex;
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  transition: box-shadow 0.2s, border-color 0.2s;
  font-family: 'Manrope', sans-serif;
}
.log-card:hover {
  border-color: #CBD5E1;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.07);
}

/* Accent bar */
.accent-bar {
  width: 4px;
  flex-shrink: 0;
}
.type-role .accent-bar { background: #3B82F6; }
.type-perm .accent-bar { background: #F59E0B; }

.card-inner {
  flex: 1;
  min-width: 0;
  padding: 18px 20px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

/* ── Header ── */
.log-header {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-weight: 700;
  flex-shrink: 0;
  box-shadow: 0 0 0 2px #fff, 0 0 0 3px #E2E8F0;
}
.av1 { background: #DBEAFE; color: #1E40AF; }
.av2 { background: #D1FAE5; color: #065F46; }
.av3 { background: #EDE9FE; color: #5B21B6; }
.av4 { background: #FFEDD5; color: #9A3412; }

.log-user {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 1px;
}
.user-name {
  font-size: 0.9375rem;
  font-weight: 700;
  color: #0F172A;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.user-email {
  font-size: 0.8125rem;
  color: #64748B;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.badge {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 0.75rem;
  font-weight: 700;
  padding: 4px 10px;
  border-radius: 20px;
  white-space: nowrap;
}
.badge-icon { font-size: 14px; }
.type-role .badge { background: #EFF6FF; color: #1D4ED8; border: 1px solid #BFDBFE; }
.type-perm .badge { background: #FFFBEB; color: #B45309; border: 1px solid #FDE68A; }

.log-time {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 0.8125rem;
  color: #94A3B8;
  white-space: nowrap;
}
.time-icon { font-size: 14px; }

/* ── Body ── */
.log-body {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* ── Footer ── */
.log-footer {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding-top: 12px;
  border-top: 1px solid #F1F5F9;
}

.footer-actor,
.footer-reason {
  display: flex;
  align-items: baseline;
  gap: 6px;
  font-size: 0.8125rem;
}
.footer-icon {
  font-size: 14px;
  color: #94A3B8;
  flex-shrink: 0;
  position: relative;
  top: 2px;
}
.footer-label {
  color: #94A3B8;
  white-space: nowrap;
  flex-shrink: 0;
}
.footer-label::after { content: ':'; }
.footer-value {
  color: #334155;
  font-weight: 600;
}
.reason-text {
  font-weight: 400;
  color: #475569;
}

.footer-created-at {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  align-self: flex-end;
  margin-top: 4px;
  color: #94A3B8;
  font-size: 0.75rem;
  white-space: nowrap;
}

.footer-created-icon {
  font-size: 14px;
}

.footer-created-label::after {
  content: ':';
}

.footer-created-value {
  color: #64748B;
  font-weight: 700;
}

@media (max-width: 600px) {
  .log-time { width: 100%; }
  .badge { margin-left: auto; }
  .footer-created-at { align-self: flex-start; }
}
</style>
