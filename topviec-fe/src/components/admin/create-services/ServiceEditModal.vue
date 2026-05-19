<template>
  <div v-if="visible" class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-sm p-4">
    <div class="bg-white rounded-xl shadow-2xl w-full max-w-lg overflow-hidden animate-in fade-in zoom-in-95 duration-200">
      
      <!-- Header -->
      <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
        <h3 class="text-lg font-bold text-slate-900 tracking-tight">Sửa dịch vụ</h3>
        <button class="text-slate-400 hover:text-slate-600 transition-colors cursor-pointer" @click="$emit('close')">
          <span class="material-symbols-outlined">close</span>
        </button>
      </div>

      <!-- Body -->
      <div class="p-6 space-y-4">
        <div>
          <label class="block text-xs font-bold text-slate-600 uppercase tracking-widest mb-2">Tên dịch vụ</label>
          <input
            v-model="form.name"
            class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-[#963131] focus:ring-2 focus:ring-[#963131]/20 transition-all focus:bg-white"
            type="text"
            placeholder="Nhập tên dịch vụ"
          />
        </div>

        <div>
          <label class="block text-xs font-bold text-slate-600 uppercase tracking-widest mb-2">Nhóm dịch vụ</label>
          <div class="relative">
            <select
              v-model="form.category"
              class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-[#963131] focus:ring-2 focus:ring-[#963131]/20 transition-all appearance-none bg-none cursor-pointer focus:bg-white"
            >
              <option v-for="g in groups" :key="g.value" :value="g.value">{{ g.label }}</option>
            </select>
            <span class="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none">expand_more</span>
          </div>
        </div>

        <div>
          <label class="block text-xs font-bold text-slate-600 uppercase tracking-widest mb-2">Mô tả</label>
          <textarea
            v-model="form.description"
            class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-lg text-sm text-slate-800 focus:outline-none focus:border-[#963131] focus:ring-2 focus:ring-[#963131]/20 transition-all resize-y min-h-[100px] focus:bg-white"
            placeholder="Nhập thông tin mô tả chi tiết"
          ></textarea>
        </div>
      </div>

      <!-- Footer -->
      <div class="px-6 py-4 border-t border-slate-100 bg-slate-50 flex items-center justify-end gap-3">
        <button
          class="px-5 py-2 text-sm font-semibold text-slate-600 bg-white border border-slate-200 rounded-lg hover:bg-slate-50 transition-colors cursor-pointer"
          @click="$emit('close')"
          :disabled="submitting"
        >
          Hủy
        </button>
        <button
          class="px-5 py-2.5 text-sm font-semibold text-white bg-gradient-to-br from-[#76191d] to-[#963131] rounded-lg hover:opacity-90 transition-opacity flex items-center gap-2 disabled:opacity-50 cursor-pointer"
          @click="submit"
          :disabled="submitting"
        >
          <span v-if="submitting" class="material-symbols-outlined text-sm animate-spin">progress_activity</span>
          Lưu thay đổi
        </button>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import type { ResServiceDTO, ReqServiceDTO } from '@/types/serviceCatalog.types'
import {
  ServiceCategory,
  SERVICE_CATEGORY_LABELS,
} from '@/constants/serviceCatalog.constants'

const props = defineProps<{
  visible: boolean
  service: ResServiceDTO | null
  submitting: boolean
}>()

const emit = defineEmits<{
  close: []
  submit: [id: number, payload: ReqServiceDTO]
}>()

const groups = [
  { value: ServiceCategory.JOB_POSTING, label: SERVICE_CATEGORY_LABELS[ServiceCategory.JOB_POSTING] },
  { value: ServiceCategory.CANDIDATE,   label: SERVICE_CATEGORY_LABELS[ServiceCategory.CANDIDATE]   },
  { value: ServiceCategory.BRANDING,    label: SERVICE_CATEGORY_LABELS[ServiceCategory.BRANDING]    },
  { value: ServiceCategory.ADDON_PACKAGE, label: SERVICE_CATEGORY_LABELS[ServiceCategory.ADDON_PACKAGE] },
]

const form = ref({
  name: '',
  category: '' as any,
  description: ''
})

watch(() => props.service, (newVal) => {
  if (newVal) {
    form.value.name = newVal.name || ''
    form.value.category = newVal.category || ''
    form.value.description = newVal.description || ''
  }
})
// Reset form on close if needed
watch(() => props.visible, (newVal) => {
  if (newVal && props.service) {
    form.value.name = props.service.name || ''
    form.value.category = props.service.category || ''
    form.value.description = props.service.description || ''
  }
})

function submit() {
  if (!props.service) return
  emit('submit', props.service.id, {
    code: props.service.code,
    name: form.value.name.trim(),
    category: form.value.category,
    unit: props.service.unit,
    description: form.value.description.trim() || null,
    isActive: props.service.isActive
  })
}
</script>
