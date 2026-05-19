<template>
  <div class="stage-toolbar">
    <div class="stage-toolbar__info">
      <h1 class="stage-toolbar__title">Quy trình phỏng vấn</h1>
      <p class="stage-toolbar__subtitle">Định nghĩa và sắp xếp các bước trong phễu tuyển dụng của bạn.</p>
    </div>
    <button class="stage-toolbar__btn" @click="$emit('add')">
      <span class="material-symbols-outlined">add_circle</span>
      Vòng mới
    </button>
  </div>
</template>

<script setup lang="ts">
defineEmits<{
  (e: 'add'): void
}>()
</script>

<style scoped>
.stage-toolbar {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 2rem;
}

.stage-toolbar__title {
  font-size: 1.875rem;
  font-weight: 800;
  letter-spacing: -0.03em;
  color: #0f172a;
  margin: 0 0 0.25rem;
  line-height: 1.1;
}

.stage-toolbar__subtitle {
  font-size: 0.875rem;
  color: #64748b;
  margin: 0;
}

.stage-toolbar__btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: #4B9AF6;
  color: #fff;
  font-weight: 700;
  font-size: 0.875rem;
  border: none;
  border-radius: 0.5rem;
  padding: 0.625rem 1.25rem;
  cursor: pointer;
  box-shadow: 0 4px 14px rgba(75, 154, 246, 0.25);
  transition: background 0.18s, box-shadow 0.18s, transform 0.12s;
  white-space: nowrap;
  flex-shrink: 0;
}

.stage-toolbar__btn:hover {
  background: #3b8de0;
  box-shadow: 0 6px 20px rgba(75, 154, 246, 0.35);
  transform: translateY(-1px);
}

.stage-toolbar__btn:active {
  transform: translateY(0);
}

.stage-toolbar__btn .material-symbols-outlined {
  font-size: 1.2rem;
}
</style>