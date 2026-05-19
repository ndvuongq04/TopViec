<template>
  <div class="form-card">
    <div class="form-body">
      <!-- Nhóm dịch vụ -->
      <div class="field-block">
        <div class="field-label-row">
          <label class="field-label">Nhóm dịch vụ</label>
        </div>
        <ServiceGroupPicker
          v-model="selectedGroup"
          :groups="groups"
        />
      </div>

      <!-- Tên dịch vụ -->
      <div class="field-block">
        <label class="field-label" for="service_name">Tên dịch vụ</label>
        <input
          id="service_name"
          v-model="serviceName"
          class="text-input"
          type="text"
          placeholder="Ví dụ: Tin khẩn cấp, Gia hạn tin 14 ngày..."
        />
      </div>

      <!-- Mã Code -->
      <div class="field-block">
        <label class="field-label" for="service_code">Mã Code</label>
        <div class="code-input-wrapper">
          <span class="code-prefix">{{ selectedGroup }}_</span>
          <input
            id="service_code"
            v-model="codeSuffix"
            class="code-suffix-input"
            type="text"
            placeholder="VD: URGENT, HOT, EXTEND_14..."
          />
        </div>
        <p v-if="fullCode" class="code-preview">
          <span class="material-symbols-outlined" style="font-size:14px;vertical-align:-2px">tag</span>
          Mã đầy đủ: <strong>{{ fullCode }}</strong>
        </p>
      </div>

      <!-- Đơn vị tính -->
      <div class="field-block">
        <label class="field-label" for="service_unit">Đơn vị tính</label>
        <input
          id="service_unit"
          v-model="serviceUnit"
          class="text-input"
          type="text"
          placeholder="Ví dụ: Theo lần, Theo ngày, Theo tháng..."
        />
      </div>

      <!-- Mô tả dịch vụ -->
      <div class="field-block">
        <label class="field-label" for="service_desc">Mô tả</label>
        <textarea
          id="service_desc"
          v-model="serviceDesc"
          class="text-input"
          rows="3"
          placeholder="Nhập mô tả cho dịch vụ này..."
        ></textarea>
      </div>
    </div>

    <div class="form-footer">
      <button class="btn-cancel" type="button">Hủy</button>
      <button class="btn-submit" type="button" @click="handleSubmit">Lưu dịch vụ</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import ServiceGroupPicker from './ServiceGroupPicker.vue'

defineProps<{
  groups: Array<{ value: string; icon: string; label: string }>
}>()

const emit = defineEmits<{
  submit: [payload: { group: string; name: string; description: string; code: string; unit: string }]
}>()

const selectedGroup = ref('JOB_POSTING')
const serviceName = ref('')
const serviceDesc = ref('')
const codeSuffix = ref('')
const serviceUnit = ref('')

const fullCode = computed(() =>
  codeSuffix.value.trim()
    ? `${selectedGroup.value}_${codeSuffix.value.trim().toUpperCase().replace(/\s+/g, '_')}`
    : ''
)

function handleSubmit() {
  emit('submit', {
    group: selectedGroup.value,
    name: serviceName.value,
    description: serviceDesc.value,
    code: fullCode.value,
    unit: serviceUnit.value,
  })
}
</script>

<style scoped>
.form-card {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 12px 32px -4px rgba(87, 66, 64, 0.06);
}

.form-body { display: flex; flex-direction: column; gap: 28px; }

.field-block { display: flex; flex-direction: column; gap: 8px; }
.field-label-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.field-label {
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #574240;
}

.text-input {
  width: 100%;
  padding: 12px 16px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  background: #fff;
  font-size: 16px;
  color: #1b1c18;
  outline: none;
  transition: background 0.15s, box-shadow 0.15s, border-color 0.15s;
  font-family: inherit;
  resize: vertical;
}
.text-input:focus {
  border-color: #963131;
  box-shadow: 0 0 0 2px rgba(150, 49, 49, 0.2);
}
.text-input::placeholder { color: rgba(87, 66, 64, 0.4); }

.code-input-wrapper {
  display: flex;
  align-items: center;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
  transition: box-shadow 0.15s, border-color 0.15s;
}
.code-input-wrapper:focus-within {
  border-color: #963131;
  box-shadow: 0 0 0 2px rgba(150, 49, 49, 0.2);
}
.code-prefix {
  padding: 12px 14px;
  background: #f8f5f5;
  border-right: 1px solid #e2e8f0;
  font-size: 13px;
  font-weight: 700;
  color: #963131;
  white-space: nowrap;
  font-family: monospace;
  letter-spacing: 0.03em;
}
.code-suffix-input {
  flex: 1;
  padding: 12px 16px;
  border: none;
  outline: none;
  font-size: 15px;
  font-family: monospace;
  color: #1b1c18;
  background: #fff;
  text-transform: uppercase;
}
.code-suffix-input::placeholder { color: rgba(87, 66, 64, 0.4); text-transform: none; }
.code-preview {
  font-size: 12px;
  color: #64748b;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 4px;
}
.code-preview strong { color: #963131; font-family: monospace; }

.form-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
  margin-top: 40px;
  padding-top: 28px;
  border-top: 1px solid rgba(228, 226, 220, 0.5);
}
.btn-cancel {
  padding: 10px 24px;
  border-radius: 8px;
  border: 1px solid #8a7170;
  background: transparent;
  font-size: 16px;
  font-weight: 600;
  color: #1b1c18;
  cursor: pointer;
  transition: background 0.15s;
  font-family: inherit;
}
.btn-cancel:hover { background: #f6f4ec; }

.btn-submit {
  padding: 10px 24px;
  border-radius: 8px;
  border: none;
  background: linear-gradient(135deg, #76191d, #963131);
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.15s, transform 0.1s;
  font-family: inherit;
}
.btn-submit:hover { opacity: 0.9; }
.btn-submit:active { transform: scale(0.97); }
</style>