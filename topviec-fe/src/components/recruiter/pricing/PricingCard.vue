<template>
  <div :class="['card', `card--${plan.status}`]">
    <span v-if="plan.popular" class="card__badge">Phổ biến nhất</span>

    <div :class="['card__header', plan.popular && 'card__header--offset']">
      <h3 class="card__name">{{ plan.name }}</h3>
      <div class="card__price">
        <span class="card__price-value">{{ plan.price }}</span>
        <span class="card__price-unit">/tháng</span>
      </div>
    </div>

    <button 
      :class="['card__btn', `card__btn--${plan.status}`]" 
      :disabled="plan.status === 'active' || plan.status === 'current-disabled'"
      @click="$emit('select', plan.id)"
    >
      {{ plan.btnLabel }}
    </button>

    <ul class="card__features">
      <li v-for="(f, i) in plan.features" :key="i" class="card__feature-item">
        <span :class="['material-symbols-outlined', 'card__feature-icon', f.active ? 'card__feature-icon--on' : 'card__feature-icon--off']">
          {{ f.active ? 'check_circle' : 'remove' }}
        </span>
        <span class="card__feature-label">{{ f.label }}</span>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  plan: {
    id: string
    name: string
    price: string
    status: string
    btnLabel: string
    popular?: boolean
    features: { label: string; active: boolean }[]
  }
}>()

defineEmits<{
  (e: 'select', id: string): void
}>()
</script>

<style scoped>
.card {
  position: relative;
  display: flex;
  flex-direction: column;
  padding: 1.5rem;
  border-radius: 0.75rem;
  background: #ffffff;
  border: 1px solid transparent;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.card--active {
  background: #f5f3ff;
  border: 2px solid #534AB7;
  box-shadow: 0 4px 24px rgba(83, 74, 183, 0.13);
}

/* Badge */
.card__badge {
  position: absolute;
  top: -12px;
  left: 50%;
  transform: translateX(-50%);
  background: #534AB7;
  color: #fff;
  font-size: 0.75rem;
  font-weight: 700;
  padding: 4px 12px;
  border-radius: 999px;
  white-space: nowrap;
}

/* Header */
.card__header { margin-bottom: 1rem; }
.card__header--offset { margin-top: 0.5rem; }
.card__name { font-weight: 700; color: #0f172a; font-size: 1rem; }
.card__price { display: flex; align-items: baseline; margin-top: 8px; }
.card__price-value { font-size: 1.875rem; font-weight: 800; color: #0f172a; }
.card__price-unit { font-size: 0.75rem; color: #94a3b8; margin-left: 4px; }

/* Button */
.card__btn {
  width: 100%;
  padding: 8px 0;
  border-radius: 0.5rem;
  font-size: 1rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  cursor: pointer;
  border: none;
  transition: box-shadow 0.2s, opacity 0.2s;
}
.card__btn--current-disabled {
  background: #f1f5f9;
  color: #94a3b8;
  cursor: not-allowed;
}
.card__btn--active {
  background: #e2e8f0;
  color: #64748b;
  cursor: not-allowed;
}
.card__btn--downgrade {
  background: transparent;
  border: 1px solid #f59e0b;
  color: #f59e0b;
}
.card__btn--downgrade:hover { background: rgba(245,158,11,0.05); }
.card__btn--upgrade {
  background: #4B9AF6;
  color: #ffffff;
}
.card__btn--upgrade:hover { box-shadow: 0 4px 12px rgba(75,154,246,0.35); }

/* Features */
.card__features { list-style: none; padding: 0; margin: 0; display: flex; flex-direction: column; gap: 12px; }
.card__feature-item { display: flex; align-items: flex-start; gap: 8px; }
.card__feature-icon { font-size: 16px; flex-shrink: 0; }
.card__feature-icon--on { color: #10b981; }
.card__feature-icon--off { color: #cbd5e1; }
.card__feature-label { font-size: 0.75rem; color: #64748b; }
</style>