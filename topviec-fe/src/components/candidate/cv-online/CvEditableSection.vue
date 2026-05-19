<template>
  <section class="cv-section">
    <div class="cv-section-header">
      <h2 class="cv-section-title">{{ title }}</h2>
    </div>

    <div v-if="items.length" class="cv-section-items">
      <article
        v-for="(item, index) in items"
        :key="item.id"
        class="cv-section-item"
        @mouseenter="hoveredIndex = index"
        @mouseleave="hoveredIndex = -1"
      >
        <BlockControls
          :is-hovered="hoveredIndex === index"
          :is-first="index === 0"
          :is-last="index === items.length - 1"
          @move-up="$emit('moveUp', index)"
          @move-down="$emit('moveDown', index)"
          @remove="$emit('remove', index)"
        />
        <slot :item="item" :index="index" />
      </article>
    </div>

    <div v-else class="cv-section-empty">
      Chua co noi dung
    </div>

    <button class="add-item-btn" type="button" @click="$emit('add')">
      <span class="material-symbols-outlined">add</span>
      {{ addLabel }}
    </button>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import BlockControls from './BlockControls.vue'

defineProps<{
  title: string
  addLabel: string
  items: Array<{ id: string }>
}>()

defineEmits<{
  add: []
  moveUp: [index: number]
  moveDown: [index: number]
  remove: [index: number]
}>()

defineSlots<{
  default(props: { item: any; index: number }): any
}>()

const hoveredIndex = ref(-1)
</script>

<style scoped>
.cv-section {
  margin-top: 1.35rem;
}

.cv-section-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.55rem;
}

.cv-section-title {
  margin: 0;
  color: #0f172a;
  font-size: 1.02rem;
  font-weight: 800;
  line-height: 1.25;
}

.cv-section-items {
  display: grid;
  gap: 0.85rem;
}

.cv-section-item {
  position: relative;
  border: 1px solid transparent;
  border-radius: 8px;
  padding: 0.65rem 2.6rem 0.65rem 0.65rem;
  transition: border-color 0.16s ease, background-color 0.16s ease;
}

.cv-section-item:hover {
  border-color: #dbe2ea;
  background: rgba(248, 250, 252, 0.72);
}

.cv-section-empty {
  border: 1px dashed #cbd5e1;
  border-radius: 8px;
  padding: 0.85rem;
  color: #94a3b8;
  font-size: 0.9rem;
}

.add-item-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  margin-top: 0.75rem;
  border: 1px dashed #94a3b8;
  border-radius: 999px;
  background: #fff;
  padding: 0.45rem 0.8rem;
  color: #334155;
  font-size: 0.86rem;
  font-weight: 700;
  cursor: pointer;
  transition: border-color 0.16s ease, color 0.16s ease, background-color 0.16s ease;
}

.add-item-btn:hover {
  border-color: #0ea5e9;
  background: #f0f9ff;
  color: #0369a1;
}

.add-item-btn .material-symbols-outlined {
  font-size: 1rem;
}
</style>
