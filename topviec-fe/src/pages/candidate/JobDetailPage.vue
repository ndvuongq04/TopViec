<script setup lang="ts">
import { useRoute } from "vue-router";
import JobDetailView from "@/components/candidate/job/JobDetailView.vue";

const route = useRoute();
const jobId = route.params.id as string;
</script>

<template>
  <JobDetailView :id="jobId" />
</template>
