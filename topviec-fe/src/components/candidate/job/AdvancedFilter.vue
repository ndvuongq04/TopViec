<script setup lang="ts">
import { ref, watch, onMounted, computed } from 'vue'
import { useIndustryStore } from '@/stores/industry.store'

export interface SearchFilters {
  industryId?: number
  salaryMin?: number
  salaryMax?: number
  isFeatured?: boolean
  isUrgent?: boolean
}

const props = withDefaults(defineProps<{
  initialIndustryId?: number
  initialSalaryMin?: number
  initialSalaryMax?: number
  initialFeatured?: boolean
  initialUrgent?: boolean
}>(), {
  initialFeatured: false,
  initialUrgent: false,
})

const emit = defineEmits<{ change: [filters: SearchFilters] }>()

const industryStore = useIndustryStore()

const SALARY_PRESETS = [
  { label: 'Tất cả' },
  { label: 'Dưới 10 triệu', max: 10_000_000 },
  { label: '10 - 15 triệu', min: 10_000_000, max: 15_000_000 },
  { label: '15 - 20 triệu', min: 15_000_000, max: 20_000_000 },
  { label: '20 - 25 triệu', min: 20_000_000, max: 25_000_000 },
  { label: '25 - 30 triệu', min: 25_000_000, max: 30_000_000 },
  { label: '30 - 50 triệu', min: 30_000_000, max: 50_000_000 },
  { label: 'Trên 50 triệu', min: 50_000_000 },
]

const selectedIndustryId = ref<number | undefined>(props.initialIndustryId)
const showIndustryDropdown = ref(false)
const isFeatured = ref(props.initialFeatured)
const isUrgent = ref(props.initialUrgent)

// Khởi tạo salary preset từ initialSalaryMin/Max
function findSalaryPresetIdx(min?: number, max?: number): number {
  if (min === undefined && max === undefined) return 0
  const idx = SALARY_PRESETS.findIndex(p => p.min === min && p.max === max)
  return idx >= 0 ? idx : -1
}

const selectedSalaryIdx = ref(findSalaryPresetIdx(props.initialSalaryMin, props.initialSalaryMax))
const customSalaryMinStr = ref(
  selectedSalaryIdx.value === -1 && props.initialSalaryMin
    ? String(props.initialSalaryMin / 1_000_000)
    : ''
)
const customSalaryMaxStr = ref(
  selectedSalaryIdx.value === -1 && props.initialSalaryMax
    ? String(props.initialSalaryMax / 1_000_000)
    : ''
)

// v-model trên type="number" trả về number ở runtime → dùng Number() trực tiếp
const customSalaryMin = computed(() => Number(customSalaryMinStr.value))
const customSalaryMax = computed(() => Number(customSalaryMaxStr.value))

const hasBothValues = computed(() =>
  customSalaryMinStr.value !== '' && customSalaryMaxStr.value !== ''
)

// Hiển thị lỗi ngay khi cả 2 ô đã có giá trị và "Từ" >= "Đến"
const salaryError = computed(() => {
  if (!hasBothValues.value) return ''
  if (isNaN(customSalaryMin.value) || isNaN(customSalaryMax.value)) return ''
  if (customSalaryMin.value >= customSalaryMax.value) return '"Từ" phải nhỏ hơn "Đến"'
  return ''
})

// Chỉ cho phép bấm Áp dụng khi cả 2 ô hợp lệ và Từ < Đến
const canApplyCustom = computed(() =>
  hasBothValues.value &&
  !isNaN(customSalaryMin.value) &&
  !isNaN(customSalaryMax.value) &&
  customSalaryMin.value > 0 &&
  customSalaryMax.value > 0 &&
  customSalaryMin.value < customSalaryMax.value
)

onMounted(async () => {
  await industryStore.fetchIndustries({ size: 50 })
})

function buildFilters(): SearchFilters {
  let salaryMin: number | undefined
  let salaryMax: number | undefined

  if (selectedSalaryIdx.value !== -1) {
    const preset = SALARY_PRESETS[selectedSalaryIdx.value]
    salaryMin = preset.min
    salaryMax = preset.max
  } else if (canApplyCustom.value) {
    salaryMin = customSalaryMin.value * 1_000_000
    salaryMax = customSalaryMax.value * 1_000_000
  }

  return {
    industryId: selectedIndustryId.value,
    salaryMin,
    salaryMax,
    isFeatured: isFeatured.value || undefined,
    isUrgent: isUrgent.value || undefined,
  }
}

