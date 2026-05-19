<template>
  <main class="detail-page">
    <!-- Loading full page -->
    <div v-if="loading" class="page-loading">
      <span class="material-symbols-outlined loading-spin">progress_activity</span>
    </div>

    <template v-else-if="application">
      <!-- Left: PDF CV -->
      <CvPreviewPanel
        :candidate-name="application.candidateName"
        :cv-preview-url="application.cvPreviewUrl"
        :cv-pdf-url="application.cvPdfUrl"
        :cv-file-url="application.cvFileUrl"
      />

      <!-- Right: Evaluation + Activity -->
      <aside class="detail-page__sidebar">
        <CandidateEvaluationPanel
          :initial-rating="application.recruiterRating"
          :initial-note="application.recruiterNote"
          :initial-tags="application.recruiterTags"
          :initial-status="application.status"
          @save="handleSave"
          @invite-interview="handleInviteInterview"
          @save-to-talent-pool="openTalentPoolModal"
        />
        <CandidateActivityLog :activities="activityLog" />
      </aside>
    </template>

    <SaveToTalentPoolModal
      :visible="showTalentPoolModal"
      :candidate-name="application?.candidateName"
      :loading="savingToPool"
      @close="showTalentPoolModal = false"
      @confirm="handleSaveToTalentPool"
    />
  </main>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import CvPreviewPanel from '@/components/recruiter/application/Cvpreviewpanel.vue'
import CandidateEvaluationPanel from '@/components/recruiter/application/Candidateevaluationpanel.vue'
import CandidateActivityLog from '@/components/recruiter/application/Candidateactivitylog.vue'
import employerApplicationService from '@/services/employerApplication.service'
import employerTalentPoolService from '@/services/employerTalentPool.service'
import SaveToTalentPoolModal from '@/components/recruiter/talent-pool/SaveToTalentPoolModal.vue'
import { useToast } from '@/composables/useToast'
import type { ResEmployerApplicationDTO } from '@/types/employerApplication.types'

const route  = useRoute()
const toast  = useToast()

const loading     = ref(true)
const application = ref<ResEmployerApplicationDTO | null>(null)

const applicationId = computed(() => Number(route.params.applicationId))

// ── Lịch sử hoạt động từ dữ liệu API ──
const activityLog = computed(() => {
  if (!application.value) return []
  const items: { text: string; time: string }[] = []

  items.push({
    text: 'Ứng viên đã nộp hồ sơ',
    time: formatDateTime(application.value.createdAt),
  })

  if (application.value.viewedAt) {
    items.push({
      text: 'Nhà tuyển dụng đã xem hồ sơ',
      time: formatDateTime(application.value.viewedAt),
    })
  }

  return items
})

function formatDateTime(iso: string): string {
  if (!iso) return ''
  const d = new Date(iso)
  return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')} - ${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
}

async function fetchApplication() {
  loading.value = true
  try {
    application.value = await employerApplicationService.getApplicationDetailByEmployer(applicationId.value)
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Không thể tải thông tin hồ sơ'
    toast.error('Lỗi tải hồ sơ', typeof msg === 'string' ? msg : msg?.[0])
  } finally {
    loading.value = false
  }
}

async function handleSave(data: { status: string; rating: number; note: string; tags: { label: string }[] }) {
  try {
    const updated = await employerApplicationService.updateApplication(applicationId.value, {
      status: data.status || undefined,
      rating: data.rating || undefined,
      note:   data.note   || undefined,
      tags:   data.tags.length ? data.tags.map(t => t.label).join(',') : undefined,
    })
    application.value = updated
    toast.success('Đã lưu đánh giá')
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Lưu đánh giá thất bại'
    toast.error('Lưu thất bại', typeof msg === 'string' ? msg : Object.values(msg)[0] as string)
  }
}

function handleInviteInterview() {
  toast.info('Tính năng đang phát triển')
}

const showTalentPoolModal = ref(false)
const savingToPool        = ref(false)

function openTalentPoolModal() {
  showTalentPoolModal.value = true
}

async function handleSaveToTalentPool(note: string) {
  if (!application.value?.candidateUserId) return
  savingToPool.value = true
  try {
    await employerTalentPoolService.addToTalentPool({
      candidateUserId: application.value.candidateUserId,
      source: 'REVIEW_CV',
      note: note || undefined,
    })
    showTalentPoolModal.value = false
    toast.success('Đã lưu vào Talent Pool!', `Ứng viên ${application.value.candidateName} đã được thêm vào Talent Pool.`)
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Không thể lưu vào Talent Pool. Vui lòng thử lại.'
    toast.error('Lỗi', typeof msg === 'string' ? msg : msg?.[0])
  } finally {
    savingToPool.value = false
  }
}

onMounted(fetchApplication)
</script>

<style scoped>
.detail-page {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  flex: 1;
  overflow-y: auto;
}

@media (min-width: 768px) {
  .detail-page { flex-direction: row; }
}

.detail-page__sidebar {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

@media (min-width: 768px) {
  .detail-page__sidebar {
    width: 400px;
    flex-shrink: 0;
  }
}

/* Loading */
.page-loading {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

@keyframes spin { to { transform: rotate(360deg); } }
.loading-spin {
  font-size: 2.5rem !important;
  color: #4B9AF6;
  animation: spin 0.8s linear infinite;
}
</style>
