<template>
  <div
    class="stage-card"
    :class="{ 'is-dragging': isDragging }"
    draggable="true"
    @dragstart="$emit('drag-start', stage.id)"
    @dragover.prevent
    @drop="$emit('drop', stage.id)"
    @dragend="$emit('drag-end')"
  >
    <div class="stage-card__drag-handle" title="Kéo để sắp xếp">
      <span class="material-symbols-outlined">drag_indicator</span>
    </div>

    <div class="stage-card__body">
      <!-- Header -->
      <div class="stage-card__header">
        <div class="stage-card__title-group">
          <span class="stage-card__order">{{ stage.order }}</span>
          <h4 class="stage-card__title">{{ stage.name }}</h4>
          <span v-if="stage.isAutomated" class="stage-card__badge stage-card__badge--automated">
            Vòng tự động
          </span>
        </div>
        <div class="stage-card__actions">
          <button class="action-btn action-btn--edit" title="Chỉnh sửa" @click="$emit('edit', stage)">
            <span class="material-symbols-outlined">edit</span>
          </button>
          <button class="action-btn action-btn--delete" title="Xoá" @click="$emit('delete', stage.id)">
            <span class="material-symbols-outlined">delete</span>
          </button>
        </div>
      </div>

      <!-- Info Grid -->
      <div class="stage-card__info-grid">
        <div class="info-cell">
          <p class="info-cell__label">Tiêu chí đánh giá</p>
          <p class="info-cell__value">{{ stage.criteria }}</p>
        </div>

        <div class="info-cell">
          <p class="info-cell__label">Người phỏng vấn mặc định</p>
          <div class="info-cell__interviewer">
            <span v-if="stage.isAutomated" class="material-symbols-outlined info-cell__bot-icon">smart_toy</span>
            <p v-else class="info-cell__value">{{ interviewerNames }}</p>
          </div>
        </div>

        <div class="info-cell">
          <p class="info-cell__label">Thời gian dự kiến</p>
          <p class="info-cell__value">{{ stage.duration }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  stage: any
  isDragging?: boolean
}>()

const interviewerNames = computed(() => {
  if (!props.stage.interviewers || props.stage.interviewers.length === 0) return 'Bot lọc tự động'
  return props.stage.interviewers.map((i: any) => i.name).join(', ')
})

defineEmits<{
  (e: 'edit', stage: any): void
  (e: 'delete', id: number): void
  (e: 'drag-start', id: number): void
  (e: 'drop', id: number): void
  (e: 'drag-end'): void
}>()
</script>

<style scoped>
.stage-card {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  background: #fff;
  border: 1.5px solid #e2e8f0;
  border-radius: 0.75rem;
  padding: 1.25rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  transition: border-color 0.2s, box-shadow 0.2s, opacity 0.2s;
  cursor: default;
}

.stage-card:hover {
  border-color: rgba(75, 154, 246, 0.45);
  box-shadow: 0 4px 16px rgba(75, 154, 246, 0.08);
}

.stage-card.is-dragging {
  opacity: 0.45;
  border-color: #4B9AF6;
}

/* Drag handle */
.stage-card__drag-handle {
  padding: 0.4rem;
  color: #cbd5e1;
  display: flex;
  align-items: center;
  cursor: grab;
  transition: color 0.2s;
  margin-top: 0.1rem;
  flex-shrink: 0;
}

.stage-card:hover .stage-card__drag-handle {
  color: #64748b;
}

.stage-card__drag-handle:active {
  cursor: grabbing;
}

/* Body */
.stage-card__body {
  flex: 1;
  min-width: 0;
}

/* Header */
.stage-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 0.875rem;
  gap: 0.5rem;
}

.stage-card__title-group {
  display: flex;
  align-items: center;
  gap: 0.625rem;
  flex-wrap: wrap;
}

.stage-card__order {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 2rem;
  border-radius: 50%;
  background: #f1f5f9;
  font-size: 0.75rem;
  font-weight: 700;
  color: #475569;
  flex-shrink: 0;
}

.stage-card__title {
  font-size: 1.125rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
}

.stage-card__badge {
  padding: 0.15rem 0.5rem;
  border-radius: 0.25rem;
  font-size: 0.75rem;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.stage-card__badge--automated {
  background: #dcfce7;
  color: #15803d;
}

/* Actions */
.stage-card__actions {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  flex-shrink: 0;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 2rem;
  border-radius: 0.5rem;
  border: none;
  background: transparent;
  color: #94a3b8;
  cursor: pointer;
  transition: color 0.18s, background 0.18s;
}

.action-btn .material-symbols-outlined {
  font-size: 1.2rem;
}

.action-btn--edit:hover {
  color: #4B9AF6;
  background: rgba(75, 154, 246, 0.07);
}

.action-btn--delete:hover {
  color: #ef4444;
  background: rgba(239, 68, 68, 0.06);
}

/* Info Grid */
.stage-card__info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.25rem;
}

@media (max-width: 640px) {
  .stage-card__info-grid {
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }
}

.info-cell__label {
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #94a3b8;
  margin: 0 0 0.25rem;
}

.info-cell__value {
  font-size: 0.875rem;
  font-weight: 500;
  color: #1e293b;
  margin: 0;
}

.info-cell__interviewer {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.info-cell__avatar {
  width: 1.25rem;
  height: 1.25rem;
  border-radius: 50%;
  object-fit: cover;
}

.info-cell__bot-icon {
  font-size: 1rem;
  color: #94a3b8;
}
</style>