function emitFilters() {
  emit('change', buildFilters())
}

function selectIndustry(id: number | undefined) {
  selectedIndustryId.value = id
  showIndustryDropdown.value = false
  emitFilters()
}

function selectSalaryPreset(idx: number) {
  selectedSalaryIdx.value = idx
  customSalaryMinStr.value = ''
  customSalaryMaxStr.value = ''
  emitFilters()
}

function handleCustomInput() {
  selectedSalaryIdx.value = -1
}

function preventNonDigits(e: KeyboardEvent) {
  if (['e', 'E', '+', '-', '.', ','].includes(e.key)) {
    e.preventDefault()
  }
}

function applyCustomSalary() {
  if (salaryError.value) return
  emitFilters()
}

function clearAll() {
  selectedIndustryId.value = undefined
  selectedSalaryIdx.value = 0
  customSalaryMinStr.value = ''
  customSalaryMaxStr.value = ''
  isFeatured.value = false
  isUrgent.value = false
  emitFilters()
}

watch([isFeatured, isUrgent], emitFilters)
</script>

<template>
  <div class="bg-background-light dark:bg-background-dark rounded-2xl p-4 select-none">

    <!-- Header -->
    <div class="flex items-center gap-2 mb-5">
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
        stroke-linecap="round" stroke-linejoin="round" class="text-primary shrink-0">
        <polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3" />
      </svg>
      <h3 class="text-sm font-bold text-primary">Lọc nâng cao</h3>
    </div>

    <!-- Lĩnh vực công việc -->
    <div class="mb-4 pb-4 border-b border-slate-200 dark:border-slate-700">
      <p class="text-sm font-bold text-text-main dark:text-white mb-2.5">Lĩnh vực công việc</p>
      <div class="relative">
        <button
          type="button"
          class="w-full flex items-center gap-2 px-3 py-2 border border-slate-200 dark:border-slate-600 rounded-lg bg-white dark:bg-surface-dark text-sm text-text-muted hover:border-primary transition-colors cursor-pointer"
          @click="showIndustryDropdown = !showIndustryDropdown"
        >
          <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="shrink-0 text-slate-400">
            <rect x="2" y="7" width="20" height="14" rx="2" /><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2" />
          </svg>
          <span class="flex-1 text-left truncate text-sm"
            :class="selectedIndustryId ? 'text-text-main dark:text-white font-medium' : 'text-text-muted'">
            {{
              selectedIndustryId
                ? industryStore.industries.find(i => i.id === selectedIndustryId)?.name ?? 'Tất cả lĩnh vực'
                : 'Tất cả lĩnh vực'
            }}
          </span>
          <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"
            class="shrink-0 text-slate-400 transition-transform" :class="showIndustryDropdown ? 'rotate-180' : ''">
            <polyline points="6 9 12 15 18 9" />
          </svg>
        </button>

        <div
          v-if="showIndustryDropdown"
          class="absolute z-20 top-full mt-1 w-full bg-white dark:bg-surface-dark border border-slate-200 dark:border-slate-600 rounded-lg shadow-lg max-h-52 overflow-y-auto"
        >
          <button
            type="button"
            class="w-full text-left px-3 py-2 text-sm hover:bg-slate-50 dark:hover:bg-slate-700 transition-colors cursor-pointer"
            :class="!selectedIndustryId ? 'text-primary font-semibold' : 'text-text-muted dark:text-slate-300'"
            @click="selectIndustry(undefined)"
          >Tất cả lĩnh vực</button>
          <button
            v-for="ind in industryStore.industries"
            :key="ind.id"
            type="button"
            class="w-full text-left px-3 py-2 text-sm hover:bg-slate-50 dark:hover:bg-slate-700 transition-colors cursor-pointer"
            :class="selectedIndustryId === ind.id
              ? 'text-primary font-semibold bg-blue-50 dark:bg-blue-900/20'
              : 'text-text-muted dark:text-slate-300'"
            @click="selectIndustry(ind.id)"
          >{{ ind.name }}</button>
        </div>
      </div>
    </div>

    <!-- Mức lương -->
    <div class="mb-4 pb-4 border-b border-slate-200 dark:border-slate-700">
      <p class="text-sm font-bold text-text-main dark:text-white mb-2.5">Mức lương</p>
      
      <!-- List preset -->
      <div class="grid grid-cols-2 gap-x-3 gap-y-1.5 mb-3">
        <label
          v-for="(preset, idx) in SALARY_PRESETS"
          :key="idx"
          class="flex items-center gap-2 py-0.5 cursor-pointer"
        >
          <input
            type="radio"
            name="salary"
            :value="idx"
            :checked="selectedSalaryIdx === idx"
            class="accent-primary w-4 h-4 shrink-0 cursor-pointer"
            @change="selectSalaryPreset(idx)"
          />
          <span
            class="text-sm leading-tight"
            :class="selectedSalaryIdx === idx ? 'text-primary font-semibold' : 'text-text-muted dark:text-slate-400'"
          >{{ preset.label }}</span>
        </label>
      </div>

      <!-- Khoảng lương tùy chỉnh -->
      <p class="text-xs font-bold text-text-muted mb-2">Hoặc nhập mức lương (triệu):</p>
      <div class="flex items-center gap-2 mb-1">
        <input
          v-model="customSalaryMinStr"
          type="number"
          min="0"
          step="1"
          placeholder="Từ"
          class="flex-1 min-w-0 px-2 py-2 border rounded-lg text-sm text-center bg-white dark:bg-surface-dark focus:outline-none focus:ring-0"
          :class="salaryError && selectedSalaryIdx === -1 ? 'border-red-500 focus:border-red-500' : 'border-slate-200 dark:border-slate-600 focus:border-primary'"
          @input="handleCustomInput"
          @keydown="preventNonDigits"
        />
        <span class="text-text-muted text-sm shrink-0">—</span>
        <input
          v-model="customSalaryMaxStr"
          type="number"
          min="0"
          step="1"
          placeholder="Đến"
          class="flex-1 min-w-0 px-2 py-2 border rounded-lg text-sm text-center bg-white dark:bg-surface-dark focus:outline-none focus:ring-0"
          :class="salaryError && selectedSalaryIdx === -1 ? 'border-red-500 focus:border-red-500' : 'border-slate-200 dark:border-slate-600 focus:border-primary'"
          @input="handleCustomInput"
          @keydown="preventNonDigits"
        />
      </div>

      <button
        type="button"
        class="mt-1 w-full py-2 rounded-lg text-sm font-semibold transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        :class="canApplyCustom ? 'bg-primary/10 text-primary cursor-pointer' : 'bg-slate-100 dark:bg-slate-700 text-text-muted dark:text-slate-300'"
        :disabled="!canApplyCustom"
        @click="applyCustomSalary"
      >Áp dụng mức nhập</button>
    </div>

    <!-- Nổi bật / Khẩn cấp -->
    <div class="mb-5">
      <p class="text-sm font-bold text-text-main dark:text-white mb-2.5">Ưu tiên hiển thị</p>
      <div class="flex flex-col gap-2">
        <label class="flex items-center gap-2.5 cursor-pointer group">
          <input
            type="checkbox"
            v-model="isFeatured"
            class="accent-primary w-4 h-4 rounded cursor-pointer"
          />
          <span class="text-sm" :class="isFeatured ? 'text-primary font-semibold' : 'text-text-muted dark:text-slate-400'">
            Tin nổi bật
          </span>
          <span class="ml-auto text-[10px] px-1.5 py-0.5 rounded-full bg-yellow-100 text-yellow-700 font-semibold">HOT</span>
        </label>
        <label class="flex items-center gap-2.5 cursor-pointer group">
          <input
            type="checkbox"
            v-model="isUrgent"
            class="accent-primary w-4 h-4 rounded cursor-pointer"
          />
          <span class="text-sm" :class="isUrgent ? 'text-primary font-semibold' : 'text-text-muted dark:text-slate-400'">
            Tuyển gấp
          </span>
          <span class="ml-auto text-[10px] px-1.5 py-0.5 rounded-full bg-red-100 text-red-600 font-semibold">GẤP</span>
        </label>
      </div>
    </div>

    <!-- Xóa lọc -->
    <button
      type="button"
      class="w-full text-center text-sm text-primary font-semibold py-2 hover:underline transition-all cursor-pointer"
      @click="clearAll"
    >Xóa lọc</button>

  </div>
</template>
