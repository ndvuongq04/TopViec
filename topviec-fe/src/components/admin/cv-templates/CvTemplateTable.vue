<template>
  <div class="overflow-x-auto">
    <table class="w-full border-collapse">
      <thead>
        <tr class="bg-slate-50/50 dark:bg-slate-800/50 border-b border-slate-200 dark:border-slate-800">
          <th class="px-6 py-4 text-left text-xs font-bold text-slate-500 uppercase tracking-wider">Mẫu CV</th>
          <th class="px-6 py-4 text-left text-xs font-bold text-slate-500 uppercase tracking-wider">Thông tin chi tiết</th>
          <th class="px-6 py-4 text-left text-xs font-bold text-slate-500 uppercase tracking-wider">Danh mục & Tính năng</th>
          <th class="px-6 py-4 text-left text-xs font-bold text-slate-500 uppercase tracking-wider">Phân hạng</th>
          <th class="px-6 py-4 text-left text-xs font-bold text-slate-500 uppercase tracking-wider">Trạng thái</th>
          <th class="px-6 py-4 text-right text-xs font-bold text-slate-500 uppercase tracking-wider">Lượt sử dụng</th>
          <th class="px-6 py-4 text-center text-xs font-bold text-slate-500 uppercase tracking-wider">Thao tác</th>
        </tr>
      </thead>
      <tbody class="divide-y divide-slate-200 dark:divide-slate-800">
        <CvTemplateTableRow
          v-for="template in templates"
          :key="template.id"
          :template="template"
          @preview="$emit('preview', template.id)"
          @edit="$emit('edit', template.id)"
          @archive="$emit('archive', template.id)"
        />
        <tr v-if="templates.length === 0">
          <td colspan="7" class="px-6 py-20 text-center">
            <div class="flex flex-col items-center gap-2 text-slate-400">
              <span class="material-symbols-outlined text-5xl">folder_off</span>
              <p class="text-base font-medium">Không tìm thấy mẫu CV nào</p>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import CvTemplateTableRow from './CvTemplateTableRow.vue'

defineProps<{ templates: any[] }>()
defineEmits<{
  preview: [id: number]
  edit: [id: number]
  archive: [id: number]
}>()
</script>