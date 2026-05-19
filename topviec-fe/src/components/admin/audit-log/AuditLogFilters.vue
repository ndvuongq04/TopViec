<template>
  <div class="bg-white dark:bg-slate-900 rounded-xl shadow-sm border border-[#963131]/5 mb-8">
    <div class="p-4 flex flex-wrap gap-3 items-center">
      
      <!-- Search -->
      <div class="flex-1 min-w-64 flex items-center bg-slate-50 dark:bg-white/5 rounded-lg px-4 border border-[#963131]/5 focus-within:ring-2 focus-within:ring-[#963131]/20 transition-all">
        <span class="material-symbols-outlined text-slate-400 text-xl">search</span>
        <input 
          v-model="localFilters.keyword"
          type="text" 
          class="bg-transparent border-none focus:ring-0 text-base w-full py-3 outline-none ml-2 text-slate-700 dark:text-slate-300" 
          placeholder="Tìm kiếm theo Email, Target ID, hoặc Mô tả..."
          @keyup.enter="handleApply"
        />
      </div>

      <!-- Vai trò (Chỉ dành cho Super Admin) -->
      <select 
        v-if="isSuperAdmin"
        v-model="localFilters.userRole"
        class="shrink-0 bg-slate-50 dark:bg-white/5 border border-[#963131]/5 rounded-lg text-base pl-4 pr-8 py-2.5 focus:ring-2 focus:ring-[#963131]/20 outline-none cursor-pointer text-slate-700 dark:text-slate-300"
        @change="handleImmediateChange"
      >
        <option value="">Tất cả vai trò</option>
        <option v-for="opt in filteredRoleOptions" :key="opt.value" :value="opt.value">
          {{ opt.label }}
        </option>
      </select>

      <!-- Danh mục -->
      <div class="min-w-[200px]">
        <SearchableSelect 
          v-model="localFilters.category"
          :options="mappedCategories"
          placeholder="Tất cả danh mục"
          @change="handleImmediateChange"
        />
      </div>
      
      <!-- Hành động -->
      <div class="min-w-[200px]">
        <SearchableSelect 
          v-model="localFilters.action"
          :options="mappedActions"
          placeholder="Tất cả hành động"
          @change="handleImmediateChange"
        />
      </div>

      <!-- Mức độ (Chỉ hiện cho Audit) -->
      <select 
        v-if="activeType === 'AUDIT'"
        v-model="localFilters.severity"
        class="shrink-0 bg-slate-50 dark:bg-white/5 border border-[#963131]/5 rounded-lg text-base pl-4 pr-8 py-2.5 focus:ring-2 focus:ring-[#963131]/20 outline-none cursor-pointer text-slate-700 dark:text-slate-300"
        @change="handleImmediateChange"
      >
        <option value="">Tất cả mức độ</option>
        <option v-for="opt in SEVERITY_OPTIONS" :key="opt.value" :value="opt.value">
          {{ opt.label }}
        </option>
      </select>

      <!-- Kết quả -->
      <select 
        v-model="localFilters.status"
        class="shrink-0 bg-slate-50 dark:bg-white/5 border border-[#963131]/5 rounded-lg text-base pl-4 pr-8 py-2.5 focus:ring-2 focus:ring-[#963131]/20 outline-none cursor-pointer text-slate-700 dark:text-slate-300"
        @change="handleImmediateChange"
      >
        <option value="">Tất cả kết quả</option>
        <option v-for="opt in LOG_STATUS_OPTIONS" :key="opt.value" :value="opt.value">
          {{ opt.label }}
        </option>
      </select>

      <!-- Date range -->
      <div class="flex items-center gap-2 bg-slate-50 dark:bg-white/5 border border-[#963131]/5 rounded-lg px-4 py-2 focus-within:ring-2 focus-within:ring-[#963131]/20 transition-all">
        <span class="text-xs font-bold text-slate-500 uppercase tracking-wide">Từ</span>
        <input 
          v-model="localFilters.startDate"
          type="date" 
          class="bg-transparent border-none text-base text-slate-700 dark:text-slate-300 outline-none" 
        />
        <span class="text-xs font-bold text-slate-500 uppercase tracking-wide">Đến</span>
        <input 
          v-model="localFilters.endDate"
          type="date" 
          class="bg-transparent border-none text-base text-slate-700 dark:text-slate-300 outline-none" 
        />
      </div>

      <!-- Actions -->
      <button 
        class="shrink-0 bg-[#963131] hover:bg-[#963131]/90 text-white px-5 py-2.5 rounded-lg text-base font-bold transition-all shadow-sm cursor-pointer"
        @click="handleApply"
      >
        Lọc
      </button>
      <button 
        class="shrink-0 text-base font-medium text-slate-500 hover:text-[#963131] transition-colors cursor-pointer"
        @click="handleReset"
      >
        Xoá bộ lọc
      </button>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed, onMounted } from 'vue'
import SearchableSelect from '@/components/ui/SearchableSelect.vue'
import { adminUserService } from '@/services/adminUser.service'
import type { AdminRole } from '@/types/adminUser.types'
import type { LogQueryParams } from '@/types/logs.types'
import { 
  LOG_CATEGORY_OPTIONS, 
  LOG_ACTION_TYPE_OPTIONS, 
  SEVERITY_OPTIONS,
  LOG_STATUS_OPTIONS,
  USER_ROLE_OPTIONS
} from '@/constants/logs.constants'

const props = defineProps<{
  modelValue: LogQueryParams
  activeType: 'AUDIT' | 'BUSINESS'
}>()

const emit = defineEmits<{
  'update:modelValue': [value: LogQueryParams]
  'apply': []
}>()

const localFilters = ref<LogQueryParams>({ 
  action: '',
  category: '',
  severity: '',
  status: '',
  keyword: '',
  userRole: '',
  startDate: '',
  endDate: '',
  ...props.modelValue 
})

const mappedCategories = computed(() => 
  LOG_CATEGORY_OPTIONS.map(opt => ({ id: opt.value, name: opt.label }))
)

const mappedActions = computed(() => 
  LOG_ACTION_TYPE_OPTIONS.map(opt => ({ id: opt.value, name: opt.label }))
)

const ADMIN_ROLE_VALUES = ['super_admin', 'content_moderator', 'support_admin', 'finance_admin']
const filteredRoleOptions = computed(() => 
  USER_ROLE_OPTIONS.filter(opt => ADMIN_ROLE_VALUES.includes(opt.value))
)

const currentAdminRole = ref<AdminRole | null>(null)
const isSuperAdmin = computed(() => currentAdminRole.value === 'super_admin')

onMounted(async () => {
  try {
    const profile = await adminUserService.getMyProfile()
    currentAdminRole.value = profile.adminRole
  } catch {
    // Fallback or silence
  }
})

// Đồng bộ khi props thay đổi (ví dụ reset từ ngoài)
watch(() => props.modelValue, (newVal) => {
  localFilters.value = { ...newVal }
}, { deep: true })

// Đồng bộ ngược lại cho v-model
watch(localFilters, (newVal) => {
  emit('update:modelValue', newVal)
}, { deep: true })

function handleImmediateChange() {
  emit('apply')
}

function handleApply() {
  emit('apply')
}

function handleReset() {
  const resetFilters = {
    userId: null as any,
    action: '',
    category: '',
    severity: '',
    status: '',
    keyword: '',
    userRole: '',
    startDate: '',
    endDate: '',
    page: 0,
    size: localFilters.value.size || 20
  }
  localFilters.value = resetFilters
  emit('update:modelValue', resetFilters)
  emit('apply')
}
</script>