<template>
  <div class="stage-notice">
    <div class="stage-notice__left">
      <div class="stage-notice__icon">
        <span class="material-symbols-outlined">info</span>
      </div>
      <div>
        <h4 class="stage-notice__title">Thông báo tự động</h4>
        <p class="stage-notice__desc">
          Ứng viên sẽ được thông báo tự động qua email khi họ tiến tới vòng tiếp theo.
        </p>
      </div>
    </div>
    <button class="stage-notice__action" @click="$emit('configure')">Cấu hình cài đặt</button>
  </div>
</template>

<script setup lang="ts">
defineEmits<{
  (e: 'configure'): void
}>()
</script>

<style scoped>
.stage-notice {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1.5rem;
  margin-top: 3rem;
  padding: 1.5rem;
  background: rgba(75, 154, 246, 0.05);
  border: 1.5px solid rgba(75, 154, 246, 0.12);
  border-radius: 0.75rem;
}

.stage-notice__left {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.stage-notice__icon {
  width: 3rem;
  height: 3rem;
  border-radius: 50%;
  background: rgba(75, 154, 246, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #4B9AF6;
  flex-shrink: 0;
}

.stage-notice__icon .material-symbols-outlined {
  font-size: 1.5rem;
}

.stage-notice__title {
  font-size: 0.9rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 0.25rem;
}

.stage-notice__desc {
  font-size: 0.8rem;
  color: #475569;
  margin: 0;
}

.stage-notice__action {
  background: transparent;
  border: none;
  font-size: 0.8rem;
  font-weight: 700;
  color: #4B9AF6;
  cursor: pointer;
  text-decoration: none;
  white-space: nowrap;
  padding: 0;
  font-family: inherit;
  transition: text-decoration 0.15s;
}

.stage-notice__action:hover {
  text-decoration: underline;
}

@media (max-width: 640px) {
  .stage-notice {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>