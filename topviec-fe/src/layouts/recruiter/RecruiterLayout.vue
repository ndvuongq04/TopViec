<template>
  <div class="flex h-screen overflow-hidden">
    <RecruiterSidebar />
    <main
      class="flex-1 bg-background-light dark:bg-background-dark"
      :class="route.meta.fullPage ? 'flex flex-col overflow-hidden' : 'overflow-y-auto scroll-smooth'"
    >
      <RecruiterHeader />
      <div :class="route.meta.fullPage ? 'flex-1 overflow-hidden' : 'px-8 pb-8'">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import RecruiterSidebar from '@/components/recruiter/layout/RecruiterSidebar.vue'
import RecruiterHeader from '@/components/recruiter/layout/RecruiterHeader.vue'

const route = useRoute()
</script>