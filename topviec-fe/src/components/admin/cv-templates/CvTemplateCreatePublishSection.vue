<template>
  <CvTemplateFormSection
    title="Trạng thái xuất bản"
    description="Thiết lập vòng đời template để đội vận hành kiểm soát việc hiển thị ngoài candidate side."
    icon="published_with_changes"
  >
    <div class="grid gap-5 md:grid-cols-2">
      <label class="space-y-2">
        <span class="text-sm font-semibold text-slate-700 dark:text-slate-200">Status</span>
        <select
          :value="form.status"
          class="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-[1rem] text-slate-900 outline-none transition focus:border-[#963131] focus:ring-4 focus:ring-[#963131]/10 dark:border-slate-700 dark:bg-slate-950 dark:text-slate-100"
          @change="update('status', ($event.target as HTMLSelectElement).value)"
        >
          <option value="draft">Draft</option>
          <option value="published">Published</option>
          <option value="archived">Archived</option>
        </select>
      </label>

      <label class="space-y-2">
        <span class="text-sm font-semibold text-slate-700 dark:text-slate-200">Visibility</span>
        <select
          :value="form.visibility"
          class="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-[1rem] text-slate-900 outline-none transition focus:border-[#963131] focus:ring-4 focus:ring-[#963131]/10 dark:border-slate-700 dark:bg-slate-950 dark:text-slate-100"
          @change="update('visibility', ($event.target as HTMLSelectElement).value)"
        >
          <option value="active">Active</option>
          <option value="inactive">Inactive</option>
        </select>
      </label>

      <label class="space-y-2">
        <span class="text-sm font-semibold text-slate-700 dark:text-slate-200">Thứ tự hiển thị</span>
        <input
          :value="form.sortOrder"
          type="number"
          min="0"
          class="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-[1rem] text-slate-900 outline-none transition focus:border-[#963131] focus:ring-4 focus:ring-[#963131]/10 dark:border-slate-700 dark:bg-slate-950 dark:text-slate-100"
          @input="update('sortOrder', Number(($event.target as HTMLInputElement).value || 0))"
        >
      </label>

      <div class="rounded-2xl border border-[#963131]/15 bg-[#963131]/5 p-4">
        <p class="text-sm font-semibold text-[#963131]">Khuyến nghị vận hành</p>
        <p class="mt-2 text-sm leading-6 text-slate-600 dark:text-slate-300">
          Tạo mới nên để <strong>Draft</strong>, kiểm tra thumbnail và metadata xong mới chuyển sang <strong>Published</strong>.
        </p>
      </div>
    </div>
  </CvTemplateFormSection>
</template>

<script setup lang="ts">
import CvTemplateFormSection from './CvTemplateFormSection.vue'

export type CvTemplateFormPublish = {
  status: string
  visibility: string
  sortOrder: number
}

defineProps<{
  form: CvTemplateFormPublish
}>()

const emit = defineEmits<{
  update: [field: keyof CvTemplateFormPublish, value: string | number]
}>()

function update(field: keyof CvTemplateFormPublish, value: string | number) {
  emit('update', field, value)
}
</script>
