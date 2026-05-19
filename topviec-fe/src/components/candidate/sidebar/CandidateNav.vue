<script setup lang="ts">
import { useRoute } from "vue-router";

const route = useRoute();

const navItems = [
  { name: "Tìm việc",     icon: "work",         path: "/" },
  { name: "Việc đã lưu",  icon: "bookmark",     path: "/saved-jobs" },
  { name: "Đã ứng tuyển", icon: "send",         path: "/applied-jobs" },
  { name: "Phỏng vấn",    icon: "chat_bubble",   path: "/interviews" },
];
</script>

<template>
  <nav
    class="flex flex-col gap-2 bg-white dark:bg-surface-dark p-3 rounded-3xl border border-slate-200 dark:border-slate-800 shadow-sm"
  >
    <router-link
      v-for="item in navItems"
      :key="item.path"
      :to="item.path"
      :class="[
        'flex items-center gap-3 px-3 py-3 rounded-xl transition-colors',
        route.path === item.path
          ? 'bg-primary/10 text-primary font-semibold'
          : 'text-text-muted dark:text-gray-400 hover:bg-slate-50 dark:hover:bg-slate-800/50 hover:text-text-main dark:hover:text-white',
      ]"
    >
      <span class="material-symbols-outlined text-xl">{{ item.icon }}</span>
      <span class="text-sm">{{ item.name }}</span>
    </router-link>
  </nav>
</template>
