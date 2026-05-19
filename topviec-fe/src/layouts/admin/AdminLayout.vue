<template>
  <div class="flex min-h-screen">
    <AdminSidebar />
    <main class="flex-1 ml-72 min-h-screen overflow-x-hidden bg-[#f8f6f6] dark:bg-background-dark/50">
      <AdminHeader />
      <div class="p-8">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import AdminSidebar from '@/components/admin/layout/AdminSidebar.vue'
import AdminHeader from '@/components/admin/layout/AdminHeader.vue'
</script>