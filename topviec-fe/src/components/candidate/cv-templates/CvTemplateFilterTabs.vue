<template>
  <section class="filter-bar">
    <div class="filter-inner">
      <!-- Filter pills -->
      <div class="pills-wrap">
        <button
          v-for="f in filters"
          :key="f.value"
          class="pill"
          :class="{ 'pill-active': activeFilter === f.value }"
          @click="$emit('update:activeFilter', f.value)"
        >
          <span class="material-symbols-outlined pill-icon">{{ f.icon }}</span>
          {{ f.label }}
        </button>
      </div>

      <!-- Language dropdown -->
      <div class="lang-wrap" ref="langRef">
        <button class="lang-btn" @click="langOpen = !langOpen">
          <span class="lang-dot" :style="{ background: currentLanguage?.color }"></span>
          {{ currentLanguage?.label }}
          <span class="material-symbols-outlined">expand_more</span>
        </button>
        <div v-if="langOpen" class="lang-dropdown">
          <button
            v-for="lang in languages"
            :key="lang.value"
            class="lang-option"
            :class="{ active: activeLanguage === lang.value }"
            @click="selectLang(lang.value)"
          >
            <span class="lang-dot" :style="{ background: lang.color }"></span>
            {{ lang.label }}
          </button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'

const props = defineProps<{
  activeFilter: string
  activeLanguage: string
  filters: Array<{ value: string; label: string; icon: string }>
  languages: Array<{ value: string; label: string; color: string }>
}>()

const emit = defineEmits<{
  'update:activeFilter': [value: string]
  'update:activeLanguage': [value: string]
}>()

const langOpen = ref(false)
const langRef = ref<HTMLElement>()

const currentLanguage = computed(() =>
  props.languages.find(l => l.value === props.activeLanguage)
)

function selectLang(value: string) {
  emit('update:activeLanguage', value)
  langOpen.value = false
}

function handleClickOutside(e: MouseEvent) {
  if (langRef.value && !langRef.value.contains(e.target as Node)) {
    langOpen.value = false
  }
}

onMounted(() => document.addEventListener('click', handleClickOutside))
onUnmounted(() => document.removeEventListener('click', handleClickOutside))
</script>

<style scoped>
.filter-bar {
  position: sticky;
  top: 64px;
  z-index: 40;
  background: rgba(249, 249, 255, 0.88);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(233, 237, 255, 0.6);
  padding: 12px 0;
  margin-bottom: 0;
}
.filter-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

/* Pills */
.pills-wrap { display: flex; flex-wrap: wrap; gap: 8px; }
.pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 18px;
  border-radius: 99px;
  font-size: 0.875rem;
  font-weight: 500;
  border: 1px solid rgba(192, 199, 212, 0.3);
  background: #fff;
  color: #0f172a;
  cursor: pointer;
  font-family: inherit;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}
.pill-active {
  background: #137fec;
  color: #fff;
  border-color: transparent;
  font-weight: 700;
  box-shadow: 0 4px 12px rgba(19, 127, 236, 0.25);
}
.pill-icon { font-size: 1.125rem; }

/* Language */
.lang-wrap { position: relative; flex-shrink: 0; }
.lang-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  border-radius: 99px;
  font-size: 1rem;
  font-weight: 600;
  background: #fff;
  border: 1px solid rgba(192, 199, 212, 0.3);
  color: #0f172a;
  cursor: pointer;
  font-family: inherit;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  transition: all 0.2s;
}
.lang-btn:hover { background: #f8fafc; border-color: #cbd5e1; }
.lang-btn .material-symbols-outlined { font-size: 1.25rem; color: #64748b; }

.lang-dot { width: 12px; height: 12px; border-radius: 50%; flex-shrink: 0; }

.lang-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.12);
  border: 1px solid rgba(192, 199, 212, 0.2);
  overflow: hidden;
  min-width: 180px;
  z-index: 50;
  padding: 6px;
}
.lang-option {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  font-size: 1rem;
  color: #0f172a;
  cursor: pointer;
  font-family: inherit;
  background: none;
  border: none;
  width: 100%;
  text-align: left;
  transition: background 0.15s;
  border-radius: 8px;
}
.lang-option:hover { background: #f1f3ff; }
.lang-option.active { font-weight: 600; background: #f1f3ff; }
</style>