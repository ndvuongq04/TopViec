<template>
  <div class="member-list">
    <MemberAssignmentItem
      v-for="m in members" :key="m.id"
      :member="m" :active="m.id === activeId"
      @click="$emit('select', m.id)"
    />
  </div>
</template>

<script setup lang="ts">
import MemberAssignmentItem from './MemberAssignmentItem.vue'
defineProps<{ members: any[]; activeId: number | null }>()
defineEmits(['select'])
</script>

<style scoped>
.member-list { display: flex; flex-direction: column; gap: 4px; }
</style>