<template>
  <div class="space-y-5 lg:sticky lg:top-6">
    <div class="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
      <div class="flex items-center justify-between border-b border-slate-200 px-5 py-4">
        <div>
          <p class="text-xs font-black uppercase tracking-[0.18em] text-slate-500">Preview</p>
          <h3 class="mt-1 text-lg font-black text-slate-900">Template với sample data</h3>
        </div>
        <div class="flex items-center gap-2">
          <span
            class="rounded-full px-3 py-1 text-[11px] font-bold uppercase tracking-[0.12em]"
            :class="preview?.valid ? 'bg-emerald-100 text-emerald-700' : 'bg-amber-100 text-amber-700'"
          >
            {{ preview?.valid ? 'Hop le' : 'Can kiem tra' }}
          </span>
          <span
            v-if="preview?.versionTag"
            class="rounded-full bg-slate-100 px-3 py-1 font-mono text-[11px] font-bold text-slate-700"
          >
            {{ preview.versionTag }}
          </span>
        </div>
      </div>

      <div class="border-b border-slate-200 bg-slate-50 px-5 py-4">
        <div class="flex flex-wrap items-center gap-2">
          <button
            type="button"
            class="inline-flex items-center gap-2 rounded-xl bg-[#963131] px-4 py-2 text-sm font-bold text-white transition hover:bg-[#963131]/90"
            :disabled="previewing"
            @click="$emit('preview')"
          >
            <span class="material-symbols-outlined text-[18px]">preview</span>
            {{ previewing ? 'Dang render...' : 'Xem preview' }}
          </button>
          <button
            type="button"
            class="inline-flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-4 py-2 text-sm font-bold text-slate-700 transition hover:bg-slate-50"
            @click="$emit('reload-sample-data')"
          >
            <span class="material-symbols-outlined text-[18px]">database</span>
            Dung sample data
          </button>
        </div>
        <p class="mt-3 text-xs leading-5 text-slate-500">
          Preview nay dung HTML da render tu BE va sample data chuan de admin so layout gan voi ban PDF hon.
        </p>
      </div>

      <div class="p-5">
        <div class="overflow-hidden rounded-2xl border border-dashed border-slate-300 bg-slate-100">
          <div v-if="previewing" class="flex h-[520px] items-center justify-center bg-slate-50 text-slate-500">
            <div class="flex flex-col items-center gap-3">
              <span class="material-symbols-outlined animate-spin text-3xl">progress_activity</span>
              <p class="text-sm font-medium">Dang render preview tu backend...</p>
            </div>
          </div>

          <iframe
            v-else-if="previewDocument"
            :srcdoc="previewDocument"
            class="h-[520px] w-full bg-white"
            sandbox="allow-same-origin"
            title="CV template preview"
          />

          <div v-else class="flex h-[520px] items-center justify-center bg-slate-50 text-slate-500">
            <div class="flex max-w-[280px] flex-col items-center gap-3 text-center">
              <span class="material-symbols-outlined text-4xl">description</span>
              <p class="text-sm font-medium">Nhap HTML/CSS hop le roi bam xem preview de kiem tra template.</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
      <div class="flex items-center justify-between gap-3">
        <h4 class="text-sm font-black uppercase tracking-[0.14em] text-slate-700">Validation</h4>
        <span class="text-xs font-semibold text-slate-400">
          Root: {{ preview?.rootPlaceholders.length ?? 0 }} | Section: {{ preview?.sections.length ?? 0 }}
        </span>
      </div>

      <div class="mt-4 space-y-4">
        <div class="rounded-2xl border border-rose-200 bg-rose-50 p-4" :class="{ 'opacity-70': !preview?.placeholderErrors.length }">
          <div class="flex items-center gap-2">
            <span class="material-symbols-outlined text-[18px] text-rose-600">error</span>
            <p class="text-sm font-bold text-rose-700">Loi placeholder</p>
          </div>
          <ul v-if="preview?.placeholderErrors.length" class="mt-3 space-y-2 text-sm text-rose-700">
            <li v-for="item in preview.placeholderErrors" :key="item">{{ item }}</li>
          </ul>
          <p v-else class="mt-3 text-sm text-emerald-700">Khong phat hien loi placeholder.</p>
        </div>

        <div class="rounded-2xl border border-amber-200 bg-amber-50 p-4" :class="{ 'opacity-70': !preview?.cssWarnings.length }">
          <div class="flex items-center gap-2">
            <span class="material-symbols-outlined text-[18px] text-amber-600">warning</span>
            <p class="text-sm font-bold text-amber-700">Canh bao CSS</p>
          </div>
          <ul v-if="preview?.cssWarnings.length" class="mt-3 space-y-2 text-sm text-amber-700">
            <li v-for="item in preview.cssWarnings" :key="item">{{ item }}</li>
          </ul>
          <p v-else class="mt-3 text-sm text-emerald-700">Khong co canh bao CSS dang luu y.</p>
        </div>
      </div>
    </div>

    <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
      <h4 class="text-sm font-black uppercase tracking-[0.14em] text-slate-700">Contract</h4>
      <div class="mt-4 space-y-4">
        <div>
          <p class="text-xs font-bold uppercase tracking-[0.12em] text-slate-400">Root placeholders</p>
          <div class="mt-2 flex flex-wrap gap-2">
            <span
              v-for="item in preview?.rootPlaceholders ?? []"
              :key="item"
              class="rounded-full bg-slate-100 px-3 py-1 font-mono text-[11px] font-semibold text-slate-700"
            >
              {{ item }}
            </span>
            <span v-if="!(preview?.rootPlaceholders?.length)" class="text-sm text-slate-400">Chua co du lieu.</span>
          </div>
        </div>

        <div>
          <p class="text-xs font-bold uppercase tracking-[0.12em] text-slate-400">Sections</p>
          <div class="mt-2 flex flex-wrap gap-2">
            <span
              v-for="item in preview?.sections ?? []"
              :key="item"
              class="rounded-full bg-[#963131]/10 px-3 py-1 text-[11px] font-semibold text-[#963131]"
            >
              {{ item }}
            </span>
            <span v-if="!(preview?.sections?.length)" class="text-sm text-slate-400">Chua co section nao.</span>
          </div>
        </div>
      </div>
    </div>

    <div class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
      <h4 class="text-sm font-black uppercase tracking-[0.14em] text-slate-700">Sample Data</h4>
      <div v-if="sampleData" class="mt-4 space-y-3 text-sm text-slate-600">
        <div class="flex items-center justify-between gap-3">
          <span>Ho ten</span>
          <span class="font-semibold text-slate-900">{{ sampleData.personalInfo.fullName || 'N/A' }}</span>
        </div>
        <div class="flex items-center justify-between gap-3">
          <span>Email</span>
          <span class="font-semibold text-slate-900">{{ sampleData.personalInfo.email || 'N/A' }}</span>
        </div>
        <div class="flex items-center justify-between gap-3">
          <span>Kinh nghiem</span>
          <span class="font-semibold text-slate-900">{{ sampleData.experiences.length }} muc</span>
        </div>
        <div class="flex items-center justify-between gap-3">
          <span>Hoc van</span>
          <span class="font-semibold text-slate-900">{{ sampleData.educations.length }} muc</span>
        </div>
        <div class="flex items-center justify-between gap-3">
          <span>Ky nang</span>
          <span class="font-semibold text-slate-900">{{ sampleData.skills.length }} muc</span>
        </div>
        <div class="flex items-center justify-between gap-3">
          <span>Du an</span>
          <span class="font-semibold text-slate-900">{{ sampleData.projects?.length ?? 0 }} muc</span>
        </div>
        <div class="flex items-center justify-between gap-3">
          <span>So thich</span>
          <span class="font-semibold text-slate-900">{{ sampleData.hobbies?.length ?? 0 }} muc</span>
        </div>
        <div class="flex items-center justify-between gap-3">
          <span>Giai thuong</span>
          <span class="font-semibold text-slate-900">{{ sampleData.awards?.length ?? 0 }} muc</span>
        </div>
        <div class="flex items-center justify-between gap-3">
          <span>Section tuy bien</span>
          <span class="font-semibold text-slate-900">{{ sampleData.customSections?.length ?? 0 }} muc</span>
        </div>
      </div>
      <p v-else class="mt-4 text-sm text-slate-400">Chua tai duoc sample data tu backend.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { CvOnlineExtraData, ResAdminCvTemplatePreview } from '@/types/cvOnline.types'

const props = defineProps<{
  preview: ResAdminCvTemplatePreview | null
  sampleData: CvOnlineExtraData | null
  previewing: boolean
}>()

defineEmits<{
  preview: []
  'reload-sample-data': []
}>()

const previewDocument = computed(() => {
  return props.preview?.renderedHtml ?? ''
})
</script>
