<template>
  <button :class="['job-item', active && 'job-item--active']" @click="$emit('click')">
    <div class="job-item__info">
      <p :class="['job-item__title', active && 'job-item__title--active']">{{ job.title }}</p>
      <p class="job-item__code">ID: {{ job.code }}</p>
    </div>
    <div class="job-item__right">
      <span v-if="job.assignment" class="job-item__badge job-item__badge--assigned">
        <span class="material-symbols-outlined" style="font-size:13px;">person</span>
        1 NTD
      </span>
      <span v-else class="job-item__badge job-item__badge--empty">
        Chưa phân công
      </span>
      <span v-if="active" class="material-symbols-outlined job-item__arrow">chevron_right</span>
    </div>
  </button>
</template>

<script setup lang="ts">
defineProps<{ job: any; active: boolean }>()
defineEmits(['click'])
</script>

<style scoped>
.job-item {
  width: 100%; text-align: left; padding: 12px;
  border-radius: 0.5rem; border: 1px solid transparent;
  background: none; cursor: pointer; display: flex;
  align-items: center; justify-content: space-between;
  transition: background 0.15s;
  font-family: 'Manrope', sans-serif;
}
.job-item:hover { background: #f1f4f9; }
.job-item--active {
  background: rgba(224,242,254,0.4); border-color: rgba(75,154,246,0.2);
}
.job-item__info { flex: 1; min-width: 0; }
.job-item__title {
  font-size: 1.125rem; font-weight: 600; color: #0f172a;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.job-item__title--active { color: #4B9AF6; font-weight: 700; }
.job-item__code { font-size: 0.75rem; color: #64748b; margin-top: 2px; }

.job-item__right {
  display: flex; align-items: center; gap: 6px;
  flex-shrink: 0; margin-left: 8px;
}
.job-item__badge {
  display: inline-flex; align-items: center; gap: 3px;
  padding: 2px 8px; border-radius: 999px;
  font-size: 0.7rem; font-weight: 700;
}
.job-item__badge--assigned {
  background: rgba(16,185,129,0.1); color: #059669;
  border: 1px solid rgba(16,185,129,0.25);
}
.job-item__badge--empty {
  background: #f1f4f9; color: #94a3b8;
  border: 1px solid #e2e8f0;
}
.job-item__arrow { font-size: 18px; color: #4B9AF6; }
</style>