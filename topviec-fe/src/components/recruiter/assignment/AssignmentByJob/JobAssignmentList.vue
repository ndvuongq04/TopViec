<template>
  <div>
    <div v-if="loading" class="flex items-center justify-center py-8">
      <span class="material-symbols-outlined animate-spin text-4xl text-slate-300">progress_activity</span>
    </div>
    <ul v-else-if="jobs.length > 0" class="job-list">
      <li v-for="job in jobs" :key="job.id">
        <JobAssignmentItem :job="job" :active="job.id === activeId" @click="$emit('select', job.id)" />
      </li>
    </ul>
    <div v-else class="flex flex-col items-center justify-center py-10 text-slate-400">
      <span class="material-symbols-outlined text-4xl mb-2">work_off</span>
      <p class="text-sm">Chưa có tin tuyển dụng nào</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import JobAssignmentItem from './JobAssignmentItem.vue'
defineProps<{ jobs: any[]; activeId: number | null; loading?: boolean }>()
defineEmits(['select'])
</script>

<style scoped>
.job-list { display: flex; flex-direction: column; gap: 4px; list-style: none; padding: 0; margin: 0; }
.job-detail__manager-avatar-wrap {
  width: 48px; height: 48px; border-radius: 50%;
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  display: flex; align-items: center; justify-content: center;
  color: #fff; font-size: 1.125rem; font-weight: 800;
  flex-shrink: 0; border: 2px solid #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
}
.job-detail__manager-meta { font-size: 0.75rem; color: #94a3b8; margin-top: 2px; }
</style>