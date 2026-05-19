<template>
  <div class="space-y-5">
    <!-- Header: Title + Filters -->
    <div class="pt-4 mt-2">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4">
        <div>
          <h2 class="text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight">Việc làm đã ứng tuyển</h2>
          <p class="text-slate-500 dark:text-gray-400 text-base mt-1">Theo dõi trạng thái và tiến độ các đơn ứng tuyển.</p>
        </div>
        <div v-if="!loading" class="flex items-center gap-2">
          <span class="inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full text-base font-bold bg-primary/10 text-primary border border-primary/20">
            <span class="material-symbols-outlined text-[18px]">send</span>
            {{ meta.totals }} đơn
          </span>
        </div>
      </div>
      <!-- Filter Tabs -->
      <div class="flex items-center gap-2 overflow-x-auto scrollbar-hide">
        <button
          v-for="filter in filterOptions"
          :key="filter.value"
          @click="handleFilterChange(filter.value)"
          class="whitespace-nowrap px-3.5 py-1.5 rounded-lg text-sm font-bold transition-all duration-200 cursor-pointer"
          :class="activeFilter === filter.value
            ? 'bg-primary text-white shadow-md shadow-primary/20'
            : 'text-slate-500 dark:text-gray-400 hover:bg-slate-100 dark:hover:bg-slate-700'"
        >
          {{ filter.label }}
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading && applications.length === 0" class="flex flex-col items-center justify-center py-20">
      <span class="material-symbols-outlined animate-spin text-primary text-4xl">progress_activity</span>
      <p class="mt-4 text-sm text-slate-400 font-medium">Đang tải danh sách...</p>
    </div>

    <!-- Empty state -->
    <div v-else-if="applications.length === 0" class="bg-white dark:bg-surface-dark py-16 px-8 rounded-2xl border border-slate-200 dark:border-slate-800 flex flex-col items-center justify-center text-center">
      <div class="w-20 h-20 bg-slate-100 dark:bg-slate-800 rounded-2xl flex items-center justify-center text-slate-400 mb-5">
        <span class="material-symbols-outlined text-4xl">work_history</span>
      </div>
      <h3 class="text-lg font-bold text-slate-900 dark:text-white mb-1.5">Không tìm thấy đơn ứng tuyển</h3>
      <p class="text-slate-500 dark:text-gray-400 text-sm mb-6 max-w-xs">Bạn không có đơn ứng tuyển nào ở trạng thái này.</p>
      <button 
        v-if="activeFilter !== 'all'"
        @click="handleFilterChange('all')"
        class="inline-flex items-center gap-2 text-primary font-bold text-sm hover:underline cursor-pointer"
      >
        <span class="material-symbols-outlined text-[16px]">filter_list_off</span>
        Xem tất cả các đơn
      </button>
    </div>

    <!-- Application List -->
    <div v-else class="flex flex-col gap-3">
      <div 
        v-for="app in applications" 
        :key="app.id"
        class="group bg-white dark:bg-surface-dark border border-slate-200 dark:border-slate-800 rounded-2xl p-4 sm:p-5 hover:border-primary/40 transition-all duration-200 hover:shadow-sm"
      >
        <div class="flex flex-col sm:flex-row gap-4">
          <!-- Company Logo -->
          <div class="flex-shrink-0 w-12 h-12 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-100 dark:border-slate-700 p-1.5 flex items-center justify-center">
            <img :src="app.jobPosting?.company.logoUrl || '/default-company.png'" :alt="app.jobPosting?.company.name" class="w-full h-full object-contain" />
          </div>

          <!-- Job Info -->
          <div class="flex-grow min-w-0">
            <div class="flex flex-col sm:flex-row sm:items-start justify-between gap-2">
              <div class="min-w-0">
                <h3 class="text-lg font-bold text-slate-900 dark:text-white group-hover:text-primary transition-colors truncate">
                  {{ app.jobPosting?.title || 'Công việc không còn tồn tại' }}
                </h3>
                <div class="flex items-center gap-1 mt-0.5">
                  <p class="text-slate-500 dark:text-gray-400 text-base truncate">{{ app.jobPosting?.company.name || 'N/A' }}</p>
                  <div v-if="app.jobPosting?.company.isBrandVerified" class="relative flex items-center shrink-0 group/verified">
                    <span
                      class="material-symbols-outlined text-blue-600 cursor-default"
                      style="font-size: 15px; font-variation-settings: 'FILL' 1"
                    >verified</span>
                    <span class="pointer-events-none absolute bottom-[calc(100%+6px)] left-1/2 -translate-x-1/2 bg-blue-800 text-white text-[11px] font-medium whitespace-nowrap px-2.5 py-1 rounded-md opacity-0 group-hover/verified:opacity-100 transition-opacity z-10">
                      Công ty đã xác minh
                    </span>
                  </div>
                </div>
              </div>
              <div :class="getStatusClasses(app.status)" class="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg text-xs font-bold border shrink-0 self-start">
                <span class="material-symbols-outlined text-[14px]">{{ getStatusIcon(app.status) }}</span>
                {{ getStatusLabel(app.status) }}
              </div>
            </div>

            <div class="mt-3 flex flex-wrap items-center gap-x-5 gap-y-1.5 text-sm text-slate-500 dark:text-gray-400">
              <div class="flex items-center gap-1">
                <span class="material-symbols-outlined text-[14px]">calendar_today</span>
                Nộp: <span class="font-medium text-slate-700 dark:text-gray-300 ml-0.5">{{ formatDate(app.createdAt) }}</span>
              </div>
              <div v-if="app.viewedAt" class="flex items-center gap-1 text-emerald-600 dark:text-emerald-400">
                <span class="material-symbols-outlined text-[14px]">visibility</span>
                Đã xem {{ formatDate(app.viewedAt) }}
              </div>
              <div v-else-if="app.status === APPLICATION_STATUS.PENDING" class="flex items-center gap-1">
                <span class="material-symbols-outlined text-[14px]">hourglass_top</span>
                Chờ phản hồi
              </div>
            </div>
          </div>

          <!-- Actions -->
          <div class="flex sm:flex-col items-center gap-1.5 shrink-0 sm:border-l sm:border-slate-100 sm:dark:border-slate-800 sm:pl-4">
            <router-link 
              :to="{ name: 'JobDetail', params: { id: app.jobPostId } }"
              class="p-2 rounded-lg text-slate-400 hover:text-primary hover:bg-primary/5 transition-all cursor-pointer"
              title="Xem chi tiết"
            >
              <span class="material-symbols-outlined text-[20px]">visibility</span>
            </router-link>
            <button 
              v-if="app.cvId"
              @click="handleViewCv(app)"
              class="p-2 rounded-lg text-slate-400 hover:text-primary hover:bg-primary/5 transition-all cursor-pointer"
              title="Xem CV"
            >
              <span class="material-symbols-outlined text-[20px]">description</span>
            </button>
            <button 
              v-if="app.status === APPLICATION_STATUS.PENDING"
              @click="handleChangeCv(app)"
              class="p-2 rounded-lg text-slate-400 hover:text-primary hover:bg-primary/5 transition-all cursor-pointer"
              title="Đổi CV"
            >
              <span class="material-symbols-outlined text-[20px]">edit_document</span>
            </button>
            <button
              v-if="app.status === APPLICATION_STATUS.PENDING"
              @click="handleWithdraw(app)"
              class="p-2 rounded-lg text-slate-400 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/20 transition-all cursor-pointer"
              title="Rút đơn"
            >
              <span class="material-symbols-outlined text-[20px]">cancel</span>
            </button>
            <template v-if="app.status === APPLICATION_STATUS.INVITED">
              <button
                @click="handleAcceptInvite(app)"
                class="p-2 rounded-lg text-slate-400 hover:text-emerald-600 hover:bg-emerald-50 dark:hover:bg-emerald-900/20 transition-all cursor-pointer"
                title="Tham gia ứng tuyển"
              >
                <span class="material-symbols-outlined text-[20px]">check_circle</span>
              </button>
              <button
                @click="handleDeclineInvite(app)"
                class="p-2 rounded-lg text-slate-400 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/20 transition-all cursor-pointer"
                title="Từ chối ứng tuyển"
              >
                <span class="material-symbols-outlined text-[20px]">do_not_disturb_on</span>
              </button>
            </template>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal đổi CV -->
    <ChangeCvModal 
      v-if="targetApp"
      :show="showChangeCvModal"
      :applicationId="targetApp.id"
      :currentCvId="targetApp.cvId"
      :jobTitle="targetApp.jobPosting?.title"
      @close="showChangeCvModal = false"
      @success="onChangeCvSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useApplicationStore } from '@/stores/application.store'
