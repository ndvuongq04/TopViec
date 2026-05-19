<template>
  <div class="footer-actions">
    <button class="btn-outline-lg" @click="$emit('export')">
      Xuất báo cáo tuyển dụng
    </button>

  </div>
</template>

<script setup lang="ts">
defineEmits<{
  (e: 'export'): void
  (e: 'invite'): void
}>()
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Manrope:wght@800&display=swap');

.footer-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
  font-family: 'Manrope', sans-serif;
}

.btn-outline-lg {
  padding: 0.75rem 2rem;
  background: #fff;
  border: 2px solid #4B9AF6;
  color: #4B9AF6;
  font-family: 'Manrope', sans-serif;
  font-size: 1rem;
  font-weight: 800;
  border-radius: 0.5rem;
  cursor: pointer;
  transition: background 0.15s;
}
.btn-outline-lg:hover { background: #eff6ff; }

.btn-primary-lg {
  padding: 0.75rem 2rem;
  background: #4B9AF6;
  color: #fff;
  font-family: 'Manrope', sans-serif;
  font-size: 1rem;
  font-weight: 800;
  border: none;
  border-radius: 0.5rem;
  cursor: pointer;
  box-shadow: 0 8px 24px rgba(75, 154, 246, 0.3);
  transition: transform 0.15s;
}
.btn-primary-lg:hover  { transform: scale(1.05); }
.btn-primary-lg:active { transform: scale(0.95); }
</style>