<template>
  <Teleport to="body">
    <div class="overlay" @click.self="$emit('close')">
      <div class="modal">
        <!-- Header -->
        <div class="modal-body">
          <div class="modal-head">
            <div>
              <h3 class="modal-title">Tạo nhóm dịch vụ mới</h3>
              <p class="modal-desc">Phân loại các dịch vụ của bạn vào nhóm mới.</p>
            </div>
            <button class="close-btn" @click="$emit('close')">
              <span class="material-symbols-outlined">close</span>
            </button>
          </div>

          <!-- Fields -->
          <div class="fields">
            <div class="field-block">
              <label class="field-label">Tên nhóm</label>
              <input
                v-model="form.name"
                class="text-input"
                type="text"
                placeholder="Ví dụ: Marketing chuyên sâu, Tư vấn nhân sự..."
              />
            </div>

            <div class="field-block">
              <label class="field-label">Biểu tượng</label>
              <div class="icon-grid">
                <button
                  v-for="icon in iconOptions"
                  :key="icon"
                  type="button"
                  class="icon-btn"
                  :class="{ 'icon-selected': form.icon === icon }"
                  @click="form.icon = icon"
                >
                  <span class="material-symbols-outlined">{{ icon }}</span>
                </button>
              </div>
            </div>

            <div class="field-block">
              <label class="field-label">Mô tả ngắn</label>
              <textarea
                v-model="form.description"
                class="text-input textarea"
                placeholder="Mô tả mục đích của nhóm dịch vụ này..."
              />
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="modal-footer">
          <button class="btn-cancel" @click="$emit('close')">Hủy bỏ</button>
          <button class="btn-submit" @click="handleCreate">Tạo nhóm ngay</button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { reactive } from 'vue'

const emit = defineEmits<{
  close: []
  created: [group: { value: string; icon: string; label: string }]
}>()

const iconOptions = ['category', 'stars', 'rocket_launch', 'business_center', 'diversity_3', 'explore']

const form = reactive({ name: '', icon: 'category', description: '' })

function handleCreate() {
  if (!form.name.trim()) return
  emit('created', {
    value: form.name.toLowerCase().replace(/\s+/g, '_'),
    icon: form.icon,
    label: form.name,
  })
}
</script>

<style scoped>
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(27, 28, 24, 0.4);
  backdrop-filter: blur(4px);
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.modal {
  background: #fff;
  border-radius: 16px;
  width: 100%;
  max-width: 480px;
  box-shadow: 0 24px 64px rgba(27, 28, 24, 0.15);
  overflow: hidden;
}

.modal-body { padding: 32px; }

.modal-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 28px;
}
.modal-title {
  font-size: 18px;
  font-weight: 700;
  color: #1b1c18;
  margin: 0 0 4px;
}
.modal-desc { font-size: 16px; color: #574240; margin: 0; }

.close-btn {
  padding: 6px;
  background: none;
  border: none;
  cursor: pointer;
  border-radius: 50%;
  color: #574240;
  transition: background 0.15s;
  display: flex;
}
.close-btn:hover { background: rgba(228, 226, 220, 0.5); }

.fields { display: flex; flex-direction: column; gap: 20px; }

.field-block { display: flex; flex-direction: column; gap: 8px; }
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
  font-family: inherit;
  transition: background 0.15s, box-shadow 0.15s, border-color 0.15s;
  box-sizing: border-box;
}
.text-input:focus {
  border-color: #963131;
  box-shadow: 0 0 0 2px rgba(150, 49, 49, 0.2);
}
.text-input::placeholder { color: rgba(87, 66, 64, 0.4); }
.textarea { min-height: 80px; resize: vertical; }

.icon-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 8px;
}
.icon-btn {
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  background: #fff;
  color: #574240;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s, color 0.15s;
}
.icon-btn:hover { background: #f8fafc; border-color: #cbd5e1; }
.icon-btn.icon-selected {
  background: rgba(118, 25, 29, 0.04);
  border-color: #76191d;
  color: #76191d;
}
.icon-btn .material-symbols-outlined { font-size: 20px; }

.modal-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 10px;
  padding: 20px 32px;
  background: #fff;
  border-top: 1px solid rgba(228, 226, 220, 0.5);
}

.btn-cancel {
  padding: 10px 22px;
  border-radius: 8px;
  border: 1px solid #8a7170;
  background: transparent;
  font-size: 16px;
  font-weight: 600;
  color: #1b1c18;
  cursor: pointer;
  font-family: inherit;
  transition: background 0.15s;
}
.btn-cancel:hover { background: #eae8e1; }

.btn-submit {
  padding: 10px 22px;
  border-radius: 8px;
  border: none;
  background: #76191d;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  font-family: inherit;
  transition: opacity 0.15s;
}
.btn-submit:hover { opacity: 0.88; }
</style>