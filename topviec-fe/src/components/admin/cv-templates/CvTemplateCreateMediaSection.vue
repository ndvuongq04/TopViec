<template>
  <CvTemplateFormSection
    title="Media và nhận diện"
    description="Thiết lập thumbnail và phối màu mặc định để template nổi bật ngay từ trang gallery."
    icon="imagesmode"
  >
    <div class="grid gap-5 lg:grid-cols-[minmax(0,1fr)_220px]">
      <div class="space-y-5">
        <label class="space-y-2">
          <span class="text-sm font-semibold text-slate-700 dark:text-slate-200">
            {{ allowUpload ? 'Thumbnail Image' : 'Thumbnail URL' }}
          </span>
          <input
            v-if="allowUpload"
            type="file"
            accept="image/*"
            class="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-[1rem] text-slate-900 outline-none transition file:mr-4 file:rounded-lg file:border-0 file:bg-[#963131]/10 file:px-3 file:py-2 file:font-semibold file:text-[#963131] focus:border-[#963131] focus:ring-4 focus:ring-[#963131]/10 dark:border-slate-700 dark:bg-slate-950 dark:text-slate-100"
            @change="handleFileChange"
          >
          <input
            v-else
            :value="form.thumbnail"
            type="url"
            placeholder="https://..."
            class="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-[1rem] text-slate-900 outline-none transition focus:border-[#963131] focus:ring-4 focus:ring-[#963131]/10 dark:border-slate-700 dark:bg-slate-950 dark:text-slate-100"
            @input="update('thumbnail', ($event.target as HTMLInputElement).value)"
          >
          <p class="text-xs text-slate-500">
            {{ allowUpload ? 'Chon anh tu may tinh de gui cung request tao template.' : 'Giữ URL thumbnail hiện tại hoặc thay bằng URL mới nếu backend hỗ trợ.' }}
          </p>
        </label>

        <div class="grid gap-4 md:grid-cols-2">
          <label class="space-y-2">
            <span class="text-sm font-semibold text-slate-700 dark:text-slate-200">Màu chủ đạo</span>
            <div class="flex items-center gap-3 rounded-xl border border-slate-200 bg-white px-3 py-2 dark:border-slate-700 dark:bg-slate-950">
              <input
                :value="form.primaryColor"
                type="color"
                class="h-10 w-14 cursor-pointer rounded-lg border-0 bg-transparent p-0"
                @input="update('primaryColor', ($event.target as HTMLInputElement).value)"
              >
              <input
                :value="form.primaryColor"
                type="text"
                class="min-w-0 flex-1 border-0 bg-transparent text-[1rem] font-medium text-slate-700 outline-none dark:text-slate-200"
                @input="update('primaryColor', ($event.target as HTMLInputElement).value)"
              >
            </div>
          </label>

          <label class="space-y-2">
            <span class="text-sm font-semibold text-slate-700 dark:text-slate-200">Font mặc định</span>
            <select
              :value="form.fontFamily"
              class="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-[1rem] text-slate-900 outline-none transition focus:border-[#963131] focus:ring-4 focus:ring-[#963131]/10 dark:border-slate-700 dark:bg-slate-950 dark:text-slate-100"
              @change="update('fontFamily', ($event.target as HTMLSelectElement).value)"
            >
              <option value="Inter">Inter</option>
              <option value="Manrope">Manrope</option>
              <option value="Merriweather">Merriweather</option>
              <option value="IBM Plex Sans">IBM Plex Sans</option>
            </select>
          </label>
        </div>
      </div>

      <div class="overflow-hidden rounded-2xl border border-dashed border-slate-300 bg-slate-50 dark:border-slate-700 dark:bg-slate-950">
        <img
          v-if="form.thumbnail"
          :src="form.thumbnail"
          alt="Preview thumbnail"
          class="h-full w-full object-cover"
        >
        <div
          v-else
          class="flex h-full min-h-[220px] flex-col items-center justify-center gap-3 px-6 text-center text-slate-400"
        >
          <span class="material-symbols-outlined text-4xl">photo</span>
          <p class="text-sm leading-6">Dán URL ảnh preview để kiểm tra thumbnail ngay tại đây.</p>
        </div>
      </div>
    </div>
  </CvTemplateFormSection>
</template>

<script setup lang="ts">
import CvTemplateFormSection from './CvTemplateFormSection.vue'

export type CvTemplateFormMedia = {
  thumbnail: string
  primaryColor: string
  fontFamily: string
}

defineProps<{
  form: CvTemplateFormMedia
  allowUpload?: boolean
}>()

const emit = defineEmits<{
  update: [field: keyof CvTemplateFormMedia, value: string]
  'select-file': [file: File | null]
}>()

function update(field: keyof CvTemplateFormMedia, value: string) {
  emit('update', field, value)
}

function handleFileChange(event: Event) {
  const file = (event.target as HTMLInputElement).files?.[0] ?? null
  emit('select-file', file)
}
</script>
