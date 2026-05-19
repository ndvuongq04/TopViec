<template>
  <aside class="cs-filters">
    <h2 class="cs-filters__title">Bộ lọc tìm kiếm</h2>

    <!-- Search input -->
    <div class="cs-filters__search-wrap">
      <span class="material-symbols-outlined cs-filters__search-icon">search</span>
      <input class="cs-filters__search-input" placeholder="Tìm theo tên, kỹ năng..." type="text" />
    </div>

    <!-- Kỹ năng -->
    <div class="cs-filters__section">
      <button class="cs-filters__section-header" @click="toggle('skills')">
        <span>Kỹ năng</span>
        <span :class="['material-symbols-outlined', 'cs-filters__chevron', !open.skills && 'cs-filters__chevron--collapsed']">expand_more</span>
      </button>
      <div v-show="open.skills" class="cs-filters__skills">
        <CandidateSkillTag
          v-for="s in activeSkills"
          :key="s"
          :label="s"
          variant="outline"
          :removable="true"
          @remove="removeSkill(s)"
        />
        <input class="cs-filters__skill-input" placeholder="+ Thêm kỹ năng" type="text" />
      </div>
    </div>

    <!-- Địa điểm -->
    <div class="cs-filters__section">
      <button class="cs-filters__section-header" @click="toggle('location')">
        <span>Địa điểm <span class="cs-filters__required">*</span></span>
        <span :class="['material-symbols-outlined', 'cs-filters__chevron', !open.location && 'cs-filters__chevron--collapsed']">expand_more</span>
      </button>
      <div v-show="open.location">
        <div v-if="locationStore.loading" class="cs-filters__loading-inline">
          <span class="material-symbols-outlined cs-filters__spin">autorenew</span>
          Đang tải...
        </div>
        <div v-else class="cs-filters__radios">
          <label
            v-for="loc in locationStore.locations"
            :key="loc.id"
            class="cs-filters__radio-label"
          >
            <input
              class="cs-filters__radio"
              type="radio"
              name="location"
              :value="loc.id"
              :checked="selectedLocationId === loc.id"
              @change="selectedLocationId = loc.id"
            />
            {{ loc.name }}
          </label>
        </div>
      </div>
    </div>

    <!-- Kinh nghiệm -->
    <div class="cs-filters__section">
      <button class="cs-filters__section-header" @click="toggle('exp')">
        <span>Kinh nghiệm</span>
        <span :class="['material-symbols-outlined', 'cs-filters__chevron', !open.exp && 'cs-filters__chevron--collapsed']">expand_more</span>
      </button>
      <div v-show="open.exp" class="cs-filters__range-wrap">
        <input v-model="expValue" class="cs-filters__range" max="10" min="0" type="range" />
        <div class="cs-filters__range-labels">
          <span>0 năm</span><span>{{ expValue }}+ năm</span><span>10+ năm</span>
        </div>
      </div>
    </div>

    <button class="cs-filters__clear-btn">Xóa tất cả bộ lọc</button>

    <button
      class="cs-filters__search-btn"
      :disabled="selectedLocationId === null"
      @click="doSearch"
    >
      <span class="material-symbols-outlined">search</span>
      Tìm kiếm
    </button>
  </aside>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useLocationStore } from '@/stores/location.store'
import CandidateSkillTag from '../shared/CandidateSkillTag.vue'

const emit = defineEmits<{
  (e: 'search', locationId: number): void
}>()

const locationStore = useLocationStore()
const selectedLocationId = ref<number | null>(null)
const expValue = ref(3)
const activeSkills = ref(['React', 'Node.js'])
const open = reactive({ skills: true, location: true, exp: true })

const toggle = (key: keyof typeof open) => { open[key] = !open[key] }
const removeSkill = (s: string) => { activeSkills.value = activeSkills.value.filter((x) => x !== s) }

function doSearch() {
  if (selectedLocationId.value !== null) {
    emit('search', selectedLocationId.value)
  }
}

onMounted(() => {
  if (!locationStore.locations.length) {
    locationStore.fetchLocations({ size: 100 })
  }
})
</script>

