<template>
  <div>
    <!-- Toolbar -->
    <div class="flex items-center gap-3 p-3.5 border-b border-slate-200 dark:border-slate-800 flex-wrap font-['Manrope']">
      <!-- Search -->
      <div class="relative flex-1 min-w-[200px] max-w-[400px]">
        <span class="material-symbols-outlined absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none text-[18px]">search</span>
        <input
          class="w-full bg-slate-50 dark:bg-slate-800/50 border border-slate-200 dark:border-slate-700 rounded-full py-2.5 pl-[42px] pr-10 text-base outline-none focus:ring-2 focus:ring-primary/20 text-slate-900 dark:text-white placeholder:text-slate-400 transition-shadow italic"
          type="text"
          placeholder="Tìm kiếm theo Email, Target ID, hoặc Nội dung..."
          :value="modelValue.keyword"
          @input="update('keyword', ($event.target as HTMLInputElement).value)"
          @keyup.enter="$emit('apply')"
        />
      </div>

      <div class="flex items-center gap-2 flex-wrap ml-auto">
        <!-- Action Filter (Searchable) -->
        <div class="w-[200px]">
          <SearchableSelect
            :modelValue="modelValue.action"
            placeholder="Hành động"
            :options="actionOptionsForSelect"
            @update:modelValue="update('action', $event); $emit('apply')"
          />
        </div>

        <!-- Category Filter (Searchable) -->
        <div class="w-[200px]">
          <SearchableSelect
            :modelValue="modelValue.category"
            placeholder="Danh mục"
            :options="categoryOptionsForSelect"
            @update:modelValue="update('category', $event); $emit('apply')"
          />
        </div>

        <!-- Severity Filter (AUDIT only) -->
        <FilterSelect
          v-if="activeType === 'AUDIT'"
          :value="modelValue.severity"
          placeholder="Mức độ"
          defaultIcon="warning"
          :options="severityOptions"
          @change="update('severity', $event); $emit('apply')"
        />

        <!-- Status Filter -->
        <FilterSelect
          :value="modelValue.status"
          placeholder="Trạng thái"
          defaultIcon="check_circle"
          :options="[
            { value: 'SUCCESS', label: 'Thành công', icon: 'done_all', colorClass: 'ico-green' },
            { value: 'FAILURE', label: 'Thất bại', icon: 'error', colorClass: 'ico-red' }
          ]"
          @change="update('status', $event); $emit('apply')"
        />

        <!-- Date range picker -->
        <div ref="dateRef" class="type-wrap">
          <button class="type-btn" type="button" @click="showDate = !showDate">
            <span class="material-symbols-outlined type-btn-icon">calendar_month</span>
            {{ dateLabel }}
            <span
              v-if="modelValue.startDate || modelValue.endDate"
              class="date-clear"
              @click.stop="clearDate"
            >
              <span class="material-symbols-outlined">close</span>
            </span>
            <span v-else class="material-symbols-outlined type-chevron" :class="{ open: showDate }">expand_more</span>
          </button>

          <Transition name="picker-fade">
            <div v-if="showDate" class="date-dropdown">
              <div class="date-dropdown-head">
                <span class="material-symbols-outlined">date_range</span>
                Khoảng thời gian
              </div>
              <div class="date-fields">
                <div class="date-field">
                  <label class="date-label">Từ ngày</label>
                  <input
                    v-model="draftDateFrom"
                    type="date"
                    class="date-input"
                    :max="draftDateTo || undefined"
                  />
                </div>
                <span class="date-sep material-symbols-outlined">arrow_forward</span>
                <div class="date-field">
                  <label class="date-label">Đến ngày</label>
                  <input
                    v-model="draftDateTo"
                    type="date"
                    class="date-input"
                    :min="draftDateFrom || undefined"
                  />
                </div>
              </div>
              <div class="date-actions">
                <button class="date-action-clear" type="button" @click="clearDate">Xóa</button>
                <button class="date-action-apply" type="button" @click="onApplyDate">Lọc thời gian</button>
              </div>
            </div>
          </Transition>
        </div>

        <button
          v-if="hasFilters"
          class="flex items-center p-[7px] border border-red-200 dark:border-red-900/50 rounded-lg bg-white dark:bg-slate-900 text-red-500 hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors cursor-pointer font-bold gap-1 text-xs"
          title="Xóa bộ lọc"
          @click="resetAll"
        >
          <span class="material-symbols-outlined text-[16px]">filter_alt_off</span>
          Xóa lọc
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch, onMounted, onUnmounted } from 'vue'
import FilterSelect from './FilterSelect.vue'
import SearchableSelect from '@/components/ui/SearchableSelect.vue'
import { 
  LOG_ACTION_TYPE_LABELS, 
  LOG_CATEGORY_LABELS, 
  SEVERITY_LABELS 
} from '@/constants/logs.constants'

