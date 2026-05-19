<template>
  <section class="section-card">
    <div class="section-header">
      <div class="icon-wrap icon-amber">
        <span class="material-symbols-outlined">description</span>
      </div>
      <h3 class="section-title">Nội dung chi tiết</h3>
    </div>

    <div class="content-fields">
      <!-- Mô tả công việc -->
      <div>
        <label class="field-label">
          Mô tả công việc <span class="required">*</span>
        </label>
        <TipTapEditor
          v-model="form.description"
          placeholder="Mô tả chi tiết các đầu công việc cần thực hiện..."
          :has-error="!!errors.description"
        />
        <p v-if="errors.description" class="field-error">{{ errors.description }}</p>
      </div>

      <div class="grid-2col">
        <!-- Yêu cầu công việc -->
        <div>
          <label class="field-label">
            Yêu cầu công việc <span class="required">*</span>
          </label>
          <TipTapEditor
            v-model="form.requirements"
            placeholder="Ví dụ: Có ít nhất 2 năm kinh nghiệm, thành thạo ReactJS..."
            :has-error="!!errors.requirements"
          />
          <p v-if="errors.requirements" class="field-error">{{ errors.requirements }}</p>
        </div>

        <!-- Quyền lợi -->
        <div>
          <label class="field-label">Quyền lợi ứng viên</label>
          <TipTapEditor
            v-model="form.benefits"
            placeholder="Ví dụ: Mức lương cạnh tranh, thưởng KPI hàng quý..."
          />
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { inject } from 'vue'
import { CREATE_JOB_FORM_KEY, CREATE_JOB_ERRORS_KEY } from '@/composables/useCreateJobForm'
import TipTapEditor from '@/components/common/TipTapEditor.vue'

const form = inject(CREATE_JOB_FORM_KEY)!
const errors = inject(CREATE_JOB_ERRORS_KEY)!
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
.icon-amber { background: #fffbeb; color: #f59e0b; }
.section-title { font-size: 1.125rem; font-weight: 700; color: #0f172a; }

.content-fields { display: flex; flex-direction: column; gap: 1.5rem; }

.required { color: #ef4444; }
.field-label {
  display: block;
  font-size: 0.875rem;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 0.5rem;
}
.field-error {
  font-size: 0.75rem;
  color: #ef4444;
  margin-top: 0.375rem;
}

.grid-2col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
}

@media (max-width: 768px) {
  .grid-2col { grid-template-columns: 1fr; }
}
</style>
