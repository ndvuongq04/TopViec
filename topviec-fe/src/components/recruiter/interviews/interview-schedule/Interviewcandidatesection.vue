<template>
  <section class="section">
    <div class="section__header">
      <h3 class="section__title">
        <span class="material-symbols-outlined">person_search</span>
        Thông tin ứng viên &amp; Vòng phỏng vấn
      </h3>
    </div>

    <div class="section__body">
      <!-- Chọn ứng viên -->
      <div class="field">
        <label class="field__label">Chọn ứng viên</label>
        <div class="field__select-wrap">
          <span class="material-symbols-outlined field__icon-left">search</span>
          <select
            class="field__select"
            :class="{ 'field__select--disabled': disabled }"
            :value="modelValue.candidateId"
            :disabled="disabled"
            @change="!disabled && emit('update:modelValue', {
              ...modelValue,
              candidateId: ($event.target as HTMLSelectElement).value
            })"
          >
            <option value="">Tìm kiếm theo tên hoặc email ứng viên...</option>
            <option v-for="c in candidates" :key="c.id" :value="c.id">
              {{ c.label }}
            </option>
          </select>
          <span class="material-symbols-outlined field__icon-right">unfold_more</span>
        </div>
      </div>

      <!-- Vòng phỏng vấn -->
      <div class="field">
        <label class="field__label">Vòng phỏng vấn</label>
        <div class="round-grid">
          <button
            v-for="round in rounds"
            :key="round.value"
            class="round-btn"
            :class="{ 'round-btn--active': modelValue.round === round.value, 'round-btn--disabled': disabled }"
            type="button"
            :disabled="disabled"
            @click="!disabled && emit('update:modelValue', { ...modelValue, round: round.value })"
          >
            {{ round.label }}
          </button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
interface Candidate {
  id: string
  label: string
}

interface RoundOption {
  value: string
  label: string
}

interface ModelValue {
  candidateId: string
  round: string
}

defineProps<{
  modelValue: ModelValue
  candidates: Candidate[]
  rounds: RoundOption[]
  disabled?: boolean
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

.field__select-wrap {
  position: relative;
}

.field__icon-left {
  position: absolute;
  left: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
  font-size: 1.1rem;
  pointer-events: none;
}

.field__icon-right {
  position: absolute;
  right: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
  font-size: 1.1rem;
  pointer-events: none;
}

.field__select {
  width: 100%;
  padding: 0.75rem 2.5rem 0.75rem 2.5rem;
  background: #f1f4f9;
  border: none;
  border-radius: 0.5rem;
  font-size: 0.875rem;
  font-family: inherit;
  color: #0f172a;
  appearance: none;
  outline: none;
  transition: box-shadow 0.18s;
}

.field__select:focus {
  box-shadow: 0 0 0 2px rgba(75,154,246,0.2);
}

/* Round buttons */
.round-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0.75rem;
}

@media (max-width: 480px) {
  .round-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

.round-btn {
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;
  border: 1px solid #e2e8f0;
  background: #fff;
  color: #64748b;
  font-size: 0.875rem;
  font-weight: 500;
  font-family: inherit;
  cursor: pointer;
  transition: border-color 0.18s, background 0.18s, color 0.18s;
}

.round-btn:hover {
  border-color: #4B9AF6;
}

.round-btn--active {
  border-width: 2px;
  border-color: #4B9AF6;
  background: #e0f2fe;
  color: #4B9AF6;
  font-weight: 700;
}

.field__select--disabled,
.round-btn--disabled {
  opacity: 0.75;
  cursor: not-allowed;
  pointer-events: none;
}
</style>