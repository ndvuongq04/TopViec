<template>
  <div class="interview-card">
    <span class="material-symbols-outlined card-icon">event</span>
    <div class="card-info">
      <p class="card-title">{{ card.title }}</p>
      <p class="card-time">{{ card.datetime }}</p>
    </div>
    <span class="card-action">Xem chi tiết</span>
  </div>
</template>

<script setup lang="ts">
defineProps<{ card: { title: string; datetime: string } }>()
</script>

<style scoped>
.interview-card {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  background: #fff;
  border: 1px solid rgba(192, 199, 212, 0.3);
  border-radius: 12px;
  cursor: pointer;
  box-shadow: 0 2px 6px rgba(0,0,0,0.04);
  transition: background 0.15s;
}
.interview-card:hover { background: #f9f9ff; }
.card-icon { font-size: 20px; color: #005ea4; flex-shrink: 0; }
.card-info { flex: 1; }
.card-title { font-size: 0.875rem; font-weight: 600; color: #071b3b; margin: 0 0 2px; }
.card-time { font-size: 0.75rem; color: #404752; margin: 0; }
.card-action { font-size: 0.75rem; font-weight: 600; color: #005ea4; white-space: nowrap; }
</style>