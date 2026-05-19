<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useLevelStore } from "@/stores/level.store";

const levelStore = useLevelStore();
const jobTypes = ref({ fullTime: false, freelance: false, remote: false });
const experience = ref<Record<number, boolean>>({});
const salaryRange = ref(50);

function clearAll() {
  jobTypes.value = { fullTime: false, freelance: false, remote: false };
  // Reset experience selection
  Object.keys(experience.value).forEach((key) => {
    experience.value[Number(key)] = false;
  });
  salaryRange.value = 50;
}

onMounted(async () => {
  if (levelStore.levels.length === 0) {
    await levelStore.fetchLevels();
  }
  // Khởi tạo state checkbox cho từng level
  levelStore.levels.forEach((level) => {
    if (experience.value[level.id] === undefined) {
      experience.value[level.id] = false;
    }
  });
});
</script>

<template>
  <div
    class="flex flex-col gap-4 bg-white dark:bg-surface-dark p-5 rounded-3xl border border-slate-200 dark:border-slate-800 shadow-sm"
  >
    <!-- Header -->
    <div class="flex items-center justify-between">
      <h3 class="font-bold text-sm text-text-main dark:text-white">Bộ lọc</h3>
      <button
        class="text-xs text-primary font-medium hover:underline"
        @click="clearAll"
      >
        Xóa tất cả
      </button>
    </div>

    <!-- Job Type -->
    <div class="flex flex-col gap-2">
      <h4
        class="text-xs font-semibold text-text-muted dark:text-gray-400 uppercase tracking-wider"
      >
        Loại hình công việc
      </h4>
      <label class="flex items-center gap-2 cursor-pointer group">
        <!-- ✅ Dùng accent-primary thay text-primary -->
        <input
          v-model="jobTypes.fullTime"
          type="checkbox"
          class="rounded border-slate-300 accent-primary h-4 w-4 cursor-pointer"
        />
        <span
          class="text-sm text-text-main dark:text-gray-300 group-hover:text-primary transition-colors"
          >Full-time</span
        >
        <span class="text-xs text-text-muted ml-auto">120</span>
      </label>
      <label class="flex items-center gap-2 cursor-pointer group">
        <input
          v-model="jobTypes.freelance"
          type="checkbox"
          class="rounded border-slate-300 accent-primary h-4 w-4 cursor-pointer"
        />
        <span
          class="text-sm text-text-main dark:text-gray-300 group-hover:text-primary transition-colors"
          >Freelance</span
        >
        <span class="text-xs text-text-muted ml-auto">45</span>
      </label>
      <label class="flex items-center gap-2 cursor-pointer group">
        <input
          v-model="jobTypes.remote"
          type="checkbox"
          class="rounded border-slate-300 accent-primary h-4 w-4 cursor-pointer"
        />
        <span
          class="text-sm text-text-main dark:text-gray-300 group-hover:text-primary transition-colors"
          >Remote</span
        >
        <span class="text-xs text-text-muted ml-auto">82</span>
      </label>
    </div>

    <div class="h-px bg-slate-100 dark:bg-slate-800" />

    <!-- Salary Range -->
    <div class="flex flex-col gap-3">
      <h4
        class="text-xs font-semibold text-text-muted dark:text-gray-400 uppercase tracking-wider"
      >
        Mức lương
      </h4>
      <input
        v-model="salaryRange"
        type="range"
        min="0"
        max="100"
        class="w-full h-2 bg-slate-200 rounded appearance-none cursor-pointer accent-primary"
      />
      <div class="flex justify-between text-xs text-text-muted">
        <span>$500</span>
        <span>$5,000+</span>
      </div>
    </div>

    <div class="h-px bg-slate-100 dark:bg-slate-800" />

    <!-- Experience -->
    <div class="flex flex-col gap-2">
      <h4
        class="text-xs font-semibold text-text-muted dark:text-gray-400 uppercase tracking-wider"
      >
        Kinh nghiệm
      </h4>

      <div v-if="levelStore.loading" class="flex flex-col gap-2">
        <div
          v-for="i in 3"
          :key="i"
          class="h-4 w-24 bg-slate-100 dark:bg-slate-800 animate-pulse rounded"
        ></div>
      </div>

      <template v-else>
        <label
          v-for="level in levelStore.levels"
          :key="level.id"
          class="flex items-center gap-2 cursor-pointer group"
        >
          <input
            v-model="experience[level.id]"
            type="checkbox"
            class="rounded border-slate-300 accent-primary h-4 w-4 cursor-pointer"
          />
          <span
            class="text-sm text-text-main dark:text-gray-300 group-hover:text-primary transition-colors"
          >
            {{ level.name }}
          </span>
        </label>
      </template>
    </div>
  </div>
</template>
