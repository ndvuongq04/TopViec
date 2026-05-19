<template>
  <section class="summary-card">
    <div class="summary-card__stripe" />

    <div class="summary-card__body">
      <h4 class="summary-card__title">Tổng quan lịch hẹn</h4>

      <div class="summary-list">
        <!-- Ứng viên -->
        <div class="summary-item">
          <div class="summary-item__icon">
            <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">person</span>
          </div>
          <div>
            <p class="summary-item__label">Ứng viên</p>
            <p class="summary-item__value">{{ candidateName || 'Chưa chọn ứng viên' }}</p>
          </div>
        </div>

        <!-- Thời gian -->
        <div class="summary-item">
          <div class="summary-item__icon">
            <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">calendar_today</span>
          </div>
          <div>
            <p class="summary-item__label">Thời gian</p>
            <p class="summary-item__value">{{ timeDisplay }}</p>
          </div>
        </div>

        <!-- Hình thức -->
        <div class="summary-item">
          <div class="summary-item__icon">
            <span
              class="material-symbols-outlined"
              style="font-variation-settings: 'FILL' 1;"
            >{{ mode === 'online' ? 'videocam' : 'location_on' }}</span>
          </div>
          <div>
            <p class="summary-item__label">Hình thức</p>
            <p class="summary-item__value">{{ mode === 'online' ? 'Phỏng vấn Online' : 'Phỏng vấn Trực tiếp' }}</p>
          </div>
        </div>
      </div>

      <hr class="summary-card__divider" />

      <button
        class="summary-card__submit"
        :disabled="loading"
        @click="$emit('submit')"
      >
        <span v-if="!loading" class="material-symbols-outlined">send</span>
        <span v-else class="material-symbols-outlined summary-card__spin">progress_activity</span>
        {{ loading ? 'Đang gửi...' : 'Xác nhận &amp; Gửi email' }}
      </button>

      <p class="summary-card__note">
        Bằng cách nhấn nút, hệ thống sẽ tự động tạo sự kiện lịch và gửi thư mời cho ứng viên.
      </p>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  candidateName: string
  date: string
  time: string
  mode: 'online' | 'offline'
  loading?: boolean
}>()

defineEmits<{
  (e: 'submit'): void
}>()

const timeDisplay = computed(() => {
  if (!props.date && !props.time) return '--/--/---- lúc --:--'
  const d = props.date
    ? new Date(props.date).toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' })
    : '--/--/----'
  return `${d} lúc ${props.time || '--:--'}`
})
</script>

<style scoped>
.summary-card {
  background: #fff;
  border-radius: 0.75rem;
  border: 1px solid rgba(75,154,246,0.12);
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
  overflow: hidden;
  position: sticky;
  top: 5.5rem;
}

.summary-card__stripe {
  height: 0.375rem;
  background: #4B9AF6;
}

.summary-card__body {
  padding: 1.5rem;
}

.summary-card__title {
  font-size: 1.125rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 1.25rem;
}

/* Summary list */
.summary-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.summary-item {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
}

.summary-item__icon {
  width: 2rem;
  height: 2rem;
  border-radius: 0.5rem;
  background: #eff6ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #4B9AF6;
  flex-shrink: 0;
}

.summary-item__icon .material-symbols-outlined {
  font-size: 1rem;
}

.summary-item__label {
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #94a3b8;
  margin: 0 0 0.15rem;
}

.summary-item__value {
  font-size: 0.875rem;
  font-weight: 600;
  color: #0f172a;
  margin: 0;
}

.summary-card__divider {
  border: none;
  border-top: 1px solid #f1f5f9;
  margin: 1.5rem 0;
}

/* Submit button */
.summary-card__submit {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 1rem;
  background: #4B9AF6;
  color: #fff;
  border: none;
  border-radius: 0.75rem;
  font-size: 1rem;
  font-weight: 800;
  font-family: inherit;
  cursor: pointer;
  box-shadow: 0 4px 14px rgba(75,154,246,0.25);
  transition: transform 0.15s, box-shadow 0.15s, background 0.18s;
}

.summary-card__submit:hover:not(:disabled) {
  transform: scale(1.02);
  box-shadow: 0 6px 20px rgba(75,154,246,0.35);
}

.summary-card__submit:active:not(:disabled) {
  transform: scale(0.97);
}

.summary-card__submit:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.summary-card__spin {
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.summary-card__note {
  font-size: 0.75rem;
  color: #94a3b8;
  text-align: center;
  margin: 0.875rem 0 0;
  line-height: 1.6;
  padding: 0 0.5rem;
}
</style>