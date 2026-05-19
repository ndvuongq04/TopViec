<template>
  <div class="bg-white dark:bg-slate-900 rounded-xl shadow-sm border border-[#963131]/5 overflow-hidden">

    <!-- Tabs trạng thái -->
    <div class="flex border-b border-[#963131]/5">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        class="px-6 py-4 text-sm font-medium transition-colors whitespace-nowrap cursor-pointer"
        :class="activeTab === tab.value
          ? 'font-bold border-b-2 border-[#963131] text-[#963131] bg-[#963131]/5'
          : 'text-slate-500 hover:text-[#963131]'"
        @click="activeTab = tab.value"
      >
        {{ tab.label }}
        <span
          class="ml-1 text-xs"
          :class="activeTab === tab.value ? 'text-[#963131]' : 'text-slate-400'"
        >({{ tab.count }})</span>
      </button>
    </div>

    <!-- Search + Nhóm -->
    <div class="p-4 flex gap-4">
      <div class="flex-1 flex items-center bg-slate-50 dark:bg-white/5 rounded-lg px-4 border border-[#963131]/5 focus-within:ring-2 focus-within:ring-[#963131]/20 transition-all">
        <span class="material-symbols-outlined text-slate-400 text-xl">search</span>
        <input
          v-model="search"
          type="text"
          class="bg-transparent border-none focus:ring-0 text-sm w-full py-3 outline-none ml-2"
          placeholder="Tìm theo tên dịch vụ..."
        />
      </div>

      <select
        v-model="group"
        class="shrink-0 min-w-48 bg-slate-50 dark:bg-white/5 border border-[#963131]/5 rounded-lg text-sm px-4 py-2 focus:ring-2 focus:ring-[#963131]/20 outline-none cursor-pointer text-slate-700 dark:text-slate-300"
      >
        <option value="">Tất cả nhóm</option>
        <option value="job">Nhóm tin tuyển dụng</option>
        <option value="profile">Nhóm hồ sơ &amp; ứng viên</option>
        <option value="brand">Nhóm thương hiệu</option>
      </select>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { ServiceGroup } from './RetailServiceGroup.vue'

const props = defineProps<{ groups: ServiceGroup[] }>()
const emit = defineEmits<{ filter: [{ search: string; group: string; status: string }] }>()

const search    = ref('')
const group     = ref('')
const activeTab = ref('')

const allServices = computed(() => props.groups.flatMap(g => g.services))

const tabs = computed(() => [
  { value: '',         label: 'Tất cả',   count: allServices.value.length },
  { value: 'active',   label: 'Đang bật', count: allServices.value.filter(s => s.active).length },
  { value: 'inactive', label: 'Đã tắt',   count: allServices.value.filter(s => !s.active).length },
])

watch([search, group, activeTab], () => {
  emit('filter', { search: search.value, group: group.value, status: activeTab.value })
})
</script>
