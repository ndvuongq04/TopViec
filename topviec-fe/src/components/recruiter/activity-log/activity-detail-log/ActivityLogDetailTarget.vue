<template>
  <div class="bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-800 p-6 shadow-sm hover:shadow-md transition-shadow">
    <div class="flex items-center gap-2 mb-6">
      <span class="material-symbols-outlined text-indigo-500">track_changes</span>
      <h3 class="text-xs font-extrabold text-slate-400 uppercase tracking-widest">Đối tượng tác động</h3>
    </div>

    <div class="flex items-center gap-5">
      <div class="w-16 h-16 rounded-2xl bg-indigo-50 dark:bg-indigo-900/20 flex items-center justify-center border-2 border-white dark:border-slate-800 shadow-inner shrink-0">
        <span class="material-symbols-outlined text-3xl text-indigo-500">
          {{ getTargetIcon(target.type) }}
        </span>
      </div>
      
      <div class="flex-1 space-y-3">
        <div>
          <h4 class="text-lg font-extrabold text-slate-900 dark:text-white leading-none">{{ target.name }}</h4>
          <p class="text-xs text-slate-400 font-bold uppercase tracking-widest mt-2">{{ target.type }}</p>
        </div>

        <div class="flex items-center gap-4 pt-3 border-t border-slate-100 dark:border-slate-800">
          <div class="flex items-center gap-2">
            <span class="text-[10px] font-bold text-slate-400 uppercase">ID:</span>
            <span class="text-xs font-mono font-bold px-2 py-0.5 bg-slate-50 dark:bg-slate-800 text-slate-600 dark:text-slate-400 rounded">
              {{ target.targetId }}
            </span>
          </div>
          <div class="flex items-center gap-2">
            <span class="text-[10px] font-bold text-slate-400 uppercase">Mô tả:</span>
            <span class="text-xs font-bold text-slate-700 dark:text-slate-300">{{ target.subInfo }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  target: { type: string; name: string; targetId: string; subInfo: string }
}>()

function getTargetIcon(type: string) {
  const t = type.toLowerCase()
  if (t.includes('job') || t.includes('tin')) return 'work'
  if (t.includes('candidate') || t.includes('ứng viên')) return 'person_search'
  if (t.includes('application') || t.includes('hồ sơ')) return 'description'
  if (t.includes('user') || t.includes('người dùng')) return 'account_circle'
  if (t.includes('company') || t.includes('công ty')) return 'business'
  return 'deployed_code'
}
</script>