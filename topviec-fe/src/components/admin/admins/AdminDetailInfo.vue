<template>
  <div class="lg:col-span-2 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 overflow-hidden shadow-sm">
    <div class="p-6">
      <h3 class="text-lg font-bold mb-6 flex items-center gap-2">
        <span class="material-symbols-outlined text-[#963131]">person</span>
        Thông tin cá nhân
      </h3>
      <div class="flex flex-col md:flex-row gap-8">
        <!-- Avatar -->
        <div class="flex flex-col items-center gap-4">
          <div class="relative">
            <div class="w-32 h-32 rounded-full border-4 border-[#963131]/10 p-1">
              <div
                class="w-full h-full rounded-full bg-[#963131]/10 flex items-center justify-center text-[#963131] text-3xl font-bold"
              >
                {{ initials }}
              </div>
            </div>
            <span
              class="absolute bottom-2 right-2 w-6 h-6 border-4 border-white dark:border-slate-900 rounded-full"
              :class="admin.isActive ? 'bg-green-500' : 'bg-red-500'"
            ></span>
          </div>
          <span
            class="px-3 py-1 text-xs font-bold rounded-full uppercase tracking-wider"
            :class="admin.isActive
              ? 'bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400'
              : 'bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400'"
          >
            {{ admin.isActive ? 'Hoạt động' : 'Bị khóa' }}
          </span>
        </div>

        <!-- Info Grid -->
        <div class="flex-1 grid grid-cols-1 md:grid-cols-2 gap-y-6 gap-x-12">
          <div class="space-y-1">
            <p class="text-xs font-semibold text-slate-400 uppercase">Họ và tên</p>
            <p class="font-medium text-slate-900 dark:text-slate-100">{{ admin.fullName }}</p>
          </div>
          <div class="space-y-1">
            <p class="text-xs font-semibold text-slate-400 uppercase">Vai trò</p>
            <p class="font-medium text-[#963131]">{{ roleLabel }}</p>
          </div>
          <div class="space-y-1">
            <p class="text-xs font-semibold text-slate-400 uppercase">Email</p>
            <p class="font-medium">{{ admin.email }}</p>
          </div>
          <div class="space-y-1">
            <p class="text-xs font-semibold text-slate-400 uppercase">Phòng ban</p>
            <p class="font-medium text-slate-600 dark:text-slate-400">{{ admin.department ?? '—' }}</p>
          </div>
          <div class="space-y-1">
            <p class="text-xs font-semibold text-slate-400 uppercase">Ngày tham gia</p>
            <p class="font-medium text-slate-600 dark:text-slate-400">{{ formattedDate }}</p>
          </div>
          <div class="space-y-1">
            <p class="text-xs font-semibold text-slate-400 uppercase">Mã Admin</p>
            <p class="font-medium text-slate-600 dark:text-slate-400 font-mono">#AD-{{ admin.adminUsersId }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import dayjs from 'dayjs'
import type { ResAdminUser } from '@/types/adminUser.types'
import { ADMIN_ROLE_LABELS } from '@/types/adminUser.types'

const props = defineProps<{
  admin: ResAdminUser
}>()

const initials = computed(() =>
  props.admin.fullName.split(' ').slice(-2).map(w => w[0]).join('').toUpperCase()
)

const roleLabel = computed(() => ADMIN_ROLE_LABELS[props.admin.adminRole] ?? props.admin.adminRole)

const formattedDate = computed(() =>
  props.admin.createdAt ? dayjs(props.admin.createdAt).format('DD/MM/YYYY') : '—'
)
</script>
