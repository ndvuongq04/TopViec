<template>
  <section class="cd-appeal">
    <div class="cd-appeal__header">
      <div>
        <h2 class="cd-appeal__title">
          <span class="material-symbols-outlined">fact_check</span>
          Kháng cáo của NTD
        </h2>

      </div>

      <div class="cd-appeal__badges">
        <span v-if="appeal" :class="['cd-appeal__badge', `cd-appeal__badge--${appeal.status}`]">
          {{ statusLabel }}
        </span>
        <span :class="['cd-appeal__badge', isSuspended ? 'cd-appeal__badge--danger' : 'cd-appeal__badge--neutral']">
          {{ isSuspended ? 'Đang bị khóa' : 'Đang hoạt động' }}
        </span>
      </div>
    </div>

    <div :class="['cd-appeal__grid', { 'cd-appeal__grid--single': isResolved }]">
      <div class="cd-appeal__card cd-appeal__card--info">
        <div class="cd-appeal__meta">
          <div>
            <span class="cd-appeal__label">Nhà tuyển dụng</span>
            <strong>{{ employerName }}</strong>
          </div>
          <div>
            <span class="cd-appeal__label">Mã khiếu nại</span>
            <strong>{{ appeal?.complaint.reportCode ?? '-' }}</strong>
          </div>
          <div>
            <span class="cd-appeal__label">Gửi kháng cáo lúc</span>
            <strong>{{ formatDateTime(appeal?.createdAt) }}</strong>
          </div>
          <div>
            <span class="cd-appeal__label">Điểm vi phạm hiện tại</span>
            <strong>{{ violationScore }} điểm</strong>
          </div>
        </div>

        <div class="cd-appeal__content">
          <span class="cd-appeal__label">Nội dung giải trình</span>
          <p>{{ appeal?.content ?? 'Chưa có kháng cáo nào từ nhà tuyển dụng cho báo cáo này.' }}</p>
        </div>

        <div v-if="appeal?.adminNote" class="cd-appeal__content">
          <span class="cd-appeal__label">Ghi chú admin</span>
          <p>{{ appeal.adminNote }}</p>
        </div>
      </div>

      <div v-if="!isResolved && canUnsuspend" class="cd-appeal__card cd-appeal__card--action">
        <div class="cd-appeal__action-head">
          <div>
            <span class="cd-appeal__label">Trạng thái xử lý kháng cáo</span>
            <strong>{{ statusLabel }}</strong>
          </div>
          <div>
            <span class="cd-appeal__label">Duyệt bởi</span>
            <strong>{{ appeal?.reviewedByAdmin?.fullName ?? '-' }}</strong>
          </div>
        </div>

        <p class="cd-appeal__action-copy">
          Admin có thể mở khóa sớm cho nhà tuyển dụng sau khi xem xét nội dung giải trình.
        </p>

        <div class="cd-appeal__form">
          <label class="cd-appeal__label" for="unsuspend-note">Ghi chú mở khóa sớm</label>
          <textarea
            id="unsuspend-note"
            v-model="note"
            class="cd-appeal__textarea"
            rows="4"
            maxlength="500"
            placeholder="Nhập ghi chú nội bộ trước khi mở khóa sớm..."
          />
        </div>

        <button
          class="cd-appeal__btn"
          :disabled="loading"
          @click="onUnsuspend"
        >
          {{ loading ? 'Đang xử lý...' : 'Mở khóa sớm' }}
        </button>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { APPEAL_STATUS_OPTIONS } from '@/constants/complaints.constants'
import { useToast } from '@/composables/useToast'
import type { ReqUnsuspendAppeal, ResAppeal } from '@/types/appeal.types'

const props = defineProps<{
  appeal: ResAppeal | null
  employerName: string
  violationScore: number
  isSuspended: boolean
  loading?: boolean
  canUnsuspend?: boolean
}>()

const emit = defineEmits<{
  unsuspend: [payload: ReqUnsuspendAppeal]
}>()

const toast = useToast()
const note = ref('')

const statusLabelMap = Object.fromEntries(
  APPEAL_STATUS_OPTIONS.map((item) => [item.value, item.label]),
) as Record<string, string>

