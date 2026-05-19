<template>
  <div class="assign-tabs">
    <div class="assign-tabs__inner">
      <button
        v-for="t in tabs" :key="t.value"
        :class="['assign-tabs__btn', modelValue === t.value && 'assign-tabs__btn--active']"
        @click="$emit('update:modelValue', t.value)"
      >{{ t.label }}</button>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ modelValue: string }>()
defineEmits(['update:modelValue'])

const tabs = [
  { value: 'member', label: 'Theo Thành viên' },
  { value: 'job',    label: 'Theo Tin tuyển dụng' },
]
</script>

<style scoped>
.assign-tabs {
  padding: 1rem; border-bottom: 1px solid rgba(226,232,240,0.5);
 flex-shrink: 0;
}
.assign-tabs__inner {
  display: flex; background: #e2e7f0;
  border-radius: 0.5rem; padding: 4px; gap: 4px;
}
.assign-tabs__btn {
  flex: 1; padding: 8px; border-radius: 6px; border: none;
  font-size: 0.875rem; font-weight: 500; cursor: pointer;
  font-family: 'Manrope', sans-serif; color: #64748b;
  background: none; transition: all 0.2s;
}
.assign-tabs__btn--active {
  background: #fff; color: #4B9AF6; font-weight: 700;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}
</style>