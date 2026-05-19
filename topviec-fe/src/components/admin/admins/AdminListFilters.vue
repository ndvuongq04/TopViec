<template>
  <div>
    <!-- Tabs -->
    <div class="flex border-b border-slate-200 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-800/30">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        class="px-6 py-4 text-sm font-medium border-b-2 transition-colors cursor-pointer"
        :class="activeTab === tab.value
          ? 'font-bold border-[#963131] text-[#963131]'
          : 'text-slate-500 hover:text-slate-700 dark:hover:text-slate-300 border-transparent'"
        @click="$emit('update:activeTab', tab.value)"
      >
        {{ tab.label }}
        <span v-if="tab.value === '' && totalCount > 0"> ({{ totalCount }})</span>
      </button>
    </div>

    <!-- Search & Sort -->
    <div class="p-4 border-b border-slate-200 dark:border-slate-800 flex flex-wrap gap-4 items-center justify-between">
      <div class="flex gap-2">
        <div class="relative w-80">
          <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 text-lg">search</span>
          <input
            :value="search"
            class="w-full pl-10 pr-4 py-2 text-sm border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 rounded-lg focus:ring-[#963131]/20 focus:border-[#963131]"
            placeholder="Tìm tên, email, ID... (Nhấn Enter để tìm)"
            type="text"
            @keyup.enter="$emit('search', ($event.target as HTMLInputElement).value)"
          />
        </div>
      </div>
      <div class="flex items-center gap-2">
        <span class="text-xs text-slate-500 font-medium">Sắp xếp theo:</span>
        <select
          :value="sort"
          class="text-sm border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 rounded-lg focus:ring-[#963131]/20"
          @change="$emit('update:sort', ($event.target as HTMLSelectElement).value)"
        >
          <option value="createdAt,desc">Ngày tạo gần nhất</option>
          <option value="fullName,asc">Tên (A-Z)</option>
          <option value="isActive,desc">Trạng thái</option>
        </select>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  search: string
  activeTab: string
  sort: string
  totalCount: number
}>()

defineEmits<{
  'update:activeTab': [value: string]
  'update:sort': [value: string]
  'search': [value: string]
}>()

const tabs = [
  { label: 'Tất cả', value: '' },
  { label: 'Super Admin', value: 'super_admin' },
  { label: 'Content Moderator', value: 'content_moderator' },
  { label: 'Support Admin', value: 'support_admin' },
  { label: 'Finance Admin', value: 'finance_admin' },
]
</script>