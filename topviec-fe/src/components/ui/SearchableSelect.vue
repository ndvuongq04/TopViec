<template>
  <div class="relative" ref="containerRef">
    <div
      class="w-full px-4 py-3 rounded-xl border border-slate-300 dark:border-slate-600 dark:bg-slate-700 focus-within:border-primary focus-within:ring-4 focus-within:ring-primary/10 transition-all flex items-center justify-between"
      :class="disabled ? 'cursor-not-allowed bg-slate-50 opacity-60' : 'cursor-pointer'"
      @click="toggleDropdown"
    >
      <span v-if="selectedItem" class="text-slate-900 dark:text-slate-100 truncate flex-1 pr-2">{{ selectedItem.name }}</span>
      <span v-else class="text-slate-400 truncate flex-1 pr-2">{{ placeholder }}</span>
      <span class="material-symbols-outlined text-slate-400 transition-transform duration-200" :class="{ 'rotate-180': isOpen }">expand_more</span>
    </div>

    <div
      v-if="isOpen"
      class="absolute z-50 w-full mt-2 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl shadow-xl overflow-hidden"
    >
      <div class="p-2 border-b border-slate-100 dark:border-slate-700">
        <div class="relative">
          <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 text-sm">search</span>
          <input
            ref="searchInputRef"
            v-model="searchQuery"
            type="text"
            class="w-full pl-9 pr-4 py-2 text-sm bg-slate-50 dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-lg focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary"
            placeholder="Tìm kiếm..."
            @click.stop
          />
        </div>
      </div>
      <ul class="max-h-60 overflow-y-auto p-1">
        <li
          v-if="filteredOptions.length === 0"
          class="px-4 py-3 text-sm text-slate-500 text-center"
        >
          Không tìm thấy kết quả
        </li>
        <li
          v-for="option in filteredOptions"
          :key="option.id"
          class="px-4 py-2.5 text-sm rounded-lg cursor-pointer transition-colors flex items-center justify-between"
          :class="modelValue?.toString() === option.id?.toString() ? 'bg-primary/10 text-primary font-semibold' : 'hover:bg-slate-50 dark:hover:bg-slate-700/50 text-slate-700 dark:text-slate-200'"
          @click="selectOption(option)"
        >
          <span class="truncate">{{ option.name }}</span>
          <span v-if="modelValue?.toString() === option.id?.toString()" class="material-symbols-outlined text-[18px]">check</span>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'

export interface SelectOption {
  id: string | number
  name: string
}

const props = withDefaults(defineProps<{
  modelValue: string | number | null
  options: SelectOption[]
  placeholder?: string
  disabled?: boolean
}>(), {
  placeholder: '-- Chọn --',
  disabled: false,
})

const emit = defineEmits<{
  'update:modelValue': [value: string | number]
  'change': [option: SelectOption]
}>()

const isOpen = ref(false)
const searchQuery = ref('')
const containerRef = ref<HTMLElement | null>(null)
const searchInputRef = ref<HTMLInputElement | null>(null)

const selectedItem = computed(() => {
  if (props.modelValue == null || props.modelValue === '') return null
  return props.options.find(opt => opt.id?.toString() === props.modelValue?.toString()) || null
})

const filteredOptions = computed(() => {
  if (!searchQuery.value.trim()) return props.options
  const query = searchQuery.value.toLowerCase().trim()
  return props.options.filter(opt => opt.name.toLowerCase().includes(query))
})

function toggleDropdown() {
  if (props.disabled) return
  isOpen.value = !isOpen.value
  if (isOpen.value) {
    searchQuery.value = ''
    setTimeout(() => {
      searchInputRef.value?.focus()
    }, 50)
  }
}

function selectOption(option: SelectOption) {
  emit('update:modelValue', option.id)
  emit('change', option)
  isOpen.value = false
}

function handleClickOutside(event: MouseEvent) {
  if (containerRef.value && !containerRef.value.contains(event.target as Node)) {
    isOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>
