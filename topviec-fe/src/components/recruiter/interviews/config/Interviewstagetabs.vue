<template>
  <div class="stage-tabs" role="tablist">
    <button
      v-for="tab in tabs"
      :key="tab.key"
      class="stage-tabs__item"
      :class="{ 'stage-tabs__item--active': activeTab === tab.key }"
      role="tab"
      :aria-selected="activeTab === tab.key"
      @click="$emit('update:activeTab', tab.key)"
    >
      {{ tab.label }}
    </button>
  </div>
</template>

<script setup lang="ts">
interface Tab {
  key: string
  label: string
}

defineProps<{
  tabs: Tab[]
  activeTab: string
}>()

defineEmits<{
  (e: 'update:activeTab', key: string): void
}>()
</script>

<style scoped>
.stage-tabs {
  display: flex;
  border-bottom: 1.5px solid #e2e8f0;
  margin-bottom: 2rem;
  overflow-x: auto;
  scrollbar-width: none;
}

.stage-tabs::-webkit-scrollbar {
  display: none;
}

.stage-tabs__item {
  padding: 1rem 1.5rem;
  border: none;
  border-bottom: 2.5px solid transparent;
  background: transparent;
  font-size: 0.875rem;
  font-weight: 600;
  color: #64748b;
  cursor: pointer;
  white-space: nowrap;
  transition: color 0.18s, border-color 0.18s;
  margin-bottom: -1.5px;
  font-family: inherit;
}

.stage-tabs__item:hover {
  color: #334155;
}

.stage-tabs__item--active {
  color: #4B9AF6;
  border-bottom-color: #4B9AF6;
  font-weight: 700;
}
</style>