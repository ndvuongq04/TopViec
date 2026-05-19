<template>
  <div class="space-y-5">
    <!-- Header: Title + Actions -->
    <div class="pt-4">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4">
        <div>
          <h2 class="text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight">Việc làm đã lưu</h2>
          <p class="text-slate-500 dark:text-gray-400 text-base mt-1">Xem và quản lý các công việc bạn đã lưu.</p>
        </div>
        <div v-if="savedJobStore.meta.totals > 0" class="flex items-center gap-2">
          <span class="inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full text-base font-bold bg-primary/10 text-primary border border-primary/20">
            <span class="material-symbols-outlined text-[18px]">bookmark</span>
            {{ savedJobStore.meta.totals }} đã lưu
          </span>
        </div>
      </div>
      <!-- Select All (only when has items) -->
      <div v-if="mappedJobs.length > 0" class="flex items-center gap-3">
        <div
          @click="toggleSelectAll"
          class="w-5 h-5 rounded-md border-2 flex items-center justify-center transition-all cursor-pointer shrink-0"
          :class="isAllSelected
            ? 'bg-primary border-primary text-white'
            : 'bg-white dark:bg-slate-800 border-slate-300 dark:border-slate-600 hover:border-primary'"
        >
          <span v-if="isAllSelected" class="material-symbols-outlined text-xs font-bold">check</span>
        </div>
        <span class="text-sm font-medium text-slate-500 dark:text-gray-400">
          {{ isAllSelected ? 'Bỏ chọn tất cả' : 'Chọn tất cả' }}
        </span>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="savedJobStore.loading && savedJobStore.savedJobs.length === 0" class="flex flex-col items-center justify-center py-20">
      <span class="material-symbols-outlined animate-spin text-primary text-4xl">progress_activity</span>
      <p class="mt-4 text-sm text-slate-400 font-medium">Đang tải danh sách...</p>
    </div>

    <!-- List -->
    <div v-else-if="mappedJobs.length > 0" class="flex flex-col gap-3">
      <JobCard
        v-for="job in mappedJobs"
        :key="job.id"
        v-bind="job"
        :selectable="true"
        :selected="selectedIds.includes(job.id)"
        @bookmark="handleUnsaveConfirm"
        @select="handleSelect"
        @quick-apply="onQuickApply"
      />
    </div>

    <!-- Empty state -->
    <div v-else class="bg-white dark:bg-surface-dark py-16 px-8 rounded-2xl border border-slate-200 dark:border-slate-800 flex flex-col items-center justify-center text-center">
      <div class="w-20 h-20 bg-slate-100 dark:bg-slate-800 rounded-2xl flex items-center justify-center text-slate-400 mb-5">
        <span class="material-symbols-outlined text-4xl">bookmark_border</span>
      </div>
      <h3 class="text-lg font-bold text-slate-900 dark:text-white mb-1.5">Chưa có việc làm nào được lưu</h3>
      <p class="text-slate-500 dark:text-gray-400 text-sm mb-6 max-w-xs">Lưu lại những công việc hấp dẫn để xem lại và ứng tuyển sau.</p>
      <router-link
        to="/"
        class="inline-flex items-center gap-2 bg-primary hover:bg-primary-hover text-white px-5 py-2.5 rounded-xl font-bold text-sm shadow-lg shadow-primary/20 transition-all active:scale-95"
      >
        <span class="material-symbols-outlined text-[18px]">search</span>
        Khám phá ngay
      </router-link>
    </div>

    <!-- Batch Action Bar -->
    <Transition name="slide-up">
      <div 
        v-if="selectedIds.length > 0"
        class="fixed bottom-6 left-1/2 -translate-x-1/2 z-50 bg-slate-900 dark:bg-white text-white dark:text-slate-900 shadow-2xl shadow-slate-900/30 rounded-2xl px-5 py-3.5 flex items-center gap-5 min-w-[300px] md:min-w-[480px]"
      >
        <div class="flex flex-col min-w-0">
          <span class="text-sm font-bold">Đã chọn {{ selectedIds.length }} việc làm</span>
          <span v-if="selectedIds.length > 10" class="text-xs text-red-400 font-medium">Tối đa 10 việc làm mỗi lần</span>
          <span v-else class="text-xs text-slate-400 dark:text-slate-500">Ứng tuyển nhanh vào các vị trí này</span>
        </div>
        <div class="flex items-center gap-2 ml-auto shrink-0">
          <button 
            @click="selectedIds = []"
            class="text-sm font-bold text-slate-400 dark:text-slate-500 hover:text-white dark:hover:text-slate-900 px-3 py-2 transition-colors cursor-pointer"
          >
            Hủy
          </button>
          <button 
            @click="handleBatchApply"
            :disabled="selectedIds.length > 10"
            class="bg-primary hover:bg-primary-hover disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer text-white px-5 py-2.5 rounded-xl font-bold text-sm transition-all flex items-center gap-2"
          >
            <span class="material-symbols-outlined text-[18px]">send</span>
            <span class="hidden sm:inline">Ứng tuyển</span>
          </button>
        </div>
      </div>
    </Transition>

    <!-- Batch Apply Selection Modal -->
    <Transition name="fade">
      <div v-if="showBatchApplyModal" class="fixed inset-0 z-[60] flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-sm">
        <div class="bg-white dark:bg-surface-dark w-full max-w-lg rounded-2xl shadow-2xl border border-slate-200 dark:border-slate-800 overflow-hidden">
          <div class="p-5 border-b border-slate-100 dark:border-slate-800 flex items-center justify-between">
            <h3 class="text-lg font-bold text-slate-900 dark:text-white">Ứng tuyển hàng loạt</h3>
            <button @click="showBatchApplyModal = false" class="p-1.5 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg transition-colors cursor-pointer">
              <span class="material-symbols-outlined text-[20px]">close</span>
            </button>
          </div>
          
          <div class="p-5">
            <div class="mb-5 p-3.5 bg-blue-50 dark:bg-blue-900/20 rounded-xl border border-blue-100 dark:border-blue-800 flex items-start gap-3">
              <span class="material-symbols-outlined text-blue-500 text-[20px] mt-0.5">info</span>
              <p class="text-sm text-blue-700 dark:text-blue-300">Bạn đang ứng tuyển vào <strong>{{ selectedIds.length }} vị trí</strong>. Chọn 1 CV chung để nộp.</p>
            </div>

            <p class="text-xs font-bold text-slate-400 uppercase tracking-wider mb-3">Chọn CV</p>
            <div class="space-y-2.5 max-h-[280px] overflow-y-auto pr-1 custom-scrollbar">
              <div 
                v-for="cv in cvsStore.cvs" 
                :key="cv.id"
                @click="selectedCvId = cv.id"
                class="p-3.5 rounded-xl border-2 transition-all cursor-pointer flex items-center gap-3.5"
                :class="selectedCvId === cv.id 
                  ? 'border-primary bg-primary/5' 
                  : 'border-slate-100 dark:border-slate-800 hover:border-slate-200 dark:hover:border-slate-700'"
              >
                <div class="w-9 h-9 rounded-lg bg-slate-100 dark:bg-slate-800 flex items-center justify-center shrink-0">
                  <span class="material-symbols-outlined text-primary text-[18px]">description</span>
                </div>
                <div class="flex-grow min-w-0">
                  <p class="font-bold text-sm text-slate-900 dark:text-white truncate">{{ cv.title }}</p>
                  <p class="text-xs text-slate-400 mt-0.5">{{ dayjs(cv.updatedAt).format('DD/MM/YYYY') }}</p>
                </div>
                <div 
                  class="w-4.5 h-4.5 rounded-full border-2 flex items-center justify-center shrink-0"
                  :class="selectedCvId === cv.id ? 'border-primary bg-primary' : 'border-slate-300 dark:border-slate-600'"
                >
                  <div v-if="selectedCvId === cv.id" class="w-1.5 h-1.5 rounded-full bg-white"></div>
                </div>
              </div>
            </div>
          </div>

          <div class="p-5 bg-slate-50 dark:bg-slate-800/30 border-t border-slate-100 dark:border-slate-800 flex items-center gap-3">
            <button 
              @click="showBatchApplyModal = false"
              class="flex-1 px-4 py-2.5 rounded-xl font-bold text-sm text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-700 transition-all cursor-pointer"
            >
              Hủy bỏ
            </button>
            <button 
              @click="confirmBatchApply"
              class="flex-1 bg-primary hover:bg-primary-hover text-white px-4 py-2.5 rounded-xl font-bold text-sm shadow-lg shadow-primary/20 transition-all flex items-center justify-center gap-2 cursor-pointer"
            >
              <span class="material-symbols-outlined text-[16px]">send</span>
              Gửi đơn ngay
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="flex items-center justify-center gap-1.5 pt-4 pb-10">
      <button
        class="p-2 rounded-lg text-slate-400 disabled:opacity-30 disabled:cursor-not-allowed hover:bg-slate-100 dark:hover:bg-slate-800 hover:text-slate-600 transition-colors cursor-pointer"
        :disabled="currentPage === 0"
        @click="changePage(currentPage - 1)"
      >
        <span class="material-symbols-outlined text-[20px]">chevron_left</span>
      </button>
      <button 
        v-for="p in totalPages" 
        :key="p"
        class="w-9 h-9 rounded-lg text-sm font-bold transition-all cursor-pointer"
        :class="currentPage === p-1 
          ? 'bg-primary text-white shadow-md shadow-primary/20' 
          : 'text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800'"
        @click="changePage(p-1)"
      >
        {{ p }}
      </button>
      <button
        class="p-2 rounded-lg text-slate-400 disabled:opacity-30 disabled:cursor-not-allowed hover:bg-slate-100 dark:hover:bg-slate-800 hover:text-slate-600 transition-colors cursor-pointer"
        :disabled="currentPage === totalPages - 1"
        @click="changePage(currentPage + 1)"
      >
        <span class="material-symbols-outlined text-[20px]">chevron_right</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed, ref } from 'vue'
