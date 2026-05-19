<template>
  <div class="block-controls" v-show="isHovered">
    <button
      type="button"
      :disabled="isFirst"
      title="Di chuyen len"
      @click="$emit('moveUp')"
    >
      <span class="material-symbols-outlined">arrow_upward</span>
    </button>
    <button
      type="button"
      :disabled="isLast"
      title="Di chuyen xuong"
      @click="$emit('moveDown')"
    >
      <span class="material-symbols-outlined">arrow_downward</span>
    </button>
    <button type="button" title="Xoa" @click="$emit('remove')">
      <span class="material-symbols-outlined">close</span>
    </button>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  isHovered: boolean
  isFirst: boolean
  isLast: boolean
}>()

defineEmits<{
  moveUp: []
  moveDown: []
  remove: []
}>()
</script>

<style scoped>
.block-controls {
  position: absolute;
  top: 0.35rem;
  right: 0.35rem;
  z-index: 5;
  display: inline-flex;
  gap: 0.25rem;
  border: 1px solid rgba(148, 163, 184, 0.35);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.96);
  padding: 0.25rem;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.12);
  backdrop-filter: blur(10px);
}

.block-controls button {
  display: inline-flex;
  width: 1.75rem;
  height: 1.75rem;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 999px;
  background: transparent;
  color: #475569;
  cursor: pointer;
  transition: background-color 0.15s ease, color 0.15s ease, opacity 0.15s ease;
}

.block-controls button:hover:not(:disabled) {
  background: #f1f5f9;
  color: #0f172a;
}

.block-controls button:disabled {
  cursor: default;
  opacity: 0.35;
}

.block-controls .material-symbols-outlined {
  font-size: 1rem;
}
</style>
