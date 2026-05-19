<template>
  <div class="header-banner">
    <div class="banner-overlay"></div>
    <div class="banner-content">
      <h1 class="banner-title">{{ title }}</h1>
      <p class="banner-subtitle">{{ subtitle }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ title: string; subtitle: string }>()
</script>

<style scoped>
.header-banner {
  position: relative;
  display: flex;
  align-items: center;
  padding: 40px 32px;
  background: linear-gradient(to right, #60a5fa, #3b82f6, #2563eb);
  overflow: hidden;
}
.banner-overlay {
  position: absolute;
  inset: 0;
  opacity: 0.18;
  background-image: url('https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?q=80&w=2070&auto=format&fit=crop');
  background-size: cover;
  background-position: center;
  mix-blend-mode: overlay;
}
.banner-content { position: relative; z-index: 1; }
.banner-title {
  font-size: 1.875rem;
  font-weight: 700;
  color: #fff;
  margin: 0 0 4px;
  letter-spacing: -0.01em;
  font-family: 'Manrope', sans-serif;
}
.banner-subtitle {
  font-size: 0.875rem;
  color: rgba(255, 255, 255, 0.88);
  font-weight: 500;
  margin: 0;
  font-family: 'Manrope', sans-serif;
}
</style>