import { useSavedJobStore } from '@/stores/savedJob.store'
import { useApplicationStore } from '@/stores/application.store'
import { useCvsStore } from '@/stores/cvs.store'
import { useToast } from '@/composables/useToast'
import { useConfirm } from '@/composables/useConfirm'
import { useQuickApply } from '@/composables/useQuickApply'
import JobCard from '@/components/candidate/job/JobCard.vue'
import { formatSalary, formatWorkType } from '@/types/jobPosting.types'
import dayjs from 'dayjs'

const savedJobStore = useSavedJobStore()
const applicationStore = useApplicationStore()
const cvsStore = useCvsStore()
const toast = useToast()
const { confirm } = useConfirm()
const { handleQuickApply } = useQuickApply()

const currentPage = ref(0)
const selectedIds = ref<number[]>([])
const showBatchApplyModal = ref(false)
const selectedCvId = ref<number | null>(null)

const isAllSelected = computed(() => {
  return mappedJobs.value.length > 0 && selectedIds.value.length === mappedJobs.value.length
})

function handleSelect(id: number, active: boolean) {
  if (active) {
    if (!selectedIds.value.includes(id)) {
      selectedIds.value.push(id)
    }
  } else {
    selectedIds.value = selectedIds.value.filter(itemId => itemId !== id)
  }
}

