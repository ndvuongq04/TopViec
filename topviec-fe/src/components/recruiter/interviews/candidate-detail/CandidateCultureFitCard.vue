<template>
  <div class="culture-card">
    <div class="culture-card__content">
      <h4 class="culture-card__title">Phù hợp văn hoá</h4>
      <div class="culture-card__tags">
        <span
          v-for="tag in tags"
          :key="tag"
          class="culture-card__tag"
        >{{ tag }}</span>
      </div>
      <div class="culture-card__score">
        <span class="culture-card__score-num">{{ score }}%</span>
        <span class="culture-card__score-label">Chỉ số tương thích</span>
      </div>
    </div>
    <!-- Decorative blob -->
    <div class="culture-card__blob" />
  </div>
</template>

<script setup lang="ts">
defineProps<{
  score: number
  tags: string[]
}>()
</script>

<style scoped>
.culture-card {
  background: #4B9AF6;
  border-radius: 0.75rem;
  padding: 1.5rem;
  color: #fff;
  box-shadow: 0 8px 24px rgba(75,154,246,0.3);
  overflow: hidden;
  position: relative;
}

.culture-card__content {
  position: relative;
  z-index: 1;
}

.culture-card__title {
  font-size: 1.125rem;
  font-weight: 800;
  margin: 0 0 1rem;
}

.culture-card__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.culture-card__tag {
  padding: 0.25rem 0.75rem;
  background: rgba(255,255,255,0.2);
  backdrop-filter: blur(4px);
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.culture-card__score {
  display: flex;
  align-items: baseline;
  gap: 0.4rem;
}

.culture-card__score-num {
  font-size: 1.875rem;
  font-weight: 800;
  line-height: 1;
}

.culture-card__score-label {
  font-size: 0.875rem;
  opacity: 0.8;
  font-weight: 500;
}

/* Decorative blob */
.culture-card__blob {
  position: absolute;
  bottom: -2rem;
  right: -2rem;
  width: 8rem;
  height: 8rem;
  background: rgba(255,255,255,0.1);
  border-radius: 50%;
  filter: blur(2rem);
  pointer-events: none;
}
</style>