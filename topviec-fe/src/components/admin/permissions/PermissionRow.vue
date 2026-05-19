<template>
  <tr class="perm-row">
    <td class="perm-row__name-cell">
      <div class="perm-row__label">
        {{ perm.label }}
        <span class="perm-row__code">{{ perm.id }}</span>
        <button class="perm-row__rename-btn" title="Đổi tên" @click="$emit('rename', { id: perm.id, label: perm.label })">
          <span class="material-symbols-outlined">edit</span>
        </button>
      </div>
    </td>

    <!-- Owner: luôn bật, khóa -->
    <td class="perm-row__toggle-cell">
      <PermissionToggle :model-value="true" :locked="true" />
    </td>

    <!-- Manager, Recruiter, Viewer -->
    <td v-for="role in ['m','r','v']" :key="role" class="perm-row__toggle-cell">
      <PermissionToggle
        :model-value="state[`${perm.id}|${role}`]"
        @update:model-value="$emit('update', perm.id, role, $event)"
      />
    </td>
  </tr>
</template>

<script setup lang="ts">
import PermissionToggle from './PermissionToggle.vue'

defineProps<{ perm: any; state: Record<string, boolean> }>()
defineEmits(['update', 'rename'])
</script>

<style scoped>
.perm-row { transition: background 0.15s ease; }
.perm-row:not(:last-child) td { border-bottom: 1px solid #e2e8f0; }
.perm-row:hover td { background: #f8fafc; }

.perm-row__name-cell { padding: 14px 24px 14px 56px; }
.perm-row__label {
  display: flex; align-items: center; gap: 10px;
  font-size: 0.875rem; color: #0f172a; font-weight: 500;
}
.perm-row__code {
  font-size: 0.75rem; color: #475569; font-family: monospace;
  background: #f1f5f9; padding: 3px 8px; border-radius: 6px;
  margin-left: 4px; border: 1px solid #e2e8f0;
}
.perm-row__rename-btn {
  display: inline-flex; align-items: center; justify-content: center;
  width: 26px; height: 26px; border-radius: 6px; border: none;
  background: transparent; color: #94a3b8; cursor: pointer;
  transition: background 0.15s, color 0.15s;
  flex-shrink: 0;
}
.perm-row__rename-btn .material-symbols-outlined { font-size: 15px; }
.perm-row__rename-btn:hover { background: #e2e8f0; color: #475569; }

.perm-row__toggle-cell { text-align: center; padding: 14px 12px; vertical-align: middle; }
</style>