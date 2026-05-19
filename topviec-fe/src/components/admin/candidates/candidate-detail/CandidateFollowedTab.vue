<template>
  <div class="tab-card">
    <table v-if="companies.length > 0" class="data-table">
      <thead>
        <tr>
          <th>Công ty</th>
          <th>Ngày theo dõi</th>
          <th class="text-right">Thao tác</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in (companies as any[])" :key="item.id" class="data-row">
          <td>
            <div class="company-cell">
              <div class="company-logo">
                <img 
                  v-if="item.company?.logoUrl" 
                  :src="item.company.logoUrl" 
                  :alt="item.company.name"
                  class="size-full object-contain"
                />
                <div v-else class="size-full bg-slate-100 flex items-center justify-center font-bold text-slate-400">
                  {{ item.company?.name?.charAt(0).toUpperCase() || 'C' }}
                </div>
              </div>
              <div class="flex flex-col">
                <span class="font-bold text-[13px] text-slate-900">{{ item.company?.name }}</span>
                <span class="text-[11px] text-slate-500">{{ item.company?.industryName || 'Đang cập nhật' }}</span>
              </div>
            </div>
          </td>
          <td class="secondary-text text-[12px] whitespace-nowrap">{{ formatDate(item.followedAt) }}</td>
          <td class="text-right">
            <a
              v-if="item.company?.slug"
              :href="router.resolve({ name: 'CompanyDetail', params: { slug: item.company.slug } }).href"
              target="_blank"
              rel="noopener noreferrer"
              class="action-link inline-flex items-center gap-1"
            >
              <span class="material-symbols-outlined text-[14px]">open_in_new</span>
              Xem
            </a>
          </td>
        </tr>
      </tbody>
    </table>
    
    <div v-else class="text-center py-12 text-slate-400">
      <span class="material-symbols-outlined text-4xl block mb-2">corporate_fare</span>
      Ứng viên chưa theo dõi công ty nào
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import type { ResCompanyDTO } from '@/types/company.types'

const router = useRouter()

defineProps<{
  companies: ResCompanyDTO[]
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
td { padding: 16px 24px; font-weight: 500; color: #1b1c18; }
.secondary-text { color: #574240; }

.company-cell { display: flex; align-items: center; gap: 12px; }
.company-initial {
  width: 32px; height: 32px;
  border-radius: 6px;
  background: #e4e2dc;
  display: flex; align-items: center; justify-content: center;
  font-weight: 700; font-size: 13px;
  color: #574240;
  flex-shrink: 0;
}
</style>