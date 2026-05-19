<template>
  <div class="toolbar">
    <div class="filter-tabs">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        class="filter-tab"
        :class="{ 'filter-tab--active': modelValue === tab.value }"
        @click="$emit('update:modelValue', tab.value)"
      >
        {{ tab.label }}
      </button>
    </div>

    <div class="search-wrap">
      <span class="search-icon material-symbols-outlined">search</span>
      <input
        class="search-input"
        type="text"
        placeholder="Tìm kiếm tin tuyển dụng..."
        :value="searchValue"
        @input="$emit('update:searchValue', ($event.target as HTMLInputElement).value)"
        @keyup.enter="$emit('search')"
      />
      <button
        v-if="searchValue"
        class="search-clear"
        @click="$emit('update:searchValue', ''); $emit('search')"
      >
        <span class="material-symbols-outlined">close</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
export type JobPostingFilterTab =
  | 'all' | 'active' | 'pending' | 'draft'
  | 'hidden' | 'closed' | 'expired' | 'interviewing' | 'completed' | 'deleted'

const tabs: { label: string; value: JobPostingFilterTab }[] = [
  { label: 'Tất cả',           value: 'all' },
  { label: 'Đang tuyển',       value: 'active' },
  { label: 'Chờ duyệt',        value: 'pending' },
  { label: 'Nháp',             value: 'draft' },
  { label: 'Đã ẩn',            value: 'hidden' },
  { label: 'Đã đóng',          value: 'closed' },
  { label: 'Hết hạn',          value: 'expired' },
  { label: 'Đang phỏng vấn',   value: 'interviewing' },
  { label: 'Đã hoàn thành',    value: 'completed' },
  { label: 'Đã xóa',           value: 'deleted' },
]

defineProps<{
  modelValue: JobPostingFilterTab
  searchValue: string
}>()

defineEmits<{
  'update:modelValue':  [value: JobPostingFilterTab]
  'update:searchValue': [value: string]
  'search':             []
}>()
</script>

<style scoped>
.toolbar {
  padding: 1rem 1.25rem;
  border-bottom: 1px solid var(--color-border-light);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  flex-wrap: wrap;
}

/* ── Filter tabs ─────────────────────────── */
.filter-tabs {
  display: flex;
  gap: 0.375rem;
  overflow-x: auto;
  flex: 1;
  min-width: 0;
}
.filter-tabs::-webkit-scrollbar { display: none; }

.filter-tab {
  padding: 0.4375rem 0.875rem;
  border-radius: var(--radius-full);
  font-size: 0.875rem;
  font-weight: 600;
  font-family: inherit;
  border: none;
  cursor: pointer;
  white-space: nowrap;
  background: #f1f5f9;
  color: #475569;
  transition: background 0.15s, color 0.15s;
  flex-shrink: 0;
}
.filter-tab:hover { background: #e2e8f0; }
.filter-tab--active {
  background: var(--color-primary);
  color: #fff;
}

/* ── Search ──────────────────────────────── */
.search-wrap {
  position: relative;
  display: flex;
  align-items: center;
  flex-shrink: 0;
  width: 15rem;
}

.search-icon {
  position: absolute;
  left: 0.75rem;
  font-size: 1.1rem !important;
  color: #94a3b8;
  pointer-events: none;
  user-select: none;
}

.search-input {
  width: 100%;
  height: 2.375rem;
  padding: 0 2.25rem 0 2.375rem;
  border-radius: var(--radius-full);
  border: 1px solid var(--color-border);
  background: #f8fafc;
  font-size: 1rem;
  font-family: inherit;
  color: var(--color-on-surface);
  outline: none;
  transition: border-color 0.15s, box-shadow 0.15s, background 0.15s;
}
.search-input::placeholder { color: #94a3b8; }
.search-input:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px color-mix(in srgb, var(--color-primary) 12%, transparent);
  background: #fff;
}

.search-clear {
  position: absolute;
  right: 0.5rem;
  width: 1.375rem;
  height: 1.375rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 50%;
  background: #e2e8f0;
  color: #64748b;
  cursor: pointer;
  padding: 0;
  transition: background 0.15s;
}
.search-clear:hover { background: #cbd5e1; }
.search-clear .material-symbols-outlined { font-size: 0.875rem !important; }

/* ── Responsive ──────────────────────────── */
@media (max-width: 640px) {
  .toolbar { flex-direction: column; align-items: stretch; }
  .search-wrap { width: 100%; }
}
</style>
