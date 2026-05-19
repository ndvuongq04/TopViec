<template>
  <div class="relative" ref="containerRef">
    <!-- Trigger: pill for banner, clean for bar -->
    <button
      type="button"
      class="group flex items-center justify-between gap-2 transition-all duration-200 active:scale-95 focus:outline-none w-full"
      :class="[
        variant === 'banner'
          ? [
              isOpen
                ? 'bg-white/30 border-white/30 ring-2 ring-white/20 text-white'
                : 'bg-white/15 hover:bg-white/25 border-white/10 text-white/90',
              'px-4 py-2 rounded-full border backdrop-blur-md text-sm font-medium'
            ]
          : [
              'px-0 py-2 text-base border-none bg-transparent',
              isOpen || selectedOption ? 'text-gray-800' : 'text-gray-400'
            ],
        'cursor-pointer'
      ]"
      @click="toggleDropdown"
    >
      <span class="truncate" :class="variant === 'banner' ? 'max-w-[100px]' : 'flex-1 text-left'">
        {{ selectedOption ? selectedOption.name : placeholder }}
      </span>
      <span
        class="material-symbols-outlined flex-shrink-0 transition-transform duration-300"
        :class="[
          isOpen ? 'rotate-180 opacity-100' : 'opacity-60',
          variant === 'banner' ? 'text-[15px]' : 'text-[18px] text-gray-400'
        ]"
      >
        expand_more
      </span>
    </button>

    <!-- Dropdown Panel -->
    <Transition
      enter-active-class="transition duration-200 ease-out origin-top-left"
      enter-from-class="opacity-0 scale-95 -translate-y-1"
      enter-to-class="opacity-100 scale-100 translate-y-0"
      leave-active-class="transition duration-150 ease-in origin-top-left"
      leave-from-class="opacity-100 scale-100 translate-y-0"
      leave-to-class="opacity-0 scale-95 -translate-y-1"
    >
      <div
        v-if="isOpen"
        class="absolute left-0 z-[100] min-w-[220px] rounded-2xl overflow-hidden"
        :class="variant === 'bar' ? 'top-full mt-6' : 'top-full mt-2'"
        style="box-shadow: 0 25px 50px -12px rgba(0,0,0,0.35), 0 0 0 1px rgba(255,255,255,0.08);"
      >
        <!-- Glass background -->
        <div class="bg-white/95 dark:bg-slate-900/95 backdrop-blur-2xl">
          <!-- Search Header -->
          <div class="px-3 pt-3 pb-2">
            <div class="relative flex items-center">
              <span class="material-symbols-outlined absolute left-3 text-[17px] text-slate-400 pointer-events-none transition-colors duration-150" style="font-variation-settings: 'wght' 400">
                search
              </span>
              <input
                ref="searchInputRef"
                v-model="searchQuery"
                type="text"
                class="w-full pl-9 pr-3 py-2 text-base rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 text-slate-800 dark:text-white placeholder-slate-400 focus:outline-none focus:border-blue-400 focus:ring-2 focus:ring-blue-400/15 transition-all"
                :placeholder="`Tìm ${placeholder.toLowerCase()}...`"
                @click.stop
                @keydown.escape="isOpen = false"
              />
            </div>
          </div>

          <!-- Divider -->
          <div class="h-px bg-slate-100 dark:bg-slate-800 mx-3" />

          <!-- Options -->
          <ul class="p-2 max-h-[260px] overflow-y-auto banner-dropdown-scroll">
            <!-- Empty State -->
            <li v-if="filteredOptions.length === 0" class="py-6 flex flex-col items-center gap-1 text-center">
              <span class="material-symbols-outlined text-slate-300 text-2xl">search_off</span>
              <span class="text-sm text-slate-400">Không tìm thấy kết quả</span>
            </li>

            <!-- Option Items -->
            <li
              v-for="option in filteredOptions"
              :key="option.id"
              class="group flex items-center justify-between gap-2 px-3 py-2.5 rounded-xl cursor-pointer transition-all duration-150 text-base"
              :class="
                modelValue?.toString() === option.id?.toString()
                  ? 'bg-blue-500 text-white font-semibold shadow-md shadow-blue-500/20'
                  : 'text-slate-700 dark:text-slate-200 hover:bg-slate-50 dark:hover:bg-slate-800/60 hover:text-blue-600 dark:hover:text-blue-400'
              "
              @click.stop="selectOption(option)"
            >
              <span class="truncate">{{ option.name }}</span>
              <span
                v-if="modelValue?.toString() === option.id?.toString()"
                class="material-symbols-outlined text-[16px] shrink-0"
                style="font-variation-settings: 'FILL' 1;"
              >
                check_circle
              </span>
            </li>
          </ul>

          <!-- Footer: Clear Selection -->
          <div v-if="modelValue" class="px-3 pb-3 pt-1">
            <button
              class="w-full flex items-center justify-center gap-1.5 py-1.5 rounded-lg text-xs font-medium text-slate-400 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/10 transition-all"
              @click.stop="clearOption"
            >
              <span class="material-symbols-outlined text-[14px]">close</span>
              Bỏ chọn
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'

export interface BannerSelectOption {
  id: string | number
  name: string
}

const props = withDefaults(defineProps<{
  modelValue: string | number | null
  options: BannerSelectOption[]
  placeholder?: string
  variant?: 'banner' | 'bar'
}>(), {
  placeholder: 'Chọn...',
  variant: 'banner',
})

const emit = defineEmits<{
  'update:modelValue': [value: string | number | null]
  'change': [option: BannerSelectOption | null]
}>()

const isOpen = ref(false)
const searchQuery = ref('')
const containerRef = ref<HTMLElement | null>(null)
const searchInputRef = ref<HTMLInputElement | null>(null)

const selectedOption = computed(() => {
  if (!props.modelValue) return null
  return props.options.find(o => o.id?.toString() === props.modelValue?.toString()) || null
})

const filteredOptions = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()
  if (!q) return props.options
  return props.options.filter(o => o.name.toLowerCase().includes(q))
})

function toggleDropdown() {
  isOpen.value = !isOpen.value
  if (isOpen.value) {
    searchQuery.value = ''
    setTimeout(() => searchInputRef.value?.focus(), 60)
  }
}

function selectOption(option: BannerSelectOption) {
  emit('update:modelValue', option.id)
  emit('change', option)
  isOpen.value = false
}

function clearOption() {
  emit('update:modelValue', null)
  emit('change', null)
  isOpen.value = false
}

function handleClickOutside(event: MouseEvent) {
  if (containerRef.value && !containerRef.value.contains(event.target as Node)) {
    isOpen.value = false
  }
}

onMounted(() => document.addEventListener('click', handleClickOutside))
onUnmounted(() => document.removeEventListener('click', handleClickOutside))
</script>

<style scoped>
.banner-dropdown-scroll::-webkit-scrollbar {
  width: 4px;
}
.banner-dropdown-scroll::-webkit-scrollbar-track {
  background: transparent;
}
.banner-dropdown-scroll::-webkit-scrollbar-thumb {
  background: rgba(148, 163, 184, 0.25);
  border-radius: 99px;
}
.banner-dropdown-scroll::-webkit-scrollbar-thumb:hover {
  background: rgba(148, 163, 184, 0.5);
}
</style>
