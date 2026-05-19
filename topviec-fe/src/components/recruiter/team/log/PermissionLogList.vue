<template>
  <div class="log-list">
    <!-- Empty state -->
    <div v-if="!logs.length" class="empty-state">
      <div class="empty-icon">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor"
          stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
          <polyline points="14 2 14 8 20 8"/>
          <line x1="9" y1="15" x2="15" y2="15"/>
        </svg>
      </div>
      <p class="empty-title">Chưa có lịch sử thay đổi quyền nào</p>
      <p class="empty-sub">Các thay đổi phân quyền sẽ xuất hiện ở đây.</p>
    </div>

    <!-- Cards -->
    <PermissionLogCard
      v-for="log in logs"
      :key="log.id"
      :log="log"
    />
  </div>
</template>

<script setup lang="ts">
import PermissionLogCard from './PermissionLogCard.vue'

defineProps<{ logs: any[] }>()
</script>

<style scoped>
.log-list { display: flex; flex-direction: column; gap: 16px; }

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 64px 24px;
  text-align: center;
  background: #fff;
  border: 1px dashed #CBD5E1;
  border-radius: 16px;
}
.empty-icon { margin-bottom: 20px; color: #94A3B8; }
.empty-title { font-size: 1.125rem; font-weight: 700; color: #0F172A; margin-bottom: 8px; }
.empty-sub { font-size: 0.875rem; color: #64748B; }
</style>
