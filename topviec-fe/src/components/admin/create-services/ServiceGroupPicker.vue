<template>
  <div class="picker-grid">
    <ServiceGroupPickerCard
      v-for="group in groups"
      :key="group.value"
      :group="group"
      :selected="modelValue === group.value"
      @select="$emit('update:modelValue', group.value)"
    />
  </div>
</template>

<script setup lang="ts">
import ServiceGroupPickerCard from './ServiceGroupPickerCard.vue'

defineProps<{
  modelValue: string
  groups: Array<{ value: string; icon: string; label: string }>
}>()

defineEmits<{
  'update:modelValue': [value: string]
}>()
</script>

<style scoped>
.picker-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}
</style>