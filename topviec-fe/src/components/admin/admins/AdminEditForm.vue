<template>
  <div class="bg-white dark:bg-slate-900 rounded-xl shadow-sm border border-[#963131]/10 p-6">
    <h3 class="text-lg font-bold mb-6 flex items-center gap-2">
      <span class="material-symbols-outlined text-[#963131]">person</span>
      Thông tin chung
    </h3>
    <div class="flex flex-col md:flex-row gap-8">
      <!-- Avatar -->
      <div class="flex flex-col items-center gap-4">
        <div class="relative">
          <div class="w-32 h-32 rounded-full bg-[#963131]/10 border-2 border-dashed border-[#963131]/30 flex items-center justify-center overflow-hidden">
            <span class="text-[#963131] text-3xl font-bold">{{ initials }}</span>
          </div>
        </div>
        <p class="text-xs text-slate-500 text-center">
          Định dạng JPG, PNG.<br />Tối đa 2MB.
        </p>
      </div>

      <!-- Form Fields -->
      <div class="flex-1 grid grid-cols-1 md:grid-cols-2 gap-4">
        <div class="space-y-2">
          <label class="text-sm font-semibold">Họ và tên</label>
          <input
            :value="fullName"
            class="w-full rounded-lg border-slate-200 focus:ring-[#963131] focus:border-[#963131]"
            type="text"
            @input="$emit('update:fullName', ($event.target as HTMLInputElement).value)"
          />
        </div>
        <div class="space-y-2">
          <label class="text-sm font-semibold">Email</label>
          <input
            :value="email"
            class="w-full rounded-lg border-slate-100 bg-slate-50 text-slate-500 cursor-not-allowed"
            type="email"
            readonly
          />
        </div>
        <div class="space-y-2 md:col-span-2">
          <label class="text-sm font-semibold">Phòng ban</label>
          <select
            :value="department"
            class="w-full rounded-lg border-slate-200 focus:ring-[#963131] focus:border-[#963131]"
            @change="$emit('update:department', ($event.target as HTMLSelectElement).value)"
          >
            <option value="">— Chọn phòng ban —</option>
            <option value="Vận hành">Vận hành</option>
            <option value="Nội dung">Nội dung</option>
            <option value="Tài chính">Tài chính</option>
            <option value="Kỹ thuật">Kỹ thuật</option>
            <option value="Hỗ trợ">Hỗ trợ</option>
          </select>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  fullName: string
  email: string
  department: string
}>()

defineEmits<{
  'update:fullName': [value: string]
  'update:department': [value: string]
}>()

const initials = computed(() =>
  props.fullName?.split(' ').slice(-2).map(w => w[0]).join('').toUpperCase() ?? ''
)
</script>
