<script setup lang="ts">
import JobSearchBar from "./JobSearchBar.vue";
</script>

<template>
  <div
    class="relative bg-gradient-to-r from-blue-400 via-blue-500 to-blue-600 p-8 md:p-14 text-white shadow-xl shadow-blue-500/20"
  >
    <!-- Decorative shapes: clipped separately so they don't affect overflow -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none">
      <!-- Dot pattern -->
      <div
        class="absolute inset-0 opacity-10"
        style="background-image: radial-gradient(#ffffff 1px, transparent 1px); background-size: 24px 24px;"
      />
      <!-- Skew shape -->
      <div class="absolute right-0 top-0 h-full w-1/3 opacity-20 bg-gradient-to-l from-white to-transparent transform skew-x-12 translate-x-12" />
    </div>

    <!-- Content -->
    <div class="relative z-10 w-full max-w-[1440px] mx-auto px-4 md:px-10">
      <div class="flex flex-col gap-8">
        <!-- Title -->
        <div class="space-y-3">
          <h1 class="text-4xl md:text-5xl font-extrabold leading-tight tracking-tight drop-shadow-sm text-white">
            Việc ngon không tự tới – lên <span class="text-yellow-300 drop-shadow-md">TopViec</span> là thấy ngay!
          </h1>
          <p class="text-blue-50 text-base md:text-lg max-w-2xl opacity-90 font-medium">
            Apply nhẹ một cái, HR gọi liền tay.
          </p>
        </div>

        <!-- Search Bar -->
        <JobSearchBar />
      </div>
    </div>
  </div>
</template>
