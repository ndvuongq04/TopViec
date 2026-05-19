<template>
  <GlobalModal
    :visible="visible"
    :title="isReassignMode ? 'Đổi người phụ trách' : 'Phân công người phụ trách'"
    :subtitle="job ? `<span class='text-primary font-bold'>${job.title}</span> | ID: ${job.code || 'JOB-' + job.id}` : ''"
    :icon="isReassignMode ? 'swap_horiz' : 'person_add'"
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
          placeholder="Nhập tên hoặc email rồi nhấn Enter..." 
          type="text"
          @keyup.enter="handleSearch"
        />
      </div>

      <!-- List Header -->
      <div class="flex justify-between items-center mb-3 px-2 shrink-0">
        <span class="text-xs font-extrabold text-slate-500 uppercase tracking-wider">Thành viên</span>
        <span class="text-xs font-extrabold text-slate-500 uppercase tracking-wider">Khối lượng công việc</span>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="flex-1 flex items-center justify-center">
        <span class="material-symbols-outlined animate-spin text-4xl text-slate-300">progress_activity</span>
      </div>
      
      <!-- Empty State -->
      <div v-else-if="recruiters.length === 0" class="flex-1 flex flex-col items-center justify-center text-slate-400">
        <span class="material-symbols-outlined text-5xl mb-2">search_off</span>
        <p class="text-sm font-medium">Không tìm thấy thành viên nào</p>
      </div>

      <!-- Member List (Scrollable) -->
      <div v-else class="flex-1 overflow-y-auto pr-2 space-y-2 -mr-2">
        <div
          v-for="member in recruiters"
          :key="member.userId"
          class="flex items-center justify-between p-3.5 rounded-xl transition-all duration-200 cursor-pointer border"
          :class="[
            selectedMemberId === member.userId ? 'bg-primary/5 border-primary/30' :
            member.isCurrentAssignee ? 'bg-amber-50 border-amber-200' :
            'hover:bg-slate-50 border-transparent'
          ]"
          @click="selectMember(member.userId)"
        >
          <div class="flex items-center gap-4">
            <div class="relative">
              <div class="w-10 h-10 rounded-full flex items-center justify-center font-bold text-sm shrink-0 border border-slate-200"
                   :class="getAvatarColorClass(member)"
                   :style="member.avatarUrl ? `background-image: url('${member.avatarUrl}'); background-size: cover;` : ''">
                <span v-if="!member.avatarUrl">{{ initials(member.name || member.email) }}</span>
              </div>
              <div class="absolute -bottom-0.5 -right-0.5 w-3.5 h-3.5 bg-emerald-500 border-2 border-white rounded-full"></div>
            </div>
            <div>
              <div class="flex items-center gap-2">
                <p class="font-bold text-slate-900 text-lg leading-tight">{{ member.email.split('@')[0] }}</p>
                <span v-if="member.isCurrentAssignee" class="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-100 text-amber-700">
                  <span class="material-symbols-outlined" style="font-size:11px;font-variation-settings:'FILL' 1">star</span>
                  Đang phụ trách
                </span>
              </div>
              <p class="text-sm text-slate-500 mt-0.5">{{ member.email }}</p>
            </div>
          </div>
          <div class="flex items-center gap-4">
            <!-- Job count badge -->
            <span 
              class="px-2.5 py-1 rounded-full text-xs font-bold flex items-center gap-1.5"
              :class="getJobCountBadgeClass(member.assignedJobCount || 0)"
            >
              <span class="material-symbols-outlined text-sm" v-if="(member.assignedJobCount || 0) < 10">work_outline</span>
              <span class="material-symbols-outlined text-sm" v-else>warning</span>
              {{ member.assignedJobCount || 0 }} Jobs
            </span>

            <!-- Checkbox/Radio indicator -->
            <div 
              class="w-5 h-5 rounded-full flex items-center justify-center transition-all duration-200"
              :class="selectedMemberId === member.userId ? 'bg-primary border-none shadow-sm shadow-primary/30' : 'border-2 border-slate-200'"
            >
              <span v-if="selectedMemberId === member.userId" class="material-symbols-outlined text-white text-sm" style="font-variation-settings: 'FILL' 1;">check</span>
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
        :disabled="!selectedMemberId || submitting || selectedMemberId === currentAssigneeId"
        @click="handleAssign"
      >
        <span v-if="submitting" class="material-symbols-outlined animate-spin text-[18px]">progress_activity</span>
        <span v-else class="material-symbols-outlined text-[18px]">{{ isReassignMode ? 'swap_horiz' : 'arrow_forward' }}</span>
        {{ isReassignMode ? 'Xác nhận đổi người' : 'Xác nhận phân công' }}
      </button>
    </template>
  </GlobalModal>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useEmployerJobAssignmentStore } from '@/stores/employerJobAssignment.store'
