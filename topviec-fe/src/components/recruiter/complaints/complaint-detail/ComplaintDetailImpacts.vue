<template>
  <section class="cd-impacts">
    <h3 class="cd-impacts__title">Ảnh hưởng có thể xảy ra</h3>
    <ul class="cd-impacts__list">
      <li v-for="item in impacts" :key="item.title" class="cd-impacts__item">
        <span :class="['material-symbols-outlined', 'cd-impacts__icon', `cd-impacts__icon--${item.iconVariant}`]">
          {{ item.icon }}
        </span>
        <div>
          <p class="cd-impacts__item-title">{{ item.title }}</p>
          <p class="cd-impacts__item-desc">{{ item.desc }}</p>
        </div>
      </li>
    </ul>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  impacts: { icon: string; iconVariant: string; title: string; desc: string }[]
}>()
</script>

<style scoped>
.cd-impacts {
  background: #fff; border-radius: 0.75rem; padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05); border: 1px solid rgba(226,232,240,0.2);
}
.cd-impacts__title { font-size: 1rem; font-weight: 800; color: #0f172a; margin-bottom: 1rem; }

.cd-impacts__list { display: flex; flex-direction: column; gap: 1rem; list-style: none; padding: 0; margin: 0; }
.cd-impacts__item { display: flex; align-items: flex-start; gap: 12px; }

.cd-impacts__icon {
  font-size: 20px; flex-shrink: 0; font-variation-settings: 'FILL' 1;
}
.cd-impacts__icon--secondary { color: #64748b; }
.cd-impacts__icon--tertiary  { color: #f59e0b; }
.cd-impacts__icon--error     { color: #ef4444; }

.cd-impacts__item-title { font-size: 0.875rem; font-weight: 700; color: #0f172a; margin-bottom: 4px; }
.cd-impacts__item-desc  { font-size: 0.75rem; color: #64748b; line-height: 1.5; }
</style>