<style scoped>
.cs-filters {
  position: sticky;
  top: 1.5rem;
  display: flex;
  width: 280px;
  flex-shrink: 0;
  flex-direction: column;
  padding: 1.5rem;
  border: 1px solid rgba(226, 232, 240, 0.3);
  border-radius: 0.75rem;
  background: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.cs-filters__title {
  margin-bottom: 1.5rem;
  color: #0f172a;
  font-size: 1.125rem;
  font-weight: 800;
}

.cs-filters__search-wrap {
  position: relative;
  margin-bottom: 1.5rem;
}

.cs-filters__search-icon {
  position: absolute;
  top: 50%;
  left: 12px;
  transform: translateY(-50%);
  color: #64748b;
  font-size: 1.25rem;
  pointer-events: none;
}

.cs-filters__search-input {
  box-sizing: border-box;
  width: 100%;
  padding: 8px 16px 8px 40px;
  border: none;
  border-radius: 999px;
  background: #f1f5f9;
  color: #0f172a;
  outline: none;
  font-family: 'Manrope', sans-serif;
  font-size: 1rem;
}

.cs-filters__search-input:focus {
  box-shadow: 0 0 0 2px rgba(75, 154, 246, 0.2);
}

.cs-filters__section {
  padding: 1rem 0;
  border-top: 1px solid rgba(226, 232, 240, 0.5);
}

.cs-filters__section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  margin-bottom: 12px;
  padding: 0;
  border: none;
  background: none;
  color: #0f172a;
  cursor: pointer;
  text-align: left;
  font-family: 'Manrope', sans-serif;
  font-size: 0.875rem;
  font-weight: 600;
}

.cs-filters__required {
  color: #ef4444;
}

.cs-filters__chevron {
  color: #64748b;
  font-size: 1.25rem;
  transition: transform 0.2s;
}

.cs-filters__chevron--collapsed {
  transform: rotate(-90deg);
}

.cs-filters__skills {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.cs-filters__skill-input {
  width: 100px;
  border: none;
  background: none;
  color: #64748b;
  outline: none;
  font-family: 'Manrope', sans-serif;
  font-size: 0.875rem;
}

.cs-filters__loading-inline {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #64748b;
  font-size: 0.875rem;
}

.cs-filters__spin {
  font-size: 1.125rem;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.cs-filters__radios {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 280px;
  overflow-y: auto;
}

.cs-filters__radio-label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #475569;
  cursor: pointer;
  font-size: 0.875rem;
  transition: color 0.15s;
}

.cs-filters__radio-label:hover {
  color: #0f172a;
}

.cs-filters__radio {
  accent-color: #4b9af6;
  cursor: pointer;
}

.cs-filters__range-wrap {
  padding: 0 8px;
}

.cs-filters__range {
  width: 100%;
  accent-color: #4b9af6;
}

.cs-filters__range-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  color: #64748b;
  font-size: 0.75rem;
  font-weight: 600;
}

.cs-filters__clear-btn {
  width: 100%;
  margin-top: 1rem;
  padding: 8px 0;
  border: none;
  background: none;
  color: #64748b;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  font-size: 1rem;
  font-weight: 600;
  transition: color 0.15s;
}

.cs-filters__clear-btn:hover {
  color: #0f172a;
}

.cs-filters__search-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  width: 100%;
  margin-top: 0.5rem;
  padding: 10px 0;
  border: none;
  border-radius: 0.5rem;
  background: #4b9af6;
  box-shadow: 0 1px 4px rgba(75, 154, 246, 0.25);
  color: #fff;
  cursor: pointer;
  font-family: 'Manrope', sans-serif;
  font-size: 1rem;
  font-weight: 700;
  transition: background 0.15s;
}

.cs-filters__search-btn:hover:not(:disabled) {
  background: #2563eb;
}

.cs-filters__search-btn:disabled {
  background: #cbd5e1;
  box-shadow: none;
  cursor: not-allowed;
}

.cs-filters__search-btn .material-symbols-outlined {
  font-size: 1.125rem;
}
</style>
