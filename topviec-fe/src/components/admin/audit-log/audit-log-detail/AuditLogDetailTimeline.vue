<template>
  <div class="ald-timeline">
    <h3 class="ald-timeline__title">
      <span class="material-symbols-outlined">timeline</span>
      Business events liên quan
    </h3>

    <div class="ald-timeline__track">
      <div v-for="event in events" :key="event.name" class="ald-timeline__item">
        <div :class="['ald-timeline__dot', `ald-timeline__dot--${event.variant}`]"></div>
        <div class="ald-timeline__content">
          <div class="ald-timeline__content-top">
            <div>
              <p :class="['ald-timeline__event-name', event.variant === 'error' && 'ald-timeline__event-name--error']">{{ event.name }}</p>
              <p class="ald-timeline__event-desc">{{ event.desc }}</p>
            </div>
            <span class="ald-timeline__time">{{ event.time }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  events: { name: string; desc: string; time: string; variant: string }[]
}>()
</script>

<style scoped>
.ald-timeline {
  background: #fff; border-radius: 1rem; padding: 2rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border: 1px solid rgba(228,226,220,0.5);
}
.ald-timeline__title {
  display: flex; align-items: center; gap: 10px;
  font-size: 1.125rem; font-weight: 700; color: #1b1c18; margin-bottom: 2rem;
}
.ald-timeline__title .material-symbols-outlined { color: #76191d; font-size: 22px; }

.ald-timeline__track {
  padding-left: 2rem;
  border-left: 1px solid rgba(221,192,190,0.3);
  display: flex; flex-direction: column; gap: 2.5rem;
  position: relative;
}

.ald-timeline__item { position: relative; display: flex; align-items: flex-start; gap: 1rem; }
.ald-timeline__dot {
  position: absolute; left: -2.375rem; top: 4px;
  width: 10px; height: 10px; border-radius: 50%;
  outline: 4px solid #fff;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  transition: transform 0.2s; flex-shrink: 0;
}
.ald-timeline__item:hover .ald-timeline__dot { transform: scale(1.4); }
.ald-timeline__dot--default { background: #e4e2dc; }
.ald-timeline__dot--success { background: #004638; }
.ald-timeline__dot--error   { background: #ba1a1a; }

.ald-timeline__content { flex: 1; }
.ald-timeline__content-top {
  display: flex; justify-content: space-between; align-items: flex-start; gap: 1rem; flex-wrap: wrap;
}
.ald-timeline__event-name {
  font-size: 0.875rem; font-weight: 700; color: #1b1c18;
  font-family: 'JetBrains Mono', monospace; margin-bottom: 6px;
}
.ald-timeline__event-name--error { color: #ba1a1a; }
.ald-timeline__event-desc { font-size: 0.875rem; color: #574240; line-height: 1.5; }

.ald-timeline__time {
  font-family: 'JetBrains Mono', monospace; font-size: 0.75rem; font-weight: 700;
  color: #574240; background: #f6f4ec; padding: 4px 8px;
  border-radius: 4px; white-space: nowrap; flex-shrink: 0;
}
</style>