<template>
  <tr class="hover:bg-slate-50/80 dark:hover:bg-slate-800/50 transition-all group">
    <!-- Thumbnail -->
    <td class="px-6 py-4">
      <div class="w-12 h-16 bg-slate-100 dark:bg-slate-800 rounded-lg overflow-hidden border border-slate-200 dark:border-slate-700 shadow-sm flex items-center justify-center shrink-0">
        <img v-if="template.thumbnail" :src="template.thumbnail" alt="CV Thumbnail" class="w-full h-full object-cover" />
        <span v-else class="material-symbols-outlined text-slate-300">draft</span>
      </div>
    </td>

    <!-- Info -->
    <td class="px-6 py-4">
      <div class="flex flex-col">
        <span class="text-[1.125rem] font-bold text-slate-900 dark:text-slate-100 leading-tight">{{ template.name }}</span>
        <span class="text-[0.75rem] font-mono text-slate-400 mt-1">{{ template.code }}</span>
        <span v-if="template.versionTag" class="mt-2 inline-flex w-fit rounded-full bg-slate-100 px-2.5 py-1 font-mono text-[0.7rem] font-bold text-slate-600">
          {{ template.versionTag }}
        </span>
      </div>
    </td>

    <!-- Category + Features -->
    <td class="px-6 py-4">
      <div class="space-y-2">
        <p class="text-[0.875rem] text-slate-600 dark:text-slate-400 font-medium">{{ template.categories }}</p>
        <div class="flex flex-wrap gap-1.5">
          <span v-for="f in template.features" :key="f" class="px-2 py-0.5 rounded-md bg-slate-100 dark:bg-slate-800 text-[0.75rem] font-bold text-slate-500 uppercase tracking-wider border border-slate-200 dark:border-slate-700">
            {{ f }}
          </span>
        </div>
      </div>
    </td>

    <!-- Tier -->
    <td class="px-6 py-4">
      <span :class="[
        'px-3 py-1 rounded-full text-[0.75rem] font-black uppercase tracking-widest border shadow-sm',
        tierStyles[template.tier] || tierStyles.free
      ]">
        {{ template.tierLabel }}
      </span>
    </td>

    <!-- Status -->
    <td class="px-6 py-4">
      <div class="flex items-center gap-2">
        <span class="w-2 h-2 rounded-full" :class="statusStyles[template.status]?.dot || 'bg-slate-300'"></span>
        <span class="text-[0.875rem] font-bold" :class="statusStyles[template.status]?.text || 'text-slate-500'">
          {{ template.statusLabel }}
        </span>
      </div>
    </td>

    <!-- Usage -->
    <td class="px-6 py-4 text-right">
      <span class="text-[0.875rem] font-black text-slate-700 dark:text-slate-300">
        {{ template.usageCount > 0 ? template.usageCount.toLocaleString('vi-VN') : '—' }}
      </span>
    </td>

    <!-- Actions -->
    <td class="px-6 py-4">
      <div class="flex items-center justify-center gap-1 transition-opacity">
        <button
          v-if="!template.isDraft"
          class="p-2 rounded-lg text-slate-400 hover:text-primary hover:bg-primary/5 transition-all cursor-pointer"
          title="Xem trước"
          @click="$emit('preview')"
        >
          <span class="material-symbols-outlined text-xl">visibility</span>
        </button>
        <button 
          class="p-2 rounded-lg text-slate-400 hover:text-blue-600 hover:bg-blue-50 transition-all cursor-pointer" 
          title="Chỉnh sửa" 
          @click="$emit('edit')"
        >
          <span class="material-symbols-outlined text-xl">edit</span>
        </button>
        <button
          v-if="!template.isDraft"
          class="p-2 rounded-lg text-slate-400 hover:text-red-600 hover:bg-red-50 transition-all cursor-pointer"
          title="Lưu trữ"
          @click="$emit('archive')"
        >
          <span class="material-symbols-outlined text-xl">archive</span>
        </button>
      </div>
    </td>
  </tr>
</template>

<script setup lang="ts">
defineProps<{
  template: {
    id: number; thumbnail: string; name: string; code: string
    versionTag?: string | null
    categories: string; features: string[]
    tier: string; tierLabel: string
    status: string; statusLabel: string
    usageCount: number; isDraft: boolean
  }
}>()

defineEmits<{ preview: []; edit: []; archive: [] }>()

const tierStyles: Record<string, string> = {
  premium: 'bg-amber-50 text-amber-700 border-amber-200/50',
  free: 'bg-slate-50 text-slate-600 border-slate-200',
  pro: 'bg-indigo-50 text-indigo-700 border-indigo-200/50'
}

const statusStyles: Record<string, { dot: string, text: string }> = {
  active: { dot: 'bg-emerald-500', text: 'text-emerald-600' },
  draft: { dot: 'bg-orange-500', text: 'text-orange-600' },
  archived: { dot: 'bg-slate-400', text: 'text-slate-500' }
}
</script>
