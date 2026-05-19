<template>
  <div class="assignment-page">
    <div class="assignment-page__header">
      <h1 class="assignment-page__title">Quản lý Phân công</h1>
      <p class="assignment-page__sub">Dashboard / Phân công / Chi tiết</p>
    </div>

    <div class="assignment-page__body">
      <!-- Left Panel -->
      <section class="assignment-page__left">
        <AssignmentTabs v-model="activeTab" />
        <AssignmentSearch
          :placeholder="activeTab === 'job' ? 'Nhập tên tin rồi nhấn Enter...' : 'Nhập email rồi nhấn Enter...'"
          @search="handleSearch"
        />

        <div class="assignment-page__list">
          <template v-if="activeTab === 'job'">
            <JobAssignmentList
              :jobs="jobList"
              :active-id="activeJobId"
              :loading="activeTab === 'job' && assignmentStore.loading"
              @select="activeJobId = $event"
            />
          </template>
          <template v-else>
            <MemberAssignmentList :members="members" :active-id="activeMemberId" @select="activeMemberId = $event" />
          </template>
        </div>
      </section>

      <!-- Right Panel -->
      <section class="assignment-page__right">
        <template v-if="activeTab === 'job'">
          <JobAssignmentDetail
            v-if="activeJob"
            :job="activeJob"
            :assignment="activeJob.assignment ?? null"
            :loading="false"
            @assign="showJobAssignModal = true"
          />
        </template>
        <template v-else>
          <template v-if="activeMember">
            <MemberAssignmentProfile :member="activeMember" @assign="showJobAssignModal = true" />
            <MemberAssignmentTable 
              ref="memberTableRef"
              :assignments="assignmentStore.jobPostsByRecruiter?.result || []"
              :loading="assignmentStore.loading"
              :active-member-id="activeMemberId"
              @revoke="handleRevoke"
              @reassign="handleOpenReassign"
            />
          </template>
        </template>
      </section>
    </div>

    <!-- Modal Phân công (Chọn người cho tin - tab job) -->
    <JobAssignmentModal
      :visible="showJobAssignModal && activeTab === 'job'"
      :job="activeJob ?? null"
      @close="showJobAssignModal = false"
      @assigned="handleConfirmAssign"
    />

    <!-- Modal Giao việc (Chọn tin cho người - tab member) -->
    <MemberJobAssignmentModal
      :visible="showJobAssignModal && activeTab === 'member'"
      :member="activeMember"
      @close="showJobAssignModal = false"
      @assigned="handleConfirmAssignJob"
    />

    <!-- Modal Đổi người phụ trách (swap từ bảng member) -->
    <JobAssignmentModal
      :visible="showReassignModal"
      :job="reassigningJob"
      @close="showReassignModal = false"
      @assigned="handleReassignSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useEmployerJobAssignmentStore } from '@/stores/employerJobAssignment.store'
import { useToast } from '@/composables/useToast'
import AssignmentTabs from '@/components/recruiter/assignment/AssignmentTabs.vue'
import AssignmentSearch from '@/components/recruiter/assignment/AssignmentSearch.vue'
import JobAssignmentList from '@/components/recruiter/assignment/AssignmentByJob/JobAssignmentList.vue'
import JobAssignmentDetail from '@/components/recruiter/assignment/AssignmentByJob/JobAssignmentDetail.vue'
import MemberAssignmentList from '@/components/recruiter/assignment/AssignmentByMember/MemberAssignmentList.vue'
import MemberAssignmentProfile from '@/components/recruiter/assignment/AssignmentByMember/MemberAssignmentProfile.vue'
import MemberAssignmentTable from '@/components/recruiter/assignment/AssignmentByMember/MemberAssignmentTable.vue'
import JobAssignmentModal from '@/components/recruiter/jobs/JobAssignmentModal.vue'
import MemberJobAssignmentModal from '@/components/recruiter/assignment/MemberJobAssignmentModal.vue'

const activeTab = ref<'job' | 'member'>('member')
const activeJobId = ref<number | null>(null)
const activeMemberId = ref<number | null>(null)
const showJobAssignModal = ref(false)
const showReassignModal = ref(false)
const reassigningJob = ref<{ id: number; title: string; assignedRecruiter: { userId: number; email: string } } | null>(null)
const assignmentStore = useEmployerJobAssignmentStore()
const toast = useToast()
const memberTableRef = ref<any>(null)

// Gọi API khi trang mount → load members và tự động chọn người đầu tiên
// watch(activeMemberId) sẽ tự trigger fetchJobPostsByRecruiter khi activeMemberId thay đổi
onMounted(async () => {
  await assignmentStore.fetchRecruiters({ size: 50 })
  const first = assignmentStore.recruiters?.result?.[0]
  if (first) {
    activeMemberId.value = first.userId  // ← watch tự gọi fetchJobPostsByRecruiter
  }
})

// Khi chọn member → gọi API lấy danh sách tin đang được giao
watch(activeMemberId, async (userId) => {
  if (userId) {
    await assignmentStore.fetchJobPostsByRecruiter(userId, { page: 0, size: 20 })
  } else {
    assignmentStore.jobPostsByRecruiter = null
  }
}, { immediate: false })

// Map dữ liệu từ ResRecruiterWithAssignmentCountDTO sang shape mà MemberAssignmentItem cần
const members = computed(() =>
  (assignmentStore.recruiters?.result || []).map((r) => ({
    id:       r.userId,
    name:     r.email.split('@')[0],       // hiển thị phần trước @ làm tên
    email:    r.email,
    role:     r.roleName,
    jobCount: r.assignedJobCount,
    avatar:   '',
    initials: r.email.split('@')[0].slice(0, 2).toUpperCase(),
    online:   r.status === 'active',
    avatarBg: 'secondary',
    avatarLarge: '',
  }))
)

