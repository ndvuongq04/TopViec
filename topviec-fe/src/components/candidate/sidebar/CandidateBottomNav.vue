<script setup lang="ts">
// AppBottomNav: Bottom navigation bar, chỉ hiển thị trên mobile (lg:hidden)
// 4 tab: Home, Jobs, Saved, Profile
</script>

<template>
  <nav
    class="lg:hidden fixed bottom-0 w-full bg-white dark:bg-surface-dark border-t border-slate-200 dark:border-slate-800 p-2 z-50 flex justify-around items-center pb-safe"
  >
    <a class="flex flex-col items-center gap-1 p-2 text-primary" href="#">
      <span class="material-symbols-outlined">home</span>
      <span class="text-[10px] font-medium">Trang chủ</span>
    </a>

    <a
      class="flex flex-col items-center gap-1 p-2 text-text-muted dark:text-gray-400"
      href="#"
    >
      <span class="material-symbols-outlined">search</span>
      <span class="text-[10px] font-medium">Tìm việc</span>
    </a>

    <a
      class="flex flex-col items-center gap-1 p-2 text-text-muted dark:text-gray-400"
      href="#"
    >
      <span class="material-symbols-outlined">bookmark</span>
      <span class="text-[10px] font-medium">Đã lưu</span>
    </a>

    <a
      class="flex flex-col items-center gap-1 p-2 text-text-muted dark:text-gray-400"
      href="#"
    >
      <span class="material-symbols-outlined">person</span>
      <span class="text-[10px] font-medium">Hồ sơ</span>
    </a>
  </nav>
</template>