const props = defineProps<{
  modelValue: any,
  activeType: 'AUDIT' | 'BUSINESS'
}>()

const emit = defineEmits<{
  'update:modelValue': [value: any]
  apply: []
  reset: []
}>()

const actionOptionsForSelect = computed(() => [
  { id: '', name: 'Tất cả hành động' },
  ...Object.entries(LOG_ACTION_TYPE_LABELS).map(([id, name]) => ({
    id, name
  }))
])

const categoryOptionsForSelect = computed(() => [
  { id: '', name: 'Tất cả danh mục' },
  ...Object.entries(LOG_CATEGORY_LABELS).map(([id, name]) => ({
    id, name
  }))
])

const severityOptions = computed(() => Object.entries(SEVERITY_LABELS).map(([value, label]) => ({
  value, label, icon: 'warning', colorClass: value === 'CRITICAL' ? 'ico-red' : value === 'HIGH' ? 'ico-orange' : 'ico-yellow'
})))

function update(key: string, value: any) {
  emit('update:modelValue', { ...props.modelValue, [key]: value })
}

// Date Picker Logic
const showDate = ref(false)
const dateRef = ref<HTMLElement | null>(null)
const draftDateFrom = ref(props.modelValue.startDate)
const draftDateTo = ref(props.modelValue.endDate)

const dateLabel = computed(() => {
  if (!props.modelValue.startDate && !props.modelValue.endDate) return 'Thời gian'
  const fmt = (d: string) => d.split('-').reverse().join('/')
  if (props.modelValue.startDate && props.modelValue.endDate) return `${fmt(props.modelValue.startDate)} – ${fmt(props.modelValue.endDate)}`
  if (props.modelValue.startDate) return `Từ ${fmt(props.modelValue.startDate)}`
  return `Đến ${fmt(props.modelValue.endDate)}`
})

watch(showDate, (isOpen) => {
  if (isOpen) {
    draftDateFrom.value = props.modelValue.startDate
    draftDateTo.value = props.modelValue.endDate
  }
})

function onApplyDate() {
  emit('update:modelValue', { ...props.modelValue, startDate: draftDateFrom.value, endDate: draftDateTo.value })
  showDate.value = false
  emit('apply')
}

function clearDate() {
  draftDateFrom.value = ''
  draftDateTo.value = ''
  emit('update:modelValue', { ...props.modelValue, startDate: '', endDate: '' })
  showDate.value = false
  emit('apply')
}

function resetAll() {
  emit('reset')
}

function onDocClick(event: MouseEvent) {
  if (dateRef.value && !dateRef.value.contains(event.target as Node)) {
    showDate.value = false
  }
}

onMounted(() => document.addEventListener('mousedown', onDocClick))
onUnmounted(() => document.removeEventListener('mousedown', onDocClick))

const hasFilters = computed(() => {
  return props.modelValue.action || 
         props.modelValue.category || 
         props.modelValue.severity || 
         props.modelValue.status || 
         props.modelValue.keyword ||
         props.modelValue.startDate || 
         props.modelValue.endDate
})
</script>

