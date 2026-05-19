<template>
  <div class="tab-card">
    <table class="data-table">
      <thead>
        <tr>
          <th>Tên CV</th>
          <th>Ngày cập nhật</th>
          <th class="text-right">Thao tác</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="cv in cvs" :key="cv.id" class="data-row">
          <td>
            <div class="cv-name-cell">
              <span class="material-symbols-outlined cv-icon">picture_as_pdf</span>
              <span class="truncate max-w-[300px]" :title="cv.title">{{ cv.title }}</span>
              <span v-if="cv.isDefault" class="primary-badge">Chính</span>
            </div>
          </td>
          <td class="secondary-text text-[12px] whitespace-nowrap">{{ formatDate(cv.updatedAt) }}</td>
          <td class="text-right">
            <a 
              v-if="cv.pdfUrl || cv.fileUrl"
              :href="cv.pdfUrl || cv.fileUrl" 
              target="_blank" 
              class="action-link"
            >
              <span class="material-symbols-outlined" style="font-size:14px;">open_in_new</span>
              Xem
            </a>
            <span v-else class="text-slate-400 italic">Trống</span>
          </td>
        </tr>
        <tr v-if="cvs.length === 0">
          <td colspan="3" class="px-6 py-10 text-center text-slate-400">
            Ứng viên chưa tải lên CV nào
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import type { ResCv } from '@/types/cvs.types'

defineProps<{
  cvs: ResCv[]
}>()

const formatDate = (iso: string) => {
  if (!iso) return '—'
  const d = new Date(iso)
  return `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
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
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #574240;
}
.data-row { border-top: 1px solid #e4e2dc; transition: background 0.15s; }
.data-row:hover { background: #f6f4ec; }
td { padding: 16px 24px; font-weight: 500; color: #1b1c18; }
.secondary-text { color: #574240; }
.text-right { text-align: right; }

.cv-name-cell { display: flex; align-items: center; gap: 10px; }
.cv-icon { font-size: 20px; color: #5f5e5b; }
.primary-badge {
  padding: 2px 8px;
  background: #ffdad7;
  color: #842325;
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
  border-radius: 99px;
}
.action-link {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  background: none; border: none;
  color: #76191d; font-size: 13px; font-weight: 600;
  cursor: pointer; font-family: inherit;
  transition: color 0.15s;
}
.action-link:hover { color: #963131; }
</style>