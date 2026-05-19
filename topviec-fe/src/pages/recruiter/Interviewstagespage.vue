<template>
  <div class="setup-page">
    <!-- Breadcrumb -->
    <Breadcrumb :items="breadcrumbItems" :hide-home="true" />

    <!-- Page Header -->
    <div class="header-section">
      <div class="header-left">
        <div class="title-group">
          <h1 class="page-title">{{ jobTitle || 'Đang tải...' }}</h1>
          <span class="job-status-badge">Đang tuyển</span>
        </div>
        <p class="page-subtitle">Quản lý các vòng phỏng vấn cho tin tuyển dụng này.</p>
      </div>
      
      <div class="header-actions">
        <button class="btn-primary" @click="openAddModal">
          <span class="material-symbols-outlined">add_circle</span>
          Vòng mới
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="setup-page__placeholder">
      <span class="material-symbols-outlined animate-spin text-primary">circle_notifications</span>
      <p>Đang tải dữ liệu...</p>
    </div>

    <!-- List -->
    <InterviewStageList
      v-else
      :stages="mappedStages"
      @edit="openEditModal"
      @delete="confirmDelete"
      @add="openAddModal"
      @reorder="handleReorder"
    />

    <!-- Edit/Add Modal -->
    <GlobalModal
      :visible="isModalVisible"
      :title="modalType === 'add' ? 'Thêm vòng phỏng vấn mới' : 'Chỉnh sửa vòng phỏng vấn'"
      :subtitle="jobTitle"
      variant="primary"
      :icon="modalType === 'add' ? 'add_circle' : 'edit'"
      :confirm-text="modalType === 'add' ? 'Tạo vòng' : 'Lưu thay đổi'"
      :loading="isSaving"
      @close="closeModal"
      @confirm="handleSave"
    >
      <div class="modal-form">
        <div class="grid grid-cols-2 gap-4">
          <div class="form-group">
            <label class="form-label">Tên vòng phỏng vấn <span class="text-rose-500">*</span></label>
            <input
              v-model="form.roundName"
              type="text"
              placeholder="Ví dụ: Phỏng vấn kỹ thuật..."
              class="form-input"
              required
            />
          </div>

          <div class="form-group">
            <label class="form-label">Thời lượng dự kiến (phút)</label>
            <input
              v-model.number="form.expectedDuration"
              type="number"
              placeholder="Ví dụ: 45, 60..."
              class="form-input"
            />
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">Tiêu chí đánh giá / Mô tả</label>
          <textarea
            v-model="form.description"
            rows="3"
            placeholder="Mô tả ngắn gọn các tiêu chí cần đánh giá trong vòng này..."
            class="form-textarea"
          ></textarea>
        </div>

        <div class="form-group">
          <div class="flex items-center gap-2">
            <input
              id="is-final"
              v-model="form.isFinal"
              type="checkbox"
              class="form-checkbox"
              disabled
            />
            <label for="is-final" class="form-label mb-0 cursor-pointer">
              Đây là vòng phỏng vấn cuối cùng
            </label>
          </div>

        </div>

        <!-- Interviewers Section -->
        <div class="form-group">
          <label class="form-label">Người phỏng vấn mặc định</label>
          <div v-for="(interviewer, index) in form.interviewers" :key="index" class="interviewer-item">
            <div class="grid grid-cols-2 gap-2 w-full">
              <input
                v-model="interviewer.name"
                placeholder="Tên"
                class="form-input text-sm"
              />
              <input
                v-model="interviewer.email"
                placeholder="Email (không bắt buộc)"
                class="form-input text-sm"
              />
            </div>
            <button
              v-if="form.interviewers.length > 1"
              class="remove-interviewer"
              @click="removeInterviewer(index)"
            >
              <span class="material-symbols-outlined">close</span>
            </button>
          </div>
          <button class="add-interviewer-btn" @click="addInterviewer">
            <span class="material-symbols-outlined">add</span>
            Thêm người phỏng vấn
          </button>
        </div>
      </div>
    </GlobalModal>

    <!-- Delete Confirmation Modal -->
    <GlobalModal
      :visible="isDeleteModalVisible"
      title="Xoá vòng phỏng vấn"
      subtitle="Hành động này không thể hoàn tác"
      variant="primary"
      icon="delete"
      confirm-text="Xác nhận xoá"
      confirm-icon="delete_forever"
      :loading="isDeleting"
      @close="isDeleteModalVisible = false"
      @confirm="handleDelete"
    >
      <p class="text-slate-600">
        Bạn có chắc chắn muốn xoá vòng phỏng vấn này không? 
        Lưu ý: Chỉ có thể xoá nếu chưa có ứng viên nào tham gia vào vòng này.
      </p>
    </GlobalModal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import Breadcrumb from '@/components/ui/Breadcrumb.vue'
