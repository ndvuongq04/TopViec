<template>
  <nav class="flex items-center gap-2 text-sm font-medium text-slate-500 dark:text-gray-400 overflow-x-auto whitespace-nowrap scrollbar-hide">
    <router-link 
      v-if="!hideHome"
      to="/" 
      class="hover:text-primary transition-all flex items-center gap-1 group"
    >
      <span class="material-symbols-outlined text-[20px] group-hover:scale-110 transition-transform">home</span>
      Trang chủ
    </router-link>
    
    <template v-for="(item, index) in items" :key="index">
      <span 
        v-if="!hideHome || index > 0"
        class="material-symbols-outlined text-slate-300 dark:text-gray-700 text-[18px]"
      >chevron_right</span>
      <router-link 
        v-if="item.to" 
        :to="item.to" 
        class="hover:text-primary transition-all"
      >
        {{ item.label }}
      </router-link>
      <span 
        v-else 
        class="text-slate-900 dark:text-white font-bold"
      >
        {{ item.label }}
      </span>
    </template>
  </nav>
</template>

<script setup lang="ts">
defineProps<{
  items: { label: string; to?: string | object }[]
  hideHome?: boolean
}>()
</script>

<style scoped>
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