import { useCvsStore } from '@/stores/cvs.store'
import ChangeCvModal from './ChangeCvModal.vue'
import { 
  APPLICATION_STATUS, 
  APPLICATION_STATUS_OPTIONS
} from '@/constants/application.constants'
import { useToast } from '@/composables/useToast'
import { useConfirm } from '@/composables/useConfirm'
import { CV_TYPE } from '@/constants/cvs.constants'
import type { ResApplication } from '@/types/application.types'
import dayjs from 'dayjs'

const toast = useToast()
const { confirm } = useConfirm()
const applicationStore = useApplicationStore()
const cvsStore = useCvsStore()

const { applications, loading, meta } = storeToRefs(applicationStore)

const activeFilter = ref('all')
const showChangeCvModal = ref(false)
const targetApp = ref<ResApplication | null>(null)

const filterOptions = [
  { label: 'Tất cả', value: 'all' },
  { label: 'Đã gửi', value: APPLICATION_STATUS.PENDING },
  { label: 'Đã xem', value: APPLICATION_STATUS.SEEN },
  { label: 'Phỏng vấn', value: APPLICATION_STATUS.INTERVIEWING },
  { label: 'Kết quả', value: 'result' }, // custom filter logic if needed or just handle in fetch
]

onMounted(() => {
  fetchData()
})

