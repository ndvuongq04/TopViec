<template>
  <main class="flex flex-col flex-1 gap-6 min-w-0 pb-20 max-w-[1440px] mx-auto w-full px-4 md:px-10 py-6">
    <header class="interviews-page__header mt-2">
      <h1 class="interviews-page__title">Lịch trình phỏng vấn</h1>
      <p class="interviews-page__subtitle">
        Theo dõi các buổi phỏng vấn sắp tới và xem lại lịch sử.
      </p>
    </header>

    <div v-if="applicationStore.loading" class="interviews-page__loading">
      Đang tải...
    </div>

    <div v-else-if="processes.length === 0" class="interviews-page__empty">
      Không có đơn ứng tuyển nào đang trong giai đoạn phỏng vấn.
    </div>

    <div v-else class="interviews-page__list">
      <InterviewProcessCard
        v-for="process in processes"
        :key="process.id"
        :process="process"
        :is-expanded="expandedId === process.id"
        @toggle="toggleExpand(process.id)"
        @showHistory="openHistory(process)"
        @showDetail="openDetail"
      />
    </div>

    <InterviewFab />

    <!-- Detail Modal -->
    <InterviewDetailModal
      :visible="isDetailModalOpen"
      :round="selectedRound"
      @close="isDetailModalOpen = false"
    @confirmed="onRoundConfirmed"
    />

    <!-- History Modal -->
    <InterviewHistoryModal
      :visible="isHistoryModalOpen"
      :process="selectedProcess"
      @close="isHistoryModalOpen = false"
    />
  </main>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import InterviewProcessCard from '@/components/candidate/interviews/Interviewprocesscard.vue'
import InterviewFab from '@/components/candidate/interviews/Interviewfab.vue'
import InterviewHistoryModal from '@/components/candidate/interviews/Interviewhistorymodal.vue'
import InterviewDetailModal from '@/components/candidate/interviews/Interviewdetailmodal.vue'
import { useApplicationStore } from '@/stores/application.store'
import { usePublicInterviewStore } from '@/stores/publicInterview.store'
import type { ResInterviewScheduleDTO } from '@/types/interview.types'

const applicationStore = useApplicationStore()
const publicInterviewStore = usePublicInterviewStore()

// Cache rounds đã load, key là applicationId
const interviewsMap = ref<Record<number, any[]>>({})

function mapToRound(interview: ResInterviewScheduleDTO) {
  const statusMap: Record<string, string> = {
    pending: 'PENDING',
    scheduled: 'PENDING_CONFIRMATION',
    confirmed: 'CONFIRMED',
    completed: 'COMPLETED',
    cancelled: 'CANCELLED',
    no_show: 'CANCELLED',
  }

  const modeMap: Record<string, string> = {
    online: 'ONLINE',
    onsite: 'ONSITE',
    phone: 'PHONE',
  }

  let scheduledDate: string | undefined
  let scheduledTime: string | undefined
  if (interview.scheduledAt) {
    const d = new Date(interview.scheduledAt)
    scheduledDate = `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}`
    scheduledTime = `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
  }

  return {
    id: interview.id,
    roundId: interview.roundId,
    roundNumber: interview.roundNumber,
    title: interview.roundName,
    status: statusMap[interview.status] ?? interview.status.toUpperCase(),
    scheduledDate,
    scheduledTime,
    mode: interview.interviewType ? (modeMap[interview.interviewType] ?? interview.interviewType.toUpperCase()) : undefined,
    location: interview.location,
    meetingLink: interview.meetingLink,
    interviewerNote: interview.interviewerNote,
  }
}

const processes = computed(() =>
  applicationStore.applications.map((app) => ({
    id: app.id,
    jobTitle: app.jobPosting?.title ?? 'Không rõ vị trí',
    companyName: app.jobPosting?.company.name ?? 'Không rõ công ty',
    companyLogo: app.jobPosting?.company.logoUrl,
    isBrandVerified: app.jobPosting?.company.isBrandVerified ?? false,
    status: app.status,
    rounds: interviewsMap.value[app.id] ?? [],
  }))
)

onMounted(() => {
  applicationStore.fetchMyApplicationsWithInterviews()
})

const expandedId = ref<number | null>(null)

const isHistoryModalOpen = ref(false)
const selectedProcess = ref<any>(null)

const isDetailModalOpen = ref(false)
const selectedRound = ref<any>(null)

const toggleExpand = async (id: number) => {
  if (expandedId.value === id) {
    expandedId.value = null
    return
  }
  expandedId.value = id

  // Chỉ fetch nếu chưa có cache
  if (!interviewsMap.value[id]) {
    try {
      await publicInterviewStore.fetchMyInterviews(id)
      interviewsMap.value[id] = publicInterviewStore.myInterviews.map(mapToRound)
    } catch {
      interviewsMap.value[id] = []
    }
  }
}

const openHistory = (process: any) => {
  selectedProcess.value = process
  isHistoryModalOpen.value = true
}

const openDetail = (round: any) => {
  selectedRound.value = round
  isDetailModalOpen.value = true
}

const onRoundConfirmed = (scheduleId: number) => {
  for (const appId in interviewsMap.value) {
    const rounds = interviewsMap.value[appId]
    const round = rounds.find((r) => r.id === scheduleId)
    if (round) {
      round.status = 'CONFIRMED'
      break
    }
  }
}
</script>

<style scoped>
.interviews-page__header {
  margin-bottom: 2.5rem;
}

.interviews-page__title {
  font-size: 1.875rem;
  font-weight: 800;
  color: #071b3b;
  letter-spacing: -0.025em;
  margin-bottom: 0.5rem;
}

.interviews-page__subtitle {
  color: #404752;
  font-weight: 500;
}

.interviews-page__list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.interviews-page__loading,
.interviews-page__empty {
  color: #707783;
  font-size: 0.875rem;
  padding: 2rem 0;
}
</style>
