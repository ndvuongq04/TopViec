<template>
  <section class="rsg-section">
    <div class="rsg-header">
      <div class="rsg-header-left">
        <span class="rsg-badge" :style="{ background: group.badgeBg, color: group.badgeColor }">
          {{ group.label }}
        </span>
        <span class="rsg-count">{{ group.services.length }} dịch vụ đang hoạt động</span>
      </div>
      <button class="rsg-add-btn" :style="{ color: group.badgeColor }" @click="$emit('add', group.key)">
        <span class="material-symbols-outlined" style="font-size:18px">add</span>
        Thêm vào nhóm này
      </button>
    </div>
    <RetailServiceTable
      :services="group.services"
      :toggling-id="togglingId"
      @edit="$emit('edit', $event)"
      @toggle="$emit('toggle', $event)"
    />
  </section>
</template>

<script setup lang="ts">
import RetailServiceTable, { type RetailService } from './RetailServiceTable.vue'

export interface ServiceGroup {
  key: string
  label: string
  badgeBg: string
  badgeColor: string
  services: RetailService[]
}

defineProps<{ group: ServiceGroup; togglingId?: number | null }>()
defineEmits<{ add: [string]; edit: [RetailService]; toggle: [RetailService] }>()
</script>

<style scoped>
.rsg-section { display: flex; flex-direction: column; gap: 16px; }
.rsg-header { display: flex; justify-content: space-between; align-items: center; }
.rsg-header-left { display: flex; align-items: center; gap: 12px; }
.rsg-badge {
  padding: 4px 12px;
  border-radius: 9999px;
  font-size: 12px;
  font-weight: 700;
}
.rsg-count { font-size: 0.75rem; color: #64748b; font-weight: 500; }
.rsg-add-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.875rem;
  font-weight: 700;
  background: none;
  border: none;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  text-decoration: none;
  transition: text-decoration 0.1s;
}
.rsg-add-btn:hover { text-decoration: underline; }
</style>