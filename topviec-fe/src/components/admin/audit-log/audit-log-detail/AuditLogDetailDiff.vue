<template>
  <div class="ald-diff">
    <h3 class="ald-diff__title">
      <span class="material-symbols-outlined">difference</span>
      Dữ liệu thay đổi
    </h3>

    <div class="ald-diff__grid">
      <div class="ald-diff__col">
        <h4 class="ald-diff__col-label">Giá trị cũ</h4>
        <div class="ald-diff__code-block">
          <pre>{{ before }}</pre>
        </div>
      </div>

      <div class="ald-diff__arrow">
        <span class="material-symbols-outlined">arrow_forward</span>
      </div>

      <div class="ald-diff__col">
        <h4 class="ald-diff__col-label">Giá trị mới</h4>
        <div class="ald-diff__code-block ald-diff__code-block--after">
          <pre>{{ after }}</pre>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ before: string; after: string }>()
</script>

<style scoped>
.ald-diff {
  background: #fff; border-radius: 1rem; padding: 2rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border: 1px solid rgba(228,226,220,0.5);
}
.ald-diff__title {
  display: flex; align-items: center; gap: 10px;
  font-size: 1.125rem; font-weight: 700; color: #1b1c18; margin-bottom: 1.5rem;
}
.ald-diff__title .material-symbols-outlined { color: #76191d; font-size: 22px; }

.ald-diff__grid {
  display: grid; grid-template-columns: 1fr 40px 1fr;
  gap: 1rem; align-items: start; position: relative;
}
@media (max-width: 768px) {
  .ald-diff__grid { grid-template-columns: 1fr; }
  .ald-diff__arrow { transform: rotate(90deg); justify-self: center; }
}

.ald-diff__col { display: flex; flex-direction: column; gap: 12px; }
.ald-diff__col-label {
  font-size: 0.75rem; font-weight: 700; text-transform: uppercase;
  letter-spacing: 0.07em; color: #574240; padding-left: 4px;
}

.ald-diff__code-block {
  background: #1e1e1e; border-radius: 0.75rem; padding: 1.25rem;
  overflow-x: auto; box-shadow: inset 0 2px 8px rgba(0,0,0,0.2);
}
.ald-diff__code-block--after {
  border: 1px solid rgba(186,26,26,0.3); position: relative;
}
.ald-diff__code-block--after::before {
  content: ''; position: absolute; inset: 0;
  background: rgba(186,26,26,0.05); border-radius: 0.75rem; pointer-events: none;
}

.ald-diff__code-block pre {
  font-family: 'JetBrains Mono', monospace;
  font-size: 0.875rem; line-height: 1.7; color: #d4d4d4;
  margin: 0; white-space: pre-wrap; word-break: break-word;
}

.ald-diff__arrow {
  width: 40px; height: 40px;
  background: #fff; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  border: 1px solid rgba(228,226,220,0.5);
  align-self: center; justify-self: center;
}
.ald-diff__arrow .material-symbols-outlined { color: #76191d; font-size: 20px; }
</style>