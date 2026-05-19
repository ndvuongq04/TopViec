<script setup lang="ts">
// CompanyCard: Card hiển thị 1 công ty đang tuyển dụng
// Tái sử dụng ở trang Companies, trang chủ
interface CompanyCardProps {
  id: number;
  slug: string;
  name: string;
  logoUrl: string;
  openJobs: number;
  isFollowing?: boolean;
}

const props = defineProps<CompanyCardProps>();

const emit = defineEmits<{
  follow: [id: number];
}>();
</script>

<template>
  <router-link
    :to="{ name: 'CompanyDetail', params: { slug: props.slug } }"
    class="min-w-[200px] flex flex-col items-center gap-3 p-4 bg-white dark:bg-surface-dark border border-slate-200 dark:border-slate-800 rounded-3xl hover:border-primary/30 hover:shadow-md transition-all cursor-pointer"
  >
    <!-- Logo công ty -->
    <div
      class="h-14 w-14 rounded-full bg-slate-50 dark:bg-slate-800 flex items-center justify-center p-2"
    >
      <img
        v-if="props.logoUrl"
        :alt="props.name"
        :src="props.logoUrl"
        class="w-full h-full object-contain"
      />
      <div
        v-else
        class="w-full h-full flex items-center justify-center bg-primary/10 text-primary font-bold text-xl rounded-full"
      >
        {{ props.name.charAt(0) }}
      </div>
    </div>

    <!-- Tên + số job -->
    <div class="text-center">
      <h4 class="font-bold text-base text-text-main dark:text-white">
        {{ props.name }}
      </h4>
      <p class="text-sm text-primary mt-1">
        {{ props.openJobs }} việc làm đang tuyển
      </p>
    </div>

    <!-- Nút Follow -->
    <button
      :class="[
        'w-full py-1.5 text-sm font-semibold rounded-full transition-colors cursor-pointer',
        props.isFollowing
          ? 'bg-primary text-white hover:bg-blue-700'
          : 'text-primary bg-primary/10 hover:bg-primary hover:text-white',
      ]"
      @click="emit('follow', props.id)"
    >
      {{ props.isFollowing ? "Đang theo dõi" : "Theo dõi" }}
    </button>
  </router-link>
</template>
