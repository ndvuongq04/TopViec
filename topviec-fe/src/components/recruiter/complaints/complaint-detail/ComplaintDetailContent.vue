<template>
  <section class="cd-content">
    <div class="cd-content__section-title">
      <span class="material-symbols-outlined">assignment_late</span>
      <h3>Nội dung khiếu nại</h3>
    </div>

    <div class="cd-content__type">
      <p class="cd-content__label">Loại vi phạm</p>
      <p class="cd-content__type-value">{{ content.violationType }}</p>
    </div>

    <div class="cd-content__summary-wrap">
      <p class="cd-content__label">Tóm tắt phản ánh</p>
      <blockquote class="cd-content__summary">"{{ content.summary }}"</blockquote>
    </div>

    <div class="cd-content__note">
      <span class="material-symbols-outlined cd-content__note-icon">info</span>
      <p><strong>{{ content.noteTitle }}:</strong> {{ content.note }}</p>
    </div>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  content: { violationType: string; summary: string; noteTitle: string; note: string }
}>()
</script>

<style scoped>
.cd-content {
  background: #fff; border-radius: 0.75rem; padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05); border: 1px solid rgba(226,232,240,0.2);
  display: flex; flex-direction: column; gap: 1rem;
}

.cd-content__section-title {
  display: flex; align-items: center; gap: 8px;
  padding-bottom: 1rem; border-bottom: 1px solid #e2e7f0;
}
.cd-content__section-title .material-symbols-outlined { color: #ef4444; font-size: 20px; }
.cd-content__section-title h3 { font-size: 1.125rem; font-weight: 800; color: #0f172a; }

.cd-content__label {
  font-size: 0.6875rem; font-weight: 700; text-transform: uppercase;
  letter-spacing: 0.07em; color: #64748b; margin-bottom: 6px;
}

.cd-content__type {
  background: #f8fafd; border-radius: 0.5rem; padding: 1rem;
  border: 1px solid rgba(226,232,240,0.5);
}
.cd-content__type-value { font-size: 1rem; font-weight: 600; color: #0f172a; }

.cd-content__summary {
  font-size: 0.875rem; color: #0f172a; line-height: 1.7;
  padding: 1rem; background: #f1f4f9; border-radius: 0.5rem;
  border-left: 4px solid #4B9AF6; font-style: italic; margin: 0;
}

.cd-content__note {
  display: flex; align-items: flex-start; gap: 10px;
  background: rgba(254,243,199,0.3); border-radius: 0.5rem; padding: 12px;
  font-size: 0.875rem; color: #64748b; line-height: 1.6;
}
.cd-content__note-icon { color: #f59e0b; font-size: 18px; flex-shrink: 0; margin-top: 1px; }
.cd-content__note strong { color: #0f172a; }
</style>
