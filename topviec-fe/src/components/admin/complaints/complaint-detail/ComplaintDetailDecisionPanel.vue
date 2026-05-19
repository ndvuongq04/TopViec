<template>
  <div class="cd-decision">
    <h2 class="cd-decision__title">
      <span class="material-symbols-outlined">gavel</span>
      Bảng quyết định xử lý
    </h2>

    <div v-if="isResolved" class="cd-decision__resolved-banner">
      <span class="material-symbols-outlined">check_circle</span>
      <p>Khiếu nại này đã được quyết định xử lý và hiện đang ở trạng thái <strong>Đã giải quyết</strong>.</p>
    </div>

    <template v-else>
      <div class="cd-decision__row">
        <div class="cd-decision__field">
          <label class="cd-decision__label">Kết luận</label>
          <select v-model="form.decision" class="cd-decision__select">
          <option value="">Chọn kết luận...</option>
          <option value="approve">Phê duyệt khiếu nại</option>
          <option value="reject">Từ chối khiếu nại</option>
        </select>
      </div>

      <div class="cd-decision__field">
        <label class="cd-decision__label">Trạng thái hiện tại</label>
        <input :value="currentStatusLabel" class="cd-decision__input" type="text" readonly />
      </div>
    </div>

      <div class="cd-decision__note">
        API xử lý hiện chỉ nhận `decision` và `resolutionNote`. Các cấu hình như nhóm vi phạm,
        điểm phạt hoặc biện pháp xử lý không còn được gửi từ form này.
      </div>
    </template>

    <div class="cd-decision__field">
      <label class="cd-decision__label">Ghi chú xử lý</label>
      <textarea
        v-if="!isResolved"
        v-model="form.resolutionNote"
        class="cd-decision__textarea"
        placeholder="Nhập ghi chú xử lý..."
        rows="4"
      />
      <div v-else class="cd-decision__textarea cd-decision__textarea--readonly">
        {{ store.currentReport?.resolutionNote || 'Không có ghi chú xử lý.' }}
      </div>
    </div>

    <div v-if="!isResolved" class="cd-decision__footer">
      <button class="cd-decision__btn cd-decision__btn--cancel" type="button" @click="onCancel">
        Hủy
      </button>
        <button
          class="cd-decision__btn cd-decision__btn--submit"
          type="button"
          :disabled="submitting || !canSubmit"
          @click="onSubmit"
        >
          {{ submitting ? 'Đang gửi...' : 'Gửi quyết định' }}
        </button>
      </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { COMPLAINT_STATUS_OPTIONS } from '@/constants/complaints.constants'
import { useToast } from '@/composables/useToast'
import { useAdminReportStore } from '@/stores/adminReport.store'
import type { ReqProcessReport } from '@/types/report.types'

const route = useRoute()
const store = useAdminReportStore()
const toast = useToast()
const PROCESSABLE_STATUSES = ['processing', 'waiting_employer'] as const

const complaintStatusLabelMap = Object.fromEntries(
  COMPLAINT_STATUS_OPTIONS.map((item) => [item.value, item.label]),
) as Record<string, string>

const submitting = ref(false)
const form = reactive({
  decision: '',
  resolutionNote: '',
})

const currentStatusLabel = computed(() => {
  const status = store.currentReport?.status
  return status ? (complaintStatusLabelMap[status] ?? status) : '-'
})

const isProcessable = computed(() => {
  const status = store.currentReport?.status
  return Boolean(
    status && PROCESSABLE_STATUSES.includes(status as (typeof PROCESSABLE_STATUSES)[number]),
  )
})

const canSubmit = computed(() => Boolean(form.decision) && isProcessable.value)

const isResolved = computed(() => store.currentReport?.status === 'resolved')

watch(
  () => store.currentReport,
  (detail) => {
    if (!detail) return
    form.resolutionNote = detail.resolutionNote ?? ''
  },
  { immediate: true },
)

function onCancel() {
  const detail = store.currentReport
  form.decision = ''
  form.resolutionNote = detail?.resolutionNote ?? ''
}

async function onSubmit() {
  const reportId = Number(route.params.id)
  if (!reportId) {
    toast.error('Lỗi', 'Không xác định được mã khiếu nại.')
    return
  }

  if (!isProcessable.value) {
    toast.error(
      'Không thể xử lý',
      'Báo cáo phải ở trạng thái processing hoặc waiting_employer mới có thể xử lý.',
    )
    return
  }

  const payload: ReqProcessReport = {
    decision: form.decision as 'approve' | 'reject',
    resolutionNote: form.resolutionNote.trim() || undefined,
  }

  submitting.value = true

  try {
    await store.processReport(reportId, payload)
    toast.success('Thành công', 'Đã xử lý khiếu nại thành công.')
  } catch {
    toast.error('Lỗi', store.error ?? 'Không thể xử lý khiếu nại.')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.cd-decision {
  background: #fff;
  border-radius: 0.75rem;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border-top: 4px solid #963131;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.cd-decision__title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.125rem;
  font-weight: 700;
  color: #1b1c18;
}

.cd-decision__title .material-symbols-outlined {
  color: #76191d;
  font-size: 22px;
}

.cd-decision__resolved-banner {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem;
  border-radius: 0.5rem;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  color: #166534;
}

.cd-decision__resolved-banner .material-symbols-outlined {
  color: #15803d;
  font-size: 1.5rem;
}

.cd-decision__resolved-banner p {
  margin: 0;
  font-size: 0.875rem;
  line-height: 1.5;
}

.cd-decision__row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
}

@media (max-width: 640px) {
  .cd-decision__row {
    grid-template-columns: 1fr;
  }
}

.cd-decision__field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.cd-decision__label {
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.07em;
  color: #574240;
}

.cd-decision__select,
.cd-decision__input,
.cd-decision__textarea {
  background: #fbf9f2;
  border: 1px solid #e4e2dc;
  border-radius: 0.5rem;
  padding: 8px 12px;
  font-size: 0.875rem;
  font-family: 'Inter', sans-serif;
  color: #1b1c18;
  outline: none;
  width: 100%;
  box-sizing: border-box;
  transition: border-color 0.15s, box-shadow 0.15s;
}

.cd-decision__select:focus,
.cd-decision__input:focus,
.cd-decision__textarea:focus {
  border-color: #76191d;
  box-shadow: 0 0 0 2px rgba(118, 25, 29, 0.12);
}

.cd-decision__input[readonly] {
  color: #574240;
}

.cd-decision__textarea {
  resize: vertical;
  min-height: 112px;
}

.cd-decision__textarea--readonly {
  white-space: pre-wrap;
  color: #574240;
  overflow-y: auto;
}

.cd-decision__note {
  border-radius: 0.75rem;
  background: #fff8e8;
  border: 1px solid #f5dec2;
  color: #8a5b18;
  padding: 0.875rem 1rem;
  font-size: 0.8125rem;
  line-height: 1.5;
}

.cd-decision__footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}

.cd-decision__btn {
  border: none;
  border-radius: 0.625rem;
  padding: 0.75rem 1.25rem;
  font-size: 0.875rem;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.15s ease, opacity 0.15s ease, background-color 0.15s ease;
}

.cd-decision__btn:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.cd-decision__btn--cancel {
  background: #ece9e1;
  color: #574240;
}

.cd-decision__btn--cancel:hover:not(:disabled) {
  background: #e4dfd4;
}

.cd-decision__btn--submit {
  background: #76191d;
  color: #fff;
  min-width: 160px;
}

.cd-decision__btn--submit:hover:not(:disabled) {
  background: #5f1216;
}
</style>
