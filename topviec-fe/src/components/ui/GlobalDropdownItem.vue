<template>
  <div class="dropdown-item-wrapper" :class="{ 'dropdown-item-wrapper--disabled': disabled }">
    <button
      class="dropdown-item"
      :class="{ 'dropdown-item--danger': danger }"
      :disabled="disabled"
      @click="handleClick"
    >
      <span v-if="icon" class="material-symbols-outlined dropdown-item__icon">{{ icon }}</span>
      <span class="dropdown-item__label">
        <slot>{{ label }}</slot>
      </span>
    </button>
    
    <!-- Tooltip -->
    <div v-if="tooltip" class="dropdown-item__tooltip">
      {{ tooltip }}
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  label?: string
  icon?: string
  danger?: boolean
  disabled?: boolean
  tooltip?: string
}>()

const emit = defineEmits<{
  click: [event: MouseEvent]
}>()

function handleClick(e: MouseEvent) {
  if (props.disabled) return
  emit('click', e)
}
</script>

<style scoped>
.dropdown-item-wrapper {
  position: relative;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  width: 100%;
  padding: 0.625rem 0.75rem;
  border-radius: 0.5rem;
  border: none;
  background: transparent;
  font-size: 1rem;       /* 16px — Primary Text (skill-font.md) */
  font-weight: 500;
  color: #334155;
  cursor: pointer;
  text-align: left;
  transition: all 0.15s;
  font-family: inherit;
}

.dropdown-item:not(:disabled):hover {
  background: #f1f5f9;
  color: #0f172a;
}

.dropdown-item--danger {
  color: #ef4444;
}

.dropdown-item--danger:not(:disabled):hover {
  background: #fee2e2;
  color: #dc2626;
}

.dropdown-item:disabled {
  cursor: not-allowed;
}

.dropdown-item-wrapper--disabled {
  opacity: 0.5;
}

.dropdown-item__icon {
  font-size: 1.125rem;
  flex-shrink: 0;
  color: #94a3b8;
  transition: color 0.15s;
}

.dropdown-item:not(:disabled):hover .dropdown-item__icon {
  color: inherit;
}

.dropdown-item--danger .dropdown-item__icon {
  color: inherit;
}

/* Tooltip implementation */
.dropdown-item__tooltip {
  display: none;
  position: absolute;
  right: calc(100% + 12px);
  top: 50%;
  transform: translateY(-50%);
  background: #0a0f1a;
  color: #f1f5f9;
  font-size: 0.75rem;
  font-weight: 500;
  line-height: 1.4;
  padding: 0.5rem 0.75rem;
  border-radius: 0.5rem;
  white-space: nowrap;
  pointer-events: none;
  z-index: 10100;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.dropdown-item__tooltip::before {
  content: '';
  position: absolute;
  left: 100%;
  top: 50%;
  transform: translateY(-50%);
  border: 6px solid transparent;
  border-left-color: #0a0f1a;
}

/* Show tooltip on hover of parent wrapper */
.dropdown-item-wrapper:hover .dropdown-item__tooltip {
  display: block;
}
</style>
