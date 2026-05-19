<template>
  <button
    type="button"
    role="switch"
    :aria-checked="model"
    class="relative inline-flex w-11 h-6 shrink-0 rounded-full transition-colors duration-200 focus:outline-none focus-visible:ring-2 focus-visible:ring-primary focus-visible:ring-offset-2 cursor-pointer"
    :class="model ? 'bg-primary' : 'bg-slate-200 dark:bg-slate-700'"
    @click="model = !model"
  >
    <span
      class="pointer-events-none absolute top-1 left-0 w-4 h-4 bg-white rounded-full shadow transition-transform duration-200"
      :class="model ? 'translate-x-6' : 'translate-x-1'"
    />
  </button>
</template>

<script setup lang="ts">
const model = defineModel<boolean>({ default: false })
</script>
