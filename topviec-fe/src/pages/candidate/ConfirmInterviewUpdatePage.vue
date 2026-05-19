<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePublicInterviewStore } from '@/stores/publicInterview.store'

type PageStatus = 'loading' | 'ready' | 'already-confirmed' | 'cancelled' | 'confirming' | 'success' | 'error'

const route = useRoute()
const router = useRouter()
const store = usePublicInterviewStore()

const status = ref<PageStatus>('loading')
const errorMessage = ref('')
const successMessage = ref('')

const info = computed(() => store.confirmUpdateInfo)

function formatDateTime(dateStr: string) {
  const d = new Date(dateStr)
  const time = `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
  const date = `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()}`
  return { time, date }
}

const token = route.query.token as string

onMounted(async () => {
  if (!token) {
    status.value = 'error'
    errorMessage.value = 'Liên kết xác nhận không hợp lệ hoặc đã hết hạn.'
    return
  }

  try {
    await store.fetchConfirmUpdateInfo(token)
    const s = store.confirmUpdateInfo?.status?.toLowerCase()
    if (s === 'confirmed' || store.confirmUpdateInfo?.confirmedByCandidate) {
      status.value = 'already-confirmed'
    } else if (s === 'cancelled' || s === 'no_show') {
      status.value = 'cancelled'
    } else {
      // scheduled → cho phép xác nhận
      status.value = 'ready'
    }
  } catch {
    status.value = 'error'
    errorMessage.value = store.error || 'Liên kết đã hết hạn hoặc không hợp lệ.'
  }
})

async function handleConfirm() {
  status.value = 'confirming'
  try {
    const res = await store.confirmUpdatedSchedule(token)
    successMessage.value = res?.message || 'Xác nhận thành công!'
    status.value = 'success'
  } catch {
    status.value = 'error'
    errorMessage.value = store.error || 'Có lỗi xảy ra. Vui lòng thử lại.'
  }
}

function goToInterviews() {
  router.push({ name: 'CandidateInterviews' })
}

function goToHome() {
  router.push({ name: 'home' })
}
</script>

<template>
  <div class="confirm-page">

    <!-- Loading -->
    <div v-if="status === 'loading'" class="confirm-card">
      <div class="confirm-icon confirm-icon--loading">
        <span class="material-symbols-outlined spin">autorenew</span>
      </div>
      <h2 class="confirm-title">Đang tải thông tin...</h2>
      <p class="confirm-desc">Vui lòng chờ trong giây lát.</p>
    </div>

    <!-- Ready: hiển thị thông tin và nút xác nhận -->
    <div v-else-if="status === 'ready' || status === 'confirming'" class="confirm-card">
      <div class="confirm-icon confirm-icon--info">
        <span class="material-symbols-outlined">event_available</span>
      </div>
      <h2 class="confirm-title">Xác nhận lịch phỏng vấn</h2>
      <p class="confirm-desc">Nhà tuyển dụng đã cập nhật lịch phỏng vấn. Vui lòng xem lại thông tin và xác nhận tham dự.</p>

      <div v-if="info" class="info-box">
        <div class="info-row">
          <span class="material-symbols-outlined info-icon">business</span>
          <div>
            <label>Công ty</label>
            <p>{{ info.companyName }}</p>
          </div>
        </div>
        <div class="info-row">
          <span class="material-symbols-outlined info-icon">work</span>
          <div>
            <label>Vị trí</label>
            <p>{{ info.jobTitle }}</p>
          </div>
        </div>
        <div class="info-row">
          <span class="material-symbols-outlined info-icon">layers</span>
          <div>
            <label>Vòng phỏng vấn</label>
            <p>Vòng {{ info.roundNumber }} — {{ info.roundName }}</p>
          </div>
        </div>
        <div class="info-row">
          <span class="material-symbols-outlined info-icon">schedule</span>
          <div>
            <label>Thời gian</label>
            <p>{{ formatDateTime(info.scheduledAt).time }} · {{ formatDateTime(info.scheduledAt).date }} ({{ info.durationMinutes }} phút)</p>
          </div>
        </div>
        <div class="info-row">
          <span class="material-symbols-outlined info-icon">videocam</span>
          <div>
            <label>Hình thức</label>
            <p>{{ info.interviewType === 'online' ? 'Trực tuyến (Online)' : info.interviewType === 'onsite' ? 'Trực tiếp tại văn phòng' : 'Qua điện thoại' }}</p>
          </div>
        </div>
        <div v-if="info.meetingLink" class="info-row">
          <span class="material-symbols-outlined info-icon">link</span>
          <div>
            <label>Link phỏng vấn</label>
            <a :href="info.meetingLink" target="_blank" class="meeting-link">{{ info.meetingLink }}</a>
          </div>
        </div>
        <div v-if="info.location" class="info-row">
          <span class="material-symbols-outlined info-icon">location_on</span>
          <div>
            <label>Địa điểm</label>
            <p>{{ info.location }}</p>
          </div>
        </div>
      </div>

      <div class="confirm-actions">
        <button class="btn-primary" :disabled="status === 'confirming'" @click="handleConfirm">
          <span v-if="status === 'confirming'" class="material-symbols-outlined spin">autorenew</span>
          <span v-else class="material-symbols-outlined">check_circle</span>
          {{ status === 'confirming' ? 'Đang xác nhận...' : 'Xác nhận tham dự' }}
        </button>
        <button class="btn-secondary" @click="goToHome">Về trang chủ</button>
      </div>
    </div>

    <!-- Already confirmed -->
    <div v-else-if="status === 'already-confirmed'" class="confirm-card">
      <div class="confirm-icon confirm-icon--success">
        <span class="material-symbols-outlined">check_circle</span>
      </div>
      <h2 class="confirm-title">Đã xác nhận trước đó</h2>
      <p class="confirm-desc">Bạn đã xác nhận tham dự lịch phỏng vấn này rồi. Hẹn gặp bạn tại buổi phỏng vấn!</p>
      <div class="confirm-actions">
        <button class="btn-primary" @click="goToInterviews">
          <span class="material-symbols-outlined">event</span>
          Xem lịch phỏng vấn
        </button>
        <button class="btn-secondary" @click="goToHome">Về trang chủ</button>
      </div>
    </div>

    <!-- Cancelled -->
    <div v-else-if="status === 'cancelled'" class="confirm-card">
      <div class="confirm-icon confirm-icon--warning">
        <span class="material-symbols-outlined">event_busy</span>
      </div>
      <h2 class="confirm-title">Lịch phỏng vấn đã bị huỷ</h2>
      <p class="confirm-desc">Lịch phỏng vấn này đã bị huỷ. Vui lòng liên hệ nhà tuyển dụng để biết thêm thông tin.</p>
      <div class="confirm-actions">
        <button class="btn-secondary" @click="goToHome">Về trang chủ</button>
      </div>
    </div>

    <!-- Success -->
    <div v-else-if="status === 'success'" class="confirm-card">
      <div class="confirm-icon confirm-icon--success">
        <span class="material-symbols-outlined">check_circle</span>
      </div>
      <h2 class="confirm-title">Xác nhận thành công!</h2>
      <p class="confirm-desc">{{ successMessage }}</p>
      <div class="confirm-actions">
        <button class="btn-primary" @click="goToInterviews">
          <span class="material-symbols-outlined">event</span>
          Xem lịch phỏng vấn
        </button>
        <button class="btn-secondary" @click="goToHome">Về trang chủ</button>
      </div>
    </div>

    <!-- Error -->
    <div v-else class="confirm-card">
      <div class="confirm-icon confirm-icon--error">
        <span class="material-symbols-outlined">cancel</span>
      </div>
      <h2 class="confirm-title">Xác nhận thất bại</h2>
      <p class="confirm-desc">{{ errorMessage }}</p>
      <div class="confirm-actions">
        <button class="btn-secondary" @click="goToHome">Về trang chủ</button>
      </div>
    </div>

  </div>
</template>

<style scoped>
.confirm-page {
  min-height: 100vh;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1.5rem;
  font-family: 'Manrope', sans-serif;
}

.confirm-card {
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
}

.confirm-icon {
  width: 5rem;
  height: 5rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.confirm-icon span { font-size: 2.75rem; }

.confirm-icon--loading { background: #eff6ff; color: #2563eb; }
.confirm-icon--info    { background: #eff6ff; color: #005ea4; }
.confirm-icon--success { background: #f0fdf4; color: #16a34a; }
.confirm-icon--warning { background: #fff7ed; color: #ea580c; }
.confirm-icon--error   { background: #fff1f2; color: #e11d48; }

.confirm-title {
  font-size: 1.375rem;
  font-weight: 800;
  color: #0f172a;
  margin: 0;
}

.confirm-desc {
  font-size: 0.9375rem;
  color: #64748b;
  margin: 0;
  line-height: 1.7;
  font-weight: 500;
}

/* Info box */
.info-box {
  width: 100%;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 0.875rem;
  padding: 1.25rem;
  display: flex;
  flex-direction: column;
  gap: 0.875rem;
  text-align: left;
}

.info-row {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
}

.info-icon {
  font-size: 1.2rem;
  color: #005ea4;
  margin-top: 0.1rem;
  flex-shrink: 0;
}

.info-row label {
  display: block;
  font-size: 0.7rem;
  font-weight: 700;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 0.125rem;
}

.info-row p {
  font-size: 0.9rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
}

.meeting-link {
  font-size: 0.9rem;
  font-weight: 700;
  color: #005ea4;
  text-decoration: none;
  word-break: break-all;
}
.meeting-link:hover { text-decoration: underline; }

/* Actions */
.confirm-actions {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  width: 100%;
}

.btn-primary {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  background: #005ea4;
  color: #fff;
  border: none;
  border-radius: 0.75rem;
  padding: 0.875rem 1.5rem;
  font-size: 0.9375rem;
  font-weight: 700;
  font-family: inherit;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 4px 12px rgba(0, 94, 164, 0.2);
}
.btn-primary span { font-size: 1.2rem; }
.btn-primary:hover:not(:disabled) {
  background: #004881;
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(0, 94, 164, 0.3);
}
.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-secondary {
  background: #f1f5f9;
  color: #475569;
  border: none;
  border-radius: 0.75rem;
  padding: 0.875rem 1.5rem;
  font-size: 0.9375rem;
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-secondary:hover { background: #e2e8f0; color: #0f172a; }

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
.spin {
  animation: spin 1.2s linear infinite;
  display: inline-block;
}
</style>
