<template>
  <section class="grid-section">
    <div class="template-grid">
      <CvTemplateCard
        v-for="template in templates"
        :key="template.id"
        :template="template"
        @use="$emit('use', template.id)"
      />
    </div>

    <!-- Load more -->
    <div v-if="hasMore" class="load-more">
      <button class="load-more-btn" @click="$emit('load-more')">
        Xem thêm mẫu CV
        <span class="material-symbols-outlined">expand_more</span>
      </button>
    </div>
  </section>
</template>

<script setup lang="ts">
import CvTemplateCard from './CvTemplateCard.vue'

defineProps<{
  templates: any[]
  hasMore: boolean
}>()

defineEmits<{ use: [id: number]; 'load-more': [] }>()
</script>

<style scoped>
.grid-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 24px 0;
}
.template-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 28px;
}
@media (max-width: 1024px) { .template-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 600px) { .template-grid { grid-template-columns: 1fr; } }

.load-more {
  margin-top: 48px;
  display: flex;
  justify-content: center;
}
.load-more-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 14px 28px;
  border-radius: 14px;
  border: 1px solid rgba(192, 199, 212, 0.35);
  background: #fff;
  color: #137fec;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.2s;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}
.load-more-btn:hover { background: #f8fafc; border-color: #137fec; transform: translateY(-2px); }
.load-more-btn .material-symbols-outlined { font-size: 20px; }
</style>