<template>
  <div class="saved-filters">
    <div class="saved-filters__search-wrap">
      <span class="material-symbols-outlined saved-filters__search-icon">search</span>
      <input
        class="saved-filters__input"
        placeholder="Tìm tên, email, vị trí..."
        type="text"
        :value="search"
        @input="$emit('update:search', ($event.target as HTMLInputElement).value)"
        @keyup.enter="$emit('do-search')"
      />
      <button v-if="search" class="saved-filters__clear" @click="$emit('update:search', ''); $emit('do-search')">
        <span class="material-symbols-outlined">close</span>
      </button>
    </div>

    <div class="saved-filters__right">
      <!-- Source dropdown -->
      <div ref="sourceRef" class="tt-dd-wrap">
        <button
          class="tt-dd-btn"
          :class="{ 'tt-dd-btn--active': source }"
          type="button"
          @click="showSource = !showSource"
        >
          <span class="material-symbols-outlined tt-dd-icon" :class="activeSourceOpt.colorClass">{{ activeSourceOpt.icon }}</span>
          <span :class="{ 'tt-dd-label--active': source }">{{ activeSourceOpt.label }}</span>
          <span class="material-symbols-outlined tt-dd-chevron" :class="{ open: showSource }">expand_more</span>
        </button>
        <Transition name="tt-fade">
          <div v-if="showSource" class="tt-dropdown">
            <button
              v-for="opt in sourceOptions"
              :key="opt.value"
              class="tt-opt"
              :class="{ 'tt-opt--on': source === opt.value }"
              type="button"
              @click="selectSource(opt.value)"
            >
              <span class="material-symbols-outlined tt-opt-icon" :class="opt.colorClass">{{ opt.icon }}</span>
              <span class="tt-opt-label">{{ opt.label }}</span>
              <span v-if="source === opt.value" class="material-symbols-outlined tt-opt-check">check</span>
            </button>
          </div>
        </Transition>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'

const props = defineProps<{
  search: string
  source: string
}>()

const emit = defineEmits<{
  'update:search': [value: string]
  'update:source': [value: string]
  'do-search': []
}>()

const sourceOptions = [
  { value: '',                       label: 'Tất cả nguồn',  icon: 'groups',          colorClass: 'ico-slate' },
  { value: 'Duyệt CV',               label: 'Duyệt CV',       icon: 'description',     colorClass: 'ico-blue'  },
  { value: 'PV UV',                  label: 'Phỏng vấn',      icon: 'record_voice_over', colorClass: 'ico-green' },
  { value: 'Tìm kiếm UV trong DB',   label: 'Tìm kiếm',       icon: 'manage_search',   colorClass: 'ico-amber' },
]

const showSource = ref(false)
const sourceRef  = ref<HTMLElement | null>(null)

const activeSourceOpt = computed(() =>
  sourceOptions.find(o => o.value === props.source) ?? sourceOptions[0]
)

function selectSource(value: string) {
  emit('update:source', value)
  showSource.value = false
  emit('do-search')
}

function onDocClick(e: MouseEvent) {
  if (sourceRef.value && !sourceRef.value.contains(e.target as Node)) showSource.value = false
}

onMounted(() => document.addEventListener('click', onDocClick))
onUnmounted(() => document.removeEventListener('click', onDocClick))
</script>

<style scoped>
.saved-filters {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  border: 1px solid rgba(226, 232, 240, 0.7);
  border-radius: 0.75rem;
  background: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

/* Search */
.saved-filters__search-wrap {
  position: relative;
  flex: 1;
  min-width: 200px;
}

.saved-filters__search-icon {
  position: absolute;
  top: 50%;
  left: 12px;
  transform: translateY(-50%);
  color: #94a3b8;
  font-size: 1.1rem !important;
  pointer-events: none;
}

.saved-filters__input {
  box-sizing: border-box;
  width: 100%;
  height: 2.375rem;
  padding: 0 2.25rem 0 2.375rem;
  border-radius: 999px;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  color: #0f172a;
  outline: none;
  font-family: 'Manrope', sans-serif;
  font-size: 0.9375rem;
  transition: border-color 0.15s, box-shadow 0.15s, background 0.15s;
}
.saved-filters__input::placeholder { color: #94a3b8; }
.saved-filters__input:focus {
  border-color: #4b9af6;
  box-shadow: 0 0 0 3px rgba(75, 154, 246, 0.12);
  background: #fff;
}

.saved-filters__clear {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border: none;
  border-radius: 50%;
  background: #e2e8f0;
  color: #64748b;
  cursor: pointer;
  padding: 0;
}
.saved-filters__clear .material-symbols-outlined { font-size: 0.75rem; }

.saved-filters__right { display: flex; align-items: center; gap: 8px; flex-shrink: 0; }

/* Dropdown — same as TeamTable */
.tt-dd-wrap { position: relative; }

.tt-dd-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  background: #fff;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
  color: #475569;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  font-size: 0.875rem;
  font-weight: 500;
  white-space: nowrap;
  transition: border-color 0.15s, box-shadow 0.15s;
}
.tt-dd-btn:hover { border-color: #cbd5e1; box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06); }
.tt-dd-btn--active { border-color: #93c5fd; background: #eff6ff; color: #1d4ed8; }
.tt-dd-icon { font-size: 16px !important; color: #94a3b8; }
.tt-dd-btn--active .tt-dd-icon { color: #2563eb; }
.tt-dd-label--active { color: #1d4ed8; font-weight: 600; }
.tt-dd-chevron { font-size: 17px !important; color: #94a3b8; transition: transform 0.15s; margin-left: 2px; }
.tt-dd-chevron.open { transform: rotate(180deg); }

.tt-dropdown {
  position: absolute;
  top: calc(100% + 6px);
  left: 0;
  z-index: 100;
  min-width: 180px;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 6px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1), 0 2px 6px rgba(0, 0, 0, 0.05);
}

.tt-opt {
  display: flex;
  align-items: center;
  gap: 9px;
  width: 100%;
  padding: 8px 10px;
  border: 1px solid transparent;
  border-radius: 8px;
  background: none;
  color: #334155;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  font-size: 0.875rem;
  font-weight: 500;
  text-align: left;
  transition: background 0.12s;
}
.tt-opt:hover { background: #f8fafc; }
.tt-opt--on { border-color: #dbeafe; background: #eff6ff; color: #1d4ed8; font-weight: 600; }
.tt-opt-icon { font-size: 16px !important; flex-shrink: 0; }
.tt-opt--on .tt-opt-icon { color: #2563eb !important; }
.tt-opt-label { flex: 1; }
.tt-opt-check { font-size: 16px !important; color: #2563eb; flex-shrink: 0; }

/* Icon colors */
.ico-slate { color: #94a3b8; }
.ico-blue  { color: #3b82f6; }
.ico-green { color: #22c55e; }
.ico-amber { color: #f59e0b; }

/* Transition */
.tt-fade-enter-active, .tt-fade-leave-active { transition: opacity 0.13s, transform 0.13s; }
.tt-fade-enter-from, .tt-fade-leave-to { opacity: 0; transform: translateY(-4px); }
</style>
