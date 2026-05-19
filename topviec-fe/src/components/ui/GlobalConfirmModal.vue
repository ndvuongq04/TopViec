<template>
  <Teleport to="body">
    <div
      v-if="state.isOpen"
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-sm"
      @click.self="onCancel"
    >
      <!-- Modal Container -->
      <div class="layout-content-container flex flex-col w-full max-w-[400px] bg-white dark:bg-zinc-900 rounded-xl shadow-2xl overflow-hidden border border-primary/10">
        <!-- Icon and Content Section -->
        <div class="p-6 flex flex-col items-center text-center">
          <!-- Warning Icon Container -->
          <div 
            class="mb-4 flex h-14 w-14 items-center justify-center rounded-full"
            :class="iconContainerClass"
          >
            <span class="material-symbols-outlined !text-4xl" :data-icon="state.icon">{{ state.icon }}</span>
          </div>
          <h3 class="text-slate-900 dark:text-slate-100 tracking-tight text-xl font-bold leading-tight pb-2">
            {{ state.title }}
          </h3>
          <p class="text-slate-600 dark:text-slate-400 text-sm font-normal leading-relaxed px-2">
            {{ state.message }}
          </p>
        </div>
        <!-- Footer / Action Buttons -->
        <div class="bg-primary/5 dark:bg-primary/10 px-5 py-4 flex flex-col sm:flex-row gap-3">
          <button 
            class="flex-1 min-w-[120px] cursor-pointer items-center justify-center overflow-hidden rounded-lg h-10 px-6 bg-white dark:bg-zinc-800 border border-primary/20 text-slate-700 dark:text-slate-200 text-sm font-bold leading-normal tracking-wide transition-colors hover:bg-slate-50 dark:hover:bg-zinc-700"
            @click="onCancel"
          >
            <span class="truncate">{{ state.cancelText }}</span>
          </button>
          <button 
            class="flex-1 min-w-[120px] cursor-pointer items-center justify-center overflow-hidden rounded-lg h-10 px-6 text-white text-sm font-bold leading-normal tracking-wide transition-opacity hover:opacity-90 active:scale-[0.98]"
            :class="confirmButtonClass"
            @click="onConfirm"
          >
            <span class="truncate">{{ state.confirmText }}</span>
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted } from 'vue'
import { useConfirm } from '@/composables/useConfirm'

const { state, onConfirm, onCancel } = useConfirm()

const iconContainerClass = computed(() => {
  if (state.value.confirmColor === 'red') return 'bg-red-500/10 text-red-500'
  if (state.value.confirmColor === 'blue') return 'bg-blue-500/10 text-blue-500'
  return 'bg-primary/10 text-primary' // default primary
})

const confirmButtonClass = computed(() => {
  if (state.value.confirmColor === 'red') return 'bg-primary shadow-primary/20 hover:opacity-90'
  if (state.value.confirmColor === 'blue') return 'bg-blue-600 shadow-blue-500/20 hover:bg-blue-700'
  return 'bg-primary shadow-primary/20 hover:opacity-90' // default primary
})

// Focus trap and escape key handler
const handleEsc = (e: KeyboardEvent) => {
  if (e.key === 'Escape' && state.value.isOpen) {
    onCancel()
  }
  if (e.key === 'Enter' && state.value.isOpen) {
    // Only trigger on enter if we aren't already focused on a button
    // (This behavior might need to be adjusted based on accessibility requirements)
    if (document.activeElement?.tagName !== 'BUTTON') {
      onConfirm()
    }
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleEsc)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleEsc)
})
</script>

