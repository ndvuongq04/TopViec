<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import BannerSelect from './BannerSelect.vue'
import { WorkType } from '@/constants/jobPosting.constants'

const router = useRouter()
const route = useRoute()

// ── Khởi tạo từ URL ──────────────────────────────────────────────────────────
const keyword  = ref((route.query.keyword  as string) || '')
const location = ref((route.query.location as string) || '')
const workType = ref((route.query.workType as string) || '')
const experience = ref((route.query.experience as string) || '')

// ── Options ──────────────────────────────────────────────────────────────────
const locations = [
  { id: 'hcm',    name: 'TP. Hồ Chí Minh' },
  { id: 'hn',     name: 'Hà Nội' },
  { id: 'dn',     name: 'Đà Nẵng' },
  { id: 'remote', name: 'Làm việc từ xa' },
  { id: 'other',  name: 'Tỉnh thành khác' },
]

const WORK_TYPE_OPTIONS = [
  { id: WorkType.FULL_TIME, name: 'Toàn thời gian' },
  { id: WorkType.PART_TIME, name: 'Bán thời gian' },
  { id: WorkType.INTERN,    name: 'Thực tập' },
  { id: WorkType.REMOTE,    name: 'Remote' },
]

const EXPERIENCE_OPTIONS = [
  { id: '0',   name: 'Chưa có kinh nghiệm' },
  { id: '1',   name: 'Dưới 1 năm' },
  { id: '1-3', name: '1 - 3 năm' },
  { id: '3-5', name: '3 - 5 năm' },
  { id: '5',   name: 'Trên 5 năm' },
]

// ── Query builder ─────────────────────────────────────────────────────────────
function buildQuery() {
  return {
    keyword:    keyword.value    || undefined,
    location:   location.value   || undefined,
    workType:   workType.value   || undefined,
    experience: experience.value || undefined,
  }
}

// ── Search (từ home → navigate; từ search page → replace) ────────────────────
function handleSearch() {
  router.push({ name: 'JobSearch', query: buildQuery() })
}

// Khi đang ở search page, pill thay đổi → replace URL ngay (không push history)
function updateInPlace() {
  if (route.name === 'JobSearch') {
    router.replace({
      name: 'JobSearch',
      query: { ...route.query, ...buildQuery() },
    })
  }
}

watch(workType,   updateInPlace)
watch(experience, updateInPlace)

// Khi navigate về trang search từ nơi khác → đồng bộ lại local state
watch(() => route.query, (q) => {
  keyword.value    = (q.keyword    as string) || ''
  location.value   = (q.location   as string) || ''
  workType.value   = (q.workType   as string) || ''
  experience.value = (q.experience as string) || ''
}, { deep: true })

// ── Clear ─────────────────────────────────────────────────────────────────────
const hasActiveFilters = () => workType.value || experience.value

function clearFilters() {
  workType.value   = ''
  experience.value = ''
}
</script>

<template>
  <div class="space-y-4">
    <div class="bg-white p-2 rounded-xl shadow-lg flex flex-col lg:flex-row items-stretch lg:items-center gap-1">
      <!-- Keyword -->
      <div class="flex-1 flex items-center px-3 gap-2 py-1 lg:border-r border-slate-100">
        <span class="material-symbols-outlined text-gray-400">search</span>
        <input
          v-model="keyword"
          class="w-full border-none focus:ring-0 text-gray-800 placeholder-gray-400 text-base py-2"
          placeholder="Tên công việc, vị trí hoặc công ty..."
          type="text"
          @keydown.enter="handleSearch"
        />
      </div>

      <!-- Location -->
      <div class="flex-1 lg:max-w-55 flex items-center px-3 gap-1 py-1 lg:border-r border-slate-100">
        <span class="material-symbols-outlined text-gray-400 text-[20px] shrink-0">location_on</span>
        <BannerSelect
          v-model="location"
          :options="locations"
          placeholder="Địa điểm"
          variant="bar"
          class="flex-1"
        />
      </div>

      <!-- Work type -->
      <div class="flex-1 lg:max-w-50 flex items-center px-3 gap-1 py-1">
        <span class="material-symbols-outlined text-gray-400 text-[20px] shrink-0">work</span>
        <BannerSelect
          v-model="workType"
          :options="WORK_TYPE_OPTIONS"
          placeholder="Loại hình"
          variant="bar"
          class="flex-1"
        />
      </div>

      <!-- Search button -->
      <button
        class="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-8 py-3 rounded-lg transition-all transform active:scale-95 shadow-md uppercase text-base tracking-wide cursor-pointer"
        @click="handleSearch"
      >
        Tìm việc
      </button>
    </div>

    <!-- Filter pills -->
    <div class="flex flex-wrap items-center gap-2">
      <!-- Experience pill -->
      <BannerSelect
        v-model="experience"
        :options="EXPERIENCE_OPTIONS"
        placeholder="Kinh nghiệm"
      />

      <!-- Clear -->
      <Transition
        enter-active-class="transition duration-200"
        enter-from-class="opacity-0 scale-90"
        enter-to-class="opacity-100 scale-100"
        leave-active-class="transition duration-150"
        leave-from-class="opacity-100 scale-100"
        leave-to-class="opacity-0 scale-90"
      >
        <button
          v-if="hasActiveFilters()"
          class="flex items-center gap-1.5 px-3 py-2 rounded-full border border-red-300/40 bg-red-500/15 hover:bg-red-500/25 text-red-100 text-sm font-medium transition-all active:scale-95 cursor-pointer"
          @click="clearFilters"
        >
          <span class="material-symbols-outlined text-[14px]">close</span>
          Xóa lọc
        </button>
        <button
          v-else
          class="px-4 py-2 rounded-full border border-white/20 bg-white/10 hover:bg-white/20 text-sm font-medium transition-all active:scale-95 text-white/80 cursor-pointer"
          @click="clearFilters"
        >
          Xóa lọc
        </button>
      </Transition>
    </div>
  </div>
</template>
