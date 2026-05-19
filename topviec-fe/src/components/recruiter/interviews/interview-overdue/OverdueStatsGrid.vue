<template>
  <div class="stats-grid">
    <!-- Tổng quá hạn -->
    <div class="stat-card">
      <div class="stat-card__icon">
        <span class="material-symbols-outlined">running_with_errors</span>
      </div>
      <p class="stat-card__number">{{ totalOverdue }}</p>
      <p class="stat-card__label">Tổng ứng viên quá hạn</p>
    </div>

    <!-- Tip card -->
    <div class="tip-card">
      <div class="tip-card__content">
        <h3 class="tip-card__title">Mẹo xử lý nhanh</h3>
        <p class="tip-card__text">
          Đối với ứng viên tiềm năng, hãy thử <strong>Gia hạn</strong> thêm 2 ngày.
          Nếu vẫn im lặng, bạn có thể <strong>Đặt lịch hộ</strong> nếu đã trao đổi qua điện thoại.
        </p>
      </div>
      <div class="tip-card__deco" aria-hidden="true">
        <span class="material-symbols-outlined">robot_2</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  totalOverdue: number
}>()
</script>

<style scoped>
.stats-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;
}

@media (min-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr 2fr;
  }
}

/* Stat card */
.stat-card {
  background: #fff;
  border: 1px solid #f1f5f9;
  border-radius: 1rem;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}

.stat-card__icon {
  width: 2.5rem;
  height: 2.5rem;
  background: #fef2f2;
  border-radius: 0.75rem;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ef4444;
  margin-bottom: 1rem;
}

.stat-card__icon .material-symbols-outlined {
  font-size: 1.3rem;
}

.stat-card__number {
  font-size: 1.875rem;
  font-weight: 800;
  color: #0f172a;
  margin: 0 0 0.25rem;
  line-height: 1;
}

.stat-card__label {
  font-size: 0.875rem;
  color: #64748b;
  font-weight: 500;
  margin: 0;
}

/* Tip card */
.tip-card {
  background: linear-gradient(135deg, #4B9AF6, #1d4ed8);
  border-radius: 1rem;
  padding: 1.5rem;
  color: #fff;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  box-shadow: 0 8px 24px rgba(75,154,246,0.25);
}

.tip-card__content {
  position: relative;
  z-index: 1;
  max-width: 32rem;
}

.tip-card__title {
  font-size: 1.125rem;
  font-weight: 800;
  margin: 0 0 0.5rem;
  line-height: 1.3;
}

.tip-card__text {
  font-size: 0.875rem;
  line-height: 1.65;
  color: rgba(255,255,255,0.8);
  margin: 0;
}

.tip-card__text strong {
  color: #fff;
  font-weight: 700;
}

/* Decorative icon */
.tip-card__deco {
  position: absolute;
  right: 0;
  bottom: 0;
  top: 0;
  width: 33%;
  display: flex;
  align-items: flex-end;
  justify-content: flex-end;
  overflow: hidden;
  opacity: 0.2;
  pointer-events: none;
}

.tip-card__deco .material-symbols-outlined {
  font-size: 7.5rem;
  transform: rotate(12deg) translate(0.5rem, 0.5rem);
}
</style>