<template>
  <div class="filter-card">

    <!-- Tabs & Search Row -->
    <div class="tabs-row">
      <div class="tabs-group">
        <button
          v-for="tab in tabs"
          :key="tab.value"
          :class="['tab-chip', activeTab === tab.value ? 'tab-chip--active' : 'tab-chip--default']"
          @click="$emit('update:activeTab', tab.value)"
        >
          {{ tab.label }}
          <span v-if="tab.count > 0" class="tab-count">{{ tab.count.toLocaleString() }}</span>
        </button>
      </div>

      <!-- Search -->
      <div class="search-wrap">
        <span class="search-icon material-symbols-outlined">search</span>
        <input
          v-model="localInput"
          type="text"
          class="search-input"
          placeholder="Tìm tên, email ứng viên... (Enter để tìm)"
          @keyup.enter="commitSearch"
        />
        <button
          v-if="localInput"
          class="search-clear"
          @click="clearSearch"
        >
          <span class="material-symbols-outlined">close</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{
  activeTab: string
  tabs:      any[]
  searchValue: string
}>()

const emit = defineEmits<{
  'update:activeTab':   [v: string]
  'update:searchValue': [v: string]
}>()

const localInput = ref(props.searchValue)

// Sync khi parent reset search từ bên ngoài
watch(() => props.searchValue, (val) => {
  localInput.value = val
})

function commitSearch() {
  emit('update:searchValue', localInput.value.trim())
}

function clearSearch() {
  localInput.value = ''
  emit('update:searchValue', '')
}
</script>

<style scoped>
.filter-card {
  background: #fff;
  padding: 1.25rem 1.5rem;
  border-radius: 0.75rem 0.75rem 0 0;
  border: 1px solid #f1f5f9;
  border-bottom: none;
  box-shadow: 0 1px 3px rgba(0,0,0,.06);
}

/* Tabs & Search Row */
.tabs-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1.5rem;
  flex-wrap: wrap;
}

.tabs-group {
  display: flex;
  gap: 0.5rem;
  overflow-x: auto;
  padding-bottom: 0.25rem;
  flex: 1;
}
.tabs-group::-webkit-scrollbar { display: none; }

.tab-chip {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.5rem 1.25rem;
  border-radius: 9999px;
  font-size: 0.875rem;
  font-weight: 700;
  white-space: nowrap;
  transition: all .15s;
  background: #f1f5f9;
  color: #475569;
  cursor: pointer;
}
.tab-chip:hover { background: #e2e8f0; }
.tab-chip--active  { background: #4B9AF6; color: #fff; box-shadow: 0 4px 12px rgba(75,154,246,.2); }

.tab-count {
  background: rgba(0,0,0,.1);
  border-radius: 9999px;
  padding: 0.1rem 0.5rem;
  font-size: 0.75rem;
}
.tab-chip--active .tab-count { background: rgba(255,255,255,.25); }

/* Search Styles */
.search-wrap {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
  max-width: 340px;
  margin-left: auto;
}
.search-icon {
  position: absolute;
  left: 1rem;
  color: #94a3b8;
  font-size: 1.25rem !important;
  pointer-events: none;
}
.search-input {
  width: 100%;
  height: 2.75rem;
  padding: 0 2.75rem 0 3rem;
  border: 1px solid #e2e8f0;
  border-radius: 9999px;
  font-size: 0.9375rem;
  color: #0f172a;
  outline: none;
  background: #f8fafc;
  transition: all .2s;
}
.search-input::placeholder { color: #94a3b8; }
.search-input:focus {
  background: #fff;
  border-color: #4B9AF6;
  box-shadow: 0 0 0 4px rgba(75,154,246,.1);
}

.search-clear {
  position: absolute;
  right: 0.875rem;
  width: 1.5rem;
  height: 1.5rem;
  border-radius: 50%;
  background: #e2e8f0;
  color: #64748b;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background .15s;
}
.search-clear:hover { background: #cbd5e1; }
.search-clear .material-symbols-outlined { font-size: 1rem !important; }

@media (max-width: 768px) {
  .tabs-row { flex-direction: column; align-items: stretch; gap: 1rem; }
  .search-wrap { max-width: none; }
}
</style>
