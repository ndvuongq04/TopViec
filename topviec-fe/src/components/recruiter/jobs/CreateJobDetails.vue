<template>
  <section class="bg-white dark:bg-slate-800 rounded-3xl p-8 shadow-sm border border-slate-100 dark:border-slate-700">
    <div class="flex items-center gap-3 mb-6">
      <div class="w-10 h-10 rounded-xl bg-amber-50 dark:bg-amber-900/20 flex items-center justify-center text-amber-500">
        <span class="material-symbols-outlined">description</span>
      </div>
      <h3 class="text-xl font-bold">Nội dung chi tiết</h3>
    </div>

    <div class="space-y-6">
      <!-- Mô tả công việc -->
      <div>
        <label class="block text-sm font-semibold text-slate-900 dark:text-slate-100 mb-2">Mô tả công việc</label>
        <div class="border border-slate-300 dark:border-slate-600 rounded-xl overflow-hidden">
          <!-- Toolbar -->
          <div class="bg-slate-50 dark:bg-slate-700 px-4 py-2 border-b border-slate-300 dark:border-slate-600 flex items-center gap-4">
            <button type="button" class="text-slate-500 hover:text-primary transition-colors cursor-pointer">
              <span class="material-symbols-outlined text-lg">format_bold</span>
            </button>
            <button type="button" class="text-slate-500 hover:text-primary transition-colors cursor-pointer">
              <span class="material-symbols-outlined text-lg">format_italic</span>
            </button>
            <button type="button" class="text-slate-500 hover:text-primary transition-colors cursor-pointer">
              <span class="material-symbols-outlined text-lg">format_list_bulleted</span>
            </button>
            <button type="button" class="text-slate-500 hover:text-primary transition-colors cursor-pointer">
              <span class="material-symbols-outlined text-lg">link</span>
            </button>
          </div>
          <textarea
            :value="modelValue.description"
            class="w-full p-4 border-none dark:bg-slate-800 focus:ring-0 outline-none resize-none"
            placeholder="Nhập chi tiết các đầu việc..."
            rows="4"
            @input="emit('update:modelValue', { ...modelValue, description: ($event.target as HTMLTextAreaElement).value })"
          ></textarea>
        </div>
      </div>

      <!-- Yêu cầu & Quyền lợi -->
      <div class="space-y-6">
        <div>
          <label class="block text-sm font-semibold text-slate-900 dark:text-slate-100 mb-2">Yêu cầu công việc</label>
          <textarea
            :value="modelValue.requirements"
            class="w-full p-4 rounded-xl border border-slate-300 dark:border-slate-600 dark:bg-slate-700 focus:border-primary focus:ring-4 focus:ring-primary/10 transition-all outline-none resize-none"
            placeholder="- Có ít nhất 2 năm kinh nghiệm..."
            rows="6"
            @input="emit('update:modelValue', { ...modelValue, requirements: ($event.target as HTMLTextAreaElement).value })"
          ></textarea>
        </div>
        <div>
          <label class="block text-sm font-semibold text-slate-900 dark:text-slate-100 mb-2">Quyền lợi ứng viên</label>
          <textarea
            :value="modelValue.benefits"
            class="w-full p-4 rounded-xl border border-slate-300 dark:border-slate-600 dark:bg-slate-700 focus:border-primary focus:ring-4 focus:ring-primary/10 transition-all outline-none resize-none"
            placeholder="- Mức lương cạnh tranh..."
            rows="6"
            @input="emit('update:modelValue', { ...modelValue, benefits: ($event.target as HTMLTextAreaElement).value })"
          ></textarea>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
export interface DetailsData {
  description: string
  requirements: string
  benefits: string
}

defineProps<{
  modelValue: DetailsData
}>()

const emit = defineEmits<{
  'update:modelValue': [value: DetailsData]
}>()
</script>
