<template>
  <div class="bg-white dark:bg-slate-900 rounded-xl shadow-sm border border-[#963131]/5 overflow-hidden">

    <!-- Tabs theo Trạng thái Ứng viên -->
    <div class="flex border-b border-[#963131]/5">
      <button
        v-for="tab in statusTabs"
        :key="tab.value"
        class="px-6 py-4 text-sm font-medium transition-colors whitespace-nowrap cursor-pointer"
        :class="activeTab === tab.value
          ? 'font-bold border-b-2 border-[#963131] text-[#963131] bg-[#963131]/5'
          : 'text-slate-500 hover:text-[#963131]'"
        @click="$emit('update:activeTab', tab.value)"
      >
        {{ tab.label }}
        <span
          class="ml-1 text-xs"
          :class="activeTab === tab.value ? 'text-[#963131]' : 'text-slate-400'"
        >({{ tab.count }})</span>
      </button>
    </div>

    <!-- Search -->
    <div class="p-4 flex gap-4">
      <div class="flex-1 flex items-center bg-slate-50 dark:bg-white/5 rounded-lg px-4 border border-[#963131]/5 focus-within:ring-2 focus-within:ring-[#963131]/20 transition-all">
        <button class="flex items-center justify-center p-1 -ml-1 hover:bg-[#963131]/10 rounded-md transition-colors cursor-pointer" @click="triggerSearch">
          <span class="material-symbols-outlined text-slate-400 hover:text-[#963131] transition-colors">search</span>
        </button>
        <input
          v-model="localSearch"
          class="bg-transparent border-none focus:ring-0 text-sm w-full py-3 outline-none"
          placeholder="Tìm theo Tên ứng viên, Email..."
          type="text"
          @keyup.enter="triggerSearch"
        />
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { UserStatus, USER_STATUS_LABELS } from '@/constants/user.constants'

const props = defineProps<{
  activeTab: string        // '' = tất cả, hoặc status value
  search: string
  candidates: any[]        // list gốc để đếm count
}>()

const emit = defineEmits<{
  'update:activeTab': [value: string]
  'update:search':    [value: string]
}>()

// ─── Search handling ─────────────────────────────────────────────────────────────
const localSearch = ref(props.search)

watch(() => props.search, (newVal) => {
  localSearch.value = newVal
})

function triggerSearch() {
  emit('update:search', localSearch.value)
}

// ─── Tabs ────────────────────────────────────────────────────────────────────────
const statusTabs = computed(() => {
  const all = props.candidates
  return [
    { value: '',                      label: 'Tất cả',                count: all.length },
    { value: UserStatus.ACTIVE,       label: USER_STATUS_LABELS[UserStatus.ACTIVE],    count: all.filter(c => c.status === UserStatus.ACTIVE).length },
    { value: UserStatus.PENDING,      label: USER_STATUS_LABELS[UserStatus.PENDING],   count: all.filter(c => c.status === UserStatus.PENDING).length },
    { value: UserStatus.LOCKED_PERM,  label: USER_STATUS_LABELS[UserStatus.LOCKED_PERM], count: all.filter(c => c.status === UserStatus.LOCKED_PERM).length },
  ]
})
</script>
