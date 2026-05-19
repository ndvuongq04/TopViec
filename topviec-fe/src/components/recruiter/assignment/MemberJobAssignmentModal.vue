<template>
  <GlobalModal
    :visible="visible"
    title="Giao việc cho thành viên"
    :subtitle="member ? `<span class='text-primary font-bold'>${member.name}</span> | <span class='text-primary font-medium'>${member.role}</span>` : ''"
    icon="add_task"
    max-width="2xl"
    @close="$emit('close')"
  >
    <!-- Content Area -->
    <div class="flex flex-col h-[50vh] min-h-[400px]">
      <!-- Search -->
      <div class="relative mb-6 shrink-0">
        <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
          <span class="material-symbols-outlined text-slate-400 text-[20px]">search</span>
        </div>
        <input 
          v-model="searchQuery"
          class="w-full pl-11 pr-4 py-3 bg-slate-50 rounded-full border border-slate-200 focus:ring-2 focus:ring-primary/20 focus:border-primary outline-none text-base text-slate-900 placeholder:text-slate-400 transition-all duration-200" 
          placeholder="Nhập tiêu đề tin rồi nhấn Enter..." 
          type="text"
          @keyup.enter="handleSearch"
        />
      </div>

      <!-- List Header -->
      <div class="flex justify-between items-center mb-3 px-2 shrink-0">
        <span class="text-xs font-extrabold text-slate-500 uppercase tracking-wider">Tin tuyển dụng khả dụng</span>
        <span class="text-xs font-extrabold text-slate-500 uppercase tracking-wider">Trạng thái</span>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="flex-1 flex items-center justify-center">
        <span class="material-symbols-outlined animate-spin text-4xl text-slate-300">progress_activity</span>
      </div>
      
      <!-- Empty State -->
      <div v-else-if="jobs.length === 0" class="flex-1 flex flex-col items-center justify-center text-slate-400">
        <span class="material-symbols-outlined text-5xl mb-2">work_off</span>
        <p class="text-sm font-medium">Tất cả tin đã được phân công</p>
      </div>

      <!-- Job List (Scrollable) -->
      <div v-else class="flex-1 overflow-y-auto pr-2 space-y-2 -mr-2 custom-scrollbar">
          <div 
            v-for="job in jobs" 
            :key="job.id"
            class="flex items-center justify-between p-3.5 rounded-xl transition-all duration-200 border group"
            :class="[
              selectedJobId === job.id ? 'bg-primary/5 border-primary/30' : 'border-transparent',
              canAssign(job.status) ? 'cursor-pointer hover:bg-slate-50' : 'cursor-not-allowed opacity-50 bg-slate-50/50'
            ]"
            :title="!canAssign(job.status) ? 'Cho phép: published, paused, renewed, interviewing, scheduled, closed' : ''"
            @click="canAssign(job.status) && selectJob(job.id)"
          >
            <div class="flex items-center gap-4">
              <div 
                class="w-10 h-10 rounded-lg bg-slate-100 flex items-center justify-center text-slate-500 transition-colors"
                :class="canAssign(job.status) ? 'group-hover:bg-primary/10 group-hover:text-primary' : ''"
              >
                <span class="material-symbols-outlined">work</span>
              </div>
              <div>
                <p class="font-bold text-slate-900 text-lg leading-tight">{{ job.title }}</p>
                <p class="text-sm text-slate-500 mt-0.5">ID: {{ job.code || 'JOB-' + job.id }}</p>
              </div>
            </div>
          <div class="flex items-center gap-4">
            <!-- Status indicator -->
            <span 
              class="px-2.5 py-1 rounded-full text-xs font-bold"
              :class="JOB_POSTING_STATUS_BADGE[job.status as JobPostingStatus] ?? 'bg-slate-100 text-slate-500'"
            >
              {{ JOB_POSTING_STATUS_LABELS[job.status as JobPostingStatus] ?? job.status }}
            </span>

            <!-- Checkbox/Radio indicator -->
            <div 
              class="w-5 h-5 rounded-full flex items-center justify-center transition-all duration-200"
              :class="selectedJobId === job.id ? 'bg-primary border-none shadow-sm shadow-primary/30' : 'border-2 border-slate-200'"
            >
              <span v-if="selectedJobId === job.id" class="material-symbols-outlined text-white text-sm" style="font-variation-settings: 'FILL' 1;">check</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Custom Footer -->
    <template #footer>
      <button 
        class="px-6 py-2.5 rounded-xl border border-slate-200 bg-white text-slate-700 font-bold text-base hover:bg-slate-50 transition-colors cursor-pointer"
        @click="$emit('close')"
        :disabled="submitting"
      >
        Hủy
      </button>
      <button 
        class="px-6 py-2.5 rounded-xl bg-primary text-white font-bold text-base shadow-lg shadow-primary/20 hover:bg-primary/90 transition-colors flex items-center gap-2 cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
        :disabled="!selectedJobId || submitting"
        @click="handleAssign"
      >
        <span v-if="submitting" class="material-symbols-outlined animate-spin text-[18px]">progress_activity</span>
        <span v-else class="material-symbols-outlined text-[18px]">arrow_forward</span>
        Xác nhận giao việc
      </button>
    </template>
  </GlobalModal>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useEmployerJobAssignmentStore } from '@/stores/employerJobAssignment.store'