import GlobalModal from '@/components/ui/GlobalModal.vue'
import InterviewStageList from '@/components/recruiter/interviews/config/Interviewstagelist.vue'
import { publicJobPostingService } from '@/services/jobPosting.service'
import employerInterviewService from '@/services/employerInterview.service'
import { useToast } from '@/composables/useToast'
import type { ResInterviewRoundDTO, InterviewerDTO } from '@/types/interview.types'

const route = useRoute()
const toast = useToast()
const jobPostId = Number(route.params.id)

const breadcrumbItems = [
  { label: 'Quản lý phỏng vấn', to: '/recruiter/interviews' },
  { label: 'Cấu hình vòng phỏng vấn' },
]

// ─── State ────────────────────────────────────────────────────────────────────
const jobTitle = ref('')
const rounds = ref<ResInterviewRoundDTO[]>([])
const isLoading = ref(false)
const isSaving = ref(false)
const isDeleting = ref(false)

// Modal state
const isModalVisible = ref(false)
const modalType = ref<'add' | 'edit'>('add')
const editingRoundId = ref<number | null>(null)
const isDeleteModalVisible = ref(false)
const roundToDeleteId = ref<number | null>(null)

const form = ref({
  roundName: '',
  description: '',
  expectedDuration: 30,
  isFinal: true,
  interviewers: [{ name: '', email: '', phone: '' }] as InterviewerDTO[]
})

// ─── Computed ─────────────────────────────────────────────────────────────────
const mappedStages = computed(() => {
  return rounds.value.map(r => ({
    id: r.id,
    order: r.roundNumber,
    name: r.roundName,
    criteria: r.description || 'Chưa có mô tả',
    duration: r.expectedDuration ? `${r.expectedDuration} phút` : 'N/A',
    isAutomated: r.interviewers.length === 0,
    interviewers: r.interviewers || [],
    isFinal: r.isFinal
  }))
})

// ─── API Calls ────────────────────────────────────────────────────────────────
async function fetchData() {
  isLoading.value = true
  try {
    const [job, roundsData] = await Promise.all([
      publicJobPostingService.getById(jobPostId),
      employerInterviewService.getRounds(jobPostId)
    ])
    jobTitle.value = job.title
    rounds.value = roundsData
  } catch (err) {
    console.error('Failed to fetch rounds:', err)
    toast.error('Lỗi', 'Không thể tải dữ liệu vòng phỏng vấn')
  } finally {
    isLoading.value = false
  }
}

async function handleSave() {
  if (!form.value.roundName.trim()) {
    toast.error('Lỗi', 'Vui lòng nhập tên vòng phỏng vấn')
    return
  }

  isSaving.value = true
  try {
    // Filter out empty interviewers
    const filteredInterviewers = form.value.interviewers.filter(i => i.name.trim())
    const payload = {
      ...form.value,
      interviewers: filteredInterviewers
    }

    if (modalType.value === 'add') {
      await employerInterviewService.createRound(jobPostId, payload)
      toast.success('Thành công', 'Đã tạo vòng phỏng vấn mới')
    } else if (editingRoundId.value) {
      await employerInterviewService.updateRound(editingRoundId.value, payload)
      toast.success('Thành công', 'Đã cập nhật vòng phỏng vấn')
    }
    
    closeModal()
    fetchData()
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Đã có lỗi xảy ra'
    toast.error('Lỗi', msg)
  } finally {
    isSaving.value = false
  }
}

async function handleDelete() {
  if (!roundToDeleteId.value) return
  
  isDeleting.value = true
  try {
    await employerInterviewService.deleteRound(roundToDeleteId.value)
    toast.success('Thành công', 'Đã xoá vòng phỏng vấn')
    isDeleteModalVisible.value = false
    fetchData()
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Không thể xoá vòng này (có thể đã có ứng viên tham gia)'
    toast.error('Lỗi', msg)
  } finally {
    isDeleting.value = false
  }
}

// ─── Handlers ─────────────────────────────────────────────────────────────────
function openAddModal() {
  modalType.value = 'add'
  editingRoundId.value = null
  form.value = {
    roundName: '',
    description: '',
    expectedDuration: 30,
    isFinal: true,
    interviewers: [{ name: '', email: '', phone: '' }]
  }
  isModalVisible.value = true
}

