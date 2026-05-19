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

    <!-- Search -->
    <div class="p-4 flex gap-4">
      <div class="flex-1 flex items-center bg-slate-50 dark:bg-white/5 rounded-lg px-4 border border-[#963131]/5 focus-within:ring-2 focus-within:ring-[#963131]/20 transition-all">
        <span class="material-symbols-outlined text-slate-400 text-xl">search</span>
        <input
          v-model="search"
          type="text"
          class="bg-transparent border-none focus:ring-0 text-sm w-full py-3 outline-none ml-2"
          placeholder="Tìm theo tên gói hoặc mã gói..."
        />
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { ResServicePackageDTO } from '@/types/servicePackage.types'

const props = defineProps<{ packages: ResServicePackageDTO[] }>()
const emit  = defineEmits<{ filter: [{ search: string; status: string }] }>()

const search    = ref('')
const activeTab = ref('')

const tabs = computed(() => [
  { value: '',         label: 'Tất cả',   count: props.packages.length },
  { value: 'active',   label: 'Đang bật', count: props.packages.filter(p => p.isActive).length },
  { value: 'inactive', label: 'Đã tắt',   count: props.packages.filter(p => !p.isActive).length },
])

watch([search, activeTab], () => {
  emit('filter', { search: search.value, status: activeTab.value })
})
</script>
