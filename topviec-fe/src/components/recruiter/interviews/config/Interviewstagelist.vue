<template>
  <section class="stage-list">
    <h3 class="stage-list__heading">
      <span class="material-symbols-outlined">reorder</span>
      Các vòng phỏng vấn đang hoạt động
    </h3>

    <div class="stage-list__items">
      <InterviewStageCard
        v-for="stage in stages"
        :key="stage.id"
        :stage="stage"
        :is-dragging="draggingId === stage.id"
        @edit="$emit('edit', $event)"
        @delete="$emit('delete', $event)"
        @drag-start="handleDragStart"
        @drop="handleDrop"
        @drag-end="handleDragEnd"
      />

      <!-- Empty placeholder / Add new -->
      <button class="stage-list__add-btn" @click="$emit('add')">
        <span class="material-symbols-outlined stage-list__add-icon">add_circle</span>
        <span class="stage-list__add-text">Nhấn để thêm vòng mới vào quy trình</span>
      </button>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import InterviewStageCard from './Interviewstagecard.vue'

const props = defineProps<{
  stages: any[]
}>()

const emit = defineEmits<{
  (e: 'edit', stage: any): void
  (e: 'delete', id: number): void
  (e: 'add'): void
  (e: 'reorder', stages: any[]): void
}>()

const draggingId = ref<number | null>(null)

function handleDragStart(id: number) {
  draggingId.value = id
}

function handleDrop(targetId: number) {
  if (draggingId.value === null || draggingId.value === targetId) return

  const list = [...props.stages]
  const fromIndex = list.findIndex(s => s.id === draggingId.value)
  const toIndex = list.findIndex(s => s.id === targetId)
  if (fromIndex === -1 || toIndex === -1) return

  const [moved] = list.splice(fromIndex, 1)
  list.splice(toIndex, 0, moved)

  // Re-assign order
  const reordered = list.map((s, i) => ({ ...s, order: i + 1 }))
  emit('reorder', reordered)
}

function handleDragEnd() {
  draggingId.value = null
}
</script>

<style scoped>
.stage-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.stage-list__heading {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.125rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 1rem;
}

.stage-list__heading .material-symbols-outlined {
  color: #4B9AF6;
  font-size: 1.3rem;
}

.stage-list__items {
  display: flex;
  flex-direction: column;
  gap: 0.875rem;
}

/* Add button */
.stage-list__add-btn {
  width: 100%;
  padding: 1.5rem 1rem;
  border: 2px dashed #e2e8f0;
  border-radius: 0.75rem;
  background: rgba(248, 250, 252, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;
  color: #94a3b8;
  cursor: pointer;
  transition: border-color 0.2s, color 0.2s;
}

.stage-list__add-btn:hover {
  border-color: rgba(75, 154, 246, 0.4);
  color: #4B9AF6;
}

.stage-list__add-btn:hover .stage-list__add-icon {
  transform: scale(1.12);
}

.stage-list__add-icon {
  font-size: 2.2rem;
  transition: transform 0.2s;
}

.stage-list__add-text {
  font-size: 0.875rem;
  font-weight: 700;
}
</style>