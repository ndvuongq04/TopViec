<template>
  <CvTemplateFormSection
    title="Phân loại và tính năng"
    description="Xác định đối tượng phù hợp để bên ứng viên tìm kiếm template nhanh và chính xác hơn."
    icon="tune"
  >
    <div class="space-y-6">
      <div class="grid gap-5 md:grid-cols-2">
        <label class="space-y-2">
          <span class="text-sm font-semibold text-slate-700 dark:text-slate-200">Category</span>
          <select
            :value="form.category"
            class="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-[1rem] text-slate-900 outline-none transition focus:border-[#963131] focus:ring-4 focus:ring-[#963131]/10 dark:border-slate-700 dark:bg-slate-950 dark:text-slate-100"
            @change="updateCategory(($event.target as HTMLSelectElement).value)"
          >
            <option v-for="option in categories" :key="option" :value="option">
              {{ option }}
            </option>
          </select>
        </label>

        <label class="space-y-2">
          <span class="text-sm font-semibold text-slate-700 dark:text-slate-200">Cấp độ phù hợp</span>
          <div class="rounded-xl border border-slate-200 bg-white p-4 dark:border-slate-700 dark:bg-slate-950">
            <div class="flex flex-wrap gap-2">
              <button
                v-for="level in levels"
                :key="level"
                type="button"
                class="cursor-pointer rounded-full border px-3 py-2 text-[0.875rem] font-medium transition"
                :class="form.levels.includes(level)
                  ? 'border-[#963131] bg-[#963131] text-white'
                  : 'border-slate-200 text-slate-600 hover:border-slate-300 hover:bg-slate-50 dark:border-slate-700 dark:text-slate-300 dark:hover:bg-slate-900'"
                @click="toggleLevel(level)"
              >
                {{ level }}
              </button>
            </div>
          </div>
        </label>
      </div>

      <div class="grid gap-5 xl:grid-cols-2">
        <div class="space-y-2">
          <span class="text-sm font-semibold text-slate-700 dark:text-slate-200">Ngành nghề phù hợp</span>
          <div class="grid gap-3 rounded-xl border border-slate-200 bg-white p-4 sm:grid-cols-2 dark:border-slate-700 dark:bg-slate-950">
            <label
              v-for="industry in industries"
              :key="industry"
              class="cursor-pointer flex items-center gap-3 rounded-lg px-2 py-2 text-[0.875rem] text-slate-700 transition hover:bg-slate-50 dark:text-slate-200 dark:hover:bg-slate-900"
            >
              <input
                :checked="form.industries.includes(industry)"
                type="checkbox"
                class="cursor-pointer h-4 w-4 rounded border-slate-300 text-[#963131] focus:ring-[#963131]"
                @change="toggleIndustry(industry)"
              >
              <span>{{ industry }}</span>
            </label>
          </div>
        </div>

        <div class="space-y-2">
          <span class="text-sm font-semibold text-slate-700 dark:text-slate-200">Features</span>
          <div class="grid gap-3 rounded-xl border border-slate-200 bg-white p-4 sm:grid-cols-2 dark:border-slate-700 dark:bg-slate-950">
            <label
              v-for="feature in features"
              :key="feature"
              class="cursor-pointer flex items-center gap-3 rounded-lg px-2 py-2 text-[0.875rem] text-slate-700 transition hover:bg-slate-50 dark:text-slate-200 dark:hover:bg-slate-900"
            >
              <input
                :checked="form.features.includes(feature)"
                type="checkbox"
                class="cursor-pointer h-4 w-4 rounded border-slate-300 text-[#963131] focus:ring-[#963131]"
                @change="toggleFeature(feature)"
              >
              <span>{{ feature }}</span>
            </label>
          </div>
        </div>
      </div>
    </div>
  </CvTemplateFormSection>
</template>

<script setup lang="ts">
import CvTemplateFormSection from './CvTemplateFormSection.vue'

export type CvTemplateFormClassification = {
  category: string
  levels: string[]
  industries: string[]
  features: string[]
}

const props = defineProps<{
  form: CvTemplateFormClassification
  categories: string[]
  levels: string[]
  industries: string[]
  features: string[]
}>()

const emit = defineEmits<{
  update: [value: CvTemplateFormClassification]
}>()

function updateCategory(value: string) {
  emit('update', { ...props.form, category: value })
}

function toggleLevel(value: string) {
  emit('update', {
    ...props.form,
    levels: toggleArray(props.form.levels, value),
  })
}

function toggleIndustry(value: string) {
  emit('update', {
    ...props.form,
    industries: toggleArray(props.form.industries, value),
  })
}

function toggleFeature(value: string) {
  emit('update', {
    ...props.form,
    features: toggleArray(props.form.features, value),
  })
}

function toggleArray(source: string[], value: string) {
  return source.includes(value)
    ? source.filter(item => item !== value)
    : [...source, value]
}
</script>
