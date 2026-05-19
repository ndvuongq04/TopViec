<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import JobSearchBar from '@/components/candidate/job/JobSearchBar.vue'
import AdvancedFilter from '@/components/candidate/job/AdvancedFilter.vue'
import type { SearchFilters } from '@/components/candidate/job/AdvancedFilter.vue'
import JobCard from '@/components/candidate/job/JobCard.vue'
import { publicJobPostingService } from '@/services/jobPosting.service'
import { useSavedJobStore } from '@/stores/savedJob.store'
import { useAuthStore } from '@/stores/auth.store'
import { useToast } from '@/composables/useToast'
import type { ResJobPostingSummary, ResJobPostingDetail, PaginationMeta } from '@/types/jobPosting.types'
import { formatSalary, formatWorkType } from '@/types/jobPosting.types'

const route = useRoute()
const router = useRouter()
const savedJobStore = useSavedJobStore()
const authStore = useAuthStore()
const toast = useToast()

// ─── State ──────────────────────────────────────────────────────────────────

const jobs = ref<ResJobPostingSummary[]>([])
const loading = ref(false)
const meta = ref<PaginationMeta>({ page: 0, pageSize: 10, pages: 0, totals: 0 })
const currentPage = ref(0)
const sort = ref('publishedAt,desc')
const showMobileFilter = ref(false)

// ─── Đọc filter từ URL ───────────────────────────────────────────────────────
const keyword = computed(() => (route.query.keyword as string) || '')

/** Map experience key → { experienceYearsMin, experienceYearsMax } */
function parseExperience(exp: string | undefined): { experienceYearsMin?: number; experienceYearsMax?: number } {
  switch (exp) {
    case '0':   return { experienceYearsMin: 0, experienceYearsMax: 1 }
    case '1':   return { experienceYearsMin: 0, experienceYearsMax: 1 }
    case '1-3': return { experienceYearsMin: 1, experienceYearsMax: 3 }
    case '3-5': return { experienceYearsMin: 3, experienceYearsMax: 5 }
    case '5':   return { experienceYearsMin: 5 }
    default:    return {}
  }
}

/** Initial values cho AdvancedFilter từ URL (khi reload hoặc link trực tiếp) */
const initialSidebarFilters = computed(() => ({
  initialIndustryId: route.query.industryId ? Number(route.query.industryId) : undefined,
  initialSalaryMin:  route.query.salaryMin  ? Number(route.query.salaryMin)  : undefined,
  initialSalaryMax:  route.query.salaryMax  ? Number(route.query.salaryMax)  : undefined,
  initialFeatured:   route.query.isFeatured === 'true',
  initialUrgent:     route.query.isUrgent   === 'true',
}))

// ─── Quick View ──────────────────────────────────────────────────────────────

const quickViewJobId = ref<number | null>(null)
const quickViewDetail = ref<ResJobPostingDetail | null>(null)
const quickViewLoading = ref(false)

const isSavedQuickView = computed(() =>
  quickViewJobId.value ? savedJobStore.isSavedMap[quickViewJobId.value] || false : false
)

const formatQuickViewSalary = computed(() =>
  quickViewDetail.value ? formatSalary(quickViewDetail.value) : ''
)

const formatQuickViewLocation = computed(() => {
  const locs = quickViewDetail.value?.locations
  if (!locs?.length) return 'Việt Nam'
  return [...new Set(locs.map(l => l.isRemote ? 'Remote' : l.name))].join(', ')
})

async function openQuickView(id: number) {
  quickViewJobId.value = id
  quickViewLoading.value = true
  quickViewDetail.value = null
  try {
    quickViewDetail.value = await publicJobPostingService.getById(id)
  } catch {
    toast.error('Không thể tải chi tiết việc làm')
    quickViewJobId.value = null
  } finally {
    quickViewLoading.value = false
  }
}

function closeQuickView() {
  quickViewJobId.value = null
  quickViewDetail.value = null
}

