<template>
  <div class="page-header">
    <div>
      <h2>Quản lý lịch phỏng vấn</h2>
      <p>Theo dõi các vị trí đang tuyển dụng và lượng lịch phỏng vấn.</p>
    </div>

  </div>
</template>

<script setup lang="ts">
defineEmits<{ (e: 'create'): void }>()
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Manrope:wght@400;700;800&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@24,400,0,0&display=swap');

.material-symbols-outlined {
  font-family: 'Material Symbols Outlined';
  font-size: 18px;
  line-height: 1;
  display: inline-block;
  vertical-align: middle;
}

.page-header {
  font-family: 'Manrope', sans-serif;
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  justify-content: space-between;
  gap: 1.5rem;
  margin-bottom: 2rem;
}
.page-header h2 {
  font-size: 1.875rem;
  font-weight: 800;
  letter-spacing: -0.025em;
  color: #0f172a;
}
.page-header p {
  color: #64748b;
  margin-top: 0.25rem;
  font-size: 1rem;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.625rem 1.5rem;
  background: #4B9AF6;
  color: #fff;
  font-family: 'Manrope', sans-serif;
  font-weight: 700;
  font-size: 1rem;
  border-radius: 0.5rem;
  border: none;
  cursor: pointer;
  box-shadow: 0 4px 14px rgba(75, 154, 246, 0.3);
  transition: transform 0.15s;
}
.btn-primary:hover  { transform: scale(1.02); }
.btn-primary:active { transform: scale(0.98); }
</style>