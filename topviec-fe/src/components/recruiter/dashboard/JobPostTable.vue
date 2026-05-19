<template>
  <div class="bg-white dark:bg-slate-800 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700/50 overflow-hidden">
    <div class="p-6 border-b border-slate-100 dark:border-slate-700/50 flex items-center justify-between">
      <h4 class="font-bold text-lg">Tin tuyển dụng gần đây</h4>
      <button class="text-slate-500 hover:text-primary transition-colors cursor-pointer">
        <span class="material-symbols-outlined">more_horiz</span>
      </button>
    </div>

    <div class="overflow-x-auto">
      <table class="w-full text-left border-collapse">
        <thead>
          <tr class="text-xs font-bold text-slate-400 uppercase tracking-wider">
            <th class="px-6 py-4">Vị trí</th>
            <th class="px-6 py-4">Ngày đăng</th>
            <th class="px-6 py-4">Lượt xem</th>
            <th class="px-6 py-4">Hồ sơ</th>
            <th class="px-6 py-4 text-right">Trạng thái</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-50 dark:divide-slate-700/50">
          <tr
            v-for="job in jobs"
            :key="job.id"
            class="hover:bg-slate-50/50 dark:hover:bg-slate-700/20 transition-colors cursor-pointer"
            @click="$emit('select', job)"
          >
            <td class="px-6 py-4">
              <div class="flex flex-col">
                <span class="font-bold text-sm">{{ job.title }}</span>
                <span class="text-xs text-slate-400">{{ job.location }}</span>
              </div>
            </td>
            <td class="px-6 py-4 text-sm text-slate-600 dark:text-slate-400">{{ job.postedAt }}</td>
            <td class="px-6 py-4 text-sm text-slate-600 dark:text-slate-400">{{ job.views.toLocaleString('vi-VN') }}</td>
            <td class="px-6 py-4 text-sm font-bold text-primary">{{ job.applications }}</td>
            <td class="px-6 py-4 text-right">
              <span
                class="px-3 py-1 rounded-full text-xs font-bold"
                :class="job.status === 'active'
                  ? 'bg-green-100 dark:bg-green-900/30 text-green-600 dark:text-green-400'
                  : 'bg-slate-100 dark:bg-slate-700 text-slate-600 dark:text-slate-400'"
              >
                {{ job.status === 'active' ? 'Hoạt động' : 'Đã đóng' }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
export interface JobPost {
  id: number
  title: string
  location: string
  postedAt: string
  views: number
  applications: number
  status: 'active' | 'closed'
}

withDefaults(defineProps<{
  jobs?: JobPost[]
}>(), {
  jobs: () => [
    { id: 1, title: 'Senior Java Developer',    location: 'Hồ Chí Minh, Quận 1', postedAt: '12/10/2023', views: 1204, applications: 85, status: 'active' },
    { id: 2, title: 'Product Designer (UI/UX)', location: 'Hà Nội, Cầu Giấy',    postedAt: '08/10/2023', views: 850,  applications: 42, status: 'active' },
    { id: 3, title: 'Marketing Executive',      location: 'Đà Nẵng, Hải Châu',   postedAt: '01/10/2023', views: 420,  applications: 12, status: 'closed' },
  ],
})

defineEmits<{
  select: [job: JobPost]
}>()
</script>