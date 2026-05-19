<template>
  <div class="profile-tab">
    <CandidateGeneralInfo :candidate="candidate" />
  </div>
</template>

<script setup lang="ts">
import CandidateGeneralInfo from './CandidateGeneralInfo.vue'

defineProps<{ candidate: any }>()
</script>

<style scoped>
.profile-tab {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
  align-items: start;
}
</style>