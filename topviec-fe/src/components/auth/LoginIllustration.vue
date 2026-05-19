<template>
  <div
    class="relative hidden w-1/2 flex-col items-center justify-center bg-primary/10 p-12 lg:flex overflow-hidden"
  >
    <!-- Decorative blobs -->
    <div
      class="absolute -bottom-16 -left-16 h-64 w-64 rounded-full bg-primary/20 blur-3xl pointer-events-none"
    ></div>
    <div
      class="absolute -right-16 -top-16 h-64 w-64 rounded-full bg-primary/10 blur-3xl pointer-events-none"
    ></div>

    <!-- Content -->
    <div class="z-10 text-center">
      <h1
        class="mb-4 text-3xl font-black text-slate-900 dark:text-white leading-tight"
      >
        Tìm kiếm sự nghiệp mơ ước tại TopViec
      </h1>
      <p class="text-slate-600 dark:text-slate-400 text-base">
        Kết nối hàng ngàn cơ hội việc làm hấp dẫn từ các công ty hàng đầu Việt
        Nam.
      </p>
    </div>

    <!-- Illustration image -->
    <div class="z-10 mt-12 w-full max-w-md">
      <div
        class="aspect-square w-full rounded-2xl bg-cover bg-center shadow-lg"
        style="
          background-image: url(&quot;https://lh3.googleusercontent.com/aida-public/AB6AXuBR6fRFsE6k7OQmJ_WNPfd-5KFSpQhRnf0thb63PLGmafoRdZ2cLo5LdenQ33X5GeKXmWBi_fl0Wr4A6hvcclJWVJ8lc11GRmA8v3Tfo4yLpBiAvkBixt4hAdEKKjEdwdp65jMbdcZ1_V7MB6k9TSjw1gdNBEgkXP68L8kBTIsgDq5l0Ni9_WNn9iFFrGNf34M8YsmZ-RPKEyaEbYfSzC07VZSpvfQNJyBEnOC6r1lfUbjFiEWYVfsGuRG8Zm3CxHZf88doo7YBBys&quot;);
        "
        role="img"
        aria-label="Professional young people working in a modern office environment"
      ></div>
    </div>
  </div>
</template>

<script setup lang="ts">
// No props needed — purely presentational
</script>