// Khi đổi tab → luôn re-fetch và reset selection
watch(activeTab, async (tab) => {
  if (tab === 'job') {
    activeJobId.value = null
    await assignmentStore.fetchJobPostsWithAssignment({ size: 50 })
    const firstJob = assignmentStore.jobPostsWithAssignment?.result?.[0] as any
    if (firstJob) activeJobId.value = firstJob.id
  } else {
    activeMemberId.value = null
    await assignmentStore.fetchRecruiters({ size: 50 })
    const first = assignmentStore.recruiters?.result?.[0]
    if (first) {
      activeMemberId.value = first.userId
      await assignmentStore.fetchJobPostsByRecruiter(first.userId, { page: 0, size: 20 })
    }
  }
})

// Map job list từ API sang shape phù hợp với JobAssignmentItem
// Lưu ý: BE trả về {id, title, status, assignment} - không phải jobPostId/jobPostTitle
const jobList = computed(() =>
  (assignmentStore.jobPostsWithAssignment?.result || []).map((item: any) => ({
    id:         item.id,
    title:      item.title,
    code:       `#JOB-${item.id}`,
    status:     item.status,
    assignment: item.assignment ?? null,   // null nếu chưa phân công
  }))
)

const activeJob = computed(() => jobList.value.find(j => j.id === activeJobId.value))
const activeMember = computed(() => members.value.find(m => m.id === activeMemberId.value))

function handleConfirmAssign() {
  showJobAssignModal.value = false
  refreshCurrentTab()
}

function handleConfirmAssignJob() {
  showJobAssignModal.value = false
  refreshCurrentTab()
}

/** Re-fetch dữ liệu theo tab đang active, sau đó chọn lại item đang active */
async function refreshCurrentTab() {
  if (activeTab.value === 'member') {
    const currentMemberId = activeMemberId.value
    await assignmentStore.fetchRecruiters({ size: 50 })
    // Giữ selection hiện tại, chỉ reload bảng tiếp theo
    if (currentMemberId) {
      await assignmentStore.fetchJobPostsByRecruiter(currentMemberId, { page: 0, size: 20 })
    }
  } else {
    const currentJobId = activeJobId.value
    await assignmentStore.fetchJobPostsWithAssignment({ size: 50 })
    // Giữ selection nếu tin vẫn tồn tại
    if (currentJobId) {
      const still = assignmentStore.jobPostsWithAssignment?.result?.find((j: any) => j.id === currentJobId)
      if (!still) activeJobId.value = (assignmentStore.jobPostsWithAssignment?.result?.[0] as any)?.id ?? null
    }
  }
}

async function handleRevoke(payload: { assignmentId: number; jobPostId: number }) {
  try {
    await assignmentStore.revokeAssignment({ jobPostId: payload.jobPostId })
    toast.success('Gỡ phân công thành công!')
    await refreshCurrentTab()
  } catch (err: any) {
    const msg = err?.response?.data?.message || 'Không thể gỡ phân công. Vui lòng thử lại.'
    toast.error('Gỡ phân công thất bại', msg)
  } finally {
    memberTableRef.value?.clearRevoking()
  }
}

/** Mở modal đổi người từ nút swap_horiz trong bảng */
function handleOpenReassign(payload: { jobPostId: number; jobTitle: string; currentUserId: number }) {
  reassigningJob.value = {
    id: payload.jobPostId,
    title: payload.jobTitle,
    assignedRecruiter: {
      userId: payload.currentUserId,
      email: activeMember.value?.email ?? '',
    },
  }
  showReassignModal.value = true
}

async function handleReassignSuccess() {
  showReassignModal.value = false
  await refreshCurrentTab()
}

async function handleSearch(keyword: string) {
  if (activeTab.value === 'member') {
    await assignmentStore.fetchRecruiters({ keyword: keyword || undefined, size: 50 })
    // Auto-select lại người đầu tiên sau khi search
    const first = assignmentStore.recruiters?.result?.[0]
    activeMemberId.value = first?.userId ?? null
    if (first) assignmentStore.fetchJobPostsByRecruiter(first.userId, { page: 0, size: 20 })
  } else {
    await assignmentStore.fetchJobPostsWithAssignment({ keyword: keyword || undefined, size: 50 })
    // Auto-select lại tin đầu tiên sau khi search
    const firstJob = assignmentStore.jobPostsWithAssignment?.result?.[0] as any
    activeJobId.value = firstJob?.id ?? null
  }
}
</script>

<style scoped>
.assignment-page {
  display: flex; flex-direction: column;
  height: calc(100vh - 64px); overflow: hidden;
}
.assignment-page__header {
   flex-shrink: 0;
}
.assignment-page__title { font-size: 1.875rem; font-weight: 800; color: #0f172a; margin-bottom: 4px; }
.assignment-page__sub { font-size: 1rem; color: #64748b; }

.assignment-page__body {
  flex: 1; display: flex; gap: 1.5rem; overflow: hidden;
 
}

.assignment-page__left {
  width: 35%; flex-shrink: 0;
  background: #fff; border-radius: 0.75rem;
  border: 1px solid rgba(226,232,240,0.3);
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  display: flex; flex-direction: column; overflow: hidden;
}
.assignment-page__list { flex: 1; overflow-y: auto; padding: 8px; }
.assignment-page__list::-webkit-scrollbar { width: 4px; }
.assignment-page__list::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 999px; }

.assignment-page__right {
  flex: 1; display: flex; flex-direction: column; gap: 1.5rem;
  overflow-y: auto; min-width: 0;
}
.assignment-page__right::-webkit-scrollbar { width: 4px; }
.assignment-page__right::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 999px; }
</style>