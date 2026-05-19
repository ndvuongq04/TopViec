<template>
  <div v-if="visible" class="fixed inset-0 z-50 flex items-center justify-center p-4">
    <!-- Backdrop -->
    <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm" @click="$emit('close')"></div>
    
    <!-- Modal -->
    <div class="relative bg-white rounded-2xl shadow-xl w-full max-w-md flex flex-col overflow-hidden">
      <!-- Header -->
      <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
        <h3 class="text-lg font-bold text-slate-900">Thêm mới hành động</h3>
        <button class="text-slate-400 hover:text-slate-600 transition-colors cursor-pointer" @click="$emit('close')">
          <span class="material-symbols-outlined">close</span>
        </button>
      </div>

      <!-- Body -->
      <div class="p-6 space-y-4">
        <div>
          <label class="block text-sm font-semibold text-slate-700 mb-1.5">Module <span class="text-red-500">*</span></label>
          <select v-model="form.moduleId" class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-[#963131]/20 focus:border-[#963131] cursor-pointer">
            <option value="" disabled>-- Chọn module --</option>
            <option v-for="g in groups" :key="g.id" :value="g.id">{{ g.label }} ({{ g.id }})</option>
          </select>
          <p v-if="errors.moduleId" class="text-red-500 text-xs mt-1">{{ errors.moduleId }}</p>
        </div>

        <div>
          <label class="block text-sm font-semibold text-slate-700 mb-1.5">Tên hành động <span class="text-red-500">*</span></label>
          <input type="text" v-model="form.name" placeholder="Ví dụ: Tải xuống CV" class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-[#963131]/20 focus:border-[#963131]" />
          <p v-if="errors.name" class="text-red-500 text-xs mt-1">{{ errors.name }}</p>
        </div>

        <div>
          <label class="block text-sm font-semibold text-slate-700 mb-1.5">Mã hành động <span class="text-red-500">*</span></label>
          <div class="flex items-stretch">
            <span class="inline-flex items-center px-3 rounded-l-lg border border-r-0 border-slate-200 bg-slate-100 text-slate-500 text-sm">
              {{ form.moduleId ? form.moduleId + ':' : '...:' }}
            </span>
            <input type="text" v-model="form.code" placeholder="download_cv" class="flex-1 w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-r-lg text-sm focus:outline-none focus:ring-2 focus:ring-[#963131]/20 focus:border-[#963131]" />
          </div>
          <p v-if="errors.code" class="text-red-500 text-xs mt-1">{{ errors.code }}</p>
          <p class="text-slate-400 text-xs mt-1">Chỉ chứa chữ cái thường, số và dấu gạch dưới (_).</p>
        </div>
      </div>

      <!-- Footer -->
      <div class="px-6 py-4 border-t border-slate-100 bg-slate-50 flex justify-end gap-3 rounded-b-2xl">
        <button class="px-4 py-2 text-sm font-semibold text-slate-600 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 shadow-sm transition-colors cursor-pointer" @click="$emit('close')">
          Huỷ
        </button>
        <button class="px-4 py-2 text-sm font-semibold text-white bg-[#963131] rounded-lg hover:opacity-90 shadow-md shadow-[#963131]/20 transition-all cursor-pointer" @click="handleSubmit">
          Thêm hành động
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, watch } from 'vue'

const props = defineProps<{
  visible: boolean;
  groups: any[];
}>()

const emit = defineEmits(['close', 'submit'])

const form = reactive({
  moduleId: '',
  name: '',
  code: ''
})

const errors = reactive({
  moduleId: '',
  name: '',
  code: ''
})

watch(() => props.visible, (val) => {
  if (val) {
    form.moduleId = ''
    form.name = ''
    form.code = ''
    clearErrors()
  }
})

const clearErrors = () => {
  errors.moduleId = ''
  errors.name = ''
  errors.code = ''
}

const checkDuplicate = (fullId: string) => {
  return props.groups.some(g => g.perms.some((p: any) => p.id === fullId))
}

const handleSubmit = () => {
  clearErrors()
  let hasError = false

  if (!form.moduleId) {
    errors.moduleId = 'Vui lòng chọn module.'
    hasError = true
  }

  if (!form.name.trim()) {
    errors.name = 'Vui lòng nhập tên hành động.'
    hasError = true
  }

  const codeVal = form.code.trim()
  if (!codeVal) {
    errors.code = 'Vui lòng nhập mã hành động.'
    hasError = true
  } else if (!/^[a-z0-9_]+$/.test(codeVal)) {
    errors.code = 'Mã hành động không hợp lệ (chỉ được dùng chữ thường, số, và dấu gạch dưới).'
    hasError = true
  }

  if (!hasError) {
    const fullId = `${form.moduleId}:${codeVal}`
    if (checkDuplicate(fullId)) {
      errors.code = `Mã hành động '${fullId}' đã tồn tại trong hệ thống.`
      hasError = true
    }
  }

  if (!hasError) {
    emit('submit', {
      moduleId: form.moduleId,
      perm: {
        id: `${form.moduleId}:${codeVal}`,
        label: form.name.trim(),
        m: false,
        r: false,
        v: false
      }
    })
  }
}
</script>
