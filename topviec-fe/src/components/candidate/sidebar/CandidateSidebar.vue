<script setup lang="ts">
// AppSidebar: Sidebar trái, ẩn trên mobile (hidden lg:flex)
// Gộp 3 component con: SidebarProfile + SidebarNav + SidebarFilters
import { useRoute } from "vue-router";
import SidebarProfile from "@/components/candidate/sidebar/CandidateProfile.vue";
import SidebarNav from "@/components/candidate/sidebar/CandidateNav.vue";
import SidebarFilters from "@/components/candidate/sidebar/CandidateFilters.vue";

const route = useRoute();
</script>

<template>
  <aside class="hidden lg:flex flex-col w-64 shrink-0 gap-6">
    <!-- Thông tin user + progress -->
    <SidebarProfile />

    <!-- Menu điều hướng -->
    <SidebarNav />

    <!-- Bộ lọc tìm kiếm: Chỉ hiện ở trang Tìm việc (Trang chủ) -->
    <SidebarFilters v-if="route.path === '/'" />
  </aside>
</template>
