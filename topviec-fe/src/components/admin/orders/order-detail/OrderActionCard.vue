<script setup lang="ts">
import { useAdminPermission } from '@/composables/useAdminPermission'

const { can } = useAdminPermission()
const emit = defineEmits<{
  refund: []
}>()
</script>

<template>
  <div v-if="can('order.change-status')" class="action-card">
    <button class="btn-action btn-action--danger" @click="emit('refund')">
      <span class="material-symbols-outlined btn-icon">replay</span>
      Hoàn tiền
    </button>
  </div>
</template>

<style scoped>
.action-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 12px 32px -4px rgba(87,66,64,0.04);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.btn-action {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 16px;
  font-size: 0.75rem;
  font-weight: 900;
  font-family: 'Manrope', sans-serif;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-icon {
  font-size: 16px;
}

.btn-action--primary {
  background: linear-gradient(135deg, #76191d 0%, #963131 100%);
  color: #ffffff;
  box-shadow: 0 4px 6px rgba(118,25,29,0.15);
}

.btn-action--outline {
  background: #ffffff;
  color: #963131;
  border: 1px solid rgba(150,49,49,0.2);
}

.btn-action--outline:hover {
  background: rgba(150,49,49,0.05);
}

.btn-action--danger {
  background: rgba(255,218,214,0.2);
  color: #ba1a1a;
}

.btn-action--danger:hover {
  background: rgba(255,218,214,0.4);
}

.action-divider {
  height: 1px;
  background: #e2e8f0;
  margin: 4px 0;
}
</style>