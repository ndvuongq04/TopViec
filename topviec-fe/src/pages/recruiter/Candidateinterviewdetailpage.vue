<template>
  <div class="page-wrapper">
    <!-- Breadcrumb -->
    <Breadcrumb :items="breadcrumbItems" hide-home />

    <!-- Loading -->
    <div v-if="loading" class="page-loading">
      <span class="material-symbols-outlined page-loading__icon">hourglass_empty</span>
      <p>Đang tải dữ liệu...</p>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="page-error">
      <span class="material-symbols-outlined page-error__icon">error_outline</span>
      <p>{{ error }}</p>
    </div>

    <main v-else class="interview-detail-page">

    <!-- ══ LEFT: CV Preview ══════════════════════════════════ -->
    <section class="cv-panel">
      <!-- Header -->
      <div class="cv-panel__header">
        <div class="cv-panel__title">
          <span class="material-symbols-outlined">description</span>
          <h4>CV Ứng viên</h4>
        </div>
        <div class="cv-panel__actions">
          <a
            v-if="history?.cvUrl"
            class="btn-action btn-action--primary"
            :href="history.cvUrl"
            target="_blank"
          >
            <span class="material-symbols-outlined">open_in_new</span>
            Mở tab mới
          </a>
          <a
            v-if="history?.cvUrl"
            class="btn-action"
            :href="history.cvUrl"
            target="_blank"
            download
          >
            <span class="material-symbols-outlined">download</span>
            Tải xuống
          </a>
        </div>
      </div>

      <!-- Viewport -->
      <div class="cv-panel__viewport">
        <template v-if="history?.cvUrl">
          <div v-if="cvLoading" class="cv-panel__loader">
            <div class="spinner"></div>
            <p>Đang chuẩn bị bản xem trước...</p>
          </div>
          <iframe
            :src="history.cvUrl"
            class="cv-panel__iframe"
            :class="{ 'cv-panel__iframe--loaded': !cvLoading }"
            title="CV ứng viên"
            @load="onCvLoad"
          />
        </template>
        <div v-else class="cv-panel__placeholder">
          <div class="cv-panel__placeholder-circle">
            <span class="material-symbols-outlined">file_off</span>
          </div>
          <p class="cv-panel__placeholder-text">Ứng viên chưa đính kèm hồ sơ CV</p>
        </div>
      </div>
    </section>

    <!-- ══ RIGHT: Evaluation + History ═══════════════════════ -->
    <aside class="right-panel">

      <!-- Card 1 — KẾT QUẢ PHỎNG VẤN & ĐÁNH GIÁ -->
      <div class="eval-card">

        <!-- Candidate summary -->
        <div class="cand-summary">
          <div class="cand-avatar">{{ candidateInitials }}</div>
          <div class="cand-info">
            <h3 class="cand-name">{{ history?.candidateName }}</h3>
          </div>
          <span class="status-chip" :class="statusConfig.cls">
            <span class="status-chip__dot"></span>
            {{ statusConfig.label }}
          </span>
        </div>

        <div class="section-divider" />

        <!-- Per-round evaluation forms -->
        <div
          v-for="(round, idx) in displayedRounds"
          :key="idx"
          class="round-eval"
        >
          <!-- Round header -->
          <div class="round-eval__header">
            <div class="round-eval__title-row">
              <span class="round-eval__badge">Vòng {{ round.roundNumber }}</span>
              <h5 class="round-eval__name">{{ round.roundName }}</h5>
            </div>
            <div class="round-eval__meta">
              <span v-if="round.scheduledAt" class="round-eval__meta-tag">
                <span class="material-symbols-outlined">schedule</span>
                {{ formatDateTime(round.scheduledAt) }}
              </span>
            </div>
          </div>

          <!-- Evaluate form UI (Interactive) -->
          <div class="eval-form">
            <!-- Kết quả -->
            <div class="eval-form__field">
              <label class="eval-form__label">Kết quả <span class="eval-form__required">*</span></label>
              <div class="evaluate-result-toggle">
                <button
                  type="button"
                  class="evaluate-result-btn evaluate-result-btn--pass"
                  :class="{ 'evaluate-result-btn--active': (round.result ?? pendingResults[round.scheduleId!]) === 'PASS' }"
                  :disabled="!!round.result"
                  @click="pendingResults[round.scheduleId!] = 'PASS'"
                >
                  <span class="material-symbols-outlined">check_circle</span>
                  Đạt (PASS)
                </button>
                <button
                  type="button"
                  class="evaluate-result-btn evaluate-result-btn--fail"
                  :class="{ 'evaluate-result-btn--active': (round.result ?? pendingResults[round.scheduleId!]) === 'FAIL' }"
                  :disabled="!!round.result"
                  @click="pendingResults[round.scheduleId!] = 'FAIL'"
                >
                  <span class="material-symbols-outlined">cancel</span>
                  Không đạt (FAIL)
                </button>
              </div>
            </div>

            <!-- Điểm đánh giá -->
            <div class="eval-form__field">
              <label class="eval-form__label">Điểm đánh giá</label>
              <div class="evaluate-stars">
                <button
                  v-for="star in 5"
                  :key="star"
                  type="button"
                  class="evaluate-star"
                  :class="{ 'evaluate-star--active': round.rating !== undefined && star <= round.rating }"
                  :disabled="!!round.result"
                  @click="round.rating = (round.rating === star ? undefined : star)"
                >
                  <span class="material-symbols-outlined">star</span>
                </button>
                <span v-if="round.rating" class="evaluate-stars__label">
                  {{ round.rating }}/5
                </span>
              </div>
            </div>

            <!-- Ghi chú nhận xét -->
            <div class="eval-form__field">
              <label class="eval-form__label">Ghi chú nhận xét</label>
              <textarea
                :value="round.note ?? ''"
                @input="round.note = ($event.target as HTMLTextAreaElement).value"
                class="eval-form__textarea"
                :readonly="!!round.result"
                rows="3"
                :placeholder="round.result ? 'Không có ghi chú' : 'Nhập nhận xét về ứng viên...'"
              />
            </div>

            <!-- Submit button per round (Only if not evaluated) -->
            <div v-if="!round.result" class="eval-form__actions">
              <button
                class="btn-save-eval"
                :disabled="isSubmitting[round.scheduleId!]"
                @click="saveRoundEvaluation(round)"
              >
                <span class="material-symbols-outlined">
                  {{ isSubmitting[round.scheduleId!] ? 'hourglass_empty' : 'save' }}
                </span>
                {{ 'Lưu kết quả' }}
              </button>
              <button
                class="btn-talent-pool-eval"
                type="button"
                @click="saveToTalentPool(round)"
              >
                <span class="material-symbols-outlined">group_add</span>
                Lưu vào TalentPool
              </button>
            </div>
          </div>

          <div v-if="idx < (history?.rounds ?? []).filter(r => r.scheduleId).length - 1" class="round-divider" />
        </div>
      </div>

      <!-- Card 2 — LỊCH SỬ PHỎNG VẤN -->
      <InterviewTimeline :items="timelineItems" />

    </aside>
  </main>

  <!-- No modal anymore -->
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import Breadcrumb from '@/components/ui/Breadcrumb.vue'
import InterviewTimeline from '@/components/recruiter/interviews/candidate-detail/InterviewTimeline.vue'
import employerInterviewService from '@/services/employerInterview.service'
import employerTalentPoolService from '@/services/employerTalentPool.service'
import type { ResInterviewHistoryDTO, RoundHistory } from '@/types/interview.types'
import { useToast } from '@/composables/useToast'

