<template>
  <section class="cd-checklist">
    <h3 class="cd-checklist__title">Bạn cần làm gì</h3>
    <ul class="cd-checklist__list">
      <li v-for="(item, i) in items" :key="i" class="cd-checklist__item">
        <span class="cd-checklist__bullet"></span>
        <span class="cd-checklist__label">{{ item }}</span>
      </li>
    </ul>
  </section>
</template>

<script setup lang="ts">
defineProps<{ items: string[] }>()
</script>

<style scoped>
.cd-checklist {
  background: #fff; border-radius: 0.75rem; padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05); border: 1px solid rgba(226,232,240,0.2);
}
.cd-checklist__title { font-size: 1rem; font-weight: 800; color: #0f172a; margin-bottom: 1rem; }

.cd-checklist__list {
  list-style: none;
  margin: 0; padding: 0;
  display: flex; flex-direction: column; gap: 4px;
}

.cd-checklist__item {
  display: flex; align-items: flex-start; gap: 10px;
  padding: 10px 12px; border-radius: 0.5rem;
}

.cd-checklist__bullet {
  flex-shrink: 0;
  width: 6px; height: 6px;
  border-radius: 50%;
  background: #4B9AF6;
  margin-top: 6px;
}

.cd-checklist__label {
  font-size: 0.875rem; font-weight: 500; color: #0f172a; line-height: 1.5;
}
</style>