<template>
  <div id="complaint-appeal-section" class="cd-response-stack">
    <section v-if="!currentAppeal" class="cd-form">
      <div class="cd-form__accent"></div>
      <div class="cd-form__group-header">
        <span class="material-symbols-outlined cd-form__group-icon cd-form__group-icon--red">gavel</span>
        <div>
          <h3 class="cd-form__title">Gửi giải trình & Bằng chứng</h3>
          <p class="cd-form__subtitle">Form này luôn hiển thị cho nhà tuyển dụng</p>
        </div>
      </div>

      <div class="cd-form__field">
        <label class="cd-form__label" for="explanation">
          Giải trình của bạn <span class="cd-form__required">*</span>
        </label>
        <textarea
          id="explanation"
          v-model="form.explanation"
          class="cd-form__textarea"
          placeholder="Cung cấp thông tin chi tiết về bối cảnh và nội dung cần giải trình..."
          rows="4"
        ></textarea>
      </div>

      <div class="cd-form__field">
        <label class="cd-form__label">Đính kèm bằng chứng</label>
        <div class="cd-form__upload" @click="triggerUpload" @dragover.prevent @drop.prevent="onDrop">
          <span class="material-symbols-outlined cd-form__upload-icon">cloud_upload</span>
          <div class="cd-form__upload-text">
            <span class="cd-form__upload-link">Tải tệp lên</span>
            <span> hoặc kéo thả vào đây</span>
          </div>
          <p class="cd-form__upload-hint">PNG, JPG, PDF tối đa 10MB</p>
          <input ref="fileInput" class="cd-form__file-input" type="file" multiple @change="onFileChange" />
        </div>

        <div v-if="uploadedFiles.length" class="cd-form__file-list">
          <div v-for="f in uploadedFiles" :key="f.name" class="cd-form__file-item">
            <span class="material-symbols-outlined">attach_file</span>
            <span class="cd-form__file-name">{{ f.name }}</span>
            <button class="cd-form__file-remove" type="button" @click="removeFile(f.name)">
              <span class="material-symbols-outlined">close</span>
            </button>
          </div>
        </div>
      </div>

      <label class="cd-form__confirm">
        <input v-model="form.confirmed" class="cd-form__checkbox" type="checkbox" />
        <span>Tôi cam kết những thông tin và tài liệu cung cấp là trung thực.</span>
      </label>

      <div class="cd-form__footer">
        <button
          class="cd-form__btn cd-form__btn--primary"
          :disabled="submitting || !canSubmitAppeal"
          @click="onSubmitAppeal"
        >
          <span v-if="submitting" class="material-symbols-outlined spin">autorenew</span>
          <span v-else class="material-symbols-outlined">send</span>
          {{ submitting ? 'Đang gửi...' : 'Gửi giải trình' }}
        </button>
      </div>
    </section>

    <section v-else class="cd-form cd-form--submitted">
      <div class="cd-form__accent cd-form__accent--amber"></div>
      <div class="cd-form__group-header">
        <span class="material-symbols-outlined cd-form__group-icon cd-form__group-icon--amber">task_alt</span>
        <div>
          <h3 class="cd-form__title">Giải trình đã gửi</h3>
          <p class="cd-form__subtitle">Bạn đã gửi giải trình cho khiếu nại này</p>
        </div>
      </div>

      <div v-if="currentAppeal" class="cd-form__submitted-content">
        <div class="cd-form__submitted-grid">
          <div>
            <p class="cd-form__label">Trạng thái</p>
            <p class="cd-form__submitted-meta">{{ appealStatusLabel }}</p>
          </div>
          <div>
            <p class="cd-form__label">Mã khiếu nại</p>
            <p class="cd-form__submitted-meta">{{ currentAppeal.complaint.reportCode }}</p>
          </div>
        </div>

        <div class="cd-form__field">
          <p class="cd-form__label">Nội dung đã gửi</p>
          <blockquote class="cd-form__submitted-quote">{{ currentAppeal.content }}</blockquote>
          <p class="cd-form__submitted-meta">
            Gửi lúc {{ formatDateTime(currentAppeal.createdAt) }}
          </p>
        </div>

        <div class="cd-form__field">
          <p class="cd-form__label">Bằng chứng đính kèm</p>
          <p class="cd-form__submitted-meta">
            API hiện tại chưa trả về danh sách file bằng chứng.
          </p>
        </div>

        <div v-if="currentAppeal.adminNote" class="cd-form__field">
          <p class="cd-form__label">Ghi chú admin</p>
          <blockquote class="cd-form__submitted-quote">{{ currentAppeal.adminNote }}</blockquote>
        </div>
      </div>

    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { APPEAL_STATUS_OPTIONS } from '@/constants/complaints.constants'
