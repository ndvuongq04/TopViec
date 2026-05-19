<template>
  <div class="cd-alert">
    <div class="cd-alert__icon-wrap">
      <span class="material-symbols-outlined">warning</span>
    </div>
    <div class="cd-alert__body">
      <h3 class="cd-alert__title">{{ complaint.alert.title }}</h3>
      <p class="cd-alert__message" v-html="highlightedMessage"></p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  complaint: {
    alert: { title: string; message: string; highlight: string[] }
  }
}>()

const highlightedMessage = computed(() => {
  let msg = props.complaint.alert.message
  props.complaint.alert.highlight.forEach(word => {
    msg = msg.replace(new RegExp(`"?${word}"?`, 'g'), `<strong>"${word}"</strong>`)
  })
  return msg
})
</script>

<style scoped>
.cd-alert {
  background: rgba(254,226,226,0.8); border: 1px solid rgba(239,68,68,0.2);
  border-radius: 0.75rem; padding: 1.25rem;
  display: flex; align-items: flex-start; gap: 1rem;
  position: relative; overflow: hidden;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}
.cd-alert::before {
  content: ''; position: absolute; inset: 0;
  background: linear-gradient(to right, rgba(239,68,68,0.05), transparent);
  pointer-events: none;
}

.cd-alert__icon-wrap {
  padding: 8px; background: #ef4444; border-radius: 0.5rem;
  color: #fff; flex-shrink: 0; box-shadow: 0 1px 4px rgba(239,68,68,0.3);
  position: relative; z-index: 1;
}
.cd-alert__icon-wrap .material-symbols-outlined {
  font-size: 20px; font-variation-settings: 'FILL' 1;
}

.cd-alert__body { position: relative; z-index: 1; flex: 1; }
.cd-alert__title { font-size: 1rem; font-weight: 800; color: #b91c1c; margin-bottom: 6px; }
.cd-alert__message {
  font-size: 0.875rem; color: rgba(185,28,28,0.9);
  font-weight: 500; line-height: 1.6;
}
</style>