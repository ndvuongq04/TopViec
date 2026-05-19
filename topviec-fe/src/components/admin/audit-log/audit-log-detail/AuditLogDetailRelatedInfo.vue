<template>
  <div class="ald-related">
    <h3 class="ald-related__title">
      <span class="material-symbols-outlined">link</span>
      Thông tin liên quan
    </h3>

    <div class="ald-related__stats">
      <div v-for="s in related.stats" :key="s.label" class="ald-related__stat">
        <span class="ald-related__stat-label">{{ s.label }}</span>
        <span class="ald-related__stat-value">{{ s.value }}</span>
      </div>
    </div>

    <div class="ald-related__links">
      <a v-for="link in related.links" :key="link.label" :href="link.href" class="ald-related__link">
        {{ link.label }}
        <span class="material-symbols-outlined ald-related__link-icon">open_in_new</span>
      </a>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  related: {
    stats: { label: string; value: string }[]
    links: { label: string; href: string }[]
  }
}>()
</script>

<style scoped>
.ald-related {
  background: #fff; border-radius: 1rem; padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border: 1px solid rgba(228,226,220,0.5);
}
.ald-related__title {
  display: flex; align-items: center; gap: 8px;
  font-size: 1.125rem; font-weight: 700; color: #1b1c18; margin-bottom: 1.25rem;
}
.ald-related__title .material-symbols-outlined { color: #76191d; font-size: 20px; }

.ald-related__stats { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1.5rem; }
.ald-related__stat {
  background: #f6f4ec; padding: 1rem; border-radius: 0.75rem;
  border: 1px solid rgba(228,226,220,0.3);
  display: flex; flex-direction: column; gap: 6px;
}
.ald-related__stat-label {
  font-size: 0.75rem; font-weight: 700; text-transform: uppercase;
  letter-spacing: 0.07em; color: #574240;
}
.ald-related__stat-value { font-size: 1.25rem; font-weight: 900; color: #1b1c18; }

.ald-related__links {
  display: flex; flex-direction: column; gap: 6px;
  border-top: 1px solid rgba(228,226,220,0.3); padding-top: 1.25rem;
}
.ald-related__link {
  display: flex; align-items: center; justify-content: space-between;
  padding: 12px; border-radius: 0.75rem; border: 1px solid transparent;
  font-size: 0.875rem; font-weight: 600; color: #76191d;
  text-decoration: none; transition: all 0.15s;
}
.ald-related__link:hover { background: #f6f4ec; border-color: rgba(228,226,220,0.3); }
.ald-related__link-icon {
  font-size: 16px; transition: transform 0.15s;
}
.ald-related__link:hover .ald-related__link-icon { transform: translate(2px, -2px); }
</style>