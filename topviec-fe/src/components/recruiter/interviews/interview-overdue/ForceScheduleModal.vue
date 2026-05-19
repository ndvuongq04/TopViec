<template>
  <GlobalModal
    :visible="visible"
    title="Đặt lịch phỏng vấn"
    :subtitle="candidateName"
    icon="event_available"
    max-width="lg"
    confirm-text="Xác nhận đặt lịch"
    confirm-icon="send"
    :loading="loading"
    @close="$emit('close')"
    @confirm="handleConfirm"
  >
    <div class="reschedule-form">
      <!-- Ngày & Giờ -->
      <div class="reschedule-form__row">
        <div class="reschedule-form__field">
          <label class="reschedule-form__label">Ngày phỏng vấn <span class="reschedule-form__required">*</span></label>
          <input
            v-model="form.date"
            type="date"
            class="reschedule-form__input"
            :class="{ 'reschedule-form__input--error': errors.date }"
            @change="errors.date = ''"
          />
          <span v-if="errors.date" class="reschedule-form__error">{{ errors.date }}</span>
        </div>
        <div class="reschedule-form__field">
          <label class="reschedule-form__label">Giờ phỏng vấn <span class="reschedule-form__required">*</span></label>
          <input
            v-model="form.time"
            type="time"
            class="reschedule-form__input"
            :class="{ 'reschedule-form__input--error': errors.time }"
            @change="errors.time = ''"
          />
          <span v-if="errors.time" class="reschedule-form__error">{{ errors.time }}</span>
        </div>
      </div>

      <!-- Hình thức -->
      <div class="reschedule-form__field">
        <label class="reschedule-form__label">Hình thức phỏng vấn</label>
        <div class="reschedule-form__mode-toggle">
          <button
            type="button"
            class="reschedule-form__mode-btn"
            :class="{ 'reschedule-form__mode-btn--active': form.interviewType === 'online' }"
            @click="form.interviewType = 'online'"
          >
            <span class="material-symbols-outlined">videocam</span>
            Trực tuyến
          </button>
          <button
            type="button"
            class="reschedule-form__mode-btn"
            :class="{ 'reschedule-form__mode-btn--active': form.interviewType === 'onsite' }"
            @click="form.interviewType = 'onsite'"
          >
            <span class="material-symbols-outlined">location_on</span>
            Trực tiếp
          </button>
        </div>
      </div>

      <!-- Link hoặc địa điểm -->
      <div v-if="form.interviewType === 'online'" class="reschedule-form__field">
        <label class="reschedule-form__label">Link họp (Google Meet / Zoom) <span class="reschedule-form__required">*</span></label>
        <input
          v-model="form.meetingLink"
          type="url"
          class="reschedule-form__input"
          :class="{ 'reschedule-form__input--error': errors.meetingLink }"
          placeholder="https://meet.google.com/..."
          @input="errors.meetingLink = ''"
        />
        <span v-if="errors.meetingLink" class="reschedule-form__error">{{ errors.meetingLink }}</span>
      </div>
      <div v-else class="reschedule-form__field">
        <label class="reschedule-form__label">Địa điểm phỏng vấn <span class="reschedule-form__required">*</span></label>
        <input
          v-model="form.location"
          type="text"
          class="reschedule-form__input"
          :class="{ 'reschedule-form__input--error': errors.location }"
          placeholder="Nhập địa chỉ văn phòng..."
          @input="errors.location = ''"
        />
        <span v-if="errors.location" class="reschedule-form__error">{{ errors.location }}</span>
      </div>

      <!-- Ghi chú -->
      <div class="reschedule-form__field">
        <label class="reschedule-form__label">Ghi chú cho người phỏng vấn</label>
        <textarea
          v-model="form.note"
          class="reschedule-form__input reschedule-form__textarea"
          rows="2"
          placeholder="Nhập ghi chú..."
        />
      </div>
    </div>
  </GlobalModal>
</template>

<script setup lang="ts">
import { reactive, watch } from 'vue'
import GlobalModal from '@/components/ui/GlobalModal.vue'

const props = defineProps<{
  visible: boolean
  loading?: boolean
  candidateName?: string
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'confirm', data: any): void
}>()

const form = reactive({
  date: '',
  time: '',
  interviewType: 'online' as 'online' | 'onsite',
  meetingLink: '',
  location: '',
  note: ''
})

const errors = reactive({
  date: '',
  time: '',
  meetingLink: '',
  location: ''
})

watch(() => props.visible, (newVal) => {
  if (newVal) {
    Object.assign(form, {
      date: '',
      time: '',
      interviewType: 'online',
      meetingLink: '',
      location: '',
      note: ''
    })
    Object.assign(errors, {
      date: '',
      time: '',
      meetingLink: '',
      location: ''
    })
  }
})

function validate(): boolean {
  let ok = true
  if (!form.date) { errors.date = 'Vui lòng chọn ngày.'; ok = false }
  if (!form.time) { errors.time = 'Vui lòng chọn giờ.'; ok = false }
  
  if (form.interviewType === 'online' && !form.meetingLink.trim()) {
    errors.meetingLink = 'Vui lòng nhập link họp.'; ok = false
  }
  if (form.interviewType === 'onsite' && !form.location.trim()) {
    errors.location = 'Vui lòng nhập địa điểm.'; ok = false
  }
  
  return ok
}

function handleConfirm() {
  if (validate()) {
    emit('confirm', { ...form })
  }
}
</script>

<style scoped>
.reschedule-form { display: flex; flex-direction: column; gap: 1rem; padding: 0.25rem 0; }
.reschedule-form__row { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
.reschedule-form__field { display: flex; flex-direction: column; gap: 0.375rem; }
.reschedule-form__label { font-size: 0.75rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.08em; color: #64748b; }
.reschedule-form__input {
  width: 100%; padding: 0.75rem 1rem; box-sizing: border-box;
  background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 0.625rem;
  font-size: 0.875rem; font-family: inherit; color: #0f172a;
  outline: none; transition: border-color 0.18s, box-shadow 0.18s;
}
.reschedule-form__input:focus { border-color: #4b9af6; box-shadow: 0 0 0 3px rgba(75,154,246,0.15); }
.reschedule-form__textarea { resize: none; }
.reschedule-form__mode-toggle {
  display: flex; gap: 0.25rem; padding: 0.25rem;
  background: #f8fafc; border-radius: 0.75rem; width: fit-content;
}
.reschedule-form__mode-btn {
  display: flex; align-items: center; gap: 0.4rem;
  padding: 0.5rem 1.25rem; border-radius: 0.625rem; border: none;
  background: transparent; font-size: 0.875rem; font-weight: 500;
  font-family: inherit; color: #64748b; cursor: pointer;
  transition: background 0.18s, color 0.18s, box-shadow 0.18s;
}
.reschedule-form__mode-btn .material-symbols-outlined { font-size: 1.1rem; }
.reschedule-form__mode-btn--active {
  background: #fff; color: #4b9af6; font-weight: 700;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}
.reschedule-form__required { color: #ef4444; margin-left: 2px; }
.reschedule-form__input--error { border-color: #ef4444 !important; }
.reschedule-form__input--error:focus { box-shadow: 0 0 0 3px rgba(239,68,68,0.15) !important; }
.reschedule-form__error { font-size: 0.75rem; color: #ef4444; font-weight: 500; margin-top: 2px; }
</style>
