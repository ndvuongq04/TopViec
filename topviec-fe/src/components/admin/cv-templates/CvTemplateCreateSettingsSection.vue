<template>
  <CvTemplateFormSection
    title="Thiết lập hiển thị và kinh doanh"
    description="Quy định layout mặc định, mức giá và các quyền tùy chỉnh cho ứng viên sau khi chọn mẫu."
    icon="dashboard_customize"
  >
    <div class="grid gap-6 xl:grid-cols-2">
      <div class="space-y-5">
        <label class="space-y-2">
          <span class="text-sm font-semibold text-slate-700 dark:text-slate-200">Layout</span>
          <select
            :value="form.layout"
            class="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-[1rem] text-slate-900 outline-none transition focus:border-[#963131] focus:ring-4 focus:ring-[#963131]/10 dark:border-slate-700 dark:bg-slate-950 dark:text-slate-100"
            @change="update('layout', ($event.target as HTMLSelectElement).value)"
          >
            <option value="single-column">Single column</option>
            <option value="two-column">Two column</option>
            <option value="split-header">Split header</option>
          </select>
        </label>

        <label class="space-y-2">
          <span class="text-sm font-semibold text-slate-700 dark:text-slate-200">Gói hiển thị</span>
          <div class="grid gap-3 sm:grid-cols-2">
            <button
              v-for="option in tiers"
              :key="option.value"
              type="button"
              class="cursor-pointer rounded-2xl border p-4 text-left transition"
              :class="form.tier === option.value
                ? 'border-[#963131] bg-[#963131]/5 ring-4 ring-[#963131]/10'
                : 'border-slate-200 hover:border-slate-300 dark:border-slate-700 dark:hover:border-slate-600'"
              @click="update('tier', option.value)"
            >
              <p class="text-[0.875rem] font-bold text-slate-900 dark:text-slate-100">{{ option.label }}</p>
              <p class="mt-1 text-[0.875rem] leading-6 text-slate-500 dark:text-slate-400">{{ option.description }}</p>
            </button>
          </div>
        </label>
      </div>

      <div class="space-y-5 rounded-2xl border border-slate-200 bg-slate-50 p-5 dark:border-slate-700 dark:bg-slate-950">
        <div>
          <p class="text-[0.875rem] font-semibold text-slate-700 dark:text-slate-200">Quyền tùy chỉnh cho ứng viên</p>
          <p class="mt-1 text-[0.875rem] leading-6 text-slate-500 dark:text-slate-400">
            Bật những quyền mà candidate được phép chỉnh sau khi chọn mẫu này.
          </p>
        </div>

        <div class="grid gap-3">
          <label
            v-for="option in customizationOptions"
            :key="option.key"
            class="cursor-pointer flex items-start gap-3 rounded-xl border border-slate-200 bg-white px-4 py-3 text-[0.875rem] dark:border-slate-700 dark:bg-slate-900"
          >
            <input
              :checked="form.customization[option.key]"
              type="checkbox"
              class="cursor-pointer mt-0.5 h-4 w-4 rounded border-slate-300 text-[#963131] focus:ring-[#963131]"
              @change="toggleCustomization(option.key)"
            >
            <div>
              <p class="font-bold text-slate-800 dark:text-slate-100">{{ option.label }}</p>
              <p class="mt-1 leading-6 text-slate-500 dark:text-slate-400">{{ option.description }}</p>
            </div>
          </label>
        </div>
      </div>
    </div>
  </CvTemplateFormSection>
</template>

<script setup lang="ts">
import CvTemplateFormSection from './CvTemplateFormSection.vue'

export type CvTemplateCustomizationSettings = {
  allowColorChange: boolean
  allowFontChange: boolean
  allowSectionReorder: boolean
  allowAvatarToggle: boolean
}

export type CvTemplateFormSettings = {
  layout: string
  tier: string
  customization: CvTemplateCustomizationSettings
}

const props = defineProps<{
  form: CvTemplateFormSettings
}>()

const emit = defineEmits<{
  update: [value: CvTemplateFormSettings]
}>()

const tiers = [
  {
    value: 'free',
    label: 'Free',
    description: 'Hiển thị cho mọi ứng viên, phù hợp kéo traffic và tăng usage.',
  },
  {
    value: 'premium',
    label: 'Premium',
    description: 'Dùng cho gói trả phí hoặc mở khóa theo chiến lược upsell.',
  },
]

const customizationOptions: Array<{
  key: keyof CvTemplateCustomizationSettings
  label: string
  description: string
}> = [
  {
    key: 'allowColorChange',
    label: 'Cho đổi màu chủ đạo',
    description: 'Ứng viên có thể thay đổi color theme của template.',
  },
  {
    key: 'allowFontChange',
    label: 'Cho đổi font chữ',
    description: 'Ứng viên có thể chuyển qua các font đã whitelist.',
  },
  {
    key: 'allowSectionReorder',
    label: 'Cho kéo thả thứ tự section',
    description: 'Cho phép thay đổi thứ tự Experience, Education, Skills...',
  },
  {
    key: 'allowAvatarToggle',
    label: 'Cho bật hoặc tắt ảnh đại diện',
    description: 'Hữu ích với template ATS-friendly hoặc vị trí cần ảnh cá nhân.',
  },
]

function update(field: keyof Omit<CvTemplateFormSettings, 'customization'>, value: string) {
  emit('update', { ...props.form, [field]: value })
}

function toggleCustomization(key: keyof CvTemplateCustomizationSettings) {
  emit('update', {
    ...props.form,
    customization: {
      ...props.form.customization,
      [key]: !props.form.customization[key],
    },
  })
}
</script>
