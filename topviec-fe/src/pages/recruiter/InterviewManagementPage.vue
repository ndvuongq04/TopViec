<template>
  <div class="interview-management-page">
    <InterviewPageHeader @create="handleCreate" />
    <InterviewStatsCards />

    <!-- Listing Panel -->
    <div class="listing-panel">

      <!-- Toolbar: Filter tabs + Search -->
      <div class="toolbar">
        <div class="filter-tabs">
          <button
            v-for="tab in tabs"
            :key="tab.value"
            class="filter-tab"
            :class="{ 'filter-tab--active': activeFilter === tab.value }"
            @click="handleFilterChange(tab.value)"
          >{{ tab.label }}</button>
        </div>

        <div class="search-wrap">
          <span class="search-icon material-symbols-outlined">search</span>
          <input
            class="search-input"
            type="text"
            placeholder="Tìm kiếm tin phỏng vấn..."
            v-model="searchInput"
            @keyup.enter="handleSearch"
          />
          <button v-if="searchInput" class="search-clear" @click="clearSearch">
            <span class="material-symbols-outlined">close</span>
          </button>
        </div>
      </div>

      <!-- Table -->
      <InterviewJobList
        :status-filter="activeFilter"
        :keyword="appliedKeyword"
        :current-page="currentPage"
        @update:total="totalJobs = $event"
      />

      <!-- Pagination -->
      <div class="pagination-bar">
        <p class="pagination-bar__info">
          Hiển thị <strong>{{ rangeStart }}–{{ rangeEnd }}</strong>
          trong số <strong>{{ totalJobs }}</strong> tin tuyển dụng
        </p>
        <div class="pagination-controls">
          <button class="page-btn" :disabled="currentPage <= 0" @click="currentPage--">
            <span class="material-symbols-outlined">chevron_left</span>
          </button>
          <button
            v-for="page in visiblePages"
            :key="page"
            class="page-btn"
            :class="{ 'page-btn--active': page === currentPage }"
            @click="currentPage = page"
          >{{ page + 1 }}</button>
          <span v-if="totalPages > 3" class="page-separator">...</span>
          <button class="page-btn" :disabled="currentPage >= totalPages - 1 || totalPages === 0" @click="currentPage++">
            <span class="material-symbols-outlined">chevron_right</span>
          </button>
        </div>
      </div>

    </div>

    <InterviewPageFooter @export="handleExport" @invite="handleInvite" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useEmployerInterviewStore } from '@/stores/employerInterview.store'

import InterviewPageHeader from '@/components/recruiter/interviews/InterviewPageHeader.vue'
import InterviewStatsCards from '@/components/recruiter/interviews/InterviewStatsCards.vue'
import InterviewJobList    from '@/components/recruiter/interviews/InterviewJobList.vue'
import InterviewPageFooter from '@/components/recruiter/interviews/InterviewPageFooter.vue'

type FilterTab = 'interviewing' | 'completed'

const tabs: { label: string; value: FilterTab }[] = [
  { label: 'Đang phỏng vấn', value: 'interviewing' },
  { label: 'Đã hoàn thành',  value: 'completed' },
]

const router = useRouter()
const store  = useEmployerInterviewStore()

// ── Filter & Search ──────────────────────────────────────
const activeFilter   = ref<FilterTab>('interviewing')
const searchInput    = ref('')
const appliedKeyword = ref('')

// ── Pagination ───────────────────────────────────────────
const currentPage = ref(0)
const totalJobs   = ref(0)
const PAGE_SIZE   = 10

const totalPages = computed(() => Math.ceil(totalJobs.value / PAGE_SIZE))

const rangeStart = computed(() =>
  totalJobs.value === 0 ? 0 : currentPage.value * PAGE_SIZE + 1
)
const rangeEnd = computed(() =>
  Math.min((currentPage.value + 1) * PAGE_SIZE, totalJobs.value)
)

const visiblePages = computed(() => {
  const pages: number[] = []
  const max = Math.min(totalPages.value, 3)
  for (let i = 0; i < max; i++) pages.push(i)
  return pages
})

// ── Handlers ─────────────────────────────────────────────
function handleFilterChange(tab: FilterTab) {
  activeFilter.value = tab
  currentPage.value  = 0
}

function handleSearch() {
  appliedKeyword.value = searchInput.value
  currentPage.value    = 0
}

function clearSearch() {
  searchInput.value    = ''
  appliedKeyword.value = ''
  currentPage.value    = 0
}

function handleCreate()  { router.push('/recruiter/create-job') }
function handleExport()  { console.log('TODO: export report') }
function handleInvite()  { router.push('/recruiter/job-applications') }

