<template>
  <div>
    <div class="border-b border-slate-200 dark:border-slate-800 flex gap-8 px-2">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        class="py-4 text-sm font-bold relative transition-colors whitespace-nowrap cursor-pointer"
        :class="activeTab === tab.key
          ? 'text-[#963131] border-b-2 border-[#963131]'
          : 'text-slate-500 hover:text-[#963131]'"
        @click="$emit('update:activeTab', tab.key)"
      >
        {{ tab.label }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  activeTab: string
  tabs: { key: string; label: string }[]
}>()

defineEmits<{
  'update:activeTab': [value: string]
}>()
</script>