const SORT_OPTIONS = [
  { label: 'Mới nhất', value: 'publishedAt,desc' },
  { label: 'Mức lương cao nhất', value: 'salaryMax,desc' },
  { label: 'Lượt xem nhiều nhất', value: 'viewCount,desc' },
]


// ─── Fetch ──────────────────────────────────────────────────────────────────

async function fetchJobs() {
  loading.value = true
  try {
    const q = route.query
    const params = {
      keyword:     (q.keyword  as string) || undefined,
      workType:    (q.workType as string) || undefined,
      ...parseExperience(q.experience as string),
      industryId:  q.industryId ? Number(q.industryId) : undefined,
      salaryMin:   q.salaryMin  ? Number(q.salaryMin)  : undefined,
      salaryMax:   q.salaryMax  ? Number(q.salaryMax)  : undefined,
      isFeatured:  q.isFeatured === 'true' ? true : undefined,
      isUrgent:    q.isUrgent   === 'true' ? true : undefined,
      page: currentPage.value,
      size: 10,
      sort: sort.value || undefined,
    }
    const result = await publicJobPostingService.getList(params)
    jobs.value = result.result
    meta.value = result.meta

    if (authStore.isAuthenticated) {
      result.result.forEach(job => savedJobStore.checkIsSaved(job.id))
    }
  } catch {
    toast.error('Không thể tải danh sách việc làm. Vui lòng thử lại.')
  } finally {
    loading.value = false
  }
}

// ─── Handlers ───────────────────────────────────────────────────────────────

function onFilterChange(filters: SearchFilters) {
  sidebarFilters.value = filters
  // Đẩy sidebar params lên URL → watcher sẽ tự gọi fetchJobs
  router.replace({
    query: {
      ...route.query,
      industryId: filters.industryId?.toString()      || undefined,
      salaryMin:  filters.salaryMin?.toString()       || undefined,
      salaryMax:  filters.salaryMax?.toString()       || undefined,
      isFeatured: filters.isFeatured ? 'true'         : undefined,
      isUrgent:   filters.isUrgent   ? 'true'         : undefined,
    },
  })
}

function onSortChange() {
  currentPage.value = 0
  fetchJobs()
}

function goToPage(page: number) {
  currentPage.value = page
  fetchJobs()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

async function handleBookmark(id: number) {
  if (!authStore.isAuthenticated) {
    toast.info('Vui lòng đăng nhập để lưu việc làm')
    return
  }
  try {
    if (savedJobStore.isSavedMap[id]) {
      await savedJobStore.unsave(id)
      toast.info('Đã bỏ lưu việc làm')
    } else {
      await savedJobStore.toggleSave(id)
      toast.success('Đã lưu việc làm thành công')
    }
  } catch {
    toast.error('Không thể thực hiện thao tác')
  }
}

// ─── Mapping ────────────────────────────────────────────────────────────────

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  const diffDays = Math.floor((Date.now() - new Date(dateStr).getTime()) / 86_400_000)
  if (diffDays === 0) return 'Hôm nay'
  if (diffDays === 1) return 'Hôm qua'
  return `${diffDays} ngày trước`
}

function mapToCardProps(job: ResJobPostingSummary) {
  const locationNames = job.locations?.length
    ? [...new Set(job.locations.map(l => l.isRemote ? 'Remote' : l.name))].join(', ')
    : 'Việt Nam'
  return {
    id: job.id,
    title: job.title,
    company: job.company.name,
    isBrandVerified: job.company.isBrandVerified,
    logoUrl: job.company.logoUrl || '/default-company.png',
    logoBg: 'bg-blue-50',
    logoBorder: 'border-blue-100',
    tags: [formatWorkType(job.workType), job.level.name],
    salaryMin: formatSalary(job),
    salaryMax: '',
    location: locationNames,
    postedAt: formatDate(job.publishedAt || job.createdAt),
    isHot: job.isHot,
    isUrgent: job.isUrgent,
    isSaved: savedJobStore.isSavedMap[job.id] || false,
  }
}

