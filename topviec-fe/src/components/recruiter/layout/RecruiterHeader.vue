<template>
  <header class="sticky top-0 z-10 flex items-center justify-between px-8 py-4 bg-background-light/80 dark:bg-background-dark/80 backdrop-blur-md">
    <div>
      <h2 class="text-2xl font-bold">{{ title }}</h2>
      <p class="text-sm text-slate-500">Chào mừng trở lại, {{ userName }}</p>
    </div>
    <div class="flex items-center gap-4">
      <!-- Search -->
      <div class="relative hidden md:block ">
        <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">search</span>
        <input
          v-model="searchQuery"
          class="pl-10 pr-4 py-2 w-64 bg-white dark:bg-slate-800 border-none rounded-xl text-sm focus:ring-2 focus:ring-primary shadow-sm outline-none"
          placeholder="Tìm ứng viên, tin đăng..."
          type="text"
        />
      </div>

      <!-- Notifications -->
      <button class="p-2 rounded-xl bg-white dark:bg-slate-800 text-slate-600 dark:text-slate-400 shadow-sm relative cursor-pointer">
        <span class="material-symbols-outlined">notifications</span>
        <span
          v-if="hasNotification"
          class="absolute top-2 right-2 size-2 bg-red-500 rounded-full border-2 border-white dark:border-slate-800"
        ></span>
      </button>

      <!-- Tạo tin -->
      <router-link
        to="/recruiter/jobs/create"
        class="bg-primary text-white px-4 py-2 rounded-xl text-sm font-bold flex items-center gap-2 hover:bg-primary/90 transition-colors cursor-pointer"
      >
        <span class="material-symbols-outlined text-lg">add</span>
        Tạo tin mới
      </router-link>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref } from 'vue'

withDefaults(defineProps<{
  title?: string
  userName?: string
  hasNotification?: boolean
}>(), {
  title: 'Tổng quan',
  userName: 'bạn',
  hasNotification: true,
})

defineEmits<{
  'create-job': []
}>()

const searchQuery = ref('')
</script>