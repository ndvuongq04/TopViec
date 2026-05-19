<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePublicInterviewStore } from '@/stores/publicInterview.store'

type PageStatus = 'loading' | 'ready' | 'selecting' | 'success' | 'expired' | 'no-slots' | 'error'

const route = useRoute()
const router = useRouter()
const store = usePublicInterviewStore()

const status = ref<PageStatus>('loading')
const errorMessage = ref('')
const successMessage = ref('')
const selectedSlotId = ref<number | null>(null)

const token = route.query.token as string
const page = computed(() => store.slotSelectionPage)

function formatDate(dateStr: string) {
  const d = new Date(dateStr)
  const days = ['Chủ nhật', 'Thứ hai', 'Thứ ba', 'Thứ tư', 'Thứ năm', 'Thứ sáu', 'Thứ bảy']
  return {
    dayOfWeek: days[d.getDay()],
    date: `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`,
    time: `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`,
  }
}

function formatTimeRange(start: string, end: string) {
  const s = new Date(start)
  const e = new Date(end)
  const fmt = (d: Date) => `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
  return `${fmt(s)} – ${fmt(e)}`
}

function availableSpots(max: number, registered: number) {
  return Math.max(0, max - registered)
}

onMounted(async () => {
  if (!token) {
    status.value = 'error'
    errorMessage.value = 'Liên kết không hợp lệ hoặc đã hết hạn.'
    return
  }
  try {
    await store.fetchSlotsByToken(token)
    const data = store.slotSelectionPage
    if (!data) {
      status.value = 'error'
      errorMessage.value = 'Không thể tải thông tin. Vui lòng thử lại.'
      return
    }
    // Kiểm tra deadline
    if (data.deadline && new Date(data.deadline) < new Date()) {
      status.value = 'expired'
      return
    }
    // Kiểm tra còn slot không
    const hasAvailable = data.slots.some(s => availableSpots(s.maxCandidates, s.registeredCount) > 0)
    if (!hasAvailable) {
      status.value = 'no-slots'
      return
    }
    status.value = 'ready'
  } catch {
    status.value = 'error'
    errorMessage.value = store.error || 'Liên kết đã hết hạn hoặc không hợp lệ.'
  }
})

async function handleConfirm() {
  if (!selectedSlotId.value) return
  status.value = 'selecting'
  try {
    const res = await store.confirmSlot(token, selectedSlotId.value)
    successMessage.value = res?.message || 'Bạn đã đăng ký lịch phỏng vấn thành công!'
    status.value = 'success'
  } catch {
    status.value = 'error'
    errorMessage.value = store.error || 'Có lỗi xảy ra. Vui lòng thử lại.'
  }
}

function goToHome() {
  router.push({ name: 'home' })
}

function goToInterviews() {
  router.push({ name: 'CandidateInterviews' })
}

function interviewTypeLabel(type: string) {
  if (type === 'online') return 'Trực tuyến'
  if (type === 'onsite') return 'Tại văn phòng'
  return 'Qua điện thoại'
}

function interviewTypeIcon(type: string) {
  if (type === 'online') return 'videocam'
  if (type === 'onsite') return 'location_on'
  return 'call'
}
</script>

<template>
  <div class="select-slot-page">

    <!-- Loading -->
    <div v-if="status === 'loading'" class="slot-card slot-card--centered">
      <div class="status-icon status-icon--loading">
        <span class="material-symbols-outlined spin">autorenew</span>
      </div>
      <h2 class="card-title">Đang tải danh sách lịch...</h2>
      <p class="card-desc">Vui lòng chờ trong giây lát.</p>
    </div>

    <!-- Expired -->
    <div v-else-if="status === 'expired'" class="slot-card slot-card--centered">
      <div class="status-icon status-icon--warning">
        <span class="material-symbols-outlined">schedule</span>
      </div>
      <h2 class="card-title">Đã hết hạn chọn lịch</h2>
      <p class="card-desc">Thời gian đăng ký lịch phỏng vấn đã kết thúc. Vui lòng liên hệ nhà tuyển dụng để được hỗ trợ.</p>
      <div class="card-actions">
        <button class="btn-secondary" @click="goToHome">Về trang chủ</button>
      </div>
    </div>

    <!-- No slots available -->
    <div v-else-if="status === 'no-slots'" class="slot-card slot-card--centered">
      <div class="status-icon status-icon--warning">
        <span class="material-symbols-outlined">event_busy</span>
      </div>
      <h2 class="card-title">Tất cả slot đã đầy</h2>
      <p class="card-desc">Hiện tại không còn slot trống. Vui lòng liên hệ nhà tuyển dụng để được sắp xếp lại.</p>
      <div class="card-actions">
        <button class="btn-secondary" @click="goToHome">Về trang chủ</button>
      </div>
    </div>

    <!-- Success -->
    <div v-else-if="status === 'success'" class="slot-card slot-card--centered">
      <div class="status-icon status-icon--success">
        <span class="material-symbols-outlined">check_circle</span>
      </div>
      <h2 class="card-title">Đăng ký thành công!</h2>
      <p class="card-desc">{{ successMessage }}</p>
      <div class="card-actions">
        <button class="btn-primary" @click="goToInterviews">
          <span class="material-symbols-outlined">event</span>
          Xem lịch phỏng vấn
        </button>
        <button class="btn-secondary" @click="goToHome">Về trang chủ</button>
      </div>
    </div>

    <!-- Error -->
    <div v-else-if="status === 'error'" class="slot-card slot-card--centered">
      <div class="status-icon status-icon--error">
        <span class="material-symbols-outlined">cancel</span>
      </div>
      <h2 class="card-title">Không thể tải trang</h2>
      <p class="card-desc">{{ errorMessage }}</p>
      <div class="card-actions">
        <button class="btn-secondary" @click="goToHome">Về trang chủ</button>
      </div>
    </div>

    <!-- Ready / Selecting -->
    <div v-else-if="(status === 'ready' || status === 'selecting') && page" class="main-layout">

      <!-- Header thông tin công ty & vị trí -->
      <div class="info-header">
        <div class="info-header__badge">
          <span class="material-symbols-outlined">event_note</span>
          Chọn lịch phỏng vấn
        </div>
        <h1 class="info-header__title">{{ page.jobTitle }}</h1>
        <p class="info-header__company">{{ page.companyName }}</p>

        <div class="info-meta">
          <div class="info-meta__item">
            <span class="material-symbols-outlined">layers</span>
            <span>Vòng {{ page.roundNumber }} — {{ page.roundName }}</span>
          </div>
          <div class="info-meta__item info-meta__item--deadline">
            <span class="material-symbols-outlined">timer</span>
            <span>Hạn chót: <strong>{{ formatDate(page.deadline).time }} · {{ formatDate(page.deadline).date }}</strong></span>
          </div>
        </div>
      </div>

      <!-- Hướng dẫn -->
      <div class="guide-box">
        <span class="material-symbols-outlined">info</span>
        <p>Vui lòng chọn <strong>một</strong> khung giờ phỏng vấn phù hợp với lịch của bạn. Sau khi xác nhận, bạn sẽ nhận được email thông báo chi tiết.</p>
      </div>

      <!-- Danh sách slots -->
      <div class="slots-grid">
        <label
          v-for="slot in page.slots"
          :key="slot.id"
          class="slot-item"
          :class="{
            'slot-item--selected': selectedSlotId === slot.id,
            'slot-item--full': availableSpots(slot.maxCandidates, slot.registeredCount) === 0,
          }"
        >
          <input
            type="radio"
            :value="slot.id"
            v-model="selectedSlotId"
            :disabled="availableSpots(slot.maxCandidates, slot.registeredCount) === 0 || status === 'selecting'"
            class="slot-radio"
          />

          <!-- Cột trái: ngày giờ -->
          <div class="slot-datetime">
            <span class="slot-datetime__dow">{{ formatDate(slot.startTime).dayOfWeek }}</span>
            <span class="slot-datetime__date">{{ formatDate(slot.startTime).date }}</span>
            <span class="slot-datetime__time">{{ formatTimeRange(slot.startTime, slot.endTime) }}</span>
          </div>

          <!-- Cột phải: thông tin -->
          <div class="slot-info">
            <div class="slot-type-badge" :class="`slot-type-badge--${slot.interviewType}`">
              <span class="material-symbols-outlined">{{ interviewTypeIcon(slot.interviewType) }}</span>
              {{ interviewTypeLabel(slot.interviewType) }}
            </div>

            <div v-if="slot.interviewerName" class="slot-detail">
              <span class="material-symbols-outlined">person</span>
              <span>{{ slot.interviewerName }}</span>
            </div>
            <div v-if="slot.meetingLink" class="slot-detail">
              <span class="material-symbols-outlined">link</span>
              <a :href="slot.meetingLink" target="_blank" class="slot-link" @click.stop>Xem link phỏng vấn</a>
            </div>
            <div v-if="slot.location" class="slot-detail">
              <span class="material-symbols-outlined">location_on</span>
              <span>{{ slot.location }}</span>
            </div>

            <div class="slot-spots" :class="{ 'slot-spots--low': availableSpots(slot.maxCandidates, slot.registeredCount) <= 2 }">
              <span class="material-symbols-outlined">group</span>
              <span v-if="availableSpots(slot.maxCandidates, slot.registeredCount) > 0">
                Còn {{ availableSpots(slot.maxCandidates, slot.registeredCount) }} chỗ trống
              </span>
              <span v-else class="slot-spots--full-text">Đã đầy chỗ</span>
            </div>
          </div>

          <!-- Tick chọn -->
          <div class="slot-check">
            <span class="material-symbols-outlined">
              {{ selectedSlotId === slot.id ? 'radio_button_checked' : 'radio_button_unchecked' }}
            </span>
          </div>
        </label>
      </div>

      <!-- Nút xác nhận -->
      <div class="confirm-footer">
        <button
          class="btn-primary btn-primary--large"
          :disabled="!selectedSlotId || status === 'selecting'"
          @click="handleConfirm"
        >
          <span v-if="status === 'selecting'" class="material-symbols-outlined spin">autorenew</span>
          <span v-else class="material-symbols-outlined">check_circle</span>
          {{ status === 'selecting' ? 'Đang xác nhận...' : 'Xác nhận lịch phỏng vấn' }}
        </button>
        <button class="btn-secondary" @click="goToHome">Về trang chủ</button>
      </div>

    </div>

  </div>
</template>

<style scoped>
.select-slot-page {
  min-height: 100vh;
  background: #f1f5f9;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 2rem 1rem 4rem;
  font-family: 'Manrope', sans-serif;
}

/* ── Trạng thái đơn giản (loading/error/success...) ── */
.slot-card--centered {
  background: #fff;
  border-radius: 1.25rem;
  padding: 2.5rem;
  max-width: 520px;
  width: 100%;
  text-align: center;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.25rem;
  margin-top: 4rem;
}

.status-icon {
  width: 5rem;
  height: 5rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.status-icon span { font-size: 2.75rem; }
.status-icon--loading { background: #eff6ff; color: #2563eb; }
.status-icon--success { background: #f0fdf4; color: #16a34a; }
.status-icon--warning { background: #fff7ed; color: #ea580c; }
.status-icon--error   { background: #fff1f2; color: #e11d48; }

.card-title {
  font-size: 1.375rem;
  font-weight: 800;
  color: #0f172a;
  margin: 0;
}
.card-desc {
  font-size: 0.9375rem;
  color: #64748b;
  margin: 0;
  line-height: 1.7;
  font-weight: 500;
}
.card-actions {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  width: 100%;
}

/* ── Layout chính ── */
.main-layout {
  width: 100%;
  max-width: 680px;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

/* ── Header thông tin ── */
.info-header {
  background: #fff;
  border-radius: 1.25rem;
  padding: 1.75rem 2rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}
.info-header__badge {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  background: #eff6ff;
  color: #005ea4;
  font-size: 0.75rem;
  font-weight: 700;
  padding: 0.3rem 0.75rem;
  border-radius: 2rem;
  width: fit-content;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}
.info-header__badge span { font-size: 1rem; }

.info-header__title {
  font-size: 1.375rem;
  font-weight: 800;
  color: #0f172a;
  margin: 0.25rem 0 0;
}
.info-header__company {
  font-size: 0.9375rem;
  font-weight: 600;
  color: #475569;
  margin: 0;
}

.info-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.875rem;
  margin-top: 0.5rem;
}
.info-meta__item {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  font-size: 0.875rem;
  color: #64748b;
  font-weight: 600;
}
.info-meta__item span.material-symbols-outlined { font-size: 1.1rem; color: #94a3b8; }
.info-meta__item--deadline { color: #b45309; }
.info-meta__item--deadline span.material-symbols-outlined { color: #f59e0b; }
.info-meta__item--deadline strong { color: #92400e; }

/* ── Hướng dẫn ── */
.guide-box {
  background: #eff6ff;
  border: 1px solid #bfdbfe;
  border-radius: 0.875rem;
  padding: 0.875rem 1.125rem;
  display: flex;
  align-items: flex-start;
  gap: 0.625rem;
  font-size: 0.875rem;
  color: #1e40af;
  font-weight: 500;
  line-height: 1.6;
}
.guide-box span.material-symbols-outlined {
  font-size: 1.2rem;
  flex-shrink: 0;
  margin-top: 0.1rem;
  color: #3b82f6;
}
.guide-box p { margin: 0; }

/* ── Danh sách slots ── */
.slots-grid {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.slot-item {
  background: #fff;
  border: 2px solid #e2e8f0;
  border-radius: 1rem;
  padding: 1.125rem 1.25rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  cursor: pointer;
  transition: border-color 0.15s, box-shadow 0.15s, transform 0.1s;
  position: relative;
}
.slot-item:hover:not(.slot-item--full) {
  border-color: #93c5fd;
  box-shadow: 0 4px 16px rgba(0, 94, 164, 0.08);
  transform: translateY(-1px);
}
.slot-item--selected {
  border-color: #005ea4 !important;
  background: #f0f7ff;
  box-shadow: 0 4px 16px rgba(0, 94, 164, 0.15);
}
.slot-item--full {
  opacity: 0.5;
  cursor: not-allowed;
  background: #f8fafc;
}

.slot-radio { display: none; }

/* Ngày giờ */
.slot-datetime {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 90px;
  border-right: 1px solid #e2e8f0;
  padding-right: 1rem;
  flex-shrink: 0;
}
.slot-datetime__dow {
  font-size: 0.7rem;
  font-weight: 700;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}
.slot-datetime__date {
  font-size: 0.875rem;
  font-weight: 800;
  color: #0f172a;
  margin: 0.1rem 0;
}
.slot-datetime__time {
  font-size: 0.9375rem;
  font-weight: 800;
  color: #005ea4;
}

/* Info */
.slot-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.slot-type-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.3rem;
  font-size: 0.75rem;
  font-weight: 700;
  padding: 0.2rem 0.6rem;
  border-radius: 2rem;
  width: fit-content;
}
.slot-type-badge span { font-size: 0.9rem; }
.slot-type-badge--online  { background: #f0fdf4; color: #16a34a; }
.slot-type-badge--onsite  { background: #fff7ed; color: #c2410c; }
.slot-type-badge--phone   { background: #faf5ff; color: #7c3aed; }

.slot-detail {
  display: flex;
  align-items: center;
  gap: 0.35rem;
  font-size: 0.8125rem;
  color: #64748b;
  font-weight: 600;
}
.slot-detail span.material-symbols-outlined { font-size: 0.95rem; color: #94a3b8; flex-shrink: 0; }

.slot-link {
  color: #005ea4;
  text-decoration: none;
  font-weight: 700;
}
.slot-link:hover { text-decoration: underline; }

.slot-spots {
  display: flex;
  align-items: center;
  gap: 0.35rem;
  font-size: 0.8rem;
  font-weight: 700;
  color: #16a34a;
  margin-top: 0.1rem;
}
.slot-spots span.material-symbols-outlined { font-size: 0.95rem; }
.slot-spots--low { color: #ea580c; }
.slot-spots--full-text { color: #94a3b8; }

/* Tick */
.slot-check {
  flex-shrink: 0;
  color: #cbd5e1;
  font-size: 1.4rem;
}
.slot-item--selected .slot-check { color: #005ea4; }

/* ── Footer xác nhận ── */
.confirm-footer {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  padding-top: 0.25rem;
}

/* ── Buttons ── */
.btn-primary {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  background: #005ea4;
  color: #fff;
  border: none;
  border-radius: 0.875rem;
  padding: 0.875rem 1.5rem;
  font-size: 0.9375rem;
  font-weight: 700;
  font-family: inherit;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 4px 12px rgba(0, 94, 164, 0.2);
  width: 100%;
}
.btn-primary span { font-size: 1.2rem; }
.btn-primary:hover:not(:disabled) {
  background: #004881;
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(0, 94, 164, 0.3);
}
.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}
.btn-primary--large { padding: 1rem 1.5rem; font-size: 1rem; }

.btn-secondary {
  background: #f1f5f9;
  color: #475569;
  border: none;
  border-radius: 0.875rem;
  padding: 0.875rem 1.5rem;
  font-size: 0.9375rem;
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  transition: all 0.2s;
  width: 100%;
}
.btn-secondary:hover { background: #e2e8f0; color: #0f172a; }

@keyframes spin {
  from { transform: rotate(0deg); }
  to   { transform: rotate(360deg); }
}
.spin {
  animation: spin 1.2s linear infinite;
  display: inline-block;
}

@media (max-width: 480px) {
  .info-header { padding: 1.25rem; }
  .slot-item { padding: 1rem; gap: 0.75rem; }
  .slot-datetime { min-width: 76px; }
}
</style>
