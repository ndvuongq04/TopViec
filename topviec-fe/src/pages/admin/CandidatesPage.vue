<template>
  <div class="space-y-6">

    <!-- Page Header -->
    <div class="flex justify-between items-end">
      <div>
        <h2 class="text-3xl font-black text-slate-900 dark:text-slate-100 tracking-tight">Quản lý Ứng Viên</h2>
        <p class="text-slate-500 text-sm mt-1">Xem và quản lý danh sách ứng viên trên hệ thống</p>
      </div>
      <button
        class="bg-[#963131] hover:bg-[#963131]/90 text-white px-5 py-2.5 rounded-lg flex items-center gap-2 font-semibold text-sm transition-all shadow-sm cursor-pointer"
        style="box-shadow: 0 4px 14px rgba(150,49,49,0.25)"
        @click="onAddNew"
      >
        <span class="material-symbols-outlined text-lg">add</span>
        Thêm ứng viên mới
      </button>
    </div>

    <!-- Tabs + Search -->
    <CandidateStatusTabs
      v-model:active-tab="filters.statusTab"
      v-model:search="filters.search"
      :candidates="store.candidates"
    />

    <!-- Table -->
    <CandidateTable
      :candidates="store.candidates"
      :total="store.meta.totals"
      :current-page="filters.page"
      :page-size="filters.size"
      :loading="store.loading"
      @view="onView"
      @delete="onDelete"
      @page-change="onPageChange"
    />

  </div>
</template>

<script setup lang="ts">
import { onMounted, watch, reactive } from 'vue'
import { useRouter } from 'vue-router'
import CandidateStatusTabs from '@/components/admin/candidates/CandidateStatusTabs.vue'
import CandidateTable from '@/components/admin/candidates/CandidateTable.vue'
import { useToast } from '@/composables/useToast'
import { useAdminCandidateStore } from '@/stores/adminCandidate.store'

const toast = useToast()
const router = useRouter()
const store = useAdminCandidateStore()

// ─── State ───────────────────────────────────────────────────────────────────────
const filters = reactive({
  statusTab: '',
  search:    '',
  page: 0,
  size: 10
})

onMounted(() => {
  loadCandidates()
})

async function loadCandidates() {
  await store.fetchCandidates({
    status: filters.statusTab || undefined,
    keyword: filters.search || undefined,
    page: filters.page,
    size: filters.size
  })
}

// ─── Watchers ────────────────────────────────────────────────────────────────────
watch(() => filters.statusTab, () => {
  filters.page = 0 // Reset to first page on filter change
  loadCandidates()
})

watch(() => filters.search, () => {
  filters.page = 0
  loadCandidates()
})

// ─── Event handlers ──────────────────────────────────────────────────────────────
function onAddNew() {
  toast.info('Tính năng', 'Thêm ứng viên mới đang được phát triển.')
}

function onView(candidate: any) {
  router.push({ name: 'admin-candidate-detail', params: { id: candidate.id } })
}

function onDelete(candidate: any) {
  if (confirm(`Bạn có chắc chắn muốn xóa ứng viên ${candidate.fullName}?`)) {
    toast.info('Thông báo', 'Chức năng xóa sẽ được cập nhật khi có API xóa ứng viên.')
  }
}

function onPageChange(page: number) {
  filters.page = page
  loadCandidates()
}
</script>
