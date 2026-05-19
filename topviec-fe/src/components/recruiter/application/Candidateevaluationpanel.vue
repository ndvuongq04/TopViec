<template>
  <div class="eval-card">
    <div class="eval-card__head">
      <h2 class="eval-card__title">
        <span class="material-symbols-outlined">assignment_turned_in</span>
        Phân loại &amp; Đánh giá
      </h2>
    </div>

    <div class="eval-card__body">
      <!-- ── Trạng thái sàng lọc ── -->
      <div class="eval-section">
        <label class="eval-section__label">Trạng thái sàng lọc</label>
        <div class="status-options">
          <label
            v-for="opt in statusOptions"
            :key="opt.value"
            class="status-opt"
            :class="`status-opt--${opt.colorKey}`"
          >
            <input
              type="radio"
              name="screening-status"
              :value="opt.value"
              v-model="selectedStatus"
              class="status-opt__radio"
            />
            <span class="status-opt__label">{{ opt.label }}</span>
            <span class="material-symbols-outlined status-opt__icon">{{ opt.icon }}</span>
          </label>
        </div>
      </div>

      <!-- ── Đánh giá chuyên môn ── -->
      <div class="eval-section">
        <label class="eval-section__label">Đánh giá chuyên môn</label>
        <div class="star-rating">
          <button
            v-for="star in 5"
            :key="star"
            class="star-btn"
            @click="rating = star"
            @mouseenter="hoverRating = star"
            @mouseleave="hoverRating = 0"
            type="button"
          >
            <span
              class="material-symbols-outlined"
              :style="{ fontVariationSettings: (hoverRating || rating) >= star ? `'FILL' 1` : `'FILL' 0` }"
              :class="(hoverRating || rating) >= star ? 'star--filled' : 'star--empty'"
            >star</span>
          </button>
        </div>
      </div>

      <!-- ── Tags ── -->
      <div class="eval-section">
        <div class="eval-section__row">
          <label class="eval-section__label">Nhãn (Tags)</label>
          <button class="btn-add-tag" type="button" @click="showTagInput = true">+ Thêm mới</button>
        </div>
        <div class="tags-container">
          <span
            v-for="(tag, i) in tags"
            :key="tag.label"
            class="tag"
            :class="tag.highlighted ? 'tag--highlighted' : ''"
          >
            {{ tag.label }}
            <span
              class="material-symbols-outlined tag__remove"
              @click="removeTag(i)"
            >close</span>
          </span>
          <input
            v-if="showTagInput"
            ref="tagInputRef"
            v-model="newTagText"
            class="tag-input"
            placeholder="Nhãn mới..."
            @keydown.enter="addTag"
            @keydown.escape="cancelTag"
            @blur="cancelTag"
          />
        </div>
      </div>

      <!-- ── Ghi chú nội bộ ── -->
      <div class="eval-section">
        <div class="eval-section__row">
          <label class="eval-section__label">Ghi chú nội bộ</label>
          <span class="note-privacy">
            <span class="material-symbols-outlined" style="font-size: 0.75rem;">visibility_off</span>
            Chỉ tuyển dụng thấy
          </span>
        </div>
        <textarea
          v-model="internalNote"
          class="note-textarea"
          rows="4"
          placeholder="Nhập nhận xét về ứng viên này..."
        ></textarea>
      </div>
    </div>

    <!-- ── Footer actions ── -->
    <div class="eval-card__footer">
      <button class="btn-primary" type="button" @click="onSave">
        <span class="material-symbols-outlined">save</span>
        Lưu Đánh Giá
      </button>
      <button class="btn-talent-pool" type="button" @click="emit('save-to-talent-pool')">
        <span class="material-symbols-outlined">group_add</span>
        Lưu vào talentPool
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, watch } from 'vue'

interface Tag {
  label: string
  highlighted?: boolean
}

interface EvaluationFormData {
  status: string
  rating: number
  tags: Tag[]
  note: string
}

const props = defineProps<{
  initialRating?: number
  initialNote?:   string
  initialTags?:   string   // chuỗi phân cách bởi dấu phẩy
  initialStatus?: string   // giá trị status thật từ backend
}>()

