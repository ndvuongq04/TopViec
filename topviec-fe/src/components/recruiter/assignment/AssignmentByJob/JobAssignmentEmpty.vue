<template>
  <div class="job-empty">
    <span class="material-symbols-outlined job-empty__icon">person_off</span>
    <h3 class="job-empty__title">Chưa có người quản lý phụ</h3>
    <p class="job-empty__desc">Tin tuyển dụng này hiện chỉ do người tạo quản lý. Thêm thành viên để chia sẻ công việc.</p>
    <button 
      class="job-empty__btn" 
      :class="!canAssign ? 'opacity-50 cursor-not-allowed' : ''"
      :disabled="!canAssign"
      :title="!canAssign ? 'Cho phép: published, paused, renewed, interviewing, scheduled, closed' : undefined"
      @click="canAssign && $emit('assign')"
    >
      <span class="material-symbols-outlined">person_add</span>
      Phân công người ngay
    </button>
  </div>
</template>

<script setup lang="ts">
defineProps<{ canAssign?: boolean }>()
defineEmits(['assign'])
</script>

<style scoped>
.job-empty {
  background: #fff; border-radius: 0.75rem; padding: 3rem 2rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border: 1px solid rgba(226,232,240,0.3);
  display: flex; flex-direction: column; align-items: center;
  text-align: center; gap: 12px;
}
.job-empty__icon { font-size: 48px; color: #cbd5e1; }
.job-empty__title { font-size: 1.125rem; font-weight: 700; color: #0f172a; }
.job-empty__desc { font-size: 0.875rem; color: #64748b; max-width: 320px; line-height: 1.6; }
.job-empty__btn {
  display: flex; align-items: center; gap: 8px;
  margin-top: 1rem; padding: 10px 24px;
  background: #4B9AF6; color: #fff; border: none;
  border-radius: 0.5rem; font-size: 0.875rem; font-weight: 600;
  cursor: pointer; font-family: 'Manrope', sans-serif;
  box-shadow: 0 2px 8px rgba(75,154,246,0.25);
  transition: all 0.15s;
}
.job-empty__btn:hover { background: #2563eb; }
.job-empty__btn:active { transform: scale(0.97); }
.job-empty__btn .material-symbols-outlined { font-size: 18px; }
</style>