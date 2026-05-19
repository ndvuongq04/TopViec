<template>
  <div class="flex items-center gap-3">
    <button @click="toggle" :class="['toggle-btn', isActive ? 'active' : 'inactive']">
      <span :class="['toggle-thumb', isActive ? 'on' : 'off']" />
    </button>
    <span :class="['text-[12px] font-medium', isActive ? 'text-tertiary' : 'text-on-surface-variant']">
      {{ isActive ? 'Đang bật' : 'Đã tắt' }}
    </span>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
const props = defineProps<{ modelValue: boolean }>()
const emit = defineEmits<{ 'update:modelValue': [boolean] }>()
const isActive = computed(() => props.modelValue)
const toggle = () => emit('update:modelValue', !props.modelValue)
</script>

<style scoped>
.toggle-btn {
  position: relative;
  display: inline-flex;
  height: 20px;
  width: 40px;
  flex-shrink: 0;
  cursor: pointer;
  border-radius: 9999px;
  border: 2px solid transparent;
  transition: background-color 0.2s;
  outline: none;
}
.toggle-btn.active  { background-color: #004638; }
.toggle-btn.inactive { background-color: #e1dfdb; }

.toggle-thumb {
  pointer-events: none;
  display: inline-block;
  height: 16px;
  width: 16px;
  border-radius: 9999px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0,0,0,0.2);
  transition: transform 0.2s;
}
.toggle-thumb.on  { transform: translateX(20px); }
.toggle-thumb.off { transform: translateX(0); }
</style>