<style scoped>
.picker-fade-enter-active,
.picker-fade-leave-active {
  transition: opacity 0.15s, transform 0.15s;
}

.picker-fade-enter-from,
.picker-fade-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

.type-wrap {
  position: relative;
}

.type-btn {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding: 8px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  background: #fff;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
  color: #334155;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  font-size: 1rem;
  font-weight: 700;
  transition: border-color 0.15s, box-shadow 0.15s;
  white-space: nowrap;
}

.type-btn:hover {
  border-color: #cbd5e1;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
}

.type-btn-icon {
  font-size: 16px;
  color: #64748b;
}

.type-chevron {
  font-size: 18px;
  color: #94a3b8;
  transition: transform 0.15s;
  margin-left: 2px;
}

.type-chevron.open {
  transform: rotate(180deg);
}

/* ── Date range picker ── */
.date-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  z-index: 20;
  width: min(420px, calc(100vw - 32px));
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  background: #fff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1), 0 2px 6px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.date-dropdown-head {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-bottom: 1px solid #f1f5f9;
  background: #f8fafc;
  color: #475569;
  font-size: 0.8rem;
  font-weight: 700;
  letter-spacing: 0.05em;
  text-transform: uppercase;
}

.date-dropdown-head .material-symbols-outlined {
  color: #4b9af6;
  font-size: 18px;
}

.date-fields {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto minmax(0, 1fr);
  align-items: end;
  column-gap: 8px;
  padding: 16px;
}

.date-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.date-label {
  font-size: 0.75rem;
  font-weight: 600;
  color: #64748b;
}

.date-input {
  width: 100%;
  padding: 8px 10px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-family: 'Manrope', sans-serif;
  font-size: 1rem;
  color: #0f172a;
  background: #f8fafc;
  outline: none;
  cursor: pointer;
  transition: border-color 0.15s, box-shadow 0.15s;
}

.date-input:focus {
  border-color: #4b9af6;
  box-shadow: 0 0 0 3px rgba(75, 154, 246, 0.1);
  background: #fff;
}

.date-sep {
  font-size: 16px;
  color: #cbd5e1;
  flex-shrink: 0;
  margin-bottom: 9px;
}

.date-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid #f1f5f9;
  background: #f8fafc;
}

.date-action-clear {
  padding: 7px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: #fff;
  color: #64748b;
  font-family: 'Manrope', sans-serif;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.15s;
}

.date-action-clear:hover {
  background: #f1f5f9;
}

.date-action-apply {
  padding: 7px 16px;
  border: none;
  border-radius: 8px;
  background: #4b9af6;
  color: #fff;
  font-family: 'Manrope', sans-serif;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.15s;
}

.date-action-apply:hover {
  background: #3b82f6;
}

.date-clear {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.12);
  color: inherit;
  margin-left: 2px;
  transition: background 0.15s;
}

.date-clear:hover {
  background: rgba(0, 0, 0, 0.22);
}

.date-clear .material-symbols-outlined {
  font-size: 13px !important;
}

:global(.dark) .type-btn {
  background: #0f172a;
  border-color: #334155;
  color: #f1f5f9;
}
:global(.dark) .type-btn:hover {
  border-color: #475569;
}
:global(.dark) .date-dropdown {
  background: #0f172a;
  border-color: #334155;
}
:global(.dark) .date-dropdown-head,
:global(.dark) .date-actions {
  background: #1e293b;
  border-color: #334155;
}
:global(.dark) .date-input {
  background: #1e293b;
  border-color: #334155;
  color: #f1f5f9;
}
:global(.dark) .date-input:focus {
  background: #0f172a;
  border-color: #3b82f6;
}
:global(.dark) .date-action-clear {
  background: #0f172a;
  border-color: #334155;
  color: #cbd5e1;
}
:global(.dark) .date-action-clear:hover {
  background: #1e293b;
}
</style>