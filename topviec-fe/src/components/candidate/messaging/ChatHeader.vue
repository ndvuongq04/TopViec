<template>
  <header class="chat-header">
    <div class="header-left">
      <img class="avatar" :src="conversation.avatar" :alt="conversation.companyName" />
      <div>
        <h2 class="company-name">
          {{ conversation.companyName }}
          <span class="material-symbols-outlined verified-icon">verified</span>
        </h2>
        <p class="position">{{ conversation.position }}</p>
      </div>
    </div>
    <div class="header-right">
      <button class="job-btn">Xem tin tuyển dụng</button>
      <button class="icon-btn">
        <span class="material-symbols-outlined">more_vert</span>
      </button>
    </div>
  </header>
</template>

<script setup lang="ts">
defineProps<{ conversation: any }>()
</script>

<style scoped>
.chat-header {
  height: 72px;
  padding: 0 24px;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(8px);
  flex-shrink: 0;
  box-shadow: 0 2px 12px rgba(0, 94, 164, 0.03);
}
.header-left { display: flex; align-items: center; gap: 12px; }
.avatar {
  width: 40px; height: 40px;
  border-radius: 50%; object-fit: cover;
  border: 1px solid #e9edff;
}
.company-name {
  font-size: 1.125rem; font-weight: 600;
  color: #071b3b; margin: 0 0 2px;
  display: flex; align-items: center; gap: 5px;
}
.verified-icon {
  font-size: 1rem; color: #4B9AF6;
  font-variation-settings: 'FILL' 1;
}
.position { font-size: 0.875rem; color: #64748b; margin: 0; }

.header-right { display: flex; align-items: center; gap: 10px; }
.job-btn {
  padding: 8px 16px;
  border: 1px solid rgba(75, 154, 246, 0.3);
  border-radius: 8px;
  background: transparent;
  color: #4B9AF6;
  font-size: 0.875rem; font-weight: 600;
  cursor: pointer;
  font-family: inherit;
  transition: background 0.15s;
}
.job-btn:hover { background: #f1f4f9; }
.icon-btn {
  width: 32px; height: 32px;
  border: none; background: none; cursor: pointer;
  border-radius: 50%; color: #64748b;
  display: flex; align-items: center; justify-content: center;
  transition: background 0.15s;
}
.icon-btn:hover { background: #e2e8f0; }
</style>