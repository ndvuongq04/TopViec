<template>
  <div class="perm-sections">
    <!-- Được cấp -->
    <div v-if="added.length" class="perm-section">
      <div class="section-label section-label--added">
        <span class="material-symbols-outlined section-icon">add_circle</span>
        Đã cấp ({{ added.length }})
      </div>
      <div class="chip-list">
        <span v-for="p in added" :key="p" class="chip chip--added">{{ p }}</span>
      </div>
    </div>

    <!-- Bị thu hồi -->
    <div v-if="removed.length" class="perm-section">
      <div class="section-label section-label--removed">
        <span class="material-symbols-outlined section-icon">remove_circle</span>
        Đã thu hồi ({{ removed.length }})
      </div>
      <div class="chip-list">
        <span v-for="p in removed" :key="p" class="chip chip--removed">{{ p }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ added: string[]; removed: string[] }>()
</script>

<style scoped>
.perm-sections {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.perm-section {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.section-label {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.02em;
}
.section-icon { font-size: 15px; }

.section-label--added  { color: #15803D; }
.section-label--removed { color: #B91C1C; }

.chip-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.chip {
  display: inline-flex;
  align-items: center;
  font-size: 0.8125rem;
  font-weight: 500;
  padding: 3px 10px;
  border-radius: 6px;
  line-height: 1.5;
}

.chip--added {
  background: #F0FDF4;
  color: #166534;
  border: 1px solid #BBF7D0;
}

.chip--removed {
  background: #FEF2F2;
  color: #991B1B;
  border: 1px solid #FECACA;
}
</style>
