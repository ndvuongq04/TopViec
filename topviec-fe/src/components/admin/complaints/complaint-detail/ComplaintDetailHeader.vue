<template>
  <div class="cd-header">
    <div class="cd-header__left">
      <Breadcrumb :items="breadcrumbItems" :hide-home="true" class="cd-header__breadcrumb" />
      <h1 class="cd-header__title">{{ complaint.id }} - {{ complaint.title }}</h1>
      <div class="cd-header__tags">
        <span
          v-for="tag in complaint.tags"
          :key="tag.label"
          :class="['cd-header__tag', `cd-header__tag--${tag.variant}`]"
        >
          <span v-if="tag.icon" class="material-symbols-outlined">{{ tag.icon }}</span>
          {{ tag.label }}
        </span>
      </div>
    </div>

    <div v-if="showActions" class="cd-header__actions">
      <button class="cd-header__btn cd-header__btn--ghost" @click="$emit('reject')">
        Từ chối
      </button>
      <button class="cd-header__btn cd-header__btn--primary" @click="$emit('confirm')">
        Duyệt khiếu nại
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import Breadcrumb from '@/components/ui/Breadcrumb.vue'

const props = defineProps<{
  complaint: {
    id: string
    title: string
    tags: { label: string; variant: string; icon?: string }[]
  }
  showActions?: boolean
}>()

defineEmits(['reject', 'confirm'])

const breadcrumbItems = computed(() => [
  { label: 'Khiếu nại', to: '/admin/reports' },
  { label: props.complaint.id },
])
</script>

<style scoped>
.cd-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 1.5rem;
  margin-bottom: 2rem;
  font-family: 'Manrope', sans-serif;
}

.cd-header__left {
  min-width: 0;
}

.cd-header__breadcrumb {
  margin-bottom: 0.75rem;
}

.cd-header__title {
  margin: 0 0 1rem;
  font-size: 1.875rem;
  line-height: 1.15;
  font-weight: 800;
  letter-spacing: -0.02em;
  color: #1b1c18;
}

.cd-header__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.cd-header__tag {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.35rem 0.75rem;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.cd-header__tag .material-symbols-outlined {
  font-size: 0.875rem;
}

.cd-header__tag--default {
  background: #e4e2dc;
  color: #574240;
}

.cd-header__tag--warning {
  background: #faeeda;
  color: #633806;
}

.cd-header__tag--success {
  background: #e1f5ee;
  color: #085041;
}

.cd-header__tag--error {
  background: #ffdad6;
  color: #93000a;
}

.cd-header__actions {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.cd-header__btn {
  padding: 0.75rem 1.25rem;
  border-radius: 0.625rem;
  border: none;
  font-size: 1rem;
  font-weight: 700;
  font-family: 'Manrope', sans-serif;
  cursor: pointer;
  transition: background 0.15s, color 0.15s, box-shadow 0.15s;
}

.cd-header__btn--ghost {
  background: #f0eee7;
  color: #1b1c18;
}

.cd-header__btn--ghost:hover {
  background: #eae8e1;
}

.cd-header__btn--primary {
  background: #963131;
  color: #fff;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.cd-header__btn--primary:hover {
  background: #76191d;
}

@media (max-width: 768px) {
  .cd-header {
    gap: 1rem;
  }

  .cd-header__title {
    font-size: 1.875rem;
  }

  .cd-header__actions {
    width: 100%;
  }

  .cd-header__btn {
    flex: 1 1 100%;
    justify-content: center;
  }
}
</style>
