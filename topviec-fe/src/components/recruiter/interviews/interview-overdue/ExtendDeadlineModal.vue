<template>
  <GlobalModal
    :visible="visible"
    title="Gia hạn phản hồi"
    :subtitle="candidateName ? `Gia hạn thời gian cho ứng viên: ${candidateName}` : 'Thiết lập thời gian phản hồi mới cho ứng viên'"
    icon="more_time"
    max-width="md"
    confirm-text="Cập nhật"
    :loading="loading"
    @close="$emit('close')"
    @confirm="handleConfirm"
  >
    <div class="extend-modal">
      <div class="extend-modal__field">
        <label class="extend-modal__label">Số ngày muốn gia hạn thêm</label>
        <div class="extend-modal__input-wrapper">
          <input
            v-model.number="days"
            type="number"
            min="1"
            max="30"
            class="extend-modal__input"
            placeholder="Nhập số ngày..."
            @keyup.enter="handleConfirm"
          />
          <div class="extend-modal__unit">ngày</div>
        </div>
      </div>

      <!-- Quick choices -->
      <div class="extend-modal__quick">
        <button
          v-for="val in [1, 3, 7]"
          :key="val"
          type="button"
          class="extend-modal__chip"
          :class="{ 'extend-modal__chip--active': days === val }"
          @click="days = val"
        >
          +{{ val }} ngày
        </button>
      </div>

      <!-- Preview -->
      <div class="extend-modal__preview">
        <div class="extend-modal__preview-icon">
          <span class="material-symbols-outlined">event_upcoming</span>
        </div>
        <div class="extend-modal__preview-content">
          <p class="extend-modal__preview-label">Hạn phản hồi mới dự kiến:</p>
          <p class="extend-modal__preview-date">
            {{ formatNewDeadline }}
          </p>
        </div>
      </div>
    </div>
  </GlobalModal>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import GlobalModal from '@/components/ui/GlobalModal.vue'

const props = defineProps<{
  visible: boolean
  loading?: boolean
  candidateName?: string
  currentDeadline?: string
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'confirm', days: number): void
}>()

const days = ref(2)

// Reset days when modal opens
watch(() => props.visible, (newVal) => {
  if (newVal) days.value = 2
})

const formatNewDeadline = computed(() => {
  const baseDate = props.currentDeadline ? new Date(props.currentDeadline) : new Date()
  const newDate = new Date(baseDate.getTime() + days.value * 86400000)
  return newDate.toLocaleString('vi-VN', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
})

function handleConfirm() {
  if (days.value > 0) {
    emit('confirm', days.value)
  }
}
</script>

<style scoped>
.extend-modal {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.extend-modal__label {
  display: block;
  font-size: 0.875rem;
  font-weight: 700;
  color: #475569;
  margin-bottom: 0.625rem;
}

.extend-modal__input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.extend-modal__input {
  width: 100%;
  padding: 0.75rem 1rem;
  padding-right: 4rem;
  background: #f8fafc;
  border: 1.5px solid #e2e8f0;
  border-radius: 0.75rem;
  font-size: 1rem;
  font-weight: 700;
  color: #0f172a;
  outline: none;
  transition: all 0.2s;
}

.extend-modal__input:focus {
  background: #fff;
  border-color: #4B9AF6;
  box-shadow: 0 0 0 4px rgba(75,154,246,0.1);
}

.extend-modal__unit {
  position: absolute;
  right: 1rem;
  font-size: 0.875rem;
  font-weight: 600;
  color: #94a3b8;
}

.extend-modal__quick {
  display: flex;
  gap: 0.75rem;
}

.extend-modal__chip {
  flex: 1;
  padding: 0.625rem;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 0.625rem;
  font-size: 0.875rem;
  font-weight: 600;
  color: #64748b;
  cursor: pointer;
  transition: all 0.15s;
}

.extend-modal__chip:hover {
  border-color: #cbd5e1;
  background: #f8fafc;
}

.extend-modal__chip--active {
  background: #4B9AF6;
  border-color: #4B9AF6;
  color: #fff;
  box-shadow: 0 4px 12px rgba(75,154,246,0.2);
}

.extend-modal__preview {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: #f0f9ff;
  border: 1px dashed #bae6fd;
  border-radius: 0.75rem;
}

.extend-modal__preview-icon {
  width: 2.5rem;
  height: 2.5rem;
  background: #fff;
  border-radius: 0.625rem;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #0ea5e9;
  flex-shrink: 0;
}

.extend-modal__preview-label {
  font-size: 0.75rem;
  font-weight: 600;
  color: #64748b;
  margin: 0 0 0.1rem;
}

.extend-modal__preview-date {
  font-size: 0.875rem;
  font-weight: 700;
  color: #0369a1;
  margin: 0;
}
</style>