import { useToast } from '@/composables/useToast'
import { useEmployerAppealStore } from '@/stores/employerAppeal.store'
import { useEmployerReportStore } from '@/stores/employerReport.store'

const toast = useToast()
const reportStore = useEmployerReportStore()
const appealStore = useEmployerAppealStore()

const form = reactive({ explanation: '', confirmed: false })
const uploadedFiles = ref<{ name: string }[]>([])
const fileInput = ref<HTMLInputElement>()
const submitting = ref(false)

const currentReport = computed(() => reportStore.currentReport)
const currentAppeal = computed(() => appealStore.currentAppeal)

const appealStatusLabel = computed(() => {
  const status = currentAppeal.value?.status
  if (!status) return '-'
  return APPEAL_STATUS_OPTIONS.find((item) => item.value === status)?.label ?? status
})

const canSubmitAppeal = computed(() =>
  form.confirmed && Boolean(form.explanation.trim()),
)

function formatDateTime(value?: string | null) {
  if (!value) return '-'
  return new Date(value).toLocaleString('vi-VN')
}

const triggerUpload = () => fileInput.value?.click()

const onFileChange = (e: Event) => {
  const files = (e.target as HTMLInputElement).files
  if (!files) return
  Array.from(files).forEach((f) => {
    if (!uploadedFiles.value.find((x) => x.name === f.name)) {
      uploadedFiles.value.push({ name: f.name })
    }
  })
}

const onDrop = (e: DragEvent) => {
  Array.from(e.dataTransfer?.files ?? []).forEach((f) => {
    if (!uploadedFiles.value.find((x) => x.name === f.name)) {
      uploadedFiles.value.push({ name: f.name })
    }
  })
}

const removeFile = (name: string) => {
  uploadedFiles.value = uploadedFiles.value.filter((f) => f.name !== name)
}

