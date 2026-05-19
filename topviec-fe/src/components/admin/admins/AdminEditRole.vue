<template>
  <div class="bg-white dark:bg-slate-900 rounded-xl shadow-sm border border-[#963131]/10 p-6">
    <h3 class="text-lg font-bold mb-6 flex items-center gap-2">
      <span class="material-symbols-outlined text-[#963131]">verified_user</span>
      Phân quyền quản trị
    </h3>
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <label
        v-for="role in roles"
        :key="role.value"
        class="relative flex flex-col p-4 border rounded-xl cursor-pointer hover:border-[#963131] group transition-all"
        :class="{ 'border-[#963131] bg-[#963131]/5': modelValue === role.value }"
      >
        <input
          :checked="modelValue === role.value"
          class="absolute top-4 right-4 text-[#963131] focus:ring-[#963131] h-4 w-4"
          name="admin-role"
          type="radio"
          :value="role.value"
          @change="$emit('update:modelValue', role.value)"
        />
        <span
          class="material-symbols-outlined mb-2"
          :class="modelValue === role.value ? 'text-[#963131]' : 'text-slate-400 group-hover:text-[#963131]'"
        >
          {{ role.icon }}
        </span>
        <span class="text-sm font-bold">{{ role.label }}</span>
        <span class="text-xs text-slate-500 mt-1">{{ role.desc }}</span>
      </label>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { AdminRole } from '@/types/adminUser.types'

defineProps<{
  modelValue: AdminRole
}>()

defineEmits<{
  'update:modelValue': [value: AdminRole]
}>()

const roles = [
  { value: 'super_admin' as AdminRole, label: 'Super Admin', desc: 'Toàn quyền hệ thống', icon: 'stars' },
  { value: 'content_moderator' as AdminRole, label: 'Content Moderator', desc: 'Quản lý bài đăng', icon: 'article' },
  { value: 'finance_admin' as AdminRole, label: 'Finance Admin', desc: 'Quản lý thanh toán', icon: 'payments' },
  { value: 'support_admin' as AdminRole, label: 'Support Admin', desc: 'Hỗ trợ khách hàng', icon: 'support_agent' },
]
</script>