import { useToast } from '@/composables/useToast'
import GlobalModal from '@/components/ui/GlobalModal.vue'

const props = defineProps<{
  visible: boolean
  job: { id: number; title: string; code?: string; assignedRecruiter?: { userId: number; email: string } | null } | null
}>()

const emit = defineEmits<{
  close: []
  assigned: []        // Phát ra sau khi giao việc thành công
}>()

const assignmentStore = useEmployerJobAssignmentStore()
const toast = useToast()
const searchQuery = ref('')
const selectedMemberId = ref<number | null>(null)
const loading = ref(false)
const submitting = ref(false)

/** ID của NTD đang phụ trách (nếu có) */
const currentAssigneeId = computed(() => props.job?.assignedRecruiter?.userId ?? null)
/** Modal đang ở chế độ đổi người (tin đã có người phụ trách) */
const isReassignMode = computed(() => !!currentAssigneeId.value)

// Mở modal → reset state → gọi API lấy danh sách NTD
// Truyền jobPostId để BE đánh dấu isCurrentAssignee
watch(() => props.visible, async (newVal) => {
  if (newVal) {
    searchQuery.value = ''
    // Pre-select NTD đang phụ trách nếu ở chế độ reassign
    selectedMemberId.value = currentAssigneeId.value
    loading.value = true
    try {
      await assignmentStore.fetchRecruiters({
        size: 50,
        jobPostId: props.job?.id,
      })
    } finally {
      loading.value = false
    }
  }
})

// Lấy danh sách NTD từ store
const recruiters = computed(() => assignmentStore.recruiters?.result || [])

async function handleSearch() {
  loading.value = true
  try {
    await assignmentStore.fetchRecruiters({
      keyword: searchQuery.value || undefined,
      jobPostId: props.job?.id,
      size: 50,
    })
  } catch {
    toast.error('Tìm kiếm thất bại', 'Không thể tải danh sách thành viên. Vui lòng thử lại.')
  } finally {
    loading.value = false
  }
}

function selectMember(id: number) {
  selectedMemberId.value = selectedMemberId.value === id ? null : id
}

async function handleAssign() {
  if (!selectedMemberId.value || !props.job) return
  // Không cho đổi về chính người đang phụ trách
  if (selectedMemberId.value === currentAssigneeId.value) return

  submitting.value = true
  try {
    if (isReassignMode.value) {
      await assignmentStore.reassignJobPost({
        jobPostId: props.job.id,
        userId: selectedMemberId.value,
      })
      toast.success(
        'Đổi người phụ trách thành công!',
        `Đã chuyển tin "${props.job.title}" sang thành viên mới.`,
      )
    } else {
      await assignmentStore.assignJobPost({
        jobPostId: props.job.id,
        userId: selectedMemberId.value,
      })
      toast.success(
        'Phân công thành công!',
        `Đã giao tin “${props.job.title}” cho thành viên được chọn.`,
      )
    }
    emit('assigned')
    emit('close')
  } catch (err: any) {
    const msg = err?.response?.data?.message || 'Không thể thực hiện. Vui lòng thử lại.'
    toast.error(isReassignMode.value ? 'Đổi người thất bại' : 'Phân công thất bại', msg)
  } finally {
    submitting.value = false
  }
}

function initials(email: string) {
  if (!email) return 'U'
  const name = email.split('@')[0]
  return name.split(/[._-]/).map(w => w[0]).join('').slice(0, 2).toUpperCase()
}

function getAvatarColorClass(member: any) {
  const colors = [
    'bg-blue-100 text-blue-700 border-blue-200',
    'bg-emerald-100 text-emerald-700 border-emerald-200',
    'bg-amber-100 text-amber-700 border-amber-200',
    'bg-purple-100 text-purple-700 border-purple-200',
    'bg-rose-100 text-rose-700 border-rose-200',
  ]
  const hash = String(member.userId).split('').reduce((acc: number, char: string) => acc + char.charCodeAt(0), 0)
  return colors[hash % colors.length]
}

function getJobCountBadgeClass(count: number) {
  if (count >= 10) return 'bg-rose-100 text-rose-700'
  if (count >= 5)  return 'bg-amber-100 text-amber-700'
  return 'bg-emerald-100 text-emerald-700'
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
