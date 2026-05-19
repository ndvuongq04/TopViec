<template>
  <tr class="group-row" @click="$emit('toggle')">
    <td colspan="5" class="group-row__cell">
      <div class="group-row__inner">
        <div class="group-row__icon">
          <span class="material-symbols-outlined">{{ group.icon }}</span>
        </div>
        <span class="group-row__label">
          {{ group.label }}
          <span class="group-row__sub">({{ group.sub }})</span>
        </span>
        <span class="group-row__count">{{ group.perms.length }} quyền</span>
        <div :class="['group-row__collapse-indicator', collapsed && 'group-row__collapse-indicator--collapsed']">
          <span class="material-symbols-outlined">expand_more</span>
        </div>
      </div>
    </td>
  </tr>
</template>

<script setup lang="ts">
defineProps<{ group: any; collapsed: boolean }>()
defineEmits(['toggle'])
</script>

<style scoped>
.group-row { cursor: pointer; transition: background 0.2s ease; }
.group-row:hover .group-row__cell { background: #f1f5f9; }

.group-row__cell {
  background: #f8fafc; padding: 14px 24px;
  border-bottom: 1px solid #e2e8f0;
  border-top: 1px solid #e2e8f0;
  transition: background 0.2s ease;
}
.group-row__inner { display: flex; align-items: center; gap: 12px; }

.group-row__icon {
  width: 36px; height: 36px; border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0; color: #475569;
  background: #fff; border: 1px solid rgba(0,0,0,0.05);
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}
.group-row__icon .material-symbols-outlined { font-size: 20px; }

.group-row__label { font-size: 1.125rem; font-weight: 600; color: #0f172a; }
.group-row__sub { font-size: 0.875rem; color: #64748b; font-weight: 500; margin-left: 4px; }

.group-row__collapse-indicator {
  width: 32px; height: 32px; display: flex; align-items: center; justify-content: center;
  color: #64748b; margin-left: 8px;
  transition: color 0.2s; border-radius: 50%;
}
.group-row:hover .group-row__collapse-indicator { color: #0f172a; background: rgba(0,0,0,0.05); }
.group-row__collapse-indicator--collapsed .material-symbols-outlined { transform: rotate(-90deg); }
.group-row__collapse-indicator .material-symbols-outlined { font-size: 24px; transition: transform 0.2s ease; }

.group-row__count {
  margin-left: auto; font-size: 0.75rem; color: #475569; font-weight: 600;
  background: #fff; padding: 4px 12px; border-radius: 20px;
  border: 1px solid #cbd5e1; box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}
</style>