<template>
  <div class="content-sections">

    <!-- Mô tả công việc -->
    <section class="content-card">
      <h2 class="section-title">
        <span class="title-bar"></span>
        Mô tả công việc
      </h2>
      <div class="prose" v-html="description || '<p>Chưa có mô tả.</p>'" />
    </section>

    <!-- Yêu cầu ứng viên -->
    <section class="content-card">
      <h2 class="section-title">
        <span class="title-bar"></span>
        Yêu cầu ứng viên
      </h2>
      <div class="prose" v-html="requirements || '<p>Chưa có yêu cầu.</p>'" />
    </section>

    <!-- Quyền lợi -->
    <section class="content-card">
      <h2 class="section-title">
        <span class="title-bar"></span>
        Quyền lợi được hưởng
      </h2>
      <div v-if="benefitsHtml" class="prose" v-html="benefitsHtml" />
      <div v-else class="benefits-grid">
        <div
          v-for="benefit in benefits"
          :key="benefit.icon"
          class="benefit-item"
        >
          <span class="material-symbols-outlined benefit-icon">{{ benefit.icon }}</span>
          <span class="benefit-text">{{ benefit.text }}</span>
        </div>
      </div>
    </section>

  </div>
</template>

<script setup lang="ts">


interface Benefit {
  icon: string
  text: string
}

withDefaults(defineProps<{
  description?: string
  requirements?: string
  benefitsHtml?: string
  benefits?: Benefit[]
}>(), {
  benefits: () => [],
})
</script>

<style scoped>
.content-sections {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.content-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 1rem;
  padding: 2rem;
  box-shadow: 0 1px 3px rgba(0,0,0,.05);
}

/* Section title */
.section-title {
  display: flex;
  align-items: center;
  gap: 0.625rem;
  font-size: 1.125rem; /* Tiêu đề thẻ 18px */
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 1.5rem;
}
.title-bar {
  display: inline-block;
  width: 0.375rem;
  height: 1.5rem;
  background: #4B9AF6;
  border-radius: 9999px;
  flex-shrink: 0;
}

/* Prose */
.prose {
  color: #334155;
  line-height: 1.75;
  font-size: 1rem; /* Nâng từ 14px lên 16px - Base Readability */
}
.prose p {
  margin-bottom: 1rem;
}
.prose ul {
  list-style-type: disc;
  padding-left: 1.25rem;
}
.prose ol {
  list-style-type: decimal;
  padding-left: 1.25rem;
}
.prose li {
  display: list-item;
  margin-bottom: 0.3rem;
}

/* Benefits */
.benefits-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
}
@media (max-width: 640px) {
  .benefits-grid { grid-template-columns: 1fr; }
}

.benefit-item {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  padding: 0.75rem;
  background: #f1f5f9;
  border-radius: 0.5rem;
}
.benefit-icon {
  color: #4B9AF6;
  font-size: 1.25rem;
  flex-shrink: 0;
  margin-top: 0.0625rem;
}
.benefit-text {
  font-size: 1rem; /* Nâng từ 14px lên 16px */
  color: #334155;
  line-height: 1.6;
}
</style>