const fetchData = async (page = 0) => {
  const status = activeFilter.value === 'all' ? undefined : activeFilter.value
  await applicationStore.fetchMyApplications({ status, page })
}

async function handleViewCv(app: ResApplication) {
  if (!app.cvId) return
  try {
    await cvsStore.fetchCvById(app.cvId)
    const cv = cvsStore.currentCv
    const url = cv
      ? cv.cvType === CV_TYPE.ONLINE
        ? (await cvsStore.exportPdf(cv.id)).pdfUrl
        : cv.fileUrl || cv.pdfUrl
      : null
    if (url) {
      window.open(url, '_blank')
    } else {
      toast.error('Lỗi', 'Không tìm thấy liên kết CV')
    }
  } catch {
    toast.error('Lỗi', 'Không thể tải CV. Vui lòng thử lại.')
  }
}

function handleChangeCv(app: ResApplication) {
  targetApp.value = app
  showChangeCvModal.value = true
}

function onChangeCvSuccess() {
  fetchData(meta.value.page)
}

const handleFilterChange = (value: string) => {
  activeFilter.value = value
  fetchData(0)
}

function getStatusLabel(status: string) {
  const option = APPLICATION_STATUS_OPTIONS.find(opt => opt.value === status)
  return option?.label || status
}


function getStatusIcon(status: string) {
  switch (status) {
    case APPLICATION_STATUS.PENDING: return 'send'
    case APPLICATION_STATUS.INVITED: return 'mail'
    case APPLICATION_STATUS.SEEN: return 'visibility'
    case APPLICATION_STATUS.INTERVIEWING: return 'event_upcoming'
    case APPLICATION_STATUS.OFFERED: return 'local_offer'
    case APPLICATION_STATUS.HIRED: return 'check_circle'
    case APPLICATION_STATUS.REJECTED: return 'cancel'
    case APPLICATION_STATUS.WITHDRAWN: return 'history'
    case APPLICATION_STATUS.EXPIRED: return 'timer_off'
    default: return 'info'
  }
}

