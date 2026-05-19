<template>
  <div class="overdue-page">
    <!-- Breadcrumb & Header -->
    <div class="overdue-page__header">
      <div>
        <Breadcrumb 
          :items="breadcrumbItems" 
          hide-home
          class="overdue-page__breadcrumb-comp"
        />
        <h2 class="overdue-page__title">Quản lý lịch phỏng vấn</h2>
        <p class="overdue-page__subtitle">Ứng viên quá hạn phản hồi &amp; chưa xác nhận lịch.</p>
      </div>
    </div>

    <!-- Stats -->
    <OverdueStatsGrid :total-overdue="overdueApplications.length" />

    <!-- Table -->
    <OverdueTable
      :items="overdueApplications"
      :loading="loading"
      @extend="handleExtendDeadline"
      @force-schedule="handleForceSchedule"
    />

    <!-- Modals -->
    <ExtendDeadlineModal
      :visible="isExtendModalOpen"
      :candidate-name="selectedApp?.candidateName"
      :current-deadline="selectedApp?.reminderDeadline"
      :loading="isExtending"
      @close="isExtendModalOpen = false"
      @confirm="confirmExtend"
    />

    <ForceScheduleModal
      :visible="isForceScheduleModalOpen"
      :candidate-name="selectedApp?.candidateName"
      :loading="isForceScheduling"
      @close="isForceScheduleModalOpen = false"
      @confirm="confirmForceSchedule"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useToast } from '@/composables/useToast'
import employerInterviewService from '@/services/employerInterview.service'
import type { ResOverdueApplicationDTO, ReqForceScheduleDTO } from '@/types/interview.types'
import OverdueStatsGrid from '@/components/recruiter/interviews/interview-overdue/OverdueStatsGrid.vue'
import OverdueTable from '@/components/recruiter/interviews/interview-overdue/OverdueTable.vue'
import Breadcrumb from '@/components/ui/Breadcrumb.vue'
import ExtendDeadlineModal from '@/components/recruiter/interviews/interview-overdue/ExtendDeadlineModal.vue'
import ForceScheduleModal from '@/components/recruiter/interviews/interview-overdue/ForceScheduleModal.vue'

const breadcrumbItems = [
  { label: 'Thiết lập phỏng vấn', to: '/recruiter/interviews' },
  { label: 'Ứng viên quá hạn' }
]

const route = useRoute()
const toast = useToast()
const jobPostId = Number(route.params.id)

// --- State ---
const loading = ref(false)
const overdueApplications = ref<ResOverdueApplicationDTO[]>([])

const isExtendModalOpen = ref(false)
const isForceScheduleModalOpen = ref(false)
const selectedAppId = ref<number | null>(null)
const selectedApp = computed(() => overdueApplications.value.find(a => a.applicationId === selectedAppId.value))
const isExtending = ref(false)
const isForceScheduling = ref(false)

// --- Fetch overdue ---
async function fetchOverdue() {
  loading.value = true
  try {
    overdueApplications.value = await employerInterviewService.getOverdueApplications(jobPostId)
  } catch (e: any) {
    toast.error('Lỗi', e?.response?.data?.message ?? 'Không thể tải danh sách ứng viên quá hạn.')
  } finally {
    loading.value = false
  }
}

// --- Lifecycle ---
onMounted(fetchOverdue)

// --- Actions ---
function handleExtendDeadline(applicationId: number) {
  selectedAppId.value = applicationId
  isExtendModalOpen.value = true
}

async function confirmExtend(days: number) {
  if (!selectedApp.value) return

  isExtending.value = true
  try {
    await employerInterviewService.extendDeadline(selectedApp.value.applicationId, { extendDays: days })
    toast.success('Thành công', `Đã gia hạn thêm ${days} ngày cho ứng viên.`)
    isExtendModalOpen.value = false
    fetchOverdue()
  } catch (e: any) {
    toast.error('Lỗi', e?.response?.data?.message ?? 'Không thể gia hạn thời gian.')
  } finally {
    isExtending.value = false
  }
}

function handleForceSchedule(applicationId: number) {
  selectedAppId.value = applicationId
  isForceScheduleModalOpen.value = true
}

async function confirmForceSchedule(form: { date: string; time: string; interviewType: 'online' | 'onsite'; meetingLink: string; location: string }) {
  if (!selectedApp.value) return

  const payload: ReqForceScheduleDTO = {
    scheduledAt: new Date(`${form.date}T${form.time}:00`).toISOString(),
    interviewType: form.interviewType,
    meetingLink: form.interviewType === 'online' ? form.meetingLink : undefined,
    location: form.interviewType === 'onsite' ? form.location : undefined,
  }

  isForceScheduling.value = true
  try {
    await employerInterviewService.forceSchedule(selectedApp.value.applicationId, payload)
    toast.success('Thành công', `Đã đặt lịch phỏng vấn hộ cho ${selectedApp.value.candidateName}.`)
    isForceScheduleModalOpen.value = false
    fetchOverdue()
  } catch (e: any) {
    toast.error('Lỗi', e?.response?.data?.message ?? 'Không thể đặt lịch phỏng vấn.')
  } finally {
    isForceScheduling.value = false
  }
}
</script>

<style scoped>
.overdue-page {
  padding: 1.5rem 0;
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

/* Header */
.overdue-page__header {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

@media (min-width: 768px) {
  .overdue-page__header {
    flex-direction: row;
    align-items: flex-end;
    justify-content: space-between;
  }
}

.overdue-page__breadcrumb-comp {
  margin-bottom: 0.75rem;
}

.overdue-page__title {
  font-size: 1.875rem;
  font-weight: 800;
  letter-spacing: -0.03em;
  color: #0f172a;
  margin: 0 0 0.25rem;
}

.overdue-page__subtitle {
  font-size: 1rem;
  color: #64748b;
  margin: 0;
}

/* Job selector */
.overdue-page__job-select {
  padding: 0.625rem 1.25rem;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 0.75rem;
  font-size: 1rem;
  font-weight: 700;
  color: #475569;
  font-family: inherit;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  outline: none;
  min-width: 15rem;
  cursor: pointer;
  transition: box-shadow 0.18s;
}

.overdue-page__job-select:focus {
  box-shadow: 0 0 0 2px rgba(75,154,246,0.2);
}
</style>