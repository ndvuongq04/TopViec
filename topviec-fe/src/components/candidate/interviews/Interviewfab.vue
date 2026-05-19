<template>
  <div class="fab">
    <button class="fab__btn">
      <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">calendar_month</span>
      <span class="fab__tooltip">Đặt lại lịch hẹn</span>
    </button>
  </div>
</template>

<style scoped>
.fab {
  position: fixed;
  bottom: 2rem;
  right: 2rem;
  z-index: 50;
}

.fab__btn {
  background: #005ea4;
  color: #fff;
  width: 3.5rem;
  height: 3.5rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  cursor: pointer;
  box-shadow: 0 10px 25px rgba(0, 94, 164, 0.25);
  transition: all 0.2s;
  position: relative;
}

.fab__btn:hover {
  box-shadow: 0 12px 30px rgba(0, 94, 164, 0.35);
  transform: scale(1.1);
}

.fab__btn:active {
  transform: scale(0.95);
}

.fab__tooltip {
  position: absolute;
  right: 100%;
  margin-right: 1rem;
  background: #071b3b;
  color: #fff;
  padding: 0.375rem 0.75rem;
  border-radius: 0.5rem;
  font-size: 0.75rem;
  font-weight: 700;
  white-space: nowrap;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.2s;
}

.fab__btn:hover .fab__tooltip {
  opacity: 1;
}
</style>