const emit = defineEmits<{
  save: [data: EvaluationFormData]
  'invite-interview': []
  'save-to-talent-pool': []
}>()

// ── Status options — value là status thật gửi lên API ──
const statusOptions = [
  { value: 'cv_passed',   colorKey: 'suitable', label: 'Phù hợp',  icon: 'check_circle' },
  { value: 'considering', colorKey: 'consider', label: 'Cân nhắc',  icon: 'help' },
  { value: 'rejected',    colorKey: 'rejected',  label: 'Từ chối',   icon: 'cancel' },
] as const

const selectedStatus = ref<string>(props.initialStatus ?? '')

// ── Rating ──
const rating      = ref(props.initialRating ?? 0)
const hoverRating = ref(0)

// ── Tags (parse từ chuỗi "tag1,tag2,tag3") ──
function parseTags(raw?: string): Tag[] {
  if (!raw) return []
  return raw.split(',').map(t => t.trim()).filter(Boolean).map(label => ({ label }))
}
const tags = ref<Tag[]>(parseTags(props.initialTags))
const showTagInput = ref(false)
const newTagText = ref('')
const tagInputRef = ref<HTMLInputElement | null>(null)

async function addTag() {
  const trimmed = newTagText.value.trim()
  if (trimmed) tags.value.push({ label: trimmed })
  newTagText.value = ''
  showTagInput.value = false
}

function cancelTag() {
  newTagText.value = ''
  showTagInput.value = false
}

function removeTag(index: number) {
  tags.value.splice(index, 1)
}

// Watch showTagInput → focus
watch(showTagInput, async (val) => {
  if (val) {
    await nextTick()
    tagInputRef.value?.focus()
  }
})

// ── Internal note ──
const internalNote = ref(props.initialNote ?? '')

// ── Save — build plain object để tránh reactive proxy issue ──
function onSave() {
  emit('save', {
    status: selectedStatus.value,
    rating: rating.value,
    tags:   [...tags.value],
    note:   internalNote.value,
  })
}
</script>