function toggleSelectAll() {
  if (isAllSelected.value) {
    selectedIds.value = []
  } else {
    selectedIds.value = mappedJobs.value.map(job => job.id)
  }
}

async function handleBatchApply() {
  if (selectedIds.value.length === 0) return
  
  if (selectedIds.value.length > 10) {
    toast.error('Lỗi', 'Tối đa 10 tin cho mỗi lần ứng tuyển hàng loạt')
    return
  }

  if (cvsStore.cvs.length === 0) {
    await cvsStore.fetchMyCvs()
  }

  if (cvsStore.cvs.length === 0) {
    toast.warning('Chú ý', 'Bạn cần có ít nhất một CV để ứng tuyển. Vui lòng tạo CV trước.')
    return
  }

  // Set default CV or first CV
  const defaultCv = cvsStore.cvs.find(cv => cv.isDefault) || cvsStore.cvs[0]
  if (defaultCv) {
    selectedCvId.value = defaultCv.id
  }

  showBatchApplyModal.value = true
}

async function confirmBatchApply() {
  if (!selectedCvId.value) {
    toast.warning('Chú ý', 'Vui lòng chọn CV để ứng tuyển')
    return
  }

  try {
    await applicationStore.bulkApply({
      jobPostIds: selectedIds.value,
      cvId: selectedCvId.value
    })
    
    toast.success('Thành công', `Đã gửi đơn ứng tuyển vào ${selectedIds.value.length} vị trí thành công!`)
    showBatchApplyModal.value = false
    selectedIds.value = []
  } catch (err: any) {
    const message = err?.response?.data?.message || 'Có lỗi xảy ra khi ứng tuyển hàng loạt'
    toast.error('Lỗi', message)
  }
}

