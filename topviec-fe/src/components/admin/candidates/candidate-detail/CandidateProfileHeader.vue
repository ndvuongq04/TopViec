<template>
  <div class="header-row">
    <div>
      <div class="breadcrumb">
        <span class="material-symbols-outlined">arrow_back</span>
        <span class="breadcrumb-link" @click="$router.back()">Quản lý Ứng viên</span>
        <span class="material-symbols-outlined sep">chevron_right</span>
        <span class="breadcrumb-current">Chi tiết #{{ candidate.id }}</span>
      </div>
      <h2 class="page-title">{{ candidate.name }}</h2>
    </div>

    <div class="action-group">
      <button class="btn-outline" @click="$emit('reset-password')">
        <span class="material-symbols-outlined">key</span>
        Reset mật khẩu
      </button>
      <button class="btn-danger" @click="$emit('lock-account')">
        <span class="material-symbols-outlined">lock</span>
        Khóa tài khoản
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ candidate: { id: string; name: string } }>()
defineEmits<{ 'reset-password': []; 'lock-account': [] }>()
</script>

<style scoped>
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 16px;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 500;
  color: #574240;
  margin-bottom: 6px;
}
.breadcrumb .material-symbols-outlined { font-size: 15px; }
.breadcrumb-link {
  cursor: pointer;
  transition: color 0.15s;
}
.breadcrumb-link:hover { color: #76191d; }
.breadcrumb-current { color: #1b1c18; }
.sep { color: #8a7170; }

.page-title {
  font-size: 30px;
  font-weight: 800;
  color: #1b1c18;
  letter-spacing: -0.02em;
  margin: 0;
}

.action-group { display: flex; gap: 10px; align-items: center; }

.btn-outline {
  display: flex; align-items: center; gap: 6px;
  padding: 10px 20px;
  border: 1px solid #8a7170;
  border-radius: 8px;
  background: transparent;
  font-size: 13px; font-weight: 600;
  color: #574240;
  cursor: pointer;
  font-family: inherit;
  transition: background 0.15s;
}
.btn-outline:hover { background: #f6f4ec; }
.btn-outline .material-symbols-outlined { font-size: 17px; }

.btn-danger {
  display: flex; align-items: center; gap: 6px;
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  background: #ba1a1a;
  color: #fff;
  font-size: 13px; font-weight: 600;
  cursor: pointer;
  font-family: inherit;
  box-shadow: 0 2px 6px rgba(186, 26, 26, 0.2);
  transition: background 0.15s;
}
.btn-danger:hover { background: #991414; }
.btn-danger .material-symbols-outlined { font-size: 17px; }
</style>