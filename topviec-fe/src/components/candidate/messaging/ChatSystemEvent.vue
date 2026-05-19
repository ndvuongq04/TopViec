<template>
  <div class="system-event">
    <div class="event-pill">
      <span class="material-symbols-outlined event-icon">{{ icon }}</span>
      <span class="event-text">{{ text }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ text: string; icon: string }>()
</script>

<style scoped>
.system-event { display: flex; justify-content: center; }
.event-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #fff;
  border: 1px solid rgba(192, 199, 212, 0.25);
  border-radius: 99px;
  box-shadow: 0 2px 6px rgba(0, 94, 164, 0.04);
}
.event-icon { font-size: 0.875rem; color: #005ea4; }
.event-text { font-size: 0.75rem; font-weight: 500; color: #404752; }
</style>