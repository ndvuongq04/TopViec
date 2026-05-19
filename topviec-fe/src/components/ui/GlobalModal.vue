<template>
  <Teleport to="body">
    <div
      v-if="visible"
      class="fixed inset-0 z-[100] flex items-center justify-center p-4 sm:p-6 bg-slate-900/60 backdrop-blur-sm transition-opacity"
      @click.self="$emit('close')"
    >
      <div 
        class="bg-white dark:bg-slate-900 w-full rounded-2xl shadow-2xl overflow-hidden flex flex-col max-h-[90vh]"
        :class="maxWidthClass"
      >
        <!-- Modal Header -->
        <div class="px-8 py-6 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between bg-slate-50/50 dark:bg-slate-900/50 shrink-0">
          <div class="flex items-center gap-3">
            <div v-if="icon" class="size-10 rounded-xl flex items-center justify-center shrink-0" :class="variantClasses.icon">
              <span class="material-symbols-outlined text-xl">{{ icon }}</span>
            </div>
            <div>
              <h3 class="text-xl font-bold text-slate-900 dark:text-slate-100 tracking-tight">{{ title }}</h3>
              <p v-if="subtitle" class="text-sm text-slate-500 mt-0.5" v-html="subtitle"></p>
            </div>
          </div>
          <button
            class="p-2 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-xl hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors cursor-pointer"
            @click="$emit('close')"
          >
            <span class="material-symbols-outlined">close</span>
          </button>
        </div>

        <!-- Modal Body (Slot) -->
        <div class="p-8 overflow-y-auto custom-scrollbar flex-1">
          <slot></slot>
        </div>

        <!-- Modal Footer -->
        <div 
          v-if="$slots.footer || showDefaultFooter"
          class="px-8 py-5 border-t border-slate-200 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-900/50 flex items-center justify-end gap-3 shrink-0"
        >
          <slot name="footer">
            <button
              class="px-5 py-2.5 text-sm font-bold text-slate-600 dark:text-slate-300 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-700 rounded-xl transition-all cursor-pointer"
              type="button"
              @click="$emit('close')"
            >
              {{ cancelText }}
            </button>
            <button
              :form="formId"
              type="submit"
              class="px-6 py-2.5 rounded-xl font-bold text-sm transition-all disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer flex items-center gap-2"
              :class="variantClasses.confirm"
              :disabled="loading"
              @click="$emit('confirm')"
            >
              <span v-if="loading" class="material-symbols-outlined animate-spin text-[18px]">progress_activity</span>
              <span v-else-if="confirmIcon" class="material-symbols-outlined text-[18px]">{{ confirmIcon }}</span>
              {{ loading ? loadingText : confirmText }}
            </button>
          </slot>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(defineProps<{
  visible: boolean
  title: string
  subtitle?: string
  icon?: string
  maxWidth?: 'sm' | 'md' | 'lg' | 'xl' | '2xl' | '3xl' | '4xl' | '5xl'
  variant?: 'primary' | 'danger'
  
  // Footer props
  showDefaultFooter?: boolean
  cancelText?: string
  confirmText?: string
  confirmIcon?: string
  loading?: boolean
  loadingText?: string
  formId?: string
}>(), {
  maxWidth: '2xl',
  variant: 'primary',
  showDefaultFooter: true,
  cancelText: 'Hủy bỏ',
  confirmText: 'Xác nhận',
  confirmIcon: 'check_circle',
  loading: false,
  loadingText: 'Đang xử lý...',
})

defineEmits<{
  close: []
  confirm: []
}>()

const maxWidthClass = computed(() => {
  const map: Record<string, string> = {
    'sm': 'max-w-sm',
    'md': 'max-w-md',
    'lg': 'max-w-lg',
    'xl': 'max-w-xl',
    '2xl': 'max-w-2xl',
    '3xl': 'max-w-3xl',
    '4xl': 'max-w-4xl',
    '5xl': 'max-w-5xl',
  }
  return map[props.maxWidth] || 'max-w-2xl'
})

const variantClasses = computed(() => {
  if (props.variant === 'danger') {
    return {
      icon: 'bg-[#963131]/10 text-[#963131]',
      confirm: 'bg-[#963131] hover:bg-[#963131]/90 text-white shadow-lg shadow-[#963131]/20'
    }
  }
  return {
    icon: 'bg-primary/10 text-primary',
    confirm: 'bg-primary hover:bg-primary/90 text-white shadow-lg shadow-primary/20'
  }
})
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #cbd5e1;
  border-radius: 20px;
}
.dark .custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #334155;
}
</style>
