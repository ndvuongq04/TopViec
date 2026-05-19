<template>
  <div class="ald-context">
    <h3 class="ald-context__title">
      <span class="material-symbols-outlined">devices</span>
      Ngữ cảnh request
    </h3>
    <div class="ald-context__grid">
      <div v-for="item in fields" :key="item.label" class="ald-context__item">
        <p class="ald-context__label">
          <span class="material-symbols-outlined">{{ item.icon }}</span>
          {{ item.label }}
        </p>
        <span :class="['ald-context__value', item.mono && 'ald-context__value--mono']">{{ item.value }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{ context: { ip: string; userAgent: string; source: string } }>()

const fields = computed(() => [
  { label: 'IP Address', icon: 'public',     value: props.context.ip,        mono: true },
  { label: 'User Agent', icon: 'laptop_mac', value: props.context.userAgent,  mono: false },
  { label: 'Source',     icon: 'api',        value: props.context.source,     mono: false },
])
</script>

<style scoped>
.ald-context {
  background: #fff; border-radius: 1rem; padding: 2rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border: 1px solid rgba(228,226,220,0.5);
}
.ald-context__title {
  display: flex; align-items: center; gap: 10px;
  font-size: 1.125rem; font-weight: 700; color: #1b1c18; margin-bottom: 1.5rem;
}
.ald-context__title .material-symbols-outlined { color: #76191d; font-size: 22px; }

.ald-context__grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 2rem; }
@media (max-width: 768px) { .ald-context__grid { grid-template-columns: 1fr; gap: 1.25rem; } }

.ald-context__item { display: flex; flex-direction: column; gap: 8px; }
.ald-context__label {
  display: flex; align-items: center; gap: 6px;
  font-size: 0.75rem; font-weight: 700; text-transform: uppercase;
  letter-spacing: 0.07em; color: #574240;
}
.ald-context__label .material-symbols-outlined { font-size: 16px; }

.ald-context__value {
  font-size: 0.875rem; font-weight: 500; color: #1b1c18;
}
.ald-context__value--mono {
  font-family: 'JetBrains Mono', monospace; font-size: 0.8125rem;
  background: #f6f4ec; padding: 6px 12px; border-radius: 0.5rem;
  border: 1px solid rgba(228,226,220,0.3); display: inline-block;
}
</style>