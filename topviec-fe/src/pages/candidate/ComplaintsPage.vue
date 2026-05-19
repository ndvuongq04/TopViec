<template>
  <main class="flex flex-col flex-1 gap-6 min-w-0 pb-20 max-w-360 mx-auto w-full px-4 md:px-10 py-6">
    <header class="mt-2">
      <h1 class="ccp-title">Danh sách khiếu nại đã gửi</h1>
      <p class="ccp-desc">Theo dõi trạng thái xử lý các báo cáo vi phạm của bạn</p>
    </header>

    <!-- Loading skeleton -->
    <div v-if="store.loading" class="flex flex-col gap-4">
      <div v-for="i in 4" :key="i" class="h-32 rounded-xl bg-slate-100 animate-pulse" />
    </div>

    <!-- Empty state -->
    <div v-else-if="!store.reports.length" class="flex flex-col items-center justify-center py-20 gap-3 text-center">
      <span class="material-symbols-outlined text-slate-300 text-[56px]">inbox</span>
      <p class="text-slate-500 font-medium">Bạn chưa gửi khiếu nại nào</p>
    </div>

    <!-- List -->
    <div v-else class="flex flex-col gap-4">
      <CandidateComplaintCard
        v-for="complaint in store.reports"
        :key="complaint.id"
        :complaint="complaint"
        @view="onView"
      />
    </div>

    <!-- Pagination -->
    <CandidateComplaintPagination
      v-if="store.meta.totals > 0"
      :current-page="currentPage"
      :total="store.meta.totals"
      :per-page="pageSize"
      @change="onPageChange"
    />
  </main>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useCandidateReportStore } from '@/stores/candidateReport.store'
import CandidateComplaintCard from '@/components/candidate/complaints/CandidateComplaintCard.vue'
import CandidateComplaintPagination from '@/components/candidate/complaints/CandidateComplaintPagination.vue'
import type { ResReportSummary } from '@/types/report.types'

const router      = useRouter()
const store       = useCandidateReportStore()
const currentPage = ref(0)
const pageSize    = ref(10)

async function loadPage(page: number) {
  await store.fetchMyReports({ page, size: pageSize.value, sort: 'createdAt,desc' })
}

onMounted(() => loadPage(currentPage.value))

function onPageChange(page: number) {
  currentPage.value = page
}

watch(currentPage, (page) => loadPage(page))

function onView(complaint: ResReportSummary) {
  router.push(`/my-complaints/${complaint.id}`)
}
</script>

<style scoped>
.ccp-title {
  font-size: 1.875rem; font-weight: 800; font-family: 'Manrope', sans-serif;
  color: #071b3b; letter-spacing: -0.02em; margin-bottom: 8px;
}
.ccp-desc { font-size: 1rem; color: #404752; font-family: 'Manrope', sans-serif; }
</style>