<template>
  <div v-if="visible" class="fixed inset-0 z-50 flex items-center justify-center p-4">
    <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm" @click="$emit('close')"></div>

    <div class="relative bg-white rounded-2xl shadow-xl w-full max-w-sm flex flex-col overflow-hidden">
      <!-- Header -->
      <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
        <h3 class="text-lg font-bold text-slate-900">Đổi tên hành động</h3>
        <button class="text-slate-400 hover:text-slate-600 transition-colors cursor-pointer" @click="$emit('close')">
          <span class="material-symbols-outlined">close</span>
        </button>
      </div>

      <!-- Body -->
      <div class="p-6 space-y-4">
        <div>
          <label class="block text-sm font-semibold text-slate-700 mb-1.5">
            Mã hành động
          </label>
          <p class="px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg text-sm font-mono text-slate-500">
            {{ permId }}
          </p>
        </div>
        <div>
          <label class="block text-sm font-semibold text-slate-700 mb-1.5">
            Tên mới <span class="text-red-500">*</span>
          </label>
          <input
            ref="inputRef"
            v-model="name"
            type="text"
            placeholder="Nhập tên mới..."
            class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-[#963131]/20 focus:border-[#963131]"
            @keydown.enter="handleSubmit"
          />
          <p v-if="error" class="text-red-500 text-xs mt-1">{{ error }}</p>
        </div>
      </div>

      <!-- Footer -->
      <div class="px-6 py-4 border-t border-slate-100 bg-slate-50 flex justify-end gap-3 rounded-b-2xl">
        <button
          class="px-4 py-2 text-sm font-semibold text-slate-600 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 shadow-sm transition-colors cursor-pointer"
          @click="$emit('close')"
        >
          Huỷ
        </button>
        <button
          class="px-4 py-2 text-sm font-semibold text-white bg-[#963131] rounded-lg hover:opacity-90 shadow-md shadow-[#963131]/20 transition-all cursor-pointer"
          @click="handleSubmit"
        >
          Lưu tên mới
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue'

const props = defineProps<{
  visible: boolean
  permId: string
  currentLabel: string
}>()

const emit = defineEmits<{
  close: []
  submit: [{ permId: string; newName: string }]
}>()

const name = ref('')
const error = ref('')
const inputRef = ref<HTMLInputElement | null>(null)

watch(() => props.visible, async (val) => {
  if (val) {
    name.value = props.currentLabel
    error.value = ''
    await nextTick()
    inputRef.value?.select()
  }
})

const handleSubmit = () => {
  const trimmed = name.value.trim()
  if (!trimmed) {
    error.value = 'Vui lòng nhập tên hành động.'
    return
  }
  if (trimmed === props.currentLabel) {
    error.value = 'Tên mới phải khác tên hiện tại.'
    return
  }
  emit('submit', { permId: props.permId, newName: trimmed })
}
</script>