function openEditModal(stage: any) {
  const originalRound = rounds.value.find(r => r.id === stage.id)
  if (!originalRound) return

  modalType.value = 'edit'
  editingRoundId.value = stage.id
  form.value = {
    roundName: originalRound.roundName,
    description: originalRound.description || '',
    expectedDuration: originalRound.expectedDuration || 30,
    isFinal: originalRound.isFinal,
    interviewers: originalRound.interviewers.length > 0 
      ? originalRound.interviewers.map(i => ({ name: i.name, email: i.email, phone: i.phone }))
      : [{ name: '', email: '', phone: '' }]
  }
  isModalVisible.value = true
}

function closeModal() {
  isModalVisible.value = false
}

function confirmDelete(id: number) {
  roundToDeleteId.value = id
  isDeleteModalVisible.value = true
}

function addInterviewer() {
  form.value.interviewers.push({ name: '', email: '', phone: '' })
}

function removeInterviewer(index: number) {
  form.value.interviewers.splice(index, 1)
}

function handleReorder(reordered: any[]) {
  // UI reorder logic - currently frontend only as BE doesn't have reorder API
  // We just follow the roundNumber assigned by BE on refresh
  console.log('Reorder requested - Persisting custom order is not yet supported by Backend API')
}

onMounted(fetchData)
</script>

<style scoped>
.setup-page {
  margin: 0 auto;
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 2rem;
  font-family: 'Manrope', sans-serif;
}

/* Header Styles */
.header-section {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 1rem;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.title-group {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.page-title {
  font-size: 1.875rem;
  font-weight: 800;
  letter-spacing: -0.03em;
  color: #0f172a;
  margin: 0;
  line-height: 1.1;
}

.job-status-badge {
  padding: 0.25rem 0.75rem;
  background: #f0fdf4;
  color: #16a34a;
  border: 1px solid #dcfce7;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 700;
}

.page-subtitle {
  font-size: 0.875rem;
  color: #64748b;
  margin: 0;
}

.btn-primary {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: #4B9AF6;
  color: #fff;
  font-weight: 700;
  font-size: 1rem;
  border: none;
  border-radius: 0.5rem;
  padding: 0.625rem 1.25rem;
  cursor: pointer;
  box-shadow: 0 4px 14px rgba(75, 154, 246, 0.25);
  transition: all 0.2s;
  white-space: nowrap;
}

.btn-primary:hover {
  background: #3b8de0;
  box-shadow: 0 6px 20px rgba(75, 154, 246, 0.35);
  transform: translateY(-1px);
}

/* Modal Form Styles */
.modal-form {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-label {
  font-size: 0.875rem;
  font-weight: 700;
  color: #475569;
  margin-bottom: 0.5rem;
}

.form-input, .form-textarea {
  padding: 0.625rem 1rem;
  border: 1.5px solid #e2e8f0;
  border-radius: 0.5rem;
  font-size: 0.9375rem;
  color: #1e293b;
  transition: border-color 0.2s, box-shadow 0.2s;
  font-family: inherit;
}

.form-input:focus, .form-textarea:focus {
  outline: none;
  border-color: #4B9AF6;
  box-shadow: 0 0 0 4px rgba(75, 154, 246, 0.1);
}

.form-textarea {
  resize: vertical;
}

.form-checkbox {
  width: 1.125rem;
  height: 1.125rem;
  border-radius: 0.25rem;
  border: 1.5px solid #e2e8f0;
  cursor: pointer;
}

.interviewer-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.remove-interviewer {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 2rem;
  border-radius: 50%;
  border: none;
  background: transparent;
  color: #94a3b8;
  cursor: pointer;
  transition: all 0.2s;
}

.remove-interviewer:hover {
  background: #fee2e2;
  color: #ef4444;
}

.add-interviewer-btn {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  background: transparent;
  border: none;
  color: #4B9AF6;
  font-size: 0.875rem;
  font-weight: 700;
  cursor: pointer;
  padding: 0;
  margin-top: 0.25rem;
}

.animate-spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.setup-page__placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  padding: 6rem 2rem;
  color: #94a3b8;
  text-align: center;
}

.setup-page__placeholder .material-symbols-outlined {
  font-size: 2.5rem;
}

.setup-page__placeholder p {
  font-size: 1rem;
  font-weight: 600;
  margin: 0;
}
</style>