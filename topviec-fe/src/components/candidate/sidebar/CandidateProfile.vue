<script setup lang="ts">
// SidebarProfile: Hiển thị avatar, tên, role và progress bar hoàn thiện profile
import { useAuthStore } from "@/stores/auth.store";

const authStore = useAuthStore();
</script>

<template>
  <div class="mb-8 p-5 bg-slate-100 dark:bg-slate-800/50 rounded-2xl border border-slate-200/50 dark:border-slate-800/50 shadow-sm">
    <div class="flex items-center gap-4 mb-4">
      <div 
        class="w-12 h-12 rounded-2xl bg-center bg-no-repeat bg-cover border border-white dark:border-slate-700 shadow-sm shrink-0"
        style="background-image: url('https://lh3.googleusercontent.com/aida-public/AB6AXuBauwug1k4YzsanpU4ayfdzX7pWz4X2mD94izZAzMDk9U46a09O4ttcwLGg4F6O5i8piR2LPJhqDT6QLkDmnOuO3znOcyh2l2yq8K3RyeBiFYb3x43fkQWuHs_thxSOMY97nRCJ23oXMk6zl3bYM8wlxQw4rBYOadovYby8hPiO0R0PqmOI-41J-6KPMXAwdvscPcXR02bxM4Sb35KTrCxqPShq3f0AxcJDmc8ccTcAUxVoWbCaQHyOp6oaF8MCPfXNNwtE2J3NDGjR');"
      ></div>
      <div class="flex flex-col min-w-0">
        <p class="font-bold text-slate-900 dark:text-white truncate">
          {{ authStore.user?.email?.split("@")[0] ?? "Nguyễn Văn Anh" }}
        </p>
        <span class="inline-flex items-center px-1.5 py-0.5 rounded-full bg-primary text-white text-[9px] font-black uppercase tracking-wider w-fit">
          {{ authStore.userRole ?? "CANDIDATE" }}
        </span>
      </div>
    </div>
    
    <div class="space-y-1.5">
      <div class="flex justify-between items-center text-[11px] font-bold">
        <span class="text-slate-500 dark:text-gray-400">Hồ sơ hoàn tất</span>
        <span class="text-primary">70%</span>
      </div>
      <div class="w-full bg-slate-200 dark:bg-slate-700 h-1.5 rounded-full overflow-hidden">
        <div class="bg-primary h-full w-[70%] transition-all duration-1000"></div>
      </div>
    </div>
  </div>
</template>