async function onSubmitAppeal() {
  const report = currentReport.value
  if (!report) {
    toast.error('Lỗi', 'Không tìm thấy thông tin báo cáo.')
    return
  }
  if (!canSubmitAppeal.value) {
    toast.warning('Thiếu nội dung', 'Vui lòng nhập giải trình và xác nhận trước khi gửi.')
    return
  }

  submitting.value = true
  try {
    await appealStore.createAppeal(report.id, { content: form.explanation.trim() })
    form.explanation = ''
    form.confirmed = false
    uploadedFiles.value = []
    await reportStore.fetchById(report.id)
    await appealStore.fetchCurrentAppeal(report.id)
    toast.success('Thành công', 'Đã gửi giải trình thành công.')
  } catch {
    toast.error('Lỗi', appealStore.error ?? 'Không thể gửi giải trình. Vui lòng thử lại.')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.cd-response-stack {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.cd-form {
  background: #fff;
  border-radius: 0.75rem;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(226, 232, 240, 0.2);
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  position: relative;
  overflow: hidden;
}

.cd-form__accent {
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: #4b9af6;
  border-radius: 4px 0 0 4px;
}

.cd-form__accent--amber {
  background: #d97706;
}

.cd-form__group-header {
  display: flex;
  align-items: flex-start;
  gap: 0.875rem;
}

.cd-form__group-icon {
  font-size: 1.75rem;
  flex-shrink: 0;
  margin-top: 2px;
}

.cd-form__group-icon--amber {
  color: #d97706;
}

.cd-form__group-icon--red {
  color: #dc2626;
}

.cd-form__title {
  font-size: 1.125rem;
  font-weight: 800;
  color: #0f172a;
  margin: 0 0 2px;
}

.cd-form__subtitle {
  font-size: 0.8rem;
  color: #64748b;
  font-weight: 600;
  margin: 0;
}

.cd-form__field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.cd-form__label {
  font-size: 0.875rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
}

.cd-form__required {
  color: #ef4444;
}

.cd-form__textarea {
  width: 100%;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 0.5rem;
  padding: 12px 16px;
  font-size: 0.875rem;
  font-family: 'Manrope', sans-serif;
  color: #0f172a;
  outline: none;
  resize: vertical;
  box-sizing: border-box;
  transition: all 0.15s;
  line-height: 1.6;
}

.cd-form__textarea:focus {
  border-color: #4b9af6;
  box-shadow: 0 0 0 2px rgba(75, 154, 246, 0.15);
}

.cd-form__textarea::placeholder {
  color: rgba(100, 116, 139, 0.5);
}

.cd-form__upload {
  border: 2px dashed #e2e8f0;
  border-radius: 0.5rem;
  background: #f8fafd;
  padding: 2rem 1rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s;
  text-align: center;
  position: relative;
}

.cd-form__upload:hover {
  background: #f1f4f9;
  border-color: #4b9af6;
}

.cd-form__upload-icon {
  font-size: 40px;
  color: #64748b;
  transition: color 0.15s;
}

.cd-form__upload:hover .cd-form__upload-icon {
  color: #4b9af6;
}

.cd-form__upload-text {
  font-size: 0.875rem;
  color: #0f172a;
}

.cd-form__upload-link {
  color: #4b9af6;
  font-weight: 700;
}

.cd-form__upload-hint {
  font-size: 0.75rem;
  color: #64748b;
}

.cd-form__file-input {
  display: none;
}

.cd-form__file-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.cd-form__file-item {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #f1f4f9;
  border-radius: 6px;
  padding: 8px 12px;
  font-size: 0.8125rem;
  color: #0f172a;
}

.cd-form__file-item .material-symbols-outlined {
  font-size: 16px;
  color: #64748b;
}

.cd-form__file-name {
  flex: 1;
}

.cd-form__file-remove {
  background: none;
  border: none;
  cursor: pointer;
  color: #64748b;
  display: flex;
  padding: 0;
  transition: color 0.15s;
}

.cd-form__file-remove:hover {
  color: #ef4444;
}

.cd-form__confirm {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  background: #f1f4f9;
  padding: 1rem;
  border-radius: 0.5rem;
  cursor: pointer;
}

.cd-form__checkbox {
  margin-top: 2px;
  accent-color: #4b9af6;
  flex-shrink: 0;
  cursor: pointer;
}

.cd-form__confirm span {
  font-size: 0.875rem;
  font-weight: 500;
  color: #0f172a;
  line-height: 1.5;
}

.cd-form__footer {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  padding-top: 1rem;
  border-top: 1px solid #e2e7f0;
}

.cd-form__btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 10px 24px;
  border-radius: 0.5rem;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  border: none;
  transition: all 0.15s;
}

.cd-form__btn--primary {
  background: #4b9af6;
  color: #fff;
  box-shadow: 0 2px 6px rgba(75, 154, 246, 0.2);
}

.cd-form__btn--primary:hover:not(:disabled) {
  background: #2563eb;
  box-shadow: 0 4px 10px rgba(75, 154, 246, 0.3);
}

.cd-form__btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.cd-form__btn:active:not(:disabled) {
  transform: scale(0.97);
}

.spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

.cd-form__submitted-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.cd-form__submitted-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.cd-form__submitted-quote {
  font-size: 0.875rem;
  color: #334155;
  line-height: 1.7;
  padding: 0.875rem 1rem;
  background: #fefce8;
  border-radius: 0.5rem;
  border-left: 4px solid #d97706;
  margin: 0;
}

.cd-form__submitted-meta {
  font-size: 0.75rem;
  color: #94a3b8;
  font-weight: 600;
  margin: 0;
}

.cd-form__empty-state {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  padding: 1rem;
  border: 1px dashed #fde68a;
  background: #fffbeb;
  border-radius: 0.75rem;
  color: #92400e;
}

.cd-form__empty-state .material-symbols-outlined {
  font-size: 1.125rem;
  margin-top: 2px;
}

.cd-form__empty-state p {
  margin: 0;
  font-size: 0.875rem;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .cd-form__submitted-grid {
    grid-template-columns: 1fr;
  }
}
</style>
