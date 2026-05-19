<template>
  <div class="ald-header">
    <div class="ald-header__body">
      <div>
        <h2 class="ald-header__title">Chi tiết nhật kí hoạt động</h2>
        <div class="ald-header__meta">
          <div class="ald-header__meta-item">
            <span class="ald-header__meta-label">Audit ID</span>
            <span class="ald-header__meta-value">{{ log.auditId }}</span>
          </div>
          <div v-if="type === 'audit'" class="ald-header__meta-item">
            <span class="ald-header__meta-label">Trace ID</span>
            <span class="ald-header__meta-value ald-header__meta-value--mono italic text-slate-400">Chưa hỗ trợ</span>
          </div>
          <div v-if="type === 'audit'" class="ald-header__divider"></div>
          <div class="ald-header__meta-item">
            <span class="ald-header__meta-label">Thời gian</span>
            <span class="ald-header__meta-value">{{ log.time }}</span>
          </div>
        </div>
      </div>

      <div class="ald-header__actions">
        <button v-if="type === 'audit'" class="ald-header__btn opacity-50 cursor-not-allowed" disabled>
          <span class="material-symbols-outlined">content_copy</span>
          Copy Trace ID
        </button>
        <button class="ald-header__btn">
          <span class="material-symbols-outlined">person_search</span>
          Xem admin
        </button>
        <button class="ald-header__btn">
          <span class="material-symbols-outlined">data_object</span>
          Xuất JSON
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{ 
  log: { auditId: string; traceId: string; time: string },
  type: 'audit' | 'business'
}>()

const copyTrace = () => navigator.clipboard?.writeText(props.log.traceId)
</script>

<style scoped>
.ald-header { margin-bottom: 2.5rem; }

.ald-header__body {
  display: flex; justify-content: space-between; align-items: flex-start;
  flex-wrap: wrap; gap: 1.5rem;
}
.ald-header__title {
  font-size: 1.875rem; font-weight: 700; color: #1b1c18;
  letter-spacing: -0.02em; margin-bottom: 1rem;
}

.ald-header__meta { display: flex; flex-wrap: wrap; align-items: center; gap: 1.5rem; }
.ald-header__meta-item { display: flex; flex-direction: column; gap: 2px; }
.ald-header__meta-label {
  font-size: 0.75rem; font-weight: 700; text-transform: uppercase;
  letter-spacing: 0.1em; color: #574240; opacity: 0.7;
}
.ald-header__meta-value { font-size: 0.875rem; font-weight: 500; color: #1b1c18; }
.ald-header__meta-value--mono {
  font-family: 'JetBrains Mono', monospace; font-size: 0.75rem;
  background: #f6f4ec; padding: 2px 8px; border-radius: 4px;
}
.ald-header__divider {
  width: 1px; height: 32px; background: rgba(221,192,190,0.3);
  display: none;
}
@media (min-width: 768px) { .ald-header__divider { display: block; } }

.ald-header__actions { display: flex; gap: 10px; flex-wrap: wrap; }
.ald-header__btn {
  display: flex; align-items: center; gap: 8px;
  padding: 10px 16px; background: #fff;
  border: 1px solid rgba(221,192,190,0.5);
  border-radius: 0.5rem; font-size: 1rem; font-weight: 600;
  color: #1b1c18; cursor: pointer;
  font-family: 'Manrope', sans-serif; transition: all 0.15s;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}
.ald-header__btn:hover { background: #f6f4ec; border-color: #8a7170; }
.ald-header__btn .material-symbols-outlined { font-size: 18px; }
</style>