const route = useRoute()
const toast = useToast()
const jobId        = Number(route.params.id)
const applicationId = Number(route.params.applicationId)

// ── State ────────────────────────────────────────────────────
const history = ref<ResInterviewHistoryDTO | null>(null)
const loading = ref(true)
const cvLoading = ref(true)
const error   = ref<string | null>(null)

function onCvLoad() {
  cvLoading.value = false
}

// ── Submission State ─────────────────────────────────────────
const isSubmitting = ref<Record<number, boolean>>({})

// ── Pending selection (chưa lưu, chỉ dùng cho UI) ───────────
const pendingResults = ref<Record<number, 'PASS' | 'FAIL' | undefined>>({})

// ── Breadcrumb ───────────────────────────────────────────────
const breadcrumbItems = computed(() => [
  { label: 'Quản lý phỏng vấn', to: { name: 'recruiter-interviews' } },
  { label: 'Thiết lập phỏng vấn', to: { name: 'recruiter-job-interview-setup', params: { id: jobId } } },
  { label: history.value?.candidateName ?? 'Chi tiết ứng viên' },
])

// ── Avatar initials ──────────────────────────────────────────
const candidateInitials = computed(() => {
  const name = history.value?.candidateName?.trim() ?? ''
  if (!name) return '?'
  const parts = name.split(' ')
  return parts.length >= 2
    ? (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
    : name.slice(0, 2).toUpperCase()
})

// ── Status chip ──────────────────────────────────────────────
const STATUS_MAP: Record<string, { label: string; cls: string }> = {
  PENDING:      { label: 'Đang xử lý',   cls: 'status-chip--pending' },
  REVIEWING:    { label: 'Xem xét CV',   cls: 'status-chip--reviewing' },
  INTERVIEWING: { label: 'Đang PV',      cls: 'status-chip--interviewing' },
  PASSED:       { label: 'Đã đạt',       cls: 'status-chip--passed' },
  REJECTED:     { label: 'Từ chối',      cls: 'status-chip--rejected' },
  OFFERED:      { label: 'Offered',      cls: 'status-chip--offered' },
  COMPLETED:    { label: 'Hoàn thành',   cls: 'status-chip--passed' },
  OVERDUE:      { label: 'Quá hạn',      cls: 'status-chip--rejected' },
}

const statusConfig = computed(() =>
  STATUS_MAP[history.value?.currentStatus ?? ''] ?? { label: history.value?.currentStatus ?? '', cls: 'status-chip--pending' }
)

// ── Filtered rounds ─────────────────────────────────────────
const displayedRounds = computed(() => {
  if (!history.value?.rounds) return []
  const roundNum = Number(route.query.roundNumber)
  if (roundNum) {
    return history.value.rounds.filter(r => r.roundNumber === roundNum && r.scheduleId)
  }
  return history.value.rounds.filter(r => r.scheduleId)
})

// ── Timeline items ───────────────────────────────────────────
const timelineItems = computed(() => {
  if (!history.value) return []
  return history.value.rounds.map(r => {
    let status: 'passed' | 'upcoming' | 'pending'
    if (!r.scheduleId)  status = 'pending'
    else if (r.result)  status = 'passed'
    else                status = 'upcoming'

    return {
      roundLabel: `Vòng ${r.roundNumber}: ${r.roundName}`,
      status,
      rating:      r.rating,
      scheduledAt: r.scheduledAt ? formatDateTime(r.scheduledAt) : undefined,
      note:        r.note,
      countdown:   status === 'upcoming' && r.scheduledAt ? getCountdown(r.scheduledAt) : undefined,
    }
  })
})

// ── Helpers ──────────────────────────────────────────────────
function formatDateTime(iso: string): string {
  const d  = new Date(iso)
  const hh = String(d.getHours()).padStart(2, '0')
  const mm = String(d.getMinutes()).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  const mo = String(d.getMonth() + 1).padStart(2, '0')
  return `${hh}:${mm} · ${dd}/${mo}/${d.getFullYear()}`
}

function getCountdown(iso: string): string {
  const days = Math.ceil((new Date(iso).getTime() - Date.now()) / 86_400_000)
  if (days > 1)  return `Còn ${days} ngày`
  if (days === 1) return 'Còn 1 ngày'
  if (days === 0) return 'Hôm nay'
  return 'Đã qua'
}

// ── Fetch ────────────────────────────────────────────────────
async function fetchHistory() {
  try {
    const data = await employerInterviewService.getInterviewHistory(applicationId)
    // Normalize result case for UI consistency
    if (data && data.rounds) {
      data.rounds.forEach(r => {
        if (r.result) r.result = r.result.toUpperCase()
      })
    }
    history.value = data
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Không thể tải dữ liệu'
  } finally {
    loading.value = false
  }
}

onMounted(fetchHistory)

async function saveRoundEvaluation(round: RoundHistory) {
  if (!round.scheduleId) return
  const selectedResult = pendingResults.value[round.scheduleId]
  if (!selectedResult) {
    toast.warning('Chú ý', 'Vui lòng chọn kết quả phỏng vấn (Đạt/Không đạt).')
    return
  }

  isSubmitting.value[round.scheduleId] = true
  try {
    await employerInterviewService.createResult(round.scheduleId, {
      result: selectedResult,
      rating: round.rating === null ? undefined : round.rating,
      note:   round.note || undefined,
      notifyCandidate: false, // Default false in-page
    })
    
    // Smoothly update history instead of full loading state
    const updatedHistory = await employerInterviewService.getInterviewHistory(applicationId)
    history.value = updatedHistory
    
    toast.success('Thành công', `Vòng ${round.roundNumber} đã được lưu kết quả.`)
  } catch (err: any) {
    toast.error('Lỗi', err?.response?.data?.message ?? 'Không thể lưu kết quả. Vui lòng thử lại.')
  } finally {
    isSubmitting.value[round.scheduleId] = false
  }
}

async function saveToTalentPool(_round: RoundHistory) {
  if (!history.value?.candidateUserId) return
  try {
    await employerTalentPoolService.addToTalentPool({
      candidateUserId: history.value.candidateUserId,
      source: 'INTERVIEW',
    })
    toast.success('Đã lưu vào TalentPool!', `Ứng viên ${history.value.candidateName} đã được thêm vào TalentPool.`)
  } catch (err: any) {
    const msg = err?.response?.data?.message ?? 'Không thể lưu vào TalentPool. Vui lòng thử lại.'
    toast.error('Lỗi', typeof msg === 'string' ? msg : msg?.[0])
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Manrope:wght@400;500;600;700;800&display=swap');

/* ── Layout ─────────────────────────────────────────────────── */
.page-wrapper {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  font-family: 'Manrope', sans-serif;
  background: #f8fafc;
  min-height: 100vh;
  color: #0f172a;
}

.interview-detail-page {
  display: grid;
  grid-template-columns: 3fr 2fr;
  gap: 1.5rem;
  align-items: start;
  min-height: 100vh;
  background: #f8fafc;
  color: #0f172a;
}

/* ── LEFT: CV Panel ─────────────────────────────────────────── */
.cv-panel {
  background: #fff;
  border-radius: 1rem;
  border: 1px solid #f1f5f9;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 1200px;
}

.cv-panel__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.875rem 1.5rem;
  background: #fff;
  border-bottom: 1px solid #f1f5f9;
  z-index: 10;
}

.cv-panel__title {
  display: flex;
  align-items: center;
  gap: 0.625rem;
}
.cv-panel__title h4 {
  font-size: 0.9375rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}
.cv-panel__title .material-symbols-outlined {
  color: #4b9af6;
  font-size: 1.25rem;
}

/* Actions */
.cv-panel__actions { display: flex; align-items: center; gap: 0.75rem; }
.btn-action {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.5rem 0.875rem;
  border-radius: 0.5rem;
  border: 1px solid #e2e8f0;
  background: #fff;
  font-size: 0.8125rem;
  font-weight: 600;
  font-family: inherit;
  color: #475569;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-action:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
  color: #1e293b;
}
.btn-action--primary {
  background: #4b9af6;
  color: #fff;
  border-color: #4b9af6;
}
.btn-action--primary:hover {
  background: #3b82f6;
  border-color: #3b82f6;
  color: #fff;
}

.btn-action .material-symbols-outlined {
  font-size: 1.1rem !important;
}

/* PDF viewport */
.cv-panel__viewport {
  flex: 1;
  position: relative;
  background: #cbd5e1; /* Màu nền cho viewport giống viewer PDF chuyên nghiệp */
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.cv-panel__loader {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  background: #f8fafc;
  z-index: 5;
}

.spinner {
  width: 2.5rem;
  height: 2.5rem;
  border: 3px solid #e2e8f0;
  border-top-color: #4b9af6;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.cv-panel__placeholder {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1.25rem;
  background: #f8fafc;
  padding: 3rem;
}
.cv-panel__placeholder-circle {
  width: 5rem;
  height: 5rem;
  border-radius: 50%;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
}
.cv-panel__placeholder-circle .material-symbols-outlined {
  font-size: 2.5rem !important;
}
.cv-panel__placeholder-text {
  font-size: 0.9375rem;
  font-weight: 500;
  color: #64748b;
  margin: 0;
  text-align: center;
}

/* ── RIGHT: Panel ───────────────────────────────────────────── */
.right-panel {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

/* ── Card shared ────────────────────────────────────────────── */
.eval-card {
  background: #fff;
  border-radius: 1rem;
  border: 1px solid #f1f5f9;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.section-divider {
  height: 1px;
  background: #f1f5f9;
  margin: 0 -1.5rem;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.125rem;          /* 18px — tiêu đề khối */
  font-weight: 800;
  color: #0f172a;
  margin: 0;
}
.card-title .material-symbols-outlined {
  font-size: 1.25rem !important;
  color: #4b9af6;
  font-family: 'Material Symbols Outlined';
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  line-height: 1;
}

/* ── Candidate Summary ──────────────────────────────────────── */
.cand-summary {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
}

.cand-avatar {
  width: 3rem;
  height: 3rem;
  border-radius: 9999px;
  background: linear-gradient(135deg, #4b9af6, #137fec);
  color: #fff;
  font-size: 1rem;              /* 16px */
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  letter-spacing: 0.02em;
}

.cand-info { flex: 1; min-width: 0; }

.cand-name {
  font-size: 1.125rem;          /* 18px — tên item chính */
  font-weight: 800;
  color: #0f172a;
  margin: 0 0 0.25rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.cand-meta {
  font-size: 0.75rem;           /* 12px — label phụ */
  font-weight: 500;
  color: #64748b;
  margin: 0 0 0.375rem;
}

.cand-job {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.75rem;           /* 12px */
  font-weight: 600;
  color: #475569;
  margin: 0;
}
.cand-job .material-symbols-outlined {
  font-size: 0.875rem !important;
  color: #94a3b8;
  font-family: 'Material Symbols Outlined';
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  line-height: 1;
}

/* Status chip */
.status-chip {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.25rem 0.625rem;
  border-radius: 9999px;
  font-size: 0.75rem;           /* 12px — badge */
  font-weight: 700;
  white-space: nowrap;
  flex-shrink: 0;
}
.status-chip__dot {
  width: 0.375rem;
  height: 0.375rem;
  border-radius: 9999px;
}
.status-chip--interviewing {
  background: #dbeafe;
  color: #1d4ed8;
}
.status-chip--interviewing .status-chip__dot { background: #3b82f6; }

/* ── Round eval header ─────────────────────────────────────── */
.round-eval { display: flex; flex-direction: column; gap: 1rem; }

.round-eval__header { display: flex; flex-direction: column; gap: 0.375rem; }

.round-eval__title-row { display: flex; align-items: center; gap: 0.625rem; }

.round-eval__badge {
  display: inline-flex;
  align-items: center;
  padding: 0.125rem 0.5rem;
  border-radius: 9999px;
  background: #eff6ff;
  color: #1d4ed8;
  font-size: 0.75rem;           /* 12px */
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  white-space: nowrap;
}

.round-eval__name {
  font-size: 0.875rem;          /* 14px */
  font-weight: 700;
  color: #0f172a;
  margin: 0;
}

.round-eval__meta {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.round-eval__meta-tag {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.75rem;           /* 12px */
  font-weight: 500;
  color: #64748b;
}
.round-eval__meta-tag .material-symbols-outlined {
  font-size: 0.875rem !important;
  font-family: 'Material Symbols Outlined';
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  line-height: 1;
}

/* Divider between rounds */
.round-divider {
  height: 1px;
  background: #f1f5f9;
  margin: 0.5rem 0;
}

/* ── Evaluate form (read-only) ──────────────────────────────── */
.eval-form { display: flex; flex-direction: column; gap: 1rem; }

.eval-form__field { display: flex; flex-direction: column; gap: 0.375rem; }

.eval-form__label {
  font-size: 0.75rem;           /* 12px — label nhỏ */
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #64748b;
}

.eval-form__empty {
  font-size: 0.75rem;
  font-weight: 500;
  color: #94a3b8;
  font-style: italic;
  margin: 0.25rem 0 0;
}

.eval-form__empty-inline {
  font-size: 0.75rem;
  font-weight: 500;
  color: #94a3b8;
  font-style: italic;
  margin-left: 0.375rem;
}

/* PASS/FAIL toggle (reuse từ JobInterviewSetupPage) */
.evaluate-result-toggle { display: flex; gap: 0.75rem; }
.evaluate-result-btn {
  flex: 1; display: flex; align-items: center; justify-content: center; gap: 0.5rem;
  padding: 0.75rem 1rem; border-radius: 0.75rem; border: 2px solid #e2e8f0;
  background: #f8fafc; font-size: 0.875rem; font-weight: 600;
  font-family: inherit; color: #94a3b8; cursor: default;
  transition: none;
}
.evaluate-result-btn .material-symbols-outlined {
  font-size: 1.2rem !important;
  font-family: 'Material Symbols Outlined';
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  line-height: 1;
}
.evaluate-result-btn--pass.evaluate-result-btn--active {
  border-color: #22c55e; color: #16a34a; background: #f0fdf4;
  box-shadow: 0 0 0 3px rgba(34, 197, 94, 0.15);
}
.evaluate-result-btn--fail.evaluate-result-btn--active {
  border-color: #ef4444; color: #dc2626; background: #fef2f2;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.15);
}

/* Star rating (read-only) */
.evaluate-stars { display: flex; align-items: center; gap: 0.25rem; }
.evaluate-star {
  background: none; border: none; padding: 0.125rem;
  cursor: default; color: #cbd5e1;
}
.evaluate-star .material-symbols-outlined {
  font-size: 1.75rem !important;
  font-family: 'Material Symbols Outlined';
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  line-height: 1;
}
.evaluate-star--active .material-symbols-outlined { font-variation-settings: 'FILL' 1; }
.evaluate-star--active { color: #f59e0b; }
.evaluate-stars__label {
  margin-left: 0.5rem;
  font-size: 0.875rem;          /* 14px */
  font-weight: 700;
  color: #f59e0b;
}

/* Note textarea (read-only) */
.eval-form__textarea {
  width: 100%;
  padding: 0.75rem 1rem;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 0.625rem;
  font-size: 0.875rem;          /* 14px */
  font-family: inherit;
  color: #475569;
  resize: none;
  outline: none;
  box-sizing: border-box;
  cursor: default;
  line-height: 1.6;
}

/* ── Loading / Error ────────────────────────────────────────── */
.page-loading,
.page-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 4rem;
  color: #64748b;
  font-size: 0.875rem;
  font-weight: 500;
}
.page-loading__icon,
.page-error__icon {
  font-size: 2.5rem !important;
  font-family: 'Material Symbols Outlined';
  font-variation-settings: 'FILL' 0, 'wght' 300, 'GRAD' 0, 'opsz' 48;
  line-height: 1;
  color: #94a3b8;
}
.page-error { color: #ef4444; }
.page-error__icon { color: #fca5a5; }

/* ── CV iframe ───────────────────────────────────────────────── */
.cv-panel__iframe {
  width: 100%;
  height: 100%;
  flex: 1;
  border: none;
  opacity: 0;
  transition: opacity 0.3s ease;
}
.cv-panel__iframe--loaded {
  opacity: 1;
}

/* ── Status chip variants ────────────────────────────────────── */
.status-chip--pending    { background: #fef3c7; color: #d97706; }
.status-chip--pending .status-chip__dot    { background: #f59e0b; }
.status-chip--reviewing  { background: #e0e7ff; color: #4338ca; }
.status-chip--reviewing .status-chip__dot  { background: #6366f1; }
.status-chip--passed     { background: #d1fae5; color: #059669; }
.status-chip--passed .status-chip__dot     { background: #10b981; }
.status-chip--rejected   { background: #fee2e2; color: #ef4444; }
.status-chip--rejected .status-chip__dot   { background: #ef4444; }
.status-chip--offered    { background: #fae8ff; color: #a21caf; }
.status-chip--offered .status-chip__dot    { background: #c026d3; }

/* ── In-page Evaluate UI ────────────────────────────────────── */
.eval-form__field { display: flex; flex-direction: column; gap: 0.5rem; }
.eval-form__label {
  font-size: 0.75rem; font-weight: 700; text-transform: uppercase;
  letter-spacing: 0.08em; color: #64748b;
}
.eval-form__required { color: #ef4444; }

.evaluate-result-toggle { display: flex; gap: 0.75rem; }
.evaluate-result-btn {
  flex: 1; display: flex; align-items: center; justify-content: center; gap: 0.5rem;
  padding: 0.625rem 0.875rem; border-radius: 0.75rem; border: 2px solid #e2e8f0;
  background: #f8fafc; font-size: 0.8125rem; font-weight: 600;
  font-family: inherit; color: #94a3b8; cursor: pointer;
  transition: all 0.2s;
}
.evaluate-result-btn .material-symbols-outlined { font-size: 1.1rem !important; }
.evaluate-result-btn--pass.evaluate-result-btn--active {
  border-color: #22c55e; color: #16a34a; background: #f0fdf4;
  box-shadow: 0 0 0 3px rgba(34, 197, 94, 0.1);
}
.evaluate-result-btn--fail.evaluate-result-btn--active {
  border-color: #ef4444; color: #dc2626; background: #fef2f2;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}

.evaluate-stars { display: flex; align-items: center; gap: 0.25rem; }
.evaluate-star {
  background: none; border: none; padding: 0.125rem;
  cursor: pointer; color: #cbd5e1; transition: transform 0.1s;
}
.evaluate-star:hover { transform: scale(1.1); }
.evaluate-star .material-symbols-outlined {
  font-size: 1.75rem !important;
  font-family: 'Material Symbols Outlined';
  font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  line-height: 1;
}
.evaluate-star--active .material-symbols-outlined { font-variation-settings: 'FILL' 1; }
.evaluate-star--active { color: #f59e0b; }
.evaluate-stars__label {
  margin-left: 0.5rem; font-size: 0.875rem; font-weight: 700; color: #f59e0b;
}

.eval-form__textarea {
  width: 100%; padding: 0.75rem 1rem; background: #f8fafc;
  border: 1px solid #e2e8f0; border-radius: 0.625rem;
  font-size: 0.875rem; font-family: inherit; color: #1e293b;
  resize: none; outline: none; box-sizing: border-box;
  line-height: 1.5; transition: border-color 0.2s;
}
.eval-form__textarea:focus { border-color: #4b9af6; background: #fff; }

.eval-form__actions {
  display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 0.5rem;
}

.btn-save-eval {
  display: inline-flex; align-items: center; gap: 0.5rem;
  padding: 0.625rem 1.25rem; border-radius: 0.625rem;
  background: #4b9af6; color: #fff; border: none;
  font-size: 0.875rem; font-weight: 700; cursor: pointer;
  transition: background 0.2s, transform 0.1s;
}
.btn-save-eval:hover:not(:disabled) { background: #3b82f6; transform: translateY(-1px); }
.btn-save-eval:active:not(:disabled) { transform: translateY(0); }
.btn-save-eval:disabled { background: #94a3b8; cursor: not-allowed; opacity: 0.7; }
.btn-save-eval .material-symbols-outlined { font-size: 1.1rem; }

.btn-talent-pool-eval {
  display: inline-flex; align-items: center; gap: 0.5rem;
  padding: 0.625rem 1.25rem; border-radius: 0.625rem;
  background: #fff; color: #059669;
  border: 2px solid #d1fae5;
  font-size: 0.875rem; font-weight: 700; cursor: pointer;
  transition: background 0.2s, color 0.2s, border-color 0.2s, transform 0.1s;
}
.btn-talent-pool-eval:hover { background: #059669; color: #fff; border-color: #059669; transform: translateY(-1px); }
.btn-talent-pool-eval:active { transform: translateY(0); }
.btn-talent-pool-eval .material-symbols-outlined { font-size: 1.1rem; }

/* ── Responsive ─────────────────────────────────────────────── */
@media (max-width: 1024px) {
  .interview-detail-page {
    grid-template-columns: 1fr;
  }
  .cv-panel { min-height: 500px; }
}
</style>
