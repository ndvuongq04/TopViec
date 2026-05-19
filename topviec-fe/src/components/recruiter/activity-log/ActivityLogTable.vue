<template>
  <div class="overflow-x-auto min-h-[300px]">
    <table class="w-full text-left">
      <thead>
        <tr class="bg-slate-50 dark:bg-slate-800/50 border-b border-slate-200 dark:border-slate-800 text-slate-500">
          <th class="px-6 py-4 text-[10px] font-extrabold uppercase tracking-widest whitespace-nowrap">Thời gian</th>
          <th class="px-6 py-4 text-[10px] font-extrabold uppercase tracking-widest whitespace-nowrap text-center">Vai trò</th>
          <th class="px-6 py-4 text-[10px] font-extrabold uppercase tracking-widest whitespace-nowrap">Thành viên</th>
          <th class="px-6 py-4 text-[10px] font-extrabold uppercase tracking-widest whitespace-nowrap">Hành động</th>
          <th class="px-6 py-4 text-[10px] font-extrabold uppercase tracking-widest whitespace-nowrap">Đối tượng</th>
          <th v-if="type === 'AUDIT'" class="px-6 py-4 text-[10px] font-extrabold uppercase tracking-widest whitespace-nowrap text-center">Mức độ</th>
          <th class="px-6 py-4 text-[10px] font-extrabold uppercase tracking-widest whitespace-nowrap text-center">Kết quả</th>
          <th class="px-6 py-4 text-[10px] font-extrabold uppercase tracking-widest whitespace-nowrap text-center">Chi tiết</th>
        </tr>
      </thead>
      <tbody class="divide-y divide-slate-100 dark:divide-slate-800">
        <ActivityLogTableRow
          v-for="log in logs"
          :key="log.id"
          :log="log"
          :type="type"
          @view="$emit('view', log.id)"
        />
        <tr v-if="logs.length === 0" class="h-64">
          <td colspan="8" class="text-center py-20">
            <div class="flex flex-col items-center gap-3 opacity-20">
              <span class="material-symbols-outlined text-6xl">database_off</span>
              <p class="font-extrabold text-xl">Không tìm thấy dữ liệu nhật ký</p>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import ActivityLogTableRow from './ActivityLogTableRow.vue'

defineProps<{ 
  logs: any[],
  type: 'AUDIT' | 'BUSINESS'
}>()

defineEmits<{ view: [id: number] }>()
</script>