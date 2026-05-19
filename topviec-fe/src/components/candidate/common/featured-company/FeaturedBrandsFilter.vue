<template>
  <div class="brands-filter">
    <div class="brands-filter__tabs" ref="tabsRef">
      <button
        v-for="cat in categories" :key="cat"
        :class="['brands-filter__tab', modelValue === cat && 'brands-filter__tab--active']"
        @click="$emit('update:modelValue', cat)"
      >{{ cat }}</button>
    </div>
    <div class="brands-filter__nav">
      <button class="brands-filter__nav-btn" :disabled="!canScrollLeft" @click="scroll('left')">
        <span class="material-symbols-outlined">chevron_left</span>
      </button>
      <button class="brands-filter__nav-btn" :disabled="!canScrollRight" @click="scroll('right')">
        <span class="material-symbols-outlined">chevron_right</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

defineProps<{ modelValue: string; categories: string[] }>()
defineEmits(['update:modelValue'])

const tabsRef = ref<HTMLElement | null>(null)
const canScrollLeft = ref(false)
const canScrollRight = ref(false)
const SCROLL_AMOUNT = 200

function updateScrollState() {
  const el = tabsRef.value
  if (!el) return
  canScrollLeft.value = el.scrollLeft > 0
  canScrollRight.value = el.scrollLeft + el.clientWidth < el.scrollWidth - 1
}

function scroll(direction: 'left' | 'right') {
  const el = tabsRef.value
  if (!el) return
  el.scrollBy({ left: direction === 'left' ? -SCROLL_AMOUNT : SCROLL_AMOUNT, behavior: 'smooth' })
  setTimeout(updateScrollState, 300)
}

onMounted(() => {
  updateScrollState()
  tabsRef.value?.addEventListener('scroll', updateScrollState)
  window.addEventListener('resize', updateScrollState)
})

onUnmounted(() => {
  tabsRef.value?.removeEventListener('scroll', updateScrollState)
  window.removeEventListener('resize', updateScrollState)
})
</script>

<style scoped>
.brands-filter {
  display: flex; align-items: center; justify-content: space-between;
  padding: 1rem 1.5rem;
  background: #fff;
  border-bottom: 1px solid rgba(192,199,212,0.15);
}
.brands-filter__tabs {
  display: flex; align-items: center; gap: 10px;
  overflow-x: auto; flex: 1;
  scroll-behavior: smooth;
}
.brands-filter__tabs::-webkit-scrollbar { display: none; }
.brands-filter__tabs { -ms-overflow-style: none; scrollbar-width: none; }

.brands-filter__tab {
  white-space: nowrap;
  padding: 8px 20px; border-radius: 999px;
  font-size: 0.875rem; font-weight: 500;
  cursor: pointer; border: 1px solid rgba(192,199,212,0.3);
  background: #fff; color: #404752;
  font-family: 'Manrope', sans-serif;
  transition: background 0.15s, color 0.15s;
}
.brands-filter__tab:hover { background: #e9edff; }
.brands-filter__tab--active {
  background: #005ea4; color: #fff;
  border-color: #005ea4;
  box-shadow: 0 1px 4px rgba(0,94,164,0.2);
}

.brands-filter__nav { display: flex; align-items: center; gap: 8px; padding-left: 1rem; flex-shrink: 0; }

.brands-filter__nav-btn {
  width: 32px; height: 32px; border-radius: 50%;
  border: 1px solid rgba(192,199,212,0.4);
  background: none; display: flex; align-items: center; justify-content: center;
  color: #404752; cursor: pointer; transition: background 0.15s, opacity 0.15s;
}
.brands-filter__nav-btn:hover:not(:disabled) { background: #e9edff; }
.brands-filter__nav-btn:disabled { opacity: 0.35; cursor: not-allowed; }
.brands-filter__nav-btn .material-symbols-outlined { font-size: 18px; }
</style>
