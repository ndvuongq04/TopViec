<template>
  <section class="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm dark:border-slate-800 dark:bg-slate-900">
    <div class="mb-5 flex items-start gap-3">
      <div
        v-if="icon"
        class="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-[#963131]/10 text-[#963131]"
      >
        <span class="material-symbols-outlined text-[20px]">{{ icon }}</span>
      </div>

      <div>
        <h3 class="text-lg font-bold text-slate-900 dark:text-slate-100">
          {{ title }}
        </h3>
        <p
          v-if="description"
          class="mt-1 text-sm leading-6 text-slate-500 dark:text-slate-400"
        >
          {{ description }}
        </p>
      </div>
    </div>

    <slot />
  </section>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  description?: string
  icon?: string
}>()
</script>
