<template>
  <div class="assign-search">
    <span class="material-symbols-outlined assign-search__icon">search</span>
    <input
      class="assign-search__input"
      :placeholder="placeholder"
      type="text"
      v-model="query"
      @keyup.enter="$emit('search', query)"
    />
    <button v-if="query" class="assign-search__clear" @click="query = ''; $emit('search', '')">
      <span class="material-symbols-outlined" style="font-size:18px;">close</span>
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
defineProps<{ placeholder?: string }>()
defineEmits<{ search: [keyword: string] }>()
const query = ref('')
</script>

<style scoped>
.assign-search {
  padding: 0.875rem 1rem;
  border-bottom: 1px solid #e2e8f0;
  position: relative;
  flex-shrink: 0;
}
.assign-search__icon {
  position: absolute;
  left: 1.75rem; top: 50%; transform: translateY(-50%);
  font-size: 1.1rem; color: #64748b; pointer-events: none;
}
.assign-search__input {
  width: 100%; background: #f1f4f9;
  border: none; border-radius: 999px;
  padding: 8px 36px 8px 40px;
  font-size: 1rem; font-family: 'Manrope', sans-serif;
  color: #0f172a; outline: none; box-sizing: border-box;
  transition: box-shadow 0.15s;
}
.assign-search__input:focus { box-shadow: 0 0 0 2px rgba(75,154,246,0.25); }
.assign-search__input::placeholder { color: #94a3b8; }
.assign-search__clear {
  position: absolute;
  right: 1.25rem; top: 50%; transform: translateY(-50%);
  background: none; border: none; cursor: pointer; padding: 0;
  color: #94a3b8; display: flex; align-items: center;
  transition: color 0.15s;
}
.assign-search__clear:hover { color: #64748b; }
</style>