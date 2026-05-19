<template>
  <section class="section-card">
    <div class="section-header">
      <div class="icon-wrap icon-blue">
        <span class="material-symbols-outlined">info</span>
      </div>
      <h3 class="section-title">Thông tin cơ bản</h3>
    </div>

    <div class="grid-form">
      <!-- Title -->
      <div class="col-full">
        <label class="field-label">
          Tiêu đề tin tuyển dụng <span class="required">*</span>
        </label>
        <input
          v-model="form.title"
          class="field-input"
          :class="{ 'field-input--error': errors.title }"
          placeholder="VD: Senior Frontend Developer (ReactJS)"
          type="text"
        />
        <p v-if="errors.title" class="field-error">{{ errors.title }}</p>
      </div>

      <!-- Industry -->
      <div>
        <label class="field-label">
          Ngành nghề <span class="required">*</span>
        </label>
        <div :class="{ 'select-wrap--error': errors.industryId }">
          <SearchableSelect
            v-model="form.industryId"
            :options="industries"
            placeholder="-- Tìm và chọn ngành nghề --"
          />
        </div>
        <p v-if="errors.industryId" class="field-error">{{ errors.industryId }}</p>
      </div>

      <!-- Level -->
      <div>
        <label class="field-label">
          Cấp bậc <span class="required">*</span>
        </label>
        <div :class="{ 'select-wrap--error': errors.levelId }">
          <SearchableSelect
            v-model="form.levelId"
            :options="levels"
            placeholder="-- Tìm và chọn cấp bậc --"
          />
        </div>
        <p v-if="errors.levelId" class="field-error">{{ errors.levelId }}</p>
      </div>

      <!-- Headcount -->
      <div>
        <label class="field-label">Số lượng tuyển</label>
        <div class="input-icon-wrap">
          <span class="material-symbols-outlined input-icon">group</span>
          <input
            v-model.number="form.headcount"
            class="field-input input-with-icon"
            :class="{ 'field-input--error': errors.headcount }"
            min="1"
            type="number"
          />
        </div>
        <p v-if="errors.headcount" class="field-error">{{ errors.headcount }}</p>
      </div>

      <!-- Deadline -->
      <div>
        <label class="field-label">
          Hạn nộp hồ sơ <span class="required">*</span>
        </label>
        <div class="input-icon-wrap">
          <span class="material-symbols-outlined input-icon">calendar_today</span>
          <input
            v-model="form.deadline"
            class="field-input input-with-icon"
            :class="{ 
              'field-input--error': errors.deadline,
              'field-input--disabled': disabledDeadline 
            }"
            type="date"
            :min="minDate"
            :disabled="disabledDeadline"
          />
        </div>
        <p v-if="errors.deadline" class="field-error">{{ errors.deadline }}</p>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { inject, ref, onMounted, computed } from 'vue'
import { industryService } from '@/services/industry.service'
import { levelService } from '@/services/level.service'
import { CREATE_JOB_FORM_KEY, CREATE_JOB_ERRORS_KEY } from '@/composables/useCreateJobForm'
import SearchableSelect from '@/components/ui/SearchableSelect.vue'
import type { SelectOption } from '@/components/ui/SearchableSelect.vue'

interface Props {
  disabledDeadline?: boolean
}
defineProps<Props>()

const form = inject(CREATE_JOB_FORM_KEY)!
const errors = inject(CREATE_JOB_ERRORS_KEY)!

const industries = ref<SelectOption[]>([])
const levels = ref<SelectOption[]>([])

const minDate = computed(() => {
  const d = new Date()
  d.setDate(d.getDate() + 1)
  return d.toISOString().split('T')[0]
})

onMounted(async () => {
  const [indRes, lvRes] = await Promise.all([
    industryService.getIndustries({ size: 100 }),
    levelService.getLevels({ size: 100 }),
  ])
  industries.value = indRes.result.map(i => ({ id: i.id, name: i.name }))
  levels.value = lvRes.result.map(l => ({ id: l.id, name: l.name }))
})
</script>

<style scoped>
.section-card {
  background: #fff;
  border-radius: 1.5rem;
  padding: 2rem;
  box-shadow: 0 1px 3px rgba(0,0,0,.06);
  border: 1px solid #f1f5f9;
}
.section-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}
.icon-wrap {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 0.75rem;
  display: flex;
  align-items: center;
  justify-content: center;
}
.icon-blue { background: #eff6ff; color: #4B9AF6; }
.section-title { font-size: 1.125rem; font-weight: 700; color: #0f172a; }

.grid-form {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
}
.col-full { grid-column: 1 / -1; }

.field-label {
  display: block;
  font-size: 0.875rem;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 0.5rem;
}
.required { color: #ef4444; }

.field-input {
  width: 100%;
  padding: 0.75rem 1rem;
  border-radius: 0.75rem;
  border: 1px solid #e2e8f0;
  outline: none;
  font-size: 1rem;
  font-family: inherit;
  color: #0f172a;
  transition: border-color 0.15s, box-shadow 0.15s;
  box-sizing: border-box;
}
.field-input:focus {
  border-color: #4B9AF6;
  box-shadow: 0 0 0 4px rgba(75,154,246,.1);
}
.field-input--error {
  border-color: #ef4444 !important;
  box-shadow: 0 0 0 4px rgba(239,68,68,.08) !important;
}
.field-input--disabled {
  background-color: #f8fafc;
  color: #64748b;
  cursor: not-allowed;
  border-color: #e2e8f0;
}
.field-input--disabled:focus {
  border-color: #e2e8f0;
  box-shadow: none;
}
.field-error {
  font-size: 0.75rem;
  color: #ef4444;
  margin-top: 0.375rem;
}

/* Error outline wrapper for SearchableSelect */
.select-wrap--error :deep(> div > div:first-child) {
  border-color: #ef4444 !important;
  box-shadow: 0 0 0 4px rgba(239,68,68,.08) !important;
}

.input-icon-wrap { position: relative; }
.input-icon {
  position: absolute;
  left: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
  font-size: 1.25rem;
  pointer-events: none;
}
.input-with-icon { padding-left: 2.5rem; }

@media (max-width: 768px) {
  .grid-form { grid-template-columns: 1fr; }
  .col-full { grid-column: 1; }
}
</style>