// ─── Watchers ───────────────────────────────────────────────────────────────

watch(() => route.query, () => {
  currentPage.value = 0
  fetchJobs()
}, { deep: true })

watch(sort, onSortChange)

onMounted(fetchJobs)
</script>

<template>
  <div class="flex flex-col flex-1 min-w-0">

    <!-- Search bar -->
    <div class="sticky top-0 z-45 bg-linear-to-r from-blue-400 via-blue-500 to-blue-600 py-5 shadow-sm">
      <!-- Dot pattern -->
      <div class="absolute inset-0 overflow-hidden pointer-events-none">
        <div
          class="absolute inset-0 opacity-10"
          style="background-image: radial-gradient(#ffffff 1px, transparent 1px); background-size: 24px 24px;"
        />
        <div class="absolute right-0 top-0 h-full w-1/3 opacity-20 bg-linear-to-l from-white to-transparent transform skew-x-12 translate-x-12" />
      </div>
      <div class="relative z-10 w-full max-w-360 mx-auto px-4 md:px-10">
        <JobSearchBar />
      </div>
    </div>

    <!-- Content -->
    <div class="max-w-360 mx-auto w-full px-4 md:px-10 py-6 flex gap-5 items-start min-h-screen">

      <!-- Sidebar (desktop) -->
      <aside v-if="!quickViewJobId" class="w-65 min-w-55 shrink-0 hidden lg:block self-stretch">
        <div class="sticky top-42.5 max-h-[calc(100vh-190px)] overflow-y-auto custom-scrollbar pr-1">
          <AdvancedFilter v-bind="initialSidebarFilters" @change="onFilterChange" />
        </div>
      </aside>

      <!-- Mobile filter overlay -->
      <Transition
        enter-active-class="transition duration-200"
        enter-from-class="opacity-0"
        enter-to-class="opacity-100"
        leave-active-class="transition duration-150"
        leave-from-class="opacity-100"
        leave-to-class="opacity-0"
      >
        <div
          v-if="showMobileFilter"
          class="fixed inset-0 z-40 bg-black/40 lg:hidden"
          @click="showMobileFilter = false"
        />
      </Transition>
      <Transition
        enter-active-class="transition duration-300"
        enter-from-class="-translate-x-full"
        enter-to-class="translate-x-0"
        leave-active-class="transition duration-200"
        leave-from-class="translate-x-0"
        leave-to-class="-translate-x-full"
      >
        <div
          v-if="showMobileFilter"
          class="fixed left-0 top-0 bottom-0 z-50 w-72 overflow-y-auto p-4 bg-background-light dark:bg-background-dark lg:hidden"
        >
          <div class="flex justify-between items-center mb-4 border-b border-slate-100 pb-3">
            <span class="text-lg font-extrabold text-text-main dark:text-white">Lọc nâng cao</span>
            <button type="button" @click="showMobileFilter = false" class="p-1 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-700">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
          <AdvancedFilter v-bind="initialSidebarFilters" @change="(f) => { onFilterChange(f); showMobileFilter = false }" />
        </div>
      </Transition>

      <!-- Main content -->
      <div class="flex-1 min-w-0 flex gap-4 items-start">

        <!-- Left col: job list -->
        <main
          class="flex flex-col gap-3 min-w-0 transition-all duration-300"
          :class="quickViewJobId ? 'w-[46%] shrink-0' : 'flex-1'"
        >

          <!-- Top controls: kết quả + pills + sort -->
          <div class="bg-background-light dark:bg-background-dark rounded-2xl px-4 py-3 flex items-center justify-between flex-wrap gap-3">

            <!-- Trái: đếm kết quả + mobile filter btn + pills -->
            <div class="flex items-center gap-2.5 flex-wrap">

              <!-- Số kết quả -->
              <p v-if="!loading" class="text-sm text-text-muted whitespace-nowrap">
                Tìm thấy <span class="font-bold text-text-main dark:text-white">{{ meta.totals }}</span> việc làm
              </p>

              <!-- Mobile: nút mở filter -->
              <button
                type="button"
                class="lg:hidden flex items-center gap-1.5 px-3 py-1.5 rounded-xl border border-slate-200 dark:border-slate-600 bg-white dark:bg-surface-dark text-sm font-bold text-text-main dark:text-white hover:border-primary transition-colors cursor-pointer"
                @click="showMobileFilter = !showMobileFilter"
              >
                <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                  <polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3" />
                </svg>
                Lọc
              </button>


            </div>

            <!-- Sort -->
            <div v-if="!quickViewJobId" class="flex items-center gap-2.5">
              <span class="text-xs font-bold text-text-muted whitespace-nowrap uppercase tracking-wider">Sắp xếp theo:</span>
              <select
                v-model="sort"
                class="px-4 py-2 border border-slate-200 dark:border-slate-600 rounded-lg text-base font-bold text-text-main dark:text-white bg-white dark:bg-surface-dark focus:border-primary focus:outline-none focus:ring-0 cursor-pointer"
              >
                <option v-for="opt in SORT_OPTIONS" :key="opt.value" :value="opt.value">
                  {{ opt.label }}
                </option>
              </select>
            </div>
          </div>

          <!-- Loading skeleton -->
          <template v-if="loading">
            <div
              v-for="i in 5"
              :key="i"
              class="h-40 bg-white dark:bg-surface-dark rounded-3xl border border-slate-200 dark:border-slate-700 animate-pulse"
            />
          </template>

          <!-- Empty state -->
          <div
            v-else-if="jobs.length === 0"
            class="bg-white dark:bg-surface-dark rounded-2xl border border-slate-200 dark:border-slate-700 py-16 flex flex-col items-center gap-3 text-center px-6"
          >
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="text-slate-300 dark:text-slate-600">
              <circle cx="11" cy="11" r="8" /><line x1="21" y1="21" x2="16.65" y2="16.65" />
            </svg>
            <p class="text-lg text-text-main dark:text-white font-extrabold">Không tìm thấy việc làm phù hợp</p>
            <p class="text-base text-text-muted font-medium">Hãy thử thay đổi bộ lọc hoặc từ khóa tìm kiếm</p>
          </div>

          <!-- Job list -->
          <template v-else>
            <div class="flex flex-col gap-3">
              <JobCard
                v-for="job in jobs"
                :key="job.id"
                v-bind="mapToCardProps(job)"
                :is-active="quickViewJobId === job.id"
                :show-quick-view="true"
                @bookmark="handleBookmark"
                @quick-view="openQuickView"
              />
            </div>
          </template>

          <!-- Pagination -->
          <div
            v-if="meta.pages > 1"
            class="flex items-center justify-center gap-2 pt-2"
          >
            <!-- Prev -->
            <button
              type="button"
              :disabled="currentPage === 0"
              class="flex items-center gap-1 px-4 py-2 rounded-xl border text-sm font-bold transition-all"
              :class="currentPage === 0
                ? 'border-slate-200 dark:border-slate-700 text-slate-300 dark:text-slate-600 cursor-not-allowed'
                : 'border-slate-200 dark:border-slate-600 text-text-muted hover:border-primary hover:text-primary cursor-pointer'"
              @click="goToPage(currentPage - 1)"
            >
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="15 18 9 12 15 6" /></svg>
              Trước
            </button>

            <!-- Page numbers -->
            <template v-for="p in meta.pages" :key="p">
              <button
                v-if="Math.abs(p - 1 - currentPage) <= 2"
                type="button"
                class="w-10 h-10 rounded-xl border text-sm font-bold transition-all cursor-pointer"
                :class="currentPage === p - 1
                  ? 'bg-primary border-primary text-white shadow-sm'
                  : 'border-slate-200 dark:border-slate-600 text-text-muted hover:border-primary hover:text-primary'"
                @click="goToPage(p - 1)"
              >{{ p }}</button>
            </template>

            <!-- Next -->
            <button
              type="button"
              :disabled="currentPage >= meta.pages - 1"
              class="flex items-center gap-1 px-4 py-2 rounded-xl border text-sm font-bold transition-all"
              :class="currentPage >= meta.pages - 1
                ? 'border-slate-200 dark:border-slate-700 text-slate-300 dark:text-slate-600 cursor-not-allowed'
                : 'border-slate-200 dark:border-slate-600 text-text-muted hover:border-primary hover:text-primary cursor-pointer'"
              @click="goToPage(currentPage + 1)"
            >
              Sau
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="9 18 15 12 9 6" /></svg>
            </button>
          </div>

        </main>

        <!-- Right col: quick view detail panel -->
        <Transition
          enter-active-class="transition-all duration-300"
          enter-from-class="opacity-0 translate-x-4"
          enter-to-class="opacity-100 translate-x-0"
          leave-active-class="transition-all duration-200"
          leave-from-class="opacity-100 translate-x-0"
          leave-to-class="opacity-0 translate-x-4"
        >
          <div
            v-if="quickViewJobId"
            class="flex-1 sticky top-42.5 bg-white dark:bg-surface-dark rounded-2xl border border-slate-200 dark:border-slate-700 overflow-hidden flex flex-col"
            style="max-height: calc(100vh - 190px)"
          >
            <!-- Loading state -->
            <div v-if="quickViewLoading" class="flex-1 flex items-center justify-center py-16">
              <div class="w-8 h-8 border-2 border-primary border-t-transparent rounded-full animate-spin" />
            </div>

            <template v-else-if="quickViewDetail">
              <!-- Header -->
              <div class="px-5 pt-5 pb-4 border-b border-slate-100 dark:border-slate-700 shrink-0">
                <div class="flex items-start justify-between gap-3">
                  <RouterLink
                    :to="`/jobs/${quickViewJobId}`"
                    class="text-base font-bold text-text-main dark:text-white hover:text-primary transition-colors line-clamp-2 flex-1"
                  >
                    {{ quickViewDetail.title }}
                  </RouterLink>
                  <button
                    class="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-700 text-slate-400 hover:text-slate-600 shrink-0 cursor-pointer transition-colors"
                    @click="closeQuickView"
                  >
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
                  </button>
                </div>

                <!-- Company -->
                <div class="flex items-center gap-1.5 mt-2">
                  <img
                    v-if="quickViewDetail.company.logoUrl"
                    :src="quickViewDetail.company.logoUrl"
                    :alt="quickViewDetail.company.name"
                    class="w-5 h-5 rounded object-contain"
                  />
                  <span class="text-sm text-text-muted">{{ quickViewDetail.company.name }}</span>
                  <div v-if="quickViewDetail.company.isBrandVerified" class="relative flex items-center group/verified">
                    <span class="material-symbols-outlined text-blue-600 cursor-default" style="font-size: 15px; font-variation-settings: 'FILL' 1">verified</span>
                    <span class="pointer-events-none absolute bottom-[calc(100%+6px)] left-1/2 -translate-x-1/2 bg-blue-800 text-white text-[11px] font-medium whitespace-nowrap px-2.5 py-1 rounded-md opacity-0 group-hover/verified:opacity-100 transition-opacity z-10">
                      Công ty đã xác minh
                    </span>
                  </div>
                </div>

                <!-- Info pills -->
                <div class="flex flex-wrap gap-1.5 mt-3">
                  <span class="flex items-center gap-0.5 px-2.5 py-1 rounded-full bg-green-50 dark:bg-green-900/20 text-green-700 dark:text-green-400 text-xs font-medium">
                    <span class="material-symbols-outlined text-[13px]">payments</span>
                    {{ formatQuickViewSalary }}
                  </span>
                  <span class="flex items-center gap-0.5 px-2.5 py-1 rounded-full bg-slate-100 dark:bg-slate-700 text-text-muted text-xs">
                    <span class="material-symbols-outlined text-[13px]">work</span>
                    {{ formatWorkType(quickViewDetail.workType) }}
                  </span>
                  <span class="flex items-center gap-0.5 px-2.5 py-1 rounded-full bg-slate-100 dark:bg-slate-700 text-text-muted text-xs">
                    <span class="material-symbols-outlined text-[13px]">military_tech</span>
                    {{ quickViewDetail.level.name }}
                  </span>
                  <span v-if="quickViewDetail.experienceYearsMin" class="flex items-center gap-0.5 px-2.5 py-1 rounded-full bg-slate-100 dark:bg-slate-700 text-text-muted text-xs">
                    <span class="material-symbols-outlined text-[13px]">schedule</span>
                    {{ quickViewDetail.experienceYearsMin }}+ năm kinh nghiệm
                  </span>
                  <span class="flex items-center gap-0.5 px-2.5 py-1 rounded-full bg-slate-100 dark:bg-slate-700 text-text-muted text-xs">
                    <span class="material-symbols-outlined text-[13px]">location_on</span>
                    {{ formatQuickViewLocation }}
                  </span>
                </div>

                <!-- Action buttons -->
                <div class="flex gap-2 mt-4">
                  <RouterLink
                    :to="`/jobs/${quickViewJobId}`"
                    class="flex-1 bg-primary hover:bg-primary/90 text-white text-sm font-semibold py-2.5 rounded-xl text-center transition-colors"
                  >
                    Ứng tuyển ngay
                  </RouterLink>
                  <button
                    class="w-10 h-10 border rounded-xl flex items-center justify-center transition-colors cursor-pointer shrink-0"
                    :class="isSavedQuickView
                      ? 'border-primary text-primary bg-primary/5'
                      : 'border-slate-200 dark:border-slate-600 text-slate-400 hover:border-primary hover:text-primary'"
                    @click="quickViewJobId && handleBookmark(quickViewJobId)"
                  >
                    <span
                      class="material-symbols-outlined text-base"
                      :style="isSavedQuickView ? 'font-variation-settings: \'FILL\' 1' : ''"
                    >bookmark</span>
                  </button>
                </div>
              </div>

              <!-- Body -->
              <div class="flex-1 overflow-y-auto p-5 space-y-5 text-sm">
                <div>
                  <h3 class="font-bold text-text-main dark:text-white mb-2">Mô tả công việc</h3>
                  <div class="text-text-muted dark:text-gray-400 leading-relaxed prose prose-sm max-w-none dark:prose-invert" v-html="quickViewDetail.description" />
                </div>
                <div>
                  <h3 class="font-bold text-text-main dark:text-white mb-2">Yêu cầu ứng viên</h3>
                  <div class="text-text-muted dark:text-gray-400 leading-relaxed prose prose-sm max-w-none dark:prose-invert" v-html="quickViewDetail.requirements" />
                </div>
                <div v-if="quickViewDetail.benefits">
                  <h3 class="font-bold text-text-main dark:text-white mb-2">Quyền lợi</h3>
                  <div class="text-text-muted dark:text-gray-400 leading-relaxed prose prose-sm max-w-none dark:prose-invert" v-html="quickViewDetail.benefits" />
                </div>
                <RouterLink
                  :to="`/jobs/${quickViewJobId}`"
                  class="inline-flex items-center gap-1 text-primary font-medium hover:underline"
                >
                  Xem chi tiết đầy đủ
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="9 18 15 12 9 6"/></svg>
                </RouterLink>
              </div>
            </template>
          </div>
        </Transition>

      </div>
    </div>
  </div>
</template>
