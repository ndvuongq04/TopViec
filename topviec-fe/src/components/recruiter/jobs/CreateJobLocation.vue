<template>
  <section class="section-card">
    <div class="section-header">
      <div class="icon-wrap icon-blue">
        <span class="material-symbols-outlined">location_on</span>
      </div>
      <h3 class="section-title">Địa điểm làm việc</h3>
    </div>

    <div class="location-list">
      <p v-if="errors.locations" class="field-error field-error--top">{{ errors.locations }}</p>

      <div
        v-for="(loc, i) in form.locations"
        :key="i"
        class="location-row"
      >
        <!-- Province SearchableSelect -->
        <div class="loc-province" :class="{ 'select-wrap--error': errors.locations && !loc.provinceId && !loc.isRemote }">
          <SearchableSelect
            v-model="loc.provinceId"
            :options="provinces"
            placeholder="-- Tìm tỉnh/thành --"
            :disabled="loc.isRemote"
          />
        </div>

        <!-- Address -->
        <div class="loc-address">
          <input
            v-model="loc.addressDetail"
            class="field-input"
            placeholder="Số nhà, đường, quận/huyện..."
            type="text"
            :disabled="loc.isRemote"
          />
        </div>

        <!-- Remote checkbox -->
        <div class="loc-remote">
          <label class="remote-label">
            <input v-model="loc.isRemote" type="checkbox" class="checkbox" @change="onRemoteChange(loc)" />
            <span>Remote</span>
          </label>
        </div>

        <!-- Delete -->
        <div class="loc-delete">
          <button
            type="button"
            class="delete-btn"
            title="Xóa"
            :disabled="form.locations.length === 1"
            @click="removeLocation(i)"
          >
            <span class="material-symbols-outlined">delete</span>
          </button>
        </div>
      </div>

      <!-- Add more -->
      <button type="button" class="add-location-btn" @click="addLocation">
        <span class="material-symbols-outlined">add_location</span>
        Thêm địa điểm làm việc khác
      </button>
    </div>
  </section>
</template>

<script setup lang="ts">
import { inject, ref, onMounted } from 'vue'
import { locationService } from '@/services/location.service'
import { CREATE_JOB_FORM_KEY, CREATE_JOB_ERRORS_KEY } from '@/composables/useCreateJobForm'
import type { CreateJobFormLocation } from '@/composables/useCreateJobForm'
import SearchableSelect from '@/components/ui/SearchableSelect.vue'
import type { SelectOption } from '@/components/ui/SearchableSelect.vue'

const form = inject(CREATE_JOB_FORM_KEY)!
const errors = inject(CREATE_JOB_ERRORS_KEY)!

const provinces = ref<SelectOption[]>([])

onMounted(async () => {
  const res = await locationService.getLocations({ size: 100 })
  provinces.value = res.result.map(p => ({ id: p.id, name: p.name }))
})

function addLocation() {
  form.locations.push({ provinceId: null, addressDetail: '', isRemote: false })
}

function removeLocation(index: number) {
  if (form.locations.length > 1) {
    form.locations.splice(index, 1)
  }
}

function onRemoteChange(loc: CreateJobFormLocation) {
  if (loc.isRemote) {
    loc.provinceId = null
    loc.addressDetail = ''
  }
}
</script>

<style scoped>
.section-card {
  background: #fff;
  border-radius: 1.5rem;
  padding: 2rem;
  box-shadow: 0 1px 3px rgba(0,0,0,.06);
  border: 1px solid #f1f5f9;
}
.section-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
}
.icon-wrap {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 0.75rem;
  display: flex;
  align-items: center;
  justify-content: center;
}
.icon-blue { background: #eff6ff; color: #2563eb; }
.section-title { font-size: 1.125rem; font-weight: 700; color: #0f172a; }

.location-list { display: flex; flex-direction: column; gap: 1rem; }

.field-error {
  font-size: 0.75rem;
  color: #ef4444;
}
.field-error--top { margin-bottom: 0.25rem; }

.location-row {
  display: grid;
  grid-template-columns: 1fr 1.5fr 5.5rem 2.5rem;
  align-items: center;
  gap: 0.75rem;
}

/* Error border override for SearchableSelect */
.select-wrap--error :deep(> div > div:first-child) {
  border-color: #ef4444 !important;
  box-shadow: 0 0 0 4px rgba(239,68,68,.08) !important;
}

.field-input {
  width: 100%;
  padding: 0.75rem 1rem;
  border-radius: 0.75rem;
  border: 1px solid #e2e8f0;
  outline: none;
  font-size: 1rem;
  font-family: inherit;
  color: #0f172a;
  box-sizing: border-box;
  transition: border-color 0.15s, box-shadow 0.15s;
}
.field-input:focus {
  border-color: #4B9AF6;
  box-shadow: 0 0 0 4px rgba(75,154,246,.1);
}
.field-input:disabled {
  background: #f8fafc;
  color: #94a3b8;
  cursor: not-allowed;
}

.remote-label {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  font-size: 0.875rem;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  white-space: nowrap;
}
.checkbox {
  accent-color: #4B9AF6;
  width: 1rem;
  height: 1rem;
  cursor: pointer;
}

.delete-btn {
  width: 2.5rem;
  height: 2.5rem;
  border: none;
  background: #fef2f2;
  color: #ef4444;
  border-radius: 0.75rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.15s;
  flex-shrink: 0;
}
.delete-btn:hover:not(:disabled) { background: #fee2e2; }
.delete-btn:disabled { opacity: 0.4; cursor: not-allowed; }
.delete-btn .material-symbols-outlined { font-size: 1rem; }

.add-location-btn {
  width: 100%;
  padding: 0.875rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  border: 2px dashed #e2e8f0;
  border-radius: 0.75rem;
  background: none;
  color: #94a3b8;
  font-size: 1rem;
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  transition: border-color 0.15s, color 0.15s;
}
.add-location-btn:hover {
  border-color: #4B9AF6;
  color: #4B9AF6;
}
.add-location-btn .material-symbols-outlined { font-size: 1.125rem; }

@media (max-width: 768px) {
  .location-row { grid-template-columns: 1fr 2.5rem; }
  .loc-province { grid-column: 1 / -1; }
  .loc-address { grid-column: 1; }
  .loc-remote { grid-column: 1; }
  .loc-delete { grid-column: 2; grid-row: 2; }
}
</style>
