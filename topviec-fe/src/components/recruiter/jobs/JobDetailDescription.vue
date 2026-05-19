<template>
  <div class="lg:col-span-2 space-y-8">
    <!-- Kỹ năng yêu cầu -->
    <section v-if="skills && skills.length" class="bg-white dark:bg-slate-800 p-8 rounded-2xl border border-slate-200 dark:border-slate-700 shadow-sm">
      <h2 class="text-lg font-bold text-slate-900 dark:text-white mb-6 flex items-center gap-2">
        <span class="w-1.5 h-6 bg-primary rounded-full"></span>
        Kỹ năng chuyên môn
      </h2>
      <div class="flex flex-wrap gap-2">
        <span
          v-for="(skill, i) in skills"
          :key="i"
          class="px-4 py-2 bg-sky-50 dark:bg-sky-900/30 text-sky-700 dark:text-sky-300 rounded-xl text-sm font-semibold border border-sky-100 dark:border-sky-800/50"
        >
          {{ skill }}
        </span>
      </div>
    </section>

    <!-- Mô tả công việc -->
    <section class="bg-white dark:bg-slate-800 p-8 rounded-2xl border border-slate-200 dark:border-slate-700 shadow-sm">
      <h2 class="text-lg font-bold text-slate-900 dark:text-white mb-6 flex items-center gap-2">
        <span class="w-1.5 h-6 bg-primary rounded-full"></span>
        Mô tả công việc
      </h2>
      <div class="text-slate-700 dark:text-slate-300 leading-relaxed space-y-4">
        <p>{{ description }}</p>
        <ul v-if="descriptionList.length" class="list-disc list-inside space-y-2 ml-2">
          <li v-for="(item, i) in descriptionList" :key="i">{{ item }}</li>
        </ul>
      </div>
    </section>

    <!-- Yêu cầu ứng viên -->
    <section class="bg-white dark:bg-slate-800 p-8 rounded-2xl border border-slate-200 dark:border-slate-700 shadow-sm">
      <h2 class="text-lg font-bold text-slate-900 dark:text-white mb-6 flex items-center gap-2">
        <span class="w-1.5 h-6 bg-primary rounded-full"></span>
        Yêu cầu ứng viên
      </h2>
      <div class="text-slate-700 dark:text-slate-300 leading-relaxed">
        <ul class="list-disc list-inside space-y-2 ml-2">
          <li v-for="(item, i) in requirements" :key="i">{{ item }}</li>
        </ul>
      </div>
    </section>

    <!-- Quyền lợi được hưởng -->
    <section class="bg-white dark:bg-slate-800 p-8 rounded-2xl border border-slate-200 dark:border-slate-700 shadow-sm">
      <h2 class="text-lg font-bold text-slate-900 dark:text-white mb-6 flex items-center gap-2">
        <span class="w-1.5 h-6 bg-primary rounded-full"></span>
        Quyền lợi được hưởng
      </h2>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div
          v-for="(benefit, i) in benefits"
          :key="i"
          class="flex items-start gap-3 p-3 bg-slate-50 dark:bg-slate-700/50 rounded-lg"
        >
          <span class="material-symbols-outlined text-primary shrink-0">{{ benefit.icon }}</span>
          <span class="text-slate-700 dark:text-slate-300">{{ benefit.text }}</span>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
export interface BenefitItem {
  icon: string
  text: string
}

defineProps<{
  description: string
  descriptionList: string[]
  requirements: string[]
  benefits: BenefitItem[]
  skills?: string[]
}>()
</script>
