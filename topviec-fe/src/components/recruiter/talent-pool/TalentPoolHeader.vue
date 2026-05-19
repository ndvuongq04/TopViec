<template>
  <div class="tp-header">

    <div class="tp-header__title-row">
      <h1 class="tp-header__title">Talent Pool</h1>
      <span class="tp-header__count">{{ total.toLocaleString() }}</span>
    </div>
    <p class="tp-header__subtitle">Quản lý và tìm kiếm ứng viên tiềm năng cho công ty.</p>
    <div class="tp-header__tabs">
      <button
        v-for="t in tabs"
        :key="t.value"
        :class="['tp-header__tab', tab === t.value && 'tp-header__tab--active']"
        @click="$emit('update:tab', t.value)"
      >
        {{ t.label }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ tab: string; total: number }>()
defineEmits(['update:tab'])

const tabs = [
  { value: 'saved', label: 'Ứng viên đã lưu' },
  { value: 'search', label: 'Tìm kiếm ứng viên' },
]
</script>

<style scoped>


.tp-header__breadcrumb a {
  color: #64748b;
  text-decoration: none;
  transition: color 0.15s;
}

.tp-header__breadcrumb a:hover {
  color: #4b9af6;
}

.tp-header__breadcrumb .material-symbols-outlined {
  color: #cbd5e1;
  font-size: 0.875rem;
}

.tp-header__title-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 0.25rem;
}

.tp-header__subtitle {
  color: #64748b;
  font-size: 1rem;
  margin: 0 0 1.5rem;
}

.tp-header__title {
  color: #0f172a;
  font-size: 1.875rem;
  font-weight: 800;
  letter-spacing: -0.02em;
}

.tp-header__count {
  padding: 2px 10px;
  border-radius: 999px;
  background: #e0f2fe;
  color: #4b9af6;
  font-size: 0.75rem;
  font-weight: 800;
}

.tp-header__tabs {
  display: flex;
  overflow-x: auto;
  border-bottom: 1px solid #e2e8f0;
}

.tp-header__tabs::-webkit-scrollbar {
  display: none;
}

.tp-header__tab {
  position: relative;
  padding: 12px 24px;
  border: none;
  background: none;
  color: #64748b;
  cursor: pointer;
  white-space: nowrap;
  font-family: 'Manrope', sans-serif;
  font-size: 0.875rem;
  font-weight: 600;
  transition: color 0.15s;
}

.tp-header__tab:hover {
  color: #0f172a;
}

.tp-header__tab--active {
  color: #4b9af6;
  font-weight: 700;
}

.tp-header__tab--active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  border-radius: 2px 2px 0 0;
  background: #4b9af6;
}
</style>