function getStatusClasses(status: string) {
  const option = APPLICATION_STATUS_OPTIONS.find(opt => opt.value === status)
  const color = option?.color || 'default'
  
  switch (color) {
    case 'blue': return 'bg-blue-50 text-blue-600 border-blue-100 dark:bg-blue-900/20 dark:text-blue-400 dark:border-blue-800'
    case 'purple': return 'bg-purple-50 text-purple-600 border-purple-100 dark:bg-purple-900/20 dark:text-purple-400 dark:border-purple-800'
    case 'orange': return 'bg-orange-50 text-orange-600 border-orange-100 dark:bg-orange-900/20 dark:text-orange-400 dark:border-orange-800'
    case 'green':
    case 'success': return 'bg-green-50 text-green-600 border-green-100 dark:bg-green-900/20 dark:text-green-400 dark:border-green-800'
    case 'red': return 'bg-red-50 text-red-600 border-red-100 dark:bg-red-900/20 dark:text-red-400 dark:border-red-800'
    case 'gray': return 'bg-slate-50 text-slate-600 border-slate-100 dark:bg-slate-800 dark:text-slate-400 dark:border-slate-700'
    default: return 'bg-slate-50 text-slate-600 border-slate-100 dark:bg-slate-800 dark:text-slate-400 dark:border-slate-700'
  }
}

function formatDate(date: string) {
  return dayjs(date).format('DD/MM/YYYY')
}

async function handleAcceptInvite(app: ResApplication) {
  const ok = await confirm({
    title: 'Tham gia ứng tuyển?',
    message: `Bạn có muốn chấp nhận lời mời ứng tuyển cho vị trí "${app.jobPosting?.title}" tại ${app.jobPosting?.company.name}?`,
    confirmText: 'Tham gia',
    cancelText: 'Hủy',
    confirmColor: 'primary',
    icon: 'check_circle',
  })

  if (ok) {
    try {
      await applicationStore.acceptInvite(app.id)
      toast.success('Thành công', 'Đã chấp nhận lời mời ứng tuyển')
    } catch {
      toast.error('Lỗi', applicationStore.error ?? 'Không thể chấp nhận lời mời. Vui lòng thử lại.')
    }
  }
}

async function handleDeclineInvite(app: ResApplication) {
  const ok = await confirm({
    title: 'Từ chối ứng tuyển?',
    message: `Bạn có muốn từ chối lời mời ứng tuyển cho vị trí "${app.jobPosting?.title}" tại ${app.jobPosting?.company.name}?`,
    confirmText: 'Từ chối',
    cancelText: 'Hủy',
    confirmColor: 'red',
    icon: 'do_not_disturb_on',
  })

  if (ok) {
    try {
      await applicationStore.declineInvite(app.id)
      toast.success('Thành công', 'Đã từ chối lời mời ứng tuyển')
    } catch {
      toast.error('Lỗi', applicationStore.error ?? 'Không thể từ chối lời mời. Vui lòng thử lại.')
    }
  }
}

async function handleWithdraw(app: ResApplication) {
  const ok = await confirm({
    title: 'Rút đơn ứng tuyển?',
    message: `Bạn có chắc chắn muốn rút đơn ứng tuyển cho vị trí "${app.jobPosting?.title}" tại ${app.jobPosting?.company.name}? Hành động này không thể hoàn tác.`,
    confirmText: 'Rút đơn',
    cancelText: 'Hủy',
    confirmColor: 'red',
    icon: 'cancel'
  })

  if (ok) {
    try {
      await applicationStore.withdraw(app.id)
      toast.success('Thành công', 'Đã rút đơn ứng tuyển thành công')
    } catch (err) {
      toast.error('Lỗi', 'Không thể rút đơn ứng tuyển lúc này')
    }
  }
}
</script>

<style scoped>
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
