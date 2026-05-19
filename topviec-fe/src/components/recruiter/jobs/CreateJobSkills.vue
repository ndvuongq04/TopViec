<template>
  <section class="section-card">
    <div class="section-header">
      <div class="icon-wrap icon-indigo">
        <span class="material-symbols-outlined">psychology</span>
      </div>
      <h3 class="section-title">Kỹ năng &amp; Kinh nghiệm</h3>
    </div>

    <div class="grid-2col">
      <!-- Skill tags -->
      <div>
        <label class="field-label">Kỹ năng yêu cầu</label>

        <!-- Tags hiển thị kỹ năng đã chọn -->
        <div v-if="form.skills.length > 0" class="tags-list">
          <span
            v-for="(skill, i) in form.skills"
            :key="skill.skillId"
            class="skill-tag"
          >
            {{ skill.skillName }}
            <button type="button" @click="removeSkill(i)">
              <span class="material-symbols-outlined">close</span>
            </button>
          </span>
        </div>

        <!-- SearchableSelect để pick kỹ năng -->
        <SearchableSelect
          :model-value="null"
          :options="availableSkillOptions"
          placeholder="Tìm và thêm kỹ năng..."
          @change="onSkillSelected"
        />
        <p class="hint">Chọn từ danh sách để thêm kỹ năng yêu cầu</p>
      </div>

      <!-- Experience range -->
      <div>
        <label class="field-label">Số năm kinh nghiệm</label>
        <div class="exp-range">
          <div class="exp-field">
            <input
              v-model.number="form.experienceYearsMin"
              class="field-input"
              placeholder="Tối thiểu"
              min="0"
              type="number"
            />
            <span class="exp-unit">Năm</span>
          </div>
          <span class="exp-dash">—</span>
          <div class="exp-field">
            <input
              v-model.number="form.experienceYearsMax"
              class="field-input"
              :class="{ 'field-input--error': errors.experienceYearsMax }"
              placeholder="Tối đa"
              min="0"
              type="number"
            />
            <span class="exp-unit">Năm</span>
          </div>
        </div>
        <p v-if="errors.experienceYearsMax" class="field-error">{{ errors.experienceYearsMax }}</p>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { inject, ref, computed, onMounted } from 'vue'
import { skillService } from '@/services/skill.service'
import { CREATE_JOB_FORM_KEY, CREATE_JOB_ERRORS_KEY } from '@/composables/useCreateJobForm'
import SearchableSelect from '@/components/ui/SearchableSelect.vue'
import type { SelectOption } from '@/components/ui/SearchableSelect.vue'

const form = inject(CREATE_JOB_FORM_KEY)!
const errors = inject(CREATE_JOB_ERRORS_KEY)!

const allSkills = ref<SelectOption[]>([])

// Chỉ show những skill chưa được chọn
const availableSkillOptions = computed(() =>
  allSkills.value.filter(s => !form.skills.some(fs => fs.skillId === Number(s.id)))
)

onMounted(async () => {
  const res = await skillService.getSkills({ size: 200 })
  allSkills.value = res.result.map(s => ({ id: s.id, name: s.name }))
})

function onSkillSelected(option: SelectOption) {
  form.skills.push({
    skillId: Number(option.id),
    skillName: option.name,
    isRequired: true,
  })
}

function removeSkill(index: number) {
  form.skills.splice(index, 1)
}
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
.icon-indigo { background: #eef2ff; color: #4f46e5; }
.section-title { font-size: 1.125rem; font-weight: 700; color: #0f172a; }

.grid-2col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
}

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
.hint { font-size: 0.75rem; color: #94a3b8; margin-top: 0.375rem; }

/* Tags */
.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
}
.skill-tag {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.25rem 0.5rem 0.25rem 0.75rem;
  background: rgba(75,154,246,.1);
  color: #4B9AF6;
  font-size: 0.875rem;
  font-weight: 500;
  border-radius: 9999px;
}
.skill-tag button {
  border: none;
  background: none;
  cursor: pointer;
  color: inherit;
  display: flex;
  padding: 0;
  line-height: 1;
  opacity: 0.7;
  transition: opacity 0.15s;
}
.skill-tag button:hover { opacity: 1; }
.skill-tag button .material-symbols-outlined { font-size: 0.875rem; }

/* Experience range */
.exp-range {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}
.exp-field { position: relative; flex: 1; }
.field-input {
  width: 100%;
  padding: 0.75rem 2.5rem 0.75rem 1rem;
  border-radius: 0.75rem;
  border: 1px solid #e2e8f0;
  outline: none;
  font-size: 1rem;
  font-family: inherit;
  color: #0f172a;
  box-sizing: border-box;
  transition: border-color 0.15s, box-shadow 0.15s;
}
.field-input:focus {
  border-color: #4B9AF6;
  box-shadow: 0 0 0 4px rgba(75,154,246,.1);
}
.field-input--error {
  border-color: #ef4444 !important;
  box-shadow: 0 0 0 4px rgba(239,68,68,.08) !important;
}
.exp-unit {
  position: absolute;
  right: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  font-size: 0.75rem;
  color: #94a3b8;
  font-weight: 500;
  pointer-events: none;
}
.exp-dash { color: #94a3b8; font-weight: 700; }

@media (max-width: 768px) {
  .grid-2col { grid-template-columns: 1fr; }
}
</style>
