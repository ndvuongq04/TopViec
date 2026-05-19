<script setup lang="ts">
import { useToast } from '@/composables/useToast'

const { toasts, remove } = useToast()

const icons: Record<string, string> = {
  success: 'check_circle',
  error:   'cancel',
  warning: 'warning',
  info:    'info',
}
</script>

<template>
  <Teleport to="body">
    <div class="fixed top-5 right-5 z-99999 flex flex-col gap-3 pointer-events-none" style="min-width:320px; max-width:400px;">
      <TransitionGroup name="toast" tag="div" class="flex flex-col gap-3">
        <div
          v-for="toast in toasts"
          :key="toast.id"
          class="pointer-events-auto flex items-center p-4 bg-white border-l-4 rounded-[8px] shadow-[0_4px_20px_rgba(0,0,0,0.08)] ring-1 ring-black/5 cursor-pointer select-none"
          :class="{
            'border-[#10b981]':  toast.type === 'success',
            'border-[#ef4444]':  toast.type === 'error',
            'border-[#f59e0b]':  toast.type === 'warning',
            'border-[#3b82f6]':  toast.type === 'info',
          }"
          @click="remove(toast.id)"
        >
          <!-- Icon Container -->
          <div
            class="flex-shrink-0 w-10 h-10 rounded-full flex items-center justify-center"
            :class="{
              'bg-[#10b981]/10 text-[#10b981]':  toast.type === 'success',
              'bg-[#ef4444]/10 text-[#ef4444]':  toast.type === 'error',
              'bg-[#f59e0b]/10 text-[#f59e0b]':  toast.type === 'warning',
              'bg-[#3b82f6]/10 text-[#3b82f6]':  toast.type === 'info',
            }"
          >
            <span class="material-symbols-outlined text-[20px]">{{ icons[toast.type] }}</span>
          </div>

          <!-- Content -->
          <div class="ml-4 flex-1">
            <p class="text-sm font-semibold text-gray-900 leading-snug">{{ toast.title }}</p>
            <p v-if="toast.message" class="text-sm text-gray-500 mt-0.5 leading-snug">{{ toast.message }}</p>
          </div>

          <!-- Close -->
          <button
            class="ml-auto flex-shrink-0 text-gray-400 hover:text-gray-600 transition-colors pt-0.5"
            @click.stop="remove(toast.id)"
          >
            <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
              <path clip-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" fill-rule="evenodd"></path>
            </svg>
          </button>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<style scoped>
/* Slide in from right */
.toast-enter-active { transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1); }
.toast-leave-active { transition: all 0.25s cubic-bezier(0.06, 0.71, 0.55, 1); }
.toast-enter-from, .toast-leave-to {
  opacity: 0;
  transform: translateX(100%);
}
</style>