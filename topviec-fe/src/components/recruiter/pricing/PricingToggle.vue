<template>
  <div class="toggle-wrap">
    <button :class="['toggle-btn', modelValue === 'monthly' && 'active']" @click="$emit('update:modelValue', 'monthly')">
      Hàng tháng
    </button>
    <button :class="['toggle-btn', modelValue === 'yearly' && 'active']" @click="$emit('update:modelValue', 'yearly')">
      Hàng năm
      <span class="badge-save">Tiết kiệm 20%</span>
    </button>
  </div>
</template>

<script setup lang="ts">
defineProps<{ modelValue: 'monthly' | 'yearly' }>()
defineEmits(['update:modelValue'])
</script>

<style scoped>
.toggle-wrap {
  display: inline-flex;
  align-items: center;
  padding: 4px;
  background: #f1f4f9;
  border-radius: 0.75rem;
  margin-top: 1.5rem;
}
.toggle-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 24px;
  font-size: 0.875rem;
  font-weight: 700;
  border: none;
  border-radius: 0.5rem;
  cursor: pointer;
  background: transparent;
  color: #64748b;
  transition: background 0.2s, box-shadow 0.2s, color 0.2s;
}
.toggle-btn.active {
  background: #ffffff;
  color: #4B9AF6;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}
.badge-save {
  background: #d1fae5;
  color: #059669;
  font-size: 0.75rem;
  font-weight: 800;
  padding: 2px 8px;
  border-radius: 999px;
}
</style>