<template>
  <div class="bg-white dark:bg-slate-800 p-6 rounded-2xl shadow-sm border border-slate-100 dark:border-slate-700/50">
    <div class="flex items-center justify-between mb-4">
      <h4 class="font-bold text-lg">Ứng viên mới</h4>
      <router-link to="/recruiter/candidates" class="text-primary text-sm font-bold hover:underline">
        Xem tất cả
      </router-link>
    </div>

    <div class="space-y-2">
      <div
        v-for="candidate in candidates"
        :key="candidate.id"
        class="flex items-center gap-3 p-3 rounded-xl hover:bg-slate-50 dark:hover:bg-slate-700/50 transition-colors cursor-pointer border border-transparent hover:border-slate-100 dark:hover:border-slate-600"
        @click="$emit('select', candidate)"
      >
        <!-- Avatar -->
        <div
          class="size-10 rounded-full bg-cover bg-center bg-slate-200 shrink-0"
          :style="candidate.avatarUrl ? `background-image: url('${candidate.avatarUrl}')` : ''"
        >
          <div v-if="!candidate.avatarUrl" class="w-full h-full flex items-center justify-center">
            <span class="material-symbols-outlined text-slate-400 text-lg">person</span>
          </div>
        </div>

        <!-- Info -->
        <div class="flex-1 min-w-0">
          <p class="text-sm font-bold truncate">{{ candidate.name }}</p>
          <p class="text-xs text-slate-500 truncate">{{ candidate.jobTitle }}</p>
        </div>

        <!-- Badge -->
        <span v-if="candidate.isNew" class="text-[10px] bg-primary/10 text-primary px-2 py-1 rounded-full font-bold shrink-0">
          New
        </span>
        <span v-else class="text-[10px] text-slate-400 font-medium shrink-0">
          {{ candidate.timeAgo }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
export interface Candidate {
  id: number
  name: string
  jobTitle: string
  avatarUrl?: string
  isNew?: boolean
  timeAgo?: string
}

withDefaults(defineProps<{
  candidates?: Candidate[]
}>(), {
  candidates: () => [
    { id: 1, name: 'Trần Văn Nam',    jobTitle: 'UI/UX Designer',       isNew: true },
    { id: 2, name: 'Lê Thị Mai',      jobTitle: 'Marketing Specialist',  isNew: false, timeAgo: '2h ago' },
    { id: 3, name: 'Nguyễn Quốc Huy', jobTitle: 'Frontend Developer',    isNew: false, timeAgo: '5h ago' },
    { id: 4, name: 'Phạm Minh Anh',   jobTitle: 'Project Manager',       isNew: false, timeAgo: '1d ago' },
  ],
})

defineEmits<{
  select: [candidate: Candidate]
}>()
</script>