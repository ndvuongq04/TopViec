<template>
  <RouterView />
  <ToastContainer />
  <GlobalConfirmModal />
</template>

<script setup lang="ts">
// App.vue - Root component TopViec
import ToastContainer from '@/components/ui/ToastContainer.vue'
import GlobalConfirmModal from '@/components/ui/GlobalConfirmModal.vue'
</script>
