<template>
  <CvTemplateFormSection
    title="Thông tin cơ bản"
    description="Đặt tên, mã định danh và mô tả ngắn để đội vận hành dễ quản lý mẫu CV."
    icon="badge"
  >
    <div class="grid gap-5 md:grid-cols-2">
      <label class="space-y-2 md:col-span-2">
        <span class="text-sm font-semibold text-slate-700 dark:text-slate-200">Tên mẫu CV</span>
        <input
          :value="form.name"
          type="text"
          placeholder="Ví dụ: Modern Product Manager"
          class="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-[1rem] text-slate-900 outline-none transition focus:border-[#963131] focus:ring-4 focus:ring-[#963131]/10 dark:border-slate-700 dark:bg-slate-950 dark:text-slate-100"
          @input="update('name', ($event.target as HTMLInputElement).value)"
        >
      </label>

      <label class="space-y-2">
        <span class="text-sm font-semibold text-slate-700 dark:text-slate-200">Mã template</span>
        <input
          :value="form.code"
          type="text"
          placeholder="CV-PM-01"
          class="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-[1rem] uppercase text-slate-900 outline-none transition focus:border-[#963131] focus:ring-4 focus:ring-[#963131]/10 dark:border-slate-700 dark:bg-slate-950 dark:text-slate-100"
          @input="update('code', ($event.target as HTMLInputElement).value.toUpperCase())"
        >
      </label>

      <label class="space-y-2">
        <span class="text-sm font-semibold text-slate-700 dark:text-slate-200">Slug</span>
        <input
          :value="form.slug"
          type="text"
          placeholder="modern-product-manager"
          class="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-[1rem] text-slate-900 outline-none transition focus:border-[#963131] focus:ring-4 focus:ring-[#963131]/10 dark:border-slate-700 dark:bg-slate-950 dark:text-slate-100"
          @input="update('slug', ($event.target as HTMLInputElement).value)"
        >
      </label>

      <label class="space-y-2 md:col-span-2">
        <span class="text-sm font-semibold text-slate-700 dark:text-slate-200">Mô tả ngắn</span>
        <textarea
          :value="form.description"
          rows="4"
          placeholder="Mô tả ngắn điểm nổi bật, đối tượng phù hợp và mục tiêu sử dụng của template."
          class="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-[1rem] text-slate-900 outline-none transition focus:border-[#963131] focus:ring-4 focus:ring-[#963131]/10 dark:border-slate-700 dark:bg-slate-950 dark:text-slate-100"
          @input="update('description', ($event.target as HTMLTextAreaElement).value)"
        />
      </label>
    </div>
  </CvTemplateFormSection>
</template>

<script setup lang="ts">
import CvTemplateFormSection from './CvTemplateFormSection.vue'

export type CvTemplateFormBasicInfo = {
  name: string
  code: string
  slug: string
  description: string
}

defineProps<{
  form: CvTemplateFormBasicInfo
}>()

const emit = defineEmits<{
  update: [field: keyof CvTemplateFormBasicInfo, value: string]
}>()

function update(field: keyof CvTemplateFormBasicInfo, value: string) {
  emit('update', field, value)
}
</script>
