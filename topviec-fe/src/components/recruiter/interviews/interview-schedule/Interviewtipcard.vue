<template>
  <div class="tip-card">
    <div class="tip-card__header">
      <span class="material-symbols-outlined tip-card__icon">lightbulb</span>
      <h5 class="tip-card__title">Gợi ý chuyên gia</h5>
    </div>
    <p class="tip-card__text">
      <slot>
        Hãy đính kèm tài liệu yêu cầu hoặc JD vị trí trong phần Ghi chú để ứng viên có sự chuẩn bị tốt nhất cho buổi trao đổi.
      </slot>
    </p>
  </div>
</template>

<script setup lang="ts">
// No props — dùng slot để truyền nội dung linh hoạt
</script>

<style scoped>
.tip-card {
  background: rgba(75,154,246,0.05);
  border: 1.5px solid rgba(75,154,246,0.12);
  border-radius: 0.75rem;
  padding: 1.25rem;
}

.tip-card__header {
  display: flex;
  align-items: center;
  gap: 0.625rem;
  margin-bottom: 0.625rem;
}

.tip-card__icon {
  color: #4B9AF6;
  font-size: 1.2rem;
}

.tip-card__title {
  font-size: 0.875rem;
  font-weight: 700;
  color: #4B9AF6;
  margin: 0;
}

.tip-card__text {
  font-size: 0.875rem;
  color: #0369a1;
  line-height: 1.65;
  margin: 0;
}
</style>