const isResolved = computed(() => props.appeal && props.appeal.status !== 'pending')

const statusLabel = computed(() => {
  if (!props.appeal) return 'Chưa có kháng cáo'
  return statusLabelMap[props.appeal.status] ?? props.appeal.status
})

function formatDateTime(value: string | null | undefined) {
  if (!value) return '-'
  return new Date(value).toLocaleString('vi-VN')
}

function onUnsuspend() {
  if (!props.appeal) {
    toast.warning('Chưa có kháng cáo', 'Báo cáo này hiện chưa có kháng cáo để mở khóa sớm.')
    return
  }
  emit('unsuspend', { appealId: props.appeal.id, note: note.value.trim() || undefined })
}
</script>

<style scoped>
.cd-appeal {
  background: #fff;
  border-radius: 0.75rem;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border-top: 4px solid #285e61;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.cd-appeal__header {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  flex-wrap: wrap;
}

.cd-appeal__title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin: 0;
  font-size: 1.125rem;
  font-weight: 800;
  color: #1b1c18;
}

.cd-appeal__title .material-symbols-outlined {
  color: #285e61;
  font-size: 22px;
}

.cd-appeal__subtitle {
  margin: 0.4rem 0 0;
  font-size: 0.875rem;
  color: #5f6b6d;
}

.cd-appeal__badges {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.cd-appeal__badge {
  display: inline-flex;
  align-items: center;
  padding: 0.4rem 0.75rem;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 700;
}

.cd-appeal__badge--pending {
  background: #e6f0ff;
  color: #1d4ed8;
}

.cd-appeal__badge--approved,
.cd-appeal__badge--success {
  background: #dcfce7;
  color: #166534;
}

.cd-appeal__badge--rejected,
.cd-appeal__badge--danger {
  background: #fee2e2;
  color: #b91c1c;
}

.cd-appeal__badge--neutral {
  background: #e7e5e4;
  color: #44403c;
}

.cd-appeal__grid {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 1.5rem;
  align-items: start;
}

.cd-appeal__grid--single {
  grid-template-columns: 1fr;
}

@media (max-width: 900px) {
  .cd-appeal__grid {
    grid-template-columns: 1fr;
  }
}

.cd-appeal__card {
  border: 1px solid #e7e5e4;
  border-radius: 0.75rem;
  padding: 1rem;
  background: #fcfcfb;
}

.cd-appeal__card--action {
  background: linear-gradient(180deg, #fcfffe 0%, #f2f8f8 100%);
}

.cd-appeal__meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem;
}

@media (max-width: 640px) {
  .cd-appeal__meta {
    grid-template-columns: 1fr;
  }
}

.cd-appeal__label {
  display: block;
  margin-bottom: 0.35rem;
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: #6b7280;
}

.cd-appeal__content {
  margin-top: 1rem;
}

.cd-appeal__content p,
.cd-appeal__action-copy {
  margin: 0;
  font-size: 0.9375rem;
  line-height: 1.6;
  color: #1f2937;
  white-space: pre-line;
}

.cd-appeal__action-head {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  flex-wrap: wrap;
}

.cd-appeal__form {
  margin-top: 1rem;
}

.cd-appeal__textarea {
  width: 100%;
  min-height: 112px;
  border: 1px solid #d6d3d1;
  border-radius: 0.75rem;
  padding: 0.875rem 1rem;
  font: inherit;
  color: #1f2937;
  background: #fff;
  outline: none;
  resize: vertical;
}

.cd-appeal__textarea--readonly {
  white-space: pre-wrap;
  color: #574240;
  background: #f9f9f9;
  border-color: #e5e7eb;
  overflow-y: auto;
}

.cd-appeal__textarea:focus {
  border-color: #285e61;
  box-shadow: 0 0 0 3px rgba(40, 94, 97, 0.12);
}

.cd-appeal__btn {
  margin-top: 1rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 0.75rem;
  background: #285e61;
  color: #fff;
  font: inherit;
  font-weight: 700;
  padding: 0.8rem 1rem;
  cursor: pointer;
  transition: background 0.15s ease;
}

.cd-appeal__btn:hover:not(:disabled) {
  background: #1d4a4d;
}

.cd-appeal__btn:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}
</style>
