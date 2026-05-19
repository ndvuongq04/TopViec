<template>
  <section class="section">
    <div class="section__header">
      <h3 class="section__title">
        <span class="material-symbols-outlined">schedule</span>
        Thời gian &amp; Hình thức
      </h3>
    </div>

    <div class="section__body">
      <!-- Ngày & Giờ -->
      <div class="date-row">
        <div class="field">
          <label class="field__label">Ngày phỏng vấn</label>
          <input
            class="field__input"
            type="date"
            :value="modelValue.date"
            @input="emit('update:modelValue', {
              ...modelValue,
              date: ($event.target as HTMLInputElement).value
            })"
          />
        </div>
        <div class="field">
          <label class="field__label">Giờ phỏng vấn</label>
          <input
            class="field__input"
            type="time"
            :value="modelValue.time"
            @input="emit('update:modelValue', {
              ...modelValue,
              time: ($event.target as HTMLInputElement).value
            })"
          />
        </div>
      </div>

      <!-- Hình thức -->
      <div class="field">
        <label class="field__label">Hình thức phỏng vấn</label>
        <div class="mode-toggle">
          <button
            class="mode-btn"
            :class="{ 'mode-btn--active': modelValue.mode === 'online' }"
            type="button"
            @click="emit('update:modelValue', { ...modelValue, mode: 'online' })"
          >
            <span class="material-symbols-outlined">videocam</span>
            Online
          </button>
          <button
            class="mode-btn"
            :class="{ 'mode-btn--active': modelValue.mode === 'offline' }"
            type="button"
            @click="emit('update:modelValue', { ...modelValue, mode: 'offline' })"
          >
            <span class="material-symbols-outlined">location_on</span>
            Trực tiếp
          </button>
        </div>
      </div>

      <!-- Link họp (chỉ hiện khi online) -->
      <Transition name="fade-slide">
        <div v-if="modelValue.mode === 'online'" class="field">
          <label class="field__label">Link họp trực tuyến (Google Meet/Zoom)</label>
          <div class="field__input-wrap">
            <span class="material-symbols-outlined field__icon">link</span>
            <input
              class="field__input field__input--icon"
              type="url"
              placeholder="https://meet.google.com/xxx-xxxx-xxx"
              :value="modelValue.meetingLink"
              @input="emit('update:modelValue', {
                ...modelValue,
                meetingLink: ($event.target as HTMLInputElement).value
              })"
            />
          </div>
        </div>

        <!-- Địa điểm (chỉ hiện khi offline) -->
        <div v-else class="field">
          <label class="field__label">Địa điểm phỏng vấn</label>
          <div class="field__input-wrap">
            <span class="material-symbols-outlined field__icon">location_on</span>
            <input
              class="field__input field__input--icon"
              type="text"
              placeholder="Nhập địa chỉ văn phòng..."
              :value="modelValue.location"
              @input="emit('update:modelValue', {
                ...modelValue,
                location: ($event.target as HTMLInputElement).value
              })"
            />
          </div>
        </div>
      </Transition>
    </div>
  </section>
</template>

<script setup lang="ts">
interface ModelValue {
  date: string
  time: string
  mode: 'online' | 'offline'
  meetingLink: string
  location: string
}

defineProps<{
  modelValue: ModelValue
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', v: ModelValue): void
}>()
</script>

<style scoped>
.section {
  background: #fff;
  border-radius: 0.75rem;
  border: 1px solid rgba(203, 213, 225, 0.3);
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  overflow: hidden;
}

.section__header {
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid #f8fafc;
  background: rgba(248,250,252,0.5);
}

.section__title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.125rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
}

.section__title .material-symbols-outlined {
  color: #4B9AF6;
  font-size: 1.2rem;
}

.section__body {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

/* Date row */
.date-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

@media (max-width: 480px) {
  .date-row { grid-template-columns: 1fr; }
}

/* Field */
.field__label {
  display: block;
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #64748b;
  margin-bottom: 0.5rem;
}

.field__input {
  width: 100%;
  padding: 0.75rem 1rem;
  background: #f1f4f9;
  border: none;
  border-radius: 0.5rem;
  font-size: 0.875rem;
  font-family: inherit;
  color: #0f172a;
  outline: none;
  box-sizing: border-box;
  transition: box-shadow 0.18s;
}

.field__input:focus {
  box-shadow: 0 0 0 2px rgba(75,154,246,0.2);
}

.field__input-wrap {
  position: relative;
}

.field__icon {
  position: absolute;
  left: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
  font-size: 1.1rem;
  pointer-events: none;
}

.field__input--icon {
  padding-left: 2.5rem;
}

/* Mode toggle */
.mode-toggle {
  display: flex;
  gap: 0.25rem;
  padding: 0.25rem;
  background: #f8fafd;
  border-radius: 0.75rem;
  width: fit-content;
}

.mode-btn {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.5rem 1.25rem;
  border-radius: 0.625rem;
  border: none;
  background: transparent;
  font-size: 0.875rem;
  font-weight: 500;
  font-family: inherit;
  color: #64748b;
  cursor: pointer;
  transition: background 0.18s, color 0.18s, box-shadow 0.18s;
}

.mode-btn .material-symbols-outlined {
  font-size: 1.1rem;
}

.mode-btn--active {
  background: #fff;
  color: #4B9AF6;
  font-weight: 700;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}

/* Transition */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.2s, transform 0.2s;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}
</style>