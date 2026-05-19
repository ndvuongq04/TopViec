<template>
  <div class="action-bar">
    <div class="action-bar__hint">
      <span class="material-symbols-outlined">info</span>
      <p>Hồ sơ này sẽ được lưu trữ trong 6 tháng theo chính sách bảo mật.</p>
    </div>

    <div class="action-bar__btns">
      <button class="action-bar__btn action-bar__btn--reject" @click="$emit('reject')">
        Từ chối
      </button>
      <button class="action-bar__btn action-bar__btn--schedule" @click="$emit('schedule')">
        Hẹn lịch vòng tiếp
      </button>
      <button class="action-bar__btn action-bar__btn--primary" @click="$emit('update')">
        Cập nhật kết quả
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
defineEmits<{
  (e: 'reject'): void
  (e: 'schedule'): void
  (e: 'update'): void
}>()
</script>

<style scoped>
.action-bar {
  background: #fff;
  border-top: 1px solid #e2e8f0;
  padding: 1.5rem 2rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-top: 3rem;
  border-radius: 0.75rem;
  border: 1px solid #e2e8f0;
}

.action-bar__hint {
  display: none;
  align-items: center;
  gap: 0.5rem;
  color: #64748b;
  font-size: 0.75rem;
  flex-shrink: 0;
}

.action-bar__hint .material-symbols-outlined {
  font-size: 0.9rem;
}

@media (min-width: 640px) {
  .action-bar__hint {
    display: flex;
  }
}

.action-bar__btns {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  width: 100%;
  justify-content: flex-end;
}

@media (max-width: 640px) {
  .action-bar__btns {
    justify-content: stretch;
  }
}

.action-bar__btn {
  padding: 0.625rem 1.5rem;
  border-radius: 0.75rem;
  font-size: 1rem;
  font-weight: 700;
  font-family: inherit;
  cursor: pointer;
  transition: background 0.18s, transform 0.12s, box-shadow 0.18s;
  white-space: nowrap;
  flex: 1 1 0;
  min-width: 0;
}

@media (min-width: 640px) {
  .action-bar__btn {
    flex: none;
  }
}

.action-bar__btn--reject {
  background: transparent;
  border: 1px solid rgba(239,68,68,0.3);
  color: #ef4444;
}

.action-bar__btn--reject:hover {
  background: rgba(239,68,68,0.06);
}

.action-bar__btn--schedule {
  background: transparent;
  border: 1px solid rgba(75,154,246,0.3);
  color: #4B9AF6;
}

.action-bar__btn--schedule:hover {
  background: rgba(75,154,246,0.05);
}

.action-bar__btn--primary {
  background: #4B9AF6;
  border: none;
  color: #fff;
  box-shadow: 0 4px 14px rgba(75,154,246,0.25);
  padding-left: 2rem;
  padding-right: 2rem;
}

.action-bar__btn--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(75,154,246,0.35);
}

.action-bar__btn--primary:active {
  transform: scale(0.97);
}
</style>