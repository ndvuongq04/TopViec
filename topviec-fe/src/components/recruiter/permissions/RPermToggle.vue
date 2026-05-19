<template>
  <label :class="['rpt-toggle', locked && 'rpt-toggle--locked']">
    <input
      type="checkbox"
      :checked="modelValue"
      :disabled="locked"
      @change="!locked && $emit('update:modelValue', ($event.target as HTMLInputElement).checked)"
    />
    <span class="rpt-toggle__track"></span>
    <span class="rpt-toggle__thumb"></span>
  </label>
</template>

<script setup lang="ts">
defineProps<{ modelValue: boolean; locked?: boolean }>()
defineEmits(['update:modelValue'])
</script>

<style scoped>
.rpt-toggle {
  position: relative; display: inline-block;
  width: 40px; height: 22px; cursor: pointer;
}
.rpt-toggle input { opacity: 0; width: 0; height: 0; position: absolute; }

.rpt-toggle__track {
  position: absolute; inset: 0; border-radius: 12px;
  background: #cbd5e1; transition: background 0.3s ease;
  box-shadow: inset 0 1px 3px rgba(0,0,0,0.1);
}
.rpt-toggle input:checked + .rpt-toggle__track { background: #4B9AF6; }

.rpt-toggle__thumb {
  position: absolute; top: 3px; left: 3px;
  width: 16px; height: 16px; border-radius: 50%;
  background: #fff; transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 4px rgba(0,0,0,0.2), 0 0 1px rgba(0,0,0,0.1);
  pointer-events: none;
}
.rpt-toggle input:checked ~ .rpt-toggle__thumb { transform: translateX(18px); }

.rpt-toggle--locked { cursor: not-allowed; opacity: 0.65; }
.rpt-toggle--locked input:checked + .rpt-toggle__track { background: #4B9AF6; }
.rpt-toggle--locked .rpt-toggle__thumb { box-shadow: none; }
</style>
