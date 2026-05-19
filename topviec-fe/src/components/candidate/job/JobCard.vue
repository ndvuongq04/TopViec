<script setup lang="ts">
// JobCard: Card hiển thị 1 job
// Tái sử dụng ở nhiều trang: home, search, saved, applied
// Props: job object chứa đầy đủ thông tin
import { RouterLink } from "vue-router";

interface JobCardProps {
  id: number;
  title: string;
  company: string;
  logoUrl: string;
  logoBg: string; // Màu nền logo: 'bg-orange-100', 'bg-blue-100'...
  logoBorder: string; // Màu border logo: 'border-orange-200'...
  tags: string[]; // ['Full-time', 'Senior Level']
  salaryMin: string;
  salaryMax: string;
  location: string;
  postedAt: string; // '2 days ago', 'Just now'...
  isHot?: boolean; // Badge "Hot"
  isUrgent?: boolean; // Badge "TUYỂN GẤP"
  isSaved?: boolean; // Trạng thái đã lưu
  isBrandVerified?: boolean | null; // Tích xanh xác minh công ty
  selectable?: boolean; // Hiển thị checkbox để chọn
  selected?: boolean; // Trạng thái checkbox
  isActive?: boolean; // Đang được xem nhanh
  showQuickView?: boolean; // Cho phép hiển thị nút gọi quick view
}

const props = defineProps<JobCardProps>();

// Emit khi bookmark được click
const emit = defineEmits<{
  bookmark: [id: number];
  select: [id: number, active: boolean];
  quickView: [id: number];
}>();
</script>

<template>
  <RouterLink
    :to="`/jobs/${props.id}`"
    class="group bg-white dark:bg-surface-dark p-5 rounded-3xl border transition-all duration-300 relative block"
    :class="props.isActive
      ? 'border-primary/50 shadow-lg shadow-blue-500/5'
      : 'border-slate-200 dark:border-slate-800 hover:border-primary/50 hover:shadow-lg hover:shadow-blue-500/5'"
  >
    <!-- Actions (Top Right) -->
    <div class="absolute top-4 right-4 z-10 flex items-center gap-2">
      <!-- Bookmark button -->
      <button
        class="transition-colors cursor-pointer bg-white/80 dark:bg-slate-800/80 p-1.5 rounded-full border border-slate-100 dark:border-slate-800 shadow-sm"
        :class="props.isSaved ? 'text-primary' : 'text-slate-300 hover:text-primary'"
        @click.prevent.stop="emit('bookmark', props.id)"
      >
        <span
          class="material-symbols-outlined text-[20px]"
          :style="props.isSaved ? 'font-variation-settings: \'FILL\' 1' : ''"
        >
          bookmark
        </span>
      </button>
    </div>

    <!-- Logo + title + company -->
    <div class="flex gap-3 items-center mb-4">
      <!-- Checkbox (inline, trong flow) -->
      <div
        v-if="props.selectable"
        @click.prevent.stop="emit('select', props.id, !props.selected)"
        class="shrink-0 w-5 h-5 rounded-md border-2 flex items-center justify-center transition-all duration-200 cursor-pointer"
        :class="props.selected
          ? 'bg-primary border-primary text-white shadow-md shadow-primary/30 scale-105'
          : 'bg-white dark:bg-slate-800 border-slate-300 dark:border-slate-600 hover:border-primary hover:shadow-sm'"
      >
        <span v-if="props.selected" class="material-symbols-outlined text-[13px] font-black leading-none">check</span>
      </div>

      <div
        :class="`h-12 w-12 rounded-full ${props.logoBg} flex items-center justify-center shrink-0 border ${props.logoBorder}`"
      >
        <img
          :alt="props.company"
          :src="props.logoUrl"
          class="h-8 w-8 object-contain opacity-90"
        />
      </div>
      <div class="min-w-0 flex-1">
        <h3
          class="text-lg font-bold text-text-main dark:text-white group-hover:text-primary transition-colors pr-6 truncate"
        >
          {{ props.title }}
        </h3>
        <div class="flex items-center gap-1">
          <p class="text-base text-text-muted">{{ props.company }}</p>
          <div v-if="props.isBrandVerified" class="relative flex items-center group/verified">
            <span
              class="material-symbols-outlined text-blue-600 cursor-default"
              style="font-size: 15px; font-variation-settings: 'FILL' 1"
            >verified</span>
            <span class="pointer-events-none absolute bottom-[calc(100%+6px)] left-1/2 -translate-x-1/2 bg-blue-800 text-white text-[11px] font-medium whitespace-nowrap px-2.5 py-1 rounded-md opacity-0 group-hover/verified:opacity-100 transition-opacity z-10">
              Công ty đã xác minh
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- Tags + salary + location + badge — tất cả cùng hàng, wrap -->
    <div class="flex flex-wrap items-center gap-2 mb-2">
      <span
        v-for="tag in props.tags"
        :key="tag"
        class="px-2.5 py-1 rounded-md bg-slate-100 dark:bg-slate-800 text-sm font-medium text-text-muted dark:text-gray-400"
      >
        {{ tag }}
      </span>

      <!-- Salary -->
      <span class="flex items-center gap-0.5 px-2.5 py-1 rounded-md bg-slate-100 dark:bg-slate-800 text-sm font-semibold text-text-main dark:text-gray-300">
        <span class="material-symbols-outlined text-[15px] text-slate-400">attach_money</span>
        {{ props.salaryMax ? `${props.salaryMin} - ${props.salaryMax}` : props.salaryMin }}
      </span>

      <!-- Location -->
      <span class="flex items-center gap-0.5 px-2.5 py-1 rounded-md bg-slate-100 dark:bg-slate-800 text-sm text-slate-500 dark:text-gray-400">
        <span class="material-symbols-outlined text-[15px]">location_on</span>
        {{ props.location }}
      </span>

      <!-- Nút xem nhanh -->
      <button
        v-if="props.showQuickView"
        class="transition-all duration-300 cursor-pointer pl-3.5 pr-2.5 py-1 rounded-full border border-slate-200 dark:border-slate-700 text-slate-500 hover:text-primary hover:border-primary hover:shadow-sm text-xs font-bold flex items-center gap-1 opacity-0 group-hover:opacity-100 bg-white dark:bg-surface-dark"
        @click.prevent.stop="emit('quickView', props.id)"
      >
        Xem nhanh
        <span class="material-symbols-outlined text-[16px] -ml-0.5">keyboard_double_arrow_right</span>
      </button>

      <!-- Badge Hot / Gấp — đẩy ra góc phải -->
      <span class="ml-auto">
        <span
          v-if="props.isUrgent"
          class="text-xs font-extrabold uppercase tracking-wide text-amber-700 bg-amber-50 border border-amber-300 px-2.5 py-1 rounded-md"
        >GẤP</span>
        <span
          v-else-if="props.isHot"
          class="text-xs font-extrabold uppercase tracking-wide text-red-700 bg-red-50 border border-red-300 px-2.5 py-1 rounded-md"
        >HOT</span>
      </span>
    </div>

    <!-- Footer: posted time -->
    <div class="flex items-center justify-between pt-3 border-t border-slate-100 dark:border-slate-800 mt-3">
      <span class="text-sm text-slate-400">{{ props.postedAt }}</span>
    </div>

  </RouterLink>
</template>
