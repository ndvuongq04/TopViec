<template>
  <div class="brands-header">
    <div class="brands-header__overlay"></div>
    <div class="brands-header__content">
      <h1 class="brands-header__title">Thương hiệu lớn tiêu biểu</h1>
      <p class="brands-header__subtitle">Hàng trăm thương hiệu lớn tiêu biểu đang tuyển dụng trên TopViec Pro</p>
    </div>
    <span class="brands-header__badge">Pro Company</span>
  </div>
</template>

<script setup lang="ts" />

<style scoped>
.brands-header {
  position: relative;
  display: flex; align-items: center; justify-content: space-between;
  padding: 2.5rem 2rem;
  background: linear-gradient(to right, #60a5fa, #3b82f6, #2563eb);
  color: #fff;
  overflow: hidden;
}
.brands-header__overlay {
  position: absolute; inset: 0;
  background-image: url('https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?q=80&w=2070&auto=format&fit=crop');
  background-size: cover; background-position: center;
  opacity: 0.2; mix-blend-mode: overlay;
}
.brands-header__content { position: relative; z-index: 1; }
.brands-header__title { font-size: 1.875rem; font-weight: 700; letter-spacing: -0.01em; margin-bottom: 4px; font-family: 'Manrope', sans-serif; }
.brands-header__subtitle { font-size: 0.875rem; font-weight: 500; opacity: 0.9; font-family: 'Manrope', sans-serif; }
.brands-header__badge {
  position: relative; z-index: 1;
  display: none;
  padding: 8px 24px;
  background: rgba(255,255,255,0.2);
  border-radius: 999px;
  font-size: 0.875rem; font-weight: 600; color: #fff;
  font-family: 'Manrope', sans-serif;
  backdrop-filter: blur(8px);
}
@media (min-width: 768px) { .brands-header__badge { display: inline-flex; } }
</style>