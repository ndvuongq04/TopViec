<template>
  <span :class="['skill-tag', `skill-tag--${variant ?? 'primary'}`]">
    {{ label }}
    <span v-if="removable" class="material-symbols-outlined skill-tag__remove" @click="$emit('remove')">close</span>
  </span>
</template>

<script setup lang="ts">
defineProps<{
  label: string
  variant?: 'primary' | 'neutral' | 'outline'
  removable?: boolean
}>()
defineEmits(['remove'])
</script>

<style scoped>
.skill-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 6px;
  font-family: 'Manrope', sans-serif;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.02em;
  white-space: nowrap;
}

.skill-tag--primary {
  background: #e0f2fe;
  color: #4b9af6;
}

.skill-tag--neutral {
  background: #f1f4f9;
  color: #64748b;
}

.skill-tag--outline {
  border: 1px solid #e2e8f0;
  background: #f1f4f9;
  color: #0f172a;
}

.skill-tag__remove {
  color: inherit;
  cursor: pointer;
  opacity: 0.6;
  font-size: 0.875rem;
  transition: opacity 0.15s;
}

.skill-tag__remove:hover {
  opacity: 1;
}
</style>
