<template>
  <div class="space-y-6">

    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white dark:bg-surface-dark p-6 rounded-3xl border border-slate-200 dark:border-slate-800 shadow-sm">
      <div>
        <h1 class="text-2xl font-bold text-text-main dark:text-white">Thông báo việc làm</h1>
        <p class="text-text-muted text-sm mt-1">Quản lý thông báo và tùy chọn tìm việc của bạn.</p>
      </div>
      <div class="flex items-center gap-4">
        <div class="px-4 py-2 bg-slate-100 dark:bg-slate-800 rounded-xl border border-slate-200 dark:border-slate-700">
          <span class="text-sm text-text-muted font-medium">Đã dùng: </span>
          <span class="text-sm font-bold text-primary">{{ alerts.length }}/5</span>
        </div>
        <button
          class="flex items-center gap-2 bg-primary hover:bg-primary-hover text-white px-5 py-2.5 rounded-2xl font-bold text-sm shadow-lg shadow-blue-500/30 transition-colors cursor-pointer"
          @click="openCreate"
        >
          <span class="material-symbols-outlined text-[20px]">add_alert</span>
          Tạo thông báo
        </button>
      </div>
    </div>

    <!-- Alert list -->
    <div class="space-y-4">
      <div
        v-for="alert in alerts"
        :key="alert.id"
        class="group bg-white dark:bg-surface-dark p-6 rounded-3xl border border-slate-200 dark:border-slate-800 shadow-sm hover:shadow-md hover:border-primary/30 transition-all"
      >
        <div class="flex flex-col md:flex-row justify-between gap-6">

          <!-- Left: info -->
          <div class="flex-1 space-y-4">
            <div>
              <h3 class="text-lg font-bold text-text-main dark:text-white group-hover:text-primary transition-colors">
                {{ alert.title }}
              </h3>
              <div class="flex flex-wrap gap-2 mt-2">
                <span
                  v-for="tag in alert.tags"
                  :key="tag"
                  class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                  :class="alert.active ? 'bg-blue-50 dark:bg-blue-900/20 text-blue-700 dark:text-blue-400' : 'bg-slate-100 dark:bg-slate-800 text-text-muted'"
                >
                  {{ tag }}
                </span>
              </div>
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-3 gap-3 text-sm text-text-muted">
              <div class="flex items-center gap-2">
                <span class="material-symbols-outlined text-[18px]">location_on</span>
                {{ alert.location }}
              </div>
              <div class="flex items-center gap-2">
                <span class="material-symbols-outlined text-[18px]">attach_money</span>
                {{ alert.salary }}
              </div>
              <div class="flex items-center gap-2">
                <span class="material-symbols-outlined text-[18px]">schedule</span>
                Tần suất: <span class="font-semibold text-text-main dark:text-white ml-1">{{ alert.frequency }}</span>
              </div>
            </div>
          </div>

          <!-- Right: toggle + actions -->
          <div class="flex flex-row md:flex-col justify-between items-end gap-4 md:border-l md:border-slate-200 dark:md:border-slate-700 md:pl-6 md:min-w-[140px]">
            <!-- Toggle -->
            <div class="flex items-center gap-3 w-full justify-between md:justify-end">
              <span class="text-xs font-bold text-text-muted uppercase tracking-wider md:hidden">Trạng thái</span>
              <ToggleSwitch v-model="alert.active" />
            </div>

            <!-- Edit / Delete (desktop) -->
            <div class="hidden md:flex items-center gap-2 w-full justify-end">
              <button class="flex-1 flex items-center justify-center gap-1 px-3 py-2 text-sm font-medium text-text-muted bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl hover:text-primary hover:border-primary transition-all cursor-pointer">
                Sửa
              </button>
              <button class="p-2 text-text-muted hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/10 rounded-xl transition-colors cursor-pointer">
                <span class="material-symbols-outlined text-[20px]">delete</span>
              </button>
            </div>
          </div>
        </div>

        <!-- Edit / Delete (mobile) -->
        <div class="flex md:hidden items-center gap-3 mt-4 pt-4 border-t border-slate-100 dark:border-slate-800">
          <button class="flex-1 py-2 text-sm font-medium text-text-muted bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl hover:bg-slate-50 cursor-pointer">
            Sửa thông báo
          </button>
          <button class="p-2 text-text-muted hover:text-red-500 hover:bg-red-50 rounded-xl transition-colors cursor-pointer">
            <span class="material-symbols-outlined text-[20px]">delete</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Empty state -->
    <div v-if="alerts.length === 0" class="bg-white dark:bg-surface-dark p-12 rounded-3xl border border-slate-200 dark:border-slate-800 shadow-sm flex flex-col items-center justify-center text-center">
      <div class="w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center text-primary mb-4">
        <span class="material-symbols-outlined text-3xl">notifications_off</span>
      </div>
      <h3 class="text-lg font-bold text-text-main dark:text-white mb-2">Chưa có thông báo nào</h3>
      <p class="text-text-muted text-sm mb-6">Tạo thông báo để nhận tin tuyển dụng phù hợp.</p>
      <button class="flex items-center gap-2 bg-primary hover:bg-primary-hover text-white px-5 py-2.5 rounded-2xl font-bold text-sm shadow-lg shadow-blue-500/30 transition-colors cursor-pointer">
        <span class="material-symbols-outlined text-[20px]">add_alert</span>
        Tạo thông báo đầu tiên
      </button>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import ToggleSwitch from '@/components/common/ToggleSwitch.vue'

interface JobAlert {
  id: number
  title: string
  tags: string[]
  location: string
  salary: string
  frequency: string
  active: boolean
}

const alerts = ref<JobAlert[]>([
  {
    id: 1,
    title: 'Senior UX Designer tại HCM',
    tags: ['UX Design', 'Figma', 'Product Design'],
    location: 'Hồ Chí Minh',
    salary: '$1,500 - $2,500',
    frequency: 'Hàng ngày',
    active: true,
  },
  {
    id: 2,
    title: 'Remote Frontend Developer',
    tags: ['ReactJS', 'VueJS', 'Tailwind CSS'],
    location: 'Remote / Hà Nội',
    salary: 'Thương lượng',
    frequency: 'Hàng tuần',
    active: false,
  },
])

function openCreate() {
  // TODO: mở modal tạo alert
}
</script>