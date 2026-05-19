<template>
  <div class="bg-white dark:bg-white/5 rounded-xl border border-slate-200 dark:border-white/10 overflow-hidden shadow-sm">
    <div class="p-6 border-b border-slate-100 dark:border-white/10 flex items-center justify-between">
      <h4 class="font-bold text-lg">Tin tuyển dụng chờ duyệt</h4>
      <router-link to="/admin/moderation" class="text-[#963131] text-sm font-semibold hover:underline">
        Xem tất cả
      </router-link>
    </div>

    <div class="overflow-x-auto">
      <table class="w-full text-left">
        <thead class="bg-slate-50 dark:bg-white/5 text-slate-500 text-xs font-bold uppercase">
          <tr>
            <th class="px-6 py-4">Mã ID</th>
            <th class="px-6 py-4">Tiêu đề</th>
            <th class="px-6 py-4">Công ty</th>
            <th class="px-6 py-4">Ngày đăng</th>
            <th class="px-6 py-4 text-center">Hành động</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-100 dark:divide-white/10 text-sm">
          <tr
            v-for="job in jobs"
            :key="job.id"
            class="hover:bg-slate-50/50 dark:hover:bg-white/5 transition-colors"
          >
            <td class="px-6 py-4 font-mono text-slate-500">{{ job.code }}</td>
            <td class="px-6 py-4 font-medium">{{ job.title }}</td>
            <td class="px-6 py-4 text-slate-600 dark:text-slate-400">{{ job.company }}</td>
            <td class="px-6 py-4 text-slate-500">{{ job.postedAt }}</td>
            <td class="px-6 py-4">
              <div class="flex items-center justify-center gap-1">
                <button
                  class="p-2 rounded-lg transition-colors text-[#963131] hover:bg-[#963131]/10"
                  title="Xem chi tiết"
                  @click="$emit('view', job)"
                >
                  <span class="material-symbols-outlined text-lg">visibility</span>
                </button>
                <button
                  class="p-2 hover:bg-emerald-100 dark:hover:bg-emerald-900/30 text-emerald-600 rounded-lg transition-colors"
                  title="Duyệt"
                  @click="$emit('approve', job)"
                >
                  <span class="material-symbols-outlined text-lg">check_circle</span>
                </button>
                <button
                  class="p-2 hover:bg-rose-100 dark:hover:bg-rose-900/30 text-rose-600 rounded-lg transition-colors"
                  title="Từ chối"
                  @click="$emit('reject', job)"
                >
                  <span class="material-symbols-outlined text-lg">cancel</span>
                </button>
              </div>
            </td>
          </tr>

          <tr v-if="jobs.length === 0">
            <td colspan="5" class="px-6 py-12 text-center text-slate-400 text-sm">
              <span class="material-symbols-outlined text-4xl block mb-2">done_all</span>
              Không có tin nào đang chờ duyệt
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
export interface PendingJob {
  id: number
  code: string
  title: string
  company: string
  postedAt: string
}

withDefaults(defineProps<{
  jobs?: PendingJob[]
}>(), {
  jobs: () => [
    { id: 1, code: '#TV-8821', title: 'Senior Frontend Developer (React)', company: 'FPT Software',   postedAt: '24/05/2024' },
    { id: 2, code: '#TV-8825', title: 'Digital Marketing Manager',          company: 'Vingroup JSC',   postedAt: '24/05/2024' },
    { id: 3, code: '#TV-8830', title: 'UI/UX Designer Senior',              company: 'Garena VN',      postedAt: '23/05/2024' },
    { id: 4, code: '#TV-8832', title: 'Business Analyst',                   company: 'Shopee Vietnam', postedAt: '23/05/2024' },
  ],
})

defineEmits<{   
  view:    [job: PendingJob]
  approve: [job: PendingJob]
  reject:  [job: PendingJob]
}>()
</script>