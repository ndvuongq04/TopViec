<template>
  <div class="perm-table-wrap">
    <table class="perm-table">
      <thead>
        <tr class="perm-table__head-row">
          <th class="perm-table__th perm-table__th--feature">Quyền hạn</th>
          <th v-for="role in roles" :key="role.key" class="perm-table__th perm-table__th--role">
            <div class="role-chip">
              <span class="role-chip__name">{{ role.name }}</span>
              <span :class="['role-chip__tag', `role-chip__tag--${role.key}`]">{{ role.label }}</span>
            </div>
          </th>
        </tr>
      </thead>
      <tbody>
        <template v-for="group in groups" :key="group.id">
          <PermissionGroupRow
            :group="group"
            :collapsed="collapsed[group.id]"
            @toggle="collapsed[group.id] = !collapsed[group.id]"
          />
          <PermissionRow
            v-for="perm in group.perms"
            v-show="!collapsed[group.id]"
            :key="perm.id"
            :perm="perm"
            :state="state"
            @update="onUpdate"
            @rename="$emit('rename', $event)"
          />
        </template>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import PermissionGroupRow from './PermissionGroupRow.vue'
import PermissionRow from './PermissionRow.vue'

const props = defineProps<{ groups: any[]; state: Record<string, boolean> }>()
const emit = defineEmits(['update:state', 'rename', 'toggle'])

const collapsed = reactive<Record<string, boolean>>(
  props.groups.reduce((acc, g) => ({ ...acc, [g.id]: true }), {})
)

const roles = [
  { key: 'owner',     name: 'Owner',     label: 'Chủ sở hữu' },
  { key: 'm',         name: 'Manager',   label: 'Quản lý'     },
  { key: 'r',         name: 'Recruiter', label: 'Tuyển dụng'  },
  { key: 'v',         name: 'Viewer',    label: 'Xem'         },
]

const onUpdate = (permId: string, role: string, val: boolean) => {
  props.state[`${permId}|${role}`] = val
  emit('toggle', { permId, role, val })
}
</script>

<style scoped>
.perm-table-wrap {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 12px; overflow: hidden;
}
.perm-table { width: 100%; border-collapse: collapse; font-size: 0.875rem; }

.perm-table__head-row { position: sticky; top: 0; z-index: 10; }
.perm-table__th {
  background: rgba(248, 250, 252, 0.5);
  border-bottom: 1px solid #e2e8f0;
  padding: 1rem;
}
.perm-table__th--feature {
  width: 340px; text-align: left; padding: 1rem 1.5rem;
  font-size: 0.75rem; font-weight: 700; color: #475569;
  text-transform: uppercase; letter-spacing: 0.06em;
}
.perm-table__th--role { text-align: center; min-width: 120px; }

.role-chip { display: inline-flex; flex-direction: column; align-items: center; gap: 6px; }
.role-chip__name { font-size: 0.875rem; font-weight: 700; color: #0f172a; }
.role-chip__tag {
  font-size: 0.75rem; padding: 4px 10px; border-radius: 12px; font-weight: 600; letter-spacing: 0.01em;
}
.role-chip__tag--owner    { background: #fee2e2; color: #991b1b; border: 1px solid #fecaca; }
.role-chip__tag--m        { background: #dbeafe; color: #1e40af; border: 1px solid #bfdbfe; }
.role-chip__tag--r        { background: #d1fae5; color: #065f46; border: 1px solid #a7f3d0; }
.role-chip__tag--v        { background: #f1f5f9; color: #475569; border: 1px solid #e2e8f0; }
</style>