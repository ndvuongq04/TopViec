<template>
  <header class="h-16 border-b border-slate-200 dark:border-white/10 flex items-center justify-between px-8 sticky top-0 bg-white/80 dark:bg-[#1e1414]/80 backdrop-blur-md z-40">

    <!-- Search -->
    <div class="flex-1 max-w-xl">
      <div class="relative group">
        <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-[#963131] transition-colors">search</span>
        <input
          v-model="searchQuery"
          class="w-full pl-10 pr-4 py-2 bg-slate-100 dark:bg-white/5 border-none rounded-lg focus:ring-2 focus:ring-[#963131]/20 text-sm outline-none"
          placeholder="Tìm kiếm hệ thống..."
          type="text"
        />
      </div>
    </div>

    <!-- Right -->
    <div class="flex items-center gap-6">

      <!-- Notification -->
      <button class="relative text-slate-500 hover:text-[#963131] transition-colors">
        <span class="material-symbols-outlined">notifications</span>
        <span class="absolute top-0 right-0 w-2 h-2 bg-[#963131] rounded-full border-2 border-white"></span>
      </button>

      <!-- Admin info -->
      <div class="flex items-center gap-3 border-l border-slate-200 dark:border-white/10 pl-6">
        <div class="text-right">
          <p class="text-sm font-bold">{{ adminName }}</p>
          <p class="text-xs text-slate-500 font-medium uppercase tracking-wider">{{ adminRole }}</p>
        </div>
        <div class="w-10 h-10 rounded-full bg-slate-200 overflow-hidden ring-2 ring-[#963131]/10">
          <img
            v-if="adminAvatar"
            :src="adminAvatar"
            alt="Admin avatar"
            class="w-full h-full object-cover"
          />
          <div v-else class="w-full h-full flex items-center justify-center bg-[#963131]/10 text-[#963131] font-bold text-sm">
            {{ initials }}
          </div>
        </div>
      </div>

    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useAuthStore } from '@/stores/auth.store'

const authStore = useAuthStore()

const searchQuery = ref('')

// Lấy thông tin từ store thực tế
const adminName = computed(() => {
  if (!authStore.user) return 'Admin'
  // Fallback về email nếu user không có trường name
  return (authStore.user as any).name || authStore.user.email || 'Admin'
})

const adminRole = computed(() => {
  if (!authStore.userRole) return 'Admin'
  return authStore.userRole.replace('_', ' ').toUpperCase()
})

const adminAvatar = computed(() => {
  return (authStore.user as any)?.avatarUrl || '' // Nếu backend có trả về avatarUrl
})

const initials = computed(() => {
   const name = adminName.value
   if (name.includes('@')) {
     return name.substring(0, 2).toUpperCase()
   }
   const parts = name.split(' ')
   if (parts.length >= 2) {
     return (parts[parts.length - 2][0] + parts[parts.length - 1][0]).toUpperCase()
   }
   return name.substring(0, 2).toUpperCase()
})
</script>