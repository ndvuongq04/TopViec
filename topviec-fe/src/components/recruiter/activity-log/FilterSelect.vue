<template>
  <div ref="dropdownRef" class="tt-dd-wrap">
    <button
      class="tt-dd-btn"
      :class="{ 'tt-dd-btn--active': value !== '' }"
      type="button"
      @click="show = !show"
    >
      <span v-if="activeOption?.icon" class="material-symbols-outlined tt-dd-icon" :class="activeOption.colorClass">
        {{ activeOption.icon }}
      </span>
      <span :class="{ 'tt-dd-label--active': value !== '' }">{{ activeOption?.label ?? placeholder }}</span>
      <span class="material-symbols-outlined tt-dd-chevron" :class="{ open: show }">expand_more</span>
    </button>
    <Transition name="tt-fade">
      <div v-if="show" class="tt-dropdown">
        <button
          class="tt-opt"
          :class="{ 'tt-opt--on': value === '' }"
          type="button"
          @click="select('')"
        >
          <span class="material-symbols-outlined tt-opt-icon ico-slate">{{ defaultIcon }}</span>
          <span class="tt-opt-label">{{ placeholder }}</span>
          <span v-if="value === ''" class="material-symbols-outlined tt-opt-check">check</span>
        </button>
        <button
          v-for="opt in options"
          :key="opt.value"
          class="tt-opt"
          :class="{ 'tt-opt--on': value === opt.value }"
          type="button"
          @click="select(opt.value)"
        >
          <span v-if="opt.icon" class="material-symbols-outlined tt-opt-icon" :class="opt.colorClass">{{ opt.icon }}</span>
          <span class="tt-opt-label">{{ opt.label }}</span>
          <span v-if="value === opt.value" class="material-symbols-outlined tt-opt-check">check</span>
        </button>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'

const props = defineProps<{
  value: string
  placeholder: string
  options: Array<{ value: string; label: string; icon?: string; colorClass?: string }>
  defaultIcon?: string
}>()

const emit = defineEmits<{ change: [value: string] }>()

const show = ref(false)
const dropdownRef = ref<HTMLElement | null>(null)

const activeOption = computed(() => props.options.find(o => o.value === props.value))

function select(val: string) {
  emit('change', val)
  show.value = false
}

function onDocClick(e: MouseEvent) {
  if (dropdownRef.value && !dropdownRef.value.contains(e.target as Node)) {
    show.value = false
  }
}

onMounted(() => document.addEventListener('mousedown', onDocClick))
onUnmounted(() => document.removeEventListener('mousedown', onDocClick))
</script>

<style scoped>
.tt-dd-wrap { position: relative; }

.tt-dd-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  background: #fff;
  box-shadow: 0 1px 2px rgba(0,0,0,0.04);
  color: #475569;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  font-size: 1rem;
  font-weight: 700;
  white-space: nowrap;
  transition: border-color 0.15s, box-shadow 0.15s;
}
.tt-dd-btn:hover { border-color: #cbd5e1; box-shadow: 0 2px 6px rgba(0,0,0,0.06); }
.tt-dd-btn--active { border-color: #93c5fd; background: #eff6ff; color: #1d4ed8; }
.tt-dd-icon { font-size: 16px; color: #94a3b8; }
.tt-dd-btn--active .tt-dd-icon { color: #2563eb; }
.tt-dd-label--active { color: #1d4ed8; font-weight: 600; }
.tt-dd-chevron { font-size: 17px; color: #94a3b8; transition: transform 0.15s; margin-left: 2px; }
.tt-dd-chevron.open { transform: rotate(180deg); }

.tt-dropdown {
  position: absolute;
  top: calc(100% + 6px);
  left: 0;
  z-index: 50;
  min-width: 240px;
  max-height: 400px;
  overflow-y: auto;
  padding: 5px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 8px 24px rgba(0,0,0,0.1), 0 2px 6px rgba(0,0,0,0.05);
}

/* Custom Scrollbar for Dropdown */
.tt-dropdown::-webkit-scrollbar { width: 5px; }
.tt-dropdown::-webkit-scrollbar-track { background: transparent; }
.tt-dropdown::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.tt-dropdown::-webkit-scrollbar-thumb:hover { background: #94a3b8; }

.tt-opt {
  display: flex;
  align-items: center;
  gap: 9px;
  width: 100%;
  padding: 7px 10px;
  border: 1px solid transparent;
  border-radius: 8px;
  background: transparent;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  font-size: 1rem;
  font-weight: 600;
  color: #334155;
  text-align: left;
  transition: background 0.12s;
}
.tt-opt:hover { background: #f8fafc; }
.tt-opt--on { border-color: #dbeafe; background: #eff6ff; color: #1d4ed8; font-weight: 600; }
.tt-opt-icon { font-size: 16px; flex-shrink: 0; }
.tt-opt--on .tt-opt-icon { color: #2563eb !important; }
.tt-opt-label { flex: 1; }
.tt-opt-check { font-size: 16px; color: #2563eb; flex-shrink: 0; }

/* Icon colors */
.ico-slate { color: #94a3b8; }
.ico-blue  { color: #3b82f6; }
.ico-green { color: #10b981; }
.ico-amber { color: #f59e0b; }
.ico-red   { color: #ef4444; }

/* Transitions */
.tt-fade-enter-active, .tt-fade-leave-active { transition: opacity 0.13s, transform 0.13s; }
.tt-fade-enter-from, .tt-fade-leave-to { opacity: 0; transform: translateY(-4px); }

:global(.dark) .tt-dd-btn {
  background: #0f172a; border-color: #334155; color: #f1f5f9;
}
:global(.dark) .tt-dd-btn:hover { border-color: #475569; }
:global(.dark) .tt-dd-btn--active { background: #1e3a8a; border-color: #3b82f6; color: #60a5fa; }
:global(.dark) .tt-dd-btn--active .tt-dd-icon { color: #60a5fa; }
:global(.dark) .tt-dd-label--active { color: #60a5fa; }
:global(.dark) .tt-dropdown { background: #0f172a; border-color: #334155; }
:global(.dark) .tt-opt { color: #f1f5f9; }
:global(.dark) .tt-opt:hover { background: #1e293b; }
:global(.dark) .tt-opt--on { background: #1e3a8a; border-color: #3b82f6; color: #60a5fa; }
:global(.dark) .tt-opt--on .tt-opt-icon { color: #60a5fa !important; }
:global(.dark) .tt-opt-check { color: #60a5fa; }
</style>