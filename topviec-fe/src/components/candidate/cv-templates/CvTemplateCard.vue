<template>
  <article class="template-card">
    <!-- New badge -->
    <div v-if="template.isNew" class="new-badge">
      <span class="material-symbols-outlined badge-star">stars</span>
      Mới
    </div>

    <!-- Thumbnail -->
    <div class="thumb-wrap" @mouseenter="hovered = true" @mouseleave="hovered = false">
      <img
        :src="template.thumbnail"
        :alt="template.name"
        class="thumb-img"
        :class="{ 'thumb-hovered': hovered }"
      />

      <!-- Hover overlay -->
      <Transition name="fade">
        <div v-if="hovered" class="thumb-overlay">
          <button class="use-btn" @click="$emit('use')">Dùng mẫu này</button>
        </div>
      </Transition>
    </div>

    <!-- Card footer -->
    <div class="card-footer">
      <!-- Color swatches -->
      <div class="color-swatches">
        <span
          v-for="(color, i) in template.colors"
          :key="i"
          class="swatch"
          :style="{ background: color }"
        ></span>
      </div>

      <h3 class="template-name">{{ template.name }}</h3>

      <div class="tag-list">
        <span v-for="tag in template.tags" :key="tag" class="tag">{{ tag }}</span>
      </div>
    </div>
  </article>
</template>

<script setup lang="ts">
import { ref } from 'vue'

defineProps<{
  template: {
    id: number
    name: string
    thumbnail: string
    tags: string[]
    colors: string[]
    isNew: boolean
  }
}>()

defineEmits<{ use: [] }>()

const hovered = ref(false)
</script>

<style scoped>
.template-card {
  position: relative;
  background: #f1f3ff;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.template-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 32px rgba(0, 94, 164, 0.08);
}

/* New badge */
.new-badge {
  position: absolute;
  top: 24px; left: 24px;
  z-index: 10;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  border-radius: 99px;
  background: #006d32;
  color: #fff;
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  box-shadow: 0 2px 8px rgba(0, 109, 50, 0.25);
}
.badge-star { font-size: 0.875rem; font-variation-settings: 'FILL' 1; }

/* Thumbnail */
.thumb-wrap {
  position: relative;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  aspect-ratio: 1 / 1.414;
  margin-bottom: 16px;
  border: 1px solid rgba(192, 199, 212, 0.15);
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
  cursor: pointer;
}
.thumb-img {
  width: 100%; height: 100%;
  object-fit: cover;
  object-position: top;
  opacity: 0.9;
  transition: all 0.3s ease;
}
.thumb-hovered { opacity: 1; transform: scale(1.05); }

.thumb-overlay {
  position: absolute;
  inset: 0;
  background: rgba(15, 23, 42, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(2px);
}
.use-btn {
  padding: 12px 28px;
  border-radius: 99px;
  border: none;
  background: linear-gradient(135deg, #137fec, #2563eb);
  color: #fff;
  font-size: 1rem;
  font-weight: 800;
  cursor: pointer;
  font-family: inherit;
  box-shadow: 0 8px 24px rgba(19, 127, 236, 0.35);
  transform: translateY(12px);
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}
.thumb-overlay .use-btn { transform: translateY(0); }

/* Footer */
.card-footer { margin-top: auto; }

.color-swatches {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 12px;
}
.swatch {
  width: 14px; height: 14px;
  border-radius: 50%;
  border: 2px solid #fff;
  box-shadow: 0 0 0 1px rgba(0,0,0,0.1);
  flex-shrink: 0;
}

.template-name {
  font-size: 1.125rem;
  font-weight: 800;
  color: #0f172a;
  margin: 0 0 12px;
  line-height: 1.4;
}

.tag-list { display: flex; flex-wrap: wrap; gap: 6px; }
.tag {
  display: inline-block;
  padding: 4px 10px;
  background: #f1f5f9;
  color: #64748b;
  font-size: 0.75rem;
  font-weight: 600;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
}

/* Transition */
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>