<style scoped>
/* ── Card shell ── */
.eval-card {
  background: #fff;
  border-radius: 0.75rem;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.06);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.eval-card__head {
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid #f1f5f9;
  background: #f8fafc;
}
.eval-card__title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.125rem; /* 18px – item title */
  font-weight: 700;
  color: #0f172a;
}
.eval-card__title .material-symbols-outlined { color: #4B9AF6; }

.eval-card__body {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

/* ── Sections ── */
.eval-section { display: flex; flex-direction: column; gap: 0.75rem; }

.eval-section__label {
  font-size: 0.75rem; /* 12px – label */
  font-weight: 700;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.eval-section__row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* ── Status radio options ── */
.status-options {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.status-opt {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem;
  border-radius: 0.75rem;
  border: 2px solid transparent;
  cursor: pointer;
  transition: background 0.15s;
}
.status-opt--suitable  { border-color: #d1fae5; background: rgba(236, 253, 245, 0.3); }
.status-opt--consider  { border-color: #fde68a; background: rgba(255, 251, 235, 0.3); }
.status-opt--rejected  { border-color: #fecdd3; background: rgba(255, 241, 242, 0.3); }

.status-opt--suitable:hover  { background: #ecfdf5; }
.status-opt--consider:hover  { background: #fffbeb; }
.status-opt--rejected:hover  { background: #fff1f2; }

.status-opt__radio {
  width: 1rem;
  height: 1rem;
  flex-shrink: 0;
}
.status-opt--suitable .status-opt__radio  { accent-color: #059669; }
.status-opt--consider .status-opt__radio  { accent-color: #d97706; }
.status-opt--rejected .status-opt__radio  { accent-color: #e11d48; }

.status-opt__label {
  font-size: 0.875rem; /* 14px */
  font-weight: 700;
  flex: 1;
}
.status-opt--suitable .status-opt__label  { color: #065f46; }
.status-opt--consider .status-opt__label  { color: #92400e; }
.status-opt--rejected .status-opt__label  { color: #9f1239; }

.status-opt__icon { margin-left: auto; font-size: 1.25rem; }
.status-opt--suitable .status-opt__icon  { color: #10b981; }
.status-opt--consider .status-opt__icon  { color: #f59e0b; }
.status-opt--rejected .status-opt__icon  { color: #f43f5e; }

/* ── Stars ── */
.star-rating { display: flex; gap: 0.375rem; }

.star-btn {
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  transition: transform 0.12s;
  line-height: 1;
}
.star-btn:hover { transform: scale(1.15); }

.star-btn .material-symbols-outlined {
  font-size: 1.875rem; /* 30px */
  transition: color 0.1s;
}
.star--filled { color: #fbbf24; }
.star--empty  { color: #e2e8f0; }

/* ── Tags ── */
.btn-add-tag {
  font-size: 0.75rem; /* 12px */
  font-weight: 700;
  color: #4B9AF6;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
}
.btn-add-tag:hover { text-decoration: underline; }

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  align-items: center;
}

.tag {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.75rem; /* 12px */
  font-weight: 700;
  background: #f1f5f9;
  color: #475569;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: background 0.15s;
}
.tag:hover { background: #e2e8f0; }

.tag--highlighted {
  background: #eff6ff;
  color: #4B9AF6;
  border-color: rgba(75, 154, 246, 0.25);
}
.tag--highlighted:hover { background: #dbeafe; }

.tag__remove {
  font-size: 0.875rem;
  opacity: 0.4;
  transition: opacity 0.15s;
}
.tag:hover .tag__remove { opacity: 1; }

.tag-input {
  padding: 0.25rem 0.625rem;
  border-radius: 9999px;
  border: 1.5px dashed #4B9AF6;
  background: #eff6ff;
  color: #1e3a5f;
  font-size: 0.75rem; /* 12px */
  font-family: inherit;
  outline: none;
  width: 8rem;
}

/* ── Note textarea ── */
.note-privacy {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.625rem;
  color: #94a3b8;
}

.note-textarea {
  width: 100%;
  border-radius: 0.75rem;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  font-size: 0.875rem; /* 14px */
  font-family: inherit;
  color: #334155;
  padding: 1rem;
  resize: vertical;
  transition: border-color 0.15s, box-shadow 0.15s;
  box-sizing: border-box;
}
.note-textarea:focus {
  outline: none;
  border-color: #4B9AF6;
  box-shadow: 0 0 0 3px rgba(75, 154, 246, 0.12);
}
.note-textarea::placeholder { color: #94a3b8; }

/* ── Footer ── */
.eval-card__footer {
  padding: 1.5rem;
  background: #f8fafc;
  border-top: 1px solid #f1f5f9;
  display: flex;
  flex-direction: row;
  gap: 0.75rem;
}

.btn-primary {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.75rem;
  border-radius: 0.75rem;
  background: #4B9AF6;
  color: #fff;
  font-size: 0.875rem; /* 14px */
  font-weight: 800;
  border: none;
  cursor: pointer;
  box-shadow: 0 4px 14px rgba(75, 154, 246, 0.25);
  transition: background 0.15s, transform 0.12s;
}
.btn-primary:hover  { background: #2563eb; transform: scale(1.02); }
.btn-primary:active { transform: scale(0.97); }

.btn-talent-pool {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.75rem;
  border-radius: 0.75rem;
  background: #fff;
  color: #059669;
  font-size: 0.875rem;
  font-weight: 800;
  border: 2px solid #d1fae5;
  cursor: pointer;
  transition: background 0.15s, color 0.15s, border-color 0.15s, transform 0.12s;
}
.btn-talent-pool:hover  { background: #059669; color: #fff; border-color: #059669; transform: scale(1.02); }
.btn-talent-pool:active { transform: scale(0.97); }

.btn-secondary {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.75rem;
  border-radius: 0.75rem;
  background: #fff;
  color: #4B9AF6;
  font-size: 0.875rem; /* 14px */
  font-weight: 800;
  border: 2px solid rgba(75, 154, 246, 0.15);
  cursor: pointer;
  transition: background 0.15s, color 0.15s;
}
.btn-secondary:hover { background: #4B9AF6; color: #fff; }
</style>