const mappedJobs = computed(() => {
  return savedJobStore.savedJobs.map((item) => {
    const job = item.jobPosting;
    const locationNames = job.locations?.length
      ? [...new Set(job.locations.map(l => l.isRemote ? 'Remote' : l.name))].join(', ')
      : 'Việt Nam';
    return {
      id: job.id,
      title: job.title,
      company: job.company.name,
      isBrandVerified: job.company.isBrandVerified,
      logoUrl: job.company.logoUrl || "/default-company.png",
      logoBg: "bg-blue-50",
      logoBorder: "border-blue-100",
      tags: [formatWorkType(job.workType), job.level.name],
      salaryMin: formatSalary(job),
      salaryMax: "",
      location: locationNames,
      postedAt: formatDate(job.publishedAt || job.createdAt),
      isHot: job.isHot,
      isUrgent: job.isUrgent,
      isSaved: true,
    };
  });
});

const totalPages = computed(() => savedJobStore.meta.pages)

function formatDate(dateStr: string) {
  if (!dateStr) return "";
  const date = dayjs(dateStr)
  const now = dayjs()
  const diffInDays = now.diff(date, 'day')

  if (diffInDays === 0) return "Hôm nay";
  if (diffInDays === 1) return "Hôm qua";
  return `${diffInDays} ngày trước`;
}

async function fetchSavedJobs() {
  await savedJobStore.fetchSavedJobs(currentPage.value)
}

async function onQuickApply(jobId: number) {
  const job = mappedJobs.value.find(j => j.id === jobId);
  if (job) {
    await handleQuickApply(jobId, job.title);
  }
}

async function handleUnsaveConfirm(jobPostId: number) {
  const job = savedJobStore.savedJobs.find(j => j.jobPostId === jobPostId);
  const jobTitle = job?.jobPosting?.title || 'việc làm này';

  const ok = await confirm({
    title: 'Bỏ lưu việc làm?',
    message: `Bạn có chắc chắn muốn bỏ lưu "${jobTitle}"?`,
    confirmText: 'Bỏ lưu',
    cancelText: 'Hủy',
    confirmColor: 'red',
    icon: 'bookmark_remove'
  })

  if (ok) {
    try {
      await savedJobStore.unsave(jobPostId)
      toast.success('Thành công', `Đã bỏ lưu ${jobTitle}`)
      
      // Nếu là item cuối cùng của trang và không phải trang đầu, lùi lại 1 trang
      if (savedJobStore.savedJobs.length === 0 && currentPage.value > 0) {
        currentPage.value--
        await fetchSavedJobs()
      }
    } catch (err) {
      toast.error('Lỗi', 'Có lỗi xảy ra khi thực hiện thao tác')
    }
  }
}

function changePage(page: number) {
  currentPage.value = page
  fetchSavedJobs()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  fetchSavedJobs()
  cvsStore.fetchMyCvs()
})
</script>

<style scoped>
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease-out;
}

.slide-up-enter-from,
.slide-up-leave-to {
  transform: translate(-50%, 100%);
  opacity: 0;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
</style>
