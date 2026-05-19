<script setup lang="ts">
import { computed } from "vue";
import { useRoute } from "vue-router";
import CandidateHeader from "@/components/candidate/layout/CandidateHeader.vue";
import CandidateFooter from "@/components/candidate/layout/CandidateFooter.vue";

const route = useRoute();

const isUnfixedHeaderPage = computed(() => {
  return route.name === 'JobSearch';
});

const isFullscreenPage = computed(() => {
  return route.name === 'CandidateMessages';
});
</script>

<template>
  <div
    class="bg-background-light dark:bg-background-dark font-display text-text-main dark:text-gray-100 flex flex-col overflow-x-clip"
    :class="isFullscreenPage ? 'h-screen overflow-hidden' : 'min-h-screen'"
  >
    <!-- Header -->
    <CandidateHeader :unfixed="isUnfixedHeaderPage" />

    <!-- Main Content Area -->
    <div
      class="layout-container flex grow flex-col w-full"
      :class="[isUnfixedHeaderPage ? '' : 'pt-[65px]', isFullscreenPage ? 'overflow-hidden' : 'pb-10']"
    >
      <!-- Content (router-view) -->
      <RouterView />
    </div>

    <!-- Footer (ẩn trên trang fullscreen) -->
    <CandidateFooter v-if="!isFullscreenPage" />
  </div>
</template>