onMounted(() => {
  store.fetchStatistics()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Manrope:wght@400;500;600;700;800&display=swap');

.interview-management-page {
  --color-surface: #ffffff;
  --color-on-surface: #0f172a;
  --color-on-surface-muted: #64748b;
  --color-border: #e2e8f0;
  --color-border-light: #f1f5f9;
  --color-primary: #137fec;
  --radius-full: 9999px;
  --radius-lg: 1rem;
  --radius-md: 0.75rem;
  --radius-sm: 0.5rem;

  font-family: 'Manrope', sans-serif;

  color: #0f172a;
  min-height: 100vh;
}

/* ── Listing Panel ─────────────────────────────────────── */
.listing-panel {
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border-light);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  margin-bottom: 2rem;
}

/* ── Toolbar ───────────────────────────────────────────── */
.toolbar {
  padding: 1rem 1.25rem;
  border-bottom: 1px solid var(--color-border-light);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  flex-wrap: wrap;
}

/* Filter tabs */
.filter-tabs {
  display: flex;
  gap: 0.375rem;
  overflow-x: auto;
  flex: 1;
  min-width: 0;
}
.filter-tabs::-webkit-scrollbar { display: none; }

.filter-tab {
  padding: 0.4375rem 0.875rem;
  border-radius: var(--radius-full);
  font-size: 0.875rem;
  font-weight: 600;
  font-family: inherit;
  border: none;
  cursor: pointer;
  white-space: nowrap;
  background: #f1f5f9;
  color: #475569;
  transition: background 0.15s, color 0.15s;
  flex-shrink: 0;
}
.filter-tab:hover { background: #e2e8f0; }
.filter-tab--active {
  background: var(--color-primary);
  color: #fff;
}

/* Search */
.search-wrap {
  position: relative;
  display: flex;
  align-items: center;
  flex-shrink: 0;
  width: 15rem;
}

.search-icon {
  position: absolute;
  left: 0.75rem;
  font-size: 1.1rem !important;
  color: #94a3b8;
  pointer-events: none;
  user-select: none;
  font-family: 'Material Symbols Outlined';
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  line-height: 1;
}

.search-input {
  width: 100%;
  height: 2.375rem;
  padding: 0 2.25rem 0 2.375rem;
  border-radius: var(--radius-full);
  border: 1px solid var(--color-border);
  background: #f8fafc;
  font-size: 1rem;
  font-family: inherit;
  color: var(--color-on-surface);
  outline: none;
  transition: border-color 0.15s, box-shadow 0.15s, background 0.15s;
}
.search-input::placeholder { color: #94a3b8; }
.search-input:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px color-mix(in srgb, var(--color-primary) 12%, transparent);
  background: #fff;
}

.search-clear {
  position: absolute;
  right: 0.5rem;
  width: 1.375rem;
  height: 1.375rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 50%;
  background: #e2e8f0;
  color: #64748b;
  cursor: pointer;
  padding: 0;
  transition: background 0.15s;
}
.search-clear:hover { background: #cbd5e1; }
.search-clear .material-symbols-outlined {
  font-size: 0.875rem !important;
  font-family: 'Material Symbols Outlined';
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  line-height: 1;
}

/* ── Pagination ────────────────────────────────────────── */
.pagination-bar {
  padding: 1rem 1.5rem;
  background: rgba(248, 250, 252, 0.3);
  border-top: 1px solid var(--color-border-light);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.pagination-bar__info {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-on-surface-muted);
  margin: 0;
}
.pagination-bar__info strong {
  font-weight: 700;
  color: var(--color-on-surface);
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.page-btn {
  width: 2rem;
  height: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-sm);
  border: none;
  background: none;
  font-size: 0.875rem;
  font-weight: 700;
  font-family: inherit;
  color: var(--color-on-surface);
  cursor: pointer;
  transition: background 0.15s;
}
.page-btn .material-symbols-outlined {
  font-size: 1.25rem !important;
  font-family: 'Material Symbols Outlined';
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  line-height: 1;
}
.page-btn:hover           { background: #f1f5f9; }
.page-btn--active         { background: var(--color-primary); color: #fff; }
.page-btn--active:hover   { background: var(--color-primary); }
.page-btn:disabled        { opacity: 0.3; cursor: default; }
.page-btn:disabled:hover  { background: none; }

.page-separator {
  color: #cbd5e1;
  margin: 0 0.25rem;
  font-size: 0.875rem;
}

/* ── Responsive ────────────────────────────────────────── */
@media (max-width: 640px) {
  .toolbar { flex-direction: column; align-items: stretch; }
  .search-wrap { width: 100%; }
}
</style>
