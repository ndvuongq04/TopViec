<template>
  <section>
    <div class="group-header">
      <div class="accent-bar"></div>
      <h4 class="group-label">{{ group.label }}</h4>
    </div>
    <div class="cards-grid">
      <ServiceCard
        v-for="service in group.services"
        :key="service.id"
        :service="service"
        @add="(qty) => $emit('add-to-cart', service, qty)"
      />
    </div>
  </section>
</template>

<script setup lang="ts">
import ServiceCard from './ServiceCard.vue'

defineProps<{
  group: {
    id: string
    label: string
    services: Array<{
      id: number
      icon: string
      iconBg: string
      iconColor: string
      name: string
      desc: string
      price: number
      unit: string
    }>
  }
}>()

defineEmits<{
  'add-to-cart': [service: { id: number; icon: string; iconBg: string; iconColor: string; name: string; desc: string; price: number; unit: string }, qty: number]
}>()
</script>

<style scoped>
.group-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}
.accent-bar {
  width: 4px;
  height: 24px;
  background: #4B9AF6;
  border-radius: 99px;
}
.group-label {
  font-size: 0.75rem;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: #94a3b8;
  margin: 0;
}
.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}
</style>