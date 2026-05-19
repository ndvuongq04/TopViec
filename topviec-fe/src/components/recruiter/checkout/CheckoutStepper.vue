<template>
  <nav class="stepper">
    <div v-for="step in steps" :key="step.number" class="stepper__item">
      <span :class="['stepper__num', step.number === current && 'stepper__num--active']">
        {{ step.number }}
      </span>
      <span :class="['stepper__label', step.number === current ? 'stepper__label--active' : 'stepper__label--inactive']">
        {{ step.label }}
      </span>
    </div>
  </nav>
</template>

<script setup lang="ts">
defineProps<{ current: number }>()

const steps = [
  { number: 1, label: 'Xác nhận đơn' },
  { number: 2, label: 'Thanh toán' },
  { number: 3, label: 'Hoàn tất' },
]
</script>

<style scoped>
.stepper {
  display: none;
  align-items: center;
  gap: 2rem;
}
@media (min-width: 768px) { .stepper { display: flex; } }

.stepper__item { display: flex; align-items: center; gap: 8px; }

.stepper__num {
  font-size: 0.75rem;
  padding: 2px 8px;
  border-radius: 999px;
  background: #f1f5f9;
  color: #94a3b8;
  font-weight: 700;
}
.stepper__num--active { background: rgba(75,154,246,0.1); color: #4B9AF6; }

.stepper__label { font-size: 0.875rem; font-weight: 500; color: #94a3b8; }
.stepper__label--active { font-weight: 800; color: #4B9AF6; border-bottom: 2px solid #4B9AF6; padding-bottom: 2px; }
</style>