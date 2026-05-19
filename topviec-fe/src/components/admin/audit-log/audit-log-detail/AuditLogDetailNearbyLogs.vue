<template>
  <div class="ald-nearby">
    <h3 class="ald-nearby__title">
      <span class="material-symbols-outlined">history</span>
      Các log gần kề
    </h3>

    <div class="ald-nearby__admin">
      <div class="ald-nearby__admin-avatar">{{ admin.initials }}</div>
      <span class="ald-nearby__admin-name">{{ admin.name }}</span>
    </div>

    <div class="ald-nearby__timeline">
      <div v-for="(log, i) in logs" :key="log.name" class="ald-nearby__item">
        <div class="ald-nearby__dot"></div>
        <div :class="['ald-nearby__content', i % 2 !== 0 && 'ald-nearby__content--right']">
          <p class="ald-nearby__log-name">{{ log.name }}</p>
          <p class="ald-nearby__log-time">{{ log.time }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  admin: { name: string; initials: string }
  logs: { name: string; time: string }[]
}>()
</script>

<style scoped>
.ald-nearby {
  background: #fff; border-radius: 1rem; padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border: 1px solid rgba(228,226,220,0.5);
}
.ald-nearby__title {
  display: flex; align-items: center; gap: 8px;
  font-size: 1.125rem; font-weight: 700; color: #1b1c18; margin-bottom: 1.25rem;
}
.ald-nearby__title .material-symbols-outlined { color: #76191d; font-size: 20px; }

.ald-nearby__admin {
  display: flex; align-items: center; gap: 8px;
  padding-bottom: 1rem; margin-bottom: 1.5rem;
  border-bottom: 1px solid rgba(228,226,220,0.3);
}
.ald-nearby__admin-avatar {
  width: 24px; height: 24px; border-radius: 50%;
  background: rgba(150,49,49,0.1); color: #76191d;
  display: flex; align-items: center; justify-content: center;
  font-weight: 700; font-size: 12px; flex-shrink: 0;
}
.ald-nearby__admin-name { font-size: 0.75rem; font-weight: 500; color: #574240; }

/* Timeline */
.ald-nearby__timeline {
  position: relative;
  display: flex; flex-direction: column; gap: 1.5rem;
  padding-left: 1.25rem;
}
.ald-nearby__timeline::before {
  content: ''; position: absolute; left: 5px; top: 0; bottom: 0; width: 1px;
  background: linear-gradient(to bottom, transparent, rgba(221,192,190,0.3), transparent);
}

.ald-nearby__item { position: relative; display: flex; align-items: flex-start; gap: 1rem; }
.ald-nearby__dot {
  position: absolute; left: -1.125rem; top: 4px;
  width: 10px; height: 10px; border-radius: 50%;
  background: #fff; border: 2px solid #e4e2dc;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1); flex-shrink: 0;
  transition: transform 0.2s;
}
.ald-nearby__item:hover .ald-nearby__dot { transform: scale(1.3); }

.ald-nearby__content { flex: 1; }
.ald-nearby__log-name {
  font-family: 'JetBrains Mono', monospace;
  font-size: 0.75rem; font-weight: 700; color: #1b1c18;
}
.ald-nearby__log-time {
  font-family: 'JetBrains Mono', monospace;
  font-size: 0.75rem; color: #574240; margin-top: 2px;
}
</style>