<template>
  <label class="picker-card" :class="{ 'is-selected': selected }">
    <input
      class="sr-only"
      type="radio"
      name="service_group"
      :value="group.value"
      :checked="selected"
      @change="$emit('select')"
    />
    <span class="material-symbols-outlined card-icon" :class="{ 'icon-active': selected }">
      {{ group.icon }}
    </span>
    <span class="card-label">{{ group.label }}</span>
    <span v-if="selected" class="check-icon material-symbols-outlined">check_circle</span>
  </label>
</template>

<script setup lang="ts">
defineProps<{
  group: { value: string; icon: string; label: string }
  selected: boolean
}>()

defineEmits<{ select: [] }>()
</script>

<style scoped>
.picker-card {
  position: relative;
  display: flex;
  flex-direction: column;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  background: #fff;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s;
  min-height: 90px;
  gap: 6px;
}
.picker-card:hover { background: #f8fafc; border-color: #cbd5e1; }

.picker-card.is-selected {
  border-color: #76191d;
  background: #fff;
  box-shadow: 0 0 0 1px #76191d;
}

.card-icon {
  font-size: 22px;
  color: #574240;
  transition: color 0.15s;
}
.icon-active { color: #76191d; }

.card-label {
  font-size: 14px;
  font-weight: 600;
  color: #1b1c18;
  line-height: 1.3;
}

.check-icon {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 16px;
  color: #76191d;
  font-variation-settings: 'FILL' 1;
}

.sr-only {
  position: absolute;
  width: 1px; height: 1px;
  padding: 0; margin: -1px;
  overflow: hidden; clip: rect(0,0,0,0);
  white-space: nowrap; border: 0;
}
</style>