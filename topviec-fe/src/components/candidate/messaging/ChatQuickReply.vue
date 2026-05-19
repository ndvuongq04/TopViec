<template>
  <button class="quick-reply" @click="$emit('click')">{{ text }}</button>
</template>

<script setup lang="ts">
defineProps<{ text: string }>()
defineEmits<{ click: [] }>()
</script>

<style scoped>
.quick-reply {
  padding: 6px 14px;
  background: #f1f4f9;
  color: #0f172a;
  font-size: 0.75rem;
  font-family: inherit;
  border: 1px solid #e2e8f0;
  border-radius: 99px;
  white-space: nowrap;
  cursor: pointer;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  transition: background 0.15s;
  flex-shrink: 0;
}
.quick-reply:hover { background: #e2e8f0; }
</style>