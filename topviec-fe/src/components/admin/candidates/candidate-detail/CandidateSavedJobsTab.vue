<template>
  <div class="tab-card">
    <table v-if="jobs.length > 0" class="data-table">
      <thead>
        <tr>
          <th>Vị trí</th>
          <th>Công ty</th>
          <th>Ngày lưu</th>
          <th class="text-right">Thao tác</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="job in jobs" :key="job.id" class="data-row">
          <td class="position-text font-bold text-slate-900">{{ job.jobPosting?.title || 'Không xác định' }}</td>
          <td class="secondary-text">{{ job.jobPosting?.company?.name || 'Không xác định' }}</td>
          <td class="secondary-text text-[12px] whitespace-nowrap">{{ formatDate(job.savedAt) }}</td>
          <td class="text-right">
            <a 
              v-if="job.jobPosting?.id"
              :href="`/jobs/${job.jobPosting.id}`" 
              target="_blank" 
              class="action-link"
            >
              <span class="material-symbols-outlined" style="font-size:14px;">open_in_new</span>
              Xem
            </a>
          </td>
        </tr>
      </tbody>
    </table>
    
    <div v-else class="text-center py-12 text-slate-400">
      <span class="material-symbols-outlined text-4xl block mb-2">bookmark</span>
      Ứng viên chưa lưu tin tuyển dụng nào
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ResSavedJobDTO } from '@/types/savedJob.types'

defineProps<{
  jobs: ResSavedJobDTO[]
}>()

const formatDate = (iso: string) => {
  if (!iso) return '—'
  const d = new Date(iso)
  return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
}
</script>

<style scoped>
.tab-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px -4px rgba(87, 66, 64, 0.05);
  overflow: hidden;
}
.data-table { width: 100%; border-collapse: collapse; font-size: 13px; text-align: left; }
thead tr { background: #fbf9f2; }
th {
  padding: 14px 24px;
  font-size: 10px; font-weight: 700;
  text-transform: uppercase; letter-spacing: 0.08em;
  color: #574240;
}
.data-row { border-top: 1px solid #e4e2dc; transition: background 0.15s; }
.data-row:hover { background: #f6f4ec; }
td { padding: 16px 24px; font-weight: 500; }
.position-text { color: #1b1c18; }
.secondary-text { color: #574240; }
.action-link {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  color: #76191d; font-size: 13px; font-weight: 600;
  transition: color 0.15s;
}
.action-link:hover { color: #963131; }
</style>