import { useToast } from '@/composables/useToast'
import { JOB_POSTING_STATUS_LABELS, JOB_POSTING_STATUS_BADGE, JobPostingStatus } from '@/constants/jobPosting.constants'
import GlobalModal from '@/components/ui/GlobalModal.vue'

const props = defineProps<{
  visible: boolean
  member: any | null
}>()

const emit = defineEmits<{
  close: []
  assigned: []
}>()

const assignmentStore = useEmployerJobAssignmentStore()
const toast = useToast()
const searchQuery = ref('')
const selectedJobId = ref<number | null>(null)
const loading = ref(false)
const submitting = ref(false)

const jobs = computed(() => assignmentStore.unassignedJobPosts?.result || [])

// Mở modal → reset + load danh sách tin chưa phân công
watch(() => props.visible, async (newVal) => {
  if (newVal) {
    searchQuery.value = ''
    selectedJobId.value = null
    loading.value = true
    try {
      await assignmentStore.fetchUnassignedJobPosts({ size: 50 })
    } catch {
      toast.error('Tải thất bại', 'Không thể lấy danh sách tin tuyển dụng.')
    } finally {
      loading.value = false
    }
  }
})

// Enter → search API
async function handleSearch() {
  loading.value = true
  try {
    await assignmentStore.fetchUnassignedJobPosts({
      keyword: searchQuery.value || undefined,
      size: 50,
    })
  } catch {
    toast.error('Tìm kiếm thất bại', 'Không thể tải danh sách tin.')
  } finally {
    loading.value = false
  }
}

function canAssign(status: string) {
  const allowed = [
    JobPostingStatus.PUBLISHED,
    JobPostingStatus.PAUSED,
    JobPostingStatus.RENEWED,
    JobPostingStatus.INTERVIEWING,
    JobPostingStatus.SCHEDULED,
    JobPostingStatus.CLOSED
  ]
  return allowed.includes(status as JobPostingStatus)
}

function selectJob(id: number) {
  selectedJobId.value = selectedJobId.value === id ? null : id
}

async function handleAssign() {
  if (!selectedJobId.value || !props.member) return
  submitting.value = true
  try {
    await assignmentStore.assignJobPost({
      jobPostId: selectedJobId.value,
      userId: props.member.id,
    })
    const jobTitle = jobs.value.find((j: any) => j.id === selectedJobId.value)?.title ?? ''
    toast.success(
      'Giao việc thành công!',
      `Đã giao tin "${jobTitle}" cho ${props.member.name}.`,
    )
    emit('assigned')
    emit('close')
  } catch (err: any) {
    const msg = err?.response?.data?.message || 'Không thể giao việc. Vui lòng thử lại.'
    toast.error('Giao việc thất bại', msg)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #cbd5e1;
  border-radius: 